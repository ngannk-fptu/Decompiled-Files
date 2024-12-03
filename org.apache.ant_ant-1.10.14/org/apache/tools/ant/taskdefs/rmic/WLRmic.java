/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.rmic;

import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.rmic.DefaultRmicAdapter;
import org.apache.tools.ant.types.Commandline;

public class WLRmic
extends DefaultRmicAdapter {
    public static final String WLRMIC_CLASSNAME = "weblogic.rmic";
    public static final String COMPILER_NAME = "weblogic";
    public static final String ERROR_NO_WLRMIC_ON_CLASSPATH = "Cannot use WebLogic rmic, as it is not available. Add it to Ant's classpath with the -lib option";
    public static final String ERROR_WLRMIC_FAILED = "Error starting WebLogic rmic: ";
    public static final String WL_RMI_STUB_SUFFIX = "_WLStub";
    public static final String WL_RMI_SKEL_SUFFIX = "_WLSkel";
    public static final String UNSUPPORTED_STUB_OPTION = "Unsupported stub option: ";

    @Override
    protected boolean areIiopAndIdlSupported() {
        return true;
    }

    @Override
    public boolean execute() throws BuildException {
        this.getRmic().log("Using WebLogic rmic", 3);
        Commandline cmd = this.setupRmicCommand(new String[]{"-noexit"});
        AntClassLoader loader = null;
        try {
            Class<?> c;
            if (this.getRmic().getClasspath() == null) {
                c = Class.forName(WLRMIC_CLASSNAME);
            } else {
                loader = this.getRmic().getProject().createClassLoader(this.getRmic().getClasspath());
                c = Class.forName(WLRMIC_CLASSNAME, true, loader);
            }
            Method doRmic = c.getMethod("main", String[].class);
            doRmic.invoke(null, new Object[]{cmd.getArguments()});
            boolean bl = true;
            return bl;
        }
        catch (ClassNotFoundException ex) {
            throw new BuildException(ERROR_NO_WLRMIC_ON_CLASSPATH, this.getRmic().getLocation());
        }
        catch (Exception ex) {
            if (ex instanceof BuildException) {
                throw (BuildException)ex;
            }
            throw new BuildException(ERROR_WLRMIC_FAILED, ex, this.getRmic().getLocation());
        }
        finally {
            if (loader != null) {
                loader.cleanup();
            }
        }
    }

    @Override
    public String getStubClassSuffix() {
        return WL_RMI_STUB_SUFFIX;
    }

    @Override
    public String getSkelClassSuffix() {
        return WL_RMI_SKEL_SUFFIX;
    }

    @Override
    protected String[] preprocessCompilerArgs(String[] compilerArgs) {
        return this.filterJvmCompilerArgs(compilerArgs);
    }

    @Override
    protected String addStubVersionOptions() {
        String stubVersion = this.getRmic().getStubVersion();
        if (null != stubVersion) {
            this.getRmic().log(UNSUPPORTED_STUB_OPTION + stubVersion, 1);
        }
        return null;
    }
}

