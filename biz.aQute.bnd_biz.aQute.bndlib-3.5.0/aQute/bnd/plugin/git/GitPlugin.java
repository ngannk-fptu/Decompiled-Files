/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.plugin.git;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.build.Project;
import aQute.bnd.service.lifecycle.LifeCyclePlugin;
import aQute.lib.io.IO;
import java.io.File;
import java.io.IOException;
import java.util.Formatter;

@BndPlugin(name="git")
public class GitPlugin
extends LifeCyclePlugin {
    private static final String GITIGNORE = ".gitignore";

    @Override
    public void created(Project p) throws Exception {
        Formatter f = new Formatter();
        f.format("/%s/\n", p.getProperty("target-dir", "generated"));
        f.format("/%s/\n", p.getProperty("bin", "bin"));
        f.format("/%s/\n", p.getProperty("testbin", "bin_test"));
        IO.store((Object)f.toString(), p.getFile(GITIGNORE));
        f.close();
        for (File dir : p.getSourcePath()) {
            this.touch(dir);
        }
        this.touch(p.getTestSrc());
    }

    private void touch(File dir) throws IOException {
        IO.mkdirs(dir);
        IO.store((Object)"", new File(dir, GITIGNORE));
    }

    public String toString() {
        return "GitPlugin";
    }
}

