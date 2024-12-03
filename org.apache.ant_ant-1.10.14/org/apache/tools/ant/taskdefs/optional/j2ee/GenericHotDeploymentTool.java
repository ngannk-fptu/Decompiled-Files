/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.j2ee.AbstractHotDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.j2ee.ServerDeploy;
import org.apache.tools.ant.types.Commandline;

public class GenericHotDeploymentTool
extends AbstractHotDeploymentTool {
    private Java java;
    private String className;
    private static final String[] VALID_ACTIONS = new String[]{"deploy"};

    public Commandline.Argument createArg() {
        return this.java.createArg();
    }

    public Commandline.Argument createJvmarg() {
        return this.java.createJvmarg();
    }

    @Override
    protected boolean isActionValid() {
        return this.getTask().getAction().equals(VALID_ACTIONS[0]);
    }

    @Override
    public void setTask(ServerDeploy task) {
        super.setTask(task);
        this.java = new Java(task);
    }

    @Override
    public void deploy() throws BuildException {
        this.java.setClassname(this.className);
        this.java.setClasspath(this.getClasspath());
        this.java.setFork(true);
        this.java.setFailonerror(true);
        this.java.execute();
    }

    @Override
    public void validateAttributes() throws BuildException {
        super.validateAttributes();
        if (this.className == null) {
            throw new BuildException("The classname attribute must be set");
        }
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Java getJava() {
        return this.java;
    }

    public String getClassName() {
        return this.className;
    }
}

