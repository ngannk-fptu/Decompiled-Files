/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.langtag.LangTag
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.assurance.claims.Birthplace;
import com.nimbusds.openid.connect.sdk.assurance.claims.CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.MSISDN;
import com.nimbusds.openid.connect.sdk.claims.Address;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.Gender;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONObject;

public class PersonClaims
extends ClaimsSet {
    public static final String NAME_CLAIM_NAME = "name";
    public static final String GIVEN_NAME_CLAIM_NAME = "given_name";
    public static final String FAMILY_NAME_CLAIM_NAME = "family_name";
    public static final String MIDDLE_NAME_CLAIM_NAME = "middle_name";
    public static final String NICKNAME_CLAIM_NAME = "nickname";
    public static final String PREFERRED_USERNAME_CLAIM_NAME = "preferred_username";
    public static final String PROFILE_CLAIM_NAME = "profile";
    public static final String PICTURE_CLAIM_NAME = "picture";
    public static final String WEBSITE_CLAIM_NAME = "website";
    public static final String EMAIL_CLAIM_NAME = "email";
    public static final String EMAIL_VERIFIED_CLAIM_NAME = "email_verified";
    public static final String GENDER_CLAIM_NAME = "gender";
    public static final String BIRTHDATE_CLAIM_NAME = "birthdate";
    public static final String ZONEINFO_CLAIM_NAME = "zoneinfo";
    public static final String LOCALE_CLAIM_NAME = "locale";
    public static final String PHONE_NUMBER_CLAIM_NAME = "phone_number";
    public static final String PHONE_NUMBER_VERIFIED_CLAIM_NAME = "phone_number_verified";
    public static final String ADDRESS_CLAIM_NAME = "address";
    public static final String UPDATED_AT_CLAIM_NAME = "updated_at";
    @Deprecated
    public static final String BIRTHPLACE_CLAIM_NAME = "birthplace";
    public static final String PLACE_OF_BIRTH_CLAIM_NAME = "place_of_birth";
    public static final String NATIONALITIES_CLAIM_NAME = "nationalities";
    public static final String BIRTH_FAMILY_NAME_CLAIM_NAME = "birth_family_name";
    public static final String BIRTH_GIVEN_NAME_CLAIM_NAME = "birth_given_name";
    public static final String BIRTH_MIDDLE_NAME_CLAIM_NAME = "birth_middle_name";
    public static final String SALUTATION_CLAIM_NAME = "salutation";
    public static final String TITLE_CLAIM_NAME = "title";
    public static final String MSISDN_CLAIM_NAME = "msisdn";
    public static final String ALSO_KNOWN_AS = "also_known_as";

    public static Set<String> getStandardClaimNames() {
        HashSet<String> names = new HashSet<String>(ClaimsSet.getStandardClaimNames());
        names.addAll(Arrays.asList(NAME_CLAIM_NAME, GIVEN_NAME_CLAIM_NAME, FAMILY_NAME_CLAIM_NAME, MIDDLE_NAME_CLAIM_NAME, NICKNAME_CLAIM_NAME, PREFERRED_USERNAME_CLAIM_NAME, PROFILE_CLAIM_NAME, PICTURE_CLAIM_NAME, WEBSITE_CLAIM_NAME, EMAIL_CLAIM_NAME, EMAIL_VERIFIED_CLAIM_NAME, GENDER_CLAIM_NAME, BIRTHDATE_CLAIM_NAME, ZONEINFO_CLAIM_NAME, LOCALE_CLAIM_NAME, PHONE_NUMBER_CLAIM_NAME, PHONE_NUMBER_VERIFIED_CLAIM_NAME, ADDRESS_CLAIM_NAME, UPDATED_AT_CLAIM_NAME, BIRTHPLACE_CLAIM_NAME, PLACE_OF_BIRTH_CLAIM_NAME, NATIONALITIES_CLAIM_NAME, BIRTH_FAMILY_NAME_CLAIM_NAME, BIRTH_GIVEN_NAME_CLAIM_NAME, BIRTH_MIDDLE_NAME_CLAIM_NAME, SALUTATION_CLAIM_NAME, TITLE_CLAIM_NAME, MSISDN_CLAIM_NAME, ALSO_KNOWN_AS));
        return Collections.unmodifiableSet(names);
    }

    public PersonClaims() {
        this(new JSONObject());
    }

    public PersonClaims(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getName() {
        return this.getStringClaim(NAME_CLAIM_NAME);
    }

    public String getName(LangTag langTag) {
        return this.getStringClaim(NAME_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getNameEntries() {
        return this.getLangTaggedClaim(NAME_CLAIM_NAME, String.class);
    }

    public void setName(String name) {
        this.setClaim(NAME_CLAIM_NAME, name);
    }

    public void setName(String name, LangTag langTag) {
        this.setClaim(NAME_CLAIM_NAME, name, langTag);
    }

    public String getGivenName() {
        return this.getStringClaim(GIVEN_NAME_CLAIM_NAME);
    }

    public String getGivenName(LangTag langTag) {
        return this.getStringClaim(GIVEN_NAME_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getGivenNameEntries() {
        return this.getLangTaggedClaim(GIVEN_NAME_CLAIM_NAME, String.class);
    }

    public void setGivenName(String givenName) {
        this.setClaim(GIVEN_NAME_CLAIM_NAME, givenName);
    }

    public void setGivenName(String givenName, LangTag langTag) {
        this.setClaim(GIVEN_NAME_CLAIM_NAME, givenName, langTag);
    }

    public String getFamilyName() {
        return this.getStringClaim(FAMILY_NAME_CLAIM_NAME);
    }

    public String getFamilyName(LangTag langTag) {
        return this.getStringClaim(FAMILY_NAME_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getFamilyNameEntries() {
        return this.getLangTaggedClaim(FAMILY_NAME_CLAIM_NAME, String.class);
    }

    public void setFamilyName(String familyName) {
        this.setClaim(FAMILY_NAME_CLAIM_NAME, familyName);
    }

    public void setFamilyName(String familyName, LangTag langTag) {
        this.setClaim(FAMILY_NAME_CLAIM_NAME, familyName, langTag);
    }

    public String getMiddleName() {
        return this.getStringClaim(MIDDLE_NAME_CLAIM_NAME);
    }

    public String getMiddleName(LangTag langTag) {
        return this.getStringClaim(MIDDLE_NAME_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getMiddleNameEntries() {
        return this.getLangTaggedClaim(MIDDLE_NAME_CLAIM_NAME, String.class);
    }

    public void setMiddleName(String middleName) {
        this.setClaim(MIDDLE_NAME_CLAIM_NAME, middleName);
    }

    public void setMiddleName(String middleName, LangTag langTag) {
        this.setClaim(MIDDLE_NAME_CLAIM_NAME, middleName, langTag);
    }

    public String getNickname() {
        return this.getStringClaim(NICKNAME_CLAIM_NAME);
    }

    public String getNickname(LangTag langTag) {
        return this.getStringClaim(NICKNAME_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getNicknameEntries() {
        return this.getLangTaggedClaim(NICKNAME_CLAIM_NAME, String.class);
    }

    public void setNickname(String nickname) {
        this.setClaim(NICKNAME_CLAIM_NAME, nickname);
    }

    public void setNickname(String nickname, LangTag langTag) {
        this.setClaim(NICKNAME_CLAIM_NAME, nickname, langTag);
    }

    public String getPreferredUsername() {
        return this.getStringClaim(PREFERRED_USERNAME_CLAIM_NAME);
    }

    public void setPreferredUsername(String preferredUsername) {
        this.setClaim(PREFERRED_USERNAME_CLAIM_NAME, preferredUsername);
    }

    public URI getProfile() {
        return this.getURIClaim(PROFILE_CLAIM_NAME);
    }

    public void setProfile(URI profile) {
        this.setURIClaim(PROFILE_CLAIM_NAME, profile);
    }

    public URI getPicture() {
        return this.getURIClaim(PICTURE_CLAIM_NAME);
    }

    public void setPicture(URI picture) {
        this.setURIClaim(PICTURE_CLAIM_NAME, picture);
    }

    public URI getWebsite() {
        return this.getURIClaim(WEBSITE_CLAIM_NAME);
    }

    public void setWebsite(URI website) {
        this.setURIClaim(WEBSITE_CLAIM_NAME, website);
    }

    public String getEmailAddress() {
        return this.getStringClaim(EMAIL_CLAIM_NAME);
    }

    public void setEmailAddress(String email) {
        this.setClaim(EMAIL_CLAIM_NAME, email);
    }

    public Boolean getEmailVerified() {
        return this.getBooleanClaim(EMAIL_VERIFIED_CLAIM_NAME);
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.setClaim(EMAIL_VERIFIED_CLAIM_NAME, emailVerified);
    }

    public Gender getGender() {
        String value = this.getStringClaim(GENDER_CLAIM_NAME);
        if (value == null) {
            return null;
        }
        return new Gender(value);
    }

    public void setGender(Gender gender) {
        if (gender != null) {
            this.setClaim(GENDER_CLAIM_NAME, gender.getValue());
        } else {
            this.setClaim(GENDER_CLAIM_NAME, null);
        }
    }

    public String getBirthdate() {
        return this.getStringClaim(BIRTHDATE_CLAIM_NAME);
    }

    public void setBirthdate(String birthdate) {
        this.setClaim(BIRTHDATE_CLAIM_NAME, birthdate);
    }

    public String getZoneinfo() {
        return this.getStringClaim(ZONEINFO_CLAIM_NAME);
    }

    public void setZoneinfo(String zoneinfo) {
        this.setClaim(ZONEINFO_CLAIM_NAME, zoneinfo);
    }

    public String getLocale() {
        return this.getStringClaim(LOCALE_CLAIM_NAME);
    }

    public void setLocale(String locale) {
        this.setClaim(LOCALE_CLAIM_NAME, locale);
    }

    public String getPhoneNumber() {
        return this.getStringClaim(PHONE_NUMBER_CLAIM_NAME);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.setClaim(PHONE_NUMBER_CLAIM_NAME, phoneNumber);
    }

    public Boolean getPhoneNumberVerified() {
        return this.getBooleanClaim(PHONE_NUMBER_VERIFIED_CLAIM_NAME);
    }

    public void setPhoneNumberVerified(Boolean phoneNumberVerified) {
        this.setClaim(PHONE_NUMBER_VERIFIED_CLAIM_NAME, phoneNumberVerified);
    }

    public Address getAddress() {
        return this.getAddress(null);
    }

    public Address getAddress(LangTag langTag) {
        String name = langTag != null ? "address#" + langTag : ADDRESS_CLAIM_NAME;
        JSONObject jsonObject = this.getClaim(name, JSONObject.class);
        if (jsonObject == null) {
            return null;
        }
        return new Address(jsonObject);
    }

    public Map<LangTag, Address> getAddressEntries() {
        Map<LangTag, JSONObject> entriesIn = this.getLangTaggedClaim(ADDRESS_CLAIM_NAME, JSONObject.class);
        HashMap<LangTag, Address> entriesOut = new HashMap<LangTag, Address>();
        for (Map.Entry<LangTag, JSONObject> en : entriesIn.entrySet()) {
            entriesOut.put(en.getKey(), new Address(en.getValue()));
        }
        return entriesOut;
    }

    public void setAddress(Address address) {
        if (address != null) {
            this.setClaim(ADDRESS_CLAIM_NAME, address.toJSONObject());
        } else {
            this.setClaim(ADDRESS_CLAIM_NAME, null);
        }
    }

    public void setAddress(Address address, LangTag langTag) {
        String key;
        String string = key = langTag == null ? ADDRESS_CLAIM_NAME : "address#" + langTag;
        if (address != null) {
            this.setClaim(key, address.toJSONObject());
        } else {
            this.setClaim(key, null);
        }
    }

    public Date getUpdatedTime() {
        return this.getDateClaim(UPDATED_AT_CLAIM_NAME);
    }

    public void setUpdatedTime(Date updatedTime) {
        this.setDateClaim(UPDATED_AT_CLAIM_NAME, updatedTime);
    }

    @Deprecated
    public Birthplace getBirthplace() {
        JSONObject jsonObject = this.getClaim(BIRTHPLACE_CLAIM_NAME, JSONObject.class);
        if (jsonObject == null) {
            return null;
        }
        return new Birthplace(jsonObject);
    }

    @Deprecated
    public void setBirthplace(Birthplace birthplace) {
        if (birthplace != null) {
            this.setClaim(BIRTHPLACE_CLAIM_NAME, birthplace.toJSONObject());
        }
    }

    public Birthplace getPlaceOfBirth() {
        JSONObject jsonObject = this.getClaim(PLACE_OF_BIRTH_CLAIM_NAME, JSONObject.class);
        if (jsonObject == null) {
            return null;
        }
        return new Birthplace(jsonObject);
    }

    public void setPlaceOfBirth(Birthplace birthplace) {
        if (birthplace != null) {
            this.setClaim(PLACE_OF_BIRTH_CLAIM_NAME, birthplace.toJSONObject());
        }
    }

    public List<CountryCode> getNationalities() {
        List<String> values = this.getStringListClaim(NATIONALITIES_CLAIM_NAME);
        if (values == null) {
            return null;
        }
        LinkedList<CountryCode> codes = new LinkedList<CountryCode>();
        for (String v : values) {
            if (v == null) continue;
            try {
                codes.add(CountryCode.parse(v));
            }
            catch (ParseException e) {
                return null;
            }
        }
        return codes;
    }

    public void setNationalities(List<CountryCode> nationalities) {
        LinkedList<String> values = null;
        if (nationalities != null) {
            values = new LinkedList<String>();
            for (CountryCode code : nationalities) {
                if (code == null) continue;
                values.add(code.getValue());
            }
        }
        this.setClaim(NATIONALITIES_CLAIM_NAME, values);
    }

    public String getBirthFamilyName() {
        return this.getStringClaim(BIRTH_FAMILY_NAME_CLAIM_NAME);
    }

    public String getBirthFamilyName(LangTag langTag) {
        return this.getStringClaim(BIRTH_FAMILY_NAME_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getBirthFamilyNameEntries() {
        return this.getLangTaggedClaim(BIRTH_FAMILY_NAME_CLAIM_NAME, String.class);
    }

    public void setBirthFamilyName(String birthFamilyName) {
        this.setClaim(BIRTH_FAMILY_NAME_CLAIM_NAME, birthFamilyName);
    }

    public void setBirthFamilyName(String birthFamilyName, LangTag langTag) {
        this.setClaim(BIRTH_FAMILY_NAME_CLAIM_NAME, birthFamilyName, langTag);
    }

    public String getBirthGivenName() {
        return this.getStringClaim(BIRTH_GIVEN_NAME_CLAIM_NAME);
    }

    public String getBirthGivenName(LangTag langTag) {
        return this.getStringClaim(BIRTH_GIVEN_NAME_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getBirthGivenNameEntries() {
        return this.getLangTaggedClaim(BIRTH_GIVEN_NAME_CLAIM_NAME, String.class);
    }

    public void setBirthGivenName(String birthGivenName) {
        this.setClaim(BIRTH_GIVEN_NAME_CLAIM_NAME, birthGivenName);
    }

    public void setBirthGivenName(String birthGivenName, LangTag langTag) {
        this.setClaim(BIRTH_GIVEN_NAME_CLAIM_NAME, birthGivenName, langTag);
    }

    public String getBirthMiddleName() {
        return this.getStringClaim(BIRTH_MIDDLE_NAME_CLAIM_NAME);
    }

    public String getBirthMiddleName(LangTag langTag) {
        return this.getStringClaim(BIRTH_MIDDLE_NAME_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getBirthMiddleNameEntries() {
        return this.getLangTaggedClaim(BIRTH_MIDDLE_NAME_CLAIM_NAME, String.class);
    }

    public void setBirthMiddleName(String birthMiddleName) {
        this.setClaim(BIRTH_MIDDLE_NAME_CLAIM_NAME, birthMiddleName);
    }

    public void setBirthMiddleName(String birthMiddleName, LangTag langTag) {
        this.setClaim(BIRTH_MIDDLE_NAME_CLAIM_NAME, birthMiddleName, langTag);
    }

    public String getSalutation() {
        return this.getStringClaim(SALUTATION_CLAIM_NAME);
    }

    public String getSalutation(LangTag langTag) {
        return this.getStringClaim(SALUTATION_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getSalutationEntries() {
        return this.getLangTaggedClaim(SALUTATION_CLAIM_NAME, String.class);
    }

    public void setSalutation(String salutation) {
        this.setClaim(SALUTATION_CLAIM_NAME, salutation);
    }

    public void setSalutation(String salutation, LangTag langTag) {
        this.setClaim(SALUTATION_CLAIM_NAME, salutation, langTag);
    }

    public String getTitle() {
        return this.getStringClaim(TITLE_CLAIM_NAME);
    }

    public String getTitle(LangTag langTag) {
        return this.getStringClaim(TITLE_CLAIM_NAME, langTag);
    }

    public Map<LangTag, String> getTitleEntries() {
        return this.getLangTaggedClaim(TITLE_CLAIM_NAME, String.class);
    }

    public void setTitle(String title) {
        this.setClaim(TITLE_CLAIM_NAME, title);
    }

    public void setTitle(String title, LangTag langTag) {
        this.setClaim(TITLE_CLAIM_NAME, title, langTag);
    }

    public MSISDN getMSISDN() {
        String value = this.getStringClaim(MSISDN_CLAIM_NAME);
        if (value == null) {
            return null;
        }
        try {
            return MSISDN.parse(value);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setMSISDN(MSISDN msisdn) {
        String value = msisdn != null ? msisdn.getValue() : null;
        this.setClaim(MSISDN_CLAIM_NAME, value);
    }

    public String getAlsoKnownAs() {
        return this.getStringClaim(ALSO_KNOWN_AS);
    }

    public String getAlsoKnownAs(LangTag langTag) {
        return this.getStringClaim(ALSO_KNOWN_AS, langTag);
    }

    public Map<LangTag, String> getAlsoKnownAsEntries() {
        return this.getLangTaggedClaim(ALSO_KNOWN_AS, String.class);
    }

    public void setAlsoKnownAs(String alsoKnownAs) {
        this.setClaim(ALSO_KNOWN_AS, alsoKnownAs);
    }

    public void setAlsoKnownAs(String alsoKnownAs, LangTag langTag) {
        this.setClaim(ALSO_KNOWN_AS, alsoKnownAs, langTag);
    }
}

