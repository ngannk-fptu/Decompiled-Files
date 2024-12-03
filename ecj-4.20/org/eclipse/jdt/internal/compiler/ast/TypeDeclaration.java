/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.CompactConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.SimpleSetOfCharArray;
import org.eclipse.jdt.internal.compiler.util.Util;

public class TypeDeclaration
extends Statement
implements ProblemSeverities,
ReferenceContext {
    public static final int CLASS_DECL = 1;
    public static final int INTERFACE_DECL = 2;
    public static final int ENUM_DECL = 3;
    public static final int ANNOTATION_TYPE_DECL = 4;
    public static final int RECORD_DECL = 5;
    public int modifiers = 0;
    public int modifiersSourceStart;
    public int functionalExpressionsCount = 0;
    public Annotation[] annotations;
    public char[] name;
    public TypeReference superclass;
    public TypeReference[] superInterfaces;
    public FieldDeclaration[] fields;
    public AbstractMethodDeclaration[] methods;
    public TypeDeclaration[] memberTypes;
    public SourceTypeBinding binding;
    public ClassScope scope;
    public MethodScope initializerScope;
    public MethodScope staticInitializerScope;
    public boolean ignoreFurtherInvestigation = false;
    public int maxFieldCount;
    public int declarationSourceStart;
    public int declarationSourceEnd;
    public int restrictedIdentifierStart = -1;
    public int bodyStart;
    public int bodyEnd;
    public CompilationResult compilationResult;
    public MethodDeclaration[] missingAbstractMethods;
    public Javadoc javadoc;
    public QualifiedAllocationExpression allocation;
    public TypeDeclaration enclosingType;
    public FieldBinding enumValuesSyntheticfield;
    public int enumConstantsCounter;
    public TypeParameter[] typeParameters;
    public RecordComponent[] recordComponents;
    public int nRecordComponents;
    public static Set<String> disallowedComponentNames = new HashSet<String>(6);
    public TypeReference[] permittedTypes;

    static {
        disallowedComponentNames.add("clone");
        disallowedComponentNames.add("finalize");
        disallowedComponentNames.add("getClass");
        disallowedComponentNames.add("hashCode");
        disallowedComponentNames.add("notify");
        disallowedComponentNames.add("notifyAll");
        disallowedComponentNames.add("toString");
        disallowedComponentNames.add("wait");
    }

    public TypeDeclaration(CompilationResult compilationResult) {
        this.compilationResult = compilationResult;
    }

    @Override
    public void abort(int abortLevel, CategorizedProblem problem) {
        switch (abortLevel) {
            case 2: {
                throw new AbortCompilation(this.compilationResult, problem);
            }
            case 4: {
                throw new AbortCompilationUnit(this.compilationResult, problem);
            }
            case 16: {
                throw new AbortMethod(this.compilationResult, problem);
            }
        }
        throw new AbortType(this.compilationResult, problem);
    }

    public final void addClinit() {
        if (this.needClassInitMethod()) {
            AbstractMethodDeclaration[] methodDeclarations = this.methods;
            if (this.methods == null) {
                boolean length = false;
                methodDeclarations = new AbstractMethodDeclaration[1];
            } else {
                int length = methodDeclarations.length;
                AbstractMethodDeclaration[] abstractMethodDeclarationArray = methodDeclarations;
                methodDeclarations = new AbstractMethodDeclaration[length + 1];
                System.arraycopy(abstractMethodDeclarationArray, 0, methodDeclarations, 1, length);
            }
            Clinit clinit = new Clinit(this.compilationResult);
            methodDeclarations[0] = clinit;
            clinit.declarationSourceStart = clinit.sourceStart = this.sourceStart;
            clinit.declarationSourceEnd = clinit.sourceEnd = this.sourceEnd;
            clinit.bodyEnd = this.sourceEnd;
            this.methods = methodDeclarations;
        }
    }

    public MethodDeclaration addMissingAbstractMethodFor(MethodBinding methodBinding) {
        TypeBinding[] argumentTypes = methodBinding.parameters;
        int argumentsLength = argumentTypes.length;
        MethodDeclaration methodDeclaration = new MethodDeclaration(this.compilationResult);
        methodDeclaration.selector = methodBinding.selector;
        methodDeclaration.sourceStart = this.sourceStart;
        methodDeclaration.sourceEnd = this.sourceEnd;
        methodDeclaration.modifiers = methodBinding.getAccessFlags() & 0xFFFFFBFF;
        if (argumentsLength > 0) {
            String baseName = "arg";
            methodDeclaration.arguments = new Argument[argumentsLength];
            Argument[] arguments = methodDeclaration.arguments;
            int i = argumentsLength;
            while (--i >= 0) {
                arguments[i] = new Argument((String.valueOf(baseName) + i).toCharArray(), 0L, null, 0);
            }
        }
        if (this.missingAbstractMethods == null) {
            this.missingAbstractMethods = new MethodDeclaration[]{methodDeclaration};
        } else {
            MethodDeclaration[] newMethods = new MethodDeclaration[this.missingAbstractMethods.length + 1];
            System.arraycopy(this.missingAbstractMethods, 0, newMethods, 1, this.missingAbstractMethods.length);
            newMethods[0] = methodDeclaration;
            this.missingAbstractMethods = newMethods;
        }
        methodDeclaration.binding = new MethodBinding(methodDeclaration.modifiers | 0x1000, methodBinding.selector, methodBinding.returnType, argumentsLength == 0 ? Binding.NO_PARAMETERS : argumentTypes, methodBinding.thrownExceptions, this.binding);
        methodDeclaration.scope = new MethodScope(this.scope, methodDeclaration, true);
        methodDeclaration.bindArguments();
        return methodDeclaration;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return flowInfo;
        }
        try {
            if ((flowInfo.tagBits & 1) == 0) {
                this.bits |= Integer.MIN_VALUE;
                LocalTypeBinding localType = (LocalTypeBinding)this.binding;
                localType.setConstantPoolName(currentScope.compilationUnitScope().computeConstantPoolName(localType));
            }
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
            this.updateMaxFieldCount();
            this.internalAnalyseCode(flowContext, flowInfo);
        }
        catch (AbortType abortType) {
            this.ignoreFurtherInvestigation = true;
        }
        return flowInfo;
    }

    public void analyseCode(ClassScope enclosingClassScope) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            this.updateMaxFieldCount();
            this.internalAnalyseCode(null, FlowInfo.initial(this.maxFieldCount));
        }
        catch (AbortType abortType) {
            this.ignoreFurtherInvestigation = true;
        }
    }

    public void analyseCode(ClassScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            if ((flowInfo.tagBits & 1) == 0) {
                this.bits |= Integer.MIN_VALUE;
                LocalTypeBinding localType = (LocalTypeBinding)this.binding;
                localType.setConstantPoolName(currentScope.compilationUnitScope().computeConstantPoolName(localType));
            }
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
            this.updateMaxFieldCount();
            this.internalAnalyseCode(flowContext, flowInfo);
        }
        catch (AbortType abortType) {
            this.ignoreFurtherInvestigation = true;
        }
    }

    public void analyseCode(CompilationUnitScope unitScope) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            this.internalAnalyseCode(null, FlowInfo.initial(this.maxFieldCount));
        }
        catch (AbortType abortType) {
            this.ignoreFurtherInvestigation = true;
        }
    }

    public boolean checkConstructors(Parser parser) {
        boolean hasConstructor = false;
        if (this.methods != null) {
            int i = this.methods.length;
            while (--i >= 0) {
                AbstractMethodDeclaration am = this.methods[i];
                if (!am.isConstructor()) continue;
                if (!CharOperation.equals(am.selector, this.name)) {
                    ConstructorDeclaration c = (ConstructorDeclaration)am;
                    if (c.constructorCall != null && !c.constructorCall.isImplicitSuper()) continue;
                    MethodDeclaration m = parser.convertToMethodDeclaration(c, this.compilationResult);
                    this.methods[i] = m;
                    continue;
                }
                switch (TypeDeclaration.kind(this.modifiers)) {
                    case 2: {
                        parser.problemReporter().interfaceCannotHaveConstructors((ConstructorDeclaration)am);
                        break;
                    }
                    case 4: {
                        parser.problemReporter().annotationTypeDeclarationCannotHaveConstructor((ConstructorDeclaration)am);
                    }
                }
                hasConstructor = true;
            }
        }
        return hasConstructor;
    }

    @Override
    public CompilationResult compilationResult() {
        return this.compilationResult;
    }

    public ConstructorDeclaration createDefaultConstructorForRecord(boolean needExplicitConstructorCall, boolean needToInsert) {
        int l;
        ConstructorDeclaration constructor = new ConstructorDeclaration(this.compilationResult);
        constructor.bits |= 0x600;
        constructor.selector = this.name;
        constructor.modifiers = this.modifiers & 7;
        constructor.arguments = this.getArgumentsFromComponents(this.recordComponents);
        int i = 0;
        int max = constructor.arguments.length;
        while (i < max) {
            if ((constructor.arguments[i].bits & 0x100000) != 0) {
                constructor.bits |= 0x100000;
                break;
            }
            ++i;
        }
        constructor.sourceStart = constructor.bodyStart = this.sourceStart;
        constructor.declarationSourceStart = constructor.bodyStart;
        constructor.sourceEnd = constructor.bodyEnd = this.sourceStart - 1;
        constructor.declarationSourceEnd = constructor.bodyEnd;
        if (needExplicitConstructorCall) {
            constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
            constructor.constructorCall.sourceStart = this.sourceStart;
            constructor.constructorCall.sourceEnd = this.sourceEnd;
        }
        ArrayList<Assignment> statements = new ArrayList<Assignment>();
        int n = l = this.recordComponents != null ? this.recordComponents.length : 0;
        if (l > 0 && this.fields != null) {
            List fNames = Arrays.stream(this.fields).filter(f -> f.isARecordComponent).map(f -> new String(f.name)).collect(Collectors.toList());
            int i2 = 0;
            while (i2 < l) {
                RecordComponent component = this.recordComponents[i2];
                if (fNames.contains(new String(component.name))) {
                    FieldReference lhs = new FieldReference(component.name, 0L);
                    lhs.receiver = ThisReference.implicitThis();
                    statements.add(new Assignment(lhs, new SingleNameReference(component.name, 0L), 0));
                }
                ++i2;
            }
        }
        constructor.statements = statements.toArray(new Statement[0]);
        if (needToInsert) {
            if (this.methods == null) {
                this.methods = new AbstractMethodDeclaration[]{constructor};
            } else {
                AbstractMethodDeclaration[] newMethods = new AbstractMethodDeclaration[this.methods.length + 1];
                System.arraycopy(this.methods, 0, newMethods, 1, this.methods.length);
                newMethods[0] = constructor;
                this.methods = newMethods;
            }
        }
        return constructor;
    }

    private Argument[] getArgumentsFromComponents(RecordComponent[] comps) {
        Argument[] args2 = comps == null || comps.length == 0 ? ASTNode.NO_ARGUMENTS : new Argument[comps.length];
        int count = 0;
        RecordComponent[] recordComponentArray = comps;
        int n = comps.length;
        int n2 = 0;
        while (n2 < n) {
            RecordComponent comp = recordComponentArray[n2];
            Argument argument = new Argument(comp.name, (long)comp.sourceStart << 32 | (long)comp.sourceEnd, comp.type, 0);
            args2[count++] = argument;
            ++n2;
        }
        return args2;
    }

    public ConstructorDeclaration createDefaultConstructor(boolean needExplicitConstructorCall, boolean needToInsert) {
        if (this.isRecord()) {
            return this.createDefaultConstructorForRecord(needExplicitConstructorCall, needToInsert);
        }
        ConstructorDeclaration constructor = new ConstructorDeclaration(this.compilationResult);
        constructor.bits |= 0x80;
        constructor.selector = this.name;
        constructor.modifiers = this.modifiers & 7;
        constructor.declarationSourceStart = constructor.sourceStart = this.sourceStart;
        constructor.sourceEnd = constructor.bodyEnd = this.sourceEnd;
        constructor.declarationSourceEnd = constructor.bodyEnd;
        if (needExplicitConstructorCall) {
            constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
            constructor.constructorCall.sourceStart = this.sourceStart;
            constructor.constructorCall.sourceEnd = this.sourceEnd;
        }
        if (needToInsert) {
            if (this.methods == null) {
                this.methods = new AbstractMethodDeclaration[]{constructor};
            } else {
                AbstractMethodDeclaration[] newMethods = new AbstractMethodDeclaration[this.methods.length + 1];
                System.arraycopy(this.methods, 0, newMethods, 1, this.methods.length);
                newMethods[0] = constructor;
                this.methods = newMethods;
            }
        }
        return constructor;
    }

    public MethodBinding createDefaultConstructorWithBinding(MethodBinding inheritedConstructorBinding, boolean eraseThrownExceptions) {
        MethodBinding[] methodBindings;
        int i;
        String baseName = "$anonymous";
        TypeBinding[] argumentTypes = inheritedConstructorBinding.parameters;
        int argumentsLength = argumentTypes.length;
        ConstructorDeclaration constructor = new ConstructorDeclaration(this.compilationResult);
        constructor.selector = new char[]{'x'};
        constructor.sourceStart = this.sourceStart;
        constructor.sourceEnd = this.sourceEnd;
        int newModifiers = this.modifiers & 7;
        if (inheritedConstructorBinding.isVarargs()) {
            newModifiers |= 0x80;
        }
        constructor.modifiers = newModifiers;
        constructor.bits |= 0x80;
        if (argumentsLength > 0) {
            constructor.arguments = new Argument[argumentsLength];
            Argument[] arguments = constructor.arguments;
            i = argumentsLength;
            while (--i >= 0) {
                arguments[i] = new Argument((String.valueOf(baseName) + i).toCharArray(), 0L, null, 0);
            }
        }
        constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
        constructor.constructorCall.sourceStart = this.sourceStart;
        constructor.constructorCall.sourceEnd = this.sourceEnd;
        if (argumentsLength > 0) {
            constructor.constructorCall.arguments = new Expression[argumentsLength];
            Expression[] args1 = constructor.constructorCall.arguments;
            i = argumentsLength;
            while (--i >= 0) {
                args1[i] = new SingleNameReference((String.valueOf(baseName) + i).toCharArray(), 0L);
            }
        }
        if (this.methods == null) {
            this.methods = new AbstractMethodDeclaration[]{constructor};
        } else {
            AbstractMethodDeclaration[] newMethods = new AbstractMethodDeclaration[this.methods.length + 1];
            System.arraycopy(this.methods, 0, newMethods, 1, this.methods.length);
            newMethods[0] = constructor;
            this.methods = newMethods;
        }
        ReferenceBinding[] thrownExceptions = eraseThrownExceptions ? this.scope.environment().convertToRawTypes(inheritedConstructorBinding.thrownExceptions, true, true) : inheritedConstructorBinding.thrownExceptions;
        SourceTypeBinding sourceType = this.binding;
        constructor.binding = new MethodBinding(constructor.modifiers, argumentsLength == 0 ? Binding.NO_PARAMETERS : argumentTypes, thrownExceptions, sourceType);
        constructor.binding.tagBits |= inheritedConstructorBinding.tagBits & 0x80L;
        constructor.binding.modifiers |= 0x4000000;
        if (inheritedConstructorBinding.parameterNonNullness != null && argumentsLength > 0) {
            int len = inheritedConstructorBinding.parameterNonNullness.length;
            constructor.binding.parameterNonNullness = new Boolean[len];
            System.arraycopy(inheritedConstructorBinding.parameterNonNullness, 0, constructor.binding.parameterNonNullness, 0, len);
        }
        constructor.scope = new MethodScope(this.scope, constructor, true);
        constructor.bindArguments();
        constructor.constructorCall.resolve(constructor.scope);
        MethodBinding[] methodBindingArray = methodBindings = sourceType.methods();
        int length = methodBindings.length;
        methodBindings = new MethodBinding[length + 1];
        System.arraycopy(methodBindingArray, 0, methodBindings, 1, length);
        methodBindings[0] = constructor.binding;
        if (++length > 1) {
            ReferenceBinding.sortMethods(methodBindings, 0, length);
        }
        sourceType.setMethods(methodBindings);
        return constructor.binding;
    }

    public FieldDeclaration declarationOf(FieldBinding fieldBinding) {
        if (fieldBinding != null && this.fields != null) {
            int i = 0;
            int max = this.fields.length;
            while (i < max) {
                FieldDeclaration fieldDecl = this.fields[i];
                if (fieldDecl.binding == fieldBinding) {
                    return fieldDecl;
                }
                ++i;
            }
        }
        return null;
    }

    public TypeDeclaration declarationOf(MemberTypeBinding memberTypeBinding) {
        if (memberTypeBinding != null && this.memberTypes != null) {
            int i = 0;
            int max = this.memberTypes.length;
            while (i < max) {
                TypeDeclaration memberTypeDecl = this.memberTypes[i];
                if (TypeBinding.equalsEquals(memberTypeDecl.binding, memberTypeBinding)) {
                    return memberTypeDecl;
                }
                ++i;
            }
        }
        return null;
    }

    public AbstractMethodDeclaration declarationOf(MethodBinding methodBinding) {
        if (methodBinding != null && this.methods != null) {
            int i = 0;
            int max = this.methods.length;
            while (i < max) {
                AbstractMethodDeclaration methodDecl = this.methods[i];
                if (methodDecl.binding == methodBinding) {
                    return methodDecl;
                }
                ++i;
            }
        }
        return null;
    }

    public RecordComponent declarationOf(RecordComponentBinding recordComponentBinding) {
        if (recordComponentBinding != null && this.recordComponents != null) {
            int i = 0;
            int max = this.fields.length;
            while (i < max) {
                RecordComponent recordComponent = this.recordComponents[i];
                if (recordComponent.binding == recordComponentBinding) {
                    return recordComponent;
                }
                ++i;
            }
        }
        return null;
    }

    public TypeDeclaration declarationOfType(char[][] typeName) {
        int typeNameLength = typeName.length;
        if (typeNameLength < 1 || !CharOperation.equals(typeName[0], this.name)) {
            return null;
        }
        if (typeNameLength == 1) {
            return this;
        }
        char[][] subTypeName = new char[typeNameLength - 1][];
        System.arraycopy(typeName, 1, subTypeName, 0, typeNameLength - 1);
        int i = 0;
        while (i < this.memberTypes.length) {
            TypeDeclaration typeDecl = this.memberTypes[i].declarationOfType(subTypeName);
            if (typeDecl != null) {
                return typeDecl;
            }
            ++i;
        }
        return null;
    }

    @Override
    public CompilationUnitDeclaration getCompilationUnitDeclaration() {
        if (this.scope != null) {
            return this.scope.compilationUnitScope().referenceContext;
        }
        return null;
    }

    public ConstructorDeclaration getConstructor(Parser parser) {
        ConstructorDeclaration cd = null;
        if (this.methods != null) {
            int i = this.methods.length;
            while (--i >= 0) {
                AbstractMethodDeclaration am = this.methods[i];
                if (!am.isConstructor()) continue;
                if (!CharOperation.equals(am.selector, this.name)) {
                    ConstructorDeclaration c = (ConstructorDeclaration)am;
                    if (c.constructorCall != null && !c.constructorCall.isImplicitSuper()) continue;
                    MethodDeclaration m = parser.convertToMethodDeclaration(c, this.compilationResult);
                    this.methods[i] = m;
                    continue;
                }
                if (am instanceof CompactConstructorDeclaration) {
                    CompactConstructorDeclaration ccd = (CompactConstructorDeclaration)am;
                    ccd.recordDeclaration = this;
                    if (ccd.arguments == null) {
                        ccd.arguments = this.getArgumentsFromComponents(this.recordComponents);
                    }
                    return ccd;
                }
                if ((this.recordComponents == null || this.recordComponents.length == 0) && am.arguments == null) {
                    return (ConstructorDeclaration)am;
                }
                cd = (ConstructorDeclaration)am;
            }
        }
        return cd;
    }

    public void generateCode(ClassFile enclosingClassFile) {
        if ((this.bits & 0x2000) != 0) {
            return;
        }
        this.bits |= 0x2000;
        if (this.ignoreFurtherInvestigation) {
            if (this.binding == null) {
                return;
            }
            ClassFile.createProblemType(this, this.scope.referenceCompilationUnit().compilationResult);
            return;
        }
        try {
            ClassFile ocf;
            ClassFile classFile = ClassFile.getNewInstance(this.binding);
            classFile.initialize(this.binding, enclosingClassFile, false);
            if (this.binding.isMemberType()) {
                classFile.recordInnerClasses(this.binding);
            } else if (this.binding.isLocalType()) {
                enclosingClassFile.recordInnerClasses(this.binding);
                classFile.recordInnerClasses(this.binding);
            }
            SourceTypeBinding nestHost = this.binding.getNestHost();
            if (nestHost != null && !TypeBinding.equalsEquals(nestHost, this.binding) && (ocf = enclosingClassFile.outerMostEnclosingClassFile()) != null) {
                ocf.recordNestMember(this.binding);
            }
            TypeVariableBinding[] typeVariables = this.binding.typeVariables();
            int i = 0;
            int max = typeVariables.length;
            while (i < max) {
                TypeVariableBinding typeVariableBinding = typeVariables[i];
                if ((typeVariableBinding.tagBits & 0x800L) != 0L) {
                    Util.recordNestedType(classFile, typeVariableBinding);
                }
                ++i;
            }
            classFile.addFieldInfos();
            if (this.memberTypes != null) {
                i = 0;
                max = this.memberTypes.length;
                while (i < max) {
                    TypeDeclaration memberType = this.memberTypes[i];
                    classFile.recordInnerClasses(memberType.binding);
                    memberType.generateCode(this.scope, classFile);
                    ++i;
                }
            }
            classFile.setForMethodInfos();
            if (this.methods != null) {
                i = 0;
                max = this.methods.length;
                while (i < max) {
                    this.methods[i].generateCode(this.scope, classFile);
                    ++i;
                }
            }
            classFile.addSpecialMethods(this);
            if (this.ignoreFurtherInvestigation) {
                throw new AbortType(this.scope.referenceCompilationUnit().compilationResult, null);
            }
            classFile.addAttributes();
            this.scope.referenceCompilationUnit().compilationResult.record(this.binding.constantPoolName(), classFile);
        }
        catch (AbortType abortType) {
            if (this.binding == null) {
                return;
            }
            ClassFile.createProblemType(this, this.scope.referenceCompilationUnit().compilationResult);
        }
    }

    @Override
    public void generateCode(BlockScope blockScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        if ((this.bits & 0x2000) != 0) {
            return;
        }
        int pc = codeStream.position;
        if (this.binding != null) {
            SyntheticArgumentBinding[] enclosingInstances = ((NestedTypeBinding)this.binding).syntheticEnclosingInstances();
            int i = 0;
            int slotSize = 0;
            int count = enclosingInstances == null ? 0 : enclosingInstances.length;
            while (i < count) {
                SyntheticArgumentBinding enclosingInstance = enclosingInstances[i];
                enclosingInstance.resolvedPosition = ++slotSize;
                if (slotSize > 255) {
                    blockScope.problemReporter().noMoreAvailableSpaceForArgument(enclosingInstance, blockScope.referenceType());
                }
                ++i;
            }
        }
        this.generateCode(codeStream.classFile);
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    public void generateCode(ClassScope classScope, ClassFile enclosingClassFile) {
        if ((this.bits & 0x2000) != 0) {
            return;
        }
        if (this.binding != null) {
            SyntheticArgumentBinding[] enclosingInstances = ((NestedTypeBinding)this.binding).syntheticEnclosingInstances();
            int i = 0;
            int slotSize = 0;
            int count = enclosingInstances == null ? 0 : enclosingInstances.length;
            while (i < count) {
                SyntheticArgumentBinding enclosingInstance = enclosingInstances[i];
                enclosingInstance.resolvedPosition = ++slotSize;
                if (slotSize > 255) {
                    classScope.problemReporter().noMoreAvailableSpaceForArgument(enclosingInstance, classScope.referenceType());
                }
                ++i;
            }
        }
        this.generateCode(enclosingClassFile);
    }

    public void generateCode(CompilationUnitScope unitScope) {
        this.generateCode((ClassFile)null);
    }

    @Override
    public boolean hasErrors() {
        return this.ignoreFurtherInvestigation;
    }

    private void internalAnalyseCode(FlowContext flowContext, FlowInfo flowInfo) {
        int count;
        int i;
        this.checkYieldUsage();
        if (!this.binding.isUsed() && this.binding.isOrEnclosedByPrivateType() && !this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
            this.scope.problemReporter().unusedPrivateType(this);
        }
        if (this.typeParameters != null && !this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
            int i2 = 0;
            int length = this.typeParameters.length;
            while (i2 < length) {
                TypeParameter typeParameter = this.typeParameters[i2];
                if ((typeParameter.binding.modifiers & 0x8000000) == 0) {
                    this.scope.problemReporter().unusedTypeParameter(typeParameter);
                }
                ++i2;
            }
        }
        FlowContext parentContext = flowContext instanceof InitializationFlowContext ? null : flowContext;
        InitializationFlowContext initializerContext = new InitializationFlowContext(parentContext, this, flowInfo, flowContext, this.initializerScope);
        InitializationFlowContext staticInitializerContext = new InitializationFlowContext(null, this, flowInfo, flowContext, this.staticInitializerScope);
        FlowInfo nonStaticFieldInfo = flowInfo.unconditionalFieldLessCopy();
        FlowInfo staticFieldInfo = flowInfo.unconditionalFieldLessCopy();
        if (this.fields != null) {
            i = 0;
            count = this.fields.length;
            while (i < count) {
                FieldDeclaration field = this.fields[i];
                if (field.isStatic()) {
                    if ((staticFieldInfo.tagBits & 1) != 0) {
                        field.bits &= Integer.MAX_VALUE;
                    }
                    staticInitializerContext.handledExceptions = Binding.ANY_EXCEPTION;
                    if ((staticFieldInfo = field.analyseCode(this.staticInitializerScope, (FlowContext)staticInitializerContext, staticFieldInfo)) == FlowInfo.DEAD_END) {
                        this.staticInitializerScope.problemReporter().initializerMustCompleteNormally(field);
                        staticFieldInfo = FlowInfo.initial(this.maxFieldCount).setReachMode(1);
                    }
                } else {
                    if ((nonStaticFieldInfo.tagBits & 1) != 0) {
                        field.bits &= Integer.MAX_VALUE;
                    }
                    initializerContext.handledExceptions = Binding.ANY_EXCEPTION;
                    if ((nonStaticFieldInfo = field.analyseCode(this.initializerScope, (FlowContext)initializerContext, nonStaticFieldInfo)) == FlowInfo.DEAD_END) {
                        this.initializerScope.problemReporter().initializerMustCompleteNormally(field);
                        nonStaticFieldInfo = FlowInfo.initial(this.maxFieldCount).setReachMode(1);
                    }
                }
                ++i;
            }
        }
        if (this.memberTypes != null) {
            i = 0;
            count = this.memberTypes.length;
            while (i < count) {
                if (flowContext != null) {
                    this.memberTypes[i].analyseCode(this.scope, flowContext, ((FlowInfo)nonStaticFieldInfo).copy().setReachMode(flowInfo.reachMode()));
                } else {
                    this.memberTypes[i].analyseCode(this.scope);
                }
                ++i;
            }
        }
        if (!(this.scope.compilerOptions().complianceLevel < 0x350000L || this.methods != null && this.methods[0].isClinit())) {
            Clinit clinit = new Clinit(this.compilationResult);
            clinit.declarationSourceStart = clinit.sourceStart = this.sourceStart;
            clinit.declarationSourceEnd = clinit.sourceEnd = this.sourceEnd;
            clinit.bodyEnd = this.sourceEnd;
            int length = this.methods == null ? 0 : this.methods.length;
            AbstractMethodDeclaration[] methodDeclarations = new AbstractMethodDeclaration[length + 1];
            methodDeclarations[0] = clinit;
            if (this.methods != null) {
                System.arraycopy(this.methods, 0, methodDeclarations, 1, length);
            }
        }
        if (this.methods != null) {
            UnconditionalFlowInfo outerInfo = flowInfo.unconditionalFieldLessCopy();
            FlowInfo constructorInfo = ((FlowInfo)nonStaticFieldInfo).unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo);
            SimpleSetOfCharArray jUnitMethodSourceValues = this.getJUnitMethodSourceValues();
            int i3 = 0;
            int count2 = this.methods.length;
            while (i3 < count2) {
                AbstractMethodDeclaration method = this.methods[i3];
                if (!method.ignoreFurtherInvestigation) {
                    if (method.isInitializationMethod()) {
                        if (method.isStatic()) {
                            ((Clinit)method).analyseCode(this.scope, staticInitializerContext, staticFieldInfo.unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo));
                        } else {
                            ((ConstructorDeclaration)method).analyseCode(this.scope, initializerContext, constructorInfo.copy(), flowInfo.reachMode());
                        }
                    } else {
                        if (method.arguments == null && jUnitMethodSourceValues.includes(method.selector) && method.binding != null) {
                            method.binding.modifiers |= 0x8000000;
                        }
                        ((MethodDeclaration)method).analyseCode(this.scope, parentContext, flowInfo.copy());
                    }
                }
                ++i3;
            }
        }
        if (this.binding.isEnum() && !this.binding.isAnonymousType()) {
            this.enumValuesSyntheticfield = this.binding.addSyntheticFieldForEnumValues();
        }
    }

    private void checkYieldUsage() {
        long sourceLevel = this.scope.compilerOptions().sourceLevel;
        if (sourceLevel < 0x3A0000L || this.name == null || !"yield".equals(new String(this.name))) {
            return;
        }
        if (sourceLevel >= 0x3A0000L) {
            this.scope.problemReporter().switchExpressionsYieldTypeDeclarationError(this);
        } else {
            this.scope.problemReporter().switchExpressionsYieldTypeDeclarationWarning(this);
        }
    }

    private SimpleSetOfCharArray getJUnitMethodSourceValues() {
        SimpleSetOfCharArray junitMethodSourceValues = new SimpleSetOfCharArray();
        AbstractMethodDeclaration[] abstractMethodDeclarationArray = this.methods;
        int n = this.methods.length;
        int n2 = 0;
        while (n2 < n) {
            AbstractMethodDeclaration methodDeclaration = abstractMethodDeclarationArray[n2];
            if (methodDeclaration.annotations != null) {
                Annotation[] annotationArray = methodDeclaration.annotations;
                int n3 = methodDeclaration.annotations.length;
                int n4 = 0;
                while (n4 < n3) {
                    Annotation annotation = annotationArray[n4];
                    if (annotation.resolvedType != null && annotation.resolvedType.id == 93) {
                        this.addJUnitMethodSourceValues(junitMethodSourceValues, annotation, methodDeclaration.selector);
                    }
                    ++n4;
                }
            }
            ++n2;
        }
        return junitMethodSourceValues;
    }

    private void addJUnitMethodSourceValues(SimpleSetOfCharArray junitMethodSourceValues, Annotation annotation, char[] methodName) {
        MemberValuePair[] memberValuePairArray = annotation.memberValuePairs();
        int n = memberValuePairArray.length;
        int n2 = 0;
        while (n2 < n) {
            MemberValuePair memberValuePair = memberValuePairArray[n2];
            if (CharOperation.equals(memberValuePair.name, TypeConstants.VALUE)) {
                Expression value = memberValuePair.value;
                if (value instanceof ArrayInitializer) {
                    ArrayInitializer arrayInitializer = (ArrayInitializer)value;
                    Expression[] expressionArray = arrayInitializer.expressions;
                    int n3 = arrayInitializer.expressions.length;
                    int n4 = 0;
                    while (n4 < n3) {
                        Expression arrayValue = expressionArray[n4];
                        junitMethodSourceValues.add(this.getValueAsChars(arrayValue));
                        ++n4;
                    }
                } else {
                    junitMethodSourceValues.add(this.getValueAsChars(value));
                }
                return;
            }
            ++n2;
        }
        junitMethodSourceValues.add(methodName);
    }

    private char[] getValueAsChars(Expression value) {
        if (value instanceof StringLiteral) {
            return ((StringLiteral)value).source;
        }
        if (value.constant instanceof StringConstant) {
            return ((StringConstant)value.constant).stringValue().toCharArray();
        }
        return CharOperation.NO_CHAR;
    }

    public static final int kind(int flags) {
        switch (flags & 0x1006200) {
            case 512: {
                return 2;
            }
            case 8704: {
                return 4;
            }
            case 16384: {
                return 3;
            }
            case 0x1000000: {
                return 5;
            }
        }
        return 1;
    }

    public boolean isRecord() {
        return (this.modifiers & 0x1000000) != 0;
    }

    public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) != 0) {
            return;
        }
        NestedTypeBinding nestedType = (NestedTypeBinding)this.binding;
        MethodScope methodScope = currentScope.methodScope();
        if (!methodScope.isStatic && !methodScope.isConstructorCall) {
            nestedType.addSyntheticArgumentAndField(nestedType.enclosingType());
        }
        if (nestedType.isAnonymousType()) {
            NestedTypeBinding nestedEnclosing;
            SyntheticArgumentBinding syntheticEnclosingInstanceArgument;
            ReferenceBinding enclosing;
            ReferenceBinding superclassBinding = (ReferenceBinding)nestedType.superclass.erasure();
            if (!(superclassBinding.enclosingType() == null || superclassBinding.isStatic() || superclassBinding.isLocalType() && ((NestedTypeBinding)superclassBinding).getSyntheticField(superclassBinding.enclosingType(), true) == null && !superclassBinding.isMemberType())) {
                nestedType.addSyntheticArgument(superclassBinding.enclosingType());
            }
            if (!methodScope.isStatic && methodScope.isConstructorCall && currentScope.compilerOptions().complianceLevel >= 0x310000L && (enclosing = nestedType.enclosingType()).isNestedType() && (syntheticEnclosingInstanceArgument = (nestedEnclosing = (NestedTypeBinding)enclosing).getSyntheticArgument(nestedEnclosing.enclosingType(), true, false)) != null) {
                nestedType.addSyntheticArgumentAndField(syntheticEnclosingInstanceArgument);
            }
        }
    }

    public void manageEnclosingInstanceAccessIfNecessary(ClassScope currentScope, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) == 0) {
            NestedTypeBinding nestedType = (NestedTypeBinding)this.binding;
            nestedType.addSyntheticArgumentAndField(this.binding.enclosingType());
        }
    }

    public final boolean needClassInitMethod() {
        if ((this.bits & 1) != 0) {
            return true;
        }
        switch (TypeDeclaration.kind(this.modifiers)) {
            case 2: 
            case 4: {
                return this.fields != null;
            }
            case 3: {
                return true;
            }
        }
        if (this.fields != null) {
            int i = this.fields.length;
            while (--i >= 0) {
                FieldDeclaration field = this.fields[i];
                if ((field.modifiers & 8) == 0) continue;
                return true;
            }
        }
        return false;
    }

    public void parseMethods(Parser parser, CompilationUnitDeclaration unit) {
        int i;
        int length;
        if (unit.ignoreMethodBodies) {
            return;
        }
        if (this.memberTypes != null) {
            length = this.memberTypes.length;
            i = 0;
            while (i < length) {
                TypeDeclaration typeDeclaration = this.memberTypes[i];
                typeDeclaration.parseMethods(parser, unit);
                this.bits |= typeDeclaration.bits & 0x80000;
                ++i;
            }
        }
        if (this.methods != null) {
            length = this.methods.length;
            i = 0;
            while (i < length) {
                AbstractMethodDeclaration abstractMethodDeclaration = this.methods[i];
                abstractMethodDeclaration.parseStatements(parser, unit);
                this.bits |= abstractMethodDeclaration.bits & 0x80000;
                ++i;
            }
        }
        if (this.fields != null) {
            length = this.fields.length;
            i = 0;
            while (i < length) {
                FieldDeclaration fieldDeclaration = this.fields[i];
                switch (fieldDeclaration.getKind()) {
                    case 2: {
                        ((Initializer)fieldDeclaration).parseStatements(parser, this, unit);
                        this.bits |= fieldDeclaration.bits & 0x80000;
                    }
                }
                ++i;
            }
        }
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        if (this.javadoc != null) {
            this.javadoc.print(indent, output);
        }
        if ((this.bits & 0x200) == 0) {
            TypeDeclaration.printIndent(indent, output);
            this.printHeader(0, output);
        }
        return this.printBody(indent, output);
    }

    public StringBuffer printBody(int indent, StringBuffer output) {
        int i;
        output.append(" {");
        if (this.memberTypes != null) {
            i = 0;
            while (i < this.memberTypes.length) {
                if (this.memberTypes[i] != null) {
                    output.append('\n');
                    this.memberTypes[i].print(indent + 1, output);
                }
                ++i;
            }
        }
        if (this.fields != null) {
            int fieldI = 0;
            while (fieldI < this.fields.length) {
                if (this.fields[fieldI] != null) {
                    output.append('\n');
                    this.fields[fieldI].print(indent + 1, output);
                }
                ++fieldI;
            }
        }
        if (this.methods != null) {
            i = 0;
            while (i < this.methods.length) {
                if (this.methods[i] != null) {
                    output.append('\n');
                    this.methods[i].print(indent + 1, output);
                }
                ++i;
            }
        }
        output.append('\n');
        return TypeDeclaration.printIndent(indent, output).append('}');
    }

    public StringBuffer printHeader(int indent, StringBuffer output) {
        int i;
        TypeDeclaration.printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            TypeDeclaration.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        switch (TypeDeclaration.kind(this.modifiers)) {
            case 1: {
                output.append("class ");
                break;
            }
            case 2: {
                output.append("interface ");
                break;
            }
            case 3: {
                output.append("enum ");
                break;
            }
            case 4: {
                output.append("@interface ");
                break;
            }
            case 5: {
                output.append("record ");
            }
        }
        output.append(this.name);
        if (this.isRecord()) {
            output.append('(');
            if (this.nRecordComponents > 0 && this.fields != null) {
                i = 0;
                while (i < this.nRecordComponents) {
                    if (i > 0) {
                        output.append(", ");
                    }
                    output.append(this.fields[i].type.getTypeName()[0]);
                    output.append(' ');
                    output.append(this.fields[i].name);
                    ++i;
                }
            }
            output.append(')');
        }
        if (this.typeParameters != null) {
            output.append("<");
            i = 0;
            while (i < this.typeParameters.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.typeParameters[i].print(0, output);
                ++i;
            }
            output.append(">");
        }
        if (!this.isRecord() && this.superclass != null) {
            output.append(" extends ");
            this.superclass.print(0, output);
        }
        if (this.superInterfaces != null && this.superInterfaces.length > 0) {
            switch (TypeDeclaration.kind(this.modifiers)) {
                case 1: 
                case 3: 
                case 5: {
                    output.append(" implements ");
                    break;
                }
                case 2: 
                case 4: {
                    output.append(" extends ");
                }
            }
            i = 0;
            while (i < this.superInterfaces.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.superInterfaces[i].print(0, output);
                ++i;
            }
        }
        if (this.permittedTypes != null && this.permittedTypes.length > 0) {
            output.append(" permits ");
            i = 0;
            while (i < this.permittedTypes.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.permittedTypes[i].print(0, output);
                ++i;
            }
        }
        return output;
    }

    @Override
    public StringBuffer printStatement(int tab, StringBuffer output) {
        return this.print(tab, output);
    }

    public int record(FunctionalExpression expression) {
        return this.functionalExpressionsCount++;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public void resolve() {
        sourceType = this.binding;
        if (sourceType == null) {
            this.ignoreFurtherInvestigation = true;
            return;
        }
        try {
            block68: {
                block65: {
                    block67: {
                        block66: {
                            if (CharOperation.equals(this.name, TypeConstants.VAR)) {
                                if (this.scope.compilerOptions().sourceLevel < 0x360000L) {
                                    this.scope.problemReporter().varIsReservedTypeNameInFuture(this);
                                } else {
                                    this.scope.problemReporter().varIsReservedTypeName(this);
                                }
                            }
                            this.scope.problemReporter().validateRestrictedKeywords(this.name, this);
                            annotationTagBits = sourceType.getAnnotationTagBits();
                            if ((annotationTagBits & 0x400000000000L) == 0L && (sourceType.modifiers & 0x100000) != 0 && this.scope.compilerOptions().sourceLevel >= 0x310000L) {
                                this.scope.problemReporter().missingDeprecatedAnnotationForType(this);
                            }
                            if ((annotationTagBits & 0x800000000000000L) != 0L && !this.binding.isFunctionalInterface(this.scope)) {
                                this.scope.problemReporter().notAFunctionalInterface(this);
                            }
                            if ((this.bits & 8) != 0) {
                                this.scope.problemReporter().undocumentedEmptyBlock(this.bodyStart - 1, this.bodyEnd);
                            }
                            v0 = needSerialVersion = this.scope.compilerOptions().getSeverity(0x20000008) != 256 && sourceType.isClass() != false && sourceType.isRecord() == false && sourceType.findSuperTypeOriginatingFrom(56, false) == null && sourceType.findSuperTypeOriginatingFrom(37, false) != null;
                            if (!needSerialVersion) break block65;
                            compilationUnitScope = this.scope.compilationUnitScope();
                            methodBinding = sourceType.getExactMethod(TypeConstants.WRITEREPLACE, Binding.NO_TYPES, compilationUnitScope);
                            if (methodBinding == null || !methodBinding.isValidBinding() || methodBinding.returnType.id != 1) ** GOTO lbl-1000
                            throwsExceptions = methodBinding.thrownExceptions;
                            if (methodBinding.thrownExceptions.length == 1 && throwsExceptions[0].id == 57) {
                                v1 = false;
                            } else lbl-1000:
                            // 2 sources

                            {
                                v1 = needSerialVersion = true;
                            }
                            if (!needSerialVersion) break block65;
                            hasWriteObjectMethod = false;
                            hasReadObjectMethod = false;
                            argumentTypeBinding = this.scope.getType(TypeConstants.JAVA_IO_OBJECTOUTPUTSTREAM, 3);
                            if (!argumentTypeBinding.isValidBinding()) break block66;
                            methodBinding = sourceType.getExactMethod(TypeConstants.WRITEOBJECT, new TypeBinding[]{argumentTypeBinding}, compilationUnitScope);
                            if (methodBinding == null || !methodBinding.isValidBinding() || methodBinding.modifiers != 2 || methodBinding.returnType != TypeBinding.VOID) ** GOTO lbl-1000
                            throwsExceptions = methodBinding.thrownExceptions;
                            if (methodBinding.thrownExceptions.length == 1 && throwsExceptions[0].id == 58) {
                                v2 = true;
                            } else lbl-1000:
                            // 2 sources

                            {
                                v2 = hasWriteObjectMethod = false;
                            }
                        }
                        if (!(argumentTypeBinding = this.scope.getType(TypeConstants.JAVA_IO_OBJECTINPUTSTREAM, 3)).isValidBinding()) break block67;
                        methodBinding = sourceType.getExactMethod(TypeConstants.READOBJECT, new TypeBinding[]{argumentTypeBinding}, compilationUnitScope);
                        if (methodBinding == null || !methodBinding.isValidBinding() || methodBinding.modifiers != 2 || methodBinding.returnType != TypeBinding.VOID) ** GOTO lbl-1000
                        throwsExceptions = methodBinding.thrownExceptions;
                        if (methodBinding.thrownExceptions.length == 1 && throwsExceptions[0].id == 58) {
                            v3 = true;
                        } else lbl-1000:
                        // 2 sources

                        {
                            v3 = false;
                        }
                        hasReadObjectMethod = v3;
                    }
                    v4 = needSerialVersion = hasWriteObjectMethod == false || hasReadObjectMethod == false;
                }
                if (sourceType.findSuperTypeOriginatingFrom(21, true) != null) {
                    current /* !! */  = sourceType;
                    do {
                        if (current /* !! */ .isGenericType()) {
                            this.scope.problemReporter().genericTypeCannotExtendThrowable(this);
                            break;
                        }
                        if (current /* !! */ .isStatic()) break;
                        if (!current /* !! */ .isLocalType()) continue;
                        nestedType = (NestedTypeBinding)current /* !! */ .erasure();
                        if (nestedType.scope.methodScope().isStatic) break;
                    } while ((current /* !! */  = current /* !! */ .enclosingType()) != null);
                }
                localMaxFieldCount = 0;
                lastVisibleFieldID = -1;
                hasEnumConstants = false;
                enumConstantsWithoutBody = null;
                if (this.memberTypes != null) {
                    i = 0;
                    count = this.memberTypes.length;
                    while (i < count) {
                        this.memberTypes[i].resolve(this.scope);
                        ++i;
                    }
                }
                if (this.recordComponents != null) {
                    var12_27 = this.recordComponents;
                    var11_33 = this.recordComponents.length;
                    count = 0;
                    while (count < var11_33) {
                        rc = var12_27[count];
                        rc.resolve(this.initializerScope);
                        ++count;
                    }
                }
                if (this.fields == null) break block68;
                i = 0;
                count = this.fields.length;
                while (i < count) {
                    field = this.fields[i];
                    switch (field.getKind()) {
                        case 3: {
                            hasEnumConstants = true;
                            if (!(field.initialization instanceof QualifiedAllocationExpression)) {
                                if (enumConstantsWithoutBody == null) {
                                    enumConstantsWithoutBody = new FieldDeclaration[count];
                                }
                                enumConstantsWithoutBody[i] = field;
                            }
                        }
                        case 1: {
                            fieldBinding = field.binding;
                            if (fieldBinding == null) {
                                if (field.initialization != null) {
                                    field.initialization.resolve(field.isStatic() != false ? this.staticInitializerScope : this.initializerScope);
                                }
                                this.ignoreFurtherInvestigation = true;
                                break;
                            }
                            if (needSerialVersion && (fieldBinding.modifiers & 24) == 24 && CharOperation.equals(TypeConstants.SERIALVERSIONUID, fieldBinding.name) && TypeBinding.equalsEquals(TypeBinding.LONG, fieldBinding.type)) {
                                needSerialVersion = false;
                            }
                            ++localMaxFieldCount;
                            lastVisibleFieldID = field.binding.id;
                            ** GOTO lbl112
                        }
                        case 2: {
                            ((Initializer)field).lastVisibleFieldID = lastVisibleFieldID + 1;
                        }
lbl112:
                        // 3 sources

                        default: {
                            field.resolve(field.isStatic() != false ? this.staticInitializerScope : this.initializerScope);
                        }
                    }
                    ++i;
                }
            }
            if (this.maxFieldCount < localMaxFieldCount) {
                this.maxFieldCount = localMaxFieldCount;
            }
            if (needSerialVersion) {
                javaxRmiCorbaStub = this.scope.getType(TypeConstants.JAVAX_RMI_CORBA_STUB, 4);
                if (javaxRmiCorbaStub.isValidBinding()) {
                    superclassBinding = this.binding.superclass;
                    while (superclassBinding != null) {
                        if (TypeBinding.equalsEquals(superclassBinding, javaxRmiCorbaStub)) {
                            needSerialVersion = false;
                            break;
                        }
                        superclassBinding = superclassBinding.superclass();
                    }
                }
                if (needSerialVersion) {
                    this.scope.problemReporter().missingSerialVersion(this);
                }
            }
            switch (TypeDeclaration.kind(this.modifiers)) {
                case 4: {
                    if (this.superclass != null) {
                        this.scope.problemReporter().annotationTypeDeclarationCannotHaveSuperclass(this);
                    }
                    if (this.superInterfaces == null) break;
                    this.scope.problemReporter().annotationTypeDeclarationCannotHaveSuperinterfaces(this);
                    break;
                }
                case 3: {
                    if (!this.binding.isAbstract()) break;
                    if (!hasEnumConstants) {
                        i = 0;
                        count = this.methods.length;
                        while (i < count) {
                            methodDeclaration = this.methods[i];
                            if (methodDeclaration.isAbstract() && methodDeclaration.binding != null) {
                                this.scope.problemReporter().enumAbstractMethodMustBeImplemented(methodDeclaration);
                            }
                            ++i;
                        }
                    } else {
                        if (enumConstantsWithoutBody == null) break;
                        i = 0;
                        count = this.methods.length;
                        while (i < count) {
                            methodDeclaration = this.methods[i];
                            if (methodDeclaration.isAbstract() && methodDeclaration.binding != null) {
                                f = 0;
                                l = enumConstantsWithoutBody.length;
                                while (f < l) {
                                    if (enumConstantsWithoutBody[f] != null) {
                                        this.scope.problemReporter().enumConstantMustImplementAbstractMethod(methodDeclaration, enumConstantsWithoutBody[f]);
                                    }
                                    ++f;
                                }
                            }
                            ++i;
                        }
                    }
                    break;
                }
            }
            missingAbstractMethodslength = this.missingAbstractMethods == null ? 0 : this.missingAbstractMethods.length;
            v5 = methodsLength = this.methods == null ? 0 : this.methods.length;
            if (methodsLength + missingAbstractMethodslength > 65535) {
                this.scope.problemReporter().tooManyMethods(this);
            }
            if (this.methods != null) {
                i = 0;
                count = this.methods.length;
                while (i < count) {
                    this.methods[i].resolve(this.scope);
                    ++i;
                }
            }
            if (this.javadoc != null) {
                if (this.scope != null && this.name != TypeConstants.PACKAGE_INFO_NAME) {
                    this.javadoc.resolve(this.scope);
                }
            } else if (!sourceType.isLocalType()) {
                visibility = sourceType.modifiers & 7;
                reporter = this.scope.problemReporter();
                severity = reporter.computeSeverity(-1610612250);
                if (severity != 256) {
                    if (this.enclosingType != null) {
                        visibility = Util.computeOuterMostVisibility(this.enclosingType, visibility);
                    }
                    javadocModifiers = this.binding.modifiers & -8 | visibility;
                    reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
                }
            }
            this.updateNestHost();
            fieldsDecls = this.fields;
            if (fieldsDecls != null) {
                var15_41 = fieldsDecls;
                var14_40 = fieldsDecls.length;
                severity = 0;
                while (severity < var14_40) {
                    fieldDeclaration = var15_41[severity];
                    fieldDeclaration.resolveJavadoc(this.initializerScope);
                    ++severity;
                }
            }
            if ((methodDecls = this.methods) != null) {
                var16_43 = methodDecls;
                var15_42 = methodDecls.length;
                var14_40 = 0;
                while (var14_40 < var15_42) {
                    methodDeclaration = var16_43[var14_40];
                    methodDeclaration.resolveJavadoc();
                    ++var14_40;
                }
            }
        }
        catch (AbortType v6) {
            this.ignoreFurtherInvestigation = true;
            return;
        }
    }

    @Override
    public void resolve(BlockScope blockScope) {
        if ((this.bits & 0x200) == 0) {
            TypeBinding existing = blockScope.getType(this.name);
            if (existing instanceof ReferenceBinding && existing != this.binding && existing.isValidBinding()) {
                ReferenceBinding existingType = (ReferenceBinding)existing;
                if (existingType instanceof TypeVariableBinding) {
                    blockScope.problemReporter().typeHiding(this, (TypeVariableBinding)existingType);
                    Scope outerScope = blockScope.parent;
                    while (outerScope != null) {
                        TypeBinding existing2 = outerScope.getType(this.name);
                        if (existing2 instanceof TypeVariableBinding && existing2.isValidBinding()) {
                            TypeVariableBinding tvb = (TypeVariableBinding)existingType;
                            Binding declaringElement = tvb.declaringElement;
                            if (declaringElement instanceof ReferenceBinding && CharOperation.equals(((ReferenceBinding)declaringElement).sourceName(), this.name)) {
                                blockScope.problemReporter().typeCollidesWithEnclosingType(this);
                                break;
                            }
                        } else {
                            if (existing2 instanceof ReferenceBinding && existing2.isValidBinding() && outerScope.isDefinedInType((ReferenceBinding)existing2)) {
                                blockScope.problemReporter().typeCollidesWithEnclosingType(this);
                                break;
                            }
                            if (existing2 == null) break;
                        }
                        outerScope = outerScope.parent;
                    }
                } else if (existingType instanceof LocalTypeBinding && ((LocalTypeBinding)existingType).scope.methodScope() == blockScope.methodScope()) {
                    blockScope.problemReporter().duplicateNestedType(this);
                } else if (existingType instanceof LocalTypeBinding && blockScope.isLambdaSubscope() && blockScope.enclosingLambdaScope().enclosingMethodScope() == ((LocalTypeBinding)existingType).scope.methodScope()) {
                    blockScope.problemReporter().duplicateNestedType(this);
                } else if (blockScope.isDefinedInType(existingType)) {
                    blockScope.problemReporter().typeCollidesWithEnclosingType(this);
                } else if (blockScope.isDefinedInSameUnit(existingType)) {
                    blockScope.problemReporter().typeHiding(this, existingType);
                }
            }
            blockScope.addLocalType(this);
        }
        if (this.binding != null) {
            blockScope.referenceCompilationUnit().record((LocalTypeBinding)this.binding);
            this.resolve();
            this.updateMaxFieldCount();
        }
    }

    public void resolve(ClassScope upperScope) {
        if (this.binding != null && this.binding instanceof LocalTypeBinding) {
            upperScope.referenceCompilationUnit().record((LocalTypeBinding)this.binding);
        }
        this.resolve();
        this.updateMaxFieldCount();
    }

    public void resolve(CompilationUnitScope upperScope) {
        this.resolve();
        this.updateMaxFieldCount();
    }

    @Override
    public void tagAsHavingErrors() {
        this.ignoreFurtherInvestigation = true;
    }

    @Override
    public void tagAsHavingIgnoredMandatoryErrors(int problemId) {
    }

    public void traverse(ASTVisitor visitor, CompilationUnitScope unitScope) {
        try {
            if (visitor.visit(this, unitScope)) {
                int length;
                int i;
                if (this.javadoc != null) {
                    this.javadoc.traverse(visitor, this.scope);
                }
                if (this.annotations != null) {
                    int annotationsLength = this.annotations.length;
                    i = 0;
                    while (i < annotationsLength) {
                        this.annotations[i].traverse(visitor, this.staticInitializerScope);
                        ++i;
                    }
                }
                if (this.superclass != null) {
                    this.superclass.traverse(visitor, this.scope);
                }
                if (this.superInterfaces != null) {
                    length = this.superInterfaces.length;
                    i = 0;
                    while (i < length) {
                        this.superInterfaces[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.permittedTypes != null) {
                    length = this.permittedTypes.length;
                    i = 0;
                    while (i < length) {
                        this.permittedTypes[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.typeParameters != null) {
                    length = this.typeParameters.length;
                    i = 0;
                    while (i < length) {
                        this.typeParameters[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.recordComponents != null) {
                    length = this.recordComponents.length;
                    i = 0;
                    while (i < length) {
                        this.recordComponents[i].traverse(visitor, this.initializerScope);
                        ++i;
                    }
                }
                if (this.memberTypes != null) {
                    length = this.memberTypes.length;
                    i = 0;
                    while (i < length) {
                        this.memberTypes[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.fields != null) {
                    length = this.fields.length;
                    i = 0;
                    while (i < length) {
                        FieldDeclaration field = this.fields[i];
                        if (field.isStatic()) {
                            field.traverse(visitor, this.staticInitializerScope);
                        } else {
                            field.traverse(visitor, this.initializerScope);
                        }
                        ++i;
                    }
                }
                if (this.methods != null) {
                    length = this.methods.length;
                    i = 0;
                    while (i < length) {
                        this.methods[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
            }
            visitor.endVisit(this, unitScope);
        }
        catch (AbortType abortType) {}
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        try {
            if (visitor.visit(this, blockScope)) {
                int length;
                int i;
                if (this.javadoc != null) {
                    this.javadoc.traverse(visitor, this.scope);
                }
                if (this.annotations != null) {
                    int annotationsLength = this.annotations.length;
                    i = 0;
                    while (i < annotationsLength) {
                        this.annotations[i].traverse(visitor, this.staticInitializerScope);
                        ++i;
                    }
                }
                if (this.superclass != null) {
                    this.superclass.traverse(visitor, this.scope);
                }
                if (this.superInterfaces != null) {
                    length = this.superInterfaces.length;
                    i = 0;
                    while (i < length) {
                        this.superInterfaces[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.permittedTypes != null) {
                    length = this.permittedTypes.length;
                    i = 0;
                    while (i < length) {
                        this.permittedTypes[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.typeParameters != null) {
                    length = this.typeParameters.length;
                    i = 0;
                    while (i < length) {
                        this.typeParameters[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.recordComponents != null) {
                    length = this.recordComponents.length;
                    i = 0;
                    while (i < length) {
                        this.recordComponents[i].traverse(visitor, this.initializerScope);
                        ++i;
                    }
                }
                if (this.memberTypes != null) {
                    length = this.memberTypes.length;
                    i = 0;
                    while (i < length) {
                        this.memberTypes[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.fields != null) {
                    length = this.fields.length;
                    i = 0;
                    while (i < length) {
                        FieldDeclaration field = this.fields[i];
                        if (!field.isStatic() || field.isFinal()) {
                            field.traverse(visitor, this.initializerScope);
                        }
                        ++i;
                    }
                }
                if (this.methods != null) {
                    length = this.methods.length;
                    i = 0;
                    while (i < length) {
                        this.methods[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
            }
            visitor.endVisit(this, blockScope);
        }
        catch (AbortType abortType) {}
    }

    public void traverse(ASTVisitor visitor, ClassScope classScope) {
        try {
            if (visitor.visit(this, classScope)) {
                int length;
                int i;
                if (this.javadoc != null) {
                    this.javadoc.traverse(visitor, this.scope);
                }
                if (this.annotations != null) {
                    int annotationsLength = this.annotations.length;
                    i = 0;
                    while (i < annotationsLength) {
                        this.annotations[i].traverse(visitor, this.staticInitializerScope);
                        ++i;
                    }
                }
                if (this.superclass != null) {
                    this.superclass.traverse(visitor, this.scope);
                }
                if (this.superInterfaces != null) {
                    length = this.superInterfaces.length;
                    i = 0;
                    while (i < length) {
                        this.superInterfaces[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.permittedTypes != null) {
                    length = this.permittedTypes.length;
                    i = 0;
                    while (i < length) {
                        this.permittedTypes[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.typeParameters != null) {
                    length = this.typeParameters.length;
                    i = 0;
                    while (i < length) {
                        this.typeParameters[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.recordComponents != null) {
                    length = this.recordComponents.length;
                    i = 0;
                    while (i < length) {
                        this.recordComponents[i].traverse(visitor, this.initializerScope);
                        ++i;
                    }
                }
                if (this.memberTypes != null) {
                    length = this.memberTypes.length;
                    i = 0;
                    while (i < length) {
                        this.memberTypes[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
                if (this.fields != null) {
                    length = this.fields.length;
                    i = 0;
                    while (i < length) {
                        FieldDeclaration field = this.fields[i];
                        if (field.isStatic()) {
                            field.traverse(visitor, this.staticInitializerScope);
                        } else {
                            field.traverse(visitor, this.initializerScope);
                        }
                        ++i;
                    }
                }
                if (this.methods != null) {
                    length = this.methods.length;
                    i = 0;
                    while (i < length) {
                        this.methods[i].traverse(visitor, this.scope);
                        ++i;
                    }
                }
            }
            visitor.endVisit(this, classScope);
        }
        catch (AbortType abortType) {}
    }

    void updateMaxFieldCount() {
        if (this.binding == null) {
            return;
        }
        TypeDeclaration outerMostType = this.scope.outerMostClassScope().referenceType();
        if (this.maxFieldCount > outerMostType.maxFieldCount) {
            outerMostType.maxFieldCount = this.maxFieldCount;
        } else {
            this.maxFieldCount = outerMostType.maxFieldCount;
        }
    }

    private SourceTypeBinding findNestHost() {
        ClassScope classScope = this.scope.enclosingTopMostClassScope();
        return classScope != null ? classScope.referenceContext.binding : null;
    }

    void updateNestHost() {
        if (this.binding == null) {
            return;
        }
        SourceTypeBinding nestHost = this.findNestHost();
        if (nestHost != null && !this.binding.equals(nestHost)) {
            this.binding.setNestHost(nestHost);
        }
    }

    public boolean isPackageInfo() {
        return CharOperation.equals(this.name, TypeConstants.PACKAGE_INFO_NAME);
    }

    public boolean isSecondary() {
        return (this.bits & 0x1000) != 0;
    }
}

