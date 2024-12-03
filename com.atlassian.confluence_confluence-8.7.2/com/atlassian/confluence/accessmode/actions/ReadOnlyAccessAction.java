/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableSet
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.interceptor.ServletRequestAware
 */
package com.atlassian.confluence.accessmode.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

public class ReadOnlyAccessAction
extends ConfluenceActionSupport
implements ServletRequestAware {
    private static final Set<String> JSON_CONTENT_TYPES = ImmutableSet.of((Object)"application/json", (Object)"application/javascript", (Object)"text/javascript");
    private String key;
    private String title;
    private String message;
    private String cssClass;
    private SpaceManager spaceManager;
    private HttpServletRequest request;

    @PermittedMethods(value={HttpMethod.ANY_METHOD})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        if (this.isJsonRequest(this.request)) {
            return "json";
        }
        this.title = this.getText("read.only.mode.default.error.title");
        this.message = this.getText("read.only.mode.default.error.description");
        this.cssClass = "read-only-access-background-image";
        return "success";
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null) {
            for (String contentType : JSON_CONTENT_TYPES) {
                if (!acceptHeader.contains(contentType)) continue;
                return true;
            }
        }
        return request.getServletPath().startsWith("/json");
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

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
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

