/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.i18n;

import java.io.Serializable;
import java.util.Collection;

public interface TranslationMode
extends Serializable {
    public Collection<String> getParams();

    public String getMarkedUpText(String var1, String var2, String var3);
}

