/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.loading;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.AllPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Enumeration;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface ClassFilePostProcessor {
    public byte[] transform(@MaybeNull ClassLoader var1, String var2, @MaybeNull ProtectionDomain var3, byte[] var4);

    @HashCodeAndEqualsPlugin.Enhance
    public static class ForClassFileTransformer
    implements ClassFilePostProcessor {
        protected static final ProtectionDomain ALL_PRIVILEGES = new ProtectionDomain(null, new AllPermissionsCollection());
        @AlwaysNull
        private static final Class<?> UNLOADED_TYPE = null;
        private final ClassFileTransformer classFileTransformer;

        public ForClassFileTransformer(ClassFileTransformer classFileTransformer) {
            this.classFileTransformer = classFileTransformer;
        }

        public byte[] transform(@MaybeNull ClassLoader classLoader, String name, @MaybeNull ProtectionDomain protectionDomain, byte[] binaryRepresentation) {
            try {
                byte[] transformed = this.classFileTransformer.transform(classLoader, name.replace('.', '/'), UNLOADED_TYPE, protectionDomain == null ? ALL_PRIVILEGES : protectionDomain, binaryRepresentation);
                return transformed == null ? binaryRepresentation : transformed;
            }
            catch (IllegalClassFormatException exception) {
                throw new IllegalStateException("Failed to transform " + name, exception);
            }
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.classFileTransformer.equals(((ForClassFileTransformer)object).classFileTransformer);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.classFileTransformer.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class AllPermissionsCollection
        extends PermissionCollection {
            private static final long serialVersionUID = 1L;

            protected AllPermissionsCollection() {
            }

            @Override
            public void add(Permission permission) {
                throw new UnsupportedOperationException("add");
            }

            @Override
            public boolean implies(Permission permission) {
                return true;
            }

            @Override
            public Enumeration<Permission> elements() {
                return Collections.enumeration(Collections.singleton(new AllPermission()));
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                return this.getClass() == object.getClass();
            }

            public int hashCode() {
                return this.getClass().hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements ClassFilePostProcessor
    {
        INSTANCE;


        @Override
        public byte[] transform(@MaybeNull ClassLoader classLoader, String name, @MaybeNull ProtectionDomain protectionDomain, byte[] binaryRepresentation) {
            return binaryRepresentation;
        }
    }
}

