/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.I18NResource;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractI18NResource
implements I18NResource {
    public static final Logger log = LoggerFactory.getLogger(AbstractI18NResource.class);

    @Override
    public ResourceBundle getBundle() {
        return this.getBundle(null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ResourceBundle getBundle(String localeString) {
        try (InputStream is = this.getPropertyResourceAsStream(localeString);){
            if (is == null) return null;
            PropertyResourceBundle propertyResourceBundle = new PropertyResourceBundle(is);
            return propertyResourceBundle;
        }
        catch (IOException e) {
            log.error("Error loading resource for locale " + localeString, (Throwable)e);
        }
        return null;
    }

    protected abstract InputStream getPropertyResourceAsStream(String var1);
}

