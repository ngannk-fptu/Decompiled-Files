/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Task
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.tuckey.web.filters.urlrewrite.CatchElem;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.Run;
import org.tuckey.web.filters.urlrewrite.Status;
import org.tuckey.web.filters.urlrewrite.utils.Log;

public class UrlRewriteDocTask
extends Task {
    private File conf = new File("/WEB-INF/urlrewrite.xml");
    private File dest = new File("urlrewrite-conf-overview.html");
    private String logLevel = "INFO";

    public void execute() throws BuildException {
        try {
            Log.setLevel("SYSOUT:" + this.logLevel);
            this.show();
        }
        catch (FileNotFoundException e) {
            throw new BuildException((Throwable)e);
        }
        catch (IOException e) {
            throw new BuildException((Throwable)e);
        }
    }

    private void show() throws IOException {
        Run.setLoadClass(false);
        CatchElem.setLoadClass(false);
        Conf confObj = new Conf(new FileInputStream(this.conf), this.conf.getName());
        confObj.initialise();
        if (!confObj.isOk()) {
            throw new BuildException("conf is not ok");
        }
        this.log("loaded fine with " + confObj.getRules().size() + " rules");
        File reportFile = this.dest;
        if (reportFile.exists()) {
            reportFile.delete();
        }
        FileWriter writer = new FileWriter(reportFile);
        Status status = new Status(confObj);
        status.displayStatusOffline();
        writer.write(status.getBuffer().toString());
        writer.close();
    }

    public void setConf(File conf) {
        this.conf = conf;
    }

    public void setDest(File dest) {
        this.dest = dest;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
}

