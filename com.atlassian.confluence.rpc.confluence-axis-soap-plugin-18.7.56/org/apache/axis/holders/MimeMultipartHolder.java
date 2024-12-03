/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.MimeMultipart
 */
package org.apache.axis.holders;

import javax.mail.internet.MimeMultipart;
import javax.xml.rpc.holders.Holder;

public final class MimeMultipartHolder
implements Holder {
    public MimeMultipart value;

    public MimeMultipartHolder() {
    }

    public MimeMultipartHolder(MimeMultipart value) {
        this.value = value;
    }
}

