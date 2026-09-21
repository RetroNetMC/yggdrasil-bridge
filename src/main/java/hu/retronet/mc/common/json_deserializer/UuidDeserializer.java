package hu.retronet.mc.common.json_deserializer;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.UUID;

public class UuidDeserializer extends ValueDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonParser p, DeserializationContext ctxt) {
        String uuidWithoutDashes = p.getString();
        return UUID.fromString(convertToStandardUUID(uuidWithoutDashes));
    }

    private String convertToStandardUUID(String uuidWithoutDashes) {
        if (uuidWithoutDashes.length() == 36 && uuidWithoutDashes.charAt(8) == '-' && uuidWithoutDashes.charAt(13) == '-' && uuidWithoutDashes.charAt(18) == '-' && uuidWithoutDashes.charAt(23) == '-') {
            return uuidWithoutDashes;
        }

        if (uuidWithoutDashes.length() != 32) {
            throw new IllegalArgumentException("Invalid UUID format");
        }

        return uuidWithoutDashes.substring(0, 8) + "-" +
                uuidWithoutDashes.substring(8, 12) + "-" +
                uuidWithoutDashes.substring(12, 16) + "-" +
                uuidWithoutDashes.substring(16, 20) + "-" +
                uuidWithoutDashes.substring(20, 32);
    }
}
