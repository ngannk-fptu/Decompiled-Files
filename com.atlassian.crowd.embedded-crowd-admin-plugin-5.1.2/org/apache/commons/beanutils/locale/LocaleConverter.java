/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.locale;

import org.apache.commons.beanutils.Converter;

public interface LocaleConverter
extends Converter {
    public <T> T convert(Class<T> var1, Object var2, String var3);
}

