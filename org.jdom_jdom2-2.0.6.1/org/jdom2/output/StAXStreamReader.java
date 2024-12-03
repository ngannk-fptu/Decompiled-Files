/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output;

import javax.xml.stream.XMLStreamReader;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.support.AbstractStAXStreamReaderProcessor;
import org.jdom2.output.support.StAXStreamReaderProcessor;

public final class StAXStreamReader
implements Cloneable {
    private static final DefaultStAXAsStreamProcessor DEFAULTPROCESSOR = new DefaultStAXAsStreamProcessor();
    private Format myFormat = null;
    private StAXStreamReaderProcessor myProcessor = null;

    public StAXStreamReader(Format format, StAXStreamReaderProcessor processor) {
        this.myFormat = format == null ? Format.getRawFormat() : format.clone();
        this.myProcessor = processor == null ? DEFAULTPROCESSOR : processor;
    }

    public StAXStreamReader() {
        this(null, null);
    }

    public StAXStreamReader(StAXStreamReader that) {
        this(that.myFormat, null);
    }

    public StAXStreamReader(Format format) {
        this(format, null);
    }

    public StAXStreamReader(StAXStreamReaderProcessor processor) {
        this(null, processor);
    }

    public void setFormat(Format newFormat) {
        this.myFormat = newFormat.clone();
    }

    public Format getFormat() {
        return this.myFormat;
    }

    public StAXStreamReaderProcessor getStAXAsStreamProcessor() {
        return this.myProcessor;
    }

    public void setStAXAsStreamProcessor(StAXStreamReaderProcessor processor) {
        this.myProcessor = processor;
    }

    public final XMLStreamReader output(Document doc) {
        return this.myProcessor.buildReader(doc, this.myFormat.clone());
    }

    public StAXStreamReader clone() {
        try {
            return (StAXStreamReader)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Unexpected CloneNotSupportedException", e);
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("StAXStreamReader[omitDeclaration = ");
        buffer.append(this.myFormat.omitDeclaration);
        buffer.append(", ");
        buffer.append("encoding = ");
        buffer.append(this.myFormat.encoding);
        buffer.append(", ");
        buffer.append("omitEncoding = ");
        buffer.append(this.myFormat.omitEncoding);
        buffer.append(", ");
        buffer.append("indent = '");
        buffer.append(this.myFormat.indent);
        buffer.append("'");
        buffer.append(", ");
        buffer.append("expandEmptyElements = ");
        buffer.append(this.myFormat.expandEmptyElements);
        buffer.append(", ");
        buffer.append("lineSeparator = '");
        block5: for (char ch : this.myFormat.lineSeparator.toCharArray()) {
            switch (ch) {
                case '\r': {
                    buffer.append("\\r");
                    continue block5;
                }
                case '\n': {
                    buffer.append("\\n");
                    continue block5;
                }
                case '\t': {
                    buffer.append("\\t");
                    continue block5;
                }
                default: {
                    buffer.append("[" + ch + "]");
                }
            }
        }
        buffer.append("', ");
        buffer.append("textMode = ");
        buffer.append((Object)((Object)this.myFormat.mode) + "]");
        return buffer.toString();
    }

    private static final class DefaultStAXAsStreamProcessor
    extends AbstractStAXStreamReaderProcessor {
        private DefaultStAXAsStreamProcessor() {
        }
    }
}

