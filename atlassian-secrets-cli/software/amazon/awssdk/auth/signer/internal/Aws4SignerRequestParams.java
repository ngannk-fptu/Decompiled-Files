/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.internal;

import java.time.Clock;
import java.time.Duration;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerUtils;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.regions.Region;

@SdkInternalApi
public final class Aws4SignerRequestParams {
    private final Clock signingClock;
    private final long requestSigningDateTimeMilli;
    private final String scope;
    private final String regionName;
    private final String serviceSigningName;
    private final String formattedRequestSigningDateTime;
    private final String formattedRequestSigningDate;

    public Aws4SignerRequestParams(Aws4SignerParams signerParams) {
        this.signingClock = this.resolveSigningClock(signerParams);
        this.requestSigningDateTimeMilli = this.signingClock.millis();
        this.formattedRequestSigningDate = Aws4SignerUtils.formatDateStamp(this.requestSigningDateTimeMilli);
        this.serviceSigningName = signerParams.signingName();
        this.regionName = this.getRegion(signerParams.signingRegion());
        this.scope = this.generateScope(this.formattedRequestSigningDate, this.serviceSigningName, this.regionName);
        this.formattedRequestSigningDateTime = Aws4SignerUtils.formatTimestamp(this.requestSigningDateTimeMilli);
    }

    public Clock getSigningClock() {
        return this.signingClock;
    }

    public String getScope() {
        return this.scope;
    }

    public String getFormattedRequestSigningDateTime() {
        return this.formattedRequestSigningDateTime;
    }

    public long getRequestSigningDateTimeMilli() {
        return this.requestSigningDateTimeMilli;
    }

    public String getRegionName() {
        return this.regionName;
    }

    public String getServiceSigningName() {
        return this.serviceSigningName;
    }

    public String getFormattedRequestSigningDate() {
        return this.formattedRequestSigningDate;
    }

    public String getSigningAlgorithm() {
        return "AWS4-HMAC-SHA256";
    }

    private Clock resolveSigningClock(Aws4SignerParams signerParams) {
        if (signerParams.signingClockOverride().isPresent()) {
            return signerParams.signingClockOverride().get();
        }
        Clock baseClock = Clock.systemUTC();
        return signerParams.timeOffset().map(offset -> Clock.offset(baseClock, Duration.ofSeconds(-offset.intValue()))).orElse(baseClock);
    }

    private String getRegion(Region region) {
        return region != null ? region.id() : null;
    }

    private String generateScope(String dateStamp, String serviceName, String regionName) {
        return dateStamp + "/" + regionName + "/" + serviceName + "/" + "aws4_request";
    }
}

