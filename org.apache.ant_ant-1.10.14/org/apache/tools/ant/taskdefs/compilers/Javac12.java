/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.compilers;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.JavaEnvUtils;

@Deprecated
public class Javac12
extends DefaultCompilerAdapter {
    protected static final String CLASSIC_COMPILER_CLASSNAME = "sun.tools.javac.Main";

    @Override
    public boolean execute() throws BuildException {
        boolean bl;
        this.attributes.log("Using classic compiler", 3);
        Commandline cmd = this.setupJavacCommand(true);
        LogOutputStream logstr = new LogOutputStream(this.attributes, 1);
        try {
            Class<?> c = Class.forName(CLASSIC_COMPILER_CLASSNAME);
            Constructor<?> cons = c.getConstructor(OutputStream.class, String.class);
            Object compiler = cons.newInstance(logstr, "javac");
            Method compile = c.getMethod("compile", String[].class);
            bl = (Boolean)compile.invoke(compiler, new Object[]{cmd.getArguments()});
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((OutputStream)logstr).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (ClassNotFoundException ex) {
                throw new BuildException("Cannot use classic compiler, as it is not available. \n A common solution is to set the environment variable JAVA_HOME to your jdk directory.\nIt is currently set to \"" + JavaEnvUtils.getJavaHome() + "\"", this.location);
            }
            catch (Exception ex) {
                if (ex instanceof BuildException) {
                    throw (BuildException)ex;
                }
                throw new BuildException("Error starting classic compiler: ", ex, this.location);
            }
        }
        ((OutputStream)logstr).close();
        return bl;
    }
}

