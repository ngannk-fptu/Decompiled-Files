/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.x2006.digsig.STPositiveInteger
 *  com.microsoft.schemas.office.x2006.digsig.STSignatureProviderUrl
 *  com.microsoft.schemas.office.x2006.digsig.STSignatureText
 *  com.microsoft.schemas.office.x2006.digsig.STVersion
 */
package com.microsoft.schemas.office.x2006.digsig;

import com.microsoft.schemas.office.x2006.digsig.STPositiveInteger;
import com.microsoft.schemas.office.x2006.digsig.STSignatureComments;
import com.microsoft.schemas.office.x2006.digsig.STSignatureProviderUrl;
import com.microsoft.schemas.office.x2006.digsig.STSignatureText;
import com.microsoft.schemas.office.x2006.digsig.STSignatureType;
import com.microsoft.schemas.office.x2006.digsig.STUniqueIdentifierWithBraces;
import com.microsoft.schemas.office.x2006.digsig.STVersion;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTSignatureInfoV1
extends XmlObject {
    public static final DocumentFactory<CTSignatureInfoV1> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsignatureinfov13a5ftype");
    public static final SchemaType type = Factory.getType();

    public String getSetupID();

    public STUniqueIdentifierWithBraces xgetSetupID();

    public void setSetupID(String var1);

    public void xsetSetupID(STUniqueIdentifierWithBraces var1);

    public String getSignatureText();

    public STSignatureText xgetSignatureText();

    public void setSignatureText(String var1);

    public void xsetSignatureText(STSignatureText var1);

    public byte[] getSignatureImage();

    public XmlBase64Binary xgetSignatureImage();

    public void setSignatureImage(byte[] var1);

    public void xsetSignatureImage(XmlBase64Binary var1);

    public String getSignatureComments();

    public STSignatureComments xgetSignatureComments();

    public void setSignatureComments(String var1);

    public void xsetSignatureComments(STSignatureComments var1);

    public String getWindowsVersion();

    public STVersion xgetWindowsVersion();

    public void setWindowsVersion(String var1);

    public void xsetWindowsVersion(STVersion var1);

    public String getOfficeVersion();

    public STVersion xgetOfficeVersion();

    public void setOfficeVersion(String var1);

    public void xsetOfficeVersion(STVersion var1);

    public String getApplicationVersion();

    public STVersion xgetApplicationVersion();

    public void setApplicationVersion(String var1);

    public void xsetApplicationVersion(STVersion var1);

    public int getMonitors();

    public STPositiveInteger xgetMonitors();

    public void setMonitors(int var1);

    public void xsetMonitors(STPositiveInteger var1);

    public int getHorizontalResolution();

    public STPositiveInteger xgetHorizontalResolution();

    public void setHorizontalResolution(int var1);

    public void xsetHorizontalResolution(STPositiveInteger var1);

    public int getVerticalResolution();

    public STPositiveInteger xgetVerticalResolution();

    public void setVerticalResolution(int var1);

    public void xsetVerticalResolution(STPositiveInteger var1);

    public int getColorDepth();

    public STPositiveInteger xgetColorDepth();

    public void setColorDepth(int var1);

    public void xsetColorDepth(STPositiveInteger var1);

    public String getSignatureProviderId();

    public STUniqueIdentifierWithBraces xgetSignatureProviderId();

    public void setSignatureProviderId(String var1);

    public void xsetSignatureProviderId(STUniqueIdentifierWithBraces var1);

    public String getSignatureProviderUrl();

    public STSignatureProviderUrl xgetSignatureProviderUrl();

    public void setSignatureProviderUrl(String var1);

    public void xsetSignatureProviderUrl(STSignatureProviderUrl var1);

    public int getSignatureProviderDetails();

    public XmlInt xgetSignatureProviderDetails();

    public void setSignatureProviderDetails(int var1);

    public void xsetSignatureProviderDetails(XmlInt var1);

    public int getSignatureType();

    public STSignatureType xgetSignatureType();

    public void setSignatureType(int var1);

    public void xsetSignatureType(STSignatureType var1);

    public String getDelegateSuggestedSigner();

    public XmlString xgetDelegateSuggestedSigner();

    public boolean isSetDelegateSuggestedSigner();

    public void setDelegateSuggestedSigner(String var1);

    public void xsetDelegateSuggestedSigner(XmlString var1);

    public void unsetDelegateSuggestedSigner();

    public String getDelegateSuggestedSigner2();

    public XmlString xgetDelegateSuggestedSigner2();

    public boolean isSetDelegateSuggestedSigner2();

    public void setDelegateSuggestedSigner2(String var1);

    public void xsetDelegateSuggestedSigner2(XmlString var1);

    public void unsetDelegateSuggestedSigner2();

    public String getDelegateSuggestedSignerEmail();

    public XmlString xgetDelegateSuggestedSignerEmail();

    public boolean isSetDelegateSuggestedSignerEmail();

    public void setDelegateSuggestedSignerEmail(String var1);

    public void xsetDelegateSuggestedSignerEmail(XmlString var1);

    public void unsetDelegateSuggestedSignerEmail();

    public String getManifestHashAlgorithm();

    public XmlAnyURI xgetManifestHashAlgorithm();

    public boolean isSetManifestHashAlgorithm();

    public void setManifestHashAlgorithm(String var1);

    public void xsetManifestHashAlgorithm(XmlAnyURI var1);

    public void unsetManifestHashAlgorithm();
}

