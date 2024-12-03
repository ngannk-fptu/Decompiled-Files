/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;

public class RecoveredTypeReference
extends RecoveredElement {
    public TypeReference typeReference;

    public RecoveredTypeReference(TypeReference typeReference, RecoveredElement parent, int bracketBalance) {
        super(parent, bracketBalance);
        this.typeReference = typeReference;
    }

    @Override
    public ASTNode parseTree() {
        return this.typeReference;
    }

    public TypeReference updateTypeReference() {
        return this.typeReference;
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered typereference: " + this.typeReference.toString();
    }

    public TypeReference updatedImportReference() {
        return this.typeReference;
    }

    @Override
    public void updateParseTree() {
        this.updatedImportReference();
    }
}

