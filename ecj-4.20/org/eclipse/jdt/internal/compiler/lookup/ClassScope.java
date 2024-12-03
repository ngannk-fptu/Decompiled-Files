/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.ExternalAnnotationSuperimposer;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeCollisionException;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;

public class ClassScope
extends Scope {
    public TypeDeclaration referenceContext;
    public TypeReference superTypeReference;
    ArrayList<Object> deferredBoundChecks;

    public ClassScope(Scope parent, TypeDeclaration context) {
        super(3, parent);
        this.referenceContext = context;
        this.deferredBoundChecks = null;
    }

    void buildAnonymousTypeBinding(SourceTypeBinding enclosingType, ReferenceBinding supertype) {
        TypeReference typeReference;
        AbstractMethodDeclaration[] methods;
        LocalTypeBinding anonymousType = this.buildLocalType(enclosingType, enclosingType.fPackage);
        anonymousType.modifiers |= 0x8000000;
        int inheritedBits = supertype.typeBits;
        if ((inheritedBits & 4) != 0 && (methods = this.referenceContext.methods) != null) {
            int i = 0;
            while (i < methods.length) {
                if (CharOperation.equals(TypeConstants.CLOSE, methods[i].selector) && methods[i].arguments == null) {
                    inheritedBits &= 0x713;
                    break;
                }
                ++i;
            }
        }
        anonymousType.typeBits |= inheritedBits;
        anonymousType.setPermittedTypes(Binding.NO_PERMITTEDTYPES);
        if (supertype.isInterface()) {
            anonymousType.setSuperClass(this.getJavaLangObject());
            anonymousType.setSuperInterfaces(new ReferenceBinding[]{supertype});
            typeReference = this.referenceContext.allocation.type;
            if (typeReference != null) {
                this.referenceContext.superInterfaces = new TypeReference[]{typeReference};
                if ((supertype.tagBits & 0x40000000L) != 0L) {
                    this.problemReporter().superTypeCannotUseWildcard(anonymousType, typeReference, supertype);
                    anonymousType.tagBits |= 0x20000L;
                    anonymousType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
                }
                if (supertype.isSealed()) {
                    this.problemReporter().sealedAnonymousClassCannotExtendSealedType(typeReference, supertype);
                    anonymousType.tagBits |= 0x20000L;
                    anonymousType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
                }
            }
        } else {
            anonymousType.setSuperClass(supertype);
            anonymousType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
            this.checkForEnumSealedPreview(supertype, anonymousType);
            typeReference = this.referenceContext.allocation.type;
            if (typeReference != null) {
                this.referenceContext.superclass = typeReference;
                if (supertype.erasure().id == 41) {
                    this.problemReporter().cannotExtendEnum(anonymousType, typeReference, supertype);
                    anonymousType.tagBits |= 0x20000L;
                    anonymousType.setSuperClass(this.getJavaLangObject());
                } else if (supertype.erasure().id == 93) {
                    if (!this.referenceContext.isRecord()) {
                        this.problemReporter().recordCannotExtendRecord(anonymousType, typeReference, supertype);
                        anonymousType.tagBits |= 0x20000L;
                        anonymousType.setSuperClass(this.getJavaLangObject());
                    }
                } else if (supertype.isFinal()) {
                    this.problemReporter().anonymousClassCannotExtendFinalClass(typeReference, supertype);
                    anonymousType.tagBits |= 0x20000L;
                    anonymousType.setSuperClass(this.getJavaLangObject());
                } else if ((supertype.tagBits & 0x40000000L) != 0L) {
                    this.problemReporter().superTypeCannotUseWildcard(anonymousType, typeReference, supertype);
                    anonymousType.tagBits |= 0x20000L;
                    anonymousType.setSuperClass(this.getJavaLangObject());
                } else if (supertype.isSealed()) {
                    this.problemReporter().sealedAnonymousClassCannotExtendSealedType(typeReference, supertype);
                    anonymousType.tagBits |= 0x20000L;
                    anonymousType.setSuperClass(this.getJavaLangObject());
                }
            }
        }
        this.connectMemberTypes();
        this.buildFieldsAndMethods();
        anonymousType.faultInTypesForFieldsAndMethods();
        anonymousType.verifyMethods(this.environment().methodVerifier());
    }

    private void checkForEnumSealedPreview(ReferenceBinding supertype, LocalTypeBinding anonymousType) {
        int sz;
        if (!(this.compilerOptions().sourceLevel >= 0x3B0000L && this.compilerOptions().enablePreviewFeatures && supertype.isEnum() && supertype instanceof SourceTypeBinding)) {
            return;
        }
        SourceTypeBinding sourceSuperType = (SourceTypeBinding)supertype;
        ReferenceBinding[] permTypes = sourceSuperType.permittedTypes();
        int n = sz = permTypes == null ? 0 : permTypes.length;
        if (sz == 0) {
            permTypes = new ReferenceBinding[]{anonymousType};
        } else {
            ReferenceBinding[] referenceBindingArray = permTypes;
            permTypes = new ReferenceBinding[sz + 1];
            System.arraycopy(referenceBindingArray, 0, permTypes, 0, sz);
            permTypes[sz] = anonymousType;
        }
        anonymousType.modifiers |= 0x10;
        sourceSuperType.setPermittedTypes(permTypes);
    }

    void buildComponents() {
        int size;
        SourceTypeBinding sourceType = this.referenceContext.binding;
        if (!sourceType.isRecord()) {
            return;
        }
        if (sourceType.areComponentsInitialized()) {
            return;
        }
        if (this.referenceContext.recordComponents == null) {
            sourceType.setComponents(Binding.NO_COMPONENTS);
            return;
        }
        RecordComponent[] recComps = this.referenceContext.recordComponents;
        int count = size = recComps.length;
        RecordComponentBinding[] componentBindings = new RecordComponentBinding[count];
        HashtableOfObject knownComponentNames = new HashtableOfObject(count);
        count = 0;
        int i = 0;
        while (i < size) {
            RecordComponent recComp = recComps[i];
            RecordComponentBinding compBinding = new RecordComponentBinding(sourceType, recComp, null, recComp.modifiers | 0x2000000);
            compBinding.id = count;
            this.checkAndSetModifiersForComponents(compBinding, recComp);
            if (knownComponentNames.containsKey(recComp.name)) {
                RecordComponentBinding previousBinding = (RecordComponentBinding)knownComponentNames.get(recComp.name);
                if (previousBinding != null) {
                    int f = 0;
                    while (f < i) {
                        RecordComponent previousComponent = recComps[f];
                        if (previousComponent.binding == previousBinding) {
                            this.problemReporter().recordDuplicateComponent(previousComponent);
                            break;
                        }
                        ++f;
                    }
                }
                knownComponentNames.put(recComp.name, null);
                this.problemReporter().recordDuplicateComponent(recComp);
                recComp.binding = null;
            } else {
                knownComponentNames.put(recComp.name, compBinding);
                componentBindings[count++] = compBinding;
            }
            ++i;
        }
        if (count != componentBindings.length) {
            RecordComponentBinding[] recordComponentBindingArray = componentBindings;
            componentBindings = new RecordComponentBinding[count];
            System.arraycopy(recordComponentBindingArray, 0, componentBindings, 0, count);
        }
        sourceType.setComponents(componentBindings);
        if (size > 0) {
            sourceType.isVarArgs = recComps[size - 1].isVarArgs();
        }
    }

    private void checkAndSetModifiersForComponents(RecordComponentBinding compBinding, RecordComponent comp) {
        int modifiers = compBinding.modifiers;
        int realModifiers = modifiers & 0xFFFF;
        if (realModifiers != 0 && comp != null) {
            this.problemReporter().recordComponentsCannotHaveModifiers(comp);
        }
    }

    void buildFields() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        if (sourceType.areFieldsInitialized()) {
            return;
        }
        if (this.referenceContext.fields == null) {
            sourceType.setFields(Binding.NO_FIELDS);
            return;
        }
        FieldDeclaration[] fields = this.referenceContext.fields;
        int size = fields.length;
        int count = 0;
        int i = 0;
        while (i < size) {
            switch (fields[i].getKind()) {
                case 1: 
                case 3: {
                    ++count;
                }
            }
            ++i;
        }
        FieldBinding[] fieldBindings = new FieldBinding[count];
        HashtableOfObject knownFieldNames = new HashtableOfObject(count);
        count = 0;
        int i2 = 0;
        while (i2 < size) {
            FieldDeclaration field = fields[i2];
            if (field.getKind() != 2) {
                FieldBinding fieldBinding = new FieldBinding(field, null, field.modifiers | 0x2000000, sourceType);
                fieldBinding.id = count;
                this.checkAndSetModifiersForField(fieldBinding, field);
                if (knownFieldNames.containsKey(field.name)) {
                    FieldBinding previousBinding = (FieldBinding)knownFieldNames.get(field.name);
                    if (previousBinding != null) {
                        int f = 0;
                        while (f < i2) {
                            FieldDeclaration previousField = fields[f];
                            if (previousField.binding == previousBinding) {
                                this.problemReporter().duplicateFieldInType(sourceType, previousField);
                                break;
                            }
                            ++f;
                        }
                    }
                    knownFieldNames.put(field.name, null);
                    this.problemReporter().duplicateFieldInType(sourceType, field);
                    field.binding = null;
                } else {
                    knownFieldNames.put(field.name, fieldBinding);
                    fieldBindings[count++] = fieldBinding;
                }
            }
            ++i2;
        }
        if (count != fieldBindings.length) {
            FieldBinding[] fieldBindingArray = fieldBindings;
            fieldBindings = new FieldBinding[count];
            System.arraycopy(fieldBindingArray, 0, fieldBindings, 0, count);
        }
        sourceType.tagBits &= 0xFFFFFFFFFFFFCFFFL;
        sourceType.setFields(fieldBindings);
    }

    void buildFieldsAndMethods() {
        this.buildComponents();
        this.buildFields();
        this.buildMethods();
        SourceTypeBinding sourceType = this.referenceContext.binding;
        if (!sourceType.isPrivate() && sourceType.superclass instanceof SourceTypeBinding && sourceType.superclass.isPrivate()) {
            ((SourceTypeBinding)sourceType.superclass).tagIndirectlyAccessibleMembers();
        }
        if (sourceType.isMemberType() && !sourceType.isLocalType()) {
            ((MemberTypeBinding)sourceType).checkSyntheticArgsAndFields();
        }
        ReferenceBinding[] memberTypes = sourceType.memberTypes;
        int i = 0;
        int length = memberTypes.length;
        while (i < length) {
            ((SourceTypeBinding)memberTypes[i]).scope.buildFieldsAndMethods();
            ++i;
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    private LocalTypeBinding buildLocalType(SourceTypeBinding enclosingType, PackageBinding packageBinding) {
        ReferenceBinding[] memberTypeBindings;
        LocalTypeBinding localType;
        block10: {
            this.referenceContext.scope = this;
            this.referenceContext.staticInitializerScope = new MethodScope(this, this.referenceContext, true);
            this.referenceContext.initializerScope = new MethodScope(this, this.referenceContext, false);
            localType = new LocalTypeBinding(this, enclosingType, this.innermostSwitchCase());
            this.referenceContext.binding = localType;
            this.checkAndSetModifiers();
            this.buildTypeVariables();
            memberTypeBindings = Binding.NO_MEMBER_TYPES;
            if (this.referenceContext.memberTypes == null) break block10;
            int size = this.referenceContext.memberTypes.length;
            memberTypeBindings = new ReferenceBinding[size];
            int count = 0;
            int i = 0;
            while (i < size) {
                block9: {
                    void var9_9;
                    ReferenceBinding referenceBinding;
                    TypeDeclaration memberContext = this.referenceContext.memberTypes[i];
                    switch (TypeDeclaration.kind(memberContext.modifiers)) {
                        case 2: {
                            if (this.compilerOptions().sourceLevel >= 0x3C0000L) break;
                        }
                        case 4: {
                            this.problemReporter().illegalLocalTypeDeclaration(memberContext);
                            break block9;
                        }
                    }
                    LocalTypeBinding localTypeBinding = localType;
                    do {
                        if (!CharOperation.equals(var9_9.sourceName, memberContext.name)) continue;
                        this.problemReporter().typeCollidesWithEnclosingType(memberContext);
                        break block9;
                    } while ((referenceBinding = var9_9.enclosingType()) != null);
                    int j = 0;
                    while (j < i) {
                        if (CharOperation.equals(this.referenceContext.memberTypes[j].name, memberContext.name)) {
                            this.problemReporter().duplicateNestedType(memberContext);
                            break block9;
                        }
                        ++j;
                    }
                    ClassScope memberScope = new ClassScope(this, this.referenceContext.memberTypes[i]);
                    LocalTypeBinding memberBinding = memberScope.buildLocalType(localType, packageBinding);
                    memberBinding.setAsMemberType();
                    memberTypeBindings[count++] = memberBinding;
                }
                ++i;
            }
            if (count != size) {
                ReferenceBinding[] referenceBindingArray = memberTypeBindings;
                memberTypeBindings = new ReferenceBinding[count];
                System.arraycopy(referenceBindingArray, 0, memberTypeBindings, 0, count);
            }
        }
        localType.setMemberTypes(memberTypeBindings);
        return localType;
    }

    void buildLocalTypeBinding(SourceTypeBinding enclosingType) {
        LocalTypeBinding localType = this.buildLocalType(enclosingType, enclosingType.fPackage);
        this.connectTypeHierarchy();
        this.connectImplicitPermittedTypes();
        if (this.compilerOptions().sourceLevel >= 0x310000L) {
            this.checkParameterizedTypeBounds();
            this.checkParameterizedSuperTypeCollisions();
        }
        this.buildFieldsAndMethods();
        localType.faultInTypesForFieldsAndMethods();
        this.referenceContext.binding.verifyMethods(this.environment().methodVerifier());
    }

    private void buildMemberTypes(AccessRestriction accessRestriction) {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        ReferenceBinding[] memberTypeBindings = Binding.NO_MEMBER_TYPES;
        if (this.referenceContext.memberTypes != null) {
            int length = this.referenceContext.memberTypes.length;
            memberTypeBindings = new ReferenceBinding[length];
            int count = 0;
            int i = 0;
            while (i < length) {
                TypeDeclaration memberContext = this.referenceContext.memberTypes[i];
                if (this.environment().root.isProcessingAnnotations && this.environment().isMissingType(memberContext.name)) {
                    throw new SourceTypeCollisionException();
                }
                block0 : switch (TypeDeclaration.kind(memberContext.modifiers)) {
                    case 2: 
                    case 4: {
                        if (this.compilerOptions().sourceLevel < 0x3C0000L && sourceType.isNestedType() && sourceType.isClass() && !sourceType.isStatic()) {
                            this.problemReporter().illegalLocalTypeDeclaration(memberContext);
                            break;
                        }
                    }
                    default: {
                        ReferenceBinding type = sourceType;
                        do {
                            if (!CharOperation.equals(type.sourceName, memberContext.name)) continue;
                            this.problemReporter().typeCollidesWithEnclosingType(memberContext);
                            break block0;
                        } while ((type = type.enclosingType()) != null);
                        int j = 0;
                        while (j < i) {
                            if (CharOperation.equals(this.referenceContext.memberTypes[j].name, memberContext.name)) {
                                this.problemReporter().duplicateNestedType(memberContext);
                                break block0;
                            }
                            ++j;
                        }
                        ClassScope memberScope = new ClassScope(this, memberContext);
                        memberTypeBindings[count++] = memberScope.buildType(sourceType, sourceType.fPackage, accessRestriction);
                    }
                }
                ++i;
            }
            if (count != length) {
                ReferenceBinding[] referenceBindingArray = memberTypeBindings;
                memberTypeBindings = new ReferenceBinding[count];
                System.arraycopy(referenceBindingArray, 0, memberTypeBindings, 0, count);
            }
        }
        sourceType.setMemberTypes(memberTypeBindings);
    }

    void buildMethods() {
        int i;
        boolean isEnum;
        SourceTypeBinding sourceType = this.referenceContext.binding;
        if (sourceType.areMethodsInitialized()) {
            return;
        }
        boolean bl = isEnum = TypeDeclaration.kind(this.referenceContext.modifiers) == 3;
        if (this.referenceContext.methods == null && !isEnum && !sourceType.isRecord()) {
            this.referenceContext.binding.setMethods(Binding.NO_METHODS);
            return;
        }
        AbstractMethodDeclaration[] methods = this.referenceContext.methods;
        int size = methods == null ? 0 : methods.length;
        int clinitIndex = -1;
        int i2 = 0;
        while (i2 < size) {
            if (methods[i2].isClinit()) {
                clinitIndex = i2;
                break;
            }
            ++i2;
        }
        int count = isEnum ? 2 : 0;
        MethodBinding[] methodBindings = new MethodBinding[(clinitIndex == -1 ? size : size - 1) + count];
        if (isEnum) {
            methodBindings[0] = sourceType.addSyntheticEnumMethod(TypeConstants.VALUES);
            methodBindings[1] = sourceType.addSyntheticEnumMethod(TypeConstants.VALUEOF);
        }
        boolean hasNativeMethods = false;
        if (sourceType.isAbstract()) {
            i = 0;
            while (i < size) {
                MethodScope scope;
                MethodBinding methodBinding;
                if (i != clinitIndex && (methodBinding = (scope = new MethodScope(this, methods[i], false)).createMethod(methods[i])) != null) {
                    methodBindings[count++] = methodBinding;
                    hasNativeMethods = hasNativeMethods || methodBinding.isNative();
                }
                ++i;
            }
        } else {
            boolean hasAbstractMethods = false;
            int i3 = 0;
            while (i3 < size) {
                MethodScope scope;
                MethodBinding methodBinding;
                if (i3 != clinitIndex && (methodBinding = (scope = new MethodScope(this, methods[i3], false)).createMethod(methods[i3])) != null) {
                    methodBindings[count++] = methodBinding;
                    hasAbstractMethods = hasAbstractMethods || methodBinding.isAbstract();
                    boolean bl2 = hasNativeMethods = hasNativeMethods || methodBinding.isNative();
                    if (methods[i3].isCanonicalConstructor()) {
                        methodBinding.tagBits |= 0x800L;
                    }
                }
                ++i3;
            }
            if (hasAbstractMethods) {
                this.problemReporter().abstractMethodInConcreteClass(sourceType);
            }
        }
        if (sourceType.isRecord()) {
            assert (this.referenceContext.isRecord());
            methodBindings = sourceType.checkAndAddSyntheticRecordMethods(methodBindings, count);
            count = methodBindings.length;
        }
        if (count != methodBindings.length) {
            MethodBinding[] methodBindingArray = methodBindings;
            methodBindings = new MethodBinding[count];
            System.arraycopy(methodBindingArray, 0, methodBindings, 0, count);
        }
        sourceType.tagBits &= 0xFFFFFFFFFFFF3FFFL;
        sourceType.setMethods(methodBindings);
        if (hasNativeMethods) {
            i = 0;
            while (i < methodBindings.length) {
                methodBindings[i].modifiers |= 0x8000000;
                ++i;
            }
            FieldBinding[] fields = sourceType.unResolvedFields();
            int i4 = 0;
            while (i4 < fields.length) {
                fields[i4].modifiers |= 0x8000000;
                ++i4;
            }
        }
        if (isEnum && this.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            LookupEnvironment environment = this.environment();
            ((SyntheticMethodBinding)methodBindings[0]).markNonNull(environment);
            ((SyntheticMethodBinding)methodBindings[1]).markNonNull(environment);
        }
    }

    SourceTypeBinding buildType(SourceTypeBinding enclosingType, PackageBinding packageBinding, AccessRestriction accessRestriction) {
        TypeParameter[] typeParameters;
        String externalAnnotationPath;
        char[][] className;
        this.referenceContext.scope = this;
        this.referenceContext.staticInitializerScope = new MethodScope(this, this.referenceContext, true);
        this.referenceContext.initializerScope = new MethodScope(this, this.referenceContext, false);
        if (enclosingType == null) {
            className = CharOperation.arrayConcat(packageBinding.compoundName, this.referenceContext.name);
            this.referenceContext.binding = new SourceTypeBinding(className, packageBinding, this);
        } else {
            className = CharOperation.deepCopy(enclosingType.compoundName);
            className[className.length - 1] = CharOperation.concat(className[className.length - 1], this.referenceContext.name, '$');
            if (packageBinding.hasType0Any(className[className.length - 1])) {
                this.parent.problemReporter().duplicateNestedType(this.referenceContext);
            }
            this.referenceContext.binding = new MemberTypeBinding(className, this, enclosingType);
        }
        SourceTypeBinding sourceType = this.referenceContext.binding;
        sourceType.module = this.module();
        this.environment().setAccessRestriction(sourceType, accessRestriction);
        ICompilationUnit compilationUnit = this.referenceContext.compilationResult.getCompilationUnit();
        if (compilationUnit != null && this.compilerOptions().isAnnotationBasedNullAnalysisEnabled && (externalAnnotationPath = compilationUnit.getExternalAnnotationPath(CharOperation.toString(sourceType.compoundName))) != null) {
            ExternalAnnotationSuperimposer.apply(sourceType, externalAnnotationPath);
        }
        sourceType.typeVariables = (typeParameters = this.referenceContext.typeParameters) == null || typeParameters.length == 0 ? Binding.NO_TYPE_VARIABLES : null;
        sourceType.fPackage.addType(sourceType);
        this.checkAndSetModifiers();
        this.buildTypeVariables();
        this.buildMemberTypes(accessRestriction);
        return sourceType;
    }

    private void buildTypeVariables() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        TypeParameter[] typeParameters = this.referenceContext.typeParameters;
        if (typeParameters == null || typeParameters.length == 0) {
            sourceType.setTypeVariables(Binding.NO_TYPE_VARIABLES);
            return;
        }
        sourceType.setTypeVariables(Binding.NO_TYPE_VARIABLES);
        if (sourceType.id == 1) {
            this.problemReporter().objectCannotBeGeneric(this.referenceContext);
            return;
        }
        sourceType.setTypeVariables(this.createTypeVariables(typeParameters, sourceType));
        sourceType.modifiers |= 0x40000000;
    }

    @Override
    void resolveTypeParameter(TypeParameter typeParameter) {
        typeParameter.resolve(this);
    }

    private void checkAndSetModifiers() {
        boolean flagSealedNonModifiers;
        SourceTypeBinding sourceType = this.referenceContext.binding;
        int modifiers = sourceType.modifiers;
        boolean isPreviewEnabled = this.compilerOptions().sourceLevel == ClassFileConstants.getLatestJDKLevel() && this.compilerOptions().enablePreviewFeatures;
        boolean is16Plus = this.compilerOptions().sourceLevel >= 0x3C0000L;
        boolean bl = flagSealedNonModifiers = isPreviewEnabled && (modifiers & 0x14000000) != 0;
        if (sourceType.isRecord()) {
            modifiers |= 0x10;
        }
        if ((modifiers & 0x400000) != 0) {
            this.problemReporter().duplicateModifierForType(sourceType);
        }
        ReferenceBinding enclosingType = sourceType.enclosingType();
        boolean isMemberType = sourceType.isMemberType();
        if (isMemberType) {
            if (sourceType.hasEnclosingInstanceContext()) {
                modifiers |= enclosingType.modifiers & 0x40000000;
            }
            modifiers |= enclosingType.modifiers & 0x800;
            if (enclosingType.isInterface()) {
                modifiers |= 1;
            }
            if (sourceType.isEnum()) {
                if (!is16Plus && !enclosingType.isStatic()) {
                    this.problemReporter().nonStaticContextForEnumMemberType(sourceType);
                } else {
                    modifiers |= 8;
                }
            } else if (sourceType.isInterface()) {
                modifiers |= 8;
            } else if (sourceType.isRecord()) {
                modifiers |= 8;
            }
        } else if (sourceType.isLocalType()) {
            if (sourceType.isEnum()) {
                if (!is16Plus) {
                    this.problemReporter().illegalLocalTypeDeclaration(this.referenceContext);
                    sourceType.modifiers = 0;
                    return;
                }
                if ((modifiers & 0xFFFF & 0xFFFFB7FF) != 0 || flagSealedNonModifiers) {
                    this.problemReporter().illegalModifierForLocalEnumDeclaration(sourceType);
                    return;
                }
                modifiers |= 8;
            } else if (sourceType.isRecord()) {
                if ((modifiers & 8) != 0) {
                    if (!(this.parent instanceof ClassScope)) {
                        this.problemReporter().recordIllegalStaticModifierForLocalClassOrInterface(sourceType);
                    }
                    return;
                }
                modifiers |= 8;
            }
            if (sourceType.isAnonymousType()) {
                if (this.compilerOptions().complianceLevel < 0x350000L) {
                    modifiers |= 0x10;
                }
                if (this.referenceContext.allocation.type == null) {
                    modifiers |= 0x4000;
                }
            } else if (this.parent.referenceContext() instanceof TypeDeclaration) {
                TypeDeclaration typeDecl = (TypeDeclaration)this.parent.referenceContext();
                if (TypeDeclaration.kind(typeDecl.modifiers) == 2) {
                    modifiers |= 8;
                }
            }
            Scope scope = this;
            block4: do {
                switch (scope.kind) {
                    case 2: {
                        MethodScope methodScope = (MethodScope)scope;
                        if (methodScope.isLambdaScope()) {
                            methodScope = methodScope.namedMethodScope();
                        }
                        if (methodScope.isInsideInitializer()) {
                            SourceTypeBinding type = ((TypeDeclaration)methodScope.referenceContext).binding;
                            if (methodScope.initializedField != null) {
                                if (!methodScope.initializedField.isViewedAsDeprecated() || sourceType.isDeprecated()) continue block4;
                                modifiers |= 0x200000;
                                break;
                            }
                            if (type.isStrictfp()) {
                                modifiers |= 0x800;
                            }
                            if (!type.isViewedAsDeprecated() || sourceType.isDeprecated()) continue block4;
                            modifiers |= 0x200000;
                            break;
                        }
                        MethodBinding method = ((AbstractMethodDeclaration)methodScope.referenceContext).binding;
                        if (method == null) break;
                        if (method.isStrictfp()) {
                            modifiers |= 0x800;
                        }
                        if (!method.isViewedAsDeprecated() || sourceType.isDeprecated()) continue block4;
                        modifiers |= 0x200000;
                        break;
                    }
                    case 3: {
                        if (enclosingType.isStrictfp()) {
                            modifiers |= 0x800;
                        }
                        if (!enclosingType.isViewedAsDeprecated() || sourceType.isDeprecated()) continue block4;
                        modifiers |= 0x200000;
                        sourceType.tagBits |= enclosingType.tagBits & 0x4000000000000000L;
                    }
                }
            } while ((scope = scope.parent) != null);
        }
        int realModifiers = modifiers & 0xFFFF;
        if ((realModifiers & 0x200) != 0) {
            if (isMemberType) {
                if ((realModifiers & 0xFFFFD1F0) != 0) {
                    if ((realModifiers & 0x2000) != 0) {
                        this.problemReporter().illegalModifierForAnnotationMemberType(sourceType);
                    } else {
                        this.problemReporter().illegalModifierForMemberInterface(sourceType);
                    }
                }
            } else if (sourceType.isLocalType()) {
                int UNEXPECTED_MODIFIERS = ~(0x2E00 | (is16Plus && this.parent instanceof ClassScope ? 8 : 0));
                if ((realModifiers & UNEXPECTED_MODIFIERS) != 0 || isPreviewEnabled && flagSealedNonModifiers) {
                    this.problemReporter().localStaticsIllegalVisibilityModifierForInterfaceLocalType(sourceType);
                }
                modifiers |= 8;
            } else if ((realModifiers & 0xFFFFD1FE) != 0) {
                if ((realModifiers & 0x2000) != 0) {
                    this.problemReporter().illegalModifierForAnnotationType(sourceType);
                } else {
                    this.problemReporter().illegalModifierForInterface(sourceType);
                }
            }
            if (sourceType.sourceName == TypeConstants.PACKAGE_INFO_NAME && this.compilerOptions().targetJDK > 0x310000L) {
                modifiers |= 0x1000;
            }
            modifiers |= 0x400;
        } else if ((realModifiers & 0x4000) != 0) {
            if (isMemberType) {
                if ((realModifiers & 0xFFFFB7F0) != 0 || flagSealedNonModifiers) {
                    this.problemReporter().illegalModifierForMemberEnum(sourceType);
                    modifiers &= 0xFFFFFBFF;
                    realModifiers &= 0xFFFFFBFF;
                }
            } else if (!sourceType.isLocalType() && ((realModifiers & 0xFFFFB7FE) != 0 || flagSealedNonModifiers)) {
                this.problemReporter().illegalModifierForEnum(sourceType);
            }
            if (!sourceType.isAnonymousType()) {
                FieldDeclaration[] fields;
                TypeDeclaration typeDeclaration;
                block107: {
                    if ((this.referenceContext.bits & 0x800) != 0) {
                        modifiers |= 0x400;
                    } else {
                        int fieldsLength;
                        typeDeclaration = this.referenceContext;
                        fields = typeDeclaration.fields;
                        int n = fieldsLength = fields == null ? 0 : fields.length;
                        if (fieldsLength != 0) {
                            AbstractMethodDeclaration[] methods = typeDeclaration.methods;
                            int methodsLength = methods == null ? 0 : methods.length;
                            boolean definesAbstractMethod = typeDeclaration.superInterfaces != null;
                            int i = 0;
                            while (i < methodsLength && !definesAbstractMethod) {
                                definesAbstractMethod = methods[i].isAbstract();
                                ++i;
                            }
                            if (definesAbstractMethod) {
                                boolean needAbstractBit = false;
                                int i2 = 0;
                                while (i2 < fieldsLength) {
                                    FieldDeclaration fieldDecl = fields[i2];
                                    if (fieldDecl.getKind() == 3) {
                                        if (!(fieldDecl.initialization instanceof QualifiedAllocationExpression)) break block107;
                                        needAbstractBit = true;
                                    }
                                    ++i2;
                                }
                                if (needAbstractBit) {
                                    modifiers |= 0x400;
                                }
                            }
                        }
                    }
                }
                typeDeclaration = this.referenceContext;
                fields = typeDeclaration.fields;
                if (fields != null) {
                    int i = 0;
                    int fieldsLength = fields.length;
                    while (i < fieldsLength) {
                        FieldDeclaration fieldDecl = fields[i];
                        if (fieldDecl.getKind() != 3 || !(fieldDecl.initialization instanceof QualifiedAllocationExpression)) {
                            ++i;
                            continue;
                        }
                        break;
                    }
                } else {
                    modifiers |= 0x10;
                }
                if (isPreviewEnabled && (modifiers & 0x10) == 0) {
                    modifiers |= 0x10000000;
                }
            }
        } else if (sourceType.isRecord()) {
            int UNEXPECTED_MODIFIERS = 0x14000000;
            if (isMemberType) {
                if ((realModifiers & 0xFFFFF7E0) != 0 || (modifiers & UNEXPECTED_MODIFIERS) != 0) {
                    this.problemReporter().illegalModifierForInnerRecord(sourceType);
                }
            } else if (sourceType.isLocalType()) {
                if ((realModifiers & 0xFFFFF7E7) != 0 || (modifiers & UNEXPECTED_MODIFIERS) != 0) {
                    this.problemReporter().illegalModifierForLocalRecord(sourceType);
                }
            } else if ((realModifiers & 0xFFFFF7EE) != 0 || (modifiers & UNEXPECTED_MODIFIERS) != 0) {
                this.problemReporter().illegalModifierForRecord(sourceType);
            }
        } else {
            if (isMemberType) {
                if ((realModifiers & 0xFFFFF3E0) != 0) {
                    this.problemReporter().illegalModifierForMemberClass(sourceType);
                }
            } else if (sourceType.isLocalType()) {
                int UNEXPECTED_MODIFIERS = ~(0xC10 | (is16Plus && this.parent instanceof ClassScope ? 8 : 0));
                if ((realModifiers & UNEXPECTED_MODIFIERS) != 0 || flagSealedNonModifiers) {
                    this.problemReporter().illegalModifierForLocalClass(sourceType);
                }
            } else if ((realModifiers & 0xFFFFF3EE) != 0) {
                this.problemReporter().illegalModifierForClass(sourceType);
            }
            if ((realModifiers & 0x410) == 1040) {
                this.problemReporter().illegalModifierCombinationFinalAbstractForClass(sourceType);
            }
        }
        if (isMemberType) {
            if (enclosingType.isInterface()) {
                if ((realModifiers & 6) != 0) {
                    this.problemReporter().illegalVisibilityModifierForInterfaceMemberType(sourceType);
                    if ((realModifiers & 4) != 0) {
                        modifiers &= 0xFFFFFFFB;
                    }
                    if ((realModifiers & 2) != 0) {
                        modifiers &= 0xFFFFFFFD;
                    }
                }
            } else {
                int accessorBits = realModifiers & 7;
                if ((accessorBits & accessorBits - 1) > 1) {
                    this.problemReporter().illegalVisibilityModifierCombinationForMemberType(sourceType);
                    if ((accessorBits & 1) != 0) {
                        if ((accessorBits & 4) != 0) {
                            modifiers &= 0xFFFFFFFB;
                        }
                        if ((accessorBits & 2) != 0) {
                            modifiers &= 0xFFFFFFFD;
                        }
                    } else if ((accessorBits & 4) != 0 && (accessorBits & 2) != 0) {
                        modifiers &= 0xFFFFFFFD;
                    }
                }
            }
            if ((realModifiers & 8) == 0) {
                if (enclosingType.isInterface()) {
                    modifiers |= 8;
                }
            } else if (!enclosingType.isStatic() && !is16Plus) {
                this.problemReporter().illegalStaticModifierForMemberType(sourceType);
            }
        }
        sourceType.modifiers = modifiers;
    }

    private void checkAndSetModifiersForField(FieldBinding fieldBinding, FieldDeclaration fieldDecl) {
        int accessorBits;
        int modifiers = fieldBinding.modifiers;
        ReferenceBinding declaringClass = fieldBinding.declaringClass;
        if ((modifiers & 0x400000) != 0) {
            this.problemReporter().duplicateModifierForField(declaringClass, fieldDecl);
        }
        if (declaringClass.isInterface()) {
            if (((modifiers |= 0x19) & 0xFFFF) != 25) {
                if ((declaringClass.modifiers & 0x2000) != 0) {
                    this.problemReporter().illegalModifierForAnnotationField(fieldDecl);
                } else {
                    this.problemReporter().illegalModifierForInterfaceField(fieldDecl);
                }
            }
            fieldBinding.modifiers = modifiers;
            return;
        }
        if (fieldDecl.getKind() == 3) {
            if ((modifiers & 0xFFFF) != 0) {
                this.problemReporter().illegalModifierForEnumConstant(declaringClass, fieldDecl);
            }
            fieldBinding.modifiers |= 0x8004019;
            return;
        }
        int realModifiers = modifiers & 0xFFFF;
        if ((realModifiers & 0xFFFFFF20) != 0) {
            this.problemReporter().illegalModifierForField(declaringClass, fieldDecl);
            modifiers &= 0xFFFF00DF;
        }
        if (((accessorBits = realModifiers & 7) & accessorBits - 1) > 1) {
            this.problemReporter().illegalVisibilityModifierCombinationForField(declaringClass, fieldDecl);
            if ((accessorBits & 1) != 0) {
                if ((accessorBits & 4) != 0) {
                    modifiers &= 0xFFFFFFFB;
                }
                if ((accessorBits & 2) != 0) {
                    modifiers &= 0xFFFFFFFD;
                }
            } else if ((accessorBits & 4) != 0 && (accessorBits & 2) != 0) {
                modifiers &= 0xFFFFFFFD;
            }
        }
        if ((realModifiers & 0x50) == 80) {
            this.problemReporter().illegalModifierCombinationFinalVolatileForField(declaringClass, fieldDecl);
        }
        if (fieldDecl.initialization == null && (modifiers & 0x10) != 0) {
            modifiers |= 0x4000000;
        }
        fieldBinding.modifiers = modifiers;
    }

    public void checkParameterizedSuperTypeCollisions() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        ReferenceBinding[] interfaces = sourceType.superInterfaces;
        HashMap invocations = new HashMap(2);
        ReferenceBinding itsSuperclass = sourceType.isInterface() ? null : sourceType.superclass;
        int i = 0;
        int length = interfaces.length;
        while (i < length) {
            ReferenceBinding one = interfaces[i];
            if (!(one == null || itsSuperclass != null && this.hasErasedCandidatesCollisions(itsSuperclass, one, invocations, sourceType, this.referenceContext))) {
                int j = 0;
                while (j < i) {
                    ReferenceBinding two = interfaces[j];
                    if (two != null && this.hasErasedCandidatesCollisions(one, two, invocations, sourceType, this.referenceContext)) break;
                    ++j;
                }
            }
            ++i;
        }
        TypeParameter[] typeParameters = this.referenceContext.typeParameters;
        int i2 = 0;
        int paramLength = typeParameters == null ? 0 : typeParameters.length;
        while (i2 < paramLength) {
            TypeReference[] boundRefs;
            TypeParameter typeParameter = typeParameters[i2];
            TypeVariableBinding typeVariable = typeParameter.binding;
            if (typeVariable != null && typeVariable.isValidBinding() && (boundRefs = typeParameter.bounds) != null) {
                boolean checkSuperclass = TypeBinding.equalsEquals(typeVariable.firstBound, typeVariable.superclass);
                int j = 0;
                int boundLength = boundRefs.length;
                block3: while (j < boundLength) {
                    TypeReference typeRef = boundRefs[j];
                    TypeBinding superType = typeRef.resolvedType;
                    if (superType != null && superType.isValidBinding()) {
                        if (checkSuperclass && this.hasErasedCandidatesCollisions(superType, typeVariable.superclass, invocations, typeVariable, typeRef)) break;
                        int index = typeVariable.superInterfaces.length;
                        while (--index >= 0) {
                            if (this.hasErasedCandidatesCollisions(superType, typeVariable.superInterfaces[index], invocations, typeVariable, typeRef)) break block3;
                        }
                    }
                    ++j;
                }
            }
            ++i2;
        }
        ReferenceBinding[] memberTypes = this.referenceContext.binding.memberTypes;
        if (memberTypes != null && memberTypes != Binding.NO_MEMBER_TYPES) {
            int i3 = 0;
            int size = memberTypes.length;
            while (i3 < size) {
                ((SourceTypeBinding)memberTypes[i3]).scope.checkParameterizedSuperTypeCollisions();
                ++i3;
            }
        }
    }

    private void checkForInheritedMemberTypes(SourceTypeBinding sourceType) {
        ReferenceBinding currentType = sourceType;
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        do {
            if (currentType.hasMemberTypes()) {
                return;
            }
            ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces == null || itsInterfaces == Binding.NO_SUPERINTERFACES) continue;
            if (interfacesToVisit == null) {
                interfacesToVisit = itsInterfaces;
                nextPosition = interfacesToVisit.length;
                continue;
            }
            int itsLength = itsInterfaces.length;
            if (nextPosition + itsLength >= interfacesToVisit.length) {
                ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
            }
            int a = 0;
            while (a < itsLength) {
                block19: {
                    ReferenceBinding next = itsInterfaces[a];
                    int b = 0;
                    while (b < nextPosition) {
                        if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                            ++b;
                            continue;
                        }
                        break block19;
                    }
                    interfacesToVisit[nextPosition++] = next;
                }
                ++a;
            }
        } while ((currentType = currentType.superclass()) != null && (currentType.tagBits & 0x10000L) == 0L);
        if (interfacesToVisit != null) {
            boolean needToTag = false;
            int i = 0;
            while (i < nextPosition) {
                ReferenceBinding anInterface = interfacesToVisit[i];
                if ((anInterface.tagBits & 0x10000L) == 0L) {
                    if (anInterface.hasMemberTypes()) {
                        return;
                    }
                    needToTag = true;
                    ReferenceBinding[] itsInterfaces = anInterface.superInterfaces();
                    if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                        int itsLength = itsInterfaces.length;
                        if (nextPosition + itsLength >= interfacesToVisit.length) {
                            ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                            interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                            System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                        }
                        int a = 0;
                        while (a < itsLength) {
                            block20: {
                                ReferenceBinding next = itsInterfaces[a];
                                int b = 0;
                                while (b < nextPosition) {
                                    if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                        ++b;
                                        continue;
                                    }
                                    break block20;
                                }
                                interfacesToVisit[nextPosition++] = next;
                            }
                            ++a;
                        }
                    }
                }
                ++i;
            }
            if (needToTag) {
                i = 0;
                while (i < nextPosition) {
                    interfacesToVisit[i].tagBits |= 0x10000L;
                    ++i;
                }
            }
        }
        currentType = sourceType;
        do {
            currentType.tagBits |= 0x10000L;
        } while ((currentType = currentType.superclass()) != null && (currentType.tagBits & 0x10000L) == 0L);
    }

    public void checkParameterizedTypeBounds() {
        int i = 0;
        int l = this.deferredBoundChecks == null ? 0 : this.deferredBoundChecks.size();
        while (i < l) {
            Object toCheck = this.deferredBoundChecks.get(i);
            if (toCheck instanceof TypeReference) {
                ((TypeReference)toCheck).checkBounds(this);
            } else if (toCheck instanceof Runnable) {
                ((Runnable)toCheck).run();
            }
            ++i;
        }
        this.deferredBoundChecks = null;
        ReferenceBinding[] memberTypes = this.referenceContext.binding.memberTypes;
        if (memberTypes != null && memberTypes != Binding.NO_MEMBER_TYPES) {
            int i2 = 0;
            int size = memberTypes.length;
            while (i2 < size) {
                ((SourceTypeBinding)memberTypes[i2]).scope.checkParameterizedTypeBounds();
                ++i2;
            }
        }
    }

    private void connectMemberTypes() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        ReferenceBinding[] memberTypes = sourceType.memberTypes;
        if (memberTypes != null && memberTypes != Binding.NO_MEMBER_TYPES) {
            int i = 0;
            int size = memberTypes.length;
            while (i < size) {
                ((SourceTypeBinding)memberTypes[i]).scope.connectTypeHierarchy();
                ++i;
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean connectSuperclass() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        if (sourceType.id == 1) {
            sourceType.setSuperClass(null);
            sourceType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
            sourceType.setPermittedTypes(Binding.NO_PERMITTEDTYPES);
            if (!sourceType.isClass()) {
                this.problemReporter().objectMustBeClass(sourceType);
            }
            if (this.referenceContext.superclass == null) {
                if (this.referenceContext.superInterfaces == null) return true;
                if (this.referenceContext.superInterfaces.length <= 0) return true;
            }
            this.problemReporter().objectCannotHaveSuperTypes(sourceType);
            return true;
        }
        if (this.referenceContext.superclass == null) {
            if (sourceType.isEnum() && this.compilerOptions().sourceLevel >= 0x310000L) {
                return this.connectEnumSuperclass();
            }
            sourceType.setSuperClass(this.getJavaLangObject());
            if (!this.detectHierarchyCycle(sourceType, sourceType.superclass, null)) return true;
            return false;
        }
        TypeReference superclassRef = this.referenceContext.superclass;
        ReferenceBinding superclass = this.findSupertype(superclassRef);
        if (superclass != null) {
            if (!superclass.isClass() && (superclass.tagBits & 0x80L) == 0L) {
                this.problemReporter().superclassMustBeAClass(sourceType, superclassRef, superclass);
            } else if (superclass.isFinal()) {
                this.problemReporter().classExtendFinalClass(sourceType, superclassRef, superclass);
            } else if ((superclass.tagBits & 0x40000000L) != 0L) {
                this.problemReporter().superTypeCannotUseWildcard(sourceType, superclassRef, superclass);
            } else if (superclass.erasure().id == 41) {
                this.problemReporter().cannotExtendEnum(sourceType, superclassRef, superclass);
            } else if (superclass.erasure().id == 93) {
                if (this.referenceContext.isRecord()) return this.connectRecordSuperclass();
                this.problemReporter().recordCannotExtendRecord(sourceType, superclassRef, superclass);
            } else {
                if ((superclass.tagBits & 0x20000L) != 0L || !superclassRef.resolvedType.isValidBinding()) {
                    sourceType.setSuperClass(superclass);
                    sourceType.tagBits |= 0x20000L;
                    return superclassRef.resolvedType.isValidBinding();
                }
                sourceType.setSuperClass(superclass);
                sourceType.typeBits |= superclass.typeBits & 0x713;
                if ((sourceType.typeBits & 3) == 0) return true;
                sourceType.typeBits |= sourceType.applyCloseableClassWhitelists(this.compilerOptions());
                return true;
            }
        }
        sourceType.tagBits |= 0x20000L;
        sourceType.setSuperClass(sourceType.isRecord() ? this.getJavaLangRecord() : this.getJavaLangObject());
        if ((sourceType.superclass.tagBits & 0x100L) != 0L) return false;
        this.detectHierarchyCycle(sourceType, sourceType.superclass, null);
        return false;
    }

    private boolean connectEnumSuperclass() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        ReferenceBinding rootEnumType = this.getJavaLangEnum();
        if ((rootEnumType.tagBits & 0x80L) != 0L) {
            sourceType.tagBits |= 0x20000L;
            sourceType.setSuperClass(rootEnumType);
            return false;
        }
        boolean foundCycle = this.detectHierarchyCycle(sourceType, rootEnumType, null);
        TypeVariableBinding[] refTypeVariables = rootEnumType.typeVariables();
        if (refTypeVariables == Binding.NO_TYPE_VARIABLES) {
            this.problemReporter().nonGenericTypeCannotBeParameterized(0, null, rootEnumType, new TypeBinding[]{sourceType});
            return false;
        }
        if (1 != refTypeVariables.length) {
            this.problemReporter().incorrectArityForParameterizedType(null, rootEnumType, new TypeBinding[]{sourceType});
            return false;
        }
        ParameterizedTypeBinding superType = this.environment().createParameterizedType(rootEnumType, new TypeBinding[]{this.environment().convertToRawType(sourceType, false)}, null);
        sourceType.tagBits |= superType.tagBits & 0x20000L;
        sourceType.setSuperClass(superType);
        if (!refTypeVariables[0].boundCheck(superType, sourceType, this, null).isOKbyJLS()) {
            this.problemReporter().typeMismatchError((TypeBinding)rootEnumType, refTypeVariables[0], sourceType, null);
        }
        return !foundCycle;
    }

    /*
     * WARNING - void declaration
     */
    private void connectImplicitPermittedTypes(SourceTypeBinding sourceType) {
        void var4_7;
        ArrayList<SourceTypeBinding> types = new ArrayList<SourceTypeBinding>();
        TypeDeclaration[] typeDeclarationArray = this.referenceCompilationUnit().types;
        int n = this.referenceCompilationUnit().types.length;
        boolean n2 = false;
        while (var4_7 < n) {
            TypeDeclaration typeDecl = typeDeclarationArray[var4_7];
            types.addAll(sourceType.collectAllTypeBindings(typeDecl, this.compilationUnitScope()));
            ++var4_7;
        }
        LinkedHashSet<ReferenceBinding> permSubTypes = new LinkedHashSet<ReferenceBinding>();
        for (ReferenceBinding referenceBinding : types) {
            if (TypeBinding.equalsEquals(referenceBinding, sourceType) || referenceBinding.findSuperTypeOriginatingFrom(sourceType) == null) continue;
            permSubTypes.add(referenceBinding);
        }
        if (sourceType.isSealed()) {
            sourceType.isLocalType();
        }
        if (permSubTypes.size() == 0) {
            if (!sourceType.isLocalType()) {
                this.problemReporter().sealedSealedTypeMissingPermits(sourceType, this.referenceContext);
            }
            return;
        }
        sourceType.setPermittedTypes(permSubTypes.toArray(new ReferenceBinding[0]));
    }

    void connectImplicitPermittedTypes() {
        ReferenceBinding[] memberTypes;
        TypeDeclaration typeDecl = this.referenceContext;
        SourceTypeBinding sourceType = typeDecl.binding;
        if (sourceType.id == 1 || sourceType.isEnum() || sourceType.isRecord()) {
            return;
        }
        if (sourceType.isSealed() && (typeDecl.permittedTypes == null || typeDecl.permittedTypes.length == 0)) {
            this.connectImplicitPermittedTypes(sourceType);
        }
        if ((memberTypes = sourceType.memberTypes) != null && memberTypes != Binding.NO_MEMBER_TYPES) {
            int i = 0;
            int size = memberTypes.length;
            while (i < size) {
                ((SourceTypeBinding)memberTypes[i]).scope.connectImplicitPermittedTypes();
                ++i;
            }
        }
    }

    void connectPermittedTypes() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        sourceType.setPermittedTypes(Binding.NO_PERMITTEDTYPES);
        if (this.referenceContext.permittedTypes == null) {
            return;
        }
        if (sourceType.id == 1 || sourceType.isEnum()) {
            return;
        }
        int length = this.referenceContext.permittedTypes.length;
        ReferenceBinding[] permittedTypeBindings = new ReferenceBinding[length];
        int count = 0;
        int i = 0;
        while (i < length) {
            block9: {
                TypeReference permittedTypeRef = this.referenceContext.permittedTypes[i];
                ReferenceBinding permittedType = this.findPermittedtype(permittedTypeRef);
                if (permittedType != null) {
                    int j = 0;
                    while (j < i) {
                        if (TypeBinding.equalsEquals(permittedTypeBindings[j], permittedType)) {
                            this.problemReporter().sealedDuplicateTypeInPermits(sourceType, permittedTypeRef, permittedType);
                            break block9;
                        }
                        ++j;
                    }
                    permittedTypeBindings[count++] = permittedType;
                }
            }
            ++i;
        }
        if (count > 0) {
            if (count != length) {
                ReferenceBinding[] referenceBindingArray = permittedTypeBindings;
                permittedTypeBindings = new ReferenceBinding[count];
                System.arraycopy(referenceBindingArray, 0, permittedTypeBindings, 0, count);
            }
            sourceType.setPermittedTypes(permittedTypeBindings);
        } else {
            sourceType.setPermittedTypes(Binding.NO_PERMITTEDTYPES);
        }
    }

    private boolean connectRecordSuperclass() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        ReferenceBinding rootRecordType = this.getJavaLangRecord();
        sourceType.setSuperClass(rootRecordType);
        if ((rootRecordType.tagBits & 0x80L) != 0L) {
            sourceType.tagBits |= 0x20000L;
            return false;
        }
        return !this.detectHierarchyCycle(sourceType, rootRecordType, null);
    }

    private boolean connectSuperInterfaces() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        sourceType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
        if (this.referenceContext.superInterfaces == null) {
            if (sourceType.isAnnotationType() && this.compilerOptions().sourceLevel >= 0x310000L) {
                ReferenceBinding annotationType = this.getJavaLangAnnotationAnnotation();
                boolean foundCycle = this.detectHierarchyCycle(sourceType, annotationType, null);
                sourceType.setSuperInterfaces(new ReferenceBinding[]{annotationType});
                return !foundCycle;
            }
            return true;
        }
        if (sourceType.id == 1) {
            return true;
        }
        boolean noProblems = true;
        int length = this.referenceContext.superInterfaces.length;
        ReferenceBinding[] interfaceBindings = new ReferenceBinding[length];
        int count = 0;
        int i = 0;
        while (i < length) {
            block17: {
                TypeReference superInterfaceRef = this.referenceContext.superInterfaces[i];
                ReferenceBinding superInterface = this.findSupertype(superInterfaceRef);
                if (superInterface == null) {
                    sourceType.tagBits |= 0x20000L;
                    noProblems = false;
                } else {
                    int j = 0;
                    while (j < i) {
                        if (TypeBinding.equalsEquals(interfaceBindings[j], superInterface)) {
                            this.problemReporter().duplicateSuperinterface(sourceType, superInterfaceRef, superInterface);
                            sourceType.tagBits |= 0x20000L;
                            noProblems = false;
                            break block17;
                        }
                        ++j;
                    }
                    if (!superInterface.isInterface() && (superInterface.tagBits & 0x80L) == 0L) {
                        this.problemReporter().superinterfaceMustBeAnInterface(sourceType, superInterfaceRef, superInterface);
                        sourceType.tagBits |= 0x20000L;
                        noProblems = false;
                    } else {
                        if (superInterface.isAnnotationType()) {
                            this.problemReporter().annotationTypeUsedAsSuperinterface(sourceType, superInterfaceRef, superInterface);
                        }
                        if ((superInterface.tagBits & 0x40000000L) != 0L) {
                            this.problemReporter().superTypeCannotUseWildcard(sourceType, superInterfaceRef, superInterface);
                            sourceType.tagBits |= 0x20000L;
                            noProblems = false;
                        } else {
                            if ((superInterface.tagBits & 0x20000L) != 0L || !superInterfaceRef.resolvedType.isValidBinding()) {
                                sourceType.tagBits |= 0x20000L;
                                noProblems &= superInterfaceRef.resolvedType.isValidBinding();
                            }
                            sourceType.typeBits |= superInterface.typeBits & 0x713;
                            if ((sourceType.typeBits & 3) != 0) {
                                sourceType.typeBits |= sourceType.applyCloseableInterfaceWhitelists();
                            }
                            interfaceBindings[count++] = superInterface;
                        }
                    }
                }
            }
            ++i;
        }
        if (count > 0) {
            if (count != length) {
                ReferenceBinding[] referenceBindingArray = interfaceBindings;
                interfaceBindings = new ReferenceBinding[count];
                System.arraycopy(referenceBindingArray, 0, interfaceBindings, 0, count);
            }
            sourceType.setSuperInterfaces(interfaceBindings);
        }
        return noProblems;
    }

    void connectTypeHierarchy() {
        SourceTypeBinding sourceType = this.referenceContext.binding;
        CompilationUnitScope compilationUnitScope = this.compilationUnitScope();
        boolean wasAlreadyConnecting = compilationUnitScope.connectingHierarchy;
        compilationUnitScope.connectingHierarchy = true;
        try {
            if ((sourceType.tagBits & 0x100L) == 0L) {
                sourceType.tagBits |= 0x100L;
                this.environment().typesBeingConnected.add(sourceType);
                boolean noProblems = this.connectSuperclass();
                noProblems &= this.connectSuperInterfaces();
                this.environment().typesBeingConnected.remove(sourceType);
                sourceType.tagBits |= 0x200L;
                this.connectPermittedTypes();
                sourceType.tagBits |= 0x40000L;
                if ((noProblems &= this.connectTypeVariables(this.referenceContext.typeParameters, false)) && sourceType.isHierarchyInconsistent()) {
                    this.problemReporter().hierarchyHasProblems(sourceType);
                }
            }
            this.connectMemberTypes();
        }
        finally {
            compilationUnitScope.connectingHierarchy = wasAlreadyConnecting;
        }
        LookupEnvironment env = this.environment();
        try {
            try {
                env.missingClassFileLocation = this.referenceContext;
                this.checkForInheritedMemberTypes(sourceType);
            }
            catch (AbortCompilation e) {
                e.updateContext(this.referenceContext, this.referenceCompilationUnit().compilationResult);
                throw e;
            }
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }

    @Override
    public boolean deferCheck(Runnable check) {
        if (this.compilationUnitScope().connectingHierarchy) {
            if (this.deferredBoundChecks == null) {
                this.deferredBoundChecks = new ArrayList();
            }
            this.deferredBoundChecks.add(check);
            return true;
        }
        return false;
    }

    private void connectTypeHierarchyWithoutMembers() {
        if (this.parent instanceof CompilationUnitScope) {
            if (((CompilationUnitScope)this.parent).imports == null) {
                ((CompilationUnitScope)this.parent).checkAndSetImports();
            }
        } else if (this.parent instanceof ClassScope) {
            ((ClassScope)this.parent).connectTypeHierarchyWithoutMembers();
        }
        SourceTypeBinding sourceType = this.referenceContext.binding;
        if ((sourceType.tagBits & 0x100L) != 0L) {
            return;
        }
        CompilationUnitScope compilationUnitScope = this.compilationUnitScope();
        boolean wasAlreadyConnecting = compilationUnitScope.connectingHierarchy;
        compilationUnitScope.connectingHierarchy = true;
        try {
            sourceType.tagBits |= 0x100L;
            this.environment().typesBeingConnected.add(sourceType);
            boolean noProblems = this.connectSuperclass();
            noProblems &= this.connectSuperInterfaces();
            this.environment().typesBeingConnected.remove(sourceType);
            sourceType.tagBits |= 0x200L;
            this.connectPermittedTypes();
            sourceType.tagBits |= 0x40000L;
            if ((noProblems &= this.connectTypeVariables(this.referenceContext.typeParameters, false)) && sourceType.isHierarchyInconsistent()) {
                this.problemReporter().hierarchyHasProblems(sourceType);
            }
        }
        finally {
            compilationUnitScope.connectingHierarchy = wasAlreadyConnecting;
        }
    }

    public boolean detectHierarchyCycle(TypeBinding superType, TypeReference reference) {
        if (!(superType instanceof ReferenceBinding)) {
            return false;
        }
        if (reference == this.superTypeReference) {
            if (superType.isTypeVariable()) {
                return false;
            }
            if (superType.isParameterizedType()) {
                superType = ((ParameterizedTypeBinding)superType).genericType();
            }
            this.compilationUnitScope().recordSuperTypeReference(superType);
            return this.detectHierarchyCycle(this.referenceContext.binding, (ReferenceBinding)superType, reference);
        }
        if ((superType.tagBits & 0x100L) == 0L && superType instanceof SourceTypeBinding) {
            ((SourceTypeBinding)superType).scope.connectTypeHierarchyWithoutMembers();
        }
        return false;
    }

    private boolean detectHierarchyCycle(SourceTypeBinding sourceType, ReferenceBinding superType, TypeReference reference) {
        if (superType.isRawType()) {
            superType = ((RawTypeBinding)superType).genericType();
        }
        if (TypeBinding.equalsEquals(sourceType, superType)) {
            this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
            sourceType.tagBits |= 0x20000L;
            return true;
        }
        if (superType.isMemberType()) {
            ReferenceBinding current = superType.enclosingType();
            do {
                if (!current.isHierarchyBeingActivelyConnected()) continue;
                this.problemReporter().hierarchyCircularity(sourceType, current, reference);
                sourceType.tagBits |= 0x20000L;
                current.tagBits |= 0x20000L;
                return true;
            } while ((current = current.enclosingType()) != null);
        }
        if (superType.isBinaryBinding()) {
            ReferenceBinding[] itsInterfaces;
            if (superType.problemId() != 1 && (superType.tagBits & 0x20000L) != 0L) {
                sourceType.tagBits |= 0x20000L;
                this.problemReporter().hierarchyHasProblems(sourceType);
                return true;
            }
            boolean hasCycle = false;
            ReferenceBinding parentType = superType.superclass();
            if (parentType != null) {
                if (TypeBinding.equalsEquals(sourceType, parentType)) {
                    this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
                    sourceType.tagBits |= 0x20000L;
                    superType.tagBits |= 0x20000L;
                    return true;
                }
                if (parentType.isParameterizedType()) {
                    parentType = ((ParameterizedTypeBinding)parentType).genericType();
                }
                hasCycle |= this.detectHierarchyCycle(sourceType, parentType, reference);
                if ((parentType.tagBits & 0x20000L) != 0L) {
                    sourceType.tagBits |= 0x20000L;
                    parentType.tagBits |= 0x20000L;
                }
            }
            if ((itsInterfaces = superType.superInterfaces()) != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                int i = 0;
                int length = itsInterfaces.length;
                while (i < length) {
                    ReferenceBinding anInterface = itsInterfaces[i];
                    if (TypeBinding.equalsEquals(sourceType, anInterface)) {
                        this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
                        sourceType.tagBits |= 0x20000L;
                        superType.tagBits |= 0x20000L;
                        return true;
                    }
                    if (anInterface.isParameterizedType()) {
                        anInterface = ((ParameterizedTypeBinding)anInterface).genericType();
                    }
                    hasCycle |= this.detectHierarchyCycle(sourceType, anInterface, reference);
                    if ((anInterface.tagBits & 0x20000L) != 0L) {
                        sourceType.tagBits |= 0x20000L;
                        superType.tagBits |= 0x20000L;
                    }
                    ++i;
                }
            }
            return hasCycle;
        }
        if (superType.isHierarchyBeingActivelyConnected()) {
            TypeReference ref = ((SourceTypeBinding)superType).scope.superTypeReference;
            if (ref != null && ref.resolvedType != null) {
                ReferenceBinding s = (ReferenceBinding)ref.resolvedType;
                do {
                    if (!s.isHierarchyBeingActivelyConnected()) continue;
                    this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
                    sourceType.tagBits |= 0x20000L;
                    superType.tagBits |= 0x20000L;
                    return true;
                } while ((s = s.enclosingType()) != null);
            }
            if (ref != null && ref.resolvedType == null) {
                char[] referredName = ref.getLastToken();
                for (SourceTypeBinding type : this.environment().typesBeingConnected) {
                    if (!CharOperation.equals(referredName, type.sourceName())) continue;
                    this.problemReporter().hierarchyCircularity(sourceType, superType, reference);
                    sourceType.tagBits |= 0x20000L;
                    superType.tagBits |= 0x20000L;
                    return true;
                }
            }
        }
        if ((superType.tagBits & 0x100L) == 0L && superType.isValidBinding() && !superType.isUnresolvedType()) {
            ((SourceTypeBinding)superType).scope.connectTypeHierarchyWithoutMembers();
        }
        if ((superType.tagBits & 0x20000L) != 0L) {
            sourceType.tagBits |= 0x20000L;
        }
        return false;
    }

    private ReferenceBinding findSupertype(TypeReference typeReference) {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        LookupEnvironment env = unitScope.environment;
        try {
            ReferenceBinding superType;
            env.missingClassFileLocation = typeReference;
            typeReference.aboutToResolve(this);
            unitScope.recordQualifiedReference(typeReference.getTypeName());
            this.superTypeReference = typeReference;
            ReferenceBinding referenceBinding = superType = (ReferenceBinding)typeReference.resolveSuperType(this);
            return referenceBinding;
        }
        catch (AbortCompilation e) {
            SourceTypeBinding sourceType = this.referenceContext.binding;
            if (sourceType.superInterfaces == null) {
                sourceType.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
            }
            if (sourceType.permittedTypes == null) {
                sourceType.setPermittedTypes(Binding.NO_PERMITTEDTYPES);
            }
            e.updateContext(typeReference, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
            this.superTypeReference = null;
        }
    }

    private ReferenceBinding findPermittedtype(TypeReference typeReference) {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        LookupEnvironment env = unitScope.environment;
        try {
            ReferenceBinding permittedType;
            env.missingClassFileLocation = typeReference;
            typeReference.aboutToResolve(this);
            unitScope.recordQualifiedReference(typeReference.getTypeName());
            ReferenceBinding referenceBinding = permittedType = (ReferenceBinding)typeReference.resolveType(this);
            return referenceBinding;
        }
        catch (AbortCompilation e) {
            SourceTypeBinding sourceType = this.referenceContext.binding;
            if (sourceType.permittedTypes == null) {
                sourceType.setPermittedTypes(Binding.NO_PERMITTEDTYPES);
            }
            e.updateContext(typeReference, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }

    @Override
    public ProblemReporter problemReporter() {
        MethodScope outerMethodScope = this.outerMostMethodScope();
        if (outerMethodScope == null) {
            ProblemReporter problemReporter = this.referenceCompilationUnit().problemReporter;
            problemReporter.referenceContext = this.referenceContext;
            return problemReporter;
        }
        return outerMethodScope.problemReporter();
    }

    public TypeDeclaration referenceType() {
        return this.referenceContext;
    }

    @Override
    public boolean hasDefaultNullnessFor(int location, int sourceStart) {
        int nullDefault;
        int nonNullByDefaultValue = this.localNonNullByDefaultValue(sourceStart);
        if (nonNullByDefaultValue != 0) {
            return (nonNullByDefaultValue & location) != 0;
        }
        SourceTypeBinding binding = this.referenceContext.binding;
        if (binding != null && (nullDefault = binding.getNullDefault()) != 0) {
            return (nullDefault & location) != 0;
        }
        return this.parent.hasDefaultNullnessFor(location, sourceStart);
    }

    @Override
    public Binding checkRedundantDefaultNullness(int nullBits, int sourceStart) {
        int nullDefault;
        Binding target = this.localCheckRedundantDefaultNullness(nullBits, sourceStart);
        if (target != null) {
            return target;
        }
        SourceTypeBinding binding = this.referenceContext.binding;
        if (binding != null && (nullDefault = binding.getNullDefault()) != 0) {
            return nullDefault == nullBits ? binding : null;
        }
        return this.parent.checkRedundantDefaultNullness(nullBits, sourceStart);
    }

    public String toString() {
        if (this.referenceContext != null) {
            return "--- Class Scope ---\n\n" + this.referenceContext.binding.toString();
        }
        return "--- Class Scope ---\n\n Binding not initialized";
    }
}

