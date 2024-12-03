/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.GroupDefinitionException
 */
package org.hibernate.validator.internal.engine.groups;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.validation.GroupDefinitionException;
import org.hibernate.validator.internal.engine.groups.Group;
import org.hibernate.validator.internal.engine.groups.Sequence;

public interface ValidationOrder {
    public static final ValidationOrder DEFAULT_GROUP = new DefaultGroupValidationOrder();
    public static final ValidationOrder DEFAULT_SEQUENCE = new DefaultSequenceValidationOrder();

    public Iterator<Group> getGroupIterator();

    public Iterator<Sequence> getSequenceIterator();

    public void assertDefaultGroupSequenceIsExpandable(List<Class<?>> var1) throws GroupDefinitionException;

    public static class DefaultGroupValidationOrder
    implements ValidationOrder {
        private final List<Group> defaultGroups = Collections.singletonList(Group.DEFAULT_GROUP);

        private DefaultGroupValidationOrder() {
        }

        @Override
        public Iterator<Group> getGroupIterator() {
            return this.defaultGroups.iterator();
        }

        @Override
        public Iterator<Sequence> getSequenceIterator() {
            return Collections.emptyIterator();
        }

        @Override
        public void assertDefaultGroupSequenceIsExpandable(List<Class<?>> defaultGroupSequence) throws GroupDefinitionException {
        }
    }

    public static class DefaultSequenceValidationOrder
    implements ValidationOrder {
        private final List<Sequence> defaultSequences = Collections.singletonList(Sequence.DEFAULT);

        private DefaultSequenceValidationOrder() {
        }

        @Override
        public Iterator<Group> getGroupIterator() {
            return Collections.emptyIterator();
        }

        @Override
        public Iterator<Sequence> getSequenceIterator() {
            return this.defaultSequences.iterator();
        }

        @Override
        public void assertDefaultGroupSequenceIsExpandable(List<Class<?>> defaultGroupSequence) throws GroupDefinitionException {
        }
    }
}

