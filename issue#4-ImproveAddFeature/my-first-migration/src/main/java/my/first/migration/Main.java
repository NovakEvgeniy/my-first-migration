package my.first.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.IllegalFormatException;

/**
 * Главный класс приложения для генерации JSON документов из записей.
 * Поддерживает два режима работы: командная строка и файловый режим.
 * 
 * @author ScalabreseGD
 * @version 1.0
 */
public class Main {
    
    // Константы для замены магических чисел
    /** Максимальная длина имени */
    public static final int MAX_NAME_LENGTH = 10;
    /** Максимальная длина значения */
    public static final int MAX_VALUE_LENGTH = 10;
    /** Максимальная длина описания */
    public static final int MAX_DESCRIPTION_LENGTH = 20;
    /** Длина отступа по умолчанию */
    public static final int DEFAULT_PADDING_LENGTH = 10;
    /** Начальная длина JSON вывода */
    public static final int JSON_OUTPUT_INITIAL_LENGTH = 256;
    /** Минимальное количество требуемых аргументов */
    public static final int MIN_REQUIRED_ARGS = 2;
    
    // Константы для сообщений
    /** Префикс сообщений об ошибках */
    public static final String ERROR_PREFIX = "Error: ";
    /** Сообщение о использовании программы */
    public static final String USAGE_MESSAGE = "Usage: java Main [name value enabled description] [-o outputfile] [--pretty] OR java Main --file filename [-o outputfile] [--pretty]";
    /** Сообщение об ограничении длины имени */
    public static final String NAME_LIMIT_MESSAGE = "Name cannot exceed " + MAX_NAME_LENGTH + " characters";
    /** Сообщение об ограничении длины значения */
    public static final String VALUE_LIMIT_MESSAGE = "Value cannot exceed " + MAX_VALUE_LENGTH + " characters";
    /** Сообщение об ограничении длины описания */
    public static final String DESCRIPTION_LIMIT_MESSAGE = "Description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters";
    /** Сообщение о неверном флаге enabled */
    public static final String ENABLED_FLAG_MESSAGE = "Enabled flag must be 'true' or 'false'";
    /** Сообщение о минимальном количестве аргументов */
    public static final String MIN_ARGS_REQUIRED_MESSAGE = "At least name and value parameters are required";
    /** Сообщение об успешной генерации JSON */
    public static final String JSON_SUCCESS_MESSAGE = "JSON document successfully generated.";
    /** Сообщение об ошибке формата JSON */
    public static final String JSON_FORMAT_ERROR = "Error in JSON format: ";
    /** Сообщение об ошибке null значения */
    public static final String NULL_VALUE_ERROR = "Error: null value encountered: ";
    /** Сообщение о непредвиденной ошибке JSON */
    public static final String UNEXPECTED_JSON_ERROR = "Unexpected error generating JSON: ";
    /** Сообщение об ошибке null записи */
    public static final String NULL_RECORD_ERROR = "WsRecord or its fields are null";
    /** Сообщение об ошибке чтения файла */
    public static final String FILE_READ_ERROR = "Error reading file: ";
    /** Сообщение об ошибке записи в файл */
    public static final String FILE_WRITE_ERROR = "Error writing to file: ";
    /** Сообщение о неверном формате файла */
    public static final String INVALID_FILE_FORMAT = "Invalid file format. Expected format: name,value,enabled,description";
    /** Префикс файлового режима */
    public static final String FILE_MODE_PREFIX = "--file";
    /** Префикс опции вывода */
    public static final String OUTPUT_OPTION_PREFIX = "-o";
    /** Шаблон JSON массива */
    public static final String ARRAY_JSON_TEMPLATE = "[%s]";
    /** Сообщение о обработке файла */
    public static final String FILE_PROCESSING_MESSAGE = "Processing file: ";
    /** Сообщение о количестве обработанных записей */
    public static final String RECORDS_PROCESSED_MESSAGE = "Records processed: ";
    /** Сообщение о сохранении вывода */
    public static final String OUTPUT_SAVED_MESSAGE = "Output saved to: ";
    
    // JSON template constants
    /** Шаблон JSON документа */
    public static final String JSON_TEMPLATE = "{\"name\":\"%s\",\"value\":\"%s\",\"enabled\":%b,\"description\":\"%s\",\"timestamp\":\"%s\"}";
    /** Разделитель вывода */
    public static final String OUTPUT_SEPARATOR = "----------------------------";
    /** Формат подсчета символов */
    public static final String CHAR_COUNT_FORMAT = "JSON output character count: %04d%n";
    /** Сообщение о завершении */
    public static final String DONE_MESSAGE = "Done.";
    /** Префикс сгенерированного JSON */
    public static final String GENERATED_JSON_PREFIX = "Generated JSON for record: ";
    
    // Default values
    /** Имя по умолчанию */
    public static final String DEFAULT_NAME = "Test Name ";
    /** Значение по умолчанию */
    public static final String DEFAULT_VALUE = "Test Value ";
    /** Описание по умолчанию */
    public static final String DEFAULT_DESCRIPTION = "Default Description";
    /** Флаг по умолчанию */
    public static final boolean DEFAULT_FLAG = true;
    /** Строковое представление true */
    public static final String TRUE_STRING = "true";
    /** Строковое представление false */
    public static final String FALSE_STRING = "false";
    
    // Timestamp format
    /** Формат временной метки */
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // Pretty print option
    /** Префикс опции красивого вывода */
    public static final String PRETTY_OPTION_PREFIX = "--pretty";
    /** Сообщение о включении красивого вывода */
    public static final String PRETTY_ENABLED_MESSAGE = "Pretty print enabled";
    
    /**
     * Главный метод приложения.
     * Обрабатывает аргументы командной строки и запускает соответствующий режим работы.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        // Всегда используем значения по умолчанию при запуске из Eclipse
        System.out.println("Running with default values...");
        
        String outputFile = null;
        boolean prettyPrint = false;
        boolean fileMode = false;
        String filename = null;
        
        // Простая обработка только явных опций
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            if (OUTPUT_OPTION_PREFIX.equals(arg) && i + 1 < args.length) {
                outputFile = args[i + 1];
                i++; // Пропускаем значение файла
            } else if (PRETTY_OPTION_PREFIX.equals(arg)) {
                prettyPrint = true;
                System.out.println(PRETTY_ENABLED_MESSAGE);
            } else if (FILE_MODE_PREFIX.equals(arg) && i + 1 < args.length) {
                fileMode = true;
                filename = args[i + 1];
                i++; // Пропускаем имя файла
            }
            // Игнорируем все остальные аргументы
        }
        
        if (fileMode) {
            if (filename == null) {
                System.out.println(ERROR_PREFIX + "Filename required after --file");
                System.out.println(USAGE_MESSAGE);
                System.exit(1);
                return;
            }
            processFileMode(filename, outputFile, prettyPrint);
        } else {
            // Всегда используем значения по умолчанию для командного режима
            processCommandLineMode(new String[]{}, outputFile, prettyPrint);
        }
    }
    
    /**
     * Обрабатывает файловый режим работы.
     * Читает записи из файла, генерирует JSON и выводит результат.
     *
     * @param filename имя файла для чтения
     * @param outputFile имя файла для вывода (может быть null)
     * @param prettyPrint флаг красивого вывода
     */
    static void processFileMode(String filename, String outputFile, boolean prettyPrint) {
        try {
            System.out.println(FILE_PROCESSING_MESSAGE + filename);
            
            List<WsRecord> records = readRecordsFromFile(filename);
            List<String> jsonRecords = new ArrayList<>();
            
            for (WsRecord record : records) {
                String jsonContent;
                if (prettyPrint) {
                    jsonContent = generatePrettyJson(record);
                } else {
                    jsonContent = String.format(
                        JSON_TEMPLATE,
                        record.getName().trim(),
                        record.getValue().trim(),
                        record.getFlag(),
                        record.getDescription().trim(),
                        record.getTimestamp()
                    );
                }
                jsonRecords.add(jsonContent);
            }
            
            // Формируем массив JSON
            String arrayJson;
            if (prettyPrint) {
                // Для pretty print формируем массив с отступами
                StringBuilder arrayBuilder = new StringBuilder("[\n");
                for (int i = 0; i < jsonRecords.size(); i++) {
                    arrayBuilder.append("  ").append(jsonRecords.get(i).replace("\n", "\n  "));
                    if (i < jsonRecords.size() - 1) {
                        arrayBuilder.append(",");
                    }
                    arrayBuilder.append("\n");
                }
                arrayBuilder.append("]");
                arrayJson = arrayBuilder.toString();
            } else {
                arrayJson = String.format(ARRAY_JSON_TEMPLATE, String.join(",", jsonRecords));
            }
            int charCount = arrayJson.length();
            
            // Записываем в файл, если указана опция -o
            if (outputFile != null) {
                writeToFile(outputFile, arrayJson);
                System.out.println(OUTPUT_SAVED_MESSAGE + outputFile);
            }
            
            System.out.println(RECORDS_PROCESSED_MESSAGE + records.size());
            System.out.println(OUTPUT_SEPARATOR);
            System.out.println(arrayJson);
            System.out.println(OUTPUT_SEPARATOR);
            System.out.printf(CHAR_COUNT_FORMAT, charCount);
            System.out.println(DONE_MESSAGE);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Читает записи из CSV файла.
     *
     * @param filename имя файла для чтения
     * @return список записей WsRecord
     * @throws IOException если произошла ошибка чтения файла
     * @throws IllegalArgumentException если формат файла неверный
     */
    static List<WsRecord> readRecordsFromFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        List<String> lines = Files.readAllLines(path);
        List<WsRecord> records = new ArrayList<>();
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue; // Пропускаем пустые строки
            }
            
            String[] parts = line.split(",", -1); // Используем -1 чтобы сохранять пустые значения
            if (parts.length < 2) {
                throw new IllegalArgumentException(INVALID_FILE_FORMAT + " at line " + (i + 1));
            }
            
            WsRecord record = new WsRecord();
            
            // Обрабатываем name
            String name = parts[0].trim();
            if (name.length() > MAX_NAME_LENGTH) {
                throw new IllegalArgumentException(NAME_LIMIT_MESSAGE + " at line " + (i + 1));
            }
            record.setName(padToLength(name, MAX_NAME_LENGTH));
            
            // Обрабатываем value
            String value = parts[1].trim();
            if (value.length() > MAX_VALUE_LENGTH) {
                throw new IllegalArgumentException(VALUE_LIMIT_MESSAGE + " at line " + (i + 1));
            }
            record.setValue(padToLength(value, MAX_VALUE_LENGTH));
            
            // Обрабатываем enabled (если есть)
            if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                String enabledFlag = parts[2].trim().toLowerCase();
                if (TRUE_STRING.equals(enabledFlag)) {
                    record.setFlag(true);
                } else if (FALSE_STRING.equals(enabledFlag)) {
                    record.setFlag(false);
                } else {
                    throw new IllegalArgumentException(ENABLED_FLAG_MESSAGE + " at line " + (i + 1));
                }
            } else {
                record.setFlag(DEFAULT_FLAG);
            }
            
            // Обрабатываем description (если есть)
            if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
                String description = parts[3].trim();
                if (description.length() > MAX_DESCRIPTION_LENGTH) {
                    throw new IllegalArgumentException(DESCRIPTION_LIMIT_MESSAGE + " at line " + (i + 1));
                }
                record.setDescription(padToLength(description, MAX_DESCRIPTION_LENGTH));
            } else {
                record.setDescription(DEFAULT_DESCRIPTION);
            }
            
            // Устанавливаем timestamp
            record.setTimestamp(generateTimestamp());
            
            records.add(record);
        }
        
        return records;
    }
    
    /**
     * Обрабатывает режим командной строки.
     * Создает запись из аргументов, генерирует JSON и выводит результат.
     *
     * @param args аргументы командной строки
     * @param outputFile имя файла для вывода (может быть null)
     * @param prettyPrint флаг красивого вывода
     */
    static void processCommandLineMode(String[] args, String outputFile, boolean prettyPrint) {
        String wsJsonOutput = " ".repeat(JSON_OUTPUT_INITIAL_LENGTH);
        int wsJsonCharCount = 0;
        
        WsRecord wsRecord;
        
        try {
            wsRecord = createWsRecordFromArgs(args);
        } catch (IllegalArgumentException e) {
            System.out.println(ERROR_PREFIX + e.getMessage());
            System.out.println(USAGE_MESSAGE);
            System.exit(1);
            return;
        }
        
        try {
            if (wsRecord == null || wsRecord.getName() == null || wsRecord.getValue() == null) {
                throw new IllegalStateException(NULL_RECORD_ERROR);
            }
            
            String jsonContent;
            if (prettyPrint) {
                jsonContent = generatePrettyJson(wsRecord);
            } else {
                jsonContent = String.format(
                    JSON_TEMPLATE,
                    wsRecord.getName().trim(),
                    wsRecord.getValue().trim(),
                    wsRecord.getFlag(),
                    wsRecord.getDescription().trim(),
                    wsRecord.getTimestamp()
                );
            }
            wsJsonOutput = jsonContent;
            wsJsonCharCount = jsonContent.length();
            System.out.println(JSON_SUCCESS_MESSAGE);
            
            // Записываем в файл, если указана опция -o
            if (outputFile != null) {
                writeToFile(outputFile, jsonContent);
                System.out.println(OUTPUT_SAVED_MESSAGE + outputFile);
            }
        } catch (IllegalFormatException e) {
            System.out.println(JSON_FORMAT_ERROR + e.getMessage());
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println(NULL_VALUE_ERROR + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println(UNEXPECTED_JSON_ERROR + e.getMessage());
            System.exit(1);
        }
        
        System.out.println(GENERATED_JSON_PREFIX + wsRecord.getDisplayString());
        System.out.println(OUTPUT_SEPARATOR);
        System.out.println(wsJsonOutput);
        System.out.println(OUTPUT_SEPARATOR);
        System.out.printf(CHAR_COUNT_FORMAT, wsJsonCharCount);
        System.out.println(DONE_MESSAGE);
        System.exit(0);
    }
    
    /**
     * Создает запись WsRecord из аргументов командной строки.
     * Всегда использует значения по умолчанию.
     *
     * @param args аргументы командной строки
     * @return запись WsRecord с значениями по умолчанию
     * @throws IllegalArgumentException если аргументы неверные
     */
    static WsRecord createWsRecordFromArgs(String[] args) throws IllegalArgumentException {
        WsRecord record = new WsRecord();
        
        // Всегда используем значения по умолчанию
        record.setName(DEFAULT_NAME);
        record.setValue(DEFAULT_VALUE);
        record.setFlag(DEFAULT_FLAG);
        record.setDescription(DEFAULT_DESCRIPTION);
        record.setTimestamp(generateTimestamp());
        
        return record;
    }
    
    /**
     * Дополняет строку пробелами до указанной длины.
     * Если строка длиннее указанной длины, она обрезается.
     *
     * @param str исходная строка
     * @param length требуемая длина
     * @return дополненная или обрезанная строка
     */
    static String padToLength(String str, int length) {
        if (str == null) {
            return " ".repeat(length);
        }
        if (str.length() >= length) {
            return str.substring(0, length);
        }
        return str + " ".repeat(length - str.length());
    }
    
    /**
     * Генерирует временную метку в формате "yyyy-MM-dd HH:mm:ss".
     *
     * @return строка с текущей временной меткой
     */
    static String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
    }
    
    /**
     * Записывает содержимое в файл.
     *
     * @param filename имя файла для записи
     * @param content содержимое для записи
     * @throws IOException если произошла ошибка записи
     */
    static void writeToFile(String filename, String content) throws IOException {
        try {
            Path path = Paths.get(filename);
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            throw new IOException(FILE_WRITE_ERROR + e.getMessage());
        }
    }
    
    /**
     * Генерирует красиво отформатированный JSON для записи.
     * Не использует библиотеку Jackson, реализовано вручную.
     *
     * @param record запись для преобразования в JSON
     * @return красиво отформатированная JSON строка
     */
    static String generatePrettyJson(WsRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"name\": \"").append(record.getName().trim()).append("\",\n");
        sb.append("  \"value\": \"").append(record.getValue().trim()).append("\",\n");
        sb.append("  \"enabled\": ").append(record.getFlag()).append(",\n");
        sb.append("  \"description\": \"").append(record.getDescription().trim()).append("\",\n");
        sb.append("  \"timestamp\": \"").append(record.getTimestamp()).append("\"\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Класс, представляющий запись с полями name, value, description, timestamp и flag.
     * Используется для хранения данных и генерации JSON.
     */
    static class WsRecord {
        private String name = " ".repeat(DEFAULT_PADDING_LENGTH);
        private String value = " ".repeat(DEFAULT_PADDING_LENGTH);
        private String blank = " ".repeat(DEFAULT_PADDING_LENGTH);
        private String description = " ".repeat(MAX_DESCRIPTION_LENGTH);
        private String timestamp = "";
        private boolean flag = false;
        
        /**
         * Устанавливает флаг в состояние enabled (true).
         */
        public void setFlagEnabled() {
            this.flag = true;
        }
        
        /**
         * Устанавливает флаг в состояние disabled (false).
         */
        public void setFlagDisabled() {
            this.flag = false;
        }
        
        /**
         * Возвращает строковое представление записи для отображения.
         *
         * @return форматированная строка с данными записи
         */
        public String getDisplayString() {
            return name + value + blank + (flag ? TRUE_STRING + " " : FALSE_STRING) + 
                   " Description: " + description.trim() + " Timestamp: " + timestamp;
        }
        
        /**
         * @return имя записи
         */
        public String getName() {
            return name;
        }
        
        /**
         * @return значение записи
         */
        public String getValue() {
            return value;
        }
        
        /**
         * @return пустое поле (не используется в JSON)
         */
        public String getBlank() {
            return blank;
        }
        
        /**
         * @return описание записи
         */
        public String getDescription() {
            return description;
        }
        
        /**
         * @return временная метка записи
         */
        public String getTimestamp() {
            return timestamp;
        }
        
        /**
         * @return флаг enabled записи
         */
        public boolean getFlag() {
            return flag;
        }
        
        /**
         * @return строковое представление флага enabled
         */
        public String getFlagAsString() {
            return flag ? TRUE_STRING : FALSE_STRING;
        }
        
        /**
         * Устанавливает имя записи.
         *
         * @param name имя записи
         */
        public void setName(String name) {
            this.name = name;
        }
        
        /**
         * Устанавливает значение записи.
         *
         * @param value значение записи
         */
        public void setValue(String value) {
            this.value = value;
        }
        
        /**
         * Устанавливает пустое поле.
         *
         * @param blank пустое поле
         */
        public void setBlank(String blank) {
            this.blank = blank;
        }
        
        /**
         * Устанавливает описание записи.
         *
         * @param description описание записи
         */
        public void setDescription(String description) {
            this.description = description;
        }
        
        /**
         * Устанавливает временную метку записи.
         *
         * @param timestamp временная метка
         */
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
        
        /**
         * Устанавливает флаг enabled записи.
         *
         * @param flag значение флага
         */
        public void setFlag(boolean flag) {
            this.flag = flag;
        }
        
        /**
         * Возвращает строковое представление записи.
         *
         * @return строковое представление записи
         */
        @Override
        public String toString() {
            return getDisplayString();
        }
    }
}