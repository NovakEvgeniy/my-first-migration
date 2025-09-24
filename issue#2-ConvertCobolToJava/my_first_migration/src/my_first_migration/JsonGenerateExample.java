package my_first_migration;

public class JsonGenerateExample {
	public static void main(String[] args) {
		// Аналог COBOL: 01 ws-json-output pic x(256).
        String wsJsonOutput = " ".repeat(256); // Объявляем переменную для хранения JSON строки. В Java нет фиксированного размера, можно проверять длину вручную.
     // Аналог COBOL: 01 ws-json-char-count pic 9(4).
        int wsJsonCharCount = 0; // Объявляем переменную для подсчета символов. Хранит количество символов JSON-строки.
        // 01  ws-record.
        WsRecord wsRecord = new WsRecord(); // Теперь ошибки "WsRecord cannot be resolved" не будет!
     // ИМИТАЦИЯ OPERATORS MOVE AND SET
        // move "Test Name" to ws-record-name
        wsRecord.name = "Test Name "; // Вручную добавляем пробел до 10 символов!
        // move "Test Value" to ws-record-value  
       // wsRecord.value = "Test Value"; // "Test Value" - это 10 символов? Нет, 9. Нужно добавить пробел.
        wsRecord.value = "Test Value "; // Теперь 10 символов.
        // set ws-record-flag-enabled to true
        wsRecord.setFlagEnabled(); // Устанавливаем флаг в "true " (с пробелом в конце)
/*     // ★★★★ ВРЕМЕННАЯ ПРОВЕРКА - НАЧАЛО ★★★★
        System.out.println("=== ВРЕМЕННАЯ ПРОВЕРКА ДЛИН СТРОК ===");
        System.out.println("Длина name: '" + wsRecord.name + "' = " + wsRecord.name.length() + " символов"); 
        System.out.println("Длина value: '" + wsRecord.value + "' = " + wsRecord.value.length() + " символов");
        System.out.println("Длина blank: '" + wsRecord.blank + "' = " + wsRecord.blank.length() + " символов");
        System.out.println("Длина flag: '" + wsRecord.flag + "' = " + wsRecord.flag.length() + " символов");
        System.out.println("=== КОНЕЦ ПРОВЕРКИ ===");
        System.out.println(); // Пустая строка для разделения
        // ★★★★ ВРЕМЕННАЯ ПРОВЕРКА - КОНЕЦ ★★★★
         */
        // ★★★★ ИМИТАЦИЯ JSON GENERATE - НАЧАЛО ★★★★
        // json generate ws-json-output from ws-record...
        // В Java нет встроенного аналога, поэтому собираем строку вручную.
        try {
            // Это прямая реализация секции NAME OF
            String jsonContent = String.format(
                "{\"name\":\"%s\",\"value\":\"%s\",\"enabled\":\"%s\"}",
                wsRecord.name.trim(), // .trim() убирает добавленные нами пробелы, как это делает COBOL для текстовых полей в JSON
                wsRecord.value.trim(),
                wsRecord.flag.trim() // Убираем пробелы у флага
            );
            wsJsonOutput = jsonContent;
            wsJsonCharCount = jsonContent.length();
            // not on exception... display "JSON document successfully generated."
            System.out.println("JSON document successfully generated.");
        } catch (Exception e) {
            // on exception... display "Error generating JSON error " JSON-CODE
            System.out.println("Error generating JSON error ");
            // В данном простом случае маловероятно, но мы поймаем исключение, если что-то пойдет не так.
            System.exit(1); // stop run
        }
        // ★★★★ ИМИТАЦИЯ JSON GENERATE - КОНЕЦ ★★★★
        // Здесь будет следующий шаг - вывод результатов.
     // ★★★★ ВОСПРОИЗВЕДЕНИЕ OPERATORS DISPLAY - НАЧАЛО ★★★★
        // display "Generated JSON for record: " ws-record
        System.out.println("Generated JSON for record: " + wsRecord.getDisplayString());
        // display "----------------------------"
        System.out.println("----------------------------");
        // display function trim(ws-json-output)
        System.out.println(wsJsonOutput); // Наша строка уже без лишних пробелов
        // display "----------------------------"
        System.out.println("----------------------------");
        // display "JSON output character count: " ws-json-char-count
        // В COBOL pic 9(4) выводится как 4 цифры, с ведущими нулями. Форматируем соответственно.
        System.out.printf("JSON output character count: %04d%n", wsJsonCharCount); // %04d - 4 цифры с ведущими нулями
        // display "Done."
        System.out.println("Done.");
        // stop run
        System.exit(0);
        // ★★★★ ВОСПРОИЗВЕДЕНИЕ OPERATORS DISPLAY - КОНЕЦ ★★★★
    }    
    // +++ КОД КЛАССА WsRecord ДОБАВЛЯЕТСЯ ЗДЕСЬ, ВНУТРИ JsonGenerateExample, НО ВНЕ main +++
    // Внутри класса JsonGenerateExample, но вне метода main
    static class WsRecord {
        // 05  ws-record-name pic x(10).
        String name = "          "; // Инициализируем 10 пробелами
        // 05  ws-record-value pic x(10).
        String value = "          ";
        // 05  ws-record-blank pic x(10).
        String blank = "          "; // Явно инициализируем пробелами
        // 05  ws-record-flag pic x(5) value "false".
        String flag = "false";
        // Аналог уровня 88. Создадим методы для удобной установки флага.
        public void setFlagEnabled() {
            this.flag = "true "; // Добавляем пробел до 5 символов
        }
        public void setFlagDisabled() {
            this.flag = "false"; // "false" уже 5 символов
        }
        // Создадим метод, который имитирует поведение DISPLAY ws-record в COBOL.
        // COBOL выводит все поля группы подряд без разделителей.
        public String getDisplayString() {
            return name + value + blank + flag;
        }        
	}

}
