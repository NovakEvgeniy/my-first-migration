# Issue 2: Initial Conversion COBOL → Java

## Project Description
This project demonstrates an initial, "naïve" approach to converting a simple COBOL program that uses `JSON GENERATE` into equivalent Java code.  
The goal is not to build a perfect Java architecture, but to illustrate the direct transfer of logic and data structures.

---

## Original COBOL Code (json-generate.cbl)
The COBOL program creates a JSON document from the `ws-record` using the `JSON GENERATE` statement and performs the following steps:

- Declares working variables (`ws-json-output`, `ws-json-char-count`).  
- Defines the record `ws-record` with the following fields:  
  - `ws-record-name` — name, 10 characters,  
  - `ws-record-value` — value, 10 characters,  
  - `ws-record-blank` — blank field, 10 characters,  
  - `ws-record-flag` — flag (5 characters) with level-88 conditions (`enabled`/`disabled`).  
- Populates the fields using `MOVE` and `SET` statements.  
- Outputs the result using `DISPLAY` statements.  

---

## Converted Java Code
A direct, literal translation of the COBOL code into Java. The same steps are reproduced:

- Working variables `wsJsonOutput` and `wsJsonCharCount`.  
- A `WsRecord` class to describe the record structure with fields `name`, `value`, `blank`, `flag`.  
- Methods `setFlagEnabled()` and `setFlagDisabled()` reproduce COBOL’s level-88 conditions.  
- `MOVE` and `SET` statements are replaced by direct assignments.  
- The `JSON GENERATE` statement is manually implemented using `String.format(...)`.  
- `DISPLAY` statements are replaced by `System.out.println(...)`.  
- To match COBOL output, the character count is printed with leading zeros (`%04d`).  

---

## Key Conversion Decisions

1. **Fixed-length COBOL fields → Java strings**  
   In COBOL, strings have a fixed length (e.g., `pic x(10)`). In Java, this limitation does not exist, so spaces are added manually and then trimmed using `.trim()`.  

2. **Level-88 conditions → class methods**  
   COBOL defines boolean-like conditions with `88`. In Java, this logic is implemented with explicit methods (`setFlagEnabled()` and `setFlagDisabled()`).  

3. **`JSON GENERATE` → manual JSON construction**  
   Since Java has no built-in equivalent, JSON is constructed manually with `String.format`. This is a simplified version, without external libraries such as Jackson or Gson.  

4. **`DISPLAY` → console output**  
   COBOL sequentially prints fields and strings. In Java, this is implemented with `System.out.println` and `System.out.printf`.  

---

## Limitations and Simplifications

- JSON generation is done manually, without third-party libraries.  
- Fixed-length field behavior is emulated with padding, which is not idiomatic in Java.  
- Exception handling (`on exception`) is simplified to a `try/catch` block with program termination.  
- The code is a demonstration of conversion and is not intended for production use.  

---

## How to Run

### COBOL
1. Install [GnuCOBOL](https://gnucobol.sourceforge.io/).  
2. Ensure JSON support was enabled during build (`--with-json`).  
3. Compile and run:  
   ```bash
   cobc -x json-generate.cbl
   ./json-generate
Java
1.	Install JDK 17+
2.	Save the file JsonGenerateExample.java under the folder my_first_migration/.
3.	Compile:
4.	javac my_first_migration/JsonGenerateExample.java
5.	Run:
6.	java my_first_migration.JsonGenerateExample
________________________________________
Conclusion
This example clearly illustrates that initial conversion often involves directly transferring the algorithm into the target language with maximum preservation of the original structure.
Such an approach quickly produces a working prototype, which should later be refactored and improved to align with the idioms and best practices of the target language (Java).
