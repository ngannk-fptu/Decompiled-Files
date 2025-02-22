/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.PackageVisibilityStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredImport;
import org.eclipse.jdt.internal.compiler.parser.RecoveredModuleReference;
import org.eclipse.jdt.internal.compiler.parser.RecoveredModuleStatement;

public class RecoveredPackageVisibilityStatement
extends RecoveredModuleStatement {
    RecoveredImport pkgRef;
    RecoveredModuleReference[] targets;
    int targetCount = 0;

    public RecoveredPackageVisibilityStatement(PackageVisibilityStatement pvs, RecoveredElement parent, int bracketBalance) {
        super(pvs, parent, bracketBalance);
    }

    @Override
    public RecoveredElement add(ImportReference pkgRef1, int bracketBalance1) {
        this.pkgRef = new RecoveredImport(pkgRef1, this, bracketBalance1);
        return this;
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
        return this;
    }

    @Override
    public String toString(int tab) {
        return super.toString();
    }

    public PackageVisibilityStatement updatedPackageVisibilityStatement() {
        PackageVisibilityStatement pvs = (PackageVisibilityStatement)this.moduleStatement;
        if (this.pkgRef != null) {
            pvs.pkgRef = this.pkgRef.updatedImportReference();
        }
        if (this.targetCount > 0) {
            int existingCount = pvs.targets != null ? pvs.targets.length : 0;
            int actualCount = 0;
            ModuleReference[] moduleRef1 = new ModuleReference[existingCount + this.targetCount];
            if (existingCount > 0) {
                System.arraycopy(pvs.targets, 0, moduleRef1, 0, existingCount);
                actualCount = existingCount;
            }
            int i = 0;
            int l = this.targetCount;
            while (i < l) {
                moduleRef1[actualCount++] = this.targets[i].updatedModuleReference();
                ++i;
            }
            pvs.targets = moduleRef1;
        }
        return pvs;
    }

    @Override
    public void updateParseTree() {
        this.updatedPackageVisibilityStatement();
    }
}

