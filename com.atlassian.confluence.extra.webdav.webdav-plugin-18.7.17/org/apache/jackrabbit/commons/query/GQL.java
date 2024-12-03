/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.query.QueryManager;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import org.apache.jackrabbit.commons.iterator.RowIteratorAdapter;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.util.Text;

public final class GQL {
    private static final String PATH = "path";
    private static final String TYPE = "type";
    private static final String ORDER = "order";
    private static final String LIMIT = "limit";
    private static final String NAME = "name";
    private static final String OR = "OR";
    private static final String JCR_MIXIN_TYPES = "jcr:mixinTypes";
    private static final String JCR_PRIMARY_TYPE = "jcr:primaryType";
    private static final String JCR_ROOT = "jcr:root";
    private static final String JCR_CONTAINS = "jcr:contains";
    private static final String JCR_SCORE = "jcr:score";
    private static final String DESCENDING = "descending";
    private static final String REP_EXCERPT = "rep:excerpt";
    private static final String NATIVE_XPATH = "jcr:nativeXPath";
    private final String statement;
    private final Session session;
    private final List<Expression> conditions = new ArrayList<Expression>();
    private final String commonPathPrefix;
    private final Filter filter;
    private Map<String, String[]> ntNames;
    private Map<String, String> childNodeNames;
    private Map<String, String> propertyNames;
    private String pathConstraint = "//*";
    private OptionalExpression typeConstraints = null;
    private Expression orderBy = new OrderByExpression();
    private int offset = 0;
    private int numResults = Integer.MAX_VALUE;

    private GQL(String statement, Session session, String commonPathPrefix, Filter filter) {
        this.statement = statement;
        this.session = session;
        this.commonPathPrefix = commonPathPrefix;
        this.filter = filter;
    }

    public static RowIterator execute(String statement, Session session) {
        return GQL.execute(statement, session, null);
    }

    public static RowIterator execute(String statement, Session session, String commonPathPrefix) {
        return GQL.execute(statement, session, commonPathPrefix, null);
    }

    public static RowIterator execute(String statement, Session session, String commonPathPrefix, Filter filter) {
        GQL query = new GQL(statement, session, commonPathPrefix, filter);
        return query.execute();
    }

    public static RowIterator executeXPath(String jcrQuery, String jcrQueryLanguage, Session session, String commonPathPrefix, Filter filter) {
        GQL query = new GQL("", session, commonPathPrefix, filter);
        return query.executeJcrQuery(jcrQuery, jcrQueryLanguage);
    }

    public static String translateToXPath(String statement, Session session, String commonPathPrefix) throws RepositoryException {
        GQL query = new GQL(statement, session, commonPathPrefix, null);
        return query.translateStatement();
    }

    public static void parse(String statement, Session session, ParserCallback callback) throws RepositoryException {
        GQL query = new GQL(statement, session, null, null);
        query.parse(callback);
    }

    private RowIterator execute() {
        String xpath;
        try {
            xpath = this.translateStatement();
        }
        catch (RepositoryException e) {
            return RowIteratorAdapter.EMPTY;
        }
        return this.executeJcrQuery(xpath, "xpath");
    }

    private RowIterator executeJcrQuery(String jcrQuery, String jcrQueryLanguage) {
        try {
            QueryManager qm = this.session.getWorkspace().getQueryManager();
            RowIterator nodes = qm.createQuery(jcrQuery, jcrQueryLanguage).execute().getRows();
            if (this.filter != null) {
                nodes = new FilteredRowIterator(nodes);
            }
            if (this.offset > 0) {
                try {
                    nodes.skip(this.offset);
                }
                catch (NoSuchElementException e) {
                    return RowIteratorAdapter.EMPTY;
                }
            }
            if (this.numResults == Integer.MAX_VALUE) {
                return new RowIterAdapter(nodes, nodes.getSize());
            }
            ArrayList<Row> resultRows = new ArrayList<Row>();
            while (this.numResults-- > 0 && nodes.hasNext()) {
                resultRows.add(nodes.nextRow());
            }
            return new RowIterAdapter(resultRows, (long)resultRows.size());
        }
        catch (RepositoryException e) {
            return RowIteratorAdapter.EMPTY;
        }
    }

    private String translateStatement() throws RepositoryException {
        this.parse(new ParserCallback(){

            @Override
            public void term(String property, String value, boolean optional) throws RepositoryException {
                GQL.this.pushExpression(property, value, optional);
            }
        });
        StringBuffer stmt = new StringBuffer();
        stmt.append(this.pathConstraint);
        RequiredExpression predicate = new RequiredExpression();
        if (this.typeConstraints != null) {
            predicate.addOperand(this.typeConstraints);
        }
        for (Expression condition : this.conditions) {
            predicate.addOperand(condition);
        }
        if (predicate.getSize() > 0) {
            stmt.append("[");
        }
        predicate.toString(stmt);
        if (predicate.getSize() > 0) {
            stmt.append("]");
        }
        stmt.append(" ");
        this.orderBy.toString(stmt);
        return stmt.toString();
    }

    private void collectNodeTypes(String ntName) throws RepositoryException {
        String[] resolvedNames;
        NodeTypeManager ntMgr = this.session.getWorkspace().getNodeTypeManager();
        for (String resolvedName : resolvedNames = this.resolveNodeTypeName(ntName)) {
            try {
                NodeType base = ntMgr.getNodeType(resolvedName);
                if (base.isMixin()) {
                    this.addTypeConstraint(new MixinComparision(resolvedName));
                } else {
                    this.addTypeConstraint(new PrimaryTypeComparision(resolvedName));
                }
                NodeTypeIterator allTypes = ntMgr.getAllNodeTypes();
                while (allTypes.hasNext()) {
                    NodeType nt = allTypes.nextNodeType();
                    NodeType[] superTypes = nt.getSupertypes();
                    if (!Arrays.asList(superTypes).contains(base)) continue;
                    if (nt.isMixin()) {
                        this.addTypeConstraint(new MixinComparision(nt.getName()));
                        continue;
                    }
                    this.addTypeConstraint(new PrimaryTypeComparision(nt.getName()));
                }
            }
            catch (NoSuchNodeTypeException e) {
                this.addTypeConstraint(new PrimaryTypeComparision(resolvedName));
            }
        }
    }

    private void addTypeConstraint(Expression expr) {
        if (this.typeConstraints == null) {
            this.typeConstraints = new OptionalExpression();
        }
        this.typeConstraints.addOperand(expr);
    }

    private String[] resolveNodeTypeName(String ntName) throws RepositoryException {
        String[] names;
        if (GQL.isPrefixed(ntName)) {
            names = new String[]{ntName};
        } else {
            if (this.ntNames == null) {
                NodeTypeManager ntMgr = this.session.getWorkspace().getNodeTypeManager();
                this.ntNames = new HashMap<String, String[]>();
                NodeTypeIterator it = ntMgr.getAllNodeTypes();
                while (it.hasNext()) {
                    String[] nts;
                    String name;
                    String localName = name = it.nextNodeType().getName();
                    int idx = name.indexOf(58);
                    if (idx != -1) {
                        localName = name.substring(idx + 1);
                    }
                    if ((nts = this.ntNames.get(localName)) == null) {
                        nts = new String[]{name};
                    } else {
                        String[] tmp = new String[nts.length + 1];
                        System.arraycopy(nts, 0, tmp, 0, nts.length);
                        tmp[nts.length] = name;
                        nts = tmp;
                    }
                    this.ntNames.put(localName, nts);
                }
            }
            if ((names = this.ntNames.get(ntName)) == null) {
                names = new String[]{ntName};
            }
        }
        return names;
    }

    private String resolvePropertyName(String name) throws RepositoryException {
        String pn;
        if (GQL.isPrefixed(name)) {
            return name;
        }
        if (this.propertyNames == null) {
            this.propertyNames = new HashMap<String, String>();
            if (this.session != null) {
                NodeTypeManager ntMgr = this.session.getWorkspace().getNodeTypeManager();
                NodeTypeIterator it = ntMgr.getAllNodeTypes();
                while (it.hasNext()) {
                    PropertyDefinition[] defs;
                    NodeType nt = it.nextNodeType();
                    for (PropertyDefinition def : defs = nt.getDeclaredPropertyDefinitions()) {
                        String pn2 = def.getName();
                        if (pn2.equals("*")) continue;
                        String localName = pn2;
                        int idx = pn2.indexOf(58);
                        if (idx != -1) {
                            localName = pn2.substring(idx + 1);
                        }
                        this.propertyNames.put(localName, pn2);
                    }
                }
            }
        }
        if ((pn = this.propertyNames.get(name)) != null) {
            return pn;
        }
        return name;
    }

    private String resolveChildNodeName(String name) throws RepositoryException {
        String cnn;
        if (GQL.isPrefixed(name)) {
            return name;
        }
        if (this.childNodeNames == null) {
            this.childNodeNames = new HashMap<String, String>();
            NodeTypeManager ntMgr = this.session.getWorkspace().getNodeTypeManager();
            NodeTypeIterator it = ntMgr.getAllNodeTypes();
            while (it.hasNext()) {
                NodeDefinition[] defs;
                NodeType nt = it.nextNodeType();
                for (NodeDefinition def : defs = nt.getDeclaredChildNodeDefinitions()) {
                    String cnn2 = def.getName();
                    if (cnn2.equals("*")) continue;
                    String localName = cnn2;
                    int idx = cnn2.indexOf(58);
                    if (idx != -1) {
                        localName = cnn2.substring(idx + 1);
                    }
                    this.childNodeNames.put(localName, cnn2);
                }
            }
        }
        if ((cnn = this.childNodeNames.get(name)) != null) {
            return cnn;
        }
        return name;
    }

    private static boolean isPrefixed(String name) {
        return name.indexOf(58) != -1;
    }

    private void parse(ParserCallback callback) throws RepositoryException {
        char[] stmt = new char[this.statement.length() + 1];
        this.statement.getChars(0, this.statement.length(), stmt, 0);
        stmt[this.statement.length()] = 32;
        StringBuffer property = new StringBuffer();
        StringBuffer value = new StringBuffer();
        boolean quoted = false;
        boolean escaped = false;
        boolean optional = false;
        for (char c : stmt) {
            switch (c) {
                case ' ': {
                    if (quoted) {
                        value.append(c);
                        break;
                    }
                    if (value.length() <= 0) break;
                    String p = property.toString();
                    String v = value.toString();
                    if (v.equals(OR) && p.length() == 0) {
                        optional = true;
                    } else {
                        callback.term(p, v, optional);
                        optional = false;
                    }
                    property.setLength(0);
                    value.setLength(0);
                    break;
                }
                case ':': {
                    if (quoted || escaped) {
                        value.append(c);
                        break;
                    }
                    if (property.length() == 0) {
                        property.append(value);
                        value.setLength(0);
                        break;
                    }
                    value.append(c);
                    break;
                }
                case '\"': {
                    if (escaped) {
                        value.append(c);
                        break;
                    }
                    quoted = !quoted;
                    break;
                }
                case '\\': {
                    if (escaped) {
                        value.append(c);
                    }
                    escaped = !escaped;
                    break;
                }
                case '*': 
                case '?': {
                    if (property.toString().equals(NAME)) {
                        value.append(c);
                        break;
                    }
                }
                case '!': 
                case '[': 
                case ']': 
                case '^': 
                case '{': 
                case '}': 
                case '~': {
                    break;
                }
                default: {
                    value.append(c);
                }
            }
            if (c == '\\') continue;
            escaped = false;
        }
    }

    private void pushExpression(String property, String value, boolean optional) throws RepositoryException {
        if (property.equals(PATH)) {
            String path = value.startsWith("/") ? "/jcr:root" + value : value;
            this.pathConstraint = ISO9075.encodePath(path) + "//*";
        } else if (property.equals(TYPE)) {
            String[] nts = Text.explode(value, 44);
            if (nts.length > 0) {
                for (String nt : nts) {
                    this.collectNodeTypes(nt);
                }
            }
        } else if (property.equals(ORDER)) {
            this.orderBy = new OrderByExpression(value);
        } else if (property.equals(LIMIT)) {
            int idx = value.indexOf("..");
            if (idx != -1) {
                String lower = value.substring(0, idx);
                String uppper = value.substring(idx + "..".length());
                if (lower.length() > 0) {
                    try {
                        this.offset = Integer.parseInt(lower);
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
                if (uppper.length() > 0) {
                    try {
                        this.numResults = Integer.parseInt(uppper) - this.offset;
                        if (this.numResults < 0) {
                            this.numResults = Integer.MAX_VALUE;
                        }
                    }
                    catch (NumberFormatException numberFormatException) {}
                }
            } else {
                try {
                    this.numResults = Integer.parseInt(value);
                }
                catch (NumberFormatException lower) {}
            }
        } else {
            Expression expr = property.equals(NAME) ? new NameExpression(value) : new ContainsExpression(property, value);
            if (optional) {
                Expression last = this.conditions.get(this.conditions.size() - 1);
                if (last instanceof OptionalExpression) {
                    ((OptionalExpression)last).addOperand(expr);
                } else {
                    OptionalExpression op = new OptionalExpression();
                    op.addOperand(last);
                    op.addOperand(expr);
                    this.conditions.set(this.conditions.size() - 1, op);
                }
            } else {
                this.conditions.add(expr);
            }
        }
    }

    private static String checkProhibited(String value) {
        if (value.startsWith("-")) {
            return value.substring(1);
        }
        return value;
    }

    private final class FilteredRowIterator
    implements RowIterator {
        private final RowIterator rows;
        private Row next;
        private long position = 0L;

        public FilteredRowIterator(RowIterator rows) {
            this.rows = rows;
            this.fetchNext();
        }

        @Override
        public void skip(long skipNum) {
            while (skipNum-- > 0L && this.hasNext()) {
                this.nextRow();
            }
        }

        @Override
        public long getSize() {
            return -1L;
        }

        @Override
        public long getPosition() {
            return this.position;
        }

        public Object next() {
            return this.nextRow();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Row nextRow() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            try {
                Row row = this.next;
                return row;
            }
            finally {
                ++this.position;
                this.fetchNext();
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        private void fetchNext() {
            this.next = null;
            while (this.next == null && this.rows.hasNext()) {
                Row r = this.rows.nextRow();
                try {
                    if (!GQL.this.filter.include(r)) continue;
                    this.next = r;
                    return;
                }
                catch (RepositoryException repositoryException) {
                }
            }
        }
    }

    private final class RowIterAdapter
    extends RowIteratorAdapter {
        private final long size;

        public RowIterAdapter(RangeIterator rangeIterator, long size) {
            super(rangeIterator);
            this.size = size;
        }

        public RowIterAdapter(Collection collection, long size) {
            super(collection);
            this.size = size;
        }

        @Override
        public Row nextRow() {
            Row next = super.nextRow();
            next = GQL.this.commonPathPrefix != null ? new RowAdapter(next, GQL.this.commonPathPrefix) : new RowAdapter(next, ".");
            return next;
        }

        @Override
        public long getSize() {
            return this.size;
        }
    }

    private static final class RowAdapter
    implements Row {
        private final Row row;
        private final String excerptPath;

        private RowAdapter(Row row, String excerptPath) {
            this.row = row;
            this.excerptPath = excerptPath;
        }

        @Override
        public Value[] getValues() throws RepositoryException {
            return this.row.getValues();
        }

        @Override
        public Value getValue(String propertyName) throws ItemNotFoundException, RepositoryException {
            if (propertyName.startsWith(GQL.REP_EXCERPT)) {
                propertyName = "rep:excerpt(" + this.excerptPath + ")";
            }
            return this.row.getValue(propertyName);
        }

        @Override
        public Node getNode() throws RepositoryException {
            return this.row.getNode();
        }

        @Override
        public Node getNode(String selectorName) throws RepositoryException {
            return this.row.getNode(selectorName);
        }

        @Override
        public String getPath() throws RepositoryException {
            return this.row.getPath();
        }

        @Override
        public String getPath(String selectorName) throws RepositoryException {
            return this.row.getPath(selectorName);
        }

        @Override
        public double getScore() throws RepositoryException {
            return this.row.getScore();
        }

        @Override
        public double getScore(String selectorName) throws RepositoryException {
            return this.row.getScore(selectorName);
        }
    }

    private class OrderByExpression
    implements Expression {
        private final String value;

        OrderByExpression() {
            this.value = "";
        }

        OrderByExpression(String value) {
            this.value = value;
        }

        @Override
        public void toString(StringBuffer buffer) throws RepositoryException {
            int start = buffer.length();
            buffer.append("order by ");
            ArrayList<String> names = new ArrayList<String>(Arrays.asList(Text.explode(this.value, 44)));
            int length = buffer.length();
            String comma = "";
            for (String name : names) {
                boolean asc;
                if (name.equals("-")) {
                    buffer.delete(start, buffer.length());
                    return;
                }
                if (name.startsWith("-")) {
                    name = name.substring(1);
                    asc = false;
                } else if (name.startsWith("+")) {
                    name = name.substring(1);
                    asc = true;
                } else {
                    asc = true;
                }
                if (name.length() <= 0) continue;
                buffer.append(comma);
                name = this.createPropertyName(GQL.this.resolvePropertyName(name));
                buffer.append(name);
                if (!asc) {
                    buffer.append(" ").append(GQL.DESCENDING);
                }
                comma = ", ";
            }
            if (buffer.length() == length) {
                this.defaultOrderBy(buffer);
            }
        }

        private String createPropertyName(String name) {
            if (name.contains("/")) {
                String[] labels = name.split("/");
                name = "";
                for (int i = 0; i < labels.length; ++i) {
                    String label = ISO9075.encode(labels[i]);
                    name = i < labels.length - 1 ? name + label + "/" : name + "@" + label;
                }
                return name;
            }
            return "@" + ISO9075.encode(name);
        }

        private void defaultOrderBy(StringBuffer buffer) {
            buffer.append("@").append(GQL.JCR_SCORE).append(" ").append(GQL.DESCENDING);
        }
    }

    private class OptionalExpression
    extends NAryExpression {
        private OptionalExpression() {
        }

        @Override
        protected String getOperation() {
            return " or ";
        }
    }

    private class RequiredExpression
    extends NAryExpression {
        private RequiredExpression() {
        }

        @Override
        protected String getOperation() {
            return " and ";
        }
    }

    private abstract class NAryExpression
    implements Expression {
        private final List<Expression> operands = new ArrayList<Expression>();

        private NAryExpression() {
        }

        @Override
        public void toString(StringBuffer buffer) throws RepositoryException {
            if (this.operands.size() > 1) {
                buffer.append("(");
            }
            String op = "";
            for (Expression expr : this.operands) {
                buffer.append(op);
                expr.toString(buffer);
                op = this.getOperation();
            }
            if (this.operands.size() > 1) {
                buffer.append(")");
            }
        }

        void addOperand(Expression expr) {
            this.operands.add(expr);
        }

        int getSize() {
            return this.operands.size();
        }

        protected abstract String getOperation();
    }

    private final class ContainsExpression
    extends PropertyExpression {
        private boolean prohibited;

        ContainsExpression(String property, String value) {
            super(property, GQL.checkProhibited(value.toLowerCase()));
            this.prohibited = false;
            this.prohibited = value.startsWith("-");
        }

        @Override
        public void toString(StringBuffer buffer) throws RepositoryException {
            if (this.property.equals(GQL.NATIVE_XPATH)) {
                buffer.append(this.value);
                return;
            }
            if (this.prohibited) {
                buffer.append("not(");
            }
            buffer.append(GQL.JCR_CONTAINS).append("(");
            if (this.property.length() == 0) {
                if (GQL.this.commonPathPrefix == null) {
                    buffer.append(".");
                } else {
                    buffer.append(ISO9075.encodePath(GQL.this.commonPathPrefix));
                }
            } else {
                String[] parts = Text.explode(this.property, 47);
                if (GQL.this.commonPathPrefix != null) {
                    buffer.append(ISO9075.encodePath(GQL.this.commonPathPrefix));
                    buffer.append("/");
                }
                String slash = "";
                for (int i = 0; i < parts.length; ++i) {
                    if (i == parts.length - 1) {
                        if (!parts[i].equals(".")) {
                            buffer.append(slash);
                            buffer.append("@");
                            buffer.append(ISO9075.encode(GQL.this.resolvePropertyName(parts[i])));
                        }
                    } else {
                        buffer.append(slash);
                        buffer.append(ISO9075.encode(GQL.this.resolveChildNodeName(parts[i])));
                    }
                    slash = "/";
                }
            }
            buffer.append(", '");
            String escapedValue = this.value.replaceAll("'", "\\\\''");
            if (this.value.indexOf(32) != -1) {
                buffer.append('\"').append(escapedValue).append('\"');
            } else {
                buffer.append(escapedValue);
            }
            buffer.append("')");
            if (this.prohibited) {
                buffer.append(")");
            }
        }
    }

    private static class NameExpression
    implements Expression {
        private final String value;

        NameExpression(String value) {
            String tmp = value;
            tmp = tmp.replaceAll("'", "''");
            tmp = tmp.replaceAll("\\*", "\\%");
            tmp = tmp.replaceAll("\\?", "\\_");
            this.value = tmp = tmp.toLowerCase();
        }

        @Override
        public void toString(StringBuffer buffer) throws RepositoryException {
            buffer.append("jcr:like(fn:lower-case(fn:name()), '");
            buffer.append(this.value);
            buffer.append("')");
        }
    }

    private class PrimaryTypeComparision
    extends ValueComparison {
        PrimaryTypeComparision(String value) {
            super(GQL.JCR_PRIMARY_TYPE, value);
        }
    }

    private class MixinComparision
    extends ValueComparison {
        MixinComparision(String value) {
            super(GQL.JCR_MIXIN_TYPES, value);
        }
    }

    private abstract class ValueComparison
    extends PropertyExpression {
        ValueComparison(String property, String value) {
            super(property, value);
        }

        @Override
        public void toString(StringBuffer buffer) throws RepositoryException {
            buffer.append("@");
            buffer.append(ISO9075.encode(GQL.this.resolvePropertyName(this.property)));
            buffer.append("='").append(this.value).append("'");
        }
    }

    private abstract class PropertyExpression
    implements Expression {
        protected final String property;
        protected final String value;

        PropertyExpression(String property, String value) {
            this.property = property;
            this.value = value;
        }
    }

    private static interface Expression {
        public void toString(StringBuffer var1) throws RepositoryException;
    }

    public static interface ParserCallback {
        public void term(String var1, String var2, boolean var3) throws RepositoryException;
    }

    public static interface Filter {
        public boolean include(Row var1) throws RepositoryException;
    }
}

