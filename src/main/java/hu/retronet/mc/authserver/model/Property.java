package hu.retronet.mc.authserver.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.retronet.mc.common.json_deserializer.EnumCustomNameJsonDeserializer;
import hu.retronet.mc.common.json_deserializer.EnumCustomNameJsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Property {
    @JsonSerialize(using = EnumCustomNameJsonSerializer.class)
    @JsonDeserialize(using = EnumCustomNameJsonDeserializer.class)
    private UserPropertyType name;

    private String value;

    public Property(String name, String value) {
        this(UserPropertyType.ofPropertyName(name).get(), value);
    }
}
