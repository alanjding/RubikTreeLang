# RubikTreeLang
A minimalistic esoteric programming language with a Rubik's Cube tree memory tape. How exactly does that work, you ask?

RubikTreeLang is modeled somewhat after the Turing machine with one read-write head and a shiftable infinite memory tape. In this language, the read-write head and its functionality remain very similar to the Turing machine. However, instead of to a one-dimensional sequential memory, data is written to stickers on a virtual 2x2x2 Rubik's Cube. Each sticker cell contains an (unsigned) one-byte payload. Shifting the tape left and right is instead replaced with legal twists of the Rubik's Cube.

Unfortunately, on a single 2x2x2 Rubik's Cube, we only have 24 slots of memory to work with. To remedy this, each sticker cell comes equipped with a potential reference to another 2x2x2 Rubik's Cube, complete with 24 more sticker cells with a payload and, \*gasp\*, potential references to yet more 2x2x2 Rubik's Cubes! We are left with a memory model with the same structure as a 24-way trie, but of course, going down the trie takes a bit of work. The presence of the trie makes it possible for the programmer to completely ignore cube twists and treat the memory tape as, simply, a 24-way trie. However, I would advise strongly against doing this, mainly because it makes the language uninteresting, but also horribly, disturbingly, wretchedly, and deathly space inefficient (a ___ byte overhead (assuming a JVM using a 16 byte class overhead, a 24 byte array overhead, and padding objects to the next highest multiple of 8 bytes) per byte of payload), as opposed to merely horribly space inefficient (an amortized __ byte overhead per byte of payload in the, uh, best case).

However, our tape is not yet "contiguous": there is no way for memory on one cube to move to another cube. As a result, the language supports an unsigned one-byte chunk of global storage accessible from any cube. Recall that the language only has one read-write head, so the global storage doubles as a way to support the arithmetic and enter-if-nonzero commands.

RubikTreeLang supports the following commands: moves, set `set_ arg`, set global `gset_ arg`, copy global to payload `gtp`, copy payload to global `ptg`, increment global `g++`, decrement global `g--`, input `input_`, output `output_`, arithmetic operators (`+`, `-`, `*`, `/`, `%`), enter-if-nonzero brackets (`{`, `}`), traverse down `v`, and traverse up `^`. Comments are also supported with the command `#`.

Commands and their arguments are to be delimited by whitespace of some sort.

### Moves

The memory can be moved around via standard Rubik's Cube turns through the following commands:

Standard moves:
- `U`: rotate the upper layer 90 degrees clockwise (where, for all such rotations, clockwise is defined as if one is looking at the specified face)
- `R`: rotate the right layer 90 degrees clockwise
- `D`: rotate the bottom (<u>d</u>own) layer 90 degrees clockwise
- `L`: rotate the left layer 90 degrees clockwise
- `F`: rotate the front layer 90 degrees clockwise
- `B`: rotate the back layer 90 degrees clockwise

Any of the above moves with a `2` following it (e.g. `U2`) correspond to a 180 degree rotation, and any of the moves with a `'` (single quote, "prime") following it (e.g. `F'`) correspond to a 90 degree counterclockwise rotation.

We also have whole cube rotations:
- `x`: rotate entire cube on `R`
- `y`: rotate entire cube on `U`
- `z`: rotate entire cube on `F`

Just like with the face turns, we can append `'` or `2` to each whole cube rotation to the same effect.

### Where is the read-write head?

The remaining operations can only be done with data accessible to the read-write head (and the global storage). Where is the read-write head, you ask? On any 2x2x2 cube, the read-write head is the at the top right cell on the cube's `F` face, marked with an `o` below:
```
     . .
     . .                          U
. .  . o  . .  . .            ( L F R B )
. .  . .  . .  . .                D
     . .
     . .
```

### Set `set_ arg`

The set operator sets the payload of the cell at the read-write head as defined in code (cf. input, which stores one byte of user input). This uses the command, `set_`, where the underscore corresponds to a character letting the interpreter know how to set the payload:
- A decimal integer literal uses `d` (example: `setd 123`). Note that since the payload bytes are unsigned, only integers between 0 and 255 inclusive will be correctly represented. Other integers are legal but will overflow the representation. Arguments larger than `INT_MAX` will cause the interpreter to throw an exception.
- An ASCII one-byte char uses `c`, which the interpreter would treat as a char literal (example: `setc 1` tells the interpreter to put the ASCII byte representation of the char `1` in the cell under the read-write head). Characters will need to be escaped as necessary as if you were writing a Java char literal.
- One or two (or more, but overflow will occur) hexadecimal digits uses `x` (example: `setx be` sets the payload's bits to `10111110`).

### Set global `gset_ arg`

Preceding the set operator with a `g` sets the global byte. The right-side argument formats remain the same (for example, `gsetd 123`, `gsetc 1`, and `gsetx be` are all valid)

### Copy global to payload `gtp`

Sets the payload of the cell at the read-write head to match the contents of the global byte. This operator takes no arguments (so its usage is simply `gtp`).

### Copy payload to global `ptg`
 
Sets the global byte to the payload of the cell under the read-write head. This operator takes no arguments (so its usage is simply `ptg`).

### Increment `g++` and decrement `g--` global

A no-arg command that increments or decrements the global byte.

### Input `input_`

The input unary operator sets the payload of the cell at the read-write head to the next character/decimal integer/hex string/binary string in the standard input stream. The underscore in the `input` call can take on the following values depending on how the user wants the interpreter to treat the input:
- `b` (`inputb`): read the earliest-occurring longest consecutive sequence of characters that are either `'0'` or `'1'` as a binary string. When stored to a cell's one-byte payload, overflow will occur if the sequence is longer than 8 characters.
- `c` (`inputc`): read the next character in stdin as a character literal. If no characters remain in stdin and `inputc` is called, then 0 is written to the payload byte.
- `d` (`inputd`): read the earliest-occurring longest consecutive sequence of characters matching `[0-9]` as a decimal integer literal. When stored to a cell's one-byte payload, overflow will occur if the integer literal is greater than 255. Arguments larger than `INT_MAX` will cause the interpreter to throw an exception.
- `x` (`inputx`): read the earliest-occurring longest consecutive sequence of characters matching `[0-9A-Fa-f]` as a hexadecimal integer literal. When stored to a cell's one-byte payload, overflow will occur if the sequence is longer than 2 characters.

`input_` behaves like the methods of a Scanner object (because that's how they're implemented): in the command line, the user must press return/enter after entering input for the interpreter to read it. Additionally, if no match is found in the line fed to stdin, `input_` (with the exception of `inputc` which waits for stdin to become populated or repopulated) stores `0` at the payload of the cell under the read-write head.

### Output `output_`

The output unary operator prints the payload of the cell at the read-write head to stdout. The operator takes no arguments:
- `b` (`outputb`): output the payload as a binary string with leading zeroes. Appends a new line after the output.
- `c` (`outputc`): output the payload as a character literal. Does not append a new line after the output.
- `d` (`outputd`): output the payload as a decimal integer. Appends a new line after the output.
- `x` (`outputx`): output the payload as a lowercase hexadecimal string with no leading zeroes or `0x`. Appends a new line after the output.
- `X` (`outputX`): output the payload as an uppercase hexadecimal string with no leading zeroes or `0x`. Appends a new line after the output.

### Arithmetic operators `+`, `-`, `*`, `/`, `%`

These are no-arg operators that take input from the cell under the read-write head and the global byte and writing the result of applying the operator to the cell under the read-write head. The symbols are consistent with most civilized languages, i.e. `+` is addition, `-` is subtraction, `*` is multiplication, `/` is integer division, and `%` is modulo (remainder). Of course, since the sources and destinations of the operator are fixed, in code, one simply types the operator and the operator alone to use them (i.e. `*` as opposed to something like `3*4`).

### Enter-if-nonzero brackets `{`, `}`

At an open enter-if-nonzero bracket, the interpreter checks whether the global byte is `0`. If _not_, then the code's program counter enters the brackets and goes along until it hits the corresponding close enter-if-nonzero bracket. Once the program counter reaches the close enter-if-nonzero bracket, it goes back to the corresponding open enter-if-unequal bracket, and the condition (whether the payload of the cell under the read-write head is equal) is checked again. One can think of this as a while loop with an implicit counter down to zero.

### Traverse down `v` and traverse up `^`

Traverse down, as its name suggests, tells the interpreter to either follow the link in the cell under the read-write head to a child Rubik's Cube if it exists, or creates a new blank Rubik's Cube. Traverse up tells the interpreter to go back up to the parent node that links to the current Rubik's Cube. If traverse up is called at the root node, it does nothing. Usage for both commands is simply `^` or `v` alone.

**N.B.: these single-char commands still need to be whitespace delimited! Even the brackets!**

### Comments `#`

Lines or parts of lines beginning with `#` are commented out.

```[code, if applicable] # This is a standard, valid comment. ```
