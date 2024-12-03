/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.Appendable;

public class LogOutputResource
extends Resource
implements Appendable {
    private static final String NAME = "[Ant log]";
    private LogOutputStream outputStream;

    public LogOutputResource(ProjectComponent managingComponent) {
        super(NAME);
        this.outputStream = new LogOutputStream(managingComponent);
    }

    public LogOutputResource(ProjectComponent managingComponent, int level) {
        super(NAME);
        this.outputStream = new LogOutputStream(managingComponent, level);
    }

    @Override
    public OutputStream getAppendOutputStream() throws IOException {
        return this.outputStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.outputStream;
    }
}

