/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.eventstream;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import software.amazon.eventstream.Message;
import software.amazon.eventstream.Prelude;

public final class MessageDecoder {
    private static final int INITIAL_BUFFER_SIZE = 0x200000;
    private final Consumer<Message> messageConsumer;
    private List<Message> bufferedOutput;
    private ByteBuffer buf;
    private Prelude currentPrelude;

    public MessageDecoder() {
        this.messageConsumer = message -> this.bufferedOutput.add((Message)message);
        this.bufferedOutput = new ArrayList<Message>();
        this.buf = ByteBuffer.allocate(0x200000);
    }

    public MessageDecoder(Consumer<Message> messageConsumer) {
        this(messageConsumer, 0x200000);
    }

    MessageDecoder(Consumer<Message> messageConsumer, int initialBufferSize) {
        this.messageConsumer = messageConsumer;
        this.buf = ByteBuffer.allocate(initialBufferSize);
        this.bufferedOutput = null;
    }

    public List<Message> getDecodedMessages() {
        if (this.bufferedOutput == null) {
            throw new IllegalStateException("");
        }
        List<Message> ret = this.bufferedOutput;
        this.bufferedOutput = new ArrayList<Message>();
        return Collections.unmodifiableList(ret);
    }

    public void feed(byte[] bytes) {
        this.feed(ByteBuffer.wrap(bytes));
    }

    public void feed(byte[] bytes, int offset, int length) {
        this.feed(ByteBuffer.wrap(bytes, offset, length));
    }

    public MessageDecoder feed(ByteBuffer byteBuffer) {
        int bytesToRead = byteBuffer.remaining();
        int bytesConsumed = 0;
        while (bytesConsumed < bytesToRead) {
            int numBytesToWrite;
            ByteBuffer readView = this.updateReadView();
            if (this.currentPrelude == null) {
                numBytesToWrite = Math.min(15 - readView.remaining(), bytesToRead - bytesConsumed);
                this.feedBuf(byteBuffer, numBytesToWrite);
                bytesConsumed += numBytesToWrite;
                readView = this.updateReadView();
                if (readView.remaining() >= 15) {
                    this.currentPrelude = Prelude.decode(readView.duplicate());
                    if (this.buf.capacity() < this.currentPrelude.getTotalLength()) {
                        this.buf = ByteBuffer.allocate(this.currentPrelude.getTotalLength());
                        this.buf.put(readView);
                        readView = this.updateReadView();
                    }
                }
            }
            if (this.currentPrelude == null) continue;
            numBytesToWrite = Math.min(this.currentPrelude.getTotalLength() - readView.remaining(), bytesToRead - bytesConsumed);
            this.feedBuf(byteBuffer, numBytesToWrite);
            bytesConsumed += numBytesToWrite;
            readView = this.updateReadView();
            if (readView.remaining() < this.currentPrelude.getTotalLength()) continue;
            this.messageConsumer.accept(Message.decode(this.currentPrelude, readView));
            this.buf.clear();
            this.currentPrelude = null;
        }
        return this;
    }

    private void feedBuf(ByteBuffer byteBuffer, int numBytesToWrite) {
        this.buf.put((ByteBuffer)byteBuffer.duplicate().limit(byteBuffer.position() + numBytesToWrite));
        byteBuffer.position(byteBuffer.position() + numBytesToWrite);
    }

    private ByteBuffer updateReadView() {
        return (ByteBuffer)this.buf.duplicate().flip();
    }

    int currentBufferSize() {
        return this.buf.capacity();
    }
}

