/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.BatchedRenderRequest;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.storage.ContentTransformerFactory;
import com.atlassian.confluence.content.render.xhtml.storage.MacroDefinitionTransformer;
import com.atlassian.confluence.content.render.xhtml.view.BatchedRenderResult;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.xhtml.MacroDefinitionTransformerImpl;
import com.atlassian.confluence.impl.content.render.xhtml.WikiToStorageConverterImpl;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.MacroDefinitionReplacer;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.xhtml.api.XhtmlVisitor;
import java.io.StringReader;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class DefaultXhtmlContent
implements XhtmlContent {
    private final Marshaller<MacroDefinition> viewMacroMarshaller;
    private final Marshaller<MacroDefinition> storageMacroMarshaller;
    private final Marshaller<Link> viewLinkMarshaller;
    private final Marshaller<Link> storageLinkMarshaller;
    private final Marshaller<EmbeddedImage> viewEmbeddedImageMarshaller;
    private final Marshaller<EmbeddedImage> storageEmbeddedImageMarshaller;
    private final Marshaller<InlineTaskList> viewInlineTaskMarshaller;
    private final Marshaller<InlineTaskList> storageInlineTaskMarshaller;
    private final Renderer viewRenderer;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final MacroDefinitionTransformer macroDefinitionTransformer;
    private final WikiToStorageConverter wikiToStorageConverter;

    public DefaultXhtmlContent(Marshaller<MacroDefinition> viewMacroMarshaller, Marshaller<MacroDefinition> storageMacroMarshaller, Marshaller<Link> viewLinkMarshaller, Marshaller<Link> storageLinkMarshaller, Marshaller<EmbeddedImage> viewEmbeddedImageMarshaller, Marshaller<EmbeddedImage> storageEmbeddedImageMarshaller, Marshaller<InlineTaskList> viewInlineTaskMarshaller, Marshaller<InlineTaskList> storageInlineTaskMarshaller, Renderer viewRenderer, XmlEventReaderFactory xmlEventReaderFactory, ContentTransformerFactory contentTransformerFactory, ExceptionTolerantMigrator wikiToXhtmlMigrator) {
        this.viewMacroMarshaller = viewMacroMarshaller;
        this.storageMacroMarshaller = storageMacroMarshaller;
        this.viewLinkMarshaller = viewLinkMarshaller;
        this.storageLinkMarshaller = storageLinkMarshaller;
        this.viewEmbeddedImageMarshaller = viewEmbeddedImageMarshaller;
        this.storageEmbeddedImageMarshaller = storageEmbeddedImageMarshaller;
        this.viewInlineTaskMarshaller = viewInlineTaskMarshaller;
        this.storageInlineTaskMarshaller = storageInlineTaskMarshaller;
        this.viewRenderer = viewRenderer;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.macroDefinitionTransformer = new MacroDefinitionTransformerImpl(contentTransformerFactory);
        this.wikiToStorageConverter = new WikiToStorageConverterImpl(wikiToXhtmlMigrator);
    }

    @Override
    public String convertWikiToStorage(String wikiContent, ConversionContext context, List<RuntimeException> migrationExceptions) {
        return this.wikiToStorageConverter.convertWikiToStorage(wikiContent, context, migrationExceptions);
    }

    @Override
    public <T extends ContentEntityObject> T convertWikiBodyToStorage(T ceo) {
        return this.wikiToStorageConverter.convertWikiBodyToStorage(ceo);
    }

    @Override
    public String convertWikiToView(String wikiContent, ConversionContext context, List<RuntimeException> migrationExceptions) throws XMLStreamException, XhtmlException {
        String storageFormat = this.convertWikiToStorage(wikiContent, context, migrationExceptions);
        if (StringUtils.isBlank((CharSequence)storageFormat)) {
            return "";
        }
        return this.convertStorageToView(storageFormat, context);
    }

    @Override
    public String convertStorageToView(String storageFragment, ConversionContext context) throws XMLStreamException, XhtmlException {
        return this.viewRenderer.render(storageFragment, context);
    }

    @Override
    public List<BatchedRenderResult> convertStorageToView(BatchedRenderRequest ... renderRequests) {
        return this.viewRenderer.render(renderRequests);
    }

    @Override
    public String convertMacroDefinitionToView(MacroDefinition macroDefinition, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.viewMacroMarshaller.marshal(macroDefinition, context));
    }

    @Override
    public String convertLinkToView(Link link, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.viewLinkMarshaller.marshal(link, context));
    }

    @Override
    public String convertEmbeddedImageToView(EmbeddedImage embeddedImage, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.viewEmbeddedImageMarshaller.marshal(embeddedImage, context));
    }

    @Override
    public String convertInlineTaskListToView(InlineTaskList inlineTaskList, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.viewInlineTaskMarshaller.marshal(inlineTaskList, context));
    }

    @Override
    public String convertMacroDefinitionToStorage(MacroDefinition macroDefinition, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.storageMacroMarshaller.marshal(macroDefinition, context));
    }

    @Override
    public String convertLinkToStorage(Link link, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.storageLinkMarshaller.marshal(link, context));
    }

    @Override
    public String convertEmbeddedImageToStorage(EmbeddedImage embeddedImage, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.storageEmbeddedImageMarshaller.marshal(embeddedImage, context));
    }

    @Override
    public String convertInlineTaskListToStorage(InlineTaskList inlineTaskList, ConversionContext context) throws XhtmlException {
        return Streamables.writeToString(this.storageInlineTaskMarshaller.marshal(inlineTaskList, context));
    }

    @Override
    public String updateMacroDefinitions(String storageFragment, ConversionContext context, MacroDefinitionUpdater updater) throws XhtmlException {
        return this.macroDefinitionTransformer.updateMacroDefinitions(storageFragment, context, updater);
    }

    @Override
    public String replaceMacroDefinitionsWithString(String storageFragment, ConversionContext context, MacroDefinitionReplacer replacer) throws XhtmlException {
        return this.macroDefinitionTransformer.replaceMacroDefinitionsWithString(storageFragment, context, replacer);
    }

    @Override
    public void handleMacroDefinitions(String storageFragment, ConversionContext context, MacroDefinitionHandler handler) throws XhtmlException {
        this.macroDefinitionTransformer.handleMacroDefinitions(storageFragment, context, handler);
    }

    @Override
    public void handleMacroDefinitions(String storageFragment, ConversionContext context, MacroDefinitionHandler handler, MacroDefinitionMarshallingStrategy strategy) throws XhtmlException {
        this.macroDefinitionTransformer.handleMacroDefinitions(storageFragment, context, handler, strategy);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void handleXhtmlElements(String storageFragment, ConversionContext context, List<? extends XhtmlVisitor> visitors) throws XhtmlException {
        XMLEventReader reader;
        block8: {
            block7: {
                reader = null;
                try {
                    reader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(storageFragment));
                    while (reader.hasNext()) {
                        XMLEvent event = reader.nextEvent();
                        for (XhtmlVisitor xhtmlVisitor : visitors) {
                            if (xhtmlVisitor.handle(event, context)) continue;
                            break block7;
                        }
                    }
                    break block8;
                }
                catch (XMLStreamException e) {
                    try {
                        throw new XhtmlException("Error occurred while reading stream", e);
                    }
                    catch (Throwable throwable) {
                        StaxUtils.closeQuietly(reader);
                        throw throwable;
                    }
                }
            }
            StaxUtils.closeQuietly(reader);
            return;
        }
        StaxUtils.closeQuietly(reader);
    }
}

