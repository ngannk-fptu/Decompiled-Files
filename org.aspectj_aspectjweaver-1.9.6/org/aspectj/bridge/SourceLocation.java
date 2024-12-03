/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.io.File;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.util.LangUtil;

public class SourceLocation
implements ISourceLocation {
    private static final long serialVersionUID = -5434765814401009794L;
    private transient int cachedHashcode = -1;
    public static final ISourceLocation UNKNOWN = new SourceLocation(ISourceLocation.NO_FILE, 0, 0, 0);
    private final File sourceFile;
    private final int startLine;
    private final int column;
    private final int endLine;
    private int offset;
    private final String context;
    private boolean noColumn;
    private String sourceFileName;

    public static final void validLine(int line) {
        if (line < 0) {
            throw new IllegalArgumentException("negative line: " + line);
        }
        if (line > 0x3FFFFFFF) {
            throw new IllegalArgumentException("line too large: " + line);
        }
    }

    public static final void validColumn(int column) {
        if (column < 0) {
            throw new IllegalArgumentException("negative column: " + column);
        }
        if (column > 0x3FFFFFFF) {
            throw new IllegalArgumentException("column too large: " + column);
        }
    }

    public SourceLocation(File file, int line) {
        this(file, line, line, -2147483647);
    }

    public SourceLocation(File file, int line, int endLine) {
        this(file, line, endLine, -2147483647);
    }

    public SourceLocation(File file, int line, int endLine, int column) {
        this(file, line, endLine, column, null);
    }

    public SourceLocation(File file, int line, int endLine, int column, String context) {
        if (column == -2147483647) {
            column = 0;
            this.noColumn = true;
        }
        if (null == file) {
            file = ISourceLocation.NO_FILE;
        }
        SourceLocation.validLine(line);
        SourceLocation.validLine(endLine);
        LangUtil.throwIaxIfFalse(line <= endLine, line + " > " + endLine);
        LangUtil.throwIaxIfFalse(column >= 0, "negative column: " + column);
        this.sourceFile = file;
        this.startLine = line;
        this.column = column;
        this.endLine = endLine;
        this.context = context;
    }

    public SourceLocation(File file, int line, int endLine, int column, String context, String sourceFileName) {
        this(file, line, endLine, column, context);
        this.sourceFileName = sourceFileName;
    }

    @Override
    public File getSourceFile() {
        return this.sourceFile;
    }

    @Override
    public int getLine() {
        return this.startLine;
    }

    @Override
    public int getColumn() {
        return this.column;
    }

    @Override
    public int getEndLine() {
        return this.endLine;
    }

    @Override
    public String getContext() {
        return this.context;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (null != this.context) {
            sb.append(this.context);
            sb.append(LangUtil.EOL);
        }
        if (this.sourceFile != ISourceLocation.NO_FILE) {
            sb.append(this.sourceFile.getPath());
        }
        if (this.startLine > 0) {
            sb.append(":");
            sb.append(this.startLine);
        }
        if (!this.noColumn) {
            sb.append(":" + this.column);
        }
        if (this.offset >= 0) {
            sb.append("::" + this.offset);
        }
        return sb.toString();
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int i) {
        this.cachedHashcode = -1;
        this.offset = i;
    }

    @Override
    public String getSourceFileName() {
        return this.sourceFileName;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SourceLocation)) {
            return false;
        }
        SourceLocation o = (SourceLocation)obj;
        return this.startLine == o.startLine && this.column == o.column && this.endLine == o.endLine && this.offset == o.offset && (this.sourceFile == null ? o.sourceFile == null : this.sourceFile.equals(o.sourceFile)) && (this.context == null ? o.context == null : this.context.equals(o.context)) && this.noColumn == o.noColumn && (this.sourceFileName == null ? o.sourceFileName == null : this.sourceFileName.equals(o.sourceFileName));
    }

    public int hashCode() {
        if (this.cachedHashcode == -1) {
            this.cachedHashcode = this.sourceFile == null ? 0 : this.sourceFile.hashCode();
            this.cachedHashcode = this.cachedHashcode * 37 + this.startLine;
            this.cachedHashcode = this.cachedHashcode * 37 + this.column;
            this.cachedHashcode = this.cachedHashcode * 37 + this.endLine;
            this.cachedHashcode = this.cachedHashcode * 37 + this.offset;
            this.cachedHashcode = this.cachedHashcode * 37 + (this.context == null ? 0 : this.context.hashCode());
            this.cachedHashcode = this.cachedHashcode * 37 + (this.noColumn ? 0 : 1);
            this.cachedHashcode = this.cachedHashcode * 37 + (this.sourceFileName == null ? 0 : this.sourceFileName.hashCode());
        }
        return this.cachedHashcode;
    }
}

