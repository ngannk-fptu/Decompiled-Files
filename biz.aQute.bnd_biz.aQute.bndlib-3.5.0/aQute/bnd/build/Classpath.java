/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build;

import aQute.bnd.build.Container;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.lib.strings.Strings;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Classpath {
    List<File> entries = new ArrayList<File>();
    private Reporter project;
    private String name;

    public Classpath(Reporter project, String name) {
        this.project = project;
        this.name = name;
    }

    public void add(Collection<Container> testpath) throws Exception {
        for (Container c : Container.flatten(testpath)) {
            if (c.getError() != null) {
                this.project.error("Adding %s to %s, got error: %s", c, this.name, c.getError());
                continue;
            }
            this.entries.add(c.getFile().getAbsoluteFile());
        }
    }

    public List<File> getEntries() {
        return this.entries;
    }

    public void visit(ClassVisitor visitor) throws Exception {
        try (Analyzer analyzer = new Analyzer();){
            for (File f : this.entries) {
                Jar jar = new Jar(f);
                Throwable throwable = null;
                try {
                    for (String path : jar.getResources().keySet()) {
                        if (!path.endsWith(".class")) continue;
                        Resource r = jar.getResource(path);
                        Clazz c = new Clazz(analyzer, path, r);
                        c.parseClassFile();
                        visitor.visit(c);
                    }
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (jar == null) continue;
                    if (throwable != null) {
                        try {
                            jar.close();
                        }
                        catch (Throwable x2) {
                            throwable.addSuppressed(x2);
                        }
                        continue;
                    }
                    jar.close();
                }
            }
        }
    }

    public String toString() {
        return Strings.join(File.pathSeparator, this.entries);
    }

    public static interface ClassVisitor {
        public boolean visit(Clazz var1) throws Exception;
    }
}

