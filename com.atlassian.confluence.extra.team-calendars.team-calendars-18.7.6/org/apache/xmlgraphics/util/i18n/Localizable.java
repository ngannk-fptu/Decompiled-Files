/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.i18n;

import java.util.Locale;
import java.util.MissingResourceException;

public interface Localizable {
    public void setLocale(Locale var1);

    public Locale getLocale();

    public String formatMessage(String var1, Object[] var2) throws MissingResourceException;
}

