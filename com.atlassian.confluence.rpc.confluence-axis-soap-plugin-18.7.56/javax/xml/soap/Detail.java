/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Iterator;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFaultElement;

public interface Detail
extends SOAPFaultElement {
    public DetailEntry addDetailEntry(Name var1) throws SOAPException;

    public Iterator getDetailEntries();
}

