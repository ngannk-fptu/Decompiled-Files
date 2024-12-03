/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package aQute.bnd.osgi;

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
import org.osgi.annotation.versioning.ProviderType;

@Deprecated
@ProviderType
public class Classpath {
    List<File> entries = new ArrayList<File>();

    public Classpath(Reporter project, String name) {
    }

    public void add(Collection<Object> testpath) throws Exception {
        throw new UnsupportedOperationException();
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

