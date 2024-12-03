/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.codehaus.stax2.io.Stax2ReferentialResult;

public class Stax2FileResult
extends Stax2ReferentialResult {
    final File mFile;

    public Stax2FileResult(File f) {
        this.mFile = f;
    }

    @Override
    public Writer constructWriter() throws IOException {
        String enc = this.getEncoding();
        if (enc != null && enc.length() > 0) {
            return new OutputStreamWriter(this.constructOutputStream(), enc);
        }
        return new FileWriter(this.mFile);
    }

    @Override
    public OutputStream constructOutputStream() throws IOException {
        return new FileOutputStream(this.mFile);
    }

    public File getFile() {
        return this.mFile;
    }
}

