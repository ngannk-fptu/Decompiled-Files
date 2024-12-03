/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.PrintStream;
import java.util.List;
import org.apache.tools.ant.Project;

public interface ArgumentProcessor {
    public int readArguments(String[] var1, int var2);

    public boolean handleArg(List<String> var1);

    public void prepareConfigure(Project var1, List<String> var2);

    public boolean handleArg(Project var1, List<String> var2);

    public void printUsage(PrintStream var1);
}

