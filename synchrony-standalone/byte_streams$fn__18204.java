/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import java.io.File;
import java.io.FileInputStream;

public final class byte_streams$fn__18204
extends AFunction {
    public static Object invokeStatic(Object file2, Object ___18005__auto__) {
        Object object = file2;
        file2 = null;
        return new FileInputStream((File)object).getChannel();
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_streams$fn__18204.invokeStatic(object3, object4);
    }
}

