/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

public class MissingEntryException
extends RuntimeException {
    protected final String resource;
    protected final String key;
    protected final ClassLoader loader;
    protected final Locale locale;
    private String debugMsg;

    public MissingEntryException(String message, String resource, String key, Locale locale, ClassLoader loader) {
        super(message);
        this.resource = resource;
        this.key = key;
        this.locale = locale;
        this.loader = loader;
    }

    public MissingEntryException(String message, Throwable cause, String resource, String key, Locale locale, ClassLoader loader) {
        super(message, cause);
        this.resource = resource;
        this.key = key;
        this.locale = locale;
        this.loader = loader;
    }

    public String getKey() {
        return this.key;
    }

    public String getResource() {
        return this.resource;
    }

    public ClassLoader getClassLoader() {
        return this.loader;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getDebugMsg() {
        if (this.debugMsg == null) {
            this.debugMsg = "Can not find entry " + this.key + " in resource file " + this.resource + " for the locale " + this.locale + ".";
            if (this.loader instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader)this.loader).getURLs();
                this.debugMsg = this.debugMsg + " The following entries in the classpath were searched: ";
                for (int i = 0; i != urls.length; ++i) {
                    this.debugMsg = this.debugMsg + urls[i] + " ";
                }
            }
        }
        return this.debugMsg;
    }
}

