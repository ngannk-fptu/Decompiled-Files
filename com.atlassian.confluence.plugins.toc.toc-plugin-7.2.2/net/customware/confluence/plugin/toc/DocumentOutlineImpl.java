/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package net.customware.confluence.plugin.toc;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.customware.confluence.plugin.toc.DefaultDocumentOutlineBuilder;
import net.customware.confluence.plugin.toc.DocumentOutline;
import org.apache.commons.lang3.StringUtils;

class DocumentOutlineImpl
implements DocumentOutline {
    static final HeadingMatcher NOT_PLACEHOLDER_MATCHER = new HeadingMatcher(){

        @Override
        public boolean matches(DocumentOutline.Heading heading) {
            return heading.getName() != null;
        }
    };
    private final List<DefaultDocumentOutlineBuilder.BuildableHeading> topLevel;

    DocumentOutlineImpl(List<DefaultDocumentOutlineBuilder.BuildableHeading> topLevelHeadings) {
        this.topLevel = topLevelHeadings;
    }

    @Override
    public Iterator<DocumentOutline.Heading> iterator() {
        return new ConstraintWrappingIterator(NOT_PLACEHOLDER_MATCHER, new AllIterator());
    }

    @Override
    public Iterator<DocumentOutline.Heading> iterator(int minLevel, int maxLevel, String includeRegex, String excludeRegex) {
        ConstraintWrappingIterator iterator = new ConstraintWrappingIterator(new TypeRangeHeadingMatcher(minLevel, maxLevel), this.iterator());
        if (StringUtils.isNotBlank((CharSequence)includeRegex)) {
            iterator = new ConstraintWrappingIterator(new RegexHeadingMatcher(includeRegex), iterator);
        }
        if (StringUtils.isNotBlank((CharSequence)excludeRegex)) {
            iterator = new ConstraintWrappingIterator(new ExclusionRegexHeadingMatcher(excludeRegex), iterator);
        }
        return iterator;
    }

    private static class ConstraintWrappingIterator
    implements Iterator<DocumentOutline.Heading> {
        private final Iterator<DocumentOutline.Heading> wrappedIterator;
        private final HeadingMatcher matcher;
        private DocumentOutline.Heading next = null;

        public ConstraintWrappingIterator(HeadingMatcher matcher, Iterator<DocumentOutline.Heading> iterator) {
            this.wrappedIterator = iterator;
            this.matcher = matcher;
        }

        @Override
        public boolean hasNext() {
            if (this.next != null) {
                return true;
            }
            if (!this.wrappedIterator.hasNext()) {
                return false;
            }
            DocumentOutline.Heading candidateHeading = this.wrappedIterator.next();
            while (!this.matcher.matches(candidateHeading)) {
                if (this.wrappedIterator.hasNext()) {
                    candidateHeading = this.wrappedIterator.next();
                    continue;
                }
                candidateHeading = null;
                break;
            }
            this.next = candidateHeading;
            return this.next != null;
        }

        @Override
        public DocumentOutline.Heading next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            DocumentOutline.Heading forReturn = this.next;
            this.next = null;
            return forReturn;
        }

        @Override
        public void remove() {
            this.wrappedIterator.remove();
        }
    }

    private class AllIterator
    implements Iterator<DocumentOutline.Heading> {
        private final Stack<Integer> childIndexes = new Stack();
        private DefaultDocumentOutlineBuilder.BuildableHeading currentHeading = null;
        private DefaultDocumentOutlineBuilder.BuildableHeading nextHeading = null;

        public AllIterator() {
            this.childIndexes.push(-1);
        }

        @Override
        public boolean hasNext() {
            if (this.nextHeading != null) {
                return true;
            }
            this.nextHeading = this.getNext(this.currentHeading, this.childIndexes.peek() + 1);
            if (this.nextHeading == null) {
                this.currentHeading = null;
            }
            return this.nextHeading != null;
        }

        private DefaultDocumentOutlineBuilder.BuildableHeading getNext(DefaultDocumentOutlineBuilder.BuildableHeading parent, int nextChildIndex) {
            if (parent == null) {
                if (nextChildIndex < DocumentOutlineImpl.this.topLevel.size()) {
                    this.childIndexes.pop();
                    this.childIndexes.push(nextChildIndex);
                    this.childIndexes.push(-1);
                    return (DefaultDocumentOutlineBuilder.BuildableHeading)DocumentOutlineImpl.this.topLevel.get(nextChildIndex);
                }
                return null;
            }
            if (parent.hasChildren() && nextChildIndex < parent.getChildCount()) {
                this.childIndexes.pop();
                this.childIndexes.push(nextChildIndex);
                this.childIndexes.push(-1);
                return parent.getChild(nextChildIndex);
            }
            this.childIndexes.pop();
            return this.getNext(parent.getParent(), this.childIndexes.peek() + 1);
        }

        @Override
        public DocumentOutline.Heading next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            DefaultDocumentOutlineBuilder.BuildableHeading heading = this.nextHeading;
            this.currentHeading = this.nextHeading;
            this.nextHeading = null;
            return heading;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected static class TypeRangeHeadingMatcher
    implements HeadingMatcher {
        private final int minType;
        private final int maxType;

        public TypeRangeHeadingMatcher(int minType, int maxType) {
            if (minType > maxType) {
                throw new IllegalArgumentException("The minType must be the same or less than the maxType");
            }
            this.minType = minType;
            this.maxType = maxType;
        }

        @Override
        public boolean matches(DocumentOutline.Heading heading) {
            int type = heading.getType();
            return type >= this.minType && type <= this.maxType;
        }
    }

    protected static class ExclusionRegexHeadingMatcher
    implements HeadingMatcher {
        private HeadingMatcher delegateMatcher;

        public ExclusionRegexHeadingMatcher(String pattern) {
            this.delegateMatcher = new RegexHeadingMatcher(pattern);
        }

        @Override
        public boolean matches(DocumentOutline.Heading heading) {
            return !this.delegateMatcher.matches(heading);
        }
    }

    protected static class RegexHeadingMatcher
    implements HeadingMatcher {
        private Pattern pattern;

        public RegexHeadingMatcher(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        @Override
        public boolean matches(DocumentOutline.Heading heading) {
            Matcher matcher = this.pattern.matcher(heading.getName());
            return matcher.matches();
        }
    }

    static interface HeadingMatcher {
        public boolean matches(DocumentOutline.Heading var1);
    }
}

