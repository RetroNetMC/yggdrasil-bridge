package hu.retronet.mc.portal.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class MinecraftGameOwnership implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<OwnershipItem> items;
    private String signature;
    private String keyId;

    @Data
    public static class OwnershipItem {
        private String name;
        private String signature;
    }
}
