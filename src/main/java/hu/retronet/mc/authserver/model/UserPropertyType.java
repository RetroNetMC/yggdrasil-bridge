package hu.retronet.mc.authserver.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import hu.retronet.mc.common.model.EnumCustomName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum UserPropertyType implements EnumCustomName {
    PREFERRED_LANGUAGE("preferredLanguage"),
    REGISTRATION_COUNTRY("registrationCountry");

    private final String name;

    public static Optional<UserPropertyType> ofPropertyName(String propertyName) {
        return Arrays.stream(values()).filter(value -> value.getName().equalsIgnoreCase(propertyName)).findFirst();
    }

    @JsonCreator
    public static UserPropertyType of(String propertyName) {
        return ofPropertyName(propertyName).orElse(null);
    }
}
