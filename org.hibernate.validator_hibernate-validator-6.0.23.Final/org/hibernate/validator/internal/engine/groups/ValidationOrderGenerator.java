/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.GroupSequence
 *  javax.validation.groups.Default
 */
package org.hibernate.validator.internal.engine.groups;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.validation.GroupSequence;
import javax.validation.groups.Default;
import org.hibernate.validator.internal.engine.groups.DefaultValidationOrder;
import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.engine.groups.Sequence;
import org.hibernate.validator.internal.engine.groups.ValidationOrder;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class ValidationOrderGenerator {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final ConcurrentMap<Class<?>, Sequence> resolvedSequences = new ConcurrentHashMap();

    public ValidationOrder getValidationOrder(Class<?> group, boolean expand) {
        if (Default.class.equals(group)) {
            return ValidationOrder.DEFAULT_GROUP;
        }
        if (expand) {
            return this.getValidationOrder(Collections.singletonList(group));
        }
        DefaultValidationOrder validationOrder = new DefaultValidationOrder();
        validationOrder.insertGroup(new Group(group));
        return validationOrder;
    }

    public ValidationOrder getValidationOrder(Collection<Class<?>> groups) {
        if (groups == null || groups.size() == 0) {
            throw LOG.getAtLeastOneGroupHasToBeSpecifiedException();
        }
        if (groups.size() == 1 && groups.contains(Default.class)) {
            return ValidationOrder.DEFAULT_GROUP;
        }
        for (Class<?> clazz : groups) {
            if (clazz.isInterface()) continue;
            throw LOG.getGroupHasToBeAnInterfaceException(clazz);
        }
        DefaultValidationOrder validationOrder = new DefaultValidationOrder();
        for (Class<?> clazz : groups) {
            if (Default.class.equals(clazz)) {
                validationOrder.insertGroup(Group.DEFAULT_GROUP);
                continue;
            }
            if (this.isGroupSequence(clazz)) {
                this.insertSequence(clazz, clazz.getAnnotation(GroupSequence.class).value(), true, validationOrder);
                continue;
            }
            Group group = new Group(clazz);
            validationOrder.insertGroup(group);
            this.insertInheritedGroups(clazz, validationOrder);
        }
        return validationOrder;
    }

    public ValidationOrder getDefaultValidationOrder(Class<?> clazz, List<Class<?>> defaultGroupSequence) {
        DefaultValidationOrder validationOrder = new DefaultValidationOrder();
        this.insertSequence(clazz, defaultGroupSequence.toArray(new Class[defaultGroupSequence.size()]), false, validationOrder);
        return validationOrder;
    }

    private boolean isGroupSequence(Class<?> clazz) {
        return clazz.getAnnotation(GroupSequence.class) != null;
    }

    private void insertInheritedGroups(Class<?> clazz, DefaultValidationOrder chain) {
        for (Class<?> inheritedGroup : clazz.getInterfaces()) {
            Group group = new Group(inheritedGroup);
            chain.insertGroup(group);
            this.insertInheritedGroups(inheritedGroup, chain);
        }
    }

    private void insertSequence(Class<?> sequenceClass, Class<?>[] sequenceElements, boolean cache, DefaultValidationOrder validationOrder) {
        Sequence sequence;
        Sequence sequence2 = sequence = cache ? (Sequence)this.resolvedSequences.get(sequenceClass) : null;
        if (sequence == null) {
            Sequence cachedResolvedSequence;
            sequence = this.resolveSequence(sequenceClass, sequenceElements, new ArrayList());
            sequence.expandInheritedGroups();
            if (cache && (cachedResolvedSequence = this.resolvedSequences.putIfAbsent(sequenceClass, sequence)) != null) {
                sequence = cachedResolvedSequence;
            }
        }
        validationOrder.insertSequence(sequence);
    }

    private Sequence resolveSequence(Class<?> sequenceClass, Class<?>[] sequenceElements, List<Class<?>> processedSequences) {
        if (processedSequences.contains(sequenceClass)) {
            throw LOG.getCyclicDependencyInGroupsDefinitionException();
        }
        processedSequences.add(sequenceClass);
        ArrayList<Group> resolvedSequenceGroups = new ArrayList<Group>();
        for (Class<?> clazz : sequenceElements) {
            if (this.isGroupSequence(clazz)) {
                Sequence tmpSequence = this.resolveSequence(clazz, clazz.getAnnotation(GroupSequence.class).value(), processedSequences);
                this.addGroups(resolvedSequenceGroups, tmpSequence.getComposingGroups());
                continue;
            }
            ArrayList<Group> list = new ArrayList<Group>();
            list.add(new Group(clazz));
            this.addGroups(resolvedSequenceGroups, list);
        }
        return new Sequence(sequenceClass, resolvedSequenceGroups);
    }

    private void addGroups(List<Group> resolvedGroupSequence, List<Group> groups) {
        for (Group tmpGroup : groups) {
            if (resolvedGroupSequence.contains(tmpGroup) && resolvedGroupSequence.indexOf(tmpGroup) < resolvedGroupSequence.size() - 1) {
                throw LOG.getUnableToExpandGroupSequenceException();
            }
            resolvedGroupSequence.add(tmpGroup);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationOrderGenerator");
        sb.append("{resolvedSequences=").append(this.resolvedSequences);
        sb.append('}');
        return sb.toString();
    }
}

