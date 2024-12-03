/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.sandbox;

import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import java.util.Optional;

public interface ConverterProvider {
    public Optional<AbstractConverter> getConverter(FileFormat var1);
}

