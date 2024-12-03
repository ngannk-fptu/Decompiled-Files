/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.privilege.SetAccessibleAction;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface LoadedTypeInitializer {
    public void onLoad(Class<?> var1);

    public boolean isAlive();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    @SuppressFBWarnings(value={"SE_BAD_FIELD"}, justification="Serialization is considered opt-in for a rare use case")
    public static class Compound
    implements LoadedTypeInitializer,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final List<LoadedTypeInitializer> loadedTypeInitializers = new ArrayList<LoadedTypeInitializer>();

        public Compound(LoadedTypeInitializer ... loadedTypeInitializer) {
            this(Arrays.asList(loadedTypeInitializer));
        }

        public Compound(List<? extends LoadedTypeInitializer> loadedTypeInitializers) {
            for (LoadedTypeInitializer loadedTypeInitializer : loadedTypeInitializers) {
                if (loadedTypeInitializer instanceof Compound) {
                    this.loadedTypeInitializers.addAll(((Compound)loadedTypeInitializer).loadedTypeInitializers);
                    continue;
                }
                if (loadedTypeInitializer instanceof NoOp) continue;
                this.loadedTypeInitializers.add(loadedTypeInitializer);
            }
        }

        @Override
        public void onLoad(Class<?> type) {
            for (LoadedTypeInitializer loadedTypeInitializer : this.loadedTypeInitializers) {
                loadedTypeInitializer.onLoad(type);
            }
        }

        @Override
        public boolean isAlive() {
            for (LoadedTypeInitializer loadedTypeInitializer : this.loadedTypeInitializers) {
                if (!loadedTypeInitializer.isAlive()) continue;
                return true;
            }
            return false;
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
            return ((Object)this.loadedTypeInitializers).equals(((Compound)object).loadedTypeInitializers);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.loadedTypeInitializers).hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForStaticField
    implements LoadedTypeInitializer,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final String fieldName;
        private final Object value;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
        private final transient Object accessControlContext;
        private static final boolean ACCESS_CONTROLLER;

        public ForStaticField(String fieldName, Object value) {
            this.fieldName = fieldName;
            this.value = value;
            this.accessControlContext = ForStaticField.getContext();
        }

        @MaybeNull
        @AccessControllerPlugin.Enhance
        private static Object getContext() {
            if (ACCESS_CONTROLLER) {
                return AccessController.getContext();
            }
            return null;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction, @MaybeNull Object object) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction, (AccessControlContext)object);
            }
            return action.run();
        }

        private Object readResolve() {
            return new ForStaticField(this.fieldName, this.value);
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Modules are assumed available when module system is supported")
        public void onLoad(Class<?> type) {
            try {
                Field field = type.getDeclaredField(this.fieldName);
                if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || JavaModule.isSupported() && !JavaModule.ofType(type).isExported(TypeDescription.ForLoadedType.of(type).getPackage(), JavaModule.ofType(ForStaticField.class))) {
                    ForStaticField.doPrivileged(new SetAccessibleAction<Field>(field), this.accessControlContext);
                }
                field.set(null, this.value);
            }
            catch (IllegalAccessException exception) {
                throw new IllegalArgumentException("Cannot access " + this.fieldName + " from " + type, exception);
            }
            catch (NoSuchFieldException exception) {
                throw new IllegalStateException("There is no field " + this.fieldName + " defined on " + type, exception);
            }
        }

        @Override
        public boolean isAlive() {
            return true;
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
            if (!this.fieldName.equals(((ForStaticField)object).fieldName)) {
                return false;
            }
            return this.value.equals(((ForStaticField)object).value);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.fieldName.hashCode()) * 31 + this.value.hashCode();
        }

        static {
            try {
                Class.forName("java.security.AccessController", false, null);
                ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
            }
            catch (ClassNotFoundException classNotFoundException) {
                ACCESS_CONTROLLER = false;
            }
            catch (SecurityException securityException) {
                ACCESS_CONTROLLER = true;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum NoOp implements LoadedTypeInitializer
    {
        INSTANCE;


        @Override
        public void onLoad(Class<?> type) {
        }

        @Override
        public boolean isAlive() {
            return false;
        }
    }
}

