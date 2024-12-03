/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import potemkin.types$definterface_PLUS_$fn__26200$fn__26204;
import potemkin.types$definterface_PLUS_$fn__26200$fn__26206;

public final class types$definterface_PLUS_$fn__26200
extends AFunction {
    Object class_name;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "remove");
    public static final Var const__4 = RT.var("clojure.core", "string?");
    public static final Var const__5 = RT.var("clojure.core", "filter");
    public static final Var const__6 = RT.var("clojure.core", "concat");
    public static final Var const__7 = RT.var("clojure.core", "list");
    public static final AFn const__8 = Symbol.intern("clojure.core", "fn");
    public static final Var const__9 = RT.var("clojure.core", "map");
    public static final Var const__10 = RT.var("potemkin.macros", "unify-gensyms");
    public static final AFn const__11 = Symbol.intern("clojure.core", "defn");
    public static final Var const__12 = RT.var("clojure.core", "apply");
    public static final Var const__13 = RT.var("clojure.core", "hash-map");
    public static final Keyword const__14 = RT.keyword(null, "inline");
    public static final Var const__15 = RT.var("clojure.core", "eval");

    public types$definterface_PLUS_$fn__26200(Object object) {
        this.class_name = object;
    }

    @Override
    public Object invoke(Object p__26199) {
        Object f;
        Object vec__26201;
        Object object = p__26199;
        p__26199 = null;
        Object object2 = vec__26201 = object;
        vec__26201 = null;
        Object seq__26202 = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object first__26203 = ((IFn)const__1.getRawRoot()).invoke(seq__26202);
        Object object3 = seq__26202;
        seq__26202 = null;
        Object seq__262022 = ((IFn)const__2.getRawRoot()).invoke(object3);
        Object object4 = first__26203;
        first__26203 = null;
        Object fn_name2 = object4;
        Object object5 = seq__262022;
        seq__262022 = null;
        Object arg_lists_PLUS_doc_string = object5;
        Object arg_lists = ((IFn)const__3.getRawRoot()).invoke(const__4.getRawRoot(), arg_lists_PLUS_doc_string);
        Object object6 = arg_lists_PLUS_doc_string;
        arg_lists_PLUS_doc_string = null;
        Object doc_string = ((IFn)const__5.getRawRoot()).invoke(const__4.getRawRoot(), object6);
        Object form_fn = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(const__8), ((IFn)const__9.getRawRoot()).invoke(new types$definterface_PLUS_$fn__26200$fn__26204(this_.class_name, fn_name2), arg_lists)));
        Object object7 = fn_name2;
        fn_name2 = null;
        Object object8 = doc_string;
        doc_string = null;
        Object object9 = ((IFn)const__7.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(const__13.getRawRoot(), ((IFn)const__0.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(const__14), ((IFn)const__7.getRawRoot()).invoke(form_fn)))));
        Object object10 = form_fn;
        form_fn = null;
        Object object11 = f = ((IFn)const__15.getRawRoot()).invoke(object10);
        f = null;
        Object object12 = arg_lists;
        arg_lists = null;
        types$definterface_PLUS_$fn__26200 this_ = null;
        return ((IFn)const__10.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(const__11), ((IFn)const__7.getRawRoot()).invoke(object7), object8, object9, ((IFn)const__9.getRawRoot()).invoke(new types$definterface_PLUS_$fn__26200$fn__26206(object11), object12))));
    }
}

