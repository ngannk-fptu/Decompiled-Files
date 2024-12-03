/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import com.sun.media.jai.util.PropertyUtil;

class JaiI18N {
    static String packageName = "javax.media.jai.registry";

    JaiI18N() {
    }

    public static String getString(String key) {
        return PropertyUtil.getString(packageName, key);
    }
}

