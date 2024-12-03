/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.lifecycle;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import java.util.Map;

public abstract class LifeCyclePlugin {
    public void init(Workspace ws) throws Exception {
    }

    public void opened(Project project) throws Exception {
    }

    public void close(Project project) throws Exception {
    }

    public void created(Project project) throws Exception {
    }

    public void delete(Project project) throws Exception {
    }

    public void addedPlugin(Workspace workspace, String name, String alias, Map<String, String> parameters) throws Exception {
    }

    public void removedPlugin(Workspace workspace, String alias) throws Exception {
    }

    public String augmentSetup(String setup, String alias, Map<String, String> parameters) throws Exception {
        return setup;
    }
}

