/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.ActivationDataFlavor
 *  javax.mail.internet.MimeBodyPart
 */
package org.bouncycastle.mail.smime.handlers;

import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.mail.smime.handlers.PKCS7ContentHandler;

public class pkcs7_signature
extends PKCS7ContentHandler {
    private static final ActivationDataFlavor ADF = new ActivationDataFlavor(MimeBodyPart.class, "application/pkcs7-signature", "Signature");
    private static final DataFlavor[] DFS = new DataFlavor[]{ADF};

    public pkcs7_signature() {
        super(ADF, DFS);
    }
}

