/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.typeconversion;

public interface ConverterManager {
    public boolean canConvert(Class<?> var1, String var2, Class<?> var3);

    public <T> T convert(Object var1, String var2, Class<T> var3);
}

