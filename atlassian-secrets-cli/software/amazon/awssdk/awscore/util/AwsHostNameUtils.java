/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.regions.Region;

@SdkProtectedApi
public final class AwsHostNameUtils {
    private static final Pattern S3_ENDPOINT_PATTERN = Pattern.compile("^(?:.+\\.)?s3[.-]([a-z0-9-]+)$");
    private static final Pattern STANDARD_CLOUDSEARCH_ENDPOINT_PATTERN = Pattern.compile("^(?:.+\\.)?([a-z0-9-]+)\\.cloudsearch$");
    private static final Pattern EXTENDED_CLOUDSEARCH_ENDPOINT_PATTERN = Pattern.compile("^(?:.+\\.)?([a-z0-9-]+)\\.cloudsearch\\..+");

    private AwsHostNameUtils() {
    }

    public static Optional<Region> parseSigningRegion(String host, String serviceHint) {
        if (host == null) {
            throw new IllegalArgumentException("hostname cannot be null");
        }
        if (host.endsWith(".amazonaws.com")) {
            int index = host.length() - ".amazonaws.com".length();
            return AwsHostNameUtils.parseStandardRegionName(host.substring(0, index));
        }
        if (serviceHint != null) {
            Matcher matcher;
            if (serviceHint.equals("cloudsearch") && !host.startsWith("cloudsearch.") && (matcher = EXTENDED_CLOUDSEARCH_ENDPOINT_PATTERN.matcher(host)).matches()) {
                return Optional.of(Region.of(matcher.group(1)));
            }
            Pattern pattern = Pattern.compile("^(?:.+\\.)?" + Pattern.quote(serviceHint) + "[.-]([a-z0-9-]+)\\.");
            Matcher matcher2 = pattern.matcher(host);
            if (matcher2.find()) {
                return Optional.of(Region.of(matcher2.group(1)));
            }
        }
        return Optional.empty();
    }

    private static Optional<Region> parseStandardRegionName(String fragment) {
        Matcher matcher = S3_ENDPOINT_PATTERN.matcher(fragment);
        if (matcher.matches()) {
            return Optional.of(Region.of(matcher.group(1)));
        }
        matcher = STANDARD_CLOUDSEARCH_ENDPOINT_PATTERN.matcher(fragment);
        if (matcher.matches()) {
            return Optional.of(Region.of(matcher.group(1)));
        }
        int index = fragment.lastIndexOf(46);
        if (index == -1) {
            return Optional.of(Region.US_EAST_1);
        }
        String region = fragment.substring(index + 1);
        if ("us-gov".equals(region)) {
            region = "us-gov-west-1";
        }
        if ("s3".equals(region)) {
            region = fragment.substring(index + 4);
        }
        return Optional.of(Region.of(region));
    }
}

