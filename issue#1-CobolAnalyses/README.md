# Issue 1: Analyze the Existing COBOL Project

## 1. Executive Summary
This COBOL program is a **proof-of-concept** example demonstrating the use of the modern **GnuCOBOL compiler extension** — the `JSON GENERATE` operation.  
Its sole practical purpose is to showcase the ability to transform COBOL data structures into JSON format.

**Core functionality**:  
- Creates an in-memory record with test data.  
- Converts it into a JSON string using the built-in generation feature.  
- Prints the result together with service information (the actual string length).  

Unlike typical COBOL systems that process transaction streams from external files, this program operates **only on hardcoded data** defined directly in the source code.  
It does **not** interact with external data sources or accept user input, which confirms its **purely demonstrational** nature.  

For the migration project, the COBOL code remains **unchanged** and serves only as a **baseline** for validating correctness.  
It is required solely to understand which functionality must be reproduced during migration.

---

## 2. COBOL Data Structures and Algorithms

### 2.1. Identified Data Structures
All data structures are declared in the **WORKING-STORAGE SECTION**:

- **`ws-json-output`** — `PIC X(256)`  
  Fixed-length string buffer for JSON output.  
  *Limitation*: may truncate larger JSON documents.  

- **`ws-json-char-count`** — `PIC 9(4)`  
  Numeric field.  
  Purpose: stores the number of characters actually written to the JSON string.  
  Max value: 9999.  

- **`ws-record`** — group variable (composite structure).  
  Purpose: the main data structure to be converted into JSON.  
  Fields:  
  - `ws-record-name`: string, 10 chars (`PIC X(10)`)  
  - `ws-record-value`: string, 10 chars (`PIC X(10)`)  
  - `ws-record-blank`: unused string, 10 chars (`PIC X(10)`), ignored during JSON generation  
  - `ws-record-flag`: string, 5 chars (`PIC X(5)`)  
    - 88-level condition `ws-record-flag-enabled`: `"true"`  
    - 88-level condition `ws-record-flag-disabled`: `"false"`  

All variables are placed in **Working-Storage Section**, since they are internal runtime data, not inputs or file-based.

---

### 2.2. Key Algorithms

- **Record Initialization**  
  Prepares `ws-record` with valid initial values using `MOVE` and `SET`, including 88-level flag initialization.  

- **JSON Generation Algorithm (core)**  
  Uses the `JSON GENERATE` statement:  
  - `FROM ws-record` → source data  
  - `COUNT IN ws-json-char-count` → captures result length  
  - `NAME OF` mapping for custom JSON keys:  
    - `ws-record-name` → `"name"`  
    - `ws-record-value` → `"value"`  
    - `ws-record-flag` → `"enabled"`  

- **Error Handling**  
  Implemented with `ON EXCEPTION / NOT ON EXCEPTION`:  
  - On failure → print diagnostic message and terminate.  
  - On success → confirm successful JSON generation.  

- **Output/Display Algorithm**  
  Displays:  
  - Original `ws-record` field values  
  - Generated JSON string (trimmed of trailing spaces)  
  - JSON string length  
  - Final completion message  

---

## 3. Project Dependencies

### 3.1. Compile-Time Dependencies
- **GnuCOBOL with JSON support**  
  Required. Must be built with:  
  - `./configure --with-json --without-db` (minimal build)  
  - or `./configure --with-json` (with DB support)  

- **json-c library (≥ 4.0.0)**  
  Required. Provides JSON handling functionality.  
  Source: [json-c GitHub](https://github.com/json-c/json-c)  

---

### 3.2. Runtime Dependencies
- `json-c` library — must be available as a dynamic library (`.so / .dll`).  
- Standard GnuCOBOL runtime libraries (e.g., `libcob`, `libgmp`).  

---

### 3.3. System Requirements
- **OS**: Unix-like systems (Linux, macOS)  
- **Tools**: `cobc` compiler, standard build environment  
- **Windows**: possibly via MinGW, Cygwin, or MSYS2 (support is limited)  

---

### 3.4. Not Required
- External data files (no FILE SECTION)  
- Databases (compiled with `--without-db`)  
- JCL scripts (not needed)  
- External system utilities  

---

### 3.5. Assumptions and Risks
- **Critical dependency**: Program cannot compile or run without GnuCOBOL (JSON-enabled) and json-c.  
- **Portability**: Limited portability due to specific compiler build requirements.  
- **Migration risks**: While migrating to Java, the following must be addressed:  
  - Choose a JSON library (e.g., Jackson, Gson).  
  - Handle COBOL-to-Java data transformation differences.  
  - Implement robust error handling for JSON generation.  

---

## Metadata
- **Analysis Date**: 30/08/2025  
- **Analyst**: Evgeniy Novak  
- **Email**: novakevgeniy1953@gmail.com  
