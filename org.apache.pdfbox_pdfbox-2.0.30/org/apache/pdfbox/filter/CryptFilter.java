/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.IdentityFilter;

final class CryptFilter
extends Filter {
    CryptFilter() {
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        COSName encryptionName = (COSName)parameters.getDictionaryObject(COSName.NAME);
        if (encryptionName == null || encryptionName.equals(COSName.IDENTITY)) {
            IdentityFilter identityFilter = new IdentityFilter();
            ((Filter)identityFilter).decode(encoded, decoded, parameters, index);
            return new DecodeResult(parameters);
        }
        throw new IOException("Unsupported crypt filter " + encryptionName.getName());
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        COSName encryptionName = (COSName)parameters.getDictionaryObject(COSName.NAME);
        if (encryptionName != null && !encryptionName.equals(COSName.IDENTITY)) {
            throw new IOException("Unsupported crypt filter " + encryptionName.getName());
        }
        IdentityFilter identityFilter = new IdentityFilter();
        ((Filter)identityFilter).encode(input, encoded, parameters);
    }
}

