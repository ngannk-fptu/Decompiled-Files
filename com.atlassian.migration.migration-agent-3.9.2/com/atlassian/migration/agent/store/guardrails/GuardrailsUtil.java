/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  javax.persistence.TupleElement
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.store.guardrails.GuardrailsTupleElement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

public final class GuardrailsUtil {
    private static final Logger log = ContextLoggerFactory.getLogger(GuardrailsUtil.class);
    private static final String[] PERCENTILES = new String[]{"P25", "P50", "P75", "P90", "P99"};
    private static final DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));

    private GuardrailsUtil() {
        throw new IllegalStateException("Utility Class");
    }

    public static String formatTuple(Tuple t, Class<?> ... args) {
        StringBuilder formattedData = new StringBuilder("{");
        for (int i = 0; i < args.length; ++i) {
            Object o = args[i].equals(BigDecimal.class) ? GuardrailsUtil.formatDecimal((BigDecimal)t.get(i, args[i])) : GuardrailsUtil.getTupleValue(t, i, args[i]);
            formattedData.append(i > 0 ? ": " : "").append(o);
        }
        return formattedData.append('}').toString();
    }

    public static <X> X getTupleValue(Tuple t, int i, Class<X> classType) {
        Object obj = t.get(i);
        if (obj == null) {
            return null;
        }
        if ((classType.equals(Integer.class) || classType.equals(BigInteger.class)) && obj.getClass().equals(BigDecimal.class)) {
            Integer val = GuardrailsUtil.getIntegerFromDecimal((BigDecimal)obj);
            return (X)(classType.equals(Integer.class) ? val : BigInteger.valueOf(val.intValue()));
        }
        return (X)obj;
    }

    public static String formatLong(long l) {
        return String.valueOf(l);
    }

    public static String formatDecimal(BigDecimal d) {
        return df.format(d);
    }

    public static String formatTupleList(List<Tuple> groups, Class<?> ... args) {
        return groups.stream().map(tuple -> GuardrailsUtil.formatTuple(tuple, args)).collect(Collectors.joining(","));
    }

    public static String formatTupleListToJson(List<Tuple> groups) {
        if (CollectionUtils.isEmpty(groups)) {
            return "[]";
        }
        return "[" + groups.stream().map(GuardrailsUtil::formatTupleToJson).collect(Collectors.joining(",")) + "]";
    }

    public static String formatTupleToJson(Tuple t) {
        StringBuilder formattedData = new StringBuilder("{");
        for (TupleElement e : t.getElements()) {
            Object o;
            Object object = o = t.get(e) != null && t.get(e).getClass().equals(BigDecimal.class) ? GuardrailsUtil.formatDecimal((BigDecimal)t.get(e)) : t.get(e);
            if (o instanceof List) {
                formattedData.append("\"").append(e.getAlias().toLowerCase()).append("\": ").append(GuardrailsUtil.formatTupleListToJson((List)o)).append(", ");
                continue;
            }
            formattedData.append("\"").append(e.getAlias().toLowerCase()).append("\": \"").append(o).append("\", ");
        }
        return formattedData.substring(0, formattedData.length() - 2) + "}";
    }

    private static Integer getIntegerFromDecimal(BigDecimal d) {
        return d.intValue();
    }

    public static Tuple getTuple(final GuardrailsTupleElement<?> ... elementsArray) {
        return new Tuple(){
            private final List<TupleElement<?>> elements;
            private final Map<String, Object> map;
            {
                this.elements = Arrays.asList(elementsArray);
                this.map = Arrays.stream(elementsArray).filter(element -> element.getValue() != null).collect(Collectors.toMap(GuardrailsTupleElement::getAlias, GuardrailsTupleElement::getValue));
            }

            public <X> X get(TupleElement<X> tupleElement) {
                return this.get(tupleElement.getAlias(), tupleElement.getJavaType());
            }

            public <X> X get(String alias, Class<X> type) {
                return type.cast(this.map.get(alias));
            }

            public Object get(String alias) {
                return this.map.get(alias);
            }

            public <X> X get(int i, Class<X> type) {
                TupleElement<?> element = this.elements.get(i);
                return this.get(element.getAlias(), type);
            }

            public Object get(int i) {
                TupleElement<?> element = this.elements.get(i);
                return this.get(element.getAlias());
            }

            public Object[] toArray() {
                return new Object[0];
            }

            public List<TupleElement<?>> getElements() {
                return this.elements;
            }
        };
    }

    public static int getMacrosCount(String stringVal) {
        int num = 0;
        try {
            num = Optional.ofNullable(stringVal).map(val -> val.split("-")).filter(vals -> ((String[])vals).length >= 2).map(vals -> Integer.parseInt(vals[1])).orElse(0);
        }
        catch (NumberFormatException e) {
            log.debug("Failed to parse integer value when getting macros count", (Throwable)e);
        }
        return num;
    }

    public static List<Tuple> getTopK(List<Tuple> results, int k) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<Tuple>();
        }
        int size = results.size();
        return size < k ? results : new ArrayList(results.subList(0, k));
    }

    public static List<GuardrailsTupleElement<Integer>> getDistributions(List<Tuple> queryResult) {
        if (queryResult == null || queryResult.isEmpty()) {
            return new ArrayList<GuardrailsTupleElement<Integer>>();
        }
        int size = queryResult.size();
        ArrayList<GuardrailsTupleElement<Integer>> distributions = new ArrayList<GuardrailsTupleElement<Integer>>();
        for (String per : PERCENTILES) {
            int idx = (int)Math.ceil((1.0 - (double)Integer.parseInt(per.substring(1)) / 100.0) * (double)size);
            Integer value = (Integer)queryResult.get(idx - 1).get(1, Integer.class);
            distributions.add(new GuardrailsTupleElement<Integer>(per, value, Integer.class));
        }
        return distributions;
    }

    public static GuardrailsTupleElement<Integer> getAverage(List<Tuple> results) {
        int ave = 0;
        if (results != null) {
            ave = (int)Math.ceil(results.stream().mapToInt(tuple -> (Integer)tuple.get(1, Integer.class)).average().orElse(0.0));
        }
        return new GuardrailsTupleElement<Integer>("mean", ave, Integer.class);
    }

    public static GuardrailsTupleElement<Integer> getMax(List<Tuple> results) {
        int max = 0;
        if (results != null && !results.isEmpty()) {
            max = (Integer)results.get(0).get(1, Integer.class);
        }
        return new GuardrailsTupleElement<Integer>("max", max, Integer.class);
    }

    public static String formatPerPageDistributionResult(List<Tuple> queryResult, int k) {
        ArrayList<GuardrailsTupleElement> res = new ArrayList<GuardrailsTupleElement>(Arrays.asList(GuardrailsUtil.getMax(queryResult), GuardrailsUtil.getAverage(queryResult)));
        res.addAll(GuardrailsUtil.getDistributions(queryResult));
        res.add(new GuardrailsTupleElement<List<Tuple>>("values", GuardrailsUtil.getTopK(queryResult, k)));
        GuardrailsTupleElement[] resArray = res.toArray(new GuardrailsTupleElement[0]);
        return GuardrailsUtil.formatTupleListToJson(Collections.singletonList(GuardrailsUtil.getTuple(resArray)));
    }

    public static List<Tuple> formatResult(List<Tuple> tuples) {
        ArrayList<Tuple> formattedTuples = new ArrayList<Tuple>();
        int sizeWithEditFrequency = 3;
        int sizeCountOnly = 2;
        for (Tuple t : tuples) {
            int size = t.getElements().size();
            if (size >= sizeWithEditFrequency) {
                formattedTuples.add(GuardrailsUtil.getTuple(new GuardrailsTupleElement<Long>("page_id", ((Number)t.get(0, Number.class)).longValue(), Long.class), new GuardrailsTupleElement<Integer>("count", ((Number)t.get(1, Number.class)).intValue(), Integer.class), new GuardrailsTupleElement<Integer>("page_edit_frequency", ((Number)t.get(2, Number.class)).intValue(), Integer.class)));
                continue;
            }
            if (size != sizeCountOnly) continue;
            formattedTuples.add(GuardrailsUtil.getTuple(new GuardrailsTupleElement<Long>("page_id", ((Number)t.get(0, Number.class)).longValue(), Long.class), new GuardrailsTupleElement<Integer>("count", ((Number)t.get(1, Number.class)).intValue(), Integer.class)));
        }
        return formattedTuples;
    }

    public static String toSqlListString(List<Tuple> list) {
        return "(" + list.stream().map(t -> String.valueOf(((Number)t.get(0, Number.class)).longValue())).collect(Collectors.joining(",")) + ")";
    }
}

