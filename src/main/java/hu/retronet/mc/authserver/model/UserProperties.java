package hu.retronet.mc.authserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProperties {
    private String id;
    private List<Property> properties = new ArrayList<>();
}
