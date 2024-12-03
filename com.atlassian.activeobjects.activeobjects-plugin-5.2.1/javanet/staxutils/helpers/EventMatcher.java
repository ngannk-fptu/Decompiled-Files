/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.helpers;

import java.util.Iterator;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public final class EventMatcher {
    private EventMatcher() {
    }

    public static boolean eventsMatch(XMLEvent a, XMLEvent b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.getEventType() == b.getEventType()) {
            switch (a.getEventType()) {
                case 1: {
                    return EventMatcher.eventsMatch(a.asStartElement(), b.asStartElement());
                }
                case 2: {
                    return EventMatcher.eventsMatch(a.asEndElement(), b.asEndElement());
                }
                case 4: 
                case 6: 
                case 12: {
                    return EventMatcher.eventsMatch(a.asCharacters(), b.asCharacters());
                }
                case 5: {
                    return EventMatcher.eventsMatch((Comment)a, (Comment)b);
                }
                case 9: {
                    return EventMatcher.eventsMatch((EntityReference)a, (EntityReference)b);
                }
                case 10: {
                    return EventMatcher.eventsMatch((Attribute)a, (Attribute)b);
                }
                case 13: {
                    return EventMatcher.eventsMatch((Namespace)a, (Namespace)b);
                }
                case 7: {
                    return EventMatcher.eventsMatch((StartDocument)a, (StartDocument)b);
                }
                case 8: {
                    return EventMatcher.eventsMatch((EndDocument)a, (EndDocument)b);
                }
                case 3: {
                    return EventMatcher.eventsMatch((ProcessingInstruction)a, (ProcessingInstruction)b);
                }
                case 11: {
                    return EventMatcher.eventsMatch((DTD)a, (DTD)b);
                }
                case 15: {
                    return EventMatcher.eventsMatch((EntityDeclaration)a, (EntityDeclaration)b);
                }
                case 14: {
                    return EventMatcher.eventsMatch((NotationDeclaration)a, (NotationDeclaration)b);
                }
            }
        }
        return false;
    }

    public static boolean eventsMatch(Attribute a, Attribute b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.getName().equals(b.getName())) {
            return a.getValue().equals(b.getValue());
        }
        return false;
    }

    public static boolean eventsMatch(Characters a, Characters b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.getEventType() == b.getEventType()) {
            return a.getData().equals(b.getData());
        }
        return false;
    }

    public static boolean eventsMatch(Comment a, Comment b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.getText().equals(b.getText());
    }

    public static boolean eventsMatch(DTD a, DTD b) {
        if (a == b) {
            return true;
        }
        if (a == null || a == null) {
            return false;
        }
        return a.getDocumentTypeDeclaration().equals(b.getDocumentTypeDeclaration());
    }

    public static boolean eventsMatch(EndDocument a, EndDocument b) {
        return a != null && b != null;
    }

    public static boolean eventsMatch(EndElement a, EndElement b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.getName().equals(b.getName());
    }

    public static boolean eventsMatch(EntityDeclaration a, EntityDeclaration b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (!a.getName().equals(b.getName())) {
            return false;
        }
        String baseURI = a.getBaseURI();
        if (!(baseURI != null ? baseURI.equals(b.getBaseURI()) : b.getBaseURI() == null)) {
            return false;
        }
        String text = a.getReplacementText();
        if (!(text != null ? text.equals(b.getReplacementText()) : b.getReplacementText() == null)) {
            return false;
        }
        String publicId = a.getPublicId();
        if (!(publicId != null ? publicId.equals(b.getPublicId()) : b.getPublicId() == null)) {
            return false;
        }
        String systemId = a.getSystemId();
        if (!(systemId != null ? systemId.equals(b.getSystemId()) : b.getSystemId() == null)) {
            return false;
        }
        String ndata = a.getNotationName();
        return ndata != null ? ndata.equals(b.getNotationName()) : b.getNotationName() == null;
    }

    public static boolean eventsMatch(EntityReference a, EntityReference b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.getName().equals(b.getName())) {
            return EventMatcher.eventsMatch(a.getDeclaration(), b.getDeclaration());
        }
        return false;
    }

    public static boolean eventsMatch(Namespace a, Namespace b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.getPrefix().equals(b.getPrefix()) && a.getNamespaceURI().equals(b.getNamespaceURI());
    }

    public static boolean eventsMatch(NotationDeclaration a, NotationDeclaration b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (!a.getName().equals(b.getName())) {
            return false;
        }
        String publicId = a.getPublicId();
        if (!(publicId != null ? publicId.equals(b.getPublicId()) : b.getPublicId() == null)) {
            return false;
        }
        String systemId = a.getSystemId();
        return systemId != null ? systemId.equals(b.getSystemId()) : b.getSystemId() == null;
    }

    public static boolean eventsMatch(ProcessingInstruction a, ProcessingInstruction b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.getTarget().equals(b.getTarget()) && a.getData().equals(b.getData());
    }

    public static boolean eventsMatch(StartDocument a, StartDocument b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (!a.getCharacterEncodingScheme().equals(b.getCharacterEncodingScheme())) {
            return false;
        }
        if (a.isStandalone() != b.isStandalone()) {
            return false;
        }
        return a.getVersion().equals(b.getVersion());
    }

    public static boolean eventsMatch(StartElement a, StartElement b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (!a.getName().equals(b.getName())) {
            return false;
        }
        if (!EventMatcher.matchAttributes(a.getAttributes(), b.getAttributes())) {
            return false;
        }
        return EventMatcher.matchNamespaces(a.getNamespaces(), b.getNamespaces());
    }

    public static boolean matchAttributes(Iterator a, Iterator b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        while (a.hasNext() && b.hasNext()) {
            Attribute B;
            Attribute A = (Attribute)a.next();
            if (EventMatcher.eventsMatch(A, B = (Attribute)b.next())) continue;
            return false;
        }
        return a.hasNext() == b.hasNext();
    }

    public static boolean matchNamespaces(Iterator a, Iterator b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        while (a.hasNext() && b.hasNext()) {
            Namespace B;
            Namespace A = (Namespace)a.next();
            if (EventMatcher.eventsMatch(A, B = (Namespace)b.next())) continue;
            return false;
        }
        return a.hasNext() == b.hasNext();
    }
}

