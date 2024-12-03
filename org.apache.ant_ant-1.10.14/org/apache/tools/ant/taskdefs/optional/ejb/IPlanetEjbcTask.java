/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc;
import org.apache.tools.ant.types.Path;
import org.xml.sax.SAXException;

public class IPlanetEjbcTask
extends Task {
    private File ejbdescriptor;
    private File iasdescriptor;
    private File dest;
    private Path classpath;
    private boolean keepgenerated = false;
    private boolean debug = false;
    private File iashome;

    public void setEjbdescriptor(File ejbdescriptor) {
        this.ejbdescriptor = ejbdescriptor;
    }

    public void setIasdescriptor(File iasdescriptor) {
        this.iasdescriptor = iasdescriptor;
    }

    public void setDest(File dest) {
        this.dest = dest;
    }

    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public void setKeepgenerated(boolean keepgenerated) {
        this.keepgenerated = keepgenerated;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setIashome(File iashome) {
        this.iashome = iashome;
    }

    @Override
    public void execute() throws BuildException {
        this.checkConfiguration();
        this.executeEjbc(this.getParser());
    }

    private void checkConfiguration() throws BuildException {
        if (this.ejbdescriptor == null) {
            String msg = "The standard EJB descriptor must be specified using the \"ejbdescriptor\" attribute.";
            throw new BuildException(msg, this.getLocation());
        }
        if (!this.ejbdescriptor.exists() || !this.ejbdescriptor.isFile()) {
            String msg = "The standard EJB descriptor (" + this.ejbdescriptor + ") was not found or isn't a file.";
            throw new BuildException(msg, this.getLocation());
        }
        if (this.iasdescriptor == null) {
            String msg = "The iAS-speific XML descriptor must be specified using the \"iasdescriptor\" attribute.";
            throw new BuildException(msg, this.getLocation());
        }
        if (!this.iasdescriptor.exists() || !this.iasdescriptor.isFile()) {
            String msg = "The iAS-specific XML descriptor (" + this.iasdescriptor + ") was not found or isn't a file.";
            throw new BuildException(msg, this.getLocation());
        }
        if (this.dest == null) {
            String msg = "The destination directory must be specified using the \"dest\" attribute.";
            throw new BuildException(msg, this.getLocation());
        }
        if (!this.dest.exists() || !this.dest.isDirectory()) {
            String msg = "The destination directory (" + this.dest + ") was not found or isn't a directory.";
            throw new BuildException(msg, this.getLocation());
        }
        if (this.iashome != null && !this.iashome.isDirectory()) {
            String msg = "If \"iashome\" is specified, it must be a valid directory (it was set to " + this.iashome + ").";
            throw new BuildException(msg, this.getLocation());
        }
    }

    private SAXParser getParser() throws BuildException {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setValidating(true);
            return saxParserFactory.newSAXParser();
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new BuildException("Unable to create a SAXParser: " + e.getMessage(), e, this.getLocation());
        }
    }

    private void executeEjbc(SAXParser saxParser) throws BuildException {
        IPlanetEjbc ejbc = new IPlanetEjbc(this.ejbdescriptor, this.iasdescriptor, this.dest, this.getClasspath().toString(), saxParser);
        ejbc.setRetainSource(this.keepgenerated);
        ejbc.setDebugOutput(this.debug);
        if (this.iashome != null) {
            ejbc.setIasHomeDir(this.iashome);
        }
        try {
            ejbc.execute();
        }
        catch (IOException e) {
            throw new BuildException("An IOException occurred while trying to read the XML descriptor file: " + e.getMessage(), e, this.getLocation());
        }
        catch (SAXException e) {
            throw new BuildException("A SAXException occurred while trying to read the XML descriptor file: " + e.getMessage(), e, this.getLocation());
        }
        catch (IPlanetEjbc.EjbcException e) {
            throw new BuildException("An exception occurred while trying to run the ejbc utility: " + e.getMessage(), e, this.getLocation());
        }
    }

    private Path getClasspath() {
        if (this.classpath == null) {
            return new Path(this.getProject()).concatSystemClasspath("last");
        }
        return this.classpath.concatSystemClasspath("ignore");
    }
}

