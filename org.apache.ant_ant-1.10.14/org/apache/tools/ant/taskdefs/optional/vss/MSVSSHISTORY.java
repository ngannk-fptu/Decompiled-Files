/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.vss;

import java.io.File;
import java.text.SimpleDateFormat;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSS;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class MSVSSHISTORY
extends MSVSS {
    @Override
    Commandline buildCmdLine() {
        Commandline commandLine = new Commandline();
        if (this.getVsspath() == null) {
            String msg = "vsspath attribute must be set!";
            throw new BuildException(msg, this.getLocation());
        }
        commandLine.setExecutable(this.getSSCommand());
        commandLine.createArgument().setValue("History");
        commandLine.createArgument().setValue(this.getVsspath());
        commandLine.createArgument().setValue("-I-");
        commandLine.createArgument().setValue(this.getVersionDate());
        commandLine.createArgument().setValue(this.getVersionLabel());
        commandLine.createArgument().setValue(this.getRecursive());
        commandLine.createArgument().setValue(this.getStyle());
        commandLine.createArgument().setValue(this.getLogin());
        commandLine.createArgument().setValue(this.getOutput());
        return commandLine;
    }

    public void setRecursive(boolean recursive) {
        super.setInternalRecursive(recursive);
    }

    public void setUser(String user) {
        super.setInternalUser(user);
    }

    public void setFromDate(String fromDate) {
        super.setInternalFromDate(fromDate);
    }

    public void setToDate(String toDate) {
        super.setInternalToDate(toDate);
    }

    public void setFromLabel(String fromLabel) {
        super.setInternalFromLabel(fromLabel);
    }

    public void setToLabel(String toLabel) {
        super.setInternalToLabel(toLabel);
    }

    public void setNumdays(int numd) {
        super.setInternalNumDays(numd);
    }

    public void setOutput(File outfile) {
        if (outfile != null) {
            super.setInternalOutputFilename(outfile.getAbsolutePath());
        }
    }

    public void setDateFormat(String dateFormat) {
        super.setInternalDateFormat(new SimpleDateFormat(dateFormat));
    }

    public void setStyle(BriefCodediffNofile attr) {
        String option;
        switch (option = attr.getValue()) {
            case "brief": {
                super.setInternalStyle("-B");
                break;
            }
            case "codediff": {
                super.setInternalStyle("-D");
                break;
            }
            case "default": {
                super.setInternalStyle("");
                break;
            }
            case "nofile": {
                super.setInternalStyle("-F-");
                break;
            }
            default: {
                throw new BuildException("Style " + attr + " unknown.", this.getLocation());
            }
        }
    }

    public static class BriefCodediffNofile
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"brief", "codediff", "nofile", "default"};
        }
    }
}

