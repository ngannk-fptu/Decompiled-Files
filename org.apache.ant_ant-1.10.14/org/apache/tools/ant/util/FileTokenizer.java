/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.Tokenizer;

public class FileTokenizer
extends ProjectComponent
implements Tokenizer {
    @Override
    public String getToken(Reader in) throws IOException {
        return FileUtils.readFully(in);
    }

    @Override
    public String getPostToken() {
        return "";
    }
}

