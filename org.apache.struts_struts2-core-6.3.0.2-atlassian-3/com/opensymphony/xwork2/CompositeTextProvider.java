/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompositeTextProvider
implements TextProvider {
    private static final Logger LOG = LogManager.getLogger(CompositeTextProvider.class);
    private List<TextProvider> textProviders = new ArrayList<TextProvider>();

    public CompositeTextProvider(List<TextProvider> textProviders) {
        this.textProviders.addAll(textProviders);
    }

    public CompositeTextProvider(TextProvider[] textProviders) {
        this(Arrays.asList(textProviders));
    }

    @Override
    public boolean hasKey(String key) {
        for (TextProvider tp : this.textProviders) {
            if (!tp.hasKey(key)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getText(String key) {
        return this.getText(key, key, Collections.emptyList());
    }

    @Override
    public String getText(String key, String defaultValue) {
        return this.getText(key, defaultValue, Collections.emptyList());
    }

    @Override
    public String getText(String key, String defaultValue, final String obj) {
        return this.getText(key, defaultValue, new ArrayList<Object>(){
            {
                this.add(obj);
            }
        });
    }

    @Override
    public String getText(String key, List<?> args) {
        return this.getText(key, key, args);
    }

    @Override
    public String getText(String key, String[] args) {
        return this.getText(key, key, args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args) {
        for (TextProvider textProvider : this.textProviders) {
            String msg = textProvider.getText(key, defaultValue, args);
            if (msg == null || msg.equals(defaultValue)) continue;
            return msg;
        }
        return defaultValue;
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        for (TextProvider textProvider : this.textProviders) {
            String msg = textProvider.getText(key, defaultValue, args);
            if (msg == null || msg.equals(defaultValue)) continue;
            return msg;
        }
        return defaultValue;
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        for (TextProvider textProvider : this.textProviders) {
            String msg = textProvider.getText(key, defaultValue, args, stack);
            if (msg == null || msg.equals(defaultValue)) continue;
            return msg;
        }
        return defaultValue;
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        for (TextProvider textProvider : this.textProviders) {
            String msg = textProvider.getText(key, defaultValue, args, stack);
            if (msg == null || msg.equals(defaultValue)) continue;
            return msg;
        }
        return defaultValue;
    }

    @Override
    public ResourceBundle getTexts(String bundleName) {
        for (TextProvider textProvider : this.textProviders) {
            ResourceBundle bundle = textProvider.getTexts(bundleName);
            if (bundle == null) continue;
            return bundle;
        }
        return null;
    }

    @Override
    public ResourceBundle getTexts() {
        for (TextProvider textProvider : this.textProviders) {
            ResourceBundle bundle = textProvider.getTexts();
            if (bundle == null) continue;
            return bundle;
        }
        return null;
    }
}

