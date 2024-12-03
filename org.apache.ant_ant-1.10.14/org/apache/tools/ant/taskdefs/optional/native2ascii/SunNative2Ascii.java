/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.native2ascii;

import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
import org.apache.tools.ant.taskdefs.optional.native2ascii.DefaultNative2Ascii;
import org.apache.tools.ant.types.Commandline;

public final class SunNative2Ascii
extends DefaultNative2Ascii {
    public static final String IMPLEMENTATION_NAME = "sun";
    private static final String SUN_TOOLS_NATIVE2ASCII_MAIN = "sun.tools.native2ascii.Main";

    @Override
    protected void setup(Commandline cmd, Native2Ascii args) throws BuildException {
        if (args.getReverse()) {
            cmd.createArgument().setValue("-reverse");
        }
        super.setup(cmd, args);
    }

    @Override
    protected boolean run(Commandline cmd, ProjectComponent log) throws BuildException {
        try {
            Class<?> n2aMain = Class.forName(SUN_TOOLS_NATIVE2ASCII_MAIN);
            Method convert = n2aMain.getMethod("convert", String[].class);
            return Boolean.TRUE.equals(convert.invoke(n2aMain.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]), new Object[]{cmd.getArguments()}));
        }
        catch (BuildException ex) {
            throw ex;
        }
        catch (NoSuchMethodException ex) {
            throw new BuildException("Could not find convert() method in %s", SUN_TOOLS_NATIVE2ASCII_MAIN);
        }
        catch (Exception ex) {
            throw new BuildException("Error starting Sun's native2ascii: ", ex);
        }
    }
}

