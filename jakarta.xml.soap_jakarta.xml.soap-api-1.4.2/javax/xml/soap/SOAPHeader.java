/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

public interface SOAPHeader
extends SOAPElement {
    public SOAPHeaderElement addHeaderElement(Name var1) throws SOAPException;

    public SOAPHeaderElement addHeaderElement(QName var1) throws SOAPException;

    public Iterator<SOAPHeaderElement> examineMustUnderstandHeaderElements(String var1);

    public Iterator<SOAPHeaderElement> examineHeaderElements(String var1);

    public Iterator<SOAPHeaderElement> extractHeaderElements(String var1);

    public SOAPHeaderElement addNotUnderstoodHeaderElement(QName var1) throws SOAPException;

    public SOAPHeaderElement addUpgradeHeaderElement(Iterator<String> var1) throws SOAPException;

    public SOAPHeaderElement addUpgradeHeaderElement(String[] var1) throws SOAPException;

    public SOAPHeaderElement addUpgradeHeaderElement(String var1) throws SOAPException;

    public Iterator<SOAPHeaderElement> examineAllHeaderElements();

    public Iterator<SOAPHeaderElement> extractAllHeaderElements();
}

