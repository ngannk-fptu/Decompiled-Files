/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 */
package com.sun.xml.ws.fault;

import com.sun.xml.ws.fault.TextType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

class ReasonType {
    @XmlElements(value={@XmlElement(name="Text", namespace="http://www.w3.org/2003/05/soap-envelope", type=TextType.class)})
    private final List<TextType> text = new ArrayList<TextType>();

    ReasonType() {
    }

    ReasonType(String txt) {
        this.text.add(new TextType(txt));
    }

    List<TextType> texts() {
        return this.text;
    }
}

