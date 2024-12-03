/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.util.stax.debug;

import java.util.Stack;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMLStreamReaderValidator
extends XMLStreamReaderWrapper {
    private static final Log log = LogFactory.getLog(XMLStreamReaderValidator.class);
    private static boolean IS_ADV_DEBUG_ENABLED = false;
    private boolean throwExceptions = false;
    private Stack stack = new Stack();

    public XMLStreamReaderValidator(XMLStreamReader delegate, boolean throwExceptions) {
        super(delegate);
        this.throwExceptions = throwExceptions;
    }

    public int next() throws XMLStreamException {
        int event = super.next();
        this.trackEvent(event);
        return event;
    }

    public String getElementText() throws XMLStreamException {
        String text = super.getElementText();
        this.trackEvent(2);
        return text;
    }

    public int nextTag() throws XMLStreamException {
        int event = super.nextTag();
        this.trackEvent(event);
        return event;
    }

    private void trackEvent(int event) throws XMLStreamException {
        this.logParserState();
        switch (event) {
            case 1: {
                this.stack.push(super.getName());
                break;
            }
            case 2: {
                QName delegateQName = super.getName();
                if (this.stack.isEmpty()) {
                    this.reportError("An END_ELEMENT event for " + delegateQName + " was encountered, but the START_ELEMENT stack is empty.");
                    break;
                }
                QName expectedQName = (QName)this.stack.pop();
                if (expectedQName.equals(delegateQName)) break;
                this.reportError("An END_ELEMENT event for " + delegateQName + " was encountered, but this doesn't match the corresponding START_ELEMENT " + expectedQName + " event.");
                break;
            }
            case 8: {
                if (this.stack.isEmpty()) break;
                this.reportError("An unexpected END_DOCUMENT event was encountered; element stack: " + this.stack);
                break;
            }
        }
    }

    private void reportError(String message) throws XMLStreamException {
        log.debug((Object)message);
        if (this.throwExceptions) {
            throw new XMLStreamException(message);
        }
    }

    protected void logParserState() {
        if (IS_ADV_DEBUG_ENABLED) {
            int currentEvent = super.getEventType();
            switch (currentEvent) {
                case 1: {
                    log.trace((Object)"START_ELEMENT: ");
                    log.trace((Object)("  QName: " + super.getName()));
                    break;
                }
                case 7: {
                    log.trace((Object)"START_DOCUMENT: ");
                    break;
                }
                case 4: {
                    log.trace((Object)"CHARACTERS: ");
                    log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 12: {
                    log.trace((Object)"CDATA: ");
                    log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 2: {
                    log.trace((Object)"END_ELEMENT: ");
                    log.trace((Object)("  QName: " + super.getName()));
                    break;
                }
                case 8: {
                    log.trace((Object)"END_DOCUMENT: ");
                    break;
                }
                case 6: {
                    log.trace((Object)"SPACE: ");
                    log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 5: {
                    log.trace((Object)"COMMENT: ");
                    log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 11: {
                    log.trace((Object)"DTD: ");
                    log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 3: {
                    log.trace((Object)"PROCESSING_INSTRUCTION: ");
                    log.trace((Object)("   [" + super.getPITarget() + "][" + super.getPIData() + "]"));
                    break;
                }
                case 9: {
                    log.trace((Object)"ENTITY_REFERENCE: ");
                    log.trace((Object)("    " + super.getLocalName() + "[" + super.getText() + "]"));
                    break;
                }
                default: {
                    log.trace((Object)("UNKNOWN_STATE: " + currentEvent));
                }
            }
        }
    }
}

