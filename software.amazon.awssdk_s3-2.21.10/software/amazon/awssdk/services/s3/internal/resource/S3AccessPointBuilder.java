/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.services.s3.internal.resource;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public class S3AccessPointBuilder {
    private static final Pattern HOSTNAME_COMPLIANT_PATTERN = Pattern.compile("[A-Za-z0-9\\-]+");
    private static final int HOSTNAME_MAX_LENGTH = 63;
    private URI endpointOverride;
    private Boolean dualstackEnabled;
    private String accessPointName;
    private String region;
    private String accountId;
    private String protocol;
    private String domain;
    private Boolean fipsEnabled;

    public static S3AccessPointBuilder create() {
        return new S3AccessPointBuilder();
    }

    public S3AccessPointBuilder endpointOverride(URI endpointOverride) {
        this.endpointOverride = endpointOverride;
        return this;
    }

    public S3AccessPointBuilder dualstackEnabled(Boolean dualstackEnabled) {
        this.dualstackEnabled = dualstackEnabled;
        return this;
    }

    public S3AccessPointBuilder fipsEnabled(Boolean fipsEnabled) {
        this.fipsEnabled = fipsEnabled;
        return this;
    }

    public S3AccessPointBuilder accessPointName(String accessPointName) {
        this.accessPointName = accessPointName;
        return this;
    }

    public S3AccessPointBuilder region(String region) {
        this.region = region;
        return this;
    }

    public S3AccessPointBuilder accountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public S3AccessPointBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public S3AccessPointBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    public URI toUri() {
        this.validateComponents();
        String uriString = this.hasEndpointOverride() ? this.createEndpointOverrideUri() : this.createAccesspointUri();
        URI result = URI.create(uriString);
        if (result.getHost() == null) {
            throw SdkClientException.create((String)("Request resulted in an invalid URI: " + result));
        }
        return result;
    }

    private boolean hasEndpointOverride() {
        return this.endpointOverride != null;
    }

    private String createAccesspointUri() {
        String uri;
        if (this.isGlobal()) {
            uri = String.format("%s://%s.accesspoint.s3-global.%s", this.protocol, SdkHttpUtils.urlEncode((String)this.accessPointName), this.domain);
        } else {
            String fipsSegment = Boolean.TRUE.equals(this.fipsEnabled) ? "-fips" : "";
            String dualStackSegment = Boolean.TRUE.equals(this.dualstackEnabled) ? ".dualstack" : "";
            uri = String.format("%s://%s-%s.s3-accesspoint%s%s.%s.%s", this.protocol, SdkHttpUtils.urlEncode((String)this.accessPointName), this.accountId, fipsSegment, dualStackSegment, this.region, this.domain);
        }
        return uri;
    }

    private String createEndpointOverrideUri() {
        Validate.isTrue((!Boolean.TRUE.equals(this.fipsEnabled) ? 1 : 0) != 0, (String)"FIPS regions are not supported with an endpoint override specified", (Object[])new Object[0]);
        Validate.isTrue((!Boolean.TRUE.equals(this.dualstackEnabled) ? 1 : 0) != 0, (String)"Dual stack is not supported with an endpoint override specified", (Object[])new Object[0]);
        StringBuilder uriSuffix = new StringBuilder(this.endpointOverride.getHost());
        if (this.endpointOverride.getPort() > 0) {
            uriSuffix.append(":").append(this.endpointOverride.getPort());
        }
        if (this.endpointOverride.getPath() != null) {
            uriSuffix.append(this.endpointOverride.getPath());
        }
        String uri = this.isGlobal() ? String.format("%s://%s.%s", this.protocol, SdkHttpUtils.urlEncode((String)this.accessPointName), uriSuffix) : String.format("%s://%s-%s.%s", this.protocol, SdkHttpUtils.urlEncode((String)this.accessPointName), this.accountId, uriSuffix);
        return uri;
    }

    private boolean isGlobal() {
        return StringUtils.isEmpty((CharSequence)this.region);
    }

    private void validateComponents() {
        S3AccessPointBuilder.validateHostnameCompliant(this.accountId, "accountId");
        if (this.isGlobal()) {
            Stream.of(this.accessPointName.split("\\.")).forEach(segment -> S3AccessPointBuilder.validateHostnameCompliant(segment, segment));
        } else {
            S3AccessPointBuilder.validateHostnameCompliant(this.accessPointName, "accessPointName");
        }
    }

    private static void validateHostnameCompliant(String hostnameComponent, String paramName) {
        if (hostnameComponent.isEmpty()) {
            throw new IllegalArgumentException(String.format("An S3 Access Point ARN has been passed that is not valid: the required '%s' component is missing.", paramName));
        }
        if (hostnameComponent.length() > 63) {
            throw new IllegalArgumentException(String.format("An S3 Access Point ARN has been passed that is not valid: the '%s' component exceeds the maximum length of %d characters.", paramName, 63));
        }
        Matcher m = HOSTNAME_COMPLIANT_PATTERN.matcher(hostnameComponent);
        if (!m.matches()) {
            throw new IllegalArgumentException(String.format("An S3 Access Point ARN has been passed that is not valid: the '%s' component must only contain alphanumeric characters and dashes.", paramName));
        }
    }
}

