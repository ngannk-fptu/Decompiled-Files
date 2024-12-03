/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$pad$fn__8469
extends AFunction {
    Object value;
    Object s;
    Object cnt;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__2 = RT.var("clojure.core", "cons");
    public static final Var const__3 = RT.var("clojure.core", "first");
    public static final Var const__4 = RT.var("ginga.core", "pad");
    public static final Var const__6 = RT.var("clojure.core", "next");
    public static final Keyword const__7 = RT.keyword(null, "else");
    public static final Var const__8 = RT.var("clojure.core", "repeat");

    public core$pad$fn__8469(Object object, Object object2, Object object3) {
        this.value = object;
        this.s = object2;
        this.cnt = object3;
    }

    @Override
    public Object invoke() {
        Object object;
        this_.s = null;
        Object s2 = ((IFn)const__0.getRawRoot()).invoke(this_.s);
        if (Numbers.isZero(this_.cnt)) {
            object = s2;
            s2 = null;
        } else {
            core$pad$fn__8469 this_;
            Object object2 = s2;
            if (object2 != null && object2 != Boolean.FALSE) {
                Object object3 = ((IFn)const__3.getRawRoot()).invoke(s2);
                this_.value = null;
                this_.cnt = null;
                Object object4 = s2;
                s2 = null;
                this_ = null;
                object = ((IFn)const__2.getRawRoot()).invoke(object3, ((IFn)const__4.getRawRoot()).invoke(this_.value, Numbers.dec(this_.cnt), ((IFn)const__6.getRawRoot()).invoke(object4)));
            } else {
                Keyword keyword2 = const__7;
                if (keyword2 != null && keyword2 != Boolean.FALSE) {
                    this_.cnt = null;
                    this_.value = null;
                    this_ = null;
                    object = ((IFn)const__8.getRawRoot()).invoke(this_.cnt, this_.value);
                } else {
                    object = null;
                }
            }
        }
        return object;
    }
}

