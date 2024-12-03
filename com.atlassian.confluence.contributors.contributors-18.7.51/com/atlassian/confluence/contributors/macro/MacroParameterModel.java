/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.core.Versioned
 *  com.atlassian.confluence.pages.BlogPost
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.node.ObjectNode
 *  org.joda.time.DateTime
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.confluence.contributors.macro;

import com.atlassian.confluence.contributors.util.AuthorRankingSystem;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.pages.BlogPost;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MacroParameterModel {
    private static final String PARAMETER_SPACE = "space";
    private static final String PARAMETER_SPACES = "spaces";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_LABELS = "labels";
    private static final String PARAMETER_LABEL = "label";
    private static final String PARAMETER_INCLUDE = "include";
    private static final String PARAMETER_ERROR_STRING = "noneFoundMessage";
    private static final String PARAMETER_MODE = "mode";
    private static final String PARAMETER_SHOW_COUNT = "showCount";
    private static final String PARAMETER_SHOWTIME = "showLastTime";
    private static final String MODE_LIST = "list";
    private static final int NO_LIMIT = -1;
    private static final String PARAMETER_LIMIT = "limit";
    private static final String PARAMETER_REVERSE = "reverse";
    private static final String PARAMETER_SHOW_ANON = "showAnonymous";
    private static final String PARAMETER_ORDER = "order";
    private static final String PARAMETER_PUBLISH_DATE = "publishDate";
    private static final String PARAMETER_SCOPE = "scope";
    private static final String PARAMETER_CONTENT_TYPE = "contentType";
    private static final String ORDER_COUNT = "count";
    private static final String ORDER_NAME = "name";
    private static final String ORDER_UPDATE = "update";
    private static final String ORDER_EDITS = "edits";
    private static final String ORDER_EDIT_TIME = "editTime";
    private static final Map<String, AuthorRankingSystem.RankType> RANK_TYPE_PER_ORDER = ImmutableMap.of((Object)"name", (Object)((Object)AuthorRankingSystem.RankType.FULL_NAME), (Object)"update", (Object)((Object)AuthorRankingSystem.RankType.LAST_ACTIVE_TIME), (Object)"count", (Object)((Object)AuthorRankingSystem.RankType.TOTAL_COUNT), (Object)"editTime", (Object)((Object)AuthorRankingSystem.RankType.EDIT_TIME), (Object)"edits", (Object)((Object)AuthorRankingSystem.RankType.EDIT_COUNT));
    private static final Map<String, String> CONTENT_TYPES_MAPPING = ImmutableMap.of((Object)"pages", (Object)"page", (Object)"blogposts", (Object)"blogpost");
    private final Map<String, String> macroParameters;
    private final SpaceContentEntityObject contextEntity;

    public MacroParameterModel(Map<String, String> macroParameters, SpaceContentEntityObject contextEntity) {
        this.macroParameters = macroParameters;
        this.contextEntity = contextEntity;
    }

    ObjectNode toJson() {
        ObjectNode json = (ObjectNode)new ObjectMapper().valueToTree((Object)Maps.filterKeys(this.macroParameters, (Predicate)Predicates.not((Predicate)Predicates.equalTo((Object)": = | RAW | = :"))));
        json.put("contextEntityId", this.contextEntity.getId());
        return json;
    }

    String getNoContributorsErrorMessage() {
        return (String)StringUtils.defaultIfEmpty((CharSequence)this.macroParameters.get(PARAMETER_ERROR_STRING), (CharSequence)String.format("No contributors found for: %s on selected page(s)", StringUtils.join(this.getIncludesParam(), (String)", ")));
    }

    Set<String> getIncludesParam() {
        return Sets.newHashSet((Object[])StringUtils.split((String)StringUtils.defaultString((String)this.macroParameters.get(PARAMETER_INCLUDE), (String)ContributorsMacroInclude.AUTHORS.macroParamValue), (String)", "));
    }

    Set<ContributorsMacroInclude> getIncludeParams() {
        return this.getIncludesParam().stream().map(input -> {
            for (ContributorsMacroInclude include : ContributorsMacroInclude.values()) {
                if (!include.macroParamValue.equals(StringUtils.trim((String)input))) continue;
                return include;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    static <T extends SpaceContentEntityObject> T getLatestVersion(Versioned entity) {
        return (T)((SpaceContentEntityObject)(entity.isLatestVersion() ? entity : entity.getLatestVersion()));
    }

    @Nonnull
    public String getSpaceKey() {
        String spaceKey = this.getSpaceKeyFromParametersOnly();
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            return MacroParameterModel.getLatestVersion((Versioned)this.contextEntity).getSpaceKey();
        }
        return spaceKey;
    }

    @Nonnull
    public String getPageTitle() {
        String pageTitle = this.macroParameters.get(PARAMETER_PAGE);
        if (StringUtils.isBlank((CharSequence)pageTitle) && StringUtils.isBlank((CharSequence)this.getSpaceKeyFromParametersOnly()) && StringUtils.isBlank((CharSequence)this.getLabelsString())) {
            return MacroParameterModel.getLatestVersion((Versioned)this.contextEntity).getTitle();
        }
        return pageTitle;
    }

    public Date getPublishedDate() {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern((String)"yyyy/MM/dd");
            DateTime dateTime = dateTimeFormatter.parseDateTime(StringUtils.defaultString((String)this.macroParameters.get(PARAMETER_PUBLISH_DATE)));
            return dateTime.toDate();
        }
        catch (IllegalArgumentException invalidDate) {
            if (this.contextEntity instanceof BlogPost) {
                return this.contextEntity.getCreationDate();
            }
            return null;
        }
    }

    @Nullable
    String getSpaceKeyFromParametersOnly() {
        return (String)StringUtils.defaultIfEmpty((CharSequence)this.macroParameters.get(PARAMETER_SPACES), (CharSequence)this.macroParameters.get(PARAMETER_SPACE));
    }

    @Nullable
    public String getLabelsString() {
        return (String)StringUtils.defaultIfEmpty((CharSequence)this.macroParameters.get(PARAMETER_LABELS), (CharSequence)this.macroParameters.get(PARAMETER_LABEL));
    }

    protected int getLimit() {
        try {
            int limit = Integer.parseInt(StringUtils.defaultString((String)this.macroParameters.get(PARAMETER_LIMIT)));
            return Math.max(limit, -1);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    protected static boolean isLimitLess(int limitValue) {
        return limitValue <= -1;
    }

    @Nullable
    public String getScope() {
        return this.macroParameters.get(PARAMETER_SCOPE);
    }

    @Nullable
    public String getContentType() {
        String contentType = this.macroParameters.get(PARAMETER_CONTENT_TYPE);
        return CONTENT_TYPES_MAPPING.get(contentType);
    }

    protected boolean isReverse() {
        return BooleanUtils.toBoolean((String)this.macroParameters.get(PARAMETER_REVERSE));
    }

    protected boolean isShowAnonymousContributions() {
        return BooleanUtils.toBoolean((String)this.macroParameters.get(PARAMETER_SHOW_ANON));
    }

    protected String getOrder() {
        return this.macroParameters.get(PARAMETER_ORDER);
    }

    @Nonnull
    protected AuthorRankingSystem.RankType getRankType(AuthorRankingSystem.RankType defaultRankType) {
        String order = this.getOrder();
        AuthorRankingSystem.RankType rankType = RANK_TYPE_PER_ORDER.get(order);
        return rankType != null ? rankType : defaultRankType;
    }

    public boolean isShowCount() {
        return BooleanUtils.toBoolean((String)this.macroParameters.get(PARAMETER_SHOW_COUNT));
    }

    public boolean isShowTime() {
        return BooleanUtils.toBoolean((String)this.macroParameters.get(PARAMETER_SHOWTIME));
    }

    public LayoutStyle getLayoutStyle() {
        return MacroParameterModel.getLayoutStyle(this.macroParameters);
    }

    static LayoutStyle getLayoutStyle(Map<String, String> macroParameters) {
        return MODE_LIST.equals(macroParameters.get(PARAMETER_MODE)) ? LayoutStyle.LIST : LayoutStyle.FLAT;
    }

    public static enum LayoutStyle {
        FLAT,
        LIST;

    }

    public static enum ContributorsMacroInclude {
        AUTHORS("authors"),
        COMMENTS("comments"),
        LABELS("labels"),
        WATCHES("watches");

        final String macroParamValue;

        private ContributorsMacroInclude(String macroParamValue) {
            this.macroParamValue = macroParamValue;
        }

        public String toString() {
            return this.macroParamValue;
        }
    }
}

