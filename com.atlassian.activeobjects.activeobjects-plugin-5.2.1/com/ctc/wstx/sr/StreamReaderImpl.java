/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.ElemCallback;
import com.ctc.wstx.sr.InputElementStack;
import javax.xml.stream.Location;
import org.codehaus.stax2.XMLStreamReader2;

public interface StreamReaderImpl
extends XMLStreamReader2 {
    public EntityDecl getCurrentEntityDecl();

    public Object withStartElement(ElemCallback var1, Location var2);

    public boolean isNamespaceAware();

    public AttributeCollector getAttributeCollector();

    public InputElementStack getInputElementStack();
}

