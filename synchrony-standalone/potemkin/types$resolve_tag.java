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
import clojure.lang.PersistentHashSet;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class types$resolve_tag
extends AFunction {
    public static final Keyword const__0 = RT.keyword(null, "tag");
    public static final Var const__1 = RT.var("clojure.core", "meta");
    public static final Var const__2 = RT.var("clojure.core", "with-meta");
    public static final Var const__3 = RT.var("clojure.core", "assoc");
    public static final AFn const__11 = PersistentHashSet.create(Symbol.intern(null, "boolean"), Symbol.intern(null, "long"), Symbol.intern(null, "double"), Symbol.intern(null, "short"), Symbol.intern(null, "int"), Symbol.intern(null, "void"), Symbol.intern(null, "byte"));
    public static final Var const__12 = RT.var("clojure.core", "resolve");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "tag"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public static Object invokeStatic(Object n) {
        Object object;
        Object temp__5802__auto__26184;
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(n);
        Object object3 = iLookupThunk.get(object2);
        if (iLookupThunk == object3) {
            __thunk__0__ = __site__0__.fault(object2);
            object3 = __thunk__0__.get(object2);
        }
        Object object4 = temp__5802__auto__26184 = object3;
        if (object4 != null && object4 != Boolean.FALSE) {
            Object object5;
            Object or__5581__auto__26183;
            Object object6 = temp__5802__auto__26184;
            temp__5802__auto__26184 = null;
            Object tag2 = object6;
            IFn iFn = (IFn)const__2.getRawRoot();
            Object object7 = n;
            IFn iFn2 = (IFn)const__3.getRawRoot();
            Object object8 = n;
            n = null;
            Object object9 = ((IFn)const__1.getRawRoot()).invoke(object8);
            Object object10 = or__5581__auto__26183 = ((IFn)const__11).invoke(tag2);
            if (object10 != null && object10 != Boolean.FALSE) {
                object5 = or__5581__auto__26183;
                or__5581__auto__26183 = null;
            } else {
                Object object11 = tag2;
                tag2 = null;
                object5 = ((IFn)const__12.getRawRoot()).invoke(object11);
            }
            object = iFn.invoke(object7, iFn2.invoke(object9, const__0, object5));
        } else {
            object = n;
            Object object12 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$resolve_tag.invokeStatic(object2);
    }
}

