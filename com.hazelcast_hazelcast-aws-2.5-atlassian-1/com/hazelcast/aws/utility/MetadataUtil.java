/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.InvalidConfigurationException
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 */
package com.hazelcast.aws.utility;

import com.hazelcast.aws.utility.RetryUtils;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public final class MetadataUtil {
    public static final String INSTANCE_METADATA_URI = "http://169.254.169.254/latest/meta-data/";
    public static final String INSTANCE_API_TOKEN_URI = "http://169.254.169.254/latest/api/token";
    public static final String IAM_SECURITY_CREDENTIALS_URI = "iam/security-credentials/";
    public static final String AVAILABILITY_ZONE_URI = "placement/availability-zone/";
    private static final ILogger LOGGER = Logger.getLogger(MetadataUtil.class);
    private static final Long TOKEN_TTL_SECONDS = 60L;
    private static final boolean IMDSv1 = Boolean.getBoolean("hazelcast.cluster.join.aws.imdsv1");

    private MetadataUtil() {
    }

    public static String retrieveMetadataFromURI(String uri, int timeoutInSeconds) {
        if (IMDSv1) {
            return MetadataUtil.retrieveMetadataFromURIv1(uri, timeoutInSeconds);
        }
        return MetadataUtil.retrieveMetadataFromURIv2(uri, timeoutInSeconds);
    }

    public static String retrieveMetadataFromURI(final String uri, final int timeoutInSeconds, int retries) {
        return RetryUtils.retry(new Callable<String>(){

            @Override
            public String call() {
                return MetadataUtil.retrieveMetadataFromURI(uri, timeoutInSeconds);
            }
        }, retries);
    }

    private static String retrieveMetadataFromURIv2(String uri, int timeoutInSeconds) {
        try {
            LOGGER.info("Using metadata services version 2");
            HttpURLConnection conn = (HttpURLConnection)new URL(uri).openConnection();
            conn.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(timeoutInSeconds));
            conn.setRequestProperty("X-aws-ec2-metadata-token", MetadataUtil.getApiToken(timeoutInSeconds));
            return MetadataUtil.readMetadata(conn);
        }
        catch (IOException io) {
            throw new InvalidConfigurationException("Unable to retrieve metadata in URI: " + uri, (Throwable)io);
        }
    }

    private static String retrieveMetadataFromURIv1(String uri, int timeoutInSeconds) {
        try {
            LOGGER.info("Using metadata services version 1");
            HttpURLConnection conn = (HttpURLConnection)new URL(uri).openConnection();
            conn.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(timeoutInSeconds));
            return MetadataUtil.readMetadata(conn);
        }
        catch (IOException io) {
            throw new InvalidConfigurationException("Unable to retrieve metadata in URI: " + uri, (Throwable)io);
        }
    }

    private static synchronized String getApiToken(int timeoutInSeconds) {
        try {
            HttpURLConnection conn = (HttpURLConnection)new URL(INSTANCE_API_TOKEN_URI).openConnection();
            conn.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(timeoutInSeconds));
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("X-aws-ec2-metadata-token-ttl-seconds", TOKEN_TTL_SECONDS.toString());
            return MetadataUtil.readMetadata(conn);
        }
        catch (IOException io) {
            throw new InvalidConfigurationException("Unable to retrieve api token", (Throwable)io);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String readMetadata(HttpURLConnection conn) throws IOException {
        StringBuilder response = new StringBuilder();
        InputStreamReader is = null;
        BufferedReader reader = null;
        try {
            String resp;
            is = new InputStreamReader(conn.getInputStream(), "UTF-8");
            reader = new BufferedReader(is);
            while ((resp = reader.readLine()) != null) {
                response.append(resp);
            }
            String string = response.toString();
            return string;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    LOGGER.warning((Throwable)e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    LOGGER.warning((Throwable)e);
                }
            }
        }
    }
}

