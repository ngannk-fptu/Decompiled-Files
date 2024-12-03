/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.core.internal.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.util.FakeIoException;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public class UnreliableFilterInputStream
extends FilterInputStream {
    private final boolean isFakeIoException;
    private int maxNumErrors = 1;
    private int currNumErrors;
    private int bytesReadBeforeException = 100;
    private int marked;
    private int position;
    private int resetCount;
    private int resetIntervalBeforeException;

    public UnreliableFilterInputStream(InputStream in, boolean isFakeIoException) {
        super(in);
        this.isFakeIoException = isFakeIoException;
    }

    @Override
    public int read() throws IOException {
        int read = super.read();
        if (read != -1) {
            ++this.position;
        }
        this.triggerError();
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.triggerError();
        int read = super.read(b, off, len);
        this.position += read;
        this.triggerError();
        return read;
    }

    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
        this.marked = this.position;
    }

    @Override
    public void reset() throws IOException {
        ++this.resetCount;
        super.reset();
        this.position = this.marked;
    }

    private void triggerError() throws FakeIoException {
        if (this.currNumErrors >= this.maxNumErrors) {
            return;
        }
        if (this.position >= this.bytesReadBeforeException) {
            if (this.resetIntervalBeforeException > 0 && this.resetCount % this.resetIntervalBeforeException != this.resetIntervalBeforeException - 1) {
                return;
            }
            ++this.currNumErrors;
            if (this.isFakeIoException) {
                throw new FakeIoException("Fake IO error " + this.currNumErrors + " on UnreliableFileInputStream: " + this);
            }
            throw new RuntimeException("Injected runtime error " + this.currNumErrors + " on UnreliableFileInputStream: " + this);
        }
    }

    public int getCurrNumErrors() {
        return this.currNumErrors;
    }

    public int getMaxNumErrors() {
        return this.maxNumErrors;
    }

    public UnreliableFilterInputStream withMaxNumErrors(int maxNumErrors) {
        this.maxNumErrors = maxNumErrors;
        return this;
    }

    public UnreliableFilterInputStream withBytesReadBeforeException(int bytesReadBeforeException) {
        this.bytesReadBeforeException = bytesReadBeforeException;
        return this;
    }

    public int getBytesReadBeforeException() {
        return this.bytesReadBeforeException;
    }

    public UnreliableFilterInputStream withResetIntervalBeforeException(int resetIntervalBeforeException) {
        this.resetIntervalBeforeException = resetIntervalBeforeException;
        return this;
    }

    public int getResetIntervalBeforeException() {
        return this.resetIntervalBeforeException;
    }

    public int getMarked() {
        return this.marked;
    }

    public int getPosition() {
        return this.position;
    }

    public boolean isFakeIoException() {
        return this.isFakeIoException;
    }

    public int getResetCount() {
        return this.resetCount;
    }

    public String toString() {
        return ToString.builder((String)"UnreliableFilterInputStream").add("isFakeIoException", (Object)this.isFakeIoException).add("maxNumErrors", (Object)this.maxNumErrors).add("currNumErrors", (Object)this.currNumErrors).add("bytesReadBeforeException", (Object)this.bytesReadBeforeException).add("marked", (Object)this.marked).add("position", (Object)this.position).add("resetCount", (Object)this.resetCount).add("resetIntervalBeforeException", (Object)this.resetIntervalBeforeException).toString();
    }
}

