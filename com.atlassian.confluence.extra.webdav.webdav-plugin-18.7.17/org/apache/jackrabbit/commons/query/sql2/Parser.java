/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query.sql2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.BindVariableValue;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.JoinCondition;
import javax.jcr.query.qom.Literal;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.PropertyExistence;
import javax.jcr.query.qom.PropertyValue;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.Source;
import javax.jcr.query.qom.StaticOperand;
import org.apache.jackrabbit.commons.query.qom.JoinType;
import org.apache.jackrabbit.commons.query.qom.Operator;

public class Parser {
    private static final int CHAR_END = -1;
    private static final int CHAR_VALUE = 2;
    private static final int CHAR_QUOTED = 3;
    private static final int CHAR_NAME = 4;
    private static final int CHAR_SPECIAL_1 = 5;
    private static final int CHAR_SPECIAL_2 = 6;
    private static final int CHAR_STRING = 7;
    private static final int CHAR_DECIMAL = 8;
    private static final int KEYWORD = 1;
    private static final int IDENTIFIER = 2;
    private static final int PARAMETER = 3;
    private static final int END = 4;
    private static final int VALUE = 5;
    private static final int MINUS = 12;
    private static final int PLUS = 13;
    private static final int OPEN = 14;
    private static final int CLOSE = 15;
    private String statement;
    private char[] statementChars;
    private int[] characterTypes;
    private int parseIndex;
    private int currentTokenType;
    private String currentToken;
    private boolean currentTokenQuoted;
    private Value currentValue;
    private ArrayList<String> expected;
    private HashMap<String, BindVariableValue> bindVariables;
    private ArrayList<Selector> selectors;
    private boolean allowTextLiterals = true;
    private boolean allowNumberLiterals = true;
    private QueryObjectModelFactory factory;
    private ValueFactory valueFactory;

    public Parser(QueryObjectModelFactory factory, ValueFactory valueFactory) {
        this.factory = factory;
        this.valueFactory = valueFactory;
    }

    public QueryObjectModel createQueryObjectModel(String query) throws RepositoryException {
        this.initialize(query);
        this.selectors = new ArrayList();
        this.expected = new ArrayList();
        this.bindVariables = new HashMap();
        this.read();
        this.read("SELECT");
        int columnParseIndex = this.parseIndex;
        ArrayList<ColumnOrWildcard> list = this.parseColumns();
        this.read("FROM");
        Source source = this.parseSource();
        Column[] columnArray = this.resolveColumns(columnParseIndex, list);
        Constraint constraint = null;
        if (this.readIf("WHERE")) {
            constraint = this.parseConstraint();
        }
        Ordering[] orderings = null;
        if (this.readIf("ORDER")) {
            this.read("BY");
            orderings = this.parseOrder();
        }
        if (this.currentToken.length() > 0) {
            throw this.getSyntaxError("<end>");
        }
        return this.factory.createQuery(source, constraint, orderings, columnArray);
    }

    private Selector parseSelector() throws RepositoryException {
        String nodeTypeName = this.readName();
        if (this.readIf("AS")) {
            String selectorName = this.readName();
            return this.factory.selector(nodeTypeName, selectorName);
        }
        return this.factory.selector(nodeTypeName, nodeTypeName);
    }

    private String readName() throws RepositoryException {
        if (this.readIf("[")) {
            if (this.currentTokenType == 5) {
                Value value = this.readString();
                this.read("]");
                return value.getString();
            }
            int level = 1;
            StringBuilder buff = new StringBuilder();
            while (true) {
                if (this.isToken("]")) {
                    if (--level <= 0) {
                        break;
                    }
                } else if (this.isToken("[")) {
                    ++level;
                }
                buff.append(this.readAny());
            }
            this.read();
            return buff.toString();
        }
        return this.readAny();
    }

    private Source parseSource() throws RepositoryException {
        Selector selector = this.parseSelector();
        this.selectors.add(selector);
        Source source = selector;
        while (true) {
            JoinType type;
            if (this.readIf("RIGHT")) {
                this.read("OUTER");
                type = JoinType.RIGHT;
            } else if (this.readIf("LEFT")) {
                this.read("OUTER");
                type = JoinType.LEFT;
            } else {
                if (!this.readIf("INNER")) break;
                type = JoinType.INNER;
            }
            this.read("JOIN");
            selector = this.parseSelector();
            this.selectors.add(selector);
            this.read("ON");
            JoinCondition on = this.parseJoinCondition();
            source = type.join(this.factory, source, selector, on);
        }
        return source;
    }

    private JoinCondition parseJoinCondition() throws RepositoryException {
        boolean identifier = this.currentTokenType == 2;
        String name = this.readName();
        if (identifier && this.readIf("(")) {
            JoinCondition c;
            if ("ISSAMENODE".equalsIgnoreCase(name)) {
                String selector1 = this.readName();
                this.read(",");
                String selector2 = this.readName();
                c = this.readIf(",") ? this.factory.sameNodeJoinCondition(selector1, selector2, this.readPath()) : this.factory.sameNodeJoinCondition(selector1, selector2, ".");
            } else if ("ISCHILDNODE".equalsIgnoreCase(name)) {
                String childSelector = this.readName();
                this.read(",");
                c = this.factory.childNodeJoinCondition(childSelector, this.readName());
            } else if ("ISDESCENDANTNODE".equalsIgnoreCase(name)) {
                String descendantSelector = this.readName();
                this.read(",");
                c = this.factory.descendantNodeJoinCondition(descendantSelector, this.readName());
            } else {
                throw this.getSyntaxError("ISSAMENODE, ISCHILDNODE, or ISDESCENDANTNODE");
            }
            this.read(")");
            return c;
        }
        String selector1 = name;
        this.read(".");
        String property1 = this.readName();
        this.read("=");
        String selector2 = this.readName();
        this.read(".");
        return this.factory.equiJoinCondition(selector1, property1, selector2, this.readName());
    }

    private Constraint parseConstraint() throws RepositoryException {
        Constraint a = this.parseAnd();
        while (this.readIf("OR")) {
            a = this.factory.or(a, this.parseAnd());
        }
        return a;
    }

    private Constraint parseAnd() throws RepositoryException {
        Constraint a = this.parseCondition();
        while (this.readIf("AND")) {
            a = this.factory.and(a, this.parseCondition());
        }
        return a;
    }

    private Constraint parseCondition() throws RepositoryException {
        Constraint a;
        if (this.readIf("NOT")) {
            a = this.factory.not(this.parseConstraint());
        } else if (this.readIf("(")) {
            a = this.parseConstraint();
            this.read(")");
        } else if (this.currentTokenType == 2) {
            String identifier = this.readName();
            if (this.readIf("(")) {
                a = this.parseConditionFuntionIf(identifier);
                if (a == null) {
                    DynamicOperand op = this.parseExpressionFunction(identifier);
                    a = this.parseCondition(op);
                }
            } else {
                a = this.readIf(".") ? this.parseCondition(this.factory.propertyValue(identifier, this.readName())) : this.parseCondition(this.factory.propertyValue(this.getOnlySelectorName(identifier), identifier));
            }
        } else if ("[".equals(this.currentToken)) {
            String name = this.readName();
            a = this.readIf(".") ? this.parseCondition(this.factory.propertyValue(name, this.readName())) : this.parseCondition(this.factory.propertyValue(this.getOnlySelectorName(name), name));
        } else {
            throw this.getSyntaxError();
        }
        return a;
    }

    private Constraint parseCondition(DynamicOperand left) throws RepositoryException {
        Constraint c;
        if (this.readIf("=")) {
            c = Operator.EQ.comparison(this.factory, left, this.parseStaticOperand());
        } else if (this.readIf("<>")) {
            c = Operator.NE.comparison(this.factory, left, this.parseStaticOperand());
        } else if (this.readIf("<")) {
            c = Operator.LT.comparison(this.factory, left, this.parseStaticOperand());
        } else if (this.readIf(">")) {
            c = Operator.GT.comparison(this.factory, left, this.parseStaticOperand());
        } else if (this.readIf("<=")) {
            c = Operator.LE.comparison(this.factory, left, this.parseStaticOperand());
        } else if (this.readIf(">=")) {
            c = Operator.GE.comparison(this.factory, left, this.parseStaticOperand());
        } else if (this.readIf("LIKE")) {
            c = Operator.LIKE.comparison(this.factory, left, this.parseStaticOperand());
        } else if (this.readIf("IS")) {
            boolean not = this.readIf("NOT");
            this.read("NULL");
            if (!(left instanceof PropertyValue)) {
                throw this.getSyntaxError("propertyName (NOT NULL is only supported for properties)");
            }
            PropertyValue p = (PropertyValue)left;
            c = this.getPropertyExistence(p);
            if (!not) {
                c = this.factory.not(c);
            }
        } else if (this.readIf("NOT")) {
            if (this.readIf("IS")) {
                this.read("NULL");
                if (!(left instanceof PropertyValue)) {
                    throw new RepositoryException("Only property values can be tested for NOT IS NULL; got: " + left.getClass().getName());
                }
                PropertyValue pv = (PropertyValue)left;
                c = this.getPropertyExistence(pv);
            } else {
                this.read("LIKE");
                c = this.factory.not(Operator.LIKE.comparison(this.factory, left, this.parseStaticOperand()));
            }
        } else {
            throw this.getSyntaxError();
        }
        return c;
    }

    private PropertyExistence getPropertyExistence(PropertyValue p) throws InvalidQueryException, RepositoryException {
        return this.factory.propertyExistence(p.getSelectorName(), p.getPropertyName());
    }

    private Constraint parseConditionFuntionIf(String functionName) throws RepositoryException {
        Constraint c;
        if ("CONTAINS".equalsIgnoreCase(functionName)) {
            String name = this.readName();
            if (this.readIf(".")) {
                if (this.readIf("*")) {
                    this.read(",");
                    c = this.factory.fullTextSearch(name, null, this.parseStaticOperand());
                } else {
                    String selector = name;
                    name = this.readName();
                    this.read(",");
                    c = this.factory.fullTextSearch(selector, name, this.parseStaticOperand());
                }
            } else {
                this.read(",");
                c = this.factory.fullTextSearch(this.getOnlySelectorName(name), name, this.parseStaticOperand());
            }
        } else if ("ISSAMENODE".equalsIgnoreCase(functionName)) {
            String name = this.readName();
            c = this.readIf(",") ? this.factory.sameNode(name, this.readPath()) : this.factory.sameNode(this.getOnlySelectorName(name), name);
        } else if ("ISCHILDNODE".equalsIgnoreCase(functionName)) {
            String name = this.readName();
            c = this.readIf(",") ? this.factory.childNode(name, this.readPath()) : this.factory.childNode(this.getOnlySelectorName(name), name);
        } else if ("ISDESCENDANTNODE".equalsIgnoreCase(functionName)) {
            String name = this.readName();
            c = this.readIf(",") ? this.factory.descendantNode(name, this.readPath()) : this.factory.descendantNode(this.getOnlySelectorName(name), name);
        } else {
            return null;
        }
        this.read(")");
        return c;
    }

    private String readPath() throws RepositoryException {
        return this.readName();
    }

    private DynamicOperand parseDynamicOperand() throws RepositoryException {
        boolean identifier = this.currentTokenType == 2;
        String name = this.readName();
        if (identifier && this.readIf("(")) {
            return this.parseExpressionFunction(name);
        }
        return this.parsePropertyValue(name);
    }

    private DynamicOperand parseExpressionFunction(String functionName) throws RepositoryException {
        DynamicOperand op;
        if ("LENGTH".equalsIgnoreCase(functionName)) {
            op = this.factory.length(this.parsePropertyValue(this.readName()));
        } else if ("NAME".equalsIgnoreCase(functionName)) {
            op = this.isToken(")") ? this.factory.nodeName(this.getOnlySelectorName("NAME()")) : this.factory.nodeName(this.readName());
        } else if ("LOCALNAME".equalsIgnoreCase(functionName)) {
            op = this.isToken(")") ? this.factory.nodeLocalName(this.getOnlySelectorName("LOCALNAME()")) : this.factory.nodeLocalName(this.readName());
        } else if ("SCORE".equalsIgnoreCase(functionName)) {
            op = this.isToken(")") ? this.factory.fullTextSearchScore(this.getOnlySelectorName("SCORE()")) : this.factory.fullTextSearchScore(this.readName());
        } else if ("LOWER".equalsIgnoreCase(functionName)) {
            op = this.factory.lowerCase(this.parseDynamicOperand());
        } else if ("UPPER".equalsIgnoreCase(functionName)) {
            op = this.factory.upperCase(this.parseDynamicOperand());
        } else {
            throw this.getSyntaxError("LENGTH, NAME, LOCALNAME, SCORE, LOWER, UPPER, or CAST");
        }
        this.read(")");
        return op;
    }

    private PropertyValue parsePropertyValue(String name) throws RepositoryException {
        if (this.readIf(".")) {
            return this.factory.propertyValue(name, this.readName());
        }
        return this.factory.propertyValue(this.getOnlySelectorName(name), name);
    }

    private StaticOperand parseStaticOperand() throws RepositoryException {
        if (this.currentTokenType == 13) {
            this.read();
        } else if (this.currentTokenType == 12) {
            this.read();
            if (this.currentTokenType != 5) {
                throw this.getSyntaxError("number");
            }
            int valueType = this.currentValue.getType();
            switch (valueType) {
                case 3: {
                    this.currentValue = this.valueFactory.createValue(-this.currentValue.getLong());
                    break;
                }
                case 4: {
                    this.currentValue = this.valueFactory.createValue(-this.currentValue.getDouble());
                    break;
                }
                case 6: {
                    this.currentValue = this.valueFactory.createValue(!this.currentValue.getBoolean());
                    break;
                }
                case 12: {
                    this.currentValue = this.valueFactory.createValue(this.currentValue.getDecimal().negate());
                    break;
                }
                default: {
                    throw this.getSyntaxError("Illegal operation: -" + this.currentValue);
                }
            }
        }
        if (this.currentTokenType == 5) {
            Literal literal = this.getUncastLiteral(this.currentValue);
            this.read();
            return literal;
        }
        if (this.currentTokenType == 3) {
            BindVariableValue var;
            this.read();
            String name = this.readName();
            if (this.readIf(":")) {
                name = name + ":" + this.readName();
            }
            if ((var = this.bindVariables.get(name)) == null) {
                var = this.factory.bindVariable(name);
                this.bindVariables.put(name, var);
            }
            return var;
        }
        if (this.readIf("TRUE")) {
            Literal literal = this.getUncastLiteral(this.valueFactory.createValue(true));
            return literal;
        }
        if (this.readIf("FALSE")) {
            Literal literal = this.getUncastLiteral(this.valueFactory.createValue(false));
            return literal;
        }
        if (this.readIf("CAST")) {
            this.read("(");
            StaticOperand op = this.parseStaticOperand();
            if (!(op instanceof Literal)) {
                throw this.getSyntaxError("literal");
            }
            Literal literal = (Literal)op;
            Value value = literal.getLiteralValue();
            this.read("AS");
            value = this.parseCastAs(value);
            this.read(")");
            literal = this.factory.literal(value);
            return literal;
        }
        throw this.getSyntaxError("static operand");
    }

    private Literal getUncastLiteral(Value value) throws RepositoryException {
        return this.factory.literal(value);
    }

    private Value parseCastAs(Value value) throws RepositoryException {
        if (this.readIf("STRING")) {
            return this.valueFactory.createValue(value.getString());
        }
        if (this.readIf("BINARY")) {
            return this.valueFactory.createValue(value.getBinary());
        }
        if (this.readIf("DATE")) {
            return this.valueFactory.createValue(value.getDate());
        }
        if (this.readIf("LONG")) {
            return this.valueFactory.createValue(value.getLong());
        }
        if (this.readIf("DOUBLE")) {
            return this.valueFactory.createValue(value.getDouble());
        }
        if (this.readIf("DECIMAL")) {
            return this.valueFactory.createValue(value.getDecimal());
        }
        if (this.readIf("BOOLEAN")) {
            return this.valueFactory.createValue(value.getBoolean());
        }
        if (this.readIf("NAME")) {
            return this.valueFactory.createValue(value.getString(), 7);
        }
        if (this.readIf("PATH")) {
            return this.valueFactory.createValue(value.getString(), 8);
        }
        if (this.readIf("REFERENCE")) {
            return this.valueFactory.createValue(value.getString(), 9);
        }
        if (this.readIf("WEAKREFERENCE")) {
            return this.valueFactory.createValue(value.getString(), 10);
        }
        if (this.readIf("URI")) {
            return this.valueFactory.createValue(value.getString(), 11);
        }
        throw this.getSyntaxError("data type (STRING|BINARY|...)");
    }

    private Ordering[] parseOrder() throws RepositoryException {
        ArrayList<Ordering> orderList = new ArrayList<Ordering>();
        do {
            Ordering ordering;
            DynamicOperand op = this.parseDynamicOperand();
            if (this.readIf("DESC")) {
                ordering = this.factory.descending(op);
            } else {
                this.readIf("ASC");
                ordering = this.factory.ascending(op);
            }
            orderList.add(ordering);
        } while (this.readIf(","));
        Ordering[] orderings = new Ordering[orderList.size()];
        orderList.toArray(orderings);
        return orderings;
    }

    private ArrayList<ColumnOrWildcard> parseColumns() throws RepositoryException {
        ArrayList<ColumnOrWildcard> list = new ArrayList<ColumnOrWildcard>();
        if (this.readIf("*")) {
            list.add(new ColumnOrWildcard());
        } else {
            do {
                ColumnOrWildcard column = new ColumnOrWildcard();
                column.propertyName = this.readName();
                if (this.readIf(".")) {
                    column.selectorName = column.propertyName;
                    if (this.readIf("*")) {
                        column.propertyName = null;
                    } else {
                        column.propertyName = this.readName();
                        column.columnName = this.readIf("AS") ? this.readName() : column.selectorName + "." + column.propertyName;
                    }
                } else if (this.readIf("AS")) {
                    column.columnName = this.readName();
                }
                list.add(column);
            } while (this.readIf(","));
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Column[] resolveColumns(int columnParseIndex, ArrayList<ColumnOrWildcard> list) throws RepositoryException {
        int oldParseIndex = this.parseIndex;
        this.parseIndex = columnParseIndex;
        try {
            ArrayList<Column> columns = new ArrayList<Column>();
            for (ColumnOrWildcard c : list) {
                if (c.propertyName == null) {
                    for (Selector selector : this.selectors) {
                        if (c.selectorName != null && !c.selectorName.equals(selector.getSelectorName())) continue;
                        Column column = this.factory.column(selector.getSelectorName(), null, null);
                        columns.add(column);
                    }
                    continue;
                }
                Column column = c.selectorName != null ? this.factory.column(c.selectorName, c.propertyName, c.columnName) : (c.columnName != null ? this.factory.column(this.getOnlySelectorName(c.propertyName), c.propertyName, c.columnName) : this.factory.column(this.getOnlySelectorName(c.propertyName), c.propertyName, c.propertyName));
                columns.add(column);
            }
            Column[] array = new Column[columns.size()];
            columns.toArray(array);
            Column[] columnArray = array;
            return columnArray;
        }
        finally {
            this.parseIndex = oldParseIndex;
        }
    }

    private boolean readIf(String token) throws RepositoryException {
        if (this.isToken(token)) {
            this.read();
            return true;
        }
        return false;
    }

    private boolean isToken(String token) {
        boolean result;
        boolean bl = result = token.equalsIgnoreCase(this.currentToken) && !this.currentTokenQuoted;
        if (result) {
            return true;
        }
        this.addExpected(token);
        return false;
    }

    private void read(String expected) throws RepositoryException {
        if (!expected.equalsIgnoreCase(this.currentToken) || this.currentTokenQuoted) {
            throw this.getSyntaxError(expected);
        }
        this.read();
    }

    private String readAny() throws RepositoryException {
        if (this.currentTokenType == 4) {
            throw this.getSyntaxError("a token");
        }
        String s = this.currentTokenType == 5 ? this.currentValue.getString() : this.currentToken;
        this.read();
        return s;
    }

    private Value readString() throws RepositoryException {
        if (this.currentTokenType != 5) {
            throw this.getSyntaxError("string value");
        }
        Value value = this.currentValue;
        this.read();
        return value;
    }

    private void addExpected(String token) {
        if (this.expected != null) {
            this.expected.add(token);
        }
    }

    private void initialize(String query) throws InvalidQueryException {
        if (query == null) {
            query = "";
        }
        this.statement = query;
        int len = query.length() + 1;
        char[] command = new char[len];
        int[] types = new int[len];
        query.getChars(0, --len, command, 0);
        command[len] = 32;
        int startLoop = 0;
        for (int i = 0; i < len; ++i) {
            char c = command[i];
            int type = 0;
            switch (c) {
                case '$': 
                case '%': 
                case '(': 
                case ')': 
                case '*': 
                case '+': 
                case ',': 
                case '-': 
                case '/': 
                case ';': 
                case '?': 
                case '[': 
                case ']': 
                case '{': 
                case '}': {
                    type = 5;
                    break;
                }
                case '!': 
                case ':': 
                case '<': 
                case '=': 
                case '>': 
                case '|': {
                    type = 6;
                    break;
                }
                case '.': {
                    type = 8;
                    break;
                }
                case '\'': {
                    type = 7;
                    types[i] = 7;
                    startLoop = i;
                    while (command[++i] != '\'') {
                        this.checkRunOver(i, len, startLoop);
                    }
                    break;
                }
                case '\"': {
                    type = 3;
                    types[i] = 3;
                    startLoop = i;
                    while (command[++i] != '\"') {
                        this.checkRunOver(i, len, startLoop);
                    }
                    break;
                }
                case '_': {
                    type = 4;
                    break;
                }
                default: {
                    if (c >= 'a' && c <= 'z') {
                        type = 4;
                        break;
                    }
                    if (c >= 'A' && c <= 'Z') {
                        type = 4;
                        break;
                    }
                    if (c >= '0' && c <= '9') {
                        type = 2;
                        break;
                    }
                    if (!Character.isJavaIdentifierPart(c)) break;
                    type = 4;
                }
            }
            types[i] = (byte)type;
        }
        this.statementChars = command;
        types[len] = -1;
        this.characterTypes = types;
        this.parseIndex = 0;
    }

    private void checkRunOver(int i, int len, int startLoop) throws InvalidQueryException {
        if (i >= len) {
            this.parseIndex = startLoop;
            throw this.getSyntaxError();
        }
    }

    private void read() throws RepositoryException {
        this.currentTokenQuoted = false;
        if (this.expected != null) {
            this.expected.clear();
        }
        int[] types = this.characterTypes;
        int i = this.parseIndex;
        int type = types[i];
        while (type == 0) {
            type = types[++i];
        }
        int start = i;
        char[] chars = this.statementChars;
        char c = chars[i++];
        this.currentToken = "";
        switch (type) {
            case 4: {
                while ((type = types[i]) == 4 || type == 2) {
                    ++i;
                }
                this.currentToken = this.statement.substring(start, i);
                if (this.currentToken.length() == 0) {
                    throw this.getSyntaxError();
                }
                this.currentTokenType = 2;
                this.parseIndex = i;
                return;
            }
            case 6: {
                if (types[i] == 6) {
                    ++i;
                }
            }
            case 5: {
                this.currentToken = this.statement.substring(start, i);
                switch (c) {
                    case '$': {
                        this.currentTokenType = 3;
                        break;
                    }
                    case '+': {
                        this.currentTokenType = 13;
                        break;
                    }
                    case '-': {
                        this.currentTokenType = 12;
                        break;
                    }
                    case '(': {
                        this.currentTokenType = 14;
                        break;
                    }
                    case ')': {
                        this.currentTokenType = 15;
                        break;
                    }
                    default: {
                        this.currentTokenType = 1;
                    }
                }
                this.parseIndex = i;
                return;
            }
            case 2: {
                long number = c - 48;
                while (true) {
                    if ((c = chars[i]) < '0' || c > '9') {
                        if (c == '.') {
                            this.readDecimal(start, i);
                            break;
                        }
                        if (c == 'E' || c == 'e') {
                            this.readDecimal(start, i);
                            break;
                        }
                        this.checkLiterals(false);
                        this.currentValue = this.valueFactory.createValue(number);
                        this.currentTokenType = 5;
                        this.currentToken = "0";
                        this.parseIndex = i;
                        break;
                    }
                    if ((number = number * 10L + (long)(c - 48)) > Integer.MAX_VALUE) {
                        this.readDecimal(start, i);
                        break;
                    }
                    ++i;
                }
                return;
            }
            case 8: {
                if (types[i] != 2) {
                    this.currentTokenType = 1;
                    this.currentToken = ".";
                    this.parseIndex = i;
                    return;
                }
                this.readDecimal(i - 1, i);
                return;
            }
            case 7: {
                this.readString(i, '\'');
                return;
            }
            case 3: {
                this.readString(i, '\"');
                return;
            }
            case -1: {
                this.currentToken = "";
                this.currentTokenType = 4;
                this.parseIndex = i;
                return;
            }
        }
        throw this.getSyntaxError();
    }

    private void readString(int i, char end) throws RepositoryException {
        char[] chars = this.statementChars;
        String result = null;
        while (true) {
            int begin = ++i;
            while (true) {
                if (chars[i] == end) {
                    if (result == null) {
                        result = this.statement.substring(begin, i);
                        break;
                    }
                    result = result + this.statement.substring(begin - 1, i);
                    break;
                }
                ++i;
            }
            if (chars[++i] != end) break;
        }
        this.currentToken = "'";
        this.checkLiterals(false);
        this.currentValue = this.valueFactory.createValue(result);
        this.parseIndex = i;
        this.currentTokenType = 5;
    }

    private void checkLiterals(boolean text) throws InvalidQueryException {
        if (text && !this.allowTextLiterals || !text && !this.allowNumberLiterals) {
            throw this.getSyntaxError("bind variable (literals of this type not allowed)");
        }
    }

    private void readDecimal(int start, int i) throws RepositoryException {
        BigDecimal bd;
        int t;
        char[] chars = this.statementChars;
        int[] types = this.characterTypes;
        while ((t = types[i]) == 8 || t == 2) {
            ++i;
        }
        if (chars[i] == 'E' || chars[i] == 'e') {
            if (chars[++i] == '+' || chars[i] == '-') {
                ++i;
            }
            if (types[i] != 2) {
                throw this.getSyntaxError();
            }
            while (types[++i] == 2) {
            }
        }
        this.parseIndex = i;
        String sub = this.statement.substring(start, i);
        try {
            bd = new BigDecimal(sub);
        }
        catch (NumberFormatException e) {
            throw new InvalidQueryException("Data conversion error converting " + sub + " to BigDecimal: " + e);
        }
        this.checkLiterals(false);
        this.currentValue = this.valueFactory.createValue(bd);
        this.currentTokenType = 5;
    }

    private InvalidQueryException getSyntaxError() {
        if (this.expected == null || this.expected.size() == 0) {
            return this.getSyntaxError(null);
        }
        StringBuilder buff = new StringBuilder();
        for (String exp : this.expected) {
            if (buff.length() > 0) {
                buff.append(", ");
            }
            buff.append(exp);
        }
        return this.getSyntaxError(buff.toString());
    }

    private InvalidQueryException getSyntaxError(String expected) {
        int index = Math.min(this.parseIndex, this.statement.length() - 1);
        String query = this.statement.substring(0, index) + "(*)" + this.statement.substring(index).trim();
        if (expected != null) {
            query = query + "; expected: " + expected;
        }
        return new InvalidQueryException("Query:\n" + query);
    }

    private String getOnlySelectorName(String propertyName) throws RepositoryException {
        if (this.selectors.size() > 1) {
            throw this.getSyntaxError("Need to specify the selector name for \"" + propertyName + "\" because the query contains more than one selector.");
        }
        return this.selectors.get(0).getSelectorName();
    }

    static class ColumnOrWildcard {
        String selectorName;
        String propertyName;
        String columnName;

        ColumnOrWildcard() {
        }
    }
}

