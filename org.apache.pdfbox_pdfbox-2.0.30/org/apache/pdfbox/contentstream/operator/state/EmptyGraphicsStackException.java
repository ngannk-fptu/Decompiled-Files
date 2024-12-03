/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.state;

import java.io.IOException;

public final class EmptyGraphicsStackException
extends IOException {
    private static final long serialVersionUID = 1L;

    EmptyGraphicsStackException() {
        super("Cannot execute restore, the graphics stack is empty");
    }
}

