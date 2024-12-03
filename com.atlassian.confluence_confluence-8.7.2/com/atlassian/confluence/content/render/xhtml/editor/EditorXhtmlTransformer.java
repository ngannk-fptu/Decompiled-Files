/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.exc.WstxLazyException
 */
package com.atlassian.confluence.content.render.xhtml.editor;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.TransformationException;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.ctc.wstx.exc.WstxLazyException;
import java.io.Reader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class EditorXhtmlTransformer
implements Transformer {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final FragmentTransformer defaultFragmentTransformer;

    public EditorXhtmlTransformer(XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformer defaultFragmentTransformer) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.defaultFragmentTransformer = defaultFragmentTransformer;
    }

    @Override
    public String transform(Reader editorFormatXml, ConversionContext conversionContext) throws XhtmlException {
        XMLEventReader xmlEventReader = null;
        try {
            xmlEventReader = this.xmlEventReaderFactory.createEditorXmlEventReader(editorFormatXml);
            return Streamables.writeToString(this.defaultFragmentTransformer.transform(xmlEventReader, this.defaultFragmentTransformer, conversionContext));
        }
        catch (WstxLazyException e) {
            throw StaxUtils.convertToXhtmlException(e);
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("Error occurred while reading stream", e);
        }
        catch (TransformationException ex) {
            throw ex;
        }
        catch (RuntimeException e) {
            StaxUtils.closeQuietly(xmlEventReader);
            throw StaxUtils.processWrappedWstxExceptionOrTrowMapped(e, e1 -> new XhtmlException("RuntimeException occurred while transforming editor format to storage format (" + e1.getMessage() + ")", (Throwable)e1));
        }
    }
}

