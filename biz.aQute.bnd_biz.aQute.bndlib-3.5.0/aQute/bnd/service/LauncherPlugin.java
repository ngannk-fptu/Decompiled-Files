/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.bnd.build.Project;
import aQute.bnd.build.ProjectLauncher;
import aQute.bnd.build.ProjectTester;

public interface LauncherPlugin {
    public ProjectLauncher getLauncher(Project var1) throws Exception;

    public ProjectTester getTester(Project var1);
}

