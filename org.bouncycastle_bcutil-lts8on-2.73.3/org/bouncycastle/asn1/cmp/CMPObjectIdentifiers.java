/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface CMPObjectIdentifiers {
    public static final ASN1ObjectIdentifier passwordBasedMac = new ASN1ObjectIdentifier("1.2.840.113533.7.66.13");
    public static final ASN1ObjectIdentifier dhBasedMac = new ASN1ObjectIdentifier("1.2.840.113533.7.66.30");
    public static final ASN1ObjectIdentifier it_caProtEncCert = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.1");
    public static final ASN1ObjectIdentifier it_signKeyPairTypes = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.2");
    public static final ASN1ObjectIdentifier it_encKeyPairTypes = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.3");
    public static final ASN1ObjectIdentifier it_preferredSymAlg = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.4");
    public static final ASN1ObjectIdentifier it_caKeyUpdateInfo = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.5");
    public static final ASN1ObjectIdentifier it_currentCRL = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.6");
    public static final ASN1ObjectIdentifier it_unsupportedOIDs = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.7");
    public static final ASN1ObjectIdentifier it_keyPairParamReq = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.10");
    public static final ASN1ObjectIdentifier it_keyPairParamRep = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.11");
    public static final ASN1ObjectIdentifier it_revPassphrase = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.12");
    public static final ASN1ObjectIdentifier it_implicitConfirm = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.13");
    public static final ASN1ObjectIdentifier it_confirmWaitTime = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.14");
    public static final ASN1ObjectIdentifier it_origPKIMessage = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.15");
    public static final ASN1ObjectIdentifier it_suppLangTags = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.16");
    public static final ASN1ObjectIdentifier id_it_caCerts = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.17");
    public static final ASN1ObjectIdentifier id_it_rootCaKeyUpdate = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.18");
    public static final ASN1ObjectIdentifier id_it_certReqTemplate = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.19");
    public static final ASN1ObjectIdentifier id_it_rootCaCert = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.20");
    public static final ASN1ObjectIdentifier id_it_certProfile = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.21");
    public static final ASN1ObjectIdentifier id_it_crlStatusList = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.22");
    public static final ASN1ObjectIdentifier id_it_crls = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.4.23");
    public static final ASN1ObjectIdentifier id_pkip = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5");
    public static final ASN1ObjectIdentifier id_regCtrl = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.1");
    public static final ASN1ObjectIdentifier id_regInfo = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.2");
    public static final ASN1ObjectIdentifier regCtrl_regToken = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.1.1");
    public static final ASN1ObjectIdentifier regCtrl_authenticator = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.1.2");
    public static final ASN1ObjectIdentifier regCtrl_pkiPublicationInfo = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.1.3");
    public static final ASN1ObjectIdentifier regCtrl_pkiArchiveOptions = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.1.4");
    public static final ASN1ObjectIdentifier regCtrl_oldCertID = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.1.5");
    public static final ASN1ObjectIdentifier regCtrl_protocolEncrKey = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.1.6");
    public static final ASN1ObjectIdentifier regCtrl_altCertTemplate = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.1.7");
    public static final ASN1ObjectIdentifier regInfo_utf8Pairs = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.2.1");
    public static final ASN1ObjectIdentifier regInfo_certReq = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.5.2.2");
    public static final ASN1ObjectIdentifier ct_encKeyWithID = new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.1.21");
    public static final ASN1ObjectIdentifier id_regCtrl_algId = id_pkip.branch("1.11");
    public static final ASN1ObjectIdentifier id_regCtrl_rsaKeyLen = id_pkip.branch("1.12");
}

