/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.method.support;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.SimpleSessionStatus;

public class ModelAndViewContainer {
    private boolean ignoreDefaultModelOnRedirect = false;
    @Nullable
    private Object view;
    private final ModelMap defaultModel = new BindingAwareModelMap();
    @Nullable
    private ModelMap redirectModel;
    private boolean redirectModelScenario = false;
    @Nullable
    private HttpStatus status;
    private final Set<String> noBinding = new HashSet<String>(4);
    private final Set<String> bindingDisabled = new HashSet<String>(4);
    private final SessionStatus sessionStatus = new SimpleSessionStatus();
    private boolean requestHandled = false;

    public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
        this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
    }

    public void setViewName(@Nullable String viewName) {
        this.view = viewName;
    }

    @Nullable
    public String getViewName() {
        return this.view instanceof String ? (String)this.view : null;
    }

    public void setView(@Nullable Object view) {
        this.view = view;
    }

    @Nullable
    public Object getView() {
        return this.view;
    }

    public boolean isViewReference() {
        return this.view instanceof String;
    }

    public ModelMap getModel() {
        if (this.useDefaultModel()) {
            return this.defaultModel;
        }
        if (this.redirectModel == null) {
            this.redirectModel = new ModelMap();
        }
        return this.redirectModel;
    }

    private boolean useDefaultModel() {
        return !this.redirectModelScenario || this.redirectModel == null && !this.ignoreDefaultModelOnRedirect;
    }

    public ModelMap getDefaultModel() {
        return this.defaultModel;
    }

    public void setRedirectModel(ModelMap redirectModel) {
        this.redirectModel = redirectModel;
    }

    public void setRedirectModelScenario(boolean redirectModelScenario) {
        this.redirectModelScenario = redirectModelScenario;
    }

    public void setStatus(@Nullable HttpStatus status) {
        this.status = status;
    }

    @Nullable
    public HttpStatus getStatus() {
        return this.status;
    }

    public void setBindingDisabled(String attributeName) {
        this.bindingDisabled.add(attributeName);
    }

    public boolean isBindingDisabled(String name) {
        return this.bindingDisabled.contains(name) || this.noBinding.contains(name);
    }

    public void setBinding(String attributeName, boolean enabled) {
        if (!enabled) {
            this.noBinding.add(attributeName);
        } else {
            this.noBinding.remove(attributeName);
        }
    }

    public SessionStatus getSessionStatus() {
        return this.sessionStatus;
    }

    public void setRequestHandled(boolean requestHandled) {
        this.requestHandled = requestHandled;
    }

    public boolean isRequestHandled() {
        return this.requestHandled;
    }

    public ModelAndViewContainer addAttribute(String name, @Nullable Object value) {
        this.getModel().addAttribute(name, value);
        return this;
    }

    public ModelAndViewContainer addAttribute(Object value) {
        this.getModel().addAttribute(value);
        return this;
    }

    public ModelAndViewContainer addAllAttributes(@Nullable Map<String, ?> attributes) {
        this.getModel().addAllAttributes(attributes);
        return this;
    }

    public ModelAndViewContainer mergeAttributes(@Nullable Map<String, ?> attributes) {
        this.getModel().mergeAttributes(attributes);
        return this;
    }

    public ModelAndViewContainer removeAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                this.getModel().remove(key);
            }
        }
        return this;
    }

    public boolean containsAttribute(String name) {
        return this.getModel().containsAttribute(name);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ModelAndViewContainer: ");
        if (!this.isRequestHandled()) {
            if (this.isViewReference()) {
                sb.append("reference to view with name '").append(this.view).append("'");
            } else {
                sb.append("View is [").append(this.view).append(']');
            }
            if (this.useDefaultModel()) {
                sb.append("; default model ");
            } else {
                sb.append("; redirect model ");
            }
            sb.append(this.getModel());
        } else {
            sb.append("Request handled directly");
        }
        return sb.toString();
    }
}

