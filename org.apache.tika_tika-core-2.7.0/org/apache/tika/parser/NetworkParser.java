/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.input.CloseShieldInputStream
 */
package org.apache.tika.parser;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.TaggedContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.utils.XMLReaderUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NetworkParser
extends AbstractParser {
    private final URI uri;
    private final Set<MediaType> supportedTypes;

    public NetworkParser(URI uri, Set<MediaType> supportedTypes) {
        this.uri = uri;
        this.supportedTypes = supportedTypes;
    }

    public NetworkParser(URI uri) {
        this(uri, Collections.singleton(MediaType.OCTET_STREAM));
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.supportedTypes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        TemporaryResources tmp = new TemporaryResources();
        try {
            TikaInputStream tis = TikaInputStream.get(stream, tmp, metadata);
            this.parse(tis, handler, metadata, context);
        }
        finally {
            tmp.dispose();
        }
    }

    private void parse(TikaInputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        if ("telnet".equals(this.uri.getScheme())) {
            try (final Socket socket = new Socket(this.uri.getHost(), this.uri.getPort());){
                new ParsingTask(stream, new FilterOutputStream(socket.getOutputStream()){

                    @Override
                    public void close() throws IOException {
                        socket.shutdownOutput();
                    }
                }).parse(socket.getInputStream(), handler, metadata, context);
            }
        }
        URL url = this.uri.toURL();
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.connect();
        try (InputStream input = connection.getInputStream();){
            new ParsingTask(stream, connection.getOutputStream()).parse((InputStream)new CloseShieldInputStream(input), handler, metadata, context);
        }
    }

    private static class MetaHandler
    extends DefaultHandler {
        private final Metadata metadata;

        public MetaHandler(Metadata metadata) {
            this.metadata = metadata;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("http://www.w3.org/1999/xhtml".equals(uri) && "meta".equals(localName)) {
                String name = attributes.getValue("", "name");
                String content = attributes.getValue("", "content");
                if (name != null && content != null) {
                    this.metadata.add(name, content);
                }
            }
        }
    }

    private static class ParsingTask
    implements Runnable {
        private final TikaInputStream input;
        private final OutputStream output;
        private volatile Exception exception = null;

        public ParsingTask(TikaInputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
        }

        public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
            Thread thread = new Thread((Runnable)this, "Tika network parser");
            thread.start();
            TaggedContentHandler tagged = new TaggedContentHandler(handler);
            try {
                XMLReaderUtils.parseSAX(stream, (ContentHandler)new TeeContentHandler(tagged, new MetaHandler(metadata)), context);
            }
            catch (SAXException e) {
                tagged.throwIfCauseOf(e);
                throw new TikaException("Invalid network parser output", e);
            }
            catch (IOException e) {
                throw new TikaException("Unable to read network parser output", e);
            }
            finally {
                try {
                    thread.join(1000L);
                }
                catch (InterruptedException e) {
                    throw new TikaException("Network parser interrupted", e);
                }
                if (this.exception != null) {
                    this.input.throwIfCauseOf(this.exception);
                    throw new TikaException("Unexpected network parser error", this.exception);
                }
            }
        }

        @Override
        public void run() {
            try {
                try {
                    IOUtils.copy((InputStream)((Object)this.input), (OutputStream)this.output);
                }
                finally {
                    this.output.close();
                }
            }
            catch (Exception e) {
                this.exception = e;
            }
        }
    }
}

