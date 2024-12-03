/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.IObj;
import clojure.lang.ISeq;
import clojure.lang.PersistentHashSet;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Util;
import clojure.lang.Var;
import java.util.Arrays;

public final class core$try_catchall
extends RestFn {
    public static final AFn const__1 = Symbol.intern(null, "catch");
    public static final Var const__2 = RT.var("clojure.core", "first");
    public static final Var const__3 = RT.var("clojure.core", "last");
    public static final Var const__4 = RT.var("clojure.core", "cons");
    public static final Var const__5 = RT.var("clojure.core", "butlast");
    public static final Var const__6 = RT.var("clojure.core", "take-last");
    public static final Object const__7 = 1L;
    public static final Object const__8 = 2L;
    public static final Var const__11 = RT.var("clojure.core", "seq");
    public static final Var const__12 = RT.var("clojure.core", "next");
    public static final AFn const__13 = Symbol.intern(null, "catch");
    public static final Var const__14 = RT.var("clojure.core", "str");
    public static final Var const__15 = RT.var("clojure.core", "pr-str");
    public static final Object const__16 = ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "="), Symbol.intern(null, "catch"), PersistentList.create(Arrays.asList(Symbol.intern(null, "quote"), Symbol.intern(null, "catch"))))))).withMeta(RT.map(RT.keyword(null, "line"), 297, RT.keyword(null, "column"), 13));
    public static final Var const__17 = RT.var("clojure.core", "symbol?");
    public static final Object const__18 = ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "symbol?"), Symbol.intern(null, "bind"))))).withMeta(RT.map(RT.keyword(null, "line"), 298, RT.keyword(null, "column"), 13));
    public static final Var const__19 = RT.var("clojure.core", "contains?");
    public static final AFn const__21 = PersistentHashSet.create(null, Symbol.intern(null, "finally"));
    public static final Object const__22 = ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "contains?"), PersistentHashSet.create(null, PersistentList.create(Arrays.asList(Symbol.intern(null, "quote"), Symbol.intern(null, "finally")))), Symbol.intern(null, "finally"))))).withMeta(RT.map(RT.keyword(null, "line"), 299, RT.keyword(null, "column"), 13));
    public static final Var const__23 = RT.var("clojure.core", "not");
    public static final Var const__24 = RT.var("clojure.core", "concat");
    public static final Var const__25 = RT.var("clojure.core", "list");
    public static final AFn const__26 = Symbol.intern("ginga.core", "if-cljs");
    public static final AFn const__27 = Symbol.intern(null, "try");
    public static final AFn const__28 = Symbol.intern(null, "catch");
    public static final AFn const__29 = Symbol.intern("js", "Error");
    public static final AFn const__30 = Symbol.intern(null, "try");
    public static final AFn const__31 = Symbol.intern(null, "catch");
    public static final AFn const__32 = Symbol.intern(null, "java.lang.Throwable");
    public static final AFn const__33 = Symbol.intern("ginga.core", "if-cljs");
    public static final AFn const__34 = Symbol.intern(null, "try");
    public static final AFn const__35 = Symbol.intern(null, "catch");
    public static final AFn const__36 = Symbol.intern("js", "Error");
    public static final AFn const__37 = Symbol.intern(null, "finally");
    public static final AFn const__38 = Symbol.intern(null, "try");
    public static final AFn const__39 = Symbol.intern(null, "catch");
    public static final AFn const__40 = Symbol.intern(null, "java.lang.Throwable");
    public static final AFn const__41 = Symbol.intern(null, "finally");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, ISeq body) {
        Object object;
        Object vec__8301;
        Object vec__8298;
        Object object2;
        if (Util.equiv((Object)const__1, ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(body)))) {
            Object object3 = ((IFn)const__5.getRawRoot()).invoke(body);
            ISeq iSeq = body;
            body = null;
            object2 = ((IFn)const__4.getRawRoot()).invoke(object3, ((IFn)const__6.getRawRoot()).invoke(const__7, iSeq));
        } else {
            Object object4 = ((IFn)const__5.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(body));
            ISeq iSeq = body;
            body = null;
            object2 = ((IFn)const__4.getRawRoot()).invoke(object4, ((IFn)const__6.getRawRoot()).invoke(const__8, iSeq));
        }
        Object vec__8295 = object2;
        Object try_body = RT.nth(vec__8295, RT.intCast(0L), null);
        Object object5 = vec__8298 = RT.nth(vec__8295, RT.intCast(1L), null);
        vec__8298 = null;
        Object seq__8299 = ((IFn)const__11.getRawRoot()).invoke(object5);
        Object first__8300 = ((IFn)const__2.getRawRoot()).invoke(seq__8299);
        Object object6 = seq__8299;
        seq__8299 = null;
        Object seq__82992 = ((IFn)const__12.getRawRoot()).invoke(object6);
        Object object7 = first__8300;
        first__8300 = null;
        Object object8 = object7;
        Object first__83002 = ((IFn)const__2.getRawRoot()).invoke(seq__82992);
        Object object9 = seq__82992;
        seq__82992 = null;
        Object seq__82993 = ((IFn)const__12.getRawRoot()).invoke(object9);
        Object object10 = first__83002;
        first__83002 = null;
        Object bind = object10;
        Object object11 = seq__82993;
        seq__82993 = null;
        Object catch_body = object11;
        Object object12 = vec__8295;
        vec__8295 = null;
        Object object13 = vec__8301 = RT.nth(object12, RT.intCast(2L), null);
        vec__8301 = null;
        Object seq__8302 = ((IFn)const__11.getRawRoot()).invoke(object13);
        Object first__8303 = ((IFn)const__2.getRawRoot()).invoke(seq__8302);
        Object object14 = seq__8302;
        seq__8302 = null;
        Object seq__83022 = ((IFn)const__12.getRawRoot()).invoke(object14);
        Object object15 = first__8303;
        first__8303 = null;
        Object object16 = object15;
        Object object17 = seq__83022;
        seq__83022 = null;
        Object fin = object17;
        Object object18 = object8;
        object8 = null;
        if (!Util.equiv(object18, (Object)const__13)) {
            throw (Throwable)((Object)new AssertionError(((IFn)const__14.getRawRoot()).invoke("Assert failed: ", ((IFn)const__15.getRawRoot()).invoke(const__16))));
        }
        Object object19 = ((IFn)const__17.getRawRoot()).invoke(bind);
        if (object19 == null || object19 == Boolean.FALSE) {
            throw (Throwable)((Object)new AssertionError(((IFn)const__14.getRawRoot()).invoke("Assert failed: ", ((IFn)const__15.getRawRoot()).invoke(const__18))));
        }
        Object object20 = ((IFn)const__19.getRawRoot()).invoke(const__21, object16);
        if (object20 == null || object20 == Boolean.FALSE) {
            throw (Throwable)((Object)new AssertionError(((IFn)const__14.getRawRoot()).invoke("Assert failed: ", ((IFn)const__15.getRawRoot()).invoke(const__22))));
        }
        Object object21 = object16;
        object16 = null;
        Object object22 = ((IFn)const__23.getRawRoot()).invoke(object21);
        if (object22 != null && object22 != Boolean.FALSE) {
            Object object23 = ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__27), try_body, ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__28), ((IFn)const__25.getRawRoot()).invoke(const__29), ((IFn)const__25.getRawRoot()).invoke(bind), catch_body))))));
            Object object24 = try_body;
            try_body = null;
            Object object25 = bind;
            bind = null;
            Object object26 = catch_body;
            catch_body = null;
            object = ((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__26), object23, ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__30), object24, ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__31), ((IFn)const__25.getRawRoot()).invoke(const__32), ((IFn)const__25.getRawRoot()).invoke(object25), object26))))))));
        } else {
            Object object27 = ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__34), try_body, ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__35), ((IFn)const__25.getRawRoot()).invoke(const__36), ((IFn)const__25.getRawRoot()).invoke(bind), catch_body))), ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__37), fin))))));
            Object object28 = try_body;
            try_body = null;
            Object object29 = bind;
            bind = null;
            Object object30 = catch_body;
            catch_body = null;
            Object object31 = fin;
            fin = null;
            object = ((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__33), object27, ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__38), object28, ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__39), ((IFn)const__25.getRawRoot()).invoke(const__40), ((IFn)const__25.getRawRoot()).invoke(object29), object30))), ((IFn)const__25.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(((IFn)const__25.getRawRoot()).invoke(const__41), object31))))))));
        }
        return object;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return core$try_catchall.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

