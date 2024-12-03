/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.dpop.verifiers;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPIssuer;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.util.singleuse.AlreadyUsedException;
import com.nimbusds.oauth2.sdk.util.singleuse.SingleUseChecker;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DefaultDPoPSingleUseChecker
implements SingleUseChecker<Map.Entry<DPoPIssuer, JWTID>> {
    private final Timer timer;
    private final ConcurrentHashMap<String, Long> cachedJTIs = new ConcurrentHashMap();

    public DefaultDPoPSingleUseChecker(final long lifetimeSeconds, long purgeIntervalSeconds) {
        this.timer = new Timer("dpop-single-use-jti-cache-purge-task", true);
        this.timer.schedule(new TimerTask(){

            @Override
            public void run() {
                long nowMS = new Date().getTime();
                long expHorizon = nowMS - lifetimeSeconds * 1000L;
                for (Map.Entry en : DefaultDPoPSingleUseChecker.this.cachedJTIs.entrySet()) {
                    if ((Long)en.getValue() >= expHorizon) continue;
                    DefaultDPoPSingleUseChecker.this.cachedJTIs.remove(en.getKey());
                }
            }
        }, purgeIntervalSeconds * 1000L, purgeIntervalSeconds * 1000L);
    }

    private static Base64URL computeSHA256(JWTID jti) {
        byte[] hash;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(jti.getValue().getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return Base64URL.encode(hash);
    }

    @Override
    public void markAsUsed(Map.Entry<DPoPIssuer, JWTID> object) throws AlreadyUsedException {
        long nowMS;
        String key = object.getKey().getValue() + ":" + DefaultDPoPSingleUseChecker.computeSHA256(object.getValue());
        if (this.cachedJTIs.putIfAbsent(key, nowMS = new Date().getTime()) != null) {
            throw new AlreadyUsedException("Detected jti replay");
        }
    }

    public int getCacheSize() {
        return this.cachedJTIs.size();
    }

    public void shutdown() {
        this.timer.cancel();
    }
}

