/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.AttributeNameIterator;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractXmlReader;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import java.util.Iterator;

public abstract class AbstractPullReader
extends AbstractXmlReader {
    protected static final int START_NODE = 1;
    protected static final int END_NODE = 2;
    protected static final int TEXT = 3;
    protected static final int COMMENT = 4;
    protected static final int OTHER = 0;
    private final FastStack elementStack = new FastStack(16);
    private final FastStack pool = new FastStack(16);
    private final FastStack lookahead = new FastStack(4);
    private final FastStack lookback = new FastStack(4);
    private boolean marked;

    protected AbstractPullReader(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected AbstractPullReader(XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    protected abstract int pullNextEvent();

    protected abstract String pullElementName();

    protected abstract String pullText();

    public boolean hasMoreChildren() {
        this.mark();
        while (true) {
            switch (this.readEvent().type) {
                case 1: {
                    this.reset();
                    return true;
                }
                case 2: {
                    this.reset();
                    return false;
                }
            }
        }
    }

    public void moveDown() {
        int currentDepth = this.elementStack.size();
        while (this.elementStack.size() <= currentDepth) {
            this.move();
            if (this.elementStack.size() >= currentDepth) continue;
            throw new RuntimeException();
        }
    }

    public void moveUp() {
        int currentDepth = this.elementStack.size();
        while (this.elementStack.size() >= currentDepth) {
            this.move();
        }
    }

    private void move() {
        Event event = this.readEvent();
        this.pool.push(event);
        switch (event.type) {
            case 1: {
                this.elementStack.push(this.pullElementName());
                break;
            }
            case 2: {
                this.elementStack.pop();
            }
        }
    }

    private Event readEvent() {
        if (this.marked) {
            if (this.lookback.hasStuff()) {
                return (Event)this.lookahead.push(this.lookback.pop());
            }
            return (Event)this.lookahead.push(this.readRealEvent());
        }
        if (this.lookback.hasStuff()) {
            return (Event)this.lookback.pop();
        }
        return this.readRealEvent();
    }

    private Event readRealEvent() {
        Event event = this.pool.hasStuff() ? (Event)this.pool.pop() : new Event();
        event.type = this.pullNextEvent();
        event.value = event.type == 3 ? this.pullText() : (event.type == 1 ? this.pullElementName() : null);
        return event;
    }

    public void mark() {
        this.marked = true;
    }

    public void reset() {
        while (this.lookahead.hasStuff()) {
            this.lookback.push(this.lookahead.pop());
        }
        this.marked = false;
    }

    public String getValue() {
        String last = null;
        StringBuffer buffer = null;
        this.mark();
        Event event = this.readEvent();
        while (true) {
            if (event.type == 3) {
                String text = event.value;
                if (text != null && text.length() > 0) {
                    if (last == null) {
                        last = text;
                    } else {
                        if (buffer == null) {
                            buffer = new StringBuffer(last);
                        }
                        buffer.append(text);
                    }
                }
            } else if (event.type != 4) break;
            event = this.readEvent();
        }
        this.reset();
        if (buffer != null) {
            return buffer.toString();
        }
        return last == null ? "" : last;
    }

    public Iterator getAttributeNames() {
        return new AttributeNameIterator(this);
    }

    public String getNodeName() {
        return this.unescapeXmlName((String)this.elementStack.peek());
    }

    public String peekNextChild() {
        this.mark();
        while (true) {
            Event ev = this.readEvent();
            switch (ev.type) {
                case 1: {
                    this.reset();
                    return ev.value;
                }
                case 2: {
                    this.reset();
                    return null;
                }
            }
        }
    }

    private static class Event {
        int type;
        String value;

        private Event() {
        }
    }
}

