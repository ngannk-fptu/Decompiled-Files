/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationProvider;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithAnnotations;
import org.eclipse.jdt.internal.compiler.classfmt.NonNullDefaultAwareTypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.env.ClassSignature;
import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SignatureWrapper;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public class BinaryTypeBinding
extends ReferenceBinding {
    public static final char[] TYPE_QUALIFIER_DEFAULT = "TypeQualifierDefault".toCharArray();
    private static final IBinaryMethod[] NO_BINARY_METHODS = new IBinaryMethod[0];
    protected ReferenceBinding superclass;
    protected ReferenceBinding enclosingType;
    protected ReferenceBinding[] superInterfaces;
    protected ReferenceBinding[] permittedSubtypes;
    protected FieldBinding[] fields;
    protected RecordComponentBinding[] components;
    protected MethodBinding[] methods;
    protected ReferenceBinding[] memberTypes;
    protected TypeVariableBinding[] typeVariables;
    protected ModuleBinding module;
    private BinaryTypeBinding prototype;
    protected LookupEnvironment environment;
    protected SimpleLookupTable storedAnnotations = null;
    private ReferenceBinding containerAnnotationType;
    int defaultNullness = 0;
    boolean memberTypesSorted = false;
    public ExternalAnnotationStatus externalAnnotationStatus = ExternalAnnotationStatus.NOT_EEA_CONFIGURED;

    static Object convertMemberValue(Object binaryValue, LookupEnvironment env, char[][][] missingTypeNames, boolean resolveEnumConstants) {
        if (binaryValue == null) {
            return null;
        }
        if (binaryValue instanceof Constant) {
            return binaryValue;
        }
        if (binaryValue instanceof ClassSignature) {
            return env.getTypeFromSignature(((ClassSignature)binaryValue).getTypeName(), 0, -1, false, null, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER);
        }
        if (binaryValue instanceof IBinaryAnnotation) {
            return BinaryTypeBinding.createAnnotation((IBinaryAnnotation)binaryValue, env, missingTypeNames);
        }
        if (binaryValue instanceof EnumConstantSignature) {
            EnumConstantSignature ref = (EnumConstantSignature)binaryValue;
            ReferenceBinding enumType = (ReferenceBinding)env.getTypeFromSignature(ref.getTypeName(), 0, -1, false, null, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER);
            if (enumType.isUnresolvedType() && !resolveEnumConstants) {
                return new ElementValuePair.UnresolvedEnumConstant(enumType, env, ref.getEnumConstantName());
            }
            enumType = (ReferenceBinding)BinaryTypeBinding.resolveType(enumType, env, false);
            return enumType.getField(ref.getEnumConstantName(), false);
        }
        if (binaryValue instanceof Object[]) {
            Object[] objects = (Object[])binaryValue;
            int length = objects.length;
            if (length == 0) {
                return objects;
            }
            Object[] values = new Object[length];
            int i = 0;
            while (i < length) {
                values[i] = BinaryTypeBinding.convertMemberValue(objects[i], env, missingTypeNames, resolveEnumConstants);
                ++i;
            }
            return values;
        }
        throw new IllegalStateException();
    }

    @Override
    public TypeBinding clone(TypeBinding outerType) {
        BinaryTypeBinding copy = new BinaryTypeBinding(this);
        copy.enclosingType = (ReferenceBinding)outerType;
        copy.tagBits = copy.enclosingType != null ? (copy.tagBits |= 0x8000000L) : (copy.tagBits &= 0xFFFFFFFFF7FFFFFFL);
        copy.tagBits |= 0x10000000L;
        return copy;
    }

    static AnnotationBinding createAnnotation(IBinaryAnnotation annotationInfo, LookupEnvironment env, char[][][] missingTypeNames) {
        IBinaryElementValuePair[] binaryPairs;
        RuntimeException ex;
        if (annotationInfo instanceof AnnotationInfo && (ex = ((AnnotationInfo)annotationInfo).exceptionDuringDecode) != null) {
            new IllegalStateException("Accessing annotation with decode error", ex).printStackTrace();
        }
        int length = (binaryPairs = annotationInfo.getElementValuePairs()) == null ? 0 : binaryPairs.length;
        ElementValuePair[] pairs = length == 0 ? Binding.NO_ELEMENT_VALUE_PAIRS : new ElementValuePair[length];
        int i = 0;
        while (i < length) {
            pairs[i] = new ElementValuePair(binaryPairs[i].getName(), BinaryTypeBinding.convertMemberValue(binaryPairs[i].getValue(), env, missingTypeNames, false), null);
            ++i;
        }
        char[] typeName = annotationInfo.getTypeName();
        LookupEnvironment env2 = annotationInfo.isExternalAnnotation() ? env.root : env;
        ReferenceBinding annotationType = env2.getTypeFromConstantPoolName(typeName, 1, typeName.length - 1, false, missingTypeNames);
        return env2.createUnresolvedAnnotation(annotationType, pairs);
    }

    public static AnnotationBinding[] createAnnotations(IBinaryAnnotation[] annotationInfos, LookupEnvironment env, char[][][] missingTypeNames) {
        int length = annotationInfos == null ? 0 : annotationInfos.length;
        AnnotationBinding[] result = length == 0 ? Binding.NO_ANNOTATIONS : new AnnotationBinding[length];
        int i = 0;
        while (i < length) {
            result[i] = BinaryTypeBinding.createAnnotation(annotationInfos[i], env, missingTypeNames);
            ++i;
        }
        return result;
    }

    public static TypeBinding resolveType(TypeBinding type, LookupEnvironment environment, boolean convertGenericToRawType) {
        switch (type.kind()) {
            case 260: {
                ((ParameterizedTypeBinding)type).resolve();
                break;
            }
            case 516: 
            case 8196: {
                return ((WildcardBinding)type).resolve();
            }
            case 68: {
                ArrayBinding arrayBinding = (ArrayBinding)type;
                TypeBinding leafComponentType = arrayBinding.leafComponentType;
                BinaryTypeBinding.resolveType(leafComponentType, environment, convertGenericToRawType);
                if (!leafComponentType.hasNullTypeAnnotations() || !environment.usesNullTypeAnnotations()) break;
                if (arrayBinding.nullTagBitsPerDimension == null) {
                    arrayBinding.nullTagBitsPerDimension = new long[arrayBinding.dimensions + 1];
                }
                arrayBinding.nullTagBitsPerDimension[arrayBinding.dimensions] = leafComponentType.tagBits & 0x180000000000000L;
                break;
            }
            case 4100: {
                ((TypeVariableBinding)type).resolve();
                break;
            }
            case 2052: {
                if (!convertGenericToRawType) break;
                return environment.convertUnresolvedBinaryToRawType(type);
            }
            default: {
                if (type instanceof UnresolvedReferenceBinding) {
                    return ((UnresolvedReferenceBinding)type).resolve(environment, convertGenericToRawType);
                }
                if (!convertGenericToRawType) break;
                return environment.convertUnresolvedBinaryToRawType(type);
            }
        }
        return type;
    }

    protected BinaryTypeBinding() {
        this.prototype = this;
    }

    public BinaryTypeBinding(BinaryTypeBinding prototype) {
        super(prototype);
        this.superclass = prototype.superclass;
        this.enclosingType = prototype.enclosingType;
        this.superInterfaces = prototype.superInterfaces;
        this.permittedSubtypes = prototype.permittedSubtypes;
        this.fields = prototype.fields;
        this.methods = prototype.methods;
        this.memberTypes = prototype.memberTypes;
        this.typeVariables = prototype.typeVariables;
        this.prototype = prototype.prototype;
        this.environment = prototype.environment;
        this.storedAnnotations = prototype.storedAnnotations;
    }

    public BinaryTypeBinding(PackageBinding packageBinding, IBinaryType binaryType, LookupEnvironment environment) {
        this(packageBinding, binaryType, environment, false);
    }

    public BinaryTypeBinding(PackageBinding packageBinding, IBinaryType binaryType, LookupEnvironment environment, boolean needFieldsAndMethods) {
        this.prototype = this;
        this.compoundName = CharOperation.splitOn('/', binaryType.getName());
        this.computeId();
        this.tagBits |= 0x40L;
        this.environment = environment;
        this.fPackage = packageBinding;
        this.fileName = binaryType.getFileName();
        char[] typeSignature = binaryType.getGenericSignature();
        this.typeVariables = typeSignature != null && typeSignature.length > 0 && typeSignature[0] == '<' ? null : Binding.NO_TYPE_VARIABLES;
        this.sourceName = binaryType.getSourceName();
        this.modifiers = binaryType.getModifiers();
        if ((binaryType.getTagBits() & 0x20000L) != 0L) {
            this.tagBits |= 0x20000L;
        }
        if (binaryType.isAnonymous()) {
            this.tagBits |= 0x834L;
        } else if (binaryType.isLocal()) {
            this.tagBits |= 0x814L;
        } else if (binaryType.isMember()) {
            this.tagBits |= 0x80CL;
        }
        char[] enclosingTypeName = binaryType.getEnclosingTypeName();
        if (enclosingTypeName != null) {
            this.enclosingType = environment.getTypeFromConstantPoolName(enclosingTypeName, 0, -1, true, null);
            this.tagBits |= 0x80CL;
            this.tagBits |= 0x8000000L;
            if (this.enclosingType().isStrictfp()) {
                this.modifiers |= 0x800;
            }
            if (this.enclosingType().isDeprecated()) {
                this.modifiers |= 0x200000;
            }
        }
        if (needFieldsAndMethods) {
            this.cachePartsFrom(binaryType, true);
        }
    }

    @Override
    public boolean canBeSeenBy(Scope sco) {
        ModuleBinding mod = sco.module();
        return mod.canAccess(this.fPackage) && super.canBeSeenBy(sco);
    }

    @Override
    public FieldBinding[] availableFields() {
        if (!this.isPrototype()) {
            return this.prototype.availableFields();
        }
        if ((this.tagBits & 0x2000L) != 0L) {
            return this.fields;
        }
        if ((this.tagBits & 0x1000L) == 0L) {
            int length = this.fields.length;
            if (length > 1) {
                ReferenceBinding.sortFields(this.fields, 0, length);
            }
            this.tagBits |= 0x1000L;
        }
        FieldBinding[] availableFields = new FieldBinding[this.fields.length];
        int count = 0;
        int i = 0;
        while (i < this.fields.length) {
            try {
                availableFields[count] = this.resolveTypeFor(this.fields[i]);
                ++count;
            }
            catch (AbortCompilation abortCompilation) {}
            ++i;
        }
        if (count < availableFields.length) {
            FieldBinding[] fieldBindingArray = availableFields;
            availableFields = new FieldBinding[count];
            System.arraycopy(fieldBindingArray, 0, availableFields, 0, count);
        }
        return availableFields;
    }

    private TypeVariableBinding[] addMethodTypeVariables(TypeVariableBinding[] methodTypeVars) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.typeVariables == null || this.typeVariables == Binding.NO_TYPE_VARIABLES) {
            return methodTypeVars;
        }
        if (methodTypeVars == null || methodTypeVars == Binding.NO_TYPE_VARIABLES) {
            return this.typeVariables;
        }
        int total = this.typeVariables.length + methodTypeVars.length;
        TypeVariableBinding[] combinedTypeVars = new TypeVariableBinding[total];
        System.arraycopy(this.typeVariables, 0, combinedTypeVars, 0, this.typeVariables.length);
        int size = this.typeVariables.length;
        int i = 0;
        int len = methodTypeVars.length;
        while (i < len) {
            block7: {
                int j = this.typeVariables.length - 1;
                while (j >= 0) {
                    if (!CharOperation.equals(methodTypeVars[i].sourceName, this.typeVariables[j].sourceName)) {
                        --j;
                        continue;
                    }
                    break block7;
                }
                combinedTypeVars[size++] = methodTypeVars[i];
            }
            ++i;
        }
        if (size != total) {
            TypeVariableBinding[] typeVariableBindingArray = combinedTypeVars;
            combinedTypeVars = new TypeVariableBinding[size];
            System.arraycopy(typeVariableBindingArray, 0, combinedTypeVars, 0, size);
        }
        return combinedTypeVars;
    }

    @Override
    public MethodBinding[] availableMethods() {
        if (!this.isPrototype()) {
            return this.prototype.availableMethods();
        }
        if ((this.tagBits & 0x8000L) != 0L) {
            return this.methods;
        }
        if ((this.tagBits & 0x4000L) == 0L) {
            int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        MethodBinding[] availableMethods = new MethodBinding[this.methods.length];
        int count = 0;
        int i = 0;
        while (i < this.methods.length) {
            try {
                availableMethods[count] = this.resolveTypesFor(this.methods[i]);
                ++count;
            }
            catch (AbortCompilation abortCompilation) {}
            ++i;
        }
        if (count < availableMethods.length) {
            MethodBinding[] methodBindingArray = availableMethods;
            availableMethods = new MethodBinding[count];
            System.arraycopy(methodBindingArray, 0, availableMethods, 0, count);
        }
        return availableMethods;
    }

    void cachePartsFrom(IBinaryType binaryType, boolean needFieldsAndMethods) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        try {
            IBinaryAnnotation[] declAnnotations;
            boolean canUseNullTypeAnnotations;
            int i;
            MethodBinding enclosingMethod;
            int size;
            this.typeVariables = Binding.NO_TYPE_VARIABLES;
            this.superInterfaces = Binding.NO_SUPERINTERFACES;
            this.permittedSubtypes = Binding.NO_PERMITTEDTYPES;
            this.memberTypes = Binding.NO_MEMBER_TYPES;
            IBinaryNestedType[] memberTypeStructures = binaryType.getMemberTypes();
            if (memberTypeStructures != null && (size = memberTypeStructures.length) > 0) {
                this.memberTypes = new ReferenceBinding[size];
                int i2 = 0;
                while (i2 < size) {
                    this.memberTypes[i2] = this.environment.getTypeFromConstantPoolName(memberTypeStructures[i2].getName(), 0, -1, false, null);
                    ++i2;
                }
                this.tagBits |= 0x10000000L;
            }
            CompilerOptions globalOptions = this.environment.globalOptions;
            long sourceLevel = globalOptions.originalSourceLevel;
            if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                this.scanTypeForNullDefaultAnnotation(binaryType, this.fPackage);
            }
            ITypeAnnotationWalker walker = this.getTypeAnnotationWalker(binaryType.getTypeAnnotations(), 0);
            ITypeAnnotationWalker toplevelWalker = binaryType.enrichWithExternalAnnotationsFor(walker, null, this.environment);
            this.externalAnnotationStatus = binaryType.getExternalAnnotationStatus();
            if (this.externalAnnotationStatus.isPotentiallyUnannotatedLib() && this.defaultNullness != 0) {
                this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
            }
            char[] typeSignature = binaryType.getGenericSignature();
            this.tagBits |= binaryType.getTagBits();
            if (this.environment.globalOptions.complianceLevel < 0x340000L) {
                this.tagBits &= 0xFFDFFFFFFFFFFFFFL;
            }
            char[][][] missingTypeNames = binaryType.getMissingTypeNames();
            SignatureWrapper wrapper = null;
            if (typeSignature != null) {
                wrapper = new SignatureWrapper(typeSignature);
                if (wrapper.signature[wrapper.start] == '<') {
                    ++wrapper.start;
                    this.typeVariables = this.createTypeVariables(wrapper, true, missingTypeNames, toplevelWalker, true);
                    ++wrapper.start;
                    this.tagBits |= 0x1000000L;
                    this.modifiers |= 0x40000000;
                }
            }
            TypeVariableBinding[] typeVars = Binding.NO_TYPE_VARIABLES;
            char[] methodDescriptor = binaryType.getEnclosingMethod();
            if (methodDescriptor != null && (enclosingMethod = this.findMethod(methodDescriptor, missingTypeNames)) != null) {
                typeVars = enclosingMethod.typeVariables;
                this.typeVariables = this.addMethodTypeVariables(typeVars);
            }
            if (typeSignature == null) {
                int size2;
                char[] superclassName = binaryType.getSuperclassName();
                if (superclassName != null) {
                    this.superclass = this.environment.getTypeFromConstantPoolName(superclassName, 0, -1, false, missingTypeNames, toplevelWalker.toSupertype((short)-1, superclassName));
                    this.tagBits |= 0x2000000L;
                    if (CharOperation.equals(superclassName, TypeConstants.CharArray_JAVA_LANG_RECORD_SLASH)) {
                        this.modifiers |= 0x1000000;
                    }
                }
                this.superInterfaces = Binding.NO_SUPERINTERFACES;
                char[][] interfaceNames = binaryType.getInterfaceNames();
                if (interfaceNames != null && (size2 = interfaceNames.length) > 0) {
                    this.superInterfaces = new ReferenceBinding[size2];
                    i = 0;
                    while (i < size2) {
                        this.superInterfaces[i] = this.environment.getTypeFromConstantPoolName(interfaceNames[i], 0, -1, false, missingTypeNames, toplevelWalker.toSupertype((short)i, superclassName));
                        i = (short)(i + 1);
                    }
                    this.tagBits |= 0x4000000L;
                }
            } else {
                short rank;
                ArrayList<TypeBinding> types;
                this.superclass = (ReferenceBinding)this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames, toplevelWalker.toSupertype((short)-1, wrapper.peekFullType()));
                this.tagBits |= 0x2000000L;
                this.superInterfaces = Binding.NO_SUPERINTERFACES;
                if (!wrapper.atEnd()) {
                    types = new ArrayList<TypeBinding>(2);
                    rank = 0;
                    do {
                        short s = rank;
                        rank = (short)(s + 1);
                        types.add(this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames, toplevelWalker.toSupertype(s, wrapper.peekFullType())));
                    } while (!wrapper.atEnd());
                    this.superInterfaces = new ReferenceBinding[types.size()];
                    types.toArray(this.superInterfaces);
                    this.tagBits |= 0x4000000L;
                }
                this.permittedSubtypes = Binding.NO_PERMITTEDTYPES;
                if (!wrapper.atEnd()) {
                    types = new ArrayList(2);
                    rank = 0;
                    do {
                        short s = rank;
                        rank = (short)(s + 1);
                        types.add(this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames, toplevelWalker.toSupertype(s, wrapper.peekFullType())));
                    } while (!wrapper.atEnd());
                    this.permittedSubtypes = new ReferenceBinding[types.size()];
                    types.toArray(this.permittedSubtypes);
                    this.extendedTagBits |= 2;
                }
            }
            char[][] permittedSubtypeNames = binaryType.getPermittedSubtypeNames();
            if (this.permittedSubtypes == Binding.NO_PERMITTEDTYPES && permittedSubtypeNames != null) {
                this.modifiers |= 0x10000000;
                short size3 = permittedSubtypeNames.length;
                if (size3 > 0) {
                    this.permittedSubtypes = new ReferenceBinding[size3];
                    short i3 = 0;
                    while (i3 < size3) {
                        this.permittedSubtypes[i3] = this.environment.getTypeFromConstantPoolName(permittedSubtypeNames[i3], 0, -1, false, missingTypeNames, toplevelWalker.toSupertype(i3, null));
                        i3 = (short)(i3 + 1);
                    }
                }
            }
            boolean bl = canUseNullTypeAnnotations = this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && this.environment.globalOptions.sourceLevel >= 0x340000L;
            if (canUseNullTypeAnnotations && this.externalAnnotationStatus.isPotentiallyUnannotatedLib()) {
                if (this.superclass != null && this.superclass.hasNullTypeAnnotations()) {
                    this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
                } else {
                    ReferenceBinding[] referenceBindingArray = this.superInterfaces;
                    int n = this.superInterfaces.length;
                    i = 0;
                    while (i < n) {
                        ReferenceBinding ifc = referenceBindingArray[i];
                        if (ifc.hasNullTypeAnnotations()) {
                            this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
                            break;
                        }
                        ++i;
                    }
                    referenceBindingArray = this.permittedSubtypes;
                    n = this.permittedSubtypes.length;
                    i = 0;
                    while (i < n) {
                        ReferenceBinding permsub = referenceBindingArray[i];
                        if (permsub.hasNullTypeAnnotations()) {
                            this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
                            break;
                        }
                        ++i;
                    }
                }
            }
            if (needFieldsAndMethods) {
                int i4;
                IBinaryField[] iComponents = null;
                if (binaryType.isRecord() && (iComponents = binaryType.getRecordComponents()) != null) {
                    VariableBinding[] createFields = this.createFields(iComponents, binaryType, sourceLevel, missingTypeNames, true);
                    this.components = (RecordComponentBinding[])createFields;
                }
                IBinaryField[] iFields = binaryType.getFields();
                VariableBinding[] createdFields = this.createFields(iFields, binaryType, sourceLevel, missingTypeNames, false);
                this.fields = (FieldBinding[])createdFields;
                IBinaryMethod[] iMethods = this.createMethods(binaryType.getMethods(), binaryType, sourceLevel, missingTypeNames);
                boolean isViewedAsDeprecated = this.isViewedAsDeprecated();
                if (isViewedAsDeprecated) {
                    i4 = 0;
                    int max = this.fields.length;
                    while (i4 < max) {
                        FieldBinding field = this.fields[i4];
                        if (!field.isDeprecated()) {
                            field.modifiers |= 0x200000;
                        }
                        ++i4;
                    }
                    i4 = 0;
                    max = this.methods.length;
                    while (i4 < max) {
                        MethodBinding method = this.methods[i4];
                        if (!method.isDeprecated()) {
                            method.modifiers |= 0x200000;
                        }
                        ++i4;
                    }
                }
                if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                    if (iComponents != null) {
                        i4 = 0;
                        while (i4 < iComponents.length) {
                            ITypeAnnotationWalker fieldWalker = ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
                            if (sourceLevel < 0x340000L) {
                                fieldWalker = binaryType.enrichWithExternalAnnotationsFor(walker, iFields[i4], this.environment);
                            }
                            this.scanFieldForNullAnnotation(iComponents[i4], this.components[i4], this.isEnum(), fieldWalker);
                            ++i4;
                        }
                    }
                    if (iFields != null) {
                        i4 = 0;
                        while (i4 < iFields.length) {
                            ITypeAnnotationWalker fieldWalker = ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
                            if (sourceLevel < 0x340000L) {
                                fieldWalker = binaryType.enrichWithExternalAnnotationsFor(walker, iFields[i4], this.environment);
                            }
                            this.scanFieldForNullAnnotation(iFields[i4], this.fields[i4], this.isEnum(), fieldWalker);
                            ++i4;
                        }
                    }
                    if (iMethods != null) {
                        i4 = 0;
                        while (i4 < iMethods.length) {
                            ITypeAnnotationWalker methodWalker = ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
                            if (sourceLevel < 0x340000L) {
                                methodWalker = binaryType.enrichWithExternalAnnotationsFor(methodWalker, iMethods[i4], this.environment);
                            }
                            this.scanMethodForNullAnnotation(iMethods[i4], this.methods[i4], methodWalker, canUseNullTypeAnnotations);
                            ++i4;
                        }
                    }
                }
            }
            if ((declAnnotations = binaryType.getAnnotations()) != null) {
                if (this.hasValueBasedTypeAnnotation(declAnnotations)) {
                    this.extendedTagBits |= 4;
                }
                IBinaryAnnotation[] iBinaryAnnotationArray = declAnnotations;
                int n = declAnnotations.length;
                int n2 = 0;
                while (n2 < n) {
                    IBinaryAnnotation annotation = iBinaryAnnotationArray[n2];
                    char[] typeName = annotation.getTypeName();
                    if (CharOperation.equals(typeName, ConstantPool.JDK_INTERNAL_PREVIEW_FEATURE)) {
                        this.tagBits |= 0x180000000L;
                        break;
                    }
                    ++n2;
                }
            }
            if (this.environment.globalOptions.storeAnnotations) {
                this.setAnnotations(BinaryTypeBinding.createAnnotations(declAnnotations, this.environment, missingTypeNames), false);
            } else if (sourceLevel >= 0x350000L && this.isDeprecated() && binaryType.getAnnotations() != null) {
                IBinaryAnnotation[] iBinaryAnnotationArray = declAnnotations;
                int n = declAnnotations.length;
                int n3 = 0;
                while (n3 < n) {
                    IBinaryAnnotation annotation = iBinaryAnnotationArray[n3];
                    if (annotation.isDeprecatedAnnotation()) {
                        AnnotationBinding[] annotationBindings = BinaryTypeBinding.createAnnotations(new IBinaryAnnotation[]{annotation}, this.environment, missingTypeNames);
                        this.setAnnotations(annotationBindings, true);
                        ElementValuePair[] elementValuePairArray = annotationBindings[0].getElementValuePairs();
                        int n4 = elementValuePairArray.length;
                        int n5 = 0;
                        while (n5 < n4) {
                            ElementValuePair elementValuePair = elementValuePairArray[n5];
                            if (CharOperation.equals(elementValuePair.name, TypeConstants.FOR_REMOVAL) && elementValuePair.value instanceof BooleanConstant && ((BooleanConstant)elementValuePair.value).booleanValue()) {
                                this.tagBits |= 0x4000000000000000L;
                                this.markImplicitTerminalDeprecation(this);
                            }
                            ++n5;
                        }
                        break;
                    }
                    ++n3;
                }
            }
            if (this.isAnnotationType()) {
                this.scanTypeForContainerAnnotation(binaryType, missingTypeNames);
            }
        }
        finally {
            if (this.components == null) {
                this.components = Binding.NO_COMPONENTS;
            }
            if (this.fields == null) {
                this.fields = Binding.NO_FIELDS;
            }
            if (this.methods == null) {
                this.methods = Binding.NO_METHODS;
            }
        }
    }

    void markImplicitTerminalDeprecation(ReferenceBinding type) {
        FieldBinding[] fieldsOfType;
        ReferenceBinding[] referenceBindingArray = type.memberTypes();
        int n = referenceBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            ReferenceBinding member = referenceBindingArray[n2];
            member.tagBits |= 0x4000000000000000L;
            this.markImplicitTerminalDeprecation(member);
            ++n2;
        }
        MethodBinding[] methodsOfType = type.unResolvedMethods();
        if (methodsOfType != null) {
            MethodBinding[] methodBindingArray = methodsOfType;
            int n3 = methodsOfType.length;
            n = 0;
            while (n < n3) {
                MethodBinding methodBinding = methodBindingArray[n];
                methodBinding.tagBits |= 0x4000000000000000L;
                ++n;
            }
        }
        if ((fieldsOfType = type.unResolvedFields()) != null) {
            FieldBinding[] fieldBindingArray = fieldsOfType;
            int n4 = fieldsOfType.length;
            int n5 = 0;
            while (n5 < n4) {
                FieldBinding fieldBinding = fieldBindingArray[n5];
                fieldBinding.tagBits |= 0x4000000000000000L;
                ++n5;
            }
        }
    }

    private ITypeAnnotationWalker getTypeAnnotationWalker(IBinaryTypeAnnotation[] annotations, int nullness) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (annotations == null || annotations.length == 0 || !this.environment.usesAnnotatedTypeSystem()) {
            if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                if (nullness == 0) {
                    nullness = this.getNullDefault();
                }
                if (nullness > 2) {
                    return new NonNullDefaultAwareTypeAnnotationWalker(nullness, this.environment);
                }
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            if (nullness == 0) {
                nullness = this.getNullDefault();
            }
            if (nullness > 2) {
                return new NonNullDefaultAwareTypeAnnotationWalker(annotations, nullness, this.environment);
            }
        }
        return new TypeAnnotationWalker(annotations);
    }

    private boolean hasValueBasedTypeAnnotation(IBinaryAnnotation[] declAnnotations) {
        boolean hasValueBasedAnnotation = false;
        if (declAnnotations != null && declAnnotations.length > 0) {
            IBinaryAnnotation[] iBinaryAnnotationArray = declAnnotations;
            int n = declAnnotations.length;
            int n2 = 0;
            while (n2 < n) {
                IBinaryAnnotation annot = iBinaryAnnotationArray[n2];
                char[] typeName = annot.getTypeName();
                if (typeName != null && typeName.length >= 25 && typeName[0] == 'L') {
                    char[][] name = CharOperation.splitOn('/', typeName, 1, typeName.length - 1);
                    try {
                        if (CharOperation.equals(name, TypeConstants.JDK_INTERNAL_VALUEBASED)) {
                            hasValueBasedAnnotation = true;
                            break;
                        }
                    }
                    catch (Exception exception) {}
                }
                ++n2;
            }
        }
        return hasValueBasedAnnotation;
    }

    private int getNullDefaultFrom(IBinaryAnnotation[] declAnnotations) {
        int result = 0;
        if (declAnnotations != null) {
            IBinaryAnnotation[] iBinaryAnnotationArray = declAnnotations;
            int n = declAnnotations.length;
            int n2 = 0;
            while (n2 < n) {
                IBinaryAnnotation annotation = iBinaryAnnotationArray[n2];
                char[][] typeName = BinaryTypeBinding.signature2qualifiedTypeName(annotation.getTypeName());
                if (this.environment.getNullAnnotationBit(typeName) == 128) {
                    result |= BinaryTypeBinding.getNonNullByDefaultValue(annotation, this.environment);
                }
                ++n2;
            }
        }
        return result;
    }

    private VariableBinding[] createFields(IBinaryField[] iFields, IBinaryType binaryType, long sourceLevel, char[][][] missingTypeNames, boolean isComponent) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        boolean save = this.environment.mayTolerateMissingType;
        this.environment.mayTolerateMissingType = true;
        VariableBinding[] tFields = isComponent ? Binding.NO_COMPONENTS : Binding.NO_FIELDS;
        try {
            int size;
            if (iFields != null && (size = iFields.length) > 0) {
                IBinaryField binaryField;
                VariableBinding[] fields1 = isComponent ? new RecordComponentBinding[size] : new FieldBinding[size];
                boolean use15specifics = sourceLevel >= 0x310000L;
                boolean hasRestrictedAccess = this.hasRestrictedAccess();
                int firstAnnotatedFieldIndex = -1;
                int i = 0;
                while (i < size) {
                    boolean forceStoreAnnotations;
                    VariableBinding field;
                    binaryField = iFields[i];
                    char[] fieldSignature = use15specifics ? binaryField.getGenericSignature() : null;
                    IBinaryAnnotation[] declAnnotations = binaryField.getAnnotations();
                    ITypeAnnotationWalker walker = this.getTypeAnnotationWalker(binaryField.getTypeAnnotations(), this.getNullDefaultFrom(declAnnotations));
                    if (sourceLevel >= 0x340000L) {
                        walker = binaryType.enrichWithExternalAnnotationsFor(walker, iFields[i], this.environment);
                    }
                    walker = walker.toField();
                    TypeBinding type = fieldSignature == null ? this.environment.getTypeFromSignature(binaryField.getTypeName(), 0, -1, false, this, missingTypeNames, walker) : this.environment.getTypeFromTypeSignature(new SignatureWrapper(fieldSignature), Binding.NO_TYPE_VARIABLES, this, missingTypeNames, walker);
                    VariableBinding variableBinding = field = isComponent ? new RecordComponentBinding(binaryField.getName(), type, binaryField.getModifiers() | 0x2000000, this) : new FieldBinding(binaryField.getName(), type, binaryField.getModifiers() | 0x2000000, this, binaryField.getConstant());
                    if (declAnnotations != null) {
                        IBinaryAnnotation[] iBinaryAnnotationArray = declAnnotations;
                        int n = declAnnotations.length;
                        int n2 = 0;
                        while (n2 < n) {
                            IBinaryAnnotation annotation = iBinaryAnnotationArray[n2];
                            char[] typeName = annotation.getTypeName();
                            if (CharOperation.equals(typeName, ConstantPool.JDK_INTERNAL_PREVIEW_FEATURE)) {
                                field.tagBits |= 0x180000000L;
                                break;
                            }
                            ++n2;
                        }
                    }
                    boolean bl = forceStoreAnnotations = !this.environment.globalOptions.storeAnnotations && this.environment.globalOptions.sourceLevel >= 0x350000L && binaryField.getAnnotations() != null && (binaryField.getTagBits() & 0x400000000000L) != 0L;
                    if (firstAnnotatedFieldIndex < 0 && (this.environment.globalOptions.storeAnnotations || forceStoreAnnotations) && binaryField.getAnnotations() != null) {
                        firstAnnotatedFieldIndex = i;
                        if (forceStoreAnnotations) {
                            this.storedAnnotations(true, true);
                        }
                    }
                    field.id = i;
                    if (use15specifics) {
                        field.tagBits |= binaryField.getTagBits();
                    }
                    if (hasRestrictedAccess) {
                        field.modifiers |= 0x40000;
                    }
                    if (fieldSignature != null) {
                        field.modifiers |= 0x40000000;
                    }
                    fields1[i] = field;
                    ++i;
                }
                tFields = fields1;
                if (firstAnnotatedFieldIndex >= 0) {
                    i = firstAnnotatedFieldIndex;
                    while (i < size) {
                        binaryField = iFields[i];
                        tFields[i].setAnnotations(BinaryTypeBinding.createAnnotations(binaryField.getAnnotations(), this.environment, missingTypeNames), false);
                        ++i;
                    }
                }
            }
        }
        finally {
            this.environment.mayTolerateMissingType = save;
        }
        return tFields;
    }

    private MethodBinding createMethod(IBinaryMethod method, IBinaryType binaryType, long sourceLevel, char[][][] missingTypeNames) {
        boolean forceStoreAnnotations;
        IBinaryAnnotation[] receiverAnnotations;
        MethodBinding result;
        int i;
        int size;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        int methodModifiers = method.getModifiers() | 0x2000000;
        if (sourceLevel < 0x310000L) {
            methodModifiers &= 0xFFFFFF7F;
        }
        if (this.isInterface() && (methodModifiers & 0x400) == 0 && (methodModifiers & 8) == 0 && (methodModifiers & 2) == 0) {
            methodModifiers |= 0x10000;
        }
        ReferenceBinding[] exceptions = Binding.NO_EXCEPTIONS;
        TypeBinding[] parameters = Binding.NO_PARAMETERS;
        TypeVariableBinding[] typeVars = Binding.NO_TYPE_VARIABLES;
        AnnotationBinding[][] paramAnnotations = null;
        TypeBinding returnType = null;
        Object argumentNames = method.getArgumentNames();
        IBinaryAnnotation[] declAnnotations = method.getAnnotations();
        boolean use15specifics = sourceLevel >= 0x310000L;
        ITypeAnnotationWalker walker = this.getTypeAnnotationWalker(method.getTypeAnnotations(), this.getNullDefaultFrom(declAnnotations));
        char[] methodSignature = method.getGenericSignature();
        if (methodSignature == null) {
            int argumentNamesLength;
            char[][] exceptionTypes;
            int size2;
            char nextChar;
            char[] methodDescriptor = method.getMethodDescriptor();
            if (sourceLevel >= 0x340000L) {
                walker = binaryType.enrichWithExternalAnnotationsFor(walker, method, this.environment);
            }
            int numOfParams = 0;
            int index = 0;
            while ((nextChar = methodDescriptor[++index]) != ')') {
                if (nextChar == '[') continue;
                ++numOfParams;
                if (nextChar != 'L') continue;
                while ((nextChar = methodDescriptor[++index]) != ';') {
                }
            }
            int startIndex = 0;
            if (method.isConstructor()) {
                if (this.isMemberType() && !this.isStatic()) {
                    ++startIndex;
                }
                if (this.isEnum()) {
                    startIndex += 2;
                }
            }
            if ((size2 = numOfParams - startIndex) > 0) {
                parameters = new TypeBinding[size2];
                if (this.environment.globalOptions.storeAnnotations) {
                    paramAnnotations = new AnnotationBinding[size2][];
                }
                index = 1;
                short visibleIdx = 0;
                int end = 0;
                int i2 = 0;
                while (i2 < numOfParams) {
                    while ((nextChar = methodDescriptor[++end]) == '[') {
                    }
                    if (nextChar == 'L') {
                        while ((nextChar = methodDescriptor[++end]) != ';') {
                        }
                    }
                    if (i2 >= startIndex) {
                        short s = visibleIdx;
                        visibleIdx = (short)(s + 1);
                        parameters[i2 - startIndex] = this.environment.getTypeFromSignature(methodDescriptor, index, end, false, this, missingTypeNames, walker.toMethodParameter(s));
                        if (paramAnnotations != null) {
                            paramAnnotations[i2 - startIndex] = BinaryTypeBinding.createAnnotations(method.getParameterAnnotations(i2 - startIndex, this.fileName), this.environment, missingTypeNames);
                        }
                    }
                    index = end + 1;
                    ++i2;
                }
            }
            if ((exceptionTypes = method.getExceptionTypeNames()) != null && (size2 = exceptionTypes.length) > 0) {
                exceptions = new ReferenceBinding[size2];
                int i3 = 0;
                while (i3 < size2) {
                    exceptions[i3] = this.environment.getTypeFromConstantPoolName(exceptionTypes[i3], 0, -1, false, missingTypeNames, walker.toThrows(i3));
                    ++i3;
                }
            }
            if (!method.isConstructor()) {
                returnType = this.environment.getTypeFromSignature(methodDescriptor, index + 1, -1, false, this, missingTypeNames, walker.toMethodReturn());
            }
            int n = argumentNamesLength = argumentNames == null ? 0 : ((char[][])argumentNames).length;
            if (startIndex > 0 && argumentNamesLength > 0) {
                if (startIndex >= argumentNamesLength) {
                    argumentNames = Binding.NO_PARAMETER_NAMES;
                } else {
                    char[][] slicedArgumentNames = new char[argumentNamesLength - startIndex][];
                    System.arraycopy(argumentNames, startIndex, slicedArgumentNames, 0, argumentNamesLength - startIndex);
                    argumentNames = slicedArgumentNames;
                }
            }
        } else {
            ArrayList<TypeBinding> types;
            if (sourceLevel >= 0x340000L) {
                walker = binaryType.enrichWithExternalAnnotationsFor(walker, method, this.environment);
            }
            methodModifiers |= 0x40000000;
            SignatureWrapper wrapper = new SignatureWrapper(methodSignature, use15specifics);
            if (wrapper.signature[wrapper.start] == '<') {
                ++wrapper.start;
                typeVars = this.createTypeVariables(wrapper, false, missingTypeNames, walker, false);
                ++wrapper.start;
            }
            if (wrapper.signature[wrapper.start] == '(') {
                ++wrapper.start;
                if (wrapper.signature[wrapper.start] == ')') {
                    ++wrapper.start;
                } else {
                    types = new ArrayList<TypeBinding>(2);
                    short rank = 0;
                    while (wrapper.signature[wrapper.start] != ')') {
                        IBinaryAnnotation[] binaryParameterAnnotations = method.getParameterAnnotations(rank, this.fileName);
                        ITypeAnnotationWalker updatedWalker = NonNullDefaultAwareTypeAnnotationWalker.updateWalkerForParamNonNullDefault(walker, this.getNullDefaultFrom(binaryParameterAnnotations), this.environment);
                        types.add(this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames, updatedWalker.toMethodParameter(rank)));
                        rank = (short)(rank + 1);
                    }
                    ++wrapper.start;
                    int numParam = types.size();
                    parameters = new TypeBinding[numParam];
                    types.toArray(parameters);
                    if (this.environment.globalOptions.storeAnnotations) {
                        paramAnnotations = new AnnotationBinding[numParam][];
                        int i4 = 0;
                        while (i4 < numParam) {
                            paramAnnotations[i4] = BinaryTypeBinding.createAnnotations(method.getParameterAnnotations(i4, this.fileName), this.environment, missingTypeNames);
                            ++i4;
                        }
                    }
                }
            }
            returnType = this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames, walker.toMethodReturn());
            if (!wrapper.atEnd() && wrapper.signature[wrapper.start] == '^') {
                types = new ArrayList(2);
                int excRank = 0;
                do {
                    ++wrapper.start;
                    types.add(this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames, walker.toThrows(excRank++)));
                } while (!wrapper.atEnd() && wrapper.signature[wrapper.start] == '^');
                exceptions = new ReferenceBinding[types.size()];
                types.toArray(exceptions);
            } else {
                char[][] exceptionTypes = method.getExceptionTypeNames();
                if (exceptionTypes != null && (size = exceptionTypes.length) > 0) {
                    exceptions = new ReferenceBinding[size];
                    i = 0;
                    while (i < size) {
                        exceptions[i] = this.environment.getTypeFromConstantPoolName(exceptionTypes[i], 0, -1, false, missingTypeNames, walker.toThrows(i));
                        ++i;
                    }
                }
            }
        }
        MethodBinding methodBinding = result = method.isConstructor() ? new MethodBinding(methodModifiers, parameters, exceptions, this) : new MethodBinding(methodModifiers, method.getSelector(), returnType, parameters, exceptions, this);
        if (declAnnotations != null) {
            IBinaryAnnotation[] i4 = declAnnotations;
            i = declAnnotations.length;
            size = 0;
            while (size < i) {
                IBinaryAnnotation annotation = i4[size];
                char[] typeName = annotation.getTypeName();
                if (CharOperation.equals(typeName, ConstantPool.JDK_INTERNAL_PREVIEW_FEATURE)) {
                    result.tagBits |= 0x180000000L;
                    break;
                }
                ++size;
            }
        }
        if ((receiverAnnotations = walker.toReceiver().getAnnotationsAtCursor(this.id, false)) != null && receiverAnnotations.length > 0) {
            result.receiver = this.environment.createAnnotatedType((TypeBinding)this, BinaryTypeBinding.createAnnotations(receiverAnnotations, this.environment, missingTypeNames));
        }
        boolean bl = forceStoreAnnotations = !this.environment.globalOptions.storeAnnotations && this.environment.globalOptions.sourceLevel >= 0x350000L && method instanceof MethodInfoWithAnnotations && (method.getTagBits() & 0x400000000000L) != 0L;
        if (this.environment.globalOptions.storeAnnotations || forceStoreAnnotations) {
            if (forceStoreAnnotations) {
                this.storedAnnotations(true, true);
            }
            IBinaryAnnotation[] annotations = method.getAnnotations();
            if (method.isConstructor()) {
                IBinaryAnnotation[] tAnnotations = walker.toMethodReturn().getAnnotationsAtCursor(this.id, false);
                result.setTypeAnnotations(BinaryTypeBinding.createAnnotations(tAnnotations, this.environment, missingTypeNames));
            }
            result.setAnnotations(BinaryTypeBinding.createAnnotations(annotations, this.environment, missingTypeNames), paramAnnotations, this.isAnnotationType() ? BinaryTypeBinding.convertMemberValue(method.getDefaultValue(), this.environment, missingTypeNames, true) : null, this.environment);
        }
        if (argumentNames != null) {
            result.parameterNames = argumentNames;
        }
        if (use15specifics) {
            result.tagBits |= method.getTagBits();
        }
        result.typeVariables = typeVars;
        int i5 = 0;
        int length = typeVars.length;
        while (i5 < length) {
            this.environment.typeSystem.fixTypeVariableDeclaringElement(typeVars[i5], result);
            ++i5;
        }
        return result;
    }

    private IBinaryMethod[] createMethods(IBinaryMethod[] iMethods, IBinaryType binaryType, long sourceLevel, char[][][] missingTypeNames) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        boolean save = this.environment.mayTolerateMissingType;
        this.environment.mayTolerateMissingType = true;
        try {
            int total = 0;
            int initialTotal = 0;
            int iClinit = -1;
            int[] toSkip = null;
            if (iMethods != null) {
                total = initialTotal = iMethods.length;
                boolean keepBridgeMethods = sourceLevel < 0x310000L;
                int i = total;
                while (--i >= 0) {
                    char[] methodName;
                    IBinaryMethod method = iMethods[i];
                    if ((method.getModifiers() & 0x1000) != 0) {
                        if (keepBridgeMethods && (method.getModifiers() & 0x40) != 0) continue;
                        if (toSkip == null) {
                            toSkip = new int[iMethods.length];
                        }
                        toSkip[i] = -1;
                        --total;
                        continue;
                    }
                    if (iClinit != -1 || (methodName = method.getSelector()).length != 8 || methodName[0] != '<') continue;
                    iClinit = i;
                    --total;
                }
            }
            if (total == 0) {
                this.methods = Binding.NO_METHODS;
                IBinaryMethod[] iBinaryMethodArray = NO_BINARY_METHODS;
                return iBinaryMethodArray;
            }
            boolean hasRestrictedAccess = this.hasRestrictedAccess();
            MethodBinding[] methods1 = new MethodBinding[total];
            if (total == initialTotal) {
                int i = 0;
                while (i < initialTotal) {
                    MethodBinding method = this.createMethod(iMethods[i], binaryType, sourceLevel, missingTypeNames);
                    if (hasRestrictedAccess) {
                        method.modifiers |= 0x40000;
                    }
                    methods1[i] = method;
                    ++i;
                }
                this.methods = methods1;
                IBinaryMethod[] iBinaryMethodArray = iMethods;
                return iBinaryMethodArray;
            }
            IBinaryMethod[] mappedBinaryMethods = new IBinaryMethod[total];
            int i = 0;
            int index = 0;
            while (i < initialTotal) {
                if (iClinit != i && (toSkip == null || toSkip[i] != -1)) {
                    MethodBinding method = this.createMethod(iMethods[i], binaryType, sourceLevel, missingTypeNames);
                    if (hasRestrictedAccess) {
                        method.modifiers |= 0x40000;
                    }
                    mappedBinaryMethods[index] = iMethods[i];
                    methods1[index++] = method;
                }
                ++i;
            }
            this.methods = methods1;
            IBinaryMethod[] iBinaryMethodArray = mappedBinaryMethods;
            return iBinaryMethodArray;
        }
        finally {
            this.environment.mayTolerateMissingType = save;
        }
    }

    private TypeVariableBinding[] createTypeVariables(SignatureWrapper wrapper, boolean assignVariables, char[][][] missingTypeNames, ITypeAnnotationWalker walker, boolean isClassTypeParameter) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        char[] typeSignature = wrapper.signature;
        int depth = 0;
        int length = typeSignature.length;
        int rank = 0;
        ArrayList<TypeVariableBinding> variables = new ArrayList<TypeVariableBinding>(1);
        depth = 0;
        boolean pendingVariable = true;
        int i = 1;
        block5: while (i < length) {
            switch (typeSignature[i]) {
                case '<': {
                    ++depth;
                    break;
                }
                case '>': {
                    if (--depth >= 0) break;
                    break block5;
                }
                case ';': {
                    if (depth != 0 || i + 1 >= length || typeSignature[i + 1] == ':') break;
                    pendingVariable = true;
                    break;
                }
                default: {
                    AnnotationBinding[] annotations;
                    if (!pendingVariable) break;
                    pendingVariable = false;
                    int colon = CharOperation.indexOf(':', typeSignature, i);
                    char[] variableName = CharOperation.subarray(typeSignature, i, colon);
                    TypeVariableBinding typeVariable = new TypeVariableBinding(variableName, this, rank, this.environment);
                    if ((annotations = BinaryTypeBinding.createAnnotations(walker.toTypeParameter(isClassTypeParameter, rank++).getAnnotationsAtCursor(0, false), this.environment, missingTypeNames)) != null && annotations != Binding.NO_ANNOTATIONS) {
                        typeVariable.setTypeAnnotations(annotations, this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled);
                    }
                    variables.add(typeVariable);
                }
            }
            ++i;
        }
        TypeVariableBinding[] result = new TypeVariableBinding[rank];
        variables.toArray(result);
        if (assignVariables) {
            this.typeVariables = result;
        }
        int i2 = 0;
        while (i2 < rank) {
            this.initializeTypeVariable(result[i2], result, wrapper, missingTypeNames, walker.toTypeParameterBounds(isClassTypeParameter, i2));
            if (this.externalAnnotationStatus.isPotentiallyUnannotatedLib() && result[i2].hasNullTypeAnnotations()) {
                this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
            }
            ++i2;
        }
        return result;
    }

    @Override
    public ReferenceBinding enclosingType() {
        if ((this.tagBits & 0x8000000L) == 0L) {
            return this.enclosingType;
        }
        this.enclosingType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.enclosingType, this.environment, false);
        this.tagBits &= 0xFFFFFFFFF7FFFFFFL;
        return this.enclosingType;
    }

    @Override
    public RecordComponentBinding[] components() {
        if (!this.isPrototype()) {
            this.components = this.prototype.components;
            return this.prototype.components;
        }
        if ((this.extendedTagBits & 1) != 0) {
            return this.components;
        }
        int i = this.components.length;
        while (--i >= 0) {
            this.resolveTypeFor(this.components[i]);
        }
        this.tagBits |= 1L;
        return this.components;
    }

    @Override
    public FieldBinding[] fields() {
        if (!this.isPrototype()) {
            this.fields = this.prototype.fields();
            return this.fields;
        }
        if ((this.tagBits & 0x2000L) != 0L) {
            return this.fields;
        }
        if ((this.tagBits & 0x1000L) == 0L) {
            int length = this.fields.length;
            if (length > 1) {
                ReferenceBinding.sortFields(this.fields, 0, length);
            }
            this.tagBits |= 0x1000L;
        }
        int i = this.fields.length;
        while (--i >= 0) {
            this.resolveTypeFor(this.fields[i]);
        }
        this.tagBits |= 0x2000L;
        return this.fields;
    }

    private MethodBinding findMethod(char[] methodDescriptor, char[][][] missingTypeNames) {
        char nextChar;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        int index = -1;
        while (methodDescriptor[++index] != '(') {
        }
        char[] selector = new char[index];
        System.arraycopy(methodDescriptor, 0, selector, 0, index);
        TypeBinding[] parameters = Binding.NO_PARAMETERS;
        int numOfParams = 0;
        int paramStart = index;
        while ((nextChar = methodDescriptor[++index]) != ')') {
            if (nextChar == '[') continue;
            ++numOfParams;
            if (nextChar != 'L') continue;
            while ((nextChar = methodDescriptor[++index]) != ';') {
            }
        }
        if (numOfParams > 0) {
            parameters = new TypeBinding[numOfParams];
            index = paramStart + 1;
            int end = paramStart;
            int i = 0;
            while (i < numOfParams) {
                TypeBinding param;
                while ((nextChar = methodDescriptor[++end]) == '[') {
                }
                if (nextChar == 'L') {
                    while ((nextChar = methodDescriptor[++end]) != ';') {
                    }
                }
                if ((param = this.environment.getTypeFromSignature(methodDescriptor, index, end, false, this, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER)) instanceof UnresolvedReferenceBinding) {
                    param = BinaryTypeBinding.resolveType(param, this.environment, true);
                }
                parameters[i] = param;
                index = end + 1;
                ++i;
            }
        }
        int parameterLength = parameters.length;
        MethodBinding[] methods2 = this.enclosingType.getMethods(selector, parameterLength);
        int i = 0;
        int max = methods2.length;
        while (i < max) {
            block14: {
                MethodBinding currentMethod = methods2[i];
                TypeBinding[] parameters2 = currentMethod.parameters;
                int currentMethodParameterLength = parameters2.length;
                if (parameterLength == currentMethodParameterLength) {
                    int j = 0;
                    while (j < currentMethodParameterLength) {
                        if (!TypeBinding.notEquals(parameters[j], parameters2[j]) || !TypeBinding.notEquals(parameters[j].erasure(), parameters2[j].erasure())) {
                            ++j;
                            continue;
                        }
                        break block14;
                    }
                    return currentMethod;
                }
            }
            ++i;
        }
        return null;
    }

    @Override
    public char[] genericTypeSignature() {
        if (!this.isPrototype()) {
            return this.prototype.computeGenericTypeSignature(this.typeVariables);
        }
        return this.computeGenericTypeSignature(this.typeVariables);
    }

    @Override
    public MethodBinding getExactConstructor(TypeBinding[] argumentTypes) {
        if (!this.isPrototype()) {
            return this.prototype.getExactConstructor(argumentTypes);
        }
        if ((this.tagBits & 0x4000L) == 0L) {
            int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        int argCount = argumentTypes.length;
        long range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods);
        if (range >= 0L) {
            int imethod = (int)range;
            int end = (int)(range >> 32);
            while (imethod <= end) {
                block8: {
                    MethodBinding method = this.methods[imethod];
                    if (method.parameters.length == argCount) {
                        this.resolveTypesFor(method);
                        TypeBinding[] toMatch = method.parameters;
                        int iarg = 0;
                        while (iarg < argCount) {
                            if (!TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                ++iarg;
                                continue;
                            }
                            break block8;
                        }
                        return method;
                    }
                }
                ++imethod;
            }
        }
        return null;
    }

    @Override
    public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope) {
        if (!this.isPrototype()) {
            return this.prototype.getExactMethod(selector, argumentTypes, refScope);
        }
        if ((this.tagBits & 0x4000L) == 0L) {
            int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        int argCount = argumentTypes.length;
        boolean foundNothing = true;
        long range = ReferenceBinding.binarySearch(selector, this.methods);
        if (range >= 0L) {
            int imethod = (int)range;
            int end = (int)(range >> 32);
            while (imethod <= end) {
                block15: {
                    MethodBinding method = this.methods[imethod];
                    foundNothing = false;
                    if (method.parameters.length == argCount) {
                        this.resolveTypesFor(method);
                        TypeBinding[] toMatch = method.parameters;
                        int iarg = 0;
                        while (iarg < argCount) {
                            if (!TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                ++iarg;
                                continue;
                            }
                            break block15;
                        }
                        return method;
                    }
                }
                ++imethod;
            }
        }
        if (foundNothing) {
            if (this.isInterface()) {
                if (this.superInterfaces().length == 1) {
                    if (refScope != null) {
                        refScope.recordTypeReference(this.superInterfaces[0]);
                    }
                    return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
                }
            } else if (this.superclass() != null) {
                if (refScope != null) {
                    refScope.recordTypeReference(this.superclass);
                }
                return this.superclass.getExactMethod(selector, argumentTypes, refScope);
            }
        }
        return null;
    }

    @Override
    public FieldBinding getField(char[] fieldName, boolean needResolve) {
        if (!this.isPrototype()) {
            return this.prototype.getField(fieldName, needResolve);
        }
        if ((this.tagBits & 0x1000L) == 0L) {
            int length = this.fields.length;
            if (length > 1) {
                ReferenceBinding.sortFields(this.fields, 0, length);
            }
            this.tagBits |= 0x1000L;
        }
        FieldBinding field = ReferenceBinding.binarySearch(fieldName, this.fields);
        return needResolve && field != null ? this.resolveTypeFor(field) : field;
    }

    @Override
    public ReferenceBinding getMemberType(char[] typeName) {
        if (!this.isPrototype()) {
            ReferenceBinding memberType = this.prototype.getMemberType(typeName);
            return memberType == null ? null : this.environment.createMemberType(memberType, this);
        }
        ReferenceBinding[] members = this.maybeSortedMemberTypes();
        if (!this.memberTypesSorted) {
            int i = members.length;
            while (--i >= 0) {
                ReferenceBinding memberType = members[i];
                if (memberType instanceof UnresolvedReferenceBinding) {
                    char[] name = memberType.sourceName;
                    int prefixLength = this.compoundName[this.compoundName.length - 1].length + 1;
                    if (name.length != prefixLength + typeName.length || !CharOperation.fragmentEquals(typeName, name, prefixLength, true)) continue;
                    members[i] = (ReferenceBinding)BinaryTypeBinding.resolveType(memberType, this.environment, false);
                    return members[i];
                }
                if (!CharOperation.equals(typeName, memberType.sourceName)) continue;
                return memberType;
            }
            return null;
        }
        int memberTypeIndex = ReferenceBinding.binarySearch(typeName, members);
        if (memberTypeIndex >= 0) {
            return members[memberTypeIndex];
        }
        return null;
    }

    @Override
    public MethodBinding[] getMethods(char[] selector) {
        long range;
        if (!this.isPrototype()) {
            return this.prototype.getMethods(selector);
        }
        if ((this.tagBits & 0x8000L) != 0L) {
            long range2 = ReferenceBinding.binarySearch(selector, this.methods);
            if (range2 >= 0L) {
                int start = (int)range2;
                int end = (int)(range2 >> 32);
                int length = end - start + 1;
                if ((this.tagBits & 0x8000L) != 0L) {
                    MethodBinding[] result = new MethodBinding[length];
                    System.arraycopy(this.methods, start, result, 0, length);
                    return result;
                }
            }
            return Binding.NO_METHODS;
        }
        if ((this.tagBits & 0x4000L) == 0L) {
            int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
            int start = (int)range;
            int end = (int)(range >> 32);
            int length = end - start + 1;
            MethodBinding[] result = new MethodBinding[length];
            int i = start;
            int index = 0;
            while (i <= end) {
                result[index] = this.resolveTypesFor(this.methods[i]);
                ++i;
                ++index;
            }
            return result;
        }
        return Binding.NO_METHODS;
    }

    @Override
    public MethodBinding[] getMethods(char[] selector, int suggestedParameterLength) {
        long range;
        if (!this.isPrototype()) {
            return this.prototype.getMethods(selector, suggestedParameterLength);
        }
        if ((this.tagBits & 0x8000L) != 0L) {
            return this.getMethods(selector);
        }
        if ((this.tagBits & 0x4000L) == 0L) {
            int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
            int start = (int)range;
            int end = (int)(range >> 32);
            int length = end - start + 1;
            int count = 0;
            int i = start;
            while (i <= end) {
                if (this.methods[i].doesParameterLengthMatch(suggestedParameterLength)) {
                    ++count;
                }
                ++i;
            }
            if (count == 0) {
                MethodBinding[] result = new MethodBinding[length];
                int i2 = start;
                int index = 0;
                while (i2 <= end) {
                    result[index++] = this.resolveTypesFor(this.methods[i2]);
                    ++i2;
                }
                return result;
            }
            MethodBinding[] result = new MethodBinding[count];
            int i3 = start;
            int index = 0;
            while (i3 <= end) {
                if (this.methods[i3].doesParameterLengthMatch(suggestedParameterLength)) {
                    result[index++] = this.resolveTypesFor(this.methods[i3]);
                }
                ++i3;
            }
            return result;
        }
        return Binding.NO_METHODS;
    }

    @Override
    public boolean hasMemberTypes() {
        if (!this.isPrototype()) {
            return this.prototype.hasMemberTypes();
        }
        return this.memberTypes.length > 0;
    }

    @Override
    public TypeVariableBinding getTypeVariable(char[] variableName) {
        if (!this.isPrototype()) {
            return this.prototype.getTypeVariable(variableName);
        }
        TypeVariableBinding variable = super.getTypeVariable(variableName);
        variable.resolve();
        return variable;
    }

    @Override
    public boolean hasTypeBit(int bit) {
        if (!this.isPrototype()) {
            return this.prototype.hasTypeBit(bit);
        }
        boolean wasToleratingMissingTypeProcessingAnnotations = this.environment.mayTolerateMissingType;
        this.environment.mayTolerateMissingType = true;
        try {
            this.superclass();
            this.superInterfaces();
        }
        finally {
            this.environment.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
        }
        return (this.typeBits & bit) != 0;
    }

    private void initializeTypeVariable(TypeVariableBinding variable, TypeVariableBinding[] existingVariables, SignatureWrapper wrapper, char[][][] missingTypeNames, ITypeAnnotationWalker walker) {
        ReferenceBinding type;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        int colon = CharOperation.indexOf(':', wrapper.signature, wrapper.start);
        wrapper.start = colon + 1;
        ReferenceBinding firstBound = null;
        short rank = 0;
        if (wrapper.signature[wrapper.start] == ':') {
            type = this.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, null);
            rank = (short)(rank + 1);
        } else {
            short s = rank;
            rank = (short)(s + 1);
            TypeBinding typeFromTypeSignature = this.environment.getTypeFromTypeSignature(wrapper, existingVariables, this, missingTypeNames, walker.toTypeBound(s));
            type = typeFromTypeSignature instanceof ReferenceBinding ? (ReferenceBinding)typeFromTypeSignature : this.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, null);
            firstBound = type;
        }
        variable.modifiers |= 0x2000000;
        variable.setSuperClass(type);
        ReferenceBinding[] bounds = null;
        if (wrapper.signature[wrapper.start] == ':') {
            ArrayList<TypeBinding> types = new ArrayList<TypeBinding>(2);
            do {
                ++wrapper.start;
                short s = rank;
                rank = (short)(s + 1);
                types.add(this.environment.getTypeFromTypeSignature(wrapper, existingVariables, this, missingTypeNames, walker.toTypeBound(s)));
            } while (wrapper.signature[wrapper.start] == ':');
            bounds = new ReferenceBinding[types.size()];
            types.toArray(bounds);
        }
        variable.setSuperInterfaces(bounds == null ? Binding.NO_SUPERINTERFACES : bounds);
        if (firstBound == null) {
            firstBound = variable.superInterfaces.length == 0 ? null : variable.superInterfaces[0];
        }
        variable.setFirstBound(firstBound);
    }

    @Override
    public boolean isEquivalentTo(TypeBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        switch (otherType.kind()) {
            case 516: 
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
            case 260: 
            case 1028: {
                return TypeBinding.equalsEquals(otherType.erasure(), this);
            }
        }
        return false;
    }

    @Override
    public boolean isGenericType() {
        if (!this.isPrototype()) {
            return this.prototype.isGenericType();
        }
        return this.typeVariables != Binding.NO_TYPE_VARIABLES;
    }

    @Override
    public boolean isHierarchyConnected() {
        if (!this.isPrototype()) {
            return this.prototype.isHierarchyConnected();
        }
        return (this.tagBits & 0x6000000L) == 0L;
    }

    @Override
    public boolean isRepeatableAnnotationType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        return this.containerAnnotationType != null;
    }

    @Override
    public int kind() {
        if (!this.isPrototype()) {
            return this.prototype.kind();
        }
        if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            return 2052;
        }
        return 4;
    }

    @Override
    public ReferenceBinding[] memberTypes() {
        if (!this.isPrototype()) {
            if ((this.tagBits & 0x10000000L) == 0L) {
                return this.memberTypes;
            }
            ReferenceBinding[] members = this.prototype.memberTypes();
            if (members != null) {
                this.memberTypes = new ReferenceBinding[members.length];
                int i = 0;
                while (i < members.length) {
                    this.memberTypes[i] = this.environment.createMemberType(members[i], this);
                    ++i;
                }
            }
            this.tagBits &= 0xFFFFFFFFEFFFFFFFL;
            this.memberTypesSorted = true;
            return this.memberTypes;
        }
        if ((this.tagBits & 0x10000000L) == 0L) {
            return this.maybeSortedMemberTypes();
        }
        int i = this.memberTypes.length;
        while (--i >= 0) {
            this.memberTypes[i] = (ReferenceBinding)BinaryTypeBinding.resolveType(this.memberTypes[i], this.environment, false);
        }
        this.tagBits &= 0xFFFFFFFFEFFFFFFFL;
        return this.maybeSortedMemberTypes();
    }

    private ReferenceBinding[] maybeSortedMemberTypes() {
        if ((this.tagBits & 0x10000000L) != 0L) {
            return this.memberTypes;
        }
        if (!this.memberTypesSorted) {
            int length = this.memberTypes.length;
            if (length > 1) {
                BinaryTypeBinding.sortMemberTypes(this.memberTypes, 0, length);
            }
            this.memberTypesSorted = true;
        }
        return this.memberTypes;
    }

    @Override
    public MethodBinding[] methods() {
        if (!this.isPrototype()) {
            this.methods = this.prototype.methods();
            return this.methods;
        }
        if ((this.tagBits & 0x8000L) != 0L) {
            return this.methods;
        }
        if ((this.tagBits & 0x4000L) == 0L) {
            int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        int i = this.methods.length;
        while (--i >= 0) {
            this.resolveTypesFor(this.methods[i]);
        }
        this.tagBits |= 0x8000L;
        return this.methods;
    }

    @Override
    public void setHierarchyCheckDone() {
        this.tagBits |= 0x300L;
    }

    @Override
    public TypeBinding prototype() {
        return this.prototype;
    }

    private boolean isPrototype() {
        return this == this.prototype;
    }

    @Override
    public boolean isRecord() {
        return (this.modifiers & 0x1000000) != 0;
    }

    @Override
    public ReferenceBinding containerAnnotationType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.containerAnnotationType instanceof UnresolvedReferenceBinding) {
            this.containerAnnotationType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.containerAnnotationType, this.environment, false);
        }
        return this.containerAnnotationType;
    }

    private RecordComponentBinding resolveTypeFor(RecordComponentBinding component) {
        TypeBinding resolvedType;
        if (!this.isPrototype()) {
            return this.prototype.resolveTypeFor(component);
        }
        if ((component.modifiers & 0x2000000) == 0) {
            return component;
        }
        component.type = resolvedType = BinaryTypeBinding.resolveType(component.type, this.environment, true);
        if ((resolvedType.tagBits & 0x80L) != 0L) {
            component.tagBits |= 0x80L;
        }
        component.modifiers &= 0xFDFFFFFF;
        return component;
    }

    private FieldBinding resolveTypeFor(FieldBinding field) {
        TypeBinding resolvedType;
        if (!this.isPrototype()) {
            return this.prototype.resolveTypeFor(field);
        }
        if ((field.modifiers & 0x2000000) == 0) {
            return field;
        }
        field.type = resolvedType = BinaryTypeBinding.resolveType(field.type, this.environment, true);
        if ((resolvedType.tagBits & 0x80L) != 0L) {
            field.tagBits |= 0x80L;
        }
        field.modifiers &= 0xFDFFFFFF;
        return field;
    }

    MethodBinding resolveTypesFor(MethodBinding method) {
        TypeBinding resolvedType;
        if (!this.isPrototype()) {
            return this.prototype.resolveTypesFor(method);
        }
        if ((method.modifiers & 0x2000000) == 0) {
            return method;
        }
        if (!method.isConstructor()) {
            TypeBinding resolvedType2;
            method.returnType = resolvedType2 = BinaryTypeBinding.resolveType(method.returnType, this.environment, true);
            if ((resolvedType2.tagBits & 0x80L) != 0L) {
                method.tagBits |= 0x80L;
            }
        }
        int i = method.parameters.length;
        while (--i >= 0) {
            method.parameters[i] = resolvedType = BinaryTypeBinding.resolveType(method.parameters[i], this.environment, true);
            if ((resolvedType.tagBits & 0x80L) == 0L) continue;
            method.tagBits |= 0x80L;
        }
        i = method.thrownExceptions.length;
        while (--i >= 0) {
            resolvedType = (ReferenceBinding)BinaryTypeBinding.resolveType(method.thrownExceptions[i], this.environment, true);
            method.thrownExceptions[i] = resolvedType;
            if ((((ReferenceBinding)resolvedType).tagBits & 0x80L) == 0L) continue;
            method.tagBits |= 0x80L;
        }
        i = method.typeVariables.length;
        while (--i >= 0) {
            method.typeVariables[i].resolve();
        }
        method.modifiers &= 0xFDFFFFFF;
        return method;
    }

    @Override
    AnnotationBinding[] retrieveAnnotations(Binding binding) {
        if (!this.isPrototype()) {
            return this.prototype.retrieveAnnotations(binding);
        }
        return AnnotationBinding.addStandardAnnotations(super.retrieveAnnotations(binding), binding.getAnnotationTagBits(), this.environment);
    }

    @Override
    public void setContainerAnnotationType(ReferenceBinding value) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        this.containerAnnotationType = value;
    }

    @Override
    public void tagAsHavingDefectiveContainerType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.containerAnnotationType != null && this.containerAnnotationType.isValidBinding()) {
            this.containerAnnotationType = new ProblemReferenceBinding(this.containerAnnotationType.compoundName, this.containerAnnotationType, 22);
        }
    }

    @Override
    SimpleLookupTable storedAnnotations(boolean forceInitialize, boolean forceStore) {
        if (!this.isPrototype()) {
            return this.prototype.storedAnnotations(forceInitialize, forceStore);
        }
        if (forceInitialize && this.storedAnnotations == null) {
            if (!this.environment.globalOptions.storeAnnotations && !forceStore) {
                return null;
            }
            this.storedAnnotations = new SimpleLookupTable(3);
        }
        return this.storedAnnotations;
    }

    private void scanFieldForNullAnnotation(IBinaryField field, VariableBinding fieldBinding, boolean isEnum, ITypeAnnotationWalker externalAnnotationWalker) {
        int nullDefaultFromField;
        IBinaryAnnotation[] annotations;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (isEnum && (field.getModifiers() & 0x4000) != 0) {
            fieldBinding.tagBits |= 0x100000000000000L;
            return;
        }
        if (!CharOperation.equals(this.fPackage.compoundName, TypeConstants.JAVA_LANG_ANNOTATION) && this.environment.usesNullTypeAnnotations()) {
            int nullDefaultFromField2;
            TypeBinding fieldType = fieldBinding.type;
            if (fieldType != null && !fieldType.isBaseType() && (fieldType.tagBits & 0x180000000000000L) == 0L && fieldType.acceptsNonNullDefault() && ((nullDefaultFromField2 = this.getNullDefaultFrom(field.getAnnotations())) == 0 ? this.hasNonNullDefaultFor(32, -1) : (nullDefaultFromField2 & 0x20) != 0)) {
                fieldBinding.type = this.environment.createAnnotatedType(fieldType, new AnnotationBinding[]{this.environment.getNonNullAnnotation()});
            }
            return;
        }
        if (fieldBinding.type == null || fieldBinding.type.isBaseType()) {
            return;
        }
        boolean explicitNullness = false;
        IBinaryAnnotation[] iBinaryAnnotationArray = annotations = externalAnnotationWalker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER ? externalAnnotationWalker.getAnnotationsAtCursor(fieldBinding.type.id, false) : field.getAnnotations();
        if (annotations != null) {
            int i = 0;
            while (i < annotations.length) {
                char[] annotationTypeName = annotations[i].getTypeName();
                if (annotationTypeName[0] == 'L') {
                    int typeBit = this.environment.getNullAnnotationBit(BinaryTypeBinding.signature2qualifiedTypeName(annotationTypeName));
                    if (typeBit == 32) {
                        fieldBinding.tagBits |= 0x100000000000000L;
                        explicitNullness = true;
                        break;
                    }
                    if (typeBit == 64) {
                        fieldBinding.tagBits |= 0x80000000000000L;
                        explicitNullness = true;
                        break;
                    }
                }
                ++i;
            }
        }
        if (explicitNullness && this.externalAnnotationStatus.isPotentiallyUnannotatedLib()) {
            this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
        }
        if (!explicitNullness && ((nullDefaultFromField = this.getNullDefaultFrom(field.getAnnotations())) == 0 ? this.hasNonNullDefaultFor(32, -1) : (nullDefaultFromField & 0x20) != 0)) {
            fieldBinding.tagBits |= 0x100000000000000L;
        }
    }

    private void scanMethodForNullAnnotation(IBinaryMethod method, MethodBinding methodBinding, ITypeAnnotationWalker externalAnnotationWalker, boolean useNullTypeAnnotations) {
        int numParamAnnotations;
        ITypeAnnotationWalker returnWalker;
        IBinaryAnnotation[] annotations;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.isEnum()) {
            int purpose = 0;
            if (CharOperation.equals(TypeConstants.VALUEOF, method.getSelector()) && methodBinding.parameters.length == 1 && methodBinding.parameters[0].id == 11) {
                purpose = 10;
            } else if (CharOperation.equals(TypeConstants.VALUES, method.getSelector()) && methodBinding.parameters == Binding.NO_PARAMETERS) {
                purpose = 9;
            }
            if (purpose != 0) {
                boolean needToDefer;
                boolean bl = needToDefer = this.environment.globalOptions.useNullTypeAnnotations == null;
                if (needToDefer) {
                    this.environment.deferredEnumMethods.add(methodBinding);
                } else {
                    SyntheticMethodBinding.markNonNull(methodBinding, purpose, this.environment);
                }
                return;
            }
        }
        IBinaryAnnotation[] iBinaryAnnotationArray = annotations = (returnWalker = externalAnnotationWalker.toMethodReturn()) != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER ? returnWalker.getAnnotationsAtCursor(methodBinding.returnType.id, false) : method.getAnnotations();
        if (annotations != null) {
            int methodDefaultNullness = 0;
            int i = 0;
            while (i < annotations.length) {
                char[] annotationTypeName = annotations[i].getTypeName();
                if (annotationTypeName[0] == 'L') {
                    int typeBit = this.environment.getNullAnnotationBit(BinaryTypeBinding.signature2qualifiedTypeName(annotationTypeName));
                    if (typeBit == 128) {
                        methodDefaultNullness |= BinaryTypeBinding.getNonNullByDefaultValue(annotations[i], this.environment);
                    } else if (typeBit == 32) {
                        methodBinding.tagBits |= 0x100000000000000L;
                        if (this.environment.usesNullTypeAnnotations() && methodBinding.returnType != null && !methodBinding.returnType.hasNullTypeAnnotations()) {
                            methodBinding.returnType = this.environment.createAnnotatedType(methodBinding.returnType, new AnnotationBinding[]{this.environment.getNonNullAnnotation()});
                        }
                    } else if (typeBit == 64) {
                        methodBinding.tagBits |= 0x80000000000000L;
                        if (this.environment.usesNullTypeAnnotations() && methodBinding.returnType != null && !methodBinding.returnType.hasNullTypeAnnotations()) {
                            methodBinding.returnType = this.environment.createAnnotatedType(methodBinding.returnType, new AnnotationBinding[]{this.environment.getNullableAnnotation()});
                        }
                    }
                }
                ++i;
            }
            methodBinding.defaultNullness = methodDefaultNullness;
        }
        TypeBinding[] parameters = methodBinding.parameters;
        int numVisibleParams = parameters.length;
        int n = numParamAnnotations = externalAnnotationWalker instanceof ExternalAnnotationProvider.IMethodAnnotationWalker ? ((ExternalAnnotationProvider.IMethodAnnotationWalker)externalAnnotationWalker).getParameterCount() : method.getAnnotatedParametersCount();
        if (numParamAnnotations > 0) {
            int j = 0;
            while (j < numVisibleParams) {
                if (numParamAnnotations > 0) {
                    IBinaryAnnotation[] paramAnnotations;
                    int startIndex = numParamAnnotations - numVisibleParams;
                    ITypeAnnotationWalker parameterWalker = externalAnnotationWalker.toMethodParameter((short)(j + startIndex));
                    IBinaryAnnotation[] iBinaryAnnotationArray2 = paramAnnotations = parameterWalker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER ? parameterWalker.getAnnotationsAtCursor(parameters[j].id, false) : method.getParameterAnnotations(j + startIndex, this.fileName);
                    if (paramAnnotations != null) {
                        int i = 0;
                        while (i < paramAnnotations.length) {
                            char[] annotationTypeName = paramAnnotations[i].getTypeName();
                            if (annotationTypeName[0] == 'L') {
                                int typeBit = this.environment.getNullAnnotationBit(BinaryTypeBinding.signature2qualifiedTypeName(annotationTypeName));
                                if (typeBit == 32) {
                                    if (methodBinding.parameterNonNullness == null) {
                                        methodBinding.parameterNonNullness = new Boolean[numVisibleParams];
                                    }
                                    methodBinding.parameterNonNullness[j] = Boolean.TRUE;
                                    if (!this.environment.usesNullTypeAnnotations() || methodBinding.parameters[j] == null || methodBinding.parameters[j].hasNullTypeAnnotations()) break;
                                    methodBinding.parameters[j] = this.environment.createAnnotatedType(methodBinding.parameters[j], new AnnotationBinding[]{this.environment.getNonNullAnnotation()});
                                    break;
                                }
                                if (typeBit == 64) {
                                    if (methodBinding.parameterNonNullness == null) {
                                        methodBinding.parameterNonNullness = new Boolean[numVisibleParams];
                                    }
                                    methodBinding.parameterNonNullness[j] = Boolean.FALSE;
                                    if (!this.environment.usesNullTypeAnnotations() || methodBinding.parameters[j] == null || methodBinding.parameters[j].hasNullTypeAnnotations()) break;
                                    methodBinding.parameters[j] = this.environment.createAnnotatedType(methodBinding.parameters[j], new AnnotationBinding[]{this.environment.getNullableAnnotation()});
                                    break;
                                }
                            }
                            ++i;
                        }
                    }
                }
                ++j;
            }
        }
        if (useNullTypeAnnotations && this.externalAnnotationStatus.isPotentiallyUnannotatedLib()) {
            if (methodBinding.returnType.hasNullTypeAnnotations() || (methodBinding.tagBits & 0x180000000000000L) != 0L || methodBinding.parameterNonNullness != null) {
                this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
            } else {
                TypeBinding[] typeBindingArray = parameters;
                int n2 = parameters.length;
                int n3 = 0;
                while (n3 < n2) {
                    TypeBinding parameter = typeBindingArray[n3];
                    if (parameter.hasNullTypeAnnotations()) {
                        this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
                        break;
                    }
                    ++n3;
                }
            }
        }
    }

    private void scanTypeForNullDefaultAnnotation(IBinaryType binaryType, PackageBinding packageBinding) {
        ReferenceBinding packageInfo;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        char[][] nonNullByDefaultAnnotationName = this.environment.getNonNullByDefaultAnnotationName();
        if (nonNullByDefaultAnnotationName == null) {
            return;
        }
        if (CharOperation.equals(CharOperation.splitOn('/', binaryType.getName()), nonNullByDefaultAnnotationName)) {
            return;
        }
        String[] stringArray = this.environment.globalOptions.nonNullByDefaultAnnotationSecondaryNames;
        int n = this.environment.globalOptions.nonNullByDefaultAnnotationSecondaryNames.length;
        int n2 = 0;
        while (n2 < n) {
            String name = stringArray[n2];
            if (CharOperation.toString(this.compoundName).equals(name)) {
                return;
            }
            ++n2;
        }
        IBinaryAnnotation[] annotations = binaryType.getAnnotations();
        boolean isPackageInfo = CharOperation.equals(this.sourceName(), TypeConstants.PACKAGE_INFO_NAME);
        if (annotations != null) {
            int nullness = 0;
            int length = annotations.length;
            int i = 0;
            while (i < length) {
                int typeBit;
                char[] annotationTypeName = annotations[i].getTypeName();
                if (annotationTypeName[0] == 'L' && (typeBit = this.environment.getNullAnnotationBit(BinaryTypeBinding.signature2qualifiedTypeName(annotationTypeName))) == 128) {
                    nullness |= BinaryTypeBinding.getNonNullByDefaultValue(annotations[i], this.environment);
                }
                ++i;
            }
            this.defaultNullness = nullness;
            if (nullness != 0) {
                if (isPackageInfo) {
                    packageBinding.setDefaultNullness(nullness);
                }
                return;
            }
        }
        if (isPackageInfo) {
            packageBinding.setDefaultNullness(0);
            return;
        }
        ReferenceBinding enclosingTypeBinding = this.enclosingType;
        if (enclosingTypeBinding != null && this.setNullDefault(enclosingTypeBinding.getNullDefault())) {
            return;
        }
        if (packageBinding.getDefaultNullness() == 0 && !isPackageInfo && (this.typeBits & 0xE0) == 0 && (packageInfo = packageBinding.getType(TypeConstants.PACKAGE_INFO_NAME, packageBinding.enclosingModule)) == null) {
            packageBinding.setDefaultNullness(0);
        }
        this.setNullDefault(packageBinding.getDefaultNullness());
    }

    boolean setNullDefault(int newNullDefault) {
        this.defaultNullness = newNullDefault;
        return newNullDefault != 0;
    }

    static int getNonNullByDefaultValue(IBinaryAnnotation annotation, LookupEnvironment environment) {
        char[] annotationTypeName = annotation.getTypeName();
        char[][] typeName = BinaryTypeBinding.signature2qualifiedTypeName(annotationTypeName);
        IBinaryElementValuePair[] elementValuePairs = annotation.getElementValuePairs();
        if (elementValuePairs == null || elementValuePairs.length == 0) {
            int nullness;
            ReferenceBinding annotationType = environment.getType(typeName, environment.UnNamedModule);
            if (annotationType == null) {
                return 0;
            }
            if (annotationType.isUnresolvedType()) {
                annotationType = ((UnresolvedReferenceBinding)annotationType).resolve(environment, false);
            }
            if ((nullness = BinaryTypeBinding.evaluateTypeQualifierDefault(annotationType)) != 0) {
                return nullness;
            }
            MethodBinding[] annotationMethods = annotationType.methods();
            if (annotationMethods != null && annotationMethods.length == 1) {
                Object value = annotationMethods[0].getDefaultValue();
                return Annotation.nullLocationBitsFromAnnotationValue(value);
            }
            return 56;
        }
        if (elementValuePairs.length > 0) {
            int nullness = 0;
            int i = 0;
            while (i < elementValuePairs.length) {
                nullness |= Annotation.nullLocationBitsFromAnnotationValue(elementValuePairs[i].getValue());
                ++i;
            }
            return nullness;
        }
        return 2;
    }

    public static int evaluateTypeQualifierDefault(ReferenceBinding annotationType) {
        AnnotationBinding[] annotationBindingArray = annotationType.getAnnotations();
        int n = annotationBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            ElementValuePair[] pairs2;
            AnnotationBinding annotationOnAnnotation = annotationBindingArray[n2];
            if (CharOperation.equals(annotationOnAnnotation.getAnnotationType().compoundName[annotationOnAnnotation.type.compoundName.length - 1], TYPE_QUALIFIER_DEFAULT) && (pairs2 = annotationOnAnnotation.getElementValuePairs()) != null) {
                ElementValuePair[] elementValuePairArray = pairs2;
                int n3 = pairs2.length;
                int n4 = 0;
                while (n4 < n3) {
                    ElementValuePair elementValuePair = elementValuePairArray[n4];
                    char[] name = elementValuePair.getName();
                    if (CharOperation.equals(name, TypeConstants.VALUE)) {
                        int nullness = 0;
                        Object value = elementValuePair.getValue();
                        if (value instanceof Object[]) {
                            Object[] values;
                            Object[] objectArray = values = (Object[])value;
                            int n5 = values.length;
                            int n6 = 0;
                            while (n6 < n5) {
                                Object value1 = objectArray[n6];
                                nullness |= Annotation.nullLocationBitsFromElementTypeAnnotationValue(value1);
                                ++n6;
                            }
                        } else {
                            nullness |= Annotation.nullLocationBitsFromElementTypeAnnotationValue(value);
                        }
                        return nullness;
                    }
                    ++n4;
                }
            }
            ++n2;
        }
        return 0;
    }

    static char[][] signature2qualifiedTypeName(char[] typeSignature) {
        return CharOperation.splitOn('/', typeSignature, 1, typeSignature.length - 1);
    }

    @Override
    int getNullDefault() {
        return this.defaultNullness;
    }

    private void scanTypeForContainerAnnotation(IBinaryType binaryType, char[][][] missingTypeNames) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        IBinaryAnnotation[] annotations = binaryType.getAnnotations();
        if (annotations != null) {
            int length = annotations.length;
            int i = 0;
            while (i < length) {
                char[] annotationTypeName = annotations[i].getTypeName();
                if (CharOperation.equals(annotationTypeName, ConstantPool.JAVA_LANG_ANNOTATION_REPEATABLE)) {
                    Object value;
                    IBinaryElementValuePair[] elementValuePairs = annotations[i].getElementValuePairs();
                    if (elementValuePairs == null || elementValuePairs.length != 1 || !((value = elementValuePairs[0].getValue()) instanceof ClassSignature)) break;
                    this.containerAnnotationType = (ReferenceBinding)this.environment.getTypeFromSignature(((ClassSignature)value).getTypeName(), 0, -1, false, null, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER);
                    break;
                }
                ++i;
            }
        }
    }

    @Override
    public ReferenceBinding superclass() {
        if (!this.isPrototype()) {
            this.superclass = this.prototype.superclass();
            return this.superclass;
        }
        if ((this.tagBits & 0x2000000L) == 0L) {
            return this.superclass;
        }
        this.superclass = (ReferenceBinding)BinaryTypeBinding.resolveType(this.superclass, this.environment, true);
        this.tagBits &= 0xFFFFFFFFFDFFFFFFL;
        if (this.superclass.problemId() == 1) {
            this.tagBits |= 0x20000L;
        } else {
            boolean wasToleratingMissingTypeProcessingAnnotations = this.environment.mayTolerateMissingType;
            this.environment.mayTolerateMissingType = true;
            try {
                this.superclass.superclass();
                this.superclass.superInterfaces();
            }
            finally {
                this.environment.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
            }
        }
        this.typeBits |= this.superclass.typeBits & 0x713;
        if ((this.typeBits & 3) != 0) {
            this.typeBits |= this.applyCloseableClassWhitelists(this.environment.globalOptions);
        }
        this.detectCircularHierarchy();
        return this.superclass;
    }

    private void breakLoop() {
        ReferenceBinding currentSuper = this.superclass;
        ReferenceBinding prevSuper = null;
        while (currentSuper != null) {
            if ((currentSuper.tagBits & 0x200L) != 0L && prevSuper instanceof BinaryTypeBinding) {
                ((BinaryTypeBinding)prevSuper).superclass = this.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
                break;
            }
            currentSuper.tagBits |= 0x200L;
            prevSuper = currentSuper;
            currentSuper = currentSuper.superclass();
        }
    }

    private void detectCircularHierarchy() {
        ReferenceBinding currentSuper = this.superclass;
        ReferenceBinding tempSuper = null;
        int count = 0;
        int skipCount = 20;
        while (currentSuper != null) {
            if (currentSuper.hasHierarchyCheckStarted()) break;
            if (TypeBinding.equalsEquals(currentSuper, this) || TypeBinding.equalsEquals(currentSuper, tempSuper)) {
                currentSuper.tagBits |= 0x20000L;
                if (currentSuper.isBinaryBinding()) {
                    this.breakLoop();
                }
                return;
            }
            if (count == skipCount) {
                tempSuper = currentSuper;
                skipCount *= 2;
                count = 0;
            }
            if (!currentSuper.isHierarchyConnected()) {
                return;
            }
            currentSuper = currentSuper.superclass();
            ++count;
        }
        tempSuper = this;
        while (TypeBinding.notEquals(currentSuper, tempSuper)) {
            ((ReferenceBinding)tempSuper).setHierarchyCheckDone();
            tempSuper = ((ReferenceBinding)tempSuper).superclass();
        }
    }

    @Override
    public ReferenceBinding[] superInterfaces() {
        if (!this.isPrototype()) {
            this.superInterfaces = this.prototype.superInterfaces();
            return this.superInterfaces;
        }
        if ((this.tagBits & 0x4000000L) == 0L) {
            return this.superInterfaces;
        }
        int i = this.superInterfaces.length;
        while (--i >= 0) {
            this.superInterfaces[i] = (ReferenceBinding)BinaryTypeBinding.resolveType(this.superInterfaces[i], this.environment, true);
            if (this.superInterfaces[i].problemId() == 1) {
                this.tagBits |= 0x20000L;
            } else {
                boolean wasToleratingMissingTypeProcessingAnnotations = this.environment.mayTolerateMissingType;
                this.environment.mayTolerateMissingType = true;
                try {
                    ReferenceBinding superType;
                    this.superInterfaces[i].superclass();
                    if (this.superInterfaces[i].isParameterizedType() && TypeBinding.equalsEquals(superType = this.superInterfaces[i].actualType(), this)) {
                        this.tagBits |= 0x20000L;
                        continue;
                    }
                    this.superInterfaces[i].superInterfaces();
                }
                finally {
                    this.environment.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
                }
            }
            this.typeBits |= this.superInterfaces[i].typeBits & 0x713;
            if ((this.typeBits & 3) == 0) continue;
            this.typeBits |= this.applyCloseableInterfaceWhitelists();
        }
        this.tagBits &= 0xFFFFFFFFFBFFFFFFL;
        return this.superInterfaces;
    }

    @Override
    public ReferenceBinding[] permittedTypes() {
        if (!this.isPrototype()) {
            this.permittedSubtypes = this.prototype.permittedTypes();
            return this.permittedSubtypes;
        }
        int i = this.permittedSubtypes.length;
        while (--i >= 0) {
            this.permittedSubtypes[i] = (ReferenceBinding)BinaryTypeBinding.resolveType(this.permittedSubtypes[i], this.environment, false);
        }
        return this.permittedSubtypes;
    }

    @Override
    public TypeVariableBinding[] typeVariables() {
        if (!this.isPrototype()) {
            this.typeVariables = this.prototype.typeVariables();
            return this.typeVariables;
        }
        if ((this.tagBits & 0x1000000L) == 0L) {
            return this.typeVariables;
        }
        int i = this.typeVariables.length;
        while (--i >= 0) {
            this.typeVariables[i].resolve();
        }
        this.tagBits &= 0xFFFFFFFFFEFFFFFFL;
        return this.typeVariables;
    }

    public String toString() {
        int length;
        int i;
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        StringBuffer buffer = new StringBuffer();
        if (this.isDeprecated()) {
            buffer.append("deprecated ");
        }
        if (this.isPublic()) {
            buffer.append("public ");
        }
        if (this.isProtected()) {
            buffer.append("protected ");
        }
        if (this.isPrivate()) {
            buffer.append("private ");
        }
        if (this.isAbstract() && this.isClass()) {
            buffer.append("abstract ");
        }
        if (this.isStatic() && this.isNestedType()) {
            buffer.append("static ");
        }
        if (this.isFinal()) {
            buffer.append("final ");
        }
        if (this.isRecord()) {
            buffer.append("record ");
        } else if (this.isEnum()) {
            buffer.append("enum ");
        } else if (this.isAnnotationType()) {
            buffer.append("@interface ");
        } else if (this.isClass()) {
            buffer.append("class ");
        } else {
            buffer.append("interface ");
        }
        buffer.append(this.compoundName != null ? CharOperation.toString(this.compoundName) : "UNNAMED TYPE");
        if (this.typeVariables == null) {
            buffer.append("<NULL TYPE VARIABLES>");
        } else if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            buffer.append("<");
            i = 0;
            length = this.typeVariables.length;
            while (i < length) {
                if (i > 0) {
                    buffer.append(", ");
                }
                if (this.typeVariables[i] == null) {
                    buffer.append("NULL TYPE VARIABLE");
                } else {
                    char[] varChars = this.typeVariables[i].toString().toCharArray();
                    buffer.append(varChars, 1, varChars.length - 2);
                }
                ++i;
            }
            buffer.append(">");
        }
        buffer.append("\n\textends ");
        buffer.append(this.superclass != null ? this.superclass.debugName() : "NULL TYPE");
        if (this.superInterfaces != null) {
            if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                buffer.append("\n\timplements : ");
                i = 0;
                length = this.superInterfaces.length;
                while (i < length) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    buffer.append(this.superInterfaces[i] != null ? this.superInterfaces[i].debugName() : "NULL TYPE");
                    ++i;
                }
            }
        } else {
            buffer.append("NULL SUPERINTERFACES");
        }
        if (this.permittedSubtypes != null) {
            if (this.permittedSubtypes != Binding.NO_PERMITTEDTYPES) {
                buffer.append("\n\tpermits : ");
                i = 0;
                length = this.permittedSubtypes.length;
                while (i < length) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    buffer.append(this.permittedSubtypes[i] != null ? this.permittedSubtypes[i].debugName() : "NULL TYPE");
                    ++i;
                }
            }
        } else {
            buffer.append("NULL PERMITTEDSUBTYPES");
        }
        if (this.enclosingType != null) {
            buffer.append("\n\tenclosing type : ");
            buffer.append(this.enclosingType.debugName());
        }
        if (this.fields != null) {
            if (this.fields != Binding.NO_FIELDS) {
                buffer.append("\n/*   fields   */");
                i = 0;
                length = this.fields.length;
                while (i < length) {
                    buffer.append(this.fields[i] != null ? "\n" + this.fields[i].toString() : "\nNULL FIELD");
                    ++i;
                }
            }
        } else {
            buffer.append("NULL FIELDS");
        }
        if (this.methods != null) {
            if (this.methods != Binding.NO_METHODS) {
                buffer.append("\n/*   methods   */");
                i = 0;
                length = this.methods.length;
                while (i < length) {
                    buffer.append(this.methods[i] != null ? "\n" + this.methods[i].toString() : "\nNULL METHOD");
                    ++i;
                }
            }
        } else {
            buffer.append("NULL METHODS");
        }
        if (this.memberTypes != null) {
            if (this.memberTypes != Binding.NO_MEMBER_TYPES) {
                buffer.append("\n/*   members   */");
                i = 0;
                length = this.memberTypes.length;
                while (i < length) {
                    buffer.append(this.memberTypes[i] != null ? "\n" + this.memberTypes[i].toString() : "\nNULL TYPE");
                    ++i;
                }
            }
        } else {
            buffer.append("NULL MEMBER TYPES");
        }
        buffer.append("\n\n\n");
        return buffer.toString();
    }

    @Override
    public TypeBinding unannotated() {
        return this.prototype;
    }

    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        if (newAnnotations.length > 0) {
            return this.environment.createAnnotatedType((TypeBinding)this.prototype, newAnnotations);
        }
        return this.prototype;
    }

    @Override
    MethodBinding[] unResolvedMethods() {
        if (!this.isPrototype()) {
            return this.prototype.unResolvedMethods();
        }
        return this.methods;
    }

    @Override
    public FieldBinding[] unResolvedFields() {
        if (!this.isPrototype()) {
            return this.prototype.unResolvedFields();
        }
        return this.fields;
    }

    @Override
    public ModuleBinding module() {
        if (!this.isPrototype()) {
            return this.prototype.module;
        }
        return this.module;
    }

    public static enum ExternalAnnotationStatus {
        FROM_SOURCE,
        NOT_EEA_CONFIGURED,
        NO_EEA_FILE,
        TYPE_IS_ANNOTATED;


        public boolean isPotentiallyUnannotatedLib() {
            switch (this) {
                case FROM_SOURCE: 
                case TYPE_IS_ANNOTATED: {
                    return false;
                }
            }
            return true;
        }
    }
}

