/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.mail;

import java.io.IOException;

public class ErrorInQuitException
extends IOException {
    private static final long serialVersionUID = 1L;

    public ErrorInQuitException(IOException e) {
        super(e.getMessage());
    }
}

