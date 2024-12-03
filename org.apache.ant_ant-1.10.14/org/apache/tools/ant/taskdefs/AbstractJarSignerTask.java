/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.LineContainsRegExp;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.JavaEnvUtils;

public abstract class AbstractJarSignerTask
extends Task {
    public static final String ERROR_NO_SOURCE = "jar must be set through jar attribute or nested filesets";
    protected static final String JARSIGNER_COMMAND = "jarsigner";
    protected File jar;
    protected String alias;
    protected String keystore;
    protected String storepass;
    protected String storetype;
    protected String keypass;
    protected boolean verbose;
    protected boolean strict = false;
    protected String maxMemory;
    protected Vector<FileSet> filesets = new Vector();
    private RedirectorElement redirector;
    private Environment sysProperties = new Environment();
    private Path path = null;
    private String executable;
    private String providerName;
    private String providerClass;
    private String providerArg;
    private List<Commandline.Argument> additionalArgs = new ArrayList<Commandline.Argument>();

    public void setMaxmemory(String max) {
        this.maxMemory = max;
    }

    public void setJar(File jar) {
        this.jar = jar;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public void setStorepass(String storepass) {
        this.storepass = storepass;
    }

    public void setStoretype(String storetype) {
        this.storetype = storetype;
    }

    public void setKeypass(String keypass) {
        this.keypass = keypass;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public void addFileset(FileSet set) {
        this.filesets.addElement(set);
    }

    public void addSysproperty(Environment.Variable sysp) {
        this.sysProperties.addVariable(sysp);
    }

    public Path createPath() {
        if (this.path == null) {
            this.path = new Path(this.getProject());
        }
        return this.path.createPath();
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setProviderClass(String providerClass) {
        this.providerClass = providerClass;
    }

    public void setProviderArg(String providerArg) {
        this.providerArg = providerArg;
    }

    public void addArg(Commandline.Argument arg) {
        this.additionalArgs.add(arg);
    }

    protected void beginExecution() {
        this.redirector = this.createRedirector();
    }

    protected void endExecution() {
        this.redirector = null;
    }

    private RedirectorElement createRedirector() {
        RedirectorElement result = new RedirectorElement();
        if (this.storepass != null) {
            StringBuilder input = new StringBuilder(this.storepass).append('\n');
            if (this.keypass != null) {
                input.append(this.keypass).append('\n');
            }
            result.setInputString(input.toString());
            result.setLogInputString(false);
            LineContainsRegExp filter = new LineContainsRegExp();
            RegularExpression rx = new RegularExpression();
            rx.setPattern("^(Enter Passphrase for keystore: |Enter key password for .+: )$");
            filter.addConfiguredRegexp(rx);
            filter.setNegate(true);
            result.createErrorFilterChain().addLineContainsRegExp(filter);
        }
        return result;
    }

    public RedirectorElement getRedirector() {
        return this.redirector;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    protected void setCommonOptions(ExecTask cmd) {
        if (this.maxMemory != null) {
            this.addValue(cmd, "-J-Xmx" + this.maxMemory);
        }
        if (this.verbose) {
            this.addValue(cmd, "-verbose");
        }
        if (this.strict) {
            this.addValue(cmd, "-strict");
        }
        for (Environment.Variable variable : this.sysProperties.getVariablesVector()) {
            this.declareSysProperty(cmd, variable);
        }
        for (Commandline.Argument arg : this.additionalArgs) {
            this.addArgument(cmd, arg);
        }
    }

    protected void declareSysProperty(ExecTask cmd, Environment.Variable property) throws BuildException {
        this.addValue(cmd, "-J-D" + property.getContent());
    }

    protected void bindToKeystore(ExecTask cmd) {
        if (null != this.keystore) {
            this.addValue(cmd, "-keystore");
            File keystoreFile = this.getProject().resolveFile(this.keystore);
            String loc = keystoreFile.exists() ? keystoreFile.getPath() : this.keystore;
            this.addValue(cmd, loc);
        }
        if (null != this.storetype) {
            this.addValue(cmd, "-storetype");
            this.addValue(cmd, this.storetype);
        }
        if (null != this.providerName) {
            this.addValue(cmd, "-providerName");
            this.addValue(cmd, this.providerName);
        }
        if (null != this.providerClass) {
            this.addValue(cmd, "-providerClass");
            this.addValue(cmd, this.providerClass);
            if (null != this.providerArg) {
                this.addValue(cmd, "-providerArg");
                this.addValue(cmd, this.providerArg);
            }
        } else if (null != this.providerArg) {
            this.log("Ignoring providerArg as providerClass has not been set");
        }
    }

    protected ExecTask createJarSigner() {
        ExecTask cmd = new ExecTask(this);
        if (this.executable == null) {
            cmd.setExecutable(JavaEnvUtils.getJdkExecutable(JARSIGNER_COMMAND));
        } else {
            cmd.setExecutable(this.executable);
        }
        cmd.setTaskType(JARSIGNER_COMMAND);
        cmd.setFailonerror(true);
        cmd.addConfiguredRedirector(this.redirector);
        return cmd;
    }

    protected Vector<FileSet> createUnifiedSources() {
        Vector<FileSet> sources = new Vector<FileSet>(this.filesets);
        if (this.jar != null) {
            FileSet sourceJar = new FileSet();
            sourceJar.setProject(this.getProject());
            sourceJar.setFile(this.jar);
            sources.add(sourceJar);
        }
        return sources;
    }

    protected Path createUnifiedSourcePath() {
        Path p = this.path == null ? new Path(this.getProject()) : (Path)this.path.clone();
        for (FileSet fileSet : this.createUnifiedSources()) {
            p.add(fileSet);
        }
        return p;
    }

    protected boolean hasResources() {
        return this.path != null || !this.filesets.isEmpty();
    }

    protected void addValue(ExecTask cmd, String value) {
        cmd.createArg().setValue(value);
    }

    protected void addArgument(ExecTask cmd, Commandline.Argument arg) {
        cmd.createArg().copyFrom(arg);
    }
}

