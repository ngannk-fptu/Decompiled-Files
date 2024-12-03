/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xml;

import java.util.Stack;
import org.jfree.data.xml.DatasetTags;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RootHandler
extends DefaultHandler
implements DatasetTags {
    private Stack subHandlers = new Stack();

    public Stack getSubHandlers() {
        return this.subHandlers;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        DefaultHandler handler = this.getCurrentHandler();
        if (handler != this) {
            handler.characters(ch, start, length);
        }
    }

    public DefaultHandler getCurrentHandler() {
        Object top;
        DefaultHandler result = this;
        if (this.subHandlers != null && this.subHandlers.size() > 0 && (top = this.subHandlers.peek()) != null) {
            result = (DefaultHandler)top;
        }
        return result;
    }

    public void pushSubHandler(DefaultHandler subhandler) {
        this.subHandlers.push(subhandler);
    }

    public DefaultHandler popSubHandler() {
        return (DefaultHandler)this.subHandlers.pop();
    }
}

