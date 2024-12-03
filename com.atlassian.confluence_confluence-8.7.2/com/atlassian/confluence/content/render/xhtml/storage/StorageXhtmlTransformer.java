/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.exc.WstxLazyException
 */
package com.atlassian.confluence.content.render.xhtml.storage;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.ctc.wstx.exc.WstxLazyException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class StorageXhtmlTransformer
implements Transformer {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final FragmentTransformer defaultFragmentTransformer;

    public StorageXhtmlTransformer(XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformer defaultFragmentTransformer) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.defaultFragmentTransformer = defaultFragmentTransformer;
    }

    @Override
    public String transform(Reader storage, ConversionContext conversionContext) throws XhtmlException {
        XMLEventReader xmlEventReader = null;
        try {
            StringWriter output = new StringWriter();
            xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(storage);
            this.defaultFragmentTransformer.transform(xmlEventReader, this.defaultFragmentTransformer, conversionContext).writeTo(output);
            return output.toString();
        }
        catch (WstxLazyException e) {
            throw StaxUtils.convertToXhtmlException(e);
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("Error occurred while reading stream", e);
        }
        catch (IOException e) {
            throw new XhtmlException("Error occurred while writing to stream", e);
        }
        catch (RuntimeException e) {
            StaxUtils.closeQuietly(xmlEventReader);
            throw StaxUtils.processWrappedWstxExceptionOrTrowMapped(e, e1 -> new XhtmlException("RuntimeException occurred while performing an XHTML storage transformation (" + e1.getMessage() + ")", (Throwable)e1));
        }
    }
}

