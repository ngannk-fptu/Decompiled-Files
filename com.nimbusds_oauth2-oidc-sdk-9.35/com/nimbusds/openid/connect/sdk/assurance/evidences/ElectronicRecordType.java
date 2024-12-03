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
public final class ElectronicRecordType
extends Identifier {
    private static final long serialVersionUID = -3135412141332663342L;
    public static final ElectronicRecordType BIRTH_REGISTER = new ElectronicRecordType("birth_register");
    public static final ElectronicRecordType POPULATION_REGISTER = new ElectronicRecordType("population_register");
    public static final ElectronicRecordType VOTER_REGISTER = new ElectronicRecordType("voter_register");
    public static final ElectronicRecordType ADOPTION_REGISTER = new ElectronicRecordType("adoption_register");
    public static final ElectronicRecordType MARRIAGE_REGISTER = new ElectronicRecordType("marriage_register");
    public static final ElectronicRecordType EDUCATION = new ElectronicRecordType("education");
    public static final ElectronicRecordType MILITARY = new ElectronicRecordType("military");
    public static final ElectronicRecordType BANK_ACCOUNT = new ElectronicRecordType("bank_account");
    public static final ElectronicRecordType UTILITY_ACCOUNT = new ElectronicRecordType("utility_account");
    public static final ElectronicRecordType MORTGAGE_ACCOUNT = new ElectronicRecordType("mortgage_account");
    public static final ElectronicRecordType LOAN_ACCOUNT = new ElectronicRecordType("loan_account");
    public static final ElectronicRecordType TAX = new ElectronicRecordType("tax");
    public static final ElectronicRecordType SOCIAL_SECURITY = new ElectronicRecordType("social_security");
    public static final ElectronicRecordType PRISON_RECORD = new ElectronicRecordType("prison_record");

    public ElectronicRecordType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ElectronicRecordType && this.toString().equals(object.toString());
    }
}

