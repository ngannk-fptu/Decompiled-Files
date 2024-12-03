/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.GenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.data.convert.CustomConversions
 *  org.springframework.data.convert.CustomConversions$StoreConversions
 *  org.springframework.data.convert.JodaTimeConverters
 *  org.springframework.data.convert.WritingConverter
 *  org.springframework.data.mapping.model.SimpleTypeHolder
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.repository.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.JodaTimeConverters;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.lang.Nullable;
import org.springframework.vault.repository.mapping.VaultSimpleTypes;

public class VaultCustomConversions
extends CustomConversions {
    private static final CustomConversions.StoreConversions STORE_CONVERSIONS;
    private static final List<Object> STORE_CONVERTERS;

    VaultCustomConversions() {
        this(Collections.emptyList());
    }

    public VaultCustomConversions(List<?> converters) {
        super(STORE_CONVERSIONS, converters);
    }

    static {
        ArrayList<CustomToStringConverter> converters = new ArrayList<CustomToStringConverter>();
        converters.add(CustomToStringConverter.INSTANCE);
        converters.addAll(JodaTimeConverters.getConvertersToRegister());
        STORE_CONVERTERS = Collections.unmodifiableList(converters);
        STORE_CONVERSIONS = CustomConversions.StoreConversions.of((SimpleTypeHolder)VaultSimpleTypes.HOLDER, STORE_CONVERTERS);
    }

    @WritingConverter
    private static enum CustomToStringConverter implements GenericConverter
    {
        INSTANCE;


        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            GenericConverter.ConvertiblePair localeToString = new GenericConverter.ConvertiblePair(Locale.class, String.class);
            GenericConverter.ConvertiblePair booleanToString = new GenericConverter.ConvertiblePair(Character.class, String.class);
            return new HashSet<GenericConverter.ConvertiblePair>(Arrays.asList(localeToString, booleanToString));
        }

        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return source != null ? source.toString() : null;
        }
    }
}

