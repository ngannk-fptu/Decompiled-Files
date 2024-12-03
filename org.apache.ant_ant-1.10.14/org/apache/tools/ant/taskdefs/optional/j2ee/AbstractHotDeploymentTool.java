/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.j2ee.ServerDeploy;
import org.apache.tools.ant.types.Path;

public abstract class AbstractHotDeploymentTool
implements HotDeploymentTool {
    private ServerDeploy task;
    private Path classpath;
    private String userName;
    private String password;
    private String server;

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.task.getProject());
        }
        return this.classpath.createPath();
    }

    protected abstract boolean isActionValid();

    @Override
    public void validateAttributes() throws BuildException {
        if (this.task.getAction() == null) {
            throw new BuildException("The \"action\" attribute must be set");
        }
        if (!this.isActionValid()) {
            throw new BuildException("Invalid action \"%s\" passed", this.task.getAction());
        }
        if (this.classpath == null) {
            throw new BuildException("The classpath attribute must be set");
        }
    }

    @Override
    public void setTask(ServerDeploy task) {
        this.task = task;
    }

    protected ServerDeploy getTask() {
        return this.task;
    }

    public Path getClasspath() {
        return this.classpath;
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}

