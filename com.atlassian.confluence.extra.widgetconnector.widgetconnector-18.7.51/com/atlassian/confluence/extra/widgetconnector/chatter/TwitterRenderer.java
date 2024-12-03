/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.Supplier
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.IOUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.widgetconnector.chatter;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.widgetconnector.AbstractWidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.WidgetRenderer;
import com.atlassian.confluence.extra.widgetconnector.services.VelocityRenderService;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WidgetRenderer.class})
public class TwitterRenderer
extends AbstractWidgetRenderer {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterRenderer.class);
    private static final String CACHE_NAME = TwitterRenderer.class.getName();
    private static final long SINGLE_TWEET_RETRIEVAL_FAILURE_DELAY = Long.getLong("com.atlassian.confluence.extra.widgetconnector.chatter.TwitterRenderer.singletweet.failure.delay", 60000L);
    private static final Pattern SINGLE_TWEET_PATTERN = Pattern.compile("^(https?)://twitter\\.com/(.*?)/status(es)??/(\\d+)+/?$");
    private static final String SINGLE_TWEET_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/tweet.vm";
    private static final String GENERIC_TEMPLATE = "com/atlassian/confluence/extra/widgetconnector/templates/twitter-generic.vm";
    private static final String GENERIC_DEFAULT_WIDTH = "400";
    private static final String GENERIC_DEFAULT_HEIGHT = "";
    private static final String SERVICE_NAME = "Twitter";
    private static final CacheSettings CACHE_SETTINGS = new CacheSettingsBuilder().local().build();
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final PageBuilderService pageBuilderService;
    private final VelocityRenderService velocityRenderService;
    private final RequestFactory<?> requestFactory;
    private final CacheFactory cacheFactory;

    @Autowired
    public TwitterRenderer(@ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport CacheManager cacheManager, @ComponentImport PageBuilderService pageBuilderService, @ComponentImport RequestFactory<?> requestFactory, VelocityRenderService velocityRenderService) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.pageBuilderService = pageBuilderService;
        this.velocityRenderService = velocityRenderService;
        this.requestFactory = requestFactory;
        this.cacheFactory = cacheManager;
        this.cache().removeAll();
    }

    public Map<String, String> getParameters(String url, Map<String, String> params) {
        Matcher singleTweetMatcher = SINGLE_TWEET_PATTERN.matcher(url);
        if (singleTweetMatcher.matches()) {
            params.putIfAbsent("width", String.valueOf(-1));
            params.putIfAbsent("height", String.valueOf(-1));
            params.put("_template", SINGLE_TWEET_TEMPLATE);
            params.put("tweetHtml", this.getTweetHtml(singleTweetMatcher.group(1), singleTweetMatcher.group(2), singleTweetMatcher.group(4)));
        } else {
            String maxWidth = params.getOrDefault("width", GENERIC_DEFAULT_WIDTH);
            String maxHeight = params.getOrDefault("height", GENERIC_DEFAULT_HEIGHT);
            params.put("tweetHtml", this.getGenericTweetHtml(url, maxWidth, maxHeight));
            params.put("_template", GENERIC_TEMPLATE);
        }
        return params;
    }

    private String getTweetHtml(String protocol, String twitterUserName, String statusId) {
        TweetRetrievalResult tweetRetrievalResult = this.getTweetRetrievalResultFromCache(statusId, (Supplier<TweetRetrievalResult>)((Supplier)() -> this.retrieve(this.getTweetHtmlUrl(protocol, statusId))));
        if (tweetRetrievalResult.successful) {
            return tweetRetrievalResult.tweetMarkup;
        }
        if (tweetRetrievalResult.isExpiredFailureResult()) {
            return this.getTweetHtml(protocol, twitterUserName, statusId);
        }
        return this.getErrorMessageMarkup(protocol, twitterUserName, statusId);
    }

    private String getGenericTweetHtml(String twitterUrl, String width, String height) {
        TweetRetrievalResult tweetRetrievalResult = this.getTweetRetrievalResultFromCache(twitterUrl, (Supplier<TweetRetrievalResult>)((Supplier)() -> this.retrieve(this.getGenericTweetHtmlUrl(twitterUrl, width, height))));
        if (tweetRetrievalResult.successful) {
            return tweetRetrievalResult.tweetMarkup;
        }
        if (tweetRetrievalResult.isExpiredFailureResult()) {
            return this.getGenericTweetHtml(twitterUrl, width, height);
        }
        return this.getErrorMessageMarkup(twitterUrl);
    }

    private String getErrorMessageMarkup(String protocol, String twitterUserName, String statusId) {
        return RenderUtils.blockError((String)this.getErrorText(protocol + String.format("://twitter.com/%s/status/%s", twitterUserName, statusId), SINGLE_TWEET_RETRIEVAL_FAILURE_DELAY / 60000L), (String)GENERIC_DEFAULT_HEIGHT);
    }

    private String getErrorMessageMarkup(String twitterUrl) {
        return RenderUtils.blockError((String)this.getErrorText(twitterUrl, SINGLE_TWEET_RETRIEVAL_FAILURE_DELAY / 60000L), (String)GENERIC_DEFAULT_HEIGHT);
    }

    private Cache<String, TweetRetrievalResult> cache() {
        return this.cacheFactory.getCache(CACHE_NAME, null, CACHE_SETTINGS);
    }

    @Nonnull
    private TweetRetrievalResult getTweetRetrievalResultFromCache(String statusId, Supplier<TweetRetrievalResult> loader) {
        TweetRetrievalResult result = (TweetRetrievalResult)this.cache().get((Object)statusId, () -> loader.get());
        if (result.isExpiredFailureResult()) {
            this.cache().remove((Object)statusId);
        }
        return result;
    }

    private String getErrorText(Object ... substitutions) {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get())).getText("com.atlassian.confluence.extra.widgetconnector.twitter.single.error", substitutions);
    }

    @VisibleForTesting
    String getTweetHtmlUrl(String protocol, String statusId) {
        return String.format("%s://api.twitter.com/1/statuses/oembed.json?id=%s&omit_script=true&lang=%s", protocol, statusId, HtmlUtil.urlEncode((String)this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()).getLanguage()));
    }

    private String getGenericTweetHtmlUrl(String twitterUrl, String width, String height) {
        return String.format("https://publish.twitter.com/oembed?url=%s&maxwidth=%s%s&lang=%s", HtmlUtil.urlEncode((String)twitterUrl), width, !height.isEmpty() ? "&maxheight=" + height : GENERIC_DEFAULT_HEIGHT, HtmlUtil.urlEncode((String)this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()).getLanguage()));
    }

    private TweetRetrievalResult retrieve(String url) {
        Request request = this.requestFactory.createRequest(Request.MethodType.GET, url);
        try {
            return (TweetRetrievalResult)request.executeAndReturn(this::extract);
        }
        catch (ResponseException retrieveTweetError) {
            LOG.error("Unable to read response from Twitter", (Throwable)retrieveTweetError);
            return this.newFailureResult(retrieveTweetError.getMessage());
        }
    }

    private TweetRetrievalResult extract(Response response) throws ResponseException {
        if (response.isSuccessful()) {
            TweetRetrievalResult tweetRetrievalResult;
            block10: {
                InputStream jsonInput = response.getResponseBodyAsStream();
                try {
                    tweetRetrievalResult = this.newSuccessfulResult(new JSONObject(IOUtils.toString((InputStream)jsonInput, (Charset)StandardCharsets.UTF_8)).getString("html"));
                    if (jsonInput == null) break block10;
                }
                catch (Throwable throwable) {
                    try {
                        if (jsonInput != null) {
                            try {
                                jsonInput.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (JSONException invalidResponse) {
                        LOG.error("Invalid JSON returned by Twitter", (Throwable)invalidResponse);
                        return this.newFailureResult(invalidResponse.getMessage());
                    }
                    catch (IOException retrieveTweetError) {
                        LOG.error("Unable to read response from Twitter", (Throwable)retrieveTweetError);
                        return this.newFailureResult(retrieveTweetError.getMessage());
                    }
                }
                jsonInput.close();
            }
            return tweetRetrievalResult;
        }
        return this.newFailureResult(response.getStatusText());
    }

    private TweetRetrievalResult newFailureResult(String failureMessage) {
        return new TweetRetrievalResult(false, System.currentTimeMillis(), null, failureMessage);
    }

    private TweetRetrievalResult newSuccessfulResult(String tweetMarkup) {
        return new TweetRetrievalResult(true, System.currentTimeMillis(), tweetMarkup, null);
    }

    @Override
    public String getEmbeddedHtml(String url, Map<String, String> params) {
        this.pageBuilderService.assembler().resources().requireWebResource("com.atlassian.confluence.extra.widgetconnector:twitter-webresources");
        return this.velocityRenderService.render(url, this.getParameters(url, params));
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    static class TweetRetrievalResult
    implements Serializable {
        private final boolean successful;
        private final long timestamp;
        private final String tweetMarkup;
        private final String errorMessage;

        TweetRetrievalResult() {
            this(false, 0L, null, null);
        }

        TweetRetrievalResult(boolean successful, long timestamp, String tweetMarkup, String errorMessage) {
            this.successful = successful;
            this.timestamp = timestamp;
            this.tweetMarkup = tweetMarkup;
            this.errorMessage = errorMessage;
        }

        public boolean isExpiredFailureResult() {
            return !this.successful && System.currentTimeMillis() - this.timestamp > SINGLE_TWEET_RETRIEVAL_FAILURE_DELAY;
        }
    }
}

