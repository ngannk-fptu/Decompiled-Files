/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.listener;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.listener.SimpleBigProjectLogger;

public class BigProjectLogger
extends SimpleBigProjectLogger
implements SubBuildListener {
    private volatile boolean subBuildStartedRaised = false;
    private final Object subBuildLock = new Object();
    public static final String HEADER = "======================================================================";
    public static final String FOOTER = "======================================================================";

    @Override
    protected String getBuildFailedMessage() {
        return super.getBuildFailedMessage() + " - at " + this.getTimestamp();
    }

    @Override
    protected String getBuildSuccessfulMessage() {
        return super.getBuildSuccessfulMessage() + " - at " + this.getTimestamp();
    }

    @Override
    public void targetStarted(BuildEvent event) {
        this.maybeRaiseSubBuildStarted(event);
        super.targetStarted(event);
    }

    @Override
    public void taskStarted(BuildEvent event) {
        this.maybeRaiseSubBuildStarted(event);
        super.taskStarted(event);
    }

    @Override
    public void buildFinished(BuildEvent event) {
        this.maybeRaiseSubBuildStarted(event);
        this.subBuildFinished(event);
        super.buildFinished(event);
    }

    @Override
    public void messageLogged(BuildEvent event) {
        this.maybeRaiseSubBuildStarted(event);
        super.messageLogged(event);
    }

    @Override
    public void subBuildStarted(BuildEvent event) {
        Project project = event.getProject();
        String path = project == null ? "With no base directory" : "In " + project.getBaseDir().getAbsolutePath();
        this.printMessage(String.format("%n%s%nEntering project %s%n%s%n%s", this.getHeader(), this.extractNameOrDefault(event), path, this.getFooter()), this.out, event.getPriority());
    }

    protected String extractNameOrDefault(BuildEvent event) {
        String name = this.extractProjectName(event);
        name = name == null ? "" : '\"' + name + '\"';
        return name;
    }

    @Override
    public void subBuildFinished(BuildEvent event) {
        this.printMessage(String.format("%n%s%nExiting %sproject %s%n%s", this.getHeader(), event.getException() != null ? "failing " : "", this.extractNameOrDefault(event), this.getFooter()), this.out, event.getPriority());
    }

    protected String getHeader() {
        return "======================================================================";
    }

    protected String getFooter() {
        return "======================================================================";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void maybeRaiseSubBuildStarted(BuildEvent event) {
        if (!this.subBuildStartedRaised) {
            Object object = this.subBuildLock;
            synchronized (object) {
                if (!this.subBuildStartedRaised) {
                    this.subBuildStartedRaised = true;
                    this.subBuildStarted(event);
                }
            }
        }
    }
}

