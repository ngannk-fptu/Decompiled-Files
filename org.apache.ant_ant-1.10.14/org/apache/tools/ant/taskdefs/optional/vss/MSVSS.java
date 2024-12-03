/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.vss;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.FileUtils;

public abstract class MSVSS
extends Task
implements MSVSSConstants {
    private String ssDir = null;
    private String vssLogin = null;
    private String vssPath = null;
    private String serverPath = null;
    private String version = null;
    private String date = null;
    private String label = null;
    private String autoResponse = null;
    private String localPath = null;
    private String comment = null;
    private String fromLabel = null;
    private String toLabel = null;
    private String outputFileName = null;
    private String user = null;
    private String fromDate = null;
    private String toDate = null;
    private String style = null;
    private boolean quiet = false;
    private boolean recursive = false;
    private boolean writable = false;
    private boolean failOnError = true;
    private boolean getLocalCopy = true;
    private int numDays = Integer.MIN_VALUE;
    private DateFormat dateFormat = DateFormat.getDateInstance(3);
    private CurrentModUpdated timestamp = null;
    private WritableFiles writableFiles = null;

    abstract Commandline buildCmdLine();

    public final void setSsdir(String dir) {
        this.ssDir = FileUtils.translatePath(dir);
    }

    public final void setLogin(String vssLogin) {
        this.vssLogin = vssLogin;
    }

    public final void setVsspath(String vssPath) {
        String projectPath = vssPath.startsWith("vss://") ? vssPath.substring(5) : vssPath;
        this.vssPath = projectPath.startsWith("$") ? projectPath : "$" + projectPath;
    }

    public final void setServerpath(String serverPath) {
        this.serverPath = serverPath;
    }

    public final void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    @Override
    public void execute() throws BuildException {
        Commandline commandLine = this.buildCmdLine();
        int result = this.run(commandLine);
        if (Execute.isFailure(result) && this.getFailOnError()) {
            String msg = "Failed executing: " + this.formatCommandLine(commandLine) + " With a return code of " + result;
            throw new BuildException(msg, this.getLocation());
        }
    }

    protected void setInternalComment(String comment) {
        this.comment = comment;
    }

    protected void setInternalAutoResponse(String autoResponse) {
        this.autoResponse = autoResponse;
    }

    protected void setInternalDate(String date) {
        this.date = date;
    }

    protected void setInternalDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    protected void setInternalFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    protected void setInternalFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    protected void setInternalFromLabel(String fromLabel) {
        this.fromLabel = fromLabel;
    }

    protected void setInternalLabel(String label) {
        this.label = label;
    }

    protected void setInternalLocalPath(String localPath) {
        this.localPath = localPath;
    }

    protected void setInternalNumDays(int numDays) {
        this.numDays = numDays;
    }

    protected void setInternalOutputFilename(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    protected void setInternalQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    protected void setInternalRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    protected void setInternalStyle(String style) {
        this.style = style;
    }

    protected void setInternalToDate(String toDate) {
        this.toDate = toDate;
    }

    protected void setInternalToLabel(String toLabel) {
        this.toLabel = toLabel;
    }

    protected void setInternalUser(String user) {
        this.user = user;
    }

    protected void setInternalVersion(String version) {
        this.version = version;
    }

    protected void setInternalWritable(boolean writable) {
        this.writable = writable;
    }

    protected void setInternalFileTimeStamp(CurrentModUpdated timestamp) {
        this.timestamp = timestamp;
    }

    protected void setInternalWritableFiles(WritableFiles writableFiles) {
        this.writableFiles = writableFiles;
    }

    protected void setInternalGetLocalCopy(boolean getLocalCopy) {
        this.getLocalCopy = getLocalCopy;
    }

    protected String getSSCommand() {
        if (this.ssDir == null) {
            return "ss";
        }
        return this.ssDir.endsWith(File.separator) ? this.ssDir + "ss" : this.ssDir + File.separator + "ss";
    }

    protected String getVsspath() {
        return this.vssPath;
    }

    protected String getQuiet() {
        return this.quiet ? "-O-" : "";
    }

    protected String getRecursive() {
        return this.recursive ? "-R" : "";
    }

    protected String getWritable() {
        return this.writable ? "-W" : "";
    }

    protected String getLabel() {
        String shortLabel = "";
        if (this.label != null && !this.label.isEmpty()) {
            shortLabel = "-L" + this.getShortLabel();
        }
        return shortLabel;
    }

    private String getShortLabel() {
        String shortLabel;
        if (this.label != null && this.label.length() > 31) {
            shortLabel = this.label.substring(0, 30);
            this.log("Label is longer than 31 characters, truncated to: " + shortLabel, 1);
        } else {
            shortLabel = this.label;
        }
        return shortLabel;
    }

    protected String getStyle() {
        return this.style != null ? this.style : "";
    }

    protected String getVersionDateLabel() {
        String versionDateLabel = "";
        if (this.version != null) {
            versionDateLabel = "-V" + this.version;
        } else if (this.date != null) {
            versionDateLabel = "-Vd" + this.date;
        } else {
            String shortLabel = this.getShortLabel();
            if (shortLabel != null && !shortLabel.isEmpty()) {
                versionDateLabel = "-VL" + shortLabel;
            }
        }
        return versionDateLabel;
    }

    protected String getVersion() {
        return this.version != null ? "-V" + this.version : "";
    }

    protected String getLocalpath() {
        String lclPath = "";
        if (this.localPath != null) {
            File dir = this.getProject().resolveFile(this.localPath);
            if (!dir.exists()) {
                boolean done;
                boolean bl = done = dir.mkdirs() || dir.exists();
                if (!done) {
                    String msg = "Directory " + this.localPath + " creation was not successful for an unknown reason";
                    throw new BuildException(msg, this.getLocation());
                }
                this.getProject().log("Created dir: " + dir.getAbsolutePath());
            }
            lclPath = "-GL" + this.localPath;
        }
        return lclPath;
    }

    protected String getComment() {
        return this.comment != null ? "-C" + this.comment : "-C-";
    }

    protected String getAutoresponse() {
        if (this.autoResponse == null) {
            return "-I-";
        }
        if (this.autoResponse.equalsIgnoreCase("Y")) {
            return "-I-Y";
        }
        if (this.autoResponse.equalsIgnoreCase("N")) {
            return "-I-N";
        }
        return "-I-";
    }

    protected String getLogin() {
        return this.vssLogin != null ? "-Y" + this.vssLogin : "";
    }

    protected String getOutput() {
        return this.outputFileName != null ? "-O" + this.outputFileName : "";
    }

    protected String getUser() {
        return this.user != null ? "-U" + this.user : "";
    }

    protected String getVersionLabel() {
        if (this.fromLabel == null && this.toLabel == null) {
            return "";
        }
        if (this.fromLabel != null && this.toLabel != null) {
            if (this.fromLabel.length() > 31) {
                this.fromLabel = this.fromLabel.substring(0, 30);
                this.log("FromLabel is longer than 31 characters, truncated to: " + this.fromLabel, 1);
            }
            if (this.toLabel.length() > 31) {
                this.toLabel = this.toLabel.substring(0, 30);
                this.log("ToLabel is longer than 31 characters, truncated to: " + this.toLabel, 1);
            }
            return "-VL" + this.toLabel + "~L" + this.fromLabel;
        }
        if (this.fromLabel != null) {
            if (this.fromLabel.length() > 31) {
                this.fromLabel = this.fromLabel.substring(0, 30);
                this.log("FromLabel is longer than 31 characters, truncated to: " + this.fromLabel, 1);
            }
            return "-V~L" + this.fromLabel;
        }
        if (this.toLabel.length() > 31) {
            this.toLabel = this.toLabel.substring(0, 30);
            this.log("ToLabel is longer than 31 characters, truncated to: " + this.toLabel, 1);
        }
        return "-VL" + this.toLabel;
    }

    protected String getVersionDate() throws BuildException {
        if (this.fromDate == null && this.toDate == null && this.numDays == Integer.MIN_VALUE) {
            return "";
        }
        if (this.fromDate != null && this.toDate != null) {
            return "-Vd" + this.toDate + "~d" + this.fromDate;
        }
        if (this.toDate != null && this.numDays != Integer.MIN_VALUE) {
            try {
                return "-Vd" + this.toDate + "~d" + this.calcDate(this.toDate, this.numDays);
            }
            catch (ParseException ex) {
                String msg = "Error parsing date: " + this.toDate;
                throw new BuildException(msg, this.getLocation());
            }
        }
        if (this.fromDate != null && this.numDays != Integer.MIN_VALUE) {
            try {
                return "-Vd" + this.calcDate(this.fromDate, this.numDays) + "~d" + this.fromDate;
            }
            catch (ParseException ex) {
                String msg = "Error parsing date: " + this.fromDate;
                throw new BuildException(msg, this.getLocation());
            }
        }
        return this.fromDate != null ? "-V~d" + this.fromDate : "-Vd" + this.toDate;
    }

    protected String getGetLocalCopy() {
        return !this.getLocalCopy ? "-G-" : "";
    }

    private boolean getFailOnError() {
        return !this.getWritableFiles().equals("skip") && this.failOnError;
    }

    public String getFileTimeStamp() {
        if (this.timestamp == null) {
            return "";
        }
        if (this.timestamp.getValue().equals("modified")) {
            return "-GTM";
        }
        if (this.timestamp.getValue().equals("updated")) {
            return "-GTU";
        }
        return "-GTC";
    }

    public String getWritableFiles() {
        if (this.writableFiles == null) {
            return "";
        }
        if (this.writableFiles.getValue().equals("replace")) {
            return "-GWR";
        }
        if (this.writableFiles.getValue().equals("skip")) {
            this.failOnError = false;
            return "-GWS";
        }
        return "";
    }

    private int run(Commandline cmd) {
        try {
            Execute exe = new Execute(new LogStreamHandler(this, 2, 1));
            if (this.serverPath != null) {
                String[] env = exe.getEnvironment();
                if (env == null) {
                    env = new String[]{};
                }
                String[] newEnv = new String[env.length + 1];
                System.arraycopy(env, 0, newEnv, 0, env.length);
                newEnv[env.length] = "SSDIR=" + this.serverPath;
                exe.setEnvironment(newEnv);
            }
            exe.setAntRun(this.getProject());
            exe.setWorkingDirectory(this.getProject().getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            exe.setVMLauncher(false);
            return exe.execute();
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
    }

    private String calcDate(String startDate, int daysToAdd) throws ParseException {
        GregorianCalendar calendar = new GregorianCalendar();
        Date currentDate = this.dateFormat.parse(startDate);
        calendar.setTime(currentDate);
        ((Calendar)calendar).add(5, daysToAdd);
        return this.dateFormat.format(calendar.getTime());
    }

    private String formatCommandLine(Commandline cmd) {
        StringBuilder sBuff = new StringBuilder(cmd.toString());
        int indexUser = sBuff.substring(0).indexOf("-Y");
        if (indexUser > 0) {
            int indexPass = sBuff.substring(0).indexOf(",", indexUser);
            int indexAfterPass = sBuff.substring(0).indexOf(" ", indexPass);
            for (int i = indexPass + 1; i < indexAfterPass; ++i) {
                sBuff.setCharAt(i, '*');
            }
        }
        return sBuff.toString();
    }

    public static class CurrentModUpdated
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"current", "modified", "updated"};
        }
    }

    public static class WritableFiles
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"replace", "skip", "fail"};
        }
    }
}

