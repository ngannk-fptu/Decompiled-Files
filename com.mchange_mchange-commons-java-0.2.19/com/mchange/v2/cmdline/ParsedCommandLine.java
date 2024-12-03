/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cmdline;

public interface ParsedCommandLine {
    public String[] getRawArgs();

    public String getSwitchPrefix();

    public boolean includesSwitch(String var1);

    public String getSwitchArg(String var1);

    public String[] getUnswitchedArgs();
}

