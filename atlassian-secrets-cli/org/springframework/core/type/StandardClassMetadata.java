/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import org.springframework.core.type.ClassMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class StandardClassMetadata
implements ClassMetadata {
    private final Class<?> introspectedClass;

    public StandardClassMetadata(Class<?> introspectedClass) {
        Assert.notNull(introspectedClass, "Class must not be null");
        this.introspectedClass = introspectedClass;
    }

    public final Class<?> getIntrospectedClass() {
        return this.introspectedClass;
    }

    @Override
    public String getClassName() {
        return this.introspectedClass.getName();
    }

    @Override
    public boolean isInterface() {
        return this.introspectedClass.isInterface();
    }

    @Override
    public boolean isAnnotation() {
        return this.introspectedClass.isAnnotation();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedClass.getModifiers());
    }

    @Override
    public boolean isConcrete() {
        return !this.isInterface() && !this.isAbstract();
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedClass.getModifiers());
    }

    @Override
    public boolean isIndependent() {
        return !this.hasEnclosingClass() || this.introspectedClass.getDeclaringClass() != null && Modifier.isStatic(this.introspectedClass.getModifiers());
    }

    @Override
    public boolean hasEnclosingClass() {
        return this.introspectedClass.getEnclosingClass() != null;
    }

    @Override
    @Nullable
    public String getEnclosingClassName() {
        Class<?> enclosingClass = this.introspectedClass.getEnclosingClass();
        return enclosingClass != null ? enclosingClass.getName() : null;
    }

    @Override
    public boolean hasSuperClass() {
        return this.introspectedClass.getSuperclass() != null;
    }

    @Override
    @Nullable
    public String getSuperClassName() {
        Class<?> superClass = this.introspectedClass.getSuperclass();
        return superClass != null ? superClass.getName() : null;
    }

    @Override
    public String[] getInterfaceNames() {
        Class<?>[] ifcs = this.introspectedClass.getInterfaces();
        String[] ifcNames = new String[ifcs.length];
        for (int i = 0; i < ifcs.length; ++i) {
            ifcNames[i] = ifcs[i].getName();
        }
        return ifcNames;
    }

    @Override
    public String[] getMemberClassNames() {
        LinkedHashSet<String> memberClassNames = new LinkedHashSet<String>(4);
        for (Class<?> nestedClass : this.introspectedClass.getDeclaredClasses()) {
            memberClassNames.add(nestedClass.getName());
        }
        return StringUtils.toStringArray(memberClassNames);
    }
}

