/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ElementTransformingXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.editor.TableStyleRemovingElementTransformer;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroBodyTransformationCondition;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroBodyParser;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.Arrays;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class DefaultStorageMacroBodyParser
implements StorageMacroBodyParser {
    private final MacroBodyTransformationCondition condition;
    private final TableStyleRemovingElementTransformer tableStyleRemovingElementTransformer;
    private final XmlOutputFactory xmlOutputFactory;

    public DefaultStorageMacroBodyParser(MacroBodyTransformationCondition condition, TableStyleRemovingElementTransformer tableStyleRemovingElementTransformer, XmlOutputFactory xmlOutputFactory) {
        this.condition = condition;
        this.tableStyleRemovingElementTransformer = tableStyleRemovingElementTransformer;
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public MacroBody getMacroBody(String macroName, XMLEventReader bodyReader, ConversionContext context, FragmentTransformer transformer) throws XMLStreamException, XhtmlException {
        if (bodyReader.hasNext()) {
            String storageMacroBody;
            ResettableXmlEventReader reader = new ResettableXmlEventReader(bodyReader);
            String string = storageMacroBody = this.tableStyleRemovingElementTransformer != null ? StaxUtils.toXmlString(new ElementTransformingXmlEventReader(reader, Arrays.asList(this.tableStyleRemovingElementTransformer)), this.xmlOutputFactory) : StaxUtils.toXmlString(reader, this.xmlOutputFactory);
            if (transformer != null && this.condition.shouldTransform(macroName)) {
                reader.reset();
                return RichTextMacroBody.withStorageAndTransform(Streamables.from(storageMacroBody), transformer.transform(reader, transformer, context));
            }
            return RichTextMacroBody.withStorage(Streamables.from(storageMacroBody));
        }
        return RichTextMacroBody.withStorage(Streamables.empty());
    }
}

