/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.configuration;

import java.io.Serializable;
import javax.cache.configuration.Factory;

public final class FactoryBuilder {
    private FactoryBuilder() {
    }

    public static <T> Factory<T> factoryOf(Class<T> clazz) {
        return new ClassFactory<T>(clazz);
    }

    public static <T> Factory<T> factoryOf(String className) {
        return new ClassFactory(className);
    }

    public static <T extends Serializable> Factory<T> factoryOf(T instance) {
        return new SingletonFactory<T>(instance);
    }

    public static class SingletonFactory<T>
    implements Factory<T>,
    Serializable {
        public static final long serialVersionUID = 201305101634L;
        private T instance;

        public SingletonFactory(T instance) {
            this.instance = instance;
        }

        @Override
        public T create() {
            return this.instance;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            SingletonFactory that = (SingletonFactory)other;
            return this.instance.equals(that.instance);
        }

        public int hashCode() {
            return this.instance.hashCode();
        }
    }

    public static class ClassFactory<T>
    implements Factory<T>,
    Serializable {
        public static final long serialVersionUID = 201305101626L;
        private String className;

        public ClassFactory(Class<T> clazz) {
            this.className = clazz.getName();
        }

        public ClassFactory(String className) {
            this.className = className;
        }

        @Override
        public T create() {
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                Class<?> clazz = loader.loadClass(this.className);
                return (T)clazz.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to create an instance of " + this.className, e);
            }
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            ClassFactory that = (ClassFactory)other;
            return this.className.equals(that.className);
        }

        public int hashCode() {
            return this.className.hashCode();
        }
    }
}

