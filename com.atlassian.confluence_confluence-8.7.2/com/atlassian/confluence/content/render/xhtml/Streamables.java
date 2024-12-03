/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Effect
 *  com.google.common.collect.Streams
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterCallback;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.util.io.CharacterCountingWriter;
import com.atlassian.fugue.Effect;
import com.google.common.collect.Streams;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.xml.stream.XMLStreamException;

public class Streamables {
    private static final EmptyStreamable EMPTY = new EmptyStreamable();

    public static Streamable from(String source) {
        if (source == null) {
            return null;
        }
        if (source.length() == 0) {
            return EMPTY;
        }
        return new StringStreamable(source);
    }

    public static Streamable combine(Streamable ... streamables) {
        if (streamables.length == 0) {
            return EMPTY;
        }
        if (streamables.length == 1) {
            return streamables[0];
        }
        return new CompositeStreamable(Streamables.defensivelyCopy(streamables));
    }

    public static Streamable combine(Iterable<Streamable> streamables) {
        return Streamables.combine((Streamable[])Streams.stream(streamables).toArray(Streamable[]::new));
    }

    public static Streamable from(XmlStreamWriterTemplate template, XmlStreamWriterCallback callback) {
        return new XmlStreamWriterTemplateStreamable(template, callback);
    }

    public static Streamable empty() {
        return EMPTY;
    }

    public static String writeToString(Streamable streamable) {
        if (streamable == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter();
            streamable.writeTo(writer);
            return writer.toString();
        }
        catch (IOException e) {
            throw new RuntimeException("Unexpected exception writing to stringwriter. " + e, e);
        }
    }

    private static Streamable[] defensivelyCopy(Streamable[] streamables) {
        Streamable[] defensiveCopy = new Streamable[streamables.length];
        System.arraycopy(streamables, 0, defensiveCopy, 0, streamables.length);
        return defensiveCopy;
    }

    @Deprecated
    public static Streamable withCharacterCounting(Streamable delegate, Effect<Long> characterCountCallback) {
        return Streamables.withCountingCharacters(delegate, arg_0 -> characterCountCallback.apply(arg_0));
    }

    public static Streamable withCountingCharacters(Streamable delegate, Consumer<Long> characterCountCallback) {
        return writer -> {
            CharacterCountingWriter countingWriter = new CharacterCountingWriter(writer);
            delegate.writeTo(countingWriter);
            characterCountCallback.accept(countingWriter.getCharacterCount());
        };
    }

    private static final class XmlStreamWriterTemplateStreamable
    implements Streamable {
        private final XmlStreamWriterTemplate template;
        private final XmlStreamWriterCallback callback;

        private XmlStreamWriterTemplateStreamable(XmlStreamWriterTemplate template, XmlStreamWriterCallback callback) {
            this.template = template;
            this.callback = callback;
        }

        @Override
        public void writeTo(Writer writer) throws IOException {
            try {
                this.template.execute(writer, this.callback);
            }
            catch (XMLStreamException e) {
                throw new IOException("Error while processing callback: " + e, e);
            }
        }
    }

    private static final class CompositeStreamable
    implements Streamable {
        private final Streamable[] streamables;

        private CompositeStreamable(Streamable[] streamables) {
            this.streamables = streamables;
        }

        @Override
        public void writeTo(Writer writer) throws IOException {
            for (Streamable streamable : this.streamables) {
                streamable.writeTo(writer);
            }
        }

        public String toString() {
            return "CompositeStreamable{" + Arrays.toString(this.streamables) + "}";
        }
    }

    private static class StringStreamable
    implements Streamable {
        private final String source;

        private StringStreamable(String source) {
            this.source = source;
        }

        @Override
        public void writeTo(Writer writer) throws IOException {
            writer.write(this.source);
        }

        public String toString() {
            return "StringStreamable{" + this.source + "}";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            StringStreamable that = (StringStreamable)o;
            return !(this.source != null ? !this.source.equals(that.source) : that.source != null);
        }

        public int hashCode() {
            return this.source != null ? this.source.hashCode() : 0;
        }
    }

    private static class EmptyStreamable
    implements Streamable {
        private EmptyStreamable() {
        }

        @Override
        public void writeTo(Writer writer) throws IOException {
        }

        public String toString() {
            return "EmptyStreamable{}";
        }
    }
}

