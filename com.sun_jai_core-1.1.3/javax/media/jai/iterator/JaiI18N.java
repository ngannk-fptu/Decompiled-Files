/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.iterator;

import com.sun.media.jai.util.PropertyUtil;

class JaiI18N {
    static String packageName = "javax.media.jai.iterator";

    JaiI18N() {
    }

    public static String getString(String key) {
        return PropertyUtil.getString(packageName, key);
    }
}

