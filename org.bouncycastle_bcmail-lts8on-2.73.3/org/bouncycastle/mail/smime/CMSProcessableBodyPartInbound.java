/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  org.bouncycastle.cms.CMSException
 *  org.bouncycastle.cms.CMSProcessable
 */
package org.bouncycastle.mail.smime;

import java.io.IOException;
import java.io.OutputStream;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.mail.smime.SMIMEUtil;

public class CMSProcessableBodyPartInbound
implements CMSProcessable {
    private final BodyPart bodyPart;
    private final String defaultContentTransferEncoding;

    public CMSProcessableBodyPartInbound(BodyPart bodyPart) {
        this(bodyPart, "7bit");
    }

    public CMSProcessableBodyPartInbound(BodyPart bodyPart, String defaultContentTransferEncoding) {
        this.bodyPart = bodyPart;
        this.defaultContentTransferEncoding = defaultContentTransferEncoding;
    }

    public void write(OutputStream out) throws IOException, CMSException {
        try {
            SMIMEUtil.outputBodyPart(out, true, this.bodyPart, this.defaultContentTransferEncoding);
        }
        catch (MessagingException e) {
            throw new CMSException("can't write BodyPart to stream: " + (Object)((Object)e), (Exception)((Object)e));
        }
    }

    public Object getContent() {
        return this.bodyPart;
    }
}

