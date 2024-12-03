/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.confluence.plugins.search.event;

import com.atlassian.analytics.api.annotations.EventName;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.codec.digest.DigestUtils;

@EventName(value="confluence.search.SiteSearchComplete")
public class SiteSearchCompleteEvent {
    private static final Pattern PATTERN = Pattern.compile("\\w+");
    private final String queryHash;
    private final String cqlHash;
    private final int numberOfTerms;
    private final int numberOfDocs;
    private final String uuid;
    private final boolean odd;

    public SiteSearchCompleteEvent(@Nonnull String queryString, @Nonnull String cqlString, @Nonnull String sessionId, int numberOfDocs, boolean odd) {
        this.queryHash = queryString.isEmpty() ? "" : DigestUtils.md5Hex((String)queryString);
        this.cqlHash = cqlString.isEmpty() ? "" : DigestUtils.md2Hex((String)cqlString);
        this.numberOfTerms = this.getNumOfWords(queryString);
        this.uuid = DigestUtils.md5Hex((String)queryString.concat(sessionId));
        this.numberOfDocs = numberOfDocs;
        this.odd = odd;
    }

    private int getNumOfWords(String str) {
        Matcher matcher = PATTERN.matcher(str);
        int n = 0;
        while (matcher.find()) {
            ++n;
        }
        return n;
    }

    public String getQueryHash() {
        return this.queryHash;
    }

    public String getCqlHash() {
        return this.cqlHash;
    }

    public int getNumberOfTerms() {
        return this.numberOfTerms;
    }

    public int getNumberOfDocs() {
        return this.numberOfDocs;
    }

    public String getUuid() {
        return this.uuid;
    }

    public boolean isOdd() {
        return this.odd;
    }
}

