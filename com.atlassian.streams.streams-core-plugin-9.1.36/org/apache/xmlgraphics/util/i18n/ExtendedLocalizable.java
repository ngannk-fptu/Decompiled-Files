/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.i18n;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.xmlgraphics.util.i18n.LocaleGroup;
import org.apache.xmlgraphics.util.i18n.Localizable;

public interface ExtendedLocalizable
extends Localizable {
    public void setLocaleGroup(LocaleGroup var1);

    public LocaleGroup getLocaleGroup();

    public void setDefaultLocale(Locale var1);

    public Locale getDefaultLocale();

    public ResourceBundle getResourceBundle();
}

