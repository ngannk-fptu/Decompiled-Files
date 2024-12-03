/*
 * Decompiled with CFR 0.152.
 */
package org.ungoverned.osgi.service.shell;

import java.io.PrintStream;

public interface Command {
    public String getName();

    public String getUsage();

    public String getShortDescription();

    public void execute(String var1, PrintStream var2, PrintStream var3);
}

