/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

public class Jvc
extends DefaultCompilerAdapter {
    @Override
    public boolean execute() throws BuildException {
        this.attributes.log("Using jvc compiler", 3);
        Path classpath = new Path(this.project);
        Path p = this.getBootClassPath();
        if (!p.isEmpty()) {
            classpath.append(p);
        }
        if (this.includeJavaRuntime) {
            classpath.addExtdirs(this.extdirs);
        }
        classpath.append(this.getCompileClasspath());
        if (this.compileSourcepath != null) {
            classpath.append(this.compileSourcepath);
        } else {
            classpath.append(this.src);
        }
        Commandline cmd = new Commandline();
        String exec = this.getJavac().getExecutable();
        cmd.setExecutable(exec == null ? "jvc" : exec);
        if (this.destDir != null) {
            cmd.createArgument().setValue("/d");
            cmd.createArgument().setFile(this.destDir);
        }
        cmd.createArgument().setValue("/cp:p");
        cmd.createArgument().setPath(classpath);
        boolean msExtensions = true;
        String mse = this.getProject().getProperty("build.compiler.jvc.extensions");
        if (mse != null) {
            msExtensions = Project.toBoolean(mse);
        }
        if (msExtensions) {
            cmd.createArgument().setValue("/x-");
            cmd.createArgument().setValue("/nomessage");
        }
        cmd.createArgument().setValue("/nologo");
        if (this.debug) {
            cmd.createArgument().setValue("/g");
        }
        if (this.optimize) {
            cmd.createArgument().setValue("/O");
        }
        if (this.verbose) {
            cmd.createArgument().setValue("/verbose");
        }
        this.addCurrentCompilerArgs(cmd);
        int firstFileName = cmd.size();
        this.logAndAddFilesToCompile(cmd);
        return this.executeExternalCompile(cmd.getCommandline(), firstFileName, false) == 0;
    }
}

