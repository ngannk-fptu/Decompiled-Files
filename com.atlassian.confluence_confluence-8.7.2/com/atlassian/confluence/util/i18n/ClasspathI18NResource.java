/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.ResourceBundleI18NResource;
import com.atlassian.confluence.util.i18n.UTF8Control;
import java.util.ResourceBundle;

public class ClasspathI18NResource
extends ResourceBundleI18NResource {
    private final String baseName;

    public ClasspathI18NResource(String baseName) {
        this.baseName = baseName;
    }

    @Override
    protected String getLocation() {
        return this.baseName;
    }

    @Override
    protected ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    @Override
    protected ResourceBundle.Control getControl() {
        return new UTF8Control();
    }
}

