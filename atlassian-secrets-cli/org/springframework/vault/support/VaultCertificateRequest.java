/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class VaultCertificateRequest {
    private final String commonName;
    private final List<String> altNames;
    private final List<String> ipSubjectAltNames;
    private final List<String> uriSubjectAltNames;
    @Nullable
    private final Duration ttl;
    private final boolean excludeCommonNameFromSubjectAltNames;

    private VaultCertificateRequest(String commonName, List<String> altNames, List<String> ipSubjectAltNames, List<String> uriSubjectAltNames, @Nullable Duration ttl, boolean excludeCommonNameFromSubjectAltNames) {
        this.commonName = commonName;
        this.altNames = altNames;
        this.ipSubjectAltNames = ipSubjectAltNames;
        this.uriSubjectAltNames = uriSubjectAltNames;
        this.ttl = ttl;
        this.excludeCommonNameFromSubjectAltNames = excludeCommonNameFromSubjectAltNames;
    }

    public static VaultCertificateRequestBuilder builder() {
        return new VaultCertificateRequestBuilder();
    }

    public static VaultCertificateRequest create(String commonName) {
        return VaultCertificateRequest.builder().commonName(commonName).build();
    }

    public String getCommonName() {
        return this.commonName;
    }

    public List<String> getAltNames() {
        return this.altNames;
    }

    public List<String> getIpSubjectAltNames() {
        return this.ipSubjectAltNames;
    }

    public List<String> getUriSubjectAltNames() {
        return this.uriSubjectAltNames;
    }

    @Nullable
    public Duration getTtl() {
        return this.ttl;
    }

    public boolean isExcludeCommonNameFromSubjectAltNames() {
        return this.excludeCommonNameFromSubjectAltNames;
    }

    public static class VaultCertificateRequestBuilder {
        @Nullable
        private String commonName;
        private List<String> altNames = new ArrayList<String>();
        private List<String> ipSubjectAltNames = new ArrayList<String>();
        private List<String> uriSubjectAltNames = new ArrayList<String>();
        @Nullable
        private Duration ttl;
        private boolean excludeCommonNameFromSubjectAltNames;

        VaultCertificateRequestBuilder() {
        }

        public VaultCertificateRequestBuilder commonName(String commonName) {
            Assert.hasText(commonName, "Common name must not be empty");
            this.commonName = commonName;
            return this;
        }

        public VaultCertificateRequestBuilder altNames(Iterable<String> altNames) {
            Assert.notNull(altNames, "Alt names must not be null");
            this.altNames = VaultCertificateRequestBuilder.toList(altNames);
            return this;
        }

        public VaultCertificateRequestBuilder withAltName(String altName) {
            Assert.hasText(altName, "Alt name must not be empty");
            this.altNames.add(altName);
            return this;
        }

        public VaultCertificateRequestBuilder ipSubjectAltNames(Iterable<String> ipSubjectAltNames) {
            Assert.notNull(ipSubjectAltNames, "IP subject alt names must not be null");
            this.ipSubjectAltNames = VaultCertificateRequestBuilder.toList(ipSubjectAltNames);
            return this;
        }

        public VaultCertificateRequestBuilder withIpSubjectAltName(String ipSubjectAltName) {
            Assert.hasText(ipSubjectAltName, "IP subject alt name must not be empty");
            this.ipSubjectAltNames.add(ipSubjectAltName);
            return this;
        }

        public VaultCertificateRequestBuilder uriSubjectAltNames(Iterable<String> uriSubjectAltNames) {
            Assert.notNull(uriSubjectAltNames, "URI subject alt names must not be null");
            this.uriSubjectAltNames = VaultCertificateRequestBuilder.toList(uriSubjectAltNames);
            return this;
        }

        public VaultCertificateRequestBuilder withUriSubjectAltName(String uriSubjectAltName) {
            Assert.hasText(uriSubjectAltName, "URI subject alt name must not be empty");
            this.uriSubjectAltNames.add(uriSubjectAltName);
            return this;
        }

        @Deprecated
        public VaultCertificateRequestBuilder ttl(int ttl) {
            Assert.isTrue(ttl > 0, "TTL must not be negative");
            this.ttl = Duration.ofSeconds(ttl);
            return this;
        }

        public VaultCertificateRequestBuilder ttl(long ttl, TimeUnit timeUnit) {
            Assert.isTrue(ttl > 0L, "TTL must not be negative");
            Assert.notNull((Object)timeUnit, "TimeUnit must be greater 0");
            this.ttl = Duration.ofSeconds(timeUnit.toSeconds(ttl));
            return this;
        }

        public VaultCertificateRequestBuilder ttl(Duration ttl) {
            Assert.notNull((Object)ttl, "TTL must not be null");
            Assert.isTrue(!ttl.isNegative(), "TTL must not be negative");
            this.ttl = ttl;
            return this;
        }

        public VaultCertificateRequestBuilder excludeCommonNameFromSubjectAltNames() {
            this.excludeCommonNameFromSubjectAltNames = true;
            return this;
        }

        public VaultCertificateRequest build() {
            List<Object> uriSubjectAltNames;
            List<Object> ipSubjectAltNames;
            List<Object> altNames;
            Assert.notNull((Object)this.commonName, "Common name must not be null");
            Assert.hasText(this.commonName, "Common name must not be empty");
            switch (this.altNames.size()) {
                case 0: {
                    altNames = Collections.emptyList();
                    break;
                }
                case 1: {
                    altNames = Collections.singletonList(this.altNames.get(0));
                    break;
                }
                default: {
                    altNames = Collections.unmodifiableList(new ArrayList<String>(this.altNames));
                }
            }
            switch (this.ipSubjectAltNames.size()) {
                case 0: {
                    ipSubjectAltNames = Collections.emptyList();
                    break;
                }
                case 1: {
                    ipSubjectAltNames = Collections.singletonList(this.ipSubjectAltNames.get(0));
                    break;
                }
                default: {
                    ipSubjectAltNames = Collections.unmodifiableList(new ArrayList<String>(this.ipSubjectAltNames));
                }
            }
            switch (this.uriSubjectAltNames.size()) {
                case 0: {
                    uriSubjectAltNames = Collections.emptyList();
                    break;
                }
                case 1: {
                    uriSubjectAltNames = Collections.singletonList(this.uriSubjectAltNames.get(0));
                    break;
                }
                default: {
                    uriSubjectAltNames = Collections.unmodifiableList(new ArrayList<String>(this.uriSubjectAltNames));
                }
            }
            return new VaultCertificateRequest(this.commonName, altNames, ipSubjectAltNames, uriSubjectAltNames, this.ttl, this.excludeCommonNameFromSubjectAltNames);
        }

        private static <E> List<E> toList(Iterable<E> iter) {
            ArrayList<E> list = new ArrayList<E>();
            for (E item : iter) {
                list.add(item);
            }
            return list;
        }
    }
}

