/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.depend;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.VectorSet;
import org.apache.tools.ant.util.depend.DependencyAnalyzer;

public abstract class AbstractAnalyzer
implements DependencyAnalyzer {
    public static final int MAX_LOOPS = 1000;
    private Path sourcePath = new Path(null);
    private Path classPath = new Path(null);
    private final Vector<String> rootClasses = new VectorSet<String>();
    private boolean determined = false;
    private Vector<File> fileDependencies;
    private Vector<String> classDependencies;
    private boolean closure = true;

    protected AbstractAnalyzer() {
        this.reset();
    }

    @Override
    public void setClosure(boolean closure) {
        this.closure = closure;
    }

    @Override
    public Enumeration<File> getFileDependencies() {
        if (!this.supportsFileDependencies()) {
            throw new BuildException("File dependencies are not supported by this analyzer");
        }
        if (!this.determined) {
            this.determineDependencies(this.fileDependencies, this.classDependencies);
        }
        return this.fileDependencies.elements();
    }

    @Override
    public Enumeration<String> getClassDependencies() {
        if (!this.determined) {
            this.determineDependencies(this.fileDependencies, this.classDependencies);
        }
        return this.classDependencies.elements();
    }

    @Override
    public File getClassContainer(String classname) throws IOException {
        String classLocation = classname.replace('.', '/') + ".class";
        return this.getResourceContainer(classLocation, this.classPath.list());
    }

    @Override
    public File getSourceContainer(String classname) throws IOException {
        String sourceLocation = classname.replace('.', '/') + ".java";
        return this.getResourceContainer(sourceLocation, this.sourcePath.list());
    }

    @Override
    public void addSourcePath(Path sourcePath) {
        if (sourcePath == null) {
            return;
        }
        this.sourcePath.append(sourcePath);
        this.sourcePath.setProject(sourcePath.getProject());
    }

    @Override
    public void addClassPath(Path classPath) {
        if (classPath == null) {
            return;
        }
        this.classPath.append(classPath);
        this.classPath.setProject(classPath.getProject());
    }

    @Override
    public void addRootClass(String className) {
        if (className == null) {
            return;
        }
        if (!this.rootClasses.contains(className)) {
            this.rootClasses.addElement(className);
        }
    }

    @Override
    public void config(String name, Object info) {
    }

    @Override
    public void reset() {
        this.rootClasses.removeAllElements();
        this.determined = false;
        this.fileDependencies = new Vector();
        this.classDependencies = new Vector();
    }

    protected Enumeration<String> getRootClasses() {
        return this.rootClasses.elements();
    }

    protected boolean isClosureRequired() {
        return this.closure;
    }

    protected abstract void determineDependencies(Vector<File> var1, Vector<String> var2);

    protected abstract boolean supportsFileDependencies();

    private File getResourceContainer(String resourceLocation, String[] paths) throws IOException {
        for (String path : paths) {
            File element = new File(path);
            if (!element.exists()) continue;
            if (element.isDirectory()) {
                File resource = new File(element, resourceLocation);
                if (!resource.exists()) continue;
                return resource;
            }
            try (ZipFile zipFile = new ZipFile(element);){
                if (zipFile.getEntry(resourceLocation) == null) continue;
                File file = element;
                return file;
            }
        }
        return null;
    }
}

