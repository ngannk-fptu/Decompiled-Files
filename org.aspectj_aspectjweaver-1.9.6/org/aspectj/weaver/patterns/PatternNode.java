/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.IHasSourceLocation;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.patterns.PatternNodeVisitor;

public abstract class PatternNode
implements IHasSourceLocation {
    protected int start = -1;
    protected int end = -1;
    protected ISourceContext sourceContext;

    @Override
    public int getStart() {
        return this.start + (this.sourceContext != null ? this.sourceContext.getOffset() : 0);
    }

    @Override
    public int getEnd() {
        return this.end + (this.sourceContext != null ? this.sourceContext.getOffset() : 0);
    }

    @Override
    public ISourceContext getSourceContext() {
        return this.sourceContext;
    }

    public String getFileName() {
        return "unknown";
    }

    public void setLocation(ISourceContext sourceContext, int start, int end) {
        this.sourceContext = sourceContext;
        this.start = start;
        this.end = end;
    }

    public void copyLocationFrom(PatternNode other) {
        this.start = other.start;
        this.end = other.end;
        this.sourceContext = other.sourceContext;
    }

    @Override
    public ISourceLocation getSourceLocation() {
        if (this.sourceContext == null) {
            return null;
        }
        return this.sourceContext.makeSourceLocation(this);
    }

    public abstract void write(CompressingDataOutputStream var1) throws IOException;

    public void writeLocation(DataOutputStream s) throws IOException {
        s.writeInt(this.start);
        s.writeInt(this.end);
    }

    public void readLocation(ISourceContext context, DataInputStream s) throws IOException {
        this.start = s.readInt();
        this.end = s.readInt();
        this.sourceContext = context;
    }

    public abstract Object accept(PatternNodeVisitor var1, Object var2);

    public Object traverse(PatternNodeVisitor visitor, Object data) {
        return this.accept(visitor, data);
    }
}

