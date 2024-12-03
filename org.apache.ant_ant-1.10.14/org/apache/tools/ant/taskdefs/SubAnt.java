/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.StringUtils;

public class SubAnt
extends Task {
    private Path buildpath;
    private Ant ant = null;
    private String subTarget = null;
    private String antfile = this.getDefaultBuildFile();
    private File genericantfile = null;
    private boolean verbose = false;
    private boolean inheritAll = false;
    private boolean inheritRefs = false;
    private boolean failOnError = true;
    private String output = null;
    private List<Property> properties = new Vector<Property>();
    private List<Ant.Reference> references = new Vector<Ant.Reference>();
    private List<PropertySet> propertySets = new Vector<PropertySet>();
    private List<Ant.TargetElement> targets = new Vector<Ant.TargetElement>();

    protected String getDefaultBuildFile() {
        return "build.xml";
    }

    @Override
    public void handleOutput(String output) {
        if (this.ant != null) {
            this.ant.handleOutput(output);
        } else {
            super.handleOutput(output);
        }
    }

    @Override
    public int handleInput(byte[] buffer, int offset, int length) throws IOException {
        if (this.ant != null) {
            return this.ant.handleInput(buffer, offset, length);
        }
        return super.handleInput(buffer, offset, length);
    }

    @Override
    public void handleFlush(String output) {
        if (this.ant != null) {
            this.ant.handleFlush(output);
        } else {
            super.handleFlush(output);
        }
    }

    @Override
    public void handleErrorOutput(String output) {
        if (this.ant != null) {
            this.ant.handleErrorOutput(output);
        } else {
            super.handleErrorOutput(output);
        }
    }

    @Override
    public void handleErrorFlush(String output) {
        if (this.ant != null) {
            this.ant.handleErrorFlush(output);
        } else {
            super.handleErrorFlush(output);
        }
    }

    @Override
    public void execute() {
        if (this.buildpath == null) {
            throw new BuildException("No buildpath specified");
        }
        String[] filenames = this.buildpath.list();
        int count = filenames.length;
        if (count < 1) {
            this.log("No sub-builds to iterate on", 1);
            return;
        }
        BuildException buildException = null;
        for (String filename : filenames) {
            File file = null;
            String subdirPath = null;
            Throwable thrownException = null;
            try {
                File directory = null;
                file = new File(filename);
                if (file.isDirectory()) {
                    if (this.verbose) {
                        subdirPath = file.getPath();
                        this.log("Entering directory: " + subdirPath + "\n", 2);
                    }
                    if (this.genericantfile != null) {
                        directory = file;
                        file = this.genericantfile;
                    } else {
                        file = new File(file, this.antfile);
                    }
                }
                this.execute(file, directory);
                if (this.verbose && subdirPath != null) {
                    this.log("Leaving directory: " + subdirPath + "\n", 2);
                }
            }
            catch (RuntimeException ex) {
                if (!this.getProject().isKeepGoingMode()) {
                    if (this.verbose && subdirPath != null) {
                        this.log("Leaving directory: " + subdirPath + "\n", 2);
                    }
                    throw ex;
                }
                thrownException = ex;
            }
            catch (Throwable ex) {
                if (!this.getProject().isKeepGoingMode()) {
                    if (this.verbose && subdirPath != null) {
                        this.log("Leaving directory: " + subdirPath + "\n", 2);
                    }
                    throw new BuildException(ex);
                }
                thrownException = ex;
            }
            if (thrownException == null) continue;
            if (thrownException instanceof BuildException) {
                this.log("File '" + file + "' failed with message '" + thrownException.getMessage() + "'.", 0);
                if (buildException == null) {
                    buildException = (BuildException)thrownException;
                }
            } else {
                this.log("Target '" + file + "' failed with message '" + thrownException.getMessage() + "'.", 0);
                this.log(StringUtils.getStackTrace(thrownException), 0);
                if (buildException == null) {
                    buildException = new BuildException(thrownException);
                }
            }
            if (!this.verbose || subdirPath == null) continue;
            this.log("Leaving directory: " + subdirPath + "\n", 2);
        }
        if (buildException != null) {
            throw buildException;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void execute(File file, File directory) throws BuildException {
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            String msg = "Invalid file: " + file;
            if (this.failOnError) {
                throw new BuildException(msg);
            }
            this.log(msg, 1);
            return;
        }
        this.ant = this.createAntTask(directory);
        String antfilename = file.getAbsolutePath();
        this.ant.setAntfile(antfilename);
        this.targets.forEach(this.ant::addConfiguredTarget);
        try {
            if (this.verbose) {
                this.log("Executing: " + antfilename, 2);
            }
            this.ant.execute();
        }
        catch (BuildException e) {
            if (this.failOnError || this.isHardError(e)) {
                throw e;
            }
            this.log("Failure for target '" + this.subTarget + "' of: " + antfilename + "\n" + e.getMessage(), 1);
        }
        catch (Throwable e) {
            if (this.failOnError || this.isHardError(e)) {
                throw new BuildException(e);
            }
            this.log("Failure for target '" + this.subTarget + "' of: " + antfilename + "\n" + e.toString(), 1);
        }
        finally {
            this.ant = null;
        }
    }

    private boolean isHardError(Throwable t) {
        if (t instanceof BuildException) {
            return this.isHardError(t.getCause());
        }
        return t instanceof OutOfMemoryError || t instanceof ThreadDeath;
    }

    public void setAntfile(String antfile) {
        this.antfile = antfile;
    }

    public void setGenericAntfile(File afile) {
        this.genericantfile = afile;
    }

    public void setFailonerror(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void setTarget(String target) {
        this.subTarget = target;
    }

    public void addConfiguredTarget(Ant.TargetElement t) {
        if (t.getName().isEmpty()) {
            throw new BuildException("target name must not be empty");
        }
        this.targets.add(t);
    }

    public void setVerbose(boolean on) {
        this.verbose = on;
    }

    public void setOutput(String s) {
        this.output = s;
    }

    public void setInheritall(boolean b) {
        this.inheritAll = b;
    }

    public void setInheritrefs(boolean b) {
        this.inheritRefs = b;
    }

    public void addProperty(Property p) {
        this.properties.add(p);
    }

    public void addReference(Ant.Reference r) {
        this.references.add(r);
    }

    public void addPropertyset(PropertySet ps) {
        this.propertySets.add(ps);
    }

    public void addDirset(DirSet set) {
        this.add(set);
    }

    public void addFileset(FileSet set) {
        this.add(set);
    }

    public void addFilelist(FileList list) {
        this.add(list);
    }

    public void add(ResourceCollection rc) {
        this.getBuildpath().add(rc);
    }

    public void setBuildpath(Path s) {
        this.getBuildpath().append(s);
    }

    public Path createBuildpath() {
        return this.getBuildpath().createPath();
    }

    public Path.PathElement createBuildpathElement() {
        return this.getBuildpath().createPathElement();
    }

    private Path getBuildpath() {
        if (this.buildpath == null) {
            this.buildpath = new Path(this.getProject());
        }
        return this.buildpath;
    }

    public void setBuildpathRef(Reference r) {
        this.createBuildpath().setRefid(r);
    }

    private Ant createAntTask(File directory) {
        Ant antTask = new Ant(this);
        antTask.init();
        if (this.subTarget != null && !this.subTarget.isEmpty()) {
            antTask.setTarget(this.subTarget);
        }
        if (this.output != null) {
            antTask.setOutput(this.output);
        }
        if (directory != null) {
            antTask.setDir(directory);
        } else {
            antTask.setUseNativeBasedir(true);
        }
        antTask.setInheritAll(this.inheritAll);
        this.properties.forEach(p -> SubAnt.copyProperty(antTask.createProperty(), p));
        this.propertySets.forEach(antTask::addPropertyset);
        antTask.setInheritRefs(this.inheritRefs);
        this.references.forEach(antTask::addReference);
        return antTask;
    }

    private static void copyProperty(Property to, Property from) {
        to.setName(from.getName());
        if (from.getValue() != null) {
            to.setValue(from.getValue());
        }
        if (from.getFile() != null) {
            to.setFile(from.getFile());
        }
        if (from.getResource() != null) {
            to.setResource(from.getResource());
        }
        if (from.getPrefix() != null) {
            to.setPrefix(from.getPrefix());
        }
        if (from.getRefid() != null) {
            to.setRefid(from.getRefid());
        }
        if (from.getEnvironment() != null) {
            to.setEnvironment(from.getEnvironment());
        }
        if (from.getClasspath() != null) {
            to.setClasspath(from.getClasspath());
        }
    }
}

