/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool;

public class OrionDeploymentTool
extends GenericDeploymentTool {
    protected static final String ORION_DD = "orion-ejb-jar.xml";
    private String jarSuffix = ".jar";

    @Override
    protected void addVendorFiles(Hashtable<String, File> ejbFiles, String baseName) {
        String ddPrefix = this.usingBaseJarName() ? "" : baseName;
        File orionDD = new File(this.getConfig().descriptorDir, ddPrefix + ORION_DD);
        if (orionDD.exists()) {
            ejbFiles.put("META-INF/orion-ejb-jar.xml", orionDD);
        } else {
            this.log("Unable to locate Orion deployment descriptor. It was expected to be in " + orionDD.getPath(), 1);
        }
    }

    @Override
    File getVendorOutputJarFile(String baseName) {
        return new File(this.getDestDir(), baseName + this.jarSuffix);
    }
}

