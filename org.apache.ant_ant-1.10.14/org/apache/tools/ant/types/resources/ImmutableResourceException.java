/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.IOException;

public class ImmutableResourceException
extends IOException {
    private static final long serialVersionUID = 1L;

    public ImmutableResourceException() {
    }

    public ImmutableResourceException(String s) {
        super(s);
    }
}

