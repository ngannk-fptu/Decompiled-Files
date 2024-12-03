/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import groovyjarjarasm.asm.ClassVisitor;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.asm.BinaryExpressionHelper;
import org.codehaus.groovy.classgen.asm.BinaryExpressionMultiTypeDispatcher;
import org.codehaus.groovy.classgen.asm.CallSiteWriter;
import org.codehaus.groovy.classgen.asm.ClosureWriter;
import org.codehaus.groovy.classgen.asm.DelegatingController;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.classgen.asm.StatementWriter;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.UnaryExpressionHelper;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.classgen.asm.sc.StaticInvocationWriter;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesBinaryExpressionMultiTypeDispatcher;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesCallSiteWriter;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesClosureWriter;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesStatementWriter;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesTypeChooser;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesUnaryExpressionHelper;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.sc.StaticCompilationVisitor;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class StaticTypesWriterController
extends DelegatingController {
    protected boolean isInStaticallyCheckedMethod = false;
    private StaticTypesCallSiteWriter callSiteWriter;
    private StaticTypesStatementWriter statementWriter;
    private StaticTypesTypeChooser typeChooser;
    private StaticInvocationWriter invocationWriter;
    private BinaryExpressionMultiTypeDispatcher binaryExprHelper;
    private UnaryExpressionHelper unaryExpressionHelper;
    private ClosureWriter closureWriter;

    public StaticTypesWriterController(WriterController normalController) {
        super(normalController);
    }

    @Override
    public void init(AsmClassGenerator asmClassGenerator, GeneratorContext gcon, ClassVisitor cv, ClassNode cn) {
        super.init(asmClassGenerator, gcon, cv, cn);
        this.callSiteWriter = new StaticTypesCallSiteWriter(this);
        this.statementWriter = new StaticTypesStatementWriter(this);
        this.typeChooser = new StaticTypesTypeChooser();
        this.invocationWriter = new StaticInvocationWriter(this);
        this.binaryExprHelper = new StaticTypesBinaryExpressionMultiTypeDispatcher(this);
        this.closureWriter = new StaticTypesClosureWriter(this);
        this.unaryExpressionHelper = new StaticTypesUnaryExpressionHelper(this);
    }

    @Override
    public void setMethodNode(MethodNode mn) {
        this.updateStaticCompileFlag(mn);
        super.setMethodNode(mn);
    }

    private void updateStaticCompileFlag(MethodNode mn) {
        ClassNode classNode = this.getClassNode();
        AnnotatedNode node = mn;
        if (classNode.implementsInterface(ClassHelper.GENERATED_CLOSURE_Type)) {
            node = classNode.getOuterClass();
        }
        this.isInStaticallyCheckedMethod = mn != null && (StaticCompilationVisitor.isStaticallyCompiled(node) || classNode.implementsInterface(ClassHelper.GENERATED_CLOSURE_Type) && classNode.getNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE) != null);
    }

    @Override
    public void setConstructorNode(ConstructorNode cn) {
        this.updateStaticCompileFlag(cn);
        super.setConstructorNode(cn);
    }

    @Override
    public boolean isFastPath() {
        if (this.isInStaticallyCheckedMethod) {
            return true;
        }
        return super.isFastPath();
    }

    @Override
    public CallSiteWriter getCallSiteWriter() {
        MethodNode methodNode = this.getMethodNode();
        if (methodNode != null && methodNode.getNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION) == Boolean.TRUE) {
            return super.getCallSiteWriter();
        }
        if (this.isInStaticallyCheckedMethod) {
            return this.callSiteWriter;
        }
        return super.getCallSiteWriter();
    }

    public CallSiteWriter getRegularCallSiteWriter() {
        return super.getCallSiteWriter();
    }

    @Override
    public StatementWriter getStatementWriter() {
        if (this.isInStaticallyCheckedMethod) {
            return this.statementWriter;
        }
        return super.getStatementWriter();
    }

    @Override
    public TypeChooser getTypeChooser() {
        if (this.isInStaticallyCheckedMethod) {
            return this.typeChooser;
        }
        return super.getTypeChooser();
    }

    @Override
    public InvocationWriter getInvocationWriter() {
        if (this.isInStaticallyCheckedMethod) {
            return this.invocationWriter;
        }
        return super.getInvocationWriter();
    }

    public InvocationWriter getRegularInvocationWriter() {
        return super.getInvocationWriter();
    }

    @Override
    public BinaryExpressionHelper getBinaryExpressionHelper() {
        if (this.isInStaticallyCheckedMethod) {
            return this.binaryExprHelper;
        }
        return super.getBinaryExpressionHelper();
    }

    @Override
    public UnaryExpressionHelper getUnaryExpressionHelper() {
        if (this.isInStaticallyCheckedMethod) {
            return this.unaryExpressionHelper;
        }
        return super.getUnaryExpressionHelper();
    }

    @Override
    public ClosureWriter getClosureWriter() {
        if (this.isInStaticallyCheckedMethod) {
            return this.closureWriter;
        }
        return super.getClosureWriter();
    }
}

