/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format$parse$iter__19179__19183;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.format.DateTimeFormatter;

public final class format$parse
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "first");
    public static final Var const__1 = RT.var("clojure.core", "vals");
    public static final Var const__2 = RT.var("clj-time.format", "formatters");

    public static Object invokeStatic(Object fmt, Object s2) {
        Object object = fmt;
        fmt = null;
        Object object2 = s2;
        s2 = null;
        return ((DateTimeFormatter)object).parseDateTime((String)object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return format$parse.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object s2) {
        format$parse$iter__19179__19183 iter__6373__auto__19197;
        Object object = s2;
        s2 = null;
        format$parse$iter__19179__19183 format$parse$iter__19179__19183 = iter__6373__auto__19197 = new format$parse$iter__19179__19183(object);
        iter__6373__auto__19197 = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)format$parse$iter__19179__19183).invoke(((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot())));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return format$parse.invokeStatic(object2);
    }
}

