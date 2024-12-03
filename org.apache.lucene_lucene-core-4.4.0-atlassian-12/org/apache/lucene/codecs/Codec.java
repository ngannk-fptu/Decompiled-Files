/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.util.Set;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.FieldInfosFormat;
import org.apache.lucene.codecs.LiveDocsFormat;
import org.apache.lucene.codecs.NormsFormat;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.SegmentInfoFormat;
import org.apache.lucene.codecs.StoredFieldsFormat;
import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.util.NamedSPILoader;

public abstract class Codec
implements NamedSPILoader.NamedSPI {
    private static final NamedSPILoader<Codec> loader = new NamedSPILoader<Codec>(Codec.class);
    private final String name;
    private static Codec defaultCodec = Codec.forName("Lucene42");

    protected Codec(String name) {
        NamedSPILoader.checkServiceName(name);
        this.name = name;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    public abstract PostingsFormat postingsFormat();

    public abstract DocValuesFormat docValuesFormat();

    public abstract StoredFieldsFormat storedFieldsFormat();

    public abstract TermVectorsFormat termVectorsFormat();

    public abstract FieldInfosFormat fieldInfosFormat();

    public abstract SegmentInfoFormat segmentInfoFormat();

    public abstract NormsFormat normsFormat();

    public abstract LiveDocsFormat liveDocsFormat();

    public static Codec forName(String name) {
        if (loader == null) {
            throw new IllegalStateException("You called Codec.forName() before all Codecs could be initialized. This likely happens if you call it from a Codec's ctor.");
        }
        return loader.lookup(name);
    }

    public static Set<String> availableCodecs() {
        if (loader == null) {
            throw new IllegalStateException("You called Codec.availableCodecs() before all Codecs could be initialized. This likely happens if you call it from a Codec's ctor.");
        }
        return loader.availableServices();
    }

    public static void reloadCodecs(ClassLoader classloader) {
        loader.reload(classloader);
    }

    public static Codec getDefault() {
        return defaultCodec;
    }

    public static void setDefault(Codec codec) {
        defaultCodec = codec;
    }

    public String toString() {
        return this.name;
    }
}

