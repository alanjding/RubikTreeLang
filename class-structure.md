# General class structure
This is a little file with a quick initial blueprint of some notes to self and how I intend to structure this project.

### Interpreter class

**Instance vars**
- Code File (preprocessed into an array with one command in each space)
- Pointer to RubiksTrie instance (the thing above the root RubiksCube)
- Pointer to current RubiksCube (which in turn allows us to get the node under the read-write head)
- Instruction pointer in code file (integer array index)
- Maintain a stack of bracket locations to know where to return to at }

**Methods**
- getRubiksTrie (mostly for the visualizer)
- getCurrentCube (mostly for the visualizer)
- processNextCommand - general stuff: a big switch statement that calls smaller methods with the proper args depending on what's read
- Methods for each command (potentially modifies RubiksCube pointer, instruction pointer)
- processRemainingCommands - run until IP reaches end of code file array

### RubiksTrie class

**Instance vars**
- Pointer to a RubiksCube class (the root cube)
- Global byte
- numCubes (recall these are trie nodes, different from nodes inside the cube itself)

**Methods**
- getGlobalByte
- setGlobalByte
- incrementNumCubes
- special toString (general structure only) for visualizer

### RubiksCube class

**Instance vars**
- Array of 24 nodes (put mapping from index to actual location in code comments)
- Parent pointer
- Pointer back up to RubiksTrie to get the global byte/index of 
- Cube index -- N.B. in constructor set this to the current numCubes in RubiksTrie then increment numCubes in RubiksTrie

**Methods**
- Twist to be made, taking two arguments (the face; and 1, 2, or prime)
    - Enumerate the faces and lateral stickers involved in the turn and create general permute method
- getRWNode (get the node under the read-write head--index 0 of array)
- special toString for visualizer (print cube index and utilize node toString for each sticker)
- getCubeIndex

### Node class

**Instance vars**
- 1 unsigned byte payload
- 8 byte pointer to another RubiksCube (or of course null)

**Methods**
- setPayload
- getPayload
- incrementPayloadBy(byte x)
- decrementPayloadBy(byte x)
- multiplyPayloadBy(byte x)
- dividePayloadBy(byte x)
- modPayloadBy(byte x)
- toString for visualizer (2 hex digits + '>' if RubiksCube pointer not null; '.' if null)

### VisualizerMethods class

**Instance vars**
- Interpreter (created from raw code file passed into this class)

**Methods**
- visualizeRubiksTrie (use RubiksTrie toString)
- visualizeCurrentRubiksCube (getCurrentCube from Interpreter and use RubiksCube toString)
- executeNextInstruction (call processNextCommand in Interpreter)

### Other things to include

InterpreterDriver? (a.k.a. **RubikTreeLang**)

VisualizerDriver? (a.k.a. **Visualizer**)

**Example RubikTreeLang files**
