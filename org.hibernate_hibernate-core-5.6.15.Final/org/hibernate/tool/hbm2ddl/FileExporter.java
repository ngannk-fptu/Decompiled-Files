/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.io.FileWriter;
import java.io.IOException;
import org.hibernate.tool.hbm2ddl.Exporter;

@Deprecated
class FileExporter
implements Exporter {
    private final FileWriter writer;

    public FileExporter(String outputFile) throws IOException {
        this.writer = new FileWriter(outputFile);
    }

    @Override
    public boolean acceptsImportScripts() {
        return false;
    }

    @Override
    public void export(String string) throws Exception {
        this.writer.write(string + '\n');
    }

    @Override
    public void release() throws Exception {
        this.writer.flush();
        this.writer.close();
    }
}

