/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.internal.securitytoken.RoleInfo;
import com.amazonaws.auth.profile.internal.securitytoken.STSProfileCredentialsServiceLoader;

public class WebIdentityTokenCredentialsProvider
implements AWSCredentialsProvider {
    private final AWSCredentialsProvider credentialsProvider;
    private final RuntimeException loadException;

    public WebIdentityTokenCredentialsProvider() {
        this(new BuilderImpl());
    }

    private WebIdentityTokenCredentialsProvider(BuilderImpl builder) {
        AWSCredentialsProvider credentialsProvider = null;
        RuntimeException loadException = null;
        try {
            String roleSessionName;
            String webIdentityTokenFile = builder.webIdentityTokenFile != null ? builder.webIdentityTokenFile : System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE");
            String roleArn = builder.roleArn != null ? builder.roleArn : System.getenv("AWS_ROLE_ARN");
            String string = roleSessionName = builder.roleSessionName != null ? builder.roleSessionName : System.getenv("AWS_ROLE_SESSION_NAME");
            if (roleSessionName == null) {
                roleSessionName = "aws-sdk-java-" + System.currentTimeMillis();
            }
            RoleInfo roleInfo = new RoleInfo().withRoleArn(roleArn).withRoleSessionName(roleSessionName).withWebIdentityTokenFilePath(webIdentityTokenFile);
            credentialsProvider = STSProfileCredentialsServiceLoader.getInstance().getAssumeRoleCredentialsProvider(roleInfo);
        }
        catch (RuntimeException e) {
            loadException = e;
        }
        this.loadException = loadException;
        this.credentialsProvider = credentialsProvider;
    }

    @Override
    public AWSCredentials getCredentials() {
        if (this.loadException != null) {
            throw this.loadException;
        }
        return this.credentialsProvider.getCredentials();
    }

    @Override
    public void refresh() {
    }

    public static WebIdentityTokenCredentialsProvider create() {
        return WebIdentityTokenCredentialsProvider.builder().build();
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    static final class BuilderImpl
    implements Builder {
        private String roleArn;
        private String roleSessionName;
        private String webIdentityTokenFile;

        BuilderImpl() {
        }

        @Override
        public Builder roleArn(String roleArn) {
            this.roleArn = roleArn;
            return this;
        }

        public void setRoleArn(String roleArn) {
            this.roleArn(roleArn);
        }

        @Override
        public Builder roleSessionName(String roleSessionName) {
            this.roleSessionName = roleSessionName;
            return this;
        }

        public void setRoleSessionName(String roleSessionName) {
            this.roleSessionName(roleSessionName);
        }

        @Override
        public Builder webIdentityTokenFile(String webIdentityTokenFile) {
            this.webIdentityTokenFile = webIdentityTokenFile;
            return this;
        }

        public void setWebIdentityTokenFile(String webIdentityTokenFile) {
            this.webIdentityTokenFile(webIdentityTokenFile);
        }

        @Override
        public WebIdentityTokenCredentialsProvider build() {
            return new WebIdentityTokenCredentialsProvider(this);
        }
    }

    public static interface Builder {
        public Builder roleArn(String var1);

        public Builder roleSessionName(String var1);

        public Builder webIdentityTokenFile(String var1);

        public WebIdentityTokenCredentialsProvider build();
    }
}

