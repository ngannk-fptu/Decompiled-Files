/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc;
import org.xml.sax.SAXException;

public class IPlanetDeploymentTool
extends GenericDeploymentTool {
    private File iashome;
    private String jarSuffix = ".jar";
    private boolean keepgenerated = false;
    private boolean debug = false;
    private String descriptorName;
    private String iasDescriptorName;
    private String displayName;
    private static final String IAS_DD = "ias-ejb-jar.xml";

    public void setIashome(File iashome) {
        this.iashome = iashome;
    }

    public void setKeepgenerated(boolean keepgenerated) {
        this.keepgenerated = keepgenerated;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setSuffix(String jarSuffix) {
        this.jarSuffix = jarSuffix;
    }

    @Override
    public void setGenericJarSuffix(String inString) {
        this.log("Since a generic JAR file is not created during processing, the iPlanet Deployment Tool does not support the \"genericjarsuffix\" attribute.  It will be ignored.", 1);
    }

    @Override
    public void processDescriptor(String descriptorName, SAXParser saxParser) {
        this.descriptorName = descriptorName;
        this.iasDescriptorName = null;
        this.log("iPlanet Deployment Tool processing: " + descriptorName + " (and " + this.getIasDescriptorName() + ")", 3);
        super.processDescriptor(descriptorName, saxParser);
    }

    @Override
    protected void checkConfiguration(String descriptorFileName, SAXParser saxParser) throws BuildException {
        int startOfName = descriptorFileName.lastIndexOf(File.separatorChar) + 1;
        String stdXml = descriptorFileName.substring(startOfName);
        if (stdXml.equals("ejb-jar.xml") && this.getConfig().baseJarName == null) {
            throw new BuildException("No name specified for the completed JAR file.  The EJB descriptor should be prepended with the JAR name or it should be specified using the attribute \"basejarname\" in the \"ejbjar\" task.", this.getLocation());
        }
        File iasDescriptor = new File(this.getConfig().descriptorDir, this.getIasDescriptorName());
        if (!iasDescriptor.exists() || !iasDescriptor.isFile()) {
            throw new BuildException("The iAS-specific EJB descriptor (" + iasDescriptor + ") was not found.", this.getLocation());
        }
        if (this.iashome != null && !this.iashome.isDirectory()) {
            throw new BuildException("If \"iashome\" is specified, it must be a valid directory (it was set to " + this.iashome + ").", this.getLocation());
        }
    }

    @Override
    protected Hashtable<String, File> parseEjbFiles(String descriptorFileName, SAXParser saxParser) throws IOException, SAXException {
        IPlanetEjbc ejbc = new IPlanetEjbc(new File(this.getConfig().descriptorDir, descriptorFileName), new File(this.getConfig().descriptorDir, this.getIasDescriptorName()), this.getConfig().srcDir, this.getCombinedClasspath().toString(), saxParser);
        ejbc.setRetainSource(this.keepgenerated);
        ejbc.setDebugOutput(this.debug);
        if (this.iashome != null) {
            ejbc.setIasHomeDir(this.iashome);
        }
        if (this.getConfig().dtdLocations != null) {
            for (EjbJar.DTDLocation dtdLocation : this.getConfig().dtdLocations) {
                ejbc.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
            }
        }
        try {
            ejbc.execute();
        }
        catch (IPlanetEjbc.EjbcException e) {
            throw new BuildException("An error has occurred while trying to execute the iAS ejbc utility", e, this.getLocation());
        }
        this.displayName = ejbc.getDisplayName();
        Hashtable<String, File> files = ejbc.getEjbFiles();
        String[] cmpDescriptors = ejbc.getCmpDescriptors();
        if (cmpDescriptors.length > 0) {
            File baseDir = this.getConfig().descriptorDir;
            int endOfPath = descriptorFileName.lastIndexOf(File.separator);
            String relativePath = descriptorFileName.substring(0, endOfPath + 1);
            for (String descriptor : cmpDescriptors) {
                int endOfCmp = descriptor.lastIndexOf(47);
                String cmpDescriptor = descriptor.substring(endOfCmp + 1);
                File cmpFile = new File(baseDir, relativePath + cmpDescriptor);
                if (!cmpFile.exists()) {
                    throw new BuildException("The CMP descriptor file (" + cmpFile + ") could not be found.", this.getLocation());
                }
                files.put(descriptor, cmpFile);
            }
        }
        return files;
    }

    @Override
    protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix) {
        ejbFiles.put("META-INF/ias-ejb-jar.xml", new File(this.getConfig().descriptorDir, this.getIasDescriptorName()));
    }

    @Override
    File getVendorOutputJarFile(String baseName) {
        File jarFile = new File(this.getDestDir(), baseName + this.jarSuffix);
        this.log("JAR file name: " + jarFile.toString(), 3);
        return jarFile;
    }

    @Override
    protected String getPublicId() {
        return null;
    }

    private String getIasDescriptorName() {
        String remainder;
        String basename;
        if (this.iasDescriptorName != null) {
            return this.iasDescriptorName;
        }
        String path = "";
        int startOfFileName = this.descriptorName.lastIndexOf(File.separatorChar);
        if (startOfFileName != -1) {
            path = this.descriptorName.substring(0, startOfFileName + 1);
        }
        if (this.descriptorName.substring(startOfFileName + 1).equals("ejb-jar.xml")) {
            basename = "";
            remainder = "ejb-jar.xml";
        } else {
            int endOfBaseName = this.descriptorName.indexOf(this.getConfig().baseNameTerminator, startOfFileName);
            if (endOfBaseName < 0 && (endOfBaseName = this.descriptorName.lastIndexOf(46) - 1) < 0) {
                endOfBaseName = this.descriptorName.length() - 1;
            }
            basename = this.descriptorName.substring(startOfFileName + 1, endOfBaseName + 1);
            remainder = this.descriptorName.substring(endOfBaseName + 1);
        }
        this.iasDescriptorName = path + basename + "ias-" + remainder;
        return this.iasDescriptorName;
    }
}

