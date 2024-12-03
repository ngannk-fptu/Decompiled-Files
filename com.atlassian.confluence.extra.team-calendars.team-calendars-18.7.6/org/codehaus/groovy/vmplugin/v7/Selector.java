/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin.v7;

import groovy.lang.AdaptingMetaClass;
import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import groovy.lang.MissingMethodException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.reflection.stdclasses.CachedSAMClass;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.NullObject;
import org.codehaus.groovy.runtime.dgmimpl.NumberNumberMetaMethod;
import org.codehaus.groovy.runtime.metaclass.ClosureMetaClass;
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;
import org.codehaus.groovy.runtime.metaclass.MethodMetaProperty;
import org.codehaus.groovy.runtime.metaclass.NewInstanceMetaMethod;
import org.codehaus.groovy.runtime.metaclass.NewStaticMetaMethod;
import org.codehaus.groovy.runtime.metaclass.ReflectionMetaMethod;
import org.codehaus.groovy.runtime.wrappers.Wrapper;
import org.codehaus.groovy.vmplugin.v7.IndyGuardsFiltersAndSignatures;
import org.codehaus.groovy.vmplugin.v7.IndyInterface;
import org.codehaus.groovy.vmplugin.v7.IndyMath;
import org.codehaus.groovy.vmplugin.v7.TypeHelper;
import org.codehaus.groovy.vmplugin.v7.TypeTransformers;

public abstract class Selector {
    public Object[] args;
    public Object[] originalArguments;
    public MetaMethod method;
    public MethodType targetType;
    public MethodType currentType;
    public String name;
    public MethodHandle handle;
    public boolean useMetaClass = false;
    public boolean cache = true;
    public MutableCallSite callSite;
    public Class sender;
    public boolean isVargs;
    public boolean safeNavigation;
    public boolean safeNavigationOrig;
    public boolean spread;
    public boolean skipSpreadCollector;
    public boolean thisCall;
    public Class selectionBase;
    public boolean catchException = true;
    public IndyInterface.CALL_TYPES callType;
    private static final IndyInterface.CALL_TYPES[] CALL_TYPES_VALUES = IndyInterface.CALL_TYPES.values();

    public static Selector getSelector(MutableCallSite callSite, Class sender, String methodName, int callID, boolean safeNavigation, boolean thisCall, boolean spreadCall, Object[] arguments) {
        IndyInterface.CALL_TYPES callType = CALL_TYPES_VALUES[callID];
        switch (callType) {
            case INIT: {
                return new InitSelector(callSite, sender, methodName, callType, safeNavigation, thisCall, spreadCall, arguments);
            }
            case METHOD: {
                return new MethodSelector(callSite, sender, methodName, callType, safeNavigation, thisCall, spreadCall, arguments);
            }
            case GET: {
                return new PropertySelector(callSite, sender, methodName, callType, safeNavigation, thisCall, spreadCall, arguments);
            }
            case SET: {
                throw new GroovyBugError("your call tried to do a property set, which is not supported.");
            }
            case CAST: {
                return new CastSelector(callSite, arguments);
            }
        }
        throw new GroovyBugError("unexpected call type");
    }

    abstract void setCallSiteTarget();

    private static Object[] spread(Object[] args, boolean spreadCall) {
        if (!spreadCall) {
            return args;
        }
        Object[] normalArguments = (Object[])args[1];
        Object[] ret = new Object[normalArguments.length + 1];
        ret[0] = args[0];
        System.arraycopy(normalArguments, 0, ret, 1, ret.length - 1);
        return ret;
    }

    private static Object unwrapIfWrapped(Object object) {
        if (object instanceof Wrapper) {
            return IndyGuardsFiltersAndSignatures.unwrap(object);
        }
        return object;
    }

    public Object getCorrectedReceiver() {
        Object receiver = this.args[0];
        if (receiver == null) {
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("receiver is null");
            }
            receiver = NullObject.getNullObject();
        }
        return receiver;
    }

    private static boolean isStatic(Method m) {
        int mods = m.getModifiers();
        return (mods & 8) != 0;
    }

    private static MetaClassImpl getMetaClassImpl(MetaClass mc, boolean includeEMC) {
        boolean valid;
        Class<?> mcc = mc.getClass();
        boolean bl = valid = mcc == MetaClassImpl.class || mcc == AdaptingMetaClass.class || mcc == ClosureMetaClass.class || includeEMC && mcc == ExpandoMetaClass.class;
        if (!valid) {
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("meta class is neither MetaClassImpl, nor AdoptingMetaClass, nor ClosureMetaClass, normal method selection path disabled.");
            }
            return null;
        }
        if (IndyInterface.LOG_ENABLED) {
            IndyInterface.LOG.info("meta class is a recognized MetaClassImpl");
        }
        return (MetaClassImpl)mc;
    }

    private static Object[] removeRealReceiver(Object[] args) {
        Object[] ar = new Object[args.length - 1];
        for (int i = 1; i < args.length; ++i) {
            ar[i - 1] = args[i];
        }
        return ar;
    }

    private static class MethodSelector
    extends Selector {
        protected MetaClass mc;
        private boolean isCategoryMethod;

        public MethodSelector(MutableCallSite callSite, Class sender, String methodName, IndyInterface.CALL_TYPES callType, Boolean safeNavigation, Boolean thisCall, Boolean spreadCall, Object[] arguments) {
            this.callType = callType;
            this.targetType = callSite.type();
            this.name = methodName;
            this.originalArguments = arguments;
            this.args = Selector.spread(arguments, spreadCall);
            this.callSite = callSite;
            this.sender = sender;
            this.safeNavigationOrig = safeNavigation;
            this.safeNavigation = safeNavigation != false && arguments[0] == null;
            this.thisCall = thisCall;
            this.spread = spreadCall;
            boolean bl = this.cache = !this.spread;
            if (IndyInterface.LOG_ENABLED) {
                StringBuilder msg = new StringBuilder("----------------------------------------------------\n\t\tinvocation of method '" + methodName + "'\n\t\tinvocation type: " + (Object)((Object)callType) + "\n\t\tsender: " + sender + "\n\t\ttargetType: " + this.targetType + "\n\t\tsafe navigation: " + safeNavigation + "\n\t\tthisCall: " + thisCall + "\n\t\tspreadCall: " + spreadCall + "\n\t\twith " + arguments.length + " arguments");
                for (int i = 0; i < arguments.length; ++i) {
                    msg.append("\n\t\t\targument[").append(i).append("] = ");
                    if (arguments[i] == null) {
                        msg.append("null");
                        continue;
                    }
                    msg.append(arguments[i].getClass().getName()).append("@").append(Integer.toHexString(System.identityHashCode(arguments[i])));
                }
                IndyInterface.LOG.info(msg.toString());
            }
        }

        public boolean setNullForSafeNavigation() {
            if (!this.safeNavigation) {
                return false;
            }
            this.handle = MethodHandles.dropArguments(IndyGuardsFiltersAndSignatures.NULL_REF, 0, this.targetType.parameterArray());
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("set null returning handle for safe navigation");
            }
            return true;
        }

        public void getMetaClass() {
            Object receiver = this.args[0];
            if (receiver == null) {
                this.mc = NullObject.getNullObject().getMetaClass();
            } else if (receiver instanceof GroovyObject) {
                this.mc = ((GroovyObject)receiver).getMetaClass();
            } else if (receiver instanceof Class) {
                Class c = (Class)receiver;
                ClassLoader cl = c.getClassLoader();
                try {
                    Class.forName(c.getName(), true, cl);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
                this.mc = GroovySystem.getMetaClassRegistry().getMetaClass(c);
                this.cache &= !ClassInfo.getClassInfo(c).hasPerInstanceMetaClasses();
            } else {
                this.mc = ((MetaClassRegistryImpl)GroovySystem.getMetaClassRegistry()).getMetaClass(receiver);
                this.cache &= !ClassInfo.getClassInfo(receiver.getClass()).hasPerInstanceMetaClasses();
            }
            this.mc.initialize();
        }

        public void chooseMeta(MetaClassImpl mci) {
            if (mci == null) {
                return;
            }
            Object receiver = this.getCorrectedReceiver();
            Object[] newArgs = Selector.removeRealReceiver(this.args);
            if (receiver instanceof Class) {
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("receiver is a class");
                }
                if (!mci.hasCustomStaticInvokeMethod()) {
                    this.method = mci.retrieveStaticMethod(this.name, newArgs);
                }
            } else {
                String changedName = this.name;
                if (receiver instanceof GeneratedClosure && changedName.equals("call")) {
                    changedName = "doCall";
                }
                if (!mci.hasCustomInvokeMethod()) {
                    this.method = mci.getMethodWithCaching(this.selectionBase, changedName, newArgs, false);
                }
            }
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("retrieved method from meta class: " + this.method);
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public void setHandleForMetaMethod() {
            MetaMethod metaMethod = this.method;
            this.isCategoryMethod = this.method instanceof GroovyCategorySupport.CategoryMethod;
            if (metaMethod instanceof NumberNumberMetaMethod || this.method instanceof GeneratedMetaMethod && (this.name.equals("next") || this.name.equals("previous"))) {
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("meta method is number method");
                }
                if (IndyMath.chooseMathMethod(this, metaMethod)) {
                    this.catchException = false;
                    if (!IndyInterface.LOG_ENABLED) return;
                    IndyInterface.LOG.info("indy math successfull");
                    return;
                }
            }
            boolean isCategoryTypeMethod = metaMethod instanceof NewInstanceMetaMethod;
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("meta method is category type method: " + isCategoryTypeMethod);
            }
            boolean isStaticCategoryTypeMethod = metaMethod instanceof NewStaticMetaMethod;
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("meta method is static category type method: " + isCategoryTypeMethod);
            }
            if (metaMethod instanceof ReflectionMetaMethod) {
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("meta method is reflective method");
                }
                ReflectionMetaMethod rmm = (ReflectionMetaMethod)metaMethod;
                metaMethod = rmm.getCachedMethod();
            }
            if (metaMethod instanceof CachedMethod) {
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("meta method is CachedMethod instance");
                }
                CachedMethod cm = (CachedMethod)metaMethod;
                this.isVargs = cm.isVargsMethod();
                try {
                    Method m = cm.getCachedMethod();
                    this.handle = this.correctClassForNameAndUnReflectOtherwise(m);
                    if (IndyInterface.LOG_ENABLED) {
                        IndyInterface.LOG.info("successfully unreflected method");
                    }
                    if (isStaticCategoryTypeMethod) {
                        this.handle = MethodHandles.insertArguments(this.handle, 0, new Object[]{null});
                        this.handle = MethodHandles.dropArguments(this.handle, 0, new Class[]{this.targetType.parameterType(0)});
                        return;
                    }
                    if (isCategoryTypeMethod || !Selector.isStatic(m)) return;
                    this.handle = MethodHandles.dropArguments(this.handle, 0, Object.class);
                    return;
                }
                catch (IllegalAccessException e) {
                    throw new GroovyBugError(e);
                }
            } else {
                if (this.method == null) return;
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("meta method is dgm helper");
                }
                this.handle = IndyGuardsFiltersAndSignatures.META_METHOD_INVOKER;
                this.handle = this.handle.bindTo(this.method);
                if (this.spread) {
                    this.args = this.originalArguments;
                    this.skipSpreadCollector = true;
                } else {
                    this.handle = this.handle.asCollector(Object[].class, this.targetType.parameterCount() - 1);
                }
                this.currentType = this.removeWrapper(this.targetType);
                if (!IndyInterface.LOG_ENABLED) return;
                IndyInterface.LOG.info("bound method name to META_METHOD_INVOKER");
            }
        }

        private MethodHandle correctClassForNameAndUnReflectOtherwise(Method m) throws IllegalAccessException {
            if (m.getDeclaringClass() == Class.class && m.getName().equals("forName") && m.getParameterTypes().length == 1) {
                return MethodHandles.insertArguments(IndyGuardsFiltersAndSignatures.CLASS_FOR_NAME, 1, true, this.sender.getClassLoader());
            }
            return IndyInterface.LOOKUP.unreflect(m);
        }

        private MethodType removeWrapper(MethodType targetType) {
            Class<?>[] types = targetType.parameterArray();
            for (int i = 0; i < types.length; ++i) {
                if (types[i] != Wrapper.class) continue;
                targetType = targetType.changeParameterType(i, Object.class);
            }
            return targetType;
        }

        public void setMetaClassCallHandleIfNedded(boolean standardMetaClass) {
            Object receiver;
            if (this.handle != null) {
                return;
            }
            this.useMetaClass = true;
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("set meta class invocation path");
            }
            if ((receiver = this.getCorrectedReceiver()) instanceof Class) {
                this.handle = IndyGuardsFiltersAndSignatures.META_CLASS_INVOKE_STATIC_METHOD.bindTo(this.mc);
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("use invokeStaticMethod with bound meta class");
                }
            } else {
                this.handle = IndyGuardsFiltersAndSignatures.MOP_INVOKE_METHOD.bindTo(this.mc);
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("use invokeMethod with bound meta class");
                }
                if (receiver instanceof GroovyObject) {
                    if (IndyInterface.LOG_ENABLED) {
                        IndyInterface.LOG.info("add MissingMethod handler for GrooObject#invokeMethod fallback path");
                    }
                    this.handle = MethodHandles.catchException(this.handle, MissingMethodException.class, IndyGuardsFiltersAndSignatures.GROOVY_OBJECT_INVOKER);
                }
            }
            this.handle = MethodHandles.insertArguments(this.handle, 1, this.name);
            if (!this.spread) {
                this.handle = this.handle.asCollector(Object[].class, this.targetType.parameterCount() - 1);
            }
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("bind method name and create collector for arguments");
            }
        }

        public void correctWrapping() {
            if (this.useMetaClass) {
                return;
            }
            Class<?>[] pt = this.handle.type().parameterArray();
            if (this.currentType != null) {
                pt = this.currentType.parameterArray();
            }
            for (int i = 1; i < this.args.length; ++i) {
                if (!(this.args[i] instanceof Wrapper)) continue;
                Class<?> type = pt[i];
                MethodType mt = MethodType.methodType(type, Wrapper.class);
                this.handle = MethodHandles.filterArguments(this.handle, i, IndyGuardsFiltersAndSignatures.UNWRAP_METHOD.asType(mt));
                if (!IndyInterface.LOG_ENABLED) continue;
                IndyInterface.LOG.info("added filter for Wrapper for argument at pos " + i);
            }
        }

        public void correctParameterLength() {
            if (this.handle == null) {
                return;
            }
            Class<?>[] params = this.handle.type().parameterArray();
            if (this.currentType != null) {
                params = this.currentType.parameterArray();
            }
            if (!this.isVargs) {
                if (this.spread && this.useMetaClass) {
                    return;
                }
                if (params.length == 2 && this.args.length == 1) {
                    this.handle = MethodHandles.insertArguments(this.handle, 1, new Object[]{null});
                }
                return;
            }
            Class<?> lastParam = params[params.length - 1];
            Object lastArg = Selector.unwrapIfWrapped(this.args[this.args.length - 1]);
            if (params.length == this.args.length) {
                if (lastArg == null) {
                    return;
                }
                if (lastParam.isInstance(lastArg)) {
                    return;
                }
                if (lastArg.getClass().isArray()) {
                    return;
                }
                this.handle = this.handle.asCollector(lastParam, 1);
            } else if (params.length > this.args.length) {
                this.handle = MethodHandles.insertArguments(this.handle, params.length - 1, Array.newInstance(lastParam.getComponentType(), 0));
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("added empty array for missing vargs part");
                }
            } else {
                this.handle = this.handle.asCollector(lastParam, this.args.length - params.length + 1);
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("changed surplus arguments to be collected for vargs call");
                }
            }
        }

        public void correctCoerce() {
            if (this.useMetaClass) {
                return;
            }
            Class<?>[] parameters = this.handle.type().parameterArray();
            if (this.currentType != null) {
                parameters = this.currentType.parameterArray();
            }
            if (this.args.length != parameters.length) {
                throw new GroovyBugError("At this point argument array length and parameter array length should be the same");
            }
            for (int i = 0; i < this.args.length; ++i) {
                Class wrappedPara;
                Class<?> got;
                Object arg;
                if (parameters[i] == Object.class || (arg = Selector.unwrapIfWrapped(this.args[i])) == null || (got = arg.getClass()) == parameters[i] || (wrappedPara = TypeHelper.getWrapperClass(parameters[i])) == TypeHelper.getWrapperClass(got) || parameters[i].isAssignableFrom(got)) continue;
                this.handle = TypeTransformers.addTransformer(this.handle, i, arg, wrappedPara);
                if (!IndyInterface.LOG_ENABLED) continue;
                IndyInterface.LOG.info("added transformer at pos " + i + " for type " + got + " to type " + wrappedPara);
            }
        }

        public void correctNullReceiver() {
            if (this.args[0] != null) {
                return;
            }
            this.handle = this.handle.bindTo(NullObject.getNullObject());
            this.handle = MethodHandles.dropArguments(this.handle, 0, new Class[]{this.targetType.parameterType(0)});
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("binding null object receiver and dropping old receiver");
            }
        }

        public void correctSpreading() {
            if (!this.spread || this.useMetaClass || this.skipSpreadCollector) {
                return;
            }
            this.handle = this.handle.asSpreader(Object[].class, this.args.length - 1);
        }

        public void addExceptionHandler() {
            if (this.handle == null || !this.catchException) {
                return;
            }
            TypeDescriptor.OfField returnType = this.handle.type().returnType();
            if (returnType != Object.class) {
                MethodType mtype = MethodType.methodType(returnType, GroovyRuntimeException.class);
                this.handle = MethodHandles.catchException(this.handle, GroovyRuntimeException.class, IndyGuardsFiltersAndSignatures.UNWRAP_EXCEPTION.asType(mtype));
            } else {
                this.handle = MethodHandles.catchException(this.handle, GroovyRuntimeException.class, IndyGuardsFiltersAndSignatures.UNWRAP_EXCEPTION);
            }
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("added GroovyRuntimeException unwrapper");
            }
        }

        public void setGuards(Object receiver) {
            if (this.handle == null) {
                return;
            }
            if (!this.cache) {
                return;
            }
            MethodHandle fallback = IndyInterface.makeFallBack(this.callSite, this.sender, this.name, this.callType.ordinal(), this.targetType, this.safeNavigationOrig, this.thisCall, this.spread);
            if (receiver instanceof GroovyObject) {
                GroovyObject go = (GroovyObject)receiver;
                MetaClass mc = go.getMetaClass();
                MethodHandle test = IndyGuardsFiltersAndSignatures.SAME_MC.bindTo(mc);
                test = test.asType(MethodType.methodType(Boolean.TYPE, this.targetType.parameterType(0)));
                this.handle = MethodHandles.guardWithTest(test, this.handle, fallback);
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("added meta class equality check");
                }
            } else if (receiver instanceof Class) {
                MethodHandle test = IndyGuardsFiltersAndSignatures.EQUALS.bindTo(receiver);
                test = test.asType(MethodType.methodType(Boolean.TYPE, this.targetType.parameterType(0)));
                this.handle = MethodHandles.guardWithTest(test, this.handle, fallback);
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("added class equality check");
                }
            }
            if (!this.useMetaClass && this.isCategoryMethod && this.method instanceof NewInstanceMetaMethod) {
                this.handle = MethodHandles.guardWithTest(IndyGuardsFiltersAndSignatures.HAS_CATEGORY_IN_CURRENT_THREAD_GUARD, this.handle, fallback);
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("added category-in-current-thread-guard for category method");
                }
            }
            this.handle = IndyInterface.switchPoint.guardWithTest(this.handle, fallback);
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("added switch point guard");
            }
            Class<?>[] pt = this.handle.type().parameterArray();
            for (int i = 0; i < this.args.length; ++i) {
                Object arg = this.args[i];
                MethodHandle test = null;
                if (arg == null) {
                    test = IndyGuardsFiltersAndSignatures.IS_NULL.asType(MethodType.methodType(Boolean.TYPE, pt[i]));
                    if (IndyInterface.LOG_ENABLED) {
                        IndyInterface.LOG.info("added null argument check at pos " + i);
                    }
                } else {
                    Class<?> argClass = arg.getClass();
                    if (pt[i].isPrimitive()) continue;
                    test = IndyGuardsFiltersAndSignatures.SAME_CLASS.bindTo(argClass).asType(MethodType.methodType(Boolean.TYPE, pt[i]));
                    if (IndyInterface.LOG_ENABLED) {
                        IndyInterface.LOG.info("added same class check at pos " + i);
                    }
                }
                Class[] drops = new Class[i];
                for (int j = 0; j < drops.length; ++j) {
                    drops[j] = pt[j];
                }
                test = MethodHandles.dropArguments(test, 0, drops);
                this.handle = MethodHandles.guardWithTest(test, this.handle, fallback);
            }
        }

        public void doCallSiteTargetSet() {
            if (!this.cache) {
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("call site stays uncached");
                }
            } else {
                this.callSite.setTarget(this.handle);
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("call site target set, preparing outside invocation");
                }
            }
        }

        public void setSelectionBase() {
            this.selectionBase = this.thisCall ? this.sender : (this.args[0] == null ? NullObject.class : this.mc.getTheClass());
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("selection base set to " + this.selectionBase);
            }
        }

        public boolean setInterceptor() {
            if (!(this.args[0] instanceof GroovyInterceptable)) {
                return false;
            }
            this.handle = MethodHandles.insertArguments(IndyGuardsFiltersAndSignatures.INTERCEPTABLE_INVOKER, 1, this.name);
            this.handle = this.handle.asCollector(Object[].class, this.targetType.parameterCount() - 1);
            this.handle = this.handle.asType(this.targetType);
            return true;
        }

        @Override
        public void setCallSiteTarget() {
            if (!this.setNullForSafeNavigation() && !this.setInterceptor()) {
                this.getMetaClass();
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("meta class is " + this.mc);
                }
                this.setSelectionBase();
                MetaClassImpl mci = Selector.getMetaClassImpl(this.mc, this.callType != IndyInterface.CALL_TYPES.GET);
                this.chooseMeta(mci);
                this.setHandleForMetaMethod();
                this.setMetaClassCallHandleIfNedded(mci != null);
                this.correctParameterLength();
                this.correctCoerce();
                this.correctWrapping();
                this.correctNullReceiver();
                this.correctSpreading();
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("casting explicit from " + this.handle.type() + " to " + this.targetType);
                }
                this.handle = MethodHandles.explicitCastArguments(this.handle, this.targetType);
                this.addExceptionHandler();
            }
            this.setGuards(this.args[0]);
            this.doCallSiteTargetSet();
        }
    }

    private static class InitSelector
    extends MethodSelector {
        private boolean beanConstructor;

        public InitSelector(MutableCallSite callSite, Class sender, String methodName, IndyInterface.CALL_TYPES callType, boolean safeNavigation, boolean thisCall, boolean spreadCall, Object[] arguments) {
            super(callSite, sender, methodName, callType, safeNavigation, thisCall, spreadCall, arguments);
        }

        @Override
        public boolean setInterceptor() {
            return false;
        }

        @Override
        public void getMetaClass() {
            Object receiver = this.args[0];
            this.mc = GroovySystem.getMetaClassRegistry().getMetaClass((Class)receiver);
        }

        @Override
        public void chooseMeta(MetaClassImpl mci) {
            MetaClassImpl.MetaConstructor mcon;
            if (mci == null) {
                return;
            }
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("getting constructor");
            }
            Object[] newArgs = Selector.removeRealReceiver(this.args);
            this.method = mci.retrieveConstructor(newArgs);
            if (this.method instanceof MetaClassImpl.MetaConstructor && (mcon = (MetaClassImpl.MetaConstructor)this.method).isBeanConstructor()) {
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("do beans constructor");
                }
                this.beanConstructor = true;
            }
        }

        /*
         * Unable to fully structure code
         */
        @Override
        public void setHandleForMetaMethod() {
            if (this.method == null) {
                return;
            }
            if (this.method instanceof MetaClassImpl.MetaConstructor) {
                if (IndyInterface.LOG_ENABLED) {
                    IndyInterface.LOG.info("meta method is MetaConstructor instance");
                }
                mc = (MetaClassImpl.MetaConstructor)this.method;
                this.isVargs = mc.isVargsMethod();
                con = mc.getCachedConstrcutor().cachedConstructor;
                try {
                    this.handle = IndyInterface.LOOKUP.unreflectConstructor(con);
                    if (!IndyInterface.LOG_ENABLED) ** GOTO lbl17
                    IndyInterface.LOG.info("successfully unreflected constructor");
                }
                catch (IllegalAccessException e) {
                    throw new GroovyBugError(e);
                }
            } else {
                super.setHandleForMetaMethod();
            }
lbl17:
            // 3 sources

            if (this.beanConstructor) {
                con = IndyGuardsFiltersAndSignatures.BEAN_CONSTRUCTOR_PROPERTY_SETTER.bindTo(this.mc);
                foldTargetType = MethodType.methodType(Object.class);
                if (this.args.length == 3) {
                    con = MethodHandles.dropArguments(con, 1, new Class[]{this.targetType.parameterType(1)});
                    foldTargetType = foldTargetType.insertParameterTypes(0, new Class[]{this.targetType.parameterType(1)});
                }
                this.handle = MethodHandles.foldArguments(con, this.handle.asType(foldTargetType));
            }
            if (this.method instanceof MetaClassImpl.MetaConstructor) {
                this.handle = MethodHandles.dropArguments(this.handle, 0, new Class[]{Class.class});
            }
        }

        @Override
        public void correctParameterLength() {
            if (this.beanConstructor) {
                return;
            }
            super.correctParameterLength();
        }

        @Override
        public void correctCoerce() {
            if (this.beanConstructor) {
                return;
            }
            super.correctCoerce();
        }

        @Override
        public void setMetaClassCallHandleIfNedded(boolean standardMetaClass) {
            if (this.handle != null) {
                return;
            }
            this.useMetaClass = true;
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("set meta class invocation path");
            }
            this.handle = IndyGuardsFiltersAndSignatures.MOP_INVOKE_CONSTRUCTOR.bindTo(this.mc);
            this.handle = this.handle.asCollector(Object[].class, this.targetType.parameterCount() - 1);
            this.handle = MethodHandles.dropArguments(this.handle, 0, Class.class);
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("create collector for arguments");
            }
        }
    }

    private static class PropertySelector
    extends MethodSelector {
        private boolean insertName = false;

        public PropertySelector(MutableCallSite callSite, Class sender, String methodName, IndyInterface.CALL_TYPES callType, boolean safeNavigation, boolean thisCall, boolean spreadCall, Object[] arguments) {
            super(callSite, sender, methodName, callType, safeNavigation, thisCall, spreadCall, arguments);
        }

        @Override
        public boolean setInterceptor() {
            return false;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public void chooseMeta(MetaClassImpl mci) {
            MetaProperty res;
            Object receiver = this.getCorrectedReceiver();
            if (receiver instanceof GroovyObject) {
                Class<?> aClass = receiver.getClass();
                Method reflectionMethod = null;
                try {
                    reflectionMethod = aClass.getMethod("getProperty", String.class);
                    if (!reflectionMethod.isSynthetic()) {
                        this.handle = MethodHandles.insertArguments(IndyGuardsFiltersAndSignatures.GROOVY_OBJECT_GET_PROPERTY, 1, this.name);
                        return;
                    }
                }
                catch (ReflectiveOperationException reflectiveOperationException) {}
            } else if (receiver instanceof Class) {
                this.handle = IndyGuardsFiltersAndSignatures.MOP_GET;
                this.handle = MethodHandles.insertArguments(this.handle, 2, this.name);
                this.handle = MethodHandles.insertArguments(this.handle, 0, this.mc);
                return;
            }
            if (this.method != null || mci == null) {
                return;
            }
            Class chosenSender = this.sender;
            if (mci.getTheClass() != chosenSender && GroovyCategorySupport.hasCategoryInCurrentThread()) {
                chosenSender = mci.getTheClass();
            }
            if ((res = mci.getEffectiveGetMetaProperty(chosenSender, receiver, this.name, false)) instanceof MethodMetaProperty) {
                MethodMetaProperty mmp = (MethodMetaProperty)res;
                this.method = mmp.getMetaMethod();
                this.insertName = true;
                return;
            } else if (res instanceof CachedField) {
                CachedField cf = (CachedField)res;
                Field f = cf.field;
                try {
                    this.handle = IndyInterface.LOOKUP.unreflectGetter(f);
                    if (!Modifier.isStatic(f.getModifiers())) return;
                    this.handle = IndyGuardsFiltersAndSignatures.META_PROPERTY_GETTER.bindTo(res);
                    return;
                }
                catch (IllegalAccessException iae) {
                    throw new GroovyBugError(iae);
                }
            } else {
                this.handle = IndyGuardsFiltersAndSignatures.META_PROPERTY_GETTER.bindTo(res);
            }
        }

        @Override
        public void setHandleForMetaMethod() {
            if (this.handle != null) {
                return;
            }
            super.setHandleForMetaMethod();
            if (this.handle != null && this.insertName && this.handle.type().parameterCount() == 2) {
                this.handle = MethodHandles.insertArguments(this.handle, 1, this.name);
            }
        }

        @Override
        public void setMetaClassCallHandleIfNedded(boolean standardMetaClass) {
            if (this.handle != null) {
                return;
            }
            this.useMetaClass = true;
            if (IndyInterface.LOG_ENABLED) {
                IndyInterface.LOG.info("set meta class invocation path for property get.");
            }
            this.handle = MethodHandles.insertArguments(IndyGuardsFiltersAndSignatures.MOP_GET, 2, this.name);
            this.handle = MethodHandles.insertArguments(this.handle, 0, this.mc);
        }
    }

    private static class CastSelector
    extends MethodSelector {
        private Class<?> staticSourceType;
        private Class<?> staticTargetType;

        public CastSelector(MutableCallSite callSite, Object[] arguments) {
            super(callSite, Selector.class, "", IndyInterface.CALL_TYPES.CAST, false, false, false, arguments);
            this.staticSourceType = callSite.type().parameterType(0);
            this.staticTargetType = callSite.type().returnType();
        }

        @Override
        public void setCallSiteTarget() {
            this.handleBoolean();
            this.handleNullWithoutBoolean();
            this.handleInstanceCase();
            this.handleCollections();
            this.handleSAM();
            this.castToTypeFallBack();
            if (!this.handle.type().equals((Object)this.callSite.type())) {
                this.castAndSetGuards();
            }
        }

        private void castAndSetGuards() {
            this.handle = MethodHandles.explicitCastArguments(this.handle, this.targetType);
            this.setGuards(this.args[0]);
            this.doCallSiteTargetSet();
        }

        private void handleNullWithoutBoolean() {
            if (this.handle != null || this.args[0] != null) {
                return;
            }
            if (this.staticTargetType.isPrimitive()) {
                this.handle = MethodHandles.insertArguments(IndyGuardsFiltersAndSignatures.GROOVY_CAST_EXCEPTION, 1, this.staticTargetType);
                this.castAndSetGuards();
            } else {
                this.handle = MethodHandles.identity(this.staticSourceType);
            }
        }

        private void handleInstanceCase() {
            if (this.handle != null) {
                return;
            }
            if (this.staticTargetType.isAssignableFrom(this.args[0].getClass())) {
                this.handle = MethodHandles.identity(this.staticSourceType);
            }
        }

        private static boolean isAbstractClassOf(Class toTest, Class givenOnCallSite) {
            if (!toTest.isAssignableFrom(givenOnCallSite)) {
                return false;
            }
            if (givenOnCallSite.isInterface()) {
                return true;
            }
            return Modifier.isAbstract(givenOnCallSite.getModifiers());
        }

        private void handleCollections() {
            if (this.handle != null) {
                return;
            }
            if (!(this.args[0] instanceof Collection)) {
                return;
            }
            if (CastSelector.isAbstractClassOf(HashSet.class, this.staticTargetType)) {
                this.handle = IndyGuardsFiltersAndSignatures.HASHSET_CONSTRUCTOR;
            } else if (CastSelector.isAbstractClassOf(ArrayList.class, this.staticTargetType)) {
                this.handle = IndyGuardsFiltersAndSignatures.ARRAYLIST_CONSTRUCTOR;
            }
        }

        private void handleSAM() {
            if (this.handle != null) {
                return;
            }
            if (!(this.args[0] instanceof Closure)) {
                return;
            }
            Method m = CachedSAMClass.getSAMMethod(this.staticTargetType);
            if (m == null) {
                return;
            }
            this.handle = MethodHandles.insertArguments(IndyGuardsFiltersAndSignatures.SAM_CONVERSION, 1, m, this.staticTargetType, this.staticTargetType.isInterface());
        }

        private void castToTypeFallBack() {
            if (this.handle != null) {
                return;
            }
            this.handle = MethodHandles.insertArguments(IndyGuardsFiltersAndSignatures.DTT_CAST_TO_TYPE, 1, this.staticTargetType);
        }

        private void handleBoolean() {
            boolean primitive;
            if (this.handle != null) {
                return;
            }
            boolean bl = primitive = this.staticTargetType == Boolean.TYPE;
            if (!primitive && this.staticTargetType != Boolean.class) {
                return;
            }
            if (this.args[0] == null) {
                if (primitive) {
                    this.handle = MethodHandles.constant(Boolean.TYPE, false);
                    this.handle = MethodHandles.dropArguments(this.handle, 0, this.staticSourceType);
                } else {
                    this.handle = IndyGuardsFiltersAndSignatures.BOOLEAN_IDENTITY;
                }
            } else if (this.args[0] instanceof Boolean) {
                this.handle = IndyGuardsFiltersAndSignatures.BOOLEAN_IDENTITY;
            } else {
                this.name = "asBoolean";
                super.setCallSiteTarget();
                return;
            }
        }
    }
}

