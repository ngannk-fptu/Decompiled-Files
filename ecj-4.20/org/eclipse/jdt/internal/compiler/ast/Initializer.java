/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;

public class Initializer
extends FieldDeclaration {
    public Block block;
    public int lastVisibleFieldID;
    public int bodyStart;
    public int bodyEnd;
    private MethodBinding methodBinding;

    public Initializer(Block block, int modifiers) {
        this.block = block;
        this.modifiers = modifiers;
        if (block != null) {
            this.declarationSourceStart = this.sourceStart = block.sourceStart;
        }
    }

    @Override
    public FlowInfo analyseCode(MethodScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.block != null) {
            return this.block.analyseCode(currentScope, flowContext, flowInfo);
        }
        return flowInfo;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        int pc = codeStream.position;
        if (this.block != null) {
            this.block.generateCode(currentScope, codeStream);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public int getKind() {
        return 2;
    }

    @Override
    public boolean isStatic() {
        return (this.modifiers & 8) != 0;
    }

    public void parseStatements(Parser parser, TypeDeclaration typeDeclaration, CompilationUnitDeclaration unit) {
        parser.parse(this, typeDeclaration, unit);
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        if (this.modifiers != 0) {
            Initializer.printIndent(indent, output);
            Initializer.printModifiers(this.modifiers, output);
            if (this.annotations != null) {
                Initializer.printAnnotations(this.annotations, output);
                output.append(' ');
            }
            output.append("{\n");
            if (this.block != null) {
                this.block.printBody(indent, output);
            }
            Initializer.printIndent(indent, output).append('}');
            return output;
        }
        if (this.block != null) {
            this.block.printStatement(indent, output);
        } else {
            Initializer.printIndent(indent, output).append("{}");
        }
        return output;
    }

    @Override
    public void resolve(MethodScope scope) {
        FieldBinding previousField = scope.initializedField;
        int previousFieldID = scope.lastVisibleFieldID;
        try {
            scope.initializedField = null;
            scope.lastVisibleFieldID = this.lastVisibleFieldID;
            if (this.isStatic()) {
                SourceTypeBinding declaringType = scope.enclosingSourceType();
                if (scope.compilerOptions().sourceLevel < 0x3C0000L && declaringType.isNestedType() && !declaringType.isStatic()) {
                    scope.problemReporter().innerTypesCannotDeclareStaticInitializers(declaringType, this);
                }
            }
            if (this.block != null) {
                this.block.resolve(scope);
            }
        }
        finally {
            scope.initializedField = previousField;
            scope.lastVisibleFieldID = previousFieldID;
        }
    }

    public MethodBinding getMethodBinding() {
        if (this.methodBinding == null) {
            BlockScope scope = this.block.scope;
            this.methodBinding = this.isStatic() ? new MethodBinding(8, CharOperation.NO_CHAR, TypeBinding.VOID, Binding.NO_PARAMETERS, Binding.NO_EXCEPTIONS, scope.enclosingSourceType()) : new MethodBinding(0, CharOperation.NO_CHAR, TypeBinding.VOID, Binding.NO_PARAMETERS, Binding.NO_EXCEPTIONS, scope.enclosingSourceType());
        }
        return this.methodBinding;
    }

    @Override
    public void traverse(ASTVisitor visitor, MethodScope scope) {
        if (visitor.visit(this, scope) && this.block != null) {
            this.block.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}

