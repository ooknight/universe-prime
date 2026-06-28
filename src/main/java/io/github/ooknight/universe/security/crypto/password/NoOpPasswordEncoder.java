package io.github.ooknight.universe.security.crypto.password;

public class NoOpPasswordEncoder extends AbstractValidatingPasswordEncoder {

    private static final PasswordEncoder INSTANCE = new NoOpPasswordEncoder();

    private NoOpPasswordEncoder() {
    }
    public static PasswordEncoder getInstance() {
        return INSTANCE;
    }
    @Override
    protected String encodeNonNullPassword(String rawPassword) {
        return rawPassword;
    }
    @Override
    protected boolean matchesNonNull(String rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }

}
