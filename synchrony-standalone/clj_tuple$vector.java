/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.PersistentUnrolledVector;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class clj_tuple$vector
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "transient");
    public static final Var const__1 = RT.var("clojure.core", "empty?");
    public static final Var const__2 = RT.var("clojure.core", "persistent!");
    public static final Var const__3 = RT.var("clojure.core", "conj!");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Var const__5 = RT.var("clojure.core", "rest");

    public static Object invokeStatic(Object x, Object y, Object z, Object w, Object u, Object v, ISeq rst) {
        Object r;
        Object object = x;
        x = null;
        Object object2 = y;
        y = null;
        Object object3 = z;
        z = null;
        Object object4 = w;
        w = null;
        Object object5 = u;
        u = null;
        Object object6 = v;
        v = null;
        Object object7 = r = ((IFn)const__0.getRawRoot()).invoke(PersistentUnrolledVector.create(object, object2, object3, object4, object5, object6));
        r = null;
        Object r2 = object7;
        ISeq iSeq = rst;
        rst = null;
        Object s2 = iSeq;
        while (true) {
            Object object8 = ((IFn)const__1.getRawRoot()).invoke(s2);
            if (object8 != null && object8 != Boolean.FALSE) break;
            Object object9 = r2;
            r2 = null;
            Object object10 = ((IFn)const__3.getRawRoot()).invoke(object9, ((IFn)const__4.getRawRoot()).invoke(s2));
            ISeq iSeq2 = s2;
            s2 = null;
            s2 = ((IFn)const__5.getRawRoot()).invoke(iSeq2);
            r2 = object10;
        }
        Object object11 = r2;
        r2 = null;
        return ((IFn)const__2.getRawRoot()).invoke(object11);
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) {
        Object object8 = object;
        object = null;
        Object object9 = object2;
        object2 = null;
        Object object10 = object3;
        object3 = null;
        Object object11 = object4;
        object4 = null;
        Object object12 = object5;
        object5 = null;
        Object object13 = object6;
        object6 = null;
        ISeq iSeq = (ISeq)object7;
        object7 = null;
        return clj_tuple$vector.invokeStatic(object8, object9, object10, object11, object12, object13, iSeq);
    }

    public static Object invokeStatic(Object x, Object y, Object z, Object w, Object u, Object v) {
        Object object = x;
        x = null;
        Object object2 = y;
        y = null;
        Object object3 = z;
        z = null;
        Object object4 = w;
        w = null;
        Object object5 = u;
        u = null;
        Object object6 = v;
        v = null;
        return PersistentUnrolledVector.create(object, object2, object3, object4, object5, object6);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
        Object object7 = object;
        object = null;
        Object object8 = object2;
        object2 = null;
        Object object9 = object3;
        object3 = null;
        Object object10 = object4;
        object4 = null;
        Object object11 = object5;
        object5 = null;
        Object object12 = object6;
        object6 = null;
        return clj_tuple$vector.invokeStatic(object7, object8, object9, object10, object11, object12);
    }

    public static Object invokeStatic(Object x, Object y, Object z, Object w, Object u) {
        Object object = x;
        x = null;
        Object object2 = y;
        y = null;
        Object object3 = z;
        z = null;
        Object object4 = w;
        w = null;
        Object object5 = u;
        u = null;
        return PersistentUnrolledVector.create(object, object2, object3, object4, object5);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5) {
        Object object6 = object;
        object = null;
        Object object7 = object2;
        object2 = null;
        Object object8 = object3;
        object3 = null;
        Object object9 = object4;
        object4 = null;
        Object object10 = object5;
        object5 = null;
        return clj_tuple$vector.invokeStatic(object6, object7, object8, object9, object10);
    }

    public static Object invokeStatic(Object x, Object y, Object z, Object w) {
        Object object = x;
        x = null;
        Object object2 = y;
        y = null;
        Object object3 = z;
        z = null;
        Object object4 = w;
        w = null;
        return PersistentUnrolledVector.create(object, object2, object3, object4);
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
        return clj_tuple$vector.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object x, Object y, Object z) {
        Object object = x;
        x = null;
        Object object2 = y;
        y = null;
        Object object3 = z;
        z = null;
        return PersistentUnrolledVector.create(object, object2, object3);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return clj_tuple$vector.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object x, Object y) {
        Object object = x;
        x = null;
        Object object2 = y;
        y = null;
        return PersistentUnrolledVector.create(object, object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return clj_tuple$vector.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object x) {
        Object object = x;
        x = null;
        return PersistentUnrolledVector.create(object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return clj_tuple$vector.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return PersistentUnrolledVector.create();
    }

    @Override
    public Object invoke() {
        return clj_tuple$vector.invokeStatic();
    }

    @Override
    public int getRequiredArity() {
        return 6;
    }
}

