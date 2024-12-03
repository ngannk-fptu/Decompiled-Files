/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.CodedInputStream
 *  com.google.protobuf.ExtensionRegistry
 *  com.google.protobuf.ExtensionRegistryLite
 *  com.google.protobuf.Message
 *  com.google.protobuf.Message$Builder
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.Decoder
 *  org.springframework.core.codec.DecodingException
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferLimitException
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.MimeType
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.protobuf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Message;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.protobuf.ProtobufCodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProtobufDecoder
extends ProtobufCodecSupport
implements Decoder<Message> {
    protected static final int DEFAULT_MESSAGE_MAX_SIZE = 262144;
    private static final ConcurrentMap<Class<?>, Method> methodCache = new ConcurrentReferenceHashMap();
    private final ExtensionRegistry extensionRegistry;
    private int maxMessageSize = 262144;

    public ProtobufDecoder() {
        this(ExtensionRegistry.newInstance());
    }

    public ProtobufDecoder(ExtensionRegistry extensionRegistry) {
        Assert.notNull((Object)extensionRegistry, (String)"ExtensionRegistry must not be null");
        this.extensionRegistry = extensionRegistry;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public int getMaxMessageSize() {
        return this.maxMessageSize;
    }

    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return Message.class.isAssignableFrom(elementType.toClass()) && this.supportsMimeType(mimeType);
    }

    public Flux<Message> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        MessageDecoderFunction decoderFunction = new MessageDecoderFunction(elementType, this.maxMessageSize);
        return Flux.from(inputStream).flatMapIterable((Function)decoderFunction).doOnTerminate(decoderFunction::discard);
    }

    public Mono<Message> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(inputStream, (int)this.maxMessageSize).map(dataBuffer -> this.decode((DataBuffer)dataBuffer, elementType, mimeType, hints));
    }

    public Message decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
        try {
            Message.Builder builder = ProtobufDecoder.getMessageBuilder(targetType.toClass());
            ByteBuffer buffer = dataBuffer.asByteBuffer();
            builder.mergeFrom(CodedInputStream.newInstance((ByteBuffer)buffer), (ExtensionRegistryLite)this.extensionRegistry);
            Message message = builder.build();
            return message;
        }
        catch (IOException ex) {
            throw new DecodingException("I/O error while parsing input stream", (Throwable)ex);
        }
        catch (Exception ex) {
            throw new DecodingException("Could not read Protobuf message: " + ex.getMessage(), (Throwable)ex);
        }
        finally {
            DataBufferUtils.release((DataBuffer)dataBuffer);
        }
    }

    private static Message.Builder getMessageBuilder(Class<?> clazz) throws Exception {
        Method method = (Method)methodCache.get(clazz);
        if (method == null) {
            method = clazz.getMethod("newBuilder", new Class[0]);
            methodCache.put(clazz, method);
        }
        return (Message.Builder)method.invoke(clazz, new Object[0]);
    }

    public List<MimeType> getDecodableMimeTypes() {
        return this.getMimeTypes();
    }

    private class MessageDecoderFunction
    implements Function<DataBuffer, Iterable<? extends Message>> {
        private final ResolvableType elementType;
        private final int maxMessageSize;
        @Nullable
        private DataBuffer output;
        private int messageBytesToRead;
        private int offset;

        public MessageDecoderFunction(ResolvableType elementType, int maxMessageSize) {
            this.elementType = elementType;
            this.maxMessageSize = maxMessageSize;
        }

        @Override
        public Iterable<? extends Message> apply(DataBuffer input) {
            try {
                ArrayList<Message> arrayList;
                int remainingBytesToRead;
                ArrayList<Message> messages = new ArrayList<Message>();
                do {
                    if (this.output == null) {
                        if (!this.readMessageSize(input)) {
                            arrayList = messages;
                            return arrayList;
                        }
                        if (this.maxMessageSize > 0 && this.messageBytesToRead > this.maxMessageSize) {
                            throw new DataBufferLimitException("The number of bytes to read for message (" + this.messageBytesToRead + ") exceeds the configured limit (" + this.maxMessageSize + ")");
                        }
                        this.output = input.factory().allocateBuffer(this.messageBytesToRead);
                    }
                    int chunkBytesToRead = Math.min(this.messageBytesToRead, input.readableByteCount());
                    remainingBytesToRead = input.readableByteCount() - chunkBytesToRead;
                    byte[] bytesToWrite = new byte[chunkBytesToRead];
                    input.read(bytesToWrite, 0, chunkBytesToRead);
                    this.output.write(bytesToWrite);
                    this.messageBytesToRead -= chunkBytesToRead;
                    if (this.messageBytesToRead != 0) continue;
                    CodedInputStream stream = CodedInputStream.newInstance((ByteBuffer)this.output.asByteBuffer());
                    DataBufferUtils.release((DataBuffer)this.output);
                    this.output = null;
                    Message message = ProtobufDecoder.getMessageBuilder(this.elementType.toClass()).mergeFrom(stream, (ExtensionRegistryLite)ProtobufDecoder.this.extensionRegistry).build();
                    messages.add(message);
                } while (remainingBytesToRead > 0);
                arrayList = messages;
                return arrayList;
            }
            catch (DecodingException ex) {
                throw ex;
            }
            catch (IOException ex) {
                throw new DecodingException("I/O error while parsing input stream", (Throwable)ex);
            }
            catch (Exception ex) {
                throw new DecodingException("Could not read Protobuf message: " + ex.getMessage(), (Throwable)ex);
            }
            finally {
                DataBufferUtils.release((DataBuffer)input);
            }
        }

        private boolean readMessageSize(DataBuffer input) {
            byte b;
            if (this.offset == 0) {
                if (input.readableByteCount() == 0) {
                    return false;
                }
                byte firstByte = input.read();
                if ((firstByte & 0x80) == 0) {
                    this.messageBytesToRead = firstByte;
                    return true;
                }
                this.messageBytesToRead = firstByte & 0x7F;
                this.offset = 7;
            }
            if (this.offset < 32) {
                while (this.offset < 32) {
                    if (input.readableByteCount() == 0) {
                        return false;
                    }
                    b = input.read();
                    this.messageBytesToRead |= (b & 0x7F) << this.offset;
                    if ((b & 0x80) == 0) {
                        this.offset = 0;
                        return true;
                    }
                    this.offset += 7;
                }
            }
            while (this.offset < 64) {
                if (input.readableByteCount() == 0) {
                    return false;
                }
                b = input.read();
                if ((b & 0x80) == 0) {
                    this.offset = 0;
                    return true;
                }
                this.offset += 7;
            }
            this.offset = 0;
            throw new DecodingException("Cannot parse message size: malformed varint");
        }

        public void discard() {
            if (this.output != null) {
                DataBufferUtils.release((DataBuffer)this.output);
            }
        }
    }
}

