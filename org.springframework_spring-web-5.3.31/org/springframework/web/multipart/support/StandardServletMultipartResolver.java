/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.Part
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.multipart.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

public class StandardServletMultipartResolver
implements MultipartResolver {
    private boolean resolveLazily = false;
    private boolean strictServletCompliance = false;

    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    public void setStrictServletCompliance(boolean strictServletCompliance) {
        this.strictServletCompliance = strictServletCompliance;
    }

    @Override
    public boolean isMultipart(HttpServletRequest request) {
        return StringUtils.startsWithIgnoreCase((String)request.getContentType(), (String)(this.strictServletCompliance ? "multipart/form-data" : "multipart/"));
    }

    @Override
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
        return new StandardMultipartHttpServletRequest(request, this.resolveLazily);
    }

    @Override
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        if (!(request instanceof AbstractMultipartHttpServletRequest) || ((AbstractMultipartHttpServletRequest)request).isResolved()) {
            try {
                for (Part part : request.getParts()) {
                    if (request.getFile(part.getName()) == null) continue;
                    part.delete();
                }
            }
            catch (Throwable ex) {
                LogFactory.getLog(this.getClass()).warn((Object)"Failed to perform cleanup of multipart items", ex);
            }
        }
    }
}

