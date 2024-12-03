/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.query.parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.parser.OrderBySource;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class PartTree
implements Streamable<OrPart> {
    private static final String KEYWORD_TEMPLATE = "(%s)(?=(\\p{Lu}|\\P{InBASIC_LATIN}))";
    private static final String QUERY_PATTERN = "find|read|get|query|search|stream";
    private static final String COUNT_PATTERN = "count";
    private static final String EXISTS_PATTERN = "exists";
    private static final String DELETE_PATTERN = "delete|remove";
    private static final Pattern PREFIX_TEMPLATE = Pattern.compile("^(find|read|get|query|search|stream|count|exists|delete|remove)((\\p{Lu}.*?))??By");
    private final Subject subject;
    private final Predicate predicate;

    public PartTree(String source, Class<?> domainClass) {
        Assert.notNull((Object)source, (String)"Source must not be null");
        Assert.notNull(domainClass, (String)"Domain class must not be null");
        Matcher matcher = PREFIX_TEMPLATE.matcher(source);
        if (!matcher.find()) {
            this.subject = new Subject(Optional.empty());
            this.predicate = new Predicate(source, domainClass);
        } else {
            this.subject = new Subject(Optional.of(matcher.group(0)));
            this.predicate = new Predicate(source.substring(matcher.group().length()), domainClass);
        }
    }

    @Override
    public Iterator<OrPart> iterator() {
        return this.predicate.iterator();
    }

    public Sort getSort() {
        return this.predicate.getOrderBySource().toSort();
    }

    public boolean isDistinct() {
        return this.subject.isDistinct();
    }

    public boolean isCountProjection() {
        return this.subject.isCountProjection();
    }

    public boolean isExistsProjection() {
        return this.subject.isExistsProjection();
    }

    public boolean isDelete() {
        return this.subject.isDelete();
    }

    public boolean isLimiting() {
        return this.getMaxResults() != null;
    }

    @Nullable
    public Integer getMaxResults() {
        return this.subject.getMaxResults().orElse(null);
    }

    public Streamable<Part> getParts() {
        return this.flatMap(Streamable::stream);
    }

    public Streamable<Part> getParts(Part.Type type) {
        return this.getParts().filter(part -> part.getType().equals((Object)type));
    }

    public boolean hasPredicate() {
        return this.predicate.iterator().hasNext();
    }

    public String toString() {
        return String.format("%s %s", StringUtils.collectionToDelimitedString((Collection)this.predicate.nodes, (String)" or "), this.predicate.getOrderBySource().toString()).trim();
    }

    private static String[] split(String text, String keyword) {
        Pattern pattern = Pattern.compile(String.format(KEYWORD_TEMPLATE, keyword));
        return pattern.split(text);
    }

    private static class Predicate
    implements Streamable<OrPart> {
        private static final Pattern ALL_IGNORE_CASE = Pattern.compile("AllIgnor(ing|e)Case");
        private static final String ORDER_BY = "OrderBy";
        private final List<OrPart> nodes;
        private final OrderBySource orderBySource;
        private boolean alwaysIgnoreCase;

        public Predicate(String predicate, Class<?> domainClass) {
            String[] parts = PartTree.split(this.detectAndSetAllIgnoreCase(predicate), ORDER_BY);
            if (parts.length > 2) {
                throw new IllegalArgumentException("OrderBy must not be used more than once in a method name!");
            }
            this.nodes = Arrays.stream(PartTree.split(parts[0], "Or")).filter(StringUtils::hasText).map(part -> new OrPart((String)part, domainClass, this.alwaysIgnoreCase)).collect(Collectors.toList());
            this.orderBySource = parts.length == 2 ? new OrderBySource(parts[1], Optional.of(domainClass)) : OrderBySource.EMPTY;
        }

        private String detectAndSetAllIgnoreCase(String predicate) {
            Matcher matcher = ALL_IGNORE_CASE.matcher(predicate);
            if (matcher.find()) {
                this.alwaysIgnoreCase = true;
                predicate = predicate.substring(0, matcher.start()) + predicate.substring(matcher.end(), predicate.length());
            }
            return predicate;
        }

        public OrderBySource getOrderBySource() {
            return this.orderBySource;
        }

        @Override
        public Iterator<OrPart> iterator() {
            return this.nodes.iterator();
        }
    }

    private static class Subject {
        private static final String DISTINCT = "Distinct";
        private static final Pattern COUNT_BY_TEMPLATE = Pattern.compile("^count(\\p{Lu}.*?)??By");
        private static final Pattern EXISTS_BY_TEMPLATE = Pattern.compile("^(exists)(\\p{Lu}.*?)??By");
        private static final Pattern DELETE_BY_TEMPLATE = Pattern.compile("^(delete|remove)(\\p{Lu}.*?)??By");
        private static final String LIMITING_QUERY_PATTERN = "(First|Top)(\\d*)?";
        private static final Pattern LIMITED_QUERY_TEMPLATE = Pattern.compile("^(find|read|get|query|search|stream)(Distinct)?(First|Top)(\\d*)?(\\p{Lu}.*?)??By");
        private final boolean distinct;
        private final boolean count;
        private final boolean exists;
        private final boolean delete;
        private final Optional<Integer> maxResults;

        public Subject(Optional<String> subject) {
            this.distinct = subject.map(it -> it.contains(DISTINCT)).orElse(false);
            this.count = this.matches(subject, COUNT_BY_TEMPLATE);
            this.exists = this.matches(subject, EXISTS_BY_TEMPLATE);
            this.delete = this.matches(subject, DELETE_BY_TEMPLATE);
            this.maxResults = this.returnMaxResultsIfFirstKSubjectOrNull(subject);
        }

        private Optional<Integer> returnMaxResultsIfFirstKSubjectOrNull(Optional<String> subject) {
            return subject.map(it -> {
                Matcher grp = LIMITED_QUERY_TEMPLATE.matcher((CharSequence)it);
                if (!grp.find()) {
                    return null;
                }
                return StringUtils.hasText((String)grp.group(4)) ? Integer.valueOf(grp.group(4)) : 1;
            });
        }

        public boolean isDelete() {
            return this.delete;
        }

        public boolean isCountProjection() {
            return this.count;
        }

        public boolean isExistsProjection() {
            return this.exists;
        }

        public boolean isDistinct() {
            return this.distinct;
        }

        public Optional<Integer> getMaxResults() {
            return this.maxResults;
        }

        private boolean matches(Optional<String> subject, Pattern pattern) {
            return subject.map(it -> pattern.matcher((CharSequence)it).find()).orElse(false);
        }
    }

    public static class OrPart
    implements Streamable<Part> {
        private final List<Part> children;

        OrPart(String source, Class<?> domainClass, boolean alwaysIgnoreCase) {
            String[] split = PartTree.split(source, "And");
            this.children = Arrays.stream(split).filter(StringUtils::hasText).map(part -> new Part((String)part, domainClass, alwaysIgnoreCase)).collect(Collectors.toList());
        }

        @Override
        public Iterator<Part> iterator() {
            return this.children.iterator();
        }

        public String toString() {
            return StringUtils.collectionToDelimitedString(this.children, (String)" and ");
        }
    }
}

