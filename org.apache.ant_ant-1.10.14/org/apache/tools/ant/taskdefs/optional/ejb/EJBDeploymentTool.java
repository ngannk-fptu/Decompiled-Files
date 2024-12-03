/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;

public interface EJBDeploymentTool {
    public void processDescriptor(String var1, SAXParser var2) throws BuildException;

    public void validateConfigured() throws BuildException;

    public void setTask(Task var1);

    public void configure(EjbJar.Config var1);
}

