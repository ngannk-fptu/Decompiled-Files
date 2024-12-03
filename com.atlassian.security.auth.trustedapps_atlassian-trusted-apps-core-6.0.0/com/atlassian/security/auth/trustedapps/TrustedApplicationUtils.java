/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import com.atlassian.security.auth.trustedapps.request.TrustedRequest;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustedApplicationUtils {
    private static final Logger log = LoggerFactory.getLogger(TrustedApplicationUtils.class);

    public static void addRequestParameters(EncryptedCertificate certificate, TrustedRequest request) {
        request.addRequestParameter("X-Seraph-Trusted-App-ID", certificate.getID());
        request.addRequestParameter("X-Seraph-Trusted-App-Cert", certificate.getCertificate());
        request.addRequestParameter("X-Seraph-Trusted-App-Key", certificate.getSecretKey());
        Integer version = certificate.getProtocolVersion();
        if (version != null) {
            request.addRequestParameter("X-Seraph-Trusted-App-Version", version.toString());
        }
        request.addRequestParameter("X-Seraph-Trusted-App-Magic", certificate.getMagicNumber());
        if (certificate.getSignature() != null) {
            request.addRequestParameter("X-Seraph-Trusted-App-Signature", certificate.getSignature());
        }
    }

    public static TransportErrorMessage parseError(String errorMessage) {
        return TransportErrorMessage.PARSER.parse(errorMessage);
    }

    public static void validateMagicNumber(String msg, String appId, Integer protocolVersion, String magicNumber) throws InvalidCertificateException {
        if (protocolVersion != null && !Constant.MAGIC.equals(magicNumber)) {
            throw new InvalidCertificateException(new TransportErrorMessage.BadMagicNumber(msg, appId));
        }
    }

    public static Integer getProtocolVersionInUse() {
        try {
            Integer protocolVersion = Integer.parseInt(System.getProperty("trustedapps.protocol.version", "" + Constant.PROTOCOL_VERSION_DEFAULT));
            if (protocolVersion != Constant.VERSION_TWO && protocolVersion != Constant.VERSION_THREE) {
                log.warn(String.format("System Property Protocol version [%d] is not in the range [%d, %d]. Defaulting to [%d]", protocolVersion, Constant.VERSION_TWO, Constant.VERSION_THREE, Constant.PROTOCOL_VERSION_DEFAULT));
                return Constant.PROTOCOL_VERSION_DEFAULT;
            }
            return protocolVersion;
        }
        catch (NumberFormatException nfe) {
            log.error(String.format("System Property Protocol Version cannot be parsed. Defaulting to [%d]", Constant.PROTOCOL_VERSION_DEFAULT), (Throwable)nfe);
            return Constant.PROTOCOL_VERSION_DEFAULT;
        }
    }

    public static byte[] generateSignatureBaseString(long timestamp, String requestUrl, String username) throws UnsupportedEncodingException {
        String signatureBaseString = Constant.VERSION_TWO.equals(TrustedApplicationUtils.getProtocolVersionInUse()) ? TrustedApplicationUtils.generateV2SignatureBaseString(timestamp, requestUrl) : TrustedApplicationUtils.generateV3SignatureBaseString(timestamp, requestUrl, username);
        return signatureBaseString.getBytes("utf-8");
    }

    @Deprecated
    public static String generateV2SignatureBaseString(long timestamp, String requestUrl) {
        if (requestUrl != null && requestUrl.contains("\n")) {
            throw new IllegalStateException("URL to sign contains illegal character [\n]");
        }
        return Long.toString(timestamp) + "\n" + requestUrl;
    }

    public static String generateV3SignatureBaseString(long timestamp, String requestUrl, String username) {
        if (username != null && username.contains("\n")) {
            throw new IllegalStateException("Username contains illegal character [\n]");
        }
        return TrustedApplicationUtils.generateV2SignatureBaseString(timestamp, requestUrl) + "\n" + username;
    }

    public static long getLoopbackCallTimeout() {
        return Long.parseLong(System.getProperty("trustedapps.loopback.timeout", "" + Constant.LOOPBACK_CALL_TIMEOUT_MILLIS_DEFAULT));
    }

    public static final class Header {
        private static final String PREFIX = "X-Seraph-Trusted-App-";

        private Header() {
        }

        public static final class Response {
            public static final String ERROR = "X-Seraph-Trusted-App-Error";
            public static final String STATUS = "X-Seraph-Trusted-App-Status";

            private Response() {
            }
        }

        public static final class Request {
            public static final String ID = "X-Seraph-Trusted-App-ID";
            public static final String SECRET_KEY = "X-Seraph-Trusted-App-Key";
            public static final String CERTIFICATE = "X-Seraph-Trusted-App-Cert";
            public static final String VERSION = "X-Seraph-Trusted-App-Version";
            public static final String MAGIC = "X-Seraph-Trusted-App-Magic";
            public static final String SIGNATURE = "X-Seraph-Trusted-App-Signature";

            private Request() {
            }
        }
    }

    public static final class Constant {
        @Deprecated
        public static final Integer VERSION = new Integer(1);
        @Deprecated
        public static final Integer VERSION_TWO = 2;
        public static final Integer VERSION_THREE = 3;
        public static final String MAGIC = String.valueOf(-1159983122);
        public static final String CHARSET_NAME = "utf-8";
        public static final String CERTIFICATE_URL_PATH = "/admin/appTrustCertificate";
        public static final String PROTOCOL_VERSION_KEY = "trustedapps.protocol.version";
        public static final int PROTOCOL_VERSION_DEFAULT = VERSION_TWO;
        private static final String SIGNATURE_CHARSET = "utf-8";
        public static final String LOOPBACK_CALL_TIMEOUT_MILLIS_KEY = "trustedapps.loopback.timeout";
        public static final long LOOPBACK_CALL_TIMEOUT_MILLIS_DEFAULT = TimeUnit.SECONDS.toMillis(60L);
        public static final long CERTIFICATE_CACHE_TIMEOUT = 900L;

        private Constant() {
        }
    }
}

