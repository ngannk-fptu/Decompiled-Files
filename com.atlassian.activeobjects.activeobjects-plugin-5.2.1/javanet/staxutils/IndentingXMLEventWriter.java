/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.regex.Pattern;
import javanet.staxutils.Indentation;
import javanet.staxutils.IndentingXMLStreamWriter;
import javanet.staxutils.events.AbstractCharactersEvent;
import javanet.staxutils.helpers.EventWriterDelegate;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class IndentingXMLEventWriter
extends EventWriterDelegate
implements Indentation {
    private int depth = 0;
    private int[] stack = new int[]{0, 0, 0, 0};
    private static final int WROTE_MARKUP = 1;
    private static final int WROTE_DATA = 2;
    private final PrefixCharacters newLineEvent = new PrefixCharacters();

    public IndentingXMLEventWriter(XMLEventWriter out) {
        super(out);
    }

    public void setIndent(String indent) {
        this.newLineEvent.setIndent(indent);
    }

    public void setNewLine(String newLine) {
        this.newLineEvent.setNewLine(newLine);
    }

    public String getIndent() {
        return this.newLineEvent.getIndent();
    }

    public String getNewLine() {
        return this.newLineEvent.getNewLine();
    }

    public static String getLineSeparator() {
        return IndentingXMLStreamWriter.getLineSeparator();
    }

    public void add(XMLEvent event) throws XMLStreamException {
        switch (event.getEventType()) {
            case 4: 
            case 6: 
            case 12: {
                this.out.add(event);
                this.afterData();
                return;
            }
            case 1: {
                this.beforeStartElement();
                this.out.add(event);
                this.afterStartElement();
                return;
            }
            case 2: {
                this.beforeEndElement();
                this.out.add(event);
                this.afterEndElement();
                return;
            }
            case 3: 
            case 5: 
            case 7: 
            case 11: {
                this.beforeMarkup();
                this.out.add(event);
                this.afterMarkup();
                return;
            }
            case 8: {
                this.out.add(event);
                this.afterEndDocument();
                break;
            }
            default: {
                this.out.add(event);
                return;
            }
        }
    }

    protected void beforeMarkup() {
        int soFar = this.stack[this.depth];
        if ((soFar & 2) == 0 && (this.depth > 0 || soFar != 0)) {
            try {
                this.newLineEvent.write(this.out, this.depth);
                if (this.depth > 0 && this.getIndent().length() > 0) {
                    this.afterMarkup();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void afterMarkup() {
        int n = this.depth;
        this.stack[n] = this.stack[n] | 1;
    }

    protected void afterData() {
        int n = this.depth;
        this.stack[n] = this.stack[n] | 2;
    }

    protected void beforeStartElement() {
        this.beforeMarkup();
        if (this.stack.length <= this.depth + 1) {
            int[] newWrote = new int[this.stack.length * 2];
            System.arraycopy(this.stack, 0, newWrote, 0, this.stack.length);
            this.stack = newWrote;
        }
        this.stack[this.depth + 1] = 0;
    }

    protected void afterStartElement() {
        this.afterMarkup();
        ++this.depth;
    }

    protected void beforeEndElement() {
        if (this.depth > 0 && this.stack[this.depth] == 1) {
            try {
                this.newLineEvent.write(this.out, this.depth - 1);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void afterEndElement() {
        if (this.depth > 0) {
            --this.depth;
        }
    }

    protected void afterEndDocument() {
        this.depth = 0;
        if (this.stack[0] == 1) {
            try {
                this.newLineEvent.write(this.out, 0);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.stack[0] = 0;
    }

    private static class PrefixCharacters
    extends AbstractCharactersEvent
    implements Indentation {
        private String indent = "  ";
        private String newLine = "\n";
        private final String[] prefixes = new String[]{null, null, null, null, null, null};
        private int minimumPrefix = 0;
        private int depth = 0;
        private static final Pattern ENCODABLE = Pattern.compile("[&<>]");

        PrefixCharacters() {
            super((String)null);
        }

        public String getIndent() {
            return this.indent;
        }

        public String getNewLine() {
            return this.newLine;
        }

        public void setIndent(String indent) {
            if (!indent.equals(this.indent)) {
                Arrays.fill(this.prefixes, null);
            }
            this.indent = indent;
        }

        public void setNewLine(String newLine) {
            if (!newLine.equals(this.newLine)) {
                Arrays.fill(this.prefixes, null);
            }
            this.newLine = newLine;
        }

        void write(XMLEventWriter out, int depth) throws XMLStreamException {
            this.depth = depth;
            out.add(this);
        }

        public String getData() {
            while (this.depth >= this.minimumPrefix + this.prefixes.length) {
                this.prefixes[this.minimumPrefix++ % this.prefixes.length] = null;
            }
            while (this.depth < this.minimumPrefix) {
                this.prefixes[--this.minimumPrefix % this.prefixes.length] = null;
            }
            int p = this.depth % this.prefixes.length;
            String data = this.prefixes[p];
            if (data == null) {
                StringBuffer b = new StringBuffer(this.newLine.length() + this.indent.length() * this.depth);
                b.append(this.newLine);
                for (int d = 0; d < this.depth; ++d) {
                    b.append(this.indent);
                }
                data = this.prefixes[p] = b.toString();
            }
            return data;
        }

        public int getEventType() {
            return 4;
        }

        public Characters asCharacters() {
            return this;
        }

        public boolean isCData() {
            return false;
        }

        public boolean isIgnorableWhiteSpace() {
            return this.isWhiteSpace();
        }

        public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
            try {
                String s = this.getData();
                if (!ENCODABLE.matcher(s).find()) {
                    writer.write(s);
                } else {
                    int first;
                    char[] data = s.toCharArray();
                    block7: for (int d = first = 0; d < data.length; ++d) {
                        switch (data[d]) {
                            case '&': {
                                writer.write(data, first, d - first);
                                writer.write("&amp;");
                                first = d + 1;
                                continue block7;
                            }
                            case '<': {
                                writer.write(data, first, d - first);
                                writer.write("&lt;");
                                first = d + 1;
                                continue block7;
                            }
                            case '>': {
                                writer.write(data, first, d - first);
                                writer.write("&gt;");
                                first = d + 1;
                                continue block7;
                            }
                        }
                    }
                    writer.write(data, first, data.length - first);
                }
            }
            catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }
    }
}

