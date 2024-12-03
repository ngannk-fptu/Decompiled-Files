/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.GenericLinkParser
 *  com.atlassian.renderer.links.UrlLink
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.content.render.xhtml.migration.LinkResolver;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.links.NotPermittedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.links.ConfluenceLinkResolver;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.links.UrlLink;
import com.atlassian.user.User;
import java.text.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XhtmlMigrationLinkResolver
implements LinkResolver {
    private static final Logger log = LoggerFactory.getLogger(XhtmlMigrationLinkResolver.class);
    private final ContentDao contentDao;
    private final GlobalSettingsManager settingsManager;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final PermissionManager permissionManager;
    public static final String DO_LINK_PERMISSION_CHECK = "DO_LINK_PERMISSION_CHECK";

    public XhtmlMigrationLinkResolver(ContentDao contentDao, GlobalSettingsManager settingsManager, ConfluenceUserResolver confluenceUserResolver, PermissionManager permissionManager) {
        this.contentDao = contentDao;
        this.settingsManager = settingsManager;
        this.confluenceUserResolver = confluenceUserResolver;
        this.permissionManager = permissionManager;
    }

    @Override
    public Link resolve(String linkText, PageContext pageContext) {
        if (log.isDebugEnabled()) {
            log.debug("Resolving Link: " + linkText);
        }
        GenericLinkParser parser = new GenericLinkParser(linkText);
        parser.parseAsContentLink();
        String destinationTitle = StringUtils.isNotBlank((CharSequence)parser.getDestinationTitle()) ? parser.getDestinationTitle() : pageContext.getPageTitle();
        String spaceKey = StringUtils.isNotBlank((CharSequence)parser.getSpaceKey()) ? parser.getSpaceKey() : pageContext.getSpaceKey();
        Link result = null;
        if (ConfluenceLinkResolver.isUrlLink(parser.getNotLinkBody())) {
            log.debug("Resolving URL Link");
            UrlLink urlLink = new UrlLink(parser);
            String url = urlLink.getUnencodedUrl();
            result = new DefaultLink(new UrlResourceIdentifier(url), new PlainTextLinkBody(urlLink.getLinkBody()), urlLink.getTitle(), null);
        } else if (this.isAnchorOnlyLink(parser)) {
            log.debug("Resolving Anchor Link");
            String linkBody = parser.getLinkBody() == null ? linkText : parser.getLinkBody();
            result = new DefaultLink(null, new PlainTextLinkBody(linkBody), parser.getLinkTitle(), parser.getAnchor());
        } else if (StringUtils.isNotBlank((CharSequence)spaceKey) && parser.getNotLinkBody().equals(spaceKey + ":")) {
            log.debug("Resolving Space Link");
            result = this.createSpaceLink(parser, spaceKey);
        } else if (parser.getContentId() > 0L) {
            log.debug("Resolving Content Entity Link");
            ContentEntityObject contentEntityObject = this.contentDao.getById(parser.getContentId());
            if (pageContext.getParam(DO_LINK_PERMISSION_CHECK) != null && !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, contentEntityObject)) {
                log.debug("Resolving not permitted Content Entity Link");
                result = new NotPermittedLink(new DefaultLink(new ContentEntityResourceIdentifier(parser.getContentId()), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), parser.getAnchor()));
            } else if (contentEntityObject instanceof Comment) {
                log.debug("Resolving Comment Content Entity Link");
                Comment comment = (Comment)contentEntityObject;
                result = new DefaultLink(new UrlResourceIdentifier(this.settingsManager.getGlobalSettings().getBaseUrl() + comment.getUrlPath()), this.getLinkBody(parser, comment.getDisplayTitle()), parser.getLinkTitle(), null);
            } else if (contentEntityObject instanceof Page) {
                log.debug("Resolving Page (Share Draft) Content Entity Link");
                Page page = (Page)contentEntityObject;
                result = new DefaultLink(new PageResourceIdentifier(page.getSpaceKey(), page.getTitle()), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), parser.getAnchor());
            } else if (contentEntityObject instanceof BlogPost) {
                log.debug("Resolving BlogPost Content Entity Link");
                BlogPost blogPost = (BlogPost)contentEntityObject;
                result = new DefaultLink(new BlogPostResourceIdentifier(blogPost.getSpaceKey(), blogPost.getTitle(), BlogPost.toCalendar(blogPost.getCreationDate())), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), parser.getAnchor());
            } else {
                log.debug("Resolving General Content Entity Link");
                result = new DefaultLink(new ContentEntityResourceIdentifier(parser.getContentId()), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), parser.getAnchor());
            }
        } else if (StringUtils.isNotBlank((CharSequence)parser.getShortcutName())) {
            result = new DefaultLink(new ShortcutResourceIdentifier(parser.getShortcutName(), parser.getShortcutValue()), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), parser.getAnchor());
        } else if (destinationTitle != null && destinationTitle.startsWith("~")) {
            String username = destinationTitle.substring(1);
            ConfluenceUser user = this.confluenceUserResolver.getUserByName(username);
            if (user == null) {
                PlainTextLinkBody linkBody = StringUtils.isBlank((CharSequence)parser.getLinkBody()) ? new PlainTextLinkBody(destinationTitle) : new PlainTextLinkBody(parser.getLinkBody());
                result = new DefaultLink(new UrlResourceIdentifier("#"), linkBody, parser.getLinkTitle(), null);
            } else {
                result = new DefaultLink(UserResourceIdentifier.createFromUsernameSource(user.getKey(), username), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), null);
            }
        } else if (StringUtils.isNotBlank((CharSequence)parser.getAttachmentName())) {
            AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier = null;
            if (BlogPostResourceIdentifier.isBlogPostLink(destinationTitle)) {
                try {
                    attachmentContainerResourceIdentifier = BlogPostResourceIdentifier.newInstanceFromLink(destinationTitle, spaceKey);
                }
                catch (ParseException e) {
                    return null;
                }
            } else if (StringUtils.isNotBlank((CharSequence)destinationTitle)) {
                attachmentContainerResourceIdentifier = new PageResourceIdentifier(spaceKey, destinationTitle);
            } else if (log.isDebugEnabled()) {
                log.debug("Unable to determine the attachment container resource identifier for link text: " + linkText);
            }
            result = new DefaultLink(new AttachmentResourceIdentifier(attachmentContainerResourceIdentifier, parser.getAttachmentName()), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), null);
        } else if (BlogPostResourceIdentifier.isBlogPostLink(destinationTitle)) {
            try {
                result = new DefaultLink(BlogPostResourceIdentifier.newInstanceFromLink(destinationTitle, spaceKey), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), parser.getAnchor());
            }
            catch (ParseException e) {
                return null;
            }
        } else if (StringUtils.isNotBlank((CharSequence)destinationTitle)) {
            result = new DefaultLink(new PageResourceIdentifier(spaceKey, destinationTitle), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), parser.getAnchor());
        } else if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            result = this.createSpaceLink(parser, spaceKey);
        }
        return result;
    }

    private boolean isAnchorOnlyLink(GenericLinkParser parser) {
        return ("#" + StringUtils.defaultString((String)parser.getAnchor())).equals(parser.getNotLinkBody());
    }

    private Link createSpaceLink(GenericLinkParser parser, String spaceKey) {
        return new DefaultLink(new SpaceResourceIdentifier(spaceKey), new PlainTextLinkBody(parser.getLinkBody()), parser.getLinkTitle(), parser.getAnchor());
    }

    private PlainTextLinkBody getLinkBody(GenericLinkParser parser, String defaultLinkBody) {
        String linkBodyText = defaultLinkBody;
        if (StringUtils.isNotBlank((CharSequence)parser.getLinkBody())) {
            linkBodyText = parser.getLinkBody();
        }
        return new PlainTextLinkBody(linkBodyText);
    }
}

