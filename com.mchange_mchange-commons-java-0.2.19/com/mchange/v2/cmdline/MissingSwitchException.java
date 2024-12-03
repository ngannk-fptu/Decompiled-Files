/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cmdline;

import com.mchange.v2.cmdline.BadCommandLineException;

public class MissingSwitchException
extends BadCommandLineException {
    String sw;

    MissingSwitchException(String string, String string2) {
        super(string);
        this.sw = string2;
    }

    public String getMissingSwitch() {
        return this.sw;
    }
}

