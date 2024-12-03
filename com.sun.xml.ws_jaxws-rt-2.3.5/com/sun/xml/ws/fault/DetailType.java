/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.bind.annotation.XmlAnyElement
 */
package com.sun.xml.ws.fault;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAnyElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class DetailType {
    @XmlAnyElement
    private final List<Element> detailEntry = new ArrayList<Element>();

    @NotNull
    List<Element> getDetails() {
        return this.detailEntry;
    }

    @Nullable
    Node getDetail(int n) {
        if (n < this.detailEntry.size()) {
            return this.detailEntry.get(n);
        }
        return null;
    }

    DetailType(Element detailObject) {
        if (detailObject != null) {
            this.detailEntry.add(detailObject);
        }
    }

    DetailType() {
    }
}

