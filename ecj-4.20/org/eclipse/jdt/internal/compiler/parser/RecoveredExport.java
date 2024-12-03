/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ExportsStatement;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredModuleReference;

public class RecoveredExport
extends RecoveredElement {
    public ExportsStatement exportReference;
    RecoveredModuleReference[] targets;
    int targetCount = 0;

    public RecoveredExport(ExportsStatement exportReference, RecoveredElement parent, int bracketBalance) {
        super(parent, bracketBalance);
        this.exportReference = exportReference;
    }

    public RecoveredElement add(ModuleReference target, int bracketBalance1) {
        if (this.targets == null) {
            this.targets = new RecoveredModuleReference[5];
            this.targetCount = 0;
        } else if (this.targetCount == this.targets.length) {
            this.targets = new RecoveredModuleReference[2 * this.targetCount];
            System.arraycopy(this.targets, 0, this.targets, 0, this.targetCount);
        }
        RecoveredModuleReference element = new RecoveredModuleReference(target, this, bracketBalance1);
        this.targets[this.targetCount++] = element;
        if (target.sourceEnd == 0) {
            return element;
        }
        return this;
    }

    @Override
    public ASTNode parseTree() {
        return this.exportReference;
    }

    @Override
    public int sourceEnd() {
        return this.exportReference.declarationSourceEnd;
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered export: " + this.exportReference.toString();
    }

    public ExportsStatement updatedExportReference() {
        if (this.targetCount > 0) {
            int existingCount = this.exportReference.targets != null ? this.exportReference.targets.length : 0;
            int actualCount = 0;
            ModuleReference[] moduleRef1 = new ModuleReference[existingCount + this.targetCount];
            if (existingCount > 0) {
                System.arraycopy(this.exportReference.targets, 0, moduleRef1, 0, existingCount);
                actualCount = existingCount;
            }
            int i = 0;
            int l = this.targetCount;
            while (i < l) {
                moduleRef1[actualCount++] = this.targets[i].updatedModuleReference();
                ++i;
            }
            this.exportReference.targets = moduleRef1;
        }
        return this.exportReference;
    }

    @Override
    public void updateParseTree() {
        this.updatedExportReference();
    }

    @Override
    public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd) {
        if (this.exportReference.declarationSourceEnd == 0) {
            this.exportReference.declarationSourceEnd = bodyEnd;
            this.exportReference.declarationEnd = bodyEnd;
        }
    }
}

