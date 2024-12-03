/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.regex;

import org.radeox.regex.Compiler;
import org.radeox.regex.JdkPattern;
import org.radeox.regex.Pattern;

public class JdkCompiler
extends Compiler {
    private boolean multiline;

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public Pattern compile(String regex) {
        return new JdkPattern(regex, this.multiline);
    }
}

