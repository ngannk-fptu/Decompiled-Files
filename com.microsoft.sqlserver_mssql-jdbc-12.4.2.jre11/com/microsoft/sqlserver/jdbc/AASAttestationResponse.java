/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package com.microsoft.sqlserver.jdbc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.sqlserver.jdbc.BaseAttestationResponse;
import com.microsoft.sqlserver.jdbc.JWTCertificateEntry;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

class AASAttestationResponse
extends BaseAttestationResponse {
    private byte[] attestationToken;
    private static ConcurrentHashMap<String, JWTCertificateEntry> certificateCache = new ConcurrentHashMap();

    AASAttestationResponse(byte[] b) throws SQLServerException {
        ByteBuffer response = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN);
        this.totalSize = response.getInt();
        this.identitySize = response.getInt();
        this.attestationTokenSize = response.getInt();
        this.enclaveType = response.getInt();
        this.enclavePK = new byte[this.identitySize];
        this.attestationToken = new byte[this.attestationTokenSize];
        response.get(this.enclavePK, 0, this.identitySize);
        response.get(this.attestationToken, 0, this.attestationTokenSize);
        this.sessionInfoSize = response.getInt();
        response.get(this.sessionID, 0, 8);
        this.dhpkSize = response.getInt();
        this.dhpkSsize = response.getInt();
        this.dhPublicKey = new byte[this.dhpkSize];
        this.publicKeySig = new byte[this.dhpkSsize];
        response.get(this.dhPublicKey, 0, this.dhpkSize);
        response.get(this.publicKeySig, 0, this.dhpkSsize);
        if (0 != response.remaining()) {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_EnclaveResponseLengthError"), "0", false);
        }
    }

    void validateToken(String attestationUrl, byte[] nonce) throws SQLServerException {
        try {
            String jwtToken = new String(this.attestationToken).trim();
            if (jwtToken.startsWith("\"") && jwtToken.endsWith("\"")) {
                jwtToken = jwtToken.substring(1, jwtToken.length() - 1);
            }
            String[] splitString = jwtToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String header = new String(decoder.decode(splitString[0]));
            String body = new String(decoder.decode(splitString[1]));
            byte[] stmtSig = decoder.decode(splitString[2]);
            JsonArray keys = null;
            JWTCertificateEntry cacheEntry = certificateCache.get(attestationUrl);
            if (null != cacheEntry && !cacheEntry.expired()) {
                keys = cacheEntry.getCertificates();
            } else if (null != cacheEntry && cacheEntry.expired()) {
                certificateCache.remove(attestationUrl);
            }
            if (null == keys) {
                String authorityUrl = new URL(attestationUrl).getAuthority();
                URL wellKnownUrl = new URL("https://" + authorityUrl + "/.well-known/openid-configuration");
                URLConnection con = wellKnownUrl.openConnection();
                String wellKnownUrlJson = Util.convertInputStreamToString(con.getInputStream());
                JsonObject attestationJson = JsonParser.parseString((String)wellKnownUrlJson).getAsJsonObject();
                URL jwksUrl = new URL(attestationJson.get("jwks_uri").getAsString());
                URLConnection jwksCon = jwksUrl.openConnection();
                String jwksUrlJson = Util.convertInputStreamToString(jwksCon.getInputStream());
                JsonObject jwksJson = JsonParser.parseString((String)jwksUrlJson).getAsJsonObject();
                keys = jwksJson.get("keys").getAsJsonArray();
                certificateCache.put(attestationUrl, new JWTCertificateEntry(keys));
            }
            JsonObject headerJsonObject = JsonParser.parseString((String)header).getAsJsonObject();
            String keyID = headerJsonObject.get("kid").getAsString();
            for (JsonElement key : keys) {
                JsonObject keyObj = key.getAsJsonObject();
                String kId = keyObj.get("kid").getAsString();
                if (!kId.equals(keyID)) continue;
                JsonArray certsFromServer = keyObj.get("x5c").getAsJsonArray();
                byte[] signatureBytes = (splitString[0] + "." + splitString[1]).getBytes();
                for (JsonElement jsonCert : certsFromServer) {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    X509Certificate cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(jsonCert.getAsString())));
                    Signature sig = Signature.getInstance("SHA256withRSA");
                    sig.initVerify(cert.getPublicKey());
                    sig.update(signatureBytes);
                    if (!sig.verify(stmtSig)) continue;
                    JsonObject bodyJsonObject = JsonParser.parseString((String)body).getAsJsonObject();
                    String aasEhd = bodyJsonObject.get("aas-ehd").getAsString();
                    if (!Arrays.equals(Base64.getUrlDecoder().decode(aasEhd), this.enclavePK)) {
                        SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_AasEhdError"), "0", false);
                    }
                    if (this.enclaveType == 1) {
                        String rpData = bodyJsonObject.get("rp_data").getAsString();
                        if (!Arrays.equals(Base64.getUrlDecoder().decode(rpData), nonce)) {
                            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_VbsRpDataError"), "0", false);
                        }
                    }
                    return;
                }
            }
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_AasJWTError"), "0", false);
        }
        catch (IOException | GeneralSecurityException e) {
            SQLServerException.makeFromDriverError(null, this, e.getLocalizedMessage(), "", false);
        }
    }

    void validateDHPublicKey(byte[] nonce) throws SQLServerException, GeneralSecurityException {
        if (this.enclaveType == 2) {
            for (int i = 0; i < this.enclavePK.length; ++i) {
                this.enclavePK[i] = (byte)(this.enclavePK[i] ^ nonce[i % nonce.length]);
            }
        }
        this.validateDHPublicKey();
    }
}

