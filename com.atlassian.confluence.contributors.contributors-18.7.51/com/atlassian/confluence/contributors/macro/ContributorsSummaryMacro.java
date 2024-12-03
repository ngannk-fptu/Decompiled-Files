/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.contributors.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.contributors.macro.BaseContributionMacro;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.contributors.search.Doc;
import com.atlassian.confluence.contributors.search.PageSearcher;
import com.atlassian.confluence.contributors.util.AuthorRanking;
import com.atlassian.confluence.contributors.util.AuthorRankingSystem;
import com.atlassian.confluence.contributors.util.DefaultPageProcessor;
import com.atlassian.confluence.contributors.util.PageProcessor;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class ContributorsSummaryMacro
extends BaseContributionMacro {
    private static final String VIEW_PAGE_PREFIX_URL = "/pages/viewpage.action?pageId=";
    private static final String PARAMETER_COLUMNS = "columns";
    private static final String PARAMETER_GROUPBY = "groupby";
    private static final String PARAMETER_SHOWZEROCOUNTS = "showZeroCounts";
    private static final String GROUPBY_PAGE = "pages";
    private static final String COLUMNS_EDITS = "edits";
    private static final int COLUMNID_EDITS = 2;
    private static final String COLUMNS_EDITED = "edited";
    private static final int COLUMNID_EDITED = 3;
    private static final String COLUMNS_COMMENTS = "comments";
    private static final int COLUMNID_COMMENTS = 4;
    private static final String COLUMNS_COMMENTED = "commented";
    private static final int COLUMNID_COMMENTED = 5;
    private static final String COLUMNS_LABELS = "labels";
    private static final String DEFAULT_COLUMNS_PARAMETER_VALUE = StringUtils.join(Arrays.asList("edits", "comments", "labels"), (String)",");
    private static final int COLUMNID_LABELS = 6;
    private static final String COLUMNS_LABELED = "labeled";
    private static final int COLUMNID_LABLED = 7;
    private static final String COLUMNS_LABELSLIST = "labellist";
    private static final int COLUMNID_LABELSLIST = 8;
    private static final String COLUMNS_WATCHES = "watches";
    private static final int COLUMNID_WATCHES = 9;
    private static final String COLUMNS_WATCHING = "watching";
    private static final int COLUMNID_WATCHING = 10;
    private static final String COLUMNS_LASTUPDATE = "lastupdate";
    private static final int COLUMNID_LASTUPDATE = 32;
    private static final Set<String> COLUMNS = ImmutableSet.of((Object)"edits", (Object)"edited", (Object)"comments", (Object)"commented", (Object)"labels", (Object)"labeled", (Object[])new String[]{"labellist", "watches", "watching", "lastupdate"});
    private static final Predicate<String> IS_VALID_COLUMN_NAME = COLUMNS::contains;
    private static final Map<String, Integer> COLUMN_NAMES_TO_ID_MAP = ImmutableMap.builder().put((Object)"edits", (Object)2).put((Object)"edited", (Object)3).put((Object)"comments", (Object)4).put((Object)"commented", (Object)5).put((Object)"labels", (Object)6).put((Object)"labeled", (Object)7).put((Object)"labellist", (Object)8).put((Object)"watches", (Object)9).put((Object)"watching", (Object)10).put((Object)"lastupdate", (Object)32).build();
    private static final Random TABLE_ID_GENERATOR = new Random();
    private final SettingsManager settingsManager;
    private final PageSearcher pageSearcher;
    private final PageProcessor pageProcessor;

    public ContributorsSummaryMacro(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport SettingsManager settingsManager, @Qualifier(value="pageSearcher") PageSearcher pageSearcher, PageProcessor pageProcessor) {
        super(localeManager, i18NBeanFactory);
        this.settingsManager = settingsManager;
        this.pageSearcher = pageSearcher;
        this.pageProcessor = pageProcessor;
    }

    public Streamable executeToStream(Map<String, String> macroParameters, Streamable macroBody, ConversionContext conversionContext) {
        ContentEntityObject contentObject = conversionContext.getEntity();
        if (contentObject instanceof AbstractPage) {
            return writer -> this.execute(macroParameters, (SpaceContentEntityObject)((AbstractPage)contentObject), writer);
        }
        if (contentObject instanceof Comment) {
            PageContext pageContext = Objects.requireNonNull(((Comment)contentObject).getContainer()).toPageContext();
            return this.executeToStream(macroParameters, macroBody, (ConversionContext)new DefaultConversionContext((RenderContext)pageContext));
        }
        if (contentObject instanceof Draft) {
            return Streamables.from((String)RenderUtils.blockError((String)this.getText("error.preview", Collections.singletonList(this.getText("com.atlassian.confluence.contributors.contributors-summary.label"))), (String)""));
        }
        return Streamables.from((String)RenderUtils.blockError((String)this.getText("error.unsupportedcontent", Collections.singletonList(this.getText("com.atlassian.confluence.contributors.contributors-summary.label"))), (String)""));
    }

    private void execute(Map<String, String> macroParameters, SpaceContentEntityObject page, Appendable output) throws IOException {
        Set<String> columnNames = ContributorsSummaryMacro.getColumnNames(macroParameters);
        Collection<Integer> columnIds = ContributorsSummaryMacro.getColumnIds(columnNames);
        PageProcessor.GroupBy groupByType = ContributorsSummaryMacro.getGroupByType(macroParameters);
        this.appendTableHeader(columnIds, groupByType, output);
        MacroParameterModel parameterModel = new MacroParameterModel(macroParameters, page);
        Iterator<AuthorRanking> rankedAuthors = this.getRankedAuthors(macroParameters, columnNames, groupByType, parameterModel);
        this.appendTableBody(columnIds, groupByType, output, rankedAuthors, parameterModel);
        ContributorsSummaryMacro.appendTableFooter(output);
    }

    private static void appendTableFooter(Appendable outputBuilder) throws IOException {
        outputBuilder.append("</tbody></table>");
    }

    /*
     * Enabled aggressive block sorting
     */
    private void appendTableBody(Iterable<Integer> columnIds, PageProcessor.GroupBy groupByType, Appendable output, Iterator<AuthorRanking> authors, MacroParameterModel parameterModel) throws IOException {
        int limit = parameterModel.getLimit();
        String baseURL = this.settingsManager.getGlobalSettings().getBaseUrl();
        boolean showAnonymous = parameterModel.isShowAnonymousContributions();
        String linkPostfix = "</a> ";
        block16: for (int count = 0; authors.hasNext() && (count < limit || MacroParameterModel.isLimitLess(limit)); ++count) {
            AuthorRanking author = authors.next();
            String linkPrefix = "<a href=\"" + baseURL;
            output.append("<tr><td>");
            switch (groupByType) {
                case PAGES: {
                    output.append(linkPrefix).append(VIEW_PAGE_PREFIX_URL).append(author.getIdString()).append("\">").append(HtmlUtil.htmlEncode((String)author.getFullNameString())).append("</a> ");
                    linkPrefix = linkPrefix + "/display/~";
                    break;
                }
                case CONTRIBUTORS: {
                    if (ContributorsSummaryMacro.isAnonymous(author)) {
                        if (!showAnonymous) continue block16;
                        output.append(DefaultPageProcessor.ANONYMOUS_USER.getName());
                    } else {
                        output.append(linkPrefix).append("/display/~").append(author.getIdString()).append("\">").append(HtmlUtil.htmlEncode((String)author.getFullNameString())).append("</a> ");
                    }
                    linkPrefix = linkPrefix + VIEW_PAGE_PREFIX_URL;
                }
            }
            output.append("</td>");
            for (int columnId : columnIds) {
                output.append("<td>");
                switch (columnId) {
                    case 4: {
                        output.append(String.valueOf(author.getComments()));
                        break;
                    }
                    case 5: {
                        for (Map.Entry entry : this.sortEntries(author.getCommentMap())) {
                            ContributorsSummaryMacro.appendCellText(entry, linkPrefix, "</a> ", showAnonymous, output);
                        }
                        break;
                    }
                    case 2: {
                        output.append(String.valueOf(author.getEdits()));
                        break;
                    }
                    case 3: {
                        for (Map.Entry entry : this.sortEntries(author.getEditMap())) {
                            ContributorsSummaryMacro.appendCellText(entry, linkPrefix, "</a> ", showAnonymous, output);
                        }
                        break;
                    }
                    case 6: {
                        output.append(String.valueOf(author.getLabels()));
                        break;
                    }
                    case 7: {
                        for (Map.Entry entry : this.sortEntries(author.getLabelMap())) {
                            ContributorsSummaryMacro.appendCellText(entry, linkPrefix, "</a> ", showAnonymous, output);
                        }
                        break;
                    }
                    case 8: {
                        for (String string : author.getLabelList()) {
                            output.append("<a href=\"").append(baseURL).append("/label/").append(HtmlUtil.htmlEncode((String)string)).append("\">").append(HtmlUtil.htmlEncode((String)string)).append("</a> ");
                        }
                        break;
                    }
                    case 32: {
                        if (author.getLastActiveTime() <= 0L) break;
                        output.append(GeneralUtil.getRelativeTime((Date)author.getLastActiveDate()));
                        break;
                    }
                    case 9: {
                        output.append(String.valueOf(author.getWatches()));
                        break;
                    }
                    case 10: {
                        for (Map.Entry entry : this.sortEntries(author.getWatchMap())) {
                            ContributorsSummaryMacro.appendCellText(entry, linkPrefix, "</a> ", showAnonymous, output);
                        }
                        break;
                    }
                }
                output.append("</td>");
            }
            output.append("</tr>");
        }
    }

    private List<Map.Entry<String, String>> sortEntries(Map<String, String> map) {
        return map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).collect(Collectors.toList());
    }

    private static boolean isAnonymous(AuthorRanking author) {
        return DefaultPageProcessor.ANONYMOUS_USER.getName().equals(author.getIdString());
    }

    private Iterator<AuthorRanking> getRankedAuthors(Map<String, String> macroParameters, Set<String> columnNames, PageProcessor.GroupBy groupByType, MacroParameterModel parameterModel) {
        Iterable<Doc> documents = this.getDocuments(parameterModel);
        AuthorRankingSystem rankingSystem = this.pageProcessor.process(documents, parameterModel.getRankType(AuthorRankingSystem.RankType.EDIT_COUNT), groupByType);
        boolean startValue = BooleanUtils.toBoolean((String)macroParameters.get(PARAMETER_SHOWZEROCOUNTS));
        List<AuthorRanking> rankedAuthors = rankingSystem.getRankedAuthors(parameterModel.isReverse(), startValue || columnNames.contains(COLUMNS_EDITS) || columnNames.contains(COLUMNS_EDITED) || columnNames.contains(COLUMNS_LASTUPDATE), startValue || columnNames.contains(COLUMNS_COMMENTS) || columnNames.contains(COLUMNS_COMMENTED) || columnNames.contains(COLUMNS_LASTUPDATE), startValue || columnNames.contains(COLUMNS_LABELS) || columnNames.contains(COLUMNS_LABELED) || columnNames.contains(COLUMNS_LABELSLIST) || columnNames.contains(COLUMNS_LASTUPDATE), startValue || columnNames.contains(COLUMNS_WATCHES) || columnNames.contains(COLUMNS_WATCHING) || columnNames.contains(COLUMNS_LASTUPDATE));
        return rankedAuthors.iterator();
    }

    private Iterable<Doc> getDocuments(MacroParameterModel params) {
        return this.pageSearcher.getDocuments(params);
    }

    private static Set<String> getColumnNames(Map<String, String> macroParameters) {
        String columnsParameter = StringUtils.defaultString((String)macroParameters.get(PARAMETER_COLUMNS), (String)DEFAULT_COLUMNS_PARAMETER_VALUE);
        return Arrays.stream(columnsParameter.split(",")).filter(IS_VALID_COLUMN_NAME).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Collection<Integer> getColumnIds(Collection<String> columnNames) {
        return columnNames.stream().map(COLUMN_NAMES_TO_ID_MAP::get).collect(Collectors.toList());
    }

    private static PageProcessor.GroupBy getGroupByType(Map<String, String> macroParameters) {
        return GROUPBY_PAGE.equals(macroParameters.get(PARAMETER_GROUPBY)) ? PageProcessor.GroupBy.PAGES : PageProcessor.GroupBy.CONTRIBUTORS;
    }

    private void appendTableHeader(Iterable<Integer> columnIds, PageProcessor.GroupBy groupByType, Appendable output) throws IOException {
        output.append("<table id='").append(this.generateTableId()).append("' summary='").append(this.getText("summary.table.summary")).append("' class='aui'><tbody><tr><th>").append(this.getText(PageProcessor.GroupBy.PAGES == groupByType ? "summary.tableheader.page" : "summary.tableheader.user")).append("</th>");
        for (int columnId : columnIds) {
            output.append("<th>");
            switch (columnId) {
                case 4: {
                    output.append(this.getText("summary.tableheader.comments"));
                    break;
                }
                case 5: {
                    output.append(this.getText("summary.tableheader.commented"));
                    break;
                }
                case 2: {
                    output.append(this.getText("summary.tableheader.edits"));
                    break;
                }
                case 3: {
                    output.append(this.getText("summary.tableheader.edited"));
                    break;
                }
                case 6: {
                    output.append(this.getText("summary.tableheader.labels"));
                    break;
                }
                case 7: {
                    output.append(this.getText("summary.tableheader.labelled"));
                    break;
                }
                case 8: {
                    output.append(this.getText("summary.tableheader.labellist"));
                    break;
                }
                case 32: {
                    output.append(this.getText("summary.tableheader.lastupdate"));
                    break;
                }
                case 9: {
                    output.append(this.getText("summary.tableheader.watches"));
                    break;
                }
                case 10: {
                    output.append(this.getText("summary.tableheader.watching"));
                    break;
                }
            }
            output.append("</th>");
        }
        output.append("</tr>");
    }

    private static void appendCellText(Map.Entry<String, String> entry, String linkPrefix, String linkPostfix, boolean showAnonymous, Appendable output) throws IOException {
        if (entry.getValue().equals(DefaultPageProcessor.ANONYMOUS_USER.getName())) {
            if (showAnonymous) {
                output.append(DefaultPageProcessor.ANONYMOUS_USER.getName()).append(" ");
            }
        } else {
            output.append(linkPrefix).append(entry.getKey()).append("\">").append(HtmlUtil.htmlEncode((String)entry.getValue())).append(linkPostfix);
        }
    }

    private String generateTableId() {
        return "contributors-summary-" + TABLE_ID_GENERATOR.nextInt(Integer.MAX_VALUE);
    }
}

