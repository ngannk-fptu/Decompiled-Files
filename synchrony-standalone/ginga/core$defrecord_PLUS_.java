/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;
import ginga.core$defrecord_PLUS_$fn__8265;
import ginga.core$defrecord_PLUS_$fn__8269;
import ginga.core$defrecord_PLUS_$fn__8274;

public final class core$defrecord_PLUS_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "map");
    public static final Var const__1 = RT.var("clojure.core", "into");
    public static final Var const__2 = RT.var("clojure.core", "apply");
    public static final Var const__3 = RT.var("clojure.core", "merge");
    public static final Var const__4 = RT.var("clojure.core", "filter");
    public static final Var const__5 = RT.var("clojure.core", "map?");
    public static final Var const__6 = RT.var("clojure.core", "symbol");
    public static final Var const__7 = RT.var("clojure.core", "str");
    public static final Var const__8 = RT.var("ginga.string", "camel-case-to-dashes");
    public static final Var const__9 = RT.var("clojure.core", "gensym");
    public static final Var const__10 = RT.var("clojure.core", "seq");
    public static final Var const__11 = RT.var("clojure.core", "concat");
    public static final Var const__12 = RT.var("clojure.core", "list");
    public static final AFn const__13 = Symbol.intern(null, "do");
    public static final AFn const__14 = Symbol.intern("clojure.core", "defrecord");
    public static final Var const__15 = RT.var("clojure.core", "vector");
    public static final AFn const__16 = Symbol.intern("clojure.core", "defn");
    public static final Var const__17 = RT.var("clojure.core", "with-meta");
    public static final Var const__18 = RT.var("clojure.core", "meta");
    public static final Var const__19 = RT.var("clojure.core", "comp");
    public static final Var const__20 = RT.var("clojure.core", "partial");
    public static final Var const__21 = RT.var("clojure.core", "get");
    public static final Var const__22 = RT.var("clojure.core", "keyword");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object name2, Object field_spec, ISeq etc) {
        Object fields = ((IFn)const__0.getRawRoot()).invoke(new core$defrecord_PLUS_$fn__8265(), field_spec);
        Object object = field_spec;
        field_spec = null;
        Object defaults = ((IFn)const__1.getRawRoot()).invoke(PersistentArrayMap.EMPTY, ((IFn)const__0.getRawRoot()).invoke(new core$defrecord_PLUS_$fn__8269(), ((IFn)const__2.getRawRoot()).invoke(const__3.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(const__5.getRawRoot(), object))));
        Object __GT_constructor = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke("->", name2));
        Object constructor = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(name2));
        Object m4 = ((IFn)const__9.getRawRoot()).invoke();
        ISeq iSeq = etc;
        etc = null;
        Object object2 = ((IFn)const__12.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(const__14), ((IFn)const__12.getRawRoot()).invoke(name2), ((IFn)const__12.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__15.getRawRoot(), ((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(fields)))), iSeq)));
        Object object3 = constructor;
        constructor = null;
        Object object4 = name2;
        name2 = null;
        Object object5 = ((IFn)const__12.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__15.getRawRoot(), ((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke()))), ((IFn)const__12.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(__GT_constructor), ((IFn)const__0.getRawRoot()).invoke(((IFn)const__19.getRawRoot()).invoke(((IFn)const__20.getRawRoot()).invoke(const__21.getRawRoot(), defaults), const__22.getRawRoot()), fields)))))));
        Object object6 = ((IFn)const__12.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__15.getRawRoot(), ((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(m4)))));
        Object object7 = __GT_constructor;
        __GT_constructor = null;
        Object object8 = defaults;
        defaults = null;
        Object object9 = m4;
        m4 = null;
        Object object10 = fields;
        fields = null;
        return ((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(const__13), object2, ((IFn)const__12.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(const__16), ((IFn)const__12.getRawRoot()).invoke(((IFn)const__17.getRawRoot()).invoke(object3, ((IFn)const__18.getRawRoot()).invoke(object4))), object5, ((IFn)const__12.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(object6, ((IFn)const__12.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(object7), ((IFn)const__0.getRawRoot()).invoke(new core$defrecord_PLUS_$fn__8274(object8, object9), object10))))))))))));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3, Object object4, Object object5) {
        Object object6 = object;
        object = null;
        Object object7 = object2;
        object2 = null;
        Object object8 = object3;
        object3 = null;
        Object object9 = object4;
        object4 = null;
        ISeq iSeq = (ISeq)object5;
        object5 = null;
        return core$defrecord_PLUS_.invokeStatic(object6, object7, object8, object9, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 4;
    }
}

