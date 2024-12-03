/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.CloseShieldInputStream
 */
package org.apache.tika.extractor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.tika.exception.CorruptedFileException;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.DocumentSelector;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.DelegatingParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ParseRecord;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ParsingEmbeddedDocumentExtractor
implements EmbeddedDocumentExtractor {
    private static final File ABSTRACT_PATH = new File("");
    private static final Parser DELEGATING_PARSER = new DelegatingParser();
    private boolean writeFileNameToContent = true;
    private final ParseContext context;

    public ParsingEmbeddedDocumentExtractor(ParseContext context) {
        this.context = context;
    }

    @Override
    public boolean shouldParseEmbedded(Metadata metadata) {
        String name;
        DocumentSelector selector = this.context.get(DocumentSelector.class);
        if (selector != null) {
            return selector.select(metadata);
        }
        FilenameFilter filter = this.context.get(FilenameFilter.class);
        if (filter != null && (name = metadata.get("resourceName")) != null) {
            return filter.accept(ABSTRACT_PATH, name);
        }
        return true;
    }

    @Override
    public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml) throws SAXException, IOException {
        if (outputHtml) {
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "class", "class", "CDATA", "package-entry");
            handler.startElement("http://www.w3.org/1999/xhtml", "div", "div", attributes);
        }
        String name = metadata.get("resourceName");
        if (this.writeFileNameToContent && name != null && name.length() > 0 && outputHtml) {
            handler.startElement("http://www.w3.org/1999/xhtml", "h1", "h1", new AttributesImpl());
            char[] chars = name.toCharArray();
            handler.characters(chars, 0, chars.length);
            handler.endElement("http://www.w3.org/1999/xhtml", "h1", "h1");
        }
        try (TemporaryResources tmp = new TemporaryResources();){
            Object container;
            TikaInputStream newStream = TikaInputStream.get((InputStream)new CloseShieldInputStream(stream), tmp, metadata);
            if (stream instanceof TikaInputStream && (container = ((TikaInputStream)((Object)stream)).getOpenContainer()) != null) {
                newStream.setOpenContainer(container);
            }
            DELEGATING_PARSER.parse((InputStream)((Object)newStream), new EmbeddedContentHandler(new BodyContentHandler(handler)), metadata, this.context);
        }
        catch (EncryptedDocumentException ede) {
            this.recordException(ede, this.context);
        }
        catch (CorruptedFileException e) {
            throw new IOException(e);
        }
        catch (TikaException e) {
            this.recordException(e, this.context);
        }
        if (outputHtml) {
            handler.endElement("http://www.w3.org/1999/xhtml", "div", "div");
        }
    }

    private void recordException(Exception e, ParseContext context) {
        ParseRecord record = context.get(ParseRecord.class);
        if (record == null) {
            return;
        }
        record.addException(e);
    }

    public Parser getDelegatingParser() {
        return DELEGATING_PARSER;
    }

    public void setWriteFileNameToContent(boolean writeFileNameToContent) {
        this.writeFileNameToContent = writeFileNameToContent;
    }
}

