/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.format.Formatter
 */
package org.springframework.data.geo.format;

import java.text.ParseException;
import java.util.Locale;
import javax.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.geo.Point;
import org.springframework.format.Formatter;

public enum PointFormatter implements Converter<String, Point>,
Formatter<Point>
{
    INSTANCE;

    public static final GenericConverter.ConvertiblePair CONVERTIBLE;
    private static final String INVALID_FORMAT = "Expected two doubles separated by a comma but got '%s'!";

    @Nonnull
    public Point convert(String source) {
        String[] parts = source.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException(String.format(INVALID_FORMAT, source));
        }
        try {
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
            return new Point(longitude, latitude);
        }
        catch (NumberFormatException o_O) {
            throw new IllegalArgumentException(String.format(INVALID_FORMAT, source), o_O);
        }
    }

    public String print(Point point, Locale locale) {
        return point == null ? null : String.format("%s,%s", point.getY(), point.getX());
    }

    public Point parse(String text, Locale locale) throws ParseException {
        return this.convert(text);
    }

    static {
        CONVERTIBLE = new GenericConverter.ConvertiblePair(String.class, Point.class);
    }
}

