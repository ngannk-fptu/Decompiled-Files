/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.GroovyObjectSupport;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageSource
extends GroovyObjectSupport {
    private final String[] bundleNames;
    private ResourceBundle[] cachedBundles;

    public MessageSource(String[] names) {
        assert (names != null);
        assert (names.length != 0);
        this.bundleNames = names;
    }

    public MessageSource(String name) {
        this(new String[]{name});
    }

    private static String[] classNames(Class[] types) {
        assert (types != null);
        assert (types.length != 0);
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; ++i) {
            assert (types[i] != null);
            names[i] = types[i].getName();
        }
        return names;
    }

    public MessageSource(Class[] types) {
        this(MessageSource.classNames(types));
    }

    public MessageSource(Class type) {
        this(new String[]{type.getName()});
    }

    private ResourceBundle[] createBundles() {
        ResourceBundle[] bundles = new ResourceBundle[this.bundleNames.length];
        for (int i = 0; i < this.bundleNames.length; ++i) {
            assert (this.bundleNames[i] != null);
            bundles[i] = ResourceBundle.getBundle(this.bundleNames[i]);
        }
        return bundles;
    }

    private ResourceBundle[] getBundles() {
        if (this.cachedBundles == null) {
            this.cachedBundles = this.createBundles();
        }
        return this.cachedBundles;
    }

    public String getMessage(String code) {
        assert (code != null);
        MissingResourceException error = null;
        ResourceBundle[] bundles = this.getBundles();
        for (int i = 0; i < bundles.length; ++i) {
            try {
                return bundles[i].getString(code);
            }
            catch (MissingResourceException e) {
                if (error == null) continue;
                error = e;
                continue;
            }
        }
        assert (error != null);
        throw error;
    }

    public String format(String code, Object[] args) {
        assert (args != null);
        String pattern = this.getMessage(code);
        return MessageFormat.format(pattern, args);
    }

    @Override
    public Object getProperty(String name) {
        return this.getMessage(name);
    }
}

