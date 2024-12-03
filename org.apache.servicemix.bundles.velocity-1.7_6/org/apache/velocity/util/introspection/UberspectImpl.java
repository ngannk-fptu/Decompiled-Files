/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeLogger;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.RuntimeLoggerLog;
import org.apache.velocity.runtime.parser.node.AbstractExecutor;
import org.apache.velocity.runtime.parser.node.BooleanPropertyExecutor;
import org.apache.velocity.runtime.parser.node.GetExecutor;
import org.apache.velocity.runtime.parser.node.MapGetExecutor;
import org.apache.velocity.runtime.parser.node.MapSetExecutor;
import org.apache.velocity.runtime.parser.node.PropertyExecutor;
import org.apache.velocity.runtime.parser.node.PutExecutor;
import org.apache.velocity.runtime.parser.node.SetExecutor;
import org.apache.velocity.runtime.parser.node.SetPropertyExecutor;
import org.apache.velocity.util.ArrayIterator;
import org.apache.velocity.util.ArrayListWrapper;
import org.apache.velocity.util.EnumerationIterator;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.IntrospectionUtils;
import org.apache.velocity.util.introspection.Introspector;
import org.apache.velocity.util.introspection.Uberspect;
import org.apache.velocity.util.introspection.UberspectLoggable;
import org.apache.velocity.util.introspection.VelMethod;
import org.apache.velocity.util.introspection.VelPropertyGet;
import org.apache.velocity.util.introspection.VelPropertySet;

public class UberspectImpl
implements Uberspect,
UberspectLoggable {
    protected Log log;
    protected Introspector introspector;

    public void init() {
        this.introspector = new Introspector(this.log);
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public void setRuntimeLogger(RuntimeLogger runtimeLogger) {
        this.setLog(new RuntimeLoggerLog(runtimeLogger));
    }

    public Iterator getIterator(Object obj, Info i) throws Exception {
        if (obj.getClass().isArray()) {
            return new ArrayIterator(obj);
        }
        if (obj instanceof Collection) {
            return ((Collection)obj).iterator();
        }
        if (obj instanceof Map) {
            return ((Map)obj).values().iterator();
        }
        if (obj instanceof Iterator) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("The iterative object in the #foreach() loop at " + i + " is of type java.util.Iterator.  Because " + "it is not resettable, if used in more than once it " + "may lead to unexpected results.");
            }
            return (Iterator)obj;
        }
        if (obj instanceof Enumeration) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("The iterative object in the #foreach() loop at " + i + " is of type java.util.Enumeration.  Because " + "it is not resettable, if used in more than once it " + "may lead to unexpected results.");
            }
            return new EnumerationIterator((Enumeration)obj);
        }
        Class<?> type = obj.getClass();
        try {
            Method iter = type.getMethod("iterator", null);
            Class<?> returns = iter.getReturnType();
            if (Iterator.class.isAssignableFrom(returns)) {
                try {
                    return (Iterator)iter.invoke(obj, null);
                }
                catch (Exception e) {
                    throw new VelocityException("Error invoking the method 'iterator' on class '" + obj.getClass().getName() + "'", e);
                }
            }
            this.log.debug("iterator() method of reference in #foreach loop at " + i + " does not return a true Iterator.");
        }
        catch (NoSuchMethodException nsme) {
            // empty catch block
        }
        this.log.debug("Could not determine type of iterator in #foreach loop at " + i);
        return null;
    }

    public VelMethod getMethod(Object obj, String methodName, Object[] args, Info i) throws Exception {
        if (obj == null) {
            return null;
        }
        Method m = this.introspector.getMethod(obj.getClass(), methodName, args);
        if (m != null) {
            return new VelMethodImpl(m);
        }
        Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            m = this.introspector.getMethod(ArrayListWrapper.class, methodName, args);
            if (m != null) {
                return new VelMethodImpl(m, true);
            }
        } else if (cls == Class.class && (m = this.introspector.getMethod((Class)obj, methodName, args)) != null) {
            return new VelMethodImpl(m);
        }
        return null;
    }

    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i) throws Exception {
        if (obj == null) {
            return null;
        }
        Class<?> claz = obj.getClass();
        AbstractExecutor executor = new PropertyExecutor(this.log, this.introspector, claz, identifier);
        if (!executor.isAlive()) {
            executor = new MapGetExecutor(this.log, claz, identifier);
        }
        if (!executor.isAlive()) {
            executor = new GetExecutor(this.log, this.introspector, claz, identifier);
        }
        if (!executor.isAlive()) {
            executor = new BooleanPropertyExecutor(this.log, this.introspector, claz, identifier);
        }
        return executor.isAlive() ? new VelGetterImpl(executor) : null;
    }

    public VelPropertySet getPropertySet(Object obj, String identifier, Object arg, Info i) throws Exception {
        if (obj == null) {
            return null;
        }
        Class<?> claz = obj.getClass();
        SetExecutor executor = new SetPropertyExecutor(this.log, this.introspector, claz, identifier, arg);
        if (!executor.isAlive()) {
            executor = new MapSetExecutor(this.log, claz, identifier);
        }
        if (!executor.isAlive()) {
            executor = new PutExecutor(this.log, this.introspector, claz, arg, identifier);
        }
        return executor.isAlive() ? new VelSetterImpl(executor) : null;
    }

    public static class VelSetterImpl
    implements VelPropertySet {
        private final SetExecutor setExecutor;

        public VelSetterImpl(SetExecutor setExecutor) {
            this.setExecutor = setExecutor;
        }

        private VelSetterImpl() {
            this.setExecutor = null;
        }

        public Object invoke(Object o, Object value) throws Exception {
            return this.setExecutor.execute(o, value);
        }

        public boolean isCacheable() {
            return true;
        }

        public String getMethodName() {
            return this.setExecutor.isAlive() ? this.setExecutor.getMethod().getName() : null;
        }
    }

    public static class VelGetterImpl
    implements VelPropertyGet {
        final AbstractExecutor getExecutor;

        public VelGetterImpl(AbstractExecutor exec) {
            this.getExecutor = exec;
        }

        private VelGetterImpl() {
            this.getExecutor = null;
        }

        public Object invoke(Object o) throws Exception {
            return this.getExecutor.execute(o);
        }

        public boolean isCacheable() {
            return true;
        }

        public String getMethodName() {
            return this.getExecutor.isAlive() ? this.getExecutor.getMethod().getName() : null;
        }
    }

    public static class VelMethodImpl
    implements VelMethod {
        final Method method;
        Boolean isVarArg;
        boolean wrapArray;

        public VelMethodImpl(Method m) {
            this(m, false);
        }

        public VelMethodImpl(Method method, boolean wrapArray) {
            this.method = method;
            this.wrapArray = wrapArray;
        }

        private VelMethodImpl() {
            this.method = null;
        }

        public Object invoke(Object o, Object[] actual) throws Exception {
            Class<?>[] formal;
            int index;
            if (this.wrapArray) {
                o = new ArrayListWrapper(o);
            }
            if (this.isVarArg() && actual.length >= (index = (formal = this.method.getParameterTypes()).length - 1)) {
                Class<?> type = formal[index].getComponentType();
                actual = this.handleVarArg(type, index, actual);
            }
            return this.doInvoke(o, actual);
        }

        protected Object doInvoke(Object o, Object[] actual) throws Exception {
            return this.method.invoke(o, actual);
        }

        public boolean isVarArg() {
            if (this.isVarArg == null) {
                Class<?>[] formal = this.method.getParameterTypes();
                if (formal == null || formal.length == 0) {
                    this.isVarArg = Boolean.FALSE;
                } else {
                    Class<?> last = formal[formal.length - 1];
                    this.isVarArg = last.isArray();
                }
            }
            return this.isVarArg;
        }

        private Object[] handleVarArg(Class type, int index, Object[] actual) {
            if (actual.length == index) {
                Object[] newActual = new Object[actual.length + 1];
                System.arraycopy(actual, 0, newActual, 0, actual.length);
                newActual[index] = Array.newInstance(type, 0);
                actual = newActual;
            } else if (actual.length == index + 1 && actual[index] != null) {
                Class<?> argClass = actual[index].getClass();
                if (!argClass.isArray() && IntrospectionUtils.isMethodInvocationConvertible(type, argClass, false)) {
                    Object lastActual = Array.newInstance(type, 1);
                    Array.set(lastActual, 0, actual[index]);
                    actual[index] = lastActual;
                }
            } else if (actual.length > index + 1) {
                int size = actual.length - index;
                Object lastActual = Array.newInstance(type, size);
                for (int i = 0; i < size; ++i) {
                    Array.set(lastActual, i, actual[index + i]);
                }
                Object[] newActual = new Object[index + 1];
                for (int i = 0; i < index; ++i) {
                    newActual[i] = actual[i];
                }
                newActual[index] = lastActual;
                actual = newActual;
            }
            return actual;
        }

        public boolean isCacheable() {
            return true;
        }

        public String getMethodName() {
            return this.method.getName();
        }

        public Class getReturnType() {
            return this.method.getReturnType();
        }
    }
}

