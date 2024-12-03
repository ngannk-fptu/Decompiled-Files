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

public class CMSProcessableBodyPart
implements CMSProcessable {
    private BodyPart bodyPart;

    public CMSProcessableBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void write(OutputStream out) throws IOException, CMSException {
        try {
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

