/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.time;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.time.Time;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

public class EditorTimeUnmarshaller
implements Unmarshaller<Time> {
    private static final String DATETIME_ATTRIBUTE = "datetime";

    @Override
    public Time unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement startElement = reader.nextEvent().asStartElement();
            Attribute datetimeAttribute = startElement.getAttributeByName(new QName("", DATETIME_ATTRIBUTE, ""));
            if (datetimeAttribute != null) {
                Time time = new Time(datetimeAttribute.getValue());
                return time;
            }
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("An error occurred while parsing a time during unmarshalling from the editor.", e);
        }
        catch (IllegalArgumentException e) {
            throw new XhtmlException("An error occurred due to invalid date time format during unmarshalling a time from storage format.", e);
        }
        finally {
            StaxUtils.closeQuietly(reader);
        }
        return null;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return "time".equals(startElementEvent.getName().getLocalPart()) && StaxUtils.hasAttributes(startElementEvent, DATETIME_ATTRIBUTE);
    }
}

