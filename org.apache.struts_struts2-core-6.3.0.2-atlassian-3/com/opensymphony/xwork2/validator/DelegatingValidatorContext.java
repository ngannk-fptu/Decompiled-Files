/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.CompositeTextProvider;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.StrutsTextProviderFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DelegatingValidatorContext
implements ValidatorContext {
    private LocaleProvider localeProvider;
    private TextProvider textProvider;
    private ValidationAware validationAware;

    public DelegatingValidatorContext(ValidationAware validationAware, TextProvider textProvider, LocaleProvider localeProvider) {
        this.textProvider = textProvider;
        this.validationAware = validationAware;
        this.localeProvider = localeProvider;
    }

    public DelegatingValidatorContext(Object object, TextProviderFactory textProviderFactory) {
        this.localeProvider = DelegatingValidatorContext.makeLocaleProvider(object);
        this.validationAware = DelegatingValidatorContext.makeValidationAware(object);
        this.textProvider = this.makeTextProvider(object, textProviderFactory);
    }

    @Deprecated
    public DelegatingValidatorContext(Class clazz) {
        this.localeProvider = new ActionContextLocaleProvider();
        this.textProvider = new StrutsTextProviderFactory().createInstance(clazz);
        this.validationAware = new LoggingValidationAware(clazz);
    }

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
    public String getFullFieldName(String fieldName) {
        return fieldName;
    }

    @Override
    public Locale getLocale() {
        return this.localeProvider.getLocale();
    }

    @Override
    public boolean isValidLocaleString(String localeStr) {
        return this.localeProvider.isValidLocaleString(localeStr);
    }

    @Override
    public boolean isValidLocale(Locale locale) {
        return this.localeProvider.isValidLocale(locale);
    }

    @Override
    public boolean hasKey(String key) {
        return this.textProvider.hasKey(key);
    }

    @Override
    public String getText(String aTextName) {
        return this.textProvider.getText(aTextName);
    }

    @Override
    public String getText(String aTextName, String defaultValue) {
        return this.textProvider.getText(aTextName, defaultValue);
    }

    @Override
    public String getText(String aTextName, String defaultValue, String obj) {
        return this.textProvider.getText(aTextName, defaultValue, obj);
    }

    @Override
    public String getText(String aTextName, List<?> args) {
        return this.textProvider.getText(aTextName, args);
    }

    @Override
    public String getText(String key, String[] args) {
        return this.textProvider.getText(key, args);
    }

    @Override
    public String getText(String aTextName, String defaultValue, List<?> args) {
        return this.textProvider.getText(aTextName, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        return this.textProvider.getText(key, defaultValue, args);
    }

    @Override
    public ResourceBundle getTexts(String aBundleName) {
        return this.textProvider.getTexts(aBundleName);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return this.textProvider.getText(key, defaultValue, args, stack);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return this.textProvider.getText(key, defaultValue, args, stack);
    }

    @Override
    public ResourceBundle getTexts() {
        return this.textProvider.getTexts();
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

    public TextProvider makeTextProvider(Object object, TextProviderFactory textProviderFactory) {
        if (object != null && object instanceof DelegatingValidatorContext) {
            return ((DelegatingValidatorContext)object).getTextProvider();
        }
        if (object != null && object instanceof TextProvider) {
            if (object instanceof CompositeTextProvider) {
                return (CompositeTextProvider)object;
            }
            return new CompositeTextProvider(new TextProvider[]{(TextProvider)object, textProviderFactory.createInstance(object.getClass())});
        }
        return textProviderFactory.createInstance(object != null ? object.getClass() : DelegatingValidatorContext.class);
    }

    protected static LocaleProvider makeLocaleProvider(Object object) {
        if (object instanceof LocaleProvider) {
            return (LocaleProvider)object;
        }
        return new ActionContextLocaleProvider();
    }

    protected static ValidationAware makeValidationAware(Object object) {
        if (object instanceof ValidationAware) {
            return (ValidationAware)object;
        }
        return new LoggingValidationAware(object);
    }

    protected void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    protected TextProvider getTextProvider() {
        return this.textProvider;
    }

    protected void setValidationAware(ValidationAware validationAware) {
        this.validationAware = validationAware;
    }

    protected ValidationAware getValidationAware() {
        return this.validationAware;
    }

    private static class LoggingValidationAware
    implements ValidationAware {
        private Logger log;

        public LoggingValidationAware(Class clazz) {
            this.log = LogManager.getLogger((Class)clazz);
        }

        public LoggingValidationAware(Object obj) {
            this.log = LogManager.getLogger(obj.getClass());
        }

        @Override
        public void setActionErrors(Collection<String> errorMessages) {
            Iterator<String> iterator = errorMessages.iterator();
            while (iterator.hasNext()) {
                String errorMessage;
                String s = errorMessage = iterator.next();
                this.addActionError(s);
            }
        }

        @Override
        public Collection<String> getActionErrors() {
            return null;
        }

        @Override
        public void setActionMessages(Collection<String> messages) {
            Iterator<String> iterator = messages.iterator();
            while (iterator.hasNext()) {
                String message;
                String s = message = iterator.next();
                this.addActionMessage(s);
            }
        }

        @Override
        public Collection<String> getActionMessages() {
            return null;
        }

        @Override
        public void setFieldErrors(Map<String, List<String>> errorMap) {
            for (Map.Entry<String, List<String>> entry : errorMap.entrySet()) {
                this.addFieldError(entry.getKey(), entry.getValue().toString());
            }
        }

        @Override
        public Map<String, List<String>> getFieldErrors() {
            return null;
        }

        @Override
        public void addActionError(String anErrorMessage) {
            this.log.error("Validation error: {}", (Object)anErrorMessage);
        }

        @Override
        public void addActionMessage(String aMessage) {
            this.log.info("Validation Message: {}", (Object)aMessage);
        }

        @Override
        public void addFieldError(String fieldName, String errorMessage) {
            this.log.error("Validation error for {}:{}", (Object)fieldName, (Object)errorMessage);
        }

        @Override
        public boolean hasActionErrors() {
            return false;
        }

        @Override
        public boolean hasActionMessages() {
            return false;
        }

        @Override
        public boolean hasErrors() {
            return false;
        }

        @Override
        public boolean hasFieldErrors() {
            return false;
        }
    }

    private static class ActionContextLocaleProvider
    implements LocaleProvider {
        private LocaleProvider localeProvider;

        private ActionContextLocaleProvider() {
        }

        private LocaleProvider getLocaleProvider() {
            if (this.localeProvider == null) {
                LocaleProviderFactory localeProviderFactory = ActionContext.getContext().getInstance(LocaleProviderFactory.class);
                this.localeProvider = localeProviderFactory.createLocaleProvider();
            }
            return this.localeProvider;
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
    }
}

