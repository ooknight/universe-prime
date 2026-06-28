package io.github.ooknight.universe.security.crypto.factory;

import io.github.ooknight.universe.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;

public class PasswordEncoderFactoriesTests {

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    public void test1() {
        String result = pe.encode("123456");
        System.out.println(result);
    }

    @Test
    public void test2() {
        boolean r1 = pe.matches("123456", "{noop}123456");
        boolean r2 = pe.matches("123456", "{bcrypt}$2a$10$HWOJICd/IjnxHT/5Q2gkueIxpfkONFUWgHFH2D6BrxgVtnFQh4ZQO");
        boolean r3 = pe.matches("123456", "{noop}abc");
        boolean r4 = pe.matches("123456", "{bcrypt}$2a$10$HWOJICd/IjnxHT/5Q2gkueIxpfkONFUWgHFH2D6BrxgVtnFQh4ZQ1");
        System.out.println(r1);
        System.out.println(r2);
        System.out.println(r3);
        System.out.println(r4);
    }

}
