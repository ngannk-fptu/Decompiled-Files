/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 */
package com.atlassian.plugin.webresource.url;

import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.plugin.webresource.util.HashBuilder;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultUrlBuilder
implements UrlBuilder {
    private final List<String> hashes = new LinkedList<String>();
    private final Map<String, String> queryString = new LinkedHashMap<String, String>();
    private final List<PrebakeError> prebakeErrors = new LinkedList<PrebakeError>();

    public void addToHash(String name, Object value) {
        this.hashes.add(String.valueOf(value));
    }

    public void addToQueryString(String key, String value) {
        if (this.queryString.containsKey(key) && !Support.equals(value, this.queryString.get(key))) {
            Support.LOGGER.warn("Different query values found for key: {} ({} / {})", (Object[])new String[]{key, this.queryString.get(key), value});
        }
        this.queryString.put(key, value);
    }

    public void addPrebakeError(PrebakeError e) {
        this.prebakeErrors.add(e);
    }

    public void addAllPrebakeErrors(Collection<PrebakeError> es) {
        this.prebakeErrors.addAll(es);
    }

    public List<PrebakeError> getPrebakeErrors() {
        return this.prebakeErrors;
    }

    public Map<String, String> buildParams() {
        return this.queryString;
    }

    public String buildHash() {
        return HashBuilder.buildHash(this.hashes);
    }

    public List<String> getHashes() {
        return this.hashes;
    }

    public String toString() {
        return "hashes=" + this.hashes + ", queryString=" + this.queryString;
    }

    public void applyTo(UrlBuilder urlBuilder) {
        for (Map.Entry<String, String> entry : this.queryString.entrySet()) {
            urlBuilder.addToQueryString(entry.getKey(), entry.getValue());
        }
        for (String hash : this.hashes) {
            urlBuilder.addToHash(null, (Object)hash);
        }
        urlBuilder.addAllPrebakeErrors(this.prebakeErrors);
    }
}

