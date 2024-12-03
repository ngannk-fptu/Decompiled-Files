/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 */
package org.apache.xml.security.stax.ext;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityHeaderHandler;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.XMLSecurityEventReader;

public abstract class AbstractInputSecurityHeaderHandler
implements XMLSecurityHeaderHandler {
    protected <T> T parseStructure(Deque<XMLSecEvent> eventDeque, int index, XMLSecurityProperties securityProperties) throws XMLSecurityException {
        try {
            Unmarshaller unmarshaller = XMLSecurityConstants.getJaxbUnmarshaller(securityProperties.isDisableSchemaValidation());
            return (T)unmarshaller.unmarshal((XMLEventReader)new XMLSecurityEventReader(eventDeque, index));
        }
        catch (JAXBException e) {
            if (e.getCause() != null && e.getCause() instanceof Exception) {
                throw new XMLSecurityException((Exception)e.getCause());
            }
            throw new XMLSecurityException((Exception)((Object)e));
        }
    }

    protected List<QName> getElementPath(Deque<XMLSecEvent> eventDeque) throws XMLSecurityException {
        XMLSecEvent xmlSecEvent = eventDeque.peek();
        return xmlSecEvent.getElementPath();
    }

    protected XMLSecEvent getResponsibleStartXMLEvent(Deque<XMLSecEvent> eventDeque, int index) {
        Iterator<XMLSecEvent> xmlSecEventIterator = eventDeque.descendingIterator();
        int curIdx = 0;
        while (curIdx++ < index) {
            xmlSecEventIterator.next();
        }
        return xmlSecEventIterator.next();
    }

    protected List<XMLSecEvent> getResponsibleXMLSecEvents(Deque<XMLSecEvent> xmlSecEvents, int index) {
        ArrayList<XMLSecEvent> xmlSecEventList = new ArrayList<XMLSecEvent>();
        Iterator<XMLSecEvent> xmlSecEventIterator = xmlSecEvents.descendingIterator();
        int curIdx = 0;
        while (curIdx++ < index && xmlSecEventIterator.hasNext()) {
            xmlSecEventIterator.next();
        }
        while (xmlSecEventIterator.hasNext()) {
            xmlSecEventList.add(xmlSecEventIterator.next());
        }
        return xmlSecEventList;
    }
}

