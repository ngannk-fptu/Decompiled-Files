/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExportsStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.OpensStatement;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.Receiver;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.RequiresStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrame;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
import org.eclipse.jdt.internal.compiler.codegen.TypeAnnotationCodeStream;
import org.eclipse.jdt.internal.compiler.codegen.VerificationTypeInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolymorphicMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClassFile
implements TypeConstants,
TypeIds {
    private byte[] bytes;
    public CodeStream codeStream;
    public ConstantPool constantPool;
    public int constantPoolOffset;
    public byte[] contents;
    public int contentsOffset;
    protected boolean creatingProblemType;
    public ClassFile enclosingClassFile;
    public byte[] header;
    public int headerOffset;
    public Map<TypeBinding, Boolean> innerClassesBindings;
    public Set<SourceTypeBinding> nestMembers;
    public List<ASTNode> bootstrapMethods = null;
    public int methodCount;
    public int methodCountOffset;
    boolean isShared = false;
    public int produceAttributes;
    public SourceTypeBinding referenceBinding;
    public boolean isNestedType;
    public long targetJDK;
    public List<TypeBinding> missingTypes = null;
    public Set<TypeBinding> visitedTypes;
    public static final int INITIAL_CONTENTS_SIZE = 400;
    public static final int INITIAL_HEADER_SIZE = 1500;
    public static final int INNER_CLASSES_SIZE = 5;
    public static final int NESTED_MEMBER_SIZE = 5;
    public static final String ALTMETAFACTORY_STRING = new String(ConstantPool.ALTMETAFACTORY);
    public static final String METAFACTORY_STRING = new String(ConstantPool.METAFACTORY);
    public static final String BOOTSTRAP_STRING = new String(ConstantPool.BOOTSTRAP);
    public static final String[] BOOTSTRAP_METHODS = new String[]{ALTMETAFACTORY_STRING, METAFACTORY_STRING, BOOTSTRAP_STRING};

    public static void createProblemType(TypeDeclaration typeDeclaration, CompilationResult unitResult) {
        ClassFile.createProblemType(typeDeclaration, null, unitResult);
    }

    private static void createProblemType(TypeDeclaration typeDeclaration, ClassFile parentClassFile, CompilationResult unitResult) {
        int i;
        int i2;
        SourceTypeBinding typeBinding = typeDeclaration.binding;
        ClassFile classFile = ClassFile.getNewInstance(typeBinding);
        classFile.initialize(typeBinding, parentClassFile, true);
        if (typeBinding.hasMemberTypes()) {
            ReferenceBinding[] members = typeBinding.memberTypes;
            i2 = 0;
            int l = members.length;
            while (i2 < l) {
                classFile.recordInnerClasses(members[i2]);
                ++i2;
            }
        }
        if (typeBinding.isNestedType()) {
            classFile.recordInnerClasses(typeBinding);
        }
        TypeVariableBinding[] typeVariables = typeBinding.typeVariables();
        i2 = 0;
        int max = typeVariables.length;
        while (i2 < max) {
            TypeVariableBinding typeVariableBinding = typeVariables[i2];
            if ((typeVariableBinding.tagBits & 0x800L) != 0L) {
                Util.recordNestedType(classFile, typeVariableBinding);
            }
            ++i2;
        }
        FieldBinding[] fields = typeBinding.fields();
        if (fields != null && fields != Binding.NO_FIELDS) {
            classFile.addFieldInfos();
        } else {
            if (classFile.contentsOffset + 2 >= classFile.contents.length) {
                classFile.resizeContents(2);
            }
            classFile.contents[classFile.contentsOffset++] = 0;
            classFile.contents[classFile.contentsOffset++] = 0;
        }
        classFile.setForMethodInfos();
        CategorizedProblem[] problems = unitResult.getErrors();
        if (problems == null) {
            problems = new CategorizedProblem[]{};
        }
        int problemsLength = problems.length;
        CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength];
        System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
        AbstractMethodDeclaration[] methodDecls = typeDeclaration.methods;
        boolean abstractMethodsOnly = false;
        if (methodDecls != null) {
            if (typeBinding.isInterface()) {
                if (typeBinding.scope.compilerOptions().sourceLevel < 0x340000L) {
                    abstractMethodsOnly = true;
                }
                classFile.addProblemClinit(problemsCopy);
            }
            i = 0;
            int length = methodDecls.length;
            while (i < length) {
                AbstractMethodDeclaration methodDecl = methodDecls[i];
                MethodBinding method = methodDecl.binding;
                if (method != null) {
                    if (abstractMethodsOnly) {
                        method.modifiers = 1025;
                    }
                    if (method.isConstructor()) {
                        if (!typeBinding.isInterface()) {
                            classFile.addProblemConstructor(methodDecl, method, problemsCopy);
                        }
                    } else if (method.isAbstract()) {
                        classFile.addAbstractMethod(methodDecl, method);
                    } else {
                        classFile.addProblemMethod(methodDecl, method, problemsCopy);
                    }
                }
                ++i;
            }
            classFile.addDefaultAbstractMethods();
        }
        if (typeDeclaration.memberTypes != null) {
            i = 0;
            int max2 = typeDeclaration.memberTypes.length;
            while (i < max2) {
                TypeDeclaration memberType = typeDeclaration.memberTypes[i];
                if (memberType.binding != null) {
                    ClassFile.createProblemType(memberType, classFile, unitResult);
                }
                ++i;
            }
        }
        classFile.addAttributes();
        unitResult.record(typeBinding.constantPoolName(), classFile);
    }

    public static ClassFile getNewInstance(SourceTypeBinding typeBinding) {
        LookupEnvironment env = typeBinding.scope.environment();
        return env.classFilePool.acquire(typeBinding);
    }

    protected ClassFile() {
    }

    public ClassFile(SourceTypeBinding typeBinding) {
        this.constantPool = new ConstantPool(this);
        CompilerOptions options = typeBinding.scope.compilerOptions();
        this.targetJDK = options.targetJDK;
        this.produceAttributes = options.produceDebugAttributes;
        this.referenceBinding = typeBinding;
        this.isNestedType = typeBinding.isNestedType();
        if (this.targetJDK >= 0x320000L) {
            this.produceAttributes |= 8;
            if (this.targetJDK >= 0x340000L) {
                this.produceAttributes |= 0x20;
                this.codeStream = new TypeAnnotationCodeStream(this);
                if (options.produceMethodParameters) {
                    this.produceAttributes |= 0x40;
                }
            } else {
                this.codeStream = new StackMapFrameCodeStream(this);
            }
        } else if (this.targetJDK == 2949124L) {
            this.targetJDK = 2949123L;
            this.produceAttributes |= 0x10;
            this.codeStream = new StackMapFrameCodeStream(this);
        } else {
            this.codeStream = new CodeStream(this);
        }
        this.initByteArrays(this.referenceBinding.methods().length + this.referenceBinding.fields().length);
    }

    public ClassFile(ModuleBinding moduleBinding, CompilerOptions options) {
        this.constantPool = new ConstantPool(this);
        this.targetJDK = options.targetJDK;
        this.produceAttributes = 1;
        this.isNestedType = false;
        this.codeStream = new StackMapFrameCodeStream(this);
        this.initByteArrays(0);
    }

    public void addAbstractMethod(AbstractMethodDeclaration method, MethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        this.completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
    }

    public void addAttributes() {
        int numberOfInnerClasses;
        Annotation[] annotations;
        TypeDeclaration typeDeclaration;
        char[] genericSignature;
        this.contents[this.methodCountOffset++] = (byte)(this.methodCount >> 8);
        this.contents[this.methodCountOffset] = (byte)this.methodCount;
        int attributesNumber = 0;
        int attributeOffset = this.contentsOffset;
        this.contentsOffset += 2;
        if ((this.produceAttributes & 1) != 0) {
            String fullFileName = new String(this.referenceBinding.scope.referenceCompilationUnit().getFileName());
            int lastIndex = (fullFileName = fullFileName.replace('\\', '/')).lastIndexOf(47);
            if (lastIndex != -1) {
                fullFileName = fullFileName.substring(lastIndex + 1, fullFileName.length());
            }
            attributesNumber += this.generateSourceAttribute(fullFileName);
        }
        if (this.referenceBinding.isDeprecated()) {
            attributesNumber += this.generateDeprecatedAttribute();
        }
        if ((genericSignature = this.referenceBinding.genericSignature()) != null) {
            attributesNumber += this.generateSignatureAttribute(genericSignature);
        }
        if (this.targetJDK >= 0x310000L && this.referenceBinding.isNestedType() && !this.referenceBinding.isMemberType()) {
            attributesNumber += this.generateEnclosingMethodAttribute();
        }
        if (this.targetJDK >= 0x300000L && (typeDeclaration = this.referenceBinding.scope.referenceContext) != null && (annotations = typeDeclaration.annotations) != null) {
            long targetMask = typeDeclaration.isPackageInfo() ? 0x80000000000L : (this.referenceBinding.isAnnotationType() ? 0x41000000000L : 0x20001000000000L);
            attributesNumber += this.generateRuntimeAnnotations(annotations, targetMask);
        }
        if (this.referenceBinding.isHierarchyInconsistent()) {
            ReferenceBinding superclass = this.referenceBinding.superclass;
            if (superclass != null) {
                this.missingTypes = superclass.collectMissingTypes(this.missingTypes);
            }
            ReferenceBinding[] superInterfaces = this.referenceBinding.superInterfaces();
            int i = 0;
            int max = superInterfaces.length;
            while (i < max) {
                this.missingTypes = superInterfaces[i].collectMissingTypes(this.missingTypes);
                ++i;
            }
            attributesNumber += this.generateHierarchyInconsistentAttribute();
        }
        if (this.bootstrapMethods != null && !this.bootstrapMethods.isEmpty()) {
            attributesNumber += this.generateBootstrapMethods(this.bootstrapMethods);
        }
        if (this.targetJDK >= 0x3C0000L) {
            attributesNumber += this.generatePermittedTypeAttributes();
        }
        int n = numberOfInnerClasses = this.innerClassesBindings == null ? 0 : this.innerClassesBindings.size();
        if (numberOfInnerClasses != 0) {
            ReferenceBinding[] innerClasses = new ReferenceBinding[numberOfInnerClasses];
            this.innerClassesBindings.keySet().toArray(innerClasses);
            Arrays.sort(innerClasses, new Comparator<ReferenceBinding>(){

                @Override
                public int compare(ReferenceBinding o1, ReferenceBinding o2) {
                    Boolean onBottom1 = ClassFile.this.innerClassesBindings.get(o1);
                    Boolean onBottom2 = ClassFile.this.innerClassesBindings.get(o2);
                    if (onBottom1.booleanValue()) {
                        if (!onBottom2.booleanValue()) {
                            return 1;
                        }
                    } else if (onBottom2.booleanValue()) {
                        return -1;
                    }
                    return CharOperation.compareTo(o1.constantPoolName(), o2.constantPoolName());
                }
            });
            attributesNumber += this.generateInnerClassAttribute(numberOfInnerClasses, innerClasses);
        }
        if (this.missingTypes != null) {
            this.generateMissingTypesAttribute();
            ++attributesNumber;
        }
        attributesNumber += this.generateTypeAnnotationAttributeForTypeDeclaration();
        if (this.targetJDK >= 0x370000L) {
            attributesNumber += this.generateNestAttributes();
        }
        if (this.targetJDK >= 0x3A0000L) {
            attributesNumber += this.generateRecordAttributes();
        }
        if (attributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[attributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[attributeOffset] = (byte)attributesNumber;
        this.header = this.constantPool.poolContent;
        this.headerOffset = this.constantPool.currentOffset;
        int constantPoolCount = this.constantPool.currentIndex;
        this.header[this.constantPoolOffset++] = (byte)(constantPoolCount >> 8);
        this.header[this.constantPoolOffset] = (byte)constantPoolCount;
    }

    public void addModuleAttributes(ModuleBinding module, Annotation[] annotations, CompilationUnitDeclaration cud) {
        char[][] packageNames;
        char[] mainClass;
        int attributesNumber = 0;
        int attributeOffset = this.contentsOffset;
        this.contentsOffset += 2;
        if ((this.produceAttributes & 1) != 0) {
            String fullFileName = new String(cud.getFileName());
            int lastIndex = (fullFileName = fullFileName.replace('\\', '/')).lastIndexOf(47);
            if (lastIndex != -1) {
                fullFileName = fullFileName.substring(lastIndex + 1, fullFileName.length());
            }
            attributesNumber += this.generateSourceAttribute(fullFileName);
        }
        attributesNumber += this.generateModuleAttribute(cud.moduleDeclaration);
        if (annotations != null) {
            long targetMask = 0x2000000000000000L;
            attributesNumber += this.generateRuntimeAnnotations(annotations, targetMask);
        }
        if ((mainClass = cud.moduleDeclaration.binding.mainClassName) != null) {
            attributesNumber += this.generateModuleMainClassAttribute(CharOperation.replaceOnCopy(mainClass, '.', '/'));
        }
        if ((packageNames = cud.moduleDeclaration.binding.getPackageNamesForClassFile()) != null) {
            attributesNumber += this.generateModulePackagesAttribute(packageNames);
        }
        if (attributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[attributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[attributeOffset] = (byte)attributesNumber;
        this.header = this.constantPool.poolContent;
        this.headerOffset = this.constantPool.currentOffset;
        int constantPoolCount = this.constantPool.currentIndex;
        this.header[this.constantPoolOffset++] = (byte)(constantPoolCount >> 8);
        this.header[this.constantPoolOffset] = (byte)constantPoolCount;
    }

    public void addDefaultAbstractMethods() {
        MethodBinding[] defaultAbstractMethods = this.referenceBinding.getDefaultAbstractMethods();
        int i = 0;
        int max = defaultAbstractMethods.length;
        while (i < max) {
            MethodBinding methodBinding = defaultAbstractMethods[i];
            this.generateMethodInfoHeader(methodBinding);
            int methodAttributeOffset = this.contentsOffset;
            int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
            this.completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
            ++i;
        }
    }

    private int addFieldAttributes(FieldBinding fieldBinding, int fieldAttributeOffset) {
        FieldDeclaration fieldDeclaration;
        char[] genericSignature;
        int attributesNumber = 0;
        Constant fieldConstant = fieldBinding.constant();
        if (fieldConstant != Constant.NotAConstant) {
            attributesNumber += this.generateConstantValueAttribute(fieldConstant, fieldBinding, fieldAttributeOffset);
        }
        if (this.targetJDK < 0x310000L && fieldBinding.isSynthetic()) {
            attributesNumber += this.generateSyntheticAttribute();
        }
        if (fieldBinding.isDeprecated()) {
            attributesNumber += this.generateDeprecatedAttribute();
        }
        if ((genericSignature = fieldBinding.genericSignature()) != null) {
            attributesNumber += this.generateSignatureAttribute(genericSignature);
        }
        if (this.targetJDK >= 0x300000L && (fieldDeclaration = fieldBinding.sourceField()) != null) {
            try {
                Annotation[] annotations;
                if (fieldDeclaration.isARecordComponent) {
                    long rcMask = 0x20002000000000L;
                    RecordComponent comp = this.getRecordComponent(fieldBinding.declaringClass, fieldBinding.name);
                    if (comp != null) {
                        fieldDeclaration.annotations = ASTNode.getRelevantAnnotations(comp.annotations, rcMask, null);
                    }
                }
                if ((annotations = fieldDeclaration.annotations) != null) {
                    attributesNumber += this.generateRuntimeAnnotations(annotations, 0x2000000000L);
                }
                if ((this.produceAttributes & 0x20) != 0) {
                    TypeReference fieldType;
                    ArrayList<AnnotationContext> allTypeAnnotationContexts = new ArrayList<AnnotationContext>();
                    if (annotations != null && (fieldDeclaration.bits & 0x100000) != 0) {
                        fieldDeclaration.getAllAnnotationContexts(19, allTypeAnnotationContexts);
                    }
                    if ((fieldType = fieldDeclaration.type) != null && (fieldType.bits & 0x100000) != 0) {
                        fieldType.getAllAnnotationContexts(19, allTypeAnnotationContexts);
                    }
                    int size = allTypeAnnotationContexts.size();
                    attributesNumber = this.completeRuntimeTypeAnnotations(attributesNumber, null, node -> size > 0, () -> allTypeAnnotationContexts);
                }
            }
            finally {
                if (fieldDeclaration.isARecordComponent) {
                    fieldDeclaration.annotations = null;
                }
            }
        }
        if ((fieldBinding.tagBits & 0x80L) != 0L) {
            this.missingTypes = fieldBinding.type.collectMissingTypes(this.missingTypes);
        }
        return attributesNumber;
    }

    private RecordComponent getRecordComponent(ReferenceBinding declaringClass, char[] name) {
        SourceTypeBinding sourceTypeBinding;
        RecordComponentBinding rcb;
        if (declaringClass instanceof SourceTypeBinding && (rcb = (sourceTypeBinding = (SourceTypeBinding)declaringClass).getRecordComponent(name)) != null) {
            RecordComponent recordComponent = rcb.sourceRecordComponent();
            return recordComponent;
        }
        return null;
    }

    private int addComponentAttributes(RecordComponentBinding recordComponentBinding, int componetAttributeOffset) {
        RecordComponent recordComponent;
        int attributesNumber = 0;
        char[] genericSignature = recordComponentBinding.genericSignature();
        if (genericSignature != null) {
            attributesNumber += this.generateSignatureAttribute(genericSignature);
        }
        if ((recordComponent = recordComponentBinding.sourceRecordComponent()) != null) {
            Annotation[] annotations = recordComponent.annotations;
            if (annotations != null) {
                attributesNumber += this.generateRuntimeAnnotations(annotations, 0x40000000L);
            }
            if ((this.produceAttributes & 0x20) != 0) {
                TypeReference recordComponentType;
                ArrayList<AnnotationContext> allTypeAnnotationContexts = new ArrayList<AnnotationContext>();
                if (annotations != null && (recordComponent.bits & 0x100000) != 0) {
                    recordComponent.getAllAnnotationContexts(19, allTypeAnnotationContexts);
                }
                if ((recordComponentType = recordComponent.type) != null && (recordComponentType.bits & 0x100000) != 0) {
                    recordComponentType.getAllAnnotationContexts(19, allTypeAnnotationContexts);
                }
                int size = allTypeAnnotationContexts.size();
                attributesNumber = this.completeRuntimeTypeAnnotations(attributesNumber, null, node -> size > 0, () -> allTypeAnnotationContexts);
            }
        }
        if ((recordComponentBinding.tagBits & 0x80L) != 0L) {
            this.missingTypes = recordComponentBinding.type.collectMissingTypes(this.missingTypes);
        }
        return attributesNumber;
    }

    private void addComponentInfo(RecordComponentBinding recordComponentBinding) {
        if (this.contentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        int nameIndex = this.constantPool.literalIndex(recordComponentBinding.name);
        this.contents[this.contentsOffset++] = (byte)(nameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)nameIndex;
        int descriptorIndex = this.constantPool.literalIndex(recordComponentBinding.type);
        this.contents[this.contentsOffset++] = (byte)(descriptorIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)descriptorIndex;
        int componentAttributeOffset = this.contentsOffset;
        int attributeNumber = 0;
        this.contentsOffset += 2;
        attributeNumber += this.addComponentAttributes(recordComponentBinding, componentAttributeOffset);
        if (this.contentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[componentAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[componentAttributeOffset] = (byte)attributeNumber;
    }

    private void addFieldInfo(FieldBinding fieldBinding) {
        if (this.contentsOffset + 8 >= this.contents.length) {
            this.resizeContents(8);
        }
        int accessFlags = fieldBinding.getAccessFlags();
        if (this.targetJDK < 0x310000L) {
            accessFlags &= 0xFFFFEFFF;
        }
        this.contents[this.contentsOffset++] = (byte)(accessFlags >> 8);
        this.contents[this.contentsOffset++] = (byte)accessFlags;
        int nameIndex = this.constantPool.literalIndex(fieldBinding.name);
        this.contents[this.contentsOffset++] = (byte)(nameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)nameIndex;
        int descriptorIndex = this.constantPool.literalIndex(fieldBinding.type);
        this.contents[this.contentsOffset++] = (byte)(descriptorIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)descriptorIndex;
        int fieldAttributeOffset = this.contentsOffset;
        int attributeNumber = 0;
        this.contentsOffset += 2;
        attributeNumber += this.addFieldAttributes(fieldBinding, fieldAttributeOffset);
        if (this.contentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[fieldAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[fieldAttributeOffset] = (byte)attributeNumber;
    }

    public void addFieldInfos() {
        SourceTypeBinding currentBinding = this.referenceBinding;
        FieldBinding[] syntheticFields = currentBinding.syntheticFields();
        int fieldCount = currentBinding.fieldCount() + (syntheticFields == null ? 0 : syntheticFields.length);
        if (fieldCount > 65535) {
            this.referenceBinding.scope.problemReporter().tooManyFields(this.referenceBinding.scope.referenceType());
        }
        if (this.contentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[this.contentsOffset++] = (byte)(fieldCount >> 8);
        this.contents[this.contentsOffset++] = (byte)fieldCount;
        FieldDeclaration[] fieldDecls = currentBinding.scope.referenceContext.fields;
        int i = 0;
        int max = fieldDecls == null ? 0 : fieldDecls.length;
        while (i < max) {
            FieldDeclaration fieldDecl = fieldDecls[i];
            if (fieldDecl.binding != null) {
                this.addFieldInfo(fieldDecl.binding);
            }
            ++i;
        }
        if (syntheticFields != null) {
            i = 0;
            max = syntheticFields.length;
            while (i < max) {
                this.addFieldInfo(syntheticFields[i]);
                ++i;
            }
        }
    }

    private void addMissingAbstractProblemMethod(MethodDeclaration methodDeclaration, MethodBinding methodBinding, CategorizedProblem problem, CompilationResult compilationResult) {
        this.generateMethodInfoHeader(methodBinding, methodBinding.modifiers & 0xFFFFF2FF);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        ++attributeNumber;
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        StringBuffer buffer = new StringBuffer(25);
        buffer.append("\t" + problem.getMessage() + "\n");
        buffer.insert(0, Messages.compilation_unresolvedProblem);
        String problemString = buffer.toString();
        this.codeStream.init(this);
        this.codeStream.preserveUnusedLocals = true;
        this.codeStream.initializeMaxLocals(methodBinding);
        this.codeStream.generateCodeAttributeForProblemMethod(problemString);
        this.completeCodeAttributeForMissingAbstractProblemMethod(methodBinding, codeAttributeOffset, compilationResult.getLineSeparatorPositions(), problem.getSourceLineNumber());
        this.completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
    }

    public void addProblemClinit(CategorizedProblem[] problems) {
        this.generateMethodInfoHeaderForClinit();
        this.contentsOffset -= 2;
        int attributeOffset = this.contentsOffset;
        this.contentsOffset += 2;
        int attributeNumber = 0;
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.resetForProblemClinit(this);
        String problemString = "";
        int problemLine = 0;
        if (problems != null) {
            int max = problems.length;
            StringBuffer buffer = new StringBuffer(25);
            int count = 0;
            int i = 0;
            while (i < max) {
                CategorizedProblem problem = problems[i];
                if (problem != null && problem.isError()) {
                    buffer.append("\t" + problem.getMessage() + "\n");
                    ++count;
                    if (problemLine == 0) {
                        problemLine = problem.getSourceLineNumber();
                    }
                    problems[i] = null;
                }
                ++i;
            }
            if (count > 1) {
                buffer.insert(0, Messages.compilation_unresolvedProblems);
            } else {
                buffer.insert(0, Messages.compilation_unresolvedProblem);
            }
            problemString = buffer.toString();
        }
        this.codeStream.generateCodeAttributeForProblemMethod(problemString);
        ++attributeNumber;
        this.completeCodeAttributeForClinit(codeAttributeOffset, problemLine, null);
        if (this.contentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[attributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[attributeOffset] = (byte)attributeNumber;
    }

    public void addProblemConstructor(AbstractMethodDeclaration method, MethodBinding methodBinding, CategorizedProblem[] problems) {
        if (methodBinding.declaringClass.isInterface()) {
            method.abort(8, null);
        }
        this.generateMethodInfoHeader(methodBinding, methodBinding.modifiers & 0xFFFFF2FF);
        int methodAttributeOffset = this.contentsOffset;
        int attributesNumber = this.generateMethodInfoAttributes(methodBinding);
        ++attributesNumber;
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.reset(method, this);
        String problemString = "";
        int problemLine = 0;
        if (problems != null) {
            int max = problems.length;
            StringBuffer buffer = new StringBuffer(25);
            int count = 0;
            int i = 0;
            while (i < max) {
                CategorizedProblem problem = problems[i];
                if (problem != null && problem.isError()) {
                    buffer.append("\t" + problem.getMessage() + "\n");
                    ++count;
                    if (problemLine == 0) {
                        problemLine = problem.getSourceLineNumber();
                    }
                }
                ++i;
            }
            if (count > 1) {
                buffer.insert(0, Messages.compilation_unresolvedProblems);
            } else {
                buffer.insert(0, Messages.compilation_unresolvedProblem);
            }
            problemString = buffer.toString();
        }
        this.codeStream.generateCodeAttributeForProblemMethod(problemString);
        this.completeCodeAttributeForProblemMethod(method, methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions(), problemLine);
        this.completeMethodInfo(methodBinding, methodAttributeOffset, attributesNumber);
    }

    public void addProblemConstructor(AbstractMethodDeclaration method, MethodBinding methodBinding, CategorizedProblem[] problems, int savedOffset) {
        this.contentsOffset = savedOffset;
        --this.methodCount;
        this.addProblemConstructor(method, methodBinding, problems);
    }

    public void addProblemMethod(AbstractMethodDeclaration method, MethodBinding methodBinding, CategorizedProblem[] problems) {
        if (methodBinding.isAbstract() && methodBinding.declaringClass.isInterface()) {
            method.abort(8, null);
        }
        this.generateMethodInfoHeader(methodBinding, methodBinding.modifiers & 0xFFFFF2FF);
        int methodAttributeOffset = this.contentsOffset;
        int attributesNumber = this.generateMethodInfoAttributes(methodBinding);
        ++attributesNumber;
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.reset(method, this);
        String problemString = "";
        int problemLine = 0;
        if (problems != null) {
            int max = problems.length;
            StringBuffer buffer = new StringBuffer(25);
            int count = 0;
            int i = 0;
            while (i < max) {
                CategorizedProblem problem = problems[i];
                if (problem != null && problem.isError() && problem.getSourceStart() >= method.declarationSourceStart && problem.getSourceEnd() <= method.declarationSourceEnd) {
                    buffer.append("\t" + problem.getMessage() + "\n");
                    ++count;
                    if (problemLine == 0) {
                        problemLine = problem.getSourceLineNumber();
                    }
                    problems[i] = null;
                }
                ++i;
            }
            if (count > 1) {
                buffer.insert(0, Messages.compilation_unresolvedProblems);
            } else {
                buffer.insert(0, Messages.compilation_unresolvedProblem);
            }
            problemString = buffer.toString();
        }
        this.codeStream.generateCodeAttributeForProblemMethod(problemString);
        this.completeCodeAttributeForProblemMethod(method, methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions(), problemLine);
        this.completeMethodInfo(methodBinding, methodAttributeOffset, attributesNumber);
    }

    public void addProblemMethod(AbstractMethodDeclaration method, MethodBinding methodBinding, CategorizedProblem[] problems, int savedOffset) {
        this.contentsOffset = savedOffset;
        --this.methodCount;
        this.addProblemMethod(method, methodBinding, problems);
    }

    public void addSpecialMethods(TypeDeclaration typeDecl) {
        this.generateMissingAbstractMethods(this.referenceBinding.scope.referenceType().missingAbstractMethods, this.referenceBinding.scope.referenceCompilationUnit().compilationResult);
        MethodBinding[] defaultAbstractMethods = this.referenceBinding.getDefaultAbstractMethods();
        int i = 0;
        int max = defaultAbstractMethods.length;
        while (i < max) {
            MethodBinding methodBinding = defaultAbstractMethods[i];
            this.generateMethodInfoHeader(methodBinding);
            int methodAttributeOffset = this.contentsOffset;
            int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
            this.completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
            ++i;
        }
        int emittedSyntheticsCount = 0;
        SyntheticMethodBinding deserializeLambdaMethod = null;
        boolean continueScanningSynthetics = true;
        while (continueScanningSynthetics) {
            int currentSyntheticsCount;
            continueScanningSynthetics = false;
            SyntheticMethodBinding[] syntheticMethods = this.referenceBinding.syntheticMethods();
            int n = currentSyntheticsCount = syntheticMethods == null ? 0 : syntheticMethods.length;
            if (emittedSyntheticsCount == currentSyntheticsCount) continue;
            int i2 = emittedSyntheticsCount;
            int max2 = currentSyntheticsCount;
            while (i2 < max2) {
                SyntheticMethodBinding syntheticMethod = syntheticMethods[i2];
                switch (syntheticMethod.purpose) {
                    case 1: 
                    case 3: {
                        this.addSyntheticFieldReadAccessMethod(syntheticMethod);
                        break;
                    }
                    case 2: 
                    case 4: {
                        this.addSyntheticFieldWriteAccessMethod(syntheticMethod);
                        break;
                    }
                    case 5: 
                    case 7: 
                    case 8: {
                        this.addSyntheticMethodAccessMethod(syntheticMethod);
                        break;
                    }
                    case 6: {
                        this.addSyntheticConstructorAccessMethod(syntheticMethod);
                        break;
                    }
                    case 9: {
                        this.addSyntheticEnumValuesMethod(syntheticMethod);
                        break;
                    }
                    case 10: {
                        this.addSyntheticEnumValueOfMethod(syntheticMethod);
                        break;
                    }
                    case 11: {
                        this.addSyntheticSwitchTable(syntheticMethod);
                        break;
                    }
                    case 12: {
                        this.addSyntheticEnumInitializationMethod(syntheticMethod);
                        break;
                    }
                    case 13: {
                        syntheticMethod.lambda.generateCode(this.referenceBinding.scope, this);
                        continueScanningSynthetics = true;
                        break;
                    }
                    case 14: {
                        this.addSyntheticArrayConstructor(syntheticMethod);
                        break;
                    }
                    case 15: {
                        this.addSyntheticArrayClone(syntheticMethod);
                        break;
                    }
                    case 16: {
                        this.addSyntheticFactoryMethod(syntheticMethod);
                        break;
                    }
                    case 17: {
                        deserializeLambdaMethod = syntheticMethod;
                        break;
                    }
                    case 18: {
                        break;
                    }
                    case 22: {
                        this.addSyntheticRecordCanonicalConstructor(typeDecl, syntheticMethod);
                        break;
                    }
                    case 19: 
                    case 20: 
                    case 21: {
                        this.addSyntheticRecordOverrideMethods(typeDecl, syntheticMethod, syntheticMethod.purpose);
                    }
                }
                ++i2;
            }
            emittedSyntheticsCount = currentSyntheticsCount;
        }
        if (deserializeLambdaMethod != null) {
            int problemResetPC = 0;
            this.codeStream.wideMode = false;
            boolean restart = false;
            do {
                try {
                    problemResetPC = this.contentsOffset;
                    this.addSyntheticDeserializeLambda(deserializeLambdaMethod, this.referenceBinding.syntheticMethods());
                    restart = false;
                }
                catch (AbortMethod e) {
                    if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
                        this.contentsOffset = problemResetPC;
                        --this.methodCount;
                        this.codeStream.resetInWideMode();
                        restart = true;
                        continue;
                    }
                    throw new AbortType(this.referenceBinding.scope.referenceContext.compilationResult, e.problem);
                }
            } while (restart);
        }
    }

    private void addSyntheticRecordCanonicalConstructor(TypeDeclaration typeDecl, SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForRecordCanonicalConstructor(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    private void addSyntheticRecordOverrideMethods(TypeDeclaration typeDecl, SyntheticMethodBinding methodBinding, int purpose) {
        if (this.bootstrapMethods == null) {
            this.bootstrapMethods = new ArrayList<ASTNode>(3);
        }
        if (!this.bootstrapMethods.contains(typeDecl)) {
            this.bootstrapMethods.add(typeDecl);
        }
        int index = this.bootstrapMethods.indexOf(typeDecl);
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        switch (purpose) {
            case 22: {
                this.codeStream.generateSyntheticBodyForRecordCanonicalConstructor(methodBinding);
                break;
            }
            case 21: {
                this.codeStream.generateSyntheticBodyForRecordEquals(methodBinding, index);
                break;
            }
            case 20: {
                this.codeStream.generateSyntheticBodyForRecordHashCode(methodBinding, index);
                break;
            }
            case 19: {
                this.codeStream.generateSyntheticBodyForRecordToString(methodBinding, index);
                break;
            }
        }
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticArrayConstructor(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForArrayConstructor(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticArrayClone(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForArrayClone(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticFactoryMethod(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForFactoryMethod(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticConstructorAccessMethod(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForConstructorAccess(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticEnumValueOfMethod(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForEnumValueOf(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        if ((this.produceAttributes & 0x40) != 0) {
            attributeNumber += this.generateMethodParameters(methodBinding);
        }
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticEnumValuesMethod(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForEnumValues(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticEnumInitializationMethod(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForEnumInitializationMethod(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticFieldReadAccessMethod(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForFieldReadAccess(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticFieldWriteAccessMethod(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForFieldWriteAccess(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticMethodAccessMethod(SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForMethodAccess(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(++attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void addSyntheticSwitchTable(SyntheticMethodBinding methodBinding) {
        SwitchStatement switchStatement;
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForSwitchTable(methodBinding);
        int code_length = this.codeStream.position;
        if (code_length > 65535 && (switchStatement = methodBinding.switchStatement) != null) {
            switchStatement.scope.problemReporter().bytecodeExceeds64KLimit(switchStatement);
        }
        this.completeCodeAttributeForSyntheticMethod(true, methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions(), ((SourceTypeBinding)methodBinding.declaringClass).scope);
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void completeCodeAttribute(int codeAttributeOffset, MethodScope scope) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        int code_length = this.codeStream.position;
        if (code_length > 65535) {
            if (this.codeStream.methodDeclaration != null) {
                this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(this.codeStream.methodDeclaration);
            } else {
                this.codeStream.lambdaExpression.scope.problemReporter().bytecodeExceeds64KLimit(this.codeStream.lambdaExpression);
            }
        }
        if (localContentsOffset + 20 >= this.contents.length) {
            this.resizeContents(20);
        }
        int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        boolean addStackMaps = (this.produceAttributes & 8) != 0;
        ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
        int exceptionHandlersCount = 0;
        int i = 0;
        int length = this.codeStream.exceptionLabelsCounter;
        while (i < length) {
            exceptionHandlersCount += this.codeStream.exceptionLabels[i].getCount() / 2;
            ++i;
        }
        int exSize = exceptionHandlersCount * 8 + 2;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        this.contents[localContentsOffset++] = (byte)(exceptionHandlersCount >> 8);
        this.contents[localContentsOffset++] = (byte)exceptionHandlersCount;
        int i2 = 0;
        int max = this.codeStream.exceptionLabelsCounter;
        while (i2 < max) {
            ExceptionLabel exceptionLabel = exceptionLabels[i2];
            if (exceptionLabel != null) {
                int iRange = 0;
                int maxRange = exceptionLabel.getCount();
                if ((maxRange & 1) != 0) {
                    if (this.codeStream.methodDeclaration != null) {
                        this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.methodDeclaration.selector)), this.codeStream.methodDeclaration);
                    } else {
                        this.codeStream.lambdaExpression.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.lambdaExpression.binding.selector)), this.codeStream.lambdaExpression);
                    }
                }
                while (iRange < maxRange) {
                    int start = exceptionLabel.ranges[iRange++];
                    this.contents[localContentsOffset++] = (byte)(start >> 8);
                    this.contents[localContentsOffset++] = (byte)start;
                    int end = exceptionLabel.ranges[iRange++];
                    this.contents[localContentsOffset++] = (byte)(end >> 8);
                    this.contents[localContentsOffset++] = (byte)end;
                    int handlerPC = exceptionLabel.position;
                    if (addStackMaps) {
                        StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
                        stackMapFrameCodeStream.addFramePosition(handlerPC);
                    }
                    this.contents[localContentsOffset++] = (byte)(handlerPC >> 8);
                    this.contents[localContentsOffset++] = (byte)handlerPC;
                    if (exceptionLabel.exceptionType == null) {
                        this.contents[localContentsOffset++] = 0;
                        this.contents[localContentsOffset++] = 0;
                        continue;
                    }
                    int nameIndex = exceptionLabel.exceptionType == TypeBinding.NULL ? this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName) : this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
                    this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                    this.contents[localContentsOffset++] = (byte)nameIndex;
                }
            }
            ++i2;
        }
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        if ((localContentsOffset += 2) + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 2) != 0) {
            attributesNumber += this.generateLineNumberAttribute();
        }
        if ((this.produceAttributes & 4) != 0) {
            boolean methodDeclarationIsStatic = this.codeStream.methodDeclaration != null ? this.codeStream.methodDeclaration.isStatic() : this.codeStream.lambdaExpression.binding.isStatic();
            attributesNumber += this.generateLocalVariableTableAttribute(code_length, methodDeclarationIsStatic, false);
        }
        if (addStackMaps) {
            attributesNumber += this.generateStackMapTableAttribute(this.codeStream.methodDeclaration != null ? this.codeStream.methodDeclaration.binding : this.codeStream.lambdaExpression.binding, code_length, codeAttributeOffset, max_locals, false, scope);
        }
        if ((this.produceAttributes & 0x10) != 0) {
            attributesNumber += this.generateStackMapAttribute(this.codeStream.methodDeclaration != null ? this.codeStream.methodDeclaration.binding : this.codeStream.lambdaExpression.binding, code_length, codeAttributeOffset, max_locals, false, scope);
        }
        if ((this.produceAttributes & 0x20) != 0) {
            attributesNumber += this.generateTypeAnnotationsOnCodeAttribute();
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }

    public int generateTypeAnnotationsOnCodeAttribute() {
        int attributesNumber = 0;
        List<AnnotationContext> allTypeAnnotationContexts = ((TypeAnnotationCodeStream)this.codeStream).allTypeAnnotationContexts;
        int i = 0;
        int max = this.codeStream.allLocalsCounter;
        while (i < max) {
            LocalDeclaration declaration;
            LocalVariableBinding localVariable = this.codeStream.locals[i];
            if (!(localVariable.isCatchParameter() || (declaration = localVariable.declaration) == null || declaration.isArgument() && (declaration.bits & 0x20000000) == 0 || localVariable.initializationCount == 0 || (declaration.bits & 0x100000) == 0)) {
                int targetType = (localVariable.tagBits & 0x2000L) == 0L ? 64 : 65;
                declaration.getAllAnnotationContexts(targetType, localVariable, allTypeAnnotationContexts);
            }
            ++i;
        }
        ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
        int i2 = 0;
        int max2 = this.codeStream.exceptionLabelsCounter;
        while (i2 < max2) {
            ExceptionLabel exceptionLabel = exceptionLabels[i2];
            if (exceptionLabel.exceptionTypeReference != null && (exceptionLabel.exceptionTypeReference.bits & 0x100000) != 0) {
                exceptionLabel.exceptionTypeReference.getAllAnnotationContexts(66, i2, allTypeAnnotationContexts, exceptionLabel.se7Annotations);
            }
            ++i2;
        }
        int size = allTypeAnnotationContexts.size();
        attributesNumber = this.completeRuntimeTypeAnnotations(attributesNumber, null, node -> size > 0, () -> allTypeAnnotationContexts);
        return attributesNumber;
    }

    public void completeCodeAttributeForClinit(int codeAttributeOffset, Scope scope) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        int code_length = this.codeStream.position;
        if (code_length > 65535) {
            this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(this.codeStream.methodDeclaration.scope.referenceType());
        }
        if (localContentsOffset + 20 >= this.contents.length) {
            this.resizeContents(20);
        }
        int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        boolean addStackMaps = (this.produceAttributes & 8) != 0;
        ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
        int exceptionHandlersCount = 0;
        int i = 0;
        int length = this.codeStream.exceptionLabelsCounter;
        while (i < length) {
            exceptionHandlersCount += this.codeStream.exceptionLabels[i].getCount() / 2;
            ++i;
        }
        int exSize = exceptionHandlersCount * 8 + 2;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        this.contents[localContentsOffset++] = (byte)(exceptionHandlersCount >> 8);
        this.contents[localContentsOffset++] = (byte)exceptionHandlersCount;
        int i2 = 0;
        int max = this.codeStream.exceptionLabelsCounter;
        while (i2 < max) {
            ExceptionLabel exceptionLabel = exceptionLabels[i2];
            if (exceptionLabel != null) {
                int iRange = 0;
                int maxRange = exceptionLabel.getCount();
                if ((maxRange & 1) != 0) {
                    this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.methodDeclaration.selector)), this.codeStream.methodDeclaration);
                }
                while (iRange < maxRange) {
                    int start = exceptionLabel.ranges[iRange++];
                    this.contents[localContentsOffset++] = (byte)(start >> 8);
                    this.contents[localContentsOffset++] = (byte)start;
                    int end = exceptionLabel.ranges[iRange++];
                    this.contents[localContentsOffset++] = (byte)(end >> 8);
                    this.contents[localContentsOffset++] = (byte)end;
                    int handlerPC = exceptionLabel.position;
                    this.contents[localContentsOffset++] = (byte)(handlerPC >> 8);
                    this.contents[localContentsOffset++] = (byte)handlerPC;
                    if (addStackMaps) {
                        StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
                        stackMapFrameCodeStream.addFramePosition(handlerPC);
                    }
                    if (exceptionLabel.exceptionType == null) {
                        this.contents[localContentsOffset++] = 0;
                        this.contents[localContentsOffset++] = 0;
                        continue;
                    }
                    int nameIndex = exceptionLabel.exceptionType == TypeBinding.NULL ? this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName) : this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
                    this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                    this.contents[localContentsOffset++] = (byte)nameIndex;
                }
            }
            ++i2;
        }
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        if ((localContentsOffset += 2) + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 2) != 0) {
            attributesNumber += this.generateLineNumberAttribute();
        }
        if ((this.produceAttributes & 4) != 0) {
            attributesNumber += this.generateLocalVariableTableAttribute(code_length, true, false);
        }
        if ((this.produceAttributes & 8) != 0) {
            attributesNumber += this.generateStackMapTableAttribute(null, code_length, codeAttributeOffset, max_locals, true, scope);
        }
        if ((this.produceAttributes & 0x10) != 0) {
            attributesNumber += this.generateStackMapAttribute(null, code_length, codeAttributeOffset, max_locals, true, scope);
        }
        if ((this.produceAttributes & 0x20) != 0) {
            attributesNumber += this.generateTypeAnnotationsOnCodeAttribute();
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }

    public void completeCodeAttributeForClinit(int codeAttributeOffset, int problemLine, MethodScope scope) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        int code_length = this.codeStream.position;
        if (code_length > 65535) {
            this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(this.codeStream.methodDeclaration.scope.referenceType());
        }
        if (localContentsOffset + 20 >= this.contents.length) {
            this.resizeContents(20);
        }
        int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        if ((localContentsOffset += 2) + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 2) != 0) {
            attributesNumber += this.generateLineNumberAttribute(problemLine);
        }
        localContentsOffset = this.contentsOffset;
        if ((this.produceAttributes & 4) != 0) {
            int localVariableNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
            if (localContentsOffset + 8 >= this.contents.length) {
                this.resizeContents(8);
            }
            this.contents[localContentsOffset++] = (byte)(localVariableNameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)localVariableNameIndex;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 2;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            ++attributesNumber;
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 8) != 0) {
            attributesNumber += this.generateStackMapTableAttribute(null, code_length, codeAttributeOffset, max_locals, true, scope);
        }
        if ((this.produceAttributes & 0x10) != 0) {
            attributesNumber += this.generateStackMapAttribute(null, code_length, codeAttributeOffset, max_locals, true, scope);
        }
        if ((this.produceAttributes & 0x20) != 0) {
            attributesNumber += this.generateTypeAnnotationsOnCodeAttribute();
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }

    public void completeCodeAttributeForMissingAbstractProblemMethod(MethodBinding binding, int codeAttributeOffset, int[] startLineIndexes, int problemLine) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        int code_length = this.codeStream.position;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        if (localContentsOffset + 50 >= this.contents.length) {
            this.resizeContents(50);
        }
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        if ((localContentsOffset += 2) + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 2) != 0) {
            if (problemLine == 0) {
                problemLine = Util.getLineNumber(binding.sourceStart(), startLineIndexes, 0, startLineIndexes.length - 1);
            }
            attributesNumber += this.generateLineNumberAttribute(problemLine);
        }
        if ((this.produceAttributes & 8) != 0) {
            attributesNumber += this.generateStackMapTableAttribute(binding, code_length, codeAttributeOffset, max_locals, false, null);
        }
        if ((this.produceAttributes & 0x10) != 0) {
            attributesNumber += this.generateStackMapAttribute(binding, code_length, codeAttributeOffset, max_locals, false, null);
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }

    public void completeCodeAttributeForProblemMethod(AbstractMethodDeclaration method, MethodBinding binding, int codeAttributeOffset, int[] startLineIndexes, int problemLine) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        int code_length = this.codeStream.position;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        if (localContentsOffset + 50 >= this.contents.length) {
            this.resizeContents(50);
        }
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        if ((localContentsOffset += 2) + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 2) != 0) {
            if (problemLine == 0) {
                problemLine = Util.getLineNumber(binding.sourceStart(), startLineIndexes, 0, startLineIndexes.length - 1);
            }
            attributesNumber += this.generateLineNumberAttribute(problemLine);
        }
        if ((this.produceAttributes & 4) != 0) {
            boolean methodDeclarationIsStatic = this.codeStream.methodDeclaration.isStatic();
            attributesNumber += this.generateLocalVariableTableAttribute(code_length, methodDeclarationIsStatic, false);
        }
        if ((this.produceAttributes & 8) != 0) {
            attributesNumber += this.generateStackMapTableAttribute(binding, code_length, codeAttributeOffset, max_locals, false, null);
        }
        if ((this.produceAttributes & 0x10) != 0) {
            attributesNumber += this.generateStackMapAttribute(binding, code_length, codeAttributeOffset, max_locals, false, null);
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }

    public void completeCodeAttributeForSyntheticMethod(boolean hasExceptionHandlers, SyntheticMethodBinding binding, int codeAttributeOffset, int[] startLineIndexes, Scope scope) {
        boolean addStackMaps;
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        int code_length = this.codeStream.position;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        if (localContentsOffset + 40 >= this.contents.length) {
            this.resizeContents(40);
        }
        boolean bl = addStackMaps = (this.produceAttributes & 8) != 0;
        if (hasExceptionHandlers) {
            ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
            int exceptionHandlersCount = 0;
            int i = 0;
            int length = this.codeStream.exceptionLabelsCounter;
            while (i < length) {
                exceptionHandlersCount += this.codeStream.exceptionLabels[i].getCount() / 2;
                ++i;
            }
            int exSize = exceptionHandlersCount * 8 + 2;
            if (exSize + localContentsOffset >= this.contents.length) {
                this.resizeContents(exSize);
            }
            this.contents[localContentsOffset++] = (byte)(exceptionHandlersCount >> 8);
            this.contents[localContentsOffset++] = (byte)exceptionHandlersCount;
            int i2 = 0;
            int max = this.codeStream.exceptionLabelsCounter;
            while (i2 < max) {
                ExceptionLabel exceptionLabel = exceptionLabels[i2];
                if (exceptionLabel != null) {
                    int iRange = 0;
                    int maxRange = exceptionLabel.getCount();
                    if ((maxRange & 1) != 0) {
                        this.referenceBinding.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidExceptionAttribute, new String(binding.selector), this.referenceBinding.scope.problemReporter().referenceContext));
                    }
                    while (iRange < maxRange) {
                        int nameIndex;
                        int start = exceptionLabel.ranges[iRange++];
                        this.contents[localContentsOffset++] = (byte)(start >> 8);
                        this.contents[localContentsOffset++] = (byte)start;
                        int end = exceptionLabel.ranges[iRange++];
                        this.contents[localContentsOffset++] = (byte)(end >> 8);
                        this.contents[localContentsOffset++] = (byte)end;
                        int handlerPC = exceptionLabel.position;
                        if (addStackMaps) {
                            StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
                            stackMapFrameCodeStream.addFramePosition(handlerPC);
                        }
                        this.contents[localContentsOffset++] = (byte)(handlerPC >> 8);
                        this.contents[localContentsOffset++] = (byte)handlerPC;
                        if (exceptionLabel.exceptionType == null) {
                            this.contents[localContentsOffset++] = 0;
                            this.contents[localContentsOffset++] = 0;
                            continue;
                        }
                        switch (exceptionLabel.exceptionType.id) {
                            case 12: {
                                nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
                                break;
                            }
                            case 7: {
                                nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangNoSuchFieldErrorConstantPoolName);
                                break;
                            }
                            default: {
                                nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
                            }
                        }
                        this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)nameIndex;
                    }
                }
                ++i2;
            }
        } else {
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
        }
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        if ((localContentsOffset += 2) + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 2) != 0) {
            int lineNumber = Util.getLineNumber(binding.sourceStart, startLineIndexes, 0, startLineIndexes.length - 1);
            attributesNumber += this.generateLineNumberAttribute(lineNumber);
        }
        if ((this.produceAttributes & 4) != 0) {
            boolean methodDeclarationIsStatic = binding.isStatic();
            attributesNumber += this.generateLocalVariableTableAttribute(code_length, methodDeclarationIsStatic, true);
        }
        if (addStackMaps) {
            attributesNumber += this.generateStackMapTableAttribute(binding, code_length, codeAttributeOffset, max_locals, false, scope);
        }
        if ((this.produceAttributes & 0x10) != 0) {
            attributesNumber += this.generateStackMapAttribute(binding, code_length, codeAttributeOffset, max_locals, false, scope);
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }

    public void completeCodeAttributeForSyntheticMethod(SyntheticMethodBinding binding, int codeAttributeOffset, int[] startLineIndexes) {
        this.completeCodeAttributeForSyntheticMethod(false, binding, codeAttributeOffset, startLineIndexes, ((SourceTypeBinding)binding.declaringClass).scope);
    }

    private void completeArgumentAnnotationInfo(Argument[] arguments, List<AnnotationContext> allAnnotationContexts) {
        int i = 0;
        int max = arguments.length;
        while (i < max) {
            Argument argument = arguments[i];
            if ((argument.bits & 0x100000) != 0) {
                argument.getAllAnnotationContexts(22, i, allAnnotationContexts);
            }
            ++i;
        }
    }

    public void completeMethodInfo(MethodBinding binding, int methodAttributeOffset, int attributesNumber) {
        if ((this.produceAttributes & 0x20) != 0) {
            ArrayList<AnnotationContext> allTypeAnnotationContexts = new ArrayList<AnnotationContext>();
            AbstractMethodDeclaration methodDeclaration = binding.sourceMethod();
            if (methodDeclaration != null) {
                TypeParameter[] typeParameters;
                TypeReference[] thrownExceptions;
                Annotation[] annotations;
                if ((methodDeclaration.bits & 0x100000) != 0) {
                    Receiver receiver;
                    Argument[] arguments = methodDeclaration.arguments;
                    if (arguments != null) {
                        this.completeArgumentAnnotationInfo(arguments, allTypeAnnotationContexts);
                    }
                    if ((receiver = methodDeclaration.receiver) != null && (receiver.type.bits & 0x100000) != 0) {
                        receiver.type.getAllAnnotationContexts(21, allTypeAnnotationContexts);
                    }
                }
                if ((annotations = methodDeclaration.annotations) != null && !methodDeclaration.isClinit() && (methodDeclaration.isConstructor() || binding.returnType.id != 6)) {
                    methodDeclaration.getAllAnnotationContexts(20, allTypeAnnotationContexts);
                }
                if (!methodDeclaration.isConstructor() && !methodDeclaration.isClinit() && binding.returnType.id != 6) {
                    MethodDeclaration declaration = (MethodDeclaration)methodDeclaration;
                    TypeReference typeReference = declaration.returnType;
                    if ((typeReference.bits & 0x100000) != 0) {
                        typeReference.getAllAnnotationContexts(20, allTypeAnnotationContexts);
                    }
                }
                if ((thrownExceptions = methodDeclaration.thrownExceptions) != null) {
                    int i = 0;
                    int max = thrownExceptions.length;
                    while (i < max) {
                        TypeReference thrownException = thrownExceptions[i];
                        thrownException.getAllAnnotationContexts(23, i, allTypeAnnotationContexts);
                        ++i;
                    }
                }
                if ((typeParameters = methodDeclaration.typeParameters()) != null) {
                    int i = 0;
                    int max = typeParameters.length;
                    while (i < max) {
                        TypeParameter typeParameter = typeParameters[i];
                        if ((typeParameter.bits & 0x100000) != 0) {
                            typeParameter.getAllAnnotationContexts(1, i, allTypeAnnotationContexts);
                        }
                        ++i;
                    }
                }
            } else if (binding.sourceLambda() != null) {
                LambdaExpression lambda = binding.sourceLambda();
                if ((lambda.bits & 0x100000) != 0 && lambda.arguments != null) {
                    this.completeArgumentAnnotationInfo(lambda.arguments, allTypeAnnotationContexts);
                }
            }
            int size = allTypeAnnotationContexts.size();
            attributesNumber = this.completeRuntimeTypeAnnotations(attributesNumber, null, node -> size > 0, () -> allTypeAnnotationContexts);
        }
        if ((this.produceAttributes & 0x40) != 0 || binding.isConstructor() && binding.declaringClass.isRecord()) {
            attributesNumber += this.generateMethodParameters(binding);
        }
        this.contents[methodAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributesNumber;
    }

    private void dumpLocations(int[] locations) {
        if (locations == null) {
            if (this.contentsOffset + 1 >= this.contents.length) {
                this.resizeContents(1);
            }
            this.contents[this.contentsOffset++] = 0;
        } else {
            int length = locations.length;
            if (this.contentsOffset + length >= this.contents.length) {
                this.resizeContents(length + 1);
            }
            this.contents[this.contentsOffset++] = (byte)(locations.length / 2);
            int i = 0;
            while (i < length) {
                this.contents[this.contentsOffset++] = (byte)locations[i];
                ++i;
            }
        }
    }

    private void dumpTargetTypeContents(int targetType, AnnotationContext annotationContext) {
        switch (targetType) {
            case 0: 
            case 1: {
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                break;
            }
            case 17: {
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                this.contents[this.contentsOffset++] = (byte)annotationContext.info2;
                break;
            }
            case 19: 
            case 20: 
            case 21: {
                break;
            }
            case 22: {
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                break;
            }
            case 66: 
            case 67: 
            case 68: 
            case 69: 
            case 70: {
                this.contents[this.contentsOffset++] = (byte)(annotationContext.info >> 8);
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                break;
            }
            case 71: {
                this.contents[this.contentsOffset++] = (byte)(annotationContext.info >> 8);
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                this.contents[this.contentsOffset++] = (byte)annotationContext.info2;
                break;
            }
            case 72: 
            case 73: 
            case 74: 
            case 75: {
                this.contents[this.contentsOffset++] = (byte)(annotationContext.info >> 8);
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                this.contents[this.contentsOffset++] = (byte)annotationContext.info2;
                break;
            }
            case 16: 
            case 23: {
                this.contents[this.contentsOffset++] = (byte)(annotationContext.info >> 8);
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                break;
            }
            case 64: 
            case 65: {
                int localVariableTableOffset = this.contentsOffset;
                LocalVariableBinding localVariable = annotationContext.variableBinding;
                int actualSize = 0;
                int initializationCount = localVariable.initializationCount;
                if (this.contentsOffset + (actualSize += 2 + 6 * initializationCount) >= this.contents.length) {
                    this.resizeContents(actualSize);
                }
                this.contentsOffset += 2;
                int numberOfEntries = 0;
                int j = 0;
                while (j < initializationCount) {
                    int startPC = localVariable.initializationPCs[j << 1];
                    int endPC = localVariable.initializationPCs[(j << 1) + 1];
                    if (startPC != endPC) {
                        ++numberOfEntries;
                        this.contents[this.contentsOffset++] = (byte)(startPC >> 8);
                        this.contents[this.contentsOffset++] = (byte)startPC;
                        int length = endPC - startPC;
                        this.contents[this.contentsOffset++] = (byte)(length >> 8);
                        this.contents[this.contentsOffset++] = (byte)length;
                        int resolvedPosition = localVariable.resolvedPosition;
                        this.contents[this.contentsOffset++] = (byte)(resolvedPosition >> 8);
                        this.contents[this.contentsOffset++] = (byte)resolvedPosition;
                    }
                    ++j;
                }
                this.contents[localVariableTableOffset++] = (byte)(numberOfEntries >> 8);
                this.contents[localVariableTableOffset] = (byte)numberOfEntries;
                break;
            }
            case 18: {
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                this.contents[this.contentsOffset++] = (byte)annotationContext.info2;
            }
        }
    }

    public char[] fileName() {
        return this.constantPool.UTF8Cache.returnKeyFor(2);
    }

    private void generateAnnotation(Annotation annotation, int currentOffset) {
        TypeBinding annotationTypeBinding;
        int startingContentsOffset = currentOffset;
        if (this.contentsOffset + 4 >= this.contents.length) {
            this.resizeContents(4);
        }
        if ((annotationTypeBinding = annotation.resolvedType) == null) {
            this.contentsOffset = startingContentsOffset;
            return;
        }
        if (annotationTypeBinding.isMemberType()) {
            this.recordInnerClasses(annotationTypeBinding);
        }
        int typeIndex = this.constantPool.literalIndex(annotationTypeBinding.signature());
        this.contents[this.contentsOffset++] = (byte)(typeIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)typeIndex;
        if (annotation instanceof NormalAnnotation) {
            NormalAnnotation normalAnnotation = (NormalAnnotation)annotation;
            MemberValuePair[] memberValuePairs = normalAnnotation.memberValuePairs;
            int memberValuePairOffset = this.contentsOffset;
            if (memberValuePairs != null) {
                int memberValuePairsCount = 0;
                int memberValuePairsLengthPosition = this.contentsOffset;
                this.contentsOffset += 2;
                int resetPosition = this.contentsOffset;
                int memberValuePairsLength = memberValuePairs.length;
                int i = 0;
                while (i < memberValuePairsLength) {
                    MemberValuePair memberValuePair = memberValuePairs[i];
                    if (this.contentsOffset + 2 >= this.contents.length) {
                        this.resizeContents(2);
                    }
                    int elementNameIndex = this.constantPool.literalIndex(memberValuePair.name);
                    this.contents[this.contentsOffset++] = (byte)(elementNameIndex >> 8);
                    this.contents[this.contentsOffset++] = (byte)elementNameIndex;
                    MethodBinding methodBinding = memberValuePair.binding;
                    if (methodBinding == null) {
                        this.contentsOffset = resetPosition;
                    } else {
                        try {
                            this.generateElementValue(memberValuePair.value, methodBinding.returnType, memberValuePairOffset);
                            if (this.contentsOffset == memberValuePairOffset) {
                                this.contents[this.contentsOffset++] = 0;
                                this.contents[this.contentsOffset++] = 0;
                                break;
                            }
                            ++memberValuePairsCount;
                            resetPosition = this.contentsOffset;
                        }
                        catch (ClassCastException | ShouldNotImplement runtimeException) {
                            this.contentsOffset = resetPosition;
                        }
                    }
                    ++i;
                }
                this.contents[memberValuePairsLengthPosition++] = (byte)(memberValuePairsCount >> 8);
                this.contents[memberValuePairsLengthPosition++] = (byte)memberValuePairsCount;
            } else {
                this.contents[this.contentsOffset++] = 0;
                this.contents[this.contentsOffset++] = 0;
            }
        } else if (annotation instanceof SingleMemberAnnotation) {
            SingleMemberAnnotation singleMemberAnnotation = (SingleMemberAnnotation)annotation;
            this.contents[this.contentsOffset++] = 0;
            this.contents[this.contentsOffset++] = 1;
            if (this.contentsOffset + 2 >= this.contents.length) {
                this.resizeContents(2);
            }
            int elementNameIndex = this.constantPool.literalIndex(VALUE);
            this.contents[this.contentsOffset++] = (byte)(elementNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)elementNameIndex;
            MethodBinding methodBinding = singleMemberAnnotation.memberValuePairs()[0].binding;
            if (methodBinding == null) {
                this.contentsOffset = startingContentsOffset;
            } else {
                int memberValuePairOffset = this.contentsOffset;
                try {
                    this.generateElementValue(singleMemberAnnotation.memberValue, methodBinding.returnType, memberValuePairOffset);
                    if (this.contentsOffset == memberValuePairOffset) {
                        this.contentsOffset = startingContentsOffset;
                    }
                }
                catch (ClassCastException | ShouldNotImplement runtimeException) {
                    this.contentsOffset = startingContentsOffset;
                }
            }
        } else {
            this.contents[this.contentsOffset++] = 0;
            this.contents[this.contentsOffset++] = 0;
        }
    }

    private int generateAnnotationDefaultAttribute(AnnotationMethodDeclaration declaration, int attributeOffset) {
        int attributesNumber = 0;
        int annotationDefaultNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.AnnotationDefaultName);
        if (this.contentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        this.contents[this.contentsOffset++] = (byte)(annotationDefaultNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)annotationDefaultNameIndex;
        int attributeLengthOffset = this.contentsOffset;
        this.contentsOffset += 4;
        this.generateElementValue(declaration.defaultValue, declaration.binding.returnType, attributeOffset);
        if (this.contentsOffset != attributeOffset) {
            int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
            this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
            this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
            this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
            this.contents[attributeLengthOffset++] = (byte)attributeLength;
            ++attributesNumber;
        }
        return attributesNumber;
    }

    public void generateCodeAttributeHeader() {
        if (this.contentsOffset + 20 >= this.contents.length) {
            this.resizeContents(20);
        }
        int constantValueNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.CodeName);
        this.contents[this.contentsOffset++] = (byte)(constantValueNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)constantValueNameIndex;
        this.contentsOffset += 12;
    }

    private int generateConstantValueAttribute(Constant fieldConstant, FieldBinding fieldBinding, int fieldAttributeOffset) {
        int localContentsOffset = this.contentsOffset;
        int attributesNumber = 1;
        if (localContentsOffset + 8 >= this.contents.length) {
            this.resizeContents(8);
        }
        int constantValueNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.ConstantValueName);
        this.contents[localContentsOffset++] = (byte)(constantValueNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)constantValueNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 2;
        switch (fieldConstant.typeID()) {
            case 5: {
                int booleanValueIndex = this.constantPool.literalIndex(fieldConstant.booleanValue() ? 1 : 0);
                this.contents[localContentsOffset++] = (byte)(booleanValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)booleanValueIndex;
                break;
            }
            case 2: 
            case 3: 
            case 4: 
            case 10: {
                int integerValueIndex = this.constantPool.literalIndex(fieldConstant.intValue());
                this.contents[localContentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 9: {
                int floatValueIndex = this.constantPool.literalIndex(fieldConstant.floatValue());
                this.contents[localContentsOffset++] = (byte)(floatValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)floatValueIndex;
                break;
            }
            case 8: {
                int doubleValueIndex = this.constantPool.literalIndex(fieldConstant.doubleValue());
                this.contents[localContentsOffset++] = (byte)(doubleValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)doubleValueIndex;
                break;
            }
            case 7: {
                int longValueIndex = this.constantPool.literalIndex(fieldConstant.longValue());
                this.contents[localContentsOffset++] = (byte)(longValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)longValueIndex;
                break;
            }
            case 11: {
                int stringValueIndex = this.constantPool.literalIndex(((StringConstant)fieldConstant).stringValue());
                if (stringValueIndex == -1) {
                    if (!this.creatingProblemType) {
                        TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
                        FieldDeclaration[] fieldDecls = typeDeclaration.fields;
                        int max = fieldDecls == null ? 0 : fieldDecls.length;
                        int i = 0;
                        while (i < max) {
                            if (fieldDecls[i].binding == fieldBinding) {
                                typeDeclaration.scope.problemReporter().stringConstantIsExceedingUtf8Limit(fieldDecls[i]);
                            }
                            ++i;
                        }
                        break;
                    }
                    this.contentsOffset = fieldAttributeOffset;
                    attributesNumber = 0;
                    break;
                }
                this.contents[localContentsOffset++] = (byte)(stringValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)stringValueIndex;
            }
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }

    private int generateDeprecatedAttribute() {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        int deprecatedAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.DeprecatedName);
        this.contents[localContentsOffset++] = (byte)(deprecatedAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)deprecatedAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateNestHostAttribute() {
        SourceTypeBinding nestHost = this.referenceBinding.getNestHost();
        if (nestHost == null) {
            return 0;
        }
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 10 >= this.contents.length) {
            this.resizeContents(10);
        }
        int nestHostAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.NestHost);
        this.contents[localContentsOffset++] = (byte)(nestHostAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)nestHostAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 2;
        int nestHostIndex = this.constantPool.literalIndexForType(nestHost.constantPoolName());
        this.contents[localContentsOffset++] = (byte)(nestHostIndex >> 8);
        this.contents[localContentsOffset++] = (byte)nestHostIndex;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateNestMembersAttribute() {
        int numberOfNestedMembers;
        int localContentsOffset = this.contentsOffset;
        List<String> nestedMembers = this.getNestMembers();
        int n = numberOfNestedMembers = nestedMembers != null ? nestedMembers.size() : 0;
        if (numberOfNestedMembers == 0) {
            return 0;
        }
        int exSize = 8 + 2 * numberOfNestedMembers;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        int attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.NestMembers);
        this.contents[localContentsOffset++] = (byte)(attributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)attributeNameIndex;
        int value = (numberOfNestedMembers << 1) + 2;
        this.contents[localContentsOffset++] = (byte)(value >> 24);
        this.contents[localContentsOffset++] = (byte)(value >> 16);
        this.contents[localContentsOffset++] = (byte)(value >> 8);
        this.contents[localContentsOffset++] = (byte)value;
        this.contents[localContentsOffset++] = (byte)(numberOfNestedMembers >> 8);
        this.contents[localContentsOffset++] = (byte)numberOfNestedMembers;
        int i = 0;
        while (i < numberOfNestedMembers) {
            char[] nestMemberName = nestedMembers.get(i).toCharArray();
            int nestedMemberIndex = this.constantPool.literalIndexForType(nestMemberName);
            this.contents[localContentsOffset++] = (byte)(nestedMemberIndex >> 8);
            this.contents[localContentsOffset++] = (byte)nestedMemberIndex;
            ++i;
        }
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateNestAttributes() {
        int nAttrs = this.generateNestMembersAttribute();
        return nAttrs += this.generateNestHostAttribute();
    }

    private int generatePermittedTypeAttributes() {
        int l;
        SourceTypeBinding type = this.referenceBinding;
        int localContentsOffset = this.contentsOffset;
        ReferenceBinding[] permittedTypes = type.permittedTypes();
        int n = l = permittedTypes != null ? permittedTypes.length : 0;
        if (l == 0) {
            return 0;
        }
        int exSize = 8 + 2 * l;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        int attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.PermittedSubclasses);
        this.contents[localContentsOffset++] = (byte)(attributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)attributeNameIndex;
        int value = (l << 1) + 2;
        this.contents[localContentsOffset++] = (byte)(value >> 24);
        this.contents[localContentsOffset++] = (byte)(value >> 16);
        this.contents[localContentsOffset++] = (byte)(value >> 8);
        this.contents[localContentsOffset++] = (byte)value;
        this.contents[localContentsOffset++] = (byte)(l >> 8);
        this.contents[localContentsOffset++] = (byte)l;
        int i = 0;
        while (i < l) {
            int permittedTypeIndex = this.constantPool.literalIndexForType(permittedTypes[i]);
            this.contents[localContentsOffset++] = (byte)(permittedTypeIndex >> 8);
            this.contents[localContentsOffset++] = (byte)permittedTypeIndex;
            ++i;
        }
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateRecordAttributes() {
        SourceTypeBinding record = this.referenceBinding;
        if (record == null || !record.isRecord()) {
            return 0;
        }
        int localContentsOffset = this.contentsOffset;
        RecordComponentBinding[] recordComponents = this.referenceBinding.components();
        if (recordComponents == null) {
            return 0;
        }
        int numberOfRecordComponents = recordComponents.length;
        int exSize = 8 + 2 * numberOfRecordComponents;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        int attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RecordClass);
        this.contents[localContentsOffset++] = (byte)(attributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)attributeNameIndex;
        int attrLengthOffset = localContentsOffset;
        int base = localContentsOffset += 4;
        this.contents[localContentsOffset++] = (byte)(numberOfRecordComponents >> 8);
        this.contents[localContentsOffset++] = (byte)numberOfRecordComponents;
        this.contentsOffset = localContentsOffset;
        int i = 0;
        while (i < numberOfRecordComponents) {
            this.addComponentInfo(recordComponents[i]);
            ++i;
        }
        int attrLength = this.contentsOffset - base;
        this.contents[attrLengthOffset++] = (byte)(attrLength >> 24);
        this.contents[attrLengthOffset++] = (byte)(attrLength >> 16);
        this.contents[attrLengthOffset++] = (byte)(attrLength >> 8);
        this.contents[attrLengthOffset++] = (byte)attrLength;
        return 1;
    }

    private int generateModuleAttribute(ModuleDeclaration module) {
        int nameIndex;
        int nameIndex2;
        SourceModuleBinding binding = module.binding;
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 10 >= this.contents.length) {
            this.resizeContents(10);
        }
        int moduleAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.ModuleName);
        this.contents[localContentsOffset++] = (byte)(moduleAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)moduleAttributeNameIndex;
        int attrLengthOffset = localContentsOffset;
        localContentsOffset += 4;
        int moduleNameIndex = this.constantPool.literalIndexForModule(binding.moduleName);
        this.contents[localContentsOffset++] = (byte)(moduleNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)moduleNameIndex;
        int flags = module.modifiers & 0xFFFF7FFF;
        this.contents[localContentsOffset++] = (byte)(flags >> 8);
        this.contents[localContentsOffset++] = (byte)flags;
        String moduleVersion = module.getModuleVersion();
        int module_version_idx = moduleVersion == null ? 0 : this.constantPool.literalIndex(moduleVersion.toCharArray());
        this.contents[localContentsOffset++] = (byte)(module_version_idx >> 8);
        this.contents[localContentsOffset++] = (byte)module_version_idx;
        int attrLength = 6;
        int requiresCountOffset = localContentsOffset;
        int requiresCount = module.requiresCount;
        int requiresSize = 2 + requiresCount * 6;
        if (localContentsOffset + requiresSize >= this.contents.length) {
            this.resizeContents(requiresSize);
        }
        localContentsOffset += 2;
        ModuleBinding javaBaseBinding = null;
        int i = 0;
        while (i < module.requiresCount) {
            RequiresStatement req = module.requires[i];
            ModuleBinding reqBinding = req.resolvedBinding;
            if (CharOperation.equals(reqBinding.moduleName, TypeConstants.JAVA_BASE)) {
                javaBaseBinding = reqBinding;
            }
            nameIndex2 = this.constantPool.literalIndexForModule(reqBinding.moduleName);
            this.contents[localContentsOffset++] = (byte)(nameIndex2 >> 8);
            this.contents[localContentsOffset++] = (byte)nameIndex2;
            flags = req.modifiers;
            this.contents[localContentsOffset++] = (byte)(flags >> 8);
            this.contents[localContentsOffset++] = (byte)flags;
            int required_version = 0;
            this.contents[localContentsOffset++] = (byte)(required_version >> 8);
            this.contents[localContentsOffset++] = (byte)required_version;
            ++i;
        }
        if (!CharOperation.equals(binding.moduleName, TypeConstants.JAVA_BASE) && javaBaseBinding == null) {
            if (localContentsOffset + 6 >= this.contents.length) {
                this.resizeContents(6);
            }
            javaBaseBinding = binding.environment.javaBaseModule();
            int javabase_index = this.constantPool.literalIndexForModule(javaBaseBinding.moduleName);
            this.contents[localContentsOffset++] = (byte)(javabase_index >> 8);
            this.contents[localContentsOffset++] = (byte)javabase_index;
            flags = 32768;
            this.contents[localContentsOffset++] = (byte)(flags >> 8);
            this.contents[localContentsOffset++] = (byte)flags;
            int required_version = 0;
            this.contents[localContentsOffset++] = (byte)(required_version >> 8);
            this.contents[localContentsOffset++] = (byte)required_version;
            ++requiresCount;
        }
        this.contents[requiresCountOffset++] = (byte)(requiresCount >> 8);
        this.contents[requiresCountOffset++] = (byte)requiresCount;
        attrLength += 2 + 6 * requiresCount;
        int exportsSize = 2 + module.exportsCount * 6;
        if (localContentsOffset + exportsSize >= this.contents.length) {
            this.resizeContents(exportsSize);
        }
        this.contents[localContentsOffset++] = (byte)(module.exportsCount >> 8);
        this.contents[localContentsOffset++] = (byte)module.exportsCount;
        int i2 = 0;
        while (i2 < module.exportsCount) {
            ExportsStatement ref = module.exports[i2];
            if (localContentsOffset + 6 >= this.contents.length) {
                this.resizeContents((module.exportsCount - i2) * 6);
            }
            nameIndex2 = this.constantPool.literalIndexForPackage(CharOperation.replaceOnCopy(ref.pkgName, '.', '/'));
            this.contents[localContentsOffset++] = (byte)(nameIndex2 >> 8);
            this.contents[localContentsOffset++] = (byte)nameIndex2;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            int exportsToCount = ref.isQualified() ? ref.targets.length : 0;
            this.contents[localContentsOffset++] = (byte)(exportsToCount >> 8);
            this.contents[localContentsOffset++] = (byte)exportsToCount;
            if (exportsToCount > 0) {
                int targetSize = 2 * exportsToCount;
                if (localContentsOffset + targetSize >= this.contents.length) {
                    this.resizeContents(targetSize);
                }
                int j = 0;
                while (j < exportsToCount) {
                    nameIndex2 = this.constantPool.literalIndexForModule(ref.targets[j].moduleName);
                    this.contents[localContentsOffset++] = (byte)(nameIndex2 >> 8);
                    this.contents[localContentsOffset++] = (byte)nameIndex2;
                    ++j;
                }
                attrLength += targetSize;
            }
            ++i2;
        }
        attrLength += exportsSize;
        int opensSize = 2 + module.opensCount * 6;
        if (localContentsOffset + opensSize >= this.contents.length) {
            this.resizeContents(opensSize);
        }
        this.contents[localContentsOffset++] = (byte)(module.opensCount >> 8);
        this.contents[localContentsOffset++] = (byte)module.opensCount;
        int i3 = 0;
        while (i3 < module.opensCount) {
            OpensStatement ref = module.opens[i3];
            if (localContentsOffset + 6 >= this.contents.length) {
                this.resizeContents((module.opensCount - i3) * 6);
            }
            nameIndex = this.constantPool.literalIndexForPackage(CharOperation.replaceOnCopy(ref.pkgName, '.', '/'));
            this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)nameIndex;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            int opensToCount = ref.isQualified() ? ref.targets.length : 0;
            this.contents[localContentsOffset++] = (byte)(opensToCount >> 8);
            this.contents[localContentsOffset++] = (byte)opensToCount;
            if (opensToCount > 0) {
                int targetSize = 2 * opensToCount;
                if (localContentsOffset + targetSize >= this.contents.length) {
                    this.resizeContents(targetSize);
                }
                int j = 0;
                while (j < opensToCount) {
                    nameIndex = this.constantPool.literalIndexForModule(ref.targets[j].moduleName);
                    this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                    this.contents[localContentsOffset++] = (byte)nameIndex;
                    ++j;
                }
                attrLength += targetSize;
            }
            ++i3;
        }
        attrLength += opensSize;
        int usesSize = 2 + 2 * module.usesCount;
        if (localContentsOffset + usesSize >= this.contents.length) {
            this.resizeContents(usesSize);
        }
        this.contents[localContentsOffset++] = (byte)(module.usesCount >> 8);
        this.contents[localContentsOffset++] = (byte)module.usesCount;
        int i4 = 0;
        while (i4 < module.usesCount) {
            nameIndex = this.constantPool.literalIndexForType(module.uses[i4].serviceInterface.resolvedType.constantPoolName());
            this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)nameIndex;
            ++i4;
        }
        attrLength += usesSize;
        int servicesSize = 2 + 4 * module.servicesCount;
        if (localContentsOffset + servicesSize >= this.contents.length) {
            this.resizeContents(servicesSize);
        }
        this.contents[localContentsOffset++] = (byte)(module.servicesCount >> 8);
        this.contents[localContentsOffset++] = (byte)module.servicesCount;
        int i5 = 0;
        while (i5 < module.servicesCount) {
            if (localContentsOffset + 4 >= this.contents.length) {
                this.resizeContents((module.servicesCount - i5) * 4);
            }
            int nameIndex3 = this.constantPool.literalIndexForType(module.services[i5].serviceInterface.resolvedType.constantPoolName());
            this.contents[localContentsOffset++] = (byte)(nameIndex3 >> 8);
            this.contents[localContentsOffset++] = (byte)nameIndex3;
            TypeReference[] impls = module.services[i5].implementations;
            int implLength = impls.length;
            this.contents[localContentsOffset++] = (byte)(implLength >> 8);
            this.contents[localContentsOffset++] = (byte)implLength;
            int targetSize = implLength * 2;
            if (localContentsOffset + targetSize >= this.contents.length) {
                this.resizeContents(targetSize);
            }
            int j = 0;
            while (j < implLength) {
                nameIndex3 = this.constantPool.literalIndexForType(impls[j].resolvedType.constantPoolName());
                this.contents[localContentsOffset++] = (byte)(nameIndex3 >> 8);
                this.contents[localContentsOffset++] = (byte)nameIndex3;
                ++j;
            }
            attrLength += targetSize;
            ++i5;
        }
        this.contents[attrLengthOffset++] = (byte)((attrLength += servicesSize) >> 24);
        this.contents[attrLengthOffset++] = (byte)(attrLength >> 16);
        this.contents[attrLengthOffset++] = (byte)(attrLength >> 8);
        this.contents[attrLengthOffset++] = (byte)attrLength;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateModuleMainClassAttribute(char[] moduleMainClass) {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 8 >= this.contents.length) {
            this.resizeContents(8);
        }
        int moduleAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.ModuleMainClass);
        this.contents[localContentsOffset++] = (byte)(moduleAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)moduleAttributeNameIndex;
        int attrLength = 2;
        this.contents[localContentsOffset++] = (byte)(attrLength >> 24);
        this.contents[localContentsOffset++] = (byte)(attrLength >> 16);
        this.contents[localContentsOffset++] = (byte)(attrLength >> 8);
        this.contents[localContentsOffset++] = (byte)attrLength;
        int moduleNameIndex = this.constantPool.literalIndexForType(moduleMainClass);
        this.contents[localContentsOffset++] = (byte)(moduleNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)moduleNameIndex;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateModulePackagesAttribute(char[][] packageNames) {
        int localContentsOffset = this.contentsOffset;
        int maxSize = 6 + 2 * packageNames.length;
        if (localContentsOffset + maxSize >= this.contents.length) {
            this.resizeContents(maxSize);
        }
        int moduleAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.ModulePackages);
        this.contents[localContentsOffset++] = (byte)(moduleAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)moduleAttributeNameIndex;
        int attrLengthOffset = localContentsOffset;
        int packageCountOffset = localContentsOffset += 4;
        localContentsOffset += 2;
        int packagesCount = 0;
        char[][] cArray = packageNames;
        int n = packageNames.length;
        int n2 = 0;
        while (n2 < n) {
            char[] packageName = cArray[n2];
            if (packageName != null && packageName.length != 0) {
                int packageNameIndex = this.constantPool.literalIndexForPackage(packageName);
                this.contents[localContentsOffset++] = (byte)(packageNameIndex >> 8);
                this.contents[localContentsOffset++] = (byte)packageNameIndex;
                ++packagesCount;
            }
            ++n2;
        }
        this.contents[packageCountOffset++] = (byte)(packagesCount >> 8);
        this.contents[packageCountOffset++] = (byte)packagesCount;
        int attrLength = 2 + 2 * packagesCount;
        this.contents[attrLengthOffset++] = (byte)(attrLength >> 24);
        this.contents[attrLengthOffset++] = (byte)(attrLength >> 16);
        this.contents[attrLengthOffset++] = (byte)(attrLength >> 8);
        this.contents[attrLengthOffset++] = (byte)attrLength;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private void generateElementValue(Expression defaultValue, TypeBinding memberValuePairReturnType, int attributeOffset) {
        Constant constant = defaultValue.constant;
        TypeBinding defaultValueBinding = defaultValue.resolvedType;
        if (defaultValueBinding == null) {
            this.contentsOffset = attributeOffset;
        } else {
            if (defaultValueBinding.isMemberType()) {
                this.recordInnerClasses(defaultValueBinding);
            }
            if (memberValuePairReturnType.isMemberType()) {
                this.recordInnerClasses(memberValuePairReturnType);
            }
            if (memberValuePairReturnType.isArrayType() && !defaultValueBinding.isArrayType()) {
                if (this.contentsOffset + 3 >= this.contents.length) {
                    this.resizeContents(3);
                }
                this.contents[this.contentsOffset++] = 91;
                this.contents[this.contentsOffset++] = 0;
                this.contents[this.contentsOffset++] = 1;
            }
            if (constant != null && constant != Constant.NotAConstant) {
                this.generateElementValue(attributeOffset, defaultValue, constant, memberValuePairReturnType.leafComponentType());
            } else {
                this.generateElementValueForNonConstantExpression(defaultValue, attributeOffset, defaultValueBinding);
            }
        }
    }

    private void generateElementValue(int attributeOffset, Expression defaultValue, Constant constant, TypeBinding binding) {
        if (this.contentsOffset + 3 >= this.contents.length) {
            this.resizeContents(3);
        }
        switch (binding.id) {
            case 5: {
                this.contents[this.contentsOffset++] = 90;
                int booleanValueIndex = this.constantPool.literalIndex(constant.booleanValue() ? 1 : 0);
                this.contents[this.contentsOffset++] = (byte)(booleanValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)booleanValueIndex;
                break;
            }
            case 3: {
                this.contents[this.contentsOffset++] = 66;
                int integerValueIndex = this.constantPool.literalIndex(constant.intValue());
                this.contents[this.contentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 2: {
                this.contents[this.contentsOffset++] = 67;
                int integerValueIndex = this.constantPool.literalIndex(constant.intValue());
                this.contents[this.contentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 10: {
                this.contents[this.contentsOffset++] = 73;
                int integerValueIndex = this.constantPool.literalIndex(constant.intValue());
                this.contents[this.contentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 4: {
                this.contents[this.contentsOffset++] = 83;
                int integerValueIndex = this.constantPool.literalIndex(constant.intValue());
                this.contents[this.contentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 9: {
                this.contents[this.contentsOffset++] = 70;
                int floatValueIndex = this.constantPool.literalIndex(constant.floatValue());
                this.contents[this.contentsOffset++] = (byte)(floatValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)floatValueIndex;
                break;
            }
            case 8: {
                this.contents[this.contentsOffset++] = 68;
                int doubleValueIndex = this.constantPool.literalIndex(constant.doubleValue());
                this.contents[this.contentsOffset++] = (byte)(doubleValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)doubleValueIndex;
                break;
            }
            case 7: {
                this.contents[this.contentsOffset++] = 74;
                int longValueIndex = this.constantPool.literalIndex(constant.longValue());
                this.contents[this.contentsOffset++] = (byte)(longValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)longValueIndex;
                break;
            }
            case 11: {
                this.contents[this.contentsOffset++] = 115;
                int stringValueIndex = this.constantPool.literalIndex(((StringConstant)constant).stringValue().toCharArray());
                if (stringValueIndex == -1) {
                    if (!this.creatingProblemType) {
                        TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
                        typeDeclaration.scope.problemReporter().stringConstantIsExceedingUtf8Limit(defaultValue);
                        break;
                    }
                    this.contentsOffset = attributeOffset;
                    break;
                }
                this.contents[this.contentsOffset++] = (byte)(stringValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)stringValueIndex;
            }
        }
    }

    private void generateElementValueForNonConstantExpression(Expression defaultValue, int attributeOffset, TypeBinding defaultValueBinding) {
        if (defaultValueBinding != null) {
            if (defaultValueBinding.isEnum()) {
                NameReference nameReference;
                if (this.contentsOffset + 5 >= this.contents.length) {
                    this.resizeContents(5);
                }
                this.contents[this.contentsOffset++] = 101;
                FieldBinding fieldBinding = null;
                if (defaultValue instanceof QualifiedNameReference) {
                    nameReference = (QualifiedNameReference)defaultValue;
                    fieldBinding = (FieldBinding)nameReference.binding;
                } else if (defaultValue instanceof SingleNameReference) {
                    nameReference = (SingleNameReference)defaultValue;
                    fieldBinding = (FieldBinding)((SingleNameReference)nameReference).binding;
                } else {
                    this.contentsOffset = attributeOffset;
                }
                if (fieldBinding != null) {
                    int enumConstantTypeNameIndex = this.constantPool.literalIndex(fieldBinding.type.signature());
                    int enumConstantNameIndex = this.constantPool.literalIndex(fieldBinding.name);
                    this.contents[this.contentsOffset++] = (byte)(enumConstantTypeNameIndex >> 8);
                    this.contents[this.contentsOffset++] = (byte)enumConstantTypeNameIndex;
                    this.contents[this.contentsOffset++] = (byte)(enumConstantNameIndex >> 8);
                    this.contents[this.contentsOffset++] = (byte)enumConstantNameIndex;
                }
            } else if (defaultValueBinding.isAnnotationType()) {
                if (this.contentsOffset + 1 >= this.contents.length) {
                    this.resizeContents(1);
                }
                this.contents[this.contentsOffset++] = 64;
                this.generateAnnotation((Annotation)defaultValue, attributeOffset);
            } else if (defaultValueBinding.isArrayType()) {
                if (this.contentsOffset + 3 >= this.contents.length) {
                    this.resizeContents(3);
                }
                this.contents[this.contentsOffset++] = 91;
                if (defaultValue instanceof ArrayInitializer) {
                    ArrayInitializer arrayInitializer = (ArrayInitializer)defaultValue;
                    int arrayLength = arrayInitializer.expressions != null ? arrayInitializer.expressions.length : 0;
                    this.contents[this.contentsOffset++] = (byte)(arrayLength >> 8);
                    this.contents[this.contentsOffset++] = (byte)arrayLength;
                    int i = 0;
                    while (i < arrayLength) {
                        this.generateElementValue(arrayInitializer.expressions[i], defaultValueBinding.leafComponentType(), attributeOffset);
                        ++i;
                    }
                } else {
                    this.contentsOffset = attributeOffset;
                }
            } else {
                if (this.contentsOffset + 3 >= this.contents.length) {
                    this.resizeContents(3);
                }
                this.contents[this.contentsOffset++] = 99;
                if (defaultValue instanceof ClassLiteralAccess) {
                    ClassLiteralAccess classLiteralAccess = (ClassLiteralAccess)defaultValue;
                    int classInfoIndex = this.constantPool.literalIndex(classLiteralAccess.targetType.signature());
                    this.contents[this.contentsOffset++] = (byte)(classInfoIndex >> 8);
                    this.contents[this.contentsOffset++] = (byte)classInfoIndex;
                } else {
                    this.contentsOffset = attributeOffset;
                }
            }
        } else {
            this.contentsOffset = attributeOffset;
        }
    }

    private int generateEnclosingMethodAttribute() {
        MethodBinding methodBinding;
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 10 >= this.contents.length) {
            this.resizeContents(10);
        }
        int enclosingMethodAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.EnclosingMethodName);
        this.contents[localContentsOffset++] = (byte)(enclosingMethodAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)enclosingMethodAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 4;
        int enclosingTypeIndex = this.constantPool.literalIndexForType(this.referenceBinding.enclosingType().constantPoolName());
        this.contents[localContentsOffset++] = (byte)(enclosingTypeIndex >> 8);
        this.contents[localContentsOffset++] = (byte)enclosingTypeIndex;
        byte methodIndexByte1 = 0;
        byte methodIndexByte2 = 0;
        if (this.referenceBinding instanceof LocalTypeBinding && (methodBinding = ((LocalTypeBinding)this.referenceBinding).enclosingMethod) != null) {
            int enclosingMethodIndex = this.constantPool.literalIndexForNameAndType(methodBinding.selector, methodBinding.signature(this));
            methodIndexByte1 = (byte)(enclosingMethodIndex >> 8);
            methodIndexByte2 = (byte)enclosingMethodIndex;
        }
        this.contents[localContentsOffset++] = methodIndexByte1;
        this.contents[localContentsOffset++] = methodIndexByte2;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateExceptionsAttribute(ReferenceBinding[] thrownsExceptions) {
        int localContentsOffset = this.contentsOffset;
        int length = thrownsExceptions.length;
        int exSize = 8 + length * 2;
        if (exSize + this.contentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        int exceptionNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.ExceptionsName);
        this.contents[localContentsOffset++] = (byte)(exceptionNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)exceptionNameIndex;
        int attributeLength = length * 2 + 2;
        this.contents[localContentsOffset++] = (byte)(attributeLength >> 24);
        this.contents[localContentsOffset++] = (byte)(attributeLength >> 16);
        this.contents[localContentsOffset++] = (byte)(attributeLength >> 8);
        this.contents[localContentsOffset++] = (byte)attributeLength;
        this.contents[localContentsOffset++] = (byte)(length >> 8);
        this.contents[localContentsOffset++] = (byte)length;
        int i = 0;
        while (i < length) {
            int exceptionIndex = this.constantPool.literalIndexForType(thrownsExceptions[i]);
            this.contents[localContentsOffset++] = (byte)(exceptionIndex >> 8);
            this.contents[localContentsOffset++] = (byte)exceptionIndex;
            ++i;
        }
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateHierarchyInconsistentAttribute() {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        int inconsistentHierarchyNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.InconsistentHierarchy);
        this.contents[localContentsOffset++] = (byte)(inconsistentHierarchyNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)inconsistentHierarchyNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateInnerClassAttribute(int numberOfInnerClasses, ReferenceBinding[] innerClasses) {
        int exSize = 8 * numberOfInnerClasses + 8;
        int localContentsOffset = this.contentsOffset;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        int attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.InnerClassName);
        this.contents[localContentsOffset++] = (byte)(attributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)attributeNameIndex;
        int value = (numberOfInnerClasses << 3) + 2;
        this.contents[localContentsOffset++] = (byte)(value >> 24);
        this.contents[localContentsOffset++] = (byte)(value >> 16);
        this.contents[localContentsOffset++] = (byte)(value >> 8);
        this.contents[localContentsOffset++] = (byte)value;
        this.contents[localContentsOffset++] = (byte)(numberOfInnerClasses >> 8);
        this.contents[localContentsOffset++] = (byte)numberOfInnerClasses;
        int i = 0;
        while (i < numberOfInnerClasses) {
            ReferenceBinding innerClass = innerClasses[i];
            int accessFlags = innerClass.getAccessFlags();
            int innerClassIndex = this.constantPool.literalIndexForType(innerClass.constantPoolName());
            this.contents[localContentsOffset++] = (byte)(innerClassIndex >> 8);
            this.contents[localContentsOffset++] = (byte)innerClassIndex;
            if (innerClass.isMemberType()) {
                int outerClassIndex = this.constantPool.literalIndexForType(innerClass.enclosingType().constantPoolName());
                this.contents[localContentsOffset++] = (byte)(outerClassIndex >> 8);
                this.contents[localContentsOffset++] = (byte)outerClassIndex;
            } else {
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = 0;
            }
            if (!innerClass.isAnonymousType()) {
                int nameIndex = this.constantPool.literalIndex(innerClass.sourceName());
                this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                this.contents[localContentsOffset++] = (byte)nameIndex;
            } else {
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = 0;
            }
            if (innerClass.isAnonymousType()) {
                ReferenceBinding superClass = innerClass.superclass();
                if (superClass == null || !superClass.isEnum() || !superClass.isSealed()) {
                    accessFlags &= 0xFFFFFFEF;
                }
            } else if (innerClass.isMemberType() && innerClass.isInterface()) {
                accessFlags |= 8;
            }
            this.contents[localContentsOffset++] = (byte)(accessFlags >> 8);
            this.contents[localContentsOffset++] = (byte)accessFlags;
            ++i;
        }
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private Map<String, Integer> createInitBootStrapMethodsMap() {
        HashMap<String, Integer> fPtr = new HashMap<String, Integer>(BOOTSTRAP_METHODS.length);
        String[] stringArray = BOOTSTRAP_METHODS;
        int n = BOOTSTRAP_METHODS.length;
        int n2 = 0;
        while (n2 < n) {
            String key = stringArray[n2];
            fPtr.put(key, 0);
            ++n2;
        }
        return fPtr;
    }

    private int generateBootstrapMethods(List<ASTNode> bootStrapMethodsList) {
        ReferenceBinding methodHandlesLookup = this.referenceBinding.scope.getJavaLangInvokeMethodHandlesLookup();
        if (methodHandlesLookup == null) {
            return 0;
        }
        this.recordInnerClasses(methodHandlesLookup);
        int numberOfBootstraps = bootStrapMethodsList != null ? bootStrapMethodsList.size() : 0;
        int localContentsOffset = this.contentsOffset;
        int exSize = 10 * numberOfBootstraps + 8;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        int attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.BootstrapMethodsName);
        this.contents[localContentsOffset++] = (byte)(attributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)attributeNameIndex;
        int attributeLengthPosition = localContentsOffset;
        localContentsOffset += 4;
        this.contents[localContentsOffset++] = (byte)(numberOfBootstraps >> 8);
        this.contents[localContentsOffset++] = (byte)numberOfBootstraps;
        Map<String, Integer> fPtr = this.createInitBootStrapMethodsMap();
        int i = 0;
        while (i < numberOfBootstraps) {
            ASTNode o = this.bootstrapMethods.get(i);
            if (o instanceof FunctionalExpression) {
                localContentsOffset = this.addBootStrapLambdaEntry(localContentsOffset, (FunctionalExpression)o, fPtr);
            } else if (o instanceof TypeDeclaration) {
                localContentsOffset = this.addBootStrapRecordEntry(localContentsOffset, (TypeDeclaration)o, fPtr);
            }
            ++i;
        }
        int attributeLength = localContentsOffset - attributeLengthPosition - 4;
        this.contents[attributeLengthPosition++] = (byte)(attributeLength >> 24);
        this.contents[attributeLengthPosition++] = (byte)(attributeLength >> 16);
        this.contents[attributeLengthPosition++] = (byte)(attributeLength >> 8);
        this.contents[attributeLengthPosition++] = (byte)attributeLength;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int addBootStrapLambdaEntry(int localContentsOffset, FunctionalExpression functional, Map<String, Integer> fPtr) {
        MethodBinding[] bridges = functional.getRequiredBridges();
        TypeBinding[] markerInterfaces = null;
        int indexForAltMetaFactory = fPtr.get(ALTMETAFACTORY_STRING);
        int indexForMetaFactory = fPtr.get(METAFACTORY_STRING);
        if (functional instanceof LambdaExpression && (markerInterfaces = ((LambdaExpression)functional).getMarkerInterfaces()) != null || bridges != null || functional.isSerializable) {
            int maxm;
            int m;
            int extraSpace = 2;
            if (markerInterfaces != null) {
                extraSpace += 2 + 2 * markerInterfaces.length;
            }
            if (bridges != null) {
                extraSpace += 2 + 2 * bridges.length;
            }
            if (extraSpace + 10 + localContentsOffset >= this.contents.length) {
                this.resizeContents(extraSpace + 10);
            }
            if (indexForAltMetaFactory == 0) {
                ReferenceBinding javaLangInvokeLambdaMetafactory = this.referenceBinding.scope.getJavaLangInvokeLambdaMetafactory();
                indexForAltMetaFactory = this.constantPool.literalIndexForMethodHandle(6, javaLangInvokeLambdaMetafactory, ConstantPool.ALTMETAFACTORY, ConstantPool.JAVA_LANG_INVOKE_LAMBDAMETAFACTORY_ALTMETAFACTORY_SIGNATURE, false);
                fPtr.put(ALTMETAFACTORY_STRING, indexForAltMetaFactory);
            }
            this.contents[localContentsOffset++] = (byte)(indexForAltMetaFactory >> 8);
            this.contents[localContentsOffset++] = (byte)indexForAltMetaFactory;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = (byte)(4 + (markerInterfaces == null ? 0 : 1 + markerInterfaces.length) + (bridges == null ? 0 : 1 + bridges.length));
            int functionalDescriptorIndex = this.constantPool.literalIndexForMethodType(functional.descriptor.original().signature());
            this.contents[localContentsOffset++] = (byte)(functionalDescriptorIndex >> 8);
            this.contents[localContentsOffset++] = (byte)functionalDescriptorIndex;
            int methodHandleIndex = this.constantPool.literalIndexForMethodHandle(functional.binding.original());
            this.contents[localContentsOffset++] = (byte)(methodHandleIndex >> 8);
            this.contents[localContentsOffset++] = (byte)methodHandleIndex;
            char[] instantiatedSignature = functional.descriptor.signature();
            int methodTypeIndex = this.constantPool.literalIndexForMethodType(instantiatedSignature);
            this.contents[localContentsOffset++] = (byte)(methodTypeIndex >> 8);
            this.contents[localContentsOffset++] = (byte)methodTypeIndex;
            int bitflags = 0;
            if (functional.isSerializable) {
                bitflags |= 1;
            }
            if (markerInterfaces != null) {
                bitflags |= 2;
            }
            if (bridges != null) {
                bitflags |= 4;
            }
            int indexForBitflags = this.constantPool.literalIndex(bitflags);
            this.contents[localContentsOffset++] = (byte)(indexForBitflags >> 8);
            this.contents[localContentsOffset++] = (byte)indexForBitflags;
            if (markerInterfaces != null) {
                int markerInterfaceCountIndex = this.constantPool.literalIndex(markerInterfaces.length);
                this.contents[localContentsOffset++] = (byte)(markerInterfaceCountIndex >> 8);
                this.contents[localContentsOffset++] = (byte)markerInterfaceCountIndex;
                m = 0;
                maxm = markerInterfaces.length;
                while (m < maxm) {
                    int classTypeIndex = this.constantPool.literalIndexForType(markerInterfaces[m]);
                    this.contents[localContentsOffset++] = (byte)(classTypeIndex >> 8);
                    this.contents[localContentsOffset++] = (byte)classTypeIndex;
                    ++m;
                }
            }
            if (bridges != null) {
                int bridgeCountIndex = this.constantPool.literalIndex(bridges.length);
                this.contents[localContentsOffset++] = (byte)(bridgeCountIndex >> 8);
                this.contents[localContentsOffset++] = (byte)bridgeCountIndex;
                m = 0;
                maxm = bridges.length;
                while (m < maxm) {
                    char[] bridgeSignature = bridges[m].signature();
                    int bridgeMethodTypeIndex = this.constantPool.literalIndexForMethodType(bridgeSignature);
                    this.contents[localContentsOffset++] = (byte)(bridgeMethodTypeIndex >> 8);
                    this.contents[localContentsOffset++] = (byte)bridgeMethodTypeIndex;
                    ++m;
                }
            }
        } else {
            if (10 + localContentsOffset >= this.contents.length) {
                this.resizeContents(10);
            }
            if (indexForMetaFactory == 0) {
                ReferenceBinding javaLangInvokeLambdaMetafactory = this.referenceBinding.scope.getJavaLangInvokeLambdaMetafactory();
                indexForMetaFactory = this.constantPool.literalIndexForMethodHandle(6, javaLangInvokeLambdaMetafactory, ConstantPool.METAFACTORY, ConstantPool.JAVA_LANG_INVOKE_LAMBDAMETAFACTORY_METAFACTORY_SIGNATURE, false);
                fPtr.put(METAFACTORY_STRING, indexForMetaFactory);
            }
            this.contents[localContentsOffset++] = (byte)(indexForMetaFactory >> 8);
            this.contents[localContentsOffset++] = (byte)indexForMetaFactory;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 3;
            int functionalDescriptorIndex = this.constantPool.literalIndexForMethodType(functional.descriptor.original().signature());
            this.contents[localContentsOffset++] = (byte)(functionalDescriptorIndex >> 8);
            this.contents[localContentsOffset++] = (byte)functionalDescriptorIndex;
            int methodHandleIndex = this.constantPool.literalIndexForMethodHandle(functional.binding instanceof PolymorphicMethodBinding ? functional.binding : functional.binding.original());
            this.contents[localContentsOffset++] = (byte)(methodHandleIndex >> 8);
            this.contents[localContentsOffset++] = (byte)methodHandleIndex;
            char[] instantiatedSignature = functional.descriptor.signature();
            int methodTypeIndex = this.constantPool.literalIndexForMethodType(instantiatedSignature);
            this.contents[localContentsOffset++] = (byte)(methodTypeIndex >> 8);
            this.contents[localContentsOffset++] = (byte)methodTypeIndex;
        }
        return localContentsOffset;
    }

    private int addBootStrapRecordEntry(int localContentsOffset, TypeDeclaration typeDecl, Map<String, Integer> fPtr) {
        SourceTypeBinding type = typeDecl.binding;
        assert (((TypeBinding)type).isRecord());
        int indexForObjectMethodBootStrap = fPtr.get(BOOTSTRAP_STRING);
        if (10 + localContentsOffset >= this.contents.length) {
            this.resizeContents(10);
        }
        if (indexForObjectMethodBootStrap == 0) {
            ReferenceBinding javaLangRuntimeObjectMethods = this.referenceBinding.scope.getJavaLangRuntimeObjectMethods();
            indexForObjectMethodBootStrap = this.constantPool.literalIndexForMethodHandle(6, javaLangRuntimeObjectMethods, ConstantPool.BOOTSTRAP, ConstantPool.JAVA_LANG_RUNTIME_OBJECTMETHOD_BOOTSTRAP_SIGNATURE, false);
            fPtr.put(BOOTSTRAP_STRING, indexForObjectMethodBootStrap);
        }
        this.contents[localContentsOffset++] = (byte)(indexForObjectMethodBootStrap >> 8);
        this.contents[localContentsOffset++] = (byte)indexForObjectMethodBootStrap;
        int numArgsLocation = localContentsOffset;
        localContentsOffset += 2;
        char[] recordName = ((TypeBinding)type).constantPoolName();
        int recordIndex = this.constantPool.literalIndexForType(recordName);
        this.contents[localContentsOffset++] = (byte)(recordIndex >> 8);
        this.contents[localContentsOffset++] = (byte)recordIndex;
        assert (type instanceof SourceTypeBinding);
        SourceTypeBinding sourceType = type;
        FieldBinding[] recordComponents = sourceType.getImplicitComponentFields();
        int numArgs = 2 + recordComponents.length;
        this.contents[numArgsLocation++] = (byte)(numArgs >> 8);
        this.contents[numArgsLocation] = (byte)numArgs;
        String names = Arrays.stream(recordComponents).map(f -> new String(f.name)).reduce((s1, s2) -> String.valueOf(s1) + ";" + s2).orElse(Util.EMPTY_STRING);
        int namesIndex = this.constantPool.literalIndex(names);
        this.contents[localContentsOffset++] = (byte)(namesIndex >> 8);
        this.contents[localContentsOffset++] = (byte)namesIndex;
        if (recordComponents.length * 2 + localContentsOffset >= this.contents.length) {
            this.resizeContents(recordComponents.length * 2);
        }
        FieldBinding[] fieldBindingArray = recordComponents;
        int n = recordComponents.length;
        int n2 = 0;
        while (n2 < n) {
            FieldBinding field = fieldBindingArray[n2];
            int methodHandleIndex = this.constantPool.literalIndexForMethodHandleFieldRef(1, recordName, field.name, field.type.signature());
            this.contents[localContentsOffset++] = (byte)(methodHandleIndex >> 8);
            this.contents[localContentsOffset++] = (byte)methodHandleIndex;
            ++n2;
        }
        return localContentsOffset;
    }

    private int generateLineNumberAttribute() {
        int localContentsOffset = this.contentsOffset;
        int attributesNumber = 0;
        int[] pcToSourceMapTable = this.codeStream.pcToSourceMap;
        if (this.codeStream.pcToSourceMap != null && this.codeStream.pcToSourceMapSize != 0) {
            int lineNumberNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
            if (localContentsOffset + 8 >= this.contents.length) {
                this.resizeContents(8);
            }
            this.contents[localContentsOffset++] = (byte)(lineNumberNameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)lineNumberNameIndex;
            int lineNumberTableOffset = localContentsOffset;
            localContentsOffset += 6;
            int numberOfEntries = 0;
            int length = this.codeStream.pcToSourceMapSize;
            int i = 0;
            while (i < length) {
                if (localContentsOffset + 4 >= this.contents.length) {
                    this.resizeContents(4);
                }
                int pc = pcToSourceMapTable[i++];
                this.contents[localContentsOffset++] = (byte)(pc >> 8);
                this.contents[localContentsOffset++] = (byte)pc;
                int lineNumber = pcToSourceMapTable[i++];
                this.contents[localContentsOffset++] = (byte)(lineNumber >> 8);
                this.contents[localContentsOffset++] = (byte)lineNumber;
                ++numberOfEntries;
            }
            int lineNumberAttr_length = numberOfEntries * 4 + 2;
            this.contents[lineNumberTableOffset++] = (byte)(lineNumberAttr_length >> 24);
            this.contents[lineNumberTableOffset++] = (byte)(lineNumberAttr_length >> 16);
            this.contents[lineNumberTableOffset++] = (byte)(lineNumberAttr_length >> 8);
            this.contents[lineNumberTableOffset++] = (byte)lineNumberAttr_length;
            this.contents[lineNumberTableOffset++] = (byte)(numberOfEntries >> 8);
            this.contents[lineNumberTableOffset++] = (byte)numberOfEntries;
            attributesNumber = 1;
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }

    private int generateLineNumberAttribute(int problemLine) {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 12 >= this.contents.length) {
            this.resizeContents(12);
        }
        int lineNumberNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
        this.contents[localContentsOffset++] = (byte)(lineNumberNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)lineNumberNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 6;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 1;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = (byte)(problemLine >> 8);
        this.contents[localContentsOffset++] = (byte)problemLine;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateLocalVariableTableAttribute(int code_length, boolean methodDeclarationIsStatic, boolean isSynthetic) {
        boolean currentInstanceIsGeneric;
        int descriptorIndex;
        int attributesNumber = 0;
        int localContentsOffset = this.contentsOffset;
        int numberOfEntries = 0;
        int localVariableNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
        int maxOfEntries = 8 + 10 * (methodDeclarationIsStatic ? 0 : 1);
        int i = 0;
        while (i < this.codeStream.allLocalsCounter) {
            LocalVariableBinding localVariableBinding = this.codeStream.locals[i];
            maxOfEntries += 10 * localVariableBinding.initializationCount;
            ++i;
        }
        if (localContentsOffset + maxOfEntries >= this.contents.length) {
            this.resizeContents(maxOfEntries);
        }
        this.contents[localContentsOffset++] = (byte)(localVariableNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)localVariableNameIndex;
        int localVariableTableOffset = localContentsOffset;
        localContentsOffset += 6;
        SourceTypeBinding declaringClassBinding = null;
        if (!methodDeclarationIsStatic && !isSynthetic) {
            ++numberOfEntries;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = (byte)(code_length >> 8);
            this.contents[localContentsOffset++] = (byte)code_length;
            int nameIndex = this.constantPool.literalIndex(ConstantPool.This);
            this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)nameIndex;
            declaringClassBinding = (SourceTypeBinding)(this.codeStream.methodDeclaration != null ? this.codeStream.methodDeclaration.binding.declaringClass : this.codeStream.lambdaExpression.binding.declaringClass);
            descriptorIndex = this.constantPool.literalIndex(declaringClassBinding.signature());
            this.contents[localContentsOffset++] = (byte)(descriptorIndex >> 8);
            this.contents[localContentsOffset++] = (byte)descriptorIndex;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
        }
        int genericLocalVariablesCounter = 0;
        LocalVariableBinding[] genericLocalVariables = null;
        int numberOfGenericEntries = 0;
        int i2 = 0;
        int max = this.codeStream.allLocalsCounter;
        while (i2 < max) {
            LocalVariableBinding localVariable = this.codeStream.locals[i2];
            int initializationCount = localVariable.initializationCount;
            if (initializationCount != 0 && localVariable.declaration != null) {
                boolean isParameterizedType;
                TypeBinding localVariableTypeBinding = localVariable.type;
                boolean bl = isParameterizedType = localVariableTypeBinding.isParameterizedType() || localVariableTypeBinding.isTypeVariable();
                if (isParameterizedType) {
                    if (genericLocalVariables == null) {
                        genericLocalVariables = new LocalVariableBinding[max];
                    }
                    genericLocalVariables[genericLocalVariablesCounter++] = localVariable;
                }
                int j = 0;
                while (j < initializationCount) {
                    int startPC = localVariable.initializationPCs[j << 1];
                    int endPC = localVariable.initializationPCs[(j << 1) + 1];
                    if (startPC != endPC) {
                        if (endPC == -1) {
                            localVariable.declaringScope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidAttribute, new String(localVariable.name)), (ASTNode)((Object)localVariable.declaringScope.methodScope().referenceContext));
                        }
                        if (isParameterizedType) {
                            ++numberOfGenericEntries;
                        }
                        ++numberOfEntries;
                        this.contents[localContentsOffset++] = (byte)(startPC >> 8);
                        this.contents[localContentsOffset++] = (byte)startPC;
                        int length = endPC - startPC;
                        this.contents[localContentsOffset++] = (byte)(length >> 8);
                        this.contents[localContentsOffset++] = (byte)length;
                        int nameIndex = this.constantPool.literalIndex(localVariable.name);
                        this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)nameIndex;
                        descriptorIndex = this.constantPool.literalIndex(localVariableTypeBinding.signature());
                        this.contents[localContentsOffset++] = (byte)(descriptorIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)descriptorIndex;
                        int resolvedPosition = localVariable.resolvedPosition;
                        this.contents[localContentsOffset++] = (byte)(resolvedPosition >> 8);
                        this.contents[localContentsOffset++] = (byte)resolvedPosition;
                    }
                    ++j;
                }
            }
            ++i2;
        }
        int value = numberOfEntries * 10 + 2;
        this.contents[localVariableTableOffset++] = (byte)(value >> 24);
        this.contents[localVariableTableOffset++] = (byte)(value >> 16);
        this.contents[localVariableTableOffset++] = (byte)(value >> 8);
        this.contents[localVariableTableOffset++] = (byte)value;
        this.contents[localVariableTableOffset++] = (byte)(numberOfEntries >> 8);
        this.contents[localVariableTableOffset] = (byte)numberOfEntries;
        ++attributesNumber;
        boolean bl = currentInstanceIsGeneric = !methodDeclarationIsStatic && declaringClassBinding != null && declaringClassBinding.typeVariables != Binding.NO_TYPE_VARIABLES;
        if (genericLocalVariablesCounter != 0 || currentInstanceIsGeneric) {
            maxOfEntries = 8 + (numberOfGenericEntries += currentInstanceIsGeneric ? 1 : 0) * 10;
            if (localContentsOffset + maxOfEntries >= this.contents.length) {
                this.resizeContents(maxOfEntries);
            }
            int localVariableTypeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTypeTableName);
            this.contents[localContentsOffset++] = (byte)(localVariableTypeNameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)localVariableTypeNameIndex;
            value = numberOfGenericEntries * 10 + 2;
            this.contents[localContentsOffset++] = (byte)(value >> 24);
            this.contents[localContentsOffset++] = (byte)(value >> 16);
            this.contents[localContentsOffset++] = (byte)(value >> 8);
            this.contents[localContentsOffset++] = (byte)value;
            this.contents[localContentsOffset++] = (byte)(numberOfGenericEntries >> 8);
            this.contents[localContentsOffset++] = (byte)numberOfGenericEntries;
            if (currentInstanceIsGeneric) {
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = (byte)(code_length >> 8);
                this.contents[localContentsOffset++] = (byte)code_length;
                int nameIndex = this.constantPool.literalIndex(ConstantPool.This);
                this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                this.contents[localContentsOffset++] = (byte)nameIndex;
                descriptorIndex = this.constantPool.literalIndex(declaringClassBinding.genericTypeSignature());
                this.contents[localContentsOffset++] = (byte)(descriptorIndex >> 8);
                this.contents[localContentsOffset++] = (byte)descriptorIndex;
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = 0;
            }
            int i3 = 0;
            while (i3 < genericLocalVariablesCounter) {
                LocalVariableBinding localVariable = genericLocalVariables[i3];
                int j = 0;
                while (j < localVariable.initializationCount) {
                    int startPC = localVariable.initializationPCs[j << 1];
                    int endPC = localVariable.initializationPCs[(j << 1) + 1];
                    if (startPC != endPC) {
                        this.contents[localContentsOffset++] = (byte)(startPC >> 8);
                        this.contents[localContentsOffset++] = (byte)startPC;
                        int length = endPC - startPC;
                        this.contents[localContentsOffset++] = (byte)(length >> 8);
                        this.contents[localContentsOffset++] = (byte)length;
                        int nameIndex = this.constantPool.literalIndex(localVariable.name);
                        this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)nameIndex;
                        descriptorIndex = this.constantPool.literalIndex(localVariable.type.genericTypeSignature());
                        this.contents[localContentsOffset++] = (byte)(descriptorIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)descriptorIndex;
                        int resolvedPosition = localVariable.resolvedPosition;
                        this.contents[localContentsOffset++] = (byte)(resolvedPosition >> 8);
                        this.contents[localContentsOffset++] = (byte)resolvedPosition;
                    }
                    ++j;
                }
                ++i3;
            }
            ++attributesNumber;
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }

    public int generateMethodInfoAttributes(MethodBinding methodBinding) {
        char[] genericSignature;
        this.contentsOffset += 2;
        if (this.contentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        int attributesNumber = 0;
        ReferenceBinding[] thrownsExceptions = methodBinding.thrownExceptions;
        if (methodBinding.thrownExceptions != Binding.NO_EXCEPTIONS) {
            attributesNumber += this.generateExceptionsAttribute(thrownsExceptions);
        }
        if (methodBinding.isDeprecated()) {
            attributesNumber += this.generateDeprecatedAttribute();
        }
        if (this.targetJDK < 0x310000L) {
            if (methodBinding.isSynthetic()) {
                attributesNumber += this.generateSyntheticAttribute();
            }
            if (methodBinding.isVarargs()) {
                attributesNumber += this.generateVarargsAttribute();
            }
        }
        if ((genericSignature = methodBinding.genericSignature()) != null) {
            attributesNumber += this.generateSignatureAttribute(genericSignature);
        }
        if (this.targetJDK >= 0x300000L) {
            AbstractMethodDeclaration methodDeclaration = methodBinding.sourceMethod();
            if (methodBinding instanceof SyntheticMethodBinding) {
                SyntheticMethodBinding syntheticMethod = (SyntheticMethodBinding)methodBinding;
                if (syntheticMethod.purpose == 7 && CharOperation.equals(syntheticMethod.selector, syntheticMethod.targetMethod.selector)) {
                    methodDeclaration = ((SyntheticMethodBinding)methodBinding).targetMethod.sourceMethod();
                }
                if (syntheticMethod.recordComponentBinding != null) {
                    assert (methodDeclaration == null);
                    long rcMask = 0x20004000000000L;
                    ReferenceBinding declaringClass = methodBinding.declaringClass;
                    RecordComponent comp = this.getRecordComponent(declaringClass, methodBinding.selector);
                    if (comp != null) {
                        Annotation[] annotations = ASTNode.getRelevantAnnotations(comp.annotations, rcMask, null);
                        if (annotations != null) {
                            assert (!methodBinding.isConstructor());
                            attributesNumber += this.generateRuntimeAnnotations(annotations, 0x4000000000L);
                        }
                        if ((this.produceAttributes & 0x20) != 0) {
                            TypeReference compType;
                            ArrayList<AnnotationContext> allTypeAnnotationContexts = new ArrayList<AnnotationContext>();
                            if (annotations != null && (comp.bits & 0x100000) != 0) {
                                comp.getAllAnnotationContexts(20, allTypeAnnotationContexts);
                            }
                            if ((compType = comp.type) != null && (compType.bits & 0x100000) != 0) {
                                compType.getAllAnnotationContexts(20, allTypeAnnotationContexts);
                            }
                            int size = allTypeAnnotationContexts.size();
                            attributesNumber = this.completeRuntimeTypeAnnotations(attributesNumber, null, node -> size > 0, () -> allTypeAnnotationContexts);
                        }
                    }
                }
            }
            if (methodDeclaration != null) {
                Argument[] arguments;
                Annotation[] annotations = methodDeclaration.annotations;
                if (annotations != null) {
                    attributesNumber += this.generateRuntimeAnnotations(annotations, methodBinding.isConstructor() ? 0x10000000000L : 0x4000000000L);
                }
                if ((methodBinding.tagBits & 0x400L) != 0L && (arguments = methodDeclaration.arguments) != null) {
                    this.propagateRecordComponentArguments(methodDeclaration);
                    attributesNumber += this.generateRuntimeAnnotationsForParameters(arguments);
                }
            } else {
                Argument[] arguments;
                LambdaExpression lambda = methodBinding.sourceLambda();
                if (lambda != null && (methodBinding.tagBits & 0x400L) != 0L && (arguments = lambda.arguments()) != null) {
                    int parameterCount = methodBinding.parameters.length;
                    int argumentCount = arguments.length;
                    if (parameterCount > argumentCount) {
                        int redShift = parameterCount - argumentCount;
                        Argument[] argumentArray = arguments;
                        arguments = new Argument[parameterCount];
                        System.arraycopy(argumentArray, 0, arguments, redShift, argumentCount);
                        int i = 0;
                        while (i < redShift) {
                            arguments[i] = new Argument(CharOperation.NO_CHAR, 0L, null, 0);
                            ++i;
                        }
                    }
                    attributesNumber += this.generateRuntimeAnnotationsForParameters(arguments);
                }
            }
        }
        if ((methodBinding.tagBits & 0x80L) != 0L) {
            this.missingTypes = methodBinding.collectMissingTypes(this.missingTypes);
        }
        return attributesNumber;
    }

    private int completeRuntimeTypeAnnotations(int attributesNumber, ASTNode node, Predicate<ASTNode> condition, Supplier<List<AnnotationContext>> supplier) {
        List<AnnotationContext> allTypeAnnotationContexts;
        int invisibleTypeAnnotationsCounter = 0;
        int visibleTypeAnnotationsCounter = 0;
        if (condition.test(node) && (allTypeAnnotationContexts = supplier.get()).size() > 0) {
            AnnotationContext[] allTypeAnnotationContextsArray = new AnnotationContext[allTypeAnnotationContexts.size()];
            allTypeAnnotationContexts.toArray(allTypeAnnotationContextsArray);
            int j = 0;
            int max2 = allTypeAnnotationContextsArray.length;
            while (j < max2) {
                AnnotationContext annotationContext = allTypeAnnotationContextsArray[j];
                if ((annotationContext.visibility & 2) != 0) {
                    ++invisibleTypeAnnotationsCounter;
                } else {
                    ++visibleTypeAnnotationsCounter;
                }
                ++j;
            }
            attributesNumber += this.generateRuntimeTypeAnnotations(allTypeAnnotationContextsArray, visibleTypeAnnotationsCounter, invisibleTypeAnnotationsCounter);
        }
        return attributesNumber;
    }

    private void propagateRecordComponentArguments(AbstractMethodDeclaration methodDeclaration) {
        if ((methodDeclaration.bits & 0x600) == 0) {
            return;
        }
        ReferenceBinding declaringClass = methodDeclaration.binding.declaringClass;
        if (declaringClass instanceof SourceTypeBinding) {
            assert (declaringClass.isRecord());
            RecordComponentBinding[] rcbs = ((SourceTypeBinding)declaringClass).components();
            Argument[] arguments = methodDeclaration.arguments;
            int i = 0;
            int length = rcbs.length;
            while (i < length) {
                RecordComponentBinding rcb = rcbs[i];
                RecordComponent recordComponent = rcb.sourceRecordComponent();
                if ((recordComponent.bits & 0x100000) != 0) {
                    methodDeclaration.bits |= 0x100000;
                    arguments[i].bits |= 0x100000;
                }
                long rcMask = 0x20008000000000L;
                arguments[i].annotations = ASTNode.getRelevantAnnotations(recordComponent.annotations, rcMask, null);
                ++i;
            }
        }
    }

    public int generateMethodInfoAttributes(MethodBinding methodBinding, AnnotationMethodDeclaration declaration) {
        int attributesNumber = this.generateMethodInfoAttributes(methodBinding);
        int attributeOffset = this.contentsOffset;
        if ((declaration.modifiers & 0x20000) != 0) {
            attributesNumber += this.generateAnnotationDefaultAttribute(declaration, attributeOffset);
        }
        return attributesNumber;
    }

    public void generateMethodInfoHeader(MethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding, methodBinding.modifiers);
    }

    public void generateMethodInfoHeader(MethodBinding methodBinding, int accessFlags) {
        ++this.methodCount;
        if (this.contentsOffset + 10 >= this.contents.length) {
            this.resizeContents(10);
        }
        if (this.targetJDK < 0x310000L) {
            accessFlags &= 0xFFFFEF7F;
        }
        if ((methodBinding.tagBits & 0x200L) != 0L) {
            accessFlags &= 0xFFFFFFFD;
        }
        this.contents[this.contentsOffset++] = (byte)(accessFlags >> 8);
        this.contents[this.contentsOffset++] = (byte)accessFlags;
        int nameIndex = this.constantPool.literalIndex(methodBinding.selector);
        this.contents[this.contentsOffset++] = (byte)(nameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)nameIndex;
        int descriptorIndex = this.constantPool.literalIndex(methodBinding.signature(this));
        this.contents[this.contentsOffset++] = (byte)(descriptorIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)descriptorIndex;
    }

    public void addSyntheticDeserializeLambda(SyntheticMethodBinding methodBinding, SyntheticMethodBinding[] syntheticMethodBindings) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForDeserializeLambda(methodBinding, syntheticMethodBindings);
        int code_length = this.codeStream.position;
        if (code_length > 65535) {
            this.referenceBinding.scope.problemReporter().bytecodeExceeds64KLimit(methodBinding, this.referenceBinding.sourceStart(), this.referenceBinding.sourceEnd());
        }
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }

    public void generateMethodInfoHeaderForClinit() {
        ++this.methodCount;
        if (this.contentsOffset + 10 >= this.contents.length) {
            this.resizeContents(10);
        }
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 8;
        int nameIndex = this.constantPool.literalIndex(ConstantPool.Clinit);
        this.contents[this.contentsOffset++] = (byte)(nameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)nameIndex;
        int descriptorIndex = this.constantPool.literalIndex(ConstantPool.ClinitSignature);
        this.contents[this.contentsOffset++] = (byte)(descriptorIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)descriptorIndex;
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 1;
    }

    public void generateMissingAbstractMethods(MethodDeclaration[] methodDeclarations, CompilationResult compilationResult) {
        if (methodDeclarations != null) {
            TypeDeclaration currentDeclaration = this.referenceBinding.scope.referenceContext;
            int typeDeclarationSourceStart = currentDeclaration.sourceStart();
            int typeDeclarationSourceEnd = currentDeclaration.sourceEnd();
            int i = 0;
            int max = methodDeclarations.length;
            while (i < max) {
                MethodDeclaration methodDeclaration = methodDeclarations[i];
                MethodBinding methodBinding = methodDeclaration.binding;
                String readableName = new String(methodBinding.readableName());
                CategorizedProblem[] problems = compilationResult.problems;
                int problemsCount = compilationResult.problemCount;
                int j = 0;
                while (j < problemsCount) {
                    CategorizedProblem problem = problems[j];
                    if (problem != null && problem.getID() == 67109264 && problem.getMessage().indexOf(readableName) != -1 && problem.getSourceStart() >= typeDeclarationSourceStart && problem.getSourceEnd() <= typeDeclarationSourceEnd) {
                        this.addMissingAbstractProblemMethod(methodDeclaration, methodBinding, problem, compilationResult);
                    }
                    ++j;
                }
                ++i;
            }
        }
    }

    private void generateMissingTypesAttribute() {
        int initialSize = this.missingTypes.size();
        int[] missingTypesIndexes = new int[initialSize];
        int numberOfMissingTypes = 0;
        if (initialSize > 1) {
            Collections.sort(this.missingTypes, new Comparator<TypeBinding>(){

                @Override
                public int compare(TypeBinding o1, TypeBinding o2) {
                    return CharOperation.compareTo(o1.constantPoolName(), o2.constantPoolName());
                }
            });
        }
        int previousIndex = 0;
        int i = 0;
        while (i < initialSize) {
            int missingTypeIndex = this.constantPool.literalIndexForType(this.missingTypes.get(i));
            if (previousIndex != missingTypeIndex) {
                previousIndex = missingTypeIndex;
                missingTypesIndexes[numberOfMissingTypes++] = missingTypeIndex;
            }
            ++i;
        }
        int attributeLength = numberOfMissingTypes * 2 + 2;
        if (this.contentsOffset + attributeLength + 6 >= this.contents.length) {
            this.resizeContents(attributeLength + 6);
        }
        int missingTypesNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.MissingTypesName);
        this.contents[this.contentsOffset++] = (byte)(missingTypesNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)missingTypesNameIndex;
        this.contents[this.contentsOffset++] = (byte)(attributeLength >> 24);
        this.contents[this.contentsOffset++] = (byte)(attributeLength >> 16);
        this.contents[this.contentsOffset++] = (byte)(attributeLength >> 8);
        this.contents[this.contentsOffset++] = (byte)attributeLength;
        this.contents[this.contentsOffset++] = (byte)(numberOfMissingTypes >> 8);
        this.contents[this.contentsOffset++] = (byte)numberOfMissingTypes;
        int i2 = 0;
        while (i2 < numberOfMissingTypes) {
            int missingTypeIndex = missingTypesIndexes[i2];
            this.contents[this.contentsOffset++] = (byte)(missingTypeIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)missingTypeIndex;
            ++i2;
        }
    }

    private boolean jdk16packageInfoAnnotation(long annotationMask, long targetMask) {
        return this.targetJDK <= 0x320000L && targetMask == 0x80000000000L && annotationMask != 0L && (annotationMask & 0x80000000000L) == 0L;
    }

    private int generateRuntimeAnnotations(Annotation[] annotations, long targetMask) {
        int attributeLength;
        int currentAnnotationOffset;
        long annotationMask;
        Annotation annotation;
        int i;
        int counter;
        int annotationsLengthOffset;
        int attributesNumber = 0;
        int length = annotations.length;
        int visibleAnnotationsCounter = 0;
        int invisibleAnnotationsCounter = 0;
        int i2 = 0;
        while (i2 < length) {
            Annotation annotation2 = annotations[i2].getPersistibleAnnotation();
            if (annotation2 != null) {
                long annotationMask2;
                long l = annotationMask2 = annotation2.resolvedType != null ? annotation2.resolvedType.getAnnotationTagBits() & 0x20600FF840000000L : 0L;
                if (annotationMask2 == 0L || (annotationMask2 & targetMask) != 0L || this.jdk16packageInfoAnnotation(annotationMask2, targetMask)) {
                    if (annotation2.isRuntimeInvisible() || annotation2.isRuntimeTypeInvisible()) {
                        ++invisibleAnnotationsCounter;
                    } else if (annotation2.isRuntimeVisible() || annotation2.isRuntimeTypeVisible()) {
                        ++visibleAnnotationsCounter;
                    }
                }
            }
            ++i2;
        }
        int annotationAttributeOffset = this.contentsOffset;
        if (invisibleAnnotationsCounter != 0) {
            if (this.contentsOffset + 10 >= this.contents.length) {
                this.resizeContents(10);
            }
            int runtimeInvisibleAnnotationsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(runtimeInvisibleAnnotationsAttributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)runtimeInvisibleAnnotationsAttributeNameIndex;
            int attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            annotationsLengthOffset = this.contentsOffset;
            this.contentsOffset += 2;
            counter = 0;
            i = 0;
            while (i < length) {
                if (invisibleAnnotationsCounter == 0) break;
                annotation = annotations[i].getPersistibleAnnotation();
                if (annotation != null) {
                    long l = annotationMask = annotation.resolvedType != null ? annotation.resolvedType.getAnnotationTagBits() & 0x20600FF840000000L : 0L;
                    if ((annotationMask == 0L || (annotationMask & targetMask) != 0L || this.jdk16packageInfoAnnotation(annotationMask, targetMask)) && (annotation.isRuntimeInvisible() || annotation.isRuntimeTypeInvisible())) {
                        currentAnnotationOffset = this.contentsOffset;
                        this.generateAnnotation(annotation, currentAnnotationOffset);
                        --invisibleAnnotationsCounter;
                        if (this.contentsOffset != currentAnnotationOffset) {
                            ++counter;
                        }
                    }
                }
                ++i;
            }
            if (counter != 0) {
                this.contents[annotationsLengthOffset++] = (byte)(counter >> 8);
                this.contents[annotationsLengthOffset++] = (byte)counter;
                attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            } else {
                this.contentsOffset = annotationAttributeOffset;
            }
        }
        annotationAttributeOffset = this.contentsOffset;
        if (visibleAnnotationsCounter != 0) {
            if (this.contentsOffset + 10 >= this.contents.length) {
                this.resizeContents(10);
            }
            int runtimeVisibleAnnotationsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(runtimeVisibleAnnotationsAttributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)runtimeVisibleAnnotationsAttributeNameIndex;
            int attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            annotationsLengthOffset = this.contentsOffset;
            this.contentsOffset += 2;
            counter = 0;
            i = 0;
            while (i < length) {
                if (visibleAnnotationsCounter == 0) break;
                annotation = annotations[i].getPersistibleAnnotation();
                if (annotation != null) {
                    long l = annotationMask = annotation.resolvedType != null ? annotation.resolvedType.getAnnotationTagBits() & 0x20600FF840000000L : 0L;
                    if ((annotationMask == 0L || (annotationMask & targetMask) != 0L || this.jdk16packageInfoAnnotation(annotationMask, targetMask)) && (annotation.isRuntimeVisible() || annotation.isRuntimeTypeVisible())) {
                        --visibleAnnotationsCounter;
                        currentAnnotationOffset = this.contentsOffset;
                        this.generateAnnotation(annotation, currentAnnotationOffset);
                        if (this.contentsOffset != currentAnnotationOffset) {
                            ++counter;
                        }
                    }
                }
                ++i;
            }
            if (counter != 0) {
                this.contents[annotationsLengthOffset++] = (byte)(counter >> 8);
                this.contents[annotationsLengthOffset++] = (byte)counter;
                attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            } else {
                this.contentsOffset = annotationAttributeOffset;
            }
        }
        return attributesNumber;
    }

    private int generateRuntimeAnnotationsForParameters(Argument[] arguments) {
        int currentAnnotationOffset;
        long annotationMask;
        Annotation annotation;
        int max;
        int j;
        Annotation[] annotations;
        Argument argument;
        int counter;
        int attributeLengthOffset;
        int attributeNameIndex;
        int argumentsLength = arguments.length;
        int invisibleParametersAnnotationsCounter = 0;
        int visibleParametersAnnotationsCounter = 0;
        int[][] annotationsCounters = new int[argumentsLength][2];
        int i = 0;
        while (i < argumentsLength) {
            Argument argument2 = arguments[i];
            Annotation[] annotations2 = argument2.annotations;
            if (annotations2 != null) {
                int j2 = 0;
                int max2 = annotations2.length;
                while (j2 < max2) {
                    Annotation annotation2 = annotations2[j2].getPersistibleAnnotation();
                    if (annotation2 != null) {
                        long annotationMask2;
                        long l = annotationMask2 = annotation2.resolvedType != null ? annotation2.resolvedType.getAnnotationTagBits() & 0x20600FF840000000L : 0L;
                        if (annotationMask2 == 0L || (annotationMask2 & 0x8000000000L) != 0L) {
                            if (annotation2.isRuntimeInvisible()) {
                                int[] nArray = annotationsCounters[i];
                                nArray[1] = nArray[1] + 1;
                                ++invisibleParametersAnnotationsCounter;
                            } else if (annotation2.isRuntimeVisible()) {
                                int[] nArray = annotationsCounters[i];
                                nArray[0] = nArray[0] + 1;
                                ++visibleParametersAnnotationsCounter;
                            }
                        }
                    }
                    ++j2;
                }
            }
            ++i;
        }
        int attributesNumber = 0;
        int annotationAttributeOffset = this.contentsOffset;
        if (invisibleParametersAnnotationsCounter != 0) {
            int globalCounter = 0;
            if (this.contentsOffset + 7 >= this.contents.length) {
                this.resizeContents(7);
            }
            attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleParameterAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(attributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)attributeNameIndex;
            attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            this.contents[this.contentsOffset++] = (byte)argumentsLength;
            int i2 = 0;
            while (i2 < argumentsLength) {
                if (this.contentsOffset + 2 >= this.contents.length) {
                    this.resizeContents(2);
                }
                if (invisibleParametersAnnotationsCounter == 0) {
                    this.contents[this.contentsOffset++] = 0;
                    this.contents[this.contentsOffset++] = 0;
                } else {
                    int numberOfInvisibleAnnotations = annotationsCounters[i2][1];
                    int invisibleAnnotationsOffset = this.contentsOffset;
                    this.contentsOffset += 2;
                    counter = 0;
                    if (numberOfInvisibleAnnotations != 0) {
                        argument = arguments[i2];
                        annotations = argument.annotations;
                        j = 0;
                        max = annotations.length;
                        while (j < max) {
                            annotation = annotations[j].getPersistibleAnnotation();
                            if (annotation != null) {
                                long l = annotationMask = annotation.resolvedType != null ? annotation.resolvedType.getAnnotationTagBits() & 0x20600FF840000000L : 0L;
                                if ((annotationMask == 0L || (annotationMask & 0x8000000000L) != 0L) && annotation.isRuntimeInvisible()) {
                                    currentAnnotationOffset = this.contentsOffset;
                                    this.generateAnnotation(annotation, currentAnnotationOffset);
                                    if (this.contentsOffset != currentAnnotationOffset) {
                                        ++counter;
                                        ++globalCounter;
                                    }
                                    --invisibleParametersAnnotationsCounter;
                                }
                            }
                            ++j;
                        }
                    }
                    this.contents[invisibleAnnotationsOffset++] = (byte)(counter >> 8);
                    this.contents[invisibleAnnotationsOffset] = (byte)counter;
                }
                ++i2;
            }
            if (globalCounter != 0) {
                int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            } else {
                this.contentsOffset = annotationAttributeOffset;
            }
        }
        if (visibleParametersAnnotationsCounter != 0) {
            int globalCounter = 0;
            if (this.contentsOffset + 7 >= this.contents.length) {
                this.resizeContents(7);
            }
            attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleParameterAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(attributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)attributeNameIndex;
            attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            this.contents[this.contentsOffset++] = (byte)argumentsLength;
            int i3 = 0;
            while (i3 < argumentsLength) {
                if (this.contentsOffset + 2 >= this.contents.length) {
                    this.resizeContents(2);
                }
                if (visibleParametersAnnotationsCounter == 0) {
                    this.contents[this.contentsOffset++] = 0;
                    this.contents[this.contentsOffset++] = 0;
                } else {
                    int numberOfVisibleAnnotations = annotationsCounters[i3][0];
                    int visibleAnnotationsOffset = this.contentsOffset;
                    this.contentsOffset += 2;
                    counter = 0;
                    if (numberOfVisibleAnnotations != 0) {
                        argument = arguments[i3];
                        annotations = argument.annotations;
                        j = 0;
                        max = annotations.length;
                        while (j < max) {
                            annotation = annotations[j].getPersistibleAnnotation();
                            if (annotation != null) {
                                long l = annotationMask = annotation.resolvedType != null ? annotation.resolvedType.getAnnotationTagBits() & 0x20600FF840000000L : 0L;
                                if ((annotationMask == 0L || (annotationMask & 0x8000000000L) != 0L) && annotation.isRuntimeVisible()) {
                                    currentAnnotationOffset = this.contentsOffset;
                                    this.generateAnnotation(annotation, currentAnnotationOffset);
                                    if (this.contentsOffset != currentAnnotationOffset) {
                                        ++counter;
                                        ++globalCounter;
                                    }
                                    --visibleParametersAnnotationsCounter;
                                }
                            }
                            ++j;
                        }
                    }
                    this.contents[visibleAnnotationsOffset++] = (byte)(counter >> 8);
                    this.contents[visibleAnnotationsOffset] = (byte)counter;
                }
                ++i3;
            }
            if (globalCounter != 0) {
                int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            } else {
                this.contentsOffset = annotationAttributeOffset;
            }
        }
        return attributesNumber;
    }

    private int generateRuntimeTypeAnnotations(AnnotationContext[] annotationContexts, int visibleTypeAnnotationsNumber, int invisibleTypeAnnotationsNumber) {
        int attributeLength;
        int currentAnnotationOffset;
        AnnotationContext annotationContext;
        int i;
        int counter;
        int annotationsLengthOffset;
        int attributeLengthOffset;
        int attributesNumber = 0;
        int length = annotationContexts.length;
        int visibleTypeAnnotationsCounter = visibleTypeAnnotationsNumber;
        int invisibleTypeAnnotationsCounter = invisibleTypeAnnotationsNumber;
        int annotationAttributeOffset = this.contentsOffset;
        if (invisibleTypeAnnotationsCounter != 0) {
            if (this.contentsOffset + 10 >= this.contents.length) {
                this.resizeContents(10);
            }
            int runtimeInvisibleAnnotationsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(runtimeInvisibleAnnotationsAttributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)runtimeInvisibleAnnotationsAttributeNameIndex;
            attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            annotationsLengthOffset = this.contentsOffset;
            this.contentsOffset += 2;
            counter = 0;
            i = 0;
            while (i < length) {
                if (invisibleTypeAnnotationsCounter == 0) break;
                annotationContext = annotationContexts[i];
                if ((annotationContext.visibility & 2) != 0) {
                    currentAnnotationOffset = this.contentsOffset;
                    this.generateTypeAnnotation(annotationContext, currentAnnotationOffset);
                    --invisibleTypeAnnotationsCounter;
                    if (this.contentsOffset != currentAnnotationOffset) {
                        ++counter;
                    }
                }
                ++i;
            }
            if (counter != 0) {
                this.contents[annotationsLengthOffset++] = (byte)(counter >> 8);
                this.contents[annotationsLengthOffset++] = (byte)counter;
                attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            } else {
                this.contentsOffset = annotationAttributeOffset;
            }
        }
        annotationAttributeOffset = this.contentsOffset;
        if (visibleTypeAnnotationsCounter != 0) {
            if (this.contentsOffset + 10 >= this.contents.length) {
                this.resizeContents(10);
            }
            int runtimeVisibleAnnotationsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(runtimeVisibleAnnotationsAttributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)runtimeVisibleAnnotationsAttributeNameIndex;
            attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            annotationsLengthOffset = this.contentsOffset;
            this.contentsOffset += 2;
            counter = 0;
            i = 0;
            while (i < length) {
                if (visibleTypeAnnotationsCounter == 0) break;
                annotationContext = annotationContexts[i];
                if ((annotationContext.visibility & 1) != 0) {
                    --visibleTypeAnnotationsCounter;
                    currentAnnotationOffset = this.contentsOffset;
                    this.generateTypeAnnotation(annotationContext, currentAnnotationOffset);
                    if (this.contentsOffset != currentAnnotationOffset) {
                        ++counter;
                    }
                }
                ++i;
            }
            if (counter != 0) {
                this.contents[annotationsLengthOffset++] = (byte)(counter >> 8);
                this.contents[annotationsLengthOffset++] = (byte)counter;
                attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            } else {
                this.contentsOffset = annotationAttributeOffset;
            }
        }
        return attributesNumber;
    }

    private int generateMethodParameters(MethodBinding binding) {
        boolean needSynthetics;
        if (binding.sourceLambda() != null) {
            return 0;
        }
        int initialContentsOffset = this.contentsOffset;
        int length = 0;
        AbstractMethodDeclaration methodDeclaration = binding.sourceMethod();
        boolean isConstructor = binding.isConstructor();
        TypeBinding[] targetParameters = binding.parameters;
        ReferenceBinding declaringClass = binding.declaringClass;
        if (declaringClass.isEnum()) {
            if (isConstructor) {
                length = this.writeArgumentName(ConstantPool.EnumName, 4096, length);
                length = this.writeArgumentName(ConstantPool.EnumOrdinal, 4096, length);
            } else if (binding instanceof SyntheticMethodBinding && CharOperation.equals(ConstantPool.ValueOf, binding.selector)) {
                length = this.writeArgumentName(ConstantPool.Name, 32768, length);
                targetParameters = Binding.NO_PARAMETERS;
            }
        }
        boolean bl = needSynthetics = isConstructor && declaringClass.isNestedType();
        if (needSynthetics) {
            boolean anonymousWithLocalSuper = declaringClass.isAnonymousType() && declaringClass.superclass().isLocalType();
            boolean anonymousWithNestedSuper = declaringClass.isAnonymousType() && declaringClass.superclass().isNestedType();
            boolean isImplicitlyDeclared = (!declaringClass.isPrivate() || declaringClass.isAnonymousType()) && !anonymousWithLocalSuper;
            ReferenceBinding[] syntheticArgumentTypes = declaringClass.syntheticEnclosingInstanceTypes();
            if (syntheticArgumentTypes != null) {
                int i = 0;
                int count = syntheticArgumentTypes.length;
                while (i < count) {
                    boolean couldForwardToMandated = anonymousWithNestedSuper ? declaringClass.superclass().enclosingType().equals(syntheticArgumentTypes[i]) : true;
                    int modifier = couldForwardToMandated && isImplicitlyDeclared ? 32768 : 4096;
                    char[] name = CharOperation.concat(TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, String.valueOf(i).toCharArray());
                    length = this.writeArgumentName(name, modifier | 0x10, length);
                    ++i;
                }
            }
            if (binding instanceof SyntheticMethodBinding) {
                targetParameters = ((SyntheticMethodBinding)binding).targetMethod.parameters;
                methodDeclaration = ((SyntheticMethodBinding)binding).targetMethod.sourceMethod();
            }
        }
        if (targetParameters != Binding.NO_PARAMETERS) {
            Argument[] arguments = null;
            if (methodDeclaration != null && methodDeclaration.arguments != null) {
                arguments = methodDeclaration.arguments;
            }
            int i = 0;
            int max = targetParameters.length;
            int argumentsLength = arguments != null ? arguments.length : 0;
            while (i < max) {
                if (argumentsLength > i && arguments[i] != null) {
                    Argument argument = arguments[i];
                    length = this.writeArgumentName(argument.name, argument.binding.modifiers, length);
                } else {
                    length = this.writeArgumentName(null, 4096, length);
                }
                ++i;
            }
        }
        if (needSynthetics) {
            SyntheticArgumentBinding[] syntheticOuterArguments = declaringClass.syntheticOuterLocalVariables();
            int count = syntheticOuterArguments == null ? 0 : syntheticOuterArguments.length;
            int i = 0;
            while (i < count) {
                length = this.writeArgumentName(syntheticOuterArguments[i].name, syntheticOuterArguments[i].modifiers | 0x1000, length);
                ++i;
            }
            i = targetParameters.length;
            int extraLength = binding.parameters.length;
            while (i < extraLength) {
                TypeBinding parameter = binding.parameters[i];
                length = this.writeArgumentName(parameter.constantPoolName(), 4096, length);
                ++i;
            }
        }
        if (length > 0) {
            int attributeLength = 1 + 4 * length;
            if (this.contentsOffset + 6 + attributeLength >= this.contents.length) {
                this.resizeContents(6 + attributeLength);
            }
            int methodParametersNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.MethodParametersName);
            this.contents[initialContentsOffset++] = (byte)(methodParametersNameIndex >> 8);
            this.contents[initialContentsOffset++] = (byte)methodParametersNameIndex;
            this.contents[initialContentsOffset++] = (byte)(attributeLength >> 24);
            this.contents[initialContentsOffset++] = (byte)(attributeLength >> 16);
            this.contents[initialContentsOffset++] = (byte)(attributeLength >> 8);
            this.contents[initialContentsOffset++] = (byte)attributeLength;
            this.contents[initialContentsOffset++] = (byte)length;
            return 1;
        }
        return 0;
    }

    private int writeArgumentName(char[] name, int modifiers, int oldLength) {
        int ensureRoomForBytes = 4;
        if (oldLength == 0) {
            ensureRoomForBytes += 7;
            this.contentsOffset += 7;
        }
        if (this.contentsOffset + ensureRoomForBytes > this.contents.length) {
            this.resizeContents(ensureRoomForBytes);
        }
        int parameterNameIndex = name == null ? 0 : this.constantPool.literalIndex(name);
        this.contents[this.contentsOffset++] = (byte)(parameterNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)parameterNameIndex;
        int flags = modifiers & 0x9010;
        this.contents[this.contentsOffset++] = (byte)(flags >> 8);
        this.contents[this.contentsOffset++] = (byte)flags;
        return oldLength + 1;
    }

    private int generateSignatureAttribute(char[] genericSignature) {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 8 >= this.contents.length) {
            this.resizeContents(8);
        }
        int signatureAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.SignatureName);
        this.contents[localContentsOffset++] = (byte)(signatureAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)signatureAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 2;
        int signatureIndex = this.constantPool.literalIndex(genericSignature);
        this.contents[localContentsOffset++] = (byte)(signatureIndex >> 8);
        this.contents[localContentsOffset++] = (byte)signatureIndex;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateSourceAttribute(String fullFileName) {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 8 >= this.contents.length) {
            this.resizeContents(8);
        }
        int sourceAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.SourceName);
        this.contents[localContentsOffset++] = (byte)(sourceAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)sourceAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 2;
        int fileNameIndex = this.constantPool.literalIndex(fullFileName.toCharArray());
        this.contents[localContentsOffset++] = (byte)(fileNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)fileNameIndex;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private int generateStackMapAttribute(MethodBinding methodBinding, int code_length, int codeAttributeOffset, int max_locals, boolean isClinit, Scope scope) {
        HashMap<Integer, StackMapFrame> frames;
        List<StackMapFrame> realFrames;
        int numberOfFrames;
        int attributesNumber = 0;
        int localContentsOffset = this.contentsOffset;
        StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
        stackMapFrameCodeStream.removeFramePosition(code_length);
        if (stackMapFrameCodeStream.hasFramePositions() && (numberOfFrames = (realFrames = this.traverse(isClinit ? null : methodBinding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames = new HashMap<Integer, StackMapFrame>(), isClinit, scope)).size()) > 1) {
            int stackMapTableAttributeOffset = localContentsOffset;
            if (localContentsOffset + 8 >= this.contents.length) {
                this.resizeContents(8);
            }
            int stackMapAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
            this.contents[localContentsOffset++] = (byte)(stackMapAttributeNameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)stackMapAttributeNameIndex;
            int stackMapAttributeLengthOffset = localContentsOffset;
            if ((localContentsOffset += 4) + 4 >= this.contents.length) {
                this.resizeContents(4);
            }
            int numberOfFramesOffset = localContentsOffset;
            if ((localContentsOffset += 2) + 2 >= this.contents.length) {
                this.resizeContents(2);
            }
            StackMapFrame currentFrame = realFrames.get(0);
            int j = 1;
            while (j < numberOfFrames) {
                currentFrame = realFrames.get(j);
                int frameOffset = currentFrame.pc;
                if (localContentsOffset + 5 >= this.contents.length) {
                    this.resizeContents(5);
                }
                this.contents[localContentsOffset++] = (byte)(frameOffset >> 8);
                this.contents[localContentsOffset++] = (byte)frameOffset;
                int numberOfLocalOffset = localContentsOffset;
                localContentsOffset += 2;
                int numberOfLocalEntries = 0;
                int numberOfLocals = currentFrame.getNumberOfLocals();
                int numberOfEntries = 0;
                int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
                int i = 0;
                while (i < localsLength && numberOfLocalEntries < numberOfLocals) {
                    VerificationTypeInfo info;
                    if (localContentsOffset + 3 >= this.contents.length) {
                        this.resizeContents(3);
                    }
                    if ((info = currentFrame.locals[i]) == null) {
                        this.contents[localContentsOffset++] = 0;
                    } else {
                        block0 : switch (info.id()) {
                            case 2: 
                            case 3: 
                            case 4: 
                            case 5: 
                            case 10: {
                                this.contents[localContentsOffset++] = 1;
                                break;
                            }
                            case 9: {
                                this.contents[localContentsOffset++] = 2;
                                break;
                            }
                            case 7: {
                                this.contents[localContentsOffset++] = 4;
                                ++i;
                                break;
                            }
                            case 8: {
                                this.contents[localContentsOffset++] = 3;
                                ++i;
                                break;
                            }
                            case 12: {
                                this.contents[localContentsOffset++] = 5;
                                break;
                            }
                            default: {
                                this.contents[localContentsOffset++] = (byte)info.tag;
                                switch (info.tag) {
                                    case 8: {
                                        int offset = info.offset;
                                        this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                        this.contents[localContentsOffset++] = (byte)offset;
                                        break block0;
                                    }
                                    case 7: {
                                        int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
                                        this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                        this.contents[localContentsOffset++] = (byte)indexForType;
                                    }
                                }
                            }
                        }
                        ++numberOfLocalEntries;
                    }
                    ++numberOfEntries;
                    ++i;
                }
                if (localContentsOffset + 4 >= this.contents.length) {
                    this.resizeContents(4);
                }
                this.contents[numberOfLocalOffset++] = (byte)(numberOfEntries >> 8);
                this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
                int numberOfStackItems = currentFrame.numberOfStackItems;
                this.contents[localContentsOffset++] = (byte)(numberOfStackItems >> 8);
                this.contents[localContentsOffset++] = (byte)numberOfStackItems;
                int i2 = 0;
                while (i2 < numberOfStackItems) {
                    VerificationTypeInfo info;
                    if (localContentsOffset + 3 >= this.contents.length) {
                        this.resizeContents(3);
                    }
                    if ((info = currentFrame.stackItems[i2]) == null) {
                        this.contents[localContentsOffset++] = 0;
                    } else {
                        block11 : switch (info.id()) {
                            case 2: 
                            case 3: 
                            case 4: 
                            case 5: 
                            case 10: {
                                this.contents[localContentsOffset++] = 1;
                                break;
                            }
                            case 9: {
                                this.contents[localContentsOffset++] = 2;
                                break;
                            }
                            case 7: {
                                this.contents[localContentsOffset++] = 4;
                                break;
                            }
                            case 8: {
                                this.contents[localContentsOffset++] = 3;
                                break;
                            }
                            case 12: {
                                this.contents[localContentsOffset++] = 5;
                                break;
                            }
                            default: {
                                this.contents[localContentsOffset++] = (byte)info.tag;
                                switch (info.tag) {
                                    case 8: {
                                        int offset = info.offset;
                                        this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                        this.contents[localContentsOffset++] = (byte)offset;
                                        break block11;
                                    }
                                    case 7: {
                                        int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
                                        this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                        this.contents[localContentsOffset++] = (byte)indexForType;
                                    }
                                }
                            }
                        }
                    }
                    ++i2;
                }
                ++j;
            }
            if (--numberOfFrames != 0) {
                this.contents[numberOfFramesOffset++] = (byte)(numberOfFrames >> 8);
                this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
                int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
                this.contents[stackMapAttributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[stackMapAttributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[stackMapAttributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[stackMapAttributeLengthOffset] = (byte)attributeLength;
                ++attributesNumber;
            } else {
                localContentsOffset = stackMapTableAttributeOffset;
            }
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }

    private int generateStackMapTableAttribute(MethodBinding methodBinding, int code_length, int codeAttributeOffset, int max_locals, boolean isClinit, Scope scope) {
        HashMap<Integer, StackMapFrame> frames;
        List<StackMapFrame> realFrames;
        int numberOfFrames;
        int attributesNumber = 0;
        int localContentsOffset = this.contentsOffset;
        StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
        stackMapFrameCodeStream.removeFramePosition(code_length);
        if (stackMapFrameCodeStream.hasFramePositions() && (numberOfFrames = (realFrames = this.traverse(isClinit ? null : methodBinding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames = new HashMap<Integer, StackMapFrame>(), isClinit, scope)).size()) > 1) {
            int stackMapTableAttributeOffset = localContentsOffset;
            if (localContentsOffset + 8 >= this.contents.length) {
                this.resizeContents(8);
            }
            int stackMapTableAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
            this.contents[localContentsOffset++] = (byte)(stackMapTableAttributeNameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)stackMapTableAttributeNameIndex;
            int stackMapTableAttributeLengthOffset = localContentsOffset;
            if ((localContentsOffset += 4) + 4 >= this.contents.length) {
                this.resizeContents(4);
            }
            int numberOfFramesOffset = localContentsOffset;
            if ((localContentsOffset += 2) + 2 >= this.contents.length) {
                this.resizeContents(2);
            }
            StackMapFrame currentFrame = realFrames.get(0);
            StackMapFrame prevFrame = null;
            int j = 1;
            while (j < numberOfFrames) {
                prevFrame = currentFrame;
                currentFrame = realFrames.get(j);
                int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
                block0 : switch (currentFrame.getFrameType(prevFrame)) {
                    case 2: {
                        int indexForType;
                        int offset;
                        if (localContentsOffset + 3 >= this.contents.length) {
                            this.resizeContents(3);
                        }
                        int numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
                        this.contents[localContentsOffset++] = (byte)(251 + numberOfDifferentLocals);
                        this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                        this.contents[localContentsOffset++] = (byte)offsetDelta;
                        int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
                        int numberOfLocals = currentFrame.getNumberOfLocals();
                        int i = index;
                        while (i < currentFrame.locals.length && numberOfDifferentLocals > 0) {
                            VerificationTypeInfo info;
                            if (localContentsOffset + 6 >= this.contents.length) {
                                this.resizeContents(6);
                            }
                            if ((info = currentFrame.locals[i]) == null) {
                                this.contents[localContentsOffset++] = 0;
                            } else {
                                block8 : switch (info.id()) {
                                    case 2: 
                                    case 3: 
                                    case 4: 
                                    case 5: 
                                    case 10: {
                                        this.contents[localContentsOffset++] = 1;
                                        break;
                                    }
                                    case 9: {
                                        this.contents[localContentsOffset++] = 2;
                                        break;
                                    }
                                    case 7: {
                                        this.contents[localContentsOffset++] = 4;
                                        ++i;
                                        break;
                                    }
                                    case 8: {
                                        this.contents[localContentsOffset++] = 3;
                                        ++i;
                                        break;
                                    }
                                    case 12: {
                                        this.contents[localContentsOffset++] = 5;
                                        break;
                                    }
                                    default: {
                                        this.contents[localContentsOffset++] = (byte)info.tag;
                                        switch (info.tag) {
                                            case 8: {
                                                offset = info.offset;
                                                this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                                this.contents[localContentsOffset++] = (byte)offset;
                                                break block8;
                                            }
                                            case 7: {
                                                indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
                                                this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                                this.contents[localContentsOffset++] = (byte)indexForType;
                                            }
                                        }
                                    }
                                }
                                --numberOfDifferentLocals;
                            }
                            ++i;
                        }
                        break;
                    }
                    case 0: {
                        if (localContentsOffset + 1 >= this.contents.length) {
                            this.resizeContents(1);
                        }
                        this.contents[localContentsOffset++] = (byte)offsetDelta;
                        break;
                    }
                    case 3: {
                        if (localContentsOffset + 3 >= this.contents.length) {
                            this.resizeContents(3);
                        }
                        this.contents[localContentsOffset++] = -5;
                        this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                        this.contents[localContentsOffset++] = (byte)offsetDelta;
                        break;
                    }
                    case 1: {
                        if (localContentsOffset + 3 >= this.contents.length) {
                            this.resizeContents(3);
                        }
                        int numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
                        this.contents[localContentsOffset++] = (byte)(251 - numberOfDifferentLocals);
                        this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                        this.contents[localContentsOffset++] = (byte)offsetDelta;
                        break;
                    }
                    case 5: {
                        int indexForType;
                        int offset;
                        if (localContentsOffset + 4 >= this.contents.length) {
                            this.resizeContents(4);
                        }
                        this.contents[localContentsOffset++] = (byte)(offsetDelta + 64);
                        if (currentFrame.stackItems[0] == null) {
                            this.contents[localContentsOffset++] = 0;
                            break;
                        }
                        switch (currentFrame.stackItems[0].id()) {
                            case 2: 
                            case 3: 
                            case 4: 
                            case 5: 
                            case 10: {
                                this.contents[localContentsOffset++] = 1;
                                break block0;
                            }
                            case 9: {
                                this.contents[localContentsOffset++] = 2;
                                break block0;
                            }
                            case 7: {
                                this.contents[localContentsOffset++] = 4;
                                break block0;
                            }
                            case 8: {
                                this.contents[localContentsOffset++] = 3;
                                break block0;
                            }
                            case 12: {
                                this.contents[localContentsOffset++] = 5;
                                break block0;
                            }
                        }
                        VerificationTypeInfo info = currentFrame.stackItems[0];
                        byte tag = (byte)info.tag;
                        this.contents[localContentsOffset++] = tag;
                        switch (tag) {
                            case 8: {
                                offset = info.offset;
                                this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                this.contents[localContentsOffset++] = (byte)offset;
                                break;
                            }
                            case 7: {
                                indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
                                this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                this.contents[localContentsOffset++] = (byte)indexForType;
                            }
                        }
                        break;
                    }
                    case 6: {
                        int indexForType;
                        int offset;
                        if (localContentsOffset + 6 >= this.contents.length) {
                            this.resizeContents(6);
                        }
                        this.contents[localContentsOffset++] = -9;
                        this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                        this.contents[localContentsOffset++] = (byte)offsetDelta;
                        if (currentFrame.stackItems[0] == null) {
                            this.contents[localContentsOffset++] = 0;
                            break;
                        }
                        switch (currentFrame.stackItems[0].id()) {
                            case 2: 
                            case 3: 
                            case 4: 
                            case 5: 
                            case 10: {
                                this.contents[localContentsOffset++] = 1;
                                break block0;
                            }
                            case 9: {
                                this.contents[localContentsOffset++] = 2;
                                break block0;
                            }
                            case 7: {
                                this.contents[localContentsOffset++] = 4;
                                break block0;
                            }
                            case 8: {
                                this.contents[localContentsOffset++] = 3;
                                break block0;
                            }
                            case 12: {
                                this.contents[localContentsOffset++] = 5;
                                break block0;
                            }
                        }
                        VerificationTypeInfo info = currentFrame.stackItems[0];
                        byte tag = (byte)info.tag;
                        this.contents[localContentsOffset++] = tag;
                        switch (tag) {
                            case 8: {
                                offset = info.offset;
                                this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                this.contents[localContentsOffset++] = (byte)offset;
                                break;
                            }
                            case 7: {
                                indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
                                this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                this.contents[localContentsOffset++] = (byte)indexForType;
                            }
                        }
                        break;
                    }
                    default: {
                        if (localContentsOffset + 5 >= this.contents.length) {
                            this.resizeContents(5);
                        }
                        this.contents[localContentsOffset++] = -1;
                        this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                        this.contents[localContentsOffset++] = (byte)offsetDelta;
                        int numberOfLocalOffset = localContentsOffset;
                        localContentsOffset += 2;
                        int numberOfLocalEntries = 0;
                        int numberOfLocals = currentFrame.getNumberOfLocals();
                        int numberOfEntries = 0;
                        int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
                        int i = 0;
                        while (i < localsLength && numberOfLocalEntries < numberOfLocals) {
                            VerificationTypeInfo info;
                            if (localContentsOffset + 3 >= this.contents.length) {
                                this.resizeContents(3);
                            }
                            if ((info = currentFrame.locals[i]) == null) {
                                this.contents[localContentsOffset++] = 0;
                            } else {
                                block41 : switch (info.id()) {
                                    case 2: 
                                    case 3: 
                                    case 4: 
                                    case 5: 
                                    case 10: {
                                        this.contents[localContentsOffset++] = 1;
                                        break;
                                    }
                                    case 9: {
                                        this.contents[localContentsOffset++] = 2;
                                        break;
                                    }
                                    case 7: {
                                        this.contents[localContentsOffset++] = 4;
                                        ++i;
                                        break;
                                    }
                                    case 8: {
                                        this.contents[localContentsOffset++] = 3;
                                        ++i;
                                        break;
                                    }
                                    case 12: {
                                        this.contents[localContentsOffset++] = 5;
                                        break;
                                    }
                                    default: {
                                        this.contents[localContentsOffset++] = (byte)info.tag;
                                        switch (info.tag) {
                                            case 8: {
                                                int offset = info.offset;
                                                this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                                this.contents[localContentsOffset++] = (byte)offset;
                                                break block41;
                                            }
                                            case 7: {
                                                int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
                                                this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                                this.contents[localContentsOffset++] = (byte)indexForType;
                                            }
                                        }
                                    }
                                }
                                ++numberOfLocalEntries;
                            }
                            ++numberOfEntries;
                            ++i;
                        }
                        if (localContentsOffset + 4 >= this.contents.length) {
                            this.resizeContents(4);
                        }
                        this.contents[numberOfLocalOffset++] = (byte)(numberOfEntries >> 8);
                        this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
                        int numberOfStackItems = currentFrame.numberOfStackItems;
                        this.contents[localContentsOffset++] = (byte)(numberOfStackItems >> 8);
                        this.contents[localContentsOffset++] = (byte)numberOfStackItems;
                        int i2 = 0;
                        while (i2 < numberOfStackItems) {
                            VerificationTypeInfo info;
                            if (localContentsOffset + 3 >= this.contents.length) {
                                this.resizeContents(3);
                            }
                            if ((info = currentFrame.stackItems[i2]) == null) {
                                this.contents[localContentsOffset++] = 0;
                            } else {
                                block52 : switch (info.id()) {
                                    case 2: 
                                    case 3: 
                                    case 4: 
                                    case 5: 
                                    case 10: {
                                        this.contents[localContentsOffset++] = 1;
                                        break;
                                    }
                                    case 9: {
                                        this.contents[localContentsOffset++] = 2;
                                        break;
                                    }
                                    case 7: {
                                        this.contents[localContentsOffset++] = 4;
                                        break;
                                    }
                                    case 8: {
                                        this.contents[localContentsOffset++] = 3;
                                        break;
                                    }
                                    case 12: {
                                        this.contents[localContentsOffset++] = 5;
                                        break;
                                    }
                                    default: {
                                        this.contents[localContentsOffset++] = (byte)info.tag;
                                        switch (info.tag) {
                                            case 8: {
                                                int offset = info.offset;
                                                this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                                this.contents[localContentsOffset++] = (byte)offset;
                                                break block52;
                                            }
                                            case 7: {
                                                int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
                                                this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                                this.contents[localContentsOffset++] = (byte)indexForType;
                                            }
                                        }
                                    }
                                }
                            }
                            ++i2;
                        }
                        break block0;
                    }
                }
                ++j;
            }
            if (--numberOfFrames != 0) {
                this.contents[numberOfFramesOffset++] = (byte)(numberOfFrames >> 8);
                this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
                int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
                this.contents[stackMapTableAttributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[stackMapTableAttributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[stackMapTableAttributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[stackMapTableAttributeLengthOffset] = (byte)attributeLength;
                ++attributesNumber;
            } else {
                localContentsOffset = stackMapTableAttributeOffset;
            }
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }

    private int generateSyntheticAttribute() {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        int syntheticAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.SyntheticName);
        this.contents[localContentsOffset++] = (byte)(syntheticAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)syntheticAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    private void generateTypeAnnotation(AnnotationContext annotationContext, int currentOffset) {
        Annotation annotation = annotationContext.annotation.getPersistibleAnnotation();
        if (annotation == null || annotation.resolvedType == null) {
            return;
        }
        int targetType = annotationContext.targetType;
        int[] locations = Annotation.getLocations(annotationContext.typeReference, annotationContext.annotation);
        if (this.contentsOffset + 5 >= this.contents.length) {
            this.resizeContents(5);
        }
        this.contents[this.contentsOffset++] = (byte)targetType;
        this.dumpTargetTypeContents(targetType, annotationContext);
        this.dumpLocations(locations);
        this.generateAnnotation(annotation, currentOffset);
    }

    private int generateTypeAnnotationAttributeForTypeDeclaration() {
        TypeParameter[] typeParameters;
        TypeReference[] superInterfaces;
        TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
        if ((typeDeclaration.bits & 0x100000) == 0) {
            return 0;
        }
        int attributesNumber = 0;
        TypeReference superclass = typeDeclaration.superclass;
        ArrayList<AnnotationContext> allTypeAnnotationContexts = new ArrayList<AnnotationContext>();
        if (superclass != null && (superclass.bits & 0x100000) != 0) {
            superclass.getAllAnnotationContexts(16, -1, allTypeAnnotationContexts);
        }
        if ((superInterfaces = typeDeclaration.superInterfaces) != null) {
            int i = 0;
            while (i < superInterfaces.length) {
                TypeReference superInterface = superInterfaces[i];
                if ((superInterface.bits & 0x100000) != 0) {
                    superInterface.getAllAnnotationContexts(16, i, allTypeAnnotationContexts);
                }
                ++i;
            }
        }
        if ((typeParameters = typeDeclaration.typeParameters) != null) {
            int i = 0;
            int max = typeParameters.length;
            while (i < max) {
                TypeParameter typeParameter = typeParameters[i];
                if ((typeParameter.bits & 0x100000) != 0) {
                    typeParameter.getAllAnnotationContexts(0, i, allTypeAnnotationContexts);
                }
                ++i;
            }
        }
        int size = allTypeAnnotationContexts.size();
        attributesNumber = this.completeRuntimeTypeAnnotations(attributesNumber, null, node -> size > 0, () -> allTypeAnnotationContexts);
        return attributesNumber;
    }

    private int generateVarargsAttribute() {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        int varargsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.VarargsName);
        this.contents[localContentsOffset++] = (byte)(varargsAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)varargsAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contentsOffset = localContentsOffset;
        return 1;
    }

    public byte[] getBytes() {
        if (this.bytes == null) {
            this.bytes = new byte[this.headerOffset + this.contentsOffset];
            System.arraycopy(this.header, 0, this.bytes, 0, this.headerOffset);
            System.arraycopy(this.contents, 0, this.bytes, this.headerOffset, this.contentsOffset);
        }
        return this.bytes;
    }

    public char[][] getCompoundName() {
        return CharOperation.splitOn('/', this.fileName());
    }

    private int getParametersCount(char[] methodSignature) {
        char currentCharacter;
        int i = CharOperation.indexOf('(', methodSignature);
        if ((currentCharacter = methodSignature[++i]) == ')') {
            return 0;
        }
        int result = 0;
        block5: while (true) {
            if ((currentCharacter = methodSignature[i]) == ')') {
                return result;
            }
            switch (currentCharacter) {
                case '[': {
                    int scanType = this.scanType(methodSignature, i + 1);
                    ++result;
                    i = scanType + 1;
                    continue block5;
                }
                case 'L': {
                    int scanType = CharOperation.indexOf(';', methodSignature, i + 1);
                    ++result;
                    i = scanType + 1;
                    continue block5;
                }
                case 'B': 
                case 'C': 
                case 'D': 
                case 'F': 
                case 'I': 
                case 'J': 
                case 'S': 
                case 'Z': {
                    ++result;
                    ++i;
                    continue block5;
                }
            }
            break;
        }
        throw new IllegalArgumentException("Invalid starting type character : " + currentCharacter);
    }

    private char[] getReturnType(char[] methodSignature) {
        int paren = CharOperation.lastIndexOf(')', methodSignature);
        return CharOperation.subarray(methodSignature, paren + 1, methodSignature.length);
    }

    private final int i4At(byte[] reference, int relativeOffset, int structOffset) {
        int position = relativeOffset + structOffset;
        return ((reference[position++] & 0xFF) << 24) + ((reference[position++] & 0xFF) << 16) + ((reference[position++] & 0xFF) << 8) + (reference[position] & 0xFF);
    }

    protected void initByteArrays(int members) {
        this.header = new byte[1500];
        this.contents = new byte[members < 15 ? 400 : 1500];
    }

    private void initializeHeader(ClassFile parentClassFile, int accessFlags) {
        this.header[this.headerOffset++] = -54;
        this.header[this.headerOffset++] = -2;
        this.header[this.headerOffset++] = -70;
        this.header[this.headerOffset++] = -66;
        long targetVersion = this.targetJDK;
        this.header[this.headerOffset++] = (byte)(targetVersion >> 8);
        this.header[this.headerOffset++] = (byte)(targetVersion >> 0);
        this.header[this.headerOffset++] = (byte)(targetVersion >> 24);
        this.header[this.headerOffset++] = (byte)(targetVersion >> 16);
        this.constantPoolOffset = this.headerOffset;
        this.headerOffset += 2;
        this.constantPool.initialize(this);
        this.enclosingClassFile = parentClassFile;
        this.contents[this.contentsOffset++] = (byte)(accessFlags >> 8);
        this.contents[this.contentsOffset++] = (byte)accessFlags;
    }

    public void initialize(SourceTypeBinding aType, ClassFile parentClassFile, boolean createProblemType) {
        int finalAbstract;
        ReferenceBinding superClass;
        int accessFlags = aType.getAccessFlags();
        if (aType.isPrivate()) {
            accessFlags &= 0xFFFFFFFE;
        }
        if (aType.isProtected()) {
            accessFlags |= 1;
        }
        accessFlags &= 0xFFFFF6D1;
        if (!aType.isInterface()) {
            accessFlags |= 0x20;
        }
        if (!(!aType.isAnonymousType() || (superClass = aType.superclass) != null && superClass.isEnum() && superClass.isSealed())) {
            accessFlags &= 0xFFFFFFEF;
        }
        if ((accessFlags & (finalAbstract = 1040)) == finalAbstract) {
            accessFlags &= ~finalAbstract;
        }
        this.initializeHeader(parentClassFile, accessFlags);
        int classNameIndex = this.constantPool.literalIndexForType(aType);
        this.contents[this.contentsOffset++] = (byte)(classNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)classNameIndex;
        int superclassNameIndex = aType.isInterface() ? this.constantPool.literalIndexForType(ConstantPool.JavaLangObjectConstantPoolName) : (aType.superclass != null ? ((aType.superclass.tagBits & 0x80L) != 0L ? this.constantPool.literalIndexForType(ConstantPool.JavaLangObjectConstantPoolName) : this.constantPool.literalIndexForType(aType.superclass)) : 0);
        this.contents[this.contentsOffset++] = (byte)(superclassNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)superclassNameIndex;
        ReferenceBinding[] superInterfacesBinding = aType.superInterfaces();
        int interfacesCount = superInterfacesBinding.length;
        int interfacesCountPosition = this.contentsOffset;
        this.contentsOffset += 2;
        int interfaceCounter = 0;
        int i = 0;
        while (i < interfacesCount) {
            ReferenceBinding binding = superInterfacesBinding[i];
            if ((binding.tagBits & 0x80L) == 0L) {
                if (this.contentsOffset + 4 >= this.contents.length) {
                    this.resizeContents(4);
                }
                ++interfaceCounter;
                int interfaceIndex = this.constantPool.literalIndexForType(binding);
                this.contents[this.contentsOffset++] = (byte)(interfaceIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)interfaceIndex;
            }
            ++i;
        }
        this.contents[interfacesCountPosition++] = (byte)(interfaceCounter >> 8);
        this.contents[interfacesCountPosition] = (byte)interfaceCounter;
        this.creatingProblemType = createProblemType;
        this.codeStream.maxFieldCount = aType.scope.outerMostClassScope().referenceType().maxFieldCount;
    }

    public void initializeForModule(ModuleBinding module) {
        this.initializeHeader(null, 32768);
        int classNameIndex = this.constantPool.literalIndexForType(TypeConstants.MODULE_INFO_NAME);
        this.contents[this.contentsOffset++] = (byte)(classNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)classNameIndex;
        this.codeStream.maxFieldCount = 0;
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 0;
    }

    private void initializeDefaultLocals(StackMapFrame frame, MethodBinding methodBinding, int maxLocals, int codeLength) {
        block22: {
            TypeBinding[] arguments;
            int resolvedPosition;
            block23: {
                block24: {
                    SyntheticArgumentBinding[] syntheticArguments;
                    LocalVariableBinding localVariableBinding;
                    if (maxLocals == 0) break block22;
                    resolvedPosition = 0;
                    boolean isConstructor = methodBinding.isConstructor();
                    if (isConstructor || !methodBinding.isStatic()) {
                        localVariableBinding = new LocalVariableBinding(ConstantPool.This, (TypeBinding)methodBinding.declaringClass, 0, false);
                        localVariableBinding.resolvedPosition = 0;
                        this.codeStream.record(localVariableBinding);
                        localVariableBinding.recordInitializationStartPC(0);
                        localVariableBinding.recordInitializationEndPC(codeLength);
                        frame.putLocal(resolvedPosition, new VerificationTypeInfo(isConstructor ? 6 : 7, methodBinding.declaringClass));
                        ++resolvedPosition;
                    }
                    if (!isConstructor) break block23;
                    if (methodBinding.declaringClass.isEnum()) {
                        localVariableBinding = new LocalVariableBinding(" name".toCharArray(), (TypeBinding)this.referenceBinding.scope.getJavaLangString(), 0, false);
                        localVariableBinding.resolvedPosition = resolvedPosition;
                        this.codeStream.record(localVariableBinding);
                        localVariableBinding.recordInitializationStartPC(0);
                        localVariableBinding.recordInitializationEndPC(codeLength);
                        frame.putLocal(resolvedPosition, new VerificationTypeInfo(this.referenceBinding.scope.getJavaLangString()));
                        localVariableBinding = new LocalVariableBinding(" ordinal".toCharArray(), (TypeBinding)TypeBinding.INT, 0, false);
                        localVariableBinding.resolvedPosition = ++resolvedPosition;
                        this.codeStream.record(localVariableBinding);
                        localVariableBinding.recordInitializationStartPC(0);
                        localVariableBinding.recordInitializationEndPC(codeLength);
                        frame.putLocal(resolvedPosition, new VerificationTypeInfo(TypeBinding.INT));
                        ++resolvedPosition;
                    }
                    if (!methodBinding.declaringClass.isNestedType()) break block24;
                    ReferenceBinding[] enclosingInstanceTypes = methodBinding.declaringClass.syntheticEnclosingInstanceTypes();
                    if (enclosingInstanceTypes != null) {
                        int i = 0;
                        int max = enclosingInstanceTypes.length;
                        while (i < max) {
                            LocalVariableBinding localVariableBinding2 = new LocalVariableBinding((" enclosingType" + i).toCharArray(), (TypeBinding)enclosingInstanceTypes[i], 0, false);
                            localVariableBinding2.resolvedPosition = resolvedPosition;
                            this.codeStream.record(localVariableBinding2);
                            localVariableBinding2.recordInitializationStartPC(0);
                            localVariableBinding2.recordInitializationEndPC(codeLength);
                            frame.putLocal(resolvedPosition, new VerificationTypeInfo(enclosingInstanceTypes[i]));
                            ++resolvedPosition;
                            ++i;
                        }
                    }
                    TypeBinding[] arguments2 = methodBinding.parameters;
                    if (methodBinding.parameters != null) {
                        int i = 0;
                        int max = arguments2.length;
                        while (i < max) {
                            TypeBinding typeBinding = arguments2[i];
                            frame.putLocal(resolvedPosition, new VerificationTypeInfo(typeBinding));
                            switch (typeBinding.id) {
                                case 7: 
                                case 8: {
                                    resolvedPosition += 2;
                                    break;
                                }
                                default: {
                                    ++resolvedPosition;
                                }
                            }
                            ++i;
                        }
                    }
                    if ((syntheticArguments = methodBinding.declaringClass.syntheticOuterLocalVariables()) == null) break block22;
                    int i = 0;
                    int max = syntheticArguments.length;
                    while (i < max) {
                        TypeBinding typeBinding = syntheticArguments[i].type;
                        LocalVariableBinding localVariableBinding3 = new LocalVariableBinding((" synthetic" + i).toCharArray(), typeBinding, 0, false);
                        localVariableBinding3.resolvedPosition = resolvedPosition;
                        this.codeStream.record(localVariableBinding3);
                        localVariableBinding3.recordInitializationStartPC(0);
                        localVariableBinding3.recordInitializationEndPC(codeLength);
                        frame.putLocal(resolvedPosition, new VerificationTypeInfo(typeBinding));
                        switch (typeBinding.id) {
                            case 7: 
                            case 8: {
                                resolvedPosition += 2;
                                break;
                            }
                            default: {
                                ++resolvedPosition;
                            }
                        }
                        ++i;
                    }
                    break block22;
                }
                arguments = methodBinding.parameters;
                if (methodBinding.parameters == null) break block22;
                int i = 0;
                int max = arguments.length;
                while (i < max) {
                    TypeBinding typeBinding = arguments[i];
                    frame.putLocal(resolvedPosition, new VerificationTypeInfo(typeBinding));
                    switch (typeBinding.id) {
                        case 7: 
                        case 8: {
                            resolvedPosition += 2;
                            break;
                        }
                        default: {
                            ++resolvedPosition;
                        }
                    }
                    ++i;
                }
                break block22;
            }
            arguments = methodBinding.parameters;
            if (methodBinding.parameters != null) {
                int i = 0;
                int max = arguments.length;
                while (i < max) {
                    TypeBinding typeBinding = arguments[i];
                    LocalVariableBinding localVariableBinding = new LocalVariableBinding((" synthetic" + i).toCharArray(), typeBinding, 0, true);
                    localVariableBinding.resolvedPosition = i;
                    this.codeStream.record(localVariableBinding);
                    localVariableBinding.recordInitializationStartPC(0);
                    localVariableBinding.recordInitializationEndPC(codeLength);
                    frame.putLocal(resolvedPosition, new VerificationTypeInfo(typeBinding));
                    switch (typeBinding.id) {
                        case 7: 
                        case 8: {
                            resolvedPosition += 2;
                            break;
                        }
                        default: {
                            ++resolvedPosition;
                        }
                    }
                    ++i;
                }
            }
        }
    }

    private void initializeLocals(boolean isStatic, int currentPC, StackMapFrame currentFrame) {
        VerificationTypeInfo[] locals = currentFrame.locals;
        int localsLength = locals.length;
        int i = 0;
        if (!isStatic) {
            i = 1;
        }
        while (i < localsLength) {
            locals[i] = null;
            ++i;
        }
        i = 0;
        int max = this.codeStream.allLocalsCounter;
        while (i < max) {
            LocalVariableBinding localVariable = this.codeStream.locals[i];
            if (localVariable != null) {
                int resolvedPosition = localVariable.resolvedPosition;
                TypeBinding localVariableTypeBinding = localVariable.type;
                int j = 0;
                while (j < localVariable.initializationCount) {
                    int startPC = localVariable.initializationPCs[j << 1];
                    int endPC = localVariable.initializationPCs[(j << 1) + 1];
                    if (currentPC >= startPC && currentPC < endPC) {
                        if (currentFrame.locals[resolvedPosition] != null) break;
                        currentFrame.locals[resolvedPosition] = new VerificationTypeInfo(localVariableTypeBinding);
                        break;
                    }
                    ++j;
                }
            }
            ++i;
        }
    }

    public ClassFile outerMostEnclosingClassFile() {
        ClassFile current = this;
        while (current.enclosingClassFile != null) {
            current = current.enclosingClassFile;
        }
        return current;
    }

    public void recordInnerClasses(TypeBinding binding) {
        this.recordInnerClasses(binding, false);
    }

    public void recordInnerClasses(TypeBinding binding, boolean onBottomForBug445231) {
        if (this.innerClassesBindings == null) {
            this.innerClassesBindings = new HashMap<TypeBinding, Boolean>(5);
        }
        ReferenceBinding innerClass = (ReferenceBinding)binding;
        this.innerClassesBindings.put(innerClass.erasure().unannotated(), onBottomForBug445231);
        ReferenceBinding enclosingType = innerClass.enclosingType();
        while (enclosingType != null && enclosingType.isNestedType()) {
            this.innerClassesBindings.put(enclosingType.erasure().unannotated(), onBottomForBug445231);
            enclosingType = enclosingType.enclosingType();
        }
    }

    public void recordNestMember(SourceTypeBinding binding) {
        SourceTypeBinding nestHost;
        SourceTypeBinding sourceTypeBinding = nestHost = binding != null ? binding.getNestHost() : null;
        if (nestHost != null && !binding.equals(nestHost)) {
            if (this.nestMembers == null) {
                this.nestMembers = new HashSet<SourceTypeBinding>(5);
            }
            this.nestMembers.add(binding);
        }
    }

    public List<String> getNestMembers() {
        if (this.nestMembers == null) {
            return null;
        }
        List<String> list = this.nestMembers.stream().map(s -> new String(s.constantPoolName())).sorted().collect(Collectors.toList());
        return list;
    }

    public int recordBootstrapMethod(FunctionalExpression expression) {
        if (this.bootstrapMethods == null) {
            this.bootstrapMethods = new ArrayList<ASTNode>();
        }
        if (expression instanceof ReferenceExpression) {
            int i = 0;
            while (i < this.bootstrapMethods.size()) {
                ASTNode node = this.bootstrapMethods.get(i);
                if (node instanceof FunctionalExpression) {
                    FunctionalExpression fexp = (FunctionalExpression)node;
                    if (fexp.binding == expression.binding && TypeBinding.equalsEquals(fexp.expectedType(), expression.expectedType())) {
                        expression.bootstrapMethodNumber = i;
                        return expression.bootstrapMethodNumber;
                    }
                }
                ++i;
            }
        }
        this.bootstrapMethods.add(expression);
        expression.bootstrapMethodNumber = this.bootstrapMethods.size() - 1;
        return expression.bootstrapMethodNumber;
    }

    public void reset(SourceTypeBinding typeBinding, CompilerOptions options) {
        if (typeBinding != null) {
            this.referenceBinding = typeBinding;
            this.isNestedType = typeBinding.isNestedType();
        } else {
            this.referenceBinding = null;
            this.isNestedType = false;
        }
        this.targetJDK = options.targetJDK;
        this.produceAttributes = options.produceDebugAttributes;
        if (this.targetJDK >= 0x320000L) {
            this.produceAttributes |= 8;
            if (this.targetJDK >= 0x340000L) {
                this.produceAttributes |= 0x20;
                if (!(this.codeStream instanceof TypeAnnotationCodeStream) && this.referenceBinding != null) {
                    this.codeStream = new TypeAnnotationCodeStream(this);
                }
                if (options.produceMethodParameters) {
                    this.produceAttributes |= 0x40;
                }
            }
        } else if (this.targetJDK == 2949124L) {
            this.targetJDK = 2949123L;
            this.produceAttributes |= 0x10;
        }
        this.bytes = null;
        this.constantPool.reset();
        this.codeStream.reset(this);
        this.constantPoolOffset = 0;
        this.contentsOffset = 0;
        this.creatingProblemType = false;
        this.enclosingClassFile = null;
        this.headerOffset = 0;
        this.methodCount = 0;
        this.methodCountOffset = 0;
        if (this.innerClassesBindings != null) {
            this.innerClassesBindings.clear();
        }
        if (this.nestMembers != null) {
            this.nestMembers.clear();
        }
        if (this.bootstrapMethods != null) {
            this.bootstrapMethods.clear();
        }
        this.missingTypes = null;
        this.visitedTypes = null;
    }

    private final void resizeContents(int minimalSize) {
        int length = this.contents.length;
        int toAdd = length;
        if (toAdd < minimalSize) {
            toAdd = minimalSize;
        }
        this.contents = new byte[length + toAdd];
        System.arraycopy(this.contents, 0, this.contents, 0, length);
    }

    private VerificationTypeInfo retrieveLocal(int currentPC, int resolvedPosition) {
        int i = 0;
        int max = this.codeStream.allLocalsCounter;
        while (i < max) {
            LocalVariableBinding localVariable = this.codeStream.locals[i];
            if (localVariable != null && resolvedPosition == localVariable.resolvedPosition) {
                int j = 0;
                while (j < localVariable.initializationCount) {
                    int startPC = localVariable.initializationPCs[j << 1];
                    int endPC = localVariable.initializationPCs[(j << 1) + 1];
                    if (currentPC >= startPC && currentPC < endPC) {
                        return new VerificationTypeInfo(localVariable.type);
                    }
                    ++j;
                }
            }
            ++i;
        }
        return null;
    }

    private int scanType(char[] methodSignature, int index) {
        switch (methodSignature[index]) {
            case '[': {
                return this.scanType(methodSignature, index + 1);
            }
            case 'L': {
                return CharOperation.indexOf(';', methodSignature, index + 1);
            }
            case 'B': 
            case 'C': 
            case 'D': 
            case 'F': 
            case 'I': 
            case 'J': 
            case 'S': 
            case 'Z': {
                return index;
            }
        }
        throw ClassFile.newIllegalArgumentException(methodSignature, index);
    }

    private static IllegalArgumentException newIllegalArgumentException(char[] string, int index) {
        return new IllegalArgumentException("\"" + String.valueOf(string) + "\" at " + index);
    }

    public void setForMethodInfos() {
        this.methodCountOffset = this.contentsOffset;
        this.contentsOffset += 2;
    }

    private List<StackMapFrame> filterFakeFrames(Set<Integer> realJumpTargets, Map<Integer, StackMapFrame> frames, int codeLength) {
        realJumpTargets.remove(codeLength);
        ArrayList<StackMapFrame> result = new ArrayList<StackMapFrame>();
        for (Integer jumpTarget : realJumpTargets) {
            StackMapFrame frame = frames.get(jumpTarget);
            if (frame == null) continue;
            result.add(frame);
        }
        Collections.sort(result, new Comparator<StackMapFrame>(){

            @Override
            public int compare(StackMapFrame frame, StackMapFrame frame2) {
                return frame.pc - frame2.pc;
            }
        });
        return result;
    }

    private TypeBinding getTypeBinding(char[] typeConstantPoolName, Scope scope, boolean checkcast) {
        if (typeConstantPoolName.length == 1) {
            switch (typeConstantPoolName[0]) {
                case 'Z': {
                    return TypeBinding.BOOLEAN;
                }
                case 'B': {
                    return TypeBinding.BYTE;
                }
                case 'C': {
                    return TypeBinding.CHAR;
                }
                case 'D': {
                    return TypeBinding.DOUBLE;
                }
                case 'F': {
                    return TypeBinding.FLOAT;
                }
                case 'I': {
                    return TypeBinding.INT;
                }
                case 'J': {
                    return TypeBinding.LONG;
                }
                case 'S': {
                    return TypeBinding.SHORT;
                }
            }
            return null;
        }
        if (typeConstantPoolName[0] == '[') {
            int dimensions = this.getDimensions(typeConstantPoolName);
            if (typeConstantPoolName.length - dimensions == 1) {
                BaseTypeBinding baseType = null;
                switch (typeConstantPoolName[typeConstantPoolName.length - 1]) {
                    case 'Z': {
                        baseType = TypeBinding.BOOLEAN;
                        break;
                    }
                    case 'B': {
                        baseType = TypeBinding.BYTE;
                        break;
                    }
                    case 'C': {
                        baseType = TypeBinding.CHAR;
                        break;
                    }
                    case 'D': {
                        baseType = TypeBinding.DOUBLE;
                        break;
                    }
                    case 'F': {
                        baseType = TypeBinding.FLOAT;
                        break;
                    }
                    case 'I': {
                        baseType = TypeBinding.INT;
                        break;
                    }
                    case 'J': {
                        baseType = TypeBinding.LONG;
                        break;
                    }
                    case 'S': {
                        baseType = TypeBinding.SHORT;
                        break;
                    }
                    case 'V': {
                        baseType = TypeBinding.VOID;
                    }
                }
                return scope.createArrayType(baseType, dimensions);
            }
            char[] typeName = CharOperation.subarray(typeConstantPoolName, dimensions + 1, typeConstantPoolName.length - 1);
            TypeBinding type = (TypeBinding)scope.getTypeOrPackage(CharOperation.splitOn('/', typeName));
            if (!type.isValidBinding()) {
                ProblemReferenceBinding problemReferenceBinding = (ProblemReferenceBinding)type;
                if ((problemReferenceBinding.problemId() & 4) != 0 || (problemReferenceBinding.problemId() & 2) != 0) {
                    type = problemReferenceBinding.closestMatch();
                } else if ((problemReferenceBinding.problemId() & 1) != 0 && this.innerClassesBindings != null) {
                    Set<TypeBinding> innerTypeBindings = this.innerClassesBindings.keySet();
                    for (TypeBinding binding : innerTypeBindings) {
                        if (!CharOperation.equals(binding.constantPoolName(), typeName)) continue;
                        type = binding;
                        break;
                    }
                }
            }
            return scope.createArrayType(type, dimensions);
        }
        char[] typeName = checkcast ? typeConstantPoolName : CharOperation.subarray(typeConstantPoolName, 1, typeConstantPoolName.length - 1);
        TypeBinding type = (TypeBinding)scope.getTypeOrPackage(CharOperation.splitOn('/', typeName));
        if (!type.isValidBinding()) {
            ProblemReferenceBinding problemReferenceBinding = (ProblemReferenceBinding)type;
            if ((problemReferenceBinding.problemId() & 4) != 0 || (problemReferenceBinding.problemId() & 2) != 0) {
                type = problemReferenceBinding.closestMatch();
            } else if ((problemReferenceBinding.problemId() & 1) != 0 && this.innerClassesBindings != null) {
                Set<TypeBinding> innerTypeBindings = this.innerClassesBindings.keySet();
                for (TypeBinding binding : innerTypeBindings) {
                    if (!CharOperation.equals(binding.constantPoolName(), typeName)) continue;
                    type = binding;
                    break;
                }
            }
        }
        return type;
    }

    private TypeBinding getNewTypeBinding(char[] typeConstantPoolName, Scope scope) {
        ProblemReferenceBinding problemReferenceBinding;
        TypeBinding type;
        char[] typeName = typeConstantPoolName;
        if (this.innerClassesBindings != null && this.isLikelyLocalTypeName(typeName)) {
            Set<TypeBinding> innerTypeBindings = this.innerClassesBindings.keySet();
            for (TypeBinding binding : innerTypeBindings) {
                if (!CharOperation.equals(binding.constantPoolName(), typeName)) continue;
                return binding;
            }
        }
        if (!((type = (TypeBinding)scope.getTypeOrPackage(CharOperation.splitOn('/', typeName))).isValidBinding() || ((problemReferenceBinding = (ProblemReferenceBinding)type).problemId() & 4) == 0 && (problemReferenceBinding.problemId() & 2) == 0)) {
            type = problemReferenceBinding.closestMatch();
        }
        return type;
    }

    private boolean isLikelyLocalTypeName(char[] typeName) {
        int dollarPos = CharOperation.lastIndexOf('$', typeName);
        while (dollarPos != -1 && dollarPos + 1 < typeName.length) {
            if (Character.isDigit(typeName[dollarPos + 1])) {
                return true;
            }
            dollarPos = CharOperation.lastIndexOf('$', typeName, 0, dollarPos - 1);
        }
        return false;
    }

    private TypeBinding getANewArrayTypeBinding(char[] typeConstantPoolName, Scope scope) {
        if (typeConstantPoolName[0] == '[') {
            int dimensions = this.getDimensions(typeConstantPoolName);
            if (typeConstantPoolName.length - dimensions == 1) {
                BaseTypeBinding baseType = null;
                switch (typeConstantPoolName[typeConstantPoolName.length - 1]) {
                    case 'Z': {
                        baseType = TypeBinding.BOOLEAN;
                        break;
                    }
                    case 'B': {
                        baseType = TypeBinding.BYTE;
                        break;
                    }
                    case 'C': {
                        baseType = TypeBinding.CHAR;
                        break;
                    }
                    case 'D': {
                        baseType = TypeBinding.DOUBLE;
                        break;
                    }
                    case 'F': {
                        baseType = TypeBinding.FLOAT;
                        break;
                    }
                    case 'I': {
                        baseType = TypeBinding.INT;
                        break;
                    }
                    case 'J': {
                        baseType = TypeBinding.LONG;
                        break;
                    }
                    case 'S': {
                        baseType = TypeBinding.SHORT;
                        break;
                    }
                    case 'V': {
                        baseType = TypeBinding.VOID;
                    }
                }
                return scope.createArrayType(baseType, dimensions);
            }
            char[] elementTypeClassName = CharOperation.subarray(typeConstantPoolName, dimensions + 1, typeConstantPoolName.length - 1);
            TypeBinding type = (TypeBinding)scope.getTypeOrPackage(CharOperation.splitOn('/', elementTypeClassName));
            if (!type.isValidBinding()) {
                ProblemReferenceBinding problemReferenceBinding = (ProblemReferenceBinding)type;
                if ((problemReferenceBinding.problemId() & 4) != 0 || (problemReferenceBinding.problemId() & 2) != 0) {
                    type = problemReferenceBinding.closestMatch();
                } else if ((problemReferenceBinding.problemId() & 1) != 0 && this.innerClassesBindings != null) {
                    Set<TypeBinding> innerTypeBindings = this.innerClassesBindings.keySet();
                    for (TypeBinding binding : innerTypeBindings) {
                        if (!CharOperation.equals(binding.constantPoolName(), elementTypeClassName)) continue;
                        type = binding;
                        break;
                    }
                }
            }
            return scope.createArrayType(type, dimensions);
        }
        TypeBinding type = (TypeBinding)scope.getTypeOrPackage(CharOperation.splitOn('/', typeConstantPoolName));
        if (!type.isValidBinding()) {
            ProblemReferenceBinding problemReferenceBinding = (ProblemReferenceBinding)type;
            if ((problemReferenceBinding.problemId() & 4) != 0 || (problemReferenceBinding.problemId() & 2) != 0) {
                type = problemReferenceBinding.closestMatch();
            } else if ((problemReferenceBinding.problemId() & 1) != 0 && this.innerClassesBindings != null) {
                Set<TypeBinding> innerTypeBindings = this.innerClassesBindings.keySet();
                for (TypeBinding binding : innerTypeBindings) {
                    if (!CharOperation.equals(binding.constantPoolName(), typeConstantPoolName)) continue;
                    type = binding;
                    break;
                }
            }
        }
        return type;
    }

    public List<StackMapFrame> traverse(MethodBinding methodBinding, int maxLocals, byte[] bytecodes, int codeOffset, int codeLength, Map<Integer, StackMapFrame> frames, boolean isClinit, Scope scope) {
        HashSet<Integer> realJumpTarget = new HashSet<Integer>();
        StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
        int[] framePositions = stackMapFrameCodeStream.getFramePositions();
        int pc = codeOffset;
        int[] constantPoolOffsets = this.constantPool.offsets;
        byte[] poolContents = this.constantPool.poolContent;
        int indexInFramePositions = 0;
        int framePositionsLength = framePositions.length;
        int currentFramePosition = framePositions[0];
        int indexInExceptionMarkers = 0;
        StackMapFrameCodeStream.ExceptionMarker[] exceptionMarkers = stackMapFrameCodeStream.getExceptionMarkers();
        int exceptionsMarkersLength = exceptionMarkers == null ? 0 : exceptionMarkers.length;
        boolean hasExceptionMarkers = exceptionsMarkersLength != 0;
        StackMapFrameCodeStream.ExceptionMarker exceptionMarker = null;
        if (hasExceptionMarkers) {
            exceptionMarker = exceptionMarkers[0];
        }
        StackMapFrame frame = new StackMapFrame(maxLocals);
        if (!isClinit) {
            this.initializeDefaultLocals(frame, methodBinding, maxLocals, codeLength);
        }
        frame.pc = -1;
        this.add(frames, frame.duplicate(), scope);
        this.addRealJumpTarget(realJumpTarget, -1);
        int i = 0;
        int max = this.codeStream.exceptionLabelsCounter;
        while (i < max) {
            ExceptionLabel exceptionLabel = this.codeStream.exceptionLabels[i];
            if (exceptionLabel != null) {
                this.addRealJumpTarget(realJumpTarget, exceptionLabel.position);
            }
            ++i;
        }
        block155: do {
            int currentPC = pc - codeOffset;
            if (hasExceptionMarkers && exceptionMarker.pc == currentPC) {
                frame.numberOfStackItems = 0;
                frame.addStackItem(new VerificationTypeInfo(exceptionMarker.getBinding()));
                if (++indexInExceptionMarkers < exceptionsMarkersLength) {
                    exceptionMarker = exceptionMarkers[indexInExceptionMarkers];
                } else {
                    hasExceptionMarkers = false;
                }
            }
            if (currentFramePosition < currentPC) {
                while ((currentFramePosition = ++indexInFramePositions < framePositionsLength ? framePositions[indexInFramePositions] : Integer.MAX_VALUE) < currentPC) {
                }
            }
            if (currentFramePosition == currentPC) {
                StackMapFrame currentFrame = frames.get(currentPC);
                if (currentFrame == null) {
                    currentFrame = this.createNewFrame(currentPC, frame, isClinit, methodBinding);
                    this.add(frames, currentFrame, scope);
                } else {
                    frame = currentFrame.merge(frame.duplicate(), scope).duplicate();
                }
                currentFramePosition = ++indexInFramePositions < framePositionsLength ? framePositions[indexInFramePositions] : Integer.MAX_VALUE;
            }
            byte opcode = (byte)this.u1At(bytecodes, 0, pc);
            switch (opcode) {
                case 0: {
                    ++pc;
                    break;
                }
                case 1: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.NULL));
                    ++pc;
                    break;
                }
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: 
                case 8: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.INT));
                    ++pc;
                    break;
                }
                case 9: 
                case 10: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.LONG));
                    ++pc;
                    break;
                }
                case 11: 
                case 12: 
                case 13: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.FLOAT));
                    ++pc;
                    break;
                }
                case 14: 
                case 15: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.DOUBLE));
                    ++pc;
                    break;
                }
                case 16: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.BYTE));
                    pc += 2;
                    break;
                }
                case 17: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.SHORT));
                    pc += 3;
                    break;
                }
                case 18: {
                    int index = this.u1At(bytecodes, 1, pc);
                    switch (this.u1At(poolContents, 0, constantPoolOffsets[index])) {
                        case 8: {
                            frame.addStackItem(new VerificationTypeInfo(scope.getJavaLangString()));
                            break;
                        }
                        case 3: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.INT));
                            break;
                        }
                        case 4: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.FLOAT));
                            break;
                        }
                        case 7: {
                            frame.addStackItem(new VerificationTypeInfo(scope.getJavaLangClass()));
                        }
                    }
                    pc += 2;
                    break;
                }
                case 19: {
                    int index = this.u2At(bytecodes, 1, pc);
                    switch (this.u1At(poolContents, 0, constantPoolOffsets[index])) {
                        case 8: {
                            frame.addStackItem(new VerificationTypeInfo(scope.getJavaLangString()));
                            break;
                        }
                        case 3: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.INT));
                            break;
                        }
                        case 4: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.FLOAT));
                            break;
                        }
                        case 7: {
                            frame.addStackItem(new VerificationTypeInfo(scope.getJavaLangClass()));
                        }
                    }
                    pc += 3;
                    break;
                }
                case 20: {
                    int index = this.u2At(bytecodes, 1, pc);
                    switch (this.u1At(poolContents, 0, constantPoolOffsets[index])) {
                        case 6: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.DOUBLE));
                            break;
                        }
                        case 5: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.LONG));
                        }
                    }
                    pc += 3;
                    break;
                }
                case 21: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.INT));
                    pc += 2;
                    break;
                }
                case 22: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.LONG));
                    pc += 2;
                    break;
                }
                case 23: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.FLOAT));
                    pc += 2;
                    break;
                }
                case 24: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.DOUBLE));
                    pc += 2;
                    break;
                }
                case 25: {
                    int index = this.u1At(bytecodes, 1, pc);
                    VerificationTypeInfo localsN = this.retrieveLocal(currentPC, index);
                    frame.addStackItem(localsN);
                    pc += 2;
                    break;
                }
                case 26: 
                case 27: 
                case 28: 
                case 29: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.INT));
                    ++pc;
                    break;
                }
                case 30: 
                case 31: 
                case 32: 
                case 33: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.LONG));
                    ++pc;
                    break;
                }
                case 34: 
                case 35: 
                case 36: 
                case 37: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.FLOAT));
                    ++pc;
                    break;
                }
                case 38: 
                case 39: 
                case 40: 
                case 41: {
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.DOUBLE));
                    ++pc;
                    break;
                }
                case 42: {
                    VerificationTypeInfo locals0 = frame.locals[0];
                    if (locals0 == null || locals0.tag != 6) {
                        locals0 = this.retrieveLocal(currentPC, 0);
                    }
                    frame.addStackItem(locals0);
                    ++pc;
                    break;
                }
                case 43: {
                    VerificationTypeInfo locals1 = this.retrieveLocal(currentPC, 1);
                    frame.addStackItem(locals1);
                    ++pc;
                    break;
                }
                case 44: {
                    VerificationTypeInfo locals2 = this.retrieveLocal(currentPC, 2);
                    frame.addStackItem(locals2);
                    ++pc;
                    break;
                }
                case 45: {
                    VerificationTypeInfo locals3 = this.retrieveLocal(currentPC, 3);
                    frame.addStackItem(locals3);
                    ++pc;
                    break;
                }
                case 46: {
                    frame.numberOfStackItems -= 2;
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.INT));
                    ++pc;
                    break;
                }
                case 47: {
                    frame.numberOfStackItems -= 2;
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.LONG));
                    ++pc;
                    break;
                }
                case 48: {
                    frame.numberOfStackItems -= 2;
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.FLOAT));
                    ++pc;
                    break;
                }
                case 49: {
                    frame.numberOfStackItems -= 2;
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.DOUBLE));
                    ++pc;
                    break;
                }
                case 50: {
                    --frame.numberOfStackItems;
                    frame.replaceWithElementType();
                    ++pc;
                    break;
                }
                case 51: {
                    frame.numberOfStackItems -= 2;
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.BYTE));
                    ++pc;
                    break;
                }
                case 52: {
                    frame.numberOfStackItems -= 2;
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.CHAR));
                    ++pc;
                    break;
                }
                case 53: {
                    frame.numberOfStackItems -= 2;
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.SHORT));
                    ++pc;
                    break;
                }
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    --frame.numberOfStackItems;
                    pc += 2;
                    break;
                }
                case 58: {
                    int index = this.u1At(bytecodes, 1, pc);
                    --frame.numberOfStackItems;
                    pc += 2;
                    break;
                }
                case 75: {
                    frame.locals[0] = frame.stackItems[frame.numberOfStackItems - 1];
                    --frame.numberOfStackItems;
                    ++pc;
                    break;
                }
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 64: 
                case 65: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 76: 
                case 77: 
                case 78: {
                    --frame.numberOfStackItems;
                    ++pc;
                    break;
                }
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: {
                    frame.numberOfStackItems -= 3;
                    ++pc;
                    break;
                }
                case 87: {
                    --frame.numberOfStackItems;
                    ++pc;
                    break;
                }
                case 88: {
                    int numberOfStackItems = frame.numberOfStackItems--;
                    switch (frame.stackItems[numberOfStackItems - 1].id()) {
                        case 7: 
                        case 8: {
                            break;
                        }
                        default: {
                            frame.numberOfStackItems -= 2;
                        }
                    }
                    ++pc;
                    break;
                }
                case 89: {
                    frame.addStackItem(frame.stackItems[frame.numberOfStackItems - 1]);
                    ++pc;
                    break;
                }
                case 90: {
                    VerificationTypeInfo info = frame.stackItems[frame.numberOfStackItems - 1];
                    --frame.numberOfStackItems;
                    VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
                    --frame.numberOfStackItems;
                    frame.addStackItem(info);
                    frame.addStackItem(info2);
                    frame.addStackItem(info);
                    ++pc;
                    break;
                }
                case 91: {
                    int numberOfStackItems;
                    VerificationTypeInfo info = frame.stackItems[frame.numberOfStackItems - 1];
                    --frame.numberOfStackItems;
                    VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
                    --frame.numberOfStackItems;
                    switch (info2.id()) {
                        case 7: 
                        case 8: {
                            frame.addStackItem(info);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            break;
                        }
                        default: {
                            numberOfStackItems = frame.numberOfStackItems--;
                            VerificationTypeInfo info3 = frame.stackItems[numberOfStackItems - 1];
                            frame.addStackItem(info);
                            frame.addStackItem(info3);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                        }
                    }
                    ++pc;
                    break;
                }
                case 92: {
                    VerificationTypeInfo info2;
                    VerificationTypeInfo info = frame.stackItems[frame.numberOfStackItems - 1];
                    --frame.numberOfStackItems;
                    switch (info.id()) {
                        case 7: 
                        case 8: {
                            frame.addStackItem(info);
                            frame.addStackItem(info);
                            break;
                        }
                        default: {
                            info2 = frame.stackItems[frame.numberOfStackItems - 1];
                            --frame.numberOfStackItems;
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                        }
                    }
                    ++pc;
                    break;
                }
                case 93: {
                    VerificationTypeInfo info = frame.stackItems[frame.numberOfStackItems - 1];
                    --frame.numberOfStackItems;
                    VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
                    --frame.numberOfStackItems;
                    switch (info.id()) {
                        case 7: 
                        case 8: {
                            frame.addStackItem(info);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            break;
                        }
                        default: {
                            VerificationTypeInfo info3 = frame.stackItems[frame.numberOfStackItems - 1];
                            --frame.numberOfStackItems;
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            frame.addStackItem(info3);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                        }
                    }
                    ++pc;
                    break;
                }
                case 94: {
                    int numberOfStackItems = frame.numberOfStackItems--;
                    VerificationTypeInfo info = frame.stackItems[numberOfStackItems - 1];
                    VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
                    --frame.numberOfStackItems;
                    block123 : switch (info.id()) {
                        case 7: 
                        case 8: {
                            switch (info2.id()) {
                                case 7: 
                                case 8: {
                                    frame.addStackItem(info);
                                    frame.addStackItem(info2);
                                    frame.addStackItem(info);
                                    break block123;
                                }
                            }
                            numberOfStackItems = frame.numberOfStackItems--;
                            VerificationTypeInfo info3 = frame.stackItems[numberOfStackItems - 1];
                            frame.addStackItem(info);
                            frame.addStackItem(info3);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            break;
                        }
                        default: {
                            numberOfStackItems = frame.numberOfStackItems--;
                            VerificationTypeInfo info3 = frame.stackItems[numberOfStackItems - 1];
                            switch (info3.id()) {
                                case 7: 
                                case 8: {
                                    frame.addStackItem(info2);
                                    frame.addStackItem(info);
                                    frame.addStackItem(info3);
                                    frame.addStackItem(info2);
                                    frame.addStackItem(info);
                                    break block123;
                                }
                            }
                            numberOfStackItems = frame.numberOfStackItems--;
                            VerificationTypeInfo info4 = frame.stackItems[numberOfStackItems - 1];
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            frame.addStackItem(info4);
                            frame.addStackItem(info3);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                        }
                    }
                    ++pc;
                    break;
                }
                case 95: {
                    VerificationTypeInfo info2;
                    int numberOfStackItems = frame.numberOfStackItems;
                    VerificationTypeInfo info = frame.stackItems[numberOfStackItems - 1];
                    frame.stackItems[numberOfStackItems - 1] = info2 = frame.stackItems[numberOfStackItems - 2];
                    frame.stackItems[numberOfStackItems - 2] = info;
                    ++pc;
                    break;
                }
                case -128: 
                case -127: 
                case -126: 
                case -125: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 113: 
                case 114: 
                case 115: 
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 127: {
                    --frame.numberOfStackItems;
                    ++pc;
                    break;
                }
                case 116: 
                case 117: 
                case 118: 
                case 119: {
                    ++pc;
                    break;
                }
                case -124: {
                    pc += 3;
                    break;
                }
                case -123: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.LONG);
                    ++pc;
                    break;
                }
                case -122: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.FLOAT);
                    ++pc;
                    break;
                }
                case -121: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.DOUBLE);
                    ++pc;
                    break;
                }
                case -120: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    ++pc;
                    break;
                }
                case -119: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.FLOAT);
                    ++pc;
                    break;
                }
                case -118: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.DOUBLE);
                    ++pc;
                    break;
                }
                case -117: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    ++pc;
                    break;
                }
                case -116: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.LONG);
                    ++pc;
                    break;
                }
                case -115: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.DOUBLE);
                    ++pc;
                    break;
                }
                case -114: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    ++pc;
                    break;
                }
                case -113: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.LONG);
                    ++pc;
                    break;
                }
                case -112: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.FLOAT);
                    ++pc;
                    break;
                }
                case -111: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.BYTE);
                    ++pc;
                    break;
                }
                case -110: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.CHAR);
                    ++pc;
                    break;
                }
                case -109: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.SHORT);
                    ++pc;
                    break;
                }
                case -108: 
                case -107: 
                case -106: 
                case -105: 
                case -104: {
                    frame.numberOfStackItems -= 2;
                    frame.addStackItem(new VerificationTypeInfo(TypeBinding.INT));
                    ++pc;
                    break;
                }
                case -103: 
                case -102: 
                case -101: 
                case -100: 
                case -99: 
                case -98: {
                    --frame.numberOfStackItems;
                    int jumpPC = currentPC + this.i2At(bytecodes, 1, pc);
                    this.addRealJumpTarget(realJumpTarget, jumpPC, frames, this.createNewFrame(jumpPC, frame, isClinit, methodBinding), scope);
                    pc += 3;
                    break;
                }
                case -97: 
                case -96: 
                case -95: 
                case -94: 
                case -93: 
                case -92: 
                case -91: 
                case -90: {
                    frame.numberOfStackItems -= 2;
                    int jumpPC = currentPC + this.i2At(bytecodes, 1, pc);
                    this.addRealJumpTarget(realJumpTarget, jumpPC, frames, this.createNewFrame(jumpPC, frame, isClinit, methodBinding), scope);
                    pc += 3;
                    break;
                }
                case -89: {
                    int jumpPC = currentPC + this.i2At(bytecodes, 1, pc);
                    this.addRealJumpTarget(realJumpTarget, jumpPC, frames, this.createNewFrame(jumpPC, frame, isClinit, methodBinding), scope);
                    this.addRealJumpTarget(realJumpTarget, (pc += 3) - codeOffset);
                    break;
                }
                case -86: {
                    --frame.numberOfStackItems;
                    ++pc;
                    while ((pc - codeOffset & 3) != 0) {
                        ++pc;
                    }
                    int jumpPC = currentPC + this.i4At(bytecodes, 0, pc);
                    this.addRealJumpTarget(realJumpTarget, jumpPC, frames, this.createNewFrame(jumpPC, frame, isClinit, methodBinding), scope);
                    int low = this.i4At(bytecodes, 0, pc += 4);
                    int high = this.i4At(bytecodes, 0, pc += 4);
                    pc += 4;
                    int length = high - low + 1;
                    int i2 = 0;
                    while (i2 < length) {
                        jumpPC = currentPC + this.i4At(bytecodes, 0, pc);
                        this.addRealJumpTarget(realJumpTarget, jumpPC, frames, this.createNewFrame(jumpPC, frame, isClinit, methodBinding), scope);
                        pc += 4;
                        ++i2;
                    }
                    continue block155;
                }
                case -85: {
                    --frame.numberOfStackItems;
                    ++pc;
                    while ((pc - codeOffset & 3) != 0) {
                        ++pc;
                    }
                    int jumpPC = currentPC + this.i4At(bytecodes, 0, pc);
                    this.addRealJumpTarget(realJumpTarget, jumpPC, frames, this.createNewFrame(jumpPC, frame, isClinit, methodBinding), scope);
                    int npairs = (int)this.u4At(bytecodes, 0, pc += 4);
                    pc += 4;
                    int i3 = 0;
                    while (i3 < npairs) {
                        jumpPC = currentPC + this.i4At(bytecodes, 0, pc += 4);
                        this.addRealJumpTarget(realJumpTarget, jumpPC, frames, this.createNewFrame(jumpPC, frame, isClinit, methodBinding), scope);
                        pc += 4;
                        ++i3;
                    }
                    continue block155;
                }
                case -84: 
                case -83: 
                case -82: 
                case -81: 
                case -80: {
                    --frame.numberOfStackItems;
                    this.addRealJumpTarget(realJumpTarget, ++pc - codeOffset);
                    break;
                }
                case -79: {
                    this.addRealJumpTarget(realJumpTarget, ++pc - codeOffset);
                    break;
                }
                case -78: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    TypeBinding typeBinding = this.getTypeBinding(descriptor, scope, false);
                    if (typeBinding != null) {
                        frame.addStackItem(new VerificationTypeInfo(typeBinding));
                    }
                    pc += 3;
                    break;
                }
                case -77: {
                    --frame.numberOfStackItems;
                    pc += 3;
                    break;
                }
                case -76: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    --frame.numberOfStackItems;
                    TypeBinding typeBinding = this.getTypeBinding(descriptor, scope, false);
                    if (typeBinding != null) {
                        frame.addStackItem(new VerificationTypeInfo(typeBinding));
                    }
                    pc += 3;
                    break;
                }
                case -75: {
                    frame.numberOfStackItems -= 2;
                    pc += 3;
                    break;
                }
                case -74: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    utf8index = this.u2At(poolContents, 1, constantPoolOffsets[nameAndTypeIndex]);
                    char[] name = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    frame.numberOfStackItems -= this.getParametersCount(descriptor) + 1;
                    char[] returnType = this.getReturnType(descriptor);
                    TypeBinding typeBinding = this.getTypeBinding(returnType, scope, false);
                    if (typeBinding != null) {
                        frame.addStackItem(new VerificationTypeInfo(typeBinding));
                    }
                    pc += 3;
                    break;
                }
                case -70: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    frame.numberOfStackItems -= this.getParametersCount(descriptor);
                    char[] returnType = this.getReturnType(descriptor);
                    TypeBinding typeBinding = this.getTypeBinding(returnType, scope, false);
                    if (typeBinding != null) {
                        frame.addStackItem(new VerificationTypeInfo(typeBinding));
                    }
                    pc += 5;
                    break;
                }
                case -73: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    utf8index = this.u2At(poolContents, 1, constantPoolOffsets[nameAndTypeIndex]);
                    char[] name = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    frame.numberOfStackItems -= this.getParametersCount(descriptor);
                    if (CharOperation.equals(ConstantPool.Init, name)) {
                        frame.stackItems[frame.numberOfStackItems - 1].tag = 7;
                    }
                    --frame.numberOfStackItems;
                    char[] returnType = this.getReturnType(descriptor);
                    TypeBinding typeBinding = this.getTypeBinding(returnType, scope, false);
                    if (typeBinding != null) {
                        frame.addStackItem(new VerificationTypeInfo(typeBinding));
                    }
                    pc += 3;
                    break;
                }
                case -72: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    utf8index = this.u2At(poolContents, 1, constantPoolOffsets[nameAndTypeIndex]);
                    char[] name = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    frame.numberOfStackItems -= this.getParametersCount(descriptor);
                    char[] returnType = this.getReturnType(descriptor);
                    TypeBinding typeBinding = this.getTypeBinding(returnType, scope, false);
                    if (typeBinding != null) {
                        frame.addStackItem(new VerificationTypeInfo(typeBinding));
                    }
                    pc += 3;
                    break;
                }
                case -71: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    utf8index = this.u2At(poolContents, 1, constantPoolOffsets[nameAndTypeIndex]);
                    char[] name = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    frame.numberOfStackItems -= this.getParametersCount(descriptor) + 1;
                    char[] returnType = this.getReturnType(descriptor);
                    TypeBinding typeBinding = this.getTypeBinding(returnType, scope, false);
                    if (typeBinding != null) {
                        frame.addStackItem(new VerificationTypeInfo(typeBinding));
                    }
                    pc += 5;
                    break;
                }
                case -69: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int utf8index = this.u2At(poolContents, 1, constantPoolOffsets[index]);
                    char[] className = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    TypeBinding typeBinding = this.getNewTypeBinding(className, scope);
                    VerificationTypeInfo verificationTypeInfo = new VerificationTypeInfo(8, typeBinding);
                    verificationTypeInfo.offset = currentPC;
                    frame.addStackItem(verificationTypeInfo);
                    pc += 3;
                    break;
                }
                case -68: {
                    ArrayBinding arrayType = null;
                    switch (this.u1At(bytecodes, 1, pc)) {
                        case 10: {
                            arrayType = scope.createArrayType(TypeBinding.INT, 1);
                            break;
                        }
                        case 8: {
                            arrayType = scope.createArrayType(TypeBinding.BYTE, 1);
                            break;
                        }
                        case 4: {
                            arrayType = scope.createArrayType(TypeBinding.BOOLEAN, 1);
                            break;
                        }
                        case 9: {
                            arrayType = scope.createArrayType(TypeBinding.SHORT, 1);
                            break;
                        }
                        case 5: {
                            arrayType = scope.createArrayType(TypeBinding.CHAR, 1);
                            break;
                        }
                        case 11: {
                            arrayType = scope.createArrayType(TypeBinding.LONG, 1);
                            break;
                        }
                        case 6: {
                            arrayType = scope.createArrayType(TypeBinding.FLOAT, 1);
                            break;
                        }
                        case 7: {
                            arrayType = scope.createArrayType(TypeBinding.DOUBLE, 1);
                        }
                    }
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(arrayType);
                    pc += 2;
                    break;
                }
                case -67: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int utf8index = this.u2At(poolContents, 1, constantPoolOffsets[index]);
                    char[] className = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    --frame.numberOfStackItems;
                    TypeBinding typeBinding = this.getANewArrayTypeBinding(className, scope);
                    if (typeBinding != null) {
                        if (typeBinding.isArrayType()) {
                            ArrayBinding arrayBinding = (ArrayBinding)typeBinding;
                            frame.addStackItem(new VerificationTypeInfo(scope.createArrayType(arrayBinding.leafComponentType(), arrayBinding.dimensions + 1)));
                        } else {
                            frame.addStackItem(new VerificationTypeInfo(scope.createArrayType(typeBinding, 1)));
                        }
                    }
                    pc += 3;
                    break;
                }
                case -66: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    ++pc;
                    break;
                }
                case -65: {
                    --frame.numberOfStackItems;
                    this.addRealJumpTarget(realJumpTarget, ++pc - codeOffset);
                    break;
                }
                case -64: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int utf8index = this.u2At(poolContents, 1, constantPoolOffsets[index]);
                    char[] className = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    TypeBinding typeBinding = this.getTypeBinding(className, scope, true);
                    if (typeBinding != null) {
                        frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(typeBinding);
                    }
                    pc += 3;
                    break;
                }
                case -63: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    pc += 3;
                    break;
                }
                case -62: 
                case -61: {
                    --frame.numberOfStackItems;
                    ++pc;
                    break;
                }
                case -60: {
                    VerificationTypeInfo localsN;
                    opcode = (byte)this.u1At(bytecodes, 1, pc);
                    if (opcode == -124) {
                        pc += 6;
                        break;
                    }
                    int index = this.u2At(bytecodes, 2, pc);
                    switch (opcode) {
                        case 21: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.INT));
                            break;
                        }
                        case 23: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.FLOAT));
                            break;
                        }
                        case 25: {
                            localsN = frame.locals[index];
                            if (localsN == null) {
                                localsN = this.retrieveLocal(currentPC, index);
                            }
                            frame.addStackItem(localsN);
                            break;
                        }
                        case 22: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.LONG));
                            break;
                        }
                        case 24: {
                            frame.addStackItem(new VerificationTypeInfo(TypeBinding.DOUBLE));
                            break;
                        }
                        case 54: {
                            --frame.numberOfStackItems;
                            break;
                        }
                        case 56: {
                            --frame.numberOfStackItems;
                            break;
                        }
                        case 58: {
                            frame.locals[index] = frame.stackItems[frame.numberOfStackItems - 1];
                            --frame.numberOfStackItems;
                            break;
                        }
                        case 55: {
                            --frame.numberOfStackItems;
                            break;
                        }
                        case 57: {
                            --frame.numberOfStackItems;
                        }
                    }
                    pc += 4;
                    break;
                }
                case -59: {
                    int index = this.u2At(bytecodes, 1, pc);
                    int utf8index = this.u2At(poolContents, 1, constantPoolOffsets[index]);
                    char[] className = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    int dimensions = this.u1At(bytecodes, 3, pc);
                    frame.numberOfStackItems -= dimensions;
                    TypeBinding typeBinding = this.getTypeBinding(className, scope, false);
                    if (typeBinding != null) {
                        frame.addStackItem(new VerificationTypeInfo(typeBinding));
                    }
                    pc += 4;
                    break;
                }
                case -58: 
                case -57: {
                    --frame.numberOfStackItems;
                    int jumpPC = currentPC + this.i2At(bytecodes, 1, pc);
                    this.addRealJumpTarget(realJumpTarget, jumpPC, frames, this.createNewFrame(jumpPC, frame, isClinit, methodBinding), scope);
                    pc += 3;
                    break;
                }
                case -56: {
                    int jumpPC = currentPC + this.i4At(bytecodes, 1, pc);
                    this.addRealJumpTarget(realJumpTarget, jumpPC, frames, this.createNewFrame(jumpPC, frame, isClinit, methodBinding), scope);
                    this.addRealJumpTarget(realJumpTarget, (pc += 5) - codeOffset);
                    break;
                }
                default: {
                    if (this.codeStream.methodDeclaration != null) {
                        this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidOpcode, new Object[]{opcode, pc, new String(methodBinding.shortReadableName())}), this.codeStream.methodDeclaration);
                        break;
                    }
                    this.codeStream.lambdaExpression.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidOpcode, new Object[]{opcode, pc, new String(methodBinding.shortReadableName())}), this.codeStream.lambdaExpression);
                }
            }
        } while (pc < codeLength + codeOffset);
        return this.filterFakeFrames(realJumpTarget, frames, codeLength);
    }

    private StackMapFrame createNewFrame(int currentPC, StackMapFrame frame, boolean isClinit, MethodBinding methodBinding) {
        StackMapFrame newFrame = frame.duplicate();
        newFrame.pc = currentPC;
        this.initializeLocals(isClinit ? true : methodBinding.isStatic(), currentPC, newFrame);
        return newFrame;
    }

    private int getDimensions(char[] returnType) {
        int dimensions = 0;
        while (returnType[dimensions] == '[') {
            ++dimensions;
        }
        return dimensions;
    }

    private void addRealJumpTarget(Set<Integer> realJumpTarget, int pc) {
        realJumpTarget.add(pc);
    }

    private void addRealJumpTarget(Set<Integer> realJumpTarget, int pc, Map<Integer, StackMapFrame> frames, StackMapFrame frame, Scope scope) {
        realJumpTarget.add(pc);
        this.add(frames, frame, scope);
    }

    private void add(Map<Integer, StackMapFrame> frames, StackMapFrame frame, Scope scope) {
        Integer key = frame.pc;
        StackMapFrame existingFrame = frames.get(key);
        if (existingFrame == null) {
            frames.put(key, frame);
        } else {
            frames.put(key, existingFrame.merge(frame, scope));
        }
    }

    private final int u1At(byte[] reference, int relativeOffset, int structOffset) {
        return reference[relativeOffset + structOffset] & 0xFF;
    }

    private final int u2At(byte[] reference, int relativeOffset, int structOffset) {
        int position = relativeOffset + structOffset;
        return ((reference[position++] & 0xFF) << 8) + (reference[position] & 0xFF);
    }

    private final long u4At(byte[] reference, int relativeOffset, int structOffset) {
        int position = relativeOffset + structOffset;
        return (((long)reference[position++] & 0xFFL) << 24) + (long)((reference[position++] & 0xFF) << 16) + (long)((reference[position++] & 0xFF) << 8) + (long)(reference[position] & 0xFF);
    }

    private final int i2At(byte[] reference, int relativeOffset, int structOffset) {
        int position = relativeOffset + structOffset;
        return (reference[position++] << 8) + (reference[position] & 0xFF);
    }

    public char[] utf8At(byte[] reference, int absoluteOffset, int bytesAvailable) {
        int length = bytesAvailable;
        char[] outputBuf = new char[bytesAvailable];
        int outputPos = 0;
        int readOffset = absoluteOffset;
        while (length != 0) {
            int x = reference[readOffset++] & 0xFF;
            --length;
            if ((0x80 & x) != 0) {
                if ((x & 0x20) != 0) {
                    length -= 2;
                    x = (x & 0xF) << 12 | (reference[readOffset++] & 0x3F) << 6 | reference[readOffset++] & 0x3F;
                } else {
                    --length;
                    x = (x & 0x1F) << 6 | reference[readOffset++] & 0x3F;
                }
            }
            outputBuf[outputPos++] = (char)x;
        }
        if (outputPos != bytesAvailable) {
            char[] cArray = outputBuf;
            outputBuf = new char[outputPos];
            System.arraycopy(cArray, 0, outputBuf, 0, outputPos);
        }
        return outputBuf;
    }
}

