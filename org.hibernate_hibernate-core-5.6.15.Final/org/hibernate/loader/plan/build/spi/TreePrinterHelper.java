/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.internal.util.StringHelper;

public class TreePrinterHelper {
    public static final int INDENTATION = 3;
    public static final TreePrinterHelper INSTANCE = new TreePrinterHelper();

    private TreePrinterHelper() {
    }

    public String generateNodePrefix(int nIndentations) {
        return StringHelper.repeat(' ', nIndentations * 3) + " - ";
    }
}

