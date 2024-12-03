/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.content.duplicatetags.internal;

import com.atlassian.confluence.impl.content.duplicatetags.internal.Node;
import com.atlassian.confluence.impl.content.duplicatetags.internal.SingleRootTreeData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

class SingleXmlBranchDuplicateAnalyser {
    private final Set<String> tagsAllowedForRemoval;
    private final int maxGroupSizeOfRepetitiveTags;
    private final int minNestedLevelToAnalyse;

    public SingleXmlBranchDuplicateAnalyser(Set<String> tagsAllowedForRemoval, int maxGroupSizeOfRepetitiveTags, int minNestedLevelToAnalyse) {
        this.tagsAllowedForRemoval = tagsAllowedForRemoval;
        this.maxGroupSizeOfRepetitiveTags = maxGroupSizeOfRepetitiveTags;
        this.minNestedLevelToAnalyse = minNestedLevelToAnalyse;
    }

    List<XMLEvent> getAllNotDuplicateTags(SingleRootTreeData treeData) {
        Set<Integer> duplicateTagIndexes = this.findDuplicateTagIndexes(treeData);
        return this.getAllTagsNotMarkedAsDuplicateTags(duplicateTagIndexes, treeData.getAllXmlEvents());
    }

    private List<XMLEvent> getAllTagsNotMarkedAsDuplicateTags(Set<Integer> duplicateTagIndexes, List<XMLEvent> allXmlEvents) {
        ArrayList<XMLEvent> eventsWithoutDuplicates = new ArrayList<XMLEvent>(allXmlEvents.size() - duplicateTagIndexes.size());
        for (int i = 0; i < allXmlEvents.size(); ++i) {
            if (duplicateTagIndexes.contains(i)) continue;
            eventsWithoutDuplicates.add(allXmlEvents.get(i));
        }
        return eventsWithoutDuplicates;
    }

    private Set<Integer> findDuplicateTagIndexes(SingleRootTreeData treeData) {
        HashSet<Integer> allDuplicateNodesToRemove = new HashSet<Integer>();
        LinkedList<Node> nodesToProcess = new LinkedList<Node>(Collections.singleton(treeData.getTopLevelNode()));
        while (!nodesToProcess.isEmpty()) {
            Node nodeToProcess = (Node)nodesToProcess.remove(0);
            allDuplicateNodesToRemove.addAll(this.findDuplicateTagIndexes(nodeToProcess));
            nodesToProcess.addAll(nodeToProcess.children);
        }
        return allDuplicateNodesToRemove;
    }

    private Set<Integer> findDuplicateTagIndexes(Node nodeToProcess) {
        if (!this.isTagAllowedToBeRemoved(nodeToProcess) || nodeToProcess.getEffectiveChildren().size() != 1) {
            return Collections.emptySet();
        }
        for (int i = 1; i <= this.maxGroupSizeOfRepetitiveTags; ++i) {
            AtomicReference<Node> referenceToNode = new AtomicReference<Node>(nodeToProcess);
            Set<Integer> duplicateIndexes = this.findDuplicateTagIndexesForTheParticularGroupSize(referenceToNode, i);
            if (duplicateIndexes.isEmpty()) continue;
            return duplicateIndexes;
        }
        return Collections.emptySet();
    }

    private List<Node> getNextGroupOfElementsAllowedToBeRemoved(AtomicReference<Node> referenceToCurrentNode, int groupSize, List<Node> previousNodeGroup) {
        ArrayList<Node> sequenceOfSingleNestedDocuments = new ArrayList<Node>();
        if (!previousNodeGroup.isEmpty() && previousNodeGroup.get(groupSize - 1).getEffectiveChildren().size() != 1) {
            return Collections.emptyList();
        }
        for (int i = 0; i < groupSize; ++i) {
            Node node = referenceToCurrentNode.get();
            if (node == null) {
                return Collections.emptyList();
            }
            if (node.markedAsDuplicate) {
                return Collections.emptyList();
            }
            if (!this.isTagAllowedToBeRemoved(node) && !node.isEmptyTag) {
                return Collections.emptyList();
            }
            if (i < groupSize - 1 && node.getEffectiveChildren().size() != 1) {
                return Collections.emptyList();
            }
            sequenceOfSingleNestedDocuments.add(node);
            referenceToCurrentNode.set(node.getEffectiveChildren().get(0));
        }
        return sequenceOfSingleNestedDocuments;
    }

    Set<Integer> findDuplicateTagIndexesForTheParticularGroupSize(AtomicReference<Node> referenceToCurrentNode, int groupSize) {
        List<Node> firstGroupOfNodes = this.getNextGroupOfElementsAllowedToBeRemoved(referenceToCurrentNode, groupSize, Collections.emptyList());
        if (firstGroupOfNodes.isEmpty()) {
            return Collections.emptySet();
        }
        List<Node> previousGroupOfTags = firstGroupOfNodes;
        int numberOfDuplicateGroups = 1;
        HashSet<Node> allDuplicates = new HashSet<Node>();
        while (true) {
            List<Node> potentialDuplicateTags;
            if ((potentialDuplicateTags = this.getNextGroupOfElementsAllowedToBeRemoved(referenceToCurrentNode, groupSize, previousGroupOfTags)).isEmpty() || !this.areGroupsEqualToEachOther(firstGroupOfNodes, potentialDuplicateTags)) {
                if (numberOfDuplicateGroups < this.minNestedLevelToAnalyse) {
                    return Collections.emptySet();
                }
                allDuplicates.forEach(node -> {
                    node.markedAsDuplicate = true;
                });
                return allDuplicates.stream().flatMap(duplicateNode -> Stream.of(duplicateNode.tagIndex, duplicateNode.closingTag.tagIndex)).collect(Collectors.toSet());
            }
            allDuplicates.addAll(potentialDuplicateTags);
            ++numberOfDuplicateGroups;
            previousGroupOfTags = potentialDuplicateTags;
        }
    }

    private boolean areGroupsEqualToEachOther(List<Node> group1, List<Node> group2) {
        if (group1.size() == 0 || group1.size() != group2.size()) {
            throw new IllegalStateException("Two arrays are empty or have different sizes");
        }
        for (int i = 0; i < group1.size(); ++i) {
            if (group1.get((int)i).elementHash == group2.get((int)i).elementHash) continue;
            return false;
        }
        return true;
    }

    private boolean isTagAllowedToBeRemoved(Node child) {
        return !StringUtils.isEmpty((CharSequence)child.tagName) && this.tagsAllowedForRemoval.contains(child.tagName);
    }
}

