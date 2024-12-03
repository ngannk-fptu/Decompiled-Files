/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDSeedValueCertificate
implements COSObjectable {
    public static final int FLAG_SUBJECT = 1;
    public static final int FLAG_ISSUER = 2;
    public static final int FLAG_OID = 4;
    public static final int FLAG_SUBJECT_DN = 8;
    public static final int FLAG_KEY_USAGE = 32;
    public static final int FLAG_URL = 64;
    private final COSDictionary dictionary;

    public PDSeedValueCertificate() {
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.TYPE, (COSBase)COSName.SV_CERT);
        this.dictionary.setDirect(true);
    }

    public PDSeedValueCertificate(COSDictionary dict) {
        this.dictionary = dict;
        this.dictionary.setDirect(true);
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public boolean isSubjectRequired() {
        return this.dictionary.getFlag(COSName.FF, 1);
    }

    public void setSubjectRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 1, flag);
    }

    public boolean isIssuerRequired() {
        return this.dictionary.getFlag(COSName.FF, 2);
    }

    public void setIssuerRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 2, flag);
    }

    public boolean isOIDRequired() {
        return this.dictionary.getFlag(COSName.FF, 4);
    }

    public void setOIDRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 4, flag);
    }

    public boolean isSubjectDNRequired() {
        return this.dictionary.getFlag(COSName.FF, 8);
    }

    public void setSubjectDNRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 8, flag);
    }

    public boolean isKeyUsageRequired() {
        return this.dictionary.getFlag(COSName.FF, 32);
    }

    public void setKeyUsageRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 32, flag);
    }

    public boolean isURLRequired() {
        return this.dictionary.getFlag(COSName.FF, 64);
    }

    public void setURLRequired(boolean flag) {
        this.dictionary.setFlag(COSName.FF, 64, flag);
    }

    public List<byte[]> getSubject() {
        COSArray array = this.dictionary.getCOSArray(COSName.SUBJECT);
        return array != null ? PDSeedValueCertificate.getListOfByteArraysFromCOSArray(array) : null;
    }

    public void setSubject(List<byte[]> subjects) {
        this.dictionary.setItem(COSName.SUBJECT, (COSBase)PDSeedValueCertificate.convertListOfByteArraysToCOSArray(subjects));
    }

    public void addSubject(byte[] subject) {
        COSArray array = this.dictionary.getCOSArray(COSName.SUBJECT);
        if (array == null) {
            array = new COSArray();
        }
        array.add(new COSString(subject));
        this.dictionary.setItem(COSName.SUBJECT, (COSBase)array);
    }

    public void removeSubject(byte[] subject) {
        COSArray array = this.dictionary.getCOSArray(COSName.SUBJECT);
        if (array != null) {
            array.remove(new COSString(subject));
        }
    }

    public List<Map<String, String>> getSubjectDN() {
        COSArray cosArray = this.dictionary.getCOSArray(COSName.SUBJECT_DN);
        if (cosArray != null) {
            List<? extends COSBase> subjectDNList = cosArray.toList();
            LinkedList<Map<String, String>> result = new LinkedList<Map<String, String>>();
            for (COSBase cOSBase : subjectDNList) {
                if (!(cOSBase instanceof COSDictionary)) continue;
                COSDictionary subjectDNItemDict = (COSDictionary)cOSBase;
                HashMap<String, String> subjectDNMap = new HashMap<String, String>();
                for (COSName key : subjectDNItemDict.keySet()) {
                    subjectDNMap.put(key.getName(), subjectDNItemDict.getString(key));
                }
                result.add(subjectDNMap);
            }
            return result;
        }
        return null;
    }

    public void setSubjectDN(List<Map<String, String>> subjectDN) {
        LinkedList<COSDictionary> subjectDNDict = new LinkedList<COSDictionary>();
        for (Map<String, String> subjectDNItem : subjectDN) {
            COSDictionary dict = new COSDictionary();
            for (Map.Entry<String, String> entry : subjectDNItem.entrySet()) {
                dict.setItem(entry.getKey(), (COSBase)new COSString(entry.getValue()));
            }
            subjectDNDict.add(dict);
        }
        this.dictionary.setItem(COSName.SUBJECT_DN, (COSBase)COSArrayList.converterToCOSArray(subjectDNDict));
    }

    public List<String> getKeyUsage() {
        COSArray array = this.dictionary.getCOSArray(COSName.KEY_USAGE);
        if (array != null) {
            LinkedList<String> keyUsageExtensions = new LinkedList<String>();
            for (COSBase item : array) {
                if (!(item instanceof COSString)) continue;
                keyUsageExtensions.add(((COSString)item).getString());
            }
            return keyUsageExtensions;
        }
        return null;
    }

    public void setKeyUsage(List<String> keyUsageExtensions) {
        this.dictionary.setItem(COSName.KEY_USAGE, (COSBase)COSArrayList.converterToCOSArray(keyUsageExtensions));
    }

    public void addKeyUsage(String keyUsageExtension) {
        String allowedChars = "01X";
        for (int c = 0; c < keyUsageExtension.length(); ++c) {
            if (allowedChars.indexOf(keyUsageExtension.charAt(c)) != -1) continue;
            throw new IllegalArgumentException("characters can only be 0, 1, X");
        }
        COSArray array = this.dictionary.getCOSArray(COSName.KEY_USAGE);
        if (array == null) {
            array = new COSArray();
        }
        array.add(new COSString(keyUsageExtension));
        this.dictionary.setItem(COSName.KEY_USAGE, (COSBase)array);
    }

    public void addKeyUsage(char digitalSignature, char nonRepudiation, char keyEncipherment, char dataEncipherment, char keyAgreement, char keyCertSign, char cRLSign, char encipherOnly, char decipherOnly) {
        String string = "" + digitalSignature + nonRepudiation + keyEncipherment + dataEncipherment + keyAgreement + keyCertSign + cRLSign + encipherOnly + decipherOnly;
        this.addKeyUsage(string);
    }

    public void removeKeyUsage(String keyUsageExtension) {
        COSArray array = this.dictionary.getCOSArray(COSName.KEY_USAGE);
        if (array != null) {
            array.remove(new COSString(keyUsageExtension));
        }
    }

    public List<byte[]> getIssuer() {
        COSArray array = this.dictionary.getCOSArray(COSName.ISSUER);
        return array != null ? PDSeedValueCertificate.getListOfByteArraysFromCOSArray(array) : null;
    }

    public void setIssuer(List<byte[]> issuers) {
        this.dictionary.setItem(COSName.ISSUER, (COSBase)PDSeedValueCertificate.convertListOfByteArraysToCOSArray(issuers));
    }

    public void addIssuer(byte[] issuer) {
        COSArray array = this.dictionary.getCOSArray(COSName.ISSUER);
        if (array == null) {
            array = new COSArray();
        }
        array.add(new COSString(issuer));
        this.dictionary.setItem(COSName.ISSUER, (COSBase)array);
    }

    public void removeIssuer(byte[] issuer) {
        COSArray array = this.dictionary.getCOSArray(COSName.ISSUER);
        if (array != null) {
            array.remove(new COSString(issuer));
        }
    }

    public List<byte[]> getOID() {
        COSArray array = this.dictionary.getCOSArray(COSName.OID);
        return array != null ? PDSeedValueCertificate.getListOfByteArraysFromCOSArray(array) : null;
    }

    public void setOID(List<byte[]> oidByteStrings) {
        this.dictionary.setItem(COSName.OID, (COSBase)PDSeedValueCertificate.convertListOfByteArraysToCOSArray(oidByteStrings));
    }

    public void addOID(byte[] oid) {
        COSArray array = this.dictionary.getCOSArray(COSName.OID);
        if (array == null) {
            array = new COSArray();
        }
        array.add(new COSString(oid));
        this.dictionary.setItem(COSName.OID, (COSBase)array);
    }

    public void removeOID(byte[] oid) {
        COSArray array = this.dictionary.getCOSArray(COSName.OID);
        if (array != null) {
            array.remove(new COSString(oid));
        }
    }

    public String getURL() {
        return this.dictionary.getString(COSName.URL);
    }

    public void setURL(String url) {
        this.dictionary.setString(COSName.URL, url);
    }

    public String getURLType() {
        return this.dictionary.getNameAsString(COSName.URL_TYPE);
    }

    public void setURLType(String urlType) {
        this.dictionary.setName(COSName.URL_TYPE, urlType);
    }

    private static List<byte[]> getListOfByteArraysFromCOSArray(COSArray array) {
        LinkedList<byte[]> result = new LinkedList<byte[]>();
        for (COSBase item : array) {
            if (!(item instanceof COSString)) continue;
            result.add(((COSString)item).getBytes());
        }
        return result;
    }

    private static COSArray convertListOfByteArraysToCOSArray(List<byte[]> strings) {
        COSArray array = new COSArray();
        for (byte[] string : strings) {
            array.add(new COSString(string));
        }
        return array;
    }
}

