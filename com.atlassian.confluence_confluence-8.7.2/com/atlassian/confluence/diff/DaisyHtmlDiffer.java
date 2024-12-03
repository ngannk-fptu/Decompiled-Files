/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.google.common.base.Throwables
 *  io.atlassian.util.concurrent.SettableFuture
 *  io.atlassian.util.concurrent.Timeout
 *  org.eclipse.core.runtime.IProgressMonitor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.diff;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.diff.DaisyDelegate;
import com.atlassian.confluence.diff.DiffException;
import com.atlassian.confluence.diff.DiffKey;
import com.atlassian.confluence.diff.DiffPostProcessor;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.diff.HtmlDiffer;
import com.atlassian.confluence.diff.InterruptedDiffException;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.google.common.base.Throwables;
import io.atlassian.util.concurrent.SettableFuture;
import io.atlassian.util.concurrent.Timeout;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaisyHtmlDiffer
implements Differ {
    private static final String TIMEOUT_PROPERTY = "confluence.html.diff.timeout";
    static final int TIMEOUT = Integer.getInteger("confluence.html.diff.timeout", 30000);
    private static final Logger log = LoggerFactory.getLogger(DaisyHtmlDiffer.class);
    private final Transformer transformer;
    private final Cache<DiffKey, SettableFuture<String>> diffCache;
    private final LocaleManager localeManager;
    @VisibleForTesting
    final HtmlDiffer diffDelegate;

    DaisyHtmlDiffer(Transformer transformer, HtmlDiffer diffDelegate, CacheFactory cacheFactory, LocaleManager localeManager) {
        this.transformer = transformer;
        this.localeManager = localeManager;
        this.diffCache = DaisyHtmlDiffer.lookupCache(cacheFactory);
        this.diffDelegate = diffDelegate;
    }

    @Deprecated(forRemoval=true)
    public DaisyHtmlDiffer(Transformer transformer, List<DiffPostProcessor> postProcessors, CacheFactory cacheFactory, LocaleManager localeManager) {
        this(transformer, new DaisyDelegate(postProcessors == null ? Collections.emptyList() : postProcessors), cacheFactory, localeManager);
    }

    private static <K, V> Cache<K, V> lookupCache(CacheFactory cacheFactory) {
        return CoreCache.DIFF_RESULT_BY_KEY.resolve(cacheName -> cacheFactory.getCache(cacheName, null, new CacheSettingsBuilder().local().build()));
    }

    @Override
    public String diff(ContentEntityObject leftContent, ContentEntityObject rightContent) {
        Locale locale = this.localeManager.getLocale(AuthenticatedUserThreadLocal.get());
        Future<String> future = this.createOrGetFutureDiff(leftContent, rightContent, locale);
        try {
            return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch (CancellationException | TimeoutException e) {
            throw new InterruptedDiffException(leftContent, rightContent, TIMEOUT);
        }
        catch (ExecutionException e) {
            Throwables.propagateIfInstanceOf((Throwable)e.getCause(), DiffException.class);
            throw Throwables.propagate((Throwable)e.getCause());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedDiffException(leftContent, rightContent, TIMEOUT);
        }
    }

    private Future<String> createOrGetFutureDiff(ContentEntityObject leftContent, ContentEntityObject rightContent, Locale locale) {
        DiffKey diffKey = new DiffKey(leftContent, rightContent, locale);
        SettableFuture future = (SettableFuture)this.diffCache.get((Object)diffKey);
        if (future == null) {
            future = new SettableFuture();
            if (diffKey.isCacheable()) {
                this.diffCache.put((Object)diffKey, (Object)future);
            }
            try {
                String diff = this.renderContentAndDiff(leftContent, rightContent);
                future.set((Object)diff);
            }
            catch (DiffException e) {
                future.setException((Throwable)e);
            }
            catch (RuntimeException e) {
                future.setException((Throwable)new DiffException(e.getMessage(), e));
            }
        }
        return future;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String renderContentAndDiff(ContentEntityObject leftContent, ContentEntityObject rightContent) {
        String string;
        long startDiff = System.currentTimeMillis();
        try {
            Timeout timeout = Timeout.getNanosTimeout((long)TIMEOUT, (TimeUnit)TimeUnit.MILLISECONDS);
            String leftHtml = this.transformToHtml(leftContent, timeout);
            String rightHtml = this.transformToHtml(rightContent, timeout);
            string = this.diffDelegate.diff(leftHtml, rightHtml, timeout);
        }
        catch (Throwable throwable) {
            log.debug("Diff generation for ({} vs. {}) took {} ms", new Object[]{leftContent, rightContent, System.currentTimeMillis() - startDiff});
            throw throwable;
        }
        log.debug("Diff generation for ({} vs. {}) took {} ms", new Object[]{leftContent, rightContent, System.currentTimeMillis() - startDiff});
        return string;
    }

    private String transformToHtml(ContentEntityObject content, Timeout timeout) throws DiffException {
        if (timeout.isExpired()) {
            throw new InterruptedDiffException("Diff timed out before transformation of " + content, TIMEOUT);
        }
        try {
            PageContext renderContext = content.toPageContext();
            renderContext.setOutputType(ConversionContextOutputType.DIFF.value());
            DefaultConversionContext conversionContext = new DefaultConversionContext(renderContext);
            return this.transformer.transform(new StringReader(content.getBodyAsString()), conversionContext);
        }
        catch (XhtmlException e) {
            throw new DiffException("Error transforming content of " + content + " for diffing", e);
        }
    }

    @Deprecated(forRemoval=true)
    public String diff(String leftHtml, String rightHtml, IProgressMonitor progressMonitor) {
        return this.diff(leftHtml, rightHtml, DaisyHtmlDiffer.asTimeout(progressMonitor));
    }

    String diff(String leftHtml, String rightHtml, Timeout timeout) {
        return this.diffDelegate.diff(leftHtml, rightHtml, timeout);
    }

    @Deprecated(forRemoval=true)
    public String diff(String leftHtml, String rightHtml, IProgressMonitor progressMonitor, boolean useOldAlgorithm) {
        return this.diffDelegate.diff(leftHtml, rightHtml, DaisyHtmlDiffer.asTimeout(progressMonitor));
    }

    private static Timeout asTimeout(IProgressMonitor progressMonitor) {
        return progressMonitor instanceof DaisyDelegate.TimingOutProgressMonitor ? ((DaisyDelegate.TimingOutProgressMonitor)progressMonitor).timeout : Timeout.getNanosTimeout((long)TIMEOUT, (TimeUnit)TimeUnit.MILLISECONDS);
    }
}

