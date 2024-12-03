/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.List;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum JavaType {
    CONSTABLE("java.lang.constant.Constable", 1537, TypeDescription.UNDEFINED, new TypeDefinition[0]),
    TYPE_DESCRIPTOR("java.lang.invoke.TypeDescriptor", 1537, TypeDescription.UNDEFINED, new TypeDefinition[0]),
    TYPE_DESCRIPTOR_OF_FIELD("java.lang.invoke.TypeDescriptor$OfField", 1537, TypeDescription.UNDEFINED, TYPE_DESCRIPTOR.getTypeStub()),
    TYPE_DESCRIPTOR_OF_METHOD("java.lang.invoke.TypeDescriptor$OfMethod", 1537, TypeDescription.UNDEFINED, TYPE_DESCRIPTOR.getTypeStub()),
    CONSTANT_DESCRIPTION("java.lang.constant.ConstantDesc", 1537, TypeDescription.UNDEFINED, new TypeDefinition[0]),
    DYNAMIC_CONSTANT_DESCRIPTION("java.lang.constant.DynamicConstantDesc", 1025, TypeDescription.ForLoadedType.of(Object.class), CONSTANT_DESCRIPTION.getTypeStub()),
    CLASS_DESCRIPTION("java.lang.constant.ClassDesc", 1537, TypeDescription.UNDEFINED, CONSTANT_DESCRIPTION.getTypeStub(), TYPE_DESCRIPTOR_OF_FIELD.getTypeStub()),
    METHOD_TYPE_DESCRIPTION("java.lang.constant.MethodTypeDesc", 1537, TypeDescription.UNDEFINED, CONSTANT_DESCRIPTION.getTypeStub(), TYPE_DESCRIPTOR_OF_METHOD.getTypeStub()),
    METHOD_HANDLE_DESCRIPTION("java.lang.constant.MethodHandleDesc", 1537, TypeDescription.UNDEFINED, CONSTANT_DESCRIPTION.getTypeStub()),
    DIRECT_METHOD_HANDLE_DESCRIPTION("java.lang.constant.DirectMethodHandleDesc", 1537, TypeDescription.UNDEFINED, METHOD_HANDLE_DESCRIPTION.getTypeStub()),
    METHOD_HANDLE("java.lang.invoke.MethodHandle", 1025, TypeDescription.ForLoadedType.of(Object.class), CONSTABLE.getTypeStub()),
    METHOD_HANDLES("java.lang.invoke.MethodHandles", 1, (Type)((Object)Object.class), new Type[0]),
    METHOD_TYPE("java.lang.invoke.MethodType", 17, TypeDescription.ForLoadedType.of(Object.class), CONSTABLE.getTypeStub(), TYPE_DESCRIPTOR_OF_METHOD.getTypeStub(), TypeDescription.ForLoadedType.of(Serializable.class)),
    METHOD_HANDLES_LOOKUP("java.lang.invoke.MethodHandles$Lookup", 25, (Type)((Object)Object.class), new Type[0]),
    CALL_SITE("java.lang.invoke.CallSite", 1025, (Type)((Object)Object.class), new Type[0]),
    VAR_HANDLE("java.lang.invoke.VarHandle", 1025, (TypeDefinition)TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), CONSTABLE.getTypeStub()),
    PARAMETER("java.lang.reflect.Parameter", 17, (Type)((Object)Object.class), new Type[]{AnnotatedElement.class}),
    EXECUTABLE("java.lang.reflect.Executable", 1025, (Type)((Object)AccessibleObject.class), new Type[]{Member.class, GenericDeclaration.class}),
    MODULE("java.lang.Module", 17, (Type)((Object)Object.class), new Type[]{AnnotatedElement.class}),
    CONSTANT_BOOTSTRAPS("java.lang.invoke.ConstantBootstraps", 17, (Type)((Object)Object.class), new Type[0]),
    RECORD("java.lang.Record", 1025, (Type)((Object)Object.class), new Type[0]),
    OBJECT_METHODS("java.lang.runtime.ObjectMethods", 1, (Type)((Object)Object.class), new Type[0]),
    ACCESS_CONTROL_CONTEXT("java.security.AccessControlContext", 17, TypeDescription.UNDEFINED, new TypeDefinition[0]);

    private final TypeDescription typeDescription;
    private transient /* synthetic */ Class loaded;
    private transient /* synthetic */ Boolean available;

    private JavaType(@MaybeNull String typeName, int modifiers, Type superClass, Type ... anInterface) {
        this(typeName, modifiers, superClass == null ? TypeDescription.Generic.UNDEFINED : TypeDefinition.Sort.describe(superClass), new TypeList.Generic.ForLoadedTypes(anInterface));
    }

    private JavaType(@MaybeNull String typeName, int modifiers, TypeDefinition superClass, TypeDefinition ... anInterface) {
        this(typeName, modifiers, superClass == null ? TypeDescription.Generic.UNDEFINED : superClass.asGenericType(), new TypeList.Generic.Explicit(anInterface));
    }

    private JavaType(@MaybeNull String typeName, int modifiers, TypeDescription.Generic superClass, TypeList.Generic interfaces) {
        this.typeDescription = new LatentTypeWithSimpleName(typeName, modifiers, superClass, interfaces);
    }

    public TypeDescription getTypeStub() {
        return this.typeDescription;
    }

    @CachedReturnPlugin.Enhance(value="loaded")
    public Class<?> load() throws ClassNotFoundException {
        Object object;
        Object object2;
        Class clazz = this.loaded;
        if (clazz != null) {
            object2 = null;
        } else {
            object = this;
            object2 = object = Class.forName(object.typeDescription.getName(), false, ClassLoadingStrategy.BOOTSTRAP_LOADER);
        }
        if (object == null) {
            object = this.loaded;
        } else {
            this.loaded = object;
        }
        return object;
    }

    public TypeDescription loadAsDescription() throws ClassNotFoundException {
        return TypeDescription.ForLoadedType.of(this.load());
    }

    public boolean isAvailable() {
        return this.doIsAvailable();
    }

    @CachedReturnPlugin.Enhance(value="available")
    private Boolean doIsAvailable() {
        Object object;
        Object object2;
        Boolean bl = this.available;
        if (bl != null) {
            object2 = null;
        } else {
            object = this;
            try {
                object.load();
                object2 = true;
            }
            catch (ClassNotFoundException ignored) {
                object2 = object = Boolean.valueOf(false);
            }
        }
        if (object == null) {
            object = this.available;
        } else {
            this.available = object;
        }
        return object;
    }

    public boolean isInstance(Object instance) {
        if (!this.isAvailable()) {
            return false;
        }
        try {
            return this.load().isInstance(instance);
        }
        catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class LatentTypeWithSimpleName
    extends TypeDescription.Latent {
        protected LatentTypeWithSimpleName(String name, int modifiers, @MaybeNull TypeDescription.Generic superClass, List<? extends TypeDescription.Generic> interfaces) {
            super(name, modifiers, superClass, interfaces);
        }

        @Override
        public String getSimpleName() {
            String name = this.getName();
            int index = Math.max(name.lastIndexOf(36), name.lastIndexOf(46));
            return index == -1 ? name : name.substring(index + 1);
        }
    }
}

