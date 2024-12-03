/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.google.common.base.Function
 */
package com.atlassian.crowd.model;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.google.common.base.Function;
import java.util.Comparator;

public class NameComparator {
    private static final DirectoryEntityNameComparator DIRECTORY_ENTITY_NAME_COMPARATOR = new DirectoryEntityNameComparator();
    private static final StringNameComparator STRING_NAME_COMPARATOR = new StringNameComparator();
    private static final DirectoryEntityNameNormaliser DIRECTORY_ENTITY_NAME_NORMALISER = new DirectoryEntityNameNormaliser();
    private static final StringNameNormaliser STRING_NAME_NORMALISER = new StringNameNormaliser();

    private NameComparator() {
    }

    public static <T> Comparator<T> of(Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return STRING_NAME_COMPARATOR;
        }
        if (User.class.isAssignableFrom(type)) {
            return DIRECTORY_ENTITY_NAME_COMPARATOR;
        }
        if (Group.class.isAssignableFrom(type)) {
            return DIRECTORY_ENTITY_NAME_COMPARATOR;
        }
        throw new IllegalArgumentException("Can't find name comparator for type " + type);
    }

    public static Comparator<DirectoryEntity> directoryEntityComparator() {
        return DIRECTORY_ENTITY_NAME_COMPARATOR;
    }

    public static <T> Function<T, String> normaliserOf(Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return STRING_NAME_NORMALISER;
        }
        if (DirectoryEntity.class.isAssignableFrom(type)) {
            return DIRECTORY_ENTITY_NAME_NORMALISER;
        }
        throw new IllegalArgumentException("Can't find name normaliser for type " + type);
    }

    public static <T> Function<T, String> nameGetter(Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return a -> (String)a;
        }
        if (DirectoryEntity.class.isAssignableFrom(type)) {
            return entity -> ((DirectoryEntity)entity).getName();
        }
        throw new IllegalArgumentException("Can't find name normaliser for type " + type);
    }

    private static class StringNameNormaliser
    implements Function<String, String> {
        private StringNameNormaliser() {
        }

        public String apply(String from) {
            return IdentifierUtils.toLowerCase((String)from);
        }
    }

    private static class DirectoryEntityNameNormaliser
    implements Function<DirectoryEntity, String> {
        private DirectoryEntityNameNormaliser() {
        }

        public String apply(DirectoryEntity from) {
            return IdentifierUtils.toLowerCase((String)from.getName());
        }
    }

    private static class StringNameComparator
    implements Comparator<String> {
        private StringNameComparator() {
        }

        @Override
        public int compare(String o1, String o2) {
            return IdentifierUtils.compareToInLowerCase((String)o1, (String)o2);
        }
    }

    private static class DirectoryEntityNameComparator
    implements Comparator<DirectoryEntity> {
        private DirectoryEntityNameComparator() {
        }

        @Override
        public int compare(DirectoryEntity o1, DirectoryEntity o2) {
            return IdentifierUtils.compareToInLowerCase((String)o1.getName(), (String)o2.getName());
        }
    }
}

