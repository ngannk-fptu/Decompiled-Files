/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.Checksum;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class ChecksumInputStream
extends FilterInputStream {
    private final Collection<Checksum> checksums = new ArrayList<Checksum>();

    public ChecksumInputStream(InputStream stream, Collection<? extends Checksum> checksums) {
        super(stream);
        this.checksums.addAll(checksums);
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int read = this.read(b, 0, 1);
        if (read > 0) {
            this.checksums.forEach(checksum -> checksum.update(b, 0, 1));
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = this.in.read(b, off, len);
        if (read > 0) {
            this.checksums.forEach(checksum -> checksum.update(b, off, read));
        }
        return read;
    }
}

