/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.NotAuthorizedException
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.hc.core5.http.HeaderElement
 *  org.apache.hc.core5.http.NameValuePair
 *  org.apache.hc.core5.http.message.BasicHeaderValueParser
 *  org.apache.hc.core5.http.message.ParserCursor
 *  org.apache.http.entity.ContentType
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.sharelinks.metaextractor;

import com.atlassian.confluence.plugins.sharelinks.DOMMetadataExtractor;
import com.atlassian.confluence.plugins.sharelinks.LinkMetaData;
import com.atlassian.confluence.plugins.sharelinks.LinkMetaDataExtractor;
import com.atlassian.confluence.plugins.sharelinks.metaextractor.OpenGraphDOMMetadataExtractor;
import com.atlassian.confluence.plugins.sharelinks.metaextractor.SimpleDOMMetadataExtractor;
import com.atlassian.confluence.plugins.sharelinks.metaextractor.TwitterDOMMetadataExtractor;
import com.atlassian.plugins.whitelist.NotAuthorizedException;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicHeaderValueParser;
import org.apache.hc.core5.http.message.ParserCursor;
import org.apache.http.entity.ContentType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MasterLinkMetaDataExtractor
implements LinkMetaDataExtractor {
    public static final String CSSQUERY_CONTENT_TYPE = "meta[http-equiv=Content-Type][content]";
    public static final String CONTENT = "content";
    public static final String CHARSET = "charset";
    private static final Pattern UNTIL_END_HEAD_OR_EOF_PATTERN = Pattern.compile(".*?</head>|.*", 34);
    private static final int MAX_HEAD_SIZE = 131072;
    private static final int DESCRIPTION_MAX_LENGTH = 180;
    private static final int DOMAIN_MAX_LENGTH = 50;
    private static final int EXCERPT_URL_MAX_LENGTH = 30;
    private final OutboundWhitelist outboundWhitelist;
    private static final Logger log = LoggerFactory.getLogger(MasterLinkMetaDataExtractor.class);
    private final List<DOMMetadataExtractor> metadataExtractors;
    private final RequestFactory<?> requestFactory;

    public MasterLinkMetaDataExtractor(RequestFactory<?> requestFactory, OutboundWhitelist outboundWhitelist) {
        this.requestFactory = requestFactory;
        this.outboundWhitelist = outboundWhitelist;
        this.metadataExtractors = ImmutableList.of((Object)new OpenGraphDOMMetadataExtractor(), (Object)new TwitterDOMMetadataExtractor(), (Object)new SimpleDOMMetadataExtractor(requestFactory));
    }

    @Override
    public LinkMetaData parseMetaData(String url, boolean isPreview) throws URISyntaxException, NotAuthorizedException {
        if (!((String)url).startsWith("http://") && !((String)url).startsWith("https://")) {
            url = "http://" + (String)url;
        }
        LinkMetaData meta = new LinkMetaData((String)url);
        meta.setExcerptedURL(this.getExcerptedUrl((String)url));
        URI uri = new URI((String)url);
        Object domain = StringUtils.isBlank((CharSequence)uri.getHost()) ? url : uri.getHost();
        meta.setDomain(MasterLinkMetaDataExtractor.getPreviewText((String)domain, 50));
        String htmlData = this.getHeadHtmlData((String)url, meta);
        Document jsoupDoc = Jsoup.parse((String)htmlData);
        if (!htmlData.isEmpty()) {
            for (DOMMetadataExtractor metadataExtractor : this.metadataExtractors) {
                metadataExtractor.updateMetadata(meta, jsoupDoc);
            }
        }
        if (isPreview) {
            meta.setDescription(MasterLinkMetaDataExtractor.getPreviewText(meta.getDescription(), 180));
        }
        return meta;
    }

    private @NonNull String getHeadHtmlData(String url, LinkMetaData meta) throws NotAuthorizedException {
        if (!this.outboundWhitelist.isAllowed(URI.create(url))) {
            log.error("Not authorized to access this url. Please contact admin to add this url to whitelist.");
            throw new NotAuthorizedException(url);
        }
        Request request = this.requestFactory.createRequest(Request.MethodType.GET, url);
        request.setHeader("accept-charset", "utf-8");
        try {
            return ((Optional)request.executeAndReturn(response -> this.processResponse(url, meta, response))).orElse("{}");
        }
        catch (ResponseException ex) {
            log.error("Failed to make request", (Throwable)ex);
            return "{}";
        }
    }

    private Optional<String> processResponse(String url, LinkMetaData meta, Response response) throws ResponseException {
        meta.setResponseHost(URI.create(url));
        if (this.isValidResponse(response)) {
            Optional<String> optional;
            block10: {
                InputStream inputStream = response.getResponseBodyAsStream();
                try {
                    Charset charset = MasterLinkMetaDataExtractor.getContentType(response).getCharset();
                    if (charset == null) {
                        inputStream.mark(Integer.MAX_VALUE);
                        charset = MasterLinkMetaDataExtractor.detectCharset(inputStream);
                        inputStream.reset();
                    }
                    meta.setCharset(charset == null ? StandardCharsets.UTF_8.name() : charset.name());
                    optional = MasterLinkMetaDataExtractor.extractHtmlHeaderContent(inputStream, meta.getCharset());
                    if (inputStream == null) break block10;
                }
                catch (Throwable throwable) {
                    try {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException ex) {
                        throw new ResponseException((Throwable)ex);
                    }
                }
                inputStream.close();
            }
            return optional;
        }
        return Optional.empty();
    }

    private static @NonNull ContentType getContentType(Response response) {
        return ContentType.parse((String)response.getHeader("Content-Type"));
    }

    private static @Nullable Charset detectCharset(InputStream inputStream) {
        HeaderElement[] contents;
        Scanner bodyScanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
        String attempt = bodyScanner.findWithinHorizon(UNTIL_END_HEAD_OR_EOF_PATTERN, 131072);
        Document jsoupDoc = Jsoup.parse((String)attempt);
        String contentType = jsoupDoc.select(CSSQUERY_CONTENT_TYPE).attr(CONTENT);
        BasicHeaderValueParser parser = new BasicHeaderValueParser();
        ParserCursor cursor = new ParserCursor(0, contentType.length());
        for (HeaderElement headerElement : contents = parser.parseElements((CharSequence)contentType, cursor)) {
            NameValuePair charsetParam = headerElement.getParameterByName(CHARSET);
            if (charsetParam == null) continue;
            return Charset.forName(charsetParam.getValue());
        }
        return null;
    }

    private static @NonNull Optional<String> extractHtmlHeaderContent(InputStream inputStream, String charset) {
        Scanner responseScanner = new Scanner(inputStream, charset);
        return Optional.ofNullable(responseScanner.findWithinHorizon(UNTIL_END_HEAD_OR_EOF_PATTERN, 131072));
    }

    private static String getPreviewText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        int lastSpaceIndex = (text = text.substring(0, maxLength)).lastIndexOf(32);
        if (lastSpaceIndex != -1) {
            text = text.substring(0, lastSpaceIndex);
        }
        return text + "\u2026";
    }

    private String getExcerptedUrl(String sourceUrl) {
        Object excerptedUrl = sourceUrl;
        String split = "//";
        int splitIndex = ((String)excerptedUrl).indexOf(split);
        if (((String)(excerptedUrl = ((String)excerptedUrl).substring(splitIndex + split.length()))).length() > 30) {
            excerptedUrl = ((String)excerptedUrl).substring(0, 29);
            excerptedUrl = (String)excerptedUrl + "\u2026";
        }
        return excerptedUrl;
    }

    private boolean isValidResponse(Response response) {
        int statusCode = response.getStatusCode();
        String mimeType = MasterLinkMetaDataExtractor.getContentType(response).getMimeType();
        return statusCode >= 200 && statusCode < 300 && (mimeType == null || mimeType.startsWith("text/"));
    }
}

