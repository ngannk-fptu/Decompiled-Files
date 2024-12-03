/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaBeanProperty;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.ProxyMetaClass;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ParameterTypes;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.PogoMetaClassSite;
import org.codehaus.groovy.runtime.wrappers.Wrapper;
import org.codehaus.groovy.util.FastArray;

public final class ClosureMetaClass
extends MetaClassImpl {
    private volatile boolean initialized;
    private final FastArray closureMethods = new FastArray(3);
    private Map<String, CachedField> attributes = new HashMap<String, CachedField>();
    private MethodChooser chooser;
    private volatile boolean attributeInitDone = false;
    private static MetaClassImpl CLOSURE_METACLASS;
    private static MetaClassImpl classMetaClass;
    private static final Object[] EMPTY_ARGUMENTS;
    private static final String CLOSURE_CALL_METHOD = "call";
    private static final String CLOSURE_DO_CALL_METHOD = "doCall";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void resetCachedMetaClasses() {
        MetaClassImpl temp = new MetaClassImpl(Closure.class);
        temp.initialize();
        Class<ClosureMetaClass> clazz = ClosureMetaClass.class;
        synchronized (ClosureMetaClass.class) {
            CLOSURE_METACLASS = temp;
            // ** MonitorExit[var1_1] (shouldn't be in output)
            if (classMetaClass == null) return;
            temp = new MetaClassImpl(Class.class);
            temp.initialize();
            clazz = ClosureMetaClass.class;
            synchronized (ClosureMetaClass.class) {
                classMetaClass = temp;
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
        }
    }

    private static synchronized MetaClass getStaticMetaClass() {
        if (classMetaClass == null) {
            classMetaClass = new MetaClassImpl(Class.class);
            classMetaClass.initialize();
        }
        return classMetaClass;
    }

    public ClosureMetaClass(MetaClassRegistry registry, Class theClass) {
        super(registry, theClass);
    }

    @Override
    public MetaProperty getMetaProperty(String name) {
        return CLOSURE_METACLASS.getMetaProperty(name);
    }

    private static void unwrap(Object[] arguments) {
        for (int i = 0; i != arguments.length; ++i) {
            if (!(arguments[i] instanceof Wrapper)) continue;
            arguments[i] = ((Wrapper)arguments[i]).unwrap();
        }
    }

    private MetaMethod pickClosureMethod(Class[] argClasses) {
        Object answer = this.chooser.chooseMethod(argClasses, false);
        return (MetaMethod)answer;
    }

    private MetaMethod getDelegateMethod(Closure closure, Object delegate, String methodName, Class[] argClasses) {
        if (delegate == closure || delegate == null) {
            return null;
        }
        if (delegate instanceof Class) {
            for (Class superClass = (Class)delegate; superClass != Object.class && superClass != null; superClass = superClass.getSuperclass()) {
                MetaClass mc = this.registry.getMetaClass(superClass);
                MetaMethod method = mc.getStaticMetaMethod(methodName, argClasses);
                if (method == null) continue;
                return method;
            }
            return null;
        }
        MetaClass delegateMetaClass = this.lookupObjectMetaClass(delegate);
        MetaMethod method = delegateMetaClass.pickMethod(methodName, argClasses);
        if (method != null) {
            return method;
        }
        if (delegateMetaClass instanceof ExpandoMetaClass && (method = ((ExpandoMetaClass)delegateMetaClass).findMixinMethod(methodName, argClasses)) != null) {
            this.onMixinMethodFound(method);
            return method;
        }
        if (delegateMetaClass instanceof MetaClassImpl && (method = MetaClassImpl.findMethodInClassHierarchy(this.getTheClass(), methodName, argClasses, this)) != null) {
            this.onSuperMethodFoundInHierarchy(method);
            return method;
        }
        return method;
    }

    @Override
    public Object invokeMethod(Class sender, Object object, String methodName, Object[] originalArguments, boolean isCallToSuper, boolean fromInsideClass) {
        boolean shouldDefer;
        this.checkInitalised();
        if (object == null) {
            throw new NullPointerException("Cannot invoke method: " + methodName + " on null object");
        }
        Object[] arguments = ClosureMetaClass.makeArguments(originalArguments, methodName);
        Class[] argClasses = MetaClassHelper.convertToTypeArray(arguments);
        ClosureMetaClass.unwrap(arguments);
        MetaMethod method = null;
        Closure closure = (Closure)object;
        if (CLOSURE_DO_CALL_METHOD.equals(methodName) || CLOSURE_CALL_METHOD.equals(methodName)) {
            method = this.pickClosureMethod(argClasses);
            if (method == null && arguments.length == 1 && arguments[0] instanceof List) {
                Object[] newArguments = ((List)arguments[0]).toArray();
                Class[] newArgClasses = MetaClassHelper.convertToTypeArray(newArguments);
                method = this.createTransformMetaMethod(this.pickClosureMethod(newArgClasses));
            }
            if (method == null) {
                throw new MissingMethodException(methodName, this.theClass, arguments, false);
            }
        }
        boolean bl = shouldDefer = closure.getResolveStrategy() == 3 && ClosureMetaClass.isInternalMethod(methodName);
        if (method == null && !shouldDefer) {
            method = CLOSURE_METACLASS.pickMethod(methodName, argClasses);
        }
        if (method != null) {
            return method.doMethodInvoke(object, arguments);
        }
        MissingMethodException last = null;
        Object callObject = object;
        Object owner = closure.getOwner();
        Object delegate = closure.getDelegate();
        Object thisObject = closure.getThisObject();
        int resolveStrategy = closure.getResolveStrategy();
        boolean invokeOnDelegate = false;
        boolean invokeOnOwner = false;
        boolean ownerFirst = true;
        switch (resolveStrategy) {
            case 4: {
                break;
            }
            case 3: {
                method = this.getDelegateMethod(closure, delegate, methodName, argClasses);
                callObject = delegate;
                if (method != null) break;
                invokeOnDelegate = delegate != closure && delegate instanceof GroovyObject;
                break;
            }
            case 2: {
                method = this.getDelegateMethod(closure, owner, methodName, argClasses);
                callObject = owner;
                if (method != null) break;
                invokeOnOwner = owner != closure && owner instanceof GroovyObject;
                break;
            }
            case 1: {
                method = this.getDelegateMethod(closure, delegate, methodName, argClasses);
                callObject = delegate;
                if (method == null) {
                    method = this.getDelegateMethod(closure, owner, methodName, argClasses);
                    callObject = owner;
                }
                if (method != null) break;
                invokeOnDelegate = delegate != closure && delegate instanceof GroovyObject;
                invokeOnOwner = owner != closure && owner instanceof GroovyObject;
                ownerFirst = false;
                break;
            }
            default: {
                method = this.getDelegateMethod(closure, thisObject, methodName, argClasses);
                callObject = thisObject;
                if (method == null) {
                    LinkedList<Closure> list = new LinkedList<Closure>();
                    Object current = closure;
                    while (current != thisObject && current instanceof Closure) {
                        Object currentClosure = current;
                        if (((Closure)currentClosure).getDelegate() != null) {
                            list.add((Closure)current);
                        }
                        current = ((Closure)currentClosure).getOwner();
                    }
                    while (!list.isEmpty() && method == null) {
                        Closure closureWithDelegate = (Closure)list.removeLast();
                        Object currentDelegate = closureWithDelegate.getDelegate();
                        method = this.getDelegateMethod(closureWithDelegate, currentDelegate, methodName, argClasses);
                        callObject = currentDelegate;
                    }
                }
                if (method != null) break;
                invokeOnDelegate = delegate != closure && delegate instanceof GroovyObject;
                boolean bl2 = invokeOnOwner = owner != closure && owner instanceof GroovyObject;
            }
        }
        if (method == null && (invokeOnOwner || invokeOnDelegate)) {
            try {
                if (ownerFirst) {
                    return ClosureMetaClass.invokeOnDelegationObjects(invokeOnOwner, owner, invokeOnDelegate, delegate, methodName, arguments);
                }
                return ClosureMetaClass.invokeOnDelegationObjects(invokeOnDelegate, delegate, invokeOnOwner, owner, methodName, arguments);
            }
            catch (MissingMethodException mme) {
                last = mme;
            }
        }
        if (method != null) {
            MetaClass metaClass = this.registry.getMetaClass(callObject.getClass());
            if (metaClass instanceof ProxyMetaClass) {
                return metaClass.invokeMethod(callObject, methodName, arguments);
            }
            return method.doMethodInvoke(callObject, arguments);
        }
        Object value = null;
        try {
            value = this.getProperty(object, methodName);
        }
        catch (MissingPropertyException closureWithDelegate) {
            // empty catch block
        }
        if (value instanceof Closure) {
            Closure cl = (Closure)value;
            MetaClass delegateMetaClass = cl.getMetaClass();
            return delegateMetaClass.invokeMethod(cl.getClass(), closure, CLOSURE_DO_CALL_METHOD, originalArguments, false, fromInsideClass);
        }
        if (last != null) {
            throw last;
        }
        throw new MissingMethodException(methodName, this.theClass, arguments, false);
    }

    private static boolean isInternalMethod(String methodName) {
        return methodName.equals("curry") || methodName.equals("ncurry") || methodName.equals("rcurry") || methodName.equals("leftShift") || methodName.equals("rightShift");
    }

    private static Object[] makeArguments(Object[] arguments, String methodName) {
        if (arguments == null) {
            return EMPTY_ARGUMENTS;
        }
        return arguments;
    }

    private static Throwable unwrap(GroovyRuntimeException gre) {
        Throwable th = gre;
        if (th.getCause() != null && th.getCause() != gre) {
            th = th.getCause();
        }
        if (th != gre && th instanceof GroovyRuntimeException) {
            return ClosureMetaClass.unwrap(th);
        }
        return th;
    }

    private static Object invokeOnDelegationObjects(boolean invoke1, Object o1, boolean invoke2, Object o2, String methodName, Object[] args) {
        Throwable th;
        GroovyObject go;
        MissingMethodException first = null;
        if (invoke1) {
            go = (GroovyObject)o1;
            try {
                return go.invokeMethod(methodName, args);
            }
            catch (MissingMethodException mme) {
                first = mme;
            }
            catch (GroovyRuntimeException gre) {
                th = ClosureMetaClass.unwrap(gre);
                if (th instanceof MissingMethodException && methodName.equals(((MissingMethodException)th).getMethod())) {
                    first = (MissingMethodException)th;
                }
                throw gre;
            }
        }
        if (invoke2 && (!invoke1 || o1 != o2)) {
            go = (GroovyObject)o2;
            try {
                return go.invokeMethod(methodName, args);
            }
            catch (MissingMethodException mme) {
                if (first == null) {
                    first = mme;
                }
            }
            catch (GroovyRuntimeException gre) {
                th = ClosureMetaClass.unwrap(gre);
                if (th instanceof MissingMethodException) {
                    first = (MissingMethodException)th;
                }
                throw gre;
            }
        }
        throw first;
    }

    private synchronized void initAttributes() {
        CachedField[] fieldArray;
        if (!this.attributes.isEmpty()) {
            return;
        }
        this.attributes.put("!", null);
        for (CachedField aFieldArray : fieldArray = this.theCachedClass.getFields()) {
            this.attributes.put(aFieldArray.getName(), aFieldArray);
        }
        this.attributeInitDone = !this.attributes.isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void initialize() {
        if (!this.isInitialized()) {
            CachedMethod[] methodArray = this.theCachedClass.getMethods();
            CachedClass cachedClass = this.theCachedClass;
            synchronized (cachedClass) {
                for (CachedMethod cachedMethod : methodArray) {
                    if (!cachedMethod.getName().equals(CLOSURE_DO_CALL_METHOD)) continue;
                    this.closureMethods.add(cachedMethod);
                }
            }
            this.assignMethodChooser();
            this.initialized = true;
        }
    }

    private void assignMethodChooser() {
        if (this.closureMethods.size() == 1) {
            final MetaMethod doCall = (MetaMethod)this.closureMethods.get(0);
            final CachedClass[] c = doCall.getParameterTypes();
            int length = c.length;
            if (length == 0) {
                this.chooser = new MethodChooser(){

                    @Override
                    public Object chooseMethod(Class[] arguments, boolean coerce) {
                        if (arguments.length == 0) {
                            return doCall;
                        }
                        return null;
                    }
                };
            } else if (length == 1 && c[0].getTheClass() == Object.class) {
                this.chooser = new MethodChooser(){

                    @Override
                    public Object chooseMethod(Class[] arguments, boolean coerce) {
                        if (arguments.length < 2) {
                            return doCall;
                        }
                        return null;
                    }
                };
            } else {
                boolean allObject = true;
                for (int i = 0; i < c.length - 1; ++i) {
                    if (c[i].getTheClass() == Object.class) continue;
                    allObject = false;
                    break;
                }
                if (allObject && c[c.length - 1].getTheClass() == Object.class) {
                    this.chooser = new MethodChooser(){

                        @Override
                        public Object chooseMethod(Class[] arguments, boolean coerce) {
                            if (arguments.length == c.length) {
                                return doCall;
                            }
                            return null;
                        }
                    };
                } else if (allObject && c[c.length - 1].getTheClass() == Object[].class) {
                    final int minimumLength = c.length - 2;
                    this.chooser = new MethodChooser(){

                        @Override
                        public Object chooseMethod(Class[] arguments, boolean coerce) {
                            if (arguments.length > minimumLength) {
                                return doCall;
                            }
                            return null;
                        }
                    };
                } else {
                    this.chooser = new MethodChooser(){

                        @Override
                        public Object chooseMethod(Class[] arguments, boolean coerce) {
                            if (doCall.isValidMethod(arguments)) {
                                return doCall;
                            }
                            return null;
                        }
                    };
                }
            }
        } else if (this.closureMethods.size() == 2) {
            MetaMethod m0 = null;
            MetaMethod m1 = null;
            for (int i = 0; i != this.closureMethods.size(); ++i) {
                MetaMethod m = (MetaMethod)this.closureMethods.get(i);
                CachedClass[] c = m.getParameterTypes();
                if (c.length == 0) {
                    m0 = m;
                    continue;
                }
                if (c.length != 1 || c[0].getTheClass() != Object.class) continue;
                m1 = m;
            }
            if (m0 != null && m1 != null) {
                this.chooser = new StandardClosureChooser(m0, m1);
            }
        }
        if (this.chooser == null) {
            this.chooser = new NormalMethodChooser(this.theClass, this.closureMethods);
        }
    }

    private MetaClass lookupObjectMetaClass(Object object) {
        if (object instanceof GroovyObject) {
            GroovyObject go = (GroovyObject)object;
            return go.getMetaClass();
        }
        Class ownerClass = object.getClass();
        if (ownerClass == Class.class) {
            ownerClass = (Class)object;
            return this.registry.getMetaClass(ownerClass);
        }
        MetaClass metaClass = InvokerHelper.getMetaClass(object);
        return metaClass;
    }

    @Override
    public List<MetaMethod> getMethods() {
        List<MetaMethod> answer = CLOSURE_METACLASS.getMetaMethods();
        answer.addAll(this.closureMethods.toList());
        return answer;
    }

    @Override
    public List<MetaMethod> getMetaMethods() {
        return CLOSURE_METACLASS.getMetaMethods();
    }

    @Override
    public List<MetaProperty> getProperties() {
        return CLOSURE_METACLASS.getProperties();
    }

    @Override
    public MetaMethod pickMethod(String name, Class[] argTypes) {
        if (argTypes == null) {
            argTypes = MetaClassHelper.EMPTY_CLASS_ARRAY;
        }
        if (name.equals(CLOSURE_CALL_METHOD) || name.equals(CLOSURE_DO_CALL_METHOD)) {
            return this.pickClosureMethod(argTypes);
        }
        return CLOSURE_METACLASS.getMetaMethod(name, argTypes);
    }

    public MetaMethod retrieveStaticMethod(String methodName, Class[] arguments) {
        return null;
    }

    @Override
    protected boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public MetaMethod getStaticMetaMethod(String name, Object[] args) {
        return CLOSURE_METACLASS.getStaticMetaMethod(name, args);
    }

    public MetaMethod getStaticMetaMethod(String name, Class[] argTypes) {
        return CLOSURE_METACLASS.getStaticMetaMethod(name, argTypes);
    }

    @Override
    public Object getProperty(Class sender, Object object, String name, boolean useSuper, boolean fromInsideClass) {
        if (object instanceof Class) {
            return ClosureMetaClass.getStaticMetaClass().getProperty(sender, object, name, useSuper, fromInsideClass);
        }
        return CLOSURE_METACLASS.getProperty(sender, object, name, useSuper, fromInsideClass);
    }

    @Override
    public Object getAttribute(Class sender, Object object, String attribute, boolean useSuper, boolean fromInsideClass) {
        CachedField mfp;
        if (object instanceof Class) {
            return ClosureMetaClass.getStaticMetaClass().getAttribute(sender, object, attribute, useSuper);
        }
        if (!this.attributeInitDone) {
            this.initAttributes();
        }
        if ((mfp = this.attributes.get(attribute)) == null) {
            return CLOSURE_METACLASS.getAttribute(sender, object, attribute, useSuper);
        }
        return mfp.getProperty(object);
    }

    @Override
    public void setAttribute(Class sender, Object object, String attribute, Object newValue, boolean useSuper, boolean fromInsideClass) {
        if (object instanceof Class) {
            ClosureMetaClass.getStaticMetaClass().setAttribute(sender, object, attribute, newValue, useSuper, fromInsideClass);
        } else {
            CachedField mfp;
            if (!this.attributeInitDone) {
                this.initAttributes();
            }
            if ((mfp = this.attributes.get(attribute)) == null) {
                CLOSURE_METACLASS.setAttribute(sender, object, attribute, newValue, useSuper, fromInsideClass);
            } else {
                mfp.setProperty(object, newValue);
            }
        }
    }

    @Override
    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        return ClosureMetaClass.getStaticMetaClass().invokeMethod(Class.class, object, methodName, arguments, false, false);
    }

    @Override
    public void setProperty(Class sender, Object object, String name, Object newValue, boolean useSuper, boolean fromInsideClass) {
        if (object instanceof Class) {
            ClosureMetaClass.getStaticMetaClass().setProperty(sender, object, name, newValue, useSuper, fromInsideClass);
        } else {
            CLOSURE_METACLASS.setProperty(sender, object, name, newValue, useSuper, fromInsideClass);
        }
    }

    public MetaMethod getMethodWithoutCaching(int index, Class sender, String methodName, Class[] arguments, boolean isCallToSuper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperties(Object bean, Map map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMetaBeanProperty(MetaBeanProperty mp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMetaMethod(MetaMethod method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addNewInstanceMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addNewStaticMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Constructor retrieveConstructor(Class[] arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CallSite createPojoCallSite(CallSite site, Object receiver, Object[] args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CallSite createPogoCallSite(CallSite site, Object[] args) {
        return new PogoMetaClassSite(site, this);
    }

    @Override
    public CallSite createPogoCallCurrentSite(CallSite site, Class sender, Object[] args) {
        return new PogoMetaClassSite(site, this);
    }

    @Override
    public List respondsTo(Object obj, String name, Object[] argTypes) {
        this.loadMetaInfo();
        return super.respondsTo(obj, name, argTypes);
    }

    @Override
    public List respondsTo(Object obj, String name) {
        this.loadMetaInfo();
        return super.respondsTo(obj, name);
    }

    private synchronized void loadMetaInfo() {
        if (this.metaMethodIndex.isEmpty()) {
            this.initialized = false;
            super.initialize();
            this.initialized = true;
        }
    }

    @Override
    protected void applyPropertyDescriptors(PropertyDescriptor[] propertyDescriptors) {
    }

    static {
        EMPTY_ARGUMENTS = new Object[0];
        ClosureMetaClass.resetCachedMetaClasses();
    }

    private static class NormalMethodChooser
    implements MethodChooser {
        private final FastArray methods;
        final Class theClass;

        NormalMethodChooser(Class theClass, FastArray methods) {
            this.theClass = theClass;
            this.methods = methods;
        }

        @Override
        public Object chooseMethod(Class[] arguments, boolean coerce) {
            if (arguments.length == 0) {
                return MetaClassHelper.chooseEmptyMethodParams(this.methods);
            }
            if (arguments.length == 1 && arguments[0] == null) {
                return MetaClassHelper.chooseMostGeneralMethodWith1NullParam(this.methods);
            }
            ArrayList<Object> matchingMethods = new ArrayList<Object>();
            Object[] data = this.methods.getArray();
            int len = this.methods.size();
            for (int i = 0; i != len; ++i) {
                Object method = data[i];
                if (!((ParameterTypes)method).isValidMethod(arguments)) continue;
                matchingMethods.add(method);
            }
            int size = matchingMethods.size();
            if (0 == size) {
                return null;
            }
            if (1 == size) {
                return matchingMethods.get(0);
            }
            return this.chooseMostSpecificParams(ClosureMetaClass.CLOSURE_DO_CALL_METHOD, matchingMethods, arguments);
        }

        private Object chooseMostSpecificParams(String name, List matchingMethods, Class[] arguments) {
            return ClosureMetaClass.doChooseMostSpecificParams(this.theClass.getName(), name, matchingMethods, arguments, true);
        }
    }

    private static class StandardClosureChooser
    implements MethodChooser {
        private final MetaMethod doCall0;
        private final MetaMethod doCall1;

        StandardClosureChooser(MetaMethod m0, MetaMethod m1) {
            this.doCall0 = m0;
            this.doCall1 = m1;
        }

        @Override
        public Object chooseMethod(Class[] arguments, boolean coerce) {
            if (arguments.length == 0) {
                return this.doCall0;
            }
            if (arguments.length == 1) {
                return this.doCall1;
            }
            return null;
        }
    }

    private static interface MethodChooser {
        public Object chooseMethod(Class[] var1, boolean var2);
    }
}

