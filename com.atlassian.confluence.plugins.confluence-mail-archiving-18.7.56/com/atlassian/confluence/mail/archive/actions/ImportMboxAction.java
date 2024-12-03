/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.StrutsUtil
 *  com.atlassian.confluence.util.io.ConfluenceFileUtils
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskUtils
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.FileUploadUtils$FileUploadException
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.mail.archive.MboxImporter;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.StrutsUtil;
import com.atlassian.confluence.util.io.ConfluenceFileUtils;
import com.atlassian.confluence.util.longrunning.LongRunningTaskUtils;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.FileUploadUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
public class ImportMboxAction
extends AbstractSpaceAdminAction {
    static final long serialVersionUID = 1L;
    public static final Logger log = LoggerFactory.getLogger(ImportMboxAction.class);
    private transient MboxImporter mboxImporter;
    private File uploadedFile;
    private String fileName;
    public boolean nonBackgroundTask;

    public void setMboxImporter(MboxImporter mboxImporter) {
        this.mboxImporter = mboxImporter;
    }

    public void validate() {
        super.validate();
        if (this.getSpace() == null) {
            log.warn("space is null");
            this.addActionError(this.getText("space.doesnt.exist"));
        }
        try {
            if (this.getFile() == null) {
                this.addActionError(this.getText("no.file.uploaded"));
            } else if (!this.validateMboxFile(this.getFile())) {
                this.addActionError(this.getText("invalid.format"));
            }
        }
        catch (FileUploadUtils.FileUploadException e) {
            StrutsUtil.localizeMultipartErrorMessages((FileUploadUtils.FileUploadException)e).forEach(arg_0 -> ((ImportMboxAction)this).addActionError(arg_0));
        }
        catch (IOException e) {
            this.addActionError(this.getText("invalid.file"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean validateMboxFile(File mboxFile) throws IOException {
        boolean bl;
        FileInputStream fis = null;
        BufferedReader in = null;
        try {
            fis = new FileInputStream(mboxFile);
        }
        catch (FileNotFoundException | NullPointerException e) {
            this.addActionError(this.getText("file.inaccessible", new Object[]{GeneralUtil.htmlEncode((String)mboxFile.toString())}));
            boolean bl2 = false;
            IOUtils.closeQuietly((Reader)in);
            IOUtils.closeQuietly((InputStream)fis);
            return bl2;
        }
        try {
            String line;
            InputStreamReader isr = new InputStreamReader((InputStream)fis, "ISO-8859-1");
            in = new BufferedReader(isr);
            boolean found = false;
            while ((line = in.readLine()) != null && !found) {
                if (!line.startsWith("From ")) continue;
                found = true;
            }
            bl = found;
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly((InputStream)fis);
            throw throwable;
        }
        IOUtils.closeQuietly((Reader)in);
        IOUtils.closeQuietly((InputStream)fis);
        return bl;
    }

    public String execute() throws Exception {
        this.mboxImporter.setSpace(this.space);
        this.mboxImporter.setFile(this.getFile());
        if (!this.nonBackgroundTask) {
            LongRunningTaskUtils.startTask((LongRunningTask)this.mboxImporter);
        } else {
            this.mboxImporter.runInternal();
        }
        return "success";
    }

    private File getFile() throws FileUploadUtils.FileUploadException {
        try {
            if (this.uploadedFile == null) {
                this.uploadedFile = FileUploadUtils.getSingleFile();
            }
            if (this.uploadedFile == null && this.fileName != null) {
                File temp = new File(this.fileName);
                String mboxDirPath = System.getProperty("confluence.mbox.directory");
                if (mboxDirPath == null || mboxDirPath.isEmpty()) {
                    log.warn("Mail import directory is not configured, please set the 'confluence.mbox.directory' system property");
                    return null;
                }
                File mboxDir = new File(mboxDirPath);
                if (!ConfluenceFileUtils.isChildOf((File)mboxDir, (File)temp)) {
                    log.warn("Cannot import mail from outside of the configured mbox directory " + mboxDirPath);
                    return null;
                }
                this.uploadedFile = temp;
            }
        }
        catch (ClassCastException exception) {
            log.error("Exception occurred when accessing file", (Throwable)exception);
        }
        return this.uploadedFile;
    }

    public boolean getNonBackgroundTask() {
        return this.nonBackgroundTask;
    }

    public void setNonBackgroundTask(boolean nonBackgroundTask) {
        this.nonBackgroundTask = nonBackgroundTask;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

