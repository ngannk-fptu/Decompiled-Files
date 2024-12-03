/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Unpack;

public class GUnzip
extends Unpack {
    private static final int BUFFER_SIZE = 8192;
    private static final String DEFAULT_EXTENSION = ".gz";

    @Override
    protected String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    protected void extract() {
        if (this.srcResource.getLastModified() > this.dest.lastModified()) {
            this.log("Expanding " + this.srcResource.getName() + " to " + this.dest.getAbsolutePath());
            try (OutputStream out = Files.newOutputStream(this.dest.toPath(), new OpenOption[0]);
                 GZIPInputStream zIn = new GZIPInputStream(this.srcResource.getInputStream());){
                byte[] buffer = new byte[8192];
                int count = 0;
                do {
                    out.write(buffer, 0, count);
                } while ((count = zIn.read(buffer, 0, buffer.length)) != -1);
            }
            catch (IOException ioe) {
                String msg = "Problem expanding gzip " + ioe.getMessage();
                throw new BuildException(msg, ioe, this.getLocation());
            }
        }
    }

    @Override
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(GUnzip.class);
    }
}

