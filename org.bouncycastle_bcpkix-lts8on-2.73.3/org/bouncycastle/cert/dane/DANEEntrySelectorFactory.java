/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Strings
 *  org.bouncycastle.util.encoders.Hex
 */
package org.bouncycastle.cert.dane;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.cert.dane.DANEEntrySelector;
import org.bouncycastle.cert.dane.DANEException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class DANEEntrySelectorFactory {
    private final DigestCalculator digestCalculator;

    public DANEEntrySelectorFactory(DigestCalculator digestCalculator) {
        this.digestCalculator = digestCalculator;
    }

    public DANEEntrySelector createSelector(String emailAddress) throws DANEException {
        byte[] enc = Strings.toUTF8ByteArray((String)emailAddress.substring(0, emailAddress.indexOf(64)));
        try {
            OutputStream cOut = this.digestCalculator.getOutputStream();
            cOut.write(enc);
            cOut.close();
        }
        catch (IOException e) {
            throw new DANEException("Unable to calculate digest string: " + e.getMessage(), e);
        }
        byte[] hash = this.digestCalculator.getDigest();
        String domainName = Strings.fromByteArray((byte[])Hex.encode((byte[])hash)) + "._smimecert." + emailAddress.substring(emailAddress.indexOf(64) + 1);
        return new DANEEntrySelector(domainName);
    }
}

