/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Globals
 *  org.apache.tomcat.util.descriptor.DigesterFactory
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import org.apache.catalina.Globals;
import org.apache.catalina.ant.BaseRedirectorHelperTask;
import org.apache.tomcat.util.descriptor.DigesterFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tools.ant.BuildException;
import org.xml.sax.InputSource;

public class ValidatorTask
extends BaseRedirectorHelperTask {
    protected String path = null;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute() throws BuildException {
        if (this.path == null) {
            throw new BuildException("Must specify 'path'");
        }
        File file = new File(this.path, "WEB-INF/web.xml");
        if (!file.canRead()) {
            throw new BuildException("Cannot find web.xml");
        }
        Thread currentThread = Thread.currentThread();
        ClassLoader oldCL = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(ValidatorTask.class.getClassLoader());
        Digester digester = DigesterFactory.newDigester((boolean)true, (boolean)true, null, (boolean)Globals.IS_SECURITY_ENABLED);
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file.getCanonicalFile()));){
            InputSource is = new InputSource(file.toURI().toURL().toExternalForm());
            is.setByteStream(stream);
            digester.parse(is);
            this.handleOutput("web.xml validated");
        }
        catch (Exception e) {
            if (this.isFailOnError()) {
                throw new BuildException("Validation failure", (Throwable)e);
            }
            this.handleErrorOutput("Validation failure: " + e);
        }
        finally {
            currentThread.setContextClassLoader(oldCL);
            this.closeRedirector();
        }
    }
}

