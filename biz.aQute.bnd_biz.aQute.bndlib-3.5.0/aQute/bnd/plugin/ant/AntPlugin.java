/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.plugin.ant;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.service.lifecycle.LifeCyclePlugin;
import aQute.lib.io.IO;
import java.io.File;
import java.io.IOException;

@BndPlugin(name="ant")
public class AntPlugin
extends LifeCyclePlugin {
    static String DEFAULT = "<?xml version='1.0' encoding='UTF-8'?>\n<project name='project' default='build'>\n        <import file='../cnf/build.xml' />\n</project>\n";

    @Override
    public void created(Project p) throws IOException {
        Workspace workspace = p.getWorkspace();
        File source = workspace.getFile("ant/project.xml");
        File dest = p.getFile("build.xml");
        if (source.isFile()) {
            IO.copy(source, dest);
        } else {
            IO.store((Object)DEFAULT, dest);
        }
    }

    public String toString() {
        return "AntPlugin";
    }
}

