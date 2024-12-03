/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite.gzip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.GZIPOutputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.gzip.GenericResponseWrapper;
import org.tuckey.web.filters.urlrewrite.gzip.ResponseUtil;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public class GzipFilter
implements Filter {
    private static final Log LOG = Log.getLog(GzipFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public final void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)sRequest;
        HttpServletResponse response = (HttpServletResponse)sResponse;
        if (!this.isIncluded(request) && this.headerContainsAcceptEncodingGzip(request) && !response.isCommitted()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(request.getRequestURL() + ". Writing with gzip compression");
            }
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            GZIPOutputStream gzout = new GZIPOutputStream(compressed);
            GenericResponseWrapper wrapper = new GenericResponseWrapper(response, gzout);
            wrapper.setDisableFlushBuffer();
            chain.doFilter((ServletRequest)request, (ServletResponse)wrapper);
            wrapper.flush();
            gzout.close();
            if (response.isCommitted()) {
                return;
            }
            switch (wrapper.getStatus()) {
                case 204: 
                case 205: 
                case 304: {
                    return;
                }
            }
            byte[] compressedBytes = compressed.toByteArray();
            boolean shouldGzippedBodyBeZero = ResponseUtil.shouldGzippedBodyBeZero(compressedBytes, request);
            boolean shouldBodyBeZero = ResponseUtil.shouldBodyBeZero(request, wrapper.getStatus());
            if (shouldGzippedBodyBeZero || shouldBodyBeZero) {
                response.setContentLength(0);
                return;
            }
            ResponseUtil.addGzipHeader(response);
            response.setContentLength(compressedBytes.length);
            response.getOutputStream().write(compressedBytes);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug(request.getRequestURL() + ". Writing without gzip compression because the request does not accept gzip.");
            }
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }

    private boolean isIncluded(HttpServletRequest request) {
        boolean includeRequest;
        String uri = (String)request.getAttribute("javax.servlet.include.request_uri");
        boolean bl = includeRequest = uri != null;
        if (includeRequest && LOG.isDebugEnabled()) {
            LOG.debug(request.getRequestURL() + " resulted in an include request. This is unusable, because" + "the response will be assembled into the overrall response. Not gzipping.");
        }
        return includeRequest;
    }

    private boolean headerContainsAcceptEncodingGzip(HttpServletRequest request) {
        Enumeration accepted = request.getHeaders("Accept-Encoding");
        while (accepted.hasMoreElements()) {
            String headerValue = (String)accepted.nextElement();
            if (headerValue.indexOf("gzip") == -1) continue;
            return true;
        }
        return false;
    }
}

