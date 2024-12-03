/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetState
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.gadgets.view.SecurityTokenFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.apache.shindig.common.crypto.BlobCrypter
 *  org.apache.shindig.common.crypto.BlobCrypterException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.renderer.internal.ContainerDomainProvider;
import com.atlassian.gadgets.renderer.internal.UpdatableBlobCrypterSecurityToken;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.gadgets.view.SecurityTokenFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import java.net.URI;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class SecurityTokenFactoryImpl
implements SecurityTokenFactory {
    private final BlobCrypter crypter;
    private final ContainerDomainProvider containerDomainProvider;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public SecurityTokenFactoryImpl(@Qualifier(value="blobCrypter") BlobCrypter crypter, ContainerDomainProvider containerDomainProvider, @ComponentImport ApplicationProperties applicationProperties) {
        this.crypter = crypter;
        this.containerDomainProvider = containerDomainProvider;
        this.applicationProperties = applicationProperties;
    }

    public String newSecurityToken(GadgetState state, String viewer) {
        UpdatableBlobCrypterSecurityToken token = new UpdatableBlobCrypterSecurityToken(this.crypter, "atlassian", this.containerDomainProvider.getDomain());
        if (viewer != null) {
            token.setModuleId(state.getId().value());
            token.setOwnerId(viewer);
            token.setViewerId(viewer);
        }
        token.setAppUrl(this.absoluteGadgetUrl(state));
        try {
            return token.encrypt();
        }
        catch (BlobCrypterException e) {
            throw new RuntimeException(e);
        }
    }

    private String absoluteGadgetUrl(GadgetState state) {
        return Uri.resolveUriAgainstBase((String)this.applicationProperties.getBaseUrl(), (URI)state.getGadgetSpecUri()).toASCIIString();
    }
}

