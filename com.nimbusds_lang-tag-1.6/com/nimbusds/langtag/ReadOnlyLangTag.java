/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.langtag;

public interface ReadOnlyLangTag {
    public String getLanguage();

    public String getPrimaryLanguage();

    public String[] getExtendedLanguageSubtags();

    public String getScript();

    public String getRegion();

    public String[] getVariants();

    public String[] getExtensions();

    public String getPrivateUse();

    public String toString();
}

