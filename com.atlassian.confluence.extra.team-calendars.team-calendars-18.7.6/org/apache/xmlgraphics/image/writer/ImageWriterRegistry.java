/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.writer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import org.apache.xmlgraphics.image.writer.ImageWriter;
import org.apache.xmlgraphics.util.Service;

public final class ImageWriterRegistry {
    private static volatile ImageWriterRegistry instance;
    private Map<String, List<ImageWriter>> imageWriterMap = new HashMap<String, List<ImageWriter>>();
    private Map<String, Integer> preferredOrder;

    public ImageWriterRegistry() {
        Properties props = new Properties();
        InputStream in = this.getClass().getResourceAsStream("default-preferred-order.properties");
        if (in != null) {
            try {
                try {
                    props.load(in);
                }
                finally {
                    in.close();
                }
            }
            catch (IOException ioe) {
                throw new RuntimeException("Could not load default preferred order due to I/O error: " + ioe.getMessage());
            }
        }
        this.setPreferredOrder(props);
        this.setup();
    }

    public ImageWriterRegistry(Properties preferredOrder) {
        this.setPreferredOrder(preferredOrder);
        this.setup();
    }

    private void setPreferredOrder(Properties preferredOrder) {
        HashMap<String, Integer> order = new HashMap<String, Integer>();
        for (Map.Entry<Object, Object> entry : preferredOrder.entrySet()) {
            order.put(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString()));
        }
        this.preferredOrder = order;
    }

    public static ImageWriterRegistry getInstance() {
        if (instance == null) {
            instance = new ImageWriterRegistry();
        }
        return instance;
    }

    private void setup() {
        Iterator<Object> iter = Service.providers(ImageWriter.class);
        while (iter.hasNext()) {
            ImageWriter writer = (ImageWriter)iter.next();
            this.register(writer);
        }
    }

    private int getPriority(ImageWriter writer) {
        int pos;
        String key = writer.getClass().getName();
        Integer value = this.preferredOrder.get(key);
        while (value == null && (pos = key.lastIndexOf(".")) >= 0) {
            key = key.substring(0, pos);
            value = this.preferredOrder.get(key);
        }
        return value != null ? value : 0;
    }

    public void register(ImageWriter writer, int priority) {
        String key = writer.getClass().getName();
        this.preferredOrder.put(key, priority);
        this.register(writer);
    }

    public synchronized void register(ImageWriter writer) {
        List<ImageWriter> entries = this.imageWriterMap.get(writer.getMIMEType());
        if (entries == null) {
            entries = new ArrayList<ImageWriter>();
            this.imageWriterMap.put(writer.getMIMEType(), entries);
        }
        int priority = this.getPriority(writer);
        ListIterator<ImageWriter> li = entries.listIterator();
        while (li.hasNext()) {
            ImageWriter w = li.next();
            if (this.getPriority(w) >= priority) continue;
            li.previous();
            break;
        }
        li.add(writer);
    }

    public synchronized ImageWriter getWriterFor(String mime) {
        List<ImageWriter> entries = this.imageWriterMap.get(mime);
        if (entries == null) {
            return null;
        }
        for (ImageWriter writer : entries) {
            if (!writer.isFunctional()) continue;
            return writer;
        }
        return null;
    }
}

