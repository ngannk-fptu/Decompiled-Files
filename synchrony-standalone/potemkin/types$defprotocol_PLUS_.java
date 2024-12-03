/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Util;
import clojure.lang.Var;

public final class types$defprotocol_PLUS_
extends RestFn {
    public static final Keyword const__0 = RT.keyword("potemkin", "body");
    public static final Var const__1 = RT.var("clojure.core", "meta");
    public static final Var const__2 = RT.var("clojure.core", "resolve");
    public static final Var const__3 = RT.var("clojure.core", "not");
    public static final Var const__4 = RT.var("potemkin.macros", "equivalent?");
    public static final Var const__6 = RT.var("clojure.core", "seq");
    public static final Var const__7 = RT.var("clojure.core", "concat");
    public static final Var const__8 = RT.var("clojure.core", "list");
    public static final AFn const__9 = Symbol.intern("clojure.core", "let");
    public static final Var const__10 = RT.var("clojure.core", "apply");
    public static final Var const__11 = RT.var("clojure.core", "vector");
    public static final AFn const__12 = Symbol.intern(null, "p__26172__auto__");
    public static final AFn const__13 = Symbol.intern("clojure.core", "defprotocol");
    public static final AFn const__14 = Symbol.intern("clojure.core", "alter-meta!");
    public static final AFn const__15 = Symbol.intern("clojure.core", "resolve");
    public static final AFn const__16 = Symbol.intern("clojure.core", "assoc");
    public static final AFn const__17 = Symbol.intern(null, "quote");
    public static final Var const__18 = RT.var("riddley.walk", "macroexpand-all");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword("potemkin", "body"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object name2, ISeq body) {
        Object object;
        Object object2;
        Object or__5581__auto__26174;
        Object prev_body;
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object3 = ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(name2));
        Object object4 = iLookupThunk.get(object3);
        if (iLookupThunk == object4) {
            __thunk__0__ = __site__0__.fault(object3);
            object4 = __thunk__0__.get(object3);
        }
        Object object5 = prev_body = object4;
        prev_body = null;
        Object object6 = or__5581__auto__26174 = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(object5, body));
        if (object6 != null && object6 != Boolean.FALSE) {
            object2 = or__5581__auto__26174;
            or__5581__auto__26174 = null;
        } else {
            object2 = Util.identical(((IFn)const__2.getRawRoot()).invoke(name2), null) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object7 = name2;
            name2 = null;
            Object object8 = ((IFn)const__8.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(const__11.getRawRoot(), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__12), ((IFn)const__8.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__13), ((IFn)const__8.getRawRoot()).invoke(object7), body)))))));
            ISeq iSeq = body;
            body = null;
            object = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__9), object8, ((IFn)const__8.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__14), ((IFn)const__8.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__15), ((IFn)const__8.getRawRoot()).invoke(const__12)))), ((IFn)const__8.getRawRoot()).invoke(const__16), ((IFn)const__8.getRawRoot()).invoke(const__0), ((IFn)const__8.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__17), ((IFn)const__8.getRawRoot()).invoke(((IFn)const__18.getRawRoot()).invoke(iSeq)))))))), ((IFn)const__8.getRawRoot()).invoke(const__12)));
        } else {
            object = null;
        }
        return object;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        ISeq iSeq = (ISeq)object4;
        object4 = null;
        return types$defprotocol_PLUS_.invokeStatic(object5, object6, object7, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 3;
    }
}

