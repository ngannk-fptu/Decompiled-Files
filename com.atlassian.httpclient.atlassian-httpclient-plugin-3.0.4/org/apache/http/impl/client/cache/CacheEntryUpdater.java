/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.util.Date;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.cache.HeapResourceFactory;
import org.apache.http.message.HeaderGroup;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
class CacheEntryUpdater {
    private final ResourceFactory resourceFactory;

    CacheEntryUpdater() {
        this(new HeapResourceFactory());
    }

    CacheEntryUpdater(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public HttpCacheEntry updateCacheEntry(String requestId, HttpCacheEntry entry, Date requestDate, Date responseDate, HttpResponse response) throws IOException {
        Args.check(response.getStatusLine().getStatusCode() == 304, "Response must have 304 status code");
        Header[] mergedHeaders = this.mergeHeaders(entry, response);
        Resource resource = null;
        if (entry.getResource() != null) {
            resource = this.resourceFactory.copy(requestId, entry.getResource());
        }
        return new HttpCacheEntry(requestDate, responseDate, entry.getStatusLine(), mergedHeaders, resource, entry.getRequestMethod());
    }

    protected Header[] mergeHeaders(HttpCacheEntry entry, HttpResponse response) {
        Header responseHeader;
        if (this.entryAndResponseHaveDateHeader(entry, response) && this.entryDateHeaderNewerThenResponse(entry, response)) {
            return entry.getAllHeaders();
        }
        HeaderGroup headerGroup = new HeaderGroup();
        headerGroup.setHeaders(entry.getAllHeaders());
        HeaderIterator it = response.headerIterator();
        while (it.hasNext()) {
            Header[] matchingHeaders;
            responseHeader = it.nextHeader();
            if ("Content-Encoding".equals(responseHeader.getName()) || "Content-Length".equals(responseHeader.getName())) continue;
            for (Header matchingHeader : matchingHeaders = headerGroup.getHeaders(responseHeader.getName())) {
                headerGroup.removeHeader(matchingHeader);
            }
        }
        it = headerGroup.iterator();
        while (it.hasNext()) {
            String warningValue;
            Header cacheHeader = it.nextHeader();
            if (!"Warning".equalsIgnoreCase(cacheHeader.getName()) || (warningValue = cacheHeader.getValue()) == null || !warningValue.startsWith("1")) continue;
            it.remove();
        }
        it = response.headerIterator();
        while (it.hasNext()) {
            responseHeader = it.nextHeader();
            if ("Content-Encoding".equals(responseHeader.getName()) || "Content-Length".equals(responseHeader.getName())) continue;
            headerGroup.addHeader(responseHeader);
        }
        return headerGroup.getAllHeaders();
    }

    private boolean entryDateHeaderNewerThenResponse(HttpCacheEntry entry, HttpResponse response) {
        Date entryDate = DateUtils.parseDate(entry.getFirstHeader("Date").getValue());
        Date responseDate = DateUtils.parseDate(response.getFirstHeader("Date").getValue());
        return entryDate != null && responseDate != null && entryDate.after(responseDate);
    }

    private boolean entryAndResponseHaveDateHeader(HttpCacheEntry entry, HttpResponse response) {
        return entry.getFirstHeader("Date") != null && response.getFirstHeader("Date") != null;
    }
}

