/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Parser;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.VisitablePredicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.SkipIndexPredicate;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.CompoundPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import com.hazelcast.query.impl.predicates.Visitor;
import com.hazelcast.util.collection.ArrayUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@BinaryInterface
public class SqlPredicate
implements IndexAwarePredicate,
VisitablePredicate,
IdentifiedDataSerializable {
    private static final boolean SKIP_INDEX_ENABLED = !Boolean.getBoolean("hazelcast.query.disableSkipIndex");
    private static final long serialVersionUID = 1L;
    private static final ComparisonPredicateFactory EQUAL_FACTORY = new ComparisonPredicateFactory(){

        @Override
        public Predicate create(String attribute, Comparable c) {
            return Predicates.equal(attribute, c);
        }
    };
    private static final ComparisonPredicateFactory NOT_EQUAL_FACTORY = new ComparisonPredicateFactory(){

        @Override
        public Predicate create(String attribute, Comparable c) {
            return Predicates.notEqual(attribute, c);
        }
    };
    private static final ComparisonPredicateFactory GREATER_THAN_FACTORY = new ComparisonPredicateFactory(){

        @Override
        public Predicate create(String attribute, Comparable c) {
            return Predicates.greaterThan(attribute, c);
        }
    };
    private static final ComparisonPredicateFactory GREATER_EQUAL_FACTORY = new ComparisonPredicateFactory(){

        @Override
        public Predicate create(String attribute, Comparable c) {
            return Predicates.greaterEqual(attribute, c);
        }
    };
    private static final ComparisonPredicateFactory LESS_EQUAL_FACTORY = new ComparisonPredicateFactory(){

        @Override
        public Predicate create(String attribute, Comparable c) {
            return Predicates.lessEqual(attribute, c);
        }
    };
    private static final ComparisonPredicateFactory LESS_THAN_FACTORY = new ComparisonPredicateFactory(){

        @Override
        public Predicate create(String attribute, Comparable c) {
            return Predicates.lessThan(attribute, c);
        }
    };
    transient Predicate predicate;
    private String sql;

    public SqlPredicate(String sql) {
        this.sql = sql;
        this.predicate = this.createPredicate(sql);
    }

    public SqlPredicate() {
    }

    @Override
    public boolean apply(Map.Entry mapEntry) {
        return this.predicate.apply(mapEntry);
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        if (this.predicate instanceof IndexAwarePredicate) {
            return ((IndexAwarePredicate)this.predicate).isIndexed(queryContext);
        }
        return false;
    }

    public Set<QueryableEntry> filter(QueryContext queryContext) {
        return ((IndexAwarePredicate)this.predicate).filter(queryContext);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.sql);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.sql = in.readUTF();
        this.predicate = this.createPredicate(this.sql);
    }

    private int getApostropheIndex(String str, int start) {
        return str.indexOf(39, start);
    }

    private int getApostropheIndexIgnoringDoubles(String str, int start) {
        int i = str.indexOf(39, start);
        int j = str.indexOf(39, i + 1);
        while (i == j - 1) {
            i = str.indexOf(39, j + 1);
            j = str.indexOf(39, i + 1);
        }
        return i;
    }

    private String removeEscapes(String phrase) {
        return phrase.length() > 2 ? phrase.replace("''", "'") : phrase;
    }

    private Predicate createPredicate(String sql) {
        Parser parser;
        List<String> sqlTokens;
        ArrayList<Object> tokens;
        String paramSql = sql;
        HashMap<String, String> mapPhrases = new HashMap<String, String>();
        int apoIndex = this.getApostropheIndex(paramSql, 0);
        if (apoIndex != -1) {
            int phraseId = 0;
            StringBuilder newSql = new StringBuilder();
            while (apoIndex != -1) {
                ++phraseId;
                int start = apoIndex + 1;
                int end = this.getApostropheIndexIgnoringDoubles(paramSql, apoIndex + 1);
                if (end == -1) {
                    throw new RuntimeException("Missing ' in sql");
                }
                String phrase = this.removeEscapes(paramSql.substring(start, end));
                String key = "$" + phraseId;
                mapPhrases.put(key, phrase);
                String before = paramSql.substring(0, apoIndex);
                paramSql = paramSql.substring(end + 1);
                newSql.append(before);
                newSql.append(key);
                apoIndex = this.getApostropheIndex(paramSql, 0);
            }
            newSql.append(paramSql);
            paramSql = newSql.toString();
        }
        if ((tokens = new ArrayList<Object>(sqlTokens = (parser = new Parser()).toPrefix(paramSql))).size() == 0) {
            throw new RuntimeException("Invalid SQL: [" + paramSql + "]");
        }
        if (tokens.size() == 1) {
            return this.eval(tokens.get(0));
        }
        block1: while (tokens.size() > 1) {
            boolean foundOperand = false;
            for (int i = 0; i < tokens.size(); ++i) {
                Object exp;
                Object second;
                Object first;
                int position;
                Object tokenObj = tokens.get(i);
                if (!(tokenObj instanceof String) || !parser.isOperand((String)tokenObj)) continue;
                String token = (String)tokenObj;
                if ("=".equals(token) || "==".equals(token)) {
                    this.createComparison(mapPhrases, tokens, i, EQUAL_FACTORY);
                    continue block1;
                }
                if ("!=".equals(token) || "<>".equals(token)) {
                    this.createComparison(mapPhrases, tokens, i, NOT_EQUAL_FACTORY);
                    continue block1;
                }
                if (">".equals(token)) {
                    this.createComparison(mapPhrases, tokens, i, GREATER_THAN_FACTORY);
                    continue block1;
                }
                if (">=".equals(token)) {
                    this.createComparison(mapPhrases, tokens, i, GREATER_EQUAL_FACTORY);
                    continue block1;
                }
                if ("<=".equals(token)) {
                    this.createComparison(mapPhrases, tokens, i, LESS_EQUAL_FACTORY);
                    continue block1;
                }
                if ("<".equals(token)) {
                    this.createComparison(mapPhrases, tokens, i, LESS_THAN_FACTORY);
                    continue block1;
                }
                if ("LIKE".equalsIgnoreCase(token)) {
                    position = i - 2;
                    this.validateOperandPosition(position);
                    first = this.toValue(tokens.remove(position), mapPhrases);
                    second = this.toValue(tokens.remove(position), mapPhrases);
                    this.setOrAdd(tokens, position, Predicates.like((String)first, (String)second));
                    continue block1;
                }
                if ("ILIKE".equalsIgnoreCase(token)) {
                    position = i - 2;
                    this.validateOperandPosition(position);
                    first = this.toValue(tokens.remove(position), mapPhrases);
                    second = this.toValue(tokens.remove(position), mapPhrases);
                    this.setOrAdd(tokens, position, Predicates.ilike((String)first, (String)second));
                    continue block1;
                }
                if ("REGEX".equalsIgnoreCase(token)) {
                    position = i - 2;
                    this.validateOperandPosition(position);
                    first = this.toValue(tokens.remove(position), mapPhrases);
                    second = this.toValue(tokens.remove(position), mapPhrases);
                    this.setOrAdd(tokens, position, Predicates.regex((String)first, (String)second));
                    continue block1;
                }
                if ("IN".equalsIgnoreCase(token)) {
                    position = i - 2;
                    this.validateOperandPosition(position);
                    exp = (String)this.toValue(tokens.remove(position), mapPhrases);
                    String[] values = this.toValue(((String)tokens.remove(position)).split(","), (Map<String, String>)mapPhrases);
                    if (this.skipIndex((String)exp)) {
                        exp = ((String)exp).substring(1);
                        this.setOrAdd(tokens, position, new SkipIndexPredicate(Predicates.in((String)exp, (Comparable[])values)));
                        continue block1;
                    }
                    this.setOrAdd(tokens, position, Predicates.in((String)exp, (Comparable[])values));
                    continue block1;
                }
                if ("NOT".equalsIgnoreCase(token)) {
                    position = i - 1;
                    this.validateOperandPosition(position);
                    exp = this.toValue(tokens.remove(position), mapPhrases);
                    this.setOrAdd(tokens, position, Predicates.not(this.eval(exp)));
                    continue block1;
                }
                if ("BETWEEN".equalsIgnoreCase(token)) {
                    position = i - 3;
                    this.validateOperandPosition(position);
                    Object expression = tokens.remove(position);
                    Object from = this.toValue(tokens.remove(position), mapPhrases);
                    Object to = this.toValue(tokens.remove(position), mapPhrases);
                    this.setOrAdd(tokens, position, Predicates.between((String)expression, (Comparable)from, (Comparable)to));
                    continue block1;
                }
                if ("AND".equalsIgnoreCase(token)) {
                    position = i - 2;
                    this.validateOperandPosition(position);
                    first = this.toValue(tokens.remove(position), mapPhrases);
                    second = this.toValue(tokens.remove(position), mapPhrases);
                    this.setOrAdd(tokens, position, SqlPredicate.flattenCompound(this.eval(first), this.eval(second), AndPredicate.class));
                    continue block1;
                }
                if ("OR".equalsIgnoreCase(token)) {
                    position = i - 2;
                    this.validateOperandPosition(position);
                    first = this.toValue(tokens.remove(position), mapPhrases);
                    second = this.toValue(tokens.remove(position), mapPhrases);
                    this.setOrAdd(tokens, position, SqlPredicate.flattenCompound(this.eval(first), this.eval(second), OrPredicate.class));
                    continue block1;
                }
                throw new RuntimeException("Unknown token " + token);
            }
            if (foundOperand) continue;
            throw new RuntimeException("Invalid SQL: [" + paramSql + "]");
        }
        return (Predicate)tokens.get(0);
    }

    private void createComparison(Map<String, String> mapPhrases, List<Object> tokens, int i, ComparisonPredicateFactory factory) {
        int position = i - 2;
        this.validateOperandPosition(position);
        String first = (String)this.toValue(tokens.remove(position), mapPhrases);
        Comparable second = (Comparable)this.toValue(tokens.remove(position), mapPhrases);
        if (this.skipIndex(first)) {
            first = first.substring(1);
            this.setOrAdd(tokens, position, new SkipIndexPredicate(factory.create(first, second)));
        } else {
            this.setOrAdd(tokens, position, factory.create(first, second));
        }
    }

    private boolean skipIndex(String first) {
        return SKIP_INDEX_ENABLED && first.startsWith("%");
    }

    private void validateOperandPosition(int pos) {
        if (pos < 0) {
            throw new RuntimeException("Invalid SQL: [" + this.sql + "]");
        }
    }

    private Object toValue(Object key, Map<String, String> phrases) {
        String value = phrases.get(key);
        if (value != null) {
            return value;
        }
        if (key instanceof String && "null".equalsIgnoreCase((String)key)) {
            return null;
        }
        return key;
    }

    private String[] toValue(String[] keys, Map<String, String> phrases) {
        for (int i = 0; i < keys.length; ++i) {
            String value = phrases.get(keys[i]);
            if (value == null) continue;
            keys[i] = value;
        }
        return keys;
    }

    private void setOrAdd(List tokens, int position, Predicate predicate) {
        if (tokens.size() == 0) {
            tokens.add(predicate);
        } else {
            tokens.set(position, predicate);
        }
    }

    private Predicate eval(Object statement) {
        if (statement instanceof String) {
            return Predicates.equal((String)statement, (Comparable)((Object)"true"));
        }
        return (Predicate)statement;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.predicate = this.createPredicate(this.sql);
    }

    static <T extends CompoundPredicate> T flattenCompound(Predicate predicateLeft, Predicate predicateRight, Class<T> klass) {
        Predicate[] predicates;
        if (klass.isInstance(predicateLeft) || klass.isInstance(predicateRight)) {
            Predicate[] left = SqlPredicate.getSubPredicatesIfClass(predicateLeft, klass);
            Predicate[] right = SqlPredicate.getSubPredicatesIfClass(predicateRight, klass);
            predicates = new Predicate[left.length + right.length];
            ArrayUtils.concat(left, right, predicates);
        } else {
            predicates = new Predicate[]{predicateLeft, predicateRight};
        }
        try {
            CompoundPredicate compoundPredicate = (CompoundPredicate)klass.newInstance();
            compoundPredicate.setPredicates(predicates);
            return (T)compoundPredicate;
        }
        catch (InstantiationException e) {
            throw new RuntimeException(String.format("%s should have a public default constructor", klass.getName()));
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("%s should have a public default constructor", klass.getName()));
        }
    }

    private static <T extends CompoundPredicate> Predicate[] getSubPredicatesIfClass(Predicate predicate, Class<T> klass) {
        if (klass.isInstance(predicate)) {
            return ((CompoundPredicate)((Object)predicate)).getPredicates();
        }
        return new Predicate[]{predicate};
    }

    public String toString() {
        return this.predicate.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SqlPredicate)) {
            return false;
        }
        SqlPredicate that = (SqlPredicate)o;
        return this.sql.equals(that.sql);
    }

    public int hashCode() {
        return this.sql.hashCode();
    }

    @Override
    public Predicate accept(Visitor visitor, Indexes indexes) {
        Predicate target = this.predicate;
        if (this.predicate instanceof VisitablePredicate) {
            target = ((VisitablePredicate)((Object)this.predicate)).accept(visitor, indexes);
        }
        return target;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    @Override
    public int getFactoryId() {
        return -32;
    }

    @Override
    public int getId() {
        return 0;
    }

    private static interface ComparisonPredicateFactory {
        public Predicate create(String var1, Comparable var2);
    }
}

