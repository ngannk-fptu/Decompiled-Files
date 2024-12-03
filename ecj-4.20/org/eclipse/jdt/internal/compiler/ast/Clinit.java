/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;

public class Clinit
extends AbstractMethodDeclaration {
    private static int ENUM_CONSTANTS_THRESHOLD = 2000;
    private FieldBinding assertionSyntheticFieldBinding = null;
    private FieldBinding classLiteralSyntheticField = null;

    public Clinit(CompilationResult compilationResult) {
        super(compilationResult);
        this.modifiers = 0;
        this.selector = TypeConstants.CLINIT;
    }

    public void analyseCode(ClassScope classScope, InitializationFlowContext staticInitializerFlowContext, FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        try {
            ExceptionHandlingFlowContext clinitContext = new ExceptionHandlingFlowContext(staticInitializerFlowContext.parent, this, Binding.NO_EXCEPTIONS, staticInitializerFlowContext, this.scope, FlowInfo.DEAD_END);
            if ((flowInfo.tagBits & 1) == 0) {
                this.bits |= 0x40;
            }
            flowInfo = flowInfo.mergedWith(staticInitializerFlowContext.initsOnReturn);
            FieldBinding[] fields = this.scope.enclosingSourceType().fields();
            int i = 0;
            int count = fields.length;
            while (i < count) {
                FieldBinding field = fields[i];
                if (field.isStatic() && !flowInfo.isDefinitelyAssigned(field)) {
                    if (field.isFinal()) {
                        this.scope.problemReporter().uninitializedBlankFinalField(field, this.scope.referenceType().declarationOf(field.original()));
                    } else if (field.isNonNull()) {
                        this.scope.problemReporter().uninitializedNonNullField(field, this.scope.referenceType().declarationOf(field.original()));
                    }
                }
                ++i;
            }
            staticInitializerFlowContext.checkInitializerExceptions(this.scope, clinitContext, flowInfo);
        }
        catch (AbortMethod abortMethod) {
            this.ignoreFurtherInvestigation = true;
        }
    }

    @Override
    public void generateCode(ClassScope classScope, ClassFile classFile) {
        TypeDeclaration referenceContext;
        int clinitOffset = 0;
        if (this.ignoreFurtherInvestigation) {
            return;
        }
        CompilationResult unitResult = null;
        int problemCount = 0;
        if (classScope != null && (referenceContext = classScope.referenceContext) != null) {
            unitResult = referenceContext.compilationResult();
            problemCount = unitResult.problemCount;
        }
        boolean restart = false;
        do {
            try {
                clinitOffset = classFile.contentsOffset;
                this.generateCode(classScope, classFile, clinitOffset);
                restart = false;
            }
            catch (AbortMethod e) {
                if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
                    classFile.contentsOffset = clinitOffset;
                    --classFile.methodCount;
                    classFile.codeStream.resetInWideMode();
                    if (unitResult != null) {
                        unitResult.problemCount = problemCount;
                    }
                    restart = true;
                    continue;
                }
                if (e.compilationResult == CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE) {
                    classFile.contentsOffset = clinitOffset;
                    --classFile.methodCount;
                    classFile.codeStream.resetForCodeGenUnusedLocals();
                    if (unitResult != null) {
                        unitResult.problemCount = problemCount;
                    }
                    restart = true;
                    continue;
                }
                classFile.contentsOffset = clinitOffset;
                --classFile.methodCount;
                restart = false;
            }
        } while (restart);
    }

    private void generateCode(ClassScope classScope, ClassFile classFile, int clinitOffset) {
        int sourcePosition;
        TypeDeclaration declaringType;
        CodeStream codeStream;
        int codeAttributeOffset;
        int constantPoolIndex;
        int constantPoolOffset;
        ConstantPool constantPool;
        block36: {
            FieldDeclaration[] fieldDeclarations;
            boolean isJava9;
            MethodScope staticInitializerScope;
            block33: {
                int max;
                int i;
                int enumCount;
                int remainingFieldCount;
                block35: {
                    block34: {
                        constantPool = classFile.constantPool;
                        constantPoolOffset = constantPool.currentOffset;
                        constantPoolIndex = constantPool.currentIndex;
                        classFile.generateMethodInfoHeaderForClinit();
                        codeAttributeOffset = classFile.contentsOffset;
                        classFile.generateCodeAttributeHeader();
                        codeStream = classFile.codeStream;
                        this.resolve(classScope);
                        codeStream.reset(this, classFile);
                        declaringType = classScope.referenceContext;
                        staticInitializerScope = declaringType.staticInitializerScope;
                        staticInitializerScope.computeLocalVariablePositions(0, codeStream);
                        if (this.assertionSyntheticFieldBinding != null) {
                            codeStream.generateClassLiteralAccessForType(classScope, classScope.outerMostClassScope().enclosingSourceType(), this.classLiteralSyntheticField);
                            codeStream.invokeJavaLangClassDesiredAssertionStatus();
                            BranchLabel falseLabel = new BranchLabel(codeStream);
                            codeStream.ifne(falseLabel);
                            codeStream.iconst_1();
                            BranchLabel jumpLabel = new BranchLabel(codeStream);
                            codeStream.decrStackSize(1);
                            codeStream.goto_(jumpLabel);
                            falseLabel.place();
                            codeStream.iconst_0();
                            jumpLabel.place();
                            codeStream.fieldAccess((byte)-77, this.assertionSyntheticFieldBinding, null);
                        }
                        isJava9 = classScope.compilerOptions().complianceLevel >= 0x350000L;
                        fieldDeclarations = declaringType.fields;
                        sourcePosition = -1;
                        remainingFieldCount = 0;
                        if (TypeDeclaration.kind(declaringType.modifiers) != 3) break block33;
                        enumCount = declaringType.enumConstantsCounter;
                        if (isJava9 || enumCount <= ENUM_CONSTANTS_THRESHOLD) break block34;
                        int begin = -1;
                        int count = 0;
                        if (fieldDeclarations == null) break block35;
                        int max2 = fieldDeclarations.length;
                        int i2 = 0;
                        while (i2 < max2) {
                            FieldDeclaration fieldDecl = fieldDeclarations[i2];
                            if (fieldDecl.isStatic()) {
                                if (fieldDecl.getKind() == 3) {
                                    if (begin == -1) {
                                        begin = i2;
                                    }
                                    if (++count > ENUM_CONSTANTS_THRESHOLD) {
                                        SyntheticMethodBinding syntheticMethod = declaringType.binding.addSyntheticMethodForEnumInitialization(begin, i2);
                                        codeStream.invoke((byte)-72, syntheticMethod, null);
                                        begin = i2;
                                        count = 1;
                                    }
                                } else {
                                    ++remainingFieldCount;
                                }
                            }
                            ++i2;
                        }
                        if (count == 0) break block35;
                        SyntheticMethodBinding syntheticMethod = declaringType.binding.addSyntheticMethodForEnumInitialization(begin, max2);
                        codeStream.invoke((byte)-72, syntheticMethod, null);
                        break block35;
                    }
                    if (fieldDeclarations != null) {
                        i = 0;
                        max = fieldDeclarations.length;
                        while (i < max) {
                            FieldDeclaration fieldDecl = fieldDeclarations[i];
                            if (fieldDecl.isStatic()) {
                                if (fieldDecl.getKind() == 3) {
                                    fieldDecl.generateCode(staticInitializerScope, codeStream);
                                } else {
                                    ++remainingFieldCount;
                                }
                            }
                            ++i;
                        }
                    }
                }
                codeStream.generateInlinedValue(enumCount);
                codeStream.anewarray(declaringType.binding);
                if (enumCount > 0 && fieldDeclarations != null) {
                    i = 0;
                    max = fieldDeclarations.length;
                    while (i < max) {
                        FieldDeclaration fieldDecl = fieldDeclarations[i];
                        if (fieldDecl.getKind() == 3) {
                            codeStream.dup();
                            codeStream.generateInlinedValue(fieldDecl.binding.id);
                            codeStream.fieldAccess((byte)-78, fieldDecl.binding, null);
                            codeStream.aastore();
                        }
                        ++i;
                    }
                }
                codeStream.fieldAccess((byte)-77, declaringType.enumValuesSyntheticfield, null);
                if (remainingFieldCount != 0) {
                    i = 0;
                    max = fieldDeclarations.length;
                    while (i < max && remainingFieldCount >= 0) {
                        FieldDeclaration fieldDecl = fieldDeclarations[i];
                        switch (fieldDecl.getKind()) {
                            case 3: {
                                break;
                            }
                            case 2: {
                                if (!fieldDecl.isStatic()) break;
                                --remainingFieldCount;
                                sourcePosition = ((Initializer)fieldDecl).block.sourceEnd;
                                fieldDecl.generateCode(staticInitializerScope, codeStream);
                                break;
                            }
                            case 1: {
                                if (!fieldDecl.binding.isStatic()) break;
                                --remainingFieldCount;
                                sourcePosition = fieldDecl.declarationEnd;
                                fieldDecl.generateCode(staticInitializerScope, codeStream);
                            }
                        }
                        ++i;
                    }
                }
                break block36;
            }
            if (fieldDeclarations != null) {
                int i = 0;
                int max = fieldDeclarations.length;
                while (i < max) {
                    FieldDeclaration fieldDecl = fieldDeclarations[i];
                    switch (fieldDecl.getKind()) {
                        case 2: {
                            if (!fieldDecl.isStatic()) break;
                            sourcePosition = ((Initializer)fieldDecl).block.sourceEnd;
                            fieldDecl.generateCode(staticInitializerScope, codeStream);
                            break;
                        }
                        case 1: {
                            if (!fieldDecl.binding.isStatic()) break;
                            sourcePosition = fieldDecl.declarationEnd;
                            fieldDecl.generateCode(staticInitializerScope, codeStream);
                        }
                    }
                    ++i;
                }
            }
            if (isJava9) {
                declaringType.binding.generateSyntheticFinalFieldInitialization(codeStream);
            }
        }
        if (codeStream.position == 0) {
            classFile.contentsOffset = clinitOffset;
            --classFile.methodCount;
            constantPool.resetForClinit(constantPoolIndex, constantPoolOffset);
        } else {
            if ((this.bits & 0x40) != 0) {
                int before = codeStream.position;
                codeStream.return_();
                if (sourcePosition != -1) {
                    codeStream.recordPositionsFrom(before, sourcePosition);
                }
            }
            codeStream.recordPositionsFrom(0, declaringType.sourceStart);
            classFile.completeCodeAttributeForClinit(codeAttributeOffset, classScope);
        }
    }

    @Override
    public boolean isClinit() {
        return true;
    }

    @Override
    public boolean isInitializationMethod() {
        return true;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public void parseStatements(Parser parser, CompilationUnitDeclaration unit) {
    }

    @Override
    public StringBuffer print(int tab, StringBuffer output) {
        Clinit.printIndent(tab, output).append("<clinit>()");
        this.printBody(tab + 1, output);
        return output;
    }

    @Override
    public void resolve(ClassScope classScope) {
        this.scope = new MethodScope(classScope, classScope.referenceContext, true);
    }

    @Override
    public void traverse(ASTVisitor visitor, ClassScope classScope) {
        visitor.visit(this, classScope);
        visitor.endVisit(this, classScope);
    }

    public void setAssertionSupport(FieldBinding assertionSyntheticFieldBinding, boolean needClassLiteralField) {
        SourceTypeBinding sourceType;
        this.assertionSyntheticFieldBinding = assertionSyntheticFieldBinding;
        if (needClassLiteralField && !(sourceType = this.scope.outerMostClassScope().enclosingSourceType()).isInterface() && !sourceType.isBaseType()) {
            this.classLiteralSyntheticField = sourceType.addSyntheticFieldForClassLiteral(sourceType, this.scope);
        }
    }
}

