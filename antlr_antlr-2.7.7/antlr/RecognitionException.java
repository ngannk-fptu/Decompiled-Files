/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ANTLRException;
import antlr.FileLineFormatter;

public class RecognitionException
extends ANTLRException {
    public String fileName;
    public int line;
    public int column;

    public RecognitionException() {
        super("parsing error");
        this.fileName = null;
        this.line = -1;
        this.column = -1;
    }

    public RecognitionException(String string) {
        super(string);
        this.fileName = null;
        this.line = -1;
        this.column = -1;
    }

    public RecognitionException(String string, String string2, int n) {
        this(string, string2, n, -1);
    }

    public RecognitionException(String string, String string2, int n, int n2) {
        super(string);
        this.fileName = string2;
        this.line = n;
        this.column = n2;
    }

    public String getFilename() {
        return this.fileName;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String getErrorMessage() {
        return this.getMessage();
    }

    public String toString() {
        return FileLineFormatter.getFormatter().getFormatString(this.fileName, this.line, this.column) + this.getMessage();
    }
}

