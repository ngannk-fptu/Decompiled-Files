/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.jackrabbit.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.QueryBuilder;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.json.JsonHandler;
import org.apache.jackrabbit.commons.json.JsonParser;

public class AuthorizableQueryManager {
    public static final int MAX_RESULT_COUNT = 2000;
    private final UserManager userManager;
    private final ValueFactory valueFactory;

    public AuthorizableQueryManager(UserManager userManager, ValueFactory valueFactory) {
        this.userManager = userManager;
        this.valueFactory = valueFactory;
    }

    public Iterator<Authorizable> execute(final String query) throws RepositoryException, IOException {
        try {
            return this.userManager.findAuthorizables(new Query(){

                @Override
                public <T> void build(QueryBuilder<T> builder) {
                    try {
                        builder.setLimit(0L, 2000L);
                        new QueryTranslator<T>(builder).translate(query);
                    }
                    catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
        }
        catch (IllegalArgumentException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw e;
        }
    }

    private class QueryTranslator<T>
    implements JsonHandler {
        private final QueryBuilder<T> queryBuilder;
        private final Stack<JsonHandler> handlers = new Stack();

        public QueryTranslator(QueryBuilder<T> queryBuilder) {
            this.queryBuilder = queryBuilder;
            this.handlers.push(new HandlerBase(){

                @Override
                public void object() {
                    QueryTranslator.this.handlers.push(new ClausesHandler());
                }
            });
        }

        public void translate(String query) throws IOException {
            new JsonParser(this).parse(query);
            if (this.handlers.size() != 1) {
                throw new IOException("Missing closing parenthesis");
            }
        }

        @Override
        public void object() throws IOException {
            this.handlers.peek().object();
        }

        @Override
        public void endObject() throws IOException {
            this.handlers.peek().endObject();
        }

        @Override
        public void array() throws IOException {
            this.handlers.peek().array();
        }

        @Override
        public void endArray() throws IOException {
            this.handlers.peek().endArray();
        }

        @Override
        public void key(String s) throws IOException {
            this.handlers.peek().key(s);
        }

        @Override
        public void value(String s) throws IOException {
            this.handlers.peek().value(s);
        }

        @Override
        public void value(boolean b) throws IOException {
            this.handlers.peek().value(b);
        }

        @Override
        public void value(long l) throws IOException {
            this.handlers.peek().value(l);
        }

        @Override
        public void value(double v) throws IOException {
            this.handlers.peek().value(v);
        }

        private Value valueFor(String s) {
            return AuthorizableQueryManager.this.valueFactory.createValue(s);
        }

        private Value valueFor(boolean b) {
            return AuthorizableQueryManager.this.valueFactory.createValue(b);
        }

        private Value valueFor(long l) {
            return AuthorizableQueryManager.this.valueFactory.createValue(l);
        }

        private Value valueFor(double v) {
            return AuthorizableQueryManager.this.valueFactory.createValue(v);
        }

        private class LimitHandler
        extends HandlerBase {
            private String currentKey;
            private Long offset;
            private Value bound;
            private Long max;

            private LimitHandler() {
            }

            @Override
            public void endObject() throws IOException {
                if (this.offset != null) {
                    QueryTranslator.this.queryBuilder.setLimit(this.offset, this.max == null ? -1L : this.max);
                } else if (this.bound != null) {
                    QueryTranslator.this.queryBuilder.setLimit(this.bound, this.max == null ? -1L : this.max);
                } else {
                    throw new IOException("Missing bound or offset");
                }
                QueryTranslator.this.handlers.pop();
            }

            @Override
            public void key(String s) throws IOException {
                this.currentKey = s;
            }

            @Override
            public void value(String s) throws IOException {
                if (!"bound".equals(this.currentKey)) {
                    throw new IOException("Unexpected: '" + this.currentKey + ':' + s + '\'');
                }
                this.bound = QueryTranslator.this.valueFor(s);
            }

            @Override
            public void value(boolean b) throws IOException {
                if (!"bound".equals(this.currentKey)) {
                    throw new IOException("Unexpected: '" + this.currentKey + ':' + b + '\'');
                }
                this.bound = QueryTranslator.this.valueFor(b);
            }

            @Override
            public void value(long l) throws IOException {
                if ("bound".equals(this.currentKey)) {
                    this.bound = QueryTranslator.this.valueFor(l);
                } else if ("offset".equals(this.currentKey)) {
                    this.offset = l;
                } else if ("max".equals(this.currentKey)) {
                    this.max = l;
                } else {
                    throw new IOException("Unexpected: '" + this.currentKey + ':' + l + '\'');
                }
            }

            @Override
            public void value(double v) throws IOException {
                if (!"bound".equals(this.currentKey)) {
                    throw new IOException("Unexpected: '" + this.currentKey + ':' + v + '\'');
                }
                this.bound = QueryTranslator.this.valueFor(v);
            }
        }

        private class OrderHandler
        extends HandlerBase {
            private String currentKey;
            private String property;
            private QueryBuilder.Direction direction;
            private boolean ignoreCase;

            private OrderHandler() {
                this.ignoreCase = true;
            }

            @Override
            public void endObject() throws IOException {
                if (this.property == null) {
                    throw new IOException("Missing property");
                }
                QueryTranslator.this.queryBuilder.setSortOrder(this.property, this.direction == null ? QueryBuilder.Direction.ASCENDING : this.direction, this.ignoreCase);
                QueryTranslator.this.handlers.pop();
            }

            @Override
            public void key(String s) throws IOException {
                this.currentKey = s;
            }

            @Override
            public void value(String s) throws IOException {
                if ("property".equals(this.currentKey)) {
                    this.property = s;
                } else if ("direction".equals(this.currentKey)) {
                    this.direction = this.directionFor(s);
                } else if ("ignoreCase".equals(this.currentKey)) {
                    this.ignoreCase = Boolean.valueOf(s);
                } else {
                    throw new IOException("Unexpected: '" + this.currentKey + ':' + s + '\'');
                }
            }

            private QueryBuilder.Direction directionFor(String direction) throws IOException {
                if ("asc".equals(direction)) {
                    return QueryBuilder.Direction.ASCENDING;
                }
                if ("desc".equals(direction)) {
                    return QueryBuilder.Direction.DESCENDING;
                }
                throw new IOException("Invalid direction '" + direction + '\'');
            }
        }

        private class RelOpHandler
        extends ConditionBase {
            private final String op;
            private String currentKey;
            private String property;
            private String pattern;
            private String expression;
            private Value value;
            private T condition;

            public RelOpHandler(String op) {
                this.op = op;
            }

            @Override
            public void endObject() throws IOException {
                if (this.property == null) {
                    throw new IOException("Property not set for condition '" + this.op + '\'');
                }
                if ("like".equals(this.op)) {
                    if (this.pattern == null) {
                        throw new IOException("Pattern not set for 'like' condition");
                    }
                    this.condition = QueryTranslator.this.queryBuilder.like(this.property, this.pattern);
                } else if ("contains".equals(this.op)) {
                    if (this.expression == null) {
                        throw new IOException("Expression not set for 'contains' condition");
                    }
                    this.condition = QueryTranslator.this.queryBuilder.contains(this.property, this.expression);
                } else {
                    if (this.value == null) {
                        throw new IOException("Value not set for '" + this.op + "' condition");
                    }
                    if ("eq".equals(this.op)) {
                        this.condition = QueryTranslator.this.queryBuilder.eq(this.property, this.value);
                    } else if ("neq".equals(this.op)) {
                        this.condition = QueryTranslator.this.queryBuilder.neq(this.property, this.value);
                    } else if ("lt".equals(this.op)) {
                        this.condition = QueryTranslator.this.queryBuilder.lt(this.property, this.value);
                    } else if ("le".equals(this.op)) {
                        this.condition = QueryTranslator.this.queryBuilder.le(this.property, this.value);
                    } else if ("ge".equals(this.op)) {
                        this.condition = QueryTranslator.this.queryBuilder.ge(this.property, this.value);
                    } else if ("gt".equals(this.op)) {
                        this.condition = QueryTranslator.this.queryBuilder.gt(this.property, this.value);
                    } else {
                        throw new IOException("Invalid condition: '" + this.op + '\'');
                    }
                }
                QueryTranslator.this.handlers.pop();
            }

            @Override
            public void key(String s) throws IOException {
                this.currentKey = s;
            }

            @Override
            public void value(String s) throws IOException {
                if ("property".equals(this.currentKey)) {
                    this.property = s;
                } else if ("pattern".equals(this.currentKey)) {
                    this.pattern = s;
                } else if ("expression".equals(this.currentKey)) {
                    this.expression = s;
                } else if ("value".equals(this.currentKey)) {
                    this.value = QueryTranslator.this.valueFor(s);
                } else {
                    throw new IOException("Expected one of 'property', 'pattern', 'expression', 'value' but found '" + this.currentKey + '\'');
                }
            }

            @Override
            public void value(boolean b) throws IOException {
                if (!"value".equals(this.currentKey)) {
                    throw new IOException("Expected 'value', found '" + this.currentKey + '\'');
                }
                this.value = QueryTranslator.this.valueFor(b);
            }

            @Override
            public void value(long l) throws IOException {
                if (!"value".equals(this.currentKey)) {
                    throw new IOException("Expected 'value', found '" + this.currentKey + '\'');
                }
                this.value = QueryTranslator.this.valueFor(l);
            }

            @Override
            public void value(double v) throws IOException {
                if (!"value".equals(this.currentKey)) {
                    throw new IOException("Expected 'value', found '" + this.currentKey + '\'');
                }
                this.value = QueryTranslator.this.valueFor(v);
            }

            @Override
            public T getCondition() {
                return this.condition;
            }
        }

        private class PrimitiveHandler
        extends ConditionBase {
            private String currentKey;
            private ConditionBase relOp;
            private ConditionBase not;
            private T condition;

            private PrimitiveHandler() {
            }

            @Override
            public void object() throws IOException {
                if (this.hasCondition()) {
                    throw new IOException("Condition on '" + this.currentKey + "' not allowed since another condition is already set");
                }
                if ("not".equals(this.currentKey)) {
                    this.not = new PrimitiveHandler();
                    QueryTranslator.this.handlers.push(this.not);
                } else {
                    this.relOp = new RelOpHandler(this.currentKey);
                    QueryTranslator.this.handlers.push(this.relOp);
                }
            }

            @Override
            public void endObject() throws IOException {
                if (!this.hasCondition()) {
                    throw new IOException("Missing term");
                }
                if (this.relOp != null) {
                    this.condition = this.relOp.getCondition();
                } else if (this.condition == null) {
                    this.condition = QueryTranslator.this.queryBuilder.not(this.not.getCondition());
                }
                QueryTranslator.this.handlers.pop();
            }

            @Override
            public void key(String s) throws IOException {
                this.currentKey = s;
            }

            @Override
            public void value(String s) throws IOException {
                if (this.hasCondition()) {
                    throw new IOException("Condition on '" + this.currentKey + "' not allowed since another condition is already set");
                }
                if ("named".equals(this.currentKey)) {
                    this.condition = QueryTranslator.this.queryBuilder.nameMatches(s);
                } else if ("exists".equals(this.currentKey)) {
                    this.condition = QueryTranslator.this.queryBuilder.exists(s);
                } else if ("impersonates".equals(this.currentKey)) {
                    this.condition = QueryTranslator.this.queryBuilder.impersonates(s);
                } else {
                    throw new IOException("Invalid condition '" + this.currentKey + '\'');
                }
            }

            private boolean hasCondition() {
                return this.condition != null || this.relOp != null || this.not != null;
            }

            @Override
            public T getCondition() {
                return this.condition;
            }
        }

        private class CompoundHandler
        extends ConditionBase {
            private final List<ConditionBase> memberHandlers;

            private CompoundHandler() {
                this.memberHandlers = new ArrayList<ConditionBase>();
            }

            @Override
            public void object() throws IOException {
                PrimitiveHandler memberHandler = new PrimitiveHandler();
                this.memberHandlers.add(memberHandler);
                QueryTranslator.this.handlers.push(memberHandler);
            }

            @Override
            public void endArray() throws IOException {
                if (this.memberHandlers.isEmpty()) {
                    throw new IOException("Empty search term");
                }
                QueryTranslator.this.handlers.pop();
            }

            @Override
            public T getCondition() {
                Iterator<ConditionBase> memberHandler = this.memberHandlers.iterator();
                Object condition = memberHandler.next().getCondition();
                while (memberHandler.hasNext()) {
                    condition = QueryTranslator.this.queryBuilder.or(condition, memberHandler.next().getCondition());
                }
                return condition;
            }
        }

        private abstract class ConditionBase
        extends HandlerBase {
            private ConditionBase() {
            }

            public abstract T getCondition();
        }

        private class ConditionHandler
        extends HandlerBase {
            private final List<ConditionBase> memberHandlers;

            private ConditionHandler() {
                this.memberHandlers = new ArrayList<ConditionBase>();
            }

            @Override
            public void object() throws IOException {
                PrimitiveHandler memberHandler = new PrimitiveHandler();
                this.memberHandlers.add(memberHandler);
                QueryTranslator.this.handlers.push(memberHandler);
            }

            @Override
            public void array() throws IOException {
                CompoundHandler memberHandler = new CompoundHandler();
                this.memberHandlers.add(memberHandler);
                QueryTranslator.this.handlers.push(memberHandler);
            }

            @Override
            public void endArray() throws IOException {
                if (this.memberHandlers.isEmpty()) {
                    throw new IOException("Empty search term");
                }
                Iterator<ConditionBase> memberHandler = this.memberHandlers.iterator();
                Object condition = memberHandler.next().getCondition();
                while (memberHandler.hasNext()) {
                    condition = QueryTranslator.this.queryBuilder.and(condition, memberHandler.next().getCondition());
                }
                QueryTranslator.this.queryBuilder.setCondition(condition);
                QueryTranslator.this.handlers.pop();
            }
        }

        private class ScopeHandler
        extends HandlerBase {
            private String currentKey;
            private String groupName;
            private Boolean declaredOnly;

            private ScopeHandler() {
            }

            @Override
            public void endObject() throws IOException {
                if (this.groupName == null) {
                    throw new IOException("Missing groupName");
                }
                QueryTranslator.this.queryBuilder.setScope(this.groupName, this.declaredOnly == null ? true : this.declaredOnly);
                QueryTranslator.this.handlers.pop();
            }

            @Override
            public void key(String s) throws IOException {
                this.currentKey = s;
            }

            @Override
            public void value(String s) throws IOException {
                if (!"groupName".equals(this.currentKey)) {
                    throw new IOException("Unexpected: '" + this.currentKey + ':' + s + '\'');
                }
                this.groupName = s;
            }

            @Override
            public void value(boolean b) throws IOException {
                if (!"declaredOnly".equals(this.currentKey)) {
                    throw new IOException("Unexpected: '" + this.currentKey + ':' + b + '\'');
                }
                this.declaredOnly = b;
            }
        }

        private class ClausesHandler
        extends HandlerBase {
            private String currentKey;

            private ClausesHandler() {
            }

            @Override
            public void object() throws IOException {
                QueryTranslator.this.handlers.push(this.handlerFor(this.currentKey));
            }

            @Override
            public void endObject() throws IOException {
                QueryTranslator.this.handlers.pop();
            }

            @Override
            public void array() throws IOException {
                QueryTranslator.this.handlers.push(this.handlerFor(this.currentKey));
            }

            @Override
            public void endArray() throws IOException {
                QueryTranslator.this.handlers.pop();
            }

            @Override
            public void key(String s) throws IOException {
                this.currentKey = s;
            }

            @Override
            public void value(String s) throws IOException {
                if (!"selector".equals(this.currentKey)) {
                    throw new IOException("String value '" + s + "' is invalid for '" + this.currentKey + '\'');
                }
                QueryTranslator.this.queryBuilder.setSelector(this.selectorFor(s));
            }

            private Class<? extends Authorizable> selectorFor(String selector) throws IOException {
                if ("user".equals(selector)) {
                    return User.class;
                }
                if ("group".equals(selector)) {
                    return Group.class;
                }
                if ("authorizable".equals(selector)) {
                    return Authorizable.class;
                }
                throw new IOException("Invalid selector '" + selector + '\'');
            }

            private JsonHandler handlerFor(String key) throws IOException {
                if ("scope".equals(key)) {
                    return new ScopeHandler();
                }
                if ("condition".equals(key)) {
                    return new ConditionHandler();
                }
                if ("order".equals(key) || "sort".equals(key)) {
                    return new OrderHandler();
                }
                if ("limit".equals(key)) {
                    return new LimitHandler();
                }
                throw new IOException("Invalid clause '" + key + '\'');
            }
        }

        private class HandlerBase
        implements JsonHandler {
            private HandlerBase() {
            }

            @Override
            public void object() throws IOException {
                throw new IOException("Syntax error: '{'");
            }

            @Override
            public void endObject() throws IOException {
                throw new IOException("Syntax error: '}'");
            }

            @Override
            public void array() throws IOException {
                throw new IOException("Syntax error: '['");
            }

            @Override
            public void endArray() throws IOException {
                throw new IOException("Syntax error: ']'");
            }

            @Override
            public void key(String s) throws IOException {
                throw new IOException("Syntax error: key '" + s + '\'');
            }

            @Override
            public void value(String s) throws IOException {
                throw new IOException("Syntax error: string '" + s + '\'');
            }

            @Override
            public void value(boolean b) throws IOException {
                throw new IOException("Syntax error: boolean '" + b + '\'');
            }

            @Override
            public void value(long l) throws IOException {
                throw new IOException("Syntax error: long '" + l + '\'');
            }

            @Override
            public void value(double v) throws IOException {
                throw new IOException("Syntax error: double '" + v + '\'');
            }
        }
    }
}

