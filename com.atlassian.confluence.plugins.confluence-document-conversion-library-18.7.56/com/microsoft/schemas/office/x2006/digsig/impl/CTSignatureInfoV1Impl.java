/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.x2006.digsig.STPositiveInteger
 *  com.microsoft.schemas.office.x2006.digsig.STSignatureProviderUrl
 *  com.microsoft.schemas.office.x2006.digsig.STSignatureText
 *  com.microsoft.schemas.office.x2006.digsig.STVersion
 */
package com.microsoft.schemas.office.x2006.digsig.impl;

import com.microsoft.schemas.office.x2006.digsig.CTSignatureInfoV1;
import com.microsoft.schemas.office.x2006.digsig.STPositiveInteger;
import com.microsoft.schemas.office.x2006.digsig.STSignatureComments;
import com.microsoft.schemas.office.x2006.digsig.STSignatureProviderUrl;
import com.microsoft.schemas.office.x2006.digsig.STSignatureText;
import com.microsoft.schemas.office.x2006.digsig.STSignatureType;
import com.microsoft.schemas.office.x2006.digsig.STUniqueIdentifierWithBraces;
import com.microsoft.schemas.office.x2006.digsig.STVersion;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSignatureInfoV1Impl
extends XmlComplexContentImpl
implements CTSignatureInfoV1 {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/2006/digsig", "SetupID"), new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureText"), new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureImage"), new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureComments"), new QName("http://schemas.microsoft.com/office/2006/digsig", "WindowsVersion"), new QName("http://schemas.microsoft.com/office/2006/digsig", "OfficeVersion"), new QName("http://schemas.microsoft.com/office/2006/digsig", "ApplicationVersion"), new QName("http://schemas.microsoft.com/office/2006/digsig", "Monitors"), new QName("http://schemas.microsoft.com/office/2006/digsig", "HorizontalResolution"), new QName("http://schemas.microsoft.com/office/2006/digsig", "VerticalResolution"), new QName("http://schemas.microsoft.com/office/2006/digsig", "ColorDepth"), new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureProviderId"), new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureProviderUrl"), new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureProviderDetails"), new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureType"), new QName("http://schemas.microsoft.com/office/2006/digsig", "DelegateSuggestedSigner"), new QName("http://schemas.microsoft.com/office/2006/digsig", "DelegateSuggestedSigner2"), new QName("http://schemas.microsoft.com/office/2006/digsig", "DelegateSuggestedSignerEmail"), new QName("http://schemas.microsoft.com/office/2006/digsig", "ManifestHashAlgorithm")};

    public CTSignatureInfoV1Impl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getSetupID() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STUniqueIdentifierWithBraces xgetSetupID() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STUniqueIdentifierWithBraces target = null;
            target = (STUniqueIdentifierWithBraces)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSetupID(String setupID) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            }
            target.setStringValue(setupID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSetupID(STUniqueIdentifierWithBraces setupID) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STUniqueIdentifierWithBraces target = null;
            target = (STUniqueIdentifierWithBraces)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            if (target == null) {
                target = (STUniqueIdentifierWithBraces)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            }
            target.set(setupID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getSignatureText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STSignatureText xgetSignatureText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignatureText target = null;
            target = (STSignatureText)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSignatureText(String signatureText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            }
            target.setStringValue(signatureText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSignatureText(STSignatureText signatureText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignatureText target = null;
            target = (STSignatureText)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (STSignatureText)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.set((XmlObject)signatureText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getSignatureImage() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target.getByteArrayValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBase64Binary xgetSignatureImage() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBase64Binary target = null;
            target = (XmlBase64Binary)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSignatureImage(byte[] signatureImage) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            }
            target.setByteArrayValue(signatureImage);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSignatureImage(XmlBase64Binary signatureImage) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBase64Binary target = null;
            target = (XmlBase64Binary)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            if (target == null) {
                target = (XmlBase64Binary)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            }
            target.set(signatureImage);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getSignatureComments() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STSignatureComments xgetSignatureComments() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignatureComments target = null;
            target = (STSignatureComments)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSignatureComments(String signatureComments) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            }
            target.setStringValue(signatureComments);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSignatureComments(STSignatureComments signatureComments) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignatureComments target = null;
            target = (STSignatureComments)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            if (target == null) {
                target = (STSignatureComments)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            }
            target.set(signatureComments);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getWindowsVersion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STVersion xgetWindowsVersion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STVersion target = null;
            target = (STVersion)this.get_store().find_element_user(PROPERTY_QNAME[4], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setWindowsVersion(String windowsVersion) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            }
            target.setStringValue(windowsVersion);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetWindowsVersion(STVersion windowsVersion) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STVersion target = null;
            target = (STVersion)this.get_store().find_element_user(PROPERTY_QNAME[4], 0);
            if (target == null) {
                target = (STVersion)this.get_store().add_element_user(PROPERTY_QNAME[4]);
            }
            target.set((XmlObject)windowsVersion);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getOfficeVersion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STVersion xgetOfficeVersion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STVersion target = null;
            target = (STVersion)this.get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOfficeVersion(String officeVersion) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            }
            target.setStringValue(officeVersion);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetOfficeVersion(STVersion officeVersion) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STVersion target = null;
            target = (STVersion)this.get_store().find_element_user(PROPERTY_QNAME[5], 0);
            if (target == null) {
                target = (STVersion)this.get_store().add_element_user(PROPERTY_QNAME[5]);
            }
            target.set((XmlObject)officeVersion);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getApplicationVersion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STVersion xgetApplicationVersion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STVersion target = null;
            target = (STVersion)this.get_store().find_element_user(PROPERTY_QNAME[6], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setApplicationVersion(String applicationVersion) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            }
            target.setStringValue(applicationVersion);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetApplicationVersion(STVersion applicationVersion) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STVersion target = null;
            target = (STVersion)this.get_store().find_element_user(PROPERTY_QNAME[6], 0);
            if (target == null) {
                target = (STVersion)this.get_store().add_element_user(PROPERTY_QNAME[6]);
            }
            target.set((XmlObject)applicationVersion);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getMonitors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPositiveInteger xgetMonitors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveInteger target = null;
            target = (STPositiveInteger)this.get_store().find_element_user(PROPERTY_QNAME[7], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMonitors(int monitors) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            }
            target.setIntValue(monitors);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMonitors(STPositiveInteger monitors) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveInteger target = null;
            target = (STPositiveInteger)this.get_store().find_element_user(PROPERTY_QNAME[7], 0);
            if (target == null) {
                target = (STPositiveInteger)this.get_store().add_element_user(PROPERTY_QNAME[7]);
            }
            target.set((XmlObject)monitors);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getHorizontalResolution() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPositiveInteger xgetHorizontalResolution() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveInteger target = null;
            target = (STPositiveInteger)this.get_store().find_element_user(PROPERTY_QNAME[8], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHorizontalResolution(int horizontalResolution) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            }
            target.setIntValue(horizontalResolution);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHorizontalResolution(STPositiveInteger horizontalResolution) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveInteger target = null;
            target = (STPositiveInteger)this.get_store().find_element_user(PROPERTY_QNAME[8], 0);
            if (target == null) {
                target = (STPositiveInteger)this.get_store().add_element_user(PROPERTY_QNAME[8]);
            }
            target.set((XmlObject)horizontalResolution);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getVerticalResolution() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPositiveInteger xgetVerticalResolution() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveInteger target = null;
            target = (STPositiveInteger)this.get_store().find_element_user(PROPERTY_QNAME[9], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVerticalResolution(int verticalResolution) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            }
            target.setIntValue(verticalResolution);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetVerticalResolution(STPositiveInteger verticalResolution) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveInteger target = null;
            target = (STPositiveInteger)this.get_store().find_element_user(PROPERTY_QNAME[9], 0);
            if (target == null) {
                target = (STPositiveInteger)this.get_store().add_element_user(PROPERTY_QNAME[9]);
            }
            target.set((XmlObject)verticalResolution);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getColorDepth() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPositiveInteger xgetColorDepth() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveInteger target = null;
            target = (STPositiveInteger)this.get_store().find_element_user(PROPERTY_QNAME[10], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setColorDepth(int colorDepth) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            }
            target.setIntValue(colorDepth);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColorDepth(STPositiveInteger colorDepth) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositiveInteger target = null;
            target = (STPositiveInteger)this.get_store().find_element_user(PROPERTY_QNAME[10], 0);
            if (target == null) {
                target = (STPositiveInteger)this.get_store().add_element_user(PROPERTY_QNAME[10]);
            }
            target.set((XmlObject)colorDepth);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getSignatureProviderId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STUniqueIdentifierWithBraces xgetSignatureProviderId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STUniqueIdentifierWithBraces target = null;
            target = (STUniqueIdentifierWithBraces)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSignatureProviderId(String signatureProviderId) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            }
            target.setStringValue(signatureProviderId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSignatureProviderId(STUniqueIdentifierWithBraces signatureProviderId) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STUniqueIdentifierWithBraces target = null;
            target = (STUniqueIdentifierWithBraces)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            if (target == null) {
                target = (STUniqueIdentifierWithBraces)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            }
            target.set(signatureProviderId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getSignatureProviderUrl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STSignatureProviderUrl xgetSignatureProviderUrl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignatureProviderUrl target = null;
            target = (STSignatureProviderUrl)this.get_store().find_element_user(PROPERTY_QNAME[12], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSignatureProviderUrl(String signatureProviderUrl) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            }
            target.setStringValue(signatureProviderUrl);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSignatureProviderUrl(STSignatureProviderUrl signatureProviderUrl) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignatureProviderUrl target = null;
            target = (STSignatureProviderUrl)this.get_store().find_element_user(PROPERTY_QNAME[12], 0);
            if (target == null) {
                target = (STSignatureProviderUrl)this.get_store().add_element_user(PROPERTY_QNAME[12]);
            }
            target.set((XmlObject)signatureProviderUrl);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSignatureProviderDetails() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInt xgetSignatureProviderDetails() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInt target = null;
            target = (XmlInt)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSignatureProviderDetails(int signatureProviderDetails) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            }
            target.setIntValue(signatureProviderDetails);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSignatureProviderDetails(XmlInt signatureProviderDetails) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInt target = null;
            target = (XmlInt)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
            if (target == null) {
                target = (XmlInt)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            }
            target.set(signatureProviderDetails);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getSignatureType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], 0));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STSignatureType xgetSignatureType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignatureType target = null;
            target = (STSignatureType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSignatureType(int signatureType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            }
            target.setIntValue(signatureType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSignatureType(STSignatureType signatureType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignatureType target = null;
            target = (STSignatureType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], 0));
            if (target == null) {
                target = (STSignatureType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            }
            target.set(signatureType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getDelegateSuggestedSigner() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetDelegateSuggestedSigner() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDelegateSuggestedSigner() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]) != 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDelegateSuggestedSigner(String delegateSuggestedSigner) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            }
            target.setStringValue(delegateSuggestedSigner);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDelegateSuggestedSigner(XmlString delegateSuggestedSigner) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], 0));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            }
            target.set(delegateSuggestedSigner);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDelegateSuggestedSigner() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[15], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getDelegateSuggestedSigner2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetDelegateSuggestedSigner2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDelegateSuggestedSigner2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]) != 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDelegateSuggestedSigner2(String delegateSuggestedSigner2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            }
            target.setStringValue(delegateSuggestedSigner2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDelegateSuggestedSigner2(XmlString delegateSuggestedSigner2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], 0));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            }
            target.set(delegateSuggestedSigner2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDelegateSuggestedSigner2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[16], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getDelegateSuggestedSignerEmail() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetDelegateSuggestedSignerEmail() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDelegateSuggestedSignerEmail() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]) != 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDelegateSuggestedSignerEmail(String delegateSuggestedSignerEmail) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            }
            target.setStringValue(delegateSuggestedSignerEmail);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDelegateSuggestedSignerEmail(XmlString delegateSuggestedSignerEmail) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], 0));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            }
            target.set(delegateSuggestedSignerEmail);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDelegateSuggestedSignerEmail() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[17], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getManifestHashAlgorithm() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], 0));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlAnyURI xgetManifestHashAlgorithm() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetManifestHashAlgorithm() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]) != 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setManifestHashAlgorithm(String manifestHashAlgorithm) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            }
            target.setStringValue(manifestHashAlgorithm);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetManifestHashAlgorithm(XmlAnyURI manifestHashAlgorithm) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], 0));
            if (target == null) {
                target = (XmlAnyURI)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            }
            target.set(manifestHashAlgorithm);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetManifestHashAlgorithm() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[18], 0);
        }
    }
}

