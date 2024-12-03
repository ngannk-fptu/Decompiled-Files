/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.core.util.filter.Filter
 *  com.atlassian.core.util.filter.FilterChain
 *  com.atlassian.core.util.filter.ListFilter
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.opensymphony.xwork2.ActionContext
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.labels.PermittedLabelView;
import com.atlassian.confluence.labels.SpecialLabelFilter;
import com.atlassian.confluence.labels.VisibleLabelFilter;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.labels.persistence.dao.RankedLabelSearchResult;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.history.UserHistory;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.util.filter.Filter;
import com.atlassian.core.util.filter.FilterChain;
import com.atlassian.core.util.filter.ListFilter;
import com.atlassian.user.User;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class LabelUtil {
    public static final String LABEL_DELIM_CHARS = " ,";
    public static final String LABEL_DELIM = " ";
    public static final int MAX_ALLOWED_LABELS_PER_INPUT = 20;
    public static final int MAX_ALLOWED_LABELS = 500;
    public static final String LABELS_STRING = "labelsString";

    public static String convertToDelimitedString(Labelable obj, User user) {
        List<Label> labels = new PermittedLabelView(obj, user, true).getLabels();
        LinkedList<Object> names = new LinkedList<Object>();
        for (int i = 0; i < labels.size(); ++i) {
            Label label = labels.get(i);
            if (Namespace.isPersonal(label)) {
                names.add("my:" + label.getName());
                continue;
            }
            names.add(label.getName());
        }
        return StringUtils.join(names.iterator(), (String)LABEL_DELIM);
    }

    public static String convertToDelimitedString(List names) {
        StringBuilder buffer = new StringBuilder(20);
        String sep = "";
        for (int i = 0; i < names.size(); ++i) {
            buffer.append(sep);
            buffer.append(names.get(i));
            sep = LABEL_DELIM;
        }
        return buffer.toString().trim();
    }

    public static String joinIds(List labels, String separator) {
        StringBuilder joinedIds = new StringBuilder();
        for (Label label : labels) {
            if (StringUtils.isNotEmpty((CharSequence)joinedIds)) {
                joinedIds.append(separator);
            }
            joinedIds.append(label.getId());
        }
        return joinedIds.toString();
    }

    public static boolean isValidLabelNames(String delimitedLabelNames) {
        return LabelUtil.isValidLabelNames(LabelUtil.split(delimitedLabelNames));
    }

    public static boolean isValidLabelNames(Collection names) {
        Iterator i = names.iterator();
        while (i.hasNext()) {
            if (LabelUtil.isValidLabelName((String)i.next())) continue;
            return false;
        }
        return true;
    }

    public static boolean isValidLabelName(String labelName) {
        if (labelName == null) {
            return false;
        }
        if ((labelName = labelName.trim()).length() == 0) {
            return false;
        }
        return LabelParser.parse(labelName) != null;
    }

    public static boolean isValidLabelLengths(String concatenatedLabelNames) {
        return LabelUtil.isValidLabelLengths(LabelUtil.split(concatenatedLabelNames));
    }

    public static boolean isValidLabelLengths(Collection labelNames) {
        Iterator i = labelNames.iterator();
        while (i.hasNext()) {
            if (LabelUtil.isValidLabelLength((String)i.next())) continue;
            return false;
        }
        return true;
    }

    public static boolean isValidLabelLength(String input) {
        ParsedLabelName parsedLabelName = LabelParser.parse(input.trim());
        return parsedLabelName != null && LabelParser.isValidLabelLength(parsedLabelName);
    }

    public static List getVisibleLabelNames(List labels, String username) {
        ArrayList<String> returnedLabelNames = new ArrayList<String>(labels.size());
        for (Label label : labels) {
            Namespace namespace = label.getNamespace();
            if (Namespace.GLOBAL.equals(namespace)) {
                returnedLabelNames.add(label.getName());
                continue;
            }
            if (!LabelUtil.labelIsVisibleToUser(namespace, label, username)) continue;
            returnedLabelNames.add(label.toStringWithNamespace());
        }
        return returnedLabelNames;
    }

    private static boolean labelIsVisibleToUser(Namespace namespace, Label label, String username) {
        return "public".equals(namespace.getVisibility()) || "owner".equals(label.getNamespace().getVisibility()) && username != null && username.equals(label.getOwner());
    }

    public static List<String> split(String input) {
        if (StringUtils.isEmpty((CharSequence)input)) {
            return Collections.EMPTY_LIST;
        }
        StringTokenizer tokens = new StringTokenizer(input, LABEL_DELIM_CHARS, false);
        LinkedHashSet<String> newLabelNames = new LinkedHashSet<String>();
        while (tokens.hasMoreTokens()) {
            newLabelNames.add(tokens.nextToken().trim());
        }
        return Lists.newLinkedList(newLabelNames);
    }

    public static int countLabels(String input) {
        if (StringUtils.isEmpty((CharSequence)input)) {
            return 0;
        }
        return new StringTokenizer(input, LABEL_DELIM_CHARS, false).countTokens();
    }

    @Deprecated
    public static boolean isFavouriteLabel(String labelName) {
        if (labelName == null) {
            return false;
        }
        return LabelUtil.isFavouriteLabel(LabelParser.parse(labelName, AuthenticatedUserThreadLocal.get()));
    }

    public static boolean isFavouriteLabel(@Nullable ParsedLabelName label) {
        if (label == null) {
            return false;
        }
        String labelPrefix = label.getPrefix();
        String namespacePrefix = labelPrefix != null ? labelPrefix.substring(0, labelPrefix.length() - 1) : null;
        return label.getName() != null && namespacePrefix != null && Namespace.PERSONAL.getPrefix().equals(namespacePrefix) && ("favourite".equals(label.getName()) || "favorite".equals(label.getName()));
    }

    public static boolean isFavouriteLabel(Label label) {
        if (label == null) {
            return false;
        }
        return Namespace.PERSONAL.equals(label.getNamespace()) && ("favourite".equals(label.getName()) || "favorite".equals(label.getName()));
    }

    public static int countLabelsWithoutFavourites(String labelsString) {
        return StringUtils.isEmpty((CharSequence)labelsString) ? 0 : Collections.list(new StringTokenizer(labelsString, LABEL_DELIM_CHARS, false)).stream().mapToInt(value -> LabelUtil.isFavouriteLabel(value.toString()) ? 0 : 1).sum();
    }

    @Deprecated
    public static int countLabelNamesWithoutFavourites(Collection<String> labelNames) {
        return labelNames.stream().mapToInt(labelName -> LabelUtil.isFavouriteLabel(labelName) ? 0 : 1).sum();
    }

    public static int countParsedLabelNamesWithoutFavourites(Collection<ParsedLabelName> labels) {
        return labels.stream().mapToInt(label -> LabelUtil.isFavouriteLabel(label) ? 0 : 1).sum();
    }

    public static int countLabelsWithoutFavourites(Collection<Label> labels) {
        return labels.stream().mapToInt(label -> LabelUtil.isFavouriteLabel(label) ? 0 : 1).sum();
    }

    private static Collection<String> filterUserLabels(Collection<String> newLabelNames, String userName) {
        LinkedList<String> filteredNewLabelNames = new LinkedList<String>();
        for (String labelName : newLabelNames) {
            ParsedLabelName r = LabelParser.parse(labelName);
            String owner = r.getOwner();
            if (StringUtils.isNotEmpty((CharSequence)owner)) {
                if (!owner.equals(userName)) continue;
                filteredNewLabelNames.add("my:" + r.getName());
                continue;
            }
            filteredNewLabelNames.add(labelName);
        }
        return filteredNewLabelNames;
    }

    @Deprecated
    public static boolean syncState(String source, LabelManager labelManager, User user, Labelable dest, boolean ignored) {
        return LabelUtil.syncState(source, labelManager, user, dest);
    }

    public static boolean syncState(String source, LabelManager labelManager, User user, Labelable dest) {
        List<String> labelNames = LabelUtil.split(source);
        return LabelUtil.syncState(labelNames, labelManager, user, dest);
    }

    public static boolean syncState(List<com.atlassian.confluence.api.model.content.Label> source, LabelManager labelManager, User user, Labelable dest) {
        Collection labelNames = source.stream().map(l -> l.getLabel()).collect(Collectors.toList());
        return LabelUtil.syncState(labelNames, labelManager, user, dest);
    }

    public static boolean syncState(Collection<String> labelNames, LabelManager labelManager, User user, Labelable dest) {
        if (!LabelUtil.isValidLabelNames(labelNames)) {
            return false;
        }
        String userName = user != null ? user.getName() : null;
        labelNames = LabelUtil.filterUserLabels(labelNames, userName);
        LinkedHashSet<Label> requestedLabels = new LinkedHashSet<Label>();
        for (String labelName : labelNames) {
            ParsedLabelName ref = LabelParser.parse(labelName);
            requestedLabels.add(ref.toLabel());
        }
        PermittedLabelView labelable = new PermittedLabelView(dest, user, true);
        LinkedHashSet<Label> existingLabels = new LinkedHashSet<Label>(labelable.getLabels());
        Sets.SetView labelsToRemove = Sets.difference(existingLabels, requestedLabels);
        for (Label label : labelsToRemove) {
            labelManager.removeLabel(dest, label);
            LabelUtil.recordLabelInteractionInHistory(label);
        }
        requestedLabels.removeAll(existingLabels);
        for (Label label : requestedLabels) {
            labelManager.addLabel(dest, label);
            LabelUtil.recordLabelInteractionInHistory(label);
        }
        return true;
    }

    public static Label addLabel(String labelReference, LabelManager labelManager, Labelable object) {
        if (!LabelUtil.isValidLabelName(labelReference) || !LabelUtil.isValidLabelLength(labelReference)) {
            return null;
        }
        ParsedLabelName parsedLabelName = LabelParser.parse(labelReference);
        if (parsedLabelName == null) {
            return null;
        }
        return LabelUtil.addLabelImpl(parsedLabelName.toLabel(), labelManager, object);
    }

    public static Label addLabel(String labelReference, LabelManager labelManager, Labelable object, ConfluenceUser user) {
        if (!LabelUtil.isValidLabelName(labelReference) || !LabelUtil.isValidLabelLength(labelReference)) {
            return null;
        }
        ParsedLabelName parsedLabelName = LabelParser.parse(labelReference);
        if (parsedLabelName == null) {
            return null;
        }
        return LabelUtil.addLabelImpl(parsedLabelName.toLabel(user), labelManager, object);
    }

    private static Label addLabelImpl(Label label, LabelManager labelManager, Labelable object) {
        int result = labelManager.addLabel(object, label);
        LabelUtil.recordLabelInteractionInHistory(label);
        if (result == 0) {
            return null;
        }
        if (result == 1) {
            return labelManager.getLabel(label);
        }
        return label;
    }

    public static void recordLabelInteractionInHistory(Label label) {
        Map session = null;
        if (ActionContext.getContext() != null) {
            session = ActionContext.getContext().getSession();
        }
        if (session == null) {
            return;
        }
        UserHistory history = (UserHistory)session.get("confluence.user.history");
        if (history == null) {
            history = new UserHistory(20);
            session.put("confluence.user.history", history);
        }
        history.addLabel(label);
    }

    public static List rankResults(List results) {
        LinkedList<RankedLabelSearchResult> rankedResults = new LinkedList<RankedLabelSearchResult>();
        int currentCount = -1;
        int rank = 0;
        for (int i = 0; i < results.size(); ++i) {
            LabelSearchResult labelSearchResult = (LabelSearchResult)results.get(i);
            if (currentCount != labelSearchResult.getCount()) {
                currentCount = labelSearchResult.getCount();
                rank = i + 1;
            }
            RankedLabelSearchResult info = new RankedLabelSearchResult(labelSearchResult.getLabel(), rank, labelSearchResult.getCount());
            rankedResults.add(info);
        }
        return rankedResults;
    }

    public static List getRecentAndPopularLabelsForEntity(ContentEntityObject entity, LabelManager labelManager, int maxResults, String user) {
        int maxRequiredResults = entity.getLabelCount() + maxResults;
        List recentlyUsedLabels = StringUtils.isNotEmpty((CharSequence)user) ? LabelUtil.filterDuplicates(labelManager.getRecentlyUsedPersonalLabels(user, maxRequiredResults)) : new ArrayList();
        List<LabelSearchResult> mostPopularLabels = SpaceContentEntityObject.class.isAssignableFrom(entity.getClass()) && StringUtils.isNotEmpty((CharSequence)((SpaceContentEntityObject)entity).getSpaceKey()) ? labelManager.getMostPopularLabelsInSpace(((SpaceContentEntityObject)entity).getSpaceKey(), maxRequiredResults) : labelManager.getMostPopularLabels(maxRequiredResults);
        LabelUtil.fixLabels(mostPopularLabels);
        mostPopularLabels = LabelUtil.filterDuplicates(mostPopularLabels);
        recentlyUsedLabels.removeAll(entity.getLabels());
        mostPopularLabels.removeAll(entity.getLabels());
        mostPopularLabels.removeAll(recentlyUsedLabels);
        int halfSuggestedCount = maxResults / 2;
        int recentLabelsUpperBound = Math.min(recentlyUsedLabels.size(), halfSuggestedCount);
        int popularLabelUpperbound = Math.min(mostPopularLabels.size(), halfSuggestedCount);
        if (recentLabelsUpperBound < halfSuggestedCount && mostPopularLabels.size() > halfSuggestedCount) {
            popularLabelUpperbound = Math.min(mostPopularLabels.size(), maxResults - recentLabelsUpperBound);
        } else if (popularLabelUpperbound < halfSuggestedCount && recentlyUsedLabels.size() > halfSuggestedCount) {
            recentLabelsUpperBound = Math.min(recentlyUsedLabels.size(), maxResults - popularLabelUpperbound);
        }
        LinkedList result = new LinkedList();
        result.addAll(recentlyUsedLabels.subList(0, recentLabelsUpperBound));
        result.addAll(mostPopularLabels.subList(0, popularLabelUpperbound));
        return result;
    }

    private static List filterDuplicates(List list) {
        HashSet tempList = new HashSet();
        tempList.addAll(list);
        ArrayList result = new ArrayList();
        result.addAll(tempList);
        return result;
    }

    public static List getRecentAndPopularLabels(String spaceKey, LabelManager labelManager, int maxResults, String user) {
        int halfSuggestedCount = maxResults / 2;
        HashSet<Label> tempLabels = new HashSet<Label>();
        List<Object> mostPopularLabels = new ArrayList();
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            tempLabels.addAll(GeneralUtil.safeSubList(labelManager.getRecentlyUsedLabelsInSpace(spaceKey), halfSuggestedCount));
            mostPopularLabels = labelManager.getMostPopularLabelsInSpace(spaceKey, halfSuggestedCount);
        } else {
            tempLabels.addAll(GeneralUtil.safeSubList(labelManager.getRecentlyUsedLabels(), halfSuggestedCount));
            mostPopularLabels = labelManager.getMostPopularLabels(halfSuggestedCount);
        }
        LabelUtil.fixLabels(mostPopularLabels);
        tempLabels.addAll(mostPopularLabels);
        ArrayList<Label> suggestedLabels = new ArrayList<Label>();
        suggestedLabels.addAll(tempLabels);
        return suggestedLabels;
    }

    public static void fixLabels(List suggestedLabels) {
        ListIterator<Label> it = suggestedLabels.listIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (!(o instanceof LabelSearchResult)) continue;
            it.set(((LabelSearchResult)o).getLabel());
        }
    }

    public static List<Label> extractLabelsFromLabellings(List<Labelling> labellings) {
        LinkedList<Label> labels = new LinkedList<Label>();
        if (labellings != null) {
            for (Labelling labelling : labellings) {
                if (StringUtils.isEmpty((CharSequence)labelling.getLabel().getDisplayTitle())) continue;
                labels.add(labelling.getLabel());
            }
        }
        return labels;
    }

    @Deprecated
    public static com.google.common.base.Predicate<Label> getLabelPredicate(User user, boolean hideSpecialLabels) {
        VisibleLabelFilter predicate;
        VisibleLabelFilter visibleLabelFilter = predicate = user != null ? new VisibleLabelFilter(user.getName()) : new VisibleLabelFilter();
        if (hideSpecialLabels) {
            predicate = Predicates.and((com.google.common.base.Predicate)predicate, (com.google.common.base.Predicate)new SpecialLabelFilter());
        }
        return predicate;
    }

    public static Predicate<Label> labelPredicate(User user, boolean hideSpecialLabels) {
        return arg_0 -> LabelUtil.getLabelPredicate(user, hideSpecialLabels).apply(arg_0);
    }

    public static ListFilter getLabelFilters(User user, boolean hideSpecialLabels) {
        FilterChain filters = new FilterChain();
        VisibleLabelFilter visibleLabelFilter = user != null ? new VisibleLabelFilter(user.getName()) : new VisibleLabelFilter();
        filters.addFilter((Filter)visibleLabelFilter);
        if (hideSpecialLabels) {
            filters.addFilter((Filter)new SpecialLabelFilter());
        }
        return new ListFilter((Filter)filters);
    }

    public static List<Label> getLabelsFor(String labels, LabelManager labelManager) {
        ArrayList<Label> labelsList = new ArrayList<Label>();
        if (labels != null) {
            for (String labelString : LabelUtil.split(labels)) {
                ParsedLabelName labelName;
                Label label;
                if (StringUtils.isBlank((CharSequence)labelString) || (label = labelManager.getLabel(labelName = LabelParser.parse(labelString))) == null) continue;
                labelsList.add(label);
            }
        }
        return labelsList;
    }

    public static String getLabelsHash(List<Label> labels) {
        String labelIds = LabelUtil.joinIds(labels, LABEL_DELIM);
        return DigestUtils.md5Hex((String)labelIds);
    }
}

