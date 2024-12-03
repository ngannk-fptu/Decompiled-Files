/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSReadable;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.util.Arrays;

public class CMSProcessableByteArray
implements CMSTypedData,
CMSReadable {
    private final ASN1ObjectIdentifier type;
    private final byte[] bytes;

    public CMSProcessableByteArray(byte[] bytes) {
        this(CMSObjectIdentifiers.data, bytes);
    }

    public CMSProcessableByteArray(ASN1ObjectIdentifier type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public void write(OutputStream zOut) throws IOException, CMSException {
        zOut.write(this.bytes);
    }

    @Override
    public Object getContent() {
        return Arrays.clone((byte[])this.bytes);
    }

    @Override
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}

