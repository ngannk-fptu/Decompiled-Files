/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.Keyword;
import clojure.lang.KeywordLookupSite;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class namespaces$import_macro
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern("potemkin.namespaces", "import-macro");
    public static final Var const__4 = RT.var("clojure.core", "resolve");
    public static final Var const__5 = RT.var("clojure.core", "meta");
    public static final Var const__6 = RT.var("clojure.core", "with-meta");
    public static final Keyword const__7 = RT.keyword(null, "name");
    public static final Var const__9 = RT.var("clojure.core", "str");
    public static final AFn const__11 = Symbol.intern(null, "do");
    public static final AFn const__12 = Symbol.intern(null, "def");
    public static final AFn const__13 = Symbol.intern("clojure.core", "alter-meta!");
    public static final AFn const__14 = Symbol.intern(null, "var");
    public static final AFn const__15 = Symbol.intern("clojure.core", "merge");
    public static final AFn const__16 = Symbol.intern("clojure.core", "dissoc");
    public static final AFn const__17 = Symbol.intern("clojure.core", "meta");
    public static final AFn const__18 = Symbol.intern(null, ".setMacro");
    public static final AFn const__19 = Symbol.intern(null, "var");
    public static final AFn const__20 = Symbol.intern("potemkin.namespaces", "link-vars");
    public static final AFn const__21 = Symbol.intern(null, "var");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "name"));
    static ILookupThunk __thunk__0__ = __site__0__;
    static final KeywordLookupSite __site__1__ = new KeywordLookupSite(RT.keyword(null, "arglists"));
    static ILookupThunk __thunk__1__ = __site__1__;
    static final KeywordLookupSite __site__2__ = new KeywordLookupSite(RT.keyword(null, "macro"));
    static ILookupThunk __thunk__2__ = __site__2__;

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object sym, Object name2) {
        Object object;
        Object or__5581__auto__26038;
        Object vr = ((IFn)const__4.getRawRoot()).invoke(sym);
        Object m4 = ((IFn)const__5.getRawRoot()).invoke(vr);
        Object object2 = name2;
        name2 = null;
        Object object3 = or__5581__auto__26038 = object2;
        if (object3 != null && object3 != Boolean.FALSE) {
            object = or__5581__auto__26038;
            or__5581__auto__26038 = null;
        } else {
            IFn iFn = (IFn)const__6.getRawRoot();
            ILookupThunk iLookupThunk = __thunk__0__;
            Object object4 = m4;
            Object object5 = iLookupThunk.get(object4);
            if (iLookupThunk == object5) {
                __thunk__0__ = __site__0__.fault(object4);
                object5 = __thunk__0__.get(object4);
            }
            object = iFn.invoke(object5, PersistentArrayMap.EMPTY);
        }
        Object n = object;
        ILookupThunk iLookupThunk = __thunk__1__;
        Object object6 = m4;
        Object object7 = iLookupThunk.get(object6);
        if (iLookupThunk == object7) {
            __thunk__1__ = __site__1__.fault(object6);
            object7 = __thunk__1__.get(object6);
        }
        Object object8 = vr;
        if (object8 == null || object8 == Boolean.FALSE) {
            throw (Throwable)new IllegalArgumentException((String)((IFn)const__9.getRawRoot()).invoke("Don't recognize ", sym));
        }
        ILookupThunk iLookupThunk2 = __thunk__2__;
        Object object9 = m4;
        m4 = null;
        Object object10 = iLookupThunk2.get(object9);
        if (iLookupThunk2 == object10) {
            __thunk__2__ = __site__2__.fault(object9);
            object10 = __thunk__2__.get(object9);
        }
        if (object10 == null || object10 == Boolean.FALSE) {
            throw (Throwable)new IllegalArgumentException((String)((IFn)const__9.getRawRoot()).invoke("Calling import-macro on a non-macro: ", sym));
        }
        Object object11 = sym;
        sym = null;
        Object object12 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__12), ((IFn)const__2.getRawRoot()).invoke(n), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(object11)))));
        Object object13 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__13), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__14), ((IFn)const__2.getRawRoot()).invoke(n)))), ((IFn)const__2.getRawRoot()).invoke(const__15), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__16), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__17), ((IFn)const__2.getRawRoot()).invoke(vr)))), ((IFn)const__2.getRawRoot()).invoke(const__7)))))));
        Object object14 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__18), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__19), ((IFn)const__2.getRawRoot()).invoke(n)))))));
        Object object15 = n;
        n = null;
        Object object16 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__20), ((IFn)const__2.getRawRoot()).invoke(vr), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__21), ((IFn)const__2.getRawRoot()).invoke(object15)))))));
        Object object17 = vr;
        vr = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__11), object12, object13, object14, object16, ((IFn)const__2.getRawRoot()).invoke(object17)));
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return namespaces$import_macro.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object sym) {
        Object object = sym;
        sym = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3), ((IFn)const__2.getRawRoot()).invoke(object), ((IFn)const__2.getRawRoot()).invoke(null)));
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return namespaces$import_macro.invokeStatic(object4, object5, object6);
    }
}

