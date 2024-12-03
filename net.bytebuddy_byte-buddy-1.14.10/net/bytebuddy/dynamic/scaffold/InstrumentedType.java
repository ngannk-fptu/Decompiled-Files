/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic.scaffold;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.RecordComponentList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface InstrumentedType
extends TypeDescription {
    public InstrumentedType withField(FieldDescription.Token var1);

    public InstrumentedType withAuxiliaryField(FieldDescription.Token var1, Object var2);

    public InstrumentedType withMethod(MethodDescription.Token var1);

    public InstrumentedType withRecordComponent(RecordComponentDescription.Token var1);

    public InstrumentedType withModifiers(int var1);

    public InstrumentedType withInterfaces(TypeList.Generic var1);

    public InstrumentedType withTypeVariable(TypeVariableToken var1);

    public InstrumentedType withAnnotations(List<? extends AnnotationDescription> var1);

    public InstrumentedType withNestHost(TypeDescription var1);

    public InstrumentedType withNestMembers(TypeList var1);

    public InstrumentedType withEnclosingType(TypeDescription var1);

    public InstrumentedType withEnclosingMethod(MethodDescription.InDefinedShape var1);

    public InstrumentedType withDeclaringType(@MaybeNull TypeDescription var1);

    public InstrumentedType withDeclaredTypes(TypeList var1);

    public InstrumentedType withPermittedSubclasses(@MaybeNull TypeList var1);

    public InstrumentedType withLocalClass(boolean var1);

    public InstrumentedType withAnonymousClass(boolean var1);

    public InstrumentedType withRecord(boolean var1);

    public InstrumentedType withInitializer(LoadedTypeInitializer var1);

    public InstrumentedType withInitializer(ByteCodeAppender var1);

    public LoadedTypeInitializer getLoadedTypeInitializer();

    public TypeInitializer getTypeInitializer();

    public TypeDescription validated();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Frozen
    extends TypeDescription.AbstractBase.OfSimpleType
    implements WithFlexibleName {
        private final TypeDescription typeDescription;
        private final LoadedTypeInitializer loadedTypeInitializer;

        protected Frozen(TypeDescription typeDescription, LoadedTypeInitializer loadedTypeInitializer) {
            this.typeDescription = typeDescription;
            this.loadedTypeInitializer = loadedTypeInitializer;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return this.typeDescription.getDeclaredAnnotations();
        }

        @Override
        public int getModifiers() {
            return this.typeDescription.getModifiers();
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            return this.typeDescription.getTypeVariables();
        }

        @Override
        public String getName() {
            return this.typeDescription.getName();
        }

        @Override
        @MaybeNull
        public TypeDescription.Generic getSuperClass() {
            return this.typeDescription.getSuperClass();
        }

        @Override
        public TypeList.Generic getInterfaces() {
            return this.typeDescription.getInterfaces();
        }

        @Override
        public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
            return this.typeDescription.getDeclaredFields();
        }

        @Override
        public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
            return this.typeDescription.getDeclaredMethods();
        }

        @Override
        public boolean isAnonymousType() {
            return this.typeDescription.isAnonymousType();
        }

        @Override
        public boolean isLocalType() {
            return this.typeDescription.isLocalType();
        }

        @Override
        @MaybeNull
        public PackageDescription getPackage() {
            return this.typeDescription.getPackage();
        }

        @Override
        @MaybeNull
        public TypeDescription getEnclosingType() {
            return this.typeDescription.getEnclosingType();
        }

        @Override
        @MaybeNull
        public TypeDescription getDeclaringType() {
            return this.typeDescription.getDeclaringType();
        }

        @Override
        public TypeList getDeclaredTypes() {
            return this.typeDescription.getDeclaredTypes();
        }

        @Override
        @MaybeNull
        public MethodDescription.InDefinedShape getEnclosingMethod() {
            return this.typeDescription.getEnclosingMethod();
        }

        @Override
        @MaybeNull
        public String getGenericSignature() {
            return this.typeDescription.getGenericSignature();
        }

        @Override
        public int getActualModifiers(boolean superFlag) {
            return this.typeDescription.getActualModifiers(superFlag);
        }

        @Override
        public TypeDescription getNestHost() {
            return this.typeDescription.getNestHost();
        }

        @Override
        public TypeList getNestMembers() {
            return this.typeDescription.getNestMembers();
        }

        @Override
        public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents() {
            return this.typeDescription.getRecordComponents();
        }

        @Override
        public boolean isRecord() {
            return this.typeDescription.isRecord();
        }

        @Override
        public boolean isSealed() {
            return this.typeDescription.isSealed();
        }

        @Override
        public TypeList getPermittedSubtypes() {
            return this.typeDescription.getPermittedSubtypes();
        }

        @Override
        public WithFlexibleName withField(FieldDescription.Token token) {
            throw new IllegalStateException("Cannot define field for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withAuxiliaryField(FieldDescription.Token token, Object value) {
            throw new IllegalStateException("Cannot define auxiliary field for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withMethod(MethodDescription.Token token) {
            throw new IllegalStateException("Cannot define method for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withRecordComponent(RecordComponentDescription.Token token) {
            throw new IllegalStateException("Cannot define record component for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withModifiers(int modifiers) {
            throw new IllegalStateException("Cannot change modifiers for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withInterfaces(TypeList.Generic interfaceTypes) {
            throw new IllegalStateException("Cannot add interfaces for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withTypeVariable(TypeVariableToken typeVariable) {
            throw new IllegalStateException("Cannot define type variable for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withAnnotations(List<? extends AnnotationDescription> annotationDescriptions) {
            throw new IllegalStateException("Cannot add annotation to frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withNestHost(TypeDescription nestHost) {
            throw new IllegalStateException("Cannot set nest host of frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withNestMembers(TypeList nestMembers) {
            throw new IllegalStateException("Cannot add nest members to frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withEnclosingType(@MaybeNull TypeDescription enclosingType) {
            throw new IllegalStateException("Cannot set enclosing type of frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withEnclosingMethod(MethodDescription.InDefinedShape enclosingMethod) {
            throw new IllegalStateException("Cannot set enclosing method of frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withDeclaringType(@MaybeNull TypeDescription declaringType) {
            throw new IllegalStateException("Cannot add declaring type to frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withDeclaredTypes(TypeList declaredTypes) {
            throw new IllegalStateException("Cannot add declared types to frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withPermittedSubclasses(@MaybeNull TypeList permittedSubclasses) {
            throw new IllegalStateException("Cannot add permitted subclasses to frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withLocalClass(boolean localClass) {
            throw new IllegalStateException("Cannot define local class state for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withAnonymousClass(boolean anonymousClass) {
            throw new IllegalStateException("Cannot define anonymous class state for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withRecord(boolean record) {
            throw new IllegalStateException("Cannot define record state for frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withInitializer(LoadedTypeInitializer loadedTypeInitializer) {
            return new Frozen(this.typeDescription, new LoadedTypeInitializer.Compound(this.loadedTypeInitializer, loadedTypeInitializer));
        }

        @Override
        public WithFlexibleName withInitializer(ByteCodeAppender byteCodeAppender) {
            throw new IllegalStateException("Cannot add initializer to frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withName(String name) {
            throw new IllegalStateException("Cannot change name of frozen type: " + this.typeDescription);
        }

        @Override
        public WithFlexibleName withTypeVariables(ElementMatcher<? super TypeDescription.Generic> matcher, Transformer<TypeVariableToken> transformer) {
            throw new IllegalStateException("Cannot add type variables of frozen type: " + this.typeDescription);
        }

        @Override
        public LoadedTypeInitializer getLoadedTypeInitializer() {
            return this.loadedTypeInitializer;
        }

        @Override
        public TypeInitializer getTypeInitializer() {
            return TypeInitializer.None.INSTANCE;
        }

        @Override
        @MaybeNull
        public ClassFileVersion getClassFileVersion() {
            return this.typeDescription.getClassFileVersion();
        }

        @Override
        public TypeDescription validated() {
            return this.typeDescription;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Default
    extends TypeDescription.AbstractBase.OfSimpleType
    implements WithFlexibleName {
        private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList("abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while"));
        private final String name;
        private final int modifiers;
        @MaybeNull
        private final TypeDescription.Generic superClass;
        private final List<? extends TypeVariableToken> typeVariables;
        private final List<? extends TypeDescription.Generic> interfaceTypes;
        private final List<? extends FieldDescription.Token> fieldTokens;
        private final Map<String, Object> auxiliaryFields;
        private final List<? extends MethodDescription.Token> methodTokens;
        private final List<? extends RecordComponentDescription.Token> recordComponentTokens;
        private final List<? extends AnnotationDescription> annotationDescriptions;
        private final TypeInitializer typeInitializer;
        private final LoadedTypeInitializer loadedTypeInitializer;
        @MaybeNull
        private final TypeDescription declaringType;
        @MaybeNull
        private final MethodDescription.InDefinedShape enclosingMethod;
        @MaybeNull
        private final TypeDescription enclosingType;
        private final List<? extends TypeDescription> declaredTypes;
        @MaybeNull
        private final List<? extends TypeDescription> permittedSubclasses;
        private final boolean anonymousClass;
        private final boolean localClass;
        private final boolean record;
        private final TypeDescription nestHost;
        private final List<? extends TypeDescription> nestMembers;

        protected Default(String name, int modifiers, @MaybeNull TypeDescription.Generic superClass, List<? extends TypeVariableToken> typeVariables, List<? extends TypeDescription.Generic> interfaceTypes, List<? extends FieldDescription.Token> fieldTokens, Map<String, Object> auxiliaryFieldValues, List<? extends MethodDescription.Token> methodTokens, List<? extends RecordComponentDescription.Token> recordComponentTokens, List<? extends AnnotationDescription> annotationDescriptions, TypeInitializer typeInitializer, LoadedTypeInitializer loadedTypeInitializer, @MaybeNull TypeDescription declaringType, @MaybeNull MethodDescription.InDefinedShape enclosingMethod, @MaybeNull TypeDescription enclosingType, List<? extends TypeDescription> declaredTypes, @MaybeNull List<? extends TypeDescription> permittedSubclasses, boolean anonymousClass, boolean localClass, boolean record, TypeDescription nestHost, List<? extends TypeDescription> nestMembers) {
            this.name = name;
            this.modifiers = modifiers;
            this.typeVariables = typeVariables;
            this.superClass = superClass;
            this.interfaceTypes = interfaceTypes;
            this.fieldTokens = fieldTokens;
            this.auxiliaryFields = auxiliaryFieldValues;
            this.methodTokens = methodTokens;
            this.recordComponentTokens = recordComponentTokens;
            this.annotationDescriptions = annotationDescriptions;
            this.typeInitializer = typeInitializer;
            this.loadedTypeInitializer = loadedTypeInitializer;
            this.declaringType = declaringType;
            this.enclosingMethod = enclosingMethod;
            this.enclosingType = enclosingType;
            this.declaredTypes = declaredTypes;
            this.permittedSubclasses = permittedSubclasses;
            this.anonymousClass = anonymousClass;
            this.localClass = localClass;
            this.record = record;
            this.nestHost = nestHost;
            this.nestMembers = nestMembers;
        }

        public static InstrumentedType of(String name, TypeDescription.Generic superClass, ModifierContributor.ForType ... modifierContributor) {
            return Default.of(name, superClass, ModifierContributor.Resolver.of(modifierContributor).resolve());
        }

        public static InstrumentedType of(String name, TypeDescription.Generic superClass, int modifiers) {
            return Factory.Default.MODIFIABLE.subclass(name, modifiers, superClass);
        }

        @Override
        public WithFlexibleName withModifiers(int modifiers) {
            return new Default(this.name, modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withField(FieldDescription.Token token) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, CompoundList.of(this.fieldTokens, token.accept(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(this))), this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withAuxiliaryField(FieldDescription.Token token, Object value) {
            HashMap<String, Object> auxiliaryFields = new HashMap<String, Object>(this.auxiliaryFields);
            Object previous = auxiliaryFields.put(token.getName(), value);
            if (previous != null) {
                if (previous == value) {
                    return this;
                }
                throw new IllegalStateException("Field " + token.getName() + " for " + this + " already mapped to " + previous + " and not " + value);
            }
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, CompoundList.of(this.fieldTokens, token.accept(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(this))), auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, new LoadedTypeInitializer.Compound(this.loadedTypeInitializer, new LoadedTypeInitializer.ForStaticField(token.getName(), value)), this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withMethod(MethodDescription.Token token) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, CompoundList.of(this.methodTokens, token.accept(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(this))), this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withRecordComponent(RecordComponentDescription.Token token) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, CompoundList.of(this.recordComponentTokens, token.accept(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(this))), this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, true, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withInterfaces(TypeList.Generic interfaceTypes) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, CompoundList.of(this.interfaceTypes, interfaceTypes.accept(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(this))), this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withAnnotations(List<? extends AnnotationDescription> annotationDescriptions) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, CompoundList.of(this.annotationDescriptions, annotationDescriptions), this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withNestHost(TypeDescription nestHost) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, nestHost.equals(this) ? TargetType.DESCRIPTION : nestHost, Collections.emptyList());
        }

        @Override
        public WithFlexibleName withNestMembers(TypeList nestMembers) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, TargetType.DESCRIPTION, CompoundList.of(this.nestMembers, nestMembers));
        }

        @Override
        public WithFlexibleName withEnclosingType(@MaybeNull TypeDescription enclosingType) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, MethodDescription.UNDEFINED, enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withEnclosingMethod(MethodDescription.InDefinedShape enclosingMethod) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, enclosingMethod, enclosingMethod.getDeclaringType(), this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withDeclaringType(@MaybeNull TypeDescription declaringType) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withDeclaredTypes(TypeList declaredTypes) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, CompoundList.of(this.declaredTypes, declaredTypes), this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withPermittedSubclasses(@MaybeNull TypeList permittedSubclasses) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, (List<? extends TypeDescription>)(permittedSubclasses == null || this.permittedSubclasses == null ? permittedSubclasses : CompoundList.of(this.permittedSubclasses, permittedSubclasses)), this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withTypeVariable(TypeVariableToken typeVariable) {
            return new Default(this.name, this.modifiers, this.superClass, CompoundList.of(this.typeVariables, typeVariable.accept(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(this))), this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withName(String name) {
            return new Default(name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withTypeVariables(ElementMatcher<? super TypeDescription.Generic> matcher, Transformer<TypeVariableToken> transformer) {
            ArrayList<TypeVariableToken> typeVariables = new ArrayList<TypeVariableToken>(this.typeVariables.size());
            int index = 0;
            for (TypeVariableToken typeVariableToken : this.typeVariables) {
                typeVariables.add(matcher.matches((TypeDescription.Generic)this.getTypeVariables().get(index++)) ? transformer.transform(this, typeVariableToken) : typeVariableToken);
            }
            return new Default(this.name, this.modifiers, this.superClass, typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withLocalClass(boolean localClass) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, false, localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withAnonymousClass(boolean anonymousClass) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, anonymousClass, false, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withRecord(boolean record) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, record ? this.recordComponentTokens : Collections.emptyList(), this.annotationDescriptions, this.typeInitializer, this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withInitializer(LoadedTypeInitializer loadedTypeInitializer) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer, new LoadedTypeInitializer.Compound(this.loadedTypeInitializer, loadedTypeInitializer), this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public WithFlexibleName withInitializer(ByteCodeAppender byteCodeAppender) {
            return new Default(this.name, this.modifiers, this.superClass, this.typeVariables, this.interfaceTypes, this.fieldTokens, this.auxiliaryFields, this.methodTokens, this.recordComponentTokens, this.annotationDescriptions, this.typeInitializer.expandWith(byteCodeAppender), this.loadedTypeInitializer, this.declaringType, this.enclosingMethod, this.enclosingType, this.declaredTypes, this.permittedSubclasses, this.anonymousClass, this.localClass, this.record, this.nestHost, this.nestMembers);
        }

        @Override
        public LoadedTypeInitializer getLoadedTypeInitializer() {
            return this.loadedTypeInitializer;
        }

        @Override
        public TypeInitializer getTypeInitializer() {
            return this.typeInitializer;
        }

        @Override
        @MaybeNull
        public MethodDescription.InDefinedShape getEnclosingMethod() {
            return this.enclosingMethod;
        }

        @Override
        @MaybeNull
        public TypeDescription getEnclosingType() {
            return this.enclosingType;
        }

        @Override
        public TypeList getDeclaredTypes() {
            return new TypeList.Explicit(this.declaredTypes);
        }

        @Override
        public boolean isAnonymousType() {
            return this.anonymousClass;
        }

        @Override
        public boolean isLocalType() {
            return this.localClass;
        }

        @Override
        @MaybeNull
        public PackageDescription getPackage() {
            int packageIndex = this.name.lastIndexOf(46);
            return packageIndex == -1 ? PackageDescription.DEFAULT : new PackageDescription.Simple(this.name.substring(0, packageIndex));
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Explicit(this.annotationDescriptions);
        }

        @Override
        @MaybeNull
        public TypeDescription getDeclaringType() {
            return this.declaringType;
        }

        @Override
        @MaybeNull
        public TypeDescription.Generic getSuperClass() {
            return this.superClass == null ? TypeDescription.Generic.UNDEFINED : new TypeDescription.Generic.LazyProjection.WithResolvedErasure(this.superClass, TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(this));
        }

        @Override
        public TypeList.Generic getInterfaces() {
            return new TypeList.Generic.ForDetachedTypes.WithResolvedErasure(this.interfaceTypes, TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(this));
        }

        @Override
        public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
            return new FieldList.ForTokens((TypeDescription)this, this.fieldTokens);
        }

        @Override
        public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
            return new MethodList.ForTokens((TypeDescription)this, this.methodTokens);
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            return TypeList.Generic.ForDetachedTypes.attachVariables(this, this.typeVariables);
        }

        @Override
        public int getModifiers() {
            return this.modifiers;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public TypeDescription getNestHost() {
            return this.nestHost.represents((Type)((Object)TargetType.class)) ? this : this.nestHost;
        }

        @Override
        public TypeList getNestMembers() {
            return this.nestHost.represents((Type)((Object)TargetType.class)) ? new TypeList.Explicit(CompoundList.of(this, this.nestMembers)) : this.nestHost.getNestMembers();
        }

        @Override
        public RecordComponentList<RecordComponentDescription.InDefinedShape> getRecordComponents() {
            return new RecordComponentList.ForTokens((TypeDescription)this, this.recordComponentTokens);
        }

        @Override
        @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming super class for given instance.")
        public boolean isRecord() {
            return this.record && this.superClass != null && this.getSuperClass().asErasure().equals(JavaType.RECORD.getTypeStub());
        }

        @Override
        public boolean isSealed() {
            return this.permittedSubclasses != null;
        }

        @Override
        public TypeList getPermittedSubtypes() {
            return (TypeList)((Object)(this.permittedSubclasses == null ? new TypeList.Empty() : new TypeList.Explicit(this.permittedSubclasses)));
        }

        @Override
        public TypeDescription validated() {
            if (!Default.isValidIdentifier(this.getName().split("\\."))) {
                throw new IllegalStateException("Illegal type name: " + this.getName() + " for " + this);
            }
            if ((this.getModifiers() & 0xFFFD89E0) != 0) {
                throw new IllegalStateException("Illegal modifiers " + this.getModifiers() + " for " + this);
            }
            if (this.isPackageType() && this.getModifiers() != 5632) {
                throw new IllegalStateException("Illegal modifiers " + this.getModifiers() + " for package " + this);
            }
            TypeDescription.Generic superClass = this.getSuperClass();
            if (superClass != null) {
                if (!superClass.accept(TypeDescription.Generic.Visitor.Validator.SUPER_CLASS).booleanValue()) {
                    throw new IllegalStateException("Illegal super class " + superClass + " for " + this);
                }
                if (!superClass.accept(TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.INSTANCE).booleanValue()) {
                    throw new IllegalStateException("Illegal type annotations on super class " + superClass + " for " + this);
                }
                if (!superClass.asErasure().isVisibleTo(this)) {
                    throw new IllegalStateException("Invisible super type " + superClass + " for " + this);
                }
            }
            HashSet<TypeDescription> interfaceErasures = new HashSet<TypeDescription>();
            for (TypeDescription.Generic interfaceType : this.getInterfaces()) {
                if (!interfaceType.accept(TypeDescription.Generic.Visitor.Validator.INTERFACE).booleanValue()) {
                    throw new IllegalStateException("Illegal interface " + interfaceType + " for " + this);
                }
                if (!interfaceType.accept(TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.INSTANCE).booleanValue()) {
                    throw new IllegalStateException("Illegal type annotations on interface " + interfaceType + " for " + this);
                }
                if (!interfaceErasures.add(interfaceType.asErasure())) {
                    throw new IllegalStateException("Already implemented interface " + interfaceType + " for " + this);
                }
                if (interfaceType.asErasure().isVisibleTo(this)) continue;
                throw new IllegalStateException("Invisible interface type " + interfaceType + " for " + this);
            }
            TypeList.Generic typeVariables = this.getTypeVariables();
            if (!typeVariables.isEmpty() && this.isAssignableTo(Throwable.class)) {
                throw new IllegalStateException("Cannot define throwable " + this + " to be generic");
            }
            HashSet<String> typeVariableNames = new HashSet<String>();
            for (TypeDescription.Generic typeVariable : typeVariables) {
                String variableSymbol = typeVariable.getSymbol();
                if (!typeVariableNames.add(variableSymbol)) {
                    throw new IllegalStateException("Duplicate type variable symbol '" + typeVariable + "' for " + this);
                }
                if (!Default.isValidIdentifier(variableSymbol)) {
                    throw new IllegalStateException("Illegal type variable name '" + typeVariable + "' for " + this);
                }
                if (!TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.ofFormalTypeVariable(typeVariable)) {
                    throw new IllegalStateException("Illegal type annotation on '" + typeVariable + "' for " + this);
                }
                boolean interfaceBound = false;
                HashSet bounds = new HashSet();
                for (Object bound : typeVariable.getUpperBounds()) {
                    if (!bound.accept(TypeDescription.Generic.Visitor.Validator.TYPE_VARIABLE).booleanValue()) {
                        throw new IllegalStateException("Illegal type variable bound " + bound + " of " + typeVariable + " for " + this);
                    }
                    if (!bound.accept(TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.INSTANCE).booleanValue()) {
                        throw new IllegalStateException("Illegal type annotations on type variable " + bound + " for " + this);
                    }
                    if (!bounds.add(bound)) {
                        throw new IllegalStateException("Duplicate bound " + bound + " of " + typeVariable + " for " + this);
                    }
                    if (interfaceBound && (bound.getSort().isTypeVariable() || !bound.isInterface())) {
                        throw new IllegalStateException("Illegal interface bound " + bound + " of " + typeVariable + " for " + this);
                    }
                    interfaceBound = true;
                }
                if (interfaceBound) continue;
                throw new IllegalStateException("Type variable " + typeVariable + " for " + this + " does not define at least one bound");
            }
            TypeDescription enclosingType = this.getEnclosingType();
            if (enclosingType != null && (enclosingType.isArray() || enclosingType.isPrimitive())) {
                throw new IllegalStateException("Cannot define array type or primitive type " + enclosingType + " + as enclosing type for " + this);
            }
            MethodDescription.InDefinedShape enclosingMethod = this.getEnclosingMethod();
            if (enclosingMethod != null && enclosingMethod.isTypeInitializer()) {
                throw new IllegalStateException("Cannot enclose type declaration in class initializer " + enclosingMethod);
            }
            TypeDescription declaringType = this.getDeclaringType();
            if (declaringType != null) {
                if (declaringType.isPrimitive() || declaringType.isArray()) {
                    throw new IllegalStateException("Cannot define array type or primitive type " + declaringType + " as declaring type for " + this);
                }
            } else if (enclosingType == null && enclosingMethod == null && (this.isLocalType() || this.isAnonymousType())) {
                throw new IllegalStateException("Cannot define an anonymous or local class without a declaring type for " + this);
            }
            HashSet<TypeDescription> declaredTypes = new HashSet<TypeDescription>();
            for (TypeDescription declaredType : this.getDeclaredTypes()) {
                if (declaredType.isArray() || declaredType.isPrimitive()) {
                    throw new IllegalStateException("Cannot define array type or primitive type " + declaredType + " + as declared type for " + this);
                }
                if (declaredTypes.add(declaredType)) continue;
                throw new IllegalStateException("Duplicate definition of declared type " + declaredType);
            }
            TypeDescription nestHost = this.getNestHost();
            if (nestHost.equals(this)) {
                HashSet nestMembers = new HashSet();
                for (TypeDescription nestMember : this.getNestMembers()) {
                    if (nestMember.isArray() || nestMember.isPrimitive()) {
                        throw new IllegalStateException("Cannot define array type or primitive type " + nestMember + " + as nest member of " + this);
                    }
                    if (!nestMember.isSamePackage(this)) {
                        throw new IllegalStateException("Cannot define nest member " + nestMember + " + within different package then " + this);
                    }
                    if (nestMembers.add(nestMember)) continue;
                    throw new IllegalStateException("Duplicate definition of nest member " + nestMember);
                }
            } else {
                if (nestHost.isArray() || nestHost.isPrimitive()) {
                    throw new IllegalStateException("Cannot define array type or primitive type " + nestHost + " + as nest host for " + this);
                }
                if (!nestHost.isSamePackage(this)) {
                    throw new IllegalStateException("Cannot define nest host " + nestHost + " + within different package then " + this);
                }
            }
            for (Object permittedSubclass : this.getPermittedSubtypes()) {
                if (permittedSubclass.isAssignableTo(this) && !permittedSubclass.equals(this)) continue;
                throw new IllegalStateException("Cannot assign permitted subclass " + permittedSubclass + " to " + this);
            }
            HashSet<TypeDescription> typeAnnotationTypes = new HashSet<TypeDescription>();
            for (Object annotationDescription : this.getDeclaredAnnotations()) {
                if (!(annotationDescription.isSupportedOn(ElementType.TYPE) || this.isAnnotation() && annotationDescription.isSupportedOn(ElementType.ANNOTATION_TYPE) || this.isPackageType() && annotationDescription.isSupportedOn(ElementType.PACKAGE))) {
                    throw new IllegalStateException("Cannot add " + annotationDescription + " on " + this);
                }
                if (typeAnnotationTypes.add(annotationDescription.getAnnotationType())) continue;
                throw new IllegalStateException("Duplicate annotation " + annotationDescription + " for " + this);
            }
            HashSet<FieldDescription.SignatureToken> fieldSignatureTokens = new HashSet<FieldDescription.SignatureToken>();
            for (FieldDescription.InDefinedShape fieldDescription : this.getDeclaredFields()) {
                String fieldName = fieldDescription.getName();
                if (!fieldSignatureTokens.add(fieldDescription.asSignatureToken())) {
                    throw new IllegalStateException("Duplicate field definition for " + fieldDescription);
                }
                if (!Default.isValidIdentifier(fieldName)) {
                    throw new IllegalStateException("Illegal field name for " + fieldDescription);
                }
                if ((fieldDescription.getModifiers() & 0xFFFDAF20) != 0) {
                    throw new IllegalStateException("Illegal field modifiers " + fieldDescription.getModifiers() + " for " + fieldDescription);
                }
                TypeDescription.Generic fieldType = fieldDescription.getType();
                if (!fieldType.accept(TypeDescription.Generic.Visitor.Validator.FIELD).booleanValue()) {
                    throw new IllegalStateException("Illegal field type " + fieldType + " for " + fieldDescription);
                }
                if (!fieldType.accept(TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.INSTANCE).booleanValue()) {
                    throw new IllegalStateException("Illegal type annotations on " + fieldType + " for " + this);
                }
                if (!fieldDescription.isSynthetic() && !fieldType.asErasure().isVisibleTo(this)) {
                    throw new IllegalStateException("Invisible field type " + fieldDescription.getType() + " for " + fieldDescription);
                }
                HashSet fieldAnnotationTypes = new HashSet();
                for (AnnotationDescription annotationDescription : fieldDescription.getDeclaredAnnotations()) {
                    if (!annotationDescription.isSupportedOn(ElementType.FIELD)) {
                        throw new IllegalStateException("Cannot add " + annotationDescription + " on " + fieldDescription);
                    }
                    if (fieldAnnotationTypes.add(annotationDescription.getAnnotationType())) continue;
                    throw new IllegalStateException("Duplicate annotation " + annotationDescription + " for " + fieldDescription);
                }
            }
            HashSet<MethodDescription.SignatureToken> methodSignatureTokens = new HashSet<MethodDescription.SignatureToken>();
            for (MethodDescription.InDefinedShape methodDescription : this.getDeclaredMethods()) {
                if (!methodSignatureTokens.add(methodDescription.asSignatureToken())) {
                    throw new IllegalStateException("Duplicate method signature for " + methodDescription);
                }
                if ((methodDescription.getModifiers() & 0xFFFFE200) != 0) {
                    throw new IllegalStateException("Illegal modifiers " + methodDescription.getModifiers() + " for " + methodDescription);
                }
                if (this.isInterface() && !methodDescription.isPublic() && !methodDescription.isPrivate()) {
                    throw new IllegalStateException("Methods declared by an interface must be public or private " + methodDescription);
                }
                HashSet<String> methodTypeVariableNames = new HashSet<String>();
                for (TypeDescription.Generic typeVariable : methodDescription.getTypeVariables()) {
                    String variableSymbol = typeVariable.getSymbol();
                    if (!methodTypeVariableNames.add(variableSymbol)) {
                        throw new IllegalStateException("Duplicate type variable symbol '" + typeVariable + "' for " + methodDescription);
                    }
                    if (!Default.isValidIdentifier(variableSymbol)) {
                        throw new IllegalStateException("Illegal type variable name '" + typeVariable + "' for " + methodDescription);
                    }
                    if (!TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.ofFormalTypeVariable(typeVariable)) {
                        throw new IllegalStateException("Illegal type annotation on '" + typeVariable + "' for " + methodDescription);
                    }
                    boolean interfaceBound = false;
                    HashSet<TypeDescription.Generic> bounds = new HashSet<TypeDescription.Generic>();
                    for (TypeDescription.Generic bound : typeVariable.getUpperBounds()) {
                        if (!bound.accept(TypeDescription.Generic.Visitor.Validator.TYPE_VARIABLE).booleanValue()) {
                            throw new IllegalStateException("Illegal type variable bound " + bound + " of " + typeVariable + " for " + methodDescription);
                        }
                        if (!bound.accept(TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.INSTANCE).booleanValue()) {
                            throw new IllegalStateException("Illegal type annotations on bound " + bound + " of " + typeVariable + " for " + this);
                        }
                        if (!bounds.add(bound)) {
                            throw new IllegalStateException("Duplicate bound " + bound + " of " + typeVariable + " for " + methodDescription);
                        }
                        if (interfaceBound && (bound.getSort().isTypeVariable() || !bound.isInterface())) {
                            throw new IllegalStateException("Illegal interface bound " + bound + " of " + typeVariable + " for " + methodDescription);
                        }
                        interfaceBound = true;
                    }
                    if (interfaceBound) continue;
                    throw new IllegalStateException("Type variable " + typeVariable + " for " + methodDescription + " does not define at least one bound");
                }
                TypeDescription.Generic returnType = methodDescription.getReturnType();
                if (methodDescription.isTypeInitializer()) {
                    throw new IllegalStateException("Illegal explicit declaration of a type initializer by " + this);
                }
                if (methodDescription.isConstructor()) {
                    if (!returnType.represents(Void.TYPE)) {
                        throw new IllegalStateException("A constructor must return void " + methodDescription);
                    }
                    if (!returnType.getDeclaredAnnotations().isEmpty()) {
                        throw new IllegalStateException("The void non-type must not be annotated for " + methodDescription);
                    }
                } else {
                    if (!Default.isValidIdentifier(methodDescription.getInternalName())) {
                        throw new IllegalStateException("Illegal method name " + returnType + " for " + methodDescription);
                    }
                    if (!returnType.accept(TypeDescription.Generic.Visitor.Validator.METHOD_RETURN).booleanValue()) {
                        throw new IllegalStateException("Illegal return type " + returnType + " for " + methodDescription);
                    }
                    if (!returnType.accept(TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.INSTANCE).booleanValue()) {
                        throw new IllegalStateException("Illegal type annotations on return type " + returnType + " for " + methodDescription);
                    }
                    if (!methodDescription.isSynthetic() && !methodDescription.getReturnType().asErasure().isVisibleTo(this)) {
                        throw new IllegalStateException("Invisible return type " + methodDescription.getReturnType() + " for " + methodDescription);
                    }
                }
                HashSet<String> parameterNames = new HashSet<String>();
                for (ParameterDescription.InDefinedShape parameterDescription : methodDescription.getParameters()) {
                    TypeDescription.Generic parameterType = parameterDescription.getType();
                    if (!parameterType.accept(TypeDescription.Generic.Visitor.Validator.METHOD_PARAMETER).booleanValue()) {
                        throw new IllegalStateException("Illegal parameter type of " + parameterDescription + " for " + methodDescription);
                    }
                    if (!parameterType.accept(TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.INSTANCE).booleanValue()) {
                        throw new IllegalStateException("Illegal type annotations on parameter " + parameterDescription + " for " + methodDescription);
                    }
                    if (!methodDescription.isSynthetic() && !parameterType.asErasure().isVisibleTo(this)) {
                        throw new IllegalStateException("Invisible parameter type of " + parameterDescription + " for " + methodDescription);
                    }
                    if (parameterDescription.isNamed()) {
                        String parameterName = parameterDescription.getName();
                        if (!parameterNames.add(parameterName)) {
                            throw new IllegalStateException("Duplicate parameter name of " + parameterDescription + " for " + methodDescription);
                        }
                        if (!Default.isValidIdentifier(parameterName)) {
                            throw new IllegalStateException("Illegal parameter name of " + parameterDescription + " for " + methodDescription);
                        }
                    }
                    if (parameterDescription.hasModifiers() && (parameterDescription.getModifiers() & 0xFFFF6FEF) != 0) {
                        throw new IllegalStateException("Illegal modifiers of " + parameterDescription + " for " + methodDescription);
                    }
                    HashSet<TypeDescription> parameterAnnotationTypes = new HashSet<TypeDescription>();
                    for (AnnotationDescription annotationDescription : parameterDescription.getDeclaredAnnotations()) {
                        if (!annotationDescription.isSupportedOn(ElementType.PARAMETER)) {
                            throw new IllegalStateException("Cannot add " + annotationDescription + " on " + parameterDescription);
                        }
                        if (parameterAnnotationTypes.add(annotationDescription.getAnnotationType())) continue;
                        throw new IllegalStateException("Duplicate annotation " + annotationDescription + " of " + parameterDescription + " for " + methodDescription);
                    }
                }
                for (TypeDescription.Generic exceptionType : methodDescription.getExceptionTypes()) {
                    if (!exceptionType.accept(TypeDescription.Generic.Visitor.Validator.EXCEPTION).booleanValue()) {
                        throw new IllegalStateException("Illegal exception type " + exceptionType + " for " + methodDescription);
                    }
                    if (!exceptionType.accept(TypeDescription.Generic.Visitor.Validator.ForTypeAnnotations.INSTANCE).booleanValue()) {
                        throw new IllegalStateException("Illegal type annotations on " + exceptionType + " for " + methodDescription);
                    }
                    if (methodDescription.isSynthetic() || exceptionType.asErasure().isVisibleTo(this)) continue;
                    throw new IllegalStateException("Invisible exception type " + exceptionType + " for " + methodDescription);
                }
                HashSet<TypeDescription> methodAnnotationTypes = new HashSet<TypeDescription>();
                for (AnnotationDescription annotationDescription : methodDescription.getDeclaredAnnotations()) {
                    if (!annotationDescription.isSupportedOn(methodDescription.isMethod() ? ElementType.METHOD : ElementType.CONSTRUCTOR)) {
                        throw new IllegalStateException("Cannot add " + annotationDescription + " on " + methodDescription);
                    }
                    if (methodAnnotationTypes.add(annotationDescription.getAnnotationType())) continue;
                    throw new IllegalStateException("Duplicate annotation " + annotationDescription + " for " + methodDescription);
                }
                AnnotationValue<?, ?> defaultValue = methodDescription.getDefaultValue();
                if (defaultValue != null && !methodDescription.isDefaultValue(defaultValue)) {
                    throw new IllegalStateException("Illegal default value " + defaultValue + "for " + methodDescription);
                }
                TypeDescription.Generic receiverType = methodDescription.getReceiverType();
                if (receiverType != null && !receiverType.accept(TypeDescription.Generic.Visitor.Validator.RECEIVER).booleanValue()) {
                    throw new IllegalStateException("Illegal receiver type " + receiverType + " for " + methodDescription);
                }
                if (methodDescription.isStatic()) {
                    if (receiverType == null) continue;
                    throw new IllegalStateException("Static method " + methodDescription + " defines a non-null receiver " + receiverType);
                }
                if (methodDescription.isConstructor()) {
                    if (receiverType != null && receiverType.asErasure().equals(enclosingType == null ? this : enclosingType)) continue;
                    throw new IllegalStateException("Constructor " + methodDescription + " defines an illegal receiver " + receiverType);
                }
                if (receiverType != null && this.equals(receiverType.asErasure())) continue;
                throw new IllegalStateException("Method " + methodDescription + " defines an illegal receiver " + receiverType);
            }
            return this;
        }

        private static boolean isValidIdentifier(String[] identifier) {
            if (identifier.length == 0) {
                return false;
            }
            for (String part : identifier) {
                if (Default.isValidIdentifier(part)) continue;
                return false;
            }
            return true;
        }

        private static boolean isValidIdentifier(String identifier) {
            if (KEYWORDS.contains(identifier) || identifier.length() == 0 || !Character.isJavaIdentifierStart(identifier.charAt(0))) {
                return false;
            }
            if (identifier.equals("package-info")) {
                return true;
            }
            for (int index = 1; index < identifier.length(); ++index) {
                if (Character.isJavaIdentifierPart(identifier.charAt(index))) continue;
                return false;
            }
            return true;
        }
    }

    public static interface Factory {
        public WithFlexibleName represent(TypeDescription var1);

        public WithFlexibleName subclass(String var1, int var2, TypeDescription.Generic var3);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements Factory
        {
            MODIFIABLE{

                public WithFlexibleName represent(TypeDescription typeDescription) {
                    return new net.bytebuddy.dynamic.scaffold.InstrumentedType$Default(typeDescription.getName(), typeDescription.getModifiers(), typeDescription.getSuperClass(), (List<? extends TypeVariableToken>)typeDescription.getTypeVariables().asTokenList(ElementMatchers.is(typeDescription)), typeDescription.getInterfaces().accept(TypeDescription.Generic.Visitor.Substitutor.ForDetachment.of(typeDescription)), (List<? extends FieldDescription.Token>)typeDescription.getDeclaredFields().asTokenList(ElementMatchers.is(typeDescription)), Collections.<String, Object>emptyMap(), (List<? extends MethodDescription.Token>)typeDescription.getDeclaredMethods().asTokenList(ElementMatchers.is(typeDescription)), (List<? extends RecordComponentDescription.Token>)typeDescription.getRecordComponents().asTokenList(ElementMatchers.is(typeDescription)), typeDescription.getDeclaredAnnotations(), TypeInitializer.None.INSTANCE, LoadedTypeInitializer.NoOp.INSTANCE, typeDescription.getDeclaringType(), typeDescription.getEnclosingMethod(), typeDescription.getEnclosingType(), typeDescription.getDeclaredTypes(), typeDescription.isSealed() ? typeDescription.getPermittedSubtypes() : TypeList.UNDEFINED, typeDescription.isAnonymousType(), typeDescription.isLocalType(), typeDescription.isRecord(), typeDescription.isNestHost() ? TargetType.DESCRIPTION : typeDescription.getNestHost(), (List<? extends TypeDescription>)(typeDescription.isNestHost() ? typeDescription.getNestMembers().filter(ElementMatchers.not(ElementMatchers.is(typeDescription))) : Collections.emptyList()));
                }
            }
            ,
            FROZEN{

                public WithFlexibleName represent(TypeDescription typeDescription) {
                    return new Frozen(typeDescription, LoadedTypeInitializer.NoOp.INSTANCE);
                }
            };


            @Override
            public WithFlexibleName subclass(String name, int modifiers, TypeDescription.Generic superClass) {
                return new net.bytebuddy.dynamic.scaffold.InstrumentedType$Default(name, modifiers, superClass, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.<String, Object>emptyMap(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), TypeInitializer.None.INSTANCE, LoadedTypeInitializer.NoOp.INSTANCE, TypeDescription.UNDEFINED, MethodDescription.UNDEFINED, TypeDescription.UNDEFINED, Collections.emptyList(), TypeList.UNDEFINED, false, false, false, TargetType.DESCRIPTION, Collections.emptyList());
            }
        }
    }

    public static interface Prepareable {
        public InstrumentedType prepare(InstrumentedType var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements Prepareable
        {
            INSTANCE;


            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface WithFlexibleName
    extends InstrumentedType {
        @Override
        public WithFlexibleName withField(FieldDescription.Token var1);

        @Override
        public WithFlexibleName withAuxiliaryField(FieldDescription.Token var1, Object var2);

        @Override
        public WithFlexibleName withMethod(MethodDescription.Token var1);

        @Override
        public WithFlexibleName withRecordComponent(RecordComponentDescription.Token var1);

        @Override
        public WithFlexibleName withModifiers(int var1);

        @Override
        public WithFlexibleName withInterfaces(TypeList.Generic var1);

        @Override
        public WithFlexibleName withNestHost(TypeDescription var1);

        @Override
        public WithFlexibleName withNestMembers(TypeList var1);

        @Override
        public WithFlexibleName withEnclosingType(@MaybeNull TypeDescription var1);

        @Override
        public WithFlexibleName withEnclosingMethod(MethodDescription.InDefinedShape var1);

        @Override
        public WithFlexibleName withDeclaringType(@MaybeNull TypeDescription var1);

        @Override
        public WithFlexibleName withDeclaredTypes(TypeList var1);

        @Override
        public WithFlexibleName withPermittedSubclasses(@MaybeNull TypeList var1);

        @Override
        public WithFlexibleName withLocalClass(boolean var1);

        @Override
        public WithFlexibleName withAnonymousClass(boolean var1);

        @Override
        public WithFlexibleName withRecord(boolean var1);

        @Override
        public WithFlexibleName withTypeVariable(TypeVariableToken var1);

        @Override
        public WithFlexibleName withAnnotations(List<? extends AnnotationDescription> var1);

        @Override
        public WithFlexibleName withInitializer(LoadedTypeInitializer var1);

        @Override
        public WithFlexibleName withInitializer(ByteCodeAppender var1);

        public WithFlexibleName withName(String var1);

        public WithFlexibleName withTypeVariables(ElementMatcher<? super TypeDescription.Generic> var1, Transformer<TypeVariableToken> var2);
    }
}

