/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.DefaultFileLineFormatter;

public abstract class FileLineFormatter {
    private static FileLineFormatter formatter = new DefaultFileLineFormatter();

    public static FileLineFormatter getFormatter() {
        return formatter;
    }

    public static void setFormatter(FileLineFormatter fileLineFormatter) {
        formatter = fileLineFormatter;
    }

    public abstract String getFormatString(String var1, int var2, int var3);
}

