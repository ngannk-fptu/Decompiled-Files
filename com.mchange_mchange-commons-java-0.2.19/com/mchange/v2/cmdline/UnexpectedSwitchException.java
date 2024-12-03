/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cmdline;

import com.mchange.v2.cmdline.BadCommandLineException;

public class UnexpectedSwitchException
extends BadCommandLineException {
    String sw;

    UnexpectedSwitchException(String string, String string2) {
        super(string);
        this.sw = string2;
    }

    public String getUnexpectedSwitch() {
        return this.sw;
    }
}

