/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.DefaultTrustedApplication
 *  com.atlassian.security.auth.trustedapps.EncryptionProvider
 *  com.atlassian.security.auth.trustedapps.RequestConditions
 *  com.atlassian.security.auth.trustedapps.RequestConditions$RulesBuilder
 *  com.atlassian.security.auth.trustedapps.TrustedApplication
 *  com.google.common.base.Preconditions
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.collections.Predicate
 *  org.apache.commons.collections.PredicateUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.confluence.security.trust.TrustedApplicationIpRestriction;
import com.atlassian.confluence.security.trust.TrustedApplicationRestriction;
import com.atlassian.confluence.security.trust.TrustedApplicationUrlRestriction;
import com.atlassian.security.auth.trustedapps.DefaultTrustedApplication;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.google.common.base.Preconditions;
import java.security.PublicKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfluenceTrustedApplication {
    private long id;
    private String name;
    private int requestTimeout;
    private AliasedKey publicKey;
    private Set<TrustedApplicationRestriction> restrictions = new HashSet<TrustedApplicationRestriction>();

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRequestTimeout() {
        return this.requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public AliasedKey getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(AliasedKey publicKey) {
        this.publicKey = publicKey;
    }

    public Set<TrustedApplicationRestriction> getRestrictions() {
        return this.restrictions;
    }

    public void setRestrictions(Set<TrustedApplicationRestriction> restrictions) {
        if (restrictions == null) {
            restrictions = new HashSet<TrustedApplicationRestriction>();
        }
        this.restrictions = restrictions;
    }

    public void addRestriction(TrustedApplicationRestriction restriction) {
        this.restrictions.add(restriction);
    }

    public boolean removeRestriction(TrustedApplicationRestriction restriction) {
        return this.restrictions.remove(restriction);
    }

    public Set<String> getUrlRestrictions() {
        return this.mapRestrictionsToStrings(this.getRestrictionsByType(TrustedApplicationUrlRestriction.class));
    }

    public Set<String> getIpRestrictions() {
        return this.mapRestrictionsToStrings(this.getRestrictionsByType(TrustedApplicationIpRestriction.class));
    }

    private <T> Set<T> getRestrictionsByType(Class<T> restrictionType) {
        return Collections.unmodifiableSet(new HashSet(CollectionUtils.select(this.restrictions, (Predicate)PredicateUtils.instanceofPredicate(restrictionType))));
    }

    private Set<String> mapRestrictionsToStrings(Set<? extends TrustedApplicationRestriction> restrictions) {
        HashSet<String> restrictionStrings = new HashSet<String>(restrictions.size());
        for (TrustedApplicationRestriction trustedApplicationRestriction : restrictions) {
            restrictionStrings.add(trustedApplicationRestriction.getRestriction());
        }
        return restrictionStrings;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConfluenceTrustedApplication that = (ConfluenceTrustedApplication)o;
        return this.id == that.id;
    }

    public int hashCode() {
        return (int)(this.id ^ this.id >>> 32);
    }

    public final TrustedApplication toDefaultTrustedApplication(@NonNull EncryptionProvider encryptionProvider) {
        Preconditions.checkNotNull((Object)encryptionProvider, (Object)"The encryptionProvider cannot be null");
        RequestConditions.RulesBuilder builder = RequestConditions.builder();
        Set<String> ip = this.getIpRestrictions();
        builder.addIPPattern(ip.toArray(new String[ip.size()]));
        Set<String> url = this.getUrlRestrictions();
        builder.addURLPattern(url.toArray(new String[url.size()]));
        return new DefaultTrustedApplication(encryptionProvider, (PublicKey)this.getPublicKey().getKey(), this.getPublicKey().getAlias(), this.getName(), builder.setCertificateTimeout((long)this.getRequestTimeout()).build());
    }
}

