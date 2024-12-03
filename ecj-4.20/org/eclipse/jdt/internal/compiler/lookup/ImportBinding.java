/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.lookup.Binding;

public class ImportBinding
extends Binding {
    public char[][] compoundName;
    public boolean onDemand;
    public ImportReference reference;
    public Binding resolvedImport;

    public ImportBinding(char[][] compoundName, boolean isOnDemand, Binding binding, ImportReference reference) {
        this.compoundName = compoundName;
        this.onDemand = isOnDemand;
        this.resolvedImport = binding;
        this.reference = reference;
    }

    @Override
    public final int kind() {
        return 32;
    }

    public boolean isStatic() {
        return this.reference != null && this.reference.isStatic();
    }

    public char[] getSimpleName() {
        if (this.reference != null) {
            return this.reference.getSimpleName();
        }
        return this.compoundName[this.compoundName.length - 1];
    }

    @Override
    public char[] readableName() {
        if (this.onDemand) {
            return CharOperation.concat(CharOperation.concatWith(this.compoundName, '.'), ".*".toCharArray());
        }
        return CharOperation.concatWith(this.compoundName, '.');
    }

    public String toString() {
        return "import : " + new String(this.readableName());
    }
}

