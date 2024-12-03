/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.seraph.util.RedirectUtils
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableSet
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.interceptor.ServletRequestAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.json.SingleErrorJSONResult;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.LoginAction;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.seraph.util.RedirectUtils;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotPermittedAction
extends LoginAction
implements Beanable,
ServletRequestAware {
    private static final Set<String> JSON_CONTENT_TYPES2 = ImmutableSet.of((Object)"application/json", (Object)"application/javascript", (Object)"text/javascript");
    @Deprecated
    public static final String[] JSON_CONTENT_TYPES = new String[]{"application/json", "application/javascript", "text/javascript"};
    private static final Logger log = LoggerFactory.getLogger(NotPermittedAction.class);
    private String key;
    private String title;
    private String message;
    private String cssClass;
    private SpaceManager spaceManager;
    private Object jsonResult;
    private HttpServletRequest request;

    @Override
    @PermittedMethods(value={HttpMethod.ANY_METHOD})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        this.setFromNotPermitted(true);
        if (!StringUtils.isNotEmpty((CharSequence)((String)this.request.getAttribute("atlassian.core.seraph.original.url")))) {
            String originalURL = this.request.getServletPath() + (String)(this.request.getQueryString() == null ? "" : "?" + this.request.getQueryString());
            this.request.setAttribute("atlassian.core.seraph.original.url", (Object)originalURL);
            if (log.isDebugEnabled()) {
                log.debug("No atlassian.core.seraph.original.url was found in the request. Storing " + originalURL);
            }
        }
        if (this.getAuthenticatedUser() == null) {
            if (this.isJsonRequest(this.request)) {
                ServletActionContext.getResponse().setStatus(401);
                this.jsonResult = new SingleErrorJSONResult("json.not.logged.in", new Object[]{RedirectUtils.getLinkLoginURL((HttpServletRequest)this.request)});
                return "json";
            }
            return "login";
        }
        if (this.isJsonRequest(this.request)) {
            ServletActionContext.getResponse().setStatus(403);
            this.jsonResult = new SingleErrorJSONResult("not.permitted.description");
            return "json";
        }
        this.title = this.getText("title.not.permitted");
        this.message = this.getText("not.permitted.description");
        this.cssClass = "not-permitted-background-image";
        return "success";
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null) {
            for (String contentType : JSON_CONTENT_TYPES2) {
                if (!acceptHeader.contains(contentType)) continue;
                return true;
            }
        }
        return request.getServletPath().startsWith("/json");
    }

    public String getLoginUrl() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String loginURL = SeraphUtils.getLoginURL(request);
        String contextPath = StringUtils.defaultString((String)request.getContextPath());
        if (log.isDebugEnabled()) {
            log.debug("Seraph login.url is " + loginURL);
        }
        if (StringUtils.isNotEmpty((CharSequence)contextPath) && StringUtils.defaultString((String)loginURL).startsWith(contextPath)) {
            loginURL = loginURL.substring(contextPath.length());
        }
        return loginURL;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Space getSpace() {
        return this.spaceManager.getSpace(this.getKey());
    }

    @Override
    public String getUsername() {
        if (this.getSpace() != null && this.getSpace().getCreator() != null) {
            ConfluenceUser user = this.getSpace().getCreator();
            return user.getFullName();
        }
        return null;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    public Object getBean() {
        return this.jsonResult;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCssClass() {
        return this.cssClass;
    }

    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
    }
}

