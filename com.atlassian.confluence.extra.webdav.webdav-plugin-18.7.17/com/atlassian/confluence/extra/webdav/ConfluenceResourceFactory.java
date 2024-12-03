/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.extra.flyingpdf.PdfExporterService
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang.ArrayUtils
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.extra.flyingpdf.PdfExporterService;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.WebdavSettingsManager;
import com.atlassian.confluence.extra.webdav.job.ContentJobQueue;
import com.atlassian.confluence.extra.webdav.resource.BlogPostContentResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.BlogPostsDayResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.BlogPostsMonthResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.BlogPostsResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.BlogPostsYearResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.DashboardResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.GeneratedResourceReadMeResource;
import com.atlassian.confluence.extra.webdav.resource.GlobalSpacesResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.NonExistentResource;
import com.atlassian.confluence.extra.webdav.resource.PageAttachmentResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.PageContentResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.PageExportsResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.PagePdfExportContentResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.PageResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.PageUrlResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.PageVersionContentResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.PageVersionsResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.PageWordExportContentResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.PersonalSpacesResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.SpaceAttachmentResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.SpaceContentResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.SpaceResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.WorkspaceResourceImpl;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfluenceResourceFactory
implements DavResourceFactory {
    private static final Pattern VERSION_NUMBER_PATTERN = Pattern.compile("[a-zA-Z] *(\\d+)\\.txt");
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfluenceResourceFactory.class);
    private final String workspaceName;
    private final ContextPathHolder contextPathHolder;
    private final UserAccessor userAccessor;
    private final PdfExporterService pdfExporterService;
    private final SettingsManager settingsManager;
    private final WebdavSettingsManager webdavSettingsManager;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final XhtmlContent xhtmlContent;
    private final AttachmentManager attachmentManager;
    private final SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser;
    private final ContentJobQueue contentJobQueue;

    @Autowired
    public ConfluenceResourceFactory(@ComponentImport ContextPathHolder contextPathHolder, @ComponentImport UserAccessor userAccessor, @ComponentImport PdfExporterService pdfExporterService, @ComponentImport SettingsManager settingsManager, WebdavSettingsManager webdavSettingsManager, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, @ComponentImport XhtmlContent xhtmlContent, @ComponentImport AttachmentManager attachmentManager, @ComponentImport SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser, ContentJobQueue contentJobQueue) {
        this.workspaceName = "default";
        this.contextPathHolder = contextPathHolder;
        this.userAccessor = userAccessor;
        this.pdfExporterService = pdfExporterService;
        this.settingsManager = settingsManager;
        this.webdavSettingsManager = webdavSettingsManager;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.xhtmlContent = xhtmlContent;
        this.attachmentManager = attachmentManager;
        this.attachmentSafeContentHeaderGuesser = attachmentSafeContentHeaderGuesser;
        this.contentJobQueue = contentJobQueue;
    }

    private DavSession getDavSession(HttpServletRequest httpServletRequest) {
        return (DavSession)httpServletRequest.getSession().getAttribute(ConfluenceDavSession.class.getName());
    }

    @Override
    public DavResource createResource(DavResourceLocator davResourceLocator, DavServletRequest davServletRequest, DavServletResponse davServletResponse) throws DavException {
        return this.createResource(davResourceLocator, this.getDavSession(davServletRequest));
    }

    @Override
    public DavResource createResource(DavResourceLocator davResourceLocator, DavSession davSession) throws DavException {
        String resourcePath = davResourceLocator.getResourcePath();
        LOGGER.debug("Trying to locate DavResource: " + resourcePath);
        ConfluenceDavSession confluenceDavSession = (ConfluenceDavSession)davSession;
        LockManager lockManager = confluenceDavSession.getLockManager();
        String[] resourcePathTokens = StringUtils.split((String)resourcePath, (char)'/');
        if (StringUtils.isBlank((String)davResourceLocator.getWorkspacePath())) {
            return new WorkspaceResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.workspaceName);
        }
        if (this.isPathPointingToDashboard(resourcePathTokens)) {
            return new DashboardResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.workspaceName);
        }
        if (this.isPathPointingToEitherGlobalOrPersonalSpaces(resourcePathTokens)) {
            if (StringUtils.equals((String)"Global", (String)resourcePathTokens[1])) {
                return new GlobalSpacesResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.spaceManager);
            }
            if (StringUtils.equals((String)"Personal", (String)resourcePathTokens[1])) {
                return new PersonalSpacesResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.spaceManager);
            }
            return new NonExistentResource(davResourceLocator, this, lockManager, confluenceDavSession);
        }
        if (this.isPathPointingToSpace(resourcePathTokens)) {
            return new SpaceResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.permissionManager, this.spaceManager, this.pageManager, this.attachmentManager, resourcePathTokens[resourcePathTokens.length - 1]);
        }
        if (this.isPathPointingToSpaceDescription(resourcePathTokens)) {
            return new SpaceContentResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.settingsManager, this.spaceManager, resourcePathTokens[2]);
        }
        if (this.isPathPointingSpaceAttachment(resourcePathTokens)) {
            return new SpaceAttachmentResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.spaceManager, this.attachmentSafeContentHeaderGuesser, this.attachmentManager, resourcePathTokens[2], resourcePathTokens[3], confluenceDavSession.getUserAgent());
        }
        if (this.isPathPointingToPage(resourcePathTokens)) {
            return new PageResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.permissionManager, this.spaceManager, this.pageManager, this.attachmentManager, this.contentJobQueue, resourcePathTokens[2], resourcePathTokens[resourcePathTokens.length - 1]);
        }
        if (this.isPathPointingToPageContent(resourcePathTokens)) {
            return new PageContentResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.settingsManager, this.pageManager, resourcePathTokens[2], resourcePathTokens[resourcePathTokens.length - 2]);
        }
        if (this.isPathPointingToPageUrl(resourcePathTokens)) {
            return new PageUrlResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.settingsManager, this.webdavSettingsManager, this.pageManager, resourcePathTokens[2], resourcePathTokens[resourcePathTokens.length - 2]);
        }
        if (this.isPathPointingToPageAttachment(resourcePathTokens)) {
            return new PageAttachmentResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.permissionManager, this.pageManager, this.attachmentSafeContentHeaderGuesser, this.attachmentManager, resourcePathTokens[2], resourcePathTokens[resourcePathTokens.length - 2], resourcePathTokens[resourcePathTokens.length - 1], confluenceDavSession.getUserAgent());
        }
        if (this.isPathPointingToPageVersions(resourcePathTokens)) {
            return new PageVersionsResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.webdavSettingsManager, this.pageManager, resourcePathTokens[2], resourcePathTokens[resourcePathTokens.length - 2]);
        }
        if (this.isPathPointingToPageVersion(resourcePathTokens)) {
            String versionFile = resourcePathTokens[resourcePathTokens.length - 1];
            Matcher versionMatcher = VERSION_NUMBER_PATTERN.matcher(versionFile);
            versionMatcher.find();
            return new PageVersionContentResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.settingsManager, this.pageManager, resourcePathTokens[2], resourcePathTokens[resourcePathTokens.length - 3], Integer.parseInt(versionMatcher.group(1)));
        }
        if (this.isPathPointingToPageVersionsReadme(resourcePathTokens)) {
            return new GeneratedResourceReadMeResource(davResourceLocator, this, lockManager, confluenceDavSession, this.settingsManager);
        }
        if (this.isPathPointingToPageExports(resourcePathTokens)) {
            return new PageExportsResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.webdavSettingsManager, this.pageManager, resourcePathTokens[2], resourcePathTokens[resourcePathTokens.length - 2]);
        }
        if (this.isPathPointingToPageWordExport(resourcePathTokens)) {
            return new PageWordExportContentResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.contextPathHolder, this.settingsManager, this.pageManager, this.xhtmlContent, resourcePathTokens[2], resourcePathTokens[resourcePathTokens.length - 3]);
        }
        if (this.isPathPointingToPagePdfExport(resourcePathTokens)) {
            return new PagePdfExportContentResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.pageManager, this.pdfExporterService, resourcePathTokens[2], resourcePathTokens[resourcePathTokens.length - 3]);
        }
        if (this.isPathPointingToPageExportsReadme(resourcePathTokens)) {
            return new GeneratedResourceReadMeResource(davResourceLocator, this, lockManager, confluenceDavSession, this.settingsManager);
        }
        if (this.isPathPointingToBlog(resourcePathTokens)) {
            return new BlogPostsResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.permissionManager, this.spaceManager, this.pageManager, resourcePathTokens[2]);
        }
        if (this.isPathPointingToBlogYear(resourcePathTokens)) {
            return new BlogPostsYearResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.permissionManager, this.spaceManager, this.pageManager, resourcePathTokens[2], new Integer(resourcePathTokens[4]));
        }
        if (this.isPathPointingToBlogMonth(resourcePathTokens)) {
            return new BlogPostsMonthResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.userAccessor, this.permissionManager, this.spaceManager, this.pageManager, resourcePathTokens[2], new Integer(resourcePathTokens[4]), new Integer(resourcePathTokens[5]));
        }
        if (this.isPathPointingToBlogDay(resourcePathTokens)) {
            return new BlogPostsDayResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.userAccessor, this.permissionManager, this.pageManager, this.spaceManager, resourcePathTokens[2], new Integer(resourcePathTokens[4]), new Integer(resourcePathTokens[5]), new Integer(resourcePathTokens[6]));
        }
        if (this.isPathPointingToBlogPostContent(resourcePathTokens)) {
            return new BlogPostContentResourceImpl(davResourceLocator, this, lockManager, confluenceDavSession, this.settingsManager, this.userAccessor, this.pageManager, resourcePathTokens[2], new Integer(resourcePathTokens[4]), new Integer(resourcePathTokens[5]), new Integer(resourcePathTokens[6]), resourcePathTokens[7].substring(0, resourcePathTokens[7].length() - ".txt".length()));
        }
        return new NonExistentResource(davResourceLocator, this, lockManager, confluenceDavSession);
    }

    private boolean hasAliasInTokens(String[] tokens, int startIndex) {
        for (int i = startIndex; i < tokens.length; ++i) {
            if (tokens[i].indexOf(64) < 0) continue;
            return true;
        }
        return false;
    }

    private boolean areTokensNumeric(String[] tokens, int lowerOffset, int upperOffset) {
        int limit = Math.min(upperOffset, tokens.length);
        for (int i = lowerOffset; i < limit; ++i) {
            String token = tokens[i];
            if (!StringUtils.isBlank((String)token) && StringUtils.isNumeric((String)token)) continue;
            return false;
        }
        return true;
    }

    private boolean isPathPointingToBlogPostContent(String[] resourcePathTokens) {
        if (resourcePathTokens.length == 8 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            return null != space && StringUtils.equals((String)"@news", (String)resourcePathTokens[3]) && this.areTokensNumeric(resourcePathTokens, 4, resourcePathTokens.length - 1) && resourcePathTokens[7].endsWith(".txt") && resourcePathTokens[7].length() > ".txt".length();
        }
        return false;
    }

    private boolean isPathPointingToBlogDay(String[] resourcePathTokens) {
        if (resourcePathTokens.length == 7 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            return null != space && StringUtils.equals((String)"@news", (String)resourcePathTokens[3]) && this.areTokensNumeric(resourcePathTokens, 4, resourcePathTokens.length);
        }
        return false;
    }

    private boolean isPathPointingToBlogMonth(String[] resourcePathTokens) {
        if (resourcePathTokens.length == 6 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            return null != space && StringUtils.equals((String)"@news", (String)resourcePathTokens[3]) && this.areTokensNumeric(resourcePathTokens, 4, resourcePathTokens.length);
        }
        return false;
    }

    private boolean isPathPointingToBlogYear(String[] resourcePathTokens) {
        if (resourcePathTokens.length == 5 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            return null != space && StringUtils.equals((String)"@news", (String)resourcePathTokens[3]) && this.areTokensNumeric(resourcePathTokens, 4, resourcePathTokens.length);
        }
        return false;
    }

    private boolean isPathPointingToBlog(String[] resourcePathTokens) {
        if (resourcePathTokens.length == 4 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            return null != space && StringUtils.equals((String)"@news", (String)resourcePathTokens[3]);
        }
        return false;
    }

    private boolean isPathPointingToPageWordExport(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 6 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 3]);
            return null != space && StringUtils.equals((String)"@exports", (String)resourcePathTokens[resourcePathTokens.length - 2]) && null != page && resourcePathTokens[resourcePathTokens.length - 1].endsWith(".doc");
        }
        return false;
    }

    private boolean isPathPointingToPagePdfExport(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 6 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 3]);
            return null != space && StringUtils.equals((String)"@exports", (String)resourcePathTokens[resourcePathTokens.length - 2]) && null != page && resourcePathTokens[resourcePathTokens.length - 1].endsWith(".pdf");
        }
        return false;
    }

    private boolean isPathPointingToPageExportsReadme(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 6 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 3]);
            return null != space && StringUtils.equals((String)"@exports", (String)resourcePathTokens[resourcePathTokens.length - 2]) && null != page && StringUtils.equals((String)resourcePathTokens[resourcePathTokens.length - 1], (String)"README.txt");
        }
        return false;
    }

    private boolean isPathPointingToPageExports(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 5 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 2]);
            return null != space && StringUtils.equals((String)"@exports", (String)resourcePathTokens[resourcePathTokens.length - 1]) && null != page;
        }
        return false;
    }

    private boolean isPathPointingToPageVersion(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 6 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 3]);
            return null != space && StringUtils.equals((String)"@versions", (String)resourcePathTokens[resourcePathTokens.length - 2]) && null != page && VERSION_NUMBER_PATTERN.matcher(resourcePathTokens[resourcePathTokens.length - 1]).find();
        }
        return false;
    }

    private boolean isPathPointingToPageVersionsReadme(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 6 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 3]);
            return null != space && StringUtils.equals((String)"@versions", (String)resourcePathTokens[resourcePathTokens.length - 2]) && null != page && StringUtils.equals((String)resourcePathTokens[resourcePathTokens.length - 1], (String)"README.txt");
        }
        return false;
    }

    private boolean isPathPointingToPageVersions(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 5 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 2]);
            return null != space && StringUtils.equals((String)"@versions", (String)resourcePathTokens[resourcePathTokens.length - 1]) && null != page;
        }
        return false;
    }

    private boolean isPathPointingToPageAttachment(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 5 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 2]);
            return null != space && !this.hasAliasInTokens(resourcePathTokens, 3) && null != page && null != this.attachmentManager.getAttachment((ContentEntityObject)page, resourcePathTokens[resourcePathTokens.length - 1]);
        }
        return false;
    }

    private boolean isPathPointingToPageContent(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 5 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 2]);
            return null != space && !this.hasAliasInTokens(resourcePathTokens, 3) && null != page && StringUtils.equals((String)(page.getTitle() + ".txt"), (String)resourcePathTokens[resourcePathTokens.length - 1]);
        }
        return false;
    }

    private boolean isPathPointingToPageUrl(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 5 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            Space space = this.spaceManager.getSpace(spaceKey);
            Page page = this.pageManager.getPage(spaceKey, resourcePathTokens[resourcePathTokens.length - 2]);
            return null != space && !this.hasAliasInTokens(resourcePathTokens, 3) && null != page && StringUtils.equals((String)(page.getTitle() + ".url"), (String)resourcePathTokens[resourcePathTokens.length - 1]);
        }
        return false;
    }

    private Collection<Page> getUniqueAncestors(List<Page> ancestors) {
        LinkedHashSet<Page> uniqueAncestors = new LinkedHashSet<Page>();
        if (null != ancestors) {
            HashSet<Long> ancestorIds = new HashSet<Long>();
            for (Page ancestor : ancestors) {
                long ancestorId = ancestor.getId();
                if (ancestorIds.contains(ancestorId)) continue;
                uniqueAncestors.add(ancestor);
                ancestorIds.add(ancestorId);
            }
        }
        return uniqueAncestors;
    }

    private boolean isPagePathSameAsBreadcrumbs(String[] pathTokensAfterSpace, Page page) {
        if (!this.webdavSettingsManager.isStrictPageResourcePathCheckingDisabled()) {
            List pageAncestors = page.getAncestors();
            Collection<Page> uniqueAncestors = this.getUniqueAncestors(pageAncestors);
            LOGGER.debug("Page ancestors: " + pageAncestors);
            LOGGER.debug("Unique ancestors: " + uniqueAncestors);
            LOGGER.debug("Expected ancestor path: " + StringUtils.join((Object[])pathTokensAfterSpace, (String)"/"));
            if (pathTokensAfterSpace.length == uniqueAncestors.size()) {
                for (int i = 0; i < pathTokensAfterSpace.length; ++i) {
                    if (StringUtils.equals((String)pathTokensAfterSpace[i], (String)((Page)pageAncestors.get(i)).getTitle())) continue;
                    return false;
                }
                return true;
            }
            return false;
        }
        LOGGER.debug("Strict page checking disabled. So I'll just assume the paths are ok.");
        return true;
    }

    private boolean isPathPointingToPage(String[] resourcePathTokens) {
        if (resourcePathTokens.length >= 4 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1]))) {
            String spaceKey = resourcePathTokens[2];
            String pageTitle = resourcePathTokens[resourcePathTokens.length - 1];
            Space space = this.spaceManager.getSpace(spaceKey);
            LOGGER.debug("Is there a space with the key " + spaceKey + "? " + (null != space));
            if (null != space) {
                Page page = this.pageManager.getPage(spaceKey, pageTitle);
                LOGGER.debug("Is there a page in space " + spaceKey + " with the title \"" + pageTitle + "\"? " + (null != page));
                return !this.hasAliasInTokens(resourcePathTokens, 3) && null != page && this.isPagePathSameAsBreadcrumbs((String[])ArrayUtils.subarray((Object[])resourcePathTokens, (int)3, (int)(resourcePathTokens.length - 1)), page);
            }
        }
        return false;
    }

    private boolean isPathPointingToSpaceDescription(String[] resourcePathTokens) {
        String spaceKey;
        Space space;
        if (resourcePathTokens.length == 4 && (StringUtils.equals((String)"Global", (String)resourcePathTokens[1]) || StringUtils.equals((String)"Personal", (String)resourcePathTokens[1])) && null != (space = this.spaceManager.getSpace(spaceKey = resourcePathTokens[2]))) {
            return StringUtils.equals((String)(space.getName() + ".txt"), (String)resourcePathTokens[3]);
        }
        return false;
    }

    private boolean isPathPointingToSpace(String[] resourcePathTokens) {
        return resourcePathTokens.length == 3 && ArrayUtils.indexOf((Object[])new String[]{"Global", "Personal"}, (Object)resourcePathTokens[1]) >= 0 && null != this.spaceManager.getSpace(resourcePathTokens[2]);
    }

    private boolean isPathPointingToEitherGlobalOrPersonalSpaces(String[] resourcePathTokens) {
        return resourcePathTokens.length == 2 && ArrayUtils.indexOf((Object[])new String[]{"Global", "Personal"}, (Object)resourcePathTokens[1]) >= 0;
    }

    private boolean isPathPointingToDashboard(String[] resourcePathTokens) {
        return resourcePathTokens.length == 1;
    }

    private boolean isPathPointingSpaceAttachment(String[] resourcePathTokens) {
        if (resourcePathTokens.length == 4 && !StringUtils.equals((String)resourcePathTokens[resourcePathTokens.length - 1], (String)"@news")) {
            Space space = this.spaceManager.getSpace(resourcePathTokens[2]);
            return null != space && null == this.pageManager.getPage(space.getKey(), resourcePathTokens[3]) && null != this.attachmentManager.getAttachment((ContentEntityObject)space.getDescription(), resourcePathTokens[3]);
        }
        return false;
    }
}

