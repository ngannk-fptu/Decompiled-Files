/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFile;
import org.apache.tools.ant.types.resources.ZipResource;
import org.apache.tools.ant.util.depend.AbstractAnalyzer;
import org.apache.tools.zip.ZipFile;

public class AntAnalyzer
extends AbstractAnalyzer {
    @Override
    protected void determineDependencies(Vector<File> files, Vector<String> classes) {
        int maxCount;
        HashSet<String> dependencies = new HashSet<String>();
        HashSet<File> containers = new HashSet<File>();
        HashSet<String> toAnalyze = new HashSet<String>(Collections.list(this.getRootClasses()));
        HashSet<String> analyzedDeps = new HashSet<String>();
        int count = 0;
        int n = maxCount = this.isClosureRequired() ? 1000 : 1;
        while (!toAnalyze.isEmpty() && count++ < maxCount) {
            analyzedDeps.clear();
            for (String classname : toAnalyze) {
                dependencies.add(classname);
                File container = null;
                try {
                    container = this.getClassContainer(classname);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (container == null) continue;
                containers.add(container);
                try {
                    InputStream inStream = container.getName().endsWith(".class") ? Files.newInputStream(Paths.get(container.getPath(), new String[0]), new OpenOption[0]) : ZipResource.getZipEntryStream(new ZipFile(container.getPath(), "UTF-8"), classname.replace('.', '/') + ".class");
                    try {
                        ClassFile classFile = new ClassFile();
                        classFile.read(inStream);
                        analyzedDeps.addAll(classFile.getClassRefs());
                    }
                    finally {
                        if (inStream == null) continue;
                        inStream.close();
                    }
                }
                catch (IOException iOException) {}
            }
            toAnalyze.clear();
            analyzedDeps.stream().filter(className -> !dependencies.contains(className)).forEach(toAnalyze::add);
        }
        dependencies.addAll(analyzedDeps);
        files.removeAllElements();
        files.addAll(containers);
        classes.removeAllElements();
        classes.addAll(dependencies);
    }

    @Override
    protected boolean supportsFileDependencies() {
        return true;
    }
}

