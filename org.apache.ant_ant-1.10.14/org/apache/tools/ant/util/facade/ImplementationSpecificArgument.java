/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.facade;

import org.apache.tools.ant.types.Commandline;

public class ImplementationSpecificArgument
extends Commandline.Argument {
    private String impl;

    public void setImplementation(String impl) {
        this.impl = impl;
    }

    public final String[] getParts(String chosenImpl) {
        if (this.impl == null || this.impl.equals(chosenImpl)) {
            return super.getParts();
        }
        return new String[0];
    }
}

