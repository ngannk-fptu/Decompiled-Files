/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.util.Locale;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;

public interface SOAPFault
extends SOAPBodyElement {
    public void setFaultCode(String var1) throws SOAPException;

    public String getFaultCode();

    public void setFaultActor(String var1) throws SOAPException;

    public String getFaultActor();

    public void setFaultString(String var1) throws SOAPException;

    public String getFaultString();

    public Detail getDetail();

    public Detail addDetail() throws SOAPException;

    public void setFaultCode(Name var1) throws SOAPException;

    public Name getFaultCodeAsName();

    public void setFaultString(String var1, Locale var2) throws SOAPException;

    public Locale getFaultStringLocale();
}

