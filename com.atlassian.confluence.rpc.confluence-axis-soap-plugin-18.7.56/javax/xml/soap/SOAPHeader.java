/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Iterator;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

public interface SOAPHeader
extends SOAPElement {
    public SOAPHeaderElement addHeaderElement(Name var1) throws SOAPException;

    public Iterator examineHeaderElements(String var1);

    public Iterator extractHeaderElements(String var1);

    public Iterator examineMustUnderstandHeaderElements(String var1);

    public Iterator examineAllHeaderElements();

    public Iterator extractAllHeaderElements();
}

