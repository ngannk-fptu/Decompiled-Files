/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.io.AttributeNameIterator;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractXmlReader;
import com.thoughtworks.xstream.io.xml.DocumentReader;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import java.util.Iterator;

public abstract class AbstractDocumentReader
extends AbstractXmlReader
implements DocumentReader {
    private FastStack pointers = new FastStack(16);
    private Object current;

    protected AbstractDocumentReader(Object rootElement) {
        this(rootElement, new XmlFriendlyNameCoder());
    }

    protected AbstractDocumentReader(Object rootElement, NameCoder nameCoder) {
        super(nameCoder);
        this.current = rootElement;
        this.pointers.push(new Pointer());
        this.reassignCurrentElement(this.current);
    }

    protected AbstractDocumentReader(Object rootElement, XmlFriendlyReplacer replacer) {
        this(rootElement, (NameCoder)replacer);
    }

    protected abstract void reassignCurrentElement(Object var1);

    protected abstract Object getParent();

    protected abstract Object getChild(int var1);

    protected abstract int getChildCount();

    public boolean hasMoreChildren() {
        Pointer pointer = (Pointer)this.pointers.peek();
        return pointer.v < this.getChildCount();
    }

    public void moveUp() {
        this.current = this.getParent();
        this.pointers.popSilently();
        this.reassignCurrentElement(this.current);
    }

    public void moveDown() {
        Pointer pointer = (Pointer)this.pointers.peek();
        this.pointers.push(new Pointer());
        this.current = this.getChild(pointer.v);
        ++pointer.v;
        this.reassignCurrentElement(this.current);
    }

    public Iterator getAttributeNames() {
        return new AttributeNameIterator(this);
    }

    public void appendErrors(ErrorWriter errorWriter) {
    }

    public Object getCurrent() {
        return this.current;
    }

    public void close() {
    }

    private static class Pointer {
        public int v;

        private Pointer() {
        }
    }
}

