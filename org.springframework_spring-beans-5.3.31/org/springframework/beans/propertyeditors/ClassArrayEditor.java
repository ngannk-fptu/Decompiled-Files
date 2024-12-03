/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.StringJoiner;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class ClassArrayEditor
extends PropertyEditorSupport {
    @Nullable
    private final ClassLoader classLoader;

    public ClassArrayEditor() {
        this(null);
    }

    public ClassArrayEditor(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText((String)text)) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray((String)text);
            Class[] classes = new Class[classNames.length];
            for (int i = 0; i < classNames.length; ++i) {
                String className = classNames[i].trim();
                classes[i] = ClassUtils.resolveClassName((String)className, (ClassLoader)this.classLoader);
            }
            this.setValue(classes);
        } else {
            this.setValue(null);
        }
    }

    @Override
    public String getAsText() {
        Object[] classes = (Class[])this.getValue();
        if (ObjectUtils.isEmpty((Object[])classes)) {
            return "";
        }
        StringJoiner sj = new StringJoiner(",");
        for (Object klass : classes) {
            sj.add(ClassUtils.getQualifiedName((Class)klass));
        }
        return sj.toString();
    }
}

