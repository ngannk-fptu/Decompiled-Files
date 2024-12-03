/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.util.fs.Revision;
import com.opensymphony.xwork2.util.fs.StrutsJarURLConnection;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JarEntryRevision
extends Revision {
    private static Logger LOG = LogManager.getLogger(JarEntryRevision.class);
    private URL jarFileURL;
    private long lastModified;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Revision build(URL fileUrl, FileManager fileManager) {
        try (StrutsJarURLConnection conn = StrutsJarURLConnection.openConnection(fileUrl);){
            conn.setUseCaches(false);
            URL url = fileManager.normalizeToFileProtocol(fileUrl);
            if (url != null) {
                JarEntryRevision jarEntryRevision = new JarEntryRevision(fileUrl, conn.getJarEntry().getTime());
                return jarEntryRevision;
            }
            Revision revision = null;
            return revision;
        }
        catch (Throwable e) {
            LOG.warn("Could not create JarEntryRevision for [{}]!", (Object)fileUrl, (Object)e);
            return null;
        }
    }

    private JarEntryRevision(URL jarFileURL, long lastModified) {
        if (jarFileURL == null) {
            throw new IllegalArgumentException("jarFileURL cannot be null");
        }
        this.jarFileURL = jarFileURL;
        this.lastModified = lastModified;
    }

    @Override
    public boolean needsReloading() {
        long lastLastModified = this.lastModified;
        try (StrutsJarURLConnection conn = StrutsJarURLConnection.openConnection(this.jarFileURL);){
            conn.setUseCaches(false);
            lastLastModified = conn.getJarEntry().getTime();
        }
        catch (Throwable e) {
            LOG.warn("Could not check if needsReloading for [{}]!", (Object)this.jarFileURL, (Object)e);
        }
        return this.lastModified < lastLastModified;
    }
}

