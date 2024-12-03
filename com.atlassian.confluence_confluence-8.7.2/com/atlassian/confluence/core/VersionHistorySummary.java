/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummaryCollaborator;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.spring.container.ContainerManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class VersionHistorySummary {
    private final long id;
    private final int version;
    private final Set<ConfluenceUser> contributors;
    private final Date lastModificationDate;
    private final String versionComment;
    @Deprecated
    private final ConfluenceUser lastModifier;

    @HtmlSafe
    public static String renderVersionComment(String versionComment) {
        WikiStyleRenderer wikiStyleRenderer = (WikiStyleRenderer)ContainerManager.getComponent((String)"wikiStyleRenderer");
        RenderMode renderMode = RenderMode.SIMPLE_TEXT.and(RenderMode.suppress((long)1L));
        PageContext context = new PageContext();
        context.pushRenderMode(renderMode);
        return wikiStyleRenderer.convertWikiToXHtml((RenderContext)context, versionComment);
    }

    @Deprecated
    public VersionHistorySummary(long contentId, int version, String lastModifier, Date lastModifiedDate, String versionComment) {
        this(contentId, version, FindUserHelper.getUserByUsername(lastModifier), lastModifiedDate, versionComment);
    }

    @Deprecated
    public VersionHistorySummary(long contentId, int version, ConfluenceUser lastModifier, Date lastModifiedDate, String versionComment) {
        this(contentId, version, lastModifier, Collections.singletonList(lastModifier), lastModifiedDate, versionComment);
    }

    @Deprecated
    public VersionHistorySummary(long contentId, int version, ConfluenceUser lastModifier, List<ConfluenceUser> contributors, Date lastModifiedDate, String versionComment) {
        this.id = contentId;
        this.version = version;
        this.lastModifier = lastModifier;
        this.contributors = new HashSet<ConfluenceUser>(contributors);
        this.contributors.add(this.lastModifier);
        this.lastModificationDate = lastModifiedDate;
        this.versionComment = versionComment;
    }

    public VersionHistorySummary(ContentEntityObject entity) {
        this.id = entity.getId();
        this.version = entity.getVersion();
        this.lastModifier = entity.getLastModifier();
        this.contributors = Collections.singleton(this.lastModifier);
        this.lastModificationDate = entity.getLastModificationDate();
        this.versionComment = entity.getVersionComment();
    }

    private VersionHistorySummary(Builder builder) {
        this.id = builder.id;
        this.version = builder.version;
        this.versionComment = builder.versionComment;
        this.lastModificationDate = builder.lastModificationDate;
        this.lastModifier = builder.lastModifier;
        this.contributors = builder.contributors;
    }

    public long getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    @Deprecated
    public ConfluenceUser getLastModifier() {
        return this.lastModifier;
    }

    @Deprecated
    public List<ConfluenceUser> getContributors() {
        return Collections.unmodifiableList(new ArrayList<ConfluenceUser>(this.contributors));
    }

    public Set<ConfluenceUser> getContributorSet() {
        return Collections.unmodifiableSet(this.contributors);
    }

    @Deprecated
    public String getLastModifierName() {
        return this.lastModifier != null ? this.lastModifier.getName() : null;
    }

    public Date getLastModificationDate() {
        return this.lastModificationDate;
    }

    public String getVersionComment() {
        return this.versionComment;
    }

    @HtmlSafe
    public String getRenderedVersionComment() {
        return VersionHistorySummary.renderVersionComment(this.getVersionComment());
    }

    public boolean isVersionCommentAvailable() {
        return StringUtils.isNotEmpty((CharSequence)this.getVersionComment()) && this.getVersionComment().trim().length() > 0;
    }

    public int hashCode() {
        return Objects.hash(this.id, this.version, this.lastModifier, this.lastModificationDate, this.versionComment);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }
        VersionHistorySummary that = (VersionHistorySummary)obj;
        return Objects.equals(this.id, that.id) && Objects.equals(this.version, that.version) && Objects.equals(this.lastModifier, that.lastModifier) && Objects.equals(this.dateToLong(this.lastModificationDate), this.dateToLong(that.lastModificationDate)) && Objects.equals(this.versionComment, that.versionComment);
    }

    private Long dateToLong(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    public static class Builder {
        private long id;
        private int version;
        private String versionComment;
        private Date lastModificationDate;
        private ConfluenceUser lastModifier;
        private Set<ConfluenceUser> contributors = new HashSet<ConfluenceUser>();

        public Builder() {
        }

        public Builder(VersionHistorySummaryCollaborator summary) {
            this.id = summary.getId();
            this.version = summary.getVersion();
            this.versionComment = summary.getVersionComment();
            this.lastModificationDate = summary.getLastModificationDate();
            this.lastModifier = summary.getLastModifier();
            this.contributors.add(summary.getCollaborator());
            this.contributors.add(this.lastModifier);
        }

        public Builder(VersionHistorySummary summary) {
            this.id = summary.id;
            this.version = summary.version;
            this.versionComment = summary.versionComment;
            this.lastModificationDate = summary.lastModificationDate;
            this.lastModifier = summary.lastModifier;
            this.contributors = new HashSet<ConfluenceUser>(summary.contributors);
            this.contributors.add(this.lastModifier);
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withVersion(int version) {
            this.version = version;
            return this;
        }

        public int getVersion() {
            return this.version;
        }

        public Builder withVersionComment(String versionComment) {
            this.versionComment = versionComment;
            return this;
        }

        public Builder withLastModificationDate(Date lastModificationDate) {
            this.lastModificationDate = new Date(lastModificationDate.getTime());
            return this;
        }

        public Builder withLastModifier(ConfluenceUser lastModifier) {
            this.lastModifier = lastModifier;
            return this;
        }

        public Builder withContributor(ConfluenceUser contributor) {
            this.contributors.add(contributor);
            return this;
        }

        public VersionHistorySummary build() {
            return new VersionHistorySummary(this);
        }
    }
}

