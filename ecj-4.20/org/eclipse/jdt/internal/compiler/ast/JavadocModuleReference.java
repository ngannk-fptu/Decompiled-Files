/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.IJavadocTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class JavadocModuleReference
extends Expression
implements IJavadocTypeReference {
    public int tagSourceStart;
    public int tagSourceEnd;
    public TypeReference typeReference;
    public ModuleReference moduleReference;

    public JavadocModuleReference(char[][] sources, long[] pos, int tagStart, int tagEnd) {
        this.moduleReference = new ModuleReference(sources, pos);
        this.tagSourceStart = tagStart;
        this.tagSourceEnd = tagEnd;
        this.sourceStart = this.moduleReference.sourceStart;
        this.sourceEnd = this.moduleReference.sourceEnd;
        this.bits |= 0x8000;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }

    @Override
    public int getTagSourceStart() {
        return this.tagSourceStart;
    }

    @Override
    public int getTagSourceEnd() {
        return this.tagSourceEnd;
    }

    public TypeReference getTypeReference() {
        return this.typeReference;
    }

    public void setTypeReference(TypeReference typeReference) {
        this.typeReference = typeReference;
        if (this.typeReference != null) {
            this.sourceEnd = this.typeReference.sourceEnd;
        }
    }

    public ModuleReference getModuleReference() {
        return this.moduleReference;
    }

    public void setModuleReference(ModuleReference moduleReference) {
        this.moduleReference = moduleReference;
        this.sourceStart = this.moduleReference.sourceStart;
        this.sourceStart = this.moduleReference.sourceEnd;
    }

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        if (this.moduleReference != null) {
            output.append(this.moduleReference.moduleName);
        }
        output.append('/');
        if (this.typeReference != null) {
            this.typeReference.printExpression(indent, output);
        }
        return output;
    }

    public ModuleBinding resolve(Scope scope) {
        return this.moduleReference.resolve(scope);
    }

    private ModuleBinding resolveModule(BlockScope scope) {
        return this.moduleReference.resolve(scope);
    }

    private ModuleBinding resolveModule(ClassScope scope) {
        return this.moduleReference.resolve(scope);
    }

    @Override
    public TypeBinding resolveType(BlockScope blockScope) {
        this.resolveModule(blockScope);
        if (this.moduleReference.binding != null && this.typeReference != null) {
            return this.typeReference.resolveType(blockScope);
        }
        return null;
    }

    @Override
    public TypeBinding resolveType(ClassScope classScope) {
        this.resolveModule(classScope);
        assert (this.moduleReference.binding != null);
        if (this.typeReference != null) {
            return this.typeReference.resolveType(classScope, -1);
        }
        return null;
    }
}

