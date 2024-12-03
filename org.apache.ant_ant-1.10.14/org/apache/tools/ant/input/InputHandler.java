/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.input;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.InputRequest;

public interface InputHandler {
    public void handleInput(InputRequest var1) throws BuildException;
}

