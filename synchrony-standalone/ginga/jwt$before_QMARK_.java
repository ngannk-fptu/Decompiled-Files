/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.Numbers;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class jwt$before_QMARK_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "clock-skew-secs");
    public static final Var const__9 = RT.var("ginga.jwt", "default-clock-skew-secs");

    public static Object invokeStatic(Object nbf, Object curr_secs, ISeq p__20378) {
        Object object;
        Object or__5581__auto__20381;
        ISeq map__20379;
        Object object2;
        ISeq iSeq = p__20378;
        p__20378 = null;
        ISeq map__203792 = iSeq;
        Object object3 = ((IFn)const__0.getRawRoot()).invoke(map__203792);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = ((IFn)const__1.getRawRoot()).invoke(map__203792);
            if (object4 != null && object4 != Boolean.FALSE) {
                ISeq iSeq2 = map__203792;
                map__203792 = null;
                object2 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(iSeq2));
            } else {
                Object object5 = ((IFn)const__3.getRawRoot()).invoke(map__203792);
                if (object5 != null && object5 != Boolean.FALSE) {
                    ISeq iSeq3 = map__203792;
                    map__203792 = null;
                    object2 = ((IFn)const__4.getRawRoot()).invoke(iSeq3);
                } else {
                    object2 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object2 = map__203792;
            map__203792 = null;
        }
        ISeq iSeq4 = map__20379 = object2;
        map__20379 = null;
        Object clock_skew_secs = RT.get(iSeq4, const__6);
        Object object6 = nbf;
        nbf = null;
        Object object7 = curr_secs;
        curr_secs = null;
        Object object8 = clock_skew_secs;
        clock_skew_secs = null;
        Object object9 = or__5581__auto__20381 = object8;
        if (object9 != null && object9 != Boolean.FALSE) {
            object = or__5581__auto__20381;
            or__5581__auto__20381 = null;
        } else {
            object = const__9.getRawRoot();
        }
        return Numbers.gte(object6, (Object)Numbers.add(object7, object)) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return jwt$before_QMARK_.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

