/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.native2ascii;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;

public interface Native2AsciiAdapter {
    public boolean convert(Native2Ascii var1, File var2, File var3) throws BuildException;
}

