/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.checksums;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class ChecksumConstant {
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String ENABLE_CHECKSUM_REQUEST_HEADER = "x-amz-te";
    public static final String CHECKSUM_ENABLED_RESPONSE_HEADER = "x-amz-transfer-encoding";
    public static final String ENABLE_MD5_CHECKSUM_HEADER_VALUE = "append-md5";
    public static final String SERVER_SIDE_ENCRYPTION_HEADER = "x-amz-server-side-encryption";
    public static final String SERVER_SIDE_CUSTOMER_ENCRYPTION_HEADER = "x-amz-server-side-encryption-customer-algorithm";
    public static final int S3_MD5_CHECKSUM_LENGTH = 16;

    private ChecksumConstant() {
    }
}

