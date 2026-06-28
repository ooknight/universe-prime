package io.github.ooknight.universe.security.crypto.bcrypt;

import io.github.ooknight.universe.security.crypto.password.AbstractValidatingPasswordEncoder;

import org.jspecify.annotations.Nullable;

import java.security.SecureRandom;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BCryptPasswordEncoder extends AbstractValidatingPasswordEncoder {

    private final int strength;
    private final BCryptVersion version;
    private final Supplier<SecureRandom> random;
    private final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    public BCryptPasswordEncoder() {
        this(-1);
    }

    public BCryptPasswordEncoder(int strength) {
        this(strength, null);
    }

    public BCryptPasswordEncoder(BCryptVersion version) {
        this(version, null);
    }

    public BCryptPasswordEncoder(BCryptVersion version, @Nullable SecureRandom random) {
        this(version, -1, random);
    }

    public BCryptPasswordEncoder(int strength, @Nullable SecureRandom random) {
        this(BCryptVersion.$2A, strength, random);
    }

    public BCryptPasswordEncoder(BCryptVersion version, int strength) {
        this(version, strength, null);
    }

    public BCryptPasswordEncoder(BCryptVersion version, int strength, @Nullable SecureRandom random) {
        if (strength != -1 && (strength < BCrypt.MIN_LOG_ROUNDS || strength > BCrypt.MAX_LOG_ROUNDS)) {
            throw new IllegalArgumentException("Bad strength");
        }
        this.version = version;
        this.strength = (strength == -1) ? 10 : strength;
        this.random = (random != null) ? () -> random : SecureRandomHolder::getInstance;
    }

    @Override
    protected String encodeNonNullPassword(String rawPassword) {
        String salt = getSalt();
        return BCrypt.hashpw(rawPassword, salt);
    }

    private String getSalt() {
        return BCrypt.gensalt(this.version.getVersion(), this.strength, this.random.get());
    }

    @Override
    protected boolean matchesNonNull(String rawPassword, String encodedPassword) {
        if (!this.BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    @Override
    protected boolean upgradeEncodingNonNull(String encodedPassword) {
        Matcher matcher = this.BCRYPT_PATTERN.matcher(encodedPassword);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Encoded password does not look like BCrypt: " + encodedPassword);
        }
        int strength = Integer.parseInt(matcher.group(2));
        return strength < this.strength;
    }

    public enum BCryptVersion {

        $2A("$2a"),

        $2Y("$2y"),

        $2B("$2b");

        private final String version;

        BCryptVersion(String version) {
            this.version = version;
        }

        public String getVersion() {
            return this.version;
        }

    }

    private static final class SecureRandomHolder {

        private static final SecureRandom INSTANCE = new SecureRandom();

        private static SecureRandom getInstance() {
            return INSTANCE;
        }

    }

}
