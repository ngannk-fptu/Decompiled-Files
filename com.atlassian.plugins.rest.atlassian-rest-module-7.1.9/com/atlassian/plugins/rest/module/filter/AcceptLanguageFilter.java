/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.filter;

import com.atlassian.plugins.rest.module.filter.HttpHeaderListAdapter;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.core.header.LanguageTag;
import com.sun.jersey.core.header.QualityFactor;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.spi.container.AdaptingContainerRequest;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AcceptLanguageFilter
implements ContainerRequestFilter {
    private static final Comparator<QualityFactor> QUALITY_COMPARATOR = Comparator.comparingInt(QualityFactor::getQuality).reversed();
    private static final CustomLanguageTag ANY_LANG = new CustomLanguageTag("*", null);

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        return new AdaptingContainerRequest(request){
            private List<Locale> acceptLanguages;

            @Override
            public void setHeaders(InBoundHeaders headers) {
                super.setHeaders(headers);
                this.acceptLanguages = null;
            }

            @Override
            public List<Locale> getAcceptableLanguages() {
                if (this.acceptLanguages == null) {
                    this.acceptLanguages = this.parseAcceptLanguage().stream().map(CustomLanguageTag::getAsLocale).collect(Collectors.toList());
                }
                return this.acceptLanguages;
            }

            private List<CustomLanguageTag> parseAcceptLanguage() {
                String acceptLanguage = this.getHeaderValue("Accept-Language");
                if (acceptLanguage == null || acceptLanguage.length() == 0) {
                    return Collections.singletonList(ANY_LANG);
                }
                try {
                    ArrayList<CustomLanguageTag> result = new ArrayList<CustomLanguageTag>();
                    HttpHeaderReader reader = HttpHeaderReader.newInstance(acceptLanguage);
                    HttpHeaderListAdapter adapter = new HttpHeaderListAdapter(reader);
                    while (reader.hasNext()) {
                        result.add(this.parserLanguageTag(adapter));
                        adapter.reset();
                        if (!reader.hasNext()) continue;
                        reader.next();
                    }
                    Collections.sort(result, QUALITY_COMPARATOR);
                    return result;
                }
                catch (ParseException e) {
                    throw new WebApplicationException((Throwable)e, Response.status(Response.Status.BAD_REQUEST).entity("Bad Accept-Language header value: '" + acceptLanguage + "'").type("text/plain").build());
                }
            }

            private CustomLanguageTag parserLanguageTag(HttpHeaderReader reader) throws ParseException {
                reader.hasNext();
                String primaryTag = null;
                String subTags = null;
                String languageTag = reader.nextToken();
                if (!languageTag.equals("*")) {
                    if (!this.isLanguageTagValid(languageTag)) {
                        throw new ParseException("String, " + languageTag + ", is not a valid language tag", 0);
                    }
                    int index = languageTag.indexOf(45);
                    if (index == -1) {
                        primaryTag = languageTag;
                        subTags = null;
                    } else {
                        primaryTag = languageTag.substring(0, index);
                        subTags = languageTag.substring(index + 1);
                    }
                } else {
                    primaryTag = languageTag;
                }
                int quality = reader.hasNext() ? HttpHeaderReader.readQualityFactorParameter(reader) : 1000;
                return new CustomLanguageTag(languageTag, primaryTag, subTags, quality);
            }

            private boolean isLanguageTagValid(String tag) {
                int alphaCount = 0;
                int parts = 0;
                for (int i = 0; i < tag.length(); ++i) {
                    char c = tag.charAt(i);
                    if (c == '-') {
                        if (alphaCount == 0) {
                            return false;
                        }
                        alphaCount = 0;
                        ++parts;
                        continue;
                    }
                    if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || Character.isDigit(c) && (parts > 0 || alphaCount > 0)) {
                        if (++alphaCount <= 8) continue;
                        return false;
                    }
                    return false;
                }
                return alphaCount != 0;
            }
        };
    }

    private static final class CustomLanguageTag
    implements QualityFactor {
        protected int quality = 1000;
        protected String tag;
        protected String primaryTag;
        protected String subTags;

        public CustomLanguageTag(String primaryTag, String subTags) {
            this(subTags != null && subTags.length() > 0 ? primaryTag + "-" + subTags : primaryTag, primaryTag, subTags, 1000);
        }

        public CustomLanguageTag(String tag, String primaryTag, String subTags, int quality) {
            this.tag = tag;
            this.primaryTag = primaryTag;
            this.subTags = subTags;
            this.quality = quality;
        }

        public final Locale getAsLocale() {
            return this.subTags == null ? new Locale(this.primaryTag) : new Locale(this.primaryTag, this.subTags);
        }

        @Override
        public int getQuality() {
            return this.quality;
        }

        public boolean equals(Object object) {
            if (object instanceof LanguageTag) {
                LanguageTag lt = (LanguageTag)object;
                return Objects.equals(this.tag, lt.getTag()) && Objects.equals(this.primaryTag, lt.getPrimaryTag()) && Objects.equals(this.subTags, lt.getSubTags());
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.tag, this.primaryTag, this.subTags);
        }

        public String toString() {
            return this.primaryTag + (this.subTags == null ? "" : this.subTags);
        }
    }
}

