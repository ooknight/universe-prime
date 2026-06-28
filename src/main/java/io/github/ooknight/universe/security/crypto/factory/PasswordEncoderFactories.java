package io.github.ooknight.universe.security.crypto.factory;

import io.github.ooknight.universe.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.github.ooknight.universe.security.crypto.password.DelegatingPasswordEncoder;
import io.github.ooknight.universe.security.crypto.password.NoOpPasswordEncoder;
import io.github.ooknight.universe.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public final class PasswordEncoderFactories {

    private PasswordEncoderFactories() {
    }

    public static PasswordEncoder createDelegatingPasswordEncoder() {
        String encodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        return new DelegatingPasswordEncoder(encodingId, encoders);
    }

}
