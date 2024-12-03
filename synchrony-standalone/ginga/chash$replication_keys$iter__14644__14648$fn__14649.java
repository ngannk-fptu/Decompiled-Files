/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.chash$replication_keys$iter__14644__14648$fn__14649$fn__14650;

public final class chash$replication_keys$iter__14644__14648$fn__14649
extends AFunction {
    Object key;
    Object s__14645;
    Object iter__14644;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__2 = RT.var("clojure.core", "chunk-first");
    public static final Var const__5 = RT.var("clojure.core", "chunk-buffer");
    public static final Var const__6 = RT.var("clojure.core", "chunk-cons");
    public static final Var const__7 = RT.var("clojure.core", "chunk");
    public static final Var const__8 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__9 = RT.var("clojure.core", "first");
    public static final Var const__10 = RT.var("clojure.core", "cons");
    public static final Var const__11 = RT.var("ginga.chash", "b64-hash");
    public static final Var const__12 = RT.var("clojure.core", "str");
    public static final Object const__13 = Character.valueOf('|');
    public static final Var const__14 = RT.var("clojure.core", "rest");

    public chash$replication_keys$iter__14644__14648$fn__14649(Object object, Object object2, Object object3) {
        this.key = object;
        this.s__14645 = object2;
        this.iter__14644 = object3;
    }

    @Override
    public Object invoke() {
        Object object;
        Object temp__5804__auto__14655;
        Object s__14645;
        Object object2 = s__14645 = (this_.s__14645 = null);
        s__14645 = null;
        Object object3 = temp__5804__auto__14655 = ((IFn)const__0.getRawRoot()).invoke(object2);
        if (object3 != null && object3 != Boolean.FALSE) {
            chash$replication_keys$iter__14644__14648$fn__14649 this_;
            Object object4 = temp__5804__auto__14655;
            temp__5804__auto__14655 = null;
            Object s__146452 = object4;
            Object object5 = ((IFn)const__1.getRawRoot()).invoke(s__146452);
            if (object5 != null && object5 != Boolean.FALSE) {
                Object c__6371__auto__14653 = ((IFn)const__2.getRawRoot()).invoke(s__146452);
                int size__6372__auto__14654 = RT.intCast(RT.count(c__6371__auto__14653));
                Object b__14647 = ((IFn)const__5.getRawRoot()).invoke(size__6372__auto__14654);
                Object object6 = c__6371__auto__14653;
                c__6371__auto__14653 = null;
                Object object7 = ((IFn)new chash$replication_keys$iter__14644__14648$fn__14649$fn__14650(size__6372__auto__14654, this_.key, object6, b__14647)).invoke();
                if (object7 != null && object7 != Boolean.FALSE) {
                    Object object8 = b__14647;
                    b__14647 = null;
                    Object object9 = s__146452;
                    s__146452 = null;
                    this_ = null;
                    object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object8), ((IFn)this_.iter__14644).invoke(((IFn)const__8.getRawRoot()).invoke(object9)));
                } else {
                    Object object10 = b__14647;
                    b__14647 = null;
                    this_ = null;
                    object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object10), null);
                }
            } else {
                Object i;
                Object object11 = i = ((IFn)const__9.getRawRoot()).invoke(s__146452);
                i = null;
                Object object12 = s__146452;
                s__146452 = null;
                this_ = null;
                object = ((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(object11, const__13, this_.key)), ((IFn)this_.iter__14644).invoke(((IFn)const__14.getRawRoot()).invoke(object12)));
            }
        } else {
            object = null;
        }
        return object;
    }
}

