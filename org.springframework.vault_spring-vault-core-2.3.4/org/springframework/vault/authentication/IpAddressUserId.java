/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.io.IOException;
import java.net.InetAddress;
import org.springframework.vault.authentication.AppIdUserIdMechanism;
import org.springframework.vault.authentication.Sha256;

public class IpAddressUserId
implements AppIdUserIdMechanism {
    @Override
    public String createUserId() {
        try {
            return Sha256.toSha256(InetAddress.getLocalHost().getHostAddress());
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

