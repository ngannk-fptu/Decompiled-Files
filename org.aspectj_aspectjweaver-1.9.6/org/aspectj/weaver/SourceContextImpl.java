/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.File;
import java.util.Arrays;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.weaver.AbstractReferenceTypeDelegate;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ISourceContext;

public class SourceContextImpl
implements ISourceContext {
    private int[] lineBreaks;
    String sourceFilename;
    public static final ISourceContext UNKNOWN_SOURCE_CONTEXT = new ISourceContext(){

        @Override
        public ISourceLocation makeSourceLocation(IHasPosition position) {
            return null;
        }

        @Override
        public ISourceLocation makeSourceLocation(int line, int offset) {
            return null;
        }

        @Override
        public int getOffset() {
            return 0;
        }

        @Override
        public void tidy() {
        }
    };

    public SourceContextImpl(AbstractReferenceTypeDelegate delegate) {
        this.sourceFilename = delegate.getSourcefilename();
    }

    public void configureFromAttribute(String name, int[] linebreaks) {
        this.sourceFilename = name;
        this.lineBreaks = linebreaks;
    }

    public void setSourceFileName(String name) {
        this.sourceFilename = name;
    }

    private File getSourceFile() {
        return new File(this.sourceFilename);
    }

    @Override
    public void tidy() {
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public ISourceLocation makeSourceLocation(IHasPosition position) {
        if (this.lineBreaks != null) {
            int line = Arrays.binarySearch(this.lineBreaks, position.getStart());
            if (line < 0) {
                line = -line;
            }
            return new SourceLocation(this.getSourceFile(), line);
        }
        return new SourceLocation(this.getSourceFile(), 0);
    }

    @Override
    public ISourceLocation makeSourceLocation(int line, int offset) {
        if (line < 0) {
            line = 0;
        }
        SourceLocation sl = new SourceLocation(this.getSourceFile(), line);
        if (offset > 0) {
            sl.setOffset(offset);
        } else if (this.lineBreaks != null) {
            int likelyOffset = 0;
            if (line > 0 && line < this.lineBreaks.length) {
                likelyOffset = this.lineBreaks[line - 1] + 1;
            }
            sl.setOffset(likelyOffset);
        }
        return sl;
    }
}

