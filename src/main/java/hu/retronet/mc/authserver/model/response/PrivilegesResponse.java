package hu.retronet.mc.authserver.model.response;

import hu.retronet.mc.authserver.model.Privileges;
import hu.retronet.mc.common.model.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegesResponse extends ErrorResponse {
    private Privileges privileges = new Privileges();
}

