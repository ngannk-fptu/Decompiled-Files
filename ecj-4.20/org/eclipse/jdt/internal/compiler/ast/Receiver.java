/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class Receiver
extends Argument {
    public NameReference qualifyingName;

    public Receiver(char[] name, long posNom, TypeReference typeReference, NameReference qualifyingName, int modifiers) {
        super(name, posNom, typeReference, modifiers);
        this.qualifyingName = qualifyingName;
    }

    @Override
    public boolean isReceiver() {
        return true;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        Receiver.printIndent(indent, output);
        Receiver.printModifiers(this.modifiers, output);
        if (this.type == null) {
            output.append("<no type> ");
        } else {
            this.type.print(0, output).append(' ');
        }
        if (this.qualifyingName != null) {
            this.qualifyingName.print(indent, output);
            output.append('.');
        }
        return output.append(this.name);
    }
}

