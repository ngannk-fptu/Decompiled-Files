/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1SequenceParser
 *  org.bouncycastle.asn1.ASN1StreamParser
 *  org.bouncycastle.asn1.cms.ContentInfoParser
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.cms.CMSException;

public class CMSContentInfoParser {
    protected ContentInfoParser _contentInfo;
    protected InputStream _data;

    protected CMSContentInfoParser(InputStream data) throws CMSException {
        this._data = data;
        try {
            ASN1StreamParser in = new ASN1StreamParser(data);
            ASN1SequenceParser seqParser = (ASN1SequenceParser)in.readObject();
            if (seqParser == null) {
                throw new CMSException("No content found.");
            }
            this._contentInfo = new ContentInfoParser(seqParser);
        }
        catch (IOException e) {
            throw new CMSException("IOException reading content.", e);
        }
        catch (ClassCastException e) {
            throw new CMSException("Unexpected object reading content.", e);
        }
    }

    public void close() throws IOException {
        this._data.close();
    }
}

