/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection;

import java.util.List;
import org.hibernate.annotations.common.reflection.Filter;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XMethod;
import org.hibernate.annotations.common.reflection.XProperty;

public interface XClass
extends XAnnotatedElement {
    public static final String ACCESS_PROPERTY = "property";
    public static final String ACCESS_FIELD = "field";
    public static final Filter DEFAULT_FILTER = new Filter(){

        @Override
        public boolean returnStatic() {
            return false;
        }

        @Override
        public boolean returnTransient() {
            return false;
        }
    };

    public String getName();

    public XClass getSuperclass();

    public XClass[] getInterfaces();

    public boolean isInterface();

    public boolean isAbstract();

    public boolean isPrimitive();

    public boolean isEnum();

    public boolean isAssignableFrom(XClass var1);

    public List<XProperty> getDeclaredProperties(String var1);

    public List<XProperty> getDeclaredProperties(String var1, Filter var2);

    public List<XMethod> getDeclaredMethods();
}

