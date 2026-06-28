package io.github.ooknight.universe.security.crypto.password;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DelegatingPasswordEncoder extends AbstractValidatingPasswordEncoder {

    private static final String DEFAULT_ID_PREFIX = "{";

    private static final String DEFAULT_ID_SUFFIX = "}";

    private static final String NO_PASSWORD_ENCODER_MAPPED = "There is no password encoder mapped for the id '%s'. "
        + "Check your configuration to ensure it matches one of the registered encoders.";

    private static final String NO_PASSWORD_ENCODER_PREFIX = "Given that there is no default password encoder configured, each password must have a password encoding prefix. "
        + "Please either prefix this password with '{noop}' or set a default password encoder in `DelegatingPasswordEncoder`.";

    private static final String MALFORMED_PASSWORD_ENCODER_PREFIX = "The name of the password encoder is improperly "
        + "formatted or incomplete. The format should be '%sENCODER%spassword'.";

    private final String idPrefix;

    private final String idSuffix;

    private final String idForEncode;

    private final PasswordEncoder passwordEncoderForEncode;

    private final Map<@Nullable String, PasswordEncoder> idToPasswordEncoder;

    private PasswordEncoder defaultPasswordEncoderForMatches = new UnmappedIdPasswordEncoder();

    public DelegatingPasswordEncoder(String idForEncode, Map<String, PasswordEncoder> idToPasswordEncoder) {
        this(idForEncode, idToPasswordEncoder, DEFAULT_ID_PREFIX, DEFAULT_ID_SUFFIX);
    }

    public DelegatingPasswordEncoder(String idForEncode, Map<String, PasswordEncoder> idToPasswordEncoder,
                                     String idPrefix, String idSuffix) {
        if (idForEncode == null) {
            throw new IllegalArgumentException("idForEncode cannot be null");
        }
        if (idPrefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        if (idSuffix == null || idSuffix.isEmpty()) {
            throw new IllegalArgumentException("suffix cannot be empty");
        }
        if (idPrefix.contains(idSuffix)) {
            throw new IllegalArgumentException("idPrefix " + idPrefix + " cannot contain idSuffix " + idSuffix);
        }

        if (!idToPasswordEncoder.containsKey(idForEncode)) {
            throw new IllegalArgumentException(
                "idForEncode " + idForEncode + "is not found in idToPasswordEncoder " + idToPasswordEncoder);
        }
        for (String id : idToPasswordEncoder.keySet()) {
            if (id == null) {
                continue;
            }
            if (!idPrefix.isEmpty() && id.contains(idPrefix)) {
                throw new IllegalArgumentException("id " + id + " cannot contain " + idPrefix);
            }
            if (id.contains(idSuffix)) {
                throw new IllegalArgumentException("id " + id + " cannot contain " + idSuffix);
            }
        }
        this.idForEncode = idForEncode;
        this.passwordEncoderForEncode = idToPasswordEncoder.get(idForEncode);
        this.idToPasswordEncoder = new HashMap<>(idToPasswordEncoder);
        this.idPrefix = idPrefix;
        this.idSuffix = idSuffix;
    }

    public void setDefaultPasswordEncoderForMatches(PasswordEncoder defaultPasswordEncoderForMatches) {
        if (defaultPasswordEncoderForMatches == null) {
            throw new IllegalArgumentException("defaultPasswordEncoderForMatches cannot be null");
        }
        this.defaultPasswordEncoderForMatches = defaultPasswordEncoderForMatches;
    }

    @Override
    protected String encodeNonNullPassword(String rawPassword) {
        return this.idPrefix + this.idForEncode + this.idSuffix + this.passwordEncoderForEncode.encode(rawPassword);
    }

    @Override
    protected boolean matchesNonNull(String rawPassword, String prefixEncodedPassword) {
        String id = extractId(prefixEncodedPassword);
        PasswordEncoder delegate = this.idToPasswordEncoder.get(id);
        if (delegate == null) {
            return this.defaultPasswordEncoderForMatches.matches(rawPassword, prefixEncodedPassword);
        }
        String encodedPassword = extractEncodedPassword(prefixEncodedPassword);
        return delegate.matches(rawPassword, encodedPassword);
    }

    private @Nullable String extractId(@Nullable String prefixEncodedPassword) {
        if (prefixEncodedPassword == null) {
            return null;
        }
        int start = prefixEncodedPassword.indexOf(this.idPrefix);
        if (start != 0) {
            return null;
        }
        int end = prefixEncodedPassword.indexOf(this.idSuffix, start);
        if (end < 0) {
            return null;
        }
        return prefixEncodedPassword.substring(start + this.idPrefix.length(), end);
    }

    @Override
    protected boolean upgradeEncodingNonNull(String prefixEncodedPassword) {
        String id = extractId(prefixEncodedPassword);
        if (!this.idForEncode.equalsIgnoreCase(id)) {
            return true;
        } else {
            String encodedPassword = extractEncodedPassword(prefixEncodedPassword);
            return this.passwordEncoderForEncode.upgradeEncoding(encodedPassword);
        }
    }

    private String extractEncodedPassword(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(this.idSuffix);
        return prefixEncodedPassword.substring(start + this.idSuffix.length());
    }

    private class UnmappedIdPasswordEncoder extends AbstractValidatingPasswordEncoder {

        @Override
        protected String encodeNonNullPassword(String rawPassword) {
            throw new UnsupportedOperationException("encode is not supported");
        }

        @Override
        protected boolean matchesNonNull(String rawPassword, String prefixEncodedPassword) {
            String id = extractId(prefixEncodedPassword);
            if (hasLength(id)) {
                throw new IllegalArgumentException(String.format(NO_PASSWORD_ENCODER_MAPPED, id));
            }
            if (hasLength(prefixEncodedPassword)) {
                int start = prefixEncodedPassword.indexOf(DelegatingPasswordEncoder.this.idPrefix);
                int end = prefixEncodedPassword.indexOf(DelegatingPasswordEncoder.this.idSuffix, start);
                if (start < 0 && end < 0) {
                    throw new IllegalArgumentException(NO_PASSWORD_ENCODER_PREFIX);
                }
            }
            throw new IllegalArgumentException(String.format(MALFORMED_PASSWORD_ENCODER_PREFIX,
                DelegatingPasswordEncoder.this.idPrefix, DelegatingPasswordEncoder.this.idSuffix));
        }

    }

}
