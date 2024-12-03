/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.cvslib;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;
import org.apache.tools.ant.taskdefs.cvslib.CVSEntry;
import org.apache.tools.ant.taskdefs.cvslib.ChangeLogParser;
import org.apache.tools.ant.taskdefs.cvslib.ChangeLogWriter;
import org.apache.tools.ant.taskdefs.cvslib.CvsUser;
import org.apache.tools.ant.taskdefs.cvslib.CvsVersion;
import org.apache.tools.ant.taskdefs.cvslib.RedirectingStreamHandler;
import org.apache.tools.ant.types.FileSet;

public class ChangeLogTask
extends AbstractCvsTask {
    private File usersFile;
    private List<CvsUser> cvsUsers = new Vector<CvsUser>();
    private File inputDir;
    private File destFile;
    private Date startDate;
    private Date endDate;
    private boolean remote = false;
    private String startTag;
    private String endTag;
    private final List<FileSet> filesets = new Vector<FileSet>();

    public void setDir(File inputDir) {
        this.inputDir = inputDir;
    }

    public void setDestfile(File destFile) {
        this.destFile = destFile;
    }

    public void setUsersfile(File usersFile) {
        this.usersFile = usersFile;
    }

    public void addUser(CvsUser user) {
        this.cvsUsers.add(user);
    }

    public void setStart(Date start) {
        this.startDate = start;
    }

    public void setEnd(Date endDate) {
        this.endDate = endDate;
    }

    public void setDaysinpast(int days) {
        long time = System.currentTimeMillis() - (long)days * 24L * 60L * 60L * 1000L;
        this.setStart(new Date(time));
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    public void setStartTag(String start) {
        this.startTag = start;
    }

    public void setEndTag(String end) {
        this.endTag = end;
    }

    public void addFileset(FileSet fileSet) {
        this.filesets.add(fileSet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        File savedDir = this.inputDir;
        try {
            this.validate();
            Properties userList = new Properties();
            this.loadUserlist(userList);
            for (CvsUser user : this.cvsUsers) {
                user.validate();
                userList.put(user.getUserID(), user.getDisplayname());
            }
            if (!this.remote) {
                this.setCommand("log");
                if (this.getTag() != null) {
                    CvsVersion myCvsVersion = new CvsVersion();
                    myCvsVersion.setProject(this.getProject());
                    myCvsVersion.setTaskName("cvsversion");
                    myCvsVersion.setCvsRoot(this.getCvsRoot());
                    myCvsVersion.setCvsRsh(this.getCvsRsh());
                    myCvsVersion.setPassfile(this.getPassFile());
                    myCvsVersion.setDest(this.inputDir);
                    myCvsVersion.execute();
                    if (myCvsVersion.supportsCvsLogWithSOption()) {
                        this.addCommandArgument("-S");
                    }
                }
            } else {
                this.setCommand("");
                this.addCommandArgument("rlog");
                this.addCommandArgument("-S");
                this.addCommandArgument("-N");
            }
            if (null != this.startTag || null != this.endTag) {
                String startValue = this.startTag == null ? "" : this.startTag;
                String endValue = this.endTag == null ? "" : this.endTag;
                this.addCommandArgument("-r" + startValue + "::" + endValue);
            } else if (null != this.startDate) {
                SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd");
                String dateRange = ">=" + outputDate.format(this.startDate);
                this.addCommandArgument("-d");
                this.addCommandArgument(dateRange);
            }
            for (FileSet fileSet : this.filesets) {
                DirectoryScanner scanner = fileSet.getDirectoryScanner(this.getProject());
                for (String file : scanner.getIncludedFiles()) {
                    this.addCommandArgument(file);
                }
            }
            ChangeLogParser parser = new ChangeLogParser(this.remote, this.getPackage(), this.getModules());
            RedirectingStreamHandler handler = new RedirectingStreamHandler(parser);
            this.log(this.getCommand(), 3);
            this.setDest(this.inputDir);
            this.setExecuteStreamHandler(handler);
            try {
                super.execute();
            }
            finally {
                String errors = handler.getErrors();
                if (null != errors) {
                    this.log(errors, 0);
                }
            }
            CVSEntry[] entrySet = parser.getEntrySetAsArray();
            CVSEntry[] filteredEntrySet = this.filterEntrySet(entrySet);
            this.replaceAuthorIdWithName(userList, filteredEntrySet);
            this.writeChangeLog(filteredEntrySet);
        }
        finally {
            this.inputDir = savedDir;
        }
    }

    private void validate() throws BuildException {
        if (null == this.inputDir) {
            this.inputDir = this.getProject().getBaseDir();
        }
        if (null == this.destFile) {
            throw new BuildException("Destfile must be set.");
        }
        if (!this.inputDir.exists()) {
            throw new BuildException("Cannot find base dir %s", this.inputDir.getAbsolutePath());
        }
        if (null != this.usersFile && !this.usersFile.exists()) {
            throw new BuildException("Cannot find user lookup list %s", this.usersFile.getAbsolutePath());
        }
        if (!(null == this.startTag && null == this.endTag || null == this.startDate && null == this.endDate)) {
            throw new BuildException("Specify either a tag or date range, not both");
        }
    }

    private void loadUserlist(Properties userList) throws BuildException {
        if (null != this.usersFile) {
            try {
                userList.load(Files.newInputStream(this.usersFile.toPath(), new OpenOption[0]));
            }
            catch (IOException ioe) {
                throw new BuildException(ioe.toString(), ioe);
            }
        }
    }

    private CVSEntry[] filterEntrySet(CVSEntry[] entrySet) {
        ArrayList<CVSEntry> results = new ArrayList<CVSEntry>();
        for (CVSEntry cvsEntry : entrySet) {
            Date date = cvsEntry.getDate();
            if (null == date || null != this.startDate && this.startDate.after(date) || null != this.endDate && this.endDate.before(date)) continue;
            results.add(cvsEntry);
        }
        return results.toArray(new CVSEntry[0]);
    }

    private void replaceAuthorIdWithName(Properties userList, CVSEntry[] entrySet) {
        for (CVSEntry entry : entrySet) {
            if (!userList.containsKey(entry.getAuthor())) continue;
            entry.setAuthor(userList.getProperty(entry.getAuthor()));
        }
    }

    private void writeChangeLog(CVSEntry[] entrySet) throws BuildException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(this.destFile.toPath(), new OpenOption[0]), StandardCharsets.UTF_8));){
            new ChangeLogWriter().printChangeLog(writer, entrySet);
            if (writer.checkError()) {
                throw new IOException("Encountered an error writing changelog");
            }
        }
        catch (UnsupportedEncodingException uee) {
            this.getProject().log(uee.toString(), 0);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.toString(), ioe);
        }
    }
}

