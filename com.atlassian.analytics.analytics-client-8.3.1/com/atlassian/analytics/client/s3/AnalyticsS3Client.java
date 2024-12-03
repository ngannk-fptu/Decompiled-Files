/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.analytics.client.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.metrics.AwsSdkMetrics;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import javax.xml.stream.FactoryConfigurationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class AnalyticsS3Client
implements DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsS3Client.class);
    private final AmazonS3Client amazonS3Client;

    public void destroy() {
        this.amazonS3Client.shutdown();
    }

    public AnalyticsS3Client() {
        this(CredentialsMode.ANONYMOUS);
    }

    public AnalyticsS3Client(CredentialsMode mode) {
        this.amazonS3Client = new AmazonS3Client(mode.getCredentials(), AnalyticsS3Client.getClientConfiguration());
        this.amazonS3Client.setS3ClientOptions(S3ClientOptions.builder().enableDualstack().build());
    }

    AnalyticsS3Client(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    private static ClientConfiguration getClientConfiguration() {
        return new ClientConfiguration().withProxyHost(AnalyticsS3Client.getProxyHost()).withProxyPort(AnalyticsS3Client.getProxyPort());
    }

    public int uploadFilesToS3Bucket(List<File> filesToUpload, String bucketName, String folderPrefix) {
        int filesUploaded = 0;
        for (File file : filesToUpload) {
            try {
                String key = folderPrefix + AnalyticsS3Client.timestamp() + "." + file.getName();
                LOG.debug("Processing {}", (Object)key);
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
                putObjectRequest.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
                this.amazonS3Client.putObject(putObjectRequest);
                ++filesUploaded;
                if (file.delete()) continue;
                file.deleteOnExit();
            }
            catch (AmazonServiceException e) {
                LOG.debug("Error sending analytics data to Amazon S3 storage, error type: " + e.getErrorType().toString() + ", error message: " + e.getMessage(), (Throwable)e);
            }
            catch (AmazonClientException e) {
                LOG.debug("Error sending analytics data to Amazon S3 storage, error message: " + e.getMessage(), (Throwable)e);
            }
        }
        return filesUploaded;
    }

    private static String timestamp() {
        return String.valueOf(Instant.now().atZone(ZoneId.of("Z")).toEpochSecond());
    }

    public InputStream getS3ObjectInputStream(String bucketName, String filePath) {
        try {
            return this.amazonS3Client.getObject(bucketName, filePath).getObjectContent();
        }
        catch (AmazonClientException | FactoryConfigurationError e) {
            AnalyticsS3Client.logFailedDownloadWarning(filePath, e.getMessage());
            return null;
        }
    }

    public AmazonS3Client getAmazonS3Client() {
        return this.amazonS3Client;
    }

    public static void unregisterMetricAdminMBean() {
        AwsSdkMetrics.unregisterMetricAdminMBean();
    }

    private static String getProxyHost() {
        return System.getProperty("https.proxyHost");
    }

    private static int getProxyPort() {
        String value = System.getProperty("https.proxyPort");
        return value == null ? -1 : Integer.parseInt(value);
    }

    private static void logFailedDownloadWarning(String filePath, String message) {
        LOG.debug("Couldn't download the remote list file {}, detailed message: {}", (Object)filePath, (Object)message);
    }

    public static enum CredentialsMode {
        ANONYMOUS{

            @Override
            AWSCredentials getCredentials() {
                return new AnonymousAWSCredentials();
            }
        }
        ,
        ENVIRONMENT{

            @Override
            AWSCredentials getCredentials() {
                return new EnvironmentVariableCredentialsProvider().getCredentials();
            }
        }
        ,
        SYSTEM{

            @Override
            AWSCredentials getCredentials() {
                return new SystemPropertiesCredentialsProvider().getCredentials();
            }
        };


        abstract AWSCredentials getCredentials();
    }
}

