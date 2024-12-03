/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.stream.Stream;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class SyntheticMethodBinding
extends MethodBinding {
    public FieldBinding targetReadField;
    public FieldBinding targetWriteField;
    public MethodBinding targetMethod;
    public TypeBinding targetEnumType;
    public LambdaExpression lambda;
    public RecordComponentBinding recordComponentBinding;
    public SwitchStatement switchStatement;
    public ReferenceExpression serializableMethodRef;
    public int purpose;
    public int startIndex;
    public int endIndex;
    public static final int FieldReadAccess = 1;
    public static final int FieldWriteAccess = 2;
    public static final int SuperFieldReadAccess = 3;
    public static final int SuperFieldWriteAccess = 4;
    public static final int MethodAccess = 5;
    public static final int ConstructorAccess = 6;
    public static final int SuperMethodAccess = 7;
    public static final int BridgeMethod = 8;
    public static final int EnumValues = 9;
    public static final int EnumValueOf = 10;
    public static final int SwitchTable = 11;
    public static final int TooManyEnumsConstants = 12;
    public static final int LambdaMethod = 13;
    public static final int ArrayConstructor = 14;
    public static final int ArrayClone = 15;
    public static final int FactoryMethod = 16;
    public static final int DeserializeLambda = 17;
    public static final int SerializableMethodReference = 18;
    public static final int RecordOverrideToString = 19;
    public static final int RecordOverrideHashCode = 20;
    public static final int RecordOverrideEquals = 21;
    public static final int RecordCanonicalConstructor = 22;
    public int sourceStart = 0;
    public int index;
    public int fakePaddedParameters = 0;

    public SyntheticMethodBinding(FieldBinding targetField, boolean isReadAccess, boolean isSuperAccess, ReferenceBinding declaringClass) {
        boolean needRename;
        int methodId;
        this.modifiers = 4104;
        this.tagBits |= 0x600000000L;
        SourceTypeBinding declaringSourceType = (SourceTypeBinding)declaringClass;
        SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
        this.selector = CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray());
        if (isReadAccess) {
            this.returnType = targetField.type;
            if (targetField.isStatic()) {
                this.parameters = Binding.NO_PARAMETERS;
            } else {
                this.parameters = new TypeBinding[1];
                this.parameters[0] = declaringSourceType;
            }
            this.targetReadField = targetField;
            this.purpose = isSuperAccess ? 3 : 1;
        } else {
            this.returnType = TypeBinding.VOID;
            if (targetField.isStatic()) {
                this.parameters = new TypeBinding[1];
                this.parameters[0] = targetField.type;
            } else {
                this.parameters = new TypeBinding[2];
                this.parameters[0] = declaringSourceType;
                this.parameters[1] = targetField.type;
            }
            this.targetWriteField = targetField;
            this.purpose = isSuperAccess ? 4 : 2;
        }
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.declaringClass = declaringSourceType;
        do {
            block19: {
                needRename = false;
                MethodBinding[] methods = declaringSourceType.methods();
                long range = ReferenceBinding.binarySearch(this.selector, methods);
                if (range >= 0L) {
                    int paramCount = this.parameters.length;
                    int imethod = (int)range;
                    int end = (int)(range >> 32);
                    while (imethod <= end) {
                        block18: {
                            MethodBinding method = methods[imethod];
                            if (method.parameters.length == paramCount) {
                                TypeBinding[] toMatch = method.parameters;
                                int i = 0;
                                while (i < paramCount) {
                                    if (!TypeBinding.notEquals(toMatch[i], this.parameters[i])) {
                                        ++i;
                                        continue;
                                    }
                                    break block18;
                                }
                                needRename = true;
                                break block19;
                            }
                        }
                        ++imethod;
                    }
                }
                if (knownAccessMethods != null) {
                    int i = 0;
                    int length = knownAccessMethods.length;
                    while (i < length) {
                        if (knownAccessMethods[i] != null && CharOperation.equals(this.selector, knownAccessMethods[i].selector) && this.areParametersEqual(methods[i])) {
                            needRename = true;
                            break;
                        }
                        ++i;
                    }
                }
            }
            if (!needRename) continue;
            this.setSelector(CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(++methodId).toCharArray()));
        } while (needRename);
        FieldDeclaration[] fieldDecls = declaringSourceType.scope.referenceContext.fields;
        if (fieldDecls != null) {
            int i = 0;
            int max = fieldDecls.length;
            while (i < max) {
                if (fieldDecls[i].binding == targetField) {
                    this.sourceStart = fieldDecls[i].sourceStart;
                    return;
                }
                ++i;
            }
        }
        this.sourceStart = declaringSourceType.scope.referenceContext.sourceStart;
    }

    public SyntheticMethodBinding(FieldBinding targetField, ReferenceBinding declaringClass, TypeBinding enumBinding, char[] selector, SwitchStatement switchStatement) {
        boolean needRename;
        int methodId;
        this.modifiers = (declaringClass.isInterface() ? 1 : 0) | 8 | 0x1000;
        this.tagBits |= 0x600000000L;
        SourceTypeBinding declaringSourceType = (SourceTypeBinding)declaringClass;
        SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
        this.selector = selector;
        this.returnType = declaringSourceType.scope.createArrayType(TypeBinding.INT, 1);
        this.parameters = Binding.NO_PARAMETERS;
        this.targetReadField = targetField;
        this.targetEnumType = enumBinding;
        this.purpose = 11;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.declaringClass = declaringSourceType;
        this.switchStatement = switchStatement;
        if (declaringSourceType.isStrictfp()) {
            this.modifiers |= 0x800;
        }
        do {
            block11: {
                needRename = false;
                MethodBinding[] methods = declaringSourceType.methods();
                long range = ReferenceBinding.binarySearch(this.selector, methods);
                if (range >= 0L) {
                    int paramCount = this.parameters.length;
                    int imethod = (int)range;
                    int end = (int)(range >> 32);
                    while (imethod <= end) {
                        block10: {
                            MethodBinding method = methods[imethod];
                            if (method.parameters.length == paramCount) {
                                TypeBinding[] toMatch = method.parameters;
                                int i = 0;
                                while (i < paramCount) {
                                    if (!TypeBinding.notEquals(toMatch[i], this.parameters[i])) {
                                        ++i;
                                        continue;
                                    }
                                    break block10;
                                }
                                needRename = true;
                                break block11;
                            }
                        }
                        ++imethod;
                    }
                }
                if (knownAccessMethods != null) {
                    int i = 0;
                    int length = knownAccessMethods.length;
                    while (i < length) {
                        if (knownAccessMethods[i] != null && CharOperation.equals(this.selector, knownAccessMethods[i].selector) && this.areParametersEqual(methods[i])) {
                            needRename = true;
                            break;
                        }
                        ++i;
                    }
                }
            }
            if (!needRename) continue;
            this.setSelector(CharOperation.concat(selector, String.valueOf(++methodId).toCharArray()));
        } while (needRename);
        this.sourceStart = declaringSourceType.scope.referenceContext.sourceStart;
    }

    public SyntheticMethodBinding(MethodBinding targetMethod, boolean isSuperAccess, ReferenceBinding declaringClass) {
        if (targetMethod.isConstructor()) {
            this.initializeConstructorAccessor(targetMethod);
        } else {
            this.initializeMethodAccessor(targetMethod, isSuperAccess, declaringClass);
        }
    }

    public SyntheticMethodBinding(MethodBinding overridenMethodToBridge, MethodBinding targetMethod, SourceTypeBinding declaringClass) {
        int methodId;
        this.declaringClass = declaringClass;
        this.selector = overridenMethodToBridge.selector;
        this.modifiers = (targetMethod.modifiers | 0x40 | 0x1000) & 0xBFFFFACF;
        this.tagBits |= 0x600000000L;
        this.returnType = overridenMethodToBridge.returnType;
        this.parameters = overridenMethodToBridge.parameters;
        this.thrownExceptions = overridenMethodToBridge.thrownExceptions;
        this.targetMethod = targetMethod;
        this.purpose = 8;
        SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
    }

    public SyntheticMethodBinding(SourceTypeBinding declaringEnum, char[] selector) {
        int methodId;
        this.declaringClass = declaringEnum;
        this.selector = selector;
        this.modifiers = 9;
        this.tagBits |= 0x600000000L;
        LookupEnvironment environment = declaringEnum.scope.environment();
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        if (selector == TypeConstants.VALUES) {
            this.returnType = environment.createArrayType(environment.convertToParameterizedType(declaringEnum), 1);
            this.parameters = Binding.NO_PARAMETERS;
            this.purpose = 9;
        } else if (selector == TypeConstants.VALUEOF) {
            this.returnType = environment.convertToParameterizedType(declaringEnum);
            this.parameters = new TypeBinding[]{declaringEnum.scope.getJavaLangString()};
            this.purpose = 10;
        }
        SyntheticMethodBinding[] knownAccessMethods = ((SourceTypeBinding)this.declaringClass).syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
        if (declaringEnum.isStrictfp()) {
            this.modifiers |= 0x800;
        }
    }

    public SyntheticMethodBinding(SourceTypeBinding declaringClass) {
        int methodId;
        this.declaringClass = declaringClass;
        this.selector = TypeConstants.DESERIALIZE_LAMBDA;
        this.modifiers = 4106;
        this.tagBits |= 0x600000000L;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.returnType = declaringClass.scope.getJavaLangObject();
        this.parameters = new TypeBinding[]{declaringClass.scope.getJavaLangInvokeSerializedLambda()};
        this.purpose = 17;
        SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
    }

    public SyntheticMethodBinding(SourceTypeBinding declaringEnum, int startIndex, int endIndex) {
        this.declaringClass = declaringEnum;
        SyntheticMethodBinding[] knownAccessMethods = declaringEnum.syntheticMethods();
        this.index = knownAccessMethods == null ? 0 : knownAccessMethods.length;
        StringBuffer buffer = new StringBuffer();
        buffer.append(TypeConstants.SYNTHETIC_ENUM_CONSTANT_INITIALIZATION_METHOD_PREFIX).append(this.index);
        this.selector = String.valueOf(buffer).toCharArray();
        this.modifiers = 10;
        this.tagBits |= 0x600000000L;
        this.purpose = 12;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.returnType = TypeBinding.VOID;
        this.parameters = Binding.NO_PARAMETERS;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public SyntheticMethodBinding(MethodBinding overridenMethodToBridge, SourceTypeBinding declaringClass) {
        int methodId;
        this.declaringClass = declaringClass;
        this.selector = overridenMethodToBridge.selector;
        this.modifiers = (overridenMethodToBridge.modifiers | 0x40 | 0x1000) & 0xBFFFFACF;
        this.tagBits |= 0x600000000L;
        this.returnType = overridenMethodToBridge.returnType;
        this.parameters = overridenMethodToBridge.parameters;
        this.thrownExceptions = overridenMethodToBridge.thrownExceptions;
        this.targetMethod = overridenMethodToBridge;
        this.purpose = 7;
        SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
    }

    public SyntheticMethodBinding(int purpose, ArrayBinding arrayType, char[] selector, SourceTypeBinding declaringClass) {
        int methodId;
        this.declaringClass = declaringClass;
        this.selector = selector;
        this.modifiers = 4106;
        this.tagBits |= 0x600000000L;
        this.returnType = arrayType;
        LookupEnvironment environment = declaringClass.environment;
        if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            if (environment.usesNullTypeAnnotations()) {
                this.returnType = environment.createAnnotatedType(this.returnType, new AnnotationBinding[]{environment.getNonNullAnnotation()});
            } else {
                this.tagBits |= 0x100000000000000L;
            }
        }
        this.parameters = new TypeBinding[]{purpose == 14 ? TypeBinding.INT : arrayType};
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.purpose = purpose;
        SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
    }

    public SyntheticMethodBinding(LambdaExpression lambda, char[] lambdaName, SourceTypeBinding declaringClass) {
        int methodId;
        this.lambda = lambda;
        this.declaringClass = declaringClass;
        this.selector = lambdaName;
        this.modifiers = lambda.binding.modifiers;
        this.tagBits |= 0x600000000L | lambda.binding.tagBits & 0x400L;
        this.returnType = lambda.binding.returnType;
        this.parameters = lambda.binding.parameters;
        TypeVariableBinding[] vars = (TypeVariableBinding[])Stream.of(this.parameters).filter(param -> param.isTypeVariable()).toArray(TypeVariableBinding[]::new);
        if (vars != null && vars.length > 0) {
            this.typeVariables = vars;
        }
        this.thrownExceptions = lambda.binding.thrownExceptions;
        this.purpose = 13;
        SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
    }

    public SyntheticMethodBinding(ReferenceExpression ref, SourceTypeBinding declaringClass) {
        int methodId;
        this.serializableMethodRef = ref;
        this.declaringClass = declaringClass;
        this.selector = ref.binding.selector;
        this.modifiers = ref.binding.modifiers;
        this.tagBits |= 0x600000000L | ref.binding.tagBits & 0x400L;
        this.returnType = ref.binding.returnType;
        this.parameters = ref.binding.parameters;
        this.thrownExceptions = ref.binding.thrownExceptions;
        this.purpose = 18;
        SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
    }

    public SyntheticMethodBinding(MethodBinding privateConstructor, MethodBinding publicConstructor, char[] selector, TypeBinding[] enclosingInstances, SourceTypeBinding declaringClass) {
        int methodId;
        this.declaringClass = declaringClass;
        this.selector = selector;
        this.modifiers = 4106;
        this.tagBits |= 0x600000000L;
        this.returnType = publicConstructor.declaringClass;
        int realParametersLength = privateConstructor.parameters.length;
        int enclosingInstancesLength = enclosingInstances.length;
        int parametersLength = enclosingInstancesLength + realParametersLength;
        this.parameters = new TypeBinding[parametersLength];
        System.arraycopy(enclosingInstances, 0, this.parameters, 0, enclosingInstancesLength);
        System.arraycopy(privateConstructor.parameters, 0, this.parameters, enclosingInstancesLength, realParametersLength);
        this.fakePaddedParameters = publicConstructor.parameters.length - realParametersLength;
        this.thrownExceptions = publicConstructor.thrownExceptions;
        this.purpose = 16;
        this.targetMethod = publicConstructor;
        SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
    }

    public SyntheticMethodBinding(ReferenceBinding declaringClass, RecordComponentBinding[] rcb) {
        int methodId;
        SourceTypeBinding declaringSourceType = (SourceTypeBinding)declaringClass;
        assert (declaringSourceType.isRecord());
        this.declaringClass = declaringSourceType;
        this.modifiers = declaringClass.modifiers & 7;
        if (this.declaringClass.isStrictfp()) {
            this.modifiers |= 0x800;
        }
        this.tagBits |= 0x600000000L;
        this.tagBits |= 0x1800L;
        this.parameters = rcb.length == 0 ? Binding.NO_PARAMETERS : new TypeBinding[rcb.length];
        int i = 0;
        while (i < rcb.length) {
            this.parameters[i] = TypeBinding.VOID;
            ++i;
        }
        this.selector = TypeConstants.INIT;
        this.returnType = TypeBinding.VOID;
        this.purpose = 22;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.declaringClass = declaringSourceType;
        this.tagBits |= 0x800L;
        SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
    }

    public SyntheticMethodBinding(ReferenceBinding declaringClass, RecordComponentBinding rcb, int index) {
        int methodId;
        SourceTypeBinding declaringSourceType = (SourceTypeBinding)declaringClass;
        assert (declaringSourceType.isRecord());
        this.declaringClass = declaringSourceType;
        this.modifiers = 1;
        if (this.declaringClass.isStrictfp()) {
            this.modifiers |= 0x800;
        }
        this.tagBits |= 0x600000000L;
        this.parameters = Binding.NO_PARAMETERS;
        this.selector = rcb.name;
        this.recordComponentBinding = rcb;
        this.purpose = 1;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        this.declaringClass = declaringSourceType;
        SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
        this.sourceStart = rcb.sourceRecordComponent().sourceStart;
    }

    public SyntheticMethodBinding(ReferenceBinding declaringClass, char[] selector, int index) {
        int methodId;
        SourceTypeBinding declaringSourceType = (SourceTypeBinding)declaringClass;
        assert (declaringSourceType.isRecord());
        this.declaringClass = declaringSourceType;
        this.modifiers = 17;
        if (this.declaringClass.isStrictfp()) {
            this.modifiers |= 0x800;
        }
        this.tagBits |= 0x600000000L;
        this.selector = selector;
        this.thrownExceptions = Binding.NO_EXCEPTIONS;
        if (selector == TypeConstants.TOSTRING) {
            this.returnType = declaringSourceType.scope.getJavaLangString();
            this.parameters = Binding.NO_PARAMETERS;
            this.purpose = 19;
        } else if (selector == TypeConstants.HASHCODE) {
            this.returnType = TypeBinding.INT;
            this.parameters = Binding.NO_PARAMETERS;
            this.purpose = 20;
        } else if (selector == TypeConstants.EQUALS) {
            this.returnType = TypeBinding.BOOLEAN;
            this.parameters = new TypeBinding[]{declaringSourceType.scope.getJavaLangObject()};
            this.purpose = 21;
        }
        SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
    }

    public void initializeConstructorAccessor(MethodBinding accessedConstructor) {
        int length;
        int i;
        boolean needRename;
        this.targetMethod = accessedConstructor;
        this.modifiers = 4096;
        this.tagBits |= 0x600000000L;
        SourceTypeBinding sourceType = (SourceTypeBinding)accessedConstructor.declaringClass;
        SyntheticMethodBinding[] knownSyntheticMethods = sourceType.syntheticMethods();
        this.index = knownSyntheticMethods == null ? 0 : knownSyntheticMethods.length;
        this.selector = accessedConstructor.selector;
        this.returnType = accessedConstructor.returnType;
        this.purpose = 6;
        int parametersLength = accessedConstructor.parameters.length;
        this.parameters = new TypeBinding[parametersLength + 1];
        System.arraycopy(accessedConstructor.parameters, 0, this.parameters, 0, parametersLength);
        this.parameters[parametersLength] = accessedConstructor.declaringClass;
        this.thrownExceptions = accessedConstructor.thrownExceptions;
        this.declaringClass = sourceType;
        do {
            block9: {
                needRename = false;
                MethodBinding[] methods = sourceType.methods();
                i = 0;
                length = methods.length;
                while (i < length) {
                    if (CharOperation.equals(this.selector, methods[i].selector) && this.areParameterErasuresEqual(methods[i])) {
                        needRename = true;
                        break block9;
                    }
                    ++i;
                }
                if (knownSyntheticMethods != null) {
                    i = 0;
                    length = knownSyntheticMethods.length;
                    while (i < length) {
                        if (knownSyntheticMethods[i] != null && CharOperation.equals(this.selector, knownSyntheticMethods[i].selector) && this.areParameterErasuresEqual(knownSyntheticMethods[i])) {
                            needRename = true;
                            break;
                        }
                        ++i;
                    }
                }
            }
            if (!needRename) continue;
            int length2 = this.parameters.length;
            this.parameters = new TypeBinding[length2 + 1];
            System.arraycopy(this.parameters, 0, this.parameters, 0, length2);
            this.parameters[length2] = this.declaringClass;
        } while (needRename);
        AbstractMethodDeclaration[] methodDecls = sourceType.scope.referenceContext.methods;
        if (methodDecls != null) {
            i = 0;
            length = methodDecls.length;
            while (i < length) {
                if (methodDecls[i].binding == accessedConstructor) {
                    this.sourceStart = methodDecls[i].sourceStart;
                    return;
                }
                ++i;
            }
        }
    }

    public void initializeMethodAccessor(MethodBinding accessedMethod, boolean isSuperAccess, ReferenceBinding receiverType) {
        int length;
        int i;
        boolean needRename;
        int methodId;
        this.targetMethod = accessedMethod;
        this.modifiers = isSuperAccess && receiverType.isInterface() && !accessedMethod.isStatic() ? 4098 : (receiverType.isInterface() ? 4105 : 4104);
        this.tagBits |= 0x600000000L;
        SourceTypeBinding declaringSourceType = (SourceTypeBinding)receiverType;
        SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
        this.index = methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
        this.selector = CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray());
        this.returnType = accessedMethod.returnType;
        int n = this.purpose = isSuperAccess ? 7 : 5;
        if (accessedMethod.isStatic() || isSuperAccess && receiverType.isInterface()) {
            this.parameters = accessedMethod.parameters;
        } else {
            this.parameters = new TypeBinding[accessedMethod.parameters.length + 1];
            this.parameters[0] = declaringSourceType;
            System.arraycopy(accessedMethod.parameters, 0, this.parameters, 1, accessedMethod.parameters.length);
        }
        this.thrownExceptions = accessedMethod.thrownExceptions;
        this.declaringClass = declaringSourceType;
        do {
            block11: {
                needRename = false;
                MethodBinding[] methods = declaringSourceType.methods();
                i = 0;
                length = methods.length;
                while (i < length) {
                    if (CharOperation.equals(this.selector, methods[i].selector) && this.areParameterErasuresEqual(methods[i])) {
                        needRename = true;
                        break block11;
                    }
                    ++i;
                }
                if (knownAccessMethods != null) {
                    i = 0;
                    length = knownAccessMethods.length;
                    while (i < length) {
                        if (knownAccessMethods[i] != null && CharOperation.equals(this.selector, knownAccessMethods[i].selector) && this.areParameterErasuresEqual(knownAccessMethods[i])) {
                            needRename = true;
                            break;
                        }
                        ++i;
                    }
                }
            }
            if (!needRename) continue;
            this.setSelector(CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(++methodId).toCharArray()));
        } while (needRename);
        AbstractMethodDeclaration[] methodDecls = declaringSourceType.scope.referenceContext.methods;
        if (methodDecls != null) {
            i = 0;
            length = methodDecls.length;
            while (i < length) {
                if (methodDecls[i].binding == accessedMethod) {
                    this.sourceStart = methodDecls[i].sourceStart;
                    return;
                }
                ++i;
            }
        }
    }

    protected boolean isConstructorRelated() {
        return this.purpose == 6;
    }

    @Override
    public LambdaExpression sourceLambda() {
        return this.lambda;
    }

    public void markNonNull(LookupEnvironment environment) {
        SyntheticMethodBinding.markNonNull(this, this.purpose, environment);
    }

    static void markNonNull(MethodBinding method, int purpose, LookupEnvironment environment) {
        switch (purpose) {
            case 9: {
                if (environment.usesNullTypeAnnotations()) {
                    TypeBinding elementType = ((ArrayBinding)method.returnType).leafComponentType();
                    AnnotationBinding nonNullAnnotation = environment.getNonNullAnnotation();
                    elementType = environment.createAnnotatedType(elementType, new AnnotationBinding[]{environment.getNonNullAnnotation()});
                    AnnotationBinding[] annotationBindingArray = new AnnotationBinding[2];
                    annotationBindingArray[0] = nonNullAnnotation;
                    method.returnType = environment.createArrayType(elementType, 1, annotationBindingArray);
                } else {
                    method.tagBits |= 0x100000000000000L;
                }
                return;
            }
            case 10: {
                if (environment.usesNullTypeAnnotations()) {
                    method.returnType = environment.createAnnotatedType(method.returnType, new AnnotationBinding[]{environment.getNonNullAnnotation()});
                } else {
                    method.tagBits |= 0x100000000000000L;
                }
                return;
            }
        }
    }

    @Override
    public void setAnnotations(AnnotationBinding[] annotations, Scope scope, boolean forceStore) {
        if (this.declaringClass.isRecord() && !this.isVarargs()) {
            AnnotationBinding[] annotationBindingArray = annotations;
            int n = annotations.length;
            int n2 = 0;
            while (n2 < n) {
                AnnotationBinding annot = annotationBindingArray[n2];
                if (annot.getAnnotationType().id == 60) {
                    scope.problemReporter().safeVarargsOnOnSyntheticRecordAccessor(this.recordComponentBinding.sourceRecordComponent());
                }
                ++n2;
            }
        }
        this.setAnnotations(annotations, forceStore);
    }
}

