/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Reflector;
import clojure.lang.Tuple;
import clojure.lang.Var;
import java.io.File;

public final class async$chain_async_stacktrace
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "into-array");
    public static final Object const__1 = RT.classForName("java.lang.StackTraceElement");
    public static final Var const__2 = RT.var("clojure.core", "concat");
    public static final Var const__3 = RT.var("ginga.async", "collapse-state-machine-stes");
    public static final Var const__4 = RT.var("ginga.core", "drop-last-while");
    public static final Var const__5 = RT.var("ginga.async", "ignorable-async-ste-after-async-call?");
    public static final Var const__6 = RT.var("clojure.core", "filter");
    public static final Var const__7 = RT.var("clojure.core", "complement");
    public static final Var const__8 = RT.var("ginga.async", "ignorable-async-ste?");
    public static final Var const__9 = RT.var("clojure.core", "str");
    public static final Object const__10 = 0L;

    public static Object invokeStatic(Object e1, Object e2, Object ns2, Object file2, Object line) {
        Object object;
        Object or__5581__auto__8754;
        String string2;
        Object G__8752 = e1;
        Throwable throwable2 = (Throwable)G__8752;
        IFn iFn = (IFn)const__0.getRawRoot();
        IFn iFn2 = (IFn)const__2.getRawRoot();
        Object object2 = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__5.getRawRoot(), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(const__8.getRawRoot()), ((Throwable)e1).getStackTrace())));
        Object object3 = ns2;
        ns2 = null;
        String string3 = (String)((IFn)const__9.getRawRoot()).invoke(object3);
        Object object4 = file2;
        if (object4 != null && object4 != Boolean.FALSE) {
            Object[] objectArray = new Object[1];
            Object object5 = file2;
            file2 = null;
            objectArray[0] = object5;
            string2 = ((File)Reflector.invokeConstructor(RT.classForName("java.io.File"), objectArray)).getName();
        } else {
            string2 = null;
        }
        String string4 = string2;
        Object object6 = line;
        line = null;
        Object object7 = or__5581__auto__8754 = object6;
        if (object7 != null && object7 != Boolean.FALSE) {
            object = or__5581__auto__8754;
            or__5581__auto__8754 = null;
        } else {
            object = const__10;
        }
        Object object8 = e2;
        e2 = null;
        throwable2.setStackTrace((StackTraceElement[])iFn.invoke(const__1, iFn2.invoke(object2, Tuple.create(new StackTraceElement(string3, "(take?)", string4, RT.intCast((Number)object))), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(const__8.getRawRoot()), ((Throwable)object8).getStackTrace())))));
        Object object9 = null;
        return e1;
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
        return async$chain_async_stacktrace.invokeStatic(object6, object7, object8, object9, object10);
    }
}

