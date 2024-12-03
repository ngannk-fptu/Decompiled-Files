/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageSessionContext;

public class DefaultImageSessionContext
extends AbstractImageSessionContext {
    private ImageContext context;
    private File baseDir;

    public DefaultImageSessionContext(ImageContext context, File baseDir) {
        this.context = context;
        this.baseDir = baseDir;
    }

    @Override
    public ImageContext getParentContext() {
        return this.context;
    }

    public File getBaseDir() {
        return this.baseDir;
    }

    @Override
    protected Source resolveURI(String uri) {
        try {
            URL url = new URL(uri);
            return new StreamSource(url.openStream(), url.toExternalForm());
        }
        catch (MalformedURLException e) {
            File f = new File(this.baseDir, uri);
            if (f.isFile()) {
                return new StreamSource(f);
            }
            return null;
        }
        catch (IOException ioe) {
            return null;
        }
    }

    @Override
    public float getTargetResolution() {
        return this.getParentContext().getSourceResolution();
    }
}

