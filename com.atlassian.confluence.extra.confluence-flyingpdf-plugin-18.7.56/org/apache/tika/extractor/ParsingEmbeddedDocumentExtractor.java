/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.extractor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.CorruptedFileException;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.DocumentSelector;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.io.IOExceptionWithCause;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.DelegatingParser;
import org.apache.tika.parser.ParseContext;
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
        String name;
        if (outputHtml) {
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "class", "class", "CDATA", "package-entry");
            handler.startElement("http://www.w3.org/1999/xhtml", "div", "div", attributes);
        }
        if ((name = metadata.get("resourceName")) != null && name.length() > 0 && outputHtml) {
            handler.startElement("http://www.w3.org/1999/xhtml", "h1", "h1", new AttributesImpl());
            char[] chars = name.toCharArray();
            handler.characters(chars, 0, chars.length);
            handler.endElement("http://www.w3.org/1999/xhtml", "h1", "h1");
        }
        try (TemporaryResources tmp2 = new TemporaryResources();){
            Object container;
            TikaInputStream newStream = TikaInputStream.get(new CloseShieldInputStream(stream), tmp2);
            if (stream instanceof TikaInputStream && (container = ((TikaInputStream)stream).getOpenContainer()) != null) {
                newStream.setOpenContainer(container);
            }
            DELEGATING_PARSER.parse(newStream, new EmbeddedContentHandler(new BodyContentHandler(handler)), metadata, this.context);
        }
        catch (EncryptedDocumentException tmp2) {
        }
        catch (CorruptedFileException e) {
            throw new IOExceptionWithCause(e);
        }
        catch (TikaException tikaException) {
            // empty catch block
        }
        if (outputHtml) {
            handler.endElement("http://www.w3.org/1999/xhtml", "div", "div");
        }
    }
}

