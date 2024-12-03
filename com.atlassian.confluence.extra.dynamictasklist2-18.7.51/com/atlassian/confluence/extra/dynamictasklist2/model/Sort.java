/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.util.RendererUtil
 *  org.apache.commons.collections.comparators.ComparatorChain
 *  org.springframework.beans.support.MutableSortDefinition
 *  org.springframework.beans.support.PropertyComparator
 *  org.springframework.beans.support.SortDefinition
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

import com.atlassian.confluence.extra.dynamictasklist2.model.Task;
import com.atlassian.renderer.util.RendererUtil;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.SortDefinition;

public abstract class Sort {
    public static final Sort NONE = new Sort(){

        @Override
        public String toString() {
            return "";
        }

        @Override
        public Comparator getComparator(boolean ascending) {
            return new Comparator(){

                public int compare(Object arg0, Object arg1) {
                    return 0;
                }
            };
        }

        @Override
        public boolean getAscendingDefault() {
            return true;
        }
    };
    public static final Sort BY_NAME = new Sort(){

        @Override
        public String toString() {
            return "name";
        }

        @Override
        public Comparator getComparator(boolean ascending) {
            return new NameComparator(ascending);
        }

        @Override
        public boolean getAscendingDefault() {
            return true;
        }
    };
    public static final Sort BY_PRIORITY = new Sort(){

        @Override
        public String toString() {
            return "priority";
        }

        @Override
        public Comparator getComparator(boolean ascending) {
            ComparatorChain chain = new ComparatorChain();
            chain.addComparator((Comparator)((Object)new PropertyComparator("priority", false, ascending)));
            chain.addComparator(BY_NAME.getComparator(ascending));
            return chain;
        }

        @Override
        public boolean getAscendingDefault() {
            return false;
        }
    };
    public static final Sort BY_CREATION_DATE = new Sort(){

        @Override
        public String toString() {
            return "date";
        }

        @Override
        public Comparator getComparator(boolean ascending) {
            ComparatorChain chain = new ComparatorChain();
            chain.addComparator((Comparator)((Object)new PropertyComparator("createdDate", false, ascending)));
            chain.addComparator(BY_NAME.getComparator(ascending));
            return chain;
        }

        @Override
        public boolean getAscendingDefault() {
            return true;
        }
    };
    public static final Sort BY_COMPLETION_DATE = new Sort(){

        @Override
        public String toString() {
            return "completed";
        }

        @Override
        public Comparator getComparator(boolean ascending) {
            ComparatorChain chain = new ComparatorChain();
            chain.addComparator((Comparator)((Object)new PropertyComparator("completed", false, ascending)));
            chain.addComparator((Comparator)((Object)new PropertyComparator("completedDate", false, ascending)));
            return chain;
        }

        @Override
        public boolean getAscendingDefault() {
            return true;
        }
    };
    public static final Sort BY_ASSIGNEE = new Sort(){

        @Override
        public String toString() {
            return "assignee";
        }

        @Override
        public Comparator getComparator(boolean ascending) {
            ComparatorChain chain = new ComparatorChain();
            chain.addComparator((Comparator)((Object)new PropertyComparator("assignee", false, ascending)));
            chain.addComparator(BY_NAME.getComparator(ascending));
            return chain;
        }

        @Override
        public boolean getAscendingDefault() {
            return true;
        }
    };

    public abstract String toString();

    public abstract Comparator getComparator(boolean var1);

    public abstract boolean getAscendingDefault();

    public static Sort valueOf(String sort) {
        if (BY_NAME.toString().equals(sort)) {
            return BY_NAME;
        }
        if (BY_PRIORITY.toString().equals(sort)) {
            return BY_PRIORITY;
        }
        if (BY_ASSIGNEE.toString().equals(sort)) {
            return BY_ASSIGNEE;
        }
        if (BY_CREATION_DATE.toString().equals(sort)) {
            return BY_CREATION_DATE;
        }
        if (BY_COMPLETION_DATE.toString().equals(sort)) {
            return BY_COMPLETION_DATE;
        }
        return NONE;
    }

    private static class PropertyComparator
    extends org.springframework.beans.support.PropertyComparator {
        public PropertyComparator(String property, boolean ignoreCase, boolean ascending) {
            super((SortDefinition)new MutableSortDefinition(property, ignoreCase, ascending));
        }
    }

    private static final class NameComparator
    implements Comparator {
        static final Pattern NUMBER_PATTERN = Pattern.compile("^(\\d+).*");
        private final boolean ascending;

        NameComparator(boolean ascending) {
            this.ascending = ascending;
        }

        public int compare(Object lhs, Object rhs) {
            int result;
            if (!(lhs instanceof Task) || !(rhs instanceof Task)) {
                return 0;
            }
            String lhsName = this.stripTaskName((Task)lhs);
            String rhsName = this.stripTaskName((Task)rhs);
            Matcher lhsMatcher = NUMBER_PATTERN.matcher(lhsName);
            Matcher rhsMatcher = NUMBER_PATTERN.matcher(rhsName);
            if (lhsMatcher.matches() && rhsMatcher.matches()) {
                result = Integer.valueOf(lhsMatcher.group(1)).compareTo(Integer.valueOf(rhsMatcher.group(1)));
                if (result == 0) {
                    return lhsName.compareTo(rhsName);
                }
            } else {
                result = lhsName.compareTo(rhsName);
            }
            return this.ascending ? result : -result;
        }

        private String stripTaskName(Task task) {
            return RendererUtil.stripBasicMarkup((String)task.getName());
        }
    }
}

