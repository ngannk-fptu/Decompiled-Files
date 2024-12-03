/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.ClaimsRequest;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import com.nimbusds.openid.connect.sdk.claims.ClaimRequirement;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.jcip.annotations.Immutable;

@Immutable
public final class ACRRequest {
    private final List<ACR> essentialACRs;
    private final List<ACR> voluntaryACRs;

    public ACRRequest(List<ACR> essentialACRs, List<ACR> voluntaryACRs) {
        this.essentialACRs = essentialACRs;
        this.voluntaryACRs = voluntaryACRs;
    }

    public List<ACR> getEssentialACRs() {
        return this.essentialACRs;
    }

    public List<ACR> getVoluntaryACRs() {
        return this.voluntaryACRs;
    }

    public boolean isEmpty() {
        return !(this.essentialACRs != null && !this.essentialACRs.isEmpty() || this.voluntaryACRs != null && !this.voluntaryACRs.isEmpty());
    }

    public ACRRequest applyDefaultACRs(OIDCClientInformation clientInfo) {
        if (this.isEmpty() && clientInfo.getOIDCMetadata().getDefaultACRs() != null) {
            LinkedList<ACR> voluntaryACRs = new LinkedList<ACR>(clientInfo.getOIDCMetadata().getDefaultACRs());
            return new ACRRequest(null, voluntaryACRs);
        }
        return this;
    }

    public void ensureACRSupport(AuthorizationRequest authzRequest, List<ACR> supportedACRs) throws GeneralException {
        if (this.getEssentialACRs() != null) {
            boolean foundSupportedEssentialACR = false;
            for (ACR acr : this.getEssentialACRs()) {
                if (supportedACRs == null || !supportedACRs.contains(acr)) continue;
                foundSupportedEssentialACR = true;
                break;
            }
            if (!foundSupportedEssentialACR) {
                String msg = "Requested essential ACR(s) not supported";
                throw new GeneralException(msg, OAuth2Error.ACCESS_DENIED.appendDescription(": " + msg), authzRequest.getClientID(), authzRequest.getRedirectionURI(), authzRequest.impliedResponseMode(), authzRequest.getState());
            }
        }
    }

    @Deprecated
    public void ensureACRSupport(AuthenticationRequest authRequest, OIDCProviderMetadata opMetadata) throws GeneralException {
        this.ensureACRSupport((AuthorizationRequest)authRequest, opMetadata.getACRs());
    }

    public static ACRRequest resolve(AuthorizationRequest authzRequest) {
        List<ACR> topLevelACRs;
        ArrayList<ACR> essentialACRs = null;
        ArrayList<ACR> voluntaryACRs = null;
        if (!(authzRequest instanceof AuthenticationRequest)) {
            return new ACRRequest(essentialACRs, voluntaryACRs);
        }
        AuthenticationRequest authRequest = (AuthenticationRequest)authzRequest;
        ClaimsRequest claimsRequest = authRequest.getClaims();
        if (claimsRequest != null) {
            for (ClaimsRequest.Entry claimEntry : claimsRequest.getIDTokenClaims()) {
                if (!claimEntry.getClaimName().equals("acr")) continue;
                if (claimEntry.getClaimRequirement().equals((Object)ClaimRequirement.ESSENTIAL)) {
                    essentialACRs = new ArrayList<ACR>();
                    if (claimEntry.getValue() != null) {
                        essentialACRs.add(new ACR(claimEntry.getValue()));
                    }
                    if (claimEntry.getValues() == null) continue;
                    for (String v : claimEntry.getValues()) {
                        essentialACRs.add(new ACR(v));
                    }
                    continue;
                }
                voluntaryACRs = new ArrayList();
                if (claimEntry.getValue() != null) {
                    voluntaryACRs.add(new ACR(claimEntry.getValue()));
                }
                if (claimEntry.getValues() == null) continue;
                for (String v : claimEntry.getValues()) {
                    voluntaryACRs.add(new ACR(v));
                }
            }
        }
        if ((topLevelACRs = authRequest.getACRValues()) != null) {
            if (voluntaryACRs == null) {
                voluntaryACRs = new ArrayList<ACR>();
            }
            voluntaryACRs.addAll(topLevelACRs);
        }
        return new ACRRequest(essentialACRs, (List<ACR>)voluntaryACRs);
    }
}

