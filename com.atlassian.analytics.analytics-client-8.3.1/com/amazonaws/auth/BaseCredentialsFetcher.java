/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
abstract class BaseCredentialsFetcher {
    private static final Log LOG = LogFactory.getLog(BaseCredentialsFetcher.class);
    private static final int REFRESH_THRESHOLD = 3600000;
    private static final int FIFTEEN_MINUTES_IN_MILLIS = 900000;
    private static final int EXPIRATION_THRESHOLD = 900000;
    private static final String ACCESS_KEY_ID = "AccessKeyId";
    private static final String SECRET_ACCESS_KEY = "SecretAccessKey";
    private static final String TOKEN = "Token";
    private final boolean allowExpiredCredentials;
    private volatile AWSCredentials credentials;
    private volatile Date credentialsExpiration;
    protected volatile Date lastInstanceProfileCheck;

    protected BaseCredentialsFetcher(boolean allowExpiredCredentials) {
        this.allowExpiredCredentials = allowExpiredCredentials;
    }

    public AWSCredentials getCredentials() {
        if (this.needsToLoadCredentials()) {
            this.fetchCredentials();
        }
        if (this.expired()) {
            throw new SdkClientException("The credentials received have been expired");
        }
        return this.credentials;
    }

    boolean needsToLoadCredentials() {
        if (this.credentials == null) {
            return true;
        }
        if (this.credentialsExpiration != null && this.isWithinExpirationThreshold()) {
            return true;
        }
        return this.lastInstanceProfileCheck != null && this.isPastRefreshThreshold();
    }

    abstract String getCredentialsResponse();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void fetchCredentials() {
        if (!this.needsToLoadCredentials()) {
            return;
        }
        try {
            this.lastInstanceProfileCheck = new Date();
            String credentialsResponse = this.getCredentialsResponse();
            JsonNode node = Jackson.fromSensitiveJsonString(credentialsResponse, JsonNode.class);
            JsonNode accessKey = node.get(ACCESS_KEY_ID);
            JsonNode secretKey = node.get(SECRET_ACCESS_KEY);
            JsonNode token = node.get(TOKEN);
            if (null == accessKey || null == secretKey) {
                throw new SdkClientException("Unable to load credentials. Access key or secret key are null.");
            }
            this.credentials = null != token ? new BasicSessionCredentials(accessKey.asText(), secretKey.asText(), token.asText()) : new BasicAWSCredentials(accessKey.asText(), secretKey.asText());
            JsonNode expirationJsonNode = node.get("Expiration");
            if (null != expirationJsonNode) {
                String expiration = expirationJsonNode.asText();
                expiration = expiration.replaceAll("\\+0000$", "Z");
                try {
                    this.credentialsExpiration = DateUtils.parseISO8601Date(expiration);
                }
                catch (Exception ex) {
                    this.handleError("Unable to parse credentials expiration date from Amazon EC2 instance", ex);
                }
            }
        }
        catch (Exception e) {
            this.handleError("Unable to load credentials from service endpoint", e);
        }
        finally {
            if (this.allowExpiredCredentials && this.credentials != null && this.credentialsExpiration != null && this.needsToLoadCredentials()) {
                long now = System.currentTimeMillis();
                long fifteenSecondsBeforeExpiration = this.credentialsExpiration.getTime() - 15000L;
                long fiveMinutesFromNow = now + 300000L;
                if (fifteenSecondsBeforeExpiration > now) {
                    long fifteenMinutesFromNow = now + 900000L;
                    long nextRefreshTime = Math.min(fifteenMinutesFromNow, fifteenSecondsBeforeExpiration);
                    this.credentialsExpiration = new Date(nextRefreshTime + 900000L);
                } else {
                    LOG.warn((Object)"Credential expiration has been extended due to a credential service availability issue. A refresh of these credentials will be attempted again in 5 minutes.");
                    this.credentialsExpiration = new Date(fiveMinutesFromNow + 900000L);
                }
            }
        }
    }

    private void handleError(String errorMessage, Exception e) {
        if (this.credentials == null || this.expired()) {
            if (e instanceof SdkClientException) {
                throw (SdkClientException)e;
            }
            throw new SdkClientException(errorMessage, e);
        }
        LOG.warn((Object)errorMessage, (Throwable)e);
    }

    public void refresh() {
        this.credentials = null;
    }

    private boolean isWithinExpirationThreshold() {
        return this.credentialsExpiration.getTime() - System.currentTimeMillis() < 900000L;
    }

    private boolean isPastRefreshThreshold() {
        return System.currentTimeMillis() - this.lastInstanceProfileCheck.getTime() > 3600000L;
    }

    private boolean expired() {
        if (this.allowExpiredCredentials) {
            return false;
        }
        if (this.credentialsExpiration == null) {
            return false;
        }
        return this.credentialsExpiration.getTime() <= System.currentTimeMillis();
    }

    Date getCredentialsExpiration() {
        return this.credentialsExpiration;
    }

    public String toString() {
        return "BaseCredentialsFetcher";
    }
}

