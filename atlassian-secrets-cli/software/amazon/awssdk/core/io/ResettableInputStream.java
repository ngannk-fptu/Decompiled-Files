/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.io.ReleasableInputStream;

@NotThreadSafe
@SdkProtectedApi
public class ResettableInputStream
extends ReleasableInputStream {
    private static final Logger log = LoggerFactory.getLogger(ResettableInputStream.class);
    private final File file;
    private FileInputStream fis;
    private FileChannel fileChannel;
    private long markPos;

    public ResettableInputStream(File file) throws IOException {
        this(new FileInputStream(file), file);
    }

    public ResettableInputStream(FileInputStream fis) throws IOException {
        this(fis, null);
    }

    private ResettableInputStream(FileInputStream fis, File file) throws IOException {
        super(fis);
        this.file = file;
        this.fis = fis;
        this.fileChannel = fis.getChannel();
        this.markPos = this.fileChannel.position();
    }

    public static ResettableInputStream newResettableInputStream(File file) {
        return ResettableInputStream.newResettableInputStream(file, null);
    }

    public static ResettableInputStream newResettableInputStream(File file, String errmsg) {
        try {
            return new ResettableInputStream(file);
        }
        catch (IOException e) {
            throw errmsg == null ? SdkClientException.builder().cause(e).build() : SdkClientException.builder().message(errmsg).cause(e).build();
        }
    }

    public static ResettableInputStream newResettableInputStream(FileInputStream fis) {
        return ResettableInputStream.newResettableInputStream(fis, null);
    }

    public static ResettableInputStream newResettableInputStream(FileInputStream fis, String errmsg) {
        try {
            return new ResettableInputStream(fis);
        }
        catch (IOException e) {
            throw SdkClientException.builder().message(errmsg).cause(e).build();
        }
    }

    @Override
    public final boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int ignored) {
        this.abortIfNeeded();
        try {
            this.markPos = this.fileChannel.position();
        }
        catch (IOException e) {
            throw SdkClientException.builder().message("Failed to mark the file position").cause(e).build();
        }
        if (log.isTraceEnabled()) {
            log.trace("File input stream marked at position " + this.markPos);
        }
    }

    @Override
    public void reset() throws IOException {
        this.abortIfNeeded();
        this.fileChannel.position(this.markPos);
        if (log.isTraceEnabled()) {
            log.trace("Reset to position " + this.markPos);
        }
    }

    @Override
    public int available() throws IOException {
        this.abortIfNeeded();
        return this.fis.available();
    }

    @Override
    public int read() throws IOException {
        this.abortIfNeeded();
        return this.fis.read();
    }

    @Override
    public long skip(long n) throws IOException {
        this.abortIfNeeded();
        return this.fis.skip(n);
    }

    @Override
    public int read(byte[] arg0, int arg1, int arg2) throws IOException {
        this.abortIfNeeded();
        return this.fis.read(arg0, arg1, arg2);
    }

    public File getFile() {
        return this.file;
    }
}

