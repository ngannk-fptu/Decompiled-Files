/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.rest.module.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;

@Provider
public class AcceptHeaderJerseyMvcFilter
implements ContainerRequestFilter {
    static final Set<String> ACCEPTED_CONTENT_TYPES = new HashSet<String>();

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        MultivaluedMap<String, String> requestHeaders = request.getRequestHeaders();
        String acceptHeader = requestHeaders.getFirst("Accept");
        String fixedHeader = this.addAppXmlWhenWildcardOnly(acceptHeader);
        fixedHeader = this.moveTextHtmlToFront(fixedHeader);
        if (acceptHeader != null && !acceptHeader.equals(fixedHeader)) {
            requestHeaders.putSingle("Accept", fixedHeader);
        }
        return request;
    }

    private String addAppXmlWhenWildcardOnly(String acceptHeader) {
        if (StringUtils.contains((CharSequence)acceptHeader, (CharSequence)"*/*")) {
            for (String contentType : ACCEPTED_CONTENT_TYPES) {
                if (!StringUtils.contains((CharSequence)acceptHeader, (CharSequence)contentType)) continue;
                return acceptHeader;
            }
            return "application/xml," + acceptHeader;
        }
        return acceptHeader;
    }

    private String moveTextHtmlToFront(String acceptHeader) {
        if ((StringUtils.contains((CharSequence)acceptHeader, (CharSequence)"text/html") || StringUtils.contains((CharSequence)acceptHeader, (CharSequence)"*/*")) && !StringUtils.startsWith((CharSequence)acceptHeader, (CharSequence)"text/html")) {
            return "text/html," + acceptHeader;
        }
        return acceptHeader;
    }

    static {
        ACCEPTED_CONTENT_TYPES.addAll(Arrays.asList("text/plain", "text/html", "application/json", "application/xml", "application/atom+xml"));
    }
}

