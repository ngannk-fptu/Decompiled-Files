/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.util.Iterator;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import org.apache.axis.message.DetailEntry;
import org.apache.axis.message.SOAPFaultElement;

public class Detail
extends SOAPFaultElement
implements javax.xml.soap.Detail {
    public javax.xml.soap.DetailEntry addDetailEntry(Name name) throws SOAPException {
        DetailEntry entry = new DetailEntry(name);
        this.addChildElement(entry);
        return entry;
    }

    public Iterator getDetailEntries() {
        return this.getChildElements();
    }
}

