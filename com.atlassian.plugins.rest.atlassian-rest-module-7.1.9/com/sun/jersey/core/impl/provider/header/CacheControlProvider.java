/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.spi.HeaderDelegateProvider;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.CacheControl;

public final class CacheControlProvider
implements HeaderDelegateProvider<CacheControl> {
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private static final Pattern COMMA_SEPARATED_LIST = Pattern.compile("[\\s]*,[\\s]*");

    @Override
    public boolean supports(Class<?> type) {
        return type == CacheControl.class;
    }

    @Override
    public String toString(CacheControl header) {
        StringBuffer b = new StringBuffer();
        if (header.isPrivate()) {
            this.appendQuotedWithSeparator(b, "private", this.buildListValue(header.getPrivateFields()));
        }
        if (header.isNoCache()) {
            this.appendQuotedWithSeparator(b, "no-cache", this.buildListValue(header.getNoCacheFields()));
        }
        if (header.isNoStore()) {
            this.appendWithSeparator(b, "no-store");
        }
        if (header.isNoTransform()) {
            this.appendWithSeparator(b, "no-transform");
        }
        if (header.isMustRevalidate()) {
            this.appendWithSeparator(b, "must-revalidate");
        }
        if (header.isProxyRevalidate()) {
            this.appendWithSeparator(b, "proxy-revalidate");
        }
        if (header.getMaxAge() != -1) {
            this.appendWithSeparator(b, "max-age", header.getMaxAge());
        }
        if (header.getSMaxAge() != -1) {
            this.appendWithSeparator(b, "s-maxage", header.getSMaxAge());
        }
        for (Map.Entry<String, String> e : header.getCacheExtension().entrySet()) {
            this.appendWithSeparator(b, e.getKey(), this.quoteIfWhitespace(e.getValue()));
        }
        return b.toString();
    }

    private void readFieldNames(List<String> fieldNames, HttpHeaderReader reader, String directiveName) throws ParseException {
        if (!reader.hasNextSeparator('=', false)) {
            return;
        }
        reader.nextSeparator('=');
        fieldNames.addAll(Arrays.asList(COMMA_SEPARATED_LIST.split(reader.nextQuotedString())));
    }

    private int readIntValue(HttpHeaderReader reader, String directiveName) throws ParseException {
        reader.nextSeparator('=');
        int index = reader.getIndex();
        try {
            return Integer.parseInt(reader.nextToken());
        }
        catch (NumberFormatException nfe) {
            ParseException pe = new ParseException("Error parsing integer value for " + directiveName + " directive", index);
            pe.initCause(nfe);
            throw pe;
        }
    }

    private void readDirective(CacheControl cacheControl, HttpHeaderReader reader) throws ParseException {
        String directiveName = reader.nextToken();
        if (directiveName.equalsIgnoreCase("private")) {
            cacheControl.setPrivate(true);
            this.readFieldNames(cacheControl.getPrivateFields(), reader, directiveName);
        } else if (directiveName.equalsIgnoreCase("public")) {
            cacheControl.getCacheExtension().put(directiveName, null);
        } else if (directiveName.equalsIgnoreCase("no-cache")) {
            cacheControl.setNoCache(true);
            this.readFieldNames(cacheControl.getNoCacheFields(), reader, directiveName);
        } else if (directiveName.equalsIgnoreCase("no-store")) {
            cacheControl.setNoStore(true);
        } else if (directiveName.equalsIgnoreCase("no-transform")) {
            cacheControl.setNoTransform(true);
        } else if (directiveName.equalsIgnoreCase("must-revalidate")) {
            cacheControl.setMustRevalidate(true);
        } else if (directiveName.equalsIgnoreCase("proxy-revalidate")) {
            cacheControl.setProxyRevalidate(true);
        } else if (directiveName.equalsIgnoreCase("max-age")) {
            cacheControl.setMaxAge(this.readIntValue(reader, directiveName));
        } else if (directiveName.equalsIgnoreCase("s-maxage")) {
            cacheControl.setSMaxAge(this.readIntValue(reader, directiveName));
        } else {
            String value = null;
            if (reader.hasNextSeparator('=', false)) {
                reader.nextSeparator('=');
                value = reader.nextTokenOrQuotedString();
            }
            cacheControl.getCacheExtension().put(directiveName, value);
        }
    }

    @Override
    public CacheControl fromString(String header) {
        if (header == null) {
            throw new IllegalArgumentException("Cache control is null");
        }
        try {
            HttpHeaderReader reader = HttpHeaderReader.newInstance(header);
            CacheControl cacheControl = new CacheControl();
            cacheControl.setNoTransform(false);
            while (reader.hasNext()) {
                this.readDirective(cacheControl, reader);
                if (!reader.hasNextSeparator(',', true)) continue;
                reader.nextSeparator(',');
            }
            return cacheControl;
        }
        catch (ParseException pe) {
            throw new IllegalArgumentException("Error parsing cache control '" + header + "'", pe);
        }
    }

    private void appendWithSeparator(StringBuffer b, String field) {
        if (b.length() > 0) {
            b.append(", ");
        }
        b.append(field);
    }

    private void appendQuotedWithSeparator(StringBuffer b, String field, String value) {
        this.appendWithSeparator(b, field);
        if (value != null && value.length() > 0) {
            b.append("=\"");
            b.append(value);
            b.append("\"");
        }
    }

    private void appendWithSeparator(StringBuffer b, String field, String value) {
        this.appendWithSeparator(b, field);
        if (value != null && value.length() > 0) {
            b.append("=");
            b.append(value);
        }
    }

    private void appendWithSeparator(StringBuffer b, String field, int value) {
        this.appendWithSeparator(b, field);
        b.append("=");
        b.append(value);
    }

    private String buildListValue(List<String> values) {
        StringBuffer b = new StringBuffer();
        for (String value : values) {
            this.appendWithSeparator(b, value);
        }
        return b.toString();
    }

    private String quoteIfWhitespace(String value) {
        if (value == null) {
            return null;
        }
        Matcher m = WHITESPACE.matcher(value);
        if (m.find()) {
            return "\"" + value + "\"";
        }
        return value;
    }
}

