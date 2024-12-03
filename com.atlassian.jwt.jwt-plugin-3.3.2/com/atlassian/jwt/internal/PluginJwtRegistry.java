/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.jwt.internal;

import com.atlassian.jwt.JwtIssuer;
import com.atlassian.jwt.JwtIssuerClaimVerifiersRegistry;
import com.atlassian.jwt.JwtIssuerRegistry;
import com.atlassian.jwt.core.reader.JwtIssuerSharedSecretService;
import com.atlassian.jwt.core.reader.JwtIssuerValidator;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.reader.JwtClaimVerifiersBuilder;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class PluginJwtRegistry
implements JwtIssuerClaimVerifiersRegistry,
JwtIssuerRegistry,
JwtIssuerSharedSecretService,
JwtIssuerValidator {
    private final ServiceTracker<JwtIssuerClaimVerifiersRegistry, JwtIssuerClaimVerifiersRegistry> claimsVerifierServiceTracker;
    private final ServiceTracker<JwtIssuerRegistry, JwtIssuerRegistry> issuerServiceTracker;

    public PluginJwtRegistry(BundleContext bundleContext) {
        this.claimsVerifierServiceTracker = new ServiceTracker(bundleContext, JwtIssuerClaimVerifiersRegistry.class.getName(), null);
        this.claimsVerifierServiceTracker.open();
        this.issuerServiceTracker = new ServiceTracker(bundleContext, JwtIssuerRegistry.class.getName(), null);
        this.issuerServiceTracker.open();
    }

    public void destroy() {
        this.claimsVerifierServiceTracker.close();
        this.issuerServiceTracker.close();
    }

    @Override
    public JwtClaimVerifiersBuilder getClaimVerifiersBuilder(@Nonnull String issuerName) {
        Objects.requireNonNull(issuerName, "issuerName");
        for (JwtIssuerClaimVerifiersRegistry registry : this.getClaimVerifiersRegistries()) {
            JwtClaimVerifiersBuilder claimVerifiersBuilder = registry.getClaimVerifiersBuilder(issuerName);
            if (claimVerifiersBuilder == null) continue;
            return claimVerifiersBuilder;
        }
        return null;
    }

    @Override
    public JwtIssuer getIssuer(@Nonnull String issuerName) {
        Objects.requireNonNull(issuerName, "issuerName");
        for (JwtIssuerRegistry registry : this.getIssuerRegistries()) {
            JwtIssuer issuer = registry.getIssuer(issuerName);
            if (issuer == null) continue;
            return issuer;
        }
        return null;
    }

    @Override
    public String getSharedSecret(@Nonnull String issuerName) throws JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException {
        String secret = this.getIssuerOrFail(issuerName).getSharedSecret();
        if (null == secret) {
            throw new JwtIssuerLacksSharedSecretException(issuerName);
        }
        return secret;
    }

    @Override
    public boolean isValid(String issuerName) {
        return issuerName != null && this.getIssuer(issuerName) != null;
    }

    private Iterable<JwtIssuerClaimVerifiersRegistry> getClaimVerifiersRegistries() {
        return this.getRegistries(this.claimsVerifierServiceTracker, JwtIssuerClaimVerifiersRegistry.class);
    }

    private JwtIssuer getIssuerOrFail(String issuerName) throws JwtUnknownIssuerException {
        JwtIssuer issuer = this.getIssuer(issuerName);
        if (issuer == null) {
            throw new JwtUnknownIssuerException(String.format("Issuer '%s' not found", issuerName));
        }
        return issuer;
    }

    private Iterable<JwtIssuerRegistry> getIssuerRegistries() {
        return this.getRegistries(this.issuerServiceTracker, JwtIssuerRegistry.class);
    }

    private <T> Iterable<T> getRegistries(ServiceTracker<T, T> serviceTracker, Class<T> clazz) {
        return serviceTracker.getTracked().values().stream().filter(clazz::isInstance).collect(Collectors.toList());
    }
}

