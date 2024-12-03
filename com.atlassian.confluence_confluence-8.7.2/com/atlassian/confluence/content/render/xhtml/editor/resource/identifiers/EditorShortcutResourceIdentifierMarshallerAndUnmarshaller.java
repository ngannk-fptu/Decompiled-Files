/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;

public class EditorShortcutResourceIdentifierMarshallerAndUnmarshaller
implements Unmarshaller<ResourceIdentifier>,
StaxStreamMarshaller<ShortcutResourceIdentifier> {
    private static final String SHORTCUT_KEY_ATTRIBUTE = "shortcut-key";
    private static final String SHORTCUT_PARAMETER_ATTRIBUTE = "shortcut-parameter";

    @Override
    public void marshal(ShortcutResourceIdentifier shortcutResourceIdentifier, XMLStreamWriter xmlStreamWriter, ConversionContext context) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(SHORTCUT_KEY_ATTRIBUTE, StringUtils.defaultString((String)shortcutResourceIdentifier.getShortcutKey()));
        xmlStreamWriter.writeAttribute(SHORTCUT_PARAMETER_ATTRIBUTE, StringUtils.defaultString((String)shortcutResourceIdentifier.getShortcutParameter()));
    }

    @Override
    public ResourceIdentifier unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StartElement startElement;
        try {
            startElement = xmlEventReader.peek().asStartElement();
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        return new ShortcutResourceIdentifier(StaxUtils.getAttributeValue(startElement, SHORTCUT_KEY_ATTRIBUTE), StaxUtils.getAttributeValue(startElement, SHORTCUT_PARAMETER_ATTRIBUTE));
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StaxUtils.hasAttributes(startElementEvent, SHORTCUT_KEY_ATTRIBUTE, SHORTCUT_PARAMETER_ATTRIBUTE);
    }
}

