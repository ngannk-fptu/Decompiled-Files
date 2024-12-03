/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.interceptor.trait.HttpChecksum
 *  software.amazon.awssdk.crt.s3.ChecksumAlgorithm
 *  software.amazon.awssdk.crt.s3.ChecksumConfig
 *  software.amazon.awssdk.crt.s3.ChecksumConfig$ChecksumLocation
 *  software.amazon.awssdk.crt.s3.S3MetaRequestOptions$MetaRequestType
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.trait.HttpChecksum;
import software.amazon.awssdk.crt.s3.ChecksumAlgorithm;
import software.amazon.awssdk.crt.s3.ChecksumConfig;
import software.amazon.awssdk.crt.s3.S3MetaRequestOptions;

@SdkInternalApi
public final class CrtChecksumUtils {
    private static final ChecksumAlgorithm DEFAULT_CHECKSUM_ALGO = ChecksumAlgorithm.CRC32;

    private CrtChecksumUtils() {
    }

    public static ChecksumConfig checksumConfig(HttpChecksum httpChecksum, S3MetaRequestOptions.MetaRequestType requestType, boolean checksumValidationEnabled) {
        if (CrtChecksumUtils.checksumNotApplicable(requestType, httpChecksum)) {
            return new ChecksumConfig();
        }
        ChecksumAlgorithm checksumAlgorithm = CrtChecksumUtils.crtChecksumAlgorithm(httpChecksum, requestType, checksumValidationEnabled);
        boolean validateChecksum = CrtChecksumUtils.validateResponseChecksum(httpChecksum, requestType, checksumValidationEnabled);
        ChecksumConfig.ChecksumLocation checksumLocation = checksumAlgorithm == ChecksumAlgorithm.NONE ? ChecksumConfig.ChecksumLocation.NONE : ChecksumConfig.ChecksumLocation.TRAILER;
        return new ChecksumConfig().withChecksumAlgorithm(checksumAlgorithm).withValidateChecksum(validateChecksum).withChecksumLocation(checksumLocation).withValidateChecksumAlgorithmList(CrtChecksumUtils.checksumAlgorithmList(httpChecksum));
    }

    private static boolean checksumNotApplicable(S3MetaRequestOptions.MetaRequestType requestType, HttpChecksum httpChecksum) {
        if (requestType != S3MetaRequestOptions.MetaRequestType.PUT_OBJECT && requestType != S3MetaRequestOptions.MetaRequestType.GET_OBJECT) {
            return true;
        }
        return httpChecksum == null;
    }

    private static List<ChecksumAlgorithm> checksumAlgorithmList(HttpChecksum httpChecksum) {
        if (httpChecksum.responseAlgorithms() == null) {
            return null;
        }
        return httpChecksum.responseAlgorithms().stream().map(CrtChecksumUtils::toCrtChecksumAlgorithm).collect(Collectors.toList());
    }

    private static ChecksumAlgorithm crtChecksumAlgorithm(HttpChecksum httpChecksum, S3MetaRequestOptions.MetaRequestType requestType, boolean checksumValidationEnabled) {
        if (requestType != S3MetaRequestOptions.MetaRequestType.PUT_OBJECT) {
            return ChecksumAlgorithm.NONE;
        }
        if (httpChecksum.requestAlgorithm() == null) {
            return checksumValidationEnabled ? DEFAULT_CHECKSUM_ALGO : ChecksumAlgorithm.NONE;
        }
        return CrtChecksumUtils.toCrtChecksumAlgorithm(httpChecksum.requestAlgorithm());
    }

    private static ChecksumAlgorithm toCrtChecksumAlgorithm(String sdkChecksum) {
        return ChecksumAlgorithm.valueOf((String)sdkChecksum.toUpperCase());
    }

    private static boolean validateResponseChecksum(HttpChecksum httpChecksum, S3MetaRequestOptions.MetaRequestType requestType, boolean checksumValidationEnabled) {
        if (requestType != S3MetaRequestOptions.MetaRequestType.GET_OBJECT) {
            return false;
        }
        return checksumValidationEnabled || httpChecksum.requestValidationMode() != null;
    }
}

