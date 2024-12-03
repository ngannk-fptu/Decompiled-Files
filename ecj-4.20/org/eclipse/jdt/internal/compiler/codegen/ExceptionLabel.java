/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.Label;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ExceptionLabel
extends Label {
    public int[] ranges = new int[]{-1, -1};
    private int count = 0;
    public TypeBinding exceptionType;
    public TypeReference exceptionTypeReference;
    public Annotation[] se7Annotations;

    public ExceptionLabel(CodeStream codeStream, TypeBinding exceptionType, TypeReference exceptionTypeReference, Annotation[] se7Annotations) {
        super(codeStream);
        this.exceptionType = exceptionType;
        this.exceptionTypeReference = exceptionTypeReference;
        this.se7Annotations = se7Annotations;
    }

    public ExceptionLabel(CodeStream codeStream, TypeBinding exceptionType) {
        super(codeStream);
        this.exceptionType = exceptionType;
    }

    public int getCount() {
        return this.count;
    }

    @Override
    public void place() {
        this.codeStream.registerExceptionHandler(this);
        this.position = this.codeStream.getPosition();
    }

    public void placeEnd() {
        int endPosition = this.codeStream.position;
        if (this.ranges[this.count - 1] == endPosition) {
            --this.count;
        } else {
            this.ranges[this.count++] = endPosition;
        }
    }

    public void placeStart() {
        int startPosition = this.codeStream.position;
        if (this.count > 0 && this.ranges[this.count - 1] == startPosition) {
            --this.count;
            return;
        }
        int length = this.ranges.length;
        if (this.count == length) {
            this.ranges = new int[length * 2];
            System.arraycopy(this.ranges, 0, this.ranges, 0, length);
        }
        this.ranges[this.count++] = startPosition;
    }

    public String toString() {
        String basic = this.getClass().getName();
        basic = basic.substring(basic.lastIndexOf(46) + 1);
        StringBuffer buffer = new StringBuffer(basic);
        buffer.append('@').append(Integer.toHexString(this.hashCode()));
        buffer.append("(type=").append(this.exceptionType == null ? CharOperation.NO_CHAR : this.exceptionType.readableName());
        buffer.append(", position=").append(this.position);
        buffer.append(", ranges = ");
        if (this.count == 0) {
            buffer.append("[]");
        } else {
            int i = 0;
            while (i < this.count) {
                if ((i & 1) == 0) {
                    buffer.append("[").append(this.ranges[i]);
                } else {
                    buffer.append(",").append(this.ranges[i]).append("]");
                }
                ++i;
            }
            if ((this.count & 1) == 1) {
                buffer.append(",?]");
            }
        }
        buffer.append(')');
        return buffer.toString();
    }
}

