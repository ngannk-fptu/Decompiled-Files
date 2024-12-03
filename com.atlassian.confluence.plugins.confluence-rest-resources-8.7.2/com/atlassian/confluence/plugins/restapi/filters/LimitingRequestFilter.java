/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ContentTooLongException
 *  com.sun.jersey.spi.container.ContainerRequest
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  org.apache.commons.fileupload.util.LimitedInputStream
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.restapi.filters;

import com.atlassian.confluence.api.service.exceptions.ContentTooLongException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.fileupload.util.LimitedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitingRequestFilter
implements ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(LimitingRequestFilter.class);
    public static final String REQUEST_MAXSIZE_SYSTEM_PROPERTY = "atlassian.rest.request.maxsize";
    public static final long REQUEST_MAXSIZE_DEFAULT_BYTES = 65536L;
    public static final long REQUEST_MAXSIZE_LARGE_BYTES = 0x500000L;
    private final long defaultValue;

    public LimitingRequestFilter(long defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ContainerRequest filter(ContainerRequest containerRequest) {
        LimitedInputStream stream = new LimitedInputStream(containerRequest.getEntityInputStream(), this.getRequestSizeLimitBytes()){

            protected void raiseError(long pSizeMax, long pCount) throws IOException {
                throw new ContentTooLongException(String.format("Request too large. Requests for this resource can be at most %s bytes", pSizeMax));
            }
        };
        containerRequest.setEntityInputStream((InputStream)stream);
        return containerRequest;
    }

    private long getRequestSizeLimitBytes() {
        long maxRequestSizeLimit;
        String strSystemSizeLimit = System.getProperty(REQUEST_MAXSIZE_SYSTEM_PROPERTY);
        if (strSystemSizeLimit != null) {
            long systemSizeLimit;
            try {
                systemSizeLimit = Long.parseLong(strSystemSizeLimit);
            }
            catch (NumberFormatException e) {
                log.debug("Bad system property value for atlassian.rest.request.maxsize: " + e.getMessage());
                return this.defaultValue;
            }
            maxRequestSizeLimit = Long.max(systemSizeLimit, this.defaultValue);
        } else {
            maxRequestSizeLimit = this.defaultValue;
        }
        return maxRequestSizeLimit;
    }

    public long getRequestMaxSizeDefaultValue() {
        return this.defaultValue;
    }
}

