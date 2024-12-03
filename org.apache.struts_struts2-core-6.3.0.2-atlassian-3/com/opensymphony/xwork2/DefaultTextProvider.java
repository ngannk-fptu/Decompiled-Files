/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.Unchainable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class DefaultTextProvider
implements TextProvider,
Serializable,
Unchainable {
    private static final Object[] EMPTY_ARGS = new Object[0];
    protected LocalizedTextProvider localizedTextProvider;

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    @Override
    public boolean hasKey(String key) {
        return this.getText(key) != null;
    }

    @Override
    public String getText(String key) {
        return this.localizedTextProvider.findDefaultText(key, ActionContext.getContext().getLocale());
    }

    @Override
    public String getText(String key, String defaultValue) {
        String text = this.getText(key);
        if (text == null) {
            return defaultValue;
        }
        return text;
    }

    @Override
    public String getText(String key, List<?> args) {
        Object[] params = args != null ? args.toArray() : EMPTY_ARGS;
        return this.localizedTextProvider.findDefaultText(key, ActionContext.getContext().getLocale(), params);
    }

    @Override
    public String getText(String key, String[] args) {
        Object[] params = args != null ? args : EMPTY_ARGS;
        return this.localizedTextProvider.findDefaultText(key, ActionContext.getContext().getLocale(), params);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args) {
        String text = this.getText(key, args);
        if (text == null && defaultValue == null) {
            defaultValue = key;
        }
        if (text == null && defaultValue != null) {
            MessageFormat format = new MessageFormat(defaultValue);
            format.setLocale(ActionContext.getContext().getLocale());
            format.applyPattern(defaultValue);
            Object[] params = args != null ? args.toArray() : EMPTY_ARGS;
            return format.format(params);
        }
        return text;
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        String text = this.getText(key, args);
        if (text == null) {
            MessageFormat format = new MessageFormat(defaultValue);
            format.setLocale(ActionContext.getContext().getLocale());
            format.applyPattern(defaultValue);
            if (args == null) {
                return format.format(EMPTY_ARGS);
            }
            return format.format(args);
        }
        return text;
    }

    @Override
    public String getText(String key, String defaultValue, String obj) {
        ArrayList<String> args = new ArrayList<String>(1);
        args.add(obj);
        return this.getText(key, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return this.getText(key, defaultValue, args);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        ArrayList<String> values = new ArrayList<String>(Arrays.asList(args));
        return this.getText(key, defaultValue, values);
    }

    @Override
    public ResourceBundle getTexts(String bundleName) {
        return this.localizedTextProvider.findResourceBundle(bundleName, ActionContext.getContext().getLocale());
    }

    @Override
    public ResourceBundle getTexts() {
        return null;
    }
}

