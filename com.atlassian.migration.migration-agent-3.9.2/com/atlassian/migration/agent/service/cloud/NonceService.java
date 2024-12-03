/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.google.common.base.Ticker
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.cloud;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.google.common.base.Ticker;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class NonceService {
    private static final Logger log = ContextLoggerFactory.getLogger(NonceService.class);
    private static final String NONCE_ATTRIBUTE = "confluence.migration.nonce";
    private static final String NONCE_CREATE_TIME_ATTRIBUTE = "confluence.migration.nonce.createtime";
    private static final long TTL_NS = TimeUnit.MINUTES.toNanos(30L);
    private static final Supplier<HttpSession> DEFAULT_SESSION_SUPPLIER = () -> {
        HttpSession session = ServletContextThreadLocal.getRequest().getSession();
        Objects.requireNonNull(session, "session is not available");
        return session;
    };
    private final Ticker ticker;
    private final Supplier<HttpSession> sessionSupplier;
    private final MigrationAgentConfiguration configuration;

    public NonceService(MigrationAgentConfiguration configuration) {
        this(configuration, Ticker.systemTicker(), DEFAULT_SESSION_SUPPLIER);
    }

    @VisibleForTesting
    NonceService(MigrationAgentConfiguration configuration, Ticker ticker, Supplier<HttpSession> sessionSupplier) {
        this.configuration = configuration;
        this.ticker = ticker;
        this.sessionSupplier = sessionSupplier;
    }

    public String generateAndSaveNonce() {
        String nonce = UUID.randomUUID().toString();
        this.getSession().setAttribute(NONCE_ATTRIBUTE, (Object)nonce);
        this.getSession().setAttribute(NONCE_CREATE_TIME_ATTRIBUTE, (Object)this.ticker.read());
        return nonce;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean validateAndDeleteNonce(String nonce) {
        if (this.configuration.isSkipNonceCheck()) {
            log.debug("Nonce check is disabled in plugin configuration. Will skip nonce check...");
            return true;
        }
        HttpSession session = this.getSession();
        try {
            String existingNonce = (String)session.getAttribute(NONCE_ATTRIBUTE);
            Long existingNonceCreationTime = (Long)session.getAttribute(NONCE_CREATE_TIME_ATTRIBUTE);
            if (existingNonce == null || existingNonceCreationTime == null) {
                boolean bl = false;
                return bl;
            }
            boolean isNotExpired = existingNonceCreationTime + TTL_NS > this.ticker.read();
            boolean bl = isNotExpired && existingNonce.equals(nonce);
            return bl;
        }
        finally {
            session.removeAttribute(NONCE_ATTRIBUTE);
            session.removeAttribute(NONCE_CREATE_TIME_ATTRIBUTE);
        }
    }

    private HttpSession getSession() {
        return this.sessionSupplier.get();
    }
}

