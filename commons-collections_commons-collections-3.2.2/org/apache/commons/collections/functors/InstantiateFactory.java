/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.functors.FunctorUtils;

public class InstantiateFactory
implements Factory,
Serializable {
    private static final long serialVersionUID = -7732226881069447957L;
    private final Class iClassToInstantiate;
    private final Class[] iParamTypes;
    private final Object[] iArgs;
    private transient Constructor iConstructor = null;
    static /* synthetic */ Class class$org$apache$commons$collections$functors$InstantiateFactory;

    public static Factory getInstance(Class classToInstantiate, Class[] paramTypes, Object[] args) {
        if (classToInstantiate == null) {
            throw new IllegalArgumentException("Class to instantiate must not be null");
        }
        if (paramTypes == null && args != null || paramTypes != null && args == null || paramTypes != null && args != null && paramTypes.length != args.length) {
            throw new IllegalArgumentException("Parameter types must match the arguments");
        }
        if (paramTypes == null || paramTypes.length == 0) {
            return new InstantiateFactory(classToInstantiate);
        }
        paramTypes = (Class[])paramTypes.clone();
        args = (Object[])args.clone();
        return new InstantiateFactory(classToInstantiate, paramTypes, args);
    }

    public InstantiateFactory(Class classToInstantiate) {
        this.iClassToInstantiate = classToInstantiate;
        this.iParamTypes = null;
        this.iArgs = null;
        this.findConstructor();
    }

    public InstantiateFactory(Class classToInstantiate, Class[] paramTypes, Object[] args) {
        this.iClassToInstantiate = classToInstantiate;
        this.iParamTypes = paramTypes;
        this.iArgs = args;
        this.findConstructor();
    }

    private void findConstructor() {
        try {
            this.iConstructor = this.iClassToInstantiate.getConstructor(this.iParamTypes);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("InstantiateFactory: The constructor must exist and be public ");
        }
    }

    public Object create() {
        if (this.iConstructor == null) {
            this.findConstructor();
        }
        try {
            return this.iConstructor.newInstance(this.iArgs);
        }
        catch (InstantiationException ex) {
            throw new FunctorException("InstantiateFactory: InstantiationException", ex);
        }
        catch (IllegalAccessException ex) {
            throw new FunctorException("InstantiateFactory: Constructor must be public", ex);
        }
        catch (InvocationTargetException ex) {
            throw new FunctorException("InstantiateFactory: Constructor threw an exception", ex);
        }
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$InstantiateFactory == null ? (class$org$apache$commons$collections$functors$InstantiateFactory = InstantiateFactory.class$("org.apache.commons.collections.functors.InstantiateFactory")) : class$org$apache$commons$collections$functors$InstantiateFactory);
        os.defaultWriteObject();
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$InstantiateFactory == null ? (class$org$apache$commons$collections$functors$InstantiateFactory = InstantiateFactory.class$("org.apache.commons.collections.functors.InstantiateFactory")) : class$org$apache$commons$collections$functors$InstantiateFactory);
        is.defaultReadObject();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

