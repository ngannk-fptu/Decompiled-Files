/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.util.FileUtils;

public class LogStreamHandler
extends PumpStreamHandler {
    public LogStreamHandler(Task task, int outlevel, int errlevel) {
        this((ProjectComponent)task, outlevel, errlevel);
    }

    public LogStreamHandler(ProjectComponent pc, int outlevel, int errlevel) {
        super(new LogOutputStream(pc, outlevel), new LogOutputStream(pc, errlevel));
    }

    @Override
    public void stop() {
        super.stop();
        FileUtils.close(this.getErr());
        FileUtils.close(this.getOut());
    }
}

