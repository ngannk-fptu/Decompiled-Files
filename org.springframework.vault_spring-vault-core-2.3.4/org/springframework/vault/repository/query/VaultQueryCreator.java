/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.data.domain.Sort
 *  org.springframework.data.keyvalue.core.query.KeyValueQuery
 *  org.springframework.data.mapping.PersistentPropertyPath
 *  org.springframework.data.mapping.context.MappingContext
 *  org.springframework.data.repository.query.ParameterAccessor
 *  org.springframework.data.repository.query.parser.AbstractQueryCreator
 *  org.springframework.data.repository.query.parser.Part
 *  org.springframework.data.repository.query.parser.Part$IgnoreCaseType
 *  org.springframework.data.repository.query.parser.Part$Type
 *  org.springframework.data.repository.query.parser.PartTree
 */
package org.springframework.vault.repository.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.data.keyvalue.core.query.KeyValueQuery;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.vault.repository.mapping.VaultPersistentEntity;
import org.springframework.vault.repository.mapping.VaultPersistentProperty;
import org.springframework.vault.repository.query.VaultQuery;

public class VaultQueryCreator
extends AbstractQueryCreator<KeyValueQuery<VaultQuery>, VaultQuery> {
    private final MappingContext<VaultPersistentEntity<?>, VaultPersistentProperty> mappingContext;

    public VaultQueryCreator(PartTree tree, ParameterAccessor parameters, MappingContext<VaultPersistentEntity<?>, VaultPersistentProperty> mappingContext) {
        super(tree, parameters);
        this.mappingContext = mappingContext;
    }

    protected VaultQuery create(Part part, Iterator<Object> parameters) {
        return new VaultQuery(this.createPredicate(part, parameters));
    }

    protected VaultQuery and(Part part, VaultQuery base, Iterator<Object> parameters) {
        return base.and(this.createPredicate(part, parameters));
    }

    private Predicate<String> createPredicate(Part part, Iterator<Object> parameters) {
        PersistentPropertyPath propertyPath = this.mappingContext.getPersistentPropertyPath(part.getProperty());
        if (propertyPath.getLeafProperty() != null && !((VaultPersistentProperty)propertyPath.getLeafProperty()).isIdProperty()) {
            throw new InvalidDataAccessApiUsageException(String.format("Cannot create criteria for non-@Id property %s", propertyPath.getLeafProperty()));
        }
        VariableAccessor accessor = VaultQueryCreator.getVariableAccessor(part);
        Predicate<String> predicate = VaultQueryCreator.from(part, accessor, parameters);
        return it -> predicate.test(accessor.toString((String)it));
    }

    private static Predicate<String> from(Part part, VariableAccessor accessor, Iterator<Object> parameters) {
        Part.Type type = part.getType();
        switch (type) {
            case AFTER: 
            case GREATER_THAN: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> it.compareTo((String)value) > 0);
            }
            case GREATER_THAN_EQUAL: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> it.compareTo((String)value) >= 0);
            }
            case BEFORE: 
            case LESS_THAN: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> it.compareTo((String)value) < 0);
            }
            case LESS_THAN_EQUAL: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> it.compareTo((String)value) <= 0);
            }
            case BETWEEN: {
                String from = accessor.nextString(parameters);
                String to = accessor.nextString(parameters);
                return it -> it.compareTo(from) >= 0 && it.compareTo(to) <= 0;
            }
            case NOT_IN: {
                return new Criteria<String[]>(accessor.nextAsArray(parameters), (value, it) -> Arrays.binarySearch(value, it) < 0);
            }
            case IN: {
                return new Criteria<String[]>(accessor.nextAsArray(parameters), (value, it) -> Arrays.binarySearch(value, it) >= 0);
            }
            case STARTING_WITH: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> it.startsWith((String)value));
            }
            case ENDING_WITH: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> it.endsWith((String)value));
            }
            case CONTAINING: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> it.contains((CharSequence)value));
            }
            case NOT_CONTAINING: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> !it.contains((CharSequence)value));
            }
            case REGEX: {
                return Pattern.compile((String)parameters.next(), VaultQueryCreator.isIgnoreCase(part) ? 2 : 0).asPredicate();
            }
            case TRUE: {
                return it -> it.equalsIgnoreCase("true");
            }
            case FALSE: {
                return it -> it.equalsIgnoreCase("false");
            }
            case SIMPLE_PROPERTY: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> it.equals(value));
            }
            case NEGATING_SIMPLE_PROPERTY: {
                return new Criteria<String>(accessor.nextString(parameters), (value, it) -> !it.equals(value));
            }
        }
        throw new IllegalArgumentException("Unsupported keyword!");
    }

    protected VaultQuery or(VaultQuery vaultQuery, VaultQuery other) {
        return vaultQuery.or(other);
    }

    protected KeyValueQuery<VaultQuery> complete(VaultQuery vaultQuery, Sort sort) {
        KeyValueQuery query = new KeyValueQuery((Object)vaultQuery);
        if (sort.isSorted()) {
            query.orderBy(sort);
        }
        return query;
    }

    private static VariableAccessor getVariableAccessor(Part part) {
        return VaultQueryCreator.isIgnoreCase(part) ? VariableAccessor.Lowercase : VariableAccessor.AsIs;
    }

    private static boolean isIgnoreCase(Part part) {
        return part.shouldIgnoreCase() != Part.IgnoreCaseType.NEVER;
    }

    static enum VariableAccessor {
        AsIs{

            @Override
            String nextString(Iterator<Object> parameters) {
                return parameters.next().toString();
            }

            @Override
            String[] nextAsArray(Iterator<Object> iterator) {
                Object next = iterator.next();
                if (next instanceof Collection) {
                    return ((Collection)next).toArray(new String[0]);
                }
                if (next != null && next.getClass().isArray()) {
                    return (String[])next;
                }
                return new String[]{(String)next};
            }

            @Override
            String toString(String value) {
                return value;
            }
        }
        ,
        Lowercase{

            @Override
            String nextString(Iterator<Object> parameters) {
                return AsIs.nextString(parameters).toLowerCase();
            }

            @Override
            String[] nextAsArray(Iterator<Object> iterator) {
                String[] original = AsIs.nextAsArray(iterator);
                String[] lowercase = new String[original.length];
                for (int i = 0; i < original.length; ++i) {
                    lowercase[i] = original[i].toLowerCase();
                }
                return lowercase;
            }

            @Override
            String toString(String value) {
                return value.toLowerCase();
            }
        };


        abstract String[] nextAsArray(Iterator<Object> var1);

        abstract String nextString(Iterator<Object> var1);

        abstract String toString(String var1);
    }

    static final class Criteria<T>
    implements Predicate<String> {
        private final T value;
        private final BiPredicate<T, String> predicate;

        public Criteria(T value, BiPredicate<T, String> predicate) {
            this.value = value;
            this.predicate = predicate;
        }

        @Override
        public boolean test(String s) {
            return this.predicate.test(this.value, s);
        }

        public T getValue() {
            return this.value;
        }

        public BiPredicate<T, String> getPredicate() {
            return this.predicate;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Criteria)) {
                return false;
            }
            Criteria other = (Criteria)o;
            T this$value = this.getValue();
            T other$value = other.getValue();
            if (this$value == null ? other$value != null : !this$value.equals(other$value)) {
                return false;
            }
            BiPredicate<T, String> this$predicate = this.getPredicate();
            BiPredicate<T, String> other$predicate = other.getPredicate();
            return !(this$predicate == null ? other$predicate != null : !this$predicate.equals(other$predicate));
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            T $value = this.getValue();
            result = result * 59 + ($value == null ? 43 : $value.hashCode());
            BiPredicate<T, String> $predicate = this.getPredicate();
            result = result * 59 + ($predicate == null ? 43 : $predicate.hashCode());
            return result;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(this.getClass().getSimpleName());
            sb.append(" [value=").append(this.value);
            sb.append(", predicate=").append(this.predicate);
            sb.append(']');
            return sb.toString();
        }
    }
}

