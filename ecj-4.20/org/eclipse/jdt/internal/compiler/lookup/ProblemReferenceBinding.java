/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.lang.reflect.Field;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ProblemReferenceBinding
extends ReferenceBinding {
    ReferenceBinding closestMatch;
    private int problemReason;

    public ProblemReferenceBinding(char[][] compoundName, ReferenceBinding closestMatch, int problemReason) {
        this.compoundName = compoundName;
        this.closestMatch = closestMatch;
        if (closestMatch != null) {
            this.sourceName = closestMatch.sourceName;
        }
        this.problemReason = problemReason;
    }

    @Override
    public TypeBinding clone(TypeBinding enclosingType) {
        throw new IllegalStateException();
    }

    @Override
    public TypeBinding closestMatch() {
        return this.closestMatch;
    }

    public ReferenceBinding closestReferenceMatch() {
        return this.closestMatch;
    }

    @Override
    public ReferenceBinding superclass() {
        if (this.closestMatch != null) {
            return this.closestMatch.superclass();
        }
        return super.superclass();
    }

    @Override
    public ReferenceBinding[] superInterfaces() {
        if (this.closestMatch != null) {
            return this.closestMatch.superInterfaces();
        }
        return super.superInterfaces();
    }

    @Override
    public boolean hasTypeBit(int bit) {
        if (this.closestMatch != null) {
            return this.closestMatch.hasTypeBit(bit);
        }
        return false;
    }

    @Override
    public int problemId() {
        return this.problemReason;
    }

    public static String problemReasonString(int problemReason) {
        try {
            Class<ProblemReasons> reasons = ProblemReasons.class;
            String simpleName = reasons.getName();
            int lastDot = simpleName.lastIndexOf(46);
            if (lastDot >= 0) {
                simpleName = simpleName.substring(lastDot + 1);
            }
            Field[] fields = reasons.getFields();
            int i = 0;
            int length = fields.length;
            while (i < length) {
                Field field = fields[i];
                if (field.getType().equals(Integer.TYPE) && field.getInt(reasons) == problemReason) {
                    return String.valueOf(simpleName) + '.' + field.getName();
                }
                ++i;
            }
        }
        catch (IllegalAccessException illegalAccessException) {}
        return "unknown";
    }

    @Override
    public void setTypeAnnotations(AnnotationBinding[] annotations, boolean evalNullAnnotations) {
    }

    @Override
    public char[] shortReadableName() {
        return this.readableName();
    }

    @Override
    public char[] sourceName() {
        return this.compoundName.length == 0 ? null : this.compoundName[this.compoundName.length - 1];
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(10);
        buffer.append("ProblemType:[compoundName=");
        buffer.append(this.compoundName == null ? "<null>" : new String(CharOperation.concatWith(this.compoundName, '.')));
        buffer.append("][problemID=").append(ProblemReferenceBinding.problemReasonString(this.problemReason));
        buffer.append("][closestMatch=");
        buffer.append(this.closestMatch == null ? "<null>" : this.closestMatch.toString());
        buffer.append("]");
        return buffer.toString();
    }
}

