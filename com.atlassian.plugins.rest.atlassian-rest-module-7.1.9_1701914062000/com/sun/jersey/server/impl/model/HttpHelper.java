/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model;

import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.header.AcceptableLanguageTag;
import com.sun.jersey.core.header.AcceptableMediaType;
import com.sun.jersey.core.header.AcceptableToken;
import com.sun.jersey.core.header.LanguageTag;
import com.sun.jersey.core.header.MatchingEntityTag;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.impl.ImplMessages;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public final class HttpHelper {
    private static final Logger LOGGER = Logger.getLogger(HttpHelper.class.getName());

    public static MediaType getContentType(HttpRequestContext request) {
        return HttpHelper.getContentType(request.getRequestHeaders().getFirst("Content-Type"));
    }

    public static MediaType getContentType(String contentTypeString) {
        try {
            return contentTypeString != null ? MediaType.valueOf(contentTypeString) : null;
        }
        catch (IllegalArgumentException e) {
            throw HttpHelper.clientError("Bad Content-Type header value: '" + contentTypeString + "'", e);
        }
    }

    public static MediaType getContentType(Object contentType) {
        if (contentType == null) {
            return null;
        }
        if (contentType instanceof MediaType) {
            return (MediaType)contentType;
        }
        return MediaType.valueOf(contentType.toString());
    }

    public static Locale getContentLanguageAsLocale(HttpRequestContext request) {
        return HttpHelper.getLanguageTagAsLocale(request.getRequestHeaders().getFirst("Content-Language"));
    }

    public static Locale getLanguageTagAsLocale(String language) {
        if (language == null) {
            return null;
        }
        try {
            return new LanguageTag(language).getAsLocale();
        }
        catch (ParseException e) {
            throw HttpHelper.clientError("Bad Content-Language header value: '" + language + "'", e);
        }
    }

    public static Set<MatchingEntityTag> getIfMatch(HttpRequestContext request) {
        String ifMatch = request.getHeaderValue("If-Match");
        if (ifMatch == null || ifMatch.length() == 0) {
            return null;
        }
        try {
            return HttpHeaderReader.readMatchingEntityTag(ifMatch);
        }
        catch (ParseException e) {
            throw HttpHelper.clientError("Bad If-Match header value: '" + ifMatch + "'", e);
        }
    }

    public static Set<MatchingEntityTag> getIfNoneMatch(HttpRequestContext request) {
        String ifNoneMatch = request.getHeaderValue("If-None-Match");
        if (ifNoneMatch == null || ifNoneMatch.length() == 0) {
            return null;
        }
        try {
            return HttpHeaderReader.readMatchingEntityTag(ifNoneMatch);
        }
        catch (ParseException e) {
            throw HttpHelper.clientError("Bad If-None-Match header value: '" + ifNoneMatch + "'", e);
        }
    }

    public static List<AcceptableMediaType> getAccept(HttpRequestContext request) {
        String accept = request.getHeaderValue("Accept");
        if (accept == null || accept.length() == 0) {
            return MediaTypes.GENERAL_ACCEPT_MEDIA_TYPE_LIST;
        }
        try {
            return HttpHeaderReader.readAcceptMediaType(accept);
        }
        catch (ParseException e) {
            throw HttpHelper.clientError(ImplMessages.BAD_ACCEPT_FIELD(accept), e);
        }
    }

    public static List<AcceptableMediaType> getAccept(HttpRequestContext request, List<QualitySourceMediaType> priorityMediaTypes) {
        String accept = request.getHeaderValue("Accept");
        if (accept == null || accept.length() == 0) {
            return MediaTypes.GENERAL_ACCEPT_MEDIA_TYPE_LIST;
        }
        try {
            return HttpHeaderReader.readAcceptMediaType(accept, priorityMediaTypes);
        }
        catch (ParseException e) {
            throw HttpHelper.clientError(ImplMessages.BAD_ACCEPT_FIELD(accept), e);
        }
    }

    @Deprecated
    public static List<AcceptableLanguageTag> getAcceptLangauge(HttpRequestContext request) {
        return HttpHelper.getAcceptLanguage(request);
    }

    public static List<AcceptableLanguageTag> getAcceptLanguage(HttpRequestContext request) {
        String acceptLanguage = request.getHeaderValue("Accept-Language");
        if (acceptLanguage == null || acceptLanguage.length() == 0) {
            return Collections.singletonList(new AcceptableLanguageTag("*", null));
        }
        try {
            return HttpHeaderReader.readAcceptLanguage(acceptLanguage);
        }
        catch (ParseException e) {
            throw HttpHelper.clientError("Bad Accept-Language header value: '" + acceptLanguage + "'", e);
        }
    }

    public static List<AcceptableToken> getAcceptCharset(HttpRequestContext request) {
        String acceptCharset = request.getHeaderValue("Accept-Charset");
        try {
            if (acceptCharset == null || acceptCharset.length() == 0) {
                return Collections.singletonList(new AcceptableToken("*"));
            }
            return HttpHeaderReader.readAcceptToken(acceptCharset);
        }
        catch (ParseException e) {
            throw HttpHelper.clientError("Bad Accept-Charset header value: '" + acceptCharset + "'", e);
        }
    }

    public static List<AcceptableToken> getAcceptEncoding(HttpRequestContext request) {
        String acceptEncoding = request.getHeaderValue("Accept-Encoding");
        try {
            if (acceptEncoding == null || acceptEncoding.length() == 0) {
                return Collections.singletonList(new AcceptableToken("*"));
            }
            return HttpHeaderReader.readAcceptToken(acceptEncoding);
        }
        catch (ParseException e) {
            throw HttpHelper.clientError("Bad Accept-Encoding header value: '" + acceptEncoding + "'", e);
        }
    }

    private static WebApplicationException clientError(String message, Exception e) {
        LOGGER.log(Level.FINEST, "Bad request: " + message, e);
        return new WebApplicationException((Throwable)e, Response.status(Response.Status.BAD_REQUEST).type("text/plain").build());
    }

    public static boolean produces(MediaType contentType, List<MediaType> accept) {
        for (MediaType a : accept) {
            if (a.getType().equals("*")) {
                return true;
            }
            if (!contentType.isCompatible(a)) continue;
            return true;
        }
        return false;
    }
}

