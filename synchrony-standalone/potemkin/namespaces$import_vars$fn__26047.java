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
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Util;
import clojure.lang.Var;

public final class namespaces$import_vars$fn__26047
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "resolve");
    public static final Var const__1 = RT.var("clojure.core", "meta");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "concat");
    public static final Var const__5 = RT.var("clojure.core", "list");
    public static final AFn const__6 = Symbol.intern(null, "throw");
    public static final AFn const__7 = Symbol.intern("clojure.core", "ex-info");
    public static final AFn const__8 = Symbol.intern("clojure.core", "format");
    public static final AFn const__9 = Symbol.intern(null, "quote");
    public static final Var const__10 = RT.var("clojure.core", "apply");
    public static final Var const__11 = RT.var("clojure.core", "hash-map");
    public static final AFn const__13 = Symbol.intern("potemkin.namespaces", "import-macro");
    public static final AFn const__15 = Symbol.intern("potemkin.namespaces", "import-fn");
    public static final Keyword const__16 = RT.keyword(null, "else");
    public static final AFn const__17 = Symbol.intern("potemkin.namespaces", "import-def");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "macro"));
    static ILookupThunk __thunk__0__ = __site__0__;
    static final KeywordLookupSite __site__1__ = new KeywordLookupSite(RT.keyword(null, "arglists"));
    static ILookupThunk __thunk__1__ = __site__1__;

    @Override
    public Object invoke(Object sym) {
        Object object;
        namespaces$import_vars$fn__26047 this_;
        Object vr = ((IFn)const__0.getRawRoot()).invoke(sym);
        Object m4 = ((IFn)const__1.getRawRoot()).invoke(vr);
        Object object2 = vr;
        vr = null;
        if (Util.identical(object2, null)) {
            Object object3 = sym;
            sym = null;
            this_ = null;
            object = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__6), ((IFn)const__5.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__7), ((IFn)const__5.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__8), ((IFn)const__5.getRawRoot()).invoke("`%s` does not exist"), ((IFn)const__5.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__9), ((IFn)const__5.getRawRoot()).invoke(object3))))))), ((IFn)const__5.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(const__11.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke()))))))));
        } else {
            ILookupThunk iLookupThunk = __thunk__0__;
            Object object4 = m4;
            Object object5 = iLookupThunk.get(object4);
            if (iLookupThunk == object5) {
                __thunk__0__ = __site__0__.fault(object4);
                object5 = __thunk__0__.get(object4);
            }
            if (object5 != null && object5 != Boolean.FALSE) {
                Object object6 = sym;
                sym = null;
                this_ = null;
                object = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__13), ((IFn)const__5.getRawRoot()).invoke(object6)));
            } else {
                ILookupThunk iLookupThunk2 = __thunk__1__;
                Object object7 = m4;
                m4 = null;
                Object object8 = iLookupThunk2.get(object7);
                if (iLookupThunk2 == object8) {
                    __thunk__1__ = __site__1__.fault(object7);
                    object8 = __thunk__1__.get(object7);
                }
                if (object8 != null && object8 != Boolean.FALSE) {
                    Object object9 = sym;
                    sym = null;
                    this_ = null;
                    object = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__15), ((IFn)const__5.getRawRoot()).invoke(object9)));
                } else {
                    Keyword keyword2 = const__16;
                    if (keyword2 != null && keyword2 != Boolean.FALSE) {
                        Object object10 = sym;
                        sym = null;
                        this_ = null;
                        object = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(const__17), ((IFn)const__5.getRawRoot()).invoke(object10)));
                    } else {
                        object = null;
                    }
                }
            }
        }
        return object;
    }
}

