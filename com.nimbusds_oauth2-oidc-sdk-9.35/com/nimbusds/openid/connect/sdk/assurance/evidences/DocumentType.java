/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class DocumentType
extends Identifier {
    private static final long serialVersionUID = -6631671451012338520L;
    public static final DocumentType IDCARD = new DocumentType("idcard");
    public static final DocumentType PASSPORT = new DocumentType("passport");
    public static final DocumentType DRIVING_PERMIT = new DocumentType("driving_permit");
    public static final DocumentType RESIDENCE_PERMIT = new DocumentType("residence_permit");
    public static final DocumentType DE_IDCARD_FOREIGNERS = new DocumentType("de_idcard_foreigners");
    public static final DocumentType DE_EMERGENCY_IDCARD = new DocumentType("de_emergency_idcard");
    public static final DocumentType DE_ERP = new DocumentType("de_erp");
    public static final DocumentType DE_ERP_REPLACEMENT_IDCARD = new DocumentType("de_erp_replacement_idcard");
    public static final DocumentType DE_IDCARD_REFUGEES = new DocumentType("de_idcard_refugees");
    public static final DocumentType DE_IDCARD_APATRIDS = new DocumentType("de_idcard_apatrids");
    public static final DocumentType DE_CERTIFICATE_OF_SUSPENSION_OF_DEPORTATION = new DocumentType("de_certificate_of_suspension_of_deportation");
    public static final DocumentType DE_PERMISSION_TO_RESIDE = new DocumentType("de_permission_to_reside");
    public static final DocumentType DE_REPLACEMENT_IDCARD = new DocumentType("de_replacement_idcard");
    public static final DocumentType JP_DRIVERS_LICENSE = new DocumentType("jp_drivers_license");
    public static final DocumentType JP_RESIDENCY_CARD_FOR_FOREIGNER = new DocumentType("jp_residency_card_for_foreigner");
    public static final DocumentType JP_INDIVIDUAL_NUMBER_CARD = new DocumentType("jp_individual_number_card");
    public static final DocumentType JP_PERMANENT_RESIDENCY_CARD_FOR_FOREIGNER = new DocumentType("jp_permanent_residency_card_for_foreigner");
    public static final DocumentType JP_HEALTH_INSURANCE_CARD = new DocumentType("jp_health_insurance_card");
    public static final DocumentType JP_RESIDENCY_CARD = new DocumentType("jp_residency_card");
    public static final DocumentType BANK_STATEMENT = new DocumentType("bank_statement");
    public static final DocumentType UTILITY_STATEMENT = new DocumentType("utility_statement");
    public static final DocumentType MORTGAGE_STATEMENT = new DocumentType("mortgage_statement");
    public static final DocumentType LOAN_STATEMENT = new DocumentType("loan_statement");
    public static final DocumentType TAX_STATEMENT = new DocumentType("tax_statement");
    public static final DocumentType SOCIAL_SECURITY_STATEMENT = new DocumentType("social_security_statement");
    public static final DocumentType PILOT_PERMIT = new DocumentType("pilot_permit");
    public static final DocumentType BIRTH_CERTIFICATE = new DocumentType("birth_certificate");
    public static final DocumentType ADOPTION_CERTIFICATE = new DocumentType("adoption_certificate");
    public static final DocumentType MARRIAGE_CERTIFICATE = new DocumentType("marriage_certificate");
    public static final DocumentType GENDER_CERTIFICATE = new DocumentType("gender_certificate");
    public static final DocumentType FIREARM_PERMIT = new DocumentType("firearm_permit");
    public static final DocumentType EDUCATION_CERTIFICATE = new DocumentType("education_certificate");
    public static final DocumentType VISA = new DocumentType("visa");
    public static final DocumentType MILITARY_ID = new DocumentType("military_id");
    public static final DocumentType VOTER_ID = new DocumentType("voter_id");

    public DocumentType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof DocumentType && this.toString().equals(object.toString());
    }
}

