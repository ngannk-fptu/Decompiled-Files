/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.webresources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.util.IOTools;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.res.StringManager;

public class ExtractingRoot
extends StandardRoot {
    private static final StringManager sm = StringManager.getManager(ExtractingRoot.class);
    private static final String APPLICATION_JARS_DIR = "application-jars";

    @Override
    protected void processWebInfLib() throws LifecycleException {
        WebResource[] possibleJars;
        if (!super.isPackedWarFile()) {
            super.processWebInfLib();
            return;
        }
        File expansionTarget = this.getExpansionTarget();
        if (!expansionTarget.isDirectory() && !expansionTarget.mkdirs()) {
            throw new LifecycleException(sm.getString("extractingRoot.targetFailed", new Object[]{expansionTarget}));
        }
        for (WebResource possibleJar : possibleJars = this.listResources("/WEB-INF/lib", false)) {
            if (!possibleJar.isFile() || !possibleJar.getName().endsWith(".jar")) continue;
            try {
                File dest = new File(expansionTarget, possibleJar.getName());
                dest = dest.getCanonicalFile();
                try (InputStream sourceStream = possibleJar.getInputStream();
                     FileOutputStream destStream = new FileOutputStream(dest);){
                    IOTools.flow(sourceStream, destStream);
                }
                this.createWebResourceSet(WebResourceRoot.ResourceSetType.CLASSES_JAR, "/WEB-INF/classes", dest.toURI().toURL(), "/");
            }
            catch (IOException ioe) {
                throw new LifecycleException(sm.getString("extractingRoot.jarFailed", new Object[]{possibleJar.getName()}), ioe);
            }
        }
    }

    private File getExpansionTarget() {
        File tmpDir = (File)this.getContext().getServletContext().getAttribute("javax.servlet.context.tempdir");
        File expansionTarget = new File(tmpDir, APPLICATION_JARS_DIR);
        return expansionTarget;
    }

    @Override
    protected boolean isPackedWarFile() {
        return false;
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        if (super.isPackedWarFile()) {
            File expansionTarget = this.getExpansionTarget();
            ExpandWar.delete(expansionTarget);
        }
    }
}

