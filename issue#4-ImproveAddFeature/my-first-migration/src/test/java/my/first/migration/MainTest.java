package my.first.migration;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Тестовый класс для класса Main.
 * Содержит unit-тесты для проверки функциональности приложения.
 * 
 * @author ScalabreseGD
 * @version 1.0
 */
public class MainTest {
    
    private Main.WsRecord wsRecord;
    
    /**
     * Инициализация перед каждым тестом.
     * Создает новый экземпляр WsRecord.
     */
    @BeforeEach
    void setUp() {
        wsRecord = new Main.WsRecord();
    }
    
    /**
     * Тестирование констант класса Main.
     */
    @Test
    void testConstants() {
        assertEquals(10, Main.MAX_NAME_LENGTH);
        assertEquals(10, Main.MAX_VALUE_LENGTH);
        assertEquals(20, Main.MAX_DESCRIPTION_LENGTH);
        assertEquals(10, Main.DEFAULT_PADDING_LENGTH);
    }
    
    /**
     * Тестирование конструктора по умолчанию WsRecord.
     */
    @Test
    void testWsRecordDefaultConstructor() {
        assertEquals("          ", wsRecord.getName());
        assertEquals("          ", wsRecord.getValue());
        assertEquals("          ", wsRecord.getBlank());
        assertEquals("                    ", wsRecord.getDescription());
        assertEquals("", wsRecord.getTimestamp());
        assertFalse(wsRecord.getFlag());
        assertEquals("false", wsRecord.getFlagAsString());
    }
    
    /**
     * Тестирование установки флага в состояние enabled.
     */
    @Test
    void testWsRecordSetFlagEnabled() {
        wsRecord.setFlagEnabled();
        assertTrue(wsRecord.getFlag());
        assertEquals("true", wsRecord.getFlagAsString());
    }

    /**
     * Тестирование установки флага в состояние disabled.
     */
    @Test
    void testWsRecordSetFlagDisabled() {
        wsRecord.setFlag(true);
        wsRecord.setFlagDisabled();
        assertFalse(wsRecord.getFlag());
        assertEquals("false", wsRecord.getFlagAsString());
    }
    
    /**
     * Тестирование установки флага через setFlag.
     */
    @Test
    void testWsRecordSetFlag() {
        wsRecord.setFlag(true);
        assertTrue(wsRecord.getFlag());
        assertEquals("true", wsRecord.getFlagAsString());
        
        wsRecord.setFlag(false);
        assertFalse(wsRecord.getFlag());
        assertEquals("false", wsRecord.getFlagAsString());
    }

    /**
     * Тестирование метода getDisplayString.
     */
    @Test
    void testWsRecordGetDisplayString() {
        wsRecord.setName("Test      ");
        wsRecord.setValue("Value     ");
        wsRecord.setDescription("Test Description  ");
        wsRecord.setTimestamp("2023-01-01 12:00:00");
        wsRecord.setFlag(false);
        String expected = "Test      Value               false Description: Test Description Timestamp: 2023-01-01 12:00:00";
        assertEquals(expected, wsRecord.getDisplayString());
    }
    
    /**
     * Тестирование сеттеров и геттеров WsRecord.
     */
    @Test
    void testWsRecordSettersAndGetters() {
        wsRecord.setName("New Name  ");
        wsRecord.setValue("New Value ");
        wsRecord.setBlank("New Blank ");
        wsRecord.setDescription("New Description    ");
        wsRecord.setTimestamp("2023-01-01 12:00:00");
        wsRecord.setFlag(true);
        
        assertEquals("New Name  ", wsRecord.getName());
        assertEquals("New Value ", wsRecord.getValue());
        assertEquals("New Blank ", wsRecord.getBlank());
        assertEquals("New Description    ", wsRecord.getDescription());
        assertEquals("2023-01-01 12:00:00", wsRecord.getTimestamp());
        assertTrue(wsRecord.getFlag());
        assertEquals("true", wsRecord.getFlagAsString());
    }

    /**
     * Тестирование логики генерации JSON.
     */
    @Test
    void testJsonGenerationLogic() {
        wsRecord.setName("Test Name ");
        wsRecord.setValue("Test Value ");
        wsRecord.setDescription("Test Description  ");
        wsRecord.setTimestamp("2023-01-01 12:00:00");
        wsRecord.setFlag(true);

        String jsonContent = String.format(
                Main.JSON_TEMPLATE,
                wsRecord.getName().trim(),
                wsRecord.getValue().trim(),
                wsRecord.getFlag(),
                wsRecord.getDescription().trim(),
                wsRecord.getTimestamp()
        );

        String expectedJson = "{\"name\":\"Test Name\",\"value\":\"Test Value\",\"enabled\":true,\"description\":\"Test Description\",\"timestamp\":\"2023-01-01 12:00:00\"}";
        assertEquals(expectedJson, jsonContent);
    }

    /**
     * Тестирование того, что генерация JSON не выбрасывает исключений.
     */
    @Test
    void testJsonGenerationDoesNotThrowException() {
        wsRecord.setName("Test      ");
        wsRecord.setValue("Value     ");
        wsRecord.setDescription("Description       ");
        wsRecord.setTimestamp("2023-01-01 12:00:00");
        wsRecord.setFlag(false);

        assertDoesNotThrow(() -> {
            String.format(
                    Main.JSON_TEMPLATE,
                    wsRecord.getName().trim(),
                    wsRecord.getValue().trim(),
                    wsRecord.getFlag(),
                    wsRecord.getDescription().trim(),
                    wsRecord.getTimestamp()
            );
        });
    }
    
    /**
     * Тестирование метода padToLength.
     */
    @Test
    void testPadToLength() {
        assertEquals("Hello     ", Main.padToLength("Hello", 10));
        assertEquals("Test", Main.padToLength("Test", 4));
        assertEquals("LongString", Main.padToLength("LongStringIsTooLong", 10));
        assertEquals("     ", Main.padToLength("", 5));
        assertEquals("A    ", Main.padToLength("A", 5));
    }
    
    /**
     * Тестирование padToLength с точной длиной.
     */
    @Test
    void testPadToLengthWithExactLength() {
        assertEquals("Exactly10 ", Main.padToLength("Exactly10", 10));
        assertEquals("Test1234  ", Main.padToLength("Test1234", 10));
    }
    
    /**
     * Тестирование padToLength с null входными данными.
     */
    @Test
    void testPadToLengthWithNullInput() {
        assertEquals("          ", Main.padToLength(null, 10));
    }
    
    /**
     * Тестирование createWsRecordFromArgs без аргументов.
     */
    @Test
    void testCreateWsRecordFromArgsWithNoArgs() {
        Main.WsRecord record = Main.createWsRecordFromArgs(new String[]{});
        assertEquals(Main.DEFAULT_NAME, record.getName());
        assertEquals(Main.DEFAULT_VALUE, record.getValue());
        assertEquals(Main.DEFAULT_DESCRIPTION, record.getDescription().trim());
        assertTrue(record.getFlag());
        assertNotNull(record.getTimestamp());
    }
    
    /**
     * Тестирование того, что createWsRecordFromArgs всегда использует значения по умолчанию.
     */
    @Test
    void testCreateWsRecordFromArgsAlwaysUsesDefaults() {
        // Метод всегда возвращает запись с значениями по умолчанию, независимо от аргументов
        Main.WsRecord record1 = Main.createWsRecordFromArgs(new String[]{});
        Main.WsRecord record2 = Main.createWsRecordFromArgs(new String[]{"John", "Doe"});
        Main.WsRecord record3 = Main.createWsRecordFromArgs(new String[]{"Alice", "Smith", "true", "Test Description"});
        
        assertEquals(record1.getName(), record2.getName());
        assertEquals(record1.getValue(), record2.getValue());
        assertEquals(record1.getDescription(), record3.getDescription());
        assertEquals(record1.getFlag(), record3.getFlag());
    }
    
    /**
     * Тестирование основной логики main метода без System.exit.
     */
    @Test
    void testMainMethodLogicWithoutSystemExit() {
        String[] args = {};
        
        Main.WsRecord wsRecord = Main.createWsRecordFromArgs(args);
        String jsonContent = String.format(
            Main.JSON_TEMPLATE,
            wsRecord.getName().trim(),
            wsRecord.getValue().trim(),
            wsRecord.getFlag(),
            wsRecord.getDescription().trim(),
            wsRecord.getTimestamp()
        );
        
        assertEquals(Main.DEFAULT_NAME, wsRecord.getName());
        assertEquals(Main.DEFAULT_VALUE, wsRecord.getValue());
        assertEquals(Main.DEFAULT_DESCRIPTION, wsRecord.getDescription().trim());
        assertTrue(wsRecord.getFlag());
        assertNotNull(wsRecord.getTimestamp());
        assertTrue(jsonContent.contains("Test Name"));
        assertTrue(jsonContent.contains("Test Value"));
        assertTrue(jsonContent.contains("Default Description"));
    }
    
    /**
     * Тестирование согласованности метода toString.
     */
    @Test
    void testWsRecordToStringConsistency() {
        wsRecord.setName("Name      ");
        wsRecord.setValue("Value     ");
        wsRecord.setDescription("Description       ");
        wsRecord.setTimestamp("2023-01-01 12:00:00");
        wsRecord.setFlag(true);
        
        String displayString = wsRecord.getDisplayString();
        String manualString = wsRecord.getName() + wsRecord.getValue() + 
                             wsRecord.getBlank() + 
                             (wsRecord.getFlag() ? "true " : "false") +
                             " Description: " + wsRecord.getDescription().trim() + 
                             " Timestamp: " + wsRecord.getTimestamp();
        
        assertEquals(manualString, displayString);
    }
    
    /**
     * Тестирование формата JSON шаблона.
     */
    @Test
    void testJsonTemplateFormat() {
        String formatted = String.format(Main.JSON_TEMPLATE, "test", "value", true, "desc", "2023-01-01 12:00:00");
        assertEquals("{\"name\":\"test\",\"value\":\"value\",\"enabled\":true,\"description\":\"desc\",\"timestamp\":\"2023-01-01 12:00:00\"}", formatted);
        
        formatted = String.format(Main.JSON_TEMPLATE, "test", "value", false, "desc", "2023-01-01 12:00:00");
        assertEquals("{\"name\":\"test\",\"value\":\"value\",\"enabled\":false,\"description\":\"desc\",\"timestamp\":\"2023-01-01 12:00:00\"}", formatted);
    }
    
    /**
     * Тестирование чтения записей из файла.
     *
     * @param tempDir временная директория для тестов
     * @throws Exception если произошла ошибка
     */
    @Test
    void testReadRecordsFromFile(@TempDir Path tempDir) throws Exception {
        // Создаем тестовый файл
        Path testFile = tempDir.resolve("test.csv");
        List<String> lines = List.of(
            "John,Doe,true,User John",
            "Jane,Smith,false,User Jane"
        );
        Files.write(testFile, lines);
        
        List<Main.WsRecord> records = Main.readRecordsFromFile(testFile.toString());
        
        assertEquals(2, records.size());
        
        // Проверяем первую запись
        Main.WsRecord first = records.get(0);
        assertEquals("John      ", first.getName());
        assertEquals("Doe       ", first.getValue());
        assertTrue(first.getFlag());
        assertNotNull(first.getTimestamp());
        
        // Проверяем вторую запись
        Main.WsRecord second = records.get(1);
        assertEquals("Jane      ", second.getName());
        assertEquals("Smith     ", second.getValue());
        assertFalse(second.getFlag());
        assertNotNull(second.getTimestamp());
    }
    
    /**
     * Тестирование чтения записей из файла с пустыми строками.
     *
     * @param tempDir временная директория для тестов
     * @throws Exception если произошла ошибка
     */
    @Test
    void testReadRecordsFromFileWithEmptyLines(@TempDir Path tempDir) throws Exception {
        Path testFile = tempDir.resolve("test.csv");
        List<String> lines = List.of(
            "John,Doe,true,Desc1",
            "",
            "Jane,Smith,false,Desc2"
        );
        Files.write(testFile, lines);
        
        List<Main.WsRecord> records = Main.readRecordsFromFile(testFile.toString());
        
        assertEquals(2, records.size()); // Пустые строки должны быть пропущены
    }
    
    /**
     * Тестирование чтения записей из файла с неверным форматом.
     *
     * @param tempDir временная директория для тестов
     * @throws Exception если произошла ошибка
     */
    @Test
    void testReadRecordsFromFileWithInvalidFormat(@TempDir Path tempDir) throws Exception {
        Path testFile = tempDir.resolve("test.csv");
        List<String> lines = List.of("John"); // Только одно поле
        Files.write(testFile, lines);
        
        assertThrows(IllegalArgumentException.class, () -> {
            Main.readRecordsFromFile(testFile.toString());
        });
    }
    
    /**
     * Тестирование чтения записей из файла с длинным именем.
     *
     * @param tempDir временная директория для тестов
     * @throws Exception если произошла ошибка
     */
    @Test
    void testReadRecordsFromFileWithLongName(@TempDir Path tempDir) throws Exception {
        Path testFile = tempDir.resolve("test.csv");
        List<String> lines = List.of("VeryLongNameExceeding10Chars,Value,true,Desc");
        Files.write(testFile, lines);
        
        assertThrows(IllegalArgumentException.class, () -> {
            Main.readRecordsFromFile(testFile.toString());
        });
    }
    
    /**
     * Тестирование чтения записей из файла с длинным описанием.
     *
     * @param tempDir временная директория для тестов
     * @throws Exception если произошла ошибка
     */
    @Test
    void testReadRecordsFromFileWithLongDescription(@TempDir Path tempDir) throws Exception {
        Path testFile = tempDir.resolve("test.csv");
        List<String> lines = List.of("John,Value,true,VeryLongDescriptionExceeding20Chars");
        Files.write(testFile, lines);
        
        assertThrows(IllegalArgumentException.class, () -> {
            Main.readRecordsFromFile(testFile.toString());
        });
    }
    
    /**
     * Тестирование чтения записей из файла с неверным флагом.
     *
     * @param tempDir временная директория для тестов
     * @throws Exception если произошла ошибка
     */
    @Test
    void testReadRecordsFromFileWithInvalidFlag(@TempDir Path tempDir) throws Exception {
        Path testFile = tempDir.resolve("test.csv");
        List<String> lines = List.of("John,Doe,invalid,Desc");
        Files.write(testFile, lines);
        
        assertThrows(IllegalArgumentException.class, () -> {
            Main.readRecordsFromFile(testFile.toString());
        });
    }
    
    /**
     * Тестирование генерации JSON массива.
     */
    @Test
    void testArrayJsonGeneration() {
        List<String> jsonRecords = List.of(
            "{\"name\":\"John\",\"value\":\"Doe\",\"enabled\":true,\"description\":\"Desc1\",\"timestamp\":\"2023-01-01 12:00:00\"}",
            "{\"name\":\"Jane\",\"value\":\"Smith\",\"enabled\":false,\"description\":\"Desc2\",\"timestamp\":\"2023-01-01 12:00:00\"}"
        );
        
        String arrayJson = String.format(Main.ARRAY_JSON_TEMPLATE, String.join(",", jsonRecords));
        String expected = "[{\"name\":\"John\",\"value\":\"Doe\",\"enabled\":true,\"description\":\"Desc1\",\"timestamp\":\"2023-01-01 12:00:00\"}," +
                         "{\"name\":\"Jane\",\"value\":\"Smith\",\"enabled\":false,\"description\":\"Desc2\",\"timestamp\":\"2023-01-01 12:00:00\"}]";
        
        assertEquals(expected, arrayJson);
    }
    
    /**
     * Тестирование padToLength с null.
     */
    @Test
    void testPadToLengthWithNull() {
        String result = Main.padToLength(null, 5);
        assertEquals("     ", result);
    }
    
    /**
     * Тестирование генерации временной метки.
     */
    @Test
    void testGenerateTimestamp() {
        String timestamp = Main.generateTimestamp();
        assertNotNull(timestamp);
        // Проверяем формат timestamp
        assertTrue(timestamp.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }
    
    /**
     * Тестирование записи в файл.
     *
     * @param tempDir временная директория для тестов
     * @throws Exception если произошла ошибка
     */
    @Test
    void testWriteToFile(@TempDir Path tempDir) throws Exception {
        Path testFile = tempDir.resolve("output.json");
        String content = "{\"name\":\"test\",\"value\":\"value\",\"enabled\":true,\"description\":\"desc\",\"timestamp\":\"2023-01-01 12:00:00\"}";
        
        Main.writeToFile(testFile.toString(), content);
        
        assertTrue(Files.exists(testFile));
        String fileContent = Files.readString(testFile);
        assertEquals(content, fileContent);
    }
    
    /**
     * Тестирование парсинга опции вывода.
     */
    @Test
    void testOutputOptionParsing() {
        String[] args = {"Test", "Value", "true", "Description", "-o", "output.json"};
        String outputFile = null;
        
        for (int i = 0; i < args.length; i++) {
            if (Main.OUTPUT_OPTION_PREFIX.equals(args[i]) && i + 1 < args.length) {
                outputFile = args[i + 1];
                break;
            }
        }
        
        assertEquals("output.json", outputFile);
    }
    
    /**
     * Комплексное тестирование padding.
     */
    @Test
    void testComprehensivePadding() {
        // Test various padding scenarios
        assertEquals("Hi        ", Main.padToLength("Hi", 10));
        assertEquals("HelloWorld", Main.padToLength("HelloWorld", 10));
        assertEquals("HelloWorl", Main.padToLength("HelloWorldTooLong", 9));
        assertEquals("     ", Main.padToLength("", 5));
        assertEquals("          ", Main.padToLength(null, 10));
    }

    /**
     * Тестирование генерации красивого JSON.
     *
     * @throws Exception если произошла ошибка
     */
    @Test
    void testGeneratePrettyJson() throws Exception {
        wsRecord.setName("Test      ");
        wsRecord.setValue("Value     ");
        wsRecord.setDescription("Test Description  ");
        wsRecord.setTimestamp("2023-01-01 12:00:00");
        wsRecord.setFlag(true);
        
        String prettyJson = Main.generatePrettyJson(wsRecord);
        
        assertNotNull(prettyJson);
        assertTrue(prettyJson.contains("\n")); // Должен содержать переносы строк
        assertTrue(prettyJson.contains("  ")); // Должен содержать отступы
        assertTrue(prettyJson.contains("\"name\": \"Test\""));
        assertTrue(prettyJson.contains("\"value\": \"Value\""));
        assertTrue(prettyJson.contains("\"enabled\": true"));
    }

    /**
     * Тестирование генерации красивого JSON с флагом false.
     *
     * @throws Exception если произошла ошибка
     */
    @Test
    void testGeneratePrettyJsonWithFalseFlag() throws Exception {
        wsRecord.setName("Test      ");
        wsRecord.setValue("Value     ");
        wsRecord.setDescription("Test Description  ");
        wsRecord.setTimestamp("2023-01-01 12:00:00");
        wsRecord.setFlag(false);
        
        String prettyJson = Main.generatePrettyJson(wsRecord);
        
        assertNotNull(prettyJson);
        assertTrue(prettyJson.contains("\"enabled\": false"));
    }

    /**
     * Тестирование согласованности формата красивого JSON.
     *
     * @throws Exception если произошла ошибка
     */
    @Test
    void testPrettyJsonFormatConsistency() throws Exception {
        wsRecord.setName("Test      ");
        wsRecord.setValue("Value     ");
        wsRecord.setDescription("Description       ");
        wsRecord.setTimestamp("2023-01-01 12:00:00");
        wsRecord.setFlag(true);
        
        String prettyJson = Main.generatePrettyJson(wsRecord);
        
        // Проверяем, что JSON содержит все ожидаемые поля с правильным форматированием
        assertTrue(prettyJson.contains("\"name\": \"Test\""));
        assertTrue(prettyJson.contains("\"value\": \"Value\""));
        assertTrue(prettyJson.contains("\"enabled\": true"));
        assertTrue(prettyJson.contains("\"description\": \"Description\""));
        assertTrue(prettyJson.contains("\"timestamp\": \"2023-01-01 12:00:00\""));
    }
}