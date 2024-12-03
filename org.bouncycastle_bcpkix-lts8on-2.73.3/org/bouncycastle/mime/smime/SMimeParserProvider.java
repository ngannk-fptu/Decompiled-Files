/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.mime.BasicMimeParser;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeParser;
import org.bouncycastle.mime.MimeParserProvider;
import org.bouncycastle.mime.smime.SMimeParserContext;
import org.bouncycastle.mime.smime.SMimeUtils;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class SMimeParserProvider
implements MimeParserProvider {
    private final String defaultContentTransferEncoding;
    private final DigestCalculatorProvider digestCalculatorProvider;

    public SMimeParserProvider(String defaultContentTransferEncoding, DigestCalculatorProvider digestCalculatorProvider) {
        this.defaultContentTransferEncoding = defaultContentTransferEncoding;
        this.digestCalculatorProvider = digestCalculatorProvider;
    }

    @Override
    public MimeParser createParser(InputStream source) throws IOException {
        return new BasicMimeParser(new SMimeParserContext(this.defaultContentTransferEncoding, this.digestCalculatorProvider), SMimeUtils.autoBuffer(source));
    }

    @Override
    public MimeParser createParser(Headers headers, InputStream source) throws IOException {
        return new BasicMimeParser(new SMimeParserContext(this.defaultContentTransferEncoding, this.digestCalculatorProvider), headers, SMimeUtils.autoBuffer(source));
    }
}

