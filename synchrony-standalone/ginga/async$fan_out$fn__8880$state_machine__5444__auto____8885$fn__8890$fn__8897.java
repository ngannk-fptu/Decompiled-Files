/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class async$fan_out$fn__8880$state_machine__5444__auto____8885$fn__8890$fn__8897
extends AFunction {
    Object out_chs;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__3 = RT.var("clojure.core.async", "close!");
    public static final Var const__5 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__6 = RT.var("clojure.core", "chunk-first");
    public static final Var const__7 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__10 = RT.var("clojure.core", "first");
    public static final Var const__11 = RT.var("clojure.core", "next");

    public async$fan_out$fn__8880$state_machine__5444__auto____8885$fn__8890$fn__8897(Object object) {
        this.out_chs = object;
    }

    @Override
    public Object invoke() {
        Object seq_8806 = ((IFn)const__0.getRawRoot()).invoke(this.out_chs);
        Object chunk_8807 = null;
        long count_8808 = 0L;
        long i_8809 = 0L;
        while (true) {
            Object out_ch;
            Object temp__5804__auto__8900;
            if (i_8809 < count_8808) {
                Object out_ch2;
                Object object = out_ch2 = ((Indexed)chunk_8807).nth(RT.intCast(i_8809));
                out_ch2 = null;
                ((IFn)const__3.getRawRoot()).invoke(object);
                Object object2 = seq_8806;
                seq_8806 = null;
                Object object3 = chunk_8807;
                chunk_8807 = null;
                ++i_8809;
                chunk_8807 = object3;
                seq_8806 = object2;
                continue;
            }
            Object object = seq_8806;
            seq_8806 = null;
            Object object4 = temp__5804__auto__8900 = ((IFn)const__0.getRawRoot()).invoke(object);
            if (object4 == null || object4 == Boolean.FALSE) break;
            Object object5 = temp__5804__auto__8900;
            temp__5804__auto__8900 = null;
            Object seq_88062 = object5;
            Object object6 = ((IFn)const__5.getRawRoot()).invoke(seq_88062);
            if (object6 != null && object6 != Boolean.FALSE) {
                Object c__6065__auto__8899 = ((IFn)const__6.getRawRoot()).invoke(seq_88062);
                Object object7 = seq_88062;
                seq_88062 = null;
                Object object8 = c__6065__auto__8899;
                Object object9 = c__6065__auto__8899;
                c__6065__auto__8899 = null;
                i_8809 = RT.intCast(0L);
                count_8808 = RT.intCast(RT.count(object9));
                chunk_8807 = object8;
                seq_8806 = ((IFn)const__7.getRawRoot()).invoke(object7);
                continue;
            }
            Object object10 = out_ch = ((IFn)const__10.getRawRoot()).invoke(seq_88062);
            out_ch = null;
            ((IFn)const__3.getRawRoot()).invoke(object10);
            Object object11 = seq_88062;
            seq_88062 = null;
            i_8809 = 0L;
            count_8808 = 0L;
            chunk_8807 = null;
            seq_8806 = ((IFn)const__11.getRawRoot()).invoke(object11);
        }
        return null;
    }
}

