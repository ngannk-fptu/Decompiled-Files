/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import org.codehaus.groovy.runtime.MetaClassHelper;

public abstract class MetaProperty {
    protected final String name;
    protected Class type;
    public static final String PROPERTY_SET_PREFIX = "set";

    public MetaProperty(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public abstract Object getProperty(Object var1);

    public abstract void setProperty(Object var1, Object var2);

    public String getName() {
        return this.name;
    }

    public Class getType() {
        return this.type;
    }

    public int getModifiers() {
        return 1;
    }

    public static String getGetterName(String propertyName, Class type) {
        String prefix = type == Boolean.TYPE || type == Boolean.class ? "is" : "get";
        return prefix + MetaClassHelper.capitalize(propertyName);
    }

    public static String getSetterName(String propertyName) {
        return PROPERTY_SET_PREFIX + MetaClassHelper.capitalize(propertyName);
    }
}

