/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AwsEc2AuthenticationOptions {
    public static final URI DEFAULT_PKCS7_IDENTITY_DOCUMENT_URI = URI.create("http://169.254.169.254/latest/dynamic/instance-identity/pkcs7");
    public static final String DEFAULT_AWS_AUTHENTICATION_PATH = "aws-ec2";
    public static final AwsEc2AuthenticationOptions DEFAULT = new AwsEc2AuthenticationOptions();
    private final String path;
    private final URI identityDocumentUri;
    @Nullable
    private final String role;
    private final Nonce nonce;

    private AwsEc2AuthenticationOptions() {
        this(DEFAULT_AWS_AUTHENTICATION_PATH, DEFAULT_PKCS7_IDENTITY_DOCUMENT_URI, "", Nonce.generated());
    }

    private AwsEc2AuthenticationOptions(String path, URI identityDocumentUri, @Nullable String role, Nonce nonce) {
        this.path = path;
        this.identityDocumentUri = identityDocumentUri;
        this.role = role;
        this.nonce = nonce;
    }

    public static AwsEc2AuthenticationOptionsBuilder builder() {
        return new AwsEc2AuthenticationOptionsBuilder();
    }

    public String getPath() {
        return this.path;
    }

    public URI getIdentityDocumentUri() {
        return this.identityDocumentUri;
    }

    @Nullable
    public String getRole() {
        return this.role;
    }

    public Nonce getNonce() {
        return this.nonce;
    }

    public static class Nonce {
        private final char[] value;

        protected Nonce(char[] value) {
            this.value = value;
        }

        public static Nonce generated() {
            return new Generated();
        }

        public static Nonce provided(char[] nonce) {
            Assert.notNull((Object)nonce, "Nonce must not be null");
            return new Provided(Arrays.copyOf(nonce, nonce.length));
        }

        public char[] getValue() {
            return this.value;
        }

        static class Provided
        extends Nonce {
            Provided(char[] nonce) {
                super(nonce);
            }
        }

        static class Generated
        extends Nonce {
            Generated() {
                super(UUID.randomUUID().toString().toCharArray());
            }
        }
    }

    public static class AwsEc2AuthenticationOptionsBuilder {
        private String path = "aws-ec2";
        private URI identityDocumentUri = DEFAULT_PKCS7_IDENTITY_DOCUMENT_URI;
        @Nullable
        private String role;
        private Nonce nonce = Nonce.generated();

        AwsEc2AuthenticationOptionsBuilder() {
        }

        public AwsEc2AuthenticationOptionsBuilder path(String path) {
            Assert.hasText(path, "Path must not be empty");
            this.path = path;
            return this;
        }

        public AwsEc2AuthenticationOptionsBuilder identityDocumentUri(URI identityDocumentUri) {
            Assert.notNull((Object)identityDocumentUri, "Identity document URI must not be null");
            this.identityDocumentUri = identityDocumentUri;
            return this;
        }

        public AwsEc2AuthenticationOptionsBuilder role(@Nullable String role) {
            this.role = role;
            return this;
        }

        public AwsEc2AuthenticationOptionsBuilder nonce(Nonce nonce) {
            Assert.notNull((Object)nonce, "Nonce must not be null");
            this.nonce = nonce;
            return this;
        }

        public AwsEc2AuthenticationOptions build() {
            Assert.notNull((Object)this.identityDocumentUri, "IdentityDocumentUri must not be null");
            return new AwsEc2AuthenticationOptions(this.path, this.identityDocumentUri, this.role, this.nonce);
        }
    }
}

