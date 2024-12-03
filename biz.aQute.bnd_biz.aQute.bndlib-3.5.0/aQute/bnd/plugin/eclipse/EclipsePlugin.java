/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.plugin.eclipse;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.service.lifecycle.LifeCyclePlugin;
import aQute.lib.io.IO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@BndPlugin(name="eclipse")
public class EclipsePlugin
extends LifeCyclePlugin {
    @Override
    public void created(Project p) throws IOException {
        this.copy("project", ".project", p);
        this.copy("classpath", ".classpath", p);
    }

    private void copy(String source, String dest, Project p) throws IOException {
        String s;
        File d = p.getFile(dest);
        if (d.isFile()) {
            return;
        }
        File f = p.getWorkspace().getFile("eclipse/" + source + ".tmpl");
        if (f.isFile()) {
            s = IO.collect(f);
        } else {
            InputStream in = EclipsePlugin.class.getResourceAsStream(source);
            if (in == null) {
                p.error("Cannot find Eclipse default for %s", source);
                return;
            }
            s = IO.collect(in);
        }
        String process = p.getReplacer().process(s);
        IO.mkdirs(d.getParentFile());
        IO.store((Object)process, d);
    }

    public String toString() {
        return "EclipsePlugin";
    }

    @Override
    public void init(Workspace ws) throws Exception {
        try (Project p = new Project(ws, ws.getFile("cnf"));){
            this.created(p);
        }
        for (Project pp : ws.getAllProjects()) {
            this.created(pp);
        }
    }
}

