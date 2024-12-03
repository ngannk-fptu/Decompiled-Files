/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build;

import aQute.bnd.build.Project;
import aQute.bnd.build.ProjectLauncher;
import aQute.lib.io.IO;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ProjectTester {
    private final Project project;
    private final ProjectLauncher launcher;
    private final List<String> tests = new ArrayList<String>();
    private File reportDir;
    private boolean continuous = true;

    public ProjectTester(Project project) throws Exception {
        this.project = project;
        this.launcher = project.getProjectLauncher();
        this.launcher.addRunVM("-ea");
        this.continuous = project.is("-testcontinuous");
        this.reportDir = new File(project.getTarget(), project.getProperty("test-reports", "test-reports"));
    }

    public ProjectLauncher getProjectLauncher() {
        return this.launcher;
    }

    public void addTest(String test) {
        this.tests.add(test);
    }

    public Collection<String> getTests() {
        return this.tests;
    }

    public Collection<File> getReports() {
        ArrayList<File> reports = new ArrayList<File>();
        for (File report : this.reportDir.listFiles()) {
            if (!report.isFile()) continue;
            reports.add(report);
        }
        return reports;
    }

    public File getReportDir() {
        return this.reportDir;
    }

    public void setReportDir(File reportDir) {
        this.reportDir = reportDir;
    }

    public Project getProject() {
        return this.project;
    }

    public boolean getContinuous() {
        return this.continuous;
    }

    public void setContinuous(boolean b) {
        this.continuous = b;
    }

    public File getCwd() {
        return this.launcher.getCwd();
    }

    public void setCwd(File dir) {
        this.launcher.setCwd(dir);
    }

    public boolean prepare() throws Exception {
        IO.mkdirs(this.reportDir);
        return true;
    }

    public abstract int test() throws Exception;
}

