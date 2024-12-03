/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.NotImplementedException
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.BaseSearchResult;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.user.User;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.NotImplementedException;

public interface SearchResult
extends BaseSearchResult {
    public static final String HIGHLIGHT_START = "@@@hl@@@";
    public static final String HIGHLIGHT_END = "@@@endhl@@@";

    public Map<String, String> getExtraFields();

    public String getContent();

    default public String getResultExcerpt() {
        throw new NotImplementedException("Not implemented.");
    }

    @HtmlSafe
    default public String getResultExcerptWithHighlights() {
        return HtmlUtil.htmlEncode(this.getResultExcerpt());
    }

    public String getType();

    public String getStatus();

    public boolean isHomePage();

    public Date getLastModificationDate();

    @Deprecated
    public String getLastModifier();

    public ConfluenceUser getLastModifierUser();

    public String getDisplayTitle();

    @HtmlSafe
    default public String getDisplayTitleWithHighlights() {
        return HtmlUtil.htmlEncode(this.getDisplayTitle());
    }

    public String getUrlPath();

    public String getLastUpdateDescription();

    public String getSpaceName();

    public String getSpaceKey();

    public boolean hasLabels();

    public Set<String> getLabels(User var1);

    public Set<String> getPersonalLabels();

    public Date getCreationDate();

    @Deprecated
    public String getCreator();

    public ConfluenceUser getCreatorUser();

    public String getOwnerType();

    public String getOwnerTitle();

    public Integer getContentVersion() throws NumberFormatException;

    default public Optional<String> getExplain() {
        return Optional.empty();
    }
}

