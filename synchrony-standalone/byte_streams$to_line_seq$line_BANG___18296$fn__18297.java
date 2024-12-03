/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class byte_streams$to_line_seq$line_BANG___18296$fn__18297
extends AFunction {
    Object reader;
    Object line_BANG_;
    public static final Var const__0 = RT.var("clojure.core", "cons");

    public byte_streams$to_line_seq$line_BANG___18296$fn__18297(Object object, Object object2) {
        this.reader = object;
        this.line_BANG_ = object2;
    }

    @Override
    public Object invoke() {
        Object object;
        Object temp__5804__auto__18301;
        this_.reader = null;
        Object object2 = temp__5804__auto__18301 = ((IFn)new byte_streams$to_line_seq$line_BANG___18296$fn__18297$fn__18298(this_.reader)).invoke();
        if (object2 != null && object2 != Boolean.FALSE) {
            Object l;
            Object object3 = temp__5804__auto__18301;
            temp__5804__auto__18301 = null;
            Object object4 = l = object3;
            l = null;
            byte_streams$to_line_seq$line_BANG___18296$fn__18297 this_ = null;
            object = ((IFn)const__0.getRawRoot()).invoke(object4, ((IFn)this_.line_BANG_).invoke());
        } else {
            object = null;
        }
        return object;
    }
}

