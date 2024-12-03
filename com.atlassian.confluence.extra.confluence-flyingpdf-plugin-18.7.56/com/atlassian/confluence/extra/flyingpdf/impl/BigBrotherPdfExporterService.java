/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentBodyConversionService
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentBodyConversionService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.PdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.analytic.EnvironmentInfo;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportResults;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportScope;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportStatus;
import com.atlassian.confluence.extra.flyingpdf.analytic.FailureLocation;
import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.analytic.PdfExportAnalyticEvent;
import com.atlassian.confluence.extra.flyingpdf.analytic.SandboxStatus;
import com.atlassian.confluence.extra.flyingpdf.analytic.SpaceExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.config.OutboundConnectionValidator;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.impl.DelegatingPdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfExportEvent;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PdfExporterService.class})
public class BigBrotherPdfExporterService
implements PdfExporterService {
    private final EventPublisher eventPublisher;
    private final ClusterManager clusterManager;
    private final DelegatingPdfExporterService delegate;
    private final OutboundConnectionValidator outboundConnectionValidator;
    private final ContentBodyConversionService pageBodyConversionService;
    private static final List<String> HTML_TAGS_WITH_SOURCE_ATTRIBUTE = Arrays.asList("audio", "embed", "iframe", "img", "input", "script", "track", "video");
    private static final String HTML_SOURCE_ATTRIBUTE_STRING = "src";
    private static final String HTML_ANCHOR_TAG_STRING = "a";
    private static final String HTML_HYPERTEXT_REFERENCE_ATTRIBUTE_STRING = "href";
    private static final String BLOCKED_URL_ANCHOR_TAG_TEXT = "blocked URL";

    public BigBrotherPdfExporterService(@ComponentImport EventPublisher eventPublisher, @ComponentImport ClusterManager clusterManager, DelegatingPdfExporterService delegate, @ComponentImport ContentBodyConversionService pageBodyConversionService, OutboundConnectionValidator outboundConnectionValidator) {
        this.eventPublisher = eventPublisher;
        this.clusterManager = clusterManager;
        this.delegate = delegate;
        this.pageBodyConversionService = pageBodyConversionService;
        this.outboundConnectionValidator = outboundConnectionValidator;
    }

    @Override
    public File createPdfForSpace(User user, Space space, ContentTree contentTree, PdfExportProgressMonitor progress, String contextPath, SpaceExportMetrics spaceExportMetrics, DecorationPolicy decorations) throws ImportExportException {
        spaceExportMetrics.setConfluencePages(contentTree.size());
        this.fillEnvironmentData(space, spaceExportMetrics.getEnvironmentInfo());
        long startTime = System.currentTimeMillis();
        try {
            File result = this.delegate.createPdfForSpace(user, space, contentTree, progress, contextPath, spaceExportMetrics, decorations);
            spaceExportMetrics.getExportResults().setExportStatus(ExportStatus.OK);
            File file = result;
            return file;
        }
        catch (Exception e) {
            this.setGenericError(spaceExportMetrics.getExportResults());
            throw e;
        }
        finally {
            spaceExportMetrics.setTotalTime((int)(System.currentTimeMillis() - startTime));
            this.eventPublisher.publish((Object)new PdfExportEvent(space));
            this.eventPublisher.publish((Object)new PdfExportAnalyticEvent(spaceExportMetrics));
        }
    }

    @Override
    public File createPdfForPage(User user, AbstractPage page, String contextPath, PageExportMetrics pageExportMetrics) throws ImportExportException {
        try {
            AbstractPage clonedPage = (AbstractPage)page.clone();
            BodyValidationResult validationResult = this.checkSSRFAgainstElementSrcAndReplaceWithAnchorTag(this.transformPageBody(clonedPage.getId(), clonedPage.getBodyAsString(), ContentRepresentation.STORAGE, ContentRepresentation.EDITOR));
            if (validationResult.isBodyUpdated()) {
                clonedPage.setBodyAsString(this.transformPageBody(clonedPage.getId(), validationResult.getUpdatedBody(), ContentRepresentation.EDITOR, ContentRepresentation.STORAGE));
            }
            pageExportMetrics.setPageId(clonedPage.getId());
            pageExportMetrics.setPageRevision(clonedPage.getConfluenceRevision().hashCode());
            this.fillEnvironmentData(clonedPage, pageExportMetrics.getEnvironmentInfo());
            File file = this.delegate.createPdfForPage(user, clonedPage, contextPath, pageExportMetrics);
            return file;
        }
        catch (Exception e) {
            pageExportMetrics.getExportResults().setFailureLocation(FailureLocation.PAGE);
            this.setGenericError(pageExportMetrics.getExportResults());
            throw e;
        }
        finally {
            this.eventPublisher.publish((Object)new PdfExportEvent(page));
            this.eventPublisher.publish((Object)new PdfExportAnalyticEvent(pageExportMetrics));
        }
    }

    private void setGenericError(ExportResults results) {
        if (results.getExportStatus() == null) {
            results.setExportStatus(ExportStatus.FAIL);
            results.setFailureLocation(FailureLocation.INTERNAL);
        }
    }

    private void fillEnvironmentData(Space space, EnvironmentInfo environmentInfo) {
        if (this.clusterManager.isClustered()) {
            environmentInfo.setDcNodeId(Optional.ofNullable(this.clusterManager.getThisNodeInformation()).map(ClusterNodeInformation::getAnonymizedNodeIdentifier).map(Object::hashCode).orElse(-1));
        }
        environmentInfo.setSpaceKey(space.getKey());
        environmentInfo.setSpaceName(space.getName());
        environmentInfo.setExportScope(ExportScope.SPACE);
        environmentInfo.setSandboxStatus(this.delegate.sandboxIsUsed() ? SandboxStatus.USED : SandboxStatus.NOT_USED);
    }

    private void fillEnvironmentData(AbstractPage page, EnvironmentInfo environmentInfo) {
        Space space = page.getSpace();
        environmentInfo.setPageName(page.getTitle());
        environmentInfo.setPageType(page.getType());
        environmentInfo.setSpaceName(space.getName());
        environmentInfo.setSpaceKey(space.getKey());
        environmentInfo.setExportScope(ExportScope.PAGE);
    }

    @Override
    public ContentTree getContentTree(User user, Space space) {
        return this.delegate.getContentTree(user, space);
    }

    @Override
    public boolean isPermitted(User user, AbstractPage page) {
        return this.delegate.isPermitted(user, page);
    }

    @Override
    public boolean isPermitted(User user, Space space) {
        return this.delegate.isPermitted(user, space);
    }

    @Override
    public boolean exportableContentExists(Space space) {
        return this.delegate.exportableContentExists(space);
    }

    @Override
    public PdfExportProgressMonitor createProgressMonitor(ProgressMeter progressMeter) {
        return this.delegate.createProgressMonitor(progressMeter);
    }

    private BodyValidationResult checkSSRFAgainstElementSrcAndReplaceWithAnchorTag(String html) {
        AtomicBoolean bodyChanged = new AtomicBoolean(false);
        Document pageHtml = Jsoup.parseBodyFragment(html);
        HTML_TAGS_WITH_SOURCE_ATTRIBUTE.forEach(tag -> {
            Elements elements = pageHtml.getElementsByTag((String)tag);
            if (elements.isEmpty()) {
                return;
            }
            elements.stream().filter(element -> element.hasAttr(HTML_SOURCE_ATTRIBUTE_STRING)).forEach(element -> {
                String srcURLString = element.attr(HTML_SOURCE_ATTRIBUTE_STRING).trim();
                if (!this.isOutboundValidationRequired(srcURLString)) {
                    return;
                }
                OutboundConnectionValidator.ValidateResult result = this.outboundConnectionValidator.validate(srcURLString);
                if (!result.isValid()) {
                    bodyChanged.set(true);
                    Element anchorElement = new Element(HTML_ANCHOR_TAG_STRING);
                    anchorElement.attr(HTML_HYPERTEXT_REFERENCE_ATTRIBUTE_STRING, element.attr(HTML_SOURCE_ATTRIBUTE_STRING));
                    anchorElement.text(BLOCKED_URL_ANCHOR_TAG_TEXT);
                    element.replaceWith(anchorElement);
                }
            });
        });
        return new BodyValidationResult(bodyChanged.get(), pageHtml.body().html());
    }

    private String transformPageBody(long pageID, String body, ContentRepresentation bodyFormat, ContentRepresentation toFormat) {
        Content pageContent = Content.builder().id(ContentId.of((long)pageID)).body(body, bodyFormat).build();
        Optional bodyToConvert = this.pageBodyConversionService.selectBodyForRepresentation(pageContent, toFormat);
        return bodyToConvert.map(b -> this.pageBodyConversionService.convert(b, toFormat).getValue()).orElse(body);
    }

    private boolean isOutboundValidationRequired(String srcURLString) {
        try {
            URI srcURI = URI.create(srcURLString);
            if (srcURI.getScheme() == null || srcURI.getHost() == null) {
                return false;
            }
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private static class BodyValidationResult {
        private final String body;
        private final boolean isUpdated;

        private BodyValidationResult(boolean isBodyUpdated, String body) {
            this.isUpdated = isBodyUpdated;
            this.body = body;
        }

        private boolean isBodyUpdated() {
            return this.isUpdated;
        }

        private String getUpdatedBody() {
            return this.body;
        }
    }
}

