/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
    public static InputStream hasSomeData(InputStream in) {
        if (in != null) {
            try {
                if (in.available() < 1) {
                    if (!in.markSupported()) {
                        in = new BufferedInputStream(in);
                    }
                    in.mark(1);
                    if (in.read() != -1) {
                        in.reset();
                    } else {
                        in = null;
                    }
                }
            }
            catch (IOException ioe) {
                in = null;
            }
        }
        return in;
    }
}

