/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.native2ascii;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
import org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapter;
import org.apache.tools.ant.types.Commandline;

public abstract class DefaultNative2Ascii
implements Native2AsciiAdapter {
    @Override
    public final boolean convert(Native2Ascii args, File srcFile, File destFile) throws BuildException {
        Commandline cmd = new Commandline();
        this.setup(cmd, args);
        this.addFiles(cmd, args, srcFile, destFile);
        return this.run(cmd, args);
    }

    protected void setup(Commandline cmd, Native2Ascii args) throws BuildException {
        if (args.getEncoding() != null) {
            cmd.createArgument().setValue("-encoding");
            cmd.createArgument().setValue(args.getEncoding());
        }
        cmd.addArguments(args.getCurrentArgs());
    }

    protected void addFiles(Commandline cmd, ProjectComponent log, File src, File dest) throws BuildException {
        cmd.createArgument().setFile(src);
        cmd.createArgument().setFile(dest);
    }

    protected abstract boolean run(Commandline var1, ProjectComponent var2) throws BuildException;
}

