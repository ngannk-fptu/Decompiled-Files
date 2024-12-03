/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.xml;

public final class XMLStreamConstantsUtils {
    private XMLStreamConstantsUtils() {
    }

    public static String getEventName(int eventId) {
        switch (eventId) {
            case 1: {
                return "StartElementEvent";
            }
            case 2: {
                return "EndElementEvent";
            }
            case 3: {
                return "ProcessingInstructionEvent";
            }
            case 4: {
                return "CharacterEvent";
            }
            case 5: {
                return "CommentEvent";
            }
            case 7: {
                return "StartDocumentEvent";
            }
            case 8: {
                return "EndDocumentEvent";
            }
            case 9: {
                return "EntityReferenceEvent";
            }
            case 10: {
                return "AttributeBase";
            }
            case 11: {
                return "DTDEvent";
            }
            case 12: {
                return "CDATA";
            }
        }
        return "UNKNOWN_EVENT_TYPE";
    }
}

