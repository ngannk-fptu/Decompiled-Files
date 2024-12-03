/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.ValidationAwareSupport;
import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActionSupport
implements Action,
Validateable,
ValidationAware,
TextProvider,
LocaleProvider,
Serializable {
    private static final Logger LOG = LogManager.getLogger(ActionSupport.class);
    private final ValidationAwareSupport validationAware = new ValidationAwareSupport();
    private transient TextProvider textProvider;
    private transient LocaleProvider localeProvider;
    protected Container container;

    @Override
    public void setActionErrors(Collection<String> errorMessages) {
        this.validationAware.setActionErrors(errorMessages);
    }

    @Override
    public Collection<String> getActionErrors() {
        return this.validationAware.getActionErrors();
    }

    @Override
    public void setActionMessages(Collection<String> messages) {
        this.validationAware.setActionMessages(messages);
    }

    @Override
    public Collection<String> getActionMessages() {
        return this.validationAware.getActionMessages();
    }

    @Override
    public void setFieldErrors(Map<String, List<String>> errorMap) {
        this.validationAware.setFieldErrors(errorMap);
    }

    @Override
    public Map<String, List<String>> getFieldErrors() {
        return this.validationAware.getFieldErrors();
    }

    @Override
    public Locale getLocale() {
        return this.getLocaleProvider().getLocale();
    }

    @Override
    public boolean isValidLocaleString(String localeStr) {
        return this.getLocaleProvider().isValidLocaleString(localeStr);
    }

    @Override
    public boolean isValidLocale(Locale locale) {
        return this.getLocaleProvider().isValidLocale(locale);
    }

    @Override
    public boolean hasKey(String key) {
        return this.getTextProvider().hasKey(key);
    }

    @Override
    public String getText(String aTextName) {
        return this.getTextProvider().getText(aTextName);
    }

    @Override
    public String getText(String aTextName, String defaultValue) {
        return this.getTextProvider().getText(aTextName, defaultValue);
    }

    @Override
    public String getText(String aTextName, String defaultValue, String obj) {
        return this.getTextProvider().getText(aTextName, defaultValue, obj);
    }

    @Override
    public String getText(String aTextName, List<?> args) {
        return this.getTextProvider().getText(aTextName, args);
    }

    @Override
    public String getText(String key, String[] args) {
        return this.getTextProvider().getText(key, args);
    }

    @Override
    public String getText(String aTextName, String defaultValue, List<?> args) {
        return this.getTextProvider().getText(aTextName, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        return this.getTextProvider().getText(key, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return this.getTextProvider().getText(key, defaultValue, args, stack);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return this.getTextProvider().getText(key, defaultValue, args, stack);
    }

    public String getFormatted(String key, String expr) {
        Map<String, ConversionData> conversionErrors = ActionContext.getContext().getConversionErrors();
        if (conversionErrors.containsKey(expr)) {
            String[] vals = (String[])conversionErrors.get(expr).getValue();
            return vals[0];
        }
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        Object val = valueStack.findValue(expr);
        return this.getText(key, Arrays.asList(val));
    }

    @Override
    public ResourceBundle getTexts() {
        return this.getTextProvider().getTexts();
    }

    @Override
    public ResourceBundle getTexts(String aBundleName) {
        return this.getTextProvider().getTexts(aBundleName);
    }

    @Override
    public void addActionError(String anErrorMessage) {
        this.validationAware.addActionError(anErrorMessage);
    }

    @Override
    public void addActionMessage(String aMessage) {
        this.validationAware.addActionMessage(aMessage);
    }

    @Override
    public void addFieldError(String fieldName, String errorMessage) {
        this.validationAware.addFieldError(fieldName, errorMessage);
    }

    public String input() throws Exception {
        return "input";
    }

    @Override
    public String execute() throws Exception {
        return "success";
    }

    @Override
    public boolean hasActionErrors() {
        return this.validationAware.hasActionErrors();
    }

    @Override
    public boolean hasActionMessages() {
        return this.validationAware.hasActionMessages();
    }

    @Override
    public boolean hasErrors() {
        return this.validationAware.hasErrors();
    }

    @Override
    public boolean hasFieldErrors() {
        return this.validationAware.hasFieldErrors();
    }

    public void clearFieldErrors() {
        this.validationAware.clearFieldErrors();
    }

    public void clearActionErrors() {
        this.validationAware.clearActionErrors();
    }

    public void clearMessages() {
        this.validationAware.clearMessages();
    }

    public void clearErrors() {
        this.validationAware.clearErrors();
    }

    public void clearErrorsAndMessages() {
        this.validationAware.clearErrorsAndMessages();
    }

    @Override
    public void validate() {
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void pause(String result) {
    }

    protected TextProvider getTextProvider() {
        if (this.textProvider == null) {
            TextProviderFactory tpf = this.getContainer().getInstance(TextProviderFactory.class);
            this.textProvider = tpf.createInstance(this.getClass());
        }
        return this.textProvider;
    }

    protected LocaleProvider getLocaleProvider() {
        if (this.localeProvider == null) {
            LocaleProviderFactory localeProviderFactory = this.getContainer().getInstance(LocaleProviderFactory.class);
            this.localeProvider = localeProviderFactory.createLocaleProvider();
        }
        return this.localeProvider;
    }

    protected Container getContainer() {
        if (this.container == null) {
            this.container = ActionContext.getContext().getContainer();
            if (this.container != null) {
                boolean devMode = Boolean.parseBoolean(this.container.getInstance(String.class, "struts.devMode"));
                if (devMode) {
                    LOG.warn("Container is null, action was created manually? Fallback to ActionContext");
                } else {
                    LOG.debug("Container is null, action was created manually? Fallback to ActionContext");
                }
            } else {
                LOG.warn("Container is null, action was created out of ActionContext scope?!?");
            }
        }
        return this.container;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }
}

