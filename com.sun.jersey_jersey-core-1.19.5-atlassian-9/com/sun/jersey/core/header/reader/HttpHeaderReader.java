/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Cookie
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.NewCookie
 */
package com.sun.jersey.core.header.reader;

import com.sun.jersey.core.header.AcceptableLanguageTag;
import com.sun.jersey.core.header.AcceptableMediaType;
import com.sun.jersey.core.header.AcceptableToken;
import com.sun.jersey.core.header.HttpDateFormat;
import com.sun.jersey.core.header.MatchingEntityTag;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.header.QualityFactor;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.core.header.reader.CookiesParser;
import com.sun.jersey.core.header.reader.HttpHeaderListAdapter;
import com.sun.jersey.core.header.reader.HttpHeaderReaderImpl;
import com.sun.jersey.core.impl.provider.header.MediaTypeProvider;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

public abstract class HttpHeaderReader {
    private static final ListElementCreator<MatchingEntityTag> MATCHING_ENTITY_TAG_CREATOR = new ListElementCreator<MatchingEntityTag>(){

        @Override
        public MatchingEntityTag create(HttpHeaderReader reader) throws ParseException {
            return MatchingEntityTag.valueOf(reader);
        }
    };
    private static final ListElementCreator<MediaType> MEDIA_TYPE_CREATOR = new ListElementCreator<MediaType>(){

        @Override
        public MediaType create(HttpHeaderReader reader) throws ParseException {
            return MediaTypeProvider.valueOf(reader);
        }
    };
    private static final ListElementCreator<AcceptableMediaType> ACCEPTABLE_MEDIA_TYPE_CREATOR = new ListElementCreator<AcceptableMediaType>(){

        @Override
        public AcceptableMediaType create(HttpHeaderReader reader) throws ParseException {
            return AcceptableMediaType.valueOf(reader);
        }
    };
    private static final Comparator<AcceptableMediaType> ACCEPTABLE_MEDIA_TYPE_COMPARATOR = new Comparator<AcceptableMediaType>(){

        @Override
        public int compare(AcceptableMediaType o1, AcceptableMediaType o2) {
            int i = o2.getQuality() - o1.getQuality();
            if (i != 0) {
                return i;
            }
            return MediaTypes.MEDIA_TYPE_COMPARATOR.compare(o1, o2);
        }
    };
    private static final ListElementCreator<QualitySourceMediaType> QUALITY_SOURCE_MEDIA_TYPE_CREATOR = new ListElementCreator<QualitySourceMediaType>(){

        @Override
        public QualitySourceMediaType create(HttpHeaderReader reader) throws ParseException {
            return QualitySourceMediaType.valueOf(reader);
        }
    };
    private static final ListElementCreator<AcceptableToken> ACCEPTABLE_TOKEN_CREATOR = new ListElementCreator<AcceptableToken>(){

        @Override
        public AcceptableToken create(HttpHeaderReader reader) throws ParseException {
            return new AcceptableToken(reader);
        }
    };
    private static final ListElementCreator<AcceptableLanguageTag> LANGUAGE_CREATOR = new ListElementCreator<AcceptableLanguageTag>(){

        @Override
        public AcceptableLanguageTag create(HttpHeaderReader reader) throws ParseException {
            return new AcceptableLanguageTag(reader);
        }
    };
    private static final Comparator<QualityFactor> QUALITY_COMPARATOR = new Comparator<QualityFactor>(){

        @Override
        public int compare(QualityFactor o1, QualityFactor o2) {
            return o2.getQuality() - o1.getQuality();
        }
    };

    public abstract boolean hasNext();

    public abstract boolean hasNextSeparator(char var1, boolean var2);

    public abstract Event next() throws ParseException;

    public abstract Event next(boolean var1) throws ParseException;

    public abstract Event next(boolean var1, boolean var2) throws ParseException;

    public abstract String nextSeparatedString(char var1, char var2) throws ParseException;

    public abstract Event getEvent();

    public abstract String getEventValue();

    public abstract String getRemainder();

    public abstract int getIndex();

    public String nextToken() throws ParseException {
        Event e = this.next(false);
        if (e != Event.Token) {
            throw new ParseException("Next event is not a Token", this.getIndex());
        }
        return this.getEventValue();
    }

    public char nextSeparator() throws ParseException {
        Event e = this.next(false);
        if (e != Event.Separator) {
            throw new ParseException("Next event is not a Separator", this.getIndex());
        }
        return this.getEventValue().charAt(0);
    }

    public void nextSeparator(char c) throws ParseException {
        Event e = this.next(false);
        if (e != Event.Separator) {
            throw new ParseException("Next event is not a Separator", this.getIndex());
        }
        if (c != this.getEventValue().charAt(0)) {
            throw new ParseException("Expected separator '" + c + "' instead of '" + this.getEventValue().charAt(0) + "'", this.getIndex());
        }
    }

    public String nextQuotedString() throws ParseException {
        Event e = this.next(false);
        if (e != Event.QuotedString) {
            throw new ParseException("Next event is not a Quoted String", this.getIndex());
        }
        return this.getEventValue();
    }

    public String nextTokenOrQuotedString() throws ParseException {
        return this.nextTokenOrQuotedString(false);
    }

    public String nextTokenOrQuotedString(boolean preserveBackslash) throws ParseException {
        Event e = this.next(false, preserveBackslash);
        if (e != Event.Token && e != Event.QuotedString) {
            throw new ParseException("Next event is not a Token or a Quoted String, " + this.getEventValue(), this.getIndex());
        }
        return this.getEventValue();
    }

    public static HttpHeaderReader newInstance(String header) {
        return new HttpHeaderReaderImpl(header);
    }

    public static HttpHeaderReader newInstance(String header, boolean processComments) {
        return new HttpHeaderReaderImpl(header, processComments);
    }

    public static Date readDate(String date) throws ParseException {
        return HttpDateFormat.readDate(date);
    }

    public static int readQualityFactor(String q) throws ParseException {
        char wholeNumber;
        char c;
        if (q == null || q.length() == 0) {
            throw new ParseException("Quality value cannot be null or an empty String", 0);
        }
        int index = 0;
        int length = q.length();
        if (length > 5) {
            throw new ParseException("Quality value is greater than the maximum length, 5", 0);
        }
        if ((c = (wholeNumber = q.charAt(index++))) == '0' || c == '1') {
            if (index == length) {
                return (c - 48) * 1000;
            }
            if ((c = q.charAt(index++)) != '.') {
                throw new ParseException("Error parsing Quality value: a decimal place is expected rather than '" + c + "'", index);
            }
            if (index == length) {
                return (c - 48) * 1000;
            }
        } else if (c == '.') {
            if (index == length) {
                throw new ParseException("Error parsing Quality value: a decimal numeral is expected after the decimal point", index);
            }
        } else {
            throw new ParseException("Error parsing Quality value: a decimal numeral '0' or '1' is expected rather than '" + c + "'", index);
        }
        int value = 0;
        int exponent = 100;
        while (index < length) {
            if ((c = q.charAt(index++)) >= '0' && c <= '9') {
                value += (c - 48) * exponent;
                exponent /= 10;
                continue;
            }
            throw new ParseException("Error parsing Quality value: a decimal numeral is expected rather than '" + c + "'", index);
        }
        if (wholeNumber == '1') {
            if (value > 0) {
                throw new ParseException("The Quality value, " + q + ", is greater than 1", index);
            }
            return 1000;
        }
        return value;
    }

    public static int readQualityFactorParameter(HttpHeaderReader reader) throws ParseException {
        int q = -1;
        while (reader.hasNext()) {
            reader.nextSeparator(';');
            if (!reader.hasNext()) {
                return 1000;
            }
            String name = reader.nextToken();
            reader.nextSeparator('=');
            String value = reader.nextTokenOrQuotedString();
            if (q != -1 || !name.equalsIgnoreCase("q")) continue;
            q = HttpHeaderReader.readQualityFactor(value);
        }
        return q == -1 ? 1000 : q;
    }

    public static Map<String, String> readParameters(HttpHeaderReader reader) throws ParseException {
        return HttpHeaderReader.readParameters(reader, false);
    }

    public static Map<String, String> readParameters(HttpHeaderReader reader, boolean fileNameFix) throws ParseException {
        LinkedHashMap<String, String> m = null;
        while (reader.hasNext()) {
            String value;
            reader.nextSeparator(';');
            while (reader.hasNextSeparator(';', true)) {
                reader.next();
            }
            if (!reader.hasNext()) break;
            String name = reader.nextToken();
            reader.nextSeparator('=');
            if ("filename".equalsIgnoreCase(name) && fileNameFix) {
                value = reader.nextTokenOrQuotedString(true);
                value = value.substring(value.lastIndexOf(92) + 1);
            } else {
                value = reader.nextTokenOrQuotedString(false);
            }
            if (m == null) {
                m = new LinkedHashMap<String, String>();
            }
            m.put(name.toLowerCase(), value);
        }
        return m;
    }

    public static Map<String, Cookie> readCookies(String header) {
        return CookiesParser.parseCookies(header);
    }

    public static Cookie readCookie(String header) {
        return CookiesParser.parseCookie(header);
    }

    public static NewCookie readNewCookie(String header) {
        return CookiesParser.parseNewCookie(header);
    }

    public static Set<MatchingEntityTag> readMatchingEntityTag(String header) throws ParseException {
        if (header.equals("*")) {
            return MatchingEntityTag.ANY_MATCH;
        }
        HttpHeaderReaderImpl reader = new HttpHeaderReaderImpl(header);
        HashSet<MatchingEntityTag> l = new HashSet<MatchingEntityTag>(1);
        HttpHeaderListAdapter adapter = new HttpHeaderListAdapter(reader);
        while (((HttpHeaderReader)reader).hasNext()) {
            l.add(MATCHING_ENTITY_TAG_CREATOR.create(adapter));
            adapter.reset();
            if (!((HttpHeaderReader)reader).hasNext()) continue;
            ((HttpHeaderReader)reader).next();
        }
        return l;
    }

    public static List<MediaType> readMediaTypes(List<MediaType> l, String header) throws ParseException {
        return HttpHeaderReader.readList(l, MEDIA_TYPE_CREATOR, header);
    }

    public static List<AcceptableMediaType> readAcceptMediaType(String header) throws ParseException {
        return HttpHeaderReader.readAcceptableList(ACCEPTABLE_MEDIA_TYPE_COMPARATOR, ACCEPTABLE_MEDIA_TYPE_CREATOR, header);
    }

    public static List<QualitySourceMediaType> readQualitySourceMediaType(String header) throws ParseException {
        return HttpHeaderReader.readAcceptableList(MediaTypes.QUALITY_SOURCE_MEDIA_TYPE_COMPARATOR, QUALITY_SOURCE_MEDIA_TYPE_CREATOR, header);
    }

    public static List<QualitySourceMediaType> readQualitySourceMediaType(String[] header) throws ParseException {
        if (header.length < 2) {
            return HttpHeaderReader.readQualitySourceMediaType(header[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (String h : header) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(h);
        }
        return HttpHeaderReader.readQualitySourceMediaType(sb.toString());
    }

    public static List<AcceptableMediaType> readAcceptMediaType(String header, final List<QualitySourceMediaType> priorityMediaTypes) throws ParseException {
        return HttpHeaderReader.readAcceptableList(new Comparator<AcceptableMediaType>(){

            @Override
            public int compare(AcceptableMediaType o1, AcceptableMediaType o2) {
                boolean q_o1_set = false;
                int q_o1 = 1000000;
                boolean q_o2_set = false;
                int q_o2 = 1000000;
                for (QualitySourceMediaType m : priorityMediaTypes) {
                    if (!q_o1_set && MediaTypes.typeEquals(o1, m)) {
                        q_o1 = o1.getQuality() * m.getQualitySource();
                        q_o1_set = true;
                        continue;
                    }
                    if (q_o2_set || !MediaTypes.typeEquals(o2, m)) continue;
                    q_o2 = o2.getQuality() * m.getQualitySource();
                    q_o2_set = true;
                }
                int i = q_o2 - q_o1;
                if (i != 0) {
                    return i;
                }
                i = o2.getQuality() - o1.getQuality();
                if (i != 0) {
                    return i;
                }
                return MediaTypes.MEDIA_TYPE_COMPARATOR.compare(o1, o2);
            }
        }, ACCEPTABLE_MEDIA_TYPE_CREATOR, header);
    }

    public static List<AcceptableToken> readAcceptToken(String header) throws ParseException {
        return HttpHeaderReader.readAcceptableList(ACCEPTABLE_TOKEN_CREATOR, header);
    }

    public static List<AcceptableLanguageTag> readAcceptLanguage(String header) throws ParseException {
        return HttpHeaderReader.readAcceptableList(LANGUAGE_CREATOR, header);
    }

    public static <T extends QualityFactor> List<T> readAcceptableList(ListElementCreator<T> c, String header) throws ParseException {
        List<T> l = HttpHeaderReader.readList(c, header);
        Collections.sort(l, QUALITY_COMPARATOR);
        return l;
    }

    public static <T> List<T> readAcceptableList(Comparator<T> comparator, ListElementCreator<T> c, String header) throws ParseException {
        List<T> l = HttpHeaderReader.readList(c, header);
        Collections.sort(l, comparator);
        return l;
    }

    public static <T> List<T> readList(ListElementCreator<T> c, String header) throws ParseException {
        return HttpHeaderReader.readList(new ArrayList(), c, header);
    }

    public static <T> List<T> readList(List<T> l, ListElementCreator<T> c, String header) throws ParseException {
        HttpHeaderReaderImpl reader = new HttpHeaderReaderImpl(header);
        HttpHeaderListAdapter adapter = new HttpHeaderListAdapter(reader);
        while (((HttpHeaderReader)reader).hasNext()) {
            l.add(c.create(adapter));
            adapter.reset();
            if (!((HttpHeaderReader)reader).hasNext()) continue;
            ((HttpHeaderReader)reader).next();
        }
        return l;
    }

    public static interface ListElementCreator<T> {
        public T create(HttpHeaderReader var1) throws ParseException;
    }

    public static enum Event {
        Token,
        QuotedString,
        Comment,
        Separator,
        Control;

    }
}

