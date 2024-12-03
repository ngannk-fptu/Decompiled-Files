/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Iterator;
import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;

public interface SOAPFault
extends SOAPBodyElement {
    public void setFaultCode(Name var1) throws SOAPException;

    public void setFaultCode(QName var1) throws SOAPException;

    public void setFaultCode(String var1) throws SOAPException;

    public Name getFaultCodeAsName();

    public QName getFaultCodeAsQName();

    public Iterator<QName> getFaultSubcodes();

    public void removeAllFaultSubcodes();

    public void appendFaultSubcode(QName var1) throws SOAPException;

    public String getFaultCode();

    public void setFaultActor(String var1) throws SOAPException;

    public String getFaultActor();

    public void setFaultString(String var1) throws SOAPException;

    public void setFaultString(String var1, Locale var2) throws SOAPException;

    public String getFaultString();

    public Locale getFaultStringLocale();

    public boolean hasDetail();

    public Detail getDetail();

    public Detail addDetail() throws SOAPException;

    public Iterator<Locale> getFaultReasonLocales() throws SOAPException;

    public Iterator<String> getFaultReasonTexts() throws SOAPException;

    public String getFaultReasonText(Locale var1) throws SOAPException;

    public void addFaultReasonText(String var1, Locale var2) throws SOAPException;

    public String getFaultNode();

    public void setFaultNode(String var1) throws SOAPException;

    public String getFaultRole();

    public void setFaultRole(String var1) throws SOAPException;
}

