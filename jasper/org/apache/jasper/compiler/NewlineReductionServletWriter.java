/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import java.io.PrintWriter;
import org.apache.jasper.compiler.ServletWriter;

public class NewlineReductionServletWriter
extends ServletWriter {
    private static final String NEWLINE_WRITE_TEXT = "out.write('\\n');";
    private boolean lastWriteWasNewline;

    public NewlineReductionServletWriter(PrintWriter writer) {
        super(writer);
    }

    @Override
    public void printil(String s) {
        if (s.equals(NEWLINE_WRITE_TEXT)) {
            if (this.lastWriteWasNewline) {
                return;
            }
            this.lastWriteWasNewline = true;
        } else {
            this.lastWriteWasNewline = false;
        }
        super.printil(s);
    }
}

