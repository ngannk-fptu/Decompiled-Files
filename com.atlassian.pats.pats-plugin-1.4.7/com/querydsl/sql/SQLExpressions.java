/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.Tuple;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.DatePart;
import com.querydsl.sql.RelationalFunctionCall;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.Union;
import com.querydsl.sql.WindowOver;
import com.querydsl.sql.WithinGroup;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class SQLExpressions {
    private static final Map<DatePart, Operator> DATE_ADD_OPS = new EnumMap<DatePart, Operator>(DatePart.class);
    private static final Map<DatePart, Operator> DATE_DIFF_OPS = new EnumMap<DatePart, Operator>(DatePart.class);
    private static final Map<DatePart, Operator> DATE_TRUNC_OPS = new EnumMap<DatePart, Operator>(DatePart.class);
    private static final WindowOver<Double> cumeDist;
    private static final WindowOver<Long> rank;
    private static final WindowOver<Long> denseRank;
    private static final WindowOver<Double> percentRank;
    private static final WindowOver<Long> rowNumber;
    public static final Expression<Object[]> all;
    public static final Expression<Long> countAll;

    private static Expression[] convertToExpressions(Object ... args) {
        Expression[] exprs = new Expression[args.length];
        for (int i = 0; i < args.length; ++i) {
            exprs[i] = args[i] instanceof Expression ? (Expression)args[i] : ConstantImpl.create(args[i]);
        }
        return exprs;
    }

    public static <T> Expression<T> set(Path<T> target, Expression<? extends T> value) {
        if (value != null) {
            return Expressions.operation(target.getType(), SQLOps.SET_PATH, target, value);
        }
        return Expressions.operation(target.getType(), SQLOps.SET_LITERAL, target, Expressions.nullExpression());
    }

    public static <T> Expression<T> set(Path<T> target, T value) {
        if (value != null) {
            return Expressions.operation(target.getType(), SQLOps.SET_LITERAL, target, Expressions.constant(value));
        }
        return Expressions.operation(target.getType(), SQLOps.SET_LITERAL, target, Expressions.nullExpression());
    }

    public static <T> SQLQuery<T> select(Expression<T> expr) {
        return new SQLQuery().select((Expression)expr);
    }

    public static SQLQuery<Tuple> select(Expression<?> ... exprs) {
        return new SQLQuery().select((Expression[])exprs);
    }

    public static <T> SQLQuery<T> selectDistinct(Expression<T> expr) {
        return (SQLQuery)((QueryBase)((Object)new SQLQuery().select((Expression)expr))).distinct();
    }

    public static SQLQuery<Tuple> selectDistinct(Expression<?> ... exprs) {
        return (SQLQuery)((QueryBase)((Object)new SQLQuery().select((Expression[])exprs))).distinct();
    }

    public static SQLQuery<Integer> selectZero() {
        return SQLExpressions.select(Expressions.ZERO);
    }

    public static SQLQuery<Integer> selectOne() {
        return SQLExpressions.select(Expressions.ONE);
    }

    public static <T> SQLQuery<T> selectFrom(RelationalPath<T> expr) {
        return (SQLQuery)SQLExpressions.select(expr).from((Expression<?>)expr);
    }

    public static <T> Union<T> union(SubQueryExpression<T> ... sq) {
        return new SQLQuery().union(sq);
    }

    public static <T> Union<T> union(List<SubQueryExpression<T>> sq) {
        return new SQLQuery().union(sq);
    }

    public static <T> Union<T> unionAll(SubQueryExpression<T> ... sq) {
        return new SQLQuery().unionAll(sq);
    }

    public static <T> Union<T> unionAll(List<SubQueryExpression<T>> sq) {
        return new SQLQuery().unionAll(sq);
    }

    public static BooleanExpression any(BooleanExpression expr) {
        return Expressions.booleanOperation(Ops.AggOps.BOOLEAN_ANY, expr);
    }

    public static BooleanExpression all(BooleanExpression expr) {
        return Expressions.booleanOperation(Ops.AggOps.BOOLEAN_ALL, expr);
    }

    public static <T> RelationalFunctionCall<T> relationalFunctionCall(Class<? extends T> type, String function, Object ... args) {
        return new RelationalFunctionCall<T>(type, function, args);
    }

    public static SimpleExpression<Long> nextval(String sequence) {
        return SQLExpressions.nextval(Long.class, sequence);
    }

    public static <T extends Number> SimpleExpression<T> nextval(Class<T> type, String sequence) {
        return Expressions.operation(type, SQLOps.NEXTVAL, ConstantImpl.create(sequence));
    }

    public static <D extends Comparable> DateExpression<D> date(DateTimeExpression<D> dateTime) {
        return Expressions.dateOperation(dateTime.getType(), Ops.DateTimeOps.DATE, dateTime);
    }

    public static <D extends Comparable> DateExpression<D> date(Class<D> type, DateTimeExpression<?> dateTime) {
        return Expressions.dateOperation(type, Ops.DateTimeOps.DATE, dateTime);
    }

    public static <D extends Comparable> DateTimeExpression<D> dateadd(DatePart unit, DateTimeExpression<D> date, int amount) {
        return Expressions.dateTimeOperation(date.getType(), DATE_ADD_OPS.get((Object)unit), date, ConstantImpl.create(amount));
    }

    public static <D extends Comparable> DateExpression<D> dateadd(DatePart unit, DateExpression<D> date, int amount) {
        return Expressions.dateOperation(date.getType(), DATE_ADD_OPS.get((Object)unit), date, ConstantImpl.create(amount));
    }

    public static <D extends Comparable> NumberExpression<Integer> datediff(DatePart unit, DateExpression<D> start, DateExpression<D> end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get((Object)unit), start, end);
    }

    public static <D extends Comparable> NumberExpression<Integer> datediff(DatePart unit, D start, DateExpression<D> end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get((Object)unit), ConstantImpl.create(start), end);
    }

    public static <D extends Comparable> NumberExpression<Integer> datediff(DatePart unit, DateExpression<D> start, D end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get((Object)unit), start, ConstantImpl.create(end));
    }

    public static <D extends Comparable> NumberExpression<Integer> datediff(DatePart unit, DateTimeExpression<D> start, DateTimeExpression<D> end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get((Object)unit), start, end);
    }

    public static <D extends Comparable> NumberExpression<Integer> datediff(DatePart unit, D start, DateTimeExpression<D> end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get((Object)unit), ConstantImpl.create(start), end);
    }

    public static <D extends Comparable> NumberExpression<Integer> datediff(DatePart unit, DateTimeExpression<D> start, D end) {
        return Expressions.numberOperation(Integer.class, DATE_DIFF_OPS.get((Object)unit), start, ConstantImpl.create(end));
    }

    public static <D extends Comparable> DateExpression<D> datetrunc(DatePart unit, DateExpression<D> expr) {
        return Expressions.dateOperation(expr.getType(), DATE_TRUNC_OPS.get((Object)unit), expr);
    }

    public static <D extends Comparable> DateTimeExpression<D> datetrunc(DatePart unit, DateTimeExpression<D> expr) {
        return Expressions.dateTimeOperation(expr.getType(), DATE_TRUNC_OPS.get((Object)unit), expr);
    }

    public static <D extends Comparable> DateTimeExpression<D> addYears(DateTimeExpression<D> date, int years) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_YEARS, date, ConstantImpl.create(years));
    }

    public static <D extends Comparable> DateTimeExpression<D> addMonths(DateTimeExpression<D> date, int months) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_MONTHS, date, ConstantImpl.create(months));
    }

    public static <D extends Comparable> DateTimeExpression<D> addWeeks(DateTimeExpression<D> date, int weeks) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_WEEKS, date, ConstantImpl.create(weeks));
    }

    public static <D extends Comparable> DateTimeExpression<D> addDays(DateTimeExpression<D> date, int days) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_DAYS, date, ConstantImpl.create(days));
    }

    public static <D extends Comparable> DateTimeExpression<D> addHours(DateTimeExpression<D> date, int hours) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_HOURS, date, ConstantImpl.create(hours));
    }

    public static <D extends Comparable> DateTimeExpression<D> addMinutes(DateTimeExpression<D> date, int minutes) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_MINUTES, date, ConstantImpl.create(minutes));
    }

    public static <D extends Comparable> DateTimeExpression<D> addSeconds(DateTimeExpression<D> date, int seconds) {
        return Expressions.dateTimeOperation(date.getType(), Ops.DateTimeOps.ADD_SECONDS, date, ConstantImpl.create(seconds));
    }

    public static <D extends Comparable> DateExpression<D> addYears(DateExpression<D> date, int years) {
        return Expressions.dateOperation(date.getType(), Ops.DateTimeOps.ADD_YEARS, date, ConstantImpl.create(years));
    }

    public static <D extends Comparable> DateExpression<D> addMonths(DateExpression<D> date, int months) {
        return Expressions.dateOperation(date.getType(), Ops.DateTimeOps.ADD_MONTHS, date, ConstantImpl.create(months));
    }

    public static <D extends Comparable> DateExpression<D> addWeeks(DateExpression<D> date, int weeks) {
        return Expressions.dateOperation(date.getType(), Ops.DateTimeOps.ADD_WEEKS, date, ConstantImpl.create(weeks));
    }

    public static <D extends Comparable> DateExpression<D> addDays(DateExpression<D> date, int days) {
        return Expressions.dateOperation(date.getType(), Ops.DateTimeOps.ADD_DAYS, date, ConstantImpl.create(days));
    }

    public static <T extends Number> WindowOver<T> sum(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)Ops.AggOps.SUM_AGG, (Expression<?>)expr);
    }

    public static WindowOver<Long> count() {
        return new WindowOver<Long>(Long.class, Ops.AggOps.COUNT_ALL_AGG);
    }

    public static WindowOver<Long> count(Expression<?> expr) {
        return new WindowOver<Long>(Long.class, (Operator)Ops.AggOps.COUNT_AGG, expr);
    }

    public static WindowOver<Long> countDistinct(Expression<?> expr) {
        return new WindowOver<Long>(Long.class, (Operator)Ops.AggOps.COUNT_DISTINCT_AGG, expr);
    }

    public static <T extends Number> WindowOver<T> avg(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)Ops.AggOps.AVG_AGG, (Expression<?>)expr);
    }

    public static <T extends Comparable> WindowOver<T> min(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)Ops.AggOps.MIN_AGG, (Expression<?>)expr);
    }

    public static <T extends Comparable> WindowOver<T> max(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)Ops.AggOps.MAX_AGG, (Expression<?>)expr);
    }

    public static <T> WindowOver<T> lead(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.LEAD, (Expression<?>)expr);
    }

    public static <T> WindowOver<T> lag(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.LAG, (Expression<?>)expr);
    }

    public static WithinGroup<Object> listagg(Expression<?> expr, String delimiter) {
        return new WithinGroup<Object>(Object.class, (Operator)SQLOps.LISTAGG, expr, (Expression<?>)ConstantImpl.create(delimiter));
    }

    public static <T> WindowOver<T> nthValue(Expression<T> expr, Number n) {
        return SQLExpressions.nthValue(expr, ConstantImpl.create(n));
    }

    public static <T> WindowOver<T> nthValue(Expression<T> expr, Expression<? extends Number> n) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.NTHVALUE, (Expression<?>)expr, (Expression<?>)n);
    }

    public static <T extends Number> WindowOver<T> ntile(T num) {
        return new WindowOver(num.getClass(), (Operator)SQLOps.NTILE, (Expression<?>)ConstantImpl.create(num));
    }

    public static WindowOver<Long> rank() {
        return rank;
    }

    public static WithinGroup<Long> rank(Object ... args) {
        return SQLExpressions.rank(SQLExpressions.convertToExpressions(args));
    }

    public static WithinGroup<Long> rank(Expression<?> ... args) {
        return new WithinGroup<Long>(Long.class, (Operator)SQLOps.RANK2, args);
    }

    public static WindowOver<Long> denseRank() {
        return denseRank;
    }

    public static WithinGroup<Long> denseRank(Object ... args) {
        return SQLExpressions.denseRank(SQLExpressions.convertToExpressions(args));
    }

    public static WithinGroup<Long> denseRank(Expression<?> ... args) {
        return new WithinGroup<Long>(Long.class, (Operator)SQLOps.DENSERANK2, args);
    }

    public static WindowOver<Double> percentRank() {
        return percentRank;
    }

    public static WithinGroup<Double> percentRank(Object ... args) {
        return SQLExpressions.percentRank(SQLExpressions.convertToExpressions(args));
    }

    public static WithinGroup<Double> percentRank(Expression<?> ... args) {
        return new WithinGroup<Double>(Double.class, (Operator)SQLOps.PERCENTRANK2, args);
    }

    public static <T extends Number> WithinGroup<T> percentileCont(T arg) {
        if (arg.doubleValue() < 0.0 || arg.doubleValue() > 1.0) {
            throw new IllegalArgumentException("The percentile value should be a number between 0 and 1");
        }
        return SQLExpressions.percentileCont(ConstantImpl.create(arg));
    }

    public static <T extends Number> WithinGroup<T> percentileCont(Expression<T> arg) {
        return new WithinGroup<T>(arg.getType(), (Operator)SQLOps.PERCENTILECONT, (Expression<?>)arg);
    }

    public static <T extends Number> WithinGroup<T> percentileDisc(T arg) {
        if (arg.doubleValue() < 0.0 || arg.doubleValue() > 1.0) {
            throw new IllegalArgumentException("The percentile value should be a number between 0 and 1");
        }
        return SQLExpressions.percentileDisc(ConstantImpl.create(arg));
    }

    public static <T extends Number> WithinGroup<T> percentileDisc(Expression<T> arg) {
        return new WithinGroup<T>(arg.getType(), (Operator)SQLOps.PERCENTILEDISC, (Expression<?>)arg);
    }

    public static WindowOver<Double> regrSlope(Expression<? extends Number> arg1, Expression<? extends Number> arg2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.REGR_SLOPE, (Expression<?>)arg1, (Expression<?>)arg2);
    }

    public static WindowOver<Double> regrIntercept(Expression<? extends Number> arg1, Expression<? extends Number> arg2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.REGR_INTERCEPT, (Expression<?>)arg1, (Expression<?>)arg2);
    }

    public static WindowOver<Double> regrCount(Expression<? extends Number> arg1, Expression<? extends Number> arg2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.REGR_COUNT, (Expression<?>)arg1, (Expression<?>)arg2);
    }

    public static WindowOver<Double> regrR2(Expression<? extends Number> arg1, Expression<? extends Number> arg2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.REGR_R2, (Expression<?>)arg1, (Expression<?>)arg2);
    }

    public static WindowOver<Double> regrAvgx(Expression<? extends Number> arg1, Expression<? extends Number> arg2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.REGR_AVGX, (Expression<?>)arg1, (Expression<?>)arg2);
    }

    public static WindowOver<Double> regrAvgy(Expression<? extends Number> arg1, Expression<? extends Number> arg2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.REGR_AVGY, (Expression<?>)arg1, (Expression<?>)arg2);
    }

    public static WindowOver<Double> regrSxx(Expression<? extends Number> arg1, Expression<? extends Number> arg2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.REGR_SXX, (Expression<?>)arg1, (Expression<?>)arg2);
    }

    public static WindowOver<Double> regrSyy(Expression<? extends Number> arg1, Expression<? extends Number> arg2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.REGR_SYY, (Expression<?>)arg1, (Expression<?>)arg2);
    }

    public static WindowOver<Double> regrSxy(Expression<? extends Number> arg1, Expression<? extends Number> arg2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.REGR_SXY, (Expression<?>)arg1, (Expression<?>)arg2);
    }

    public static WindowOver<Double> cumeDist() {
        return cumeDist;
    }

    public static WithinGroup<Double> cumeDist(Object ... args) {
        return SQLExpressions.cumeDist(SQLExpressions.convertToExpressions(args));
    }

    public static WithinGroup<Double> cumeDist(Expression<?> ... args) {
        return new WithinGroup<Double>(Double.class, (Operator)SQLOps.CUMEDIST2, args);
    }

    public static WindowOver<Double> corr(Expression<? extends Number> expr1, Expression<? extends Number> expr2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.CORR, (Expression<?>)expr1, (Expression<?>)expr2);
    }

    public static WindowOver<Double> covarPop(Expression<? extends Number> expr1, Expression<? extends Number> expr2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.COVARPOP, (Expression<?>)expr1, (Expression<?>)expr2);
    }

    public static WindowOver<Double> covarSamp(Expression<? extends Number> expr1, Expression<? extends Number> expr2) {
        return new WindowOver<Double>(Double.class, (Operator)SQLOps.COVARSAMP, (Expression<?>)expr1, (Expression<?>)expr2);
    }

    public static <T> WindowOver<T> ratioToReport(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.RATIOTOREPORT, (Expression<?>)expr);
    }

    public static WindowOver<Long> rowNumber() {
        return rowNumber;
    }

    public static <T extends Number> WindowOver<T> stddev(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.STDDEV, (Expression<?>)expr);
    }

    public static <T extends Number> WindowOver<T> stddevDistinct(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.STDDEV_DISTINCT, (Expression<?>)expr);
    }

    public static <T extends Number> WindowOver<T> stddevPop(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.STDDEVPOP, (Expression<?>)expr);
    }

    public static <T extends Number> WindowOver<T> stddevSamp(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.STDDEVSAMP, (Expression<?>)expr);
    }

    public static <T extends Number> WindowOver<T> variance(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.VARIANCE, (Expression<?>)expr);
    }

    public static <T extends Number> WindowOver<T> varPop(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.VARPOP, (Expression<?>)expr);
    }

    public static <T extends Number> WindowOver<T> varSamp(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.VARSAMP, (Expression<?>)expr);
    }

    public static <T> WindowOver<T> firstValue(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.FIRSTVALUE, (Expression<?>)expr);
    }

    public static <T> WindowOver<T> lastValue(Expression<T> expr) {
        return new WindowOver<T>(expr.getType(), (Operator)SQLOps.LASTVALUE, (Expression<?>)expr);
    }

    public static StringExpression left(Expression<String> lhs, int rhs) {
        return SQLExpressions.left(lhs, ConstantImpl.create(rhs));
    }

    public static StringExpression right(Expression<String> lhs, int rhs) {
        return SQLExpressions.right(lhs, ConstantImpl.create(rhs));
    }

    public static StringExpression left(Expression<String> lhs, Expression<Integer> rhs) {
        return Expressions.stringOperation(Ops.StringOps.LEFT, lhs, rhs);
    }

    public static StringExpression right(Expression<String> lhs, Expression<Integer> rhs) {
        return Expressions.stringOperation(Ops.StringOps.RIGHT, lhs, rhs);
    }

    public static StringExpression groupConcat(Expression<String> expr) {
        return Expressions.stringOperation(SQLOps.GROUP_CONCAT, expr);
    }

    public static StringExpression groupConcat(Expression<String> expr, String separator) {
        return Expressions.stringOperation(SQLOps.GROUP_CONCAT2, expr, Expressions.constant(separator));
    }

    private SQLExpressions() {
    }

    static {
        DATE_ADD_OPS.put(DatePart.year, Ops.DateTimeOps.ADD_YEARS);
        DATE_ADD_OPS.put(DatePart.month, Ops.DateTimeOps.ADD_MONTHS);
        DATE_ADD_OPS.put(DatePart.week, Ops.DateTimeOps.ADD_WEEKS);
        DATE_ADD_OPS.put(DatePart.day, Ops.DateTimeOps.ADD_DAYS);
        DATE_ADD_OPS.put(DatePart.hour, Ops.DateTimeOps.ADD_HOURS);
        DATE_ADD_OPS.put(DatePart.minute, Ops.DateTimeOps.ADD_MINUTES);
        DATE_ADD_OPS.put(DatePart.second, Ops.DateTimeOps.ADD_SECONDS);
        DATE_ADD_OPS.put(DatePart.millisecond, null);
        DATE_DIFF_OPS.put(DatePart.year, Ops.DateTimeOps.DIFF_YEARS);
        DATE_DIFF_OPS.put(DatePart.month, Ops.DateTimeOps.DIFF_MONTHS);
        DATE_DIFF_OPS.put(DatePart.week, Ops.DateTimeOps.DIFF_WEEKS);
        DATE_DIFF_OPS.put(DatePart.day, Ops.DateTimeOps.DIFF_DAYS);
        DATE_DIFF_OPS.put(DatePart.hour, Ops.DateTimeOps.DIFF_HOURS);
        DATE_DIFF_OPS.put(DatePart.minute, Ops.DateTimeOps.DIFF_MINUTES);
        DATE_DIFF_OPS.put(DatePart.second, Ops.DateTimeOps.DIFF_SECONDS);
        DATE_DIFF_OPS.put(DatePart.millisecond, null);
        DATE_TRUNC_OPS.put(DatePart.year, Ops.DateTimeOps.TRUNC_YEAR);
        DATE_TRUNC_OPS.put(DatePart.month, Ops.DateTimeOps.TRUNC_MONTH);
        DATE_TRUNC_OPS.put(DatePart.week, Ops.DateTimeOps.TRUNC_WEEK);
        DATE_TRUNC_OPS.put(DatePart.day, Ops.DateTimeOps.TRUNC_DAY);
        DATE_TRUNC_OPS.put(DatePart.hour, Ops.DateTimeOps.TRUNC_HOUR);
        DATE_TRUNC_OPS.put(DatePart.minute, Ops.DateTimeOps.TRUNC_MINUTE);
        DATE_TRUNC_OPS.put(DatePart.second, Ops.DateTimeOps.TRUNC_SECOND);
        cumeDist = new WindowOver<Double>(Double.class, SQLOps.CUMEDIST);
        rank = new WindowOver<Long>(Long.class, SQLOps.RANK);
        denseRank = new WindowOver<Long>(Long.class, SQLOps.DENSERANK);
        percentRank = new WindowOver<Double>(Double.class, SQLOps.PERCENTRANK);
        rowNumber = new WindowOver<Long>(Long.class, SQLOps.ROWNUMBER);
        all = Wildcard.all;
        countAll = Wildcard.count;
    }
}

