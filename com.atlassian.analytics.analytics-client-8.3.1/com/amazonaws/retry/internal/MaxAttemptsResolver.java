/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.retry.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.auth.profile.internal.BasicProfile;
import com.amazonaws.auth.profile.internal.BasicProfileConfigFileLoader;
import com.amazonaws.profile.path.AwsProfileFileLocationProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public final class MaxAttemptsResolver {
    private static final Log log = LogFactory.getLog(MaxAttemptsResolver.class);
    private static final String PROFILE_PROPERTY = "max_attempts";
    private final Integer maxAttempts;
    private final BasicProfileConfigFileLoader configFileLoader;

    public MaxAttemptsResolver() {
        this.configFileLoader = BasicProfileConfigFileLoader.INSTANCE;
        this.maxAttempts = this.resolveMaxAttempts();
    }

    @SdkTestInternalApi
    MaxAttemptsResolver(AwsProfileFileLocationProvider configFileLocationProvider) {
        this.configFileLoader = new BasicProfileConfigFileLoader(configFileLocationProvider);
        this.maxAttempts = this.resolveMaxAttempts();
    }

    public Integer maxAttempts() {
        return this.maxAttempts;
    }

    private Integer resolveMaxAttempts() {
        Integer attempts = this.envVar();
        if (attempts != null) {
            return attempts;
        }
        attempts = this.systemProperty();
        if (attempts != null) {
            return attempts;
        }
        return this.profile();
    }

    private Integer profile() {
        BasicProfile profile = this.configFileLoader.getProfile();
        if (profile == null) {
            return null;
        }
        String val = profile.getPropertyValue(PROFILE_PROPERTY);
        return this.parseInteger(val);
    }

    private Integer systemProperty() {
        return this.parseInteger(System.getProperty("com.amazonaws.sdk.maxAttempts"));
    }

    private Integer envVar() {
        return this.parseInteger(System.getenv("AWS_MAX_ATTEMPTS"));
    }

    private Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        }
        catch (NumberFormatException ex) {
            log.warn((Object)"Fail to parse com.amazonaws.sdk.maxAttempts", (Throwable)ex);
            return null;
        }
    }
}

