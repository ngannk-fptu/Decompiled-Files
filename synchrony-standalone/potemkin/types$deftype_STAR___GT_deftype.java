/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentHashSet;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class types$deftype_STAR___GT_deftype
extends AFunction {
    public static final Var const__0 = RT.var("potemkin.types", "deftype->deftype*");
    public static final Var const__1 = RT.var("clojure.core", "seq");
    public static final Var const__2 = RT.var("clojure.core", "first");
    public static final Var const__3 = RT.var("clojure.core", "next");
    public static final Var const__4 = RT.var("clojure.core", "list*");
    public static final AFn const__5 = Symbol.intern(null, "deftype");
    public static final Var const__6 = RT.var("clojure.core", "symbol");
    public static final Var const__7 = RT.var("clojure.core", "name");
    public static final Var const__8 = RT.var("clojure.core", "concat");
    public static final Var const__9 = RT.var("clojure.core", "remove");
    public static final AFn const__11 = PersistentHashSet.create(Symbol.intern(null, "clojure.lang.IType"));

    public static Object invokeStatic(Object x) {
        Object vec__26157;
        Object object = x;
        x = null;
        Object object2 = vec__26157 = ((IFn)const__0.getRawRoot()).invoke(object);
        vec__26157 = null;
        Object seq__26158 = ((IFn)const__1.getRawRoot()).invoke(object2);
        Object first__26159 = ((IFn)const__2.getRawRoot()).invoke(seq__26158);
        Object object3 = seq__26158;
        seq__26158 = null;
        Object seq__261582 = ((IFn)const__3.getRawRoot()).invoke(object3);
        first__26159 = null;
        Object first__261592 = ((IFn)const__2.getRawRoot()).invoke(seq__261582);
        Object object4 = seq__261582;
        seq__261582 = null;
        Object seq__261583 = ((IFn)const__3.getRawRoot()).invoke(object4);
        Object object5 = first__261592;
        first__261592 = null;
        Object dname = object5;
        Object first__261593 = ((IFn)const__2.getRawRoot()).invoke(seq__261583);
        Object object6 = seq__261583;
        seq__261583 = null;
        Object seq__261584 = ((IFn)const__3.getRawRoot()).invoke(object6);
        first__261593 = null;
        Object first__261594 = ((IFn)const__2.getRawRoot()).invoke(seq__261584);
        Object object7 = seq__261584;
        seq__261584 = null;
        Object seq__261585 = ((IFn)const__3.getRawRoot()).invoke(object7);
        Object object8 = first__261594;
        first__261594 = null;
        Object params2 = object8;
        Object first__261595 = ((IFn)const__2.getRawRoot()).invoke(seq__261585);
        Object object9 = seq__261585;
        seq__261585 = null;
        Object seq__261586 = ((IFn)const__3.getRawRoot()).invoke(object9);
        first__261595 = null;
        Object first__261596 = ((IFn)const__2.getRawRoot()).invoke(seq__261586);
        Object object10 = seq__261586;
        seq__261586 = null;
        Object seq__261587 = ((IFn)const__3.getRawRoot()).invoke(object10);
        Object object11 = first__261596;
        first__261596 = null;
        Object object12 = object11;
        Object object13 = seq__261587;
        seq__261587 = null;
        Object body = object13;
        Object object14 = dname;
        dname = null;
        Object object15 = params2;
        params2 = null;
        Object object16 = object12;
        object12 = null;
        Object object17 = body;
        body = null;
        return ((IFn)const__4.getRawRoot()).invoke(const__5, ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object14)), object15, ((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__11, object16), object17));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$deftype_STAR___GT_deftype.invokeStatic(object2);
    }
}

