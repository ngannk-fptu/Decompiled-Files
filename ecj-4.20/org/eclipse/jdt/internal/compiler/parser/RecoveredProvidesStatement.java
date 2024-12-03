/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ProvidesStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredModuleStatement;

public class RecoveredProvidesStatement
extends RecoveredModuleStatement {
    SingleTypeReference impl;

    public RecoveredProvidesStatement(ProvidesStatement providesStatement, RecoveredElement parent, int bracketBalance) {
        super(providesStatement, parent, bracketBalance);
    }

    public RecoveredElement add(SingleTypeReference impl1, int bracketBalance1) {
        this.impl = impl1;
        return this;
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered Provides: " + super.toString();
    }

    public ProvidesStatement updatedProvidesStatement() {
        ProvidesStatement providesStatement = (ProvidesStatement)this.moduleStatement;
        if (providesStatement.implementations == null) {
            TypeReference[] typeReferenceArray;
            if (this.impl != null) {
                TypeReference[] typeReferenceArray2 = new TypeReference[1];
                typeReferenceArray = typeReferenceArray2;
                typeReferenceArray2[0] = this.impl;
            } else {
                typeReferenceArray = new TypeReference[]{};
            }
            providesStatement.implementations = typeReferenceArray;
        }
        return providesStatement;
    }

    @Override
    public void updateParseTree() {
        this.updatedProvidesStatement();
    }
}

