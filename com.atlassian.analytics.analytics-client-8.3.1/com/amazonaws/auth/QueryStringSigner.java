/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.SignableRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.AbstractAWSSigner;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.SignatureVersion;
import com.amazonaws.auth.Signer;
import com.amazonaws.auth.SigningAlgorithm;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class QueryStringSigner
extends AbstractAWSSigner
implements Signer {
    private Date overriddenDate;

    @Override
    public void sign(SignableRequest<?> request, AWSCredentials credentials) throws SdkClientException {
        this.sign(request, SignatureVersion.V2, SigningAlgorithm.HmacSHA256, credentials);
    }

    public void sign(SignableRequest<?> request, SignatureVersion version, SigningAlgorithm algorithm, AWSCredentials credentials) throws SdkClientException {
        if (credentials instanceof AnonymousAWSCredentials) {
            return;
        }
        AWSCredentials sanitizedCredentials = this.sanitizeCredentials(credentials);
        request.addParameter("AWSAccessKeyId", sanitizedCredentials.getAWSAccessKeyId());
        request.addParameter("SignatureVersion", version.toString());
        int timeOffset = request.getTimeOffset();
        request.addParameter("Timestamp", this.getFormattedTimestamp(timeOffset));
        if (sanitizedCredentials instanceof AWSSessionCredentials) {
            this.addSessionCredentials(request, (AWSSessionCredentials)sanitizedCredentials);
        }
        String stringToSign = null;
        if (version.equals((Object)SignatureVersion.V1)) {
            stringToSign = this.calculateStringToSignV1(request.getParameters());
        } else if (version.equals((Object)SignatureVersion.V2)) {
            request.addParameter("SignatureMethod", algorithm.toString());
            stringToSign = this.calculateStringToSignV2(request);
        } else {
            throw new SdkClientException("Invalid Signature Version specified");
        }
        String signatureValue = this.signAndBase64Encode(stringToSign, sanitizedCredentials.getAWSSecretKey(), algorithm);
        request.addParameter("Signature", signatureValue);
    }

    private String calculateStringToSignV1(Map<String, List<String>> parameters) {
        StringBuilder data = new StringBuilder();
        TreeMap<String, List<String>> sorted = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
        sorted.putAll(parameters);
        for (Map.Entry entry : sorted.entrySet()) {
            for (String value : (List)entry.getValue()) {
                data.append((String)entry.getKey()).append(value);
            }
        }
        return data.toString();
    }

    private String calculateStringToSignV2(SignableRequest<?> request) throws SdkClientException {
        URI endpoint = request.getEndpoint();
        StringBuilder data = new StringBuilder();
        data.append("POST").append("\n").append(this.getCanonicalizedEndpoint(endpoint)).append("\n").append(this.getCanonicalizedResourcePath(request)).append("\n").append(this.getCanonicalizedQueryString(request.getParameters()));
        return data.toString();
    }

    private String getCanonicalizedResourcePath(SignableRequest<?> request) {
        String resourcePath = "";
        if (request.getEndpoint().getPath() != null) {
            resourcePath = resourcePath + request.getEndpoint().getPath();
        }
        if (request.getResourcePath() != null) {
            if (resourcePath.length() > 0 && !resourcePath.endsWith("/") && !request.getResourcePath().startsWith("/")) {
                resourcePath = resourcePath + "/";
            }
            resourcePath = resourcePath + request.getResourcePath();
        } else if (!resourcePath.endsWith("/")) {
            resourcePath = resourcePath + "/";
        }
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }
        if (resourcePath.startsWith("//")) {
            resourcePath = resourcePath.substring(1);
        }
        return resourcePath;
    }

    private String getFormattedTimestamp(int offset) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (this.overriddenDate != null) {
            return df.format(this.overriddenDate);
        }
        return df.format(this.getSignatureDate(offset));
    }

    void overrideDate(Date date) {
        this.overriddenDate = date;
    }

    @Override
    protected void addSessionCredentials(SignableRequest<?> request, AWSSessionCredentials credentials) {
        request.addParameter("SecurityToken", credentials.getSessionToken());
    }
}

