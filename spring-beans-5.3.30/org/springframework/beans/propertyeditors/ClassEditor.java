/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class ClassEditor
extends PropertyEditorSupport {
    @Nullable
    private final ClassLoader classLoader;

    public ClassEditor() {
        this(null);
    }

    public ClassEditor(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText((String)text)) {
            this.setValue(ClassUtils.resolveClassName((String)text.trim(), (ClassLoader)this.classLoader));
        } else {
            this.setValue(null);
        }
    }

    @Override
    public String getAsText() {
        Class clazz = (Class)this.getValue();
        if (clazz != null) {
            return ClassUtils.getQualifiedName((Class)clazz);
        }
        return "";
    }
}

