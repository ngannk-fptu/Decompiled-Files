/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationProvider;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationHolder;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.ExternalAnnotationSuperimposer;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.util.Util;

public class SourceTypeBinding
extends ReferenceBinding {
    public ReferenceBinding superclass;
    public ReferenceBinding[] superInterfaces;
    private FieldBinding[] fields;
    private MethodBinding[] methods;
    public ReferenceBinding[] memberTypes;
    public TypeVariableBinding[] typeVariables;
    public ReferenceBinding[] permittedTypes;
    public ClassScope scope;
    protected SourceTypeBinding prototype;
    LookupEnvironment environment;
    public ModuleBinding module;
    private static final int METHOD_EMUL = 0;
    private static final int FIELD_EMUL = 1;
    private static final int CLASS_LITERAL_EMUL = 2;
    private static final int MAX_SYNTHETICS = 3;
    HashMap[] synthetics;
    char[] genericReferenceTypeSignature;
    private SimpleLookupTable storedAnnotations = null;
    public int defaultNullness;
    boolean memberTypesSorted = false;
    private int nullnessDefaultInitialized = 0;
    private ReferenceBinding containerAnnotationType = null;
    public ExternalAnnotationProvider externalAnnotationProvider;
    private SourceTypeBinding nestHost;
    public HashSet<SourceTypeBinding> nestMembers;
    private boolean isRecordDeclaration = false;
    private RecordComponentBinding[] components;
    public boolean isVarArgs = false;
    private FieldBinding[] implicitComponentFields;
    private MethodBinding[] recordComponentAccessors = null;

    public SourceTypeBinding(char[][] compoundName, PackageBinding fPackage, ClassScope scope) {
        this.compoundName = compoundName;
        this.fPackage = fPackage;
        this.fileName = scope.referenceCompilationUnit().getFileName();
        this.modifiers = scope.referenceContext.modifiers;
        this.sourceName = scope.referenceContext.name;
        this.scope = scope;
        this.environment = scope.environment();
        this.components = Binding.UNINITIALIZED_COMPONENTS;
        this.fields = Binding.UNINITIALIZED_FIELDS;
        this.methods = Binding.UNINITIALIZED_METHODS;
        this.prototype = this;
        this.isRecordDeclaration = scope.referenceContext.isRecord();
        this.computeId();
    }

    public SourceTypeBinding(SourceTypeBinding prototype) {
        super(prototype);
        this.prototype = prototype.prototype;
        this.prototype.tagBits |= 0x800000L;
        this.tagBits &= 0xFFFFFFFFFF7FFFFFL;
        this.superclass = prototype.superclass;
        this.superInterfaces = prototype.superInterfaces;
        this.fields = prototype.fields;
        this.methods = prototype.methods;
        this.memberTypes = prototype.memberTypes;
        this.typeVariables = prototype.typeVariables;
        this.environment = prototype.environment;
        this.scope = prototype.scope;
        this.synthetics = prototype.synthetics;
        this.genericReferenceTypeSignature = prototype.genericReferenceTypeSignature;
        this.storedAnnotations = prototype.storedAnnotations;
        this.defaultNullness = prototype.defaultNullness;
        this.nullnessDefaultInitialized = prototype.nullnessDefaultInitialized;
        this.containerAnnotationType = prototype.containerAnnotationType;
        this.tagBits |= 0x10000000L;
        this.isRecordDeclaration = this.prototype.isRecordDeclaration;
    }

    private void addDefaultAbstractMethods() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if ((this.tagBits & 0x400L) != 0L) {
            return;
        }
        this.tagBits |= 0x400L;
        if (this.isClass() && this.isAbstract()) {
            if (this.scope.compilerOptions().targetJDK >= 0x2E0000L) {
                return;
            }
            ReferenceBinding[] itsInterfaces = this.superInterfaces();
            if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
                MethodBinding[] defaultAbstracts = null;
                int defaultAbstractsCount = 0;
                ReferenceBinding[] interfacesToVisit = itsInterfaces;
                int nextPosition = interfacesToVisit.length;
                int i = 0;
                while (i < nextPosition) {
                    ReferenceBinding superType = interfacesToVisit[i];
                    if (superType.isValidBinding()) {
                        MethodBinding[] superMethods = superType.methods();
                        int m = superMethods.length;
                        block1: while (--m >= 0) {
                            MethodBinding method = superMethods[m];
                            if (this.implementsMethod(method)) continue;
                            if (defaultAbstractsCount == 0) {
                                defaultAbstracts = new MethodBinding[5];
                            } else {
                                int k = 0;
                                while (k < defaultAbstractsCount) {
                                    MethodBinding alreadyAdded = defaultAbstracts[k];
                                    if (CharOperation.equals(alreadyAdded.selector, method.selector) && alreadyAdded.areParametersEqual(method)) continue block1;
                                    ++k;
                                }
                            }
                            MethodBinding defaultAbstract = new MethodBinding(method.modifiers | 0x80000 | 0x1000, method.selector, method.returnType, method.parameters, method.thrownExceptions, this);
                            if (defaultAbstractsCount == defaultAbstracts.length) {
                                MethodBinding[] methodBindingArray = defaultAbstracts;
                                defaultAbstracts = new MethodBinding[2 * defaultAbstractsCount];
                                System.arraycopy(methodBindingArray, 0, defaultAbstracts, 0, defaultAbstractsCount);
                            }
                            defaultAbstracts[defaultAbstractsCount++] = defaultAbstract;
                        }
                        itsInterfaces = superType.superInterfaces();
                        if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
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
                        }
                    }
                    ++i;
                }
                if (defaultAbstractsCount > 0) {
                    int length = this.methods.length;
                    System.arraycopy(this.methods, 0, this.setMethods(new MethodBinding[length + defaultAbstractsCount]), 0, length);
                    System.arraycopy(defaultAbstracts, 0, this.methods, length, defaultAbstractsCount);
                    if ((length += defaultAbstractsCount) > 1) {
                        ReferenceBinding.sortMethods(this.methods, 0, length);
                    }
                }
            }
        }
    }

    public FieldBinding addSyntheticFieldForInnerclass(LocalVariableBinding actualOuterLocalVariable) {
        boolean needRecheck;
        FieldBinding synthField;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        if ((synthField = (FieldBinding)this.synthetics[1].get(actualOuterLocalVariable)) == null) {
            synthField = new SyntheticFieldBinding(CharOperation.concat(TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX, actualOuterLocalVariable.name), actualOuterLocalVariable.type, 4114, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put(actualOuterLocalVariable, synthField);
        }
        int index = 1;
        block0: do {
            needRecheck = false;
            FieldBinding existingField = this.getField(synthField.name, true);
            if (existingField == null) continue;
            TypeDeclaration typeDecl = this.scope.referenceContext;
            FieldDeclaration[] fieldDeclarations = typeDecl.fields;
            int max = fieldDeclarations == null ? 0 : fieldDeclarations.length;
            int i = 0;
            while (i < max) {
                FieldDeclaration fieldDecl = fieldDeclarations[i];
                if (fieldDecl.binding == existingField) {
                    synthField.name = CharOperation.concat(TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX, actualOuterLocalVariable.name, ("$" + String.valueOf(index++)).toCharArray());
                    needRecheck = true;
                    continue block0;
                }
                ++i;
            }
        } while (needRecheck);
        return synthField;
    }

    public FieldBinding addSyntheticFieldForInnerclass(ReferenceBinding enclosingType) {
        boolean needRecheck;
        FieldBinding synthField;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        if ((synthField = (FieldBinding)this.synthetics[1].get(enclosingType)) == null) {
            synthField = new SyntheticFieldBinding(CharOperation.concat(TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, String.valueOf(enclosingType.depth()).toCharArray()), enclosingType, 4112, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put(enclosingType, synthField);
        }
        block0: do {
            needRecheck = false;
            FieldBinding existingField = this.getField(synthField.name, true);
            if (existingField == null) continue;
            TypeDeclaration typeDecl = this.scope.referenceContext;
            FieldDeclaration[] fieldDeclarations = typeDecl.fields;
            int max = fieldDeclarations == null ? 0 : fieldDeclarations.length;
            int i = 0;
            while (i < max) {
                FieldDeclaration fieldDecl = fieldDeclarations[i];
                if (fieldDecl.binding == existingField) {
                    if (this.scope.compilerOptions().complianceLevel >= 0x310000L) {
                        synthField.name = CharOperation.concat(synthField.name, "$".toCharArray());
                        needRecheck = true;
                        continue block0;
                    }
                    this.scope.problemReporter().duplicateFieldInType(this, fieldDecl);
                    continue block0;
                }
                ++i;
            }
        } while (needRecheck);
        return synthField;
    }

    public FieldBinding addSyntheticFieldForClassLiteral(TypeBinding targetType, BlockScope blockScope) {
        FieldBinding existingField;
        FieldBinding synthField;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[2] == null) {
            this.synthetics[2] = new HashMap(5);
        }
        if ((synthField = (FieldBinding)this.synthetics[2].get(targetType)) == null) {
            synthField = new SyntheticFieldBinding(CharOperation.concat(TypeConstants.SYNTHETIC_CLASS, String.valueOf(this.synthetics[2].size()).toCharArray()), blockScope.getJavaLangClass(), 4104, this, Constant.NotAConstant, this.synthetics[2].size());
            this.synthetics[2].put(targetType, synthField);
        }
        if ((existingField = this.getField(synthField.name, true)) != null) {
            TypeDeclaration typeDecl = blockScope.referenceType();
            FieldDeclaration[] typeDeclarationFields = typeDecl.fields;
            int max = typeDeclarationFields == null ? 0 : typeDeclarationFields.length;
            int i = 0;
            while (i < max) {
                FieldDeclaration fieldDecl = typeDeclarationFields[i];
                if (fieldDecl.binding == existingField) {
                    blockScope.problemReporter().duplicateFieldInType(this, fieldDecl);
                    break;
                }
                ++i;
            }
        }
        return synthField;
    }

    public FieldBinding addSyntheticFieldForAssert(BlockScope blockScope) {
        boolean needRecheck;
        FieldBinding synthField;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        if ((synthField = (FieldBinding)this.synthetics[1].get("assertionEmulation")) == null) {
            synthField = new SyntheticFieldBinding(TypeConstants.SYNTHETIC_ASSERT_DISABLED, TypeBinding.BOOLEAN, (this.isInterface() ? 1 : 0) | 8 | 0x1000 | 0x10, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put("assertionEmulation", synthField);
        }
        int index = 0;
        block0: do {
            needRecheck = false;
            FieldBinding existingField = this.getField(synthField.name, true);
            if (existingField == null) continue;
            TypeDeclaration typeDecl = this.scope.referenceContext;
            int max = typeDecl.fields == null ? 0 : typeDecl.fields.length;
            int i = 0;
            while (i < max) {
                FieldDeclaration fieldDecl = typeDecl.fields[i];
                if (fieldDecl.binding == existingField) {
                    synthField.name = CharOperation.concat(TypeConstants.SYNTHETIC_ASSERT_DISABLED, ("_" + String.valueOf(index++)).toCharArray());
                    needRecheck = true;
                    continue block0;
                }
                ++i;
            }
        } while (needRecheck);
        return synthField;
    }

    public FieldBinding addSyntheticFieldForEnumValues() {
        boolean needRecheck;
        FieldBinding synthField;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        if ((synthField = (FieldBinding)this.synthetics[1].get("enumConstantValues")) == null) {
            synthField = new SyntheticFieldBinding(TypeConstants.SYNTHETIC_ENUM_VALUES, this.scope.createArrayType(this, 1), 4122, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put("enumConstantValues", synthField);
        }
        int index = 0;
        block0: do {
            needRecheck = false;
            FieldBinding existingField = this.getField(synthField.name, true);
            if (existingField == null) continue;
            TypeDeclaration typeDecl = this.scope.referenceContext;
            FieldDeclaration[] fieldDeclarations = typeDecl.fields;
            int max = fieldDeclarations == null ? 0 : fieldDeclarations.length;
            int i = 0;
            while (i < max) {
                FieldDeclaration fieldDecl = fieldDeclarations[i];
                if (fieldDecl.binding == existingField) {
                    synthField.name = CharOperation.concat(TypeConstants.SYNTHETIC_ENUM_VALUES, ("_" + String.valueOf(index++)).toCharArray());
                    needRecheck = true;
                    continue block0;
                }
                ++i;
            }
        } while (needRecheck);
        return synthField;
    }

    public SyntheticMethodBinding addSyntheticMethod(FieldBinding targetField, boolean isReadAccess, boolean isSuperAccess) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(targetField);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(targetField, isReadAccess, isSuperAccess, (ReferenceBinding)this);
            accessors = new SyntheticMethodBinding[2];
            this.synthetics[0].put(targetField, accessors);
            accessors[isReadAccess ? 0 : 1] = accessMethod;
        } else {
            accessMethod = accessors[isReadAccess ? 0 : 1];
            if (accessMethod == null) {
                accessMethod = new SyntheticMethodBinding(targetField, isReadAccess, isSuperAccess, (ReferenceBinding)this);
                accessors[isReadAccess ? 0 : 1] = accessMethod;
            }
        }
        return accessMethod;
    }

    public SyntheticMethodBinding addSyntheticEnumMethod(char[] selector) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(selector);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(this, selector);
            accessors = new SyntheticMethodBinding[2];
            this.synthetics[0].put(selector, accessors);
            accessors[0] = accessMethod;
        } else {
            accessMethod = accessors[0];
            if (accessMethod == null) {
                accessors[0] = accessMethod = new SyntheticMethodBinding(this, selector);
            }
        }
        return accessMethod;
    }

    public SyntheticFieldBinding addSyntheticFieldForSwitchEnum(char[] fieldName, String key) {
        boolean needRecheck;
        SyntheticFieldBinding synthField;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[1] == null) {
            this.synthetics[1] = new HashMap(5);
        }
        if ((synthField = (SyntheticFieldBinding)this.synthetics[1].get(key)) == null) {
            synthField = new SyntheticFieldBinding(fieldName, this.scope.createArrayType(TypeBinding.INT, 1), (this.isInterface() ? 17 : 66) | 8 | 0x1000, this, Constant.NotAConstant, this.synthetics[1].size());
            this.synthetics[1].put(key, synthField);
        }
        int index = 0;
        block0: do {
            needRecheck = false;
            FieldBinding existingField = this.getField(synthField.name, true);
            if (existingField == null) continue;
            TypeDeclaration typeDecl = this.scope.referenceContext;
            FieldDeclaration[] fieldDeclarations = typeDecl.fields;
            int max = fieldDeclarations == null ? 0 : fieldDeclarations.length;
            int i = 0;
            while (i < max) {
                FieldDeclaration fieldDecl = fieldDeclarations[i];
                if (fieldDecl.binding == existingField) {
                    synthField.name = CharOperation.concat(fieldName, ("_" + String.valueOf(index++)).toCharArray());
                    needRecheck = true;
                    continue block0;
                }
                ++i;
            }
        } while (needRecheck);
        return synthField;
    }

    public SyntheticMethodBinding addSyntheticMethodForSwitchEnum(TypeBinding enumBinding, SwitchStatement switchStatement) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = null;
        char[] selector = CharOperation.concat(TypeConstants.SYNTHETIC_SWITCH_ENUM_TABLE, enumBinding.constantPoolName());
        CharOperation.replace(selector, '/', '$');
        String key = new String(selector);
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(key);
        if (accessors == null) {
            SyntheticFieldBinding fieldBinding = this.addSyntheticFieldForSwitchEnum(selector, key);
            accessMethod = new SyntheticMethodBinding(fieldBinding, this, enumBinding, selector, switchStatement);
            accessors = new SyntheticMethodBinding[2];
            this.synthetics[0].put(key, accessors);
            accessors[0] = accessMethod;
        } else {
            accessMethod = accessors[0];
            if (accessMethod == null) {
                SyntheticFieldBinding fieldBinding = this.addSyntheticFieldForSwitchEnum(selector, key);
                accessors[0] = accessMethod = new SyntheticMethodBinding(fieldBinding, this, enumBinding, selector, switchStatement);
            }
        }
        return accessMethod;
    }

    public SyntheticMethodBinding addSyntheticMethodForEnumInitialization(int begin, int end) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = new SyntheticMethodBinding(this, begin, end);
        SyntheticMethodBinding[] accessors = new SyntheticMethodBinding[2];
        this.synthetics[0].put(accessMethod.selector, accessors);
        accessors[0] = accessMethod;
        return accessMethod;
    }

    public SyntheticMethodBinding addSyntheticMethod(LambdaExpression lambda) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding lambdaMethod = null;
        SyntheticMethodBinding[] lambdaMethods = (SyntheticMethodBinding[])this.synthetics[0].get(lambda);
        if (lambdaMethods == null) {
            lambdaMethod = new SyntheticMethodBinding(lambda, CharOperation.concat(TypeConstants.ANONYMOUS_METHOD, Integer.toString(lambda.ordinal).toCharArray()), this);
            lambdaMethods = new SyntheticMethodBinding[1];
            this.synthetics[0].put(lambda, lambdaMethods);
            lambdaMethods[0] = lambdaMethod;
        } else {
            lambdaMethod = lambdaMethods[0];
        }
        if (lambda.isSerializable) {
            this.addDeserializeLambdaMethod();
        }
        return lambdaMethod;
    }

    public SyntheticMethodBinding addSyntheticMethod(ReferenceExpression ref) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (!ref.isSerializable) {
            return null;
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding lambdaMethod = null;
        SyntheticMethodBinding[] lambdaMethods = (SyntheticMethodBinding[])this.synthetics[0].get(ref);
        if (lambdaMethods == null) {
            lambdaMethod = new SyntheticMethodBinding(ref, this);
            lambdaMethods = new SyntheticMethodBinding[1];
            this.synthetics[0].put(ref, lambdaMethods);
            lambdaMethods[0] = lambdaMethod;
        } else {
            lambdaMethod = lambdaMethods[0];
        }
        this.addDeserializeLambdaMethod();
        return lambdaMethod;
    }

    private void addDeserializeLambdaMethod() {
        SyntheticMethodBinding[] deserializeLambdaMethods = (SyntheticMethodBinding[])this.synthetics[0].get(TypeConstants.DESERIALIZE_LAMBDA);
        if (deserializeLambdaMethods == null) {
            SyntheticMethodBinding deserializeLambdaMethod = new SyntheticMethodBinding(this);
            deserializeLambdaMethods = new SyntheticMethodBinding[1];
            this.synthetics[0].put(TypeConstants.DESERIALIZE_LAMBDA, deserializeLambdaMethods);
            deserializeLambdaMethods[0] = deserializeLambdaMethod;
        }
    }

    public SyntheticMethodBinding addSyntheticMethod(MethodBinding targetMethod, boolean isSuperAccess) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(targetMethod);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(targetMethod, isSuperAccess, (ReferenceBinding)this);
            accessors = new SyntheticMethodBinding[2];
            this.synthetics[0].put(targetMethod, accessors);
            accessors[isSuperAccess ? 0 : 1] = accessMethod;
        } else {
            accessMethod = accessors[isSuperAccess ? 0 : 1];
            if (accessMethod == null) {
                accessMethod = new SyntheticMethodBinding(targetMethod, isSuperAccess, (ReferenceBinding)this);
                accessors[isSuperAccess ? 0 : 1] = accessMethod;
            }
        }
        if (targetMethod.declaringClass.isStatic()) {
            if (targetMethod.isConstructor() && targetMethod.parameters.length >= 254 || targetMethod.parameters.length >= 255) {
                this.scope.problemReporter().tooManyParametersForSyntheticMethod(targetMethod.sourceMethod());
            }
        } else if (targetMethod.isConstructor() && targetMethod.parameters.length >= 253 || targetMethod.parameters.length >= 254) {
            this.scope.problemReporter().tooManyParametersForSyntheticMethod(targetMethod.sourceMethod());
        }
        return accessMethod;
    }

    public SyntheticMethodBinding addSyntheticArrayMethod(ArrayBinding arrayType, int purpose, char[] selector) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding arrayMethod = null;
        SyntheticMethodBinding[] arrayMethods = (SyntheticMethodBinding[])this.synthetics[0].get(arrayType);
        if (arrayMethods == null) {
            arrayMethod = new SyntheticMethodBinding(purpose, arrayType, selector, this);
            arrayMethods = new SyntheticMethodBinding[2];
            this.synthetics[0].put(arrayType, arrayMethods);
            arrayMethods[purpose == 14 ? 0 : 1] = arrayMethod;
        } else {
            arrayMethod = arrayMethods[purpose == 14 ? 0 : 1];
            if (arrayMethod == null) {
                arrayMethod = new SyntheticMethodBinding(purpose, arrayType, selector, this);
                arrayMethods[purpose == 14 ? 0 : 1] = arrayMethod;
            }
        }
        return arrayMethod;
    }

    public SyntheticMethodBinding addSyntheticFactoryMethod(MethodBinding privateConstructor, MethodBinding publicConstructor, TypeBinding[] enclosingInstances, char[] selector) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding factory = new SyntheticMethodBinding(privateConstructor, publicConstructor, selector, enclosingInstances, this);
        this.synthetics[0].put(selector, new SyntheticMethodBinding[]{factory});
        return factory;
    }

    public SyntheticMethodBinding addSyntheticBridgeMethod(MethodBinding inheritedMethodToBridge, MethodBinding targetMethod) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.isInterface() && this.scope.compilerOptions().sourceLevel <= 0x330000L) {
            return null;
        }
        if (TypeBinding.equalsEquals(inheritedMethodToBridge.returnType.erasure(), targetMethod.returnType.erasure()) && inheritedMethodToBridge.areParameterErasuresEqual(targetMethod)) {
            return null;
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        } else {
            for (Object synthetic : this.synthetics[0].keySet()) {
                if (!(synthetic instanceof MethodBinding)) continue;
                MethodBinding method = (MethodBinding)synthetic;
                if (!CharOperation.equals(inheritedMethodToBridge.selector, method.selector) || !TypeBinding.equalsEquals(inheritedMethodToBridge.returnType.erasure(), method.returnType.erasure()) || !inheritedMethodToBridge.areParameterErasuresEqual(method)) continue;
                return null;
            }
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(inheritedMethodToBridge);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, targetMethod, this);
            accessors = new SyntheticMethodBinding[2];
            this.synthetics[0].put(inheritedMethodToBridge, accessors);
            accessors[1] = accessMethod;
        } else {
            accessMethod = accessors[1];
            if (accessMethod == null) {
                accessors[1] = accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, targetMethod, this);
            }
        }
        return accessMethod;
    }

    public SyntheticMethodBinding addSyntheticBridgeMethod(MethodBinding inheritedMethodToBridge) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.scope.compilerOptions().complianceLevel <= 0x310000L) {
            return null;
        }
        if (this.isInterface() && !inheritedMethodToBridge.isDefaultMethod()) {
            return null;
        }
        if (inheritedMethodToBridge.isAbstract() || inheritedMethodToBridge.isFinal() || inheritedMethodToBridge.isStatic()) {
            return null;
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        } else {
            for (Object synthetic : this.synthetics[0].keySet()) {
                if (!(synthetic instanceof MethodBinding)) continue;
                MethodBinding method = (MethodBinding)synthetic;
                if (!CharOperation.equals(inheritedMethodToBridge.selector, method.selector) || !TypeBinding.equalsEquals(inheritedMethodToBridge.returnType.erasure(), method.returnType.erasure()) || !inheritedMethodToBridge.areParameterErasuresEqual(method)) continue;
                return null;
            }
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(inheritedMethodToBridge);
        if (accessors == null) {
            accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, this);
            accessors = new SyntheticMethodBinding[2];
            this.synthetics[0].put(inheritedMethodToBridge, accessors);
            accessors[1] = accessMethod;
        } else {
            accessMethod = accessors[1];
            if (accessMethod == null) {
                accessors[1] = accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, this);
            }
        }
        return accessMethod;
    }

    public MethodBinding[] checkAndAddSyntheticRecordMethods(MethodBinding[] methodBindings, int count) {
        if (!this.isRecordDeclaration) {
            return methodBindings;
        }
        List<MethodBinding> implicitMethods = this.checkAndAddSyntheticRecordComponentAccessors(methodBindings);
        implicitMethods = this.checkAndAddSyntheticRecordOverrideMethods(methodBindings, implicitMethods);
        int i = 0;
        while (i < count) {
            implicitMethods.add(methodBindings[i]);
            ++i;
        }
        return implicitMethods.toArray(new MethodBinding[0]);
    }

    public List<MethodBinding> checkAndAddSyntheticRecordOverrideMethods(MethodBinding[] methodBindings, List<MethodBinding> implicitMethods) {
        boolean isEqualsPresent;
        SyntheticMethodBinding m2;
        if (!this.hasMethodWithNumArgs(TypeConstants.TOSTRING, 0)) {
            m2 = this.addSyntheticRecordOverrideMethod(TypeConstants.TOSTRING, implicitMethods.size());
            implicitMethods.add(m2);
        }
        if (!this.hasMethodWithNumArgs(TypeConstants.HASHCODE, 0)) {
            m2 = this.addSyntheticRecordOverrideMethod(TypeConstants.HASHCODE, implicitMethods.size());
            implicitMethods.add(m2);
        }
        if (!(isEqualsPresent = Arrays.stream(methodBindings).filter(m -> CharOperation.equals(TypeConstants.EQUALS, m.selector)).anyMatch(m -> m.parameters != null && m.parameters.length == 1 && m.parameters[0].equals(this.scope.getJavaLangObject())))) {
            SyntheticMethodBinding m3 = this.addSyntheticRecordOverrideMethod(TypeConstants.EQUALS, implicitMethods.size());
            implicitMethods.add(m3);
        }
        if (this.isRecordDeclaration && this.getImplicitCanonicalConstructor() == -1) {
            MethodBinding explicitCanon = null;
            MethodBinding[] methodBindingArray = methodBindings;
            int n = methodBindings.length;
            int n2 = 0;
            while (n2 < n) {
                MethodBinding m4 = methodBindingArray[n2];
                if (m4.isCompactConstructor() || (m4.tagBits & 0x800L) != 0L) {
                    explicitCanon = m4;
                    break;
                }
                ++n2;
            }
            if (explicitCanon == null) {
                implicitMethods.add(this.addSyntheticRecordCanonicalConstructor());
            }
        }
        return implicitMethods;
    }

    public List<MethodBinding> checkAndAddSyntheticRecordComponentAccessors(MethodBinding[] methodBindings) {
        ArrayList<MethodBinding> implicitMethods = new ArrayList<MethodBinding>(0);
        if (this.fields == null) {
            return implicitMethods;
        }
        List filteredComponents = Arrays.stream(this.fields).filter(f -> f.isRecordComponent()).map(f -> new String(f.name)).collect(Collectors.toList());
        List<Object> accessors = new ArrayList<MethodBinding>();
        if (this.methods != null) {
            accessors = Arrays.stream(methodBindings).filter(m -> m.selector != null && m.selector.length > 0).filter(m -> filteredComponents.contains(new String(m.selector))).filter(m -> m.parameterNames == null || m.parameterNames.length == 0).collect(Collectors.toList());
            List candidates = accessors.stream().map(m -> new String(m.selector)).collect(Collectors.toList());
            filteredComponents.removeAll(candidates);
        }
        int missingCount = filteredComponents.size();
        int i = 0;
        while (i < missingCount) {
            RecordComponentBinding rcb = this.getRecordComponent(((String)filteredComponents.get(i)).toCharArray());
            if (rcb != null) {
                implicitMethods.add(this.addSyntheticRecordComponentAccessor(rcb, i));
            }
            ++i;
        }
        accessors.addAll(implicitMethods);
        this.recordComponentAccessors = accessors.toArray(new MethodBinding[0]);
        return implicitMethods;
    }

    public SyntheticMethodBinding addSyntheticRecordCanonicalConstructor() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding canonicalConstructor = new SyntheticMethodBinding((ReferenceBinding)this, this.components);
        SyntheticMethodBinding[] accessors = new SyntheticMethodBinding[2];
        this.synthetics[0].put(TypeConstants.INIT, accessors);
        accessors[0] = canonicalConstructor;
        return canonicalConstructor;
    }

    public void removeSyntheticRecordCanonicalConstructor(SyntheticMethodBinding implicitCanonicalConstructor) {
        if (this.synthetics == null || this.synthetics[0] == null) {
            return;
        }
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(TypeConstants.INIT);
        if (accessors == null || accessors.length < 1) {
            return;
        }
        if (accessors[0] == implicitCanonicalConstructor) {
            this.synthetics[0].remove(TypeConstants.INIT);
        }
    }

    public SyntheticMethodBinding addSyntheticRecordComponentAccessor(RecordComponentBinding rcb, int index) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = new SyntheticMethodBinding((ReferenceBinding)this, rcb, index);
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(rcb.name);
        if (accessors == null) {
            accessors = new SyntheticMethodBinding[2];
            this.synthetics[0].put(rcb.name, accessors);
            accessors[0] = accessMethod;
        } else {
            accessMethod = accessors[0];
            if (accessMethod == null) {
                accessors[0] = accessMethod;
            }
        }
        return accessMethod;
    }

    public SyntheticMethodBinding addSyntheticRecordOverrideMethod(char[] selector, int index) {
        if (this.synthetics == null) {
            this.synthetics = new HashMap[3];
        }
        if (this.synthetics[0] == null) {
            this.synthetics[0] = new HashMap(5);
        }
        SyntheticMethodBinding accessMethod = null;
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(selector);
        accessMethod = new SyntheticMethodBinding((ReferenceBinding)this, selector, index);
        if (accessors == null) {
            accessors = new SyntheticMethodBinding[2];
            this.synthetics[0].put(selector, accessors);
            accessors[0] = accessMethod;
        } else {
            accessMethod = accessors[0];
            if (accessMethod == null) {
                accessors[0] = accessMethod;
            }
        }
        return accessMethod;
    }

    private void removeSyntheticRecordOverrideMethod(MethodBinding smb) {
        if (this.synthetics == null) {
            return;
        }
        HashMap syntheticMethods = this.synthetics[0];
        if (syntheticMethods == null) {
            return;
        }
        syntheticMethods.remove(smb.selector);
    }

    boolean areComponentsInitialized() {
        if (!this.isPrototype()) {
            return this.prototype.areComponentsInitialized();
        }
        return this.components != Binding.UNINITIALIZED_COMPONENTS;
    }

    boolean areFieldsInitialized() {
        if (!this.isPrototype()) {
            return this.prototype.areFieldsInitialized();
        }
        return this.fields != Binding.UNINITIALIZED_FIELDS;
    }

    boolean areMethodsInitialized() {
        if (!this.isPrototype()) {
            return this.prototype.areMethodsInitialized();
        }
        return this.methods != Binding.UNINITIALIZED_METHODS;
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
    public TypeBinding clone(TypeBinding immaterial) {
        return new SourceTypeBinding(this);
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        if (!this.isPrototype()) {
            return this.prototype.computeUniqueKey();
        }
        char[] uniqueKey = super.computeUniqueKey(isLeaf);
        if (uniqueKey.length == 2) {
            return uniqueKey;
        }
        if (Util.isClassFileName(this.fileName)) {
            return uniqueKey;
        }
        int end = CharOperation.lastIndexOf('.', this.fileName);
        if (end != -1) {
            char[] topLevelType;
            int start = CharOperation.lastIndexOf('/', this.fileName) + 1;
            char[] mainTypeName = CharOperation.subarray(this.fileName, start, end);
            start = CharOperation.lastIndexOf('/', uniqueKey) + 1;
            if (start == 0) {
                start = 1;
            }
            if ((end = this.isMemberType() ? CharOperation.indexOf('$', uniqueKey, start) : -1) == -1) {
                end = CharOperation.indexOf('<', uniqueKey, start);
            }
            if (end == -1) {
                end = CharOperation.indexOf(';', uniqueKey, start);
            }
            if (!CharOperation.equals(topLevelType = CharOperation.subarray(uniqueKey, start, end), mainTypeName)) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(uniqueKey, 0, start);
                buffer.append(mainTypeName);
                buffer.append('~');
                buffer.append(topLevelType);
                buffer.append(uniqueKey, end, uniqueKey.length - end);
                int length = buffer.length();
                uniqueKey = new char[length];
                buffer.getChars(0, length, uniqueKey, 0);
                return uniqueKey;
            }
        }
        return uniqueKey;
    }

    private void checkAnnotationsInType() {
        this.getAnnotationTagBits();
        ReferenceBinding enclosingType = this.enclosingType();
        if (enclosingType != null && enclosingType.isViewedAsDeprecated() && !this.isDeprecated()) {
            this.modifiers |= 0x200000;
            this.tagBits |= enclosingType.tagBits & 0x4000000000000000L;
        }
        int i = 0;
        int length = this.memberTypes.length;
        while (i < length) {
            ((SourceTypeBinding)this.memberTypes[i]).checkAnnotationsInType();
            ++i;
        }
    }

    void faultInTypesForFieldsAndMethods() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        this.checkPermitsInType();
        this.checkAnnotationsInType();
        this.internalFaultInTypeForFieldsAndMethods();
    }

    private Map.Entry<TypeReference, ReferenceBinding> getFirstSealedSuperTypeOrInterface(TypeDeclaration typeDecl) {
        boolean isAnySuperTypeSealed;
        boolean bl = isAnySuperTypeSealed = typeDecl.superclass != null && this.superclass != null ? this.superclass.isSealed() : false;
        if (isAnySuperTypeSealed) {
            return new AbstractMap.SimpleEntry<TypeReference, ReferenceBinding>(typeDecl.superclass, this.superclass);
        }
        ReferenceBinding[] superInterfaces1 = this.superInterfaces();
        int l = superInterfaces1 != null ? superInterfaces1.length : 0;
        int i = 0;
        while (i < l) {
            ReferenceBinding superInterface = superInterfaces1[i];
            if (superInterface.isSealed()) {
                return new AbstractMap.SimpleEntry<TypeReference, ReferenceBinding>(typeDecl.superInterfaces[i], superInterface);
            }
            ++i;
        }
        return null;
    }

    private void checkPermitsInType() {
        Map.Entry<TypeReference, ReferenceBinding> sealedEntry;
        boolean foundSealedSuperTypeOrInterface;
        int l;
        int i;
        boolean hasPermittedTypes;
        TypeDeclaration typeDecl = this.scope.referenceContext;
        if (this.isInterface() && this.isSealed() && this.isNonSealed()) {
            this.scope.problemReporter().sealedInterfaceIsSealedAndNonSealed(this, typeDecl);
            return;
        }
        boolean bl = hasPermittedTypes = this.permittedTypes != null && this.permittedTypes.length > 0;
        if (hasPermittedTypes) {
            TypeReference permittedTypeRef;
            ModuleBinding sourceModuleBinding;
            boolean isUnnamedModule;
            if (!this.isSealed()) {
                this.scope.problemReporter().sealedMissingSealedModifier(this, typeDecl);
            }
            if (isUnnamedModule = (sourceModuleBinding = this.module()).isUnnamed()) {
                PackageBinding sourceTypePackage = this.getPackage();
                i = 0;
                l = this.permittedTypes.length;
                while (i < l) {
                    ReferenceBinding permType = this.permittedTypes[i];
                    if (permType.isValidBinding() && sourceTypePackage != permType.getPackage()) {
                        permittedTypeRef = typeDecl.permittedTypes[i];
                        this.scope.problemReporter().sealedPermittedTypeOutsideOfPackage(permType, this, permittedTypeRef, sourceTypePackage);
                    }
                    ++i;
                }
            } else {
                int i2 = 0;
                int l2 = this.permittedTypes.length;
                while (i2 < l2) {
                    ModuleBinding permTypeModule;
                    ReferenceBinding permType = this.permittedTypes[i2];
                    if (permType.isValidBinding() && (permTypeModule = permType.module()) != null && sourceModuleBinding != permTypeModule) {
                        permittedTypeRef = typeDecl.permittedTypes[i2];
                        this.scope.problemReporter().sealedPermittedTypeOutsideOfModule(permType, this, permittedTypeRef, sourceModuleBinding);
                    }
                    ++i2;
                }
            }
        }
        boolean bl2 = foundSealedSuperTypeOrInterface = (sealedEntry = this.getFirstSealedSuperTypeOrInterface(typeDecl)) != null;
        if (this.isLocalType()) {
            if (this.isSealed() || this.isNonSealed()) {
                return;
            }
            if (foundSealedSuperTypeOrInterface) {
                this.scope.problemReporter().sealedLocalDirectSuperTypeSealed(this, sealedEntry.getKey(), sealedEntry.getValue());
                return;
            }
        } else if (this.isNonSealed() && !foundSealedSuperTypeOrInterface) {
            if (this.isClass() && !this.isRecord()) {
                this.scope.problemReporter().sealedDisAllowedNonSealedModifierInClass(this, typeDecl);
            } else if (this.isInterface()) {
                this.scope.problemReporter().sealedDisAllowedNonSealedModifierInInterface(this, typeDecl);
            }
        }
        if (foundSealedSuperTypeOrInterface) {
            if (!(this.isFinal() || this.isSealed() || this.isNonSealed())) {
                if (this.isClass()) {
                    this.scope.problemReporter().sealedMissingClassModifier(this, typeDecl, sealedEntry.getValue());
                } else if (this.isInterface()) {
                    this.scope.problemReporter().sealedMissingInterfaceModifier(this, typeDecl, sealedEntry.getValue());
                }
            }
            List<SourceTypeBinding> typesInCU = this.collectAllTypeBindings(typeDecl, this.scope.compilationUnitScope());
            if (!typeDecl.isRecord() && typeDecl.superclass != null && !this.checkPermitsAndAdd(this.superclass, typesInCU)) {
                this.scope.problemReporter().sealedSuperClassDoesNotPermit(this, typeDecl.superclass, this.superclass);
            }
            i = 0;
            l = this.superInterfaces.length;
            while (i < l) {
                ReferenceBinding superInterface = this.superInterfaces[i];
                if (superInterface != null && !this.checkPermitsAndAdd(superInterface, typesInCU)) {
                    TypeReference superInterfaceRef = typeDecl.superInterfaces[i];
                    this.scope.problemReporter().sealedSuperInterfaceDoesNotPermit(this, superInterfaceRef, superInterface);
                }
                ++i;
            }
        }
        int i3 = 0;
        int length = this.memberTypes.length;
        while (i3 < length) {
            ((SourceTypeBinding)this.memberTypes[i3]).checkPermitsInType();
            ++i3;
        }
        if (this.scope.referenceContext.permittedTypes == null) {
            return;
        }
        int l3 = this.permittedTypes.length <= this.scope.referenceContext.permittedTypes.length ? this.permittedTypes.length : this.scope.referenceContext.permittedTypes.length;
        i = 0;
        while (i < l3) {
            TypeReference permittedTypeRef = this.scope.referenceContext.permittedTypes[i];
            ReferenceBinding permittedType = this.permittedTypes[i];
            if (permittedType != null && permittedType.isValidBinding()) {
                if (this.isClass()) {
                    ReferenceBinding permSuperType = permittedType.superclass();
                    if (!TypeBinding.equalsEquals(this, permSuperType = this.getActualType(permSuperType))) {
                        this.scope.problemReporter().sealedNotDirectSuperClass(permittedType, permittedTypeRef, this);
                    }
                } else if (this.isInterface()) {
                    ReferenceBinding[] permSuperInterfaces = permittedType.superInterfaces();
                    boolean foundSuperInterface = false;
                    if (permSuperInterfaces != null) {
                        ReferenceBinding[] referenceBindingArray = permSuperInterfaces;
                        int n = permSuperInterfaces.length;
                        int n2 = 0;
                        while (n2 < n) {
                            ReferenceBinding psi = referenceBindingArray[n2];
                            if (TypeBinding.equalsEquals(this, psi = this.getActualType(psi))) {
                                foundSuperInterface = true;
                                break;
                            }
                            ++n2;
                        }
                        if (!foundSuperInterface) {
                            this.scope.problemReporter().sealedNotDirectSuperInterface(permittedType, permittedTypeRef, this);
                        }
                    }
                }
            }
            ++i;
        }
    }

    private ReferenceBinding getActualType(ReferenceBinding ref) {
        return ref.isParameterizedType() || ref.isRawType() ? ref.actualType() : ref;
    }

    public List<SourceTypeBinding> collectAllTypeBindings(TypeDeclaration typeDecl, CompilationUnitScope unitScope) {
        class TypeBindingsCollector
        extends ASTVisitor {
            List<SourceTypeBinding> types = new ArrayList<SourceTypeBinding>();

            TypeBindingsCollector() {
            }

            @Override
            public boolean visit(TypeDeclaration localTypeDeclaration, BlockScope scope1) {
                this.checkAndAddBinding(localTypeDeclaration.binding);
                return true;
            }

            @Override
            public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope1) {
                this.checkAndAddBinding(memberTypeDeclaration.binding);
                return true;
            }

            @Override
            public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope1) {
                this.checkAndAddBinding(typeDeclaration.binding);
                return true;
            }

            private void checkAndAddBinding(SourceTypeBinding stb) {
                if (stb != null) {
                    this.types.add(stb);
                }
            }
        }
        TypeBindingsCollector typeCollector = new TypeBindingsCollector();
        typeDecl.traverse((ASTVisitor)typeCollector, unitScope);
        return typeCollector.types;
    }

    private boolean checkPermitsAndAdd(ReferenceBinding superType, List<SourceTypeBinding> types) {
        if (superType == null || superType.equals(this.scope.getJavaLangObject()) || !superType.isSealed()) {
            return true;
        }
        if (superType.isSealed()) {
            ReferenceBinding[] superPermittedTypes;
            superType = this.getActualType(superType);
            ReferenceBinding[] referenceBindingArray = superPermittedTypes = superType.permittedTypes();
            int n = superPermittedTypes.length;
            int n2 = 0;
            while (n2 < n) {
                ReferenceBinding permittedType = referenceBindingArray[n2];
                if ((permittedType = this.getActualType(permittedType)).isValidBinding() && TypeBinding.equalsEquals(this, permittedType)) {
                    return true;
                }
                ++n2;
            }
        }
        return false;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public RecordComponentBinding[] components() {
        block23: {
            if (!this.isRecordDeclaration) {
                return null;
            }
            if (!this.isPrototype()) {
                if ((this.extendedTagBits & 1) != 0) {
                    return this.components;
                }
                this.extendedTagBits |= 1;
                this.components = this.prototype.components();
                return this.components;
            }
            if ((this.extendedTagBits & 1) != 0) {
                return this.components;
            }
            if (!this.areComponentsInitialized()) {
                this.scope.buildComponents();
            }
            failed = 0;
            resolvedComponents = this.components;
            try {
                componentsSnapshot = this.components;
                i = 0;
                length = componentsSnapshot.length;
                while (i < length) {
                    if (this.resolveTypeFor(componentsSnapshot[i]) == null) {
                        if (resolvedComponents == componentsSnapshot) {
                            resolvedComponents = new RecordComponentBinding[length];
                            System.arraycopy(componentsSnapshot, 0, resolvedComponents, 0, length);
                        }
                        resolvedComponents[i] = null;
                        ++failed;
                    } else {
                        rcb = resolvedComponents[i];
                        accessor = this.getRecordComponentAccessor(rcb.name);
                        if (accessor instanceof SyntheticMethodBinding) {
                            smb = (SyntheticMethodBinding)accessor;
                            leafType = rcb.type.leafComponentType();
                            if (leafType instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0) {
                                smb.modifiers |= 0x40000000;
                            }
                            smb.returnType = rcb.type.unannotated();
                            var13_13 = this.fields;
                            var12_12 = this.fields.length;
                            var11_11 = 0;
                            while (var11_11 < var12_12) {
                                f = var13_13[var11_11];
                                if (f.isRecordComponent() && CharOperation.equals(f.name, rcb.name)) {
                                    smb.targetReadField = f;
                                    ASTNode.copyRecordComponentAnnotations(this.scope, smb, rcb.sourceRecordComponent().annotations);
                                    break;
                                }
                                ++var11_11;
                            }
                        }
                    }
                    ++i;
                }
            }
            finally {
                if (failed <= 0) break block23;
                newSize = resolvedComponents.length - failed;
                if (newSize == 0) {
                    return this.setComponents(Binding.NO_COMPONENTS);
                }
                newComponents = new RecordComponentBinding[newSize];
                i = 0;
                j = 0;
                length = resolvedComponents.length;
                ** while (i < length)
            }
lbl-1000:
            // 1 sources

            {
                if (resolvedComponents[i] != null) {
                    newComponents[j++] = resolvedComponents[i];
                }
                ++i;
                continue;
            }
lbl64:
            // 1 sources

            this.setComponents(newComponents);
        }
        var18_28 = this.methods;
        var17_24 = this.methods.length;
        var16_22 = 0;
        while (var16_22 < var17_24) {
            method = var18_28[var16_22];
            if (method instanceof SyntheticMethodBinding) {
                smb = (SyntheticMethodBinding)method;
                if (smb.purpose == 22) {
                    i = 0;
                    l = smb.parameters.length;
                    while (i < l) {
                        smb.parameters[i] = this.components[i].type;
                        ++i;
                    }
                }
            }
            ++var16_22;
        }
        this.extendedTagBits |= 1;
        return this.components;
    }

    public RecordComponentBinding resolveTypeFor(RecordComponentBinding component) {
        RecordComponent[] componentDecls;
        if (!this.isPrototype()) {
            return this.prototype.resolveTypeFor(component);
        }
        if ((component.modifiers & 0x2000000) == 0) {
            return component;
        }
        component.getAnnotationTagBits();
        if ((component.getAnnotationTagBits() & 0x400000000000L) != 0L) {
            component.modifiers |= 0x100000;
        }
        if (this.isViewedAsDeprecated() && !component.isDeprecated()) {
            component.modifiers |= 0x200000;
            component.tagBits |= this.tagBits & 0x4000000000000000L;
        }
        if (this.hasRestrictedAccess()) {
            component.modifiers |= 0x40000;
        }
        int length = (componentDecls = this.scope.referenceContext.recordComponents) == null ? 0 : componentDecls.length;
        int f = 0;
        while (f < length) {
            if (componentDecls[f].binding == component) {
                TypeBinding leafType;
                TypeBinding componentType;
                MethodScope initializationScope = this.scope.referenceContext.initializerScope;
                RecordComponent componentDecl = componentDecls[f];
                component.type = componentType = componentDecl.type.resolveType(initializationScope, true);
                component.modifiers &= 0xFDFFFFFF;
                if (componentType == null) {
                    componentDecl.binding = null;
                    return null;
                }
                if (componentType == TypeBinding.VOID) {
                    this.scope.problemReporter().recordComponentCannotBeVoid(componentDecl);
                    componentDecl.binding = null;
                    return null;
                }
                if (componentType.isArrayType() && ((ArrayBinding)componentType).leafComponentType == TypeBinding.VOID) {
                    this.scope.problemReporter().variableTypeCannotBeVoidArray(componentDecl);
                    componentDecl.binding = null;
                    return null;
                }
                if ((componentType.tagBits & 0x80L) != 0L) {
                    component.tagBits |= 0x80L;
                }
                if ((leafType = componentType.leafComponentType()) instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0) {
                    component.modifiers |= 0x40000000;
                }
                Annotation[] annotations = componentDecl.annotations;
                ASTNode.copyRecordComponentAnnotations(initializationScope, component, annotations);
                long sourceLevel = this.scope.compilerOptions().sourceLevel;
                if (sourceLevel >= 0x340000L) {
                    if (annotations != null && annotations.length != 0) {
                        ASTNode.copySE8AnnotationsToType(initializationScope, component, annotations, false);
                    }
                    Annotation.isTypeUseCompatible(componentDecl.type, this.scope, annotations);
                }
                if (initializationScope.shouldCheckAPILeaks(this, component.isPublic()) && componentDecl.type != null) {
                    initializationScope.detectAPILeaks(componentDecl.type, componentType);
                }
                if (this.externalAnnotationProvider != null) {
                    ExternalAnnotationSuperimposer.annotateComponentBinding(component, this.externalAnnotationProvider, this.environment);
                }
                return component;
            }
            ++f;
        }
        return null;
    }

    private void internalFaultInTypeForFieldsAndMethods() {
        this.fields();
        this.methods();
        int i = 0;
        int length = this.memberTypes.length;
        while (i < length) {
            ((SourceTypeBinding)this.memberTypes[i]).internalFaultInTypeForFieldsAndMethods();
            ++i;
        }
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public FieldBinding[] fields() {
        block14: {
            this.components();
            if (!this.isPrototype()) {
                if ((this.tagBits & 8192L) != 0L) {
                    return this.fields;
                }
                this.tagBits |= 8192L;
                this.fields = this.prototype.fields();
                return this.fields;
            }
            if ((this.tagBits & 8192L) != 0L) {
                return this.fields;
            }
            failed = 0;
            resolvedFields = this.fields;
            try {
                if ((this.tagBits & 4096L) == 0L) {
                    length = this.fields.length;
                    if (length > 1) {
                        ReferenceBinding.sortFields(this.fields, 0, length);
                    }
                    this.tagBits |= 4096L;
                }
                fieldsSnapshot = this.fields;
                i = 0;
                length = fieldsSnapshot.length;
                while (i < length) {
                    if (this.resolveTypeFor(fieldsSnapshot[i]) == null) {
                        if (resolvedFields == fieldsSnapshot) {
                            resolvedFields = new FieldBinding[length];
                            System.arraycopy(fieldsSnapshot, 0, resolvedFields, 0, length);
                        }
                        resolvedFields[i] = null;
                        ++failed;
                    }
                    ++i;
                }
            }
            finally {
                if (failed <= 0) break block14;
                newSize = resolvedFields.length - failed;
                if (newSize == 0) {
                    return this.setFields(Binding.NO_FIELDS);
                }
                newFields = new FieldBinding[newSize];
                i = 0;
                j = 0;
                length = resolvedFields.length;
                ** while (i < length)
            }
lbl-1000:
            // 1 sources

            {
                if (resolvedFields[i] != null) {
                    newFields[j++] = resolvedFields[i];
                }
                ++i;
                continue;
            }
lbl46:
            // 1 sources

            this.setFields(newFields);
        }
        this.tagBits |= 8192L;
        this.computeRecordComponents();
        return this.fields;
    }

    @Override
    public char[] genericTypeSignature() {
        if (!this.isPrototype()) {
            return this.prototype.genericTypeSignature();
        }
        if (this.genericReferenceTypeSignature == null) {
            this.genericReferenceTypeSignature = this.computeGenericTypeSignature(this.typeVariables);
        }
        return this.genericReferenceTypeSignature;
    }

    public char[] genericSignature() {
        int length;
        int i;
        if (!this.isPrototype()) {
            return this.prototype.genericSignature();
        }
        StringBuffer sig = null;
        if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            sig = new StringBuffer(10);
            sig.append('<');
            i = 0;
            length = this.typeVariables.length;
            while (i < length) {
                sig.append(this.typeVariables[i].genericSignature());
                ++i;
            }
            sig.append('>');
        } else {
            block10: {
                if (this.superclass == null || !this.superclass.isParameterizedType()) {
                    i = 0;
                    length = this.superInterfaces.length;
                    while (i < length) {
                        if (!this.superInterfaces[i].isParameterizedType()) {
                            ++i;
                            continue;
                        }
                        break block10;
                    }
                    return null;
                }
            }
            sig = new StringBuffer(10);
        }
        if (this.superclass != null) {
            sig.append(this.superclass.genericTypeSignature());
        } else {
            sig.append(this.scope.getJavaLangObject().genericTypeSignature());
        }
        i = 0;
        length = this.superInterfaces.length;
        while (i < length) {
            sig.append(this.superInterfaces[i].genericTypeSignature());
            ++i;
        }
        return sig.toString().toCharArray();
    }

    @Override
    public long getAnnotationTagBits() {
        if (!this.isPrototype()) {
            return this.prototype.getAnnotationTagBits();
        }
        if ((this.tagBits & 0x200000000L) == 0L && this.scope != null) {
            if ((this.tagBits & 0x200L) == 0L) {
                CompilationUnitScope pkgCUS = this.scope.compilationUnitScope();
                boolean current = pkgCUS.connectingHierarchy;
                pkgCUS.connectingHierarchy = true;
                try {
                    this.initAnnotationTagBits();
                }
                finally {
                    pkgCUS.connectingHierarchy = current;
                }
            } else {
                this.initAnnotationTagBits();
            }
        }
        return this.tagBits;
    }

    private void initAnnotationTagBits() {
        TypeDeclaration typeDecl = this.scope.referenceContext;
        boolean old = typeDecl.staticInitializerScope.insideTypeAnnotation;
        try {
            typeDecl.staticInitializerScope.insideTypeAnnotation = true;
            ASTNode.resolveAnnotations((BlockScope)typeDecl.staticInitializerScope, typeDecl.annotations, (Binding)this);
        }
        finally {
            typeDecl.staticInitializerScope.insideTypeAnnotation = old;
        }
        if ((this.tagBits & 0x400000000000L) != 0L) {
            this.modifiers |= 0x100000;
        }
    }

    public MethodBinding[] getDefaultAbstractMethods() {
        if (!this.isPrototype()) {
            return this.prototype.getDefaultAbstractMethods();
        }
        int count = 0;
        int i = this.methods.length;
        while (--i >= 0) {
            if (!this.methods[i].isDefaultAbstract()) continue;
            ++count;
        }
        if (count == 0) {
            return Binding.NO_METHODS;
        }
        MethodBinding[] result = new MethodBinding[count];
        count = 0;
        int i2 = this.methods.length;
        while (--i2 >= 0) {
            if (!this.methods[i2].isDefaultAbstract()) continue;
            result[count++] = this.methods[i2];
        }
        return result;
    }

    @Override
    public MethodBinding getExactConstructor(TypeBinding[] argumentTypes) {
        block17: {
            long range;
            int argCount;
            block16: {
                if (!this.isPrototype()) {
                    return this.prototype.getExactConstructor(argumentTypes);
                }
                argCount = argumentTypes.length;
                if (this.isRecordDeclaration && argCount > 0) {
                    this.methods();
                }
                if ((this.tagBits & 0x8000L) == 0L) break block16;
                long range2 = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods);
                if (range2 < 0L) break block17;
                int imethod = (int)range2;
                int end = (int)(range2 >> 32);
                while (imethod <= end) {
                    block14: {
                        MethodBinding method = this.methods[imethod];
                        if (method.parameters.length == argCount) {
                            TypeBinding[] toMatch = method.parameters;
                            int iarg = 0;
                            while (iarg < argCount) {
                                if (!TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                    ++iarg;
                                    continue;
                                }
                                break block14;
                            }
                            return method;
                        }
                    }
                    ++imethod;
                }
                break block17;
            }
            if ((this.tagBits & 0x4000L) == 0L) {
                int length = this.methods.length;
                if (length > 1) {
                    ReferenceBinding.sortMethods(this.methods, 0, length);
                }
                this.tagBits |= 0x4000L;
            }
            if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0L) {
                int imethod = (int)range;
                int end = (int)(range >> 32);
                while (imethod <= end) {
                    block15: {
                        MethodBinding method = this.methods[imethod];
                        if (this.resolveTypesFor(method) == null || method.returnType == null) {
                            this.methods();
                            return this.getExactConstructor(argumentTypes);
                        }
                        if (method.parameters.length == argCount) {
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
        }
        return null;
    }

    MethodBinding getSyntheticCanon() {
        if (this.isRecordDeclaration) {
            int len;
            SyntheticMethodBinding[] smbs = this.syntheticMethods();
            int n = len = smbs != null ? smbs.length : 0;
            if (len > 0) {
                SyntheticMethodBinding[] syntheticMethodBindingArray = smbs;
                int n2 = smbs.length;
                int n3 = 0;
                while (n3 < n2) {
                    SyntheticMethodBinding method = syntheticMethodBindingArray[n3];
                    if (CharOperation.equals(TypeConstants.INIT, method.selector)) {
                        return method;
                    }
                    ++n3;
                }
            }
        }
        return null;
    }

    @Override
    public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope) {
        if (!this.isPrototype()) {
            return this.prototype.getExactMethod(selector, argumentTypes, refScope);
        }
        int argCount = argumentTypes.length;
        boolean foundNothing = true;
        if ((this.tagBits & 0x8000L) != 0L) {
            long range = ReferenceBinding.binarySearch(selector, this.methods);
            if (range >= 0L) {
                int imethod = (int)range;
                int end = (int)(range >> 32);
                while (imethod <= end) {
                    block27: {
                        MethodBinding method = this.methods[imethod];
                        foundNothing = false;
                        if (method.parameters.length == argCount) {
                            TypeBinding[] toMatch = method.parameters;
                            int iarg = 0;
                            while (iarg < argCount) {
                                if (!TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                    ++iarg;
                                    continue;
                                }
                                break block27;
                            }
                            return method;
                        }
                    }
                    ++imethod;
                }
            }
        } else {
            long range;
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
                int imethod = start;
                while (imethod <= end) {
                    MethodBinding method = this.methods[imethod];
                    if (this.resolveTypesFor(method) == null || method.returnType == null) {
                        this.methods();
                        return this.getExactMethod(selector, argumentTypes, refScope);
                    }
                    ++imethod;
                }
                boolean isSource15 = this.scope.compilerOptions().sourceLevel >= 0x310000L;
                int i = start;
                while (i <= end) {
                    MethodBinding method1 = this.methods[i];
                    int j = end;
                    while (j > i) {
                        boolean paramsMatch;
                        MethodBinding method2 = this.methods[j];
                        boolean bl = paramsMatch = isSource15 ? method1.areParameterErasuresEqual(method2) : method1.areParametersEqual(method2);
                        if (paramsMatch) {
                            this.methods();
                            return this.getExactMethod(selector, argumentTypes, refScope);
                        }
                        --j;
                    }
                    ++i;
                }
                int imethod2 = start;
                while (imethod2 <= end) {
                    block28: {
                        MethodBinding method = this.methods[imethod2];
                        TypeBinding[] toMatch = method.parameters;
                        if (toMatch.length == argCount) {
                            int iarg = 0;
                            while (iarg < argCount) {
                                if (!TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                    ++iarg;
                                    continue;
                                }
                                break block28;
                            }
                            return method;
                        }
                    }
                    ++imethod2;
                }
            }
        }
        if (foundNothing) {
            if (this.isInterface()) {
                if (this.superInterfaces.length == 1) {
                    if (refScope != null) {
                        refScope.recordTypeReference(this.superInterfaces[0]);
                    }
                    return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
                }
            } else if (this.superclass != null) {
                if (refScope != null) {
                    refScope.recordTypeReference(this.superclass);
                }
                return this.superclass.getExactMethod(selector, argumentTypes, refScope);
            }
        }
        return null;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public FieldBinding getField(char[] fieldName, boolean needResolve) {
        if (!this.isPrototype()) {
            return this.prototype.getField(fieldName, needResolve);
        }
        if ((this.tagBits & 8192L) != 0L) {
            return ReferenceBinding.binarySearch(fieldName, this.fields);
        }
        if ((this.tagBits & 4096L) == 0L) {
            length = this.fields.length;
            if (length > 1) {
                ReferenceBinding.sortFields(this.fields, 0, length);
            }
            this.tagBits |= 4096L;
        }
        if ((field = ReferenceBinding.binarySearch(fieldName, this.fields)) == null) return null;
        result = null;
        try {
            var6_6 = result = this.resolveTypeFor(field);
            return var6_6;
        }
        finally {
            if (result != null) return var6_6;
            newSize = this.fields.length - 1;
            if (newSize == 0) {
                this.setFields(Binding.NO_FIELDS);
                return var6_6;
            }
            newFields = new FieldBinding[newSize];
            index = 0;
            i = 0;
            length = this.fields.length;
            ** while (i < length)
        }
lbl-1000:
        // 1 sources

        {
            f = this.fields[i];
            if (f != field) {
                newFields[index++] = f;
            }
            ++i;
            continue;
        }
lbl32:
        // 1 sources

        this.setFields(newFields);
        return var6_6;
    }

    @Override
    public MethodBinding[] getMethods(char[] selector) {
        MethodBinding method;
        int end;
        int start;
        long range;
        if (!this.isPrototype()) {
            return this.prototype.getMethods(selector);
        }
        if ((this.tagBits & 0x8000L) != 0L) {
            long range2 = ReferenceBinding.binarySearch(selector, this.methods);
            if (range2 >= 0L) {
                int start2 = (int)range2;
                int end2 = (int)(range2 >> 32);
                int length = end2 - start2 + 1;
                MethodBinding[] result = new MethodBinding[length];
                System.arraycopy(this.methods, start2, result, 0, length);
                return result;
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
            start = (int)range;
            end = (int)(range >> 32);
            int i = start;
            while (i <= end) {
                method = this.methods[i];
                if (this.resolveTypesFor(method) == null || method.returnType == null) {
                    this.methods();
                    return this.getMethods(selector);
                }
                ++i;
            }
        } else {
            return Binding.NO_METHODS;
        }
        int length = end - start + 1;
        MethodBinding[] result = new MethodBinding[length];
        System.arraycopy(this.methods, start, result, 0, length);
        boolean isSource15 = this.scope.compilerOptions().sourceLevel >= 0x310000L;
        int i = 0;
        length = result.length - 1;
        while (i < length) {
            method = result[i];
            int j = length;
            while (j > i) {
                boolean paramsMatch;
                boolean bl = paramsMatch = isSource15 ? method.areParameterErasuresEqual(result[j]) : method.areParametersEqual(result[j]);
                if (paramsMatch) {
                    this.methods();
                    return this.getMethods(selector);
                }
                --j;
            }
            ++i;
        }
        return result;
    }

    public void generateSyntheticFinalFieldInitialization(CodeStream codeStream) {
        if (this.synthetics == null || this.synthetics[1] == null) {
            return;
        }
        Collection syntheticFields = this.synthetics[1].values();
        for (FieldBinding field : syntheticFields) {
            MethodBinding[] accessors;
            if (!CharOperation.prefixEquals(TypeConstants.SYNTHETIC_SWITCH_ENUM_TABLE, field.name) || !field.isFinal() || (accessors = (MethodBinding[])this.synthetics[0].get(new String(field.name))) == null || accessors[0] == null) continue;
            codeStream.invoke((byte)-72, accessors[0], null);
            codeStream.fieldAccess((byte)-77, field, null);
        }
    }

    public FieldBinding getSyntheticField(LocalVariableBinding actualOuterLocalVariable) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null || this.synthetics[1] == null) {
            return null;
        }
        return (FieldBinding)this.synthetics[1].get(actualOuterLocalVariable);
    }

    public FieldBinding getSyntheticField(ReferenceBinding targetEnclosingType, boolean onlyExactMatch) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null || this.synthetics[1] == null) {
            return null;
        }
        FieldBinding field2 = (FieldBinding)this.synthetics[1].get(targetEnclosingType);
        if (field2 != null) {
            return field2;
        }
        if (!onlyExactMatch) {
            for (FieldBinding field2 : this.synthetics[1].values()) {
                if (!CharOperation.prefixEquals(TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, field2.name) || field2.type.findSuperTypeOriginatingFrom(targetEnclosingType) == null) continue;
                return field2;
            }
        }
        return null;
    }

    public SyntheticMethodBinding getSyntheticBridgeMethod(MethodBinding inheritedMethodToBridge) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            return null;
        }
        if (this.synthetics[0] == null) {
            return null;
        }
        SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(inheritedMethodToBridge);
        if (accessors == null) {
            return null;
        }
        return accessors[1];
    }

    @Override
    public boolean hasTypeBit(int bit) {
        if (!this.isPrototype()) {
            return this.prototype.hasTypeBit(bit);
        }
        return (this.typeBits & bit) != 0;
    }

    @Override
    public void initializeDeprecatedAnnotationTagBits() {
        if (!this.isPrototype()) {
            this.prototype.initializeDeprecatedAnnotationTagBits();
            return;
        }
        if ((this.tagBits & 0x400000000L) == 0L) {
            TypeDeclaration typeDecl = this.scope.referenceContext;
            boolean old = typeDecl.staticInitializerScope.insideTypeAnnotation;
            try {
                typeDecl.staticInitializerScope.insideTypeAnnotation = true;
                ASTNode.resolveDeprecatedAnnotations(typeDecl.staticInitializerScope, typeDecl.annotations, this);
                this.tagBits |= 0x400000000L;
            }
            finally {
                typeDecl.staticInitializerScope.insideTypeAnnotation = old;
            }
            if ((this.tagBits & 0x400000000000L) != 0L) {
                this.modifiers |= 0x100000;
            }
        }
    }

    @Override
    void initializeForStaticImports() {
        if (!this.isPrototype()) {
            this.prototype.initializeForStaticImports();
            return;
        }
        if (this.scope == null) {
            return;
        }
        if (this.superInterfaces == null) {
            this.scope.connectTypeHierarchy();
        }
        this.scope.buildFields();
        this.scope.buildMethods();
    }

    @Override
    int getNullDefault() {
        if (!this.isPrototype()) {
            return this.prototype.getNullDefault();
        }
        switch (this.nullnessDefaultInitialized) {
            case 0: {
                this.getAnnotationTagBits();
            }
            case 1: {
                this.getPackage().isViewedAsDeprecated();
                this.nullnessDefaultInitialized = 2;
            }
        }
        return this.defaultNullness;
    }

    @Override
    public boolean isEquivalentTo(TypeBinding otherType) {
        if (!this.isPrototype()) {
            return this.prototype.isEquivalentTo(otherType);
        }
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
            case 260: {
                int otherLength;
                ReferenceBinding enclosing;
                if (!((otherType.tagBits & 0x40000000L) != 0L || this.isMemberType() && otherType.isMemberType())) {
                    return false;
                }
                ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
                if (TypeBinding.notEquals(this, otherParamType.genericType())) {
                    return false;
                }
                if (!this.isStatic() && (enclosing = this.enclosingType()) != null) {
                    ReferenceBinding otherEnclosing = otherParamType.enclosingType();
                    if (otherEnclosing == null) {
                        return false;
                    }
                    if ((otherEnclosing.tagBits & 0x40000000L) == 0L ? TypeBinding.notEquals(enclosing, otherEnclosing) : !enclosing.isEquivalentTo(otherParamType.enclosingType())) {
                        return false;
                    }
                }
                int length = this.typeVariables == null ? 0 : this.typeVariables.length;
                TypeBinding[] otherArguments = otherParamType.arguments;
                int n = otherLength = otherArguments == null ? 0 : otherArguments.length;
                if (otherLength != length) {
                    return false;
                }
                int i = 0;
                while (i < length) {
                    if (!this.typeVariables[i].isTypeArgumentContainedBy(otherArguments[i])) {
                        return false;
                    }
                    ++i;
                }
                return true;
            }
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
        return (this.tagBits & 0x200L) != 0L;
    }

    @Override
    public boolean isRepeatableAnnotationType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        return this.containerAnnotationType != null;
    }

    @Override
    public boolean isTaggedRepeatable() {
        return (this.tagBits & 0x1000000000000000L) != 0L;
    }

    @Override
    public boolean canBeSeenBy(Scope sco) {
        SourceTypeBinding invocationType = sco.enclosingSourceType();
        if (TypeBinding.equalsEquals(invocationType, this)) {
            return true;
        }
        return this.environment.canTypeBeAccessed(this, sco) && super.canBeSeenBy(sco);
    }

    @Override
    public ReferenceBinding[] memberTypes() {
        if (!this.isPrototype()) {
            if ((this.tagBits & 0x10000000L) == 0L) {
                return this.sortedMemberTypes();
            }
            this.memberTypes = this.prototype.memberTypes();
            ReferenceBinding[] members = this.memberTypes;
            int membersLength = members == null ? 0 : members.length;
            this.memberTypes = new ReferenceBinding[membersLength];
            int i = 0;
            while (i < membersLength) {
                this.memberTypes[i] = this.environment.createMemberType(members[i], this);
                ++i;
            }
            this.tagBits &= 0xFFFFFFFFEFFFFFFFL;
            this.memberTypesSorted = true;
        }
        return this.sortedMemberTypes();
    }

    private ReferenceBinding[] sortedMemberTypes() {
        if (!this.memberTypesSorted) {
            int length = this.memberTypes.length;
            if (length > 1) {
                SourceTypeBinding.sortMemberTypes(this.memberTypes, 0, length);
            }
            this.memberTypesSorted = true;
        }
        return this.memberTypes;
    }

    @Override
    public boolean hasMemberTypes() {
        if (!this.isPrototype()) {
            return this.prototype.hasMemberTypes();
        }
        return this.memberTypes.length > 0;
    }

    private int getImplicitCanonicalConstructor() {
        if (this.methods != null && this.scope.compilerOptions().sourceLevel >= 0x3A0000L) {
            int i = 0;
            int l = this.methods.length;
            while (i < l) {
                MethodBinding method = this.methods[i];
                if ((method.tagBits & 0x800L) != 0L && (method.tagBits & 0x1000L) != 0L) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    private MethodBinding checkAndGetExplicitCanonicalConstructors() {
        RecordComponentBinding[] recComps = this.components;
        int nRecordComponents = recComps.length;
        MethodBinding explictCanConstr = null;
        MethodBinding[] methodBindingArray = this.methods;
        int n = this.methods.length;
        int n2 = 0;
        while (n2 < n) {
            MethodBinding method = methodBindingArray[n2];
            if (method.isConstructor() && (method.tagBits & 0x1000L) == 0L && method.parameters.length == nRecordComponents) {
                boolean isEC = true;
                int j = 0;
                while (j < nRecordComponents) {
                    if (TypeBinding.notEquals(method.parameters[j], recComps[j].type)) {
                        isEC = false;
                        break;
                    }
                    ++j;
                }
                if (isEC) {
                    explictCanConstr = this.checkRecordCanonicalConstructor(method);
                    if (explictCanConstr != null) break;
                    isEC = false;
                }
            }
            ++n2;
        }
        return explictCanConstr;
    }

    private int getImplicitMethod(MethodBinding[] resolvedMethods, char[] name) {
        if (resolvedMethods != null && this.scope.compilerOptions().sourceLevel >= 0x3C0000L) {
            int i = 0;
            int l = resolvedMethods.length;
            while (i < l) {
                MethodBinding method = resolvedMethods[i];
                if (method != null && CharOperation.equals(method.selector, name) && ((method.tagBits & 0x1000L) != 0L || method instanceof SyntheticMethodBinding)) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    /*
     * Exception decompiling
     */
    @Override
    public MethodBinding[] methods() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[DOLOOP]], but top level block is 6[UNCONDITIONALDOLOOP]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    static boolean isAtleastAsAccessibleAsRecord(MethodBinding canonicalConstructor) {
        ReferenceBinding enclosingRecord = canonicalConstructor.declaringClass;
        if (enclosingRecord.isPublic()) {
            return canonicalConstructor.isPublic();
        }
        if (enclosingRecord.isProtected()) {
            return canonicalConstructor.isPublic() || canonicalConstructor.isProtected();
        }
        if (enclosingRecord.isPrivate()) {
            return true;
        }
        return !canonicalConstructor.isPrivate();
    }

    private void checkCanonicalConstructorParameterNames(MethodBinding explicitCanonicalConstructor, AbstractMethodDeclaration methodDecl) {
        int l;
        int n = l = explicitCanonicalConstructor.parameters != null ? explicitCanonicalConstructor.parameters.length : 0;
        if (l == 0) {
            return;
        }
        ReferenceBinding enclosingRecord = explicitCanonicalConstructor.declaringClass;
        assert (enclosingRecord.isRecord());
        assert (enclosingRecord instanceof SourceTypeBinding);
        SourceTypeBinding recordBinding = (SourceTypeBinding)enclosingRecord;
        RecordComponentBinding[] comps = recordBinding.components();
        Argument[] args = methodDecl.arguments;
        int i = 0;
        while (i < l) {
            if (!CharOperation.equals(args[i].name, comps[i].name)) {
                this.scope.problemReporter().recordIllegalParameterNameInCanonicalConstructor(comps[i], args[i]);
            }
            ++i;
        }
    }

    private MethodBinding checkRecordCanonicalConstructor(MethodBinding explicitCanonicalConstructor) {
        TypeParameter[] typeParameters;
        AbstractMethodDeclaration methodDecl = explicitCanonicalConstructor.sourceMethod();
        if (methodDecl == null) {
            return null;
        }
        if (!SourceTypeBinding.isAtleastAsAccessibleAsRecord(explicitCanonicalConstructor)) {
            this.scope.problemReporter().recordCanonicalConstructorVisibilityReduced(methodDecl);
        }
        if ((typeParameters = methodDecl.typeParameters()) != null && typeParameters.length > 0) {
            this.scope.problemReporter().recordCanonicalConstructorShouldNotBeGeneric(methodDecl);
        }
        if (explicitCanonicalConstructor.thrownExceptions != null && explicitCanonicalConstructor.thrownExceptions.length > 0) {
            this.scope.problemReporter().recordCanonicalConstructorHasThrowsClause(methodDecl);
        }
        this.checkCanonicalConstructorParameterNames(explicitCanonicalConstructor, methodDecl);
        explicitCanonicalConstructor.tagBits |= 0x800L;
        return explicitCanonicalConstructor;
    }

    @Override
    public ReferenceBinding[] permittedTypes() {
        return this.permittedTypes;
    }

    @Override
    public TypeBinding prototype() {
        return this.prototype;
    }

    public boolean isPrototype() {
        return this == this.prototype;
    }

    @Override
    public boolean isRecord() {
        return this.isRecordDeclaration;
    }

    @Override
    public ReferenceBinding containerAnnotationType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.containerAnnotationType instanceof UnresolvedReferenceBinding) {
            this.containerAnnotationType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.containerAnnotationType, this.scope.environment(), false);
        }
        return this.containerAnnotationType;
    }

    public FieldBinding resolveTypeFor(FieldBinding field) {
        FieldDeclaration[] fieldDecls;
        if (!this.isPrototype()) {
            return this.prototype.resolveTypeFor(field);
        }
        if ((field.modifiers & 0x2000000) == 0) {
            return field;
        }
        long sourceLevel = this.scope.compilerOptions().sourceLevel;
        if (sourceLevel >= 0x310000L && (field.getAnnotationTagBits() & 0x400000000000L) != 0L) {
            field.modifiers |= 0x100000;
        }
        if (this.isViewedAsDeprecated() && !field.isDeprecated()) {
            field.modifiers |= 0x200000;
            field.tagBits |= this.tagBits & 0x4000000000000000L;
        }
        if (this.hasRestrictedAccess()) {
            field.modifiers |= 0x40000;
        }
        int length = (fieldDecls = this.scope.referenceContext.fields) == null ? 0 : fieldDecls.length;
        int f = 0;
        while (f < length) {
            if (fieldDecls[f].binding == field) {
                MethodScope initializationScope = field.isStatic() ? this.scope.referenceContext.staticInitializerScope : this.scope.referenceContext.initializerScope;
                FieldBinding previousField = initializationScope.initializedField;
                try {
                    RecordComponentBinding rcb;
                    TypeBinding leafType;
                    TypeBinding fieldType;
                    initializationScope.initializedField = field;
                    FieldDeclaration fieldDecl = fieldDecls[f];
                    field.type = fieldType = fieldDecl.getKind() == 3 ? initializationScope.environment().convertToRawType(this, false) : fieldDecl.type.resolveType(initializationScope, true);
                    field.modifiers &= 0xFDFFFFFF;
                    if (fieldType == null) {
                        fieldDecl.binding = null;
                        return null;
                    }
                    if (fieldType == TypeBinding.VOID) {
                        this.scope.problemReporter().variableTypeCannotBeVoid(fieldDecl);
                        fieldDecl.binding = null;
                        return null;
                    }
                    if (fieldType.isArrayType() && ((ArrayBinding)fieldType).leafComponentType == TypeBinding.VOID) {
                        this.scope.problemReporter().variableTypeCannotBeVoidArray(fieldDecl);
                        fieldDecl.binding = null;
                        return null;
                    }
                    if ((fieldType.tagBits & 0x80L) != 0L) {
                        field.tagBits |= 0x80L;
                    }
                    if ((leafType = fieldType.leafComponentType()) instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0) {
                        field.modifiers |= 0x40000000;
                    }
                    Annotation[] relevantRecordComponentAnnotations = null;
                    if (sourceLevel >= 0x3A0000L && field.isRecordComponent() && (rcb = this.getRecordComponent(field.name)) != null) {
                        relevantRecordComponentAnnotations = ASTNode.copyRecordComponentAnnotations(initializationScope, field, rcb.sourceRecordComponent().annotations);
                    }
                    if (sourceLevel >= 0x340000L) {
                        Annotation[] annotations = fieldDecl.annotations;
                        if (annotations == null && relevantRecordComponentAnnotations != null) {
                            annotations = relevantRecordComponentAnnotations;
                        }
                        if (annotations != null && annotations.length != 0) {
                            ASTNode.copySE8AnnotationsToType(initializationScope, field, annotations, fieldDecl.getKind() == 3);
                        }
                        Annotation.isTypeUseCompatible(fieldDecl.type, this.scope, annotations);
                    }
                    if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                        if (fieldDecl.getKind() == 3) {
                            field.tagBits |= 0x100000000000000L;
                        } else {
                            if (this.hasNonNullDefaultFor(32, fieldDecl.sourceStart)) {
                                field.fillInDefaultNonNullness(fieldDecl, initializationScope);
                            }
                            if (!this.scope.validateNullAnnotation(field.tagBits, fieldDecl.type, fieldDecl.annotations)) {
                                field.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                            }
                        }
                    }
                    if (initializationScope.shouldCheckAPILeaks(this, field.isPublic()) && fieldDecl.type != null) {
                        initializationScope.detectAPILeaks(fieldDecl.type, fieldType);
                    }
                }
                finally {
                    initializationScope.initializedField = previousField;
                }
                if (this.externalAnnotationProvider != null) {
                    ExternalAnnotationSuperimposer.annotateFieldBinding(field, this.externalAnnotationProvider, this.environment);
                }
                return field;
            }
            ++f;
        }
        return null;
    }

    public MethodBinding resolveTypesFor(MethodBinding method) {
        ProblemReporter problemReporter = this.scope.problemReporter();
        IErrorHandlingPolicy suspendedPolicy = problemReporter.suspendTempErrorHandlingPolicy();
        try {
            MethodBinding methodBinding = this.resolveTypesWithSuspendedTempErrorHandlingPolicy(method);
            return methodBinding;
        }
        finally {
            problemReporter.resumeTempErrorHandlingPolicy(suspendedPolicy);
        }
    }

    private MethodBinding resolveTypesWithSuspendedTempErrorHandlingPolicy(MethodBinding method) {
        long nullTagBits;
        Annotation[] annotations;
        TypeReference[] exceptionTypes;
        AbstractMethodDeclaration methodDecl;
        int i;
        if (!this.isPrototype()) {
            return this.prototype.resolveTypesFor(method);
        }
        if ((method.modifiers & 0x2000000) == 0) {
            return method;
        }
        long sourceLevel = this.scope.compilerOptions().sourceLevel;
        if (sourceLevel >= 0x310000L) {
            ReferenceBinding object = this.scope.getJavaLangObject();
            TypeVariableBinding[] tvb = method.typeVariables;
            i = 0;
            while (i < tvb.length) {
                tvb[i].superclass = object;
                ++i;
            }
            if ((method.getAnnotationTagBits() & 0x400000000000L) != 0L) {
                method.modifiers |= 0x100000;
            }
        }
        if (this.isViewedAsDeprecated() && !method.isDeprecated()) {
            method.modifiers |= 0x200000;
            method.tagBits |= this.tagBits & 0x4000000000000000L;
        }
        if (this.hasRestrictedAccess()) {
            method.modifiers |= 0x40000;
        }
        if ((methodDecl = method.sourceMethod()) == null) {
            return null;
        }
        TypeParameter[] typeParameters = methodDecl.typeParameters();
        if (typeParameters != null) {
            methodDecl.scope.connectTypeVariables(typeParameters, true);
            i = 0;
            int paramLength = typeParameters.length;
            while (i < paramLength) {
                typeParameters[i].checkBounds(methodDecl.scope);
                ++i;
            }
        }
        if ((exceptionTypes = methodDecl.thrownExceptions) != null) {
            int size = exceptionTypes.length;
            method.thrownExceptions = new ReferenceBinding[size];
            int count = 0;
            int i2 = 0;
            while (i2 < size) {
                ReferenceBinding resolvedExceptionType = (ReferenceBinding)exceptionTypes[i2].resolveType(methodDecl.scope, true);
                if (resolvedExceptionType != null) {
                    if (resolvedExceptionType.isBoundParameterizedType()) {
                        methodDecl.scope.problemReporter().invalidParameterizedExceptionType(resolvedExceptionType, exceptionTypes[i2]);
                    } else if (resolvedExceptionType.findSuperTypeOriginatingFrom(21, true) == null && resolvedExceptionType.isValidBinding()) {
                        methodDecl.scope.problemReporter().cannotThrowType(exceptionTypes[i2], resolvedExceptionType);
                    } else {
                        if ((resolvedExceptionType.tagBits & 0x80L) != 0L) {
                            method.tagBits |= 0x80L;
                        }
                        if (exceptionTypes[i2].hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY)) {
                            methodDecl.scope.problemReporter().nullAnnotationUnsupportedLocation(exceptionTypes[i2]);
                        }
                        method.modifiers |= resolvedExceptionType.modifiers & 0x40000000;
                        method.thrownExceptions[count++] = resolvedExceptionType;
                    }
                }
                ++i2;
            }
            if (count < size) {
                method.thrownExceptions = new ReferenceBinding[count];
                System.arraycopy(method.thrownExceptions, 0, method.thrownExceptions, 0, count);
            }
        }
        if (methodDecl.receiver != null) {
            method.receiver = methodDecl.receiver.type.resolveType(methodDecl.scope, true);
        }
        boolean reportUnavoidableGenericTypeProblems = this.scope.compilerOptions().reportUnavoidableGenericTypeProblems;
        boolean foundArgProblem = false;
        boolean checkAPIleak = methodDecl.scope.shouldCheckAPILeaks(this, method.isPublic());
        Argument[] arguments = methodDecl.arguments;
        if (arguments != null) {
            int size = arguments.length;
            method.parameters = Binding.NO_PARAMETERS;
            TypeBinding[] newParameters = new TypeBinding[size];
            int i3 = 0;
            while (i3 < size) {
                TypeBinding parameterType;
                boolean deferRawTypeCheck;
                Argument arg = arguments[i3];
                if (arg.annotations != null) {
                    method.tagBits |= 0x400L;
                }
                boolean bl = deferRawTypeCheck = !reportUnavoidableGenericTypeProblems && !method.isConstructor() && (arg.type.bits & 0x40000000) == 0;
                if (deferRawTypeCheck) {
                    arg.type.bits |= 0x40000000;
                }
                try {
                    ASTNode.handleNonNullByDefault(methodDecl.scope, arg.annotations, arg);
                    parameterType = arg.type.resolveType(methodDecl.scope, true);
                }
                finally {
                    if (deferRawTypeCheck) {
                        arg.type.bits &= 0xBFFFFFFF;
                    }
                }
                if (parameterType == null) {
                    foundArgProblem = true;
                } else if (parameterType == TypeBinding.VOID) {
                    if (!this.isRecordDeclaration || !(methodDecl instanceof ConstructorDeclaration) || (methodDecl.bits & 0x400) == 0) {
                        methodDecl.scope.problemReporter().argumentTypeCannotBeVoid(methodDecl, arg);
                    }
                    foundArgProblem = true;
                } else {
                    TypeBinding leafType;
                    if ((parameterType.tagBits & 0x80L) != 0L) {
                        method.tagBits |= 0x80L;
                    }
                    if ((leafType = parameterType.leafComponentType()) instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0) {
                        method.modifiers |= 0x40000000;
                    }
                    newParameters[i3] = parameterType;
                    if (checkAPIleak) {
                        methodDecl.scope.detectAPILeaks(arg.type, parameterType);
                    }
                    arg.binding = new LocalVariableBinding((LocalDeclaration)arg, parameterType, arg.modifiers, methodDecl.scope);
                }
                ++i3;
            }
            if (!foundArgProblem) {
                method.parameters = newParameters;
            }
        }
        if (sourceLevel >= 0x330000L) {
            if ((method.tagBits & 0x8000000000000L) != 0L) {
                if (!method.isVarargs()) {
                    methodDecl.scope.problemReporter().safeVarargsOnFixedArityMethod(method);
                } else if (!(method.isStatic() || method.isFinal() || method.isConstructor() || sourceLevel >= 0x350000L && method.isPrivate())) {
                    methodDecl.scope.problemReporter().safeVarargsOnNonFinalInstanceMethod(method);
                }
            } else {
                this.checkAndFlagHeapPollution(method, methodDecl);
            }
        }
        boolean foundReturnTypeProblem = false;
        if (!method.isConstructor()) {
            TypeReference returnType;
            TypeReference typeReference = returnType = methodDecl instanceof MethodDeclaration ? ((MethodDeclaration)methodDecl).returnType : null;
            if (returnType == null) {
                methodDecl.scope.problemReporter().missingReturnType(methodDecl);
                method.returnType = null;
                foundReturnTypeProblem = true;
            } else {
                TypeBinding methodType;
                boolean deferRawTypeCheck;
                boolean bl = deferRawTypeCheck = !reportUnavoidableGenericTypeProblems && (returnType.bits & 0x40000000) == 0;
                if (deferRawTypeCheck) {
                    returnType.bits |= 0x40000000;
                }
                try {
                    methodType = returnType.resolveType(methodDecl.scope, true);
                }
                finally {
                    if (deferRawTypeCheck) {
                        returnType.bits &= 0xBFFFFFFF;
                    }
                }
                if (methodType == null) {
                    foundReturnTypeProblem = true;
                } else {
                    TypeBinding leafType;
                    if ((methodType.tagBits & 0x80L) != 0L) {
                        method.tagBits |= 0x80L;
                    }
                    method.returnType = methodType;
                    if (sourceLevel >= 0x340000L && !method.isVoidMethod()) {
                        Annotation[] annotations2 = methodDecl.annotations;
                        if (annotations2 != null && annotations2.length != 0) {
                            ASTNode.copySE8AnnotationsToType(methodDecl.scope, method, methodDecl.annotations, false);
                        }
                        Annotation.isTypeUseCompatible(returnType, this.scope, methodDecl.annotations);
                    }
                    if ((leafType = methodType.leafComponentType()) instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0) {
                        method.modifiers |= 0x40000000;
                    } else if (leafType == TypeBinding.VOID && methodDecl.annotations != null) {
                        SourceTypeBinding.rejectTypeAnnotatedVoidMethod(methodDecl);
                    }
                    if (checkAPIleak) {
                        methodDecl.scope.detectAPILeaks(returnType, methodType);
                    }
                }
            }
        } else if (sourceLevel >= 0x340000L && (annotations = methodDecl.annotations) != null && annotations.length != 0) {
            ASTNode.copySE8AnnotationsToType(methodDecl.scope, method, methodDecl.annotations, false);
        }
        if (foundArgProblem) {
            methodDecl.binding = null;
            method.parameters = Binding.NO_PARAMETERS;
            if (typeParameters != null) {
                int i4 = 0;
                int length = typeParameters.length;
                while (i4 < length) {
                    typeParameters[i4].binding = null;
                    ++i4;
                }
            }
            return null;
        }
        CompilerOptions compilerOptions = this.scope.compilerOptions();
        if (compilerOptions.isAnnotationBasedNullAnalysisEnabled && !method.isConstructor() && method.returnType != null && (nullTagBits = method.tagBits & 0x180000000000000L) != 0L) {
            TypeReference returnTypeRef = ((MethodDeclaration)methodDecl).returnType;
            if (this.scope.environment().usesNullTypeAnnotations()) {
                if (!this.scope.validateNullAnnotation(nullTagBits, returnTypeRef, methodDecl.annotations)) {
                    method.returnType.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                }
                method.tagBits &= 0xFE7FFFFFFFFFFFFFL;
            } else if (!this.scope.validateNullAnnotation(nullTagBits, returnTypeRef, methodDecl.annotations)) {
                method.tagBits &= 0xFE7FFFFFFFFFFFFFL;
            }
        }
        if (this.externalAnnotationProvider != null) {
            ExternalAnnotationSuperimposer.annotateMethodBinding(method, arguments, this.externalAnnotationProvider, this.environment);
        }
        if (compilerOptions.storeAnnotations) {
            this.createArgumentBindings(method, compilerOptions);
        }
        if (foundReturnTypeProblem) {
            return method;
        }
        method.modifiers &= 0xFDFFFFFF;
        return method;
    }

    private void checkAndFlagHeapPollution(MethodBinding method, AbstractMethodDeclaration methodDecl) {
        if (method.parameters != null && method.parameters.length > 0 && method.isVarargs() && !method.parameters[method.parameters.length - 1].isReifiable()) {
            methodDecl.scope.problemReporter().possibleHeapPollutionFromVararg(methodDecl.arguments[methodDecl.arguments.length - 1]);
        }
    }

    private void checkAndFlagHeapPollutionForRecordImplicit(MethodBinding method, TypeDeclaration recordDecl) {
        int lastParamIndex;
        if (this.isRecordDeclaration && this.isVarArgs && method.parameters != null && method.parameters.length > 0 && !method.parameters[lastParamIndex = method.parameters.length - 1].isReifiable()) {
            this.scope.problemReporter().possibleHeapPollutionFromVararg(recordDecl.recordComponents[lastParamIndex]);
        }
    }

    private static void rejectTypeAnnotatedVoidMethod(AbstractMethodDeclaration methodDecl) {
        Annotation[] annotations = methodDecl.annotations;
        int length = annotations == null ? 0 : annotations.length;
        int i = 0;
        while (i < length) {
            ReferenceBinding binding = (ReferenceBinding)annotations[i].resolvedType;
            if (binding != null && (binding.tagBits & 0x20000000000000L) != 0L && (binding.tagBits & 0x4000000000L) == 0L) {
                methodDecl.scope.problemReporter().illegalUsageOfTypeAnnotations(annotations[i]);
            }
            ++i;
        }
    }

    private void createArgumentBindings(MethodBinding method, CompilerOptions compilerOptions) {
        AbstractMethodDeclaration methodDecl;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
            this.getNullDefault();
        }
        if ((methodDecl = method.sourceMethod()) != null) {
            if (method.parameters != Binding.NO_PARAMETERS) {
                methodDecl.createArgumentBindings();
            }
            if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
                new ImplicitNullAnnotationVerifier(this.scope.environment()).checkImplicitNullAnnotations(method, methodDecl, true, this.scope);
            }
        }
    }

    public void evaluateNullAnnotations() {
        boolean isInDefaultPkg;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.nullnessDefaultInitialized > 0 || !this.scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            return;
        }
        if ((this.tagBits & 0x180000000000000L) != 0L) {
            Annotation[] annotations = this.scope.referenceContext.annotations;
            int i = 0;
            while (i < annotations.length) {
                ReferenceBinding annotationType = annotations[i].getCompilerAnnotation().getAnnotationType();
                if (annotationType != null && annotationType.hasNullBit(96)) {
                    this.scope.problemReporter().nullAnnotationUnsupportedLocation(annotations[i]);
                    this.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                }
                ++i;
            }
        }
        boolean isPackageInfo = CharOperation.equals(this.sourceName, TypeConstants.PACKAGE_INFO_NAME);
        PackageBinding pkg = this.getPackage();
        boolean bl = isInDefaultPkg = pkg.compoundName == CharOperation.NO_CHAR_CHAR;
        if (!isPackageInfo) {
            boolean isInNullnessAnnotationPackage = this.scope.environment().isNullnessAnnotationPackage(pkg);
            if (!(pkg.getDefaultNullness() != 0 || isInDefaultPkg || isInNullnessAnnotationPackage || this instanceof NestedTypeBinding)) {
                ReferenceBinding packageInfo = pkg.getType(TypeConstants.PACKAGE_INFO_NAME, this.module);
                if (packageInfo == null) {
                    this.scope.problemReporter().missingNonNullByDefaultAnnotation(this.scope.referenceContext);
                    pkg.setDefaultNullness(2);
                } else {
                    packageInfo.getAnnotationTagBits();
                }
            }
        }
        this.nullnessDefaultInitialized = 1;
        if (this.defaultNullness != 0) {
            TypeDeclaration typeDecl = this.scope.referenceContext;
            if (isPackageInfo) {
                if (pkg.enclosingModule.getDefaultNullness() == this.defaultNullness) {
                    this.scope.problemReporter().nullDefaultAnnotationIsRedundant(typeDecl, typeDecl.annotations, pkg.enclosingModule);
                } else {
                    pkg.setDefaultNullness(this.defaultNullness);
                }
            } else {
                Binding target = this.scope.parent.checkRedundantDefaultNullness(this.defaultNullness, typeDecl.declarationSourceStart);
                if (target != null) {
                    this.scope.problemReporter().nullDefaultAnnotationIsRedundant(typeDecl, typeDecl.annotations, target);
                }
            }
        } else if (isPackageInfo || isInDefaultPkg && !(this instanceof NestedTypeBinding)) {
            this.scope.problemReporter().missingNonNullByDefaultAnnotation(this.scope.referenceContext);
            if (!isInDefaultPkg) {
                pkg.setDefaultNullness(2);
            }
        }
        this.maybeMarkTypeParametersNonNull();
    }

    private void maybeMarkTypeParametersNonNull() {
        if (this.typeVariables != null && this.typeVariables.length > 0) {
            if (this.scope == null || !this.scope.hasDefaultNullnessFor(128, this.sourceStart())) {
                return;
            }
            AnnotationBinding[] annots = new AnnotationBinding[]{this.environment.getNonNullAnnotation()};
            int i = 0;
            while (i < this.typeVariables.length) {
                TypeVariableBinding tvb = this.typeVariables[i];
                if ((tvb.tagBits & 0x180000000000000L) == 0L) {
                    this.typeVariables[i] = (TypeVariableBinding)this.environment.createAnnotatedType((TypeBinding)tvb, annots);
                }
                ++i;
            }
        }
    }

    @Override
    boolean hasNonNullDefaultFor(int location, int sourceStart) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.scope == null) {
            return (this.defaultNullness & location) != 0;
        }
        Scope skope = this.scope.referenceContext.initializerScope;
        if (skope == null) {
            skope = this.scope;
        }
        return ((Scope)skope).hasDefaultNullnessFor(location, sourceStart);
    }

    @Override
    protected boolean hasMethodWithNumArgs(char[] selector, int numArgs) {
        if ((this.tagBits & 0x8000L) != 0L) {
            return super.hasMethodWithNumArgs(selector, numArgs);
        }
        if (this.scope != null && this.scope.referenceContext.methods != null) {
            AbstractMethodDeclaration[] abstractMethodDeclarationArray = this.scope.referenceContext.methods;
            int n = this.scope.referenceContext.methods.length;
            int n2 = 0;
            while (n2 < n) {
                AbstractMethodDeclaration method = abstractMethodDeclarationArray[n2];
                if (CharOperation.equals(method.selector, selector) && (numArgs == 0 ? method.arguments == null : method.arguments != null && method.arguments.length == numArgs)) {
                    return true;
                }
                ++n2;
            }
        }
        return false;
    }

    @Override
    public AnnotationHolder retrieveAnnotationHolder(Binding binding, boolean forceInitialization) {
        if (!this.isPrototype()) {
            return this.prototype.retrieveAnnotationHolder(binding, forceInitialization);
        }
        if (forceInitialization) {
            binding.getAnnotationTagBits();
        }
        return super.retrieveAnnotationHolder(binding, false);
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

    public RecordComponentBinding[] setComponents(RecordComponentBinding[] comps) {
        if (!this.isPrototype()) {
            return this.prototype.setComponents(comps);
        }
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.components = comps;
                ++i;
            }
        }
        this.components = comps;
        return comps;
    }

    public FieldBinding[] setFields(FieldBinding[] fields) {
        if (!this.isPrototype()) {
            return this.prototype.setFields(fields);
        }
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.fields = fields;
                ++i;
            }
        }
        this.fields = fields;
        return fields;
    }

    public ReferenceBinding[] setMemberTypes(ReferenceBinding[] memberTypes) {
        if (!this.isPrototype()) {
            return this.prototype.setMemberTypes(memberTypes);
        }
        this.memberTypes = memberTypes;
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.tagBits |= 0x10000000L;
                annotatedType.memberTypes();
                ++i;
            }
        }
        this.sortedMemberTypes();
        return this.memberTypes;
    }

    public MethodBinding[] setMethods(MethodBinding[] methods) {
        if (!this.isPrototype()) {
            return this.prototype.setMethods(methods);
        }
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.methods = methods;
                ++i;
            }
        }
        this.methods = methods;
        return methods;
    }

    public ReferenceBinding[] setPermittedTypes(ReferenceBinding[] permittedTypes) {
        if (!this.isPrototype()) {
            return this.prototype.setPermittedTypes(permittedTypes);
        }
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.permittedTypes = permittedTypes;
                ++i;
            }
        }
        this.permittedTypes = permittedTypes;
        return permittedTypes;
    }

    public ReferenceBinding setSuperClass(ReferenceBinding superClass) {
        if (!this.isPrototype()) {
            return this.prototype.setSuperClass(superClass);
        }
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.superclass = superClass;
                ++i;
            }
        }
        this.superclass = superClass;
        return this.superclass;
    }

    public ReferenceBinding[] setSuperInterfaces(ReferenceBinding[] superInterfaces) {
        if (!this.isPrototype()) {
            return this.prototype.setSuperInterfaces(superInterfaces);
        }
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.superInterfaces = superInterfaces;
                ++i;
            }
        }
        this.superInterfaces = superInterfaces;
        return superInterfaces;
    }

    public TypeVariableBinding[] setTypeVariables(TypeVariableBinding[] typeVariables) {
        if (!this.isPrototype()) {
            return this.prototype.setTypeVariables(typeVariables);
        }
        if ((this.tagBits & 0x800000L) != 0L) {
            TypeBinding[] annotatedTypes = this.scope.environment().getAnnotatedTypes(this);
            int i = 0;
            int length = annotatedTypes == null ? 0 : annotatedTypes.length;
            while (i < length) {
                SourceTypeBinding annotatedType = (SourceTypeBinding)annotatedTypes[i];
                annotatedType.typeVariables = typeVariables;
                ++i;
            }
        }
        this.typeVariables = typeVariables;
        return typeVariables;
    }

    public final int sourceEnd() {
        if (!this.isPrototype()) {
            return this.prototype.sourceEnd();
        }
        return this.scope.referenceContext.sourceEnd;
    }

    public final int sourceStart() {
        if (!this.isPrototype()) {
            return this.prototype.sourceStart();
        }
        return this.scope.referenceContext.sourceStart;
    }

    @Override
    SimpleLookupTable storedAnnotations(boolean forceInitialize, boolean forceStore) {
        if (!this.isPrototype()) {
            return this.prototype.storedAnnotations(forceInitialize, forceStore);
        }
        if (forceInitialize && this.storedAnnotations == null && this.scope != null) {
            this.scope.referenceCompilationUnit().compilationResult.hasAnnotations = true;
            CompilerOptions globalOptions = this.scope.environment().globalOptions;
            if (!globalOptions.storeAnnotations && !forceStore) {
                return null;
            }
            this.storedAnnotations = new SimpleLookupTable(3);
        }
        return this.storedAnnotations;
    }

    @Override
    public ReferenceBinding superclass() {
        if (!this.isPrototype()) {
            this.superclass = this.prototype.superclass();
            return this.superclass;
        }
        return this.superclass;
    }

    @Override
    public ReferenceBinding[] superInterfaces() {
        ReferenceBinding[] referenceBindingArray;
        if (!this.isPrototype()) {
            this.superInterfaces = this.prototype.superInterfaces();
            return this.superInterfaces;
        }
        if (this.superInterfaces != null) {
            referenceBindingArray = this.superInterfaces;
        } else if (this.isAnnotationType()) {
            this.superInterfaces = new ReferenceBinding[]{this.scope.getJavaLangAnnotationAnnotation()};
            referenceBindingArray = this.superInterfaces;
        } else {
            referenceBindingArray = null;
        }
        return referenceBindingArray;
    }

    public SyntheticMethodBinding[] syntheticMethods() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null || this.synthetics[0] == null || this.synthetics[0].size() == 0) {
            return null;
        }
        int index = 0;
        SyntheticMethodBinding[] bindings = new SyntheticMethodBinding[1];
        for (SyntheticMethodBinding[] methodAccessors : this.synthetics[0].values()) {
            int i = 0;
            int max = methodAccessors.length;
            while (i < max) {
                if (methodAccessors[i] != null) {
                    if (index + 1 > bindings.length) {
                        SyntheticMethodBinding[] syntheticMethodBindingArray = bindings;
                        bindings = new SyntheticMethodBinding[index + 1];
                        System.arraycopy(syntheticMethodBindingArray, 0, bindings, 0, index);
                    }
                    bindings[index++] = methodAccessors[i];
                }
                ++i;
            }
        }
        int length = bindings.length;
        SyntheticMethodBinding[] sortedBindings = new SyntheticMethodBinding[length];
        int i = 0;
        while (i < length) {
            SyntheticMethodBinding binding;
            sortedBindings[binding.index] = binding = bindings[i];
            ++i;
        }
        return sortedBindings;
    }

    public FieldBinding[] syntheticFields() {
        SyntheticFieldBinding synthBinding;
        int i;
        Iterator elements;
        int literalSize;
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.synthetics == null) {
            return null;
        }
        int fieldSize = this.synthetics[1] == null ? 0 : this.synthetics[1].size();
        int totalSize = fieldSize + (literalSize = this.synthetics[2] == null ? 0 : this.synthetics[2].size());
        if (totalSize == 0) {
            return null;
        }
        FieldBinding[] bindings = new FieldBinding[totalSize];
        if (this.synthetics[1] != null) {
            elements = this.synthetics[1].values().iterator();
            i = 0;
            while (i < fieldSize) {
                synthBinding = (SyntheticFieldBinding)elements.next();
                bindings[synthBinding.index] = synthBinding;
                ++i;
            }
        }
        if (this.synthetics[2] != null) {
            elements = this.synthetics[2].values().iterator();
            i = 0;
            while (i < literalSize) {
                synthBinding = (SyntheticFieldBinding)elements.next();
                bindings[fieldSize + synthBinding.index] = synthBinding;
                ++i;
            }
        }
        return bindings;
    }

    public String toString() {
        int length;
        int i;
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        StringBuffer buffer = new StringBuffer(30);
        buffer.append("(id=");
        if (this.id == Integer.MAX_VALUE) {
            buffer.append("NoId");
        } else {
            buffer.append(this.id);
        }
        buffer.append(")\n");
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
        if (this.enclosingType() != null) {
            buffer.append("\n\tenclosing type : ");
            buffer.append(this.enclosingType().debugName());
        }
        if (this.fields != null) {
            if (this.fields != Binding.NO_FIELDS) {
                buffer.append("\n/*   fields   */");
                i = 0;
                length = this.fields.length;
                while (i < length) {
                    buffer.append('\n').append(this.fields[i] != null ? this.fields[i].toString() : "NULL FIELD");
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
                    buffer.append('\n').append(this.methods[i] != null ? this.methods[i].toString() : "NULL METHOD");
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
                    buffer.append('\n').append(this.memberTypes[i] != null ? this.memberTypes[i].toString() : "NULL TYPE");
                    ++i;
                }
            }
        } else {
            buffer.append("NULL MEMBER TYPES");
        }
        buffer.append("\n\n");
        return buffer.toString();
    }

    @Override
    public TypeVariableBinding[] typeVariables() {
        if (!this.isPrototype()) {
            this.typeVariables = this.prototype.typeVariables();
            return this.typeVariables;
        }
        return this.typeVariables != null ? this.typeVariables : Binding.NO_TYPE_VARIABLES;
    }

    void verifyMethods(MethodVerifier verifier) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        verifier.verify(this);
        int i = this.memberTypes.length;
        while (--i >= 0) {
            ((SourceTypeBinding)this.memberTypes[i]).verifyMethods(verifier);
        }
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
    public FieldBinding[] unResolvedFields() {
        if (!this.isPrototype()) {
            return this.prototype.unResolvedFields();
        }
        return this.fields;
    }

    public void tagIndirectlyAccessibleMembers() {
        if (!this.isPrototype()) {
            this.prototype.tagIndirectlyAccessibleMembers();
            return;
        }
        int i = 0;
        while (i < this.fields.length) {
            if (!this.fields[i].isPrivate()) {
                this.fields[i].modifiers |= 0x8000000;
            }
            ++i;
        }
        i = 0;
        while (i < this.memberTypes.length) {
            if (!this.memberTypes[i].isPrivate()) {
                this.memberTypes[i].modifiers |= 0x8000000;
            }
            ++i;
        }
        if (this.superclass.isPrivate() && this.superclass instanceof SourceTypeBinding) {
            ((SourceTypeBinding)this.superclass).tagIndirectlyAccessibleMembers();
        }
    }

    @Override
    public ModuleBinding module() {
        if (!this.isPrototype()) {
            return this.prototype.module;
        }
        return this.module;
    }

    public SourceTypeBinding getNestHost() {
        return this.nestHost;
    }

    public void setNestHost(SourceTypeBinding nestHost) {
        this.nestHost = nestHost;
    }

    public boolean isNestmateOf(SourceTypeBinding other) {
        CompilerOptions options = this.scope.compilerOptions();
        if (options.targetJDK < 0x370000L || options.complianceLevel < 0x370000L) {
            return false;
        }
        SourceTypeBinding otherHost = other.getNestHost();
        return TypeBinding.equalsEquals(this, other) || TypeBinding.equalsEquals(this.nestHost == null ? this : this.nestHost, otherHost == null ? other : otherHost);
    }

    public FieldBinding[] getImplicitComponentFields() {
        return this.implicitComponentFields;
    }

    public RecordComponentBinding getRecordComponent(char[] name) {
        if (this.isRecordDeclaration && this.components != null) {
            RecordComponentBinding[] recordComponentBindingArray = this.components;
            int n = this.components.length;
            int n2 = 0;
            while (n2 < n) {
                RecordComponentBinding rcb = recordComponentBindingArray[n2];
                if (CharOperation.equals(name, rcb.name)) {
                    return rcb;
                }
                ++n2;
            }
        }
        return null;
    }

    public MethodBinding getRecordComponentAccessor(char[] name) {
        MethodBinding accessor = null;
        if (this.recordComponentAccessors != null) {
            MethodBinding[] methodBindingArray = this.recordComponentAccessors;
            int n = this.recordComponentAccessors.length;
            int n2 = 0;
            while (n2 < n) {
                MethodBinding m = methodBindingArray[n2];
                if (CharOperation.equals(m.selector, name)) {
                    accessor = m;
                    break;
                }
                ++n2;
            }
        }
        return accessor;
    }

    public void computeRecordComponents() {
        if (!this.isRecord() || this.implicitComponentFields != null) {
            return;
        }
        List recordComponentNames = Stream.of(this.components).map(arg -> new String(arg.name)).collect(Collectors.toList());
        ArrayList<FieldBinding> list = new ArrayList<FieldBinding>();
        if (recordComponentNames != null && recordComponentNames.size() > 0 && this.fields != null) {
            for (String rc : recordComponentNames) {
                FieldBinding[] fieldBindingArray = this.fields;
                int n = this.fields.length;
                int n2 = 0;
                while (n2 < n) {
                    FieldBinding f = fieldBindingArray[n2];
                    if (rc.equals(new String(f.name))) {
                        list.add(f);
                    }
                    ++n2;
                }
            }
        }
        this.implicitComponentFields = list.toArray(new FieldBinding[0]);
    }

    public void cleanUp() {
        if (this.environment != null) {
            this.environment.typeSystem.cleanUp(this.id);
        }
        this.scope = null;
    }
}

