/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.optional.depend;

import java.io.File;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.StreamUtils;
import org.apache.tools.ant.util.depend.DependencyAnalyzer;

public class DependScanner
extends DirectoryScanner {
    public static final String DEFAULT_ANALYZER_CLASS = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
    private Vector<String> rootClasses;
    private Vector<String> included;
    private Vector<File> additionalBaseDirs = new Vector();
    private DirectoryScanner parentScanner;

    public DependScanner(DirectoryScanner parentScanner) {
        this.parentScanner = parentScanner;
    }

    public synchronized void setRootClasses(Vector<String> rootClasses) {
        this.rootClasses = rootClasses;
    }

    @Override
    public String[] getIncludedFiles() {
        return this.included.toArray(new String[this.getIncludedFilesCount()]);
    }

    @Override
    public synchronized int getIncludedFilesCount() {
        if (this.included == null) {
            throw new IllegalStateException();
        }
        return this.included.size();
    }

    @Override
    public synchronized void scan() throws IllegalStateException {
        DependencyAnalyzer analyzer;
        this.included = new Vector();
        String analyzerClassName = DEFAULT_ANALYZER_CLASS;
        try {
            Class<DependencyAnalyzer> analyzerClass = Class.forName(analyzerClassName).asSubclass(DependencyAnalyzer.class);
            analyzer = analyzerClass.newInstance();
        }
        catch (Exception e) {
            throw new BuildException("Unable to load dependency analyzer: " + analyzerClassName, e);
        }
        analyzer.addClassPath(new Path(null, this.basedir.getPath()));
        this.additionalBaseDirs.stream().map(File::getPath).map(p -> new Path(null, (String)p)).forEach(analyzer::addClassPath);
        this.rootClasses.forEach(analyzer::addRootClass);
        Set parentSet = Stream.of(this.parentScanner.getIncludedFiles()).collect(Collectors.toSet());
        StreamUtils.enumerationAsStream(analyzer.getClassDependencies()).map(cName -> cName.replace('.', File.separatorChar) + ".class").filter(fName -> new File(this.basedir, (String)fName).exists() && parentSet.contains(fName)).forEach(fName -> this.included.addElement((String)fName));
    }

    @Override
    public void addDefaultExcludes() {
    }

    @Override
    public String[] getExcludedDirectories() {
        return null;
    }

    @Override
    public String[] getExcludedFiles() {
        return null;
    }

    @Override
    public String[] getIncludedDirectories() {
        return new String[0];
    }

    @Override
    public int getIncludedDirsCount() {
        return 0;
    }

    @Override
    public String[] getNotIncludedDirectories() {
        return null;
    }

    @Override
    public String[] getNotIncludedFiles() {
        return null;
    }

    @Override
    public void setExcludes(String[] excludes) {
    }

    @Override
    public void setIncludes(String[] includes) {
    }

    @Override
    public void setCaseSensitive(boolean isCaseSensitive) {
    }

    public void addBasedir(File baseDir) {
        this.additionalBaseDirs.addElement(baseDir);
    }
}

