package hu.retronet.mc.authserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Privileges {
    private Privilege onlineChat = new Privilege();
    private Privilege multiplayerServer = new Privilege();
    private Privilege multiplayerRealms = new Privilege();
}

