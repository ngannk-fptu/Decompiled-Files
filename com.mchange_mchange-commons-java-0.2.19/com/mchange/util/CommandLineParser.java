/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

public interface CommandLineParser {
    public boolean checkSwitch(String var1);

    public String findSwitchArg(String var1);

    public boolean checkArgv();

    public int findLastSwitched();

    public String[] findUnswitchedArgs();
}

