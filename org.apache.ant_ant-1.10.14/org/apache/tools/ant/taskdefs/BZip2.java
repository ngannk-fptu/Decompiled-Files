/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Pack;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.bzip2.CBZip2OutputStream;

public class BZip2
extends Pack {
    @Override
    protected void pack() {
        CBZip2OutputStream zOut = null;
        try {
            BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(this.zipFile.toPath(), new OpenOption[0]));
            bos.write(66);
            bos.write(90);
            zOut = new CBZip2OutputStream(bos);
            this.zipResource(this.getSrcResource(), zOut);
        }
        catch (IOException ioe) {
            try {
                String msg = "Problem creating bzip2 " + ioe.getMessage();
                throw new BuildException(msg, ioe, this.getLocation());
            }
            catch (Throwable throwable) {
                FileUtils.close(zOut);
                throw throwable;
            }
        }
        FileUtils.close(zOut);
    }

    @Override
    protected boolean supportsNonFileResources() {
        return this.getClass().equals(BZip2.class);
    }
}

