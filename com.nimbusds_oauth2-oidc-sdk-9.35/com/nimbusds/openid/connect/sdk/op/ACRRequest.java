/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ciba.CIBARequest;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.OIDCClaimsRequest;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import com.nimbusds.openid.connect.sdk.claims.ClaimRequirement;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSetRequest;
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
        if (!(authzRequest instanceof AuthenticationRequest)) {
            return new ACRRequest(null, null);
        }
        AuthenticationRequest authRequest = (AuthenticationRequest)authzRequest;
        return ACRRequest.resolve(authRequest.getACRValues(), authRequest.getOIDCClaims());
    }

    public static ACRRequest resolve(CIBARequest cibaRequest) {
        if (cibaRequest.isSigned()) {
            throw new IllegalArgumentException("The CIBA request must be resolved (not signed)");
        }
        if (cibaRequest.getScope() != null && !cibaRequest.getScope().contains(OIDCScopeValue.OPENID)) {
            return new ACRRequest(null, null);
        }
        return ACRRequest.resolve(cibaRequest.getACRValues(), cibaRequest.getOIDCClaims());
    }

    private static ClaimsSetRequest.Entry getACRClaimRequest(OIDCClaimsRequest claimsRequest) {
        if (claimsRequest == null) {
            return null;
        }
        ClaimsSetRequest idTokenClaimsRequest = claimsRequest.getIDTokenClaimsRequest();
        if (idTokenClaimsRequest == null) {
            return null;
        }
        for (ClaimsSetRequest.Entry en : idTokenClaimsRequest.getEntries()) {
            if (!"acr".equals(en.getClaimName())) continue;
            return en;
        }
        return null;
    }

    public static ACRRequest resolve(List<ACR> acrValues, OIDCClaimsRequest claimsRequest) {
        ArrayList<ACR> essentialACRs = null;
        ArrayList<ACR> voluntaryACRs = null;
        ClaimsSetRequest.Entry en = ACRRequest.getACRClaimRequest(claimsRequest);
        if (en != null) {
            if (en.getClaimRequirement().equals((Object)ClaimRequirement.ESSENTIAL)) {
                essentialACRs = new ArrayList<ACR>();
                if (en.getValueAsString() != null) {
                    essentialACRs.add(new ACR(en.getValueAsString()));
                }
                if (en.getValuesAsListOfStrings() != null) {
                    for (String v : en.getValuesAsListOfStrings()) {
                        essentialACRs.add(new ACR(v));
                    }
                }
            } else {
                voluntaryACRs = new ArrayList();
                if (en.getValueAsString() != null) {
                    voluntaryACRs.add(new ACR(en.getValueAsString()));
                }
                if (en.getValuesAsListOfStrings() != null) {
                    for (String v : en.getValuesAsListOfStrings()) {
                        voluntaryACRs.add(new ACR(v));
                    }
                }
            }
        }
        if (acrValues != null) {
            if (voluntaryACRs == null) {
                voluntaryACRs = new ArrayList<ACR>();
            }
            voluntaryACRs.addAll(acrValues);
        }
        return new ACRRequest(essentialACRs, (List<ACR>)voluntaryACRs);
    }
}

