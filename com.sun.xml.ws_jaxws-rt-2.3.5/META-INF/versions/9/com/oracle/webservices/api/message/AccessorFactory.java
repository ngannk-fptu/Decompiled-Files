/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.oracle.webservices.api.message;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.PropertySet;
import com.oracle.webservices.api.message.ReadOnlyPropertyException;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
final class AccessorFactory {
    private AccessorFactory() {
    }

    static MethodHandles.Lookup createPrivateLookup(Class clazz, MethodHandles.Lookup lookup) throws IllegalAccessException {
        return AccessorFactory.class.getModule() == clazz.getModule() ? MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()) : MethodHandles.privateLookupIn(clazz, lookup);
    }

    static BasePropertySet.Accessor createAccessor(Field f, String name, MethodHandles.Lookup lookup) throws IllegalAccessException {
        return new VarHandleAccessor(lookup.unreflectVarHandle(f), name);
    }

    static BasePropertySet.Accessor createAccessor(Method getter, Method setter, String value, MethodHandles.Lookup lookup) throws IllegalAccessException {
        return new MethodHandleAccessor(lookup.unreflect(getter), setter != null ? lookup.unreflect(setter) : null, value);
    }

    static final class MethodHandleAccessor
    implements BasePropertySet.Accessor {
        @NotNull
        private final MethodHandle getter;
        @Nullable
        private final MethodHandle setter;
        private final String name;

        protected MethodHandleAccessor(MethodHandle getter, MethodHandle setter, String value) {
            this.getter = getter;
            this.setter = setter;
            this.name = value;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean hasValue(PropertySet props) {
            return this.get(props) != null;
        }

        @Override
        public Object get(PropertySet props) {
            try {
                return this.getter.invoke(props);
            }
            catch (Throwable ex) {
                this.handle(ex);
                return 0;
            }
        }

        @Override
        public void set(PropertySet props, Object value) {
            if (this.setter == null) {
                throw new ReadOnlyPropertyException(this.getName());
            }
            try {
                this.setter.invoke(props, value);
            }
            catch (Throwable e) {
                this.handle(e);
            }
        }

        private Exception handle(Throwable t) {
            if (t instanceof Error) {
                throw (Error)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            throw new Error(t);
        }
    }

    static final class VarHandleAccessor
    implements BasePropertySet.Accessor {
        private final VarHandle vh;
        private final String name;

        protected VarHandleAccessor(VarHandle vh, String name) {
            this.vh = vh;
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean hasValue(PropertySet props) {
            return this.get(props) != null;
        }

        @Override
        public Object get(PropertySet props) {
            return this.vh.get(props);
        }

        @Override
        public void set(PropertySet props, Object value) {
            this.vh.set(props, value);
        }
    }
}

