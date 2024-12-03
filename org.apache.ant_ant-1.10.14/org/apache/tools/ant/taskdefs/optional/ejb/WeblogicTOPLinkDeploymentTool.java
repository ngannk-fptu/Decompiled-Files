/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.ejb.DescriptorHandler;
import org.apache.tools.ant.taskdefs.optional.ejb.WeblogicDeploymentTool;

public class WeblogicTOPLinkDeploymentTool
extends WeblogicDeploymentTool {
    private static final String TL_DTD_LOC = "http://www.objectpeople.com/tlwl/dtd/toplink-cmp_2_5_1.dtd";
    private String toplinkDescriptor;
    private String toplinkDTD;

    public void setToplinkdescriptor(String inString) {
        this.toplinkDescriptor = inString;
    }

    public void setToplinkdtd(String inString) {
        this.toplinkDTD = inString;
    }

    @Override
    protected DescriptorHandler getDescriptorHandler(File srcDir) {
        DescriptorHandler handler = super.getDescriptorHandler(srcDir);
        if (this.toplinkDTD != null) {
            handler.registerDTD("-//The Object People, Inc.//DTD TOPLink for WebLogic CMP 2.5.1//EN", this.toplinkDTD);
        } else {
            handler.registerDTD("-//The Object People, Inc.//DTD TOPLink for WebLogic CMP 2.5.1//EN", TL_DTD_LOC);
        }
        return handler;
    }

    @Override
    protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix) {
        super.addVendorFiles(ejbFiles, ddPrefix);
        File toplinkDD = new File(this.getConfig().descriptorDir, ddPrefix + this.toplinkDescriptor);
        if (toplinkDD.exists()) {
            ejbFiles.put("META-INF/" + this.toplinkDescriptor, toplinkDD);
        } else {
            this.log("Unable to locate toplink deployment descriptor. It was expected to be in " + toplinkDD.getPath(), 1);
        }
    }

    @Override
    public void validateConfigured() throws BuildException {
        super.validateConfigured();
        if (this.toplinkDescriptor == null) {
            throw new BuildException("The toplinkdescriptor attribute must be specified");
        }
    }
}

