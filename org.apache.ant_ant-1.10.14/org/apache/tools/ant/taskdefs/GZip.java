/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Pack;

public class GZip
extends Pack {
    @Override
    protected void pack() {
        try (GZIPOutputStream zOut = new GZIPOutputStream(Files.newOutputStream(this.zipFile.toPath(), new OpenOption[0]));){
            this.zipResource(this.getSrcResource(), zOut);
        }
        catch (IOException ioe) {
            String msg = "Problem creating gzip " + ioe.getMessage();
            throw new BuildException(msg, ioe, this.getLocation());
        }
    }

    @Override
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(GZip.class);
    }
}

