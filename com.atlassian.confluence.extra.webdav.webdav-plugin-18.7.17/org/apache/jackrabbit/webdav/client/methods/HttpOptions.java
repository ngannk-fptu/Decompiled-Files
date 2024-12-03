/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.HttpOptions
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.header.FieldValueParser;

public class HttpOptions
extends org.apache.http.client.methods.HttpOptions {
    public HttpOptions(URI uri) {
        super(uri);
    }

    public HttpOptions(String uri) {
        super(URI.create(uri));
    }

    public Set<String> getDavComplianceClasses(HttpResponse response) {
        Header[] headers = response.getHeaders("DAV");
        return this.parseTokenOrCodedUrlheaderField(headers, false);
    }

    public Set<String> getSearchGrammars(HttpResponse response) {
        Header[] headers = response.getHeaders("DASL");
        return this.parseTokenOrCodedUrlheaderField(headers, true);
    }

    private Set<String> parseTokenOrCodedUrlheaderField(Header[] headers, boolean removeBrackets) {
        if (headers == null) {
            return Collections.emptySet();
        }
        HashSet<String> result = new HashSet<String>();
        for (Header h : headers) {
            for (String s : FieldValueParser.tokenizeList(h.getValue())) {
                if (removeBrackets && s.startsWith("<") && s.endsWith(">")) {
                    s = s.substring(1, s.length() - 1);
                }
                result.add(s.trim());
            }
        }
        return Collections.unmodifiableSet(result);
    }
}

