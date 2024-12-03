/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool;

public class JbossDeploymentTool
extends GenericDeploymentTool {
    protected static final String JBOSS_DD = "jboss.xml";
    protected static final String JBOSS_CMP10D = "jaws.xml";
    protected static final String JBOSS_CMP20D = "jbosscmp-jdbc.xml";
    private String jarSuffix = ".jar";

    public void setSuffix(String inString) {
        this.jarSuffix = inString;
    }

    @Override
    protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix) {
        File jbossCMPD;
        File jbossDD = new File(this.getConfig().descriptorDir, ddPrefix + JBOSS_DD);
        if (!jbossDD.exists()) {
            this.log("Unable to locate jboss deployment descriptor. It was expected to be in " + jbossDD.getPath(), 1);
            return;
        }
        ejbFiles.put("META-INF/jboss.xml", jbossDD);
        String descriptorFileName = JBOSS_CMP10D;
        if ("2.0".equals(this.getParent().getCmpversion())) {
            descriptorFileName = JBOSS_CMP20D;
        }
        if ((jbossCMPD = new File(this.getConfig().descriptorDir, ddPrefix + descriptorFileName)).exists()) {
            ejbFiles.put("META-INF/" + descriptorFileName, jbossCMPD);
        } else {
            this.log("Unable to locate jboss cmp descriptor. It was expected to be in " + jbossCMPD.getPath(), 3);
        }
    }

    @Override
    File getVendorOutputJarFile(String baseName) {
        if (this.getDestDir() == null && this.getParent().getDestdir() == null) {
            throw new BuildException("DestDir not specified");
        }
        if (this.getDestDir() == null) {
            return new File(this.getParent().getDestdir(), baseName + this.jarSuffix);
        }
        return new File(this.getDestDir(), baseName + this.jarSuffix);
    }

    @Override
    public void validateConfigured() throws BuildException {
    }

    private EjbJar getParent() {
        return (EjbJar)this.getTask();
    }
}

