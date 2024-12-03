/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.cvslib;

import java.io.ByteArrayOutputStream;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.cvslib.ChangeLogParser;
import org.apache.tools.ant.taskdefs.cvslib.RedirectingOutputStream;
import org.apache.tools.ant.util.FileUtils;

class RedirectingStreamHandler
extends PumpStreamHandler {
    RedirectingStreamHandler(ChangeLogParser parser) {
        super(new RedirectingOutputStream(parser), new ByteArrayOutputStream());
    }

    String getErrors() {
        try {
            ByteArrayOutputStream error = (ByteArrayOutputStream)this.getErr();
            return error.toString("ASCII");
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public void stop() {
        super.stop();
        FileUtils.close(this.getErr());
        FileUtils.close(this.getOut());
    }
}

