/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.Label;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

public class BranchLabel
extends Label {
    private int[] forwardReferences = new int[10];
    private int forwardReferenceCount = 0;
    BranchLabel delegate;
    public int tagBits;
    public static final int WIDE = 1;
    public static final int USED = 2;

    public BranchLabel() {
    }

    public BranchLabel(CodeStream codeStream) {
        super(codeStream);
    }

    void addForwardReference(int pos) {
        if (this.delegate != null) {
            this.delegate.addForwardReference(pos);
            return;
        }
        int count = this.forwardReferenceCount;
        if (count >= 1) {
            int previousValue = this.forwardReferences[count - 1];
            if (previousValue < pos) {
                int length = this.forwardReferences.length;
                if (count >= length) {
                    this.forwardReferences = new int[2 * length];
                    System.arraycopy(this.forwardReferences, 0, this.forwardReferences, 0, length);
                }
                this.forwardReferences[this.forwardReferenceCount++] = pos;
            } else if (previousValue > pos) {
                int[] refs = this.forwardReferences;
                int i = 0;
                int max = this.forwardReferenceCount;
                while (i < max) {
                    if (refs[i] == pos) {
                        return;
                    }
                    ++i;
                }
                int length = refs.length;
                if (count >= length) {
                    this.forwardReferences = new int[2 * length];
                    System.arraycopy(refs, 0, this.forwardReferences, 0, length);
                }
                this.forwardReferences[this.forwardReferenceCount++] = pos;
                Arrays.sort(this.forwardReferences, 0, this.forwardReferenceCount);
            }
        } else {
            int length = this.forwardReferences.length;
            if (count >= length) {
                this.forwardReferences = new int[2 * length];
                System.arraycopy(this.forwardReferences, 0, this.forwardReferences, 0, length);
            }
            this.forwardReferences[this.forwardReferenceCount++] = pos;
        }
    }

    public void becomeDelegateFor(BranchLabel otherLabel) {
        otherLabel.delegate = this;
        int otherCount = otherLabel.forwardReferenceCount;
        if (otherCount == 0) {
            return;
        }
        int[] mergedForwardReferences = new int[this.forwardReferenceCount + otherCount];
        int indexInMerge = 0;
        int j = 0;
        int i = 0;
        int max = this.forwardReferenceCount;
        int max2 = otherLabel.forwardReferenceCount;
        while (i < max) {
            block6: {
                int value1 = this.forwardReferences[i];
                while (j < max2) {
                    int value2 = otherLabel.forwardReferences[j];
                    if (value1 < value2) {
                        mergedForwardReferences[indexInMerge++] = value1;
                        break block6;
                    }
                    if (value1 == value2) {
                        mergedForwardReferences[indexInMerge++] = value1;
                        ++j;
                        break block6;
                    }
                    mergedForwardReferences[indexInMerge++] = value2;
                    ++j;
                }
                mergedForwardReferences[indexInMerge++] = value1;
            }
            ++i;
        }
        while (j < max2) {
            mergedForwardReferences[indexInMerge++] = otherLabel.forwardReferences[j];
            ++j;
        }
        this.forwardReferences = mergedForwardReferences;
        this.forwardReferenceCount = indexInMerge;
    }

    void branch() {
        this.tagBits |= 2;
        if (this.delegate != null) {
            this.delegate.branch();
            return;
        }
        if (this.position == -1) {
            this.addForwardReference(this.codeStream.position);
            this.codeStream.position += 2;
            this.codeStream.classFileOffset += 2;
        } else {
            this.codeStream.writePosition(this);
        }
    }

    void branchWide() {
        this.tagBits |= 2;
        if (this.delegate != null) {
            this.delegate.branchWide();
            return;
        }
        if (this.position == -1) {
            this.addForwardReference(this.codeStream.position);
            this.tagBits |= 1;
            this.codeStream.position += 4;
            this.codeStream.classFileOffset += 4;
        } else {
            this.codeStream.writeWidePosition(this);
        }
    }

    public int forwardReferenceCount() {
        if (this.delegate != null) {
            this.delegate.forwardReferenceCount();
        }
        return this.forwardReferenceCount;
    }

    public int[] forwardReferences() {
        if (this.delegate != null) {
            this.delegate.forwardReferences();
        }
        return this.forwardReferences;
    }

    public void initialize(CodeStream stream) {
        this.codeStream = stream;
        this.position = -1;
        this.forwardReferenceCount = 0;
        this.delegate = null;
    }

    public boolean isCaseLabel() {
        return false;
    }

    public boolean isStandardLabel() {
        return true;
    }

    @Override
    public void place() {
        if (this.position == -1) {
            this.position = this.codeStream.position;
            this.codeStream.addLabel(this);
            int oldPosition = this.position;
            boolean isOptimizedBranch = false;
            if (this.forwardReferenceCount != 0) {
                boolean bl = isOptimizedBranch = this.forwardReferences[this.forwardReferenceCount - 1] + 2 == this.position && this.codeStream.bCodeStream[this.codeStream.classFileOffset - 3] == -89;
                if (isOptimizedBranch) {
                    if (this.codeStream.lastAbruptCompletion == this.position) {
                        this.codeStream.lastAbruptCompletion = -1;
                    }
                    this.codeStream.position = this.position -= 3;
                    this.codeStream.classFileOffset -= 3;
                    --this.forwardReferenceCount;
                    if (this.codeStream.lastEntryPC == oldPosition) {
                        this.codeStream.lastEntryPC = this.position;
                    }
                    if ((this.codeStream.generateAttributes & 0x1C) != 0) {
                        LocalVariableBinding[] locals = this.codeStream.locals;
                        int i = 0;
                        int max = locals.length;
                        while (i < max) {
                            LocalVariableBinding local = locals[i];
                            if (local != null && local.initializationCount > 0) {
                                if (local.initializationPCs[(local.initializationCount - 1 << 1) + 1] == oldPosition) {
                                    local.initializationPCs[(local.initializationCount - 1 << 1) + 1] = this.position;
                                }
                                if (local.initializationPCs[local.initializationCount - 1 << 1] == oldPosition) {
                                    local.initializationPCs[local.initializationCount - 1 << 1] = this.position;
                                }
                            }
                            ++i;
                        }
                    }
                    if ((this.codeStream.generateAttributes & 2) != 0) {
                        this.codeStream.removeUnusedPcToSourceMapEntries();
                    }
                }
            }
            int i = 0;
            while (i < this.forwardReferenceCount) {
                this.codeStream.writePosition(this, this.forwardReferences[i]);
                ++i;
            }
            if (isOptimizedBranch) {
                this.codeStream.optimizeBranch(oldPosition, this);
            }
        }
    }

    public String toString() {
        String basic = this.getClass().getName();
        basic = basic.substring(basic.lastIndexOf(46) + 1);
        StringBuffer buffer = new StringBuffer(basic);
        buffer.append('@').append(Integer.toHexString(this.hashCode()));
        buffer.append("(position=").append(this.position);
        if (this.delegate != null) {
            buffer.append("delegate=").append(this.delegate);
        }
        buffer.append(", forwards = [");
        int i = 0;
        while (i < this.forwardReferenceCount - 1) {
            buffer.append(String.valueOf(this.forwardReferences[i]) + ", ");
            ++i;
        }
        if (this.forwardReferenceCount >= 1) {
            buffer.append(this.forwardReferences[this.forwardReferenceCount - 1]);
        }
        buffer.append("] )");
        return buffer.toString();
    }
}

