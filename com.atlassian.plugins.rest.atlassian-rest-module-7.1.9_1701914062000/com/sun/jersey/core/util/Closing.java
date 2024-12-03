/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import java.io.IOException;
import java.io.InputStream;

public class Closing {
    private final InputStream in;

    public static Closing with(InputStream in) {
        return new Closing(in);
    }

    public Closing(InputStream in) {
        this.in = in;
    }

    public void f(Closure c) throws IOException {
        if (this.in == null) {
            return;
        }
        try {
            c.f(this.in);
        }
        finally {
            this.in.close();
        }
    }

    public static interface Closure {
        public void f(InputStream var1) throws IOException;
    }
}

