/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeBodyPart
 *  org.bouncycastle.cms.CMSException
 *  org.bouncycastle.cms.CMSProcessable
 */
package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.io.OutputStream;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.mail.smime.util.CRLFOutputStream;

public class CMSProcessableBodyPartOutbound
implements CMSProcessable {
    private BodyPart bodyPart;
    private String defaultContentTransferEncoding;

    public CMSProcessableBodyPartOutbound(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    public CMSProcessableBodyPartOutbound(BodyPart bodyPart, String defaultContentTransferEncoding) {
        this.bodyPart = bodyPart;
        this.defaultContentTransferEncoding = defaultContentTransferEncoding;
    }

    public void write(OutputStream out) throws IOException, CMSException {
        try {
            if (SMIMEUtil.isCanonicalisationRequired((MimeBodyPart)this.bodyPart, this.defaultContentTransferEncoding)) {
                out = new CRLFOutputStream(out);
            }
            this.bodyPart.writeTo(out);
        }
        catch (MessagingException e) {
            throw new CMSException("can't write BodyPart to stream.", (Exception)((Object)e));
        }
    }

    public Object getContent() {
        return this.bodyPart;
    }
}

