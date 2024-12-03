/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.j2ee;

import java.io.File;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.j2ee.AbstractHotDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.j2ee.GenericHotDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.j2ee.JonasHotDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.j2ee.WebLogicHotDeploymentTool;

public class ServerDeploy
extends Task {
    private String action;
    private File source;
    private List<AbstractHotDeploymentTool> vendorTools = new Vector<AbstractHotDeploymentTool>();

    public void addGeneric(GenericHotDeploymentTool tool) {
        tool.setTask(this);
        this.vendorTools.add(tool);
    }

    public void addWeblogic(WebLogicHotDeploymentTool tool) {
        tool.setTask(this);
        this.vendorTools.add(tool);
    }

    public void addJonas(JonasHotDeploymentTool tool) {
        tool.setTask(this);
        this.vendorTools.add(tool);
    }

    @Override
    public void execute() throws BuildException {
        for (HotDeploymentTool hotDeploymentTool : this.vendorTools) {
            hotDeploymentTool.validateAttributes();
            hotDeploymentTool.deploy();
        }
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public File getSource() {
        return this.source;
    }

    public void setSource(File source) {
        this.source = source;
    }
}

