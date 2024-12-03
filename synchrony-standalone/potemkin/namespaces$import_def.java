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

public final class namespaces$import_def
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern("potemkin.namespaces", "import-def");
    public static final Var const__4 = RT.var("clojure.core", "resolve");
    public static final Var const__5 = RT.var("clojure.core", "meta");
    public static final Keyword const__6 = RT.keyword(null, "name");
    public static final Var const__7 = RT.var("clojure.core", "with-meta");
    public static final AFn const__9 = (AFn)((Object)RT.map(RT.keyword(null, "dynamic"), Boolean.TRUE));
    public static final Var const__11 = RT.var("clojure.core", "str");
    public static final AFn const__12 = Symbol.intern(null, "do");
    public static final AFn const__13 = Symbol.intern(null, "def");
    public static final AFn const__14 = Symbol.intern("clojure.core", "deref");
    public static final AFn const__15 = Symbol.intern("clojure.core", "alter-meta!");
    public static final AFn const__16 = Symbol.intern(null, "var");
    public static final AFn const__17 = Symbol.intern("clojure.core", "merge");
    public static final AFn const__18 = Symbol.intern("clojure.core", "dissoc");
    public static final AFn const__19 = Symbol.intern("clojure.core", "meta");
    public static final AFn const__20 = Symbol.intern("potemkin.namespaces", "link-vars");
    public static final AFn const__21 = Symbol.intern(null, "var");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "name"));
    static ILookupThunk __thunk__0__ = __site__0__;
    static final KeywordLookupSite __site__1__ = new KeywordLookupSite(RT.keyword(null, "dynamic"));
    static ILookupThunk __thunk__1__ = __site__1__;
    static final KeywordLookupSite __site__2__ = new KeywordLookupSite(RT.keyword(null, "ns"));
    static ILookupThunk __thunk__2__ = __site__2__;

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object sym, Object name2) {
        Object object;
        Object or__5581__auto__26040;
        Object vr = ((IFn)const__4.getRawRoot()).invoke(sym);
        Object m4 = ((IFn)const__5.getRawRoot()).invoke(vr);
        Object object2 = name2;
        name2 = null;
        Object object3 = or__5581__auto__26040 = object2;
        if (object3 != null && object3 != Boolean.FALSE) {
            object = or__5581__auto__26040;
            or__5581__auto__26040 = null;
        } else {
            ILookupThunk iLookupThunk = __thunk__0__;
            Object object4 = m4;
            object = iLookupThunk.get(object4);
            if (iLookupThunk == object) {
                __thunk__0__ = __site__0__.fault(object4);
                object = __thunk__0__.get(object4);
            }
        }
        Object n = object;
        IFn iFn = (IFn)const__7.getRawRoot();
        Object object5 = n;
        n = null;
        ILookupThunk iLookupThunk = __thunk__1__;
        Object object6 = m4;
        Object object7 = iLookupThunk.get(object6);
        if (iLookupThunk == object7) {
            __thunk__1__ = __site__1__.fault(object6);
            object7 = __thunk__1__.get(object6);
        }
        Object n2 = iFn.invoke(object5, object7 != null && object7 != Boolean.FALSE ? const__9 : PersistentArrayMap.EMPTY);
        ILookupThunk iLookupThunk2 = __thunk__2__;
        Object object8 = m4;
        m4 = null;
        Object object9 = iLookupThunk2.get(object8);
        if (iLookupThunk2 == object9) {
            __thunk__2__ = __site__2__.fault(object8);
            object9 = __thunk__2__.get(object8);
        }
        Object object10 = vr;
        if (object10 == null || object10 == Boolean.FALSE) {
            Object object11 = sym;
            sym = null;
            throw (Throwable)new IllegalArgumentException((String)((IFn)const__11.getRawRoot()).invoke("Don't recognize ", object11));
        }
        Object object12 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__13), ((IFn)const__2.getRawRoot()).invoke(n2), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__14), ((IFn)const__2.getRawRoot()).invoke(vr)))))));
        Object object13 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__15), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__16), ((IFn)const__2.getRawRoot()).invoke(n2)))), ((IFn)const__2.getRawRoot()).invoke(const__17), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__18), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__19), ((IFn)const__2.getRawRoot()).invoke(vr)))), ((IFn)const__2.getRawRoot()).invoke(const__6)))))));
        Object object14 = n2;
        n2 = null;
        Object object15 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__20), ((IFn)const__2.getRawRoot()).invoke(vr), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__21), ((IFn)const__2.getRawRoot()).invoke(object14)))))));
        Object object16 = vr;
        vr = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__12), object12, object13, object15, ((IFn)const__2.getRawRoot()).invoke(object16)));
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
        return namespaces$import_def.invokeStatic(object5, object6, object7, object8);
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
        return namespaces$import_def.invokeStatic(object4, object5, object6);
    }
}

