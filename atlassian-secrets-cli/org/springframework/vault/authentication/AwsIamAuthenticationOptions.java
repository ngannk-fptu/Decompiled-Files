/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.amazonaws.auth.AWSCredentials
 *  com.amazonaws.auth.AWSCredentialsProvider
 *  com.amazonaws.auth.AWSStaticCredentialsProvider
 */
package org.springframework.vault.authentication;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import java.net.URI;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AwsIamAuthenticationOptions {
    public static final String DEFAULT_AWS_AUTHENTICATION_PATH = "aws";
    private final String path;
    private final AWSCredentialsProvider credentialsProvider;
    @Nullable
    private final String role;
    @Nullable
    private final String serverId;
    private final URI endpointUri;

    private AwsIamAuthenticationOptions(String path, AWSCredentialsProvider credentialsProvider, @Nullable String role, @Nullable String serverId, URI endpointUri) {
        this.path = path;
        this.credentialsProvider = credentialsProvider;
        this.role = role;
        this.serverId = serverId;
        this.endpointUri = endpointUri;
    }

    public static AwsIamAuthenticationOptionsBuilder builder() {
        return new AwsIamAuthenticationOptionsBuilder();
    }

    public String getPath() {
        return this.path;
    }

    public AWSCredentialsProvider getCredentialsProvider() {
        return this.credentialsProvider;
    }

    @Nullable
    public String getRole() {
        return this.role;
    }

    @Nullable
    public String getServerId() {
        return this.serverId;
    }

    @Nullable
    @Deprecated
    public String getServerName() {
        return this.serverId;
    }

    public URI getEndpointUri() {
        return this.endpointUri;
    }

    public static class AwsIamAuthenticationOptionsBuilder {
        private String path = "aws";
        @Nullable
        private AWSCredentialsProvider credentialsProvider;
        @Nullable
        private String role;
        @Nullable
        private String serverId;
        private URI endpointUri = URI.create("https://sts.amazonaws.com/");

        AwsIamAuthenticationOptionsBuilder() {
        }

        public AwsIamAuthenticationOptionsBuilder path(String path) {
            Assert.hasText(path, "Path must not be empty");
            this.path = path;
            return this;
        }

        public AwsIamAuthenticationOptionsBuilder credentials(AWSCredentials credentials) {
            Assert.notNull((Object)credentials, "Credentials must not be null");
            return this.credentialsProvider((AWSCredentialsProvider)new AWSStaticCredentialsProvider(credentials));
        }

        public AwsIamAuthenticationOptionsBuilder credentialsProvider(AWSCredentialsProvider credentialsProvider) {
            Assert.notNull((Object)credentialsProvider, "AWSCredentialsProvider must not be null");
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        public AwsIamAuthenticationOptionsBuilder role(String role) {
            Assert.hasText(role, "Role must not be null or empty");
            this.role = role;
            return this;
        }

        public AwsIamAuthenticationOptionsBuilder serverId(String serverId) {
            Assert.hasText(serverId, "Server name must not be null or empty");
            this.serverId = serverId;
            return this;
        }

        public AwsIamAuthenticationOptionsBuilder serverName(String serverName) {
            return this.serverId(serverName);
        }

        public AwsIamAuthenticationOptionsBuilder endpointUri(URI endpointUri) {
            Assert.notNull((Object)endpointUri, "Endpoint URI must not be null");
            this.endpointUri = endpointUri;
            return this;
        }

        public AwsIamAuthenticationOptions build() {
            Assert.state(this.credentialsProvider != null, "Credentials or CredentialProvider must not be null");
            return new AwsIamAuthenticationOptions(this.path, this.credentialsProvider, this.role, this.serverId, this.endpointUri);
        }
    }
}

