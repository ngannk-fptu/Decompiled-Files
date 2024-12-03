/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.InterfaceHelperClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.asm.AssertionWriter;
import org.codehaus.groovy.classgen.asm.BinaryExpressionHelper;
import org.codehaus.groovy.classgen.asm.CallSiteWriter;
import org.codehaus.groovy.classgen.asm.ClosureWriter;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.StatementWriter;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.UnaryExpressionHelper;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.control.SourceUnit;

public class DelegatingController
extends WriterController {
    private WriterController delegationController;

    public DelegatingController(WriterController normalController) {
        this.delegationController = normalController;
    }

    @Override
    public void init(AsmClassGenerator asmClassGenerator, GeneratorContext gcon, ClassVisitor cv, ClassNode cn) {
        this.delegationController.init(asmClassGenerator, gcon, cv, cn);
    }

    @Override
    public void setMethodNode(MethodNode mn) {
        this.delegationController.setMethodNode(mn);
    }

    @Override
    public void setConstructorNode(ConstructorNode cn) {
        this.delegationController.setConstructorNode(cn);
    }

    @Override
    public boolean isFastPath() {
        return this.delegationController.isFastPath();
    }

    @Override
    public CallSiteWriter getCallSiteWriter() {
        return this.delegationController.getCallSiteWriter();
    }

    @Override
    public StatementWriter getStatementWriter() {
        return this.delegationController.getStatementWriter();
    }

    @Override
    public TypeChooser getTypeChooser() {
        return this.delegationController.getTypeChooser();
    }

    @Override
    public AsmClassGenerator getAcg() {
        return this.delegationController.getAcg();
    }

    @Override
    public AssertionWriter getAssertionWriter() {
        return this.delegationController.getAssertionWriter();
    }

    @Override
    public BinaryExpressionHelper getBinaryExpressionHelper() {
        return this.delegationController.getBinaryExpressionHelper();
    }

    @Override
    public UnaryExpressionHelper getUnaryExpressionHelper() {
        return this.delegationController.getUnaryExpressionHelper();
    }

    @Override
    public String getClassName() {
        return this.delegationController.getClassName();
    }

    @Override
    public ClassNode getClassNode() {
        return this.delegationController.getClassNode();
    }

    @Override
    public ClassVisitor getClassVisitor() {
        return this.delegationController.getClassVisitor();
    }

    @Override
    public ClosureWriter getClosureWriter() {
        return this.delegationController.getClosureWriter();
    }

    @Override
    public CompileStack getCompileStack() {
        return this.delegationController.getCompileStack();
    }

    @Override
    public ConstructorNode getConstructorNode() {
        return this.delegationController.getConstructorNode();
    }

    @Override
    public GeneratorContext getContext() {
        return this.delegationController.getContext();
    }

    @Override
    public ClassVisitor getCv() {
        return this.delegationController.getCv();
    }

    @Override
    public InterfaceHelperClassNode getInterfaceClassLoadingClass() {
        return this.delegationController.getInterfaceClassLoadingClass();
    }

    @Override
    public String getInternalBaseClassName() {
        return this.delegationController.getInternalBaseClassName();
    }

    @Override
    public String getInternalClassName() {
        return this.delegationController.getInternalClassName();
    }

    @Override
    public InvocationWriter getInvocationWriter() {
        return this.delegationController.getInvocationWriter();
    }

    @Override
    public MethodNode getMethodNode() {
        return this.delegationController.getMethodNode();
    }

    @Override
    public MethodVisitor getMethodVisitor() {
        return this.delegationController.getMethodVisitor();
    }

    @Override
    public OperandStack getOperandStack() {
        return this.delegationController.getOperandStack();
    }

    @Override
    public ClassNode getOutermostClass() {
        return this.delegationController.getOutermostClass();
    }

    @Override
    public ClassNode getReturnType() {
        return this.delegationController.getReturnType();
    }

    @Override
    public SourceUnit getSourceUnit() {
        return this.delegationController.getSourceUnit();
    }

    @Override
    public boolean isConstructor() {
        return this.delegationController.isConstructor();
    }

    @Override
    public boolean isInClosure() {
        return this.delegationController.isInClosure();
    }

    @Override
    public boolean isInClosureConstructor() {
        return this.delegationController.isInClosureConstructor();
    }

    @Override
    public boolean isNotClinit() {
        return this.delegationController.isNotClinit();
    }

    @Override
    public boolean isInScriptBody() {
        return this.delegationController.isInScriptBody();
    }

    @Override
    public boolean isNotExplicitThisInClosure(boolean implicitThis) {
        return this.delegationController.isNotExplicitThisInClosure(implicitThis);
    }

    @Override
    public boolean isStaticConstructor() {
        return this.delegationController.isStaticConstructor();
    }

    @Override
    public boolean isStaticContext() {
        return this.delegationController.isStaticContext();
    }

    @Override
    public boolean isStaticMethod() {
        return this.delegationController.isStaticMethod();
    }

    @Override
    public void setInterfaceClassLoadingClass(InterfaceHelperClassNode ihc) {
        this.delegationController.setInterfaceClassLoadingClass(ihc);
    }

    @Override
    public void setMethodVisitor(MethodVisitor methodVisitor) {
        this.delegationController.setMethodVisitor(methodVisitor);
    }

    @Override
    public boolean shouldOptimizeForInt() {
        return this.delegationController.shouldOptimizeForInt();
    }

    @Override
    public void switchToFastPath() {
        this.delegationController.switchToFastPath();
    }

    @Override
    public void switchToSlowPath() {
        this.delegationController.switchToSlowPath();
    }

    @Override
    public int getBytecodeVersion() {
        return this.delegationController.getBytecodeVersion();
    }

    @Override
    public void setLineNumber(int n) {
        this.delegationController.setLineNumber(n);
    }

    @Override
    public int getLineNumber() {
        return this.delegationController.getLineNumber();
    }

    @Override
    public void resetLineNumber() {
        this.delegationController.resetLineNumber();
    }
}

