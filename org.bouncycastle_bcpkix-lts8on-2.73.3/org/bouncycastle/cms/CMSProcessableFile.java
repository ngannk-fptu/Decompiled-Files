/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.cms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSReadable;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.util.io.Streams;

public class CMSProcessableFile
implements CMSTypedData,
CMSReadable {
    private static final int DEFAULT_BUF_SIZE = 32768;
    private final ASN1ObjectIdentifier type;
    private final File file;
    private final int bufSize;

    public CMSProcessableFile(File file) {
        this(file, 32768);
    }

    public CMSProcessableFile(File file, int bufSize) {
        this(CMSObjectIdentifiers.data, file, bufSize);
    }

    public CMSProcessableFile(ASN1ObjectIdentifier type, File file, int bufSize) {
        this.type = type;
        this.file = file;
        this.bufSize = bufSize;
    }

    @Override
    public InputStream getInputStream() throws IOException, CMSException {
        return new BufferedInputStream(new FileInputStream(this.file), this.bufSize);
    }

    @Override
    public void write(OutputStream zOut) throws IOException, CMSException {
        FileInputStream fIn = new FileInputStream(this.file);
        Streams.pipeAll((InputStream)fIn, (OutputStream)zOut, (int)this.bufSize);
        fIn.close();
    }

    @Override
    public Object getContent() {
        return this.file;
    }

    @Override
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}

