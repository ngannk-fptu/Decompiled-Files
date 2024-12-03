/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cmdline;

import com.mchange.v2.cmdline.BadCommandLineException;
import com.mchange.v2.cmdline.ParsedCommandLine;
import com.mchange.v2.cmdline.ParsedCommandLineImpl;

public final class CommandLineUtils {
    public static ParsedCommandLine parse(String[] stringArray, String string, String[] stringArray2, String[] stringArray3, String[] stringArray4) throws BadCommandLineException {
        return new ParsedCommandLineImpl(stringArray, string, stringArray2, stringArray3, stringArray4);
    }

    private CommandLineUtils() {
    }
}

