/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;

public interface Tokenizer {
    public String getToken(Reader var1) throws IOException;

    public String getPostToken();
}

