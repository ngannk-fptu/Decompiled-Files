/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;

public class RecoveredModuleReference
extends RecoveredElement {
    public ModuleReference moduleReference;

    public RecoveredModuleReference(ModuleReference moduleReference, RecoveredElement parent, int bracketBalance) {
        super(parent, bracketBalance);
        this.moduleReference = moduleReference;
    }

    @Override
    public ASTNode parseTree() {
        return this.moduleReference;
    }

    @Override
    public int sourceEnd() {
        return this.moduleReference.sourceEnd;
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered ModuleReference: " + this.moduleReference.toString();
    }

    public ModuleReference updatedModuleReference() {
        return this.moduleReference;
    }

    @Override
    public void updateParseTree() {
        this.updatedModuleReference();
    }
}

