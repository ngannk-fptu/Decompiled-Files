/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import java.util.Set;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.NamedSPILoader;

public abstract class PostingsFormat
implements NamedSPILoader.NamedSPI {
    private static final NamedSPILoader<PostingsFormat> loader = new NamedSPILoader<PostingsFormat>(PostingsFormat.class);
    public static final PostingsFormat[] EMPTY = new PostingsFormat[0];
    private final String name;

    protected PostingsFormat(String name) {
        NamedSPILoader.checkServiceName(name);
        this.name = name;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    public abstract FieldsConsumer fieldsConsumer(SegmentWriteState var1) throws IOException;

    public abstract FieldsProducer fieldsProducer(SegmentReadState var1) throws IOException;

    public String toString() {
        return "PostingsFormat(name=" + this.name + ")";
    }

    public static PostingsFormat forName(String name) {
        if (loader == null) {
            throw new IllegalStateException("You called PostingsFormat.forName() before all formats could be initialized. This likely happens if you call it from a PostingsFormat's ctor.");
        }
        return loader.lookup(name);
    }

    public static Set<String> availablePostingsFormats() {
        if (loader == null) {
            throw new IllegalStateException("You called PostingsFormat.availablePostingsFormats() before all formats could be initialized. This likely happens if you call it from a PostingsFormat's ctor.");
        }
        return loader.availableServices();
    }

    public static void reloadPostingsFormats(ClassLoader classloader) {
        loader.reload(classloader);
    }
}

