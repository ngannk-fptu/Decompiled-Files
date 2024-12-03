/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cmdline;

import com.mchange.v2.cmdline.BadCommandLineException;

public class UnexpectedSwitchArgumentException
extends BadCommandLineException {
    String sw;
    String arg;

    UnexpectedSwitchArgumentException(String string, String string2, String string3) {
        super(string);
        this.sw = string2;
        this.arg = string3;
    }

    public String getSwitch() {
        return this.sw;
    }

    public String getUnexpectedArgument() {
        return this.arg;
    }
}

