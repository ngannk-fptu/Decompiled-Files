/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.shindig.auth.BlobCrypterSecurityToken
 *  org.apache.shindig.auth.SecurityToken
 *  org.apache.shindig.auth.SecurityTokenDecoder
 *  org.apache.shindig.auth.SecurityTokenException
 *  org.apache.shindig.common.crypto.BlobCrypter
 *  org.apache.shindig.common.crypto.BlobCrypterException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.gadgets.renderer.internal.ContainerDomainProvider;
import com.atlassian.gadgets.renderer.internal.UpdatableBlobCrypterSecurityToken;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.shindig.auth.BlobCrypterSecurityToken;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.auth.SecurityTokenDecoder;
import org.apache.shindig.auth.SecurityTokenException;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="securityTokenDecoder")
public class CustomBlobCrypterSecurityTokenDecoder
implements SecurityTokenDecoder {
    private final BlobCrypter crypter;
    private final ContainerDomainProvider containerDomainProvider;
    private static final int MAX_TOKEN_LIFETIME_SECS = 3600;
    private static final String OWNER_KEY = "o";
    private static final String VIEWER_KEY = "v";
    private static final String GADGET_KEY = "g";
    private static final String GADGET_INSTANCE_KEY = "i";
    private static final String TRUSTED_JSON_KEY = "j";

    @Autowired
    public CustomBlobCrypterSecurityTokenDecoder(@Qualifier(value="blobCrypter") BlobCrypter crypter, ContainerDomainProvider containerDomainProvider) {
        this.containerDomainProvider = containerDomainProvider;
        this.crypter = crypter;
    }

    public SecurityToken createToken(Map<String, String> tokenParameters) throws SecurityTokenException {
        String token = tokenParameters.get("token");
        if (StringUtils.isEmpty((CharSequence)token)) {
            return null;
        }
        String[] fields = token.split(":");
        if (fields.length != 2) {
            throw new SecurityTokenException("Invalid security token " + token);
        }
        String container = fields[0];
        if (!"atlassian".equals(container) && !"default".equals(container)) {
            throw new SecurityTokenException("Unknown container " + token);
        }
        String crypted = fields[1];
        String activeUrl = tokenParameters.get("activeUrl");
        String domain = this.containerDomainProvider.getDomain();
        try {
            return CustomBlobCrypterSecurityTokenDecoder.decrypt(this.crypter, container, domain, crypted, activeUrl);
        }
        catch (BlobCrypterException e) {
            throw new SecurityTokenException((Exception)((Object)e));
        }
    }

    static BlobCrypterSecurityToken decrypt(BlobCrypter crypter, String container, String domain, String token, String activeUrl) throws BlobCrypterException {
        Map values = crypter.unwrap(token, 3600);
        UpdatableBlobCrypterSecurityToken t = new UpdatableBlobCrypterSecurityToken(crypter, container, domain);
        t.setOwnerId((String)values.get(OWNER_KEY));
        t.setViewerId((String)values.get(VIEWER_KEY));
        t.setAppUrl((String)values.get(GADGET_KEY));
        t.setActiveUrl(activeUrl);
        String moduleId = (String)values.get(GADGET_INSTANCE_KEY);
        if (moduleId != null) {
            t.setModuleId(moduleId);
        }
        t.setTrustedJson((String)values.get(TRUSTED_JSON_KEY));
        return t;
    }
}

