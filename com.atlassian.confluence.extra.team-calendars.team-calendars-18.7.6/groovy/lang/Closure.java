/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyCallable;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MissingFieldException;
import groovy.lang.MissingPropertyException;
import groovy.lang.TrampolineClosure;
import groovy.lang.Writable;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.reflection.stdclasses.CachedClosureClass;
import org.codehaus.groovy.runtime.ComposedClosure;
import org.codehaus.groovy.runtime.CurriedClosure;
import org.codehaus.groovy.runtime.ExceptionUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.codehaus.groovy.runtime.callsite.BooleanClosureWrapper;
import org.codehaus.groovy.runtime.memoize.LRUCache;
import org.codehaus.groovy.runtime.memoize.Memoize;
import org.codehaus.groovy.runtime.memoize.UnlimitedConcurrentCache;

public abstract class Closure<V>
extends GroovyObjectSupport
implements Cloneable,
Runnable,
GroovyCallable<V>,
Serializable {
    public static final int OWNER_FIRST = 0;
    public static final int DELEGATE_FIRST = 1;
    public static final int OWNER_ONLY = 2;
    public static final int DELEGATE_ONLY = 3;
    public static final int TO_SELF = 4;
    public static final int DONE = 1;
    public static final int SKIP = 2;
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final Closure IDENTITY = new Closure<Object>(null){

        public Object doCall(Object args) {
            return args;
        }
    };
    private Object delegate;
    private Object owner;
    private Object thisObject;
    private int resolveStrategy = 0;
    private int directive;
    protected Class[] parameterTypes;
    protected int maximumNumberOfParameters;
    private static final long serialVersionUID = 4368710879820278874L;
    private BooleanClosureWrapper bcw;

    public Closure(Object owner, Object thisObject) {
        this.owner = owner;
        this.delegate = owner;
        this.thisObject = thisObject;
        CachedClosureClass cachedClass = (CachedClosureClass)ReflectionCache.getCachedClass(this.getClass());
        this.parameterTypes = cachedClass.getParameterTypes();
        this.maximumNumberOfParameters = cachedClass.getMaximumNumberOfParameters();
    }

    public Closure(Object owner) {
        this(owner, null);
    }

    public void setResolveStrategy(int resolveStrategy) {
        this.resolveStrategy = resolveStrategy;
    }

    public int getResolveStrategy() {
        return this.resolveStrategy;
    }

    public Object getThisObject() {
        return this.thisObject;
    }

    @Override
    public Object getProperty(String property) {
        if ("delegate".equals(property)) {
            return this.getDelegate();
        }
        if ("owner".equals(property)) {
            return this.getOwner();
        }
        if ("maximumNumberOfParameters".equals(property)) {
            return this.getMaximumNumberOfParameters();
        }
        if ("parameterTypes".equals(property)) {
            return this.getParameterTypes();
        }
        if ("metaClass".equals(property)) {
            return this.getMetaClass();
        }
        if ("class".equals(property)) {
            return this.getClass();
        }
        if ("directive".equals(property)) {
            return this.getDirective();
        }
        if ("resolveStrategy".equals(property)) {
            return this.getResolveStrategy();
        }
        if ("thisObject".equals(property)) {
            return this.getThisObject();
        }
        switch (this.resolveStrategy) {
            case 1: {
                return this.getPropertyDelegateFirst(property);
            }
            case 3: {
                return InvokerHelper.getProperty(this.delegate, property);
            }
            case 2: {
                return InvokerHelper.getProperty(this.owner, property);
            }
            case 4: {
                return super.getProperty(property);
            }
        }
        return this.getPropertyOwnerFirst(property);
    }

    private Object getPropertyDelegateFirst(String property) {
        if (this.delegate == null) {
            return this.getPropertyOwnerFirst(property);
        }
        return this.getPropertyTryThese(property, this.delegate, this.owner);
    }

    private Object getPropertyOwnerFirst(String property) {
        return this.getPropertyTryThese(property, this.owner, this.delegate);
    }

    private Object getPropertyTryThese(String property, Object firstTry, Object secondTry) {
        try {
            return InvokerHelper.getProperty(firstTry, property);
        }
        catch (MissingPropertyException e1) {
            if (secondTry != null && firstTry != this && firstTry != secondTry) {
                try {
                    return InvokerHelper.getProperty(secondTry, property);
                }
                catch (GroovyRuntimeException groovyRuntimeException) {
                    // empty catch block
                }
            }
            throw e1;
        }
        catch (MissingFieldException e2) {
            if (secondTry != null && firstTry != this && firstTry != secondTry) {
                try {
                    return InvokerHelper.getProperty(secondTry, property);
                }
                catch (GroovyRuntimeException groovyRuntimeException) {
                    // empty catch block
                }
            }
            throw e2;
        }
    }

    @Override
    public void setProperty(String property, Object newValue) {
        if ("delegate".equals(property)) {
            this.setDelegate(newValue);
        } else if ("metaClass".equals(property)) {
            this.setMetaClass((MetaClass)newValue);
        } else if ("resolveStrategy".equals(property)) {
            this.setResolveStrategy(((Number)newValue).intValue());
        } else if ("directive".equals(property)) {
            this.setDirective(((Number)newValue).intValue());
        } else {
            switch (this.resolveStrategy) {
                case 1: {
                    this.setPropertyDelegateFirst(property, newValue);
                    break;
                }
                case 3: {
                    InvokerHelper.setProperty(this.delegate, property, newValue);
                    break;
                }
                case 2: {
                    InvokerHelper.setProperty(this.owner, property, newValue);
                    break;
                }
                case 4: {
                    super.setProperty(property, newValue);
                    break;
                }
                default: {
                    this.setPropertyOwnerFirst(property, newValue);
                }
            }
        }
    }

    private void setPropertyDelegateFirst(String property, Object newValue) {
        if (this.delegate == null) {
            this.setPropertyOwnerFirst(property, newValue);
        } else {
            this.setPropertyTryThese(property, newValue, this.delegate, this.owner);
        }
    }

    private void setPropertyOwnerFirst(String property, Object newValue) {
        this.setPropertyTryThese(property, newValue, this.owner, this.delegate);
    }

    private void setPropertyTryThese(String property, Object newValue, Object firstTry, Object secondTry) {
        try {
            InvokerHelper.setProperty(firstTry, property, newValue);
        }
        catch (GroovyRuntimeException e1) {
            if (firstTry != null && firstTry != this && firstTry != secondTry) {
                try {
                    InvokerHelper.setProperty(secondTry, property, newValue);
                    return;
                }
                catch (GroovyRuntimeException groovyRuntimeException) {
                    // empty catch block
                }
            }
            throw e1;
        }
    }

    public boolean isCase(Object candidate) {
        if (this.bcw == null) {
            this.bcw = new BooleanClosureWrapper(this);
        }
        return this.bcw.call(candidate);
    }

    @Override
    public V call() {
        Object[] NOARGS = EMPTY_OBJECT_ARRAY;
        return this.call(NOARGS);
    }

    public V call(Object ... args) {
        try {
            return (V)this.getMetaClass().invokeMethod((Object)this, "doCall", args);
        }
        catch (InvokerInvocationException e) {
            ExceptionUtils.sneakyThrow(e.getCause());
            return null;
        }
        catch (Exception e) {
            return (V)Closure.throwRuntimeException(e);
        }
    }

    public V call(Object arguments) {
        return this.call(new Object[]{arguments});
    }

    protected static Object throwRuntimeException(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        throw new GroovyRuntimeException(throwable.getMessage(), throwable);
    }

    public Object getOwner() {
        return this.owner;
    }

    public Object getDelegate() {
        return this.delegate;
    }

    public void setDelegate(Object delegate) {
        this.delegate = delegate;
    }

    public Class[] getParameterTypes() {
        return this.parameterTypes;
    }

    public int getMaximumNumberOfParameters() {
        return this.maximumNumberOfParameters;
    }

    public Closure asWritable() {
        return new WritableClosure();
    }

    @Override
    public void run() {
        this.call();
    }

    public Closure<V> curry(Object ... arguments) {
        return new CurriedClosure(this, arguments);
    }

    public Closure<V> curry(Object argument) {
        return this.curry(new Object[]{argument});
    }

    public Closure<V> rcurry(Object ... arguments) {
        return new CurriedClosure(-arguments.length, this, arguments);
    }

    public Closure<V> rcurry(Object argument) {
        return this.rcurry(new Object[]{argument});
    }

    public Closure<V> ncurry(int n, Object ... arguments) {
        return new CurriedClosure(n, this, arguments);
    }

    public Closure<V> ncurry(int n, Object argument) {
        return this.ncurry(n, new Object[]{argument});
    }

    public <W> Closure<W> rightShift(Closure<W> other) {
        return new ComposedClosure<W>(this, other);
    }

    public Closure<V> leftShift(Closure other) {
        return new ComposedClosure(other, this);
    }

    public V leftShift(Object arg) {
        return this.call(arg);
    }

    public Closure<V> memoize() {
        return Memoize.buildMemoizeFunction(new UnlimitedConcurrentCache(), this);
    }

    public Closure<V> memoizeAtMost(int maxCacheSize) {
        if (maxCacheSize < 0) {
            throw new IllegalArgumentException("A non-negative number is required as the maxCacheSize parameter for memoizeAtMost.");
        }
        return Memoize.buildMemoizeFunction(new LRUCache(maxCacheSize), this);
    }

    public Closure<V> memoizeAtLeast(int protectedCacheSize) {
        if (protectedCacheSize < 0) {
            throw new IllegalArgumentException("A non-negative number is required as the protectedCacheSize parameter for memoizeAtLeast.");
        }
        return Memoize.buildSoftReferenceMemoizeFunction(protectedCacheSize, new UnlimitedConcurrentCache(), this);
    }

    public Closure<V> memoizeBetween(int protectedCacheSize, int maxCacheSize) {
        if (protectedCacheSize < 0) {
            throw new IllegalArgumentException("A non-negative number is required as the protectedCacheSize parameter for memoizeBetween.");
        }
        if (maxCacheSize < 0) {
            throw new IllegalArgumentException("A non-negative number is required as the maxCacheSize parameter for memoizeBetween.");
        }
        if (protectedCacheSize > maxCacheSize) {
            throw new IllegalArgumentException("The maxCacheSize parameter to memoizeBetween is required to be greater or equal to the protectedCacheSize parameter.");
        }
        return Memoize.buildSoftReferenceMemoizeFunction(protectedCacheSize, new LRUCache(maxCacheSize), this);
    }

    public Closure<V> trampoline(Object ... args) {
        return new TrampolineClosure<V>(this.curry(args));
    }

    public Closure<V> trampoline() {
        return new TrampolineClosure(this);
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public int getDirective() {
        return this.directive;
    }

    public void setDirective(int directive) {
        this.directive = directive;
    }

    public Closure<V> dehydrate() {
        Closure result = (Closure)this.clone();
        result.delegate = null;
        result.owner = null;
        result.thisObject = null;
        return result;
    }

    public Closure<V> rehydrate(Object delegate, Object owner, Object thisObject) {
        Closure result = (Closure)this.clone();
        result.delegate = delegate;
        result.owner = owner;
        result.thisObject = thisObject;
        return result;
    }

    private class WritableClosure
    extends Closure
    implements Writable {
        public WritableClosure() {
            super(Closure.this);
        }

        @Override
        public Writer writeTo(Writer out) throws IOException {
            Closure.this.call(new Object[]{out});
            return out;
        }

        @Override
        public Object invokeMethod(String method, Object arguments) {
            if ("clone".equals(method)) {
                return this.clone();
            }
            if ("curry".equals(method)) {
                return this.curry((Object[])arguments);
            }
            if ("asWritable".equals(method)) {
                return this.asWritable();
            }
            return Closure.this.invokeMethod(method, arguments);
        }

        @Override
        public Object getProperty(String property) {
            return Closure.this.getProperty(property);
        }

        @Override
        public void setProperty(String property, Object newValue) {
            Closure.this.setProperty(property, newValue);
        }

        @Override
        public Object call() {
            return ((Closure)this.getOwner()).call();
        }

        public Object call(Object arguments) {
            return ((Closure)this.getOwner()).call(arguments);
        }

        public Object call(Object ... args) {
            return ((Closure)this.getOwner()).call(args);
        }

        public Object doCall(Object ... args) {
            return this.call(args);
        }

        @Override
        public Object getDelegate() {
            return Closure.this.getDelegate();
        }

        @Override
        public void setDelegate(Object delegate) {
            Closure.this.setDelegate(delegate);
        }

        @Override
        public Class[] getParameterTypes() {
            return Closure.this.getParameterTypes();
        }

        @Override
        public int getMaximumNumberOfParameters() {
            return Closure.this.getMaximumNumberOfParameters();
        }

        @Override
        public Closure asWritable() {
            return this;
        }

        @Override
        public void run() {
            Closure.this.run();
        }

        @Override
        public Object clone() {
            return ((Closure)Closure.this.clone()).asWritable();
        }

        public int hashCode() {
            return Closure.this.hashCode();
        }

        public boolean equals(Object arg0) {
            return Closure.this.equals(arg0);
        }

        public String toString() {
            StringWriter writer = new StringWriter();
            try {
                this.writeTo(writer);
            }
            catch (IOException e) {
                return null;
            }
            return writer.toString();
        }

        public Closure curry(Object ... arguments) {
            return new CurriedClosure(this, arguments).asWritable();
        }

        @Override
        public void setResolveStrategy(int resolveStrategy) {
            Closure.this.setResolveStrategy(resolveStrategy);
        }

        @Override
        public int getResolveStrategy() {
            return Closure.this.getResolveStrategy();
        }
    }
}

