/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.j2ee.AbstractHotDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.j2ee.ServerDeploy;

public class WebLogicHotDeploymentTool
extends AbstractHotDeploymentTool
implements HotDeploymentTool {
    private static final int STRING_BUFFER_SIZE = 1024;
    private static final String WEBLOGIC_DEPLOY_CLASS_NAME = "weblogic.deploy";
    private static final String[] VALID_ACTIONS = new String[]{"delete", "deploy", "list", "undeploy", "update"};
    private boolean debug;
    private String application;
    private String component;

    @Override
    public void deploy() {
        Java java = new Java(this.getTask());
        java.setFork(true);
        java.setFailonerror(true);
        java.setClasspath(this.getClasspath());
        java.setClassname(WEBLOGIC_DEPLOY_CLASS_NAME);
        java.createArg().setLine(this.getArguments());
        java.execute();
    }

    @Override
    public void validateAttributes() throws BuildException {
        super.validateAttributes();
        String action = this.getTask().getAction();
        if (this.getPassword() == null) {
            throw new BuildException("The password attribute must be set.");
        }
        if ((action.equals("deploy") || action.equals("update")) && this.application == null) {
            throw new BuildException("The application attribute must be set if action = %s", action);
        }
        if ((action.equals("deploy") || action.equals("update")) && this.getTask().getSource() == null) {
            throw new BuildException("The source attribute must be set if action = %s", action);
        }
        if ((action.equals("delete") || action.equals("undeploy")) && this.application == null) {
            throw new BuildException("The application attribute must be set if action = %s", action);
        }
    }

    public String getArguments() throws BuildException {
        String action = this.getTask().getAction();
        if (action.equals("deploy") || action.equals("update")) {
            return this.buildDeployArgs();
        }
        if (action.equals("delete") || action.equals("undeploy")) {
            return this.buildUndeployArgs();
        }
        if (action.equals("list")) {
            return this.buildListArgs();
        }
        return null;
    }

    @Override
    protected boolean isActionValid() {
        String action = this.getTask().getAction();
        for (String validAction : VALID_ACTIONS) {
            if (!action.equals(validAction)) continue;
            return true;
        }
        return false;
    }

    protected StringBuffer buildArgsPrefix() {
        ServerDeploy task = this.getTask();
        return new StringBuffer(1024).append(this.getServer() != null ? "-url " + this.getServer() : "").append(" ").append(this.debug ? "-debug " : "").append(this.getUserName() != null ? "-username " + this.getUserName() : "").append(" ").append(task.getAction()).append(" ").append(this.getPassword()).append(" ");
    }

    protected String buildDeployArgs() {
        String args = this.buildArgsPrefix().append(this.application).append(" ").append(this.getTask().getSource()).toString();
        if (this.component != null) {
            args = "-component " + this.component + " " + args;
        }
        return args;
    }

    protected String buildUndeployArgs() {
        return this.buildArgsPrefix().append(this.application).append(" ").toString();
    }

    protected String buildListArgs() {
        return this.buildArgsPrefix().toString();
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void setComponent(String component) {
        this.component = component;
    }
}

