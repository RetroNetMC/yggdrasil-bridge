package hu.retronet.mc.authserver.model;

public record Agent(String name, int version) {
    public Agent() {
        this(null, -1);
    }
}
