/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.plugin.maven;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.service.lifecycle.LifeCyclePlugin;
import aQute.lib.io.IO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.Map;

@BndPlugin(name="maven")
public class MavenPlugin
extends LifeCyclePlugin {
    @Override
    public void created(Project p) throws IOException {
        Workspace workspace = p.getWorkspace();
        this.copy("pom.xml", "pom.xml", p);
        File root = workspace.getFile("pom.xml");
        this.doRoot(p, root);
        String rootPom = IO.collect(root);
        if (!rootPom.contains(this.getTag(p))) {
            rootPom = rootPom.replaceAll("<!-- DO NOT EDIT MANAGED BY BND MAVEN LIFECYCLE PLUGIN -->\n", "$0\n\t\t" + this.getTag(p) + "\n");
            IO.store((Object)rootPom, root);
        }
    }

    private void doRoot(Project p, File root) throws IOException {
        if (!root.isFile()) {
            IO.delete(root);
            this.copy("rootpom.xml", "../pom.xml", p);
        }
    }

    private void copy(String source, String dest, Project p) throws IOException {
        String s;
        File f = p.getWorkspace().getFile("maven/" + source + ".tmpl");
        if (f.isFile()) {
            s = IO.collect(f);
        } else {
            try (InputStream in = MavenPlugin.class.getResourceAsStream(source);){
                if (in == null) {
                    p.error("Cannot find Maven default for %s", source);
                    return;
                }
                s = IO.collect(in);
            }
        }
        String process = p.getReplacer().process(s);
        File d = p.getFile(dest);
        IO.mkdirs(d.getParentFile());
        IO.store((Object)process, d);
    }

    @Override
    public String augmentSetup(String setup, String alias, Map<String, String> parameters) throws Exception {
        try (Formatter f = new Formatter();){
            f.format("%s", setup);
            f.format("\n#\n# Change disk layout to fit maven\n#\n\n", new Object[0]);
            f.format("-outputmask = ${@bsn}-${version;===S;${@version}}.jar\n", new Object[0]);
            f.format("src=src/main/java\n", new Object[0]);
            f.format("bin=target/classes\n", new Object[0]);
            f.format("testsrc=src/test/java\n", new Object[0]);
            f.format("testbin=target/test-classes\n", new Object[0]);
            f.format("target-dir=target\n", new Object[0]);
            String string = f.toString();
            return string;
        }
    }

    @Override
    public void delete(Project p) throws IOException {
        File root = p.getWorkspace().getFile("pom.xml");
        String rootPom = IO.collect(root);
        if (rootPom.contains(this.getTag(p))) {
            rootPom = rootPom.replaceAll("\n\\s*" + this.getTag(p) + "\\s*", "\n");
            IO.store((Object)rootPom, root);
        }
    }

    private String getTag(Project p) {
        return "<module>" + p + "</module>";
    }

    public String toString() {
        return "MavenPlugin";
    }
}

