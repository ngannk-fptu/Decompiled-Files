/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff;

import org.apache.commons.jrcs.diff.DiffException;

public class PatchFailedException
extends DiffException {
    public PatchFailedException() {
    }

    public PatchFailedException(String msg) {
        super(msg);
    }
}

