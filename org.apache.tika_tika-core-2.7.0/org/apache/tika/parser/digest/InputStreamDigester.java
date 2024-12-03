/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser.digest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.BoundedInputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.DigestingParser;
import org.apache.tika.parser.ParseContext;

public class InputStreamDigester
implements DigestingParser.Digester {
    private final String algorithm;
    private final String algorithmKeyName;
    private final DigestingParser.Encoder encoder;
    private final int markLimit;

    public InputStreamDigester(int markLimit, String algorithm, DigestingParser.Encoder encoder) {
        this(markLimit, algorithm, algorithm, encoder);
    }

    public InputStreamDigester(int markLimit, String algorithm, String algorithmKeyName, DigestingParser.Encoder encoder) {
        this.algorithm = algorithm;
        this.algorithmKeyName = algorithmKeyName;
        this.encoder = encoder;
        this.markLimit = markLimit;
        if (markLimit < 0) {
            throw new IllegalArgumentException("markLimit must be >= 0");
        }
    }

    private static MessageDigest updateDigest(MessageDigest digest, InputStream data) throws IOException {
        byte[] buffer = new byte[1024];
        int read = data.read(buffer, 0, 1024);
        while (read > -1) {
            digest.update(buffer, 0, read);
            read = data.read(buffer, 0, 1024);
        }
        return digest;
    }

    private MessageDigest newMessageDigest() {
        try {
            Provider provider = this.getProvider();
            if (provider == null) {
                return MessageDigest.getInstance(this.algorithm);
            }
            return MessageDigest.getInstance(this.algorithm, provider);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected Provider getProvider() {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void digest(InputStream is, Metadata metadata, ParseContext parseContext) throws IOException {
        TikaInputStream tis = TikaInputStream.cast(is);
        if (tis != null && tis.hasFile()) {
            long sz = -1L;
            if (tis.hasFile()) {
                sz = tis.getLength();
            }
            if (sz > (long)this.markLimit) {
                this.digestFile(tis.getFile(), metadata);
                return;
            }
        }
        BoundedInputStream bis = new BoundedInputStream(this.markLimit, is);
        boolean finishedStream = false;
        bis.mark(this.markLimit + 1);
        finishedStream = this.digestStream(bis, metadata);
        bis.reset();
        if (finishedStream) {
            return;
        }
        if (tis != null) {
            this.digestFile(tis.getFile(), metadata);
        } else {
            TemporaryResources tmp = new TemporaryResources();
            try {
                TikaInputStream tmpTikaInputStream = TikaInputStream.get(is, tmp, metadata);
                this.digestFile(tmpTikaInputStream.getFile(), metadata);
            }
            finally {
                try {
                    tmp.dispose();
                }
                catch (TikaException e) {
                    throw new IOException(e);
                }
            }
        }
    }

    private String getMetadataKey() {
        return "X-TIKA:digest:" + this.algorithmKeyName;
    }

    private void digestFile(File f, Metadata m) throws IOException {
        try (FileInputStream is = new FileInputStream(f);){
            this.digestStream(is, m);
        }
    }

    private boolean digestStream(InputStream is, Metadata metadata) throws IOException {
        MessageDigest messageDigest = this.newMessageDigest();
        InputStreamDigester.updateDigest(messageDigest, is);
        byte[] digestBytes = messageDigest.digest();
        if (is instanceof BoundedInputStream && ((BoundedInputStream)is).hasHitBound()) {
            return false;
        }
        metadata.set(this.getMetadataKey(), this.encoder.encode(digestBytes));
        return true;
    }
}

