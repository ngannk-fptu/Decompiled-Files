/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.PrintStream;
import org.apache.tools.ant.BuildListener;

public interface BuildLogger
extends BuildListener {
    public void setMessageOutputLevel(int var1);

    default public int getMessageOutputLevel() {
        return 2;
    }

    public void setOutputPrintStream(PrintStream var1);

    public void setEmacsMode(boolean var1);

    public void setErrorPrintStream(PrintStream var1);
}

