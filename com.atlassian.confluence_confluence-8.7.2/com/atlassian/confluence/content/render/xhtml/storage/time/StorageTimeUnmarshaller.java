/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.IllegalFieldValueException
 */
package com.atlassian.confluence.content.render.xhtml.storage.time;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.time.Time;
import com.atlassian.confluence.content.render.xhtml.storage.time.StorageTimeConstants;
import com.atlassian.confluence.content.render.xhtml.storage.time.TimeModelDecorator;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.joda.time.IllegalFieldValueException;

public class StorageTimeUnmarshaller
implements Unmarshaller<Time> {
    private final TimeModelDecorator dateLozengeDecorator;

    public StorageTimeUnmarshaller(TimeModelDecorator timeModelDecorator) {
        this.dateLozengeDecorator = timeModelDecorator;
    }

    @Override
    public Time unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement startElement = reader.nextEvent().asStartElement();
            Attribute datetimeAttribute = startElement.getAttributeByName(StorageTimeConstants.DATETIME_ATTRIBUTE);
            if (datetimeAttribute != null) {
                Time result = new Time(datetimeAttribute.getValue());
                this.dateLozengeDecorator.decorate(result);
                Time time = result;
                return time;
            }
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("Exception reading data while unmarshalling a time from storage format.", e);
        }
        catch (IllegalFieldValueException e) {
            throw new XhtmlException("An error occurred due to invalid time format during unmarshalling a time from storage format.", e);
        }
        finally {
            StaxUtils.closeQuietly(reader);
        }
        return null;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return "time".equals(startElementEvent.getName().getLocalPart()) && StaxUtils.hasAttributes(startElementEvent, StorageTimeConstants.DATETIME_ATTRIBUTE.getLocalPart());
    }
}

