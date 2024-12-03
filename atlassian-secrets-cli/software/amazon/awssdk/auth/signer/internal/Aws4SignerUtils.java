/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.internal;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public final class Aws4SignerUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("UTC"));
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneId.of("UTC"));

    private Aws4SignerUtils() {
    }

    public static String formatDateStamp(long timeMilli) {
        return DATE_FORMATTER.format(Instant.ofEpochMilli(timeMilli));
    }

    public static String formatDateStamp(Instant instant) {
        return DATE_FORMATTER.format(instant);
    }

    public static String formatTimestamp(long timeMilli) {
        return TIME_FORMATTER.format(Instant.ofEpochMilli(timeMilli));
    }

    public static String formatTimestamp(Instant instant) {
        return TIME_FORMATTER.format(instant);
    }

    public static long calculateRequestContentLength(SdkHttpFullRequest.Builder mutableRequest) {
        long originalContentLength;
        String contentLength = mutableRequest.firstMatchingHeader("Content-Length").orElse(null);
        if (contentLength != null) {
            originalContentLength = Long.parseLong(contentLength);
        } else {
            try {
                originalContentLength = Aws4SignerUtils.getContentLength(mutableRequest.contentStreamProvider().newStream());
            }
            catch (IOException e) {
                throw SdkClientException.builder().message("Cannot get the content-length of the request content.").cause(e).build();
            }
        }
        return originalContentLength;
    }

    private static long getContentLength(InputStream content) throws IOException {
        int read;
        long contentLength = 0L;
        byte[] tmp = new byte[4096];
        while ((read = content.read(tmp)) != -1) {
            contentLength += (long)read;
        }
        return contentLength;
    }
}

