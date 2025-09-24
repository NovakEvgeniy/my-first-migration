package my.first.migration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    
    @Test
    void testWsRecordSetFlagEnabled() {
        Main.WsRecord record = new Main.WsRecord();
        record.setFlagEnabled();
        assertEquals("true", record.flag, "Флаг должен быть установлен в 'true'");
    }

    @Test
    void testWsRecordSetFlagDisabled() {
        Main.WsRecord record = new Main.WsRecord();
        record.setFlagDisabled();
        assertEquals("false", record.flag, "Флаг должен быть установлен в 'false'");
    }

    @Test
    void testWsRecordGetDisplayString() {
        Main.WsRecord record = new Main.WsRecord();
        record.name = "Test      ";
        record.value = "Value     ";
        String expected = "Test      Value               false";
        assertEquals(expected, record.getDisplayString());
    }

    @Test
    void testJsonGenerationLogic() {
        Main.WsRecord wsRecord = new Main.WsRecord();
        wsRecord.name = "Test Name ";
        wsRecord.value = "Test Value ";
        wsRecord.setFlagEnabled();

        String jsonContent = String.format(
                "{\"name\":\"%s\",\"value\":\"%s\",\"enabled\":%s}",
                wsRecord.name.trim(),
                wsRecord.value.trim(),
                wsRecord.flag.trim()
        );

        String expectedJson = "{\"name\":\"Test Name\",\"value\":\"Test Value\",\"enabled\":true}";
        assertEquals(expectedJson, jsonContent);
    }

    @Test
    void testJsonGenerationDoesNotThrowException() {
        Main.WsRecord wsRecord = new Main.WsRecord();
        wsRecord.name = "Test      ";
        wsRecord.value = "Value     ";
        wsRecord.setFlagDisabled();

        assertDoesNotThrow(() -> {
            String jsonContent = String.format(
                    "{\"name\":\"%s\",\"value\":\"%s\",\"enabled\":%s}",
                    wsRecord.name.trim(),
                    wsRecord.value.trim(),
                    wsRecord.flag.trim()
            );
        });
    }
    
    @Test
    void testPadToLength() {
        assertEquals("Hello     ", Main.padToLength("Hello", 10));
        assertEquals("Test", Main.padToLength("Test", 4));
        assertEquals("LongString", Main.padToLength("LongStringIsTooLong", 10));
    }
    
    @Test
    void testCreateWsRecordFromArgsWithNoArgs() {
        Main.WsRecord record = Main.createWsRecordFromArgs(new String[]{});
        assertEquals("Test Name ", record.name);
        assertEquals("Test Value ", record.value);
        assertEquals("true", record.flag);
    }
    
    @Test
    void testCreateWsRecordFromArgsWithNameAndValue() {
        Main.WsRecord record = Main.createWsRecordFromArgs(
            new String[]{"John", "Doe"}
        );
        assertEquals("John      ", record.name);
        assertEquals("Doe       ", record.value);
        assertEquals("true", record.flag);
    }
    
    @Test
    void testCreateWsRecordFromArgsWithAllParameters() {
        Main.WsRecord record = Main.createWsRecordFromArgs(
            new String[]{"Alice", "Smith", "false"}
        );
        assertEquals("Alice     ", record.name);
        assertEquals("Smith     ", record.value);
        assertEquals("false", record.flag);
    }
    
    @Test
    void testCreateWsRecordFromArgsThrowsExceptionForInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> {
            Main.createWsRecordFromArgs(new String[]{"onlyOneArg"});
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            Main.createWsRecordFromArgs(
                new String[]{"VeryLongNameExceeding10Chars", "value"}
            );
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            Main.createWsRecordFromArgs(
                new String[]{"name", "value", "invalid-flag"}
            );
        });
    }
}