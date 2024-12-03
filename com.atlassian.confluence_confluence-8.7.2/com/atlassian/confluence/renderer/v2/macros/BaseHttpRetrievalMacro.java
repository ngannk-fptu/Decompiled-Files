/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.renderer.v2.macros;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.confluence.util.http.HttpRetrievalService;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public abstract class BaseHttpRetrievalMacro
extends BaseMacro {
    private HttpRetrievalService httpRetrievalService;
    private UserAccessor userAccessor;

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        String url = this.cleanupUrl(RenderUtils.getParameter((Map)parameters, (String)"url", (int)0));
        if (!StringUtils.isNotEmpty((CharSequence)url)) {
            return RenderUtils.blockError((String)"Could not retrieve RSS feed: no URL", (String)"");
        }
        HttpResponse response = null;
        try {
            response = this.httpRetrievalService.get(url);
            if (response.isNotFound()) {
                String string = this.notFound(url);
                return string;
            }
            if (response.isNotPermitted()) {
                String string = this.notPermitted(url);
                return string;
            }
            if (response.isFailed()) {
                String string = this.failed(url, response);
                return string;
            }
            String string = this.successfulResponse(parameters, renderContext, url, response);
            return string;
        }
        catch (IOException e) {
            throw new MacroException("Unable to retrieve " + url + ": " + e.getMessage(), (Throwable)e);
        }
        finally {
            if (response != null) {
                response.finish();
            }
        }
    }

    public abstract String successfulResponse(Map var1, RenderContext var2, String var3, HttpResponse var4) throws MacroException;

    public String notFound(String url) {
        return RenderUtils.blockError((String)("Could not retrieve " + url + " - Page Not Found"), (String)"");
    }

    public String notPermitted(String url) {
        return RenderUtils.blockError((String)("Could not retrieve " + url + " - Not Permitted"), (String)"");
    }

    public String failed(String url, HttpResponse response) {
        return RenderUtils.blockError((String)("Could not retrieve " + url + " - Request failed"), (String)response.getStatusMessage());
    }

    protected String cleanupUrl(String url) {
        if (url.indexOf(40) > 0) {
            url = url.replaceAll("\\(", "%28");
        }
        if (url.indexOf(41) > 0) {
            url = url.replaceAll("\\)", "%29");
        }
        if (url.indexOf("&amp;") > 0) {
            url = url.replaceAll("&amp;", "&");
        }
        return url;
    }

    public void setHttpRetrievalService(HttpRetrievalService httpRetrievalService) {
        this.httpRetrievalService = httpRetrievalService;
    }

    protected ConfluenceUser getRemoteUser() {
        String principal = ServletActionContext.getRequest().getRemoteUser();
        if (this.userAccessor == null) {
            this.userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
        }
        return this.userAccessor.getUserByName(principal);
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }
}

