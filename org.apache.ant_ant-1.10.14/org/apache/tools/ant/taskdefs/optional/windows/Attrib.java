/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.windows;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FileSet;

public class Attrib
extends ExecuteOn {
    private static final String ATTR_READONLY = "R";
    private static final String ATTR_ARCHIVE = "A";
    private static final String ATTR_SYSTEM = "S";
    private static final String ATTR_HIDDEN = "H";
    private static final String SET = "+";
    private static final String UNSET = "-";
    private boolean haveAttr = false;

    public Attrib() {
        super.setExecutable("attrib");
        super.setParallel(false);
    }

    public void setFile(File src) {
        FileSet fs = new FileSet();
        fs.setFile(src);
        this.addFileset(fs);
    }

    public void setReadonly(boolean value) {
        this.addArg(value, ATTR_READONLY);
    }

    public void setArchive(boolean value) {
        this.addArg(value, ATTR_ARCHIVE);
    }

    public void setSystem(boolean value) {
        this.addArg(value, ATTR_SYSTEM);
    }

    public void setHidden(boolean value) {
        this.addArg(value, ATTR_HIDDEN);
    }

    @Override
    protected void checkConfiguration() {
        if (!this.haveAttr()) {
            throw new BuildException("Missing attribute parameter", this.getLocation());
        }
        super.checkConfiguration();
    }

    @Override
    public void setExecutable(String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the executable attribute", this.getLocation());
    }

    public void setCommand(String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the command attribute", this.getLocation());
    }

    @Override
    public void setAddsourcefile(boolean b) {
        throw new BuildException(this.getTaskType() + " doesn't support the addsourcefile attribute", this.getLocation());
    }

    @Override
    public void setSkipEmptyFilesets(boolean skip) {
        throw new BuildException(this.getTaskType() + " doesn't support the skipemptyfileset attribute", this.getLocation());
    }

    @Override
    public void setParallel(boolean parallel) {
        throw new BuildException(this.getTaskType() + " doesn't support the parallel attribute", this.getLocation());
    }

    @Override
    public void setMaxParallel(int max) {
        throw new BuildException(this.getTaskType() + " doesn't support the maxparallel attribute", this.getLocation());
    }

    @Override
    protected boolean isValidOs() {
        return this.getOs() == null && this.getOsFamily() == null ? Os.isFamily("windows") : super.isValidOs();
    }

    private static String getSignString(boolean attr) {
        return attr ? SET : UNSET;
    }

    private void addArg(boolean sign, String attribute) {
        this.createArg().setValue(Attrib.getSignString(sign) + attribute);
        this.haveAttr = true;
    }

    private boolean haveAttr() {
        return this.haveAttr;
    }
}

