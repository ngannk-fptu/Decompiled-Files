/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.j2ee;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.j2ee.GenericHotDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool;
import org.apache.tools.ant.types.Path;

public class JonasHotDeploymentTool
extends GenericHotDeploymentTool
implements HotDeploymentTool {
    protected static final String DEFAULT_ORB = "RMI";
    private static final String JONAS_DEPLOY_CLASS_NAME = "org.objectweb.jonas.adm.JonasAdmin";
    private static final String[] VALID_ACTIONS = new String[]{"delete", "deploy", "list", "undeploy", "update"};
    private File jonasroot;
    private String orb = null;
    private String davidHost;
    private int davidPort;

    public void setDavidhost(String inValue) {
        this.davidHost = inValue;
    }

    public void setDavidport(int inValue) {
        this.davidPort = inValue;
    }

    public void setJonasroot(File inValue) {
        this.jonasroot = inValue;
    }

    public void setOrb(String inValue) {
        this.orb = inValue;
    }

    @Override
    public Path getClasspath() {
        Path aClassPath = super.getClasspath();
        if (aClassPath == null) {
            aClassPath = new Path(this.getTask().getProject());
        }
        if (this.orb != null) {
            String aOrbJar = new File(this.jonasroot, "lib/" + this.orb + "_jonas.jar").toString();
            String aConfigDir = new File(this.jonasroot, "config/").toString();
            Path aJOnASOrbPath = new Path(aClassPath.getProject(), aOrbJar + File.pathSeparator + aConfigDir);
            aClassPath.append(aJOnASOrbPath);
        }
        return aClassPath;
    }

    @Override
    public void validateAttributes() throws BuildException {
        Java java = this.getJava();
        String action = this.getTask().getAction();
        if (action == null) {
            throw new BuildException("The \"action\" attribute must be set");
        }
        if (!this.isActionValid()) {
            throw new BuildException("Invalid action \"%s\" passed", action);
        }
        if (this.getClassName() == null) {
            this.setClassName(JONAS_DEPLOY_CLASS_NAME);
        }
        if (this.jonasroot == null || this.jonasroot.isDirectory()) {
            java.createJvmarg().setValue("-Dinstall.root=" + this.jonasroot);
            java.createJvmarg().setValue("-Djava.security.policy=" + this.jonasroot + "/config/java.policy");
            if ("DAVID".equals(this.orb)) {
                java.createJvmarg().setValue("-Dorg.omg.CORBA.ORBClass=org.objectweb.david.libs.binding.orbs.iiop.IIOPORB");
                java.createJvmarg().setValue("-Dorg.omg.CORBA.ORBSingletonClass=org.objectweb.david.libs.binding.orbs.ORBSingletonClass");
                java.createJvmarg().setValue("-Djavax.rmi.CORBA.StubClass=org.objectweb.david.libs.stub_factories.rmi.StubDelegate");
                java.createJvmarg().setValue("-Djavax.rmi.CORBA.PortableRemoteObjectClass=org.objectweb.david.libs.binding.rmi.ORBPortableRemoteObjectDelegate");
                java.createJvmarg().setValue("-Djavax.rmi.CORBA.UtilClass=org.objectweb.david.libs.helpers.RMIUtilDelegate");
                java.createJvmarg().setValue("-Ddavid.CosNaming.default_method=0");
                java.createJvmarg().setValue("-Ddavid.rmi.ValueHandlerClass=com.sun.corba.se.internal.io.ValueHandlerImpl");
                if (this.davidHost != null) {
                    java.createJvmarg().setValue("-Ddavid.CosNaming.default_host=" + this.davidHost);
                }
                if (this.davidPort != 0) {
                    java.createJvmarg().setValue("-Ddavid.CosNaming.default_port=" + this.davidPort);
                }
            }
        }
        if (this.getServer() != null) {
            java.createArg().setLine("-n " + this.getServer());
        }
        if ("deploy".equals(action) || "update".equals(action) || "redeploy".equals(action)) {
            java.createArg().setLine("-a " + this.getTask().getSource());
        } else if (action.equals("delete") || action.equals("undeploy")) {
            java.createArg().setLine("-r " + this.getTask().getSource());
        } else if (action.equals("list")) {
            java.createArg().setValue("-l");
        }
    }

    @Override
    protected boolean isActionValid() {
        String action = this.getTask().getAction();
        for (String validAction : VALID_ACTIONS) {
            if (!action.equals(validAction)) continue;
            return true;
        }
        return false;
    }
}

