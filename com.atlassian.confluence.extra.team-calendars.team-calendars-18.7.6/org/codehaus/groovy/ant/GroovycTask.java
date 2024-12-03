/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.DirectoryScanner
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.types.Path
 *  org.apache.tools.ant.util.FileNameMapper
 *  org.apache.tools.ant.util.GlobPatternMapper
 *  org.apache.tools.ant.util.SourceFileScanner
 */
package org.codehaus.groovy.ant;

import groovy.lang.GroovyClassLoader;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.SourceFileScanner;
import org.codehaus.groovy.ant.CompileTaskSupport;
import org.codehaus.groovy.control.CompilationUnit;

public class GroovycTask
extends CompileTaskSupport {
    protected boolean force;

    public void setForce(boolean flag) {
        this.force = flag;
    }

    @Override
    protected void compile() {
        Path path = this.getClasspath();
        if (path != null) {
            this.config.setClasspath(path.toString());
        }
        this.config.setTargetDirectory(this.destdir);
        GroovyClassLoader gcl = this.createClassLoader();
        CompilationUnit compilation = new CompilationUnit(this.config, null, gcl);
        GlobPatternMapper mapper = new GlobPatternMapper();
        mapper.setFrom("*.groovy");
        mapper.setTo("*.class");
        int count = 0;
        String[] list = this.src.list();
        for (int i = 0; i < list.length; ++i) {
            File basedir = this.getProject().resolveFile(list[i]);
            if (!basedir.exists()) {
                throw new BuildException("Source directory does not exist: " + basedir, this.getLocation());
            }
            DirectoryScanner scanner = this.getDirectoryScanner(basedir);
            String[] includes = scanner.getIncludedFiles();
            if (this.force) {
                this.log.debug("Forcefully including all files from: " + basedir);
                for (int j = 0; j < includes.length; ++j) {
                    File file = new File(basedir, includes[j]);
                    this.log.debug("    " + file);
                    compilation.addSource(file);
                    ++count;
                }
                continue;
            }
            this.log.debug("Including changed files from: " + basedir);
            SourceFileScanner sourceScanner = new SourceFileScanner((Task)this);
            File[] files = sourceScanner.restrictAsFiles(includes, basedir, this.destdir, (FileNameMapper)mapper);
            for (int j = 0; j < files.length; ++j) {
                this.log.debug("    " + files[j]);
                compilation.addSource(files[j]);
                ++count;
            }
        }
        if (count > 0) {
            this.log.info("Compiling " + count + " source file" + (count > 1 ? "s" : "") + " to " + this.destdir);
            compilation.compile();
        } else {
            this.log.info("No sources found to compile");
        }
    }
}

