/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.functors.ConstantFactory;
import org.apache.commons.collections.functors.FunctorUtils;
import org.apache.commons.collections.functors.InstantiateFactory;

public class PrototypeFactory {
    static /* synthetic */ Class class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeCloneFactory;
    static /* synthetic */ Class class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeSerializationFactory;

    public static Factory getInstance(Object prototype) {
        if (prototype == null) {
            return ConstantFactory.NULL_INSTANCE;
        }
        try {
            Method method = prototype.getClass().getMethod("clone", null);
            return new PrototypeCloneFactory(prototype, method);
        }
        catch (NoSuchMethodException ex) {
            try {
                prototype.getClass().getConstructor(prototype.getClass());
                return new InstantiateFactory(prototype.getClass(), new Class[]{prototype.getClass()}, new Object[]{prototype});
            }
            catch (NoSuchMethodException ex2) {
                if (prototype instanceof Serializable) {
                    return new PrototypeSerializationFactory((Serializable)prototype);
                }
                throw new IllegalArgumentException("The prototype must be cloneable via a public clone method");
            }
        }
    }

    private PrototypeFactory() {
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static class PrototypeSerializationFactory
    implements Factory,
    Serializable {
        private static final long serialVersionUID = -8704966966139178833L;
        private final Serializable iPrototype;

        private PrototypeSerializationFactory(Serializable prototype) {
            this.iPrototype = prototype;
        }

        public Object create() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            ByteArrayInputStream bais = null;
            try {
                ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(this.iPrototype);
                bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream in = new ObjectInputStream(bais);
                Object object = in.readObject();
                return object;
            }
            catch (ClassNotFoundException ex) {
                throw new FunctorException(ex);
            }
            catch (IOException ex) {
                throw new FunctorException(ex);
            }
            finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                }
                catch (IOException ex) {}
                try {
                    if (baos != null) {
                        baos.close();
                    }
                }
                catch (IOException ex) {}
            }
        }

        private void writeObject(ObjectOutputStream os) throws IOException {
            FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeSerializationFactory == null ? (class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeSerializationFactory = PrototypeFactory.class$("org.apache.commons.collections.functors.PrototypeFactory$PrototypeSerializationFactory")) : class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeSerializationFactory);
            os.defaultWriteObject();
        }

        private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
            FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeSerializationFactory == null ? (class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeSerializationFactory = PrototypeFactory.class$("org.apache.commons.collections.functors.PrototypeFactory$PrototypeSerializationFactory")) : class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeSerializationFactory);
            is.defaultReadObject();
        }
    }

    static class PrototypeCloneFactory
    implements Factory,
    Serializable {
        private static final long serialVersionUID = 5604271422565175555L;
        private final Object iPrototype;
        private transient Method iCloneMethod;

        private PrototypeCloneFactory(Object prototype, Method method) {
            this.iPrototype = prototype;
            this.iCloneMethod = method;
        }

        private void findCloneMethod() {
            try {
                this.iCloneMethod = this.iPrototype.getClass().getMethod("clone", null);
            }
            catch (NoSuchMethodException ex) {
                throw new IllegalArgumentException("PrototypeCloneFactory: The clone method must exist and be public ");
            }
        }

        public Object create() {
            if (this.iCloneMethod == null) {
                this.findCloneMethod();
            }
            try {
                return this.iCloneMethod.invoke(this.iPrototype, (Object[])null);
            }
            catch (IllegalAccessException ex) {
                throw new FunctorException("PrototypeCloneFactory: Clone method must be public", ex);
            }
            catch (InvocationTargetException ex) {
                throw new FunctorException("PrototypeCloneFactory: Clone method threw an exception", ex);
            }
        }

        private void writeObject(ObjectOutputStream os) throws IOException {
            FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeCloneFactory == null ? (class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeCloneFactory = PrototypeFactory.class$("org.apache.commons.collections.functors.PrototypeFactory$PrototypeCloneFactory")) : class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeCloneFactory);
            os.defaultWriteObject();
        }

        private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
            FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeCloneFactory == null ? (class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeCloneFactory = PrototypeFactory.class$("org.apache.commons.collections.functors.PrototypeFactory$PrototypeCloneFactory")) : class$org$apache$commons$collections$functors$PrototypeFactory$PrototypeCloneFactory);
            is.defaultReadObject();
        }
    }
}

