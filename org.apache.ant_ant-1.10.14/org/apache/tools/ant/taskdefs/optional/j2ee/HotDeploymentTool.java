/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.j2ee.ServerDeploy;

public interface HotDeploymentTool {
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_DEPLOY = "deploy";
    public static final String ACTION_LIST = "list";
    public static final String ACTION_UNDEPLOY = "undeploy";
    public static final String ACTION_UPDATE = "update";

    public void validateAttributes() throws BuildException;

    public void deploy() throws BuildException;

    public void setTask(ServerDeploy var1);
}

