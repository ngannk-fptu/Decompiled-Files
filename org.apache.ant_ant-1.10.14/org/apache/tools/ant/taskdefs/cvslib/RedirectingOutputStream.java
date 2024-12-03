/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.cvslib;

import org.apache.tools.ant.taskdefs.cvslib.ChangeLogParser;
import org.apache.tools.ant.util.LineOrientedOutputStream;

class RedirectingOutputStream
extends LineOrientedOutputStream {
    private final ChangeLogParser parser;

    public RedirectingOutputStream(ChangeLogParser parser) {
        this.parser = parser;
    }

    @Override
    protected void processLine(String line) {
        this.parser.stdout(line);
    }
}

