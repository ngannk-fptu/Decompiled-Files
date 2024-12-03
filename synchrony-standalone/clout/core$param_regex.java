/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class core$param_regex
extends RestFn {
    public static final Var const__2 = RT.var("clout.core", "trim-pattern");
    public static final Var const__3 = RT.var("clojure.core", "str");

    public static Object invokeStatic(Object regexs, Object key2, ISeq p__34730) {
        Object pattern;
        Object object;
        Object or__5581__auto__34736;
        Object pattern2;
        ISeq vec__34731;
        ISeq iSeq = p__34730;
        p__34730 = null;
        ISeq iSeq2 = vec__34731 = iSeq;
        vec__34731 = null;
        Object object2 = pattern2 = RT.nth(iSeq2, RT.intCast(0L), null);
        pattern2 = null;
        Object object3 = or__5581__auto__34736 = ((IFn)const__2.getRawRoot()).invoke(object2);
        if (object3 != null && object3 != Boolean.FALSE) {
            object = or__5581__auto__34736;
            or__5581__auto__34736 = null;
        } else {
            Object or__5581__auto__34735;
            Object object4 = regexs;
            regexs = null;
            Object object5 = key2;
            key2 = null;
            Object object6 = or__5581__auto__34735 = ((IFn)object4).invoke(object5);
            if (object6 != null && object6 != Boolean.FALSE) {
                object = or__5581__auto__34735;
                or__5581__auto__34735 = null;
            } else {
                object = "[^/,;?]+";
            }
        }
        Object object7 = pattern = object;
        pattern = null;
        return ((IFn)const__3.getRawRoot()).invoke("(", object7, ")");
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return core$param_regex.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

