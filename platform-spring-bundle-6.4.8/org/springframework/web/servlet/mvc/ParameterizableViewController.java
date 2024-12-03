/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.support.RequestContextUtils;

public class ParameterizableViewController
extends AbstractController {
    @Nullable
    private Object view;
    @Nullable
    private HttpStatus statusCode;
    private boolean statusOnly;

    public ParameterizableViewController() {
        super(false);
        this.setSupportedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name());
    }

    public void setViewName(@Nullable String viewName) {
        this.view = viewName;
    }

    @Nullable
    public String getViewName() {
        if (this.view instanceof String) {
            String viewName = (String)this.view;
            if (this.getStatusCode() != null && this.getStatusCode().is3xxRedirection()) {
                return viewName.startsWith("redirect:") ? viewName : "redirect:" + viewName;
            }
            return viewName;
        }
        return null;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Nullable
    public View getView() {
        return this.view instanceof View ? (View)this.view : null;
    }

    public void setStatusCode(@Nullable HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    @Nullable
    public HttpStatus getStatusCode() {
        return this.statusCode;
    }

    public void setStatusOnly(boolean statusOnly) {
        this.statusOnly = statusOnly;
    }

    public boolean isStatusOnly() {
        return this.statusOnly;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String viewName = this.getViewName();
        if (this.getStatusCode() != null) {
            if (this.getStatusCode().is3xxRedirection()) {
                request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, (Object)this.getStatusCode());
            } else {
                response.setStatus(this.getStatusCode().value());
                if (this.getStatusCode().equals((Object)HttpStatus.NO_CONTENT) && viewName == null) {
                    return null;
                }
            }
        }
        if (this.isStatusOnly()) {
            return null;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addAllObjects(RequestContextUtils.getInputFlashMap(request));
        if (viewName != null) {
            modelAndView.setViewName(viewName);
        } else {
            modelAndView.setView(this.getView());
        }
        return modelAndView;
    }

    public String toString() {
        return "ParameterizableViewController [" + this.formatStatusAndView() + "]";
    }

    private String formatStatusAndView() {
        StringBuilder sb = new StringBuilder();
        if (this.statusCode != null) {
            sb.append("status=").append((Object)this.statusCode);
        }
        if (this.view != null) {
            sb.append(sb.length() != 0 ? ", " : "");
            String viewName = this.getViewName();
            sb.append("view=").append(viewName != null ? "\"" + viewName + "\"" : this.view);
        }
        return sb.toString();
    }
}

