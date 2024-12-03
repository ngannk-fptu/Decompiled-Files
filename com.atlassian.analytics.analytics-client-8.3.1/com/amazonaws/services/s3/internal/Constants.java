/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.SSEAlgorithm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Constants {
    public static final String S3_HOSTNAME = "s3.amazonaws.com";
    public static final String S3_EXTERNAL_1_HOSTNAME = "s3-external-1.amazonaws.com";
    public static final String S3_ACCELERATE_HOSTNAME = "s3-accelerate.amazonaws.com";
    public static final String S3_ACCELERATE_DUALSTACK_HOSTNAME = "s3-accelerate.dualstack.amazonaws.com";
    public static final String S3_DUALSTACK_QUALIFIER = "dualstack";
    public static final String S3_SERVICE_DISPLAY_NAME = "Amazon S3";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String URL_ENCODING = "url";
    public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    public static final String XML_NAMESPACE = "http://s3.amazonaws.com/doc/2006-03-01/";
    public static final String NULL_VERSION_ID = "null";
    public static final int FAILED_PRECONDITION_STATUS_CODE = 412;
    public static final int KB = 1024;
    public static final int MB = 0x100000;
    public static final long GB = 0x40000000L;
    public static final int MAXIMUM_UPLOAD_PARTS = 10000;
    public static final int DEFAULT_STREAM_BUFFER_SIZE = 131073;
    private static Log log = LogFactory.getLog(AmazonS3Client.class);
    public static final int NO_SUCH_BUCKET_STATUS_CODE = 404;
    public static final int BUCKET_ACCESS_FORBIDDEN_STATUS_CODE = 403;
    public static final int BUCKET_REDIRECT_STATUS_CODE = 301;
    public static final String REQUESTER_PAYS = "requester";
    public static final String SSE_AWS_KMS_ENCRYPTION_SCHEME = SSEAlgorithm.KMS.getAlgorithm();

    @Deprecated
    public static int getStreamBufferSize() {
        int streamBufferSize = 131073;
        String bufferSizeOverride = System.getProperty("com.amazonaws.sdk.s3.defaultStreamBufferSize");
        if (bufferSizeOverride != null) {
            try {
                streamBufferSize = Integer.parseInt(bufferSizeOverride);
            }
            catch (Exception e) {
                log.warn((Object)("Unable to parse buffer size override from value: " + bufferSizeOverride));
            }
        }
        return streamBufferSize;
    }

    public static Integer getS3StreamBufferSize() {
        String s = System.getProperty("com.amazonaws.sdk.s3.defaultStreamBufferSize");
        if (s == null) {
            return null;
        }
        try {
            return Integer.valueOf(s);
        }
        catch (Exception e) {
            log.warn((Object)("Unable to parse buffer size override from value: " + s));
            return null;
        }
    }
}

