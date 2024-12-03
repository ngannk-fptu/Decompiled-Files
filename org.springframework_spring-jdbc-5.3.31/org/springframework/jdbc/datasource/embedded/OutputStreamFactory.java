/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource.embedded;

import java.io.IOException;
import java.io.OutputStream;

public final class OutputStreamFactory {
    private OutputStreamFactory() {
    }

    public static OutputStream getNoopOutputStream() {
        return new OutputStream(){

            @Override
            public void write(int b) throws IOException {
            }
        };
    }
}

