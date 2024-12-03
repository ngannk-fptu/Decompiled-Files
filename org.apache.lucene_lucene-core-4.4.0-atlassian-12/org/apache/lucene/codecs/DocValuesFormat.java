/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import java.util.Set;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.NamedSPILoader;

public abstract class DocValuesFormat
implements NamedSPILoader.NamedSPI {
    private static final NamedSPILoader<DocValuesFormat> loader = new NamedSPILoader<DocValuesFormat>(DocValuesFormat.class);
    private final String name;

    protected DocValuesFormat(String name) {
        NamedSPILoader.checkServiceName(name);
        this.name = name;
    }

    public abstract DocValuesConsumer fieldsConsumer(SegmentWriteState var1) throws IOException;

    public abstract DocValuesProducer fieldsProducer(SegmentReadState var1) throws IOException;

    @Override
    public final String getName() {
        return this.name;
    }

    public String toString() {
        return "DocValuesFormat(name=" + this.name + ")";
    }

    public static DocValuesFormat forName(String name) {
        if (loader == null) {
            throw new IllegalStateException("You called DocValuesFormat.forName() before all formats could be initialized. This likely happens if you call it from a DocValuesFormat's ctor.");
        }
        return loader.lookup(name);
    }

    public static Set<String> availableDocValuesFormats() {
        if (loader == null) {
            throw new IllegalStateException("You called DocValuesFormat.availableDocValuesFormats() before all formats could be initialized. This likely happens if you call it from a DocValuesFormat's ctor.");
        }
        return loader.availableServices();
    }

    public static void reloadDocValuesFormats(ClassLoader classloader) {
        loader.reload(classloader);
    }
}

