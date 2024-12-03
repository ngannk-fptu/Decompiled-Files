/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.regex;

import org.radeox.regex.JdkCompiler;
import org.radeox.regex.Pattern;

public abstract class Compiler {
    public static Compiler create() {
        return new JdkCompiler();
    }

    public abstract void setMultiline(boolean var1);

    public abstract Pattern compile(String var1);
}

