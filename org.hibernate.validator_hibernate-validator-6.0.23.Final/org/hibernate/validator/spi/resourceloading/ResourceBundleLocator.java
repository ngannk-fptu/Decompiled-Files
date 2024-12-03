/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.spi.resourceloading;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleLocator {
    public ResourceBundle getResourceBundle(Locale var1);
}

