/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.DigestInfo
 */
package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.x509.DigestInfo;

public class MessageImprint {
    private final DigestInfo messageImprint;

    public MessageImprint(DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }

    public DigestInfo toASN1Structure() {
        return this.messageImprint;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MessageImprint) {
            return this.messageImprint.equals((Object)((MessageImprint)o).messageImprint);
        }
        return false;
    }

    public int hashCode() {
        return this.messageImprint.hashCode();
    }
}

