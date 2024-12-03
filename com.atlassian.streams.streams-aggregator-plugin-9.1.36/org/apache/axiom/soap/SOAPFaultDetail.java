/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import java.util.Iterator;
import org.apache.axiom.om.OMElement;

public interface SOAPFaultDetail
extends OMElement {
    public void addDetailEntry(OMElement var1);

    public Iterator getAllDetailEntries();
}

