/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Locale;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Document;

public interface SOAPBody
extends SOAPElement {
    public SOAPFault addFault() throws SOAPException;

    public boolean hasFault();

    public SOAPFault getFault();

    public SOAPBodyElement addBodyElement(Name var1) throws SOAPException;

    public SOAPFault addFault(Name var1, String var2, Locale var3) throws SOAPException;

    public SOAPFault addFault(Name var1, String var2) throws SOAPException;

    public SOAPBodyElement addDocument(Document var1) throws SOAPException;
}

