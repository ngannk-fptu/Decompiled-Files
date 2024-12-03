/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.DirectoryScanner
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.types.FileSet
 *  org.apache.tools.ant.types.Parameter
 *  org.apache.tools.ant.util.FlatFileNameMapper
 *  org.apache.tools.ant.util.IdentityMapper
 */
package org.w3c.tidy.ant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.util.FlatFileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.w3c.tidy.Tidy;

public class JTidyTask
extends Task {
    private List filesets = new ArrayList();
    private File destdir;
    private File destfile;
    private File srcfile;
    private boolean failonerror;
    private boolean flatten;
    private Tidy tidy;
    private Properties props;
    private File properties;

    public void setDestdir(File destdir) {
        this.destdir = destdir;
    }

    public void setDestfile(File destfile) {
        this.destfile = destfile;
    }

    public void setSrcfile(File srcfile) {
        this.srcfile = srcfile;
    }

    public void setFailonerror(boolean failonerror) {
        this.failonerror = failonerror;
    }

    public void setFlatten(boolean flatten) {
        this.flatten = flatten;
    }

    public void setProperties(File properties) {
        this.properties = properties;
    }

    public void addFileset(FileSet fileSet) {
        this.filesets.add(fileSet);
    }

    public void addConfiguredParameter(Parameter prop) {
        this.props.setProperty(prop.getName(), prop.getValue());
    }

    public void init() {
        super.init();
        this.tidy = new Tidy();
        this.props = new Properties();
    }

    protected void validateParameters() throws BuildException {
        if (this.srcfile == null && this.filesets.size() == 0) {
            throw new BuildException("Specify at least srcfile or a fileset.");
        }
        if (this.srcfile != null && this.filesets.size() > 0) {
            throw new BuildException("You can't specify both srcfile and nested filesets.");
        }
        if (this.destfile == null && this.destdir == null) {
            throw new BuildException("One of destfile or destdir must be set.");
        }
        if (this.srcfile == null && this.destfile != null) {
            throw new BuildException("You only can use destfile with srcfile.");
        }
        if (this.srcfile != null && this.srcfile.isDirectory()) {
            throw new BuildException("srcfile can't be a directory.");
        }
        if (this.properties != null && this.properties.isDirectory()) {
            throw new BuildException("Invalid properties file specified: " + this.properties.getPath());
        }
    }

    public void execute() throws BuildException {
        this.validateParameters();
        if (this.properties != null) {
            try {
                this.props.load(new FileInputStream(this.properties));
            }
            catch (IOException e) {
                throw new BuildException("Unable to load properties file " + this.properties, (Throwable)e);
            }
        }
        this.tidy.setErrout(new PrintWriter(new ByteArrayOutputStream()));
        this.tidy.setConfigurationFromProps(this.props);
        if (this.srcfile != null) {
            this.executeSingle();
        } else {
            this.executeSet();
        }
    }

    protected void executeSingle() {
        if (!this.srcfile.exists()) {
            throw new BuildException("Could not find source file " + this.srcfile.getAbsolutePath() + ".");
        }
        if (this.destfile == null) {
            this.destfile = new File(this.destdir, this.srcfile.getName());
        }
        this.processFile(this.srcfile, this.destfile);
    }

    protected void executeSet() {
        Object mapper = null;
        mapper = this.flatten ? new FlatFileNameMapper() : new IdentityMapper();
        mapper.setTo(this.destdir.getAbsolutePath());
        Iterator iterator = this.filesets.iterator();
        while (iterator.hasNext()) {
            FileSet fileSet = (FileSet)iterator.next();
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(this.getProject());
            String[] sourceFiles = directoryScanner.getIncludedFiles();
            File inputdir = directoryScanner.getBasedir();
            mapper.setFrom(inputdir.getAbsolutePath());
            for (int j = 0; j < sourceFiles.length; ++j) {
                String[] mapped = mapper.mapFileName(sourceFiles[j]);
                this.processFile(new File(inputdir, sourceFiles[j]), new File(this.destdir, mapped[0]));
            }
        }
    }

    protected void processFile(File inputFile, File outputFile) {
        BufferedOutputStream os;
        BufferedInputStream is;
        this.log("Processing " + inputFile.getAbsolutePath(), 4);
        try {
            is = new BufferedInputStream(new FileInputStream(inputFile));
        }
        catch (IOException e) {
            throw new BuildException("Unable to open file " + inputFile);
        }
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(outputFile));
        }
        catch (IOException e) {
            throw new BuildException("Unable to open destination file " + outputFile, (Throwable)e);
        }
        this.tidy.parse((InputStream)is, (OutputStream)os);
        try {
            ((InputStream)is).close();
        }
        catch (IOException e1) {
            // empty catch block
        }
        try {
            ((OutputStream)os).flush();
            ((OutputStream)os).close();
        }
        catch (IOException e1) {
            // empty catch block
        }
        if (this.tidy.getParseErrors() > 0 && !this.tidy.getForceOutput()) {
            outputFile.delete();
        }
        if (this.failonerror && this.tidy.getParseErrors() > 0) {
            throw new BuildException("Tidy was unable to process file " + inputFile + ", " + this.tidy.getParseErrors() + " returned.");
        }
    }
}

