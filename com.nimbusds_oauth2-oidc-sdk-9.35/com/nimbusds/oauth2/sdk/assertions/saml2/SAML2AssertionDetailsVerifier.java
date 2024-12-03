/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.proc.ClockSkewAware
 *  com.nimbusds.jwt.util.DateUtils
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.assertions.saml2;

import com.nimbusds.jwt.proc.ClockSkewAware;
import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.oauth2.sdk.assertions.saml2.BadSAML2AssertionException;
import com.nimbusds.oauth2.sdk.assertions.saml2.SAML2AssertionDetails;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import java.util.Date;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class SAML2AssertionDetailsVerifier
implements ClockSkewAware {
    public static final int DEFAULT_MAX_CLOCK_SKEW_SECONDS = 60;
    private static final BadSAML2AssertionException EXPIRED_SAML2_ASSERTION_EXCEPTION = new BadSAML2AssertionException("Expired SAML 2.0 assertion");
    private static final BadSAML2AssertionException SAML2_ASSERTION_BEFORE_USE_EXCEPTION = new BadSAML2AssertionException("SAML 2.0 assertion before use time");
    private final Set<Audience> expectedAudience;
    private final BadSAML2AssertionException unexpectedAudienceException;
    private int maxClockSkewSeconds = 60;

    public SAML2AssertionDetailsVerifier(Set<Audience> expectedAudience) {
        if (CollectionUtils.isEmpty(expectedAudience)) {
            throw new IllegalArgumentException("The expected audience set must not be null or empty");
        }
        this.expectedAudience = expectedAudience;
        this.unexpectedAudienceException = new BadSAML2AssertionException("Invalid SAML 2.0 audience, expected " + expectedAudience);
    }

    public Set<Audience> getExpectedAudience() {
        return this.expectedAudience;
    }

    public int getMaxClockSkew() {
        return this.maxClockSkewSeconds;
    }

    public void setMaxClockSkew(int maxClockSkewSeconds) {
        this.maxClockSkewSeconds = maxClockSkewSeconds;
    }

    public void verify(SAML2AssertionDetails assertionDetails) throws BadSAML2AssertionException {
        if (!Audience.matchesAny(this.expectedAudience, assertionDetails.getAudience())) {
            throw this.unexpectedAudienceException;
        }
        Date now = new Date();
        if (!DateUtils.isAfter((Date)assertionDetails.getExpirationTime(), (Date)now, (long)this.maxClockSkewSeconds)) {
            throw EXPIRED_SAML2_ASSERTION_EXCEPTION;
        }
        if (assertionDetails.getNotBeforeTime() != null && !DateUtils.isBefore((Date)assertionDetails.getNotBeforeTime(), (Date)now, (long)this.maxClockSkewSeconds)) {
            throw SAML2_ASSERTION_BEFORE_USE_EXCEPTION;
        }
    }
}

