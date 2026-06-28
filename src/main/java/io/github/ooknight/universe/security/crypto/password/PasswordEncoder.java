package io.github.ooknight.universe.security.crypto.password;

import org.jspecify.annotations.Nullable;

public interface PasswordEncoder {

    @Nullable String encode(@Nullable CharSequence rawPassword);

    boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword);

    default boolean upgradeEncoding(@Nullable String encodedPassword) {
        return false;
    }

}
