/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.BatchedRenderRequest;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inlinetask.InlineTaskList;
import com.atlassian.confluence.content.render.xhtml.storage.MacroDefinitionTransformer;
import com.atlassian.confluence.content.render.xhtml.view.BatchedRenderResult;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.MacroDefinitionReplacer;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import com.atlassian.confluence.xhtml.api.XhtmlVisitor;
import java.util.List;
import javax.xml.stream.XMLStreamException;

public interface XhtmlContent
extends MacroDefinitionTransformer,
WikiToStorageConverter {
    @Override
    public String convertWikiToStorage(String var1, ConversionContext var2, List<RuntimeException> var3);

    @Override
    public <T extends ContentEntityObject> T convertWikiBodyToStorage(T var1);

    public String convertWikiToView(String var1, ConversionContext var2, List<RuntimeException> var3) throws XMLStreamException, XhtmlException;

    public String convertStorageToView(String var1, ConversionContext var2) throws XMLStreamException, XhtmlException;

    public List<BatchedRenderResult> convertStorageToView(BatchedRenderRequest ... var1);

    public String convertMacroDefinitionToView(MacroDefinition var1, ConversionContext var2) throws XhtmlException;

    public String convertLinkToView(Link var1, ConversionContext var2) throws XhtmlException;

    public String convertEmbeddedImageToView(EmbeddedImage var1, ConversionContext var2) throws XhtmlException;

    public String convertInlineTaskListToView(InlineTaskList var1, ConversionContext var2) throws XhtmlException;

    public String convertMacroDefinitionToStorage(MacroDefinition var1, ConversionContext var2) throws XhtmlException;

    public String convertLinkToStorage(Link var1, ConversionContext var2) throws XhtmlException;

    public String convertEmbeddedImageToStorage(EmbeddedImage var1, ConversionContext var2) throws XhtmlException;

    public String convertInlineTaskListToStorage(InlineTaskList var1, ConversionContext var2) throws XhtmlException;

    @Override
    public String updateMacroDefinitions(String var1, ConversionContext var2, MacroDefinitionUpdater var3) throws XhtmlException;

    @Override
    public String replaceMacroDefinitionsWithString(String var1, ConversionContext var2, MacroDefinitionReplacer var3) throws XhtmlException;

    @Override
    public void handleMacroDefinitions(String var1, ConversionContext var2, MacroDefinitionHandler var3) throws XhtmlException;

    @Override
    public void handleMacroDefinitions(String var1, ConversionContext var2, MacroDefinitionHandler var3, MacroDefinitionMarshallingStrategy var4) throws XhtmlException;

    public void handleXhtmlElements(String var1, ConversionContext var2, List<? extends XhtmlVisitor> var3) throws XhtmlException;
}

