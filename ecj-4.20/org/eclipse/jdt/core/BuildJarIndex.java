/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Task
 *  org.eclipse.jdt.core.index.JavaIndexer
 */
package org.eclipse.jdt.core;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.jdt.core.index.JavaIndexer;
import org.eclipse.jdt.internal.antadapter.AntAdapterMessages;

public class BuildJarIndex
extends Task {
    private String jarPath;
    private String indexPath;

    public void execute() throws BuildException {
        if (this.jarPath == null) {
            throw new BuildException(AntAdapterMessages.getString("buildJarIndex.jarFile.cannot.be.null"));
        }
        if (this.indexPath == null) {
            throw new BuildException(AntAdapterMessages.getString("buildJarIndex.indexFile.cannot.be.null"));
        }
        try {
            JavaIndexer.generateIndexForJar((String)this.jarPath, (String)this.indexPath);
        }
        catch (IOException e) {
            throw new BuildException(AntAdapterMessages.getString("buildJarIndex.ioexception.occured", e.getLocalizedMessage()), (Throwable)e);
        }
    }

    public void setJarPath(String path) {
        this.jarPath = path;
    }

    public void setIndexPath(String path) {
        this.indexPath = path;
    }
}

