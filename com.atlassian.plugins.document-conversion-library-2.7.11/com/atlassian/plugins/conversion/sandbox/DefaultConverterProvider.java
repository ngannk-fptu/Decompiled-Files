/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.plugins.conversion.sandbox;

import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.convert.image.CellsConverter;
import com.atlassian.plugins.conversion.convert.image.ImagingConverter;
import com.atlassian.plugins.conversion.convert.image.SlidesConverter;
import com.atlassian.plugins.conversion.convert.image.WordsConverter;
import com.atlassian.plugins.conversion.sandbox.ConverterProvider;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;

public class DefaultConverterProvider
implements ConverterProvider {
    private static List<AbstractConverter> CONVERTERS = ImmutableList.of((Object)new ImagingConverter(), (Object)new SlidesConverter(), (Object)new WordsConverter(), (Object)new CellsConverter());

    @Override
    public Optional<AbstractConverter> getConverter(FileFormat fileFormat) {
        return CONVERTERS.stream().filter(x -> x.handlesFileFormat(fileFormat)).findFirst();
    }
}

