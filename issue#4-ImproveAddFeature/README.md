# Issue 4: Improve Code Quality and Add Features

## Project Description

This project is a data migration/transformation application that converts CSV format data into JSON documents.

**Input Data**: 
- Data can be located in `data.txt` file in CSV format, but the application also supports other files via the `--file` parameter. The `data.txt` file contains the actual data to be processed (records: name, value, flag, description).
- Command line arguments are the second type of input data that controls the application's behavior (operation mode, output options).

**Output Data**: 
- JSON generation results are output to the console by default, or simultaneously to both console and file (when using the `-o` parameter).
- Flexible output: support for both regular and "pretty" JSON format.

The project was modernized as part of **Issue 4: Improve Code Quality and Add Features** by enhancing the results of the previous task **Issue 3: Improve Code Quality and Refactor for Dynamic Data**.

## Modernization Roadmap

### Phase 1: Mandatory Refactoring (Code Quality)

#### Step 1.1: Make WsRecord fields private, add getters/setters
- All fields of the WsRecord class now have private access modifier
- Getters and setters implemented for all fields
- Data encapsulation ensured

#### Step 1.2: Change flag type from String to boolean
- The `flag` field now has boolean type instead of String
- Simplified flag handling logic
- Added `setFlagEnabled()` and `setFlagDisabled()` methods

#### Step 1.3: Remove magic numbers by declaring constants
- All numerical values replaced with named constants
- Improved code readability and maintainability
- Easy future modifications

#### Step 1.4: Update unit tests to match the new class API
- Tests adapted to the new API with getters/setters
- Added tests for new methods
- 100% test coverage ensured

#### Step 1.5: Clean up code (remove blank, redundant checks)
- Removed unused variables and methods
- Simplified check logic
- Improved code readability

### Phase 2: Feature Addition (by priority)

#### Step 2.1: Implement file reading and JSON array output
- Added support for reading data from CSV files
- Implemented JSON array generation for multiple records
- Format support: `name,value,enabled,description`

#### Step 2.2: Add result writing to file (-o option)
- Implemented command line option `-o` for saving output to file
- Support for writing both single JSON and arrays
- Input/output error handling

#### Step 2.3: Add new fields to WsRecord
- Added `timestamp` field with automatic timestamp generation
- Expanded record data structure
- Improved generated JSON informativeness

#### Step 2.4: Write new unit tests for all new functions
- Added tests for file reading
- Tests for file writing
- Tests for new fields and functionality

### Phase 3: Final Polish

#### Step 3.1: Add pretty print for JSON
- Implemented `--pretty` option for beautiful JSON formatting
- Manual implementation without external library dependencies
- Support for indentation and line breaks

#### Step 3.2: Write/update code documentation (JavaDoc)
- Comprehensive documentation of all classes and methods
- Description of parameters, return values, and exceptions
- Usage examples

#### Step 3.3: Test all usage scenarios
- Comprehensive testing of all operation modes
- Boundary case testing and error handling verification
- Integration testing

## How to Build and Run

### Requirements
- Java 11 or higher
- Maven 3.6 or higher
- JUnit 5 (automatically included via Maven)

### Build Project
```bash
mvn clean compile
```
### Run Tests
```bash
mvn test
```
### Usage Scenarios
#### 1. Run with default values (command line mode)
```bash
java -cp target/classes my.first.migration.Main
```
2. Run with output saved to file
```bash
java -cp target/classes my.first.migration.Main -o output.json
```
3. Run with pretty JSON output
bash
java -cp target/classes my.first.migration.Main --pretty
4. Run with data reading from file
bash
java -cp target/classes my.first.migration.Main --file data.txt
5. Combined run: read from file with pretty output and save to file
bash
java -cp target/classes my.first.migration.Main --file data.txt --pretty -o output.json
Input File Format
Data file should be in CSV format:

text
name,value,enabled,description
John,Doe,true,User John
Jane,Smith,false,User Jane
Output Data Examples
Standard JSON output:
json
{"name":"Test Name","value":"Test Value","enabled":true,"description":"Default Description","timestamp":"2023-10-01 12:00:00"}
Pretty JSON output (with --pretty):
json
{
  "name": "Test Name",
  "value": "Test Value",
  "enabled": true,
  "description": "Default Description",
  "timestamp": "2023-10-01 12:00:00"
}
JSON array for multiple records:
json
[
  {"name":"John","value":"Doe","enabled":true,"description":"User John","timestamp":"2023-10-01 12:00:00"},
  {"name":"Jane","value":"Smith","enabled":false,"description":"User Jane","timestamp":"2023-10-01 12:00:00"}
]
Implementation Features
Encapsulation: All WsRecord class fields are protected, accessed via getters/setters

Type Safety: Using boolean instead of String for flags

Constants: No magic numbers, all values extracted to constants

Error Handling: Comprehensive exception handling

Testing: Complete unit test coverage

Flexibility: Support for various operation modes via command line arguments

Metadata
Novak Evgeniy
Email: novakevgeniy1953@gmail.com
September 23, 2025
