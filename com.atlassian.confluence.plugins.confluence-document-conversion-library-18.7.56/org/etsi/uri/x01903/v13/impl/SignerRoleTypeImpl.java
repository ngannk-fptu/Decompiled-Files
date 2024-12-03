/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.CertifiedRolesListType
 */
package org.etsi.uri.x01903.v13.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.CertifiedRolesListType;
import org.etsi.uri.x01903.v13.ClaimedRolesListType;
import org.etsi.uri.x01903.v13.SignerRoleType;

public class SignerRoleTypeImpl
extends XmlComplexContentImpl
implements SignerRoleType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "ClaimedRoles"), new QName("http://uri.etsi.org/01903/v1.3.2#", "CertifiedRoles")};

    public SignerRoleTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ClaimedRolesListType getClaimedRoles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ClaimedRolesListType target = null;
            target = (ClaimedRolesListType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetClaimedRoles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setClaimedRoles(ClaimedRolesListType claimedRoles) {
        this.generatedSetterHelperImpl(claimedRoles, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ClaimedRolesListType addNewClaimedRoles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ClaimedRolesListType target = null;
            target = (ClaimedRolesListType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetClaimedRoles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertifiedRolesListType getCertifiedRoles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertifiedRolesListType target = null;
            target = (CertifiedRolesListType)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCertifiedRoles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setCertifiedRoles(CertifiedRolesListType certifiedRoles) {
        this.generatedSetterHelperImpl((XmlObject)certifiedRoles, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertifiedRolesListType addNewCertifiedRoles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertifiedRolesListType target = null;
            target = (CertifiedRolesListType)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCertifiedRoles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }
}

