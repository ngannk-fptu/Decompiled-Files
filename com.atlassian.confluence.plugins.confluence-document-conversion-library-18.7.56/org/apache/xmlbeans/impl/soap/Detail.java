/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.soap;

import java.util.Iterator;
import org.apache.xmlbeans.impl.soap.DetailEntry;
import org.apache.xmlbeans.impl.soap.Name;
import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.soap.SOAPFaultElement;

public interface Detail
extends SOAPFaultElement {
    public DetailEntry addDetailEntry(Name var1) throws SOAPException;

    public Iterator<DetailEntry> getDetailEntries();
}

