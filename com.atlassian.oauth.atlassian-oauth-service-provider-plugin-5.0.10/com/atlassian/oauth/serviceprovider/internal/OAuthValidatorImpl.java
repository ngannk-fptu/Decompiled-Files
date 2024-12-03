/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.oauth.OAuthAccessor
 *  net.oauth.OAuthException
 *  net.oauth.OAuthMessage
 *  net.oauth.OAuthProblemException
 *  net.oauth.OAuthValidator
 *  net.oauth.SimpleOAuthValidator
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.serviceprovider.internal.InMemoryNonceService;
import com.atlassian.oauth.serviceprovider.internal.NonceChecker;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;

public class OAuthValidatorImpl
implements OAuthValidator {
    private final InMemoryNonceService nonceService;
    private final NonceChecker nonceChecker;
    private final OAuthValidator simpleValidator;

    public OAuthValidatorImpl() {
        this(null);
    }

    public OAuthValidatorImpl(NonceChecker nonceChecker) {
        this.nonceChecker = nonceChecker;
        this.nonceService = new InMemoryNonceService(TimeUnit.MILLISECONDS.toSeconds(300000L));
        this.simpleValidator = this.getSimpleValidator();
    }

    public void validateMessage(OAuthMessage message, OAuthAccessor accessor) throws OAuthException, IOException, URISyntaxException {
        message.requireParameters(new String[]{"oauth_signature_method", "oauth_consumer_key"});
        if (!message.getParameter("oauth_signature_method").equals("RSA-SHA1")) {
            throw new OAuthProblemException("signature_method_rejected");
        }
        this.simpleValidator.validateMessage(message, accessor);
    }

    private OAuthValidator getSimpleValidator() {
        return new SimpleOAuthValidator(300000L, Double.parseDouble("1.0")){

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            protected void validateTimestampAndNonce(OAuthMessage message) throws IOException, OAuthProblemException {
                super.validateTimestampAndNonce(message);
                long timestamp = Long.parseLong(message.getParameter("oauth_timestamp"));
                String nonce = message.getParameter("oauth_nonce");
                if (OAuthValidatorImpl.this.nonceChecker != null) {
                    if (!OAuthValidatorImpl.this.nonceChecker.isNonceUnique(message.getConsumerKey(), nonce)) throw new OAuthProblemException("nonce_used");
                    OAuthValidatorImpl.this.nonceChecker.addNonce(message.getConsumerKey(), nonce);
                    return;
                } else {
                    OAuthValidatorImpl.this.nonceService.validateNonce(message.getConsumerKey(), timestamp, nonce);
                }
            }
        };
    }
}

