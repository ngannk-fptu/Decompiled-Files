/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.version.Version;
import aQute.lib.exceptions.Exceptions;
import aQute.lib.strings.Strings;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class FilterParser {
    final Map<String, Expression> cache = new HashMap<String, Expression>();

    public Expression parse(String s) {
        Rover rover = new Rover();
        rover.s = s;
        rover.n = 0;
        return this.parse(rover);
    }

    public Expression parse(Requirement req) {
        String f = req.getDirectives().get("filter");
        if (f == null) {
            return Expression.FALSE;
        }
        return this.parse(f);
    }

    public Expression parse(Rover rover) {
        try {
            String cacheKey = rover.findExpr();
            Expression e = this.cache.get(cacheKey);
            if (e != null) {
                rover.n += cacheKey.length();
                return e;
            }
            rover.ws();
            char c = rover.current();
            if (c != '(') {
                throw new IllegalArgumentException("Expression must start with a '('");
            }
            rover.next();
            rover.ws();
            e = this.parse0(rover);
            rover.ws();
            c = rover.current();
            if (c != ')') {
                throw new IllegalArgumentException("Expression must end with a ')'");
            }
            rover.next();
            this.cache.put(cacheKey, e);
            return e;
        }
        catch (RuntimeException re) {
            throw new RuntimeException("Parsing failed: " + re.getMessage() + ":\n" + rover + "\n", re);
        }
    }

    Expression parse0(Rover rover) {
        rover.ws();
        switch (rover.next()) {
            case '&': {
                return And.make(this.parseExprs(rover));
            }
            case '|': {
                return Or.make(this.parseExprs(rover));
            }
            case '!': {
                return Not.make(this.parse(rover));
            }
        }
        --rover.n;
        String key = rover.getKey();
        char s = rover.next();
        if (s == '=') {
            String value = rover.getValue();
            if (value.indexOf(42) >= 0) {
                return new PatternExpression(key, value);
            }
            return SimpleExpression.make(key, Op.EQUAL, value);
        }
        char eq = rover.next();
        if (eq != '=') {
            throw new IllegalArgumentException("Expected an = after " + rover.current());
        }
        switch (s) {
            case '~': {
                return new ApproximateExpression(key, rover.getValue());
            }
            case '>': {
                return SimpleExpression.make(key, Op.GREATER_OR_EQUAL, rover.getValue());
            }
            case '<': {
                return SimpleExpression.make(key, Op.LESS_OR_EQUAL, rover.getValue());
            }
        }
        throw new IllegalArgumentException("Expected '~=', '>=', '<='");
    }

    private List<Expression> parseExprs(Rover rover) {
        ArrayList<Expression> exprs = new ArrayList<Expression>();
        rover.ws();
        while (rover.current() == '(') {
            Expression expr = this.parse(rover);
            exprs.add(expr);
            rover.ws();
        }
        return exprs;
    }

    public static String namespaceToCategory(String namespace) {
        String result;
        if ("osgi.wiring.package".equals(namespace)) {
            result = "Import-Package";
        } else if ("osgi.wiring.bundle".equals(namespace)) {
            result = "Require-Bundle";
        } else if ("osgi.wiring.host".equals(namespace)) {
            result = "Fragment-Host";
        } else if ("osgi.identity".equals(namespace)) {
            result = "ID";
        } else if ("osgi.content".equals(namespace)) {
            result = "Content";
        } else if ("osgi.extender".equals(namespace)) {
            result = "Extender";
        } else if ("osgi.service".equals(namespace)) {
            result = "Service";
        } else {
            if ("osgi.contract".equals(namespace)) {
                return "Contract";
            }
            result = namespace;
        }
        return result;
    }

    public static String toString(Requirement r) {
        try {
            StringBuilder sb = new StringBuilder();
            String category = FilterParser.namespaceToCategory(r.getNamespace());
            if (category != null && category.length() > 0) {
                sb.append(FilterParser.namespaceToCategory(category)).append(": ");
            }
            FilterParser fp = new FilterParser();
            String filter = r.getDirectives().get("filter");
            if (filter == null) {
                sb.append("<no filter>");
            } else {
                Expression parse = fp.parse(filter);
                sb.append(parse);
            }
            return sb.toString();
        }
        catch (Exception e) {
            return Exceptions.toString(e);
        }
    }

    public String simple(Resource resource) {
        if (resource == null) {
            return "<>";
        }
        List<Capability> capabilities = resource.getCapabilities("osgi.identity");
        if (capabilities.isEmpty()) {
            return resource.toString();
        }
        Capability c = capabilities.get(0);
        String bsn = (String)c.getAttributes().get("osgi.identity");
        Object version = c.getAttributes().get("version");
        if (version == null) {
            return bsn;
        }
        return bsn + ";version=" + version;
    }

    static class Rover {
        String s;
        int n = 0;

        Rover() {
        }

        char next() {
            return this.s.charAt(this.n++);
        }

        char wsNext() {
            this.ws();
            return this.next();
        }

        char current() {
            return this.s.charAt(this.n);
        }

        void ws() {
            while (Character.isWhitespace(this.current())) {
                ++this.n;
            }
        }

        String findExpr() {
            int nn = this.n;
            int level = 0;
            while (nn < this.s.length()) {
                char c = this.s.charAt(nn++);
                switch (c) {
                    case '(': {
                        ++level;
                        break;
                    }
                    case '\\': {
                        ++nn;
                        break;
                    }
                    case ')': {
                        if (--level != 0) break;
                        return this.s.substring(this.n, nn);
                    }
                }
            }
            return "";
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.s).append("\n");
            for (int i = 0; i < this.n; ++i) {
                sb.append(" ");
            }
            sb.append("|");
            return sb.toString();
        }

        private boolean isOpChar(char s) {
            return s == '=' || s == '~' || s == '>' || s == '<' || s == '(' || s == ')';
        }

        String getKey() {
            int n = this.n;
            while (!this.isOpChar(this.current())) {
                this.next();
            }
            return this.s.substring(n, this.n).trim();
        }

        String getValue() {
            int n = this.n;
            while (this.current() != ')') {
                char c = this.next();
                if (c != '\\') continue;
                ++this.n;
            }
            return this.s.substring(n, this.n);
        }
    }

    public static abstract class ExpressionVisitor<T> {
        private final T defaultValue;

        public ExpressionVisitor(T defaultValue) {
            this.defaultValue = defaultValue;
        }

        public T visit(RangeExpression expr) {
            return this.defaultValue;
        }

        public T visit(SimpleExpression expr) {
            return this.defaultValue;
        }

        public T visit(PackageExpression expr) {
            return this.defaultValue;
        }

        public T visit(HostExpression expr) {
            return this.defaultValue;
        }

        public T visit(BundleExpression expr) {
            return this.defaultValue;
        }

        public T visit(IdentityExpression expr) {
            return this.defaultValue;
        }

        public T visit(And expr) {
            return this.defaultValue;
        }

        public T visit(Or expr) {
            return this.defaultValue;
        }

        public T visit(Not expr) {
            return this.defaultValue;
        }

        public T visit(PatternExpression expr) {
            return this.defaultValue;
        }

        public T visit(ApproximateExpression expr) {
            return this.defaultValue;
        }

        public T visitTrue() {
            return this.defaultValue;
        }

        public T visitFalse() {
            return this.defaultValue;
        }
    }

    public static class ApproximateExpression
    extends SimpleExpression {
        public ApproximateExpression(String key, String value) {
            super(key, Op.EQUAL, value);
        }

        @Override
        protected boolean eval(Object scalar) {
            if (scalar instanceof String) {
                return ((String)scalar).trim().equalsIgnoreCase(this.value);
            }
            return false;
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class PatternExpression
    extends SimpleExpression {
        final Pattern pattern;

        public PatternExpression(String key, String value) {
            super(key, Op.EQUAL, value);
            value = Pattern.quote(value);
            this.pattern = Pattern.compile(value.replace("\\*", ".*"));
        }

        @Override
        protected boolean eval(Object scalar) {
            if (scalar instanceof String) {
                return this.pattern.matcher((String)scalar).matches();
            }
            return false;
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Not
    extends Expression {
        Expression expr;

        private Not(Expression expr) {
            this.expr = expr;
        }

        @Override
        public boolean eval(Map<String, ?> map) {
            return !this.expr.eval(map);
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        public static Expression make(Expression expr) {
            if (expr == TRUE) {
                return FALSE;
            }
            if (expr == FALSE) {
                return TRUE;
            }
            Expression notexpr = expr.not();
            if (notexpr != null) {
                return notexpr;
            }
            return new Not(expr);
        }

        @Override
        Expression not() {
            return this.expr;
        }

        @Override
        public void toString(StringBuilder sb) {
            sb.append("!(");
            this.expr.toString(sb);
            sb.append(")");
        }
    }

    public static class Or
    extends SubExpression {
        private Or(List<Expression> exprs) {
            this.expressions = exprs.toArray(new Expression[0]);
        }

        @Override
        public boolean eval(Map<String, ?> map) {
            for (Expression e : this.expressions) {
                if (!e.eval(map)) continue;
                return true;
            }
            return false;
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        static Expression make(List<Expression> exprs) {
            Iterator<Expression> i = exprs.iterator();
            while (i.hasNext()) {
                Expression e = i.next();
                if (e == TRUE) {
                    return TRUE;
                }
                if (e != FALSE) continue;
                i.remove();
            }
            if (exprs.size() == 0) {
                return FALSE;
            }
            if (exprs.size() == 1) {
                return exprs.get(0);
            }
            return new Or(exprs);
        }

        @Override
        public void toString(StringBuilder sb) {
            sb.append("|");
            super.toString(sb);
        }
    }

    public static class And
    extends SubExpression {
        private And(List<Expression> exprs) {
            this.expressions = exprs.toArray(new Expression[0]);
        }

        @Override
        public boolean eval(Map<String, ?> map) {
            for (Expression e : this.expressions) {
                if (e.eval(map)) continue;
                return false;
            }
            return true;
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        static Expression make(List<Expression> exprs) {
            Iterator<Expression> i = exprs.iterator();
            while (i.hasNext()) {
                Expression e = i.next();
                if (e == FALSE) {
                    return FALSE;
                }
                if (e != TRUE) continue;
                i.remove();
            }
            if (exprs.size() == 0) {
                return TRUE;
            }
            SimpleExpression lower = null;
            SimpleExpression higher = null;
            WithRangeExpression wre = null;
            for (Expression e : exprs) {
                if (e instanceof WithRangeExpression) {
                    wre = (WithRangeExpression)e;
                    continue;
                }
                if (!(e instanceof SimpleExpression)) continue;
                SimpleExpression se = (SimpleExpression)e;
                if (!se.key.equals("version") && !se.key.equals("bundle-version")) continue;
                if (se.op == Op.GREATER || se.op == Op.GREATER_OR_EQUAL) {
                    lower = se;
                    continue;
                }
                if (se.op != Op.LESS && se.op != Op.LESS_OR_EQUAL) continue;
                higher = se;
            }
            RangeExpression range = null;
            if (lower != null || higher != null) {
                if (lower != null && higher != null) {
                    exprs.remove(lower);
                    exprs.remove(higher);
                    range = new RangeExpression("version", lower, higher);
                } else if (lower != null && lower.op == Op.GREATER_OR_EQUAL && higher == null) {
                    exprs.remove(lower);
                    range = new RangeExpression("version", lower, null);
                }
            }
            if (range != null) {
                if (wre != null) {
                    wre.range = range;
                } else {
                    exprs.add(range);
                }
            }
            if (exprs.size() == 1) {
                return exprs.get(0);
            }
            return new And(exprs);
        }

        @Override
        public void toString(StringBuilder sb) {
            if (this.expressions != null && this.expressions.length > 0 && this.expressions[0] instanceof WithRangeExpression) {
                sb.append(this.expressions[0]);
                for (int i = 1; i < this.expressions.length; ++i) {
                    sb.append("; ");
                    this.expressions[i].toString(sb);
                }
                return;
            }
            sb.append("&");
            super.toString(sb);
        }
    }

    public static abstract class SubExpression
    extends Expression {
        Expression[] expressions;

        @Override
        void toString(StringBuilder sb) {
            for (Expression e : this.expressions) {
                sb.append("(");
                e.toString(sb);
                sb.append(")");
            }
        }

        public Expression[] getExpressions() {
            return this.expressions;
        }

        @Override
        public String query() {
            if (this.expressions == null || this.expressions.length == 0) {
                return null;
            }
            if (this.expressions[0] instanceof WithRangeExpression) {
                return this.expressions[0].query();
            }
            ArrayList<String> words = new ArrayList<String>();
            for (Expression e : this.expressions) {
                String query = e.query();
                if (query == null) continue;
                words.add(query);
            }
            return Strings.join(" ", words);
        }
    }

    public static class IdentityExpression
    extends WithRangeExpression {
        final String identity;

        public IdentityExpression(String value) {
            this.identity = value;
        }

        @Override
        public boolean eval(Map<String, ?> map) {
            String p = (String)map.get("osgi.identity");
            if (p == null) {
                return false;
            }
            return this.identity.equals(p);
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        @Override
        void toString(StringBuilder sb) {
            sb.append(this.identity);
            super.toString(sb);
        }

        public String getSymbolicName() {
            return this.identity;
        }

        @Override
        public String query() {
            return "bsn:" + this.identity;
        }

        @Override
        public String printExcludingRange() {
            return this.identity;
        }
    }

    public static class BundleExpression
    extends WithRangeExpression {
        final String bundleName;

        public BundleExpression(String value) {
            this.bundleName = value;
        }

        @Override
        public boolean eval(Map<String, ?> map) {
            String p = (String)map.get("osgi.wiring.bundle");
            if (p == null) {
                return false;
            }
            return this.bundleName.equals(p) && super.eval(map);
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        @Override
        void toString(StringBuilder sb) {
            sb.append(this.bundleName);
            super.toString(sb);
        }

        @Override
        public String query() {
            return "bsn:" + this.bundleName;
        }

        @Override
        public String printExcludingRange() {
            return this.bundleName;
        }
    }

    public static class HostExpression
    extends WithRangeExpression {
        final String hostName;

        public HostExpression(String value) {
            this.hostName = value;
        }

        @Override
        public boolean eval(Map<String, ?> map) {
            String p = (String)map.get("osgi.wiring.host");
            if (p == null) {
                return false;
            }
            return this.hostName.equals(p) && super.eval(map);
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        @Override
        void toString(StringBuilder sb) {
            sb.append(this.hostName);
            super.toString(sb);
        }

        public String getHostName() {
            return this.hostName;
        }

        @Override
        public String query() {
            return "bsn:" + this.hostName;
        }

        @Override
        public String printExcludingRange() {
            return this.hostName;
        }
    }

    public static class PackageExpression
    extends WithRangeExpression {
        final String packageName;

        public PackageExpression(String value) {
            this.packageName = value;
        }

        @Override
        public boolean eval(Map<String, ?> map) {
            String p = (String)map.get("osgi.wiring.package");
            if (p == null) {
                return false;
            }
            return this.packageName.equals(p) && super.eval(map);
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        @Override
        void toString(StringBuilder sb) {
            sb.append(this.packageName);
            super.toString(sb);
        }

        public String getPackageName() {
            return this.packageName;
        }

        @Override
        public String query() {
            return "p:" + this.packageName;
        }

        @Override
        public String printExcludingRange() {
            return this.packageName;
        }
    }

    public static abstract class WithRangeExpression
    extends Expression {
        RangeExpression range;

        @Override
        public boolean eval(Map<String, ?> map) {
            return this.range == null || this.range.eval(map);
        }

        @Override
        void toString(StringBuilder sb) {
            if (this.range == null) {
                return;
            }
            sb.append("; ");
            this.range.toString(sb);
        }

        public RangeExpression getRangeExpression() {
            return this.range;
        }

        public abstract String printExcludingRange();
    }

    public static class SimpleExpression
    extends Expression {
        final Op op;
        final String key;
        final String value;
        transient Object cached;

        public SimpleExpression(String key, Op op, String value) {
            this.key = key;
            this.op = op;
            this.value = value;
        }

        @Override
        public boolean eval(Map<String, ?> map) {
            Object target = map.get(this.key);
            if (target instanceof Iterable) {
                for (Object scalar : (Iterable)target) {
                    if (!this.eval(scalar)) continue;
                    return true;
                }
                return false;
            }
            if (target.getClass().isArray()) {
                int l = Array.getLength(target);
                for (int i = 0; i < l; ++i) {
                    if (!this.eval(Array.get(target, i))) continue;
                    return true;
                }
                return false;
            }
            return this.eval(target);
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        protected boolean eval(Object scalar) {
            if (this.cached == null || this.cached.getClass() != scalar.getClass()) {
                Class<?> scalarClass = scalar.getClass();
                if (scalarClass == String.class) {
                    this.cached = this.value;
                } else if (scalarClass == Byte.class) {
                    this.cached = Byte.parseByte(this.value);
                } else if (scalarClass == Short.class) {
                    this.cached = Short.parseShort(this.value);
                } else if (scalarClass == Integer.class) {
                    this.cached = Integer.parseInt(this.value);
                } else if (scalarClass == Long.class) {
                    this.cached = Long.parseLong(this.value);
                } else if (scalarClass == Float.class) {
                    this.cached = Float.valueOf(Float.parseFloat(this.value));
                } else if (scalarClass == Double.class) {
                    this.cached = Double.parseDouble(this.value);
                } else if (scalarClass == Character.class) {
                    this.cached = this.value;
                } else {
                    try {
                        Method factory = scalarClass.getMethod("valueOf", String.class);
                        this.cached = factory.invoke(null, this.value);
                    }
                    catch (Exception e) {
                        try {
                            Constructor<?> constructor = scalarClass.getConstructor(String.class);
                            this.cached = constructor.newInstance(this.value);
                        }
                        catch (Exception e1) {
                            this.cached = this.value;
                        }
                    }
                }
            }
            if (this.op == Op.EQUAL) {
                return this.cached == scalar || this.cached.equals(scalar);
            }
            if (this.op == Op.NOT_EQUAL) {
                return !this.cached.equals(scalar);
            }
            if (this.cached instanceof Comparable) {
                int result = ((Comparable)scalar).compareTo(this.cached);
                switch (this.op) {
                    case LESS: {
                        return result < 0;
                    }
                    case LESS_OR_EQUAL: {
                        return result <= 0;
                    }
                    case GREATER: {
                        return result > 0;
                    }
                    case GREATER_OR_EQUAL: {
                        return result >= 0;
                    }
                }
            }
            return false;
        }

        static Expression make(String key, Op op, String value) {
            if (op == Op.EQUAL) {
                if ("osgi.wiring.bundle".equals(key)) {
                    return new BundleExpression(value);
                }
                if ("osgi.wiring.host".equals(key)) {
                    return new HostExpression(value);
                }
                if ("osgi.wiring.package".equals(key)) {
                    return new PackageExpression(value);
                }
                if ("osgi.identity".equals(key)) {
                    return new IdentityExpression(value);
                }
            }
            return new SimpleExpression(key, op, value);
        }

        @Override
        Expression not() {
            Op alt = this.op.not();
            if (alt == null) {
                return null;
            }
            return new SimpleExpression(this.key, alt, this.value);
        }

        @Override
        public void toString(StringBuilder sb) {
            sb.append(this.key).append(this.op.toString()).append(this.value);
        }

        @Override
        public String query() {
            return this.value;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public Op getOp() {
            return this.op;
        }
    }

    public static class RangeExpression
    extends SimpleExpression {
        final SimpleExpression low;
        final SimpleExpression high;

        public RangeExpression(String key, SimpleExpression low, SimpleExpression high) {
            super(key, Op.RANGE, null);
            this.low = low;
            this.high = high;
        }

        @Override
        protected boolean eval(Object scalar) {
            return !(this.low != null && !this.low.eval(scalar) || this.high != null && !this.high.eval(scalar));
        }

        @Override
        public <T> T visit(ExpressionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        static Expression make(String key, SimpleExpression low, SimpleExpression high) {
            if (key.indexOf("version") >= 0) {
                try {
                    Version a = Version.parseVersion(low.value);
                    Version b = Version.parseVersion(high.value);
                    if (a.compareTo(b) > 0) {
                        return FALSE;
                    }
                    if (a.equals(Version.LOWEST) && b.equals(Version.HIGHEST)) {
                        return TRUE;
                    }
                    if (b.equals(Version.HIGHEST)) {
                        return low;
                    }
                    if (a.equals(Version.LOWEST)) {
                        return high;
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            return new RangeExpression(key, low, high);
        }

        public String getRangeString() {
            StringBuilder sb = new StringBuilder();
            if (this.low != null) {
                if (this.high == null) {
                    sb.append(this.low.value);
                } else {
                    if (this.low.op == Op.GREATER) {
                        sb.append("(");
                    } else {
                        sb.append("[");
                    }
                    sb.append(this.low.value);
                }
            }
            if (this.high != null) {
                sb.append(",");
                if (this.low == null) {
                    sb.append("[0.0.0,");
                }
                sb.append(this.high.value);
                if (this.high.op == Op.LESS) {
                    sb.append(")");
                } else {
                    sb.append("]");
                }
            }
            return sb.toString();
        }

        @Override
        public void toString(StringBuilder sb) {
            sb.append(this.key).append("=").append(this.getRangeString());
        }

        public SimpleExpression getLow() {
            return this.low;
        }

        public SimpleExpression getHigh() {
            return this.high;
        }
    }

    public static abstract class Expression {
        static Expression TRUE = new Expression(){

            @Override
            public boolean eval(Map<String, ?> map) {
                return true;
            }

            @Override
            Expression not() {
                return FALSE;
            }

            @Override
            public <T> T visit(ExpressionVisitor<T> visitor) {
                return visitor.visitTrue();
            }

            @Override
            void toString(StringBuilder sb) {
                sb.append("true");
            }
        };
        static Expression FALSE = new Expression(){

            @Override
            public boolean eval(Map<String, ?> map) {
                return false;
            }

            @Override
            public <T> T visit(ExpressionVisitor<T> visitor) {
                return visitor.visitFalse();
            }

            @Override
            Expression not() {
                return TRUE;
            }

            @Override
            void toString(StringBuilder sb) {
                sb.append("false");
            }
        };

        public abstract boolean eval(Map<String, ?> var1);

        public abstract <T> T visit(ExpressionVisitor<T> var1);

        Expression not() {
            return null;
        }

        abstract void toString(StringBuilder var1);

        public String toString() {
            StringBuilder sb = new StringBuilder();
            this.toString(sb);
            return sb.toString();
        }

        public String query() {
            return null;
        }
    }

    public static enum Op {
        GREATER(">"),
        GREATER_OR_EQUAL(">="),
        LESS("<"),
        LESS_OR_EQUAL("<="),
        EQUAL("="),
        NOT_EQUAL("!="),
        RANGE("..");

        private String symbol;

        private Op(String s) {
            this.symbol = s;
        }

        public Op not() {
            switch (this) {
                case GREATER: {
                    return LESS_OR_EQUAL;
                }
                case GREATER_OR_EQUAL: {
                    return LESS;
                }
                case LESS: {
                    return GREATER_OR_EQUAL;
                }
                case LESS_OR_EQUAL: {
                    return GREATER;
                }
                case EQUAL: {
                    return NOT_EQUAL;
                }
                case NOT_EQUAL: {
                    return EQUAL;
                }
            }
            return null;
        }

        public String toString() {
            return this.symbol;
        }
    }
}

