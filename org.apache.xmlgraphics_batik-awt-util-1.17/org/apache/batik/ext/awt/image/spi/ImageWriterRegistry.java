/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.Service
 */
package org.apache.batik.ext.awt.image.spi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.util.Service;

public final class ImageWriterRegistry {
    private static ImageWriterRegistry instance;
    private final Map imageWriterMap = new HashMap();

    private ImageWriterRegistry() {
        this.setup();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ImageWriterRegistry getInstance() {
        Class<ImageWriterRegistry> clazz = ImageWriterRegistry.class;
        synchronized (ImageWriterRegistry.class) {
            if (instance == null) {
                instance = new ImageWriterRegistry();
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    private void setup() {
        Iterator iter = Service.providers(ImageWriter.class);
        while (iter.hasNext()) {
            ImageWriter writer = (ImageWriter)iter.next();
            this.register(writer);
        }
    }

    public void register(ImageWriter writer) {
        this.imageWriterMap.put(writer.getMIMEType(), writer);
    }

    public ImageWriter getWriterFor(String mime) {
        return (ImageWriter)this.imageWriterMap.get(mime);
    }
}

