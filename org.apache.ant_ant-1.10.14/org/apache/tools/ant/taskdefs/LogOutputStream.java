/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.LineOrientedOutputStream;

public class LogOutputStream
extends LineOrientedOutputStream {
    private ProjectComponent pc;
    private int level = 2;

    public LogOutputStream(ProjectComponent pc) {
        this.pc = pc;
    }

    public LogOutputStream(Task task, int level) {
        this((ProjectComponent)task, level);
    }

    public LogOutputStream(ProjectComponent pc, int level) {
        this(pc);
        this.level = level;
    }

    @Override
    protected void processBuffer() {
        try {
            super.processBuffer();
        }
        catch (IOException e) {
            throw new RuntimeException("Impossible IOException caught: " + e);
        }
    }

    @Override
    protected void processLine(String line) {
        this.processLine(line, this.level);
    }

    protected void processLine(String line, int level) {
        this.pc.log(line, level);
    }

    public int getMessageLevel() {
        return this.level;
    }
}

