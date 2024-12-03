/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.asm;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.ExceptionMethod;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.Attribute;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ConstantDynamic;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.ModuleVisitor;
import net.bytebuddy.jar.asm.RecordComponentVisitor;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.jar.asm.TypePath;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public abstract class ClassVisitorFactory<T> {
    private static final String DELEGATE = "delegate";
    private static final String LABELS = "labels";
    private static final String WRAP = "wrap";
    private final Class<?> type;
    private static final boolean ACCESS_CONTROLLER;

    protected ClassVisitorFactory(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return this.type;
    }

    public static <S> ClassVisitorFactory<S> of(Class<S> classVisitor) {
        return ClassVisitorFactory.of(classVisitor, new ByteBuddy().with(TypeValidation.DISABLED));
    }

    public static <S> ClassVisitorFactory<S> of(Class<S> classVisitor, ByteBuddy byteBuddy) {
        return (ClassVisitorFactory)ClassVisitorFactory.doPrivileged(new CreateClassVisitorFactory<S>(classVisitor, byteBuddy));
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    private static DynamicType.Builder<?> toVisitorBuilder(ByteBuddy byteBuddy, Class<?> sourceVisitor, Class<?> targetVisitor, @MaybeNull Class<?> sourceTypePath, @MaybeNull Class<?> targetTypePath, Implementation appendix) throws Exception {
        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition builder = byteBuddy.subclass(sourceVisitor, (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).defineField(DELEGATE, targetVisitor, Visibility.PRIVATE, FieldManifestation.FINAL).defineConstructor(Visibility.PUBLIC).withParameters(targetVisitor).intercept(MethodCall.invoke(sourceVisitor.getDeclaredConstructor(Integer.TYPE)).with(OpenedClassReader.ASM_API).andThen(FieldAccessor.ofField(DELEGATE).setsArgumentAt(0)).andThen(appendix)).defineMethod(WRAP, sourceVisitor, Visibility.PUBLIC, Ownership.STATIC).withParameters(targetVisitor).intercept(new Implementation.Simple(new NullCheckedConstruction(targetVisitor)));
        if (sourceTypePath == null || targetTypePath == null) {
            return builder;
        }
        return builder.defineMethod("typePath", targetTypePath, Visibility.PRIVATE, Ownership.STATIC).withParameters(sourceTypePath).intercept(new Implementation.Simple(new TypePathTranslator(sourceTypePath, targetTypePath)));
    }

    private static DynamicType.Builder<?> toMethodVisitorBuilder(ByteBuddy byteBuddy, Class<?> sourceVisitor, Class<?> targetVisitor, @MaybeNull Class<?> sourceTypePath, @MaybeNull Class<?> targetTypePath, @MaybeNull Class<?> sourceLabel, @MaybeNull Class<?> targetLabel, @MaybeNull Class<?> sourceType, @MaybeNull Class<?> targetType, @MaybeNull Class<?> sourceHandle, @MaybeNull Class<?> targetHandle, @MaybeNull Class<?> sourceConstantDynamic, @MaybeNull Class<?> targetConstantDynamic) throws Exception {
        DynamicType.Builder<?> builder = ClassVisitorFactory.toVisitorBuilder(byteBuddy, sourceVisitor, targetVisitor, sourceTypePath, targetTypePath, FieldAccessor.ofField(LABELS).setsValue((StackManipulation)new StackManipulation.Compound(TypeCreation.of(TypeDescription.ForLoadedType.of(HashMap.class)), Duplication.SINGLE, MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(HashMap.class).getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(0)))).getOnly())), (java.lang.reflect.Type)((Object)Map.class)));
        if (sourceLabel != null && targetLabel != null) {
            builder = builder.defineField(LABELS, (java.lang.reflect.Type)((Object)Map.class), Visibility.PRIVATE, FieldManifestation.FINAL).defineMethod("label", targetLabel, Visibility.PRIVATE).withParameters(sourceLabel).intercept(new Implementation.Simple(new LabelTranslator(targetLabel))).defineMethod(LABELS, (TypeDefinition)TypeDescription.ArrayProjection.of(TypeDescription.ForLoadedType.of(targetLabel)), Visibility.PRIVATE).withParameters(TypeDescription.ArrayProjection.of(TypeDescription.ForLoadedType.of(sourceLabel))).intercept(new Implementation.Simple(new LabelArrayTranslator(sourceLabel, targetLabel))).defineMethod("frames", (java.lang.reflect.Type)((Object)Object[].class), Visibility.PRIVATE).withParameters(new java.lang.reflect.Type[]{Object[].class}).intercept(new Implementation.Simple(new FrameTranslator(sourceLabel, targetLabel)));
        }
        if (sourceHandle != null && targetHandle != null) {
            builder = builder.defineMethod("handle", targetHandle, Visibility.PRIVATE, Ownership.STATIC).withParameters(sourceHandle).intercept(new Implementation.Simple(new HandleTranslator(sourceHandle, targetHandle)));
        }
        if (sourceConstantDynamic != null && targetConstantDynamic != null && sourceHandle != null && targetHandle != null) {
            builder = builder.defineMethod("constantDyanmic", targetConstantDynamic, Visibility.PRIVATE, Ownership.STATIC).withParameters(sourceConstantDynamic).intercept(new Implementation.Simple(new ConstantDynamicTranslator(sourceConstantDynamic, targetConstantDynamic, sourceHandle, targetHandle)));
        }
        return builder.defineMethod("constant", (java.lang.reflect.Type)((Object)Object.class), Visibility.PRIVATE, Ownership.STATIC).withParameters(new java.lang.reflect.Type[]{Object.class}).intercept(new Implementation.Simple(new ConstantTranslator(sourceHandle, targetHandle, sourceType, targetType, sourceConstantDynamic, targetConstantDynamic))).defineMethod("constants", (java.lang.reflect.Type)((Object)Object[].class), Visibility.PRIVATE, Ownership.STATIC).withParameters(new java.lang.reflect.Type[]{Object[].class}).intercept(new Implementation.Simple(new ConstantArrayTranslator()));
    }

    private static MethodCall.ArgumentLoader.Factory toConvertedParameter(TypeDescription source, Class<?> target, String method, int offset, boolean virtual) {
        return new MethodCall.ArgumentLoader.ForStackManipulation((StackManipulation)new StackManipulation.Compound(virtual ? MethodVariableAccess.loadThis() : StackManipulation.Trivial.INSTANCE, MethodVariableAccess.REFERENCE.loadFrom(offset), MethodInvocation.invoke((MethodDescription.InDefinedShape)((MethodList)source.getDeclaredMethods().filter(ElementMatchers.named(method))).getOnly())), target);
    }

    private static DynamicType toAttributeWrapper(DynamicType.Builder<?> builder, Class<?> source, Class<?> target, TypeDescription sourceWrapper, TypeDescription targetWrapper) throws Exception {
        return builder.defineField(DELEGATE, target, Visibility.PUBLIC, FieldManifestation.FINAL).defineConstructor(Visibility.PUBLIC).withParameters(target).intercept(MethodCall.invoke(source.getDeclaredConstructor(String.class)).onSuper().with((StackManipulation)new StackManipulation.Compound(MethodVariableAccess.REFERENCE.loadFrom(1), FieldAccess.forField(new FieldDescription.ForLoadedField(target.getField("type"))).read()), (java.lang.reflect.Type)((Object)String.class)).andThen(FieldAccessor.ofField(DELEGATE).setsArgumentAt(0))).defineMethod("attribute", source, Visibility.PUBLIC, Ownership.STATIC).withParameters(target).intercept(new Implementation.Simple(new AttributeTranslator(source, target, sourceWrapper, targetWrapper))).method(ElementMatchers.isProtected()).intercept(ExceptionMethod.throwing(UnsupportedOperationException.class)).method(ElementMatchers.named("isUnknown")).intercept(MethodCall.invoke(target.getMethod("isUnknown", new Class[0])).onField(DELEGATE)).method(ElementMatchers.named("isCodeAttribute")).intercept(MethodCall.invoke(target.getMethod("isCodeAttribute", new Class[0])).onField(DELEGATE)).make();
    }

    public abstract T wrap(ClassVisitor var1);

    public abstract ClassVisitor unwrap(T var1);

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
        return this.type.equals(((ClassVisitorFactory)object).type);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.type.hashCode();
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class CreateClassVisitorFactory<S>
    implements PrivilegedAction<ClassVisitorFactory<S>> {
        private final Class<S> classVisitor;
        private final ByteBuddy byteBuddy;

        protected CreateClassVisitorFactory(Class<S> classVisitor, ByteBuddy byteBuddy) {
            this.classVisitor = classVisitor;
            this.byteBuddy = byteBuddy;
        }

        @Override
        public ClassVisitorFactory<S> run() {
            if (!ClassVisitor.class.getSimpleName().equals(this.classVisitor.getSimpleName())) {
                throw new IllegalArgumentException("Expected a class named " + ClassVisitor.class.getSimpleName() + ": " + this.classVisitor);
            }
            try {
                DynamicType targetAttribute;
                DynamicType sourceAttribute;
                String prefix = this.classVisitor.getPackage().getName();
                HashMap utilities = new HashMap();
                for (Class type : Arrays.asList(Attribute.class, Label.class, Type.class, TypePath.class, Handle.class, ConstantDynamic.class)) {
                    Class<?> utility;
                    try {
                        utility = Class.forName(prefix + "." + type.getSimpleName(), false, this.classVisitor.getClassLoader());
                    }
                    catch (ClassNotFoundException ignored) {
                        continue;
                    }
                    utilities.put(type, utility);
                }
                if (utilities.containsKey(Label.class)) {
                    utilities.put(Label[].class, Class.forName("[L" + ((Class)utilities.get(Label.class)).getName() + ";", false, this.classVisitor.getClassLoader()));
                }
                HashMap equivalents = new HashMap();
                HashMap builders = new HashMap();
                for (Class type : Arrays.asList(ClassVisitor.class, AnnotationVisitor.class, ModuleVisitor.class, RecordComponentVisitor.class, FieldVisitor.class, MethodVisitor.class)) {
                    DynamicType.Builder unwrapper;
                    DynamicType.Builder wrapper;
                    Class<?> equivalent;
                    try {
                        equivalent = Class.forName(prefix + "." + type.getSimpleName(), false, this.classVisitor.getClassLoader());
                    }
                    catch (ClassNotFoundException ignored) {
                        continue;
                    }
                    if (type == MethodVisitor.class) {
                        wrapper = ClassVisitorFactory.toMethodVisitorBuilder(this.byteBuddy, type, equivalent, TypePath.class, (Class)utilities.get(TypePath.class), Label.class, (Class)utilities.get(Label.class), Type.class, (Class)utilities.get(Type.class), Handle.class, (Class)utilities.get(Handle.class), ConstantDynamic.class, (Class)utilities.get(ConstantDynamic.class));
                        unwrapper = ClassVisitorFactory.toMethodVisitorBuilder(this.byteBuddy, equivalent, type, (Class)utilities.get(TypePath.class), TypePath.class, (Class)utilities.get(Label.class), Label.class, (Class)utilities.get(Type.class), Type.class, (Class)utilities.get(Handle.class), Handle.class, (Class)utilities.get(ConstantDynamic.class), ConstantDynamic.class);
                    } else {
                        wrapper = ClassVisitorFactory.toVisitorBuilder(this.byteBuddy, type, equivalent, TypePath.class, (Class)utilities.get(TypePath.class), new Implementation.Simple(MethodReturn.VOID));
                        unwrapper = ClassVisitorFactory.toVisitorBuilder(this.byteBuddy, equivalent, type, (Class)utilities.get(TypePath.class), TypePath.class, new Implementation.Simple(MethodReturn.VOID));
                    }
                    equivalents.put(type, equivalent);
                    builders.put(type, wrapper);
                    builders.put(equivalent, unwrapper);
                }
                ArrayList<DynamicType> dynamicTypes = new ArrayList<DynamicType>();
                HashMap<Object, TypeDescription> generated = new HashMap<Object, TypeDescription>();
                if (utilities.containsKey(Attribute.class)) {
                    DynamicType.Builder<Attribute> source = this.byteBuddy.subclass(Attribute.class, (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS);
                    DynamicType.Builder builder = this.byteBuddy.subclass((Class)utilities.get(Attribute.class), (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS);
                    sourceAttribute = ClassVisitorFactory.toAttributeWrapper(source, Attribute.class, (Class)utilities.get(Attribute.class), source.toTypeDescription(), builder.toTypeDescription());
                    dynamicTypes.add(sourceAttribute);
                    targetAttribute = ClassVisitorFactory.toAttributeWrapper(builder, (Class)utilities.get(Attribute.class), Attribute.class, builder.toTypeDescription(), source.toTypeDescription());
                    dynamicTypes.add(targetAttribute);
                } else {
                    sourceAttribute = null;
                    targetAttribute = null;
                }
                for (Map.Entry entry : equivalents.entrySet()) {
                    DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition wrapper = (DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition)builders.get(entry.getKey());
                    DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition unwrapper = (DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition)builders.get(entry.getValue());
                    for (Method method : ((Class)entry.getKey()).getMethods()) {
                        Method target;
                        if (method.getDeclaringClass() == Object.class) continue;
                        Class<?>[] parameter = method.getParameterTypes();
                        Class[] match = new Class[parameter.length];
                        ArrayList<MethodCall.ArgumentLoader.Factory> left = new ArrayList<MethodCall.ArgumentLoader.Factory>(parameter.length);
                        ArrayList<MethodCall.ArgumentLoader.Factory> right = new ArrayList<MethodCall.ArgumentLoader.Factory>(match.length);
                        boolean unsupported = false;
                        boolean unresolved = false;
                        int offset = 1;
                        for (int index = 0; index < parameter.length; ++index) {
                            if (entry.getKey() == MethodVisitor.class && parameter[index] == Label.class) {
                                match[index] = (Class)utilities.get(Label.class);
                                left.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getKey())).toTypeDescription(), match[index], "label", offset, true));
                                right.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getValue())).toTypeDescription(), parameter[index], "label", offset, true));
                            } else if (entry.getKey() == MethodVisitor.class && parameter[index] == Label[].class) {
                                match[index] = (Class)utilities.get(Label[].class);
                                left.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getKey())).toTypeDescription(), match[index], ClassVisitorFactory.LABELS, offset, true));
                                right.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getValue())).toTypeDescription(), parameter[index], ClassVisitorFactory.LABELS, offset, true));
                            } else if (parameter[index] == TypePath.class) {
                                match[index] = (Class)utilities.get(TypePath.class);
                                left.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getKey())).toTypeDescription(), match[index], "typePath", offset, false));
                                right.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getValue())).toTypeDescription(), parameter[index], "typePath", offset, false));
                            } else if (entry.getKey() == MethodVisitor.class && parameter[index] == Handle.class) {
                                match[index] = (Class)utilities.get(Handle.class);
                                left.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getKey())).toTypeDescription(), match[index], "handle", offset, false));
                                right.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getValue())).toTypeDescription(), parameter[index], "handle", offset, false));
                            } else if (entry.getKey() == MethodVisitor.class && parameter[index] == Object.class) {
                                match[index] = Object.class;
                                left.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getKey())).toTypeDescription(), Object.class, "constant", offset, false));
                                right.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getValue())).toTypeDescription(), Object.class, "constant", offset, false));
                            } else if (entry.getKey() == MethodVisitor.class && parameter[index] == Object[].class) {
                                match[index] = Object[].class;
                                if (method.getName().equals("visitFrame")) {
                                    left.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getKey())).toTypeDescription(), Object[].class, "frames", offset, true));
                                    right.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getValue())).toTypeDescription(), Object[].class, "frames", offset, true));
                                } else {
                                    left.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getKey())).toTypeDescription(), Object[].class, "constants", offset, false));
                                    right.add(ClassVisitorFactory.toConvertedParameter(((DynamicType.Builder)builders.get(entry.getValue())).toTypeDescription(), Object[].class, "constants", offset, false));
                                }
                            } else if (parameter[index] == Attribute.class) {
                                match[index] = (Class)utilities.get(Attribute.class);
                                if (sourceAttribute != null && targetAttribute != null) {
                                    left.add(ClassVisitorFactory.toConvertedParameter(targetAttribute.getTypeDescription(), (Class)utilities.get(Attribute.class), "attribute", offset, false));
                                    right.add(ClassVisitorFactory.toConvertedParameter(sourceAttribute.getTypeDescription(), Attribute.class, "attribute", offset, false));
                                } else {
                                    unsupported = true;
                                }
                            } else {
                                match[index] = parameter[index];
                                left.add(new MethodCall.ArgumentLoader.ForMethodParameter.Factory(index));
                                right.add(new MethodCall.ArgumentLoader.ForMethodParameter.Factory(index));
                            }
                            if (match[index] == null) {
                                unresolved = true;
                                break;
                            }
                            offset += parameter[index] == Long.TYPE || parameter[index] == Double.TYPE ? 2 : 1;
                        }
                        if (unresolved) {
                            target = null;
                            unsupported = true;
                        } else {
                            try {
                                target = ((Class)entry.getValue()).getMethod(method.getName(), match);
                            }
                            catch (NoSuchMethodException ignored) {
                                target = null;
                                unsupported = true;
                            }
                        }
                        if (unsupported) {
                            wrapper = wrapper.method(ElementMatchers.is(method)).intercept(ExceptionMethod.throwing(UnsupportedOperationException.class));
                            if (target == null) continue;
                            unwrapper = unwrapper.method(ElementMatchers.is(target)).intercept(ExceptionMethod.throwing(UnsupportedOperationException.class));
                            continue;
                        }
                        MethodCall wrapping = MethodCall.invoke(target).onField(ClassVisitorFactory.DELEGATE).with(left);
                        MethodCall unwrapping = MethodCall.invoke(method).onField(ClassVisitorFactory.DELEGATE).with(right);
                        Class returned = (Class)equivalents.get(method.getReturnType());
                        if (returned != null) {
                            wrapping = MethodCall.invoke((MethodDescription)((MethodList)((DynamicType.Builder)builders.get(method.getReturnType())).toTypeDescription().getDeclaredMethods().filter(ElementMatchers.named(ClassVisitorFactory.WRAP))).getOnly()).withMethodCall(wrapping);
                            unwrapping = MethodCall.invoke((MethodDescription)((MethodList)((DynamicType.Builder)builders.get(returned)).toTypeDescription().getDeclaredMethods().filter(ElementMatchers.named(ClassVisitorFactory.WRAP))).getOnly()).withMethodCall(unwrapping);
                        }
                        wrapper = wrapper.method(ElementMatchers.is(method)).intercept(wrapping);
                        unwrapper = unwrapper.method(ElementMatchers.is(target)).intercept(unwrapping);
                    }
                    DynamicType.Unloaded left = wrapper.make();
                    DynamicType.Unloaded right = unwrapper.make();
                    generated.put(entry.getKey(), left.getTypeDescription());
                    generated.put(entry.getValue(), right.getTypeDescription());
                    dynamicTypes.add(left);
                    dynamicTypes.add(right);
                }
                ClassLoader classLoader = new MultipleParentClassLoader.Builder(false).appendMostSpecific(ClassVisitor.class, this.classVisitor).build();
                ClassVisitorFactory classVisitorFactory = (ClassVisitorFactory)this.byteBuddy.subclass(ClassVisitorFactory.class, (ConstructorStrategy)ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING).method(ElementMatchers.named(ClassVisitorFactory.WRAP)).intercept(MethodCall.construct((MethodDescription)((MethodList)((TypeDescription)generated.get(this.classVisitor)).getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly()).withArgument(0)).method(ElementMatchers.named("unwrap")).intercept(MethodCall.construct((MethodDescription)((MethodList)((TypeDescription)generated.get(ClassVisitor.class)).getDeclaredMethods().filter(ElementMatchers.isConstructor())).getOnly()).withArgument(0).withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC)).make().include(dynamicTypes).load(classLoader).getLoaded().getConstructor(Class.class).newInstance(this.classVisitor);
                if (classLoader instanceof MultipleParentClassLoader && classLoader != ClassVisitor.class.getClassLoader() && classLoader != this.classVisitor.getClassLoader() && !((MultipleParentClassLoader)classLoader).seal()) {
                    throw new IllegalStateException("Failed to seal multiple parent class loader: " + classLoader);
                }
                return classVisitorFactory;
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("Failed to generate factory for " + this.classVisitor.getName(), exception);
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
            if (!this.classVisitor.equals(((CreateClassVisitorFactory)object).classVisitor)) {
                return false;
            }
            return this.byteBuddy.equals(((CreateClassVisitorFactory)object).byteBuddy);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.classVisitor.hashCode()) * 31 + this.byteBuddy.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class AttributeTranslator
    implements ByteCodeAppender {
        protected static final String NAME = "attribute";
        private final Class<?> sourceAttribute;
        private final Class<?> targetAttribute;
        private final TypeDescription sourceWrapper;
        private final TypeDescription targetWrapper;

        protected AttributeTranslator(Class<?> sourceAttribute, Class<?> targetAttribute, TypeDescription sourceWrapper, TypeDescription targetWrapper) {
            this.sourceAttribute = sourceAttribute;
            this.targetAttribute = targetAttribute;
            this.sourceWrapper = sourceWrapper;
            this.targetWrapper = targetWrapper;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label nullCheck = new Label();
            Label wrapperCheck = new Label();
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitJumpInsn(199, nullCheck);
            methodVisitor.visitInsn(1);
            methodVisitor.visitInsn(176);
            methodVisitor.visitLabel(nullCheck);
            implementationContext.getFrameGeneration().same(methodVisitor, instrumentedMethod.getParameters().asTypeList());
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitTypeInsn(193, this.targetWrapper.getInternalName());
            methodVisitor.visitJumpInsn(153, wrapperCheck);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitTypeInsn(192, this.targetWrapper.getInternalName());
            methodVisitor.visitFieldInsn(180, this.targetWrapper.getInternalName(), ClassVisitorFactory.DELEGATE, Type.getDescriptor(this.sourceAttribute));
            methodVisitor.visitInsn(176);
            methodVisitor.visitLabel(wrapperCheck);
            implementationContext.getFrameGeneration().same(methodVisitor, instrumentedMethod.getParameters().asTypeList());
            methodVisitor.visitTypeInsn(187, this.sourceWrapper.getInternalName());
            methodVisitor.visitInsn(89);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(183, this.sourceWrapper.getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(this.targetAttribute)), false);
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(3, 1);
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
            if (!this.sourceAttribute.equals(((AttributeTranslator)object).sourceAttribute)) {
                return false;
            }
            if (!this.targetAttribute.equals(((AttributeTranslator)object).targetAttribute)) {
                return false;
            }
            if (!this.sourceWrapper.equals(((AttributeTranslator)object).sourceWrapper)) {
                return false;
            }
            return this.targetWrapper.equals(((AttributeTranslator)object).targetWrapper);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.sourceAttribute.hashCode()) * 31 + this.targetAttribute.hashCode()) * 31 + this.sourceWrapper.hashCode()) * 31 + this.targetWrapper.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class TypePathTranslator
    implements ByteCodeAppender {
        protected static final String NAME = "typePath";
        private final Class<?> sourceTypePath;
        private final Class<?> targetTypePath;

        protected TypePathTranslator(Class<?> sourceTypePath, Class<?> targetTypePath) {
            this.sourceTypePath = sourceTypePath;
            this.targetTypePath = targetTypePath;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label nullCheck = new Label();
            Label end = new Label();
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitJumpInsn(199, nullCheck);
            methodVisitor.visitInsn(1);
            methodVisitor.visitJumpInsn(167, end);
            implementationContext.getFrameGeneration().same(methodVisitor, instrumentedMethod.getParameters().asTypeList());
            methodVisitor.visitLabel(nullCheck);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceTypePath), "toString", Type.getMethodDescriptor(Type.getType(String.class), new Type[0]), false);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(this.targetTypePath), "fromString", Type.getMethodDescriptor(Type.getType(this.targetTypePath), Type.getType(String.class)), false);
            methodVisitor.visitLabel(end);
            implementationContext.getFrameGeneration().same1(methodVisitor, TypeDescription.ForLoadedType.of(this.targetTypePath), instrumentedMethod.getParameters().asTypeList());
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(1, 2);
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
            if (!this.sourceTypePath.equals(((TypePathTranslator)object).sourceTypePath)) {
                return false;
            }
            return this.targetTypePath.equals(((TypePathTranslator)object).targetTypePath);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.sourceTypePath.hashCode()) * 31 + this.targetTypePath.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class FrameTranslator
    implements ByteCodeAppender {
        protected static final String NAME = "frames";
        private final Class<?> sourceLabel;
        private final Class<?> targetLabel;

        protected FrameTranslator(Class<?> sourceLabel, Class<?> targetLabel) {
            this.sourceLabel = sourceLabel;
            this.targetLabel = targetLabel;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label nullCheck = new Label();
            Label loop = new Label();
            Label store = new Label();
            Label end = new Label();
            Label label = new Label();
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitJumpInsn(199, nullCheck);
            methodVisitor.visitInsn(1);
            methodVisitor.visitInsn(176);
            methodVisitor.visitLabel(nullCheck);
            implementationContext.getFrameGeneration().same(methodVisitor, CompoundList.of(implementationContext.getInstrumentedType(), instrumentedMethod.getParameters().asTypeList()));
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitInsn(190);
            methodVisitor.visitTypeInsn(189, Type.getInternalName(Object.class));
            methodVisitor.visitVarInsn(58, 2);
            methodVisitor.visitInsn(3);
            methodVisitor.visitVarInsn(54, 3);
            methodVisitor.visitLabel(loop);
            implementationContext.getFrameGeneration().append(methodVisitor, Arrays.asList(TypeDescription.ForLoadedType.of(Object[].class), TypeDescription.ForLoadedType.of(Integer.TYPE)), CompoundList.of(implementationContext.getInstrumentedType(), instrumentedMethod.getParameters().asTypeList()));
            methodVisitor.visitVarInsn(21, 3);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitInsn(190);
            methodVisitor.visitJumpInsn(162, end);
            methodVisitor.visitVarInsn(25, 2);
            methodVisitor.visitVarInsn(21, 3);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitVarInsn(21, 3);
            methodVisitor.visitInsn(50);
            methodVisitor.visitTypeInsn(193, Type.getInternalName(this.sourceLabel));
            methodVisitor.visitJumpInsn(153, label);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitVarInsn(21, 3);
            methodVisitor.visitInsn(50);
            methodVisitor.visitTypeInsn(192, Type.getInternalName(this.sourceLabel));
            methodVisitor.visitMethodInsn(183, implementationContext.getInstrumentedType().getInternalName(), "label", Type.getMethodDescriptor(Type.getType(this.targetLabel), Type.getType(this.sourceLabel)), false);
            methodVisitor.visitJumpInsn(167, store);
            methodVisitor.visitLabel(label);
            implementationContext.getFrameGeneration().full(methodVisitor, Arrays.asList(TypeDescription.ForLoadedType.of(Object[].class), TypeDescription.ForLoadedType.of(Integer.TYPE)), CompoundList.of(Collections.singletonList(implementationContext.getInstrumentedType()), instrumentedMethod.getParameters().asTypeList(), Arrays.asList(TypeDescription.ForLoadedType.of(Object[].class), TypeDescription.ForLoadedType.of(Integer.TYPE))));
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitVarInsn(21, 3);
            methodVisitor.visitInsn(50);
            methodVisitor.visitLabel(store);
            implementationContext.getFrameGeneration().full(methodVisitor, Arrays.asList(TypeDescription.ForLoadedType.of(Object[].class), TypeDescription.ForLoadedType.of(Integer.TYPE), TypeDescription.ForLoadedType.of(Object.class)), CompoundList.of(Collections.singletonList(implementationContext.getInstrumentedType()), instrumentedMethod.getParameters().asTypeList(), Arrays.asList(TypeDescription.ForLoadedType.of(Object[].class), TypeDescription.ForLoadedType.of(Integer.TYPE))));
            methodVisitor.visitInsn(83);
            methodVisitor.visitIincInsn(3, 1);
            methodVisitor.visitJumpInsn(167, loop);
            methodVisitor.visitLabel(end);
            implementationContext.getFrameGeneration().chop(methodVisitor, 1, CompoundList.of(Collections.singletonList(implementationContext.getInstrumentedType()), instrumentedMethod.getParameters().asTypeList(), Collections.singletonList(TypeDescription.ForLoadedType.of(Object[].class))));
            methodVisitor.visitVarInsn(25, 2);
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(5, 4);
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
            if (!this.sourceLabel.equals(((FrameTranslator)object).sourceLabel)) {
                return false;
            }
            return this.targetLabel.equals(((FrameTranslator)object).targetLabel);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.sourceLabel.hashCode()) * 31 + this.targetLabel.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ConstantArrayTranslator
    implements ByteCodeAppender {
        protected static final String NAME = "constants";

        protected ConstantArrayTranslator() {
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label nullCheck = new Label();
            Label loop = new Label();
            Label end = new Label();
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitJumpInsn(199, nullCheck);
            methodVisitor.visitInsn(1);
            methodVisitor.visitInsn(176);
            methodVisitor.visitLabel(nullCheck);
            implementationContext.getFrameGeneration().same(methodVisitor, instrumentedMethod.getParameters().asTypeList());
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitInsn(190);
            methodVisitor.visitTypeInsn(189, Type.getInternalName(Object.class));
            methodVisitor.visitVarInsn(58, 1);
            methodVisitor.visitInsn(3);
            methodVisitor.visitVarInsn(54, 2);
            methodVisitor.visitLabel(loop);
            implementationContext.getFrameGeneration().append(methodVisitor, Arrays.asList(TypeDescription.ForLoadedType.of(Object[].class), TypeDescription.ForLoadedType.of(Integer.TYPE)), instrumentedMethod.getParameters().asTypeList());
            methodVisitor.visitVarInsn(21, 2);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitInsn(190);
            methodVisitor.visitJumpInsn(162, end);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitVarInsn(21, 2);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitVarInsn(21, 2);
            methodVisitor.visitInsn(50);
            methodVisitor.visitMethodInsn(184, implementationContext.getInstrumentedType().getInternalName(), "constant", Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class)), false);
            methodVisitor.visitInsn(83);
            methodVisitor.visitIincInsn(2, 1);
            methodVisitor.visitJumpInsn(167, loop);
            methodVisitor.visitLabel(end);
            implementationContext.getFrameGeneration().chop(methodVisitor, 1, CompoundList.of(instrumentedMethod.getParameters().asTypeList(), TypeDescription.ForLoadedType.of(Object[].class)));
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(4, 3);
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class ConstantTranslator
    implements ByteCodeAppender {
        protected static final String NAME = "constant";
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final Class<?> sourceHandle;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final Class<?> targetHandle;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final Class<?> sourceType;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final Class<?> targetType;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final Class<?> sourceConstantDynamic;
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final Class<?> targetConstantDynamic;

        protected ConstantTranslator(@MaybeNull Class<?> sourceHandle, @MaybeNull Class<?> targetHandle, @MaybeNull Class<?> sourceType, @MaybeNull Class<?> targetType, @MaybeNull Class<?> sourceConstantDynamic, @MaybeNull Class<?> targetConstantDynamic) {
            this.sourceHandle = sourceHandle;
            this.targetHandle = targetHandle;
            this.sourceType = sourceType;
            this.targetType = targetType;
            this.sourceConstantDynamic = sourceConstantDynamic;
            this.targetConstantDynamic = targetConstantDynamic;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label noType = new Label();
            Label noHandle = new Label();
            Label noConstantDynamic = new Label();
            if (this.sourceType != null && this.targetType != null) {
                methodVisitor.visitVarInsn(25, 0);
                methodVisitor.visitTypeInsn(193, Type.getInternalName(this.sourceType));
                methodVisitor.visitJumpInsn(153, noType);
                methodVisitor.visitVarInsn(25, 0);
                methodVisitor.visitTypeInsn(192, Type.getInternalName(this.sourceType));
                methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceType), "getDescriptor", Type.getMethodDescriptor(Type.getType(String.class), new Type[0]), false);
                methodVisitor.visitMethodInsn(184, Type.getInternalName(this.targetType), "getType", Type.getMethodDescriptor(Type.getType(this.targetType), Type.getType(String.class)), false);
                methodVisitor.visitInsn(176);
                methodVisitor.visitLabel(noType);
                implementationContext.getFrameGeneration().same(methodVisitor, instrumentedMethod.getParameters().asTypeList());
            }
            if (this.sourceHandle != null && this.targetHandle != null) {
                methodVisitor.visitVarInsn(25, 0);
                methodVisitor.visitTypeInsn(193, Type.getInternalName(this.sourceHandle));
                methodVisitor.visitJumpInsn(153, noHandle);
                methodVisitor.visitVarInsn(25, 0);
                methodVisitor.visitTypeInsn(192, Type.getInternalName(this.sourceHandle));
                methodVisitor.visitMethodInsn(184, implementationContext.getInstrumentedType().getInternalName(), "handle", Type.getMethodDescriptor(Type.getType(this.targetHandle), Type.getType(this.sourceHandle)), false);
                methodVisitor.visitInsn(176);
                methodVisitor.visitLabel(noHandle);
                implementationContext.getFrameGeneration().same(methodVisitor, instrumentedMethod.getParameters().asTypeList());
            }
            if (this.sourceConstantDynamic != null && this.targetConstantDynamic != null) {
                methodVisitor.visitVarInsn(25, 0);
                methodVisitor.visitTypeInsn(193, Type.getInternalName(this.sourceConstantDynamic));
                methodVisitor.visitJumpInsn(153, noConstantDynamic);
                methodVisitor.visitVarInsn(25, 0);
                methodVisitor.visitTypeInsn(192, Type.getInternalName(this.sourceConstantDynamic));
                methodVisitor.visitMethodInsn(184, implementationContext.getInstrumentedType().getInternalName(), "constantDyanmic", Type.getMethodDescriptor(Type.getType(this.targetConstantDynamic), Type.getType(this.sourceConstantDynamic)), false);
                methodVisitor.visitInsn(176);
                methodVisitor.visitLabel(noConstantDynamic);
                implementationContext.getFrameGeneration().same(methodVisitor, instrumentedMethod.getParameters().asTypeList());
            }
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(1, 1);
        }

        public boolean equals(@MaybeNull Object object) {
            block50: {
                block49: {
                    Class<?> clazz;
                    block48: {
                        Class<?> clazz2;
                        Class<?> clazz3;
                        block47: {
                            block46: {
                                Class<?> clazz4;
                                block45: {
                                    block44: {
                                        block43: {
                                            Class<?> clazz5;
                                            block42: {
                                                block41: {
                                                    block40: {
                                                        Class<?> clazz6;
                                                        block39: {
                                                            block38: {
                                                                block37: {
                                                                    Class<?> clazz7;
                                                                    block36: {
                                                                        block35: {
                                                                            block34: {
                                                                                Class<?> clazz8;
                                                                                block33: {
                                                                                    if (this == object) {
                                                                                        return true;
                                                                                    }
                                                                                    if (object == null) {
                                                                                        return false;
                                                                                    }
                                                                                    if (this.getClass() != object.getClass()) {
                                                                                        return false;
                                                                                    }
                                                                                    clazz3 = ((ConstantTranslator)object).sourceHandle;
                                                                                    clazz8 = clazz2 = this.sourceHandle;
                                                                                    if (clazz3 == null) break block33;
                                                                                    if (clazz8 == null) break block34;
                                                                                    if (!clazz2.equals(clazz3)) {
                                                                                        return false;
                                                                                    }
                                                                                    break block35;
                                                                                }
                                                                                if (clazz8 == null) break block35;
                                                                            }
                                                                            return false;
                                                                        }
                                                                        clazz3 = ((ConstantTranslator)object).targetHandle;
                                                                        clazz7 = clazz2 = this.targetHandle;
                                                                        if (clazz3 == null) break block36;
                                                                        if (clazz7 == null) break block37;
                                                                        if (!clazz2.equals(clazz3)) {
                                                                            return false;
                                                                        }
                                                                        break block38;
                                                                    }
                                                                    if (clazz7 == null) break block38;
                                                                }
                                                                return false;
                                                            }
                                                            clazz3 = ((ConstantTranslator)object).sourceType;
                                                            clazz6 = clazz2 = this.sourceType;
                                                            if (clazz3 == null) break block39;
                                                            if (clazz6 == null) break block40;
                                                            if (!clazz2.equals(clazz3)) {
                                                                return false;
                                                            }
                                                            break block41;
                                                        }
                                                        if (clazz6 == null) break block41;
                                                    }
                                                    return false;
                                                }
                                                clazz3 = ((ConstantTranslator)object).targetType;
                                                clazz5 = clazz2 = this.targetType;
                                                if (clazz3 == null) break block42;
                                                if (clazz5 == null) break block43;
                                                if (!clazz2.equals(clazz3)) {
                                                    return false;
                                                }
                                                break block44;
                                            }
                                            if (clazz5 == null) break block44;
                                        }
                                        return false;
                                    }
                                    clazz3 = ((ConstantTranslator)object).sourceConstantDynamic;
                                    clazz4 = clazz2 = this.sourceConstantDynamic;
                                    if (clazz3 == null) break block45;
                                    if (clazz4 == null) break block46;
                                    if (!clazz2.equals(clazz3)) {
                                        return false;
                                    }
                                    break block47;
                                }
                                if (clazz4 == null) break block47;
                            }
                            return false;
                        }
                        clazz3 = ((ConstantTranslator)object).targetConstantDynamic;
                        clazz = clazz2 = this.targetConstantDynamic;
                        if (clazz3 == null) break block48;
                        if (clazz == null) break block49;
                        if (!clazz2.equals(clazz3)) {
                            return false;
                        }
                        break block50;
                    }
                    if (clazz == null) break block50;
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            int n = this.getClass().hashCode() * 31;
            Class<?> clazz = this.sourceHandle;
            if (clazz != null) {
                n = n + clazz.hashCode();
            }
            int n2 = n * 31;
            clazz = this.targetHandle;
            if (clazz != null) {
                n2 = n2 + clazz.hashCode();
            }
            int n3 = n2 * 31;
            clazz = this.sourceType;
            if (clazz != null) {
                n3 = n3 + clazz.hashCode();
            }
            int n4 = n3 * 31;
            clazz = this.targetType;
            if (clazz != null) {
                n4 = n4 + clazz.hashCode();
            }
            int n5 = n4 * 31;
            clazz = this.sourceConstantDynamic;
            if (clazz != null) {
                n5 = n5 + clazz.hashCode();
            }
            int n6 = n5 * 31;
            clazz = this.targetConstantDynamic;
            if (clazz != null) {
                n6 = n6 + clazz.hashCode();
            }
            return n6;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class ConstantDynamicTranslator
    implements ByteCodeAppender {
        protected static final String NAME = "constantDyanmic";
        private final Class<?> sourceConstantDynamic;
        private final Class<?> targetConstantDynamic;
        private final Class<?> sourceHandle;
        private final Class<?> targetHandle;

        protected ConstantDynamicTranslator(Class<?> sourceConstantDynamic, Class<?> targetConstantDynamic, Class<?> sourceHandle, Class<?> targetHandle) {
            this.sourceConstantDynamic = sourceConstantDynamic;
            this.targetConstantDynamic = targetConstantDynamic;
            this.sourceHandle = sourceHandle;
            this.targetHandle = targetHandle;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label loop = new Label();
            Label end = new Label();
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceConstantDynamic), "getBootstrapMethodArgumentCount", Type.getMethodDescriptor(Type.INT_TYPE, new Type[0]), false);
            methodVisitor.visitTypeInsn(189, Type.getInternalName(Object.class));
            methodVisitor.visitVarInsn(58, 1);
            methodVisitor.visitInsn(3);
            methodVisitor.visitVarInsn(54, 2);
            methodVisitor.visitLabel(loop);
            implementationContext.getFrameGeneration().append(methodVisitor, Arrays.asList(TypeDescription.ForLoadedType.of(Object[].class), TypeDescription.ForLoadedType.of(Integer.TYPE)), instrumentedMethod.getParameters().asTypeList());
            methodVisitor.visitVarInsn(21, 2);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitInsn(190);
            methodVisitor.visitJumpInsn(162, end);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitVarInsn(21, 2);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitVarInsn(21, 2);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceConstantDynamic), "getBootstrapMethodArgument", Type.getMethodDescriptor(Type.getType(Object.class), Type.INT_TYPE), false);
            methodVisitor.visitMethodInsn(184, implementationContext.getInstrumentedType().getInternalName(), "ldc", Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class)), false);
            methodVisitor.visitInsn(83);
            methodVisitor.visitIincInsn(2, 1);
            methodVisitor.visitJumpInsn(167, loop);
            methodVisitor.visitLabel(end);
            implementationContext.getFrameGeneration().chop(methodVisitor, 1, CompoundList.of(instrumentedMethod.getParameters().asTypeList(), TypeDescription.ForLoadedType.of(Object[].class)));
            methodVisitor.visitTypeInsn(187, Type.getInternalName(this.targetConstantDynamic));
            methodVisitor.visitInsn(89);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceConstantDynamic), "getName", Type.getMethodDescriptor(Type.getType(String.class), new Type[0]), false);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceConstantDynamic), "getDescriptor", Type.getMethodDescriptor(Type.getType(String.class), new Type[0]), false);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceConstantDynamic), "getBootstrapMethod", Type.getMethodDescriptor(Type.getType(this.sourceHandle), new Type[0]), false);
            methodVisitor.visitMethodInsn(184, implementationContext.getInstrumentedType().getInternalName(), "handle", Type.getMethodDescriptor(Type.getType(this.targetHandle), Type.getType(this.sourceHandle)), false);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitMethodInsn(183, Type.getInternalName(this.targetConstantDynamic), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.getType(String.class), Type.getType(this.targetHandle), Type.getType(Object[].class)), false);
            methodVisitor.visitInsn(176);
            methodVisitor.visitMaxs(6, 3);
            return new ByteCodeAppender.Size(6, 3);
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
            if (!this.sourceConstantDynamic.equals(((ConstantDynamicTranslator)object).sourceConstantDynamic)) {
                return false;
            }
            if (!this.targetConstantDynamic.equals(((ConstantDynamicTranslator)object).targetConstantDynamic)) {
                return false;
            }
            if (!this.sourceHandle.equals(((ConstantDynamicTranslator)object).sourceHandle)) {
                return false;
            }
            return this.targetHandle.equals(((ConstantDynamicTranslator)object).targetHandle);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.sourceConstantDynamic.hashCode()) * 31 + this.targetConstantDynamic.hashCode()) * 31 + this.sourceHandle.hashCode()) * 31 + this.targetHandle.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class HandleTranslator
    implements ByteCodeAppender {
        protected static final String NAME = "handle";
        private final Class<?> sourceHandle;
        private final Class<?> targetHandle;

        protected HandleTranslator(Class<?> sourceHandle, Class<?> targetHandle) {
            this.sourceHandle = sourceHandle;
            this.targetHandle = targetHandle;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label nullCheck = new Label();
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitJumpInsn(199, nullCheck);
            methodVisitor.visitInsn(1);
            methodVisitor.visitInsn(176);
            methodVisitor.visitLabel(nullCheck);
            implementationContext.getFrameGeneration().same(methodVisitor, instrumentedMethod.getParameters().asTypeList());
            methodVisitor.visitTypeInsn(187, Type.getInternalName(this.targetHandle));
            methodVisitor.visitInsn(89);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceHandle), "getTag", Type.getMethodDescriptor(Type.INT_TYPE, new Type[0]), false);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceHandle), "getOwner", Type.getMethodDescriptor(Type.getType(String.class), new Type[0]), false);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceHandle), "getName", Type.getMethodDescriptor(Type.getType(String.class), new Type[0]), false);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceHandle), "getDesc", Type.getMethodDescriptor(Type.getType(String.class), new Type[0]), false);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(182, Type.getInternalName(this.sourceHandle), "isInterface", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[0]), false);
            methodVisitor.visitMethodInsn(183, Type.getInternalName(this.targetHandle), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE, Type.getType(String.class), Type.getType(String.class), Type.getType(String.class), Type.BOOLEAN_TYPE), false);
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(7, 1);
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
            if (!this.sourceHandle.equals(((HandleTranslator)object).sourceHandle)) {
                return false;
            }
            return this.targetHandle.equals(((HandleTranslator)object).targetHandle);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.sourceHandle.hashCode()) * 31 + this.targetHandle.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class LabelArrayTranslator
    implements ByteCodeAppender {
        protected static final String NAME = "labels";
        private final Class<?> sourceLabel;
        private final Class<?> targetLabel;

        protected LabelArrayTranslator(Class<?> sourceLabel, Class<?> targetLabel) {
            this.sourceLabel = sourceLabel;
            this.targetLabel = targetLabel;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label nullCheck = new Label();
            Label loop = new Label();
            Label end = new Label();
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitJumpInsn(199, nullCheck);
            methodVisitor.visitInsn(1);
            methodVisitor.visitInsn(176);
            methodVisitor.visitLabel(nullCheck);
            implementationContext.getFrameGeneration().same(methodVisitor, CompoundList.of(implementationContext.getInstrumentedType(), instrumentedMethod.getParameters().asTypeList()));
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitInsn(190);
            methodVisitor.visitTypeInsn(189, Type.getInternalName(this.targetLabel));
            methodVisitor.visitVarInsn(58, 2);
            methodVisitor.visitInsn(3);
            methodVisitor.visitVarInsn(54, 3);
            methodVisitor.visitLabel(loop);
            implementationContext.getFrameGeneration().append(methodVisitor, Arrays.asList(TypeDescription.ArrayProjection.of(TypeDescription.ForLoadedType.of(this.targetLabel)), TypeDescription.ForLoadedType.of(Integer.TYPE)), CompoundList.of(implementationContext.getInstrumentedType(), instrumentedMethod.getParameters().asTypeList()));
            methodVisitor.visitVarInsn(21, 3);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitInsn(190);
            methodVisitor.visitJumpInsn(162, end);
            methodVisitor.visitVarInsn(25, 2);
            methodVisitor.visitVarInsn(21, 3);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitVarInsn(21, 3);
            methodVisitor.visitInsn(50);
            methodVisitor.visitMethodInsn(183, implementationContext.getInstrumentedType().getInternalName(), "label", Type.getMethodDescriptor(Type.getType(this.targetLabel), Type.getType(this.sourceLabel)), false);
            methodVisitor.visitInsn(83);
            methodVisitor.visitIincInsn(3, 1);
            methodVisitor.visitJumpInsn(167, loop);
            methodVisitor.visitLabel(end);
            implementationContext.getFrameGeneration().chop(methodVisitor, 1, CompoundList.of(Collections.singletonList(implementationContext.getInstrumentedType()), instrumentedMethod.getParameters().asTypeList(), Collections.singletonList(TypeDescription.ArrayProjection.of(TypeDescription.ForLoadedType.of(this.targetLabel)))));
            methodVisitor.visitVarInsn(25, 2);
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(5, 4);
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
            if (!this.sourceLabel.equals(((LabelArrayTranslator)object).sourceLabel)) {
                return false;
            }
            return this.targetLabel.equals(((LabelArrayTranslator)object).targetLabel);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.sourceLabel.hashCode()) * 31 + this.targetLabel.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class LabelTranslator
    implements ByteCodeAppender {
        protected static final String NAME = "label";
        private final Class<?> target;

        protected LabelTranslator(Class<?> target) {
            this.target = target;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            Label nullCheck = new Label();
            Label end = new Label();
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitJumpInsn(199, nullCheck);
            methodVisitor.visitInsn(1);
            methodVisitor.visitInsn(176);
            methodVisitor.visitLabel(nullCheck);
            implementationContext.getFrameGeneration().same(methodVisitor, CompoundList.of(implementationContext.getInstrumentedType(), instrumentedMethod.getParameters().asTypeList()));
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitFieldInsn(180, implementationContext.getInstrumentedType().getInternalName(), ClassVisitorFactory.LABELS, Type.getDescriptor(Map.class));
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitMethodInsn(185, Type.getInternalName(Map.class), "get", Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class)), true);
            methodVisitor.visitTypeInsn(192, Type.getInternalName(this.target));
            methodVisitor.visitVarInsn(58, 2);
            methodVisitor.visitVarInsn(25, 2);
            methodVisitor.visitJumpInsn(199, end);
            methodVisitor.visitTypeInsn(187, Type.getInternalName(this.target));
            methodVisitor.visitInsn(89);
            methodVisitor.visitMethodInsn(183, Type.getInternalName(this.target), "<init>", "()V", false);
            methodVisitor.visitVarInsn(58, 2);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitFieldInsn(180, implementationContext.getInstrumentedType().getInternalName(), ClassVisitorFactory.LABELS, Type.getDescriptor(Map.class));
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitVarInsn(25, 2);
            methodVisitor.visitMethodInsn(185, Type.getInternalName(Map.class), "put", Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class), Type.getType(Object.class)), true);
            methodVisitor.visitInsn(87);
            methodVisitor.visitLabel(end);
            implementationContext.getFrameGeneration().append(methodVisitor, Collections.singletonList(TypeDescription.ForLoadedType.of(this.target)), CompoundList.of(implementationContext.getInstrumentedType(), instrumentedMethod.getParameters().asTypeList()));
            methodVisitor.visitVarInsn(25, 2);
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(3, 3);
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
            return this.target.equals(((LabelTranslator)object).target);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.target.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class NullCheckedConstruction
    implements ByteCodeAppender {
        private final Class<?> type;

        protected NullCheckedConstruction(Class<?> type) {
            this.type = type;
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitVarInsn(25, 0);
            Label label = new Label();
            methodVisitor.visitJumpInsn(198, label);
            methodVisitor.visitTypeInsn(187, implementationContext.getInstrumentedType().getInternalName());
            methodVisitor.visitInsn(89);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(183, implementationContext.getInstrumentedType().getInternalName(), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(this.type)), false);
            methodVisitor.visitInsn(176);
            methodVisitor.visitLabel(label);
            implementationContext.getFrameGeneration().same(methodVisitor, instrumentedMethod.getParameters().asTypeList());
            methodVisitor.visitInsn(1);
            methodVisitor.visitInsn(176);
            return new ByteCodeAppender.Size(3, 1);
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
            return this.type.equals(((NullCheckedConstruction)object).type);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.type.hashCode();
        }
    }
}

