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

    public Stax2FileResult(File file) {
        this.mFile = file;
    }

    public Writer constructWriter() throws IOException {
        String string = this.getEncoding();
        if (string != null && string.length() > 0) {
            return new OutputStreamWriter(this.constructOutputStream(), string);
        }
        return new FileWriter(this.mFile);
    }

    public OutputStream constructOutputStream() throws IOException {
        return new FileOutputStream(this.mFile);
    }

    public File getFile() {
        return this.mFile;
    }
}

