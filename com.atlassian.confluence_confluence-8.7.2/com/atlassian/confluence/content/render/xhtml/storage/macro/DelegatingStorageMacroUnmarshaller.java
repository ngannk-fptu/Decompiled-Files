/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV1Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV2Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class DelegatingStorageMacroUnmarshaller
implements Unmarshaller<MacroDefinition> {
    private final StorageMacroV1Unmarshaller v1Unmarshaller;
    private final StorageMacroV2Unmarshaller v2Unmarshaller;

    public DelegatingStorageMacroUnmarshaller(StorageMacroV1Unmarshaller v1Unmarshaller, StorageMacroV2Unmarshaller v2Unmarshaller) {
        this.v1Unmarshaller = v1Unmarshaller;
        this.v2Unmarshaller = v2Unmarshaller;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.v1Unmarshaller.handles(startElementEvent, conversionContext) || this.v2Unmarshaller.handles(startElementEvent, conversionContext);
    }

    @Override
    public MacroDefinition unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement macroElementEvent = (StartElement)reader.peek();
            if (this.v1Unmarshaller.handles(macroElementEvent, conversionContext)) {
                return this.v1Unmarshaller.unmarshal(reader, mainFragmentTransformer, conversionContext);
            }
            if (this.v2Unmarshaller.handles(macroElementEvent, conversionContext)) {
                return this.v2Unmarshaller.unmarshal(reader, mainFragmentTransformer, conversionContext);
            }
            throw new XhtmlException("Unhandled start element " + macroElementEvent.getName());
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
    }
}

