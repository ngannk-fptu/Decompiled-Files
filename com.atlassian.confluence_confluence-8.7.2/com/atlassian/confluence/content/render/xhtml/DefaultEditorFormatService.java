/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionThrowingMigrator;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.StringReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class DefaultEditorFormatService
implements EditorFormatService {
    private final Marshaller<MacroDefinition> editorMacroMarshaller;
    private final Marshaller<Link> editorLinkMarshaller;
    private final Marshaller<EmbeddedImage> editorEmbeddedImageMarshaller;
    private final Unmarshaller<MacroDefinition> editorMacroUnmarshaller;
    private final Unmarshaller<Link> editorLinkUnmarshaller;
    private final Unmarshaller<EmbeddedImage> editorEmbeddedImageUnmarshaller;
    private final FragmentTransformer editorToStorageFragmentTransformer;
    private final FragmentTransformer storageToEditorFragmentTransformer;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final ExceptionThrowingMigrator wikiToEditorHtmlMigrator;

    public DefaultEditorFormatService(Marshaller<MacroDefinition> editorMacroMarshaller, Marshaller<Link> editorLinkMarshaller, Marshaller<EmbeddedImage> editorEmbeddedImageMarshaller, Unmarshaller<MacroDefinition> editorMacroUnmarshaller, Unmarshaller<Link> editorLinkUnmarshaller, Unmarshaller<EmbeddedImage> editorEmbeddedImageUnmarshaller, FragmentTransformer editorToStorageFragmentTransformer, FragmentTransformer storageToEditorFragmentTransformer, XmlEventReaderFactory xmlEventReaderFactory, ExceptionThrowingMigrator wikiToEditorHtmlMigrator) {
        this.editorMacroMarshaller = editorMacroMarshaller;
        this.editorLinkMarshaller = editorLinkMarshaller;
        this.editorEmbeddedImageMarshaller = editorEmbeddedImageMarshaller;
        this.editorMacroUnmarshaller = editorMacroUnmarshaller;
        this.editorLinkUnmarshaller = editorLinkUnmarshaller;
        this.editorEmbeddedImageUnmarshaller = editorEmbeddedImageUnmarshaller;
        this.editorToStorageFragmentTransformer = editorToStorageFragmentTransformer;
        this.storageToEditorFragmentTransformer = storageToEditorFragmentTransformer;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.wikiToEditorHtmlMigrator = wikiToEditorHtmlMigrator;
    }

    @Override
    public String convertWikiToEdit(String wikiContent, ConversionContext context) throws XhtmlException {
        return this.wikiToEditorHtmlMigrator.migrate(wikiContent, context != null ? context.getRenderContext() : null);
    }

    @Override
    public String convertStorageToEdit(String storageFragment, ConversionContext context) throws XMLStreamException, XhtmlException {
        Streamable editorFormat = this.streamStorageToEdit(storageFragment, context);
        return Streamables.writeToString(editorFormat);
    }

    @Override
    public String convertEditToStorage(String editFragment, ConversionContext context) throws XMLStreamException, XhtmlException {
        XMLEventReader xmlEventReader = this.xmlEventReaderFactory.createEditorXmlEventReader(new StringReader(editFragment));
        return Streamables.writeToString(this.editorToStorageFragmentTransformer.transform(xmlEventReader, this.storageToEditorFragmentTransformer, context));
    }

    @Override
    public String convertMacroDefinitionToEdit(MacroDefinition macroDefinition, ConversionContext context) throws XhtmlException {
        if (macroDefinition.getBody() instanceof RichTextMacroBody) {
            Streamable bodyText;
            try {
                bodyText = this.streamStorageToEdit(Streamables.writeToString(macroDefinition.getStorageBodyStream()), context);
            }
            catch (XMLStreamException e) {
                throw new XhtmlException(e);
            }
            macroDefinition.setBody(RichTextMacroBody.withStorageAndTransform(macroDefinition.getBody().getStorageBodyStream(), bodyText));
        }
        return Streamables.writeToString(this.editorMacroMarshaller.marshal(macroDefinition, context));
    }

    @Override
    public String convertLinkToEdit(Link link, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.editorLinkMarshaller.marshal(link, context));
    }

    @Override
    public String convertEmbeddedImageToEdit(EmbeddedImage embeddedImage, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.editorEmbeddedImageMarshaller.marshal(embeddedImage, context));
    }

    @Override
    public MacroDefinition convertEditToMacroDefinition(String editFragment, ConversionContext context) throws XMLStreamException, XhtmlException {
        XMLEventReader reader = this.xmlEventReaderFactory.createEditorXmlEventReader(new StringReader(editFragment));
        return this.editorMacroUnmarshaller.unmarshal(reader, this.editorToStorageFragmentTransformer, context);
    }

    @Override
    public Link convertEditToLink(String editFragment, ConversionContext context) throws XMLStreamException, XhtmlException {
        XMLEventReader reader = this.xmlEventReaderFactory.createEditorXmlEventReader(new StringReader(editFragment));
        return this.editorLinkUnmarshaller.unmarshal(reader, this.editorToStorageFragmentTransformer, context);
    }

    @Override
    public EmbeddedImage convertEditToEmbeddedImage(String editFragment, ConversionContext context) throws XMLStreamException, XhtmlException {
        XMLEventReader reader = this.xmlEventReaderFactory.createEditorXmlEventReader(new StringReader(editFragment));
        return this.editorEmbeddedImageUnmarshaller.unmarshal(reader, this.editorToStorageFragmentTransformer, context);
    }

    private Streamable streamStorageToEdit(String storageFragment, ConversionContext context) throws XMLStreamException, XhtmlException {
        XMLEventReader xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(storageFragment));
        return this.storageToEditorFragmentTransformer.transform(xmlEventReader, this.storageToEditorFragmentTransformer, context);
    }
}

