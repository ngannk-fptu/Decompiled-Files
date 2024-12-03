/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webresource.spi;

import com.atlassian.webresource.spi.TransformerDto;

public class TransformationDto {
    public final String extension;
    public final Iterable<TransformerDto> transformers;

    public TransformationDto(String extension, Iterable<TransformerDto> transformers) {
        this.extension = extension;
        this.transformers = transformers;
    }
}

