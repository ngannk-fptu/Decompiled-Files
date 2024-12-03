/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.faces.context.ExternalContext
 *  javax.faces.context.FacesContext
 */
package org.springframework.web.context.request;

import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.FacesRequestAttributes;
import org.springframework.web.context.request.NativeWebRequest;

public class FacesWebRequest
extends FacesRequestAttributes
implements NativeWebRequest {
    public FacesWebRequest(FacesContext facesContext) {
        super(facesContext);
    }

    @Override
    public Object getNativeRequest() {
        return this.getExternalContext().getRequest();
    }

    @Override
    public Object getNativeResponse() {
        return this.getExternalContext().getResponse();
    }

    @Override
    public <T> T getNativeRequest(@Nullable Class<T> requiredType) {
        Object request;
        if (requiredType != null && requiredType.isInstance(request = this.getExternalContext().getRequest())) {
            return (T)request;
        }
        return null;
    }

    @Override
    public <T> T getNativeResponse(@Nullable Class<T> requiredType) {
        Object response;
        if (requiredType != null && requiredType.isInstance(response = this.getExternalContext().getResponse())) {
            return (T)response;
        }
        return null;
    }

    @Override
    @Nullable
    public String getHeader(String headerName) {
        return (String)this.getExternalContext().getRequestHeaderMap().get(headerName);
    }

    @Override
    @Nullable
    public String[] getHeaderValues(String headerName) {
        return (String[])this.getExternalContext().getRequestHeaderValuesMap().get(headerName);
    }

    @Override
    public Iterator<String> getHeaderNames() {
        return this.getExternalContext().getRequestHeaderMap().keySet().iterator();
    }

    @Override
    @Nullable
    public String getParameter(String paramName) {
        return (String)this.getExternalContext().getRequestParameterMap().get(paramName);
    }

    @Override
    public Iterator<String> getParameterNames() {
        return this.getExternalContext().getRequestParameterNames();
    }

    @Override
    @Nullable
    public String[] getParameterValues(String paramName) {
        return (String[])this.getExternalContext().getRequestParameterValuesMap().get(paramName);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.getExternalContext().getRequestParameterValuesMap();
    }

    @Override
    public Locale getLocale() {
        return this.getFacesContext().getExternalContext().getRequestLocale();
    }

    @Override
    public String getContextPath() {
        return this.getFacesContext().getExternalContext().getRequestContextPath();
    }

    @Override
    @Nullable
    public String getRemoteUser() {
        return this.getFacesContext().getExternalContext().getRemoteUser();
    }

    @Override
    @Nullable
    public Principal getUserPrincipal() {
        return this.getFacesContext().getExternalContext().getUserPrincipal();
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.getFacesContext().getExternalContext().isUserInRole(role);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public boolean checkNotModified(long lastModifiedTimestamp) {
        return false;
    }

    @Override
    public boolean checkNotModified(@Nullable String eTag) {
        return false;
    }

    @Override
    public boolean checkNotModified(@Nullable String etag, long lastModifiedTimestamp) {
        return false;
    }

    @Override
    public String getDescription(boolean includeClientInfo) {
        ExternalContext externalContext = this.getExternalContext();
        StringBuilder sb = new StringBuilder();
        sb.append("context=").append(externalContext.getRequestContextPath());
        if (includeClientInfo) {
            String user;
            Object session = externalContext.getSession(false);
            if (session != null) {
                sb.append(";session=").append(this.getSessionId());
            }
            if (StringUtils.hasLength(user = externalContext.getRemoteUser())) {
                sb.append(";user=").append(user);
            }
        }
        return sb.toString();
    }

    public String toString() {
        return "FacesWebRequest: " + this.getDescription(true);
    }
}

