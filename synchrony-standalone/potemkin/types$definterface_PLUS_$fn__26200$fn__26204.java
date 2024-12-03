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
import clojure.lang.Var;

public final class types$definterface_PLUS_$fn__26200$fn__26204
extends AFunction {
    Object class_name;
    Object fn_name;
    public static final Var const__0 = RT.var("clojure.core", "map");
    public static final Var const__1 = RT.var("potemkin.types", "untag");
    public static final Var const__2 = RT.var("clojure.core", "seq");
    public static final Var const__3 = RT.var("clojure.core", "concat");
    public static final Var const__4 = RT.var("clojure.core", "list");
    public static final Var const__5 = RT.var("clojure.core", "vec");
    public static final AFn const__6 = Symbol.intern("clojure.core", "with-meta");
    public static final AFn const__7 = Symbol.intern("clojure.core", "list");
    public static final AFn const__8 = Symbol.intern(null, "quote");
    public static final Var const__9 = RT.var("clojure.core", "symbol");
    public static final Var const__10 = RT.var("clojure.core", "str");
    public static final Var const__11 = RT.var("potemkin.types", "munge-fn-name");
    public static final AFn const__12 = Symbol.intern("clojure.core", "with-meta");
    public static final AFn const__13 = Symbol.intern("riddley.walk", "macroexpand");
    public static final Var const__14 = RT.var("clojure.core", "first");
    public static final Var const__15 = RT.var("clojure.core", "apply");
    public static final Var const__16 = RT.var("clojure.core", "hash-map");
    public static final Keyword const__17 = RT.keyword(null, "tag");
    public static final Var const__18 = RT.var("clojure.core", "rest");
    public static final Var const__19 = RT.var("clojure.core", "meta");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "tag"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public types$definterface_PLUS_$fn__26200$fn__26204(Object object, Object object2) {
        this.class_name = object;
        this.fn_name = object2;
    }

    @Override
    public Object invoke(Object args) {
        Object object = args;
        args = null;
        Object args2 = ((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot(), object);
        IFn iFn = (IFn)const__2.getRawRoot();
        IFn iFn2 = (IFn)const__3.getRawRoot();
        Object object2 = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(args2));
        IFn iFn3 = (IFn)const__4.getRawRoot();
        IFn iFn4 = (IFn)const__2.getRawRoot();
        IFn iFn5 = (IFn)const__3.getRawRoot();
        Object object3 = ((IFn)const__4.getRawRoot()).invoke(const__6);
        Object object4 = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__7), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__8), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(".", ((IFn)const__11.getRawRoot()).invoke(this_.fn_name))))))), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__12), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__13), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(args2))))), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__16.getRawRoot(), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__17), ((IFn)const__4.getRawRoot()).invoke(this_.class_name)))))))), ((IFn)const__18.getRawRoot()).invoke(args2))));
        IFn iFn6 = (IFn)const__4.getRawRoot();
        IFn iFn7 = (IFn)const__15.getRawRoot();
        Object object5 = const__16.getRawRoot();
        IFn iFn8 = (IFn)const__2.getRawRoot();
        IFn iFn9 = (IFn)const__3.getRawRoot();
        Object object6 = ((IFn)const__4.getRawRoot()).invoke(const__17);
        IFn iFn10 = (IFn)const__4.getRawRoot();
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object7 = args2;
        args2 = null;
        Object object8 = ((IFn)const__19.getRawRoot()).invoke(object7);
        Object object9 = iLookupThunk.get(object8);
        if (iLookupThunk == object9) {
            __thunk__0__ = __site__0__.fault(object8);
            object9 = __thunk__0__.get(object8);
        }
        types$definterface_PLUS_$fn__26200$fn__26204 this_ = null;
        return iFn.invoke(iFn2.invoke(object2, iFn3.invoke(iFn4.invoke(iFn5.invoke(object3, object4, iFn6.invoke(iFn7.invoke(object5, iFn8.invoke(iFn9.invoke(object6, iFn10.invoke(object9))))))))));
    }
}

