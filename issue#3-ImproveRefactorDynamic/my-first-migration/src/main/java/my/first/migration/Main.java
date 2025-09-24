package my.first.migration;

public class Main {
    
    public static void main(String[] args) {
        String wsJsonOutput = " ".repeat(256);
        int wsJsonCharCount = 0;
        
        WsRecord wsRecord;
        
        try {
            wsRecord = createWsRecordFromArgs(args);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Usage: java Main [name value enabled]");
            System.out.println("  name: string (up to 10 chars)");
            System.out.println("  value: string (up to 10 chars)");
            System.out.println("  enabled: true/false (optional)");
            System.exit(1);
            return;
        }
        
        try {
            // Проверка на null для лучшей диагностики
            if (wsRecord == null || wsRecord.name == null || 
                wsRecord.value == null || wsRecord.flag == null) {
                throw new IllegalStateException("WsRecord or its fields are null");
            }
            
            String jsonContent = String.format(
                "{\"name\":\"%s\",\"value\":\"%s\",\"enabled\":%s}",
                wsRecord.name.trim(),
                wsRecord.value.trim(),
                wsRecord.flag.trim()
            );
            wsJsonOutput = jsonContent;
            wsJsonCharCount = jsonContent.length();
            System.out.println("JSON document successfully generated.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error in JSON format: " + e.getMessage());
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Error: null value encountered: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Unexpected error generating JSON: " + e.getMessage());
            System.exit(1);
        }
        
        System.out.println("Generated JSON for record: " + wsRecord.getDisplayString());
        System.out.println("----------------------------");
        System.out.println(wsJsonOutput);
        System.out.println("----------------------------");
        System.out.printf("JSON output character count: %04d%n", wsJsonCharCount);
        System.out.println("Done.");
        System.exit(0);
    }
    
    /**
     * Создает и заполняет WsRecord на основе аргументов командной строки
     */
    static WsRecord createWsRecordFromArgs(String[] args) throws IllegalArgumentException {
        WsRecord record = new WsRecord();
        
        if (args.length == 0) {
            // Значения по умолчанию при отсутствии аргументов
            record.name = "Test Name ";
            record.value = "Test Value ";
            record.setFlagEnabled();
            return record;
        }
        
        if (args.length < 2) {
            throw new IllegalArgumentException("At least name and value parameters are required");
        }
        
        String name = args[0];
        if (name.length() > 10) {
            throw new IllegalArgumentException("Name cannot exceed 10 characters");
        }
        record.name = padToLength(name, 10);
        
        String value = args[1];
        if (value.length() > 10) {
            throw new IllegalArgumentException("Value cannot exceed 10 characters");
        }
        record.value = padToLength(value, 10);
        
        if (args.length >= 3) {
            String enabledFlag = args[2].toLowerCase();
            if ("true".equals(enabledFlag)) {
                record.setFlagEnabled();
            } else if ("false".equals(enabledFlag)) {
                record.setFlagDisabled();
            } else {
                throw new IllegalArgumentException("Enabled flag must be 'true' or 'false'");
            }
        } else {
            // Значение по умолчанию, если флаг не указан
            record.setFlagEnabled();
        }
        
        return record;
    }
    
    /**
     * Дополняет строку пробелами до указанной длины
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
    
    static class WsRecord {
        String name = "          ";
        String value = "          ";
        String blank = "          ";
        String flag = "false";
        
        public void setFlagEnabled() {
            this.flag = "true";
        }
        
        public void setFlagDisabled() {
            this.flag = "false";
        }
        
        public String getDisplayString() {
            return name + value + blank + flag;
        }        
    }
}