/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostsForDateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import java.text.ParseException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BlogPostResourceIdentifier
extends BlogPostsForDateResourceIdentifier
implements AttachmentContainerResourceIdentifier,
NamedResourceIdentifier {
    private final String title;
    public static final Pattern BLOG_POST_LINK_REGEX = Pattern.compile("(?:([\\w]+):)?/(\\d{4}/\\d{2}/\\d{2})/(.+)");

    public static boolean isBlogPostLink(String link) {
        return StringUtils.isNotBlank((CharSequence)link) && BLOG_POST_LINK_REGEX.matcher(link).matches();
    }

    public static BlogPostResourceIdentifier newInstanceFromLink(String link, String defaultSpaceKey) throws ParseException {
        Matcher matcher = BLOG_POST_LINK_REGEX.matcher(link);
        if (!matcher.matches()) {
            throw new IllegalStateException("Link is not in blog post format: " + link);
        }
        String spaceKey = StringUtils.isBlank((CharSequence)matcher.group(1)) ? defaultSpaceKey : matcher.group(1);
        String datePath = matcher.group(2);
        String title = matcher.group(3);
        Calendar postingDay = Calendar.getInstance();
        postingDay.setTime(XhtmlConstants.DATE_FORMATS.getPostingDayFormat().parse(datePath));
        return new BlogPostResourceIdentifier(spaceKey, title, postingDay);
    }

    public BlogPostResourceIdentifier(String spaceKey, String title, Calendar postingDay) {
        super(spaceKey, postingDay);
        if (StringUtils.isBlank((CharSequence)title)) {
            throw new IllegalArgumentException("Blog post title cannot be null or empty");
        }
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public String getResourceName() {
        return this.title;
    }

    public boolean isPopulated() {
        return StringUtils.isNotBlank((CharSequence)this.getSpaceKey()) || StringUtils.isNotBlank((CharSequence)this.getTitle()) || this.getPostingDay() != null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        BlogPostResourceIdentifier that = (BlogPostResourceIdentifier)o;
        return StringUtils.equals((CharSequence)this.title, (CharSequence)that.title);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        return builder.appendSuper(super.hashCode()).append((Object)this.title).toHashCode();
    }
}

