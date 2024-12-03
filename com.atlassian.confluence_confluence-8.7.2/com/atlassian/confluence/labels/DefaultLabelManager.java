/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.labels;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.event.events.label.LabelAddEvent;
import com.atlassian.confluence.event.events.label.LabelCreateEvent;
import com.atlassian.confluence.event.events.label.LabelDeleteEvent;
import com.atlassian.confluence.event.events.label.LabelManagerMetricsEvent;
import com.atlassian.confluence.event.events.label.LabelRemoveEvent;
import com.atlassian.confluence.impl.search.IndexerEventPublisher;
import com.atlassian.confluence.internal.labels.LabelManagerInternal;
import com.atlassian.confluence.internal.labels.persistence.LabelDaoInternal;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.labels.dto.CountableLabel;
import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import com.atlassian.confluence.labels.dto.RankedLiteLabelSearchResult;
import com.atlassian.confluence.labels.persistence.dao.LabelDao;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.labels.persistence.dao.RankedLabelSearchResult;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.event.Event;
import com.atlassian.event.EventManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultLabelManager
implements LabelManagerInternal {
    private static float[] bucketBorders = new float[]{0.01f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f};
    private LabelDaoInternal labelDaoInternal;
    private LabelDao dao;
    private EventManager eventManager;
    private EventPublisher eventPublisher;
    private ConfluenceIndexer indexer;

    public void setLabelDao(@Qualifier(value="labelDao") LabelDao dao) {
        this.dao = dao;
    }

    public void setLabelDaoInternal(@Qualifier(value="labelDao") LabelDaoInternal dao) {
        this.labelDaoInternal = dao;
    }

    @Deprecated
    public void setEventManager(EventManager manager) {
        this.eventManager = manager;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Deprecated
    public void setIndexer(ConfluenceIndexer indexer) {
        this.indexer = indexer;
    }

    @Override
    public int addLabel(Labelable labelable, Label label) {
        EditableLabelable content = this.convertToEditable(labelable);
        boolean labelCreated = false;
        if (!label.isPersistent()) {
            Label persistentLabel = this.dao.findByLabel(label);
            if (persistentLabel == null) {
                DefaultLabelManager.validateLabel(label);
                this.dao.save(label);
                labelCreated = true;
                this.publishEvent(new LabelCreateEvent(label));
            } else {
                label = persistentLabel;
            }
        }
        if (this.dao.findLabellingByContentAndLabel(content, label) != null) {
            return 0;
        }
        String userName = AuthenticatedUserThreadLocal.get() != null ? AuthenticatedUserThreadLocal.get().getName() : null;
        Labelling labelling = new Labelling(label, content, userName);
        content.addLabelling(labelling);
        labelling.setCreationDate(this.now());
        labelling.setLastModificationDate(this.now());
        this.indexIfNecessary(content);
        this.publishEvent(new LabelAddEvent(label, content));
        if (labelCreated) {
            return 2;
        }
        return 1;
    }

    private void publishEvent(Event event) {
        if (this.eventPublisher != null) {
            this.eventPublisher.publish((Object)event);
        } else if (this.eventManager != null) {
            this.eventManager.publishEvent(event);
        }
    }

    private void indexIfNecessary(Labelable content) {
        if (content instanceof Searchable) {
            this.indexSearchableObject((Searchable)content);
        }
    }

    private Date now() {
        return Calendar.getInstance().getTime();
    }

    private int internalRemove(Labelable labelable, Label label, boolean deleteIfUnused) {
        EditableLabelable content = this.convertToEditable(labelable);
        Label persistentLabel = this.dao.findByLabel(label);
        if (persistentLabel == null) {
            return 0;
        }
        label = persistentLabel;
        Labelling labelling = this.dao.findLabellingByContentAndLabel(content, label);
        if (labelling == null) {
            return 0;
        }
        content.removeLabelling(labelling);
        this.dao.remove(labelling);
        this.indexIfNecessary(content);
        this.publishEvent(new LabelRemoveEvent(label, content));
        if (deleteIfUnused && this.getContentCount(label) == 0) {
            this.internalDelete(label);
            return 4;
        }
        return 3;
    }

    @Override
    public int removeLabel(Labelable content, Label label) {
        return this.internalRemove(content, label, true);
    }

    @Override
    public void removeLabels(Labelable content, List labels) {
        for (Object label : labels) {
            this.internalRemove(content, (Label)label, true);
        }
    }

    @Override
    public void removeAllLabels(Labelable content) {
        if (content == null) {
            return;
        }
        for (Label label : content.getLabels()) {
            this.internalRemove(this.convertToEditable(content), label, true);
        }
    }

    private EditableLabelable convertToEditable(Labelable labelable) {
        if (labelable == null || labelable instanceof EditableLabelable) {
            return (EditableLabelable)labelable;
        }
        throw new IllegalArgumentException(String.format("Unsupported type of Labelable, %s does not implement EditableLabelable", labelable.getClass().getName()));
    }

    @Override
    public Label getLabel(long id) {
        return this.dao.findById(id);
    }

    @Override
    public List<Label> getSuggestedLabels(Labelable content) {
        return this.getSuggestedLabels(content, DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<Label> getSuggestedLabels(Labelable content, int count) {
        return this.dao.findBySingleDegreeSeparation(this.convertToEditable(content), count);
    }

    @Override
    public List<Label> getSuggestedLabelsInSpace(Labelable content, String spaceKey) {
        return this.getSuggestedLabelsInSpace(content, spaceKey, DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<Label> getSuggestedLabelsInSpace(Labelable content, String spaceKey, int maxResults) {
        return this.dao.findBySingleDegreeSeparation(this.convertToEditable(content), spaceKey, maxResults);
    }

    @Override
    public List<Label> getRelatedLabels(Label label) {
        return this.getRelatedLabels(label, DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<Label> getRelatedLabels(Label label, int count) {
        if (!label.isPersistent()) {
            label = this.dao.findByLabel(label);
        }
        return this.dao.findBySingleDegreeSeparation(label, count);
    }

    @Override
    public List<Label> getRelatedLabels(List<? extends Label> labels, String spaceKey, int maxResultsPerLabel) {
        HashSet labelSet = Sets.newHashSet();
        for (Label label : labels) {
            List<Label> relatedLabels = StringUtils.isBlank((CharSequence)spaceKey) ? this.getRelatedLabels(label, maxResultsPerLabel) : this.getRelatedLabelsInSpace(label, spaceKey, maxResultsPerLabel);
            labelSet.addAll(relatedLabels);
        }
        labelSet.removeAll(labels);
        return Lists.newArrayList((Iterable)labelSet);
    }

    @Override
    public List<Label> getRelatedLabelsInSpace(Label label, String spaceKey) {
        return this.getRelatedLabelsInSpace(label, spaceKey, DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<Label> getRelatedLabelsInSpace(Label label, String spaceKey, int maxResults) {
        if (!label.isPersistent()) {
            label = this.dao.findByLabel(label);
        }
        return this.dao.findBySingleDegreeSeparation(label, spaceKey, maxResults);
    }

    @Override
    public List<Space> getSpacesContainingContentWithLabel(Label label) {
        if (!label.isPersistent()) {
            label = this.dao.findByLabel(label);
        }
        return this.dao.findSpacesContainingContentWithLabel(label);
    }

    @Override
    public List<Label> getUsersLabels(String name) {
        return this.dao.findByDetails(null, Namespace.PERSONAL.toString(), name);
    }

    @Override
    public List<Label> getTeamLabels() {
        return this.dao.findByDetails(null, Namespace.TEAM.toString(), null);
    }

    @Override
    public List<Label> getTeamLabels(String name) {
        return this.dao.findByDetails(name, Namespace.TEAM.toString(), null);
    }

    @Override
    public List<Label> getTeamLabelsForSpace(String spaceKey) {
        return this.dao.findByDetailsInSpace(null, Namespace.TEAM.toString(), null, spaceKey);
    }

    @Override
    public List<Label> getTeamLabelsForSpaces(Collection<Space> spaces) {
        return this.dao.findByDetailsInSpaces(null, Namespace.TEAM.toString(), null, spaces);
    }

    @Override
    public List<Label> getRecentlyUsedLabelsInSpace(String key) {
        return this.getRecentlyUsedLabelsInSpace(key, DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<Label> getRecentlyUsedLabelsInSpace(String key, int maxResults) {
        return this.distinctLabels(this.dao.findRecentlyUsedBySpace(key, maxResults));
    }

    @Override
    public List<Label> getRecentlyUsedLabels(int maxResults) {
        return this.distinctLabels(this.dao.findRecentlyUsed(maxResults));
    }

    @Override
    public List<Label> getRecentlyUsedLabels() {
        return this.getRecentlyUsedLabels(DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<Label> getRecentlyUsedPersonalLabels(String username) {
        return this.getRecentlyUsedPersonalLabels(username, DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<Label> getRecentlyUsedPersonalLabels(String username, int maxResults) {
        return this.dao.findRecentlyUsedUserLabels(username, maxResults);
    }

    @Override
    public List<Labelling> getRecentlyUsedPersonalLabellings(String username, int maxResults) {
        return this.dao.findRecentlyUsedUserLabellings(username, maxResults);
    }

    @Override
    public List<? extends Labelable> getCurrentContentForLabel(Label label) {
        if (!label.isPersistent()) {
            label = this.dao.findByLabel(label);
        }
        return this.dao.findCurrentContentForLabel(label);
    }

    @Override
    public List<? extends Labelable> getContentForAllLabels(Collection<Label> labels, int maxResults, int offset) {
        return this.getForLabels(ContentEntityObject.class, offset, maxResults, this.reattachLabels(labels.toArray(new Label[0]))).getList();
    }

    private Label[] reattachLabels(Label ... labels) {
        ArrayList<Label> persistent = new ArrayList<Label>(labels.length);
        for (Label label : labels) {
            if (!label.isPersistent()) {
                persistent.add(this.dao.findByLabel(label));
                continue;
            }
            persistent.add(label);
        }
        return persistent.toArray(new Label[0]);
    }

    @Override
    public PartialList<ContentEntityObject> getContentForLabel(int offset, int maxResults, Label label) {
        return this.getForLabels(ContentEntityObject.class, offset, maxResults, label);
    }

    @Override
    public PartialList<ContentEntityObject> getContentForAllLabels(int offset, int maxResults, Label ... labels) {
        return this.getForLabels(ContentEntityObject.class, offset, maxResults, labels);
    }

    @Override
    public <T extends EditableLabelable> PartialList<T> getForLabel(Class<T> labelableClass, int offset, int maxResults, Label label) {
        return this.getForLabels(labelableClass, offset, maxResults, label);
    }

    @Override
    public <T extends EditableLabelable> PartialList<T> getForLabels(Class<T> labelableClass, int offset, int maxResults, Label ... labels) {
        return this.dao.findForAllLabels(labelableClass, offset, maxResults, this.reattachLabels(labels));
    }

    @Override
    public PartialList<EditableLabelable> getForLabels(int offset, int maxResults, Label ... labels) {
        return this.dao.findForAllLabels(offset, maxResults, this.reattachLabels(labels));
    }

    @Override
    public PartialList<ContentEntityObject> getContentInSpaceForLabel(int offset, int maxResults, String spaceKey, Label label) {
        return this.getContentInSpaceForAllLabels(offset, maxResults, spaceKey, label);
    }

    @Override
    public PartialList<ContentEntityObject> getContentInSpaceForAllLabels(int offset, int maxResults, String spaceKey, Label ... labels) {
        return this.dao.findContentInSpaceForAllLabels(offset, maxResults, spaceKey, labels);
    }

    @Override
    public PartialList<ContentEntityObject> getContentInSpacesForAllLabels(int offset, int maxResults, Set<String> spaceKeys, Label ... labels) {
        return this.dao.findContentInSpacesForAllLabels(offset, maxResults, spaceKeys, this.reattachLabels(labels));
    }

    @Override
    public PartialList<ContentEntityObject> getAllContentForLabel(int offset, int maxResults, Label label) {
        return this.getAllContentForAllLabels(offset, maxResults, label);
    }

    @Override
    public PartialList<ContentEntityObject> getAllContentForAllLabels(int offset, int maxResults, Label ... labels) {
        return this.dao.findAllContentForAllLabels(offset, maxResults, this.reattachLabels(labels));
    }

    @Override
    public List<? extends Labelable> getCurrentContentForLabelAndSpace(Label label, String spaceKey) {
        if (!label.isPersistent()) {
            label = this.dao.findByLabel(label);
        }
        return this.dao.findContentInSpaceForAllLabels(0, -1, spaceKey, label).getList();
    }

    @Override
    public List<? extends Labelable> getCurrentContentWithPersonalLabel(String username) {
        return this.dao.findAllUserLabelledContent(username);
    }

    @Override
    public List<Space> getSpacesWithLabel(Label label) {
        if (!label.isPersistent()) {
            label = this.dao.findByLabel(label);
        }
        return this.dao.findSpacesWithLabel(label);
    }

    @Override
    public boolean deleteLabel(long id) {
        return this.deleteLabel(this.getLabel(id));
    }

    @Override
    public List<Space> getFavouriteSpaces(String username) {
        return this.dao.getFavouriteSpaces(username);
    }

    @Override
    public List<Labelling> getFavouriteLabellingsByContentIds(Collection<ContentId> contentIds, UserKey userKey) {
        return this.dao.getFavouriteLabellingsByContentIds(Collections2.transform(contentIds, ContentId::asLong), userKey);
    }

    @Override
    public boolean deleteLabel(Label label) {
        if (label == null) {
            return false;
        }
        if ((label = this.getLabel(label)) == null) {
            return false;
        }
        for (ContentEntityObject content : this.getContent(label)) {
            this.indexSearchableObject(content);
            this.internalRemove(content, label, false);
        }
        return this.internalDelete(label);
    }

    private boolean internalDelete(Label label) {
        this.dao.remove(label);
        this.publishEvent(new LabelDeleteEvent(label));
        return true;
    }

    @Override
    public Label getLabel(ParsedLabelName parsedLabelName) {
        if (parsedLabelName == null) {
            return null;
        }
        return this.getLabel(parsedLabelName.toLabel());
    }

    @Override
    public Label getLabel(String unparsedLabelName) {
        return this.getLabel(LabelParser.parse(unparsedLabelName));
    }

    @Override
    public List<Label> getLabels(Collection<String> unparsedLabelNames) {
        ArrayList<Label> labelList = new ArrayList<Label>();
        for (String term : unparsedLabelNames) {
            ParsedLabelName parsedLabelName = LabelParser.parse(term = term.trim());
            Label label = this.getLabel(parsedLabelName);
            if (label == null) continue;
            labelList.add(label);
        }
        return labelList;
    }

    @Override
    public Label getLabel(Label label) {
        return this.dao.findByLabel(label);
    }

    @Override
    public Label getLabel(String labelName, Namespace namespace) {
        return this.getLabel(new Label(labelName, namespace));
    }

    @Override
    public List<Label> getLabelsByDetail(String labelName, String namespace, String spaceKey, String owner) {
        return this.dao.findByDetailsInSpace(labelName, namespace, owner, spaceKey);
    }

    @Override
    public List<Label> getLabelsInSpace(String key) {
        return this.dao.findBySpace(key, Namespace.GLOBAL.toString());
    }

    @Override
    public List<Label> getLabelsInSpace(String key, LimitedRequest limitedRequest) {
        return this.labelDaoInternal.findByDetailsInSpace(null, Namespace.GLOBAL.toString(), null, key, limitedRequest);
    }

    @Override
    public long getTotalLabelInSpace(String key) {
        return this.labelDaoInternal.getTotalLabelInSpace(null, Namespace.GLOBAL.toString(), null, key);
    }

    @Override
    public List<LabelSearchResult> getMostPopularLabels() {
        return this.getMostPopularLabels(DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<LabelSearchResult> getMostPopularLabels(int count) {
        long startTime = System.currentTimeMillis();
        List<LabelSearchResult> searchResultList = this.dao.findMostPopular(Namespace.GLOBAL.toString(), count);
        this.sendMetricsAnalyticsInfo("getMostPopularLabels", startTime, count, searchResultList.size());
        return searchResultList;
    }

    @Override
    public List<LabelSearchResult> getMostPopularLabelsInSpace(String key) {
        return this.getMostPopularLabelsInSpace(key, DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<LabelSearchResult> getMostPopularLabelsInSpace(String key, int count) {
        long startTime = System.currentTimeMillis();
        List<LabelSearchResult> searchResultList = this.dao.findMostPopularBySpace(Namespace.GLOBAL.toString(), key, count);
        this.sendMetricsAnalyticsInfo("getMostPopularLabelsInSpace", startTime, count, searchResultList.size());
        return searchResultList;
    }

    @Override
    public Set<RankedLabelSearchResult> getMostPopularLabelsWithRanks(Comparator<? super RankedLabelSearchResult> comparator) {
        return this.getMostPopularLabelsWithRanks(DEFAULT_LABEL_COUNT, comparator);
    }

    @Override
    public Set<RankedLabelSearchResult> getMostPopularLabelsWithRanks(int maxResults, Comparator<? super RankedLabelSearchResult> comparator) {
        return this.getRankedLabels(this.getMostPopularLabels(maxResults), comparator);
    }

    @Override
    public Set<RankedLabelSearchResult> getMostPopularLabelsWithRanksInSpace(String key, int maxResults, Comparator<? super RankedLabelSearchResult> comparator) {
        return this.getRankedLabels(this.getMostPopularLabelsInSpace(key, maxResults), comparator);
    }

    private Set<RankedLabelSearchResult> getRankedLabels(List<LabelSearchResult> popularLabels, Comparator<? super RankedLabelSearchResult> comparator) {
        Map<Integer, List<LabelSearchResult>> buckets = this.getBuckets(popularLabels);
        TreeSet<RankedLabelSearchResult> results = new TreeSet<RankedLabelSearchResult>(comparator);
        buckets.forEach((bucket, values) -> {
            for (LabelSearchResult lsr : values) {
                results.add(new RankedLabelSearchResult(lsr, (int)bucket));
            }
        });
        return results;
    }

    private <Countable extends CountableLabel> Map<Integer, List<Countable>> getFreqMap(List<Countable> popularLabels) {
        TreeMap freqMap = Maps.newTreeMap();
        for (CountableLabel labelSearchResult : popularLabels) {
            int count = labelSearchResult.getCount();
            List values = (List)freqMap.get(count);
            if (values == null) {
                values = Lists.newLinkedList();
            }
            values.add(labelSearchResult);
            freqMap.put(count, values);
        }
        return freqMap;
    }

    private <Countable extends CountableLabel> Map<Integer, List<Countable>> getBuckets(List<Countable> popularLabels) {
        int bucket = 1;
        int cumulativeSum = 0;
        int totalSize = 0;
        Map<Integer, List<Countable>> freqMap = this.getFreqMap(popularLabels);
        for (Integer frequency : freqMap.keySet()) {
            totalSize += frequency.intValue();
        }
        TreeMap buckets = Maps.newTreeMap();
        for (Map.Entry<Integer, List<Countable>> entry : freqMap.entrySet()) {
            float bucketBorder;
            Integer currentFreq = entry.getKey();
            List<Countable> freqValues = entry.getValue();
            float currentPosition = (float)(cumulativeSum += currentFreq.intValue()) / (float)totalSize;
            for (int i = 0; i < bucketBorders.length && currentPosition > (bucketBorder = bucketBorders[i]); ++i) {
                bucket = i + 1;
            }
            List bucketValues = (List)buckets.get(bucket);
            if (bucketValues == null) {
                bucketValues = Lists.newLinkedList();
            }
            bucketValues.addAll(freqValues);
            buckets.put(bucket, bucketValues);
            bucket = 1;
        }
        return buckets;
    }

    private void indexSearchableObject(Searchable object) {
        if (this.indexer != null) {
            this.indexer.reIndex(object);
        } else if (this.eventPublisher != null) {
            new IndexerEventPublisher(this.eventPublisher).publishCallbackEvent(indexer -> indexer.reIndex(object));
        }
    }

    private List<Label> distinctLabels(List<Label> objects) {
        return Lists.newArrayList((Iterable)Sets.newLinkedHashSet(objects));
    }

    @Override
    public List getContent(Label label) {
        return this.dao.findContentForLabel(label, -1);
    }

    @Override
    public int getContentCount(Label label) {
        return this.dao.findContentCountForLabel(label);
    }

    @Override
    public Label createLabel(Label label) {
        DefaultLabelManager.validateLabel(label);
        this.dao.save(label);
        return label;
    }

    @Override
    public List<Labelling> getRecentlyUsedLabellings(int maxResults) {
        return this.dao.findRecentlyUsedLabelling(maxResults);
    }

    @Override
    public List<Labelling> getRecentlyUsedLabellingsInSpace(String spaceKey, int maxResults) {
        return this.dao.findRecentlyUsedLabellingsBySpace(spaceKey, maxResults);
    }

    @Override
    public PageResponse<Label> findGlobalLabelsByNamePrefix(String namePrefix, LimitedRequest pageRequest) {
        return this.dao.findGlobalLabelsByNamePrefix(namePrefix, pageRequest);
    }

    @Override
    public PageResponse<Label> findTeamLabelsByNamePrefix(String namePrefix, LimitedRequest pageRequest) {
        return this.dao.findTeamLabelsByNamePrefix(namePrefix, pageRequest);
    }

    @Override
    public List<LiteLabelSearchResult> getMostPopularLabelsInSpaceLite(String spaceKey, int maxResults) {
        return this.getMostPopularLabelsInSpace(spaceKey, maxResults).stream().map(LiteLabelSearchResult::new).collect(Collectors.toList());
    }

    @Override
    public List<LiteLabelSearchResult> getMostPopularLabelsInSiteLite(int maxResults) {
        return this.getMostPopularLabels(maxResults).stream().map(LiteLabelSearchResult::new).collect(Collectors.toList());
    }

    @Override
    public Set<RankedLiteLabelSearchResult> calculateRanksForLiteLabels(List<LiteLabelSearchResult> labelList, Comparator<? super RankedLiteLabelSearchResult> comparator) {
        Map<Integer, List<LiteLabelSearchResult>> buckets = this.getBuckets(labelList);
        TreeSet<RankedLiteLabelSearchResult> results = new TreeSet<RankedLiteLabelSearchResult>(comparator);
        buckets.forEach((bucketNumber, values) -> {
            for (LiteLabelSearchResult lsr : values) {
                results.add(new RankedLiteLabelSearchResult(lsr, (int)bucketNumber));
            }
        });
        return results;
    }

    private static void validateLabel(Label label) {
        if (label.getName() != null && !LabelUtil.isValidLabelName(label.getName())) {
            throw new IllegalArgumentException("Invalid label '" + label.getName() + "'");
        }
    }

    private void sendMetricsAnalyticsInfo(String methodName, long startTime, int maxResults, int responseSize) {
        long duration = System.currentTimeMillis() - startTime;
        this.publishEvent(new LabelManagerMetricsEvent(this, methodName, duration, maxResults, responseSize));
    }
}

