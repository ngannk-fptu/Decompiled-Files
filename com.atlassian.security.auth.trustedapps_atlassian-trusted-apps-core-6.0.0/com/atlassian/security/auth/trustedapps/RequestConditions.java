/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.AtlassianIPMatcher;
import com.atlassian.security.auth.trustedapps.DefaultURLMatcher;
import com.atlassian.security.auth.trustedapps.IPAddressFormatException;
import com.atlassian.security.auth.trustedapps.IPMatcher;
import com.atlassian.security.auth.trustedapps.URLMatcher;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class RequestConditions {
    private final long certificateTimeout;
    private final Set<String> urlPatterns;
    private final Set<String> ipPatterns;

    private RequestConditions(long certificateTimeout, Set<String> ipPatterns, Set<String> urlPatterns) {
        this.certificateTimeout = certificateTimeout;
        this.ipPatterns = Collections.unmodifiableSet(new HashSet<String>(ipPatterns));
        this.urlPatterns = Collections.unmodifiableSet(new HashSet<String>(urlPatterns));
    }

    public static RulesBuilder builder() {
        return new RulesBuilder();
    }

    public long getCertificateTimeout() {
        return this.certificateTimeout;
    }

    public URLMatcher getURLMatcher() {
        return new DefaultURLMatcher(this.urlPatterns);
    }

    public IPMatcher getIPMatcher() {
        return new AtlassianIPMatcher(this.ipPatterns);
    }

    public Iterable<String> getURLPatterns() {
        return this.urlPatterns;
    }

    public Iterable<String> getIPPatterns() {
        return this.ipPatterns;
    }

    public static final class RulesBuilder {
        private long certificateTimeout = 0L;
        private Set<String> urlPatterns = new HashSet<String>();
        private Set<String> ipPatterns = new HashSet<String>();

        private RulesBuilder() {
        }

        public RulesBuilder addURLPattern(String ... pattern) {
            for (String p : pattern) {
                this.urlPatterns.add(p);
            }
            return this;
        }

        public RulesBuilder addIPPattern(String ... pattern) throws IPAddressFormatException {
            for (String p : pattern) {
                AtlassianIPMatcher.parsePatternString(p);
                this.ipPatterns.add(p);
            }
            return this;
        }

        public RulesBuilder setCertificateTimeout(long timeout) {
            if (timeout < 0L) {
                throw new IllegalArgumentException("timeout must be >= 0");
            }
            this.certificateTimeout = timeout;
            return this;
        }

        public RequestConditions build() {
            return new RequestConditions(this.certificateTimeout, this.ipPatterns, this.urlPatterns);
        }
    }
}

