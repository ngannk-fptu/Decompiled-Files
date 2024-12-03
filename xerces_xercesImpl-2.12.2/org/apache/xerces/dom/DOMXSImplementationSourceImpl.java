/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.util.ArrayList;
import org.apache.xerces.dom.DOMImplementationListImpl;
import org.apache.xerces.dom.DOMImplementationSourceImpl;
import org.apache.xerces.dom.PSVIDOMImplementationImpl;
import org.apache.xerces.impl.xs.XSImplementationImpl;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

public class DOMXSImplementationSourceImpl
extends DOMImplementationSourceImpl {
    @Override
    public DOMImplementation getDOMImplementation(String string) {
        DOMImplementation dOMImplementation = super.getDOMImplementation(string);
        if (dOMImplementation != null) {
            return dOMImplementation;
        }
        dOMImplementation = PSVIDOMImplementationImpl.getDOMImplementation();
        if (this.testImpl(dOMImplementation, string)) {
            return dOMImplementation;
        }
        dOMImplementation = XSImplementationImpl.getDOMImplementation();
        if (this.testImpl(dOMImplementation, string)) {
            return dOMImplementation;
        }
        return null;
    }

    @Override
    public DOMImplementationList getDOMImplementationList(String string) {
        ArrayList<DOMImplementation> arrayList = new ArrayList<DOMImplementation>();
        DOMImplementationList dOMImplementationList = super.getDOMImplementationList(string);
        for (int i = 0; i < dOMImplementationList.getLength(); ++i) {
            arrayList.add(dOMImplementationList.item(i));
        }
        DOMImplementation dOMImplementation = PSVIDOMImplementationImpl.getDOMImplementation();
        if (this.testImpl(dOMImplementation, string)) {
            arrayList.add(dOMImplementation);
        }
        if (this.testImpl(dOMImplementation = XSImplementationImpl.getDOMImplementation(), string)) {
            arrayList.add(dOMImplementation);
        }
        return new DOMImplementationListImpl(arrayList);
    }
}

