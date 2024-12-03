/*
 * Decompiled with CFR 0.152.
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
        if (StringUtils.hasText(text)) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(text);
            Class[] classes = new Class[classNames.length];
            for (int i2 = 0; i2 < classNames.length; ++i2) {
                String className = classNames[i2].trim();
                classes[i2] = ClassUtils.resolveClassName(className, this.classLoader);
            }
            this.setValue(classes);
        } else {
            this.setValue(null);
        }
    }

    @Override
    public String getAsText() {
        Object[] classes = (Class[])this.getValue();
        if (ObjectUtils.isEmpty(classes)) {
            return "";
        }
        StringJoiner sj = new StringJoiner(",");
        for (Object klass : classes) {
            sj.add(ClassUtils.getQualifiedName(klass));
        }
        return sj.toString();
    }
}

