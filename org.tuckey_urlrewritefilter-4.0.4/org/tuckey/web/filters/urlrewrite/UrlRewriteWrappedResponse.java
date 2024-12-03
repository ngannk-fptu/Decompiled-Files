/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package org.tuckey.web.filters.urlrewrite;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.tuckey.web.filters.urlrewrite.RewrittenOutboundUrl;
import org.tuckey.web.filters.urlrewrite.UrlRewriter;

public class UrlRewriteWrappedResponse
extends HttpServletResponseWrapper {
    private UrlRewriter urlRerwiter;
    private HttpServletResponse httpServletResponse;
    private HttpServletRequest httpServletRequest;
    HashMap overridenRequestParameters;
    String overridenMethod;

    public UrlRewriteWrappedResponse(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, UrlRewriter urlRerwiter) {
        super(httpServletResponse);
        this.httpServletResponse = httpServletResponse;
        this.httpServletRequest = httpServletRequest;
        this.urlRerwiter = urlRerwiter;
    }

    public String encodeURL(String s) {
        RewrittenOutboundUrl rou = this.processPreEncodeURL(s);
        if (rou == null) {
            return super.encodeURL(s);
        }
        if (rou.isEncode()) {
            rou.setTarget(super.encodeURL(rou.getTarget()));
        }
        return this.processPostEncodeURL(rou.getTarget()).getTarget();
    }

    public String encodeRedirectURL(String s) {
        RewrittenOutboundUrl rou = this.processPreEncodeURL(s);
        if (rou == null) {
            return super.encodeURL(s);
        }
        if (rou.isEncode()) {
            rou.setTarget(super.encodeRedirectURL(rou.getTarget()));
        }
        return this.processPostEncodeURL(rou.getTarget()).getTarget();
    }

    public String encodeUrl(String s) {
        RewrittenOutboundUrl rou = this.processPreEncodeURL(s);
        if (rou == null) {
            return super.encodeURL(s);
        }
        if (rou.isEncode()) {
            rou.setTarget(super.encodeUrl(rou.getTarget()));
        }
        return this.processPostEncodeURL(rou.getTarget()).getTarget();
    }

    public String encodeRedirectUrl(String s) {
        RewrittenOutboundUrl rou = this.processPreEncodeURL(s);
        if (rou == null) {
            return super.encodeURL(s);
        }
        if (rou.isEncode()) {
            rou.setTarget(super.encodeRedirectUrl(rou.getTarget()));
        }
        return this.processPostEncodeURL(rou.getTarget()).getTarget();
    }

    private RewrittenOutboundUrl processPreEncodeURL(String s) {
        if (this.urlRerwiter == null) {
            return null;
        }
        return this.urlRerwiter.processEncodeURL(this.httpServletResponse, this.httpServletRequest, false, s);
    }

    private RewrittenOutboundUrl processPostEncodeURL(String s) {
        if (this.urlRerwiter == null) {
            return null;
        }
        return this.urlRerwiter.processEncodeURL(this.httpServletResponse, this.httpServletRequest, true, s);
    }

    public void addOverridenRequestParameter(String k, String v) {
        if (this.overridenRequestParameters == null) {
            this.overridenRequestParameters = new HashMap();
        }
        if (this.overridenRequestParameters.get(k) == null) {
            this.overridenRequestParameters.put(k, new String[]{v});
        } else {
            String[] currentValues = (String[])this.overridenRequestParameters.get(k);
            String[] finalValues = new String[currentValues.length + 1];
            System.arraycopy(currentValues, 0, finalValues, 0, currentValues.length);
            finalValues[finalValues.length - 1] = v;
            this.overridenRequestParameters.put(k, finalValues);
        }
    }

    public HashMap getOverridenRequestParameters() {
        return this.overridenRequestParameters;
    }

    public String getOverridenMethod() {
        return this.overridenMethod;
    }

    public void setOverridenMethod(String overridenMethod) {
        this.overridenMethod = overridenMethod;
    }
}

