/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityEvent.TokenSecurityEvent;
import org.apache.xml.security.stax.securityToken.SecurityToken;

public class KeyValueTokenSecurityEvent
extends TokenSecurityEvent<SecurityToken> {
    public KeyValueTokenSecurityEvent() {
        super(SecurityEventConstants.KeyValueToken);
    }

    public KeyValueTokenType getKeyValueTokenType() {
        try {
            String algo = this.getSecurityToken().getPublicKey().getAlgorithm();
            return KeyValueTokenType.valueOf(algo);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
        catch (XMLSecurityException e) {
            return null;
        }
    }

    public static enum KeyValueTokenType {
        RSA,
        DSA,
        EC;

    }
}

