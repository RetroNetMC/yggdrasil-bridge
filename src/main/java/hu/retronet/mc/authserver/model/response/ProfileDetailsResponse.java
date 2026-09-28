package hu.retronet.mc.authserver.model.response;

import hu.retronet.mc.common.model.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailsResponse extends ErrorResponse {
    private UUID id;
    private String name;
    private Map<String, String> properties = new HashMap<>();

}
