/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.common.util;

import java.io.File;

public final class ImportsUtil {
    private ImportsUtil() {
    }

    public static File getImportsLocation(String sharedHomeLocation) {
        return ImportsUtil.getImportsLocation(new File(sharedHomeLocation));
    }

    public static File getImportsLocation(File sharedHomeLocation) {
        return new File(sharedHomeLocation, "import");
    }
}

