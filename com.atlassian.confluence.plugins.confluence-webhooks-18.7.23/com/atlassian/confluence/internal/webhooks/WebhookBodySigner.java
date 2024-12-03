/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookRequestEnricher
 *  javax.annotation.Nonnull
 *  org.apache.commons.codec.binary.Hex
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.internal.webhooks;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookRequestEnricher;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.annotation.Nonnull;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class WebhookBodySigner
implements WebhookRequestEnricher {
    private static final Logger log = LoggerFactory.getLogger(WebhookBodySigner.class);
    private static final String HEADER_NAME = "X-Hub-Signature";
    private static final String ALGORITHM = "sha256";
    private static final String ALGORITHM_HMAC_NAME = "Hmac" + "sha256".toUpperCase();
    private static final String ALGORITHM_DIGEST_PREFIX = "sha256".toLowerCase() + "=";
    public static final String SECRET_KEY = "secret";

    public void enrich(@Nonnull WebhookInvocation webhookInvocation) {
        String secret = (String)webhookInvocation.getWebhook().getConfiguration().get(SECRET_KEY);
        if (StringUtils.isNotEmpty((CharSequence)secret)) {
            this.signBody(webhookInvocation, secret);
        } else if (secret != null) {
            log.warn("Secret provided to webhook signature provider is an empty string and will be ignored for invocation [{}]", (Object)webhookInvocation.getId());
        }
    }

    public int getWeight() {
        return 1000;
    }

    private void signBody(@Nonnull WebhookInvocation webhookInvocation, String secret) {
        byte[] body = webhookInvocation.getRequestBuilder().getBody();
        try {
            Mac mac = Mac.getInstance(ALGORITHM_HMAC_NAME);
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM_HMAC_NAME);
            mac.init(keySpec);
            byte[] digest = mac.doFinal(body);
            String digestString = Hex.encodeHexString((byte[])digest);
            webhookInvocation.getRequestBuilder().header(HEADER_NAME, ALGORITHM_DIGEST_PREFIX + digestString);
        }
        catch (InvalidKeyException e) {
            log.warn("Secret provided to webhook signature is invalid and will be ignored for invocation [{}]", (Object)webhookInvocation.getId());
        }
        catch (NoSuchAlgorithmException e) {
            log.error("Unable to initialize the signing algorithm [{}]. Webhooks will not be signed", (Object)ALGORITHM);
        }
    }
}

