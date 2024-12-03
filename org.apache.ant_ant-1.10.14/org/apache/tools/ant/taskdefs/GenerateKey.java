/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.JavaEnvUtils;

public class GenerateKey
extends Task {
    protected String alias;
    protected String keystore;
    protected String storepass;
    protected String storetype;
    protected String keypass;
    protected String sigalg;
    protected String keyalg;
    protected String saname;
    protected String dname;
    protected DistinguishedName expandedDname;
    protected int keysize;
    protected int validity;
    protected boolean verbose;

    public DistinguishedName createDname() throws BuildException {
        if (null != this.expandedDname) {
            throw new BuildException("DName sub-element can only be specified once.");
        }
        if (null != this.dname) {
            throw new BuildException("It is not possible to specify dname  both as attribute and element.");
        }
        this.expandedDname = new DistinguishedName();
        return this.expandedDname;
    }

    public void setDname(String dname) {
        if (null != this.expandedDname) {
            throw new BuildException("It is not possible to specify dname  both as attribute and element.");
        }
        this.dname = dname;
    }

    public void setSaname(String saname) {
        this.saname = saname;
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

    public void setSigalg(String sigalg) {
        this.sigalg = sigalg;
    }

    public void setKeyalg(String keyalg) {
        this.keyalg = keyalg;
    }

    public void setKeysize(String keysize) throws BuildException {
        try {
            this.keysize = Integer.parseInt(keysize);
        }
        catch (NumberFormatException nfe) {
            throw new BuildException("KeySize attribute should be a integer");
        }
    }

    public void setValidity(String validity) throws BuildException {
        try {
            this.validity = Integer.parseInt(validity);
        }
        catch (NumberFormatException nfe) {
            throw new BuildException("Validity attribute should be a integer");
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void execute() throws BuildException {
        if (null == this.alias) {
            throw new BuildException("alias attribute must be set");
        }
        if (null == this.storepass) {
            throw new BuildException("storepass attribute must be set");
        }
        if (null == this.dname && null == this.expandedDname) {
            throw new BuildException("dname must be set");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("-genkey ");
        if (this.verbose) {
            sb.append("-v ");
        }
        sb.append("-alias \"");
        sb.append(this.alias);
        sb.append("\" ");
        if (null != this.dname) {
            sb.append("-dname \"");
            sb.append(this.dname);
            sb.append("\" ");
        }
        if (null != this.expandedDname) {
            sb.append("-dname \"");
            sb.append(this.expandedDname);
            sb.append("\" ");
        }
        if (null != this.keystore) {
            sb.append("-keystore \"");
            sb.append(this.keystore);
            sb.append("\" ");
        }
        if (null != this.storepass) {
            sb.append("-storepass \"");
            sb.append(this.storepass);
            sb.append("\" ");
        }
        if (null != this.storetype) {
            sb.append("-storetype \"");
            sb.append(this.storetype);
            sb.append("\" ");
        }
        sb.append("-keypass \"");
        if (null != this.keypass) {
            sb.append(this.keypass);
        } else {
            sb.append(this.storepass);
        }
        sb.append("\" ");
        if (null != this.sigalg) {
            sb.append("-sigalg \"");
            sb.append(this.sigalg);
            sb.append("\" ");
        }
        if (null != this.keyalg) {
            sb.append("-keyalg \"");
            sb.append(this.keyalg);
            sb.append("\" ");
        }
        if (0 < this.keysize) {
            sb.append("-keysize \"");
            sb.append(this.keysize);
            sb.append("\" ");
        }
        if (0 < this.validity) {
            sb.append("-validity \"");
            sb.append(this.validity);
            sb.append("\" ");
        }
        if (null != this.saname) {
            sb.append("-ext ");
            sb.append("\"san=");
            sb.append(this.saname);
            sb.append("\" ");
        }
        this.log("Generating Key for " + this.alias);
        ExecTask cmd = new ExecTask(this);
        cmd.setExecutable(JavaEnvUtils.getJdkExecutable("keytool"));
        Commandline.Argument arg = cmd.createArg();
        arg.setLine(sb.toString());
        cmd.setFailonerror(true);
        cmd.setTaskName(this.getTaskName());
        cmd.execute();
    }

    public static class DistinguishedName {
        private List<DnameParam> params = new Vector<DnameParam>();

        public Object createParam() {
            DnameParam param = new DnameParam();
            this.params.add(param);
            return param;
        }

        public Enumeration<DnameParam> getParams() {
            return Collections.enumeration(this.params);
        }

        public String toString() {
            return this.params.stream().map(p -> this.encode(p.getName()) + "=" + this.encode(p.getValue())).collect(Collectors.joining(", "));
        }

        public String encode(String string) {
            return String.join((CharSequence)"\\,", string.split(","));
        }
    }

    public static class DnameParam {
        private String name;
        private String value;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isComplete() {
            return this.name != null && this.value != null;
        }
    }
}

