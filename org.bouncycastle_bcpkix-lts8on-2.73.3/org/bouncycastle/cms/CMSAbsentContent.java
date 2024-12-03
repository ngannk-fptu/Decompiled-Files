/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSReadable;
import org.bouncycastle.cms.CMSTypedData;

public class CMSAbsentContent
implements CMSTypedData,
CMSReadable {
    private final ASN1ObjectIdentifier type;

    public CMSAbsentContent() {
        this(CMSObjectIdentifiers.data);
    }

    public CMSAbsentContent(ASN1ObjectIdentifier type) {
        this.type = type;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public void write(OutputStream zOut) throws IOException, CMSException {
    }

    @Override
    public Object getContent() {
        return null;
    }

    @Override
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}

