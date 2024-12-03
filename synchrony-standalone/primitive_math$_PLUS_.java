/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class primitive_math$_PLUS_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "list");
    public static final AFn const__1 = Symbol.intern("primitive_math.Primitives", "add");
    public static final Var const__2 = RT.var("clojure.core", "list*");
    public static final AFn const__3 = Symbol.intern(null, "+");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object x__17683__auto__, Object y__17684__auto__, ISeq rest__17685__auto__) {
        Object object = x__17683__auto__;
        x__17683__auto__ = null;
        Object object2 = y__17684__auto__;
        y__17684__auto__ = null;
        ISeq iSeq = rest__17685__auto__;
        rest__17685__auto__ = null;
        return ((IFn)const__2.getRawRoot()).invoke(const__3, ((IFn)const__0.getRawRoot()).invoke(const__3, object, object2), iSeq);
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3, Object object4, Object object5) {
        Object object6 = object;
        object = null;
        Object object7 = object2;
        object2 = null;
        Object object8 = object3;
        object3 = null;
        Object object9 = object4;
        object4 = null;
        ISeq iSeq = (ISeq)object5;
        object5 = null;
        return primitive_math$_PLUS_.invokeStatic(object6, object7, object8, object9, iSeq);
    }

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object x__17683__auto__, Object y__17684__auto__) {
        Object object = x__17683__auto__;
        x__17683__auto__ = null;
        Object object2 = y__17684__auto__;
        y__17684__auto__ = null;
        return ((IFn)const__0.getRawRoot()).invoke(const__1, object, object2);
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
        return primitive_math$_PLUS_.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object x17691) {
        Object var2_2 = null;
        return x17691;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return primitive_math$_PLUS_.invokeStatic(object4, object5, object6);
    }

    @Override
    public int getRequiredArity() {
        return 4;
    }
}

