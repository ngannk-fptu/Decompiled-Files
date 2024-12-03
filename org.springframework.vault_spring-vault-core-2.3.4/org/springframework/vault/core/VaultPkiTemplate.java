/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.ResponseEntity
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.web.client.HttpStatusCodeException
 */
package org.springframework.vault.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.support.VaultCertificateRequest;
import org.springframework.vault.support.VaultCertificateResponse;
import org.springframework.vault.support.VaultSignCertificateRequestResponse;
import org.springframework.web.client.HttpStatusCodeException;

public class VaultPkiTemplate
implements VaultPkiOperations {
    private final VaultOperations vaultOperations;
    private final String path;

    public VaultPkiTemplate(VaultOperations vaultOperations, String path) {
        Assert.notNull((Object)vaultOperations, (String)"VaultOperations must not be null");
        Assert.hasText((String)path, (String)"Path must not be empty");
        this.vaultOperations = vaultOperations;
        this.path = path;
    }

    @Override
    public VaultCertificateResponse issueCertificate(String roleName, VaultCertificateRequest certificateRequest) throws VaultException {
        Assert.hasText((String)roleName, (String)"Role name must not be empty");
        Assert.notNull((Object)certificateRequest, (String)"Certificate request must not be null");
        return this.requestCertificate(roleName, "{path}/issue/{roleName}", VaultPkiTemplate.createIssueRequest(certificateRequest), VaultCertificateResponse.class);
    }

    @Override
    public VaultSignCertificateRequestResponse signCertificateRequest(String roleName, String csr, VaultCertificateRequest certificateRequest) throws VaultException {
        Assert.hasText((String)roleName, (String)"Role name must not be empty");
        Assert.hasText((String)csr, (String)"CSR name must not be empty");
        Assert.notNull((Object)certificateRequest, (String)"Certificate request must not be null");
        Map<String, Object> body = VaultPkiTemplate.createIssueRequest(certificateRequest);
        body.put("csr", csr);
        return this.requestCertificate(roleName, "{path}/sign/{roleName}", body, VaultSignCertificateRequestResponse.class);
    }

    private <T> T requestCertificate(String roleName, String requestPath, Map<String, Object> request, Class<T> responseType) {
        request.put("format", "der");
        Object response = this.vaultOperations.doWithSession(restOperations -> {
            try {
                return restOperations.postForObject(requestPath, (Object)request, responseType, new Object[]{this.path, roleName});
            }
            catch (HttpStatusCodeException e) {
                throw VaultResponses.buildException(e);
            }
        });
        Assert.state((response != null ? 1 : 0) != 0, (String)"VaultCertificateResponse must not be null");
        return (T)response;
    }

    @Override
    public void revoke(String serialNumber) throws VaultException {
        Assert.hasText((String)serialNumber, (String)"Serial number must not be null or empty");
        this.vaultOperations.doWithSession(restOperations -> {
            try {
                restOperations.postForObject("{path}/revoke", Collections.singletonMap("serial_number", serialNumber), Map.class, new Object[]{this.path});
                return null;
            }
            catch (HttpStatusCodeException e) {
                throw VaultResponses.buildException(e);
            }
        });
    }

    @Override
    public InputStream getCrl(VaultPkiOperations.Encoding encoding) throws VaultException {
        Assert.notNull((Object)((Object)encoding), (String)"Encoding must not be null");
        return this.vaultOperations.doWithSession(restOperations -> {
            String requestPath = encoding == VaultPkiOperations.Encoding.DER ? "{path}/crl" : "{path}/crl/pem";
            try {
                ResponseEntity response = restOperations.getForEntity(requestPath, byte[].class, new Object[]{this.path});
                if (response.getStatusCode() == HttpStatus.OK) {
                    return new ByteArrayInputStream((byte[])response.getBody());
                }
                return null;
            }
            catch (HttpStatusCodeException e) {
                throw VaultResponses.buildException(e);
            }
        });
    }

    private static Map<String, Object> createIssueRequest(VaultCertificateRequest certificateRequest) {
        Assert.notNull((Object)certificateRequest, (String)"Certificate request must not be null");
        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("common_name", certificateRequest.getCommonName());
        if (!certificateRequest.getAltNames().isEmpty()) {
            request.put("alt_names", StringUtils.collectionToDelimitedString(certificateRequest.getAltNames(), (String)","));
        }
        if (!certificateRequest.getIpSubjectAltNames().isEmpty()) {
            request.put("ip_sans", StringUtils.collectionToDelimitedString(certificateRequest.getIpSubjectAltNames(), (String)","));
        }
        if (!certificateRequest.getUriSubjectAltNames().isEmpty()) {
            request.put("uri_sans", StringUtils.collectionToDelimitedString(certificateRequest.getUriSubjectAltNames(), (String)","));
        }
        if (certificateRequest.getTtl() != null) {
            request.put("ttl", certificateRequest.getTtl().get(ChronoUnit.SECONDS));
        }
        if (certificateRequest.isExcludeCommonNameFromSubjectAltNames()) {
            request.put("exclude_cn_from_sans", true);
        }
        return request;
    }
}

