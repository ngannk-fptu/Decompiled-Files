/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import javax.xml.stream.XMLStreamException;

public interface EditorFormatService {
    public String convertWikiToEdit(String var1, ConversionContext var2) throws XhtmlException;

    public String convertStorageToEdit(String var1, ConversionContext var2) throws XMLStreamException, XhtmlException;

    public String convertEditToStorage(String var1, ConversionContext var2) throws XMLStreamException, XhtmlException;

    public String convertMacroDefinitionToEdit(MacroDefinition var1, ConversionContext var2) throws XhtmlException;

    public String convertLinkToEdit(Link var1, ConversionContext var2) throws XhtmlException;

    public String convertEmbeddedImageToEdit(EmbeddedImage var1, ConversionContext var2) throws XhtmlException;

    public MacroDefinition convertEditToMacroDefinition(String var1, ConversionContext var2) throws XMLStreamException, XhtmlException;

    public Link convertEditToLink(String var1, ConversionContext var2) throws XMLStreamException, XhtmlException;

    public EmbeddedImage convertEditToEmbeddedImage(String var1, ConversionContext var2) throws XMLStreamException, XhtmlException;
}

