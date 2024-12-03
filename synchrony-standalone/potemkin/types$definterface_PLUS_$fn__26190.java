/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.types$definterface_PLUS_$fn__26190$fn__26194;

public final class types$definterface_PLUS_$fn__26190
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "remove");
    public static final Var const__4 = RT.var("clojure.core", "string?");
    public static final Var const__5 = RT.var("clojure.core", "map");

    @Override
    public Object invoke(Object p__26189) {
        Object arg_lists_PLUS_doc_string;
        Object vec__26191;
        Object object = p__26189;
        p__26189 = null;
        Object object2 = vec__26191 = object;
        vec__26191 = null;
        Object seq__26192 = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object first__26193 = ((IFn)const__1.getRawRoot()).invoke(seq__26192);
        Object object3 = seq__26192;
        seq__26192 = null;
        Object seq__261922 = ((IFn)const__2.getRawRoot()).invoke(object3);
        Object object4 = first__26193;
        first__26193 = null;
        Object fn_name2 = object4;
        Object object5 = seq__261922;
        seq__261922 = null;
        Object object6 = arg_lists_PLUS_doc_string = object5;
        arg_lists_PLUS_doc_string = null;
        Object arg_lists = ((IFn)const__3.getRawRoot()).invoke(const__4.getRawRoot(), object6);
        Object object7 = fn_name2;
        fn_name2 = null;
        Object object8 = arg_lists;
        arg_lists = null;
        types$definterface_PLUS_$fn__26190 this_ = null;
        return ((IFn)const__5.getRawRoot()).invoke(new types$definterface_PLUS_$fn__26190$fn__26194(object7), object8);
    }
}

