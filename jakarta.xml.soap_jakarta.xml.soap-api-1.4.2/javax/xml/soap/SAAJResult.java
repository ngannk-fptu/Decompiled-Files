/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMResult;

public class SAAJResult
extends DOMResult {
    public SAAJResult() throws SOAPException {
        this(MessageFactory.newInstance().createMessage());
    }

    public SAAJResult(String protocol) throws SOAPException {
        this(MessageFactory.newInstance(protocol).createMessage());
    }

    public SAAJResult(SOAPMessage message) {
        super(message.getSOAPPart());
    }

    public SAAJResult(SOAPElement rootNode) {
        super(rootNode);
    }

    public Node getResult() {
        return (Node)super.getNode().getFirstChild();
    }
}

