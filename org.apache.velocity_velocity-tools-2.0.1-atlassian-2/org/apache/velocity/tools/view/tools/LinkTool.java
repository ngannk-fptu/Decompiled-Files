/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.velocity.tools.view.tools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.tools.generic.ValueParser;
import org.apache.velocity.tools.view.context.ViewContext;

@Deprecated
public class LinkTool
extends org.apache.velocity.tools.view.LinkTool {
    @Deprecated
    public static final String SELF_ABSOLUTE_KEY = "self-absolute";
    @Deprecated
    public static final String SELF_INCLUDE_PARAMETERS_KEY = "self-include-parameters";
    @Deprecated
    public static final String AUTO_IGNORE_PARAMETERS_KEY = "auto-ignore-parameters";
    @Deprecated
    protected ServletContext application;
    private HashSet<String> parametersToIgnore;
    private boolean autoIgnore = true;

    @Override
    protected void configure(ValueParser parser) {
        Boolean autoIgnoreParams;
        Boolean selfParams;
        Boolean selfAbsolute = parser.getBoolean(SELF_ABSOLUTE_KEY);
        if (selfAbsolute != null) {
            this.setForceRelative(selfAbsolute == false);
        }
        if ((selfParams = parser.getBoolean(SELF_INCLUDE_PARAMETERS_KEY)) != null) {
            this.setIncludeRequestParams(selfParams);
        }
        if ((autoIgnoreParams = parser.getBoolean(AUTO_IGNORE_PARAMETERS_KEY)) != null) {
            this.setAutoIgnoreParameters(autoIgnoreParams);
        }
        super.configure(parser);
    }

    @Deprecated
    public void init(Object obj) {
        if (obj instanceof ViewContext) {
            ViewContext ctx = (ViewContext)obj;
            this.setRequest(ctx.getRequest());
            this.setResponse(ctx.getResponse());
            this.application = ctx.getServletContext();
            if (ctx.getVelocityEngine() != null) {
                this.LOG = ctx.getVelocityEngine().getLog();
            }
        }
    }

    @Deprecated
    public void setXhtml(boolean useXhtml) {
        this.setXHTML(useXhtml);
    }

    @Deprecated
    public void setSelfAbsolute(boolean selfAbsolute) {
        this.setForceRelative(!selfAbsolute);
    }

    @Deprecated
    public void setSelfIncludeParameters(boolean selfParams) {
        this.setIncludeRequestParams(selfParams);
    }

    @Deprecated
    public void setAutoIgnoreParameters(boolean autoIgnore) {
        this.autoIgnore = autoIgnore;
    }

    @Deprecated
    public void setRequest(HttpServletRequest request) {
        this.request = request;
        this.setFromRequest(request);
    }

    @Deprecated
    public void setResponse(HttpServletResponse response) {
        this.response = response;
        if (response != null) {
            this.setCharacterEncoding(response.getCharacterEncoding());
        }
    }

    @Deprecated
    public LinkTool setAnchor(String anchor) {
        return (LinkTool)this.anchor(anchor);
    }

    @Deprecated
    public LinkTool setRelative(String uri) {
        return (LinkTool)this.relative(uri);
    }

    @Deprecated
    public LinkTool setAbsolute(String uri) {
        return (LinkTool)this.absolute(uri);
    }

    @Deprecated
    public LinkTool setURI(String uri) {
        return (LinkTool)this.uri(uri);
    }

    @Deprecated
    public String getURI() {
        return this.path;
    }

    @Deprecated
    public LinkTool addQueryData(String key, Object value) {
        return (LinkTool)this.append(key, value);
    }

    @Deprecated
    public LinkTool addQueryData(Map parameters) {
        return (LinkTool)this.params(parameters);
    }

    @Deprecated
    public String getQueryData() {
        return this.getQuery();
    }

    @Deprecated
    public String encodeURL(String url) {
        return this.encode(url);
    }

    @Deprecated
    public LinkTool addIgnore(String parameterName) {
        LinkTool copy = (LinkTool)this.duplicate();
        if (copy.parametersToIgnore == null) {
            copy.parametersToIgnore = new HashSet(1);
        }
        copy.parametersToIgnore.add(parameterName);
        return copy;
    }

    @Deprecated
    public LinkTool addAllParameters() {
        if (this.parametersToIgnore != null) {
            String[] ignoreThese = new String[this.parametersToIgnore.size()];
            return (LinkTool)this.addRequestParamsExcept(this.parametersToIgnore.toArray(ignoreThese));
        }
        if (this.autoIgnore) {
            return (LinkTool)this.addMissingRequestParams(new Object[0]);
        }
        return (LinkTool)this.addRequestParams(new Object[0]);
    }

    @Override
    public void setParam(Object key, Object value, boolean append) {
        super.setParam(key, value, append);
        if (this.autoIgnore) {
            if (this.parametersToIgnore == null) {
                this.parametersToIgnore = new HashSet(1);
            }
            this.parametersToIgnore.add(String.valueOf(key));
        }
    }

    @Override
    public void setParams(Object obj, boolean append) {
        Map params;
        super.setParams(obj, append);
        if (this.autoIgnore && obj instanceof Map && !(params = (Map)obj).isEmpty()) {
            if (this.parametersToIgnore == null) {
                this.parametersToIgnore = new HashSet(params.size());
            }
            Iterator iterator = ((Map)obj).entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry e;
                Map.Entry entry = e = iterator.next();
                String key = String.valueOf(entry.getKey());
                this.parametersToIgnore.add(key);
            }
        }
    }

    @Override
    protected LinkTool duplicate(boolean deep) {
        LinkTool that = (LinkTool)super.duplicate(deep);
        if (this.parametersToIgnore != null) {
            that.parametersToIgnore = new HashSet<String>(this.parametersToIgnore);
        }
        return that;
    }
}

