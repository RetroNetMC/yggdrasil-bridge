package hu.retronet.mc.authserver.model.response;

import hu.retronet.mc.common.model.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlockListResponse extends ErrorResponse {
    private List<UUID> blockedProfiles = new ArrayList<>();
}
