/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.security.crypto.codec.Hex
 *  org.springframework.security.crypto.codec.Utf8
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.security.core.token;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.token.DefaultToken;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class KeyBasedPersistenceTokenService
implements TokenService,
InitializingBean {
    private int pseudoRandomNumberBytes = 32;
    private String serverSecret;
    private Integer serverInteger;
    private SecureRandom secureRandom;

    @Override
    public Token allocateToken(String extendedInformation) {
        Assert.notNull((Object)extendedInformation, (String)"Must provided non-null extendedInformation (but it can be empty)");
        long creationTime = new Date().getTime();
        String serverSecret = this.computeServerSecretApplicableAt(creationTime);
        String pseudoRandomNumber = this.generatePseudoRandomNumber();
        String content = creationTime + ":" + pseudoRandomNumber + ":" + extendedInformation;
        String key = this.computeKey(serverSecret, content);
        return new DefaultToken(key, creationTime, extendedInformation);
    }

    private String computeKey(String serverSecret, String content) {
        String sha512Hex = Sha512DigestUtils.shaHex(content + ":" + serverSecret);
        String keyPayload = content + ":" + sha512Hex;
        return Utf8.decode((byte[])Base64.getEncoder().encode(Utf8.encode((CharSequence)keyPayload)));
    }

    @Override
    public Token verifyToken(String key) {
        long creationTime;
        if (key == null || "".equals(key)) {
            return null;
        }
        String[] tokens = StringUtils.delimitedListToStringArray((String)Utf8.decode((byte[])Base64.getDecoder().decode(Utf8.encode((CharSequence)key))), (String)":");
        Assert.isTrue((tokens.length >= 4 ? 1 : 0) != 0, () -> "Expected 4 or more tokens but found " + tokens.length);
        try {
            creationTime = Long.decode(tokens[0]);
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Expected number but found " + tokens[0]);
        }
        String serverSecret = this.computeServerSecretApplicableAt(creationTime);
        String pseudoRandomNumber = tokens[1];
        StringBuilder extendedInfo = new StringBuilder();
        for (int i = 2; i < tokens.length - 1; ++i) {
            if (i > 2) {
                extendedInfo.append(":");
            }
            extendedInfo.append(tokens[i]);
        }
        String sha1Hex = tokens[tokens.length - 1];
        String content = creationTime + ":" + pseudoRandomNumber + ":" + extendedInfo.toString();
        String expectedSha512Hex = Sha512DigestUtils.shaHex(content + ":" + serverSecret);
        Assert.isTrue((boolean)expectedSha512Hex.equals(sha1Hex), (String)"Key verification failure");
        return new DefaultToken(key, creationTime, extendedInfo.toString());
    }

    private String generatePseudoRandomNumber() {
        byte[] randomBytes = new byte[this.pseudoRandomNumberBytes];
        this.secureRandom.nextBytes(randomBytes);
        return new String(Hex.encode((byte[])randomBytes));
    }

    private String computeServerSecretApplicableAt(long time) {
        return this.serverSecret + ":" + new Long(time % (long)this.serverInteger.intValue()).intValue();
    }

    public void setServerSecret(String serverSecret) {
        this.serverSecret = serverSecret;
    }

    public void setSecureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public void setPseudoRandomNumberBytes(int pseudoRandomNumberBytes) {
        Assert.isTrue((pseudoRandomNumberBytes >= 0 ? 1 : 0) != 0, (String)"Must have a positive pseudo random number bit size");
        this.pseudoRandomNumberBytes = pseudoRandomNumberBytes;
    }

    public void setServerInteger(Integer serverInteger) {
        this.serverInteger = serverInteger;
    }

    public void afterPropertiesSet() {
        Assert.hasText((String)this.serverSecret, (String)"Server secret required");
        Assert.notNull((Object)this.serverInteger, (String)"Server integer required");
        Assert.notNull((Object)this.secureRandom, (String)"SecureRandom instance required");
    }
}

