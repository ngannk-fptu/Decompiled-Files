/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.util.HashMap;
import java.util.Map;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMWriterFactory
implements WriterFactory {
    private final Abdera abdera;
    private final Map<String, NamedWriter> writers;
    private final Map<String, Class<? extends StreamWriter>> streamwriters;

    public FOMWriterFactory() {
        this(new Abdera());
    }

    public FOMWriterFactory(Abdera abdera) {
        this.abdera = abdera;
        HashMap w = this.getAbdera().getConfiguration().getNamedWriters();
        this.writers = w != null ? w : new HashMap();
        HashMap s = this.getAbdera().getConfiguration().getStreamWriters();
        this.streamwriters = s != null ? s : new HashMap();
    }

    protected Abdera getAbdera() {
        return this.abdera;
    }

    @Override
    public <T extends Writer> T getWriter() {
        return (T)this.getAbdera().getWriter();
    }

    @Override
    public <T extends Writer> T getWriter(String name) {
        return (T)(name != null ? (Writer)this.getWriters().get(name.toLowerCase()) : this.getWriter());
    }

    @Override
    public <T extends Writer> T getWriterByMediaType(String mediatype) {
        Map<String, NamedWriter> writers = this.getWriters();
        for (NamedWriter writer : writers.values()) {
            if (!writer.outputsFormat(mediatype)) continue;
            return (T)writer;
        }
        return null;
    }

    private Map<String, NamedWriter> getWriters() {
        return this.writers;
    }

    private Map<String, Class<? extends StreamWriter>> getStreamWriters() {
        return this.streamwriters;
    }

    @Override
    public <T extends StreamWriter> T newStreamWriter() {
        return (T)this.getAbdera().newStreamWriter();
    }

    @Override
    public <T extends StreamWriter> T newStreamWriter(String name) {
        Class<? extends StreamWriter> _class = this.getStreamWriters().get(name);
        StreamWriter sw = null;
        if (_class != null) {
            try {
                sw = _class.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException(Localizer.sprintf("IMPLEMENTATION.NOT.AVAILABLE", "StreamWriter"), e);
            }
        }
        return (T)sw;
    }
}

