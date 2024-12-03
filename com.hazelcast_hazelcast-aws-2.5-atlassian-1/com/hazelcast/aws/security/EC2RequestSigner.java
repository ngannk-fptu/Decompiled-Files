/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.util.QuickMath
 */
package com.hazelcast.aws.security;

import com.hazelcast.aws.AwsConfig;
import com.hazelcast.aws.utility.AwsURLEncoder;
import com.hazelcast.util.QuickMath;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EC2RequestSigner {
    private static final String NEW_LINE = "\n";
    private static final String API_TERMINATOR = "aws4_request";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String UTF_8 = "UTF-8";
    private static final int DATE_LENGTH = 8;
    private static final int LAST_INDEX = 8;
    private final AwsConfig config;
    private final String timestamp;
    private String service;
    private Map<String, String> attributes;
    private String endpoint;

    public EC2RequestSigner(AwsConfig config, String timeStamp, String endpoint) {
        this.config = config;
        this.timestamp = timeStamp;
        this.service = null;
        this.endpoint = endpoint;
    }

    public String getCredentialScope() {
        String dateStamp = this.timestamp.substring(0, 8);
        return String.format("%s/%s/%s/%s", dateStamp, this.config.getRegion(), this.service, API_TERMINATOR);
    }

    public String getSignedHeaders() {
        return "host";
    }

    public String sign(String service, Map<String, String> attributes) {
        this.service = service;
        this.attributes = attributes;
        String canonicalRequest = this.getCanonicalizedRequest();
        String stringToSign = this.createStringToSign(canonicalRequest);
        byte[] signingKey = this.deriveSigningKey();
        return this.createSignature(stringToSign, signingKey);
    }

    private String getCanonicalizedRequest() {
        return "GET\n/\n" + this.getCanonicalizedQueryString(this.attributes) + NEW_LINE + this.getCanonicalHeaders() + NEW_LINE + this.getSignedHeaders() + NEW_LINE + this.sha256Hashhex("");
    }

    private String createStringToSign(String canonicalRequest) {
        return "AWS4-HMAC-SHA256\n" + this.timestamp + NEW_LINE + this.getCredentialScope() + NEW_LINE + this.sha256Hashhex(canonicalRequest);
    }

    private byte[] deriveSigningKey() {
        String signKey = this.config.getSecretKey();
        String dateStamp = this.timestamp.substring(0, 8);
        try {
            String key = "AWS4" + signKey;
            Mac mDate = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec skDate = new SecretKeySpec(key.getBytes(UTF_8), HMAC_SHA256);
            mDate.init(skDate);
            byte[] kDate = mDate.doFinal(dateStamp.getBytes(UTF_8));
            Mac mRegion = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec skRegion = new SecretKeySpec(kDate, HMAC_SHA256);
            mRegion.init(skRegion);
            byte[] kRegion = mRegion.doFinal(this.config.getRegion().getBytes(UTF_8));
            Mac mService = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec skService = new SecretKeySpec(kRegion, HMAC_SHA256);
            mService.init(skService);
            byte[] kService = mService.doFinal(this.service.getBytes(UTF_8));
            Mac mSigning = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec skSigning = new SecretKeySpec(kService, HMAC_SHA256);
            mSigning.init(skSigning);
            return mSigning.doFinal(API_TERMINATOR.getBytes(UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
        catch (InvalidKeyException e) {
            return null;
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String createSignature(String stringToSign, byte[] signingKey) {
        byte[] signature;
        try {
            Mac signMac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec signKS = new SecretKeySpec(signingKey, HMAC_SHA256);
            signMac.init(signKS);
            signature = signMac.doFinal(stringToSign.getBytes(UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
        catch (InvalidKeyException e) {
            return null;
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        return QuickMath.bytesToHex((byte[])signature);
    }

    protected String getCanonicalHeaders() {
        return String.format("host:%s%s", this.endpoint, NEW_LINE);
    }

    public String getCanonicalizedQueryString(Map<String, String> attributes) {
        List<String> components = this.getListOfEntries(attributes);
        Collections.sort(components);
        return this.getCanonicalizedQueryString(components);
    }

    protected String getCanonicalizedQueryString(List<String> list) {
        Iterator<String> it = list.iterator();
        StringBuilder result = new StringBuilder(it.next());
        while (it.hasNext()) {
            result.append('&').append(it.next());
        }
        return result.toString();
    }

    protected void addComponents(List<String> components, Map<String, String> attributes, String key) {
        components.add(AwsURLEncoder.urlEncode(key) + '=' + AwsURLEncoder.urlEncode(attributes.get(key)));
    }

    protected List<String> getListOfEntries(Map<String, String> entries) {
        ArrayList<String> components = new ArrayList<String>();
        for (String key : entries.keySet()) {
            this.addComponents(components, entries, key);
        }
        return components;
    }

    private String sha256Hashhex(String in) {
        String payloadHash;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(in.getBytes(UTF_8));
            byte[] digest = md.digest();
            payloadHash = QuickMath.bytesToHex((byte[])digest);
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        return payloadHash;
    }

    public String createFormattedCredential() {
        return this.config.getAccessKey() + '/' + this.timestamp.substring(0, 8) + '/' + this.config.getRegion() + '/' + "ec2/aws4_request";
    }
}

