/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.utility.nullability.MaybeNull;

@HashCodeAndEqualsPlugin.Enhance
public class StreamDrainer {
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final StreamDrainer DEFAULT = new StreamDrainer();
    private static final int END_OF_STREAM = -1;
    private static final int FROM_BEGINNING = 0;
    private final int bufferSize;

    public StreamDrainer() {
        this(1024);
    }

    public StreamDrainer(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public byte[] drain(InputStream inputStream) throws IOException {
        int currentRead;
        ArrayList<byte[]> previousBytes = new ArrayList<byte[]>();
        byte[] currentArray = new byte[this.bufferSize];
        int currentIndex = 0;
        do {
            if ((currentIndex += Math.max(currentRead = inputStream.read(currentArray, currentIndex, this.bufferSize - currentIndex), 0)) != this.bufferSize) continue;
            previousBytes.add(currentArray);
            currentArray = new byte[this.bufferSize];
            currentIndex = 0;
        } while (currentRead != -1);
        byte[] result = new byte[previousBytes.size() * this.bufferSize + currentIndex];
        int arrayIndex = 0;
        for (byte[] previousByte : previousBytes) {
            System.arraycopy(previousByte, 0, result, arrayIndex++ * this.bufferSize, this.bufferSize);
        }
        System.arraycopy(currentArray, 0, result, arrayIndex * this.bufferSize, currentIndex);
        return result;
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        return this.bufferSize == ((StreamDrainer)object).bufferSize;
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.bufferSize;
    }
}

