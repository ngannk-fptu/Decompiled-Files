/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.codec;

import java.util.Arrays;
import java.util.List;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Encoder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

public abstract class AbstractEncoder<T>
implements Encoder<T> {
    private final List<MimeType> encodableMimeTypes;

    protected AbstractEncoder(MimeType ... supportedMimeTypes) {
        this.encodableMimeTypes = Arrays.asList(supportedMimeTypes);
    }

    @Override
    public List<MimeType> getEncodableMimeTypes() {
        return this.encodableMimeTypes;
    }

    @Override
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        if (mimeType == null) {
            return true;
        }
        return this.encodableMimeTypes.stream().anyMatch(candidate -> candidate.isCompatibleWith(mimeType));
    }
}

