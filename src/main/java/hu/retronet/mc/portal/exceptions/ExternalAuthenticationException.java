package hu.retronet.mc.portal.exceptions;

public class ExternalAuthenticationException extends Exception {
    public ExternalAuthenticationException(String errorMessage) {
        super(errorMessage);
    }
}
