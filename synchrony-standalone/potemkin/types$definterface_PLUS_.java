/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;
import java.util.regex.Pattern;
import potemkin.types$definterface_PLUS_$fn__26190;
import potemkin.types$definterface_PLUS_$fn__26197;
import potemkin.types$definterface_PLUS_$fn__26200;

public final class types$definterface_PLUS_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "map");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "mapcat");
    public static final Var const__3 = RT.var("clojure.string", "replace");
    public static final Var const__4 = RT.var("clojure.core", "str");
    public static final Var const__5 = RT.var("clojure.core", "*ns*");
    public static final Object const__6 = Pattern.compile("\\-");
    public static final Var const__7 = RT.var("clojure.core", "seq");
    public static final Var const__8 = RT.var("clojure.core", "concat");
    public static final Var const__9 = RT.var("clojure.core", "list");
    public static final AFn const__10 = Symbol.intern("clojure.core", "let");
    public static final Var const__11 = RT.var("clojure.core", "apply");
    public static final Var const__12 = RT.var("clojure.core", "vector");
    public static final AFn const__13 = Symbol.intern(null, "p__26188__auto__");
    public static final AFn const__14 = Symbol.intern(null, "do");
    public static final AFn const__15 = Symbol.intern("clojure.core", "import");
    public static final Var const__16 = RT.var("clojure.core", "symbol");
    public static final AFn const__17 = Symbol.intern("clojure.core", "definterface");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object name2, ISeq body) {
        Object object;
        ((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot(), body);
        Object unrolled_body = ((IFn)const__2.getRawRoot()).invoke(new types$definterface_PLUS_$fn__26190(), body);
        Object class_name = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__5.get(), ".", name2), const__6, "_");
        IFn iFn = (IFn)const__7.getRawRoot();
        IFn iFn2 = (IFn)const__8.getRawRoot();
        Object object2 = ((IFn)const__9.getRawRoot()).invoke(const__10);
        IFn iFn3 = (IFn)const__9.getRawRoot();
        IFn iFn4 = (IFn)const__11.getRawRoot();
        Object object3 = const__12.getRawRoot();
        IFn iFn5 = (IFn)const__7.getRawRoot();
        IFn iFn6 = (IFn)const__8.getRawRoot();
        Object object4 = ((IFn)const__9.getRawRoot()).invoke(const__13);
        IFn iFn7 = (IFn)const__9.getRawRoot();
        Object object5 = ((IFn)new types$definterface_PLUS_$fn__26197(class_name)).invoke();
        if (object5 != null && object5 != Boolean.FALSE) {
            object = ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__14), ((IFn)const__9.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__15), ((IFn)const__9.getRawRoot()).invoke(((IFn)const__16.getRawRoot()).invoke(class_name))))), ((IFn)const__9.getRawRoot()).invoke(null)));
        } else {
            Object object6 = name2;
            name2 = null;
            Object object7 = unrolled_body;
            unrolled_body = null;
            object = ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__17), ((IFn)const__9.getRawRoot()).invoke(object6), object7));
        }
        Object object8 = class_name;
        class_name = null;
        ISeq iSeq = body;
        body = null;
        return iFn.invoke(iFn2.invoke(object2, iFn3.invoke(iFn4.invoke(object3, iFn5.invoke(iFn6.invoke(object4, iFn7.invoke(object))))), ((IFn)const__0.getRawRoot()).invoke(new types$definterface_PLUS_$fn__26200(object8), iSeq), ((IFn)const__9.getRawRoot()).invoke(const__13)));
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
        return types$definterface_PLUS_.invokeStatic(object5, object6, object7, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 3;
    }
}

