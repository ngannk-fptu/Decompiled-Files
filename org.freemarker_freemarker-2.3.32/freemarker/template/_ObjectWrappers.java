/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.Configuration;
import freemarker.template.SimpleObjectWrapper;

public final class _ObjectWrappers {
    public static final SimpleObjectWrapper SAFE_OBJECT_WRAPPER = new SimpleObjectWrapper(Configuration.VERSION_2_3_0);

    static {
        SAFE_OBJECT_WRAPPER.writeProtect();
    }
}

