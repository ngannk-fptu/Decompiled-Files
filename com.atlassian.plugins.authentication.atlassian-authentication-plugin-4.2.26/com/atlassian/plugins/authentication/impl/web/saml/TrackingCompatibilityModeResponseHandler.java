/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.saml;

import com.google.common.base.MoreObjects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.onelogin.saml2.settings.CompatibilityModeViolationHandler;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class TrackingCompatibilityModeResponseHandler
implements CompatibilityModeViolationHandler {
    private static final Logger log = LoggerFactory.getLogger(TrackingCompatibilityModeResponseHandler.class);
    private final AtomicLong conditionlessResponseCounter = new AtomicLong();
    private final AtomicLong noAuthnStatementResponseCounter = new AtomicLong();
    private final AtomicLong multiAuthnStatementResponseCounter = new AtomicLong();
    private final Cache<String, Boolean> conditionlessResponseMessageShownCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).build();
    private final Cache<String, Boolean> noAuthnStatementsResponseMessageShownCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).build();

    public CompatibilityModeResponseData getCompatibilityModeResponseData() {
        this.conditionlessResponseMessageShownCache.invalidateAll();
        this.noAuthnStatementsResponseMessageShownCache.invalidateAll();
        return new CompatibilityModeResponseData(this.conditionlessResponseCounter.getAndSet(0L), this.noAuthnStatementResponseCounter.getAndSet(0L), this.multiAuthnStatementResponseCounter.getAndSet(0L));
    }

    @Override
    public void handleCompatibilityModeAssistedReponse(Iterable<String> issuers, boolean hasConditions, int amountOfAuthnStatements) {
        if (!hasConditions) {
            this.conditionlessResponseCounter.incrementAndGet();
            this.logWarningIfApplicable(issuers, this.conditionlessResponseMessageShownCache, "A SAML response without a Conditions element has been received. Those responses have been DEPRECATED and will not be supported in future versions of this product. The issuer of the response is [{}].");
        }
        if (amountOfAuthnStatements == 0) {
            this.noAuthnStatementResponseCounter.incrementAndGet();
            this.logWarningIfApplicable(issuers, this.noAuthnStatementsResponseMessageShownCache, "A SAML response with no AuthnStatement elements has been received. Those responses have been DEPRECATED and will not be supported in future versions of this product. The issuer of the response is [{}].");
        } else if (amountOfAuthnStatements > 1) {
            this.multiAuthnStatementResponseCounter.incrementAndGet();
        }
    }

    private void logWarningIfApplicable(Iterable<String> issuers, Cache<String, Boolean> messageShownCache, String logMessage) {
        issuers.forEach(issuer -> {
            try {
                messageShownCache.get(issuer, () -> {
                    log.warn(logMessage, issuer);
                    return true;
                });
            }
            catch (ExecutionException executionException) {
                // empty catch block
            }
        });
    }

    public static class CompatibilityModeResponseData {
        private final long amountOfConditionlessResponses;
        private final long amountOfResponsesWithNoAuthnStatements;
        private final long amountOfResponsesWithMultipleAuthnStatements;

        public CompatibilityModeResponseData(long amountOfConditionlessResponses, long amountOfResponsesWithNoAuthnStatements, long amountOfResponsesWithMultipleAuthnStatements) {
            this.amountOfConditionlessResponses = amountOfConditionlessResponses;
            this.amountOfResponsesWithNoAuthnStatements = amountOfResponsesWithNoAuthnStatements;
            this.amountOfResponsesWithMultipleAuthnStatements = amountOfResponsesWithMultipleAuthnStatements;
        }

        public long getAmountOfConditionlessResponses() {
            return this.amountOfConditionlessResponses;
        }

        public long getAmountOfResponsesWithNoAuthnStatements() {
            return this.amountOfResponsesWithNoAuthnStatements;
        }

        public long getAmountOfResponsesWithMultipleAuthnStatements() {
            return this.amountOfResponsesWithMultipleAuthnStatements;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CompatibilityModeResponseData that = (CompatibilityModeResponseData)o;
            return this.amountOfConditionlessResponses == that.amountOfConditionlessResponses && this.amountOfResponsesWithNoAuthnStatements == that.amountOfResponsesWithNoAuthnStatements && this.amountOfResponsesWithMultipleAuthnStatements == that.amountOfResponsesWithMultipleAuthnStatements;
        }

        public int hashCode() {
            return Objects.hash(this.amountOfConditionlessResponses, this.amountOfResponsesWithNoAuthnStatements, this.amountOfResponsesWithMultipleAuthnStatements);
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("amountOfConditionlessResponses", this.amountOfConditionlessResponses).add("amountOfResponsesWithNoAuthnStatements", this.amountOfResponsesWithNoAuthnStatements).add("amountOfResponsesWithMultipleAuthnStatements", this.amountOfResponsesWithMultipleAuthnStatements).toString();
        }
    }
}

