/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.oro.text.regex.MalformedPatternException
 *  org.apache.oro.text.regex.Pattern
 *  org.apache.oro.text.regex.Perl5Compiler
 */
package org.radeox.regex;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Perl5Compiler;
import org.radeox.regex.Compiler;
import org.radeox.regex.OroPattern;
import org.radeox.regex.Pattern;

public class OroCompiler
extends Compiler {
    private Perl5Compiler internalCompiler = new Perl5Compiler();
    private boolean multiline;

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public Pattern compile(String regex) {
        org.apache.oro.text.regex.Pattern pattern = null;
        try {
            pattern = this.internalCompiler.compile(regex, (this.multiline ? 8 : 16) | 0x8000);
        }
        catch (MalformedPatternException malformedPatternException) {
            // empty catch block
        }
        return new OroPattern(regex, this.multiline, pattern);
    }
}

