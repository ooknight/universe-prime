package io.github.ooknight.universe.security.crypto.password;

import org.jspecify.annotations.Nullable;

public abstract class AbstractValidatingPasswordEncoder implements PasswordEncoder {

    protected static boolean hasLength(@Nullable CharSequence str) {
        return (str != null && str.length() > 0);
    }
    protected static boolean hasLength(@Nullable String str) {
        return (str != null && !str.isEmpty());
    }
    @Override
    public final @Nullable String encode(@Nullable CharSequence rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        return encodeNonNullPassword(rawPassword.toString());
    }
    protected abstract String encodeNonNullPassword(String rawPassword);
    @Override
    public final boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
        if (!hasLength(rawPassword) || !hasLength(encodedPassword)) {
            return false;
        }
        return matchesNonNull(rawPassword.toString(), encodedPassword);
    }
    protected abstract boolean matchesNonNull(String rawPassword, String encodedPassword);
    @Override
    public final boolean upgradeEncoding(@Nullable String encodedPassword) {
        if (!hasLength(encodedPassword)) {
            return false;
        }
        return upgradeEncodingNonNull(encodedPassword);
    }
    protected boolean upgradeEncodingNonNull(String encodedPassword) {
        return false;
    }

}
