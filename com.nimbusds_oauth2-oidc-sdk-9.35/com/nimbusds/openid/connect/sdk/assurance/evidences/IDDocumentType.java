/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Deprecated
@Immutable
public final class IDDocumentType
extends Identifier {
    private static final long serialVersionUID = -6631671451012338520L;
    public static final IDDocumentType IDCARD = new IDDocumentType("idcard");
    public static final IDDocumentType PASSPORT = new IDDocumentType("passport");
    public static final IDDocumentType DRIVING_PERMIT = new IDDocumentType("driving_permit");
    public static final IDDocumentType DE_IDCARD_FOREIGNERS = new IDDocumentType("de_idcard_foreigners");
    public static final IDDocumentType DE_EMERGENCY_IDCARD = new IDDocumentType("de_emergency_idcard");
    public static final IDDocumentType DE_ERP = new IDDocumentType("de_erp");
    public static final IDDocumentType DE_ERP_REPLACEMENT_IDCARD = new IDDocumentType("de_erp_replacement_idcard");
    public static final IDDocumentType DE_IDCARD_REFUGEES = new IDDocumentType("de_idcard_refugees");
    public static final IDDocumentType DE_IDCARD_APATRIDS = new IDDocumentType("de_idcard_apatrids");
    public static final IDDocumentType DE_CERTIFICATE_OF_SUSPENSION_OF_DEPORTATION = new IDDocumentType("de_certificate_of_suspension_of_deportation");
    public static final IDDocumentType DE_PERMISSION_TO_RESIDE = new IDDocumentType("de_permission_to_reside");
    public static final IDDocumentType DE_REPLACEMENT_IDCARD = new IDDocumentType("de_replacement_idcard");
    public static final IDDocumentType JP_DRIVERS_LICENSE = new IDDocumentType("jp_drivers_license");
    public static final IDDocumentType JP_RESIDENCY_CARD_FOR_FOREIGNER = new IDDocumentType("jp_residency_card_for_foreigner");
    public static final IDDocumentType JP_INDIVIDUAL_NUMBER_CARD = new IDDocumentType("jp_individual_number_card");
    public static final IDDocumentType JP_PERMANENT_RESIDENCY_CARD_FOR_FOREIGNER = new IDDocumentType("jp_permanent_residency_card_for_foreigner");
    public static final IDDocumentType JP_HEALTH_INSURANCE_CARD = new IDDocumentType("jp_health_insurance_card");
    public static final IDDocumentType JP_RESIDENCY_CARD = new IDDocumentType("jp_residency_card");

    public IDDocumentType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof IDDocumentType && this.toString().equals(object.toString());
    }
}

