/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import java.lang.reflect.Array;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.wrappers.Wrapper;

public class ParameterTypes {
    private static final Class[] NO_PARAMETERS = new Class[0];
    protected volatile Class[] nativeParamTypes;
    protected volatile CachedClass[] parameterTypes;
    protected boolean isVargsMethod;

    public ParameterTypes() {
    }

    public ParameterTypes(Class[] pt) {
        this.nativeParamTypes = pt;
    }

    public ParameterTypes(String[] pt) {
        this.nativeParamTypes = new Class[pt.length];
        for (int i = 0; i != pt.length; ++i) {
            try {
                this.nativeParamTypes[i] = Class.forName(pt[i]);
                continue;
            }
            catch (ClassNotFoundException e) {
                NoClassDefFoundError err = new NoClassDefFoundError();
                err.initCause(e);
                throw err;
            }
        }
    }

    public ParameterTypes(CachedClass[] parameterTypes) {
        this.setParametersTypes(parameterTypes);
    }

    protected final void setParametersTypes(CachedClass[] pt) {
        this.parameterTypes = pt;
        this.isVargsMethod = pt.length > 0 && pt[pt.length - 1].isArray;
    }

    public CachedClass[] getParameterTypes() {
        if (this.parameterTypes == null) {
            this.getParametersTypes0();
        }
        return this.parameterTypes;
    }

    private synchronized void getParametersTypes0() {
        Class[] npt;
        if (this.parameterTypes != null) {
            return;
        }
        Class[] classArray = npt = this.nativeParamTypes == null ? this.getPT() : this.nativeParamTypes;
        if (npt.length == 0) {
            this.nativeParamTypes = NO_PARAMETERS;
            this.setParametersTypes(CachedClass.EMPTY_ARRAY);
        } else {
            CachedClass[] pt = new CachedClass[npt.length];
            for (int i = 0; i != npt.length; ++i) {
                pt[i] = ReflectionCache.getCachedClass(npt[i]);
            }
            this.nativeParamTypes = npt;
            this.setParametersTypes(pt);
        }
    }

    public Class[] getNativeParameterTypes() {
        if (this.nativeParamTypes == null) {
            this.getNativeParameterTypes0();
        }
        return this.nativeParamTypes;
    }

    private synchronized void getNativeParameterTypes0() {
        Class[] npt;
        if (this.nativeParamTypes != null) {
            return;
        }
        if (this.parameterTypes != null) {
            npt = new Class[this.parameterTypes.length];
            for (int i = 0; i != this.parameterTypes.length; ++i) {
                npt[i] = this.parameterTypes[i].getTheClass();
            }
        } else {
            npt = this.getPT();
        }
        this.nativeParamTypes = npt;
    }

    protected Class[] getPT() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public boolean isVargsMethod() {
        return this.isVargsMethod;
    }

    public boolean isVargsMethod(Object[] arguments) {
        if (!this.isVargsMethod) {
            return false;
        }
        int lenMinus1 = this.parameterTypes.length - 1;
        if (lenMinus1 == arguments.length) {
            return true;
        }
        if (lenMinus1 > arguments.length) {
            return false;
        }
        if (arguments.length > this.parameterTypes.length) {
            return true;
        }
        Object last = arguments[arguments.length - 1];
        if (last == null) {
            return true;
        }
        Class<?> clazz = last.getClass();
        return !clazz.equals(this.parameterTypes[lenMinus1].getTheClass());
    }

    public final Object[] coerceArgumentsToClasses(Object[] argumentArray) {
        argumentArray = this.correctArguments(argumentArray);
        CachedClass[] pt = this.parameterTypes;
        int len = argumentArray.length;
        for (int i = 0; i < len; ++i) {
            Object argument = argumentArray[i];
            if (argument == null) continue;
            argumentArray[i] = pt[i].coerceArgument(argument);
        }
        return argumentArray;
    }

    public Object[] correctArguments(Object[] argumentArray) {
        if (argumentArray == null) {
            return MetaClassHelper.EMPTY_ARRAY;
        }
        CachedClass[] pt = this.getParameterTypes();
        if (pt.length == 1 && argumentArray.length == 0) {
            if (this.isVargsMethod) {
                return new Object[]{Array.newInstance(pt[0].getTheClass().getComponentType(), 0)};
            }
            return MetaClassHelper.ARRAY_WITH_NULL;
        }
        if (this.isVargsMethod && this.isVargsMethod(argumentArray)) {
            return ParameterTypes.fitToVargs(argumentArray, pt);
        }
        return argumentArray;
    }

    private static Object[] fitToVargs(Object[] argumentArrayOrig, CachedClass[] paramTypes) {
        Class<?> vargsClassOrig = paramTypes[paramTypes.length - 1].getTheClass().getComponentType();
        Class vargsClass = ReflectionCache.autoboxType(vargsClassOrig);
        Object[] argumentArray = (Object[])argumentArrayOrig.clone();
        MetaClassHelper.unwrap(argumentArray);
        if (argumentArray.length == paramTypes.length - 1) {
            Object vargs;
            Object[] newArgs = new Object[paramTypes.length];
            System.arraycopy(argumentArray, 0, newArgs, 0, argumentArray.length);
            newArgs[newArgs.length - 1] = vargs = Array.newInstance(vargsClass, 0);
            return newArgs;
        }
        if (argumentArray.length == paramTypes.length) {
            Object lastArgument = argumentArray[argumentArray.length - 1];
            if (lastArgument != null && !lastArgument.getClass().isArray()) {
                Object wrapped = ParameterTypes.makeCommonArray(argumentArray, paramTypes.length - 1, vargsClass);
                Object[] newArgs = new Object[paramTypes.length];
                System.arraycopy(argumentArray, 0, newArgs, 0, paramTypes.length - 1);
                newArgs[newArgs.length - 1] = wrapped;
                return newArgs;
            }
            return argumentArray;
        }
        if (argumentArray.length > paramTypes.length) {
            Object vargs;
            Object[] newArgs = new Object[paramTypes.length];
            System.arraycopy(argumentArray, 0, newArgs, 0, paramTypes.length - 1);
            newArgs[newArgs.length - 1] = vargs = ParameterTypes.makeCommonArray(argumentArray, paramTypes.length - 1, vargsClass);
            return newArgs;
        }
        throw new GroovyBugError("trying to call a vargs method without enough arguments");
    }

    private static Object makeCommonArray(Object[] arguments, int offset, Class baseClass) {
        Object[] result = (Object[])Array.newInstance(baseClass, arguments.length - offset);
        for (int i = offset; i < arguments.length; ++i) {
            Object v = arguments[i];
            result[i - offset] = v = DefaultTypeTransformation.castToType(v, baseClass);
        }
        return result;
    }

    public boolean isValidMethod(Class[] arguments) {
        if (arguments == null) {
            return true;
        }
        int size = arguments.length;
        CachedClass[] pt = this.getParameterTypes();
        int paramMinus1 = pt.length - 1;
        if (this.isVargsMethod && size >= paramMinus1) {
            return ParameterTypes.isValidVarargsMethod(arguments, size, pt, paramMinus1);
        }
        if (pt.length == size) {
            return ParameterTypes.isValidExactMethod(arguments, pt);
        }
        return pt.length == 1 && size == 0 && !pt[0].isPrimitive;
    }

    private static boolean isValidExactMethod(Class[] arguments, CachedClass[] pt) {
        int size = pt.length;
        for (int i = 0; i < size; ++i) {
            if (pt[i].isAssignableFrom(arguments[i])) continue;
            return false;
        }
        return true;
    }

    public boolean isValidExactMethod(Object[] args) {
        this.getParametersTypes0();
        int size = args.length;
        if (size != this.parameterTypes.length) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (args[i] == null || this.parameterTypes[i].isAssignableFrom(args[i].getClass())) continue;
            return false;
        }
        return true;
    }

    public boolean isValidExactMethod(Class[] args) {
        this.getParametersTypes0();
        int size = args.length;
        if (size != this.parameterTypes.length) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (args[i] == null || this.parameterTypes[i].isAssignableFrom(args[i])) continue;
            return false;
        }
        return true;
    }

    private static boolean testComponentAssignable(Class toTestAgainst, Class toTest) {
        Class<?> component = toTest.getComponentType();
        if (component == null) {
            return false;
        }
        return MetaClassHelper.isAssignableFrom(toTestAgainst, component);
    }

    private static boolean isValidVarargsMethod(Class[] arguments, int size, CachedClass[] pt, int paramMinus1) {
        for (int i = 0; i < paramMinus1; ++i) {
            if (pt[i].isAssignableFrom(arguments[i])) continue;
            return false;
        }
        CachedClass varg = pt[paramMinus1];
        Class<?> clazz = varg.getTheClass().getComponentType();
        if (size == pt.length && (varg.isAssignableFrom(arguments[paramMinus1]) || ParameterTypes.testComponentAssignable(clazz, arguments[paramMinus1]))) {
            return true;
        }
        for (int i = paramMinus1; i < size; ++i) {
            if (MetaClassHelper.isAssignableFrom(clazz, arguments[i])) continue;
            return false;
        }
        return true;
    }

    public boolean isValidMethod(Object[] arguments) {
        if (arguments == null) {
            return true;
        }
        int size = arguments.length;
        CachedClass[] paramTypes = this.getParameterTypes();
        int paramMinus1 = paramTypes.length - 1;
        if (size >= paramMinus1 && paramTypes.length > 0 && paramTypes[paramMinus1].isArray) {
            for (int i = 0; i < paramMinus1; ++i) {
                if (paramTypes[i].isAssignableFrom(ParameterTypes.getArgClass(arguments[i]))) continue;
                return false;
            }
            CachedClass varg = paramTypes[paramMinus1];
            Class<?> clazz = varg.getTheClass().getComponentType();
            if (size == paramTypes.length && (varg.isAssignableFrom(ParameterTypes.getArgClass(arguments[paramMinus1])) || ParameterTypes.testComponentAssignable(clazz, ParameterTypes.getArgClass(arguments[paramMinus1])))) {
                return true;
            }
            for (int i = paramMinus1; i < size; ++i) {
                if (MetaClassHelper.isAssignableFrom(clazz, ParameterTypes.getArgClass(arguments[i]))) continue;
                return false;
            }
            return true;
        }
        if (paramTypes.length == size) {
            for (int i = 0; i < size; ++i) {
                if (paramTypes[i].isAssignableFrom(ParameterTypes.getArgClass(arguments[i]))) continue;
                return false;
            }
            return true;
        }
        return paramTypes.length == 1 && size == 0 && !paramTypes[0].isPrimitive;
    }

    private static Class getArgClass(Object arg) {
        Class cls = arg == null ? null : (arg instanceof Wrapper ? ((Wrapper)arg).getType() : arg.getClass());
        return cls;
    }
}

