/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.axis.i18n.ProjectResourceBundle;

public class MessageBundle {
    private boolean loaded = false;
    private ProjectResourceBundle _resourceBundle = null;
    private final String projectName;
    private final String packageName;
    private final String resourceName;
    private final Locale locale;
    private final ClassLoader classLoader;
    private final ResourceBundle parent;

    public final ProjectResourceBundle getResourceBundle() {
        if (!this.loaded) {
            this._resourceBundle = ProjectResourceBundle.getBundle(this.projectName, this.packageName, this.resourceName, this.locale, this.classLoader, this.parent);
            this.loaded = true;
        }
        return this._resourceBundle;
    }

    public MessageBundle(String projectName, String packageName, String resourceName, Locale locale, ClassLoader classLoader, ResourceBundle parent) throws MissingResourceException {
        this.projectName = projectName;
        this.packageName = packageName;
        this.resourceName = resourceName;
        this.locale = locale;
        this.classLoader = classLoader;
        this.parent = parent;
    }

    public String getMessage(String key) throws MissingResourceException {
        return this.getMessage(key, (String[])null);
    }

    public String getMessage(String key, String arg0) throws MissingResourceException {
        return this.getMessage(key, new String[]{arg0});
    }

    public String getMessage(String key, String arg0, String arg1) throws MissingResourceException {
        return this.getMessage(key, new String[]{arg0, arg1});
    }

    public String getMessage(String key, String arg0, String arg1, String arg2) throws MissingResourceException {
        return this.getMessage(key, new String[]{arg0, arg1, arg2});
    }

    public String getMessage(String key, String arg0, String arg1, String arg2, String arg3) throws MissingResourceException {
        return this.getMessage(key, new String[]{arg0, arg1, arg2, arg3});
    }

    public String getMessage(String key, String arg0, String arg1, String arg2, String arg3, String arg4) throws MissingResourceException {
        return this.getMessage(key, new String[]{arg0, arg1, arg2, arg3, arg4});
    }

    public String getMessage(String key, String[] array) throws MissingResourceException {
        String msg = null;
        if (this.getResourceBundle() != null) {
            msg = this.getResourceBundle().getString(key);
        }
        if (msg == null) {
            throw new MissingResourceException("Cannot find resource key \"" + key + "\" in base name " + this.getResourceBundle().getResourceName(), this.getResourceBundle().getResourceName(), key);
        }
        return MessageFormat.format(msg, array);
    }
}

