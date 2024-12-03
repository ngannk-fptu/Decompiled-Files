/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.GroupDefinitionException
 */
package org.hibernate.validator.internal.engine.groups;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.validation.GroupDefinitionException;
import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.engine.groups.Sequence;
import org.hibernate.validator.internal.engine.groups.ValidationOrder;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public final class DefaultValidationOrder
implements ValidationOrder {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private List<Group> groupList;
    private Map<Class<?>, Sequence> sequenceMap;

    @Override
    public Iterator<Group> getGroupIterator() {
        if (this.groupList == null) {
            return Collections.emptyIterator();
        }
        return this.groupList.iterator();
    }

    @Override
    public Iterator<Sequence> getSequenceIterator() {
        if (this.sequenceMap == null) {
            return Collections.emptyIterator();
        }
        return this.sequenceMap.values().iterator();
    }

    public void insertGroup(Group group) {
        if (this.groupList == null) {
            this.groupList = new ArrayList<Group>(5);
        }
        if (!this.groupList.contains(group)) {
            this.groupList.add(group);
        }
    }

    public void insertSequence(Sequence sequence) {
        if (sequence == null) {
            return;
        }
        if (this.sequenceMap == null) {
            this.sequenceMap = CollectionHelper.newHashMap(5);
        }
        this.sequenceMap.putIfAbsent(sequence.getDefiningClass(), sequence);
    }

    public String toString() {
        return "ValidationOrder{groupList=" + this.groupList + ", sequenceMap=" + this.sequenceMap + '}';
    }

    @Override
    public void assertDefaultGroupSequenceIsExpandable(List<Class<?>> defaultGroupSequence) throws GroupDefinitionException {
        if (this.sequenceMap == null) {
            return;
        }
        for (Map.Entry<Class<?>, Sequence> entry : this.sequenceMap.entrySet()) {
            List<Group> sequenceGroups = entry.getValue().getComposingGroups();
            int defaultGroupIndex = sequenceGroups.indexOf(Group.DEFAULT_GROUP);
            if (defaultGroupIndex == -1) continue;
            List<Group> defaultGroupList = this.buildTempGroupList(defaultGroupSequence);
            this.ensureDefaultGroupSequenceIsExpandable(sequenceGroups, defaultGroupList, defaultGroupIndex);
        }
    }

    private void ensureDefaultGroupSequenceIsExpandable(List<Group> groupList, List<Group> defaultGroupList, int defaultGroupIndex) {
        for (int i = 0; i < defaultGroupList.size(); ++i) {
            int index;
            Group group = defaultGroupList.get(i);
            if (Group.DEFAULT_GROUP.equals(group) || (index = groupList.indexOf(group)) == -1 || i == 0 && index == defaultGroupIndex - 1 || i == defaultGroupList.size() - 1 && index == defaultGroupIndex + 1) continue;
            throw LOG.getUnableToExpandDefaultGroupListException(defaultGroupList, groupList);
        }
    }

    private List<Group> buildTempGroupList(List<Class<?>> defaultGroupSequence) {
        ArrayList<Group> groups = new ArrayList<Group>();
        for (Class<?> clazz : defaultGroupSequence) {
            Group g = new Group(clazz);
            groups.add(g);
        }
        return groups;
    }
}

