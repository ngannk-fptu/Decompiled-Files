/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;

public class CaseLabel
extends BranchLabel {
    public int instructionPosition = -1;

    public CaseLabel(CodeStream codeStream) {
        super(codeStream);
    }

    @Override
    void branch() {
        if (this.position == -1) {
            this.addForwardReference(this.codeStream.position);
            this.codeStream.position += 4;
            this.codeStream.classFileOffset += 4;
        } else {
            this.codeStream.writeSignedWord(this.position - this.instructionPosition);
        }
    }

    @Override
    void branchWide() {
        this.branch();
    }

    @Override
    public boolean isCaseLabel() {
        return true;
    }

    @Override
    public boolean isStandardLabel() {
        return false;
    }

    @Override
    public void place() {
        this.position = (this.tagBits & 2) != 0 ? this.codeStream.getPosition() : this.codeStream.position;
        if (this.instructionPosition != -1) {
            int offset = this.position - this.instructionPosition;
            int[] forwardRefs = this.forwardReferences();
            int i = 0;
            int length = this.forwardReferenceCount();
            while (i < length) {
                this.codeStream.writeSignedWord(forwardRefs[i], offset);
                ++i;
            }
            this.codeStream.addLabel(this);
        }
    }

    void placeInstruction() {
        if (this.instructionPosition == -1) {
            this.instructionPosition = this.codeStream.position;
        }
    }
}

