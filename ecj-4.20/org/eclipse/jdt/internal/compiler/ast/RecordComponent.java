/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.List;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class RecordComponent
extends AbstractVariableDeclaration {
    public RecordComponentBinding binding;

    public RecordComponent(char[] name, int sourceStart, int sourceEnd) {
        this.name = name;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.declarationEnd = sourceEnd;
    }

    public RecordComponent(char[] name, long posNom, TypeReference tr, int modifiers) {
        this(name, (int)(posNom >>> 32), (int)posNom);
        this.declarationSourceEnd = (int)posNom;
        this.modifiers = modifiers;
        this.type = tr;
        if (tr != null) {
            this.bits |= tr.bits & 0x100000;
        }
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        return flowInfo;
    }

    public void checkModifiers() {
        if ((this.modifiers & 0xFFFF & 0xFFFFFFEF) != 0) {
            this.modifiers = this.modifiers & 0xFFBFFFFF | 0x800000;
        }
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceStart);
    }

    @Override
    public int getKind() {
        return 7;
    }

    public void getAllAnnotationContexts(int targetType, List<AnnotationContext> allAnnotationContexts) {
        TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this, targetType, allAnnotationContexts);
        this.traverse(collector, null);
    }

    public boolean isVarArgs() {
        return this.type != null && (this.type.bits & 0x4000) != 0;
    }

    @Override
    public void resolve(BlockScope scope) {
        RecordComponent.resolveAnnotations(scope, this.annotations, this.binding);
        if (this.annotations != null) {
            int i = 0;
            int max = this.annotations.length;
            while (i < max) {
                TypeBinding resolvedAnnotationType = this.annotations[i].resolvedType;
                if (resolvedAnnotationType != null && (resolvedAnnotationType.getAnnotationTagBits() & 0x20000000000000L) != 0L) {
                    this.bits |= 0x100000;
                    break;
                }
                ++i;
            }
        }
    }

    void validateNullAnnotations(BlockScope scope) {
        if (!scope.validateNullAnnotation(this.binding.tagBits, this.type, this.annotations)) {
            this.binding.tagBits &= 0xFE7FFFFFFFFFFFFFL;
        }
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        RecordComponent.printIndent(indent, output);
        RecordComponent.printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            RecordComponent.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        if (this.type == null) {
            output.append("<no type> ");
        } else {
            this.type.print(0, output).append(' ');
        }
        return output.append(this.name);
    }

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        return this.print(indent, output).append(';');
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                int annotationsLength = this.annotations.length;
                int i = 0;
                while (i < annotationsLength) {
                    this.annotations[i].traverse(visitor, scope);
                    ++i;
                }
            }
            this.type.traverse(visitor, scope);
            if (this.initialization != null) {
                this.initialization.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}

