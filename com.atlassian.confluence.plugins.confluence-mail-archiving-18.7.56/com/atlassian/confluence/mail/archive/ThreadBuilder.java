/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.mail.archive.ThreadNode;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadBuilder {
    private static final Logger log = LoggerFactory.getLogger(ThreadBuilder.class);
    private static final Integer THREAD_SIZE_LIMIT = Integer.getInteger("confluence.mail.archive.thread.limit", 1000);
    private static final ImmutableSet<String> REQUIRED_FIELDS = ImmutableSet.of((Object)SearchFieldNames.HANDLE, (Object)SearchFieldNames.TITLE, (Object)"canonicalsubject", (Object)"references", (Object)"messageid", (Object)"from", (Object[])new String[]{"created"});
    private final SearchManager searchManager;
    private final Stack<String> toLookUp = new Stack();
    private final Set<String> lookedUp = new HashSet<String>();
    private final Map<String, SearchResult> messages = new HashMap<String, SearchResult>();
    private final Map<String, SearchResult> messagesCache = new HashMap<String, SearchResult>();
    private String spaceKey;
    private boolean called;
    private final Set<String> searchedSubjects = new HashSet<String>();
    private String originalMessageId;

    public ThreadBuilder(SearchManager searchManager) {
        this.searchManager = Objects.requireNonNull(searchManager);
    }

    public ThreadNode buildThreadAround(String spaceKey, String messageId) {
        this.checkNotAlreadyUsed();
        this.originalMessageId = messageId;
        UtilTimerStack.push((String)("ThreadBuilder: " + messageId));
        this.spaceKey = spaceKey;
        this.findAllMessagesRelatedTo(messageId);
        ThreadNode returnValue = this.buildThreadFromMessages(this.messages);
        UtilTimerStack.pop((String)("ThreadBuilder: " + messageId));
        return returnValue;
    }

    private void findAllMessagesRelatedTo(String messageId) {
        log.debug("Finding related mail to {}", (Object)messageId);
        this.toLookUp.add(messageId);
        while (!this.toLookUp.isEmpty()) {
            this.processNextId();
        }
    }

    private void processNextId() {
        String messageId = this.toLookUp.pop();
        this.addThisMessage(messageId);
        if (this.isEnoughMessagesInQueue()) {
            return;
        }
        this.addMessagesReferencingThisToQueue(messageId);
    }

    private boolean isEnoughMessagesInQueue() {
        return this.messages.size() + this.toLookUp.size() >= THREAD_SIZE_LIMIT;
    }

    private void addMessagesReferencingThisToQueue(String messageId) {
        for (SearchResult result : this.getMailQuerySearchResults(new TermQuery("references", messageId))) {
            String referencingMessageId = result.getField("messageid");
            this.messagesCache.put(referencingMessageId, result);
            this.addIdToSearchList(referencingMessageId);
        }
    }

    private SearchResults getMailQuerySearchResults(TermQuery termQuery) {
        SearchQuery mailQuery = (SearchQuery)BooleanQuery.builder().addMust((Object)new TermQuery(SearchFieldNames.SPACE_KEY, this.spaceKey)).addMust((Object)new TermQuery(SearchFieldNames.CLASS_NAME, CustomContentEntityObject.class.getName())).addMust((Object)new TermQuery(SearchFieldNames.CONTENT_PLUGIN_KEY, "com.atlassian.confluence.plugins.confluence-mail-archiving:mail")).addMust((Object)termQuery).build();
        ContentSearch mailSearch = new ContentSearch(mailQuery, null);
        try {
            return this.searchManager.search((ISearch)mailSearch, REQUIRED_FIELDS);
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException(e);
        }
    }

    private ThreadNode buildThreadFromMessages(Map<String, SearchResult> messages) {
        log.debug("Trying to build tree based on references. |messages| = {}", (Object)messages.size());
        Map<String, ThreadNode> nodesById = this.toNodeMap(messages);
        messages.forEach((messageId, result) -> {
            Set references = result.getFieldValues("references");
            if (!references.isEmpty()) {
                this.linkReferencedMessages(nodesById, (String)messageId, references.toArray(new String[0]));
            }
        });
        return this.makeSingleRootedThread(nodesById);
    }

    private ThreadNode makeSingleRootedThread(Map<String, ThreadNode> nodesById) {
        List<ThreadNode> orphanNodes = this.getOrphanNodes(nodesById);
        if (orphanNodes.size() > 1) {
            this.cleanOrphanNodesBySubject(nodesById.values(), orphanNodes);
            log.debug("{} orphaned nodes left after trying to link them by subject", (Object)orphanNodes.size());
        } else {
            log.debug("No orphaned nodes has been found");
        }
        ThreadNode rootNode = orphanNodes.stream().filter(node -> node.getNodeWithMessageId(this.originalMessageId) != null).findFirst().orElse(ThreadNode.getEmptyThreadNode());
        log.debug("Tree of {} nodes has been completely built", (Object)nodesById.size());
        return rootNode;
    }

    private void cleanOrphanNodesBySubject(Collection<ThreadNode> allNodes, List<ThreadNode> orphanNodes) {
        log.debug("Building tree based on subject. |allNodes|={}, |orphanNodes|={}", (Object)allNodes.size(), (Object)orphanNodes.size());
        Iterator<ThreadNode> it = orphanNodes.iterator();
        while (it.hasNext()) {
            ThreadNode threadNode = it.next();
            ThreadNode parent = this.findPossibleParentBySubject(threadNode, allNodes);
            if (parent == null) continue;
            threadNode.setParent(parent);
            if (threadNode.getParent() == null) continue;
            it.remove();
        }
    }

    private ThreadNode findPossibleParentBySubject(ThreadNode orphanedNode, Collection<ThreadNode> allNodes) {
        return allNodes.stream().filter(potentialParent -> potentialParent != orphanedNode && StringUtils.isNotBlank((CharSequence)orphanedNode.getCanonicalSubject()) && orphanedNode.getCanonicalSubject().equals(potentialParent.getTitle())).findFirst().orElse(null);
    }

    private void linkReferencedMessages(Map<String, ThreadNode> nodesById, String messageId, String[] references) {
        if (nodesById.containsKey(references[references.length - 1])) {
            nodesById.get(messageId).setParent(nodesById.get(references[references.length - 1]));
        } else if (references.length > 1) {
            for (int i = references.length - 2; i >= 0; --i) {
                if (!nodesById.containsKey(references[i])) continue;
                ThreadNode dummyParent = ThreadNode.getEmptyThreadNode();
                nodesById.get(messageId).setParent(dummyParent);
                dummyParent.setParent(nodesById.get(references[references.length - 1]));
                break;
            }
        }
    }

    private Map<String, ThreadNode> toNodeMap(Map<String, SearchResult> searchResults) {
        return searchResults.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ThreadNode(this.toId(((SearchResult)entry.getValue()).getField(SearchFieldNames.HANDLE)), (SearchResult)entry.getValue())));
    }

    private void addThisMessage(String messageId) {
        this.lookedUp.add(messageId);
        if (this.messagesCache.containsKey(messageId)) {
            this.addThisMessage(messageId, this.messagesCache.get(messageId));
        } else {
            this.searchAndAddThisMessage(messageId);
        }
    }

    private void searchAndAddThisMessage(String messageId) {
        for (SearchResult result : this.getMailQuerySearchResults(new TermQuery("messageid", messageId))) {
            this.addThisMessage(messageId, result);
        }
    }

    private void addThisMessage(String messageId, SearchResult searchResult) {
        this.messages.put(messageId, searchResult);
        if (this.isEnoughMessagesInQueue()) {
            return;
        }
        this.addReferencedMessages(searchResult);
        String canonicalSubject = searchResult.getField("canonicalsubject");
        if (StringUtils.isNotBlank((CharSequence)canonicalSubject)) {
            this.addMessagesWithSameSubject(canonicalSubject);
        }
    }

    private void addMessagesWithSameSubject(String canonicalSubject) {
        if (!this.searchedSubjects.contains(canonicalSubject)) {
            this.searchedSubjects.add(canonicalSubject);
            for (SearchResult result : this.getMailQuerySearchResults(new TermQuery("canonicalsubject", canonicalSubject))) {
                this.messagesCache.put(result.getField("messageid"), result);
                this.addIdToSearchList(result.getField("messageid"));
            }
        }
    }

    private void addReferencedMessages(SearchResult searchResult) {
        Set referencedMessages = searchResult.getFieldValues("references");
        if (!referencedMessages.isEmpty()) {
            for (String referenceId : referencedMessages) {
                this.addIdToSearchList(referenceId);
            }
        }
    }

    private long toId(String value) {
        return Long.parseLong(value.substring(CustomContentEntityObject.class.getName().length() + 1));
    }

    private void addIdToSearchList(String messageId) {
        if (!this.toLookUp.contains(messageId) && !this.lookedUp.contains(messageId) && this.toLookUp.size() + this.messages.size() < THREAD_SIZE_LIMIT) {
            this.toLookUp.add(messageId);
        }
    }

    private List<ThreadNode> getOrphanNodes(Map<String, ThreadNode> nodesById) {
        return nodesById.values().stream().filter(node -> node.getParent() == null).collect(Collectors.toList());
    }

    private void checkNotAlreadyUsed() {
        if (this.called) {
            throw new IllegalStateException("Attempting to re-use a single-use object");
        }
        this.called = true;
    }
}

