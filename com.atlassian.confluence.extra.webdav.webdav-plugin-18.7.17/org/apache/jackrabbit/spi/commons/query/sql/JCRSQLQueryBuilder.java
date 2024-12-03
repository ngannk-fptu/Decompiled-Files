/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.jcr.NamespaceException;
import javax.jcr.query.InvalidQueryException;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.conversion.NameException;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.PathBuilder;
import org.apache.jackrabbit.spi.commons.query.AndQueryNode;
import org.apache.jackrabbit.spi.commons.query.LocationStepQueryNode;
import org.apache.jackrabbit.spi.commons.query.NAryQueryNode;
import org.apache.jackrabbit.spi.commons.query.NodeTypeQueryNode;
import org.apache.jackrabbit.spi.commons.query.NotQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrderQueryNode;
import org.apache.jackrabbit.spi.commons.query.PathQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.RelationQueryNode;
import org.apache.jackrabbit.spi.commons.query.TextsearchQueryNode;
import org.apache.jackrabbit.spi.commons.query.sql.ASTAndExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTAscendingOrderSpec;
import org.apache.jackrabbit.spi.commons.query.sql.ASTBracketExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTContainsExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTDescendingOrderSpec;
import org.apache.jackrabbit.spi.commons.query.sql.ASTExcerptFunction;
import org.apache.jackrabbit.spi.commons.query.sql.ASTFromClause;
import org.apache.jackrabbit.spi.commons.query.sql.ASTIdentifier;
import org.apache.jackrabbit.spi.commons.query.sql.ASTLiteral;
import org.apache.jackrabbit.spi.commons.query.sql.ASTLowerFunction;
import org.apache.jackrabbit.spi.commons.query.sql.ASTNotExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTOrExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTOrderByClause;
import org.apache.jackrabbit.spi.commons.query.sql.ASTOrderSpec;
import org.apache.jackrabbit.spi.commons.query.sql.ASTPredicate;
import org.apache.jackrabbit.spi.commons.query.sql.ASTQuery;
import org.apache.jackrabbit.spi.commons.query.sql.ASTSelectList;
import org.apache.jackrabbit.spi.commons.query.sql.ASTUpperFunction;
import org.apache.jackrabbit.spi.commons.query.sql.ASTWhereClause;
import org.apache.jackrabbit.spi.commons.query.sql.DefaultParserVisitor;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParser;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserVisitor;
import org.apache.jackrabbit.spi.commons.query.sql.Node;
import org.apache.jackrabbit.spi.commons.query.sql.ParseException;
import org.apache.jackrabbit.spi.commons.query.sql.QueryFormat;
import org.apache.jackrabbit.spi.commons.query.sql.SimpleNode;
import org.apache.jackrabbit.util.ISO8601;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRSQLQueryBuilder
implements JCRSQLParserVisitor {
    private static final Logger log = LoggerFactory.getLogger(JCRSQLQueryBuilder.class);
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static Map<NameResolver, JCRSQLParser> parsers = new ReferenceMap<NameResolver, JCRSQLParser>(AbstractReferenceMap.ReferenceStrength.WEAK, AbstractReferenceMap.ReferenceStrength.WEAK);
    private final ASTQuery stmt;
    private QueryRootNode root;
    private NameResolver resolver;
    private final AndQueryNode constraintNode;
    private Name nodeTypeName;
    private final List pathConstraints = new ArrayList();
    private final QueryNodeFactory factory;

    private JCRSQLQueryBuilder(ASTQuery statement, NameResolver resolver, QueryNodeFactory factory) {
        this.stmt = statement;
        this.resolver = resolver;
        this.factory = factory;
        this.constraintNode = factory.createAndQueryNode(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static QueryRootNode createQuery(String statement, NameResolver resolver, QueryNodeFactory factory) throws InvalidQueryException {
        try {
            JCRSQLQueryBuilder builder;
            JCRSQLParser parser;
            Map<NameResolver, JCRSQLParser> map = parsers;
            synchronized (map) {
                parser = parsers.get(resolver);
                if (parser == null) {
                    parser = new JCRSQLParser(new StringReader(statement));
                    parser.setNameResolver(resolver);
                    parsers.put(resolver, parser);
                }
            }
            JCRSQLParser jCRSQLParser = parser;
            synchronized (jCRSQLParser) {
                parser.ReInit(new StringReader(statement));
                builder = new JCRSQLQueryBuilder(parser.Query(), resolver, factory);
            }
            return builder.getRootNode();
        }
        catch (ParseException e) {
            throw new InvalidQueryException(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            throw new InvalidQueryException(e.getMessage());
        }
        catch (Throwable t) {
            throw new InvalidQueryException(t.getMessage());
        }
    }

    public static String toString(QueryRootNode root, NameResolver resolver) throws InvalidQueryException {
        return QueryFormat.toString(root, resolver);
    }

    private QueryRootNode getRootNode() {
        if (this.root == null) {
            this.stmt.jjtAccept(this, null);
        }
        return this.root;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTQuery node, Object data) {
        LocationStepQueryNode[] steps;
        this.root = this.factory.createQueryRootNode();
        this.root.setLocationNode(this.factory.createPathQueryNode(this.root));
        node.childrenAccept(this, this.root);
        PathQueryNode pathNode = this.root.getLocationNode();
        pathNode.setAbsolute(true);
        if (this.pathConstraints.size() == 0) {
            LocationStepQueryNode step = this.factory.createLocationStepQueryNode(pathNode);
            step.setNameTest(null);
            step.setIncludeDescendants(true);
            pathNode.addPathStep(step);
        } else {
            MergingPathQueryNode path;
            try {
                while (this.pathConstraints.size() > 1) {
                    path = null;
                    Iterator it = this.pathConstraints.iterator();
                    while (it.hasNext() && !(path = (MergingPathQueryNode)it.next()).needsMerge()) {
                        path = null;
                    }
                    if (path == null) {
                        throw new IllegalArgumentException("Invalid combination of jcr:path clauses");
                    }
                    this.pathConstraints.remove(path);
                    MergingPathQueryNode[] paths = this.pathConstraints.toArray(new MergingPathQueryNode[this.pathConstraints.size()]);
                    paths = path.doMerge(paths);
                    this.pathConstraints.clear();
                    this.pathConstraints.addAll(Arrays.asList(paths));
                }
            }
            catch (NoSuchElementException e) {
                throw new IllegalArgumentException("Invalid combination of jcr:path clauses");
            }
            path = (MergingPathQueryNode)this.pathConstraints.get(0);
            LocationStepQueryNode[] steps2 = path.getPathSteps();
            for (int i = 0; i < steps2.length; ++i) {
                LocationStepQueryNode step = this.factory.createLocationStepQueryNode(pathNode);
                step.setNameTest(steps2[i].getNameTest());
                step.setIncludeDescendants(steps2[i].getIncludeDescendants());
                step.setIndex(steps2[i].getIndex());
                pathNode.addPathStep(step);
            }
        }
        if (this.constraintNode.getNumOperands() == 1) {
            steps = pathNode.getPathSteps();
            steps[steps.length - 1].addPredicate(this.constraintNode.getOperands()[0]);
        } else if (this.constraintNode.getNumOperands() > 1) {
            steps = pathNode.getPathSteps();
            steps[steps.length - 1].addPredicate(this.constraintNode);
        }
        if (this.nodeTypeName != null) {
            steps = pathNode.getPathSteps();
            NodeTypeQueryNode nodeType = this.factory.createNodeTypeQueryNode(steps[steps.length - 1], this.nodeTypeName);
            steps[steps.length - 1].addPredicate(nodeType);
        }
        return this.root;
    }

    @Override
    public Object visit(ASTSelectList node, Object data) {
        final QueryRootNode root = (QueryRootNode)data;
        node.childrenAccept(new DefaultParserVisitor(){

            @Override
            public Object visit(ASTIdentifier node, Object data) {
                root.addSelectProperty(node.getName());
                return data;
            }

            @Override
            public Object visit(ASTExcerptFunction node, Object data) {
                root.addSelectProperty(NameFactoryImpl.getInstance().create("internal", "excerpt(.)"));
                return data;
            }
        }, root);
        return data;
    }

    @Override
    public Object visit(ASTFromClause node, Object data) {
        QueryRootNode root = (QueryRootNode)data;
        return node.childrenAccept(new DefaultParserVisitor(){

            @Override
            public Object visit(ASTIdentifier node, Object data) {
                if (!node.getName().equals(NameConstants.NT_BASE)) {
                    JCRSQLQueryBuilder.this.nodeTypeName = node.getName();
                }
                return data;
            }
        }, root);
    }

    @Override
    public Object visit(ASTWhereClause node, Object data) {
        return node.childrenAccept(this, this.constraintNode);
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Object visit(ASTPredicate node, Object data) {
        void var5_22;
        NAryQueryNode parent = (NAryQueryNode)data;
        int type = node.getOperationType();
        try {
            final Name[] tmp = new Name[2];
            final ASTLiteral[] value = new ASTLiteral[1];
            node.childrenAccept(new DefaultParserVisitor(){

                @Override
                public Object visit(ASTIdentifier node, Object data) {
                    if (tmp[0] == null) {
                        tmp[0] = node.getName();
                    } else if (tmp[1] == null) {
                        tmp[1] = node.getName();
                    }
                    return data;
                }

                @Override
                public Object visit(ASTLiteral node, Object data) {
                    value[0] = node;
                    return data;
                }

                @Override
                public Object visit(ASTLowerFunction node, Object data) {
                    this.getIdentifier(node);
                    return data;
                }

                @Override
                public Object visit(ASTUpperFunction node, Object data) {
                    this.getIdentifier(node);
                    return data;
                }

                private void getIdentifier(SimpleNode node) {
                    Node n;
                    if (node.jjtGetNumChildren() > 0 && (n = node.jjtGetChild(0)) instanceof ASTIdentifier) {
                        ASTIdentifier identifier = (ASTIdentifier)n;
                        if (tmp[0] == null) {
                            tmp[0] = identifier.getName();
                        } else if (tmp[1] == null) {
                            tmp[1] = identifier.getName();
                        }
                    }
                }
            }, data);
            Name identifier = tmp[0];
            if (identifier != null && identifier.equals(NameConstants.JCR_PATH)) {
                if (tmp[1] != null) return data;
                this.createPathQuery(value[0].getValue(), parent.getType());
                return data;
            }
            if (type == 24) {
                AndQueryNode between = this.factory.createAndQueryNode(parent);
                RelationQueryNode rel = this.createRelationQueryNode(between, identifier, 20, (ASTLiteral)node.children[1]);
                node.childrenAccept(this, rel);
                between.addOperand(rel);
                rel = this.createRelationQueryNode(between, identifier, 22, (ASTLiteral)node.children[2]);
                node.childrenAccept(this, rel);
                between.addOperand(rel);
                AndQueryNode andQueryNode = between;
            } else if (type == 20 || type == 18 || type == 22 || type == 16 || type == 14 || type == 12) {
                RelationQueryNode relationQueryNode = this.createRelationQueryNode(parent, identifier, type, value[0]);
                node.childrenAccept(this, relationQueryNode);
            } else if (type == 23) {
                ASTLiteral pattern = value[0];
                if (node.getEscapeString() != null) {
                    if (node.getEscapeString().length() != 1) throw new IllegalArgumentException("ESCAPE string value must have length 1: '" + node.getEscapeString() + "'");
                    pattern.setValue(JCRSQLQueryBuilder.translateEscaping(pattern.getValue(), node.getEscapeString().charAt(0), '\\'));
                } else {
                    pattern.setValue(pattern.getValue().replaceAll("\\\\", "\\\\\\\\"));
                }
                RelationQueryNode relationQueryNode = this.createRelationQueryNode(parent, identifier, type, pattern);
                node.childrenAccept(this, relationQueryNode);
            } else if (type == 25) {
                OrQueryNode in = this.factory.createOrQueryNode(parent);
                for (int i = 1; i < node.children.length; ++i) {
                    RelationQueryNode rel = this.createRelationQueryNode(in, identifier, 11, (ASTLiteral)node.children[i]);
                    node.childrenAccept(this, rel);
                    in.addOperand(rel);
                }
                OrQueryNode orQueryNode = in;
            } else if (type == 26 || type == 27) {
                RelationQueryNode relationQueryNode = this.createRelationQueryNode(parent, identifier, type, null);
            } else if (type == 28) {
                ASTLiteral literal = node.children.length == 1 ? (ASTLiteral)node.children[0] : (ASTLiteral)node.children[1];
                RelationQueryNode relationQueryNode = this.createRelationQueryNode(parent, identifier, type, literal);
            } else {
                if (type != 29) throw new IllegalArgumentException("Unknown operation type: " + type);
                RelationQueryNode relationQueryNode = this.createRelationQueryNode(parent, NameConstants.JCR_PRIMARYTYPE, type, (ASTLiteral)node.children[0]);
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Too few arguments in predicate");
        }
        if (var5_22 == null) return data;
        parent.addOperand(var5_22);
        return data;
    }

    @Override
    public Object visit(ASTOrExpression node, Object data) {
        NAryQueryNode parent = (NAryQueryNode)data;
        OrQueryNode orQuery = this.factory.createOrQueryNode(parent);
        node.childrenAccept(this, orQuery);
        if (orQuery.getNumOperands() > 0) {
            parent.addOperand(orQuery);
        }
        return parent;
    }

    @Override
    public Object visit(ASTAndExpression node, Object data) {
        NAryQueryNode parent = (NAryQueryNode)data;
        AndQueryNode andQuery = this.factory.createAndQueryNode(parent);
        node.childrenAccept(this, andQuery);
        if (andQuery.getNumOperands() > 0) {
            parent.addOperand(andQuery);
        }
        return parent;
    }

    @Override
    public Object visit(ASTNotExpression node, Object data) {
        NAryQueryNode parent = (NAryQueryNode)data;
        NotQueryNode notQuery = this.factory.createNotQueryNode(parent);
        node.childrenAccept(this, notQuery);
        if (notQuery.getNumOperands() > 0) {
            parent.addOperand(notQuery);
        }
        return parent;
    }

    @Override
    public Object visit(ASTBracketExpression node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTOrderByClause node, Object data) {
        QueryRootNode root = (QueryRootNode)data;
        OrderQueryNode order = this.factory.createOrderQueryNode(root);
        root.setOrderNode(order);
        node.childrenAccept(this, order);
        return root;
    }

    @Override
    public Object visit(ASTOrderSpec node, Object data) {
        OrderQueryNode order = (OrderQueryNode)data;
        final Name[] identifier = new Name[1];
        node.childrenAccept(new DefaultParserVisitor(){

            @Override
            public Object visit(ASTIdentifier node, Object data) {
                identifier[0] = node.getName();
                return data;
            }
        }, data);
        OrderQueryNode.OrderSpec spec = new OrderQueryNode.OrderSpec(identifier[0], true);
        order.addOrderSpec(spec);
        node.childrenAccept(this, spec);
        return data;
    }

    @Override
    public Object visit(ASTAscendingOrderSpec node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTDescendingOrderSpec node, Object data) {
        OrderQueryNode.OrderSpec spec = (OrderQueryNode.OrderSpec)data;
        spec.setAscending(false);
        return data;
    }

    @Override
    public Object visit(ASTContainsExpression node, Object data) {
        NAryQueryNode parent = (NAryQueryNode)data;
        try {
            Path relPath = null;
            if (node.getPropertyName() != null) {
                PathBuilder builder = new PathBuilder();
                builder.addLast(node.getPropertyName());
                relPath = builder.getPath();
            }
            TextsearchQueryNode tsNode = this.factory.createTextsearchQueryNode(parent, node.getQuery());
            tsNode.setRelativePath(relPath);
            tsNode.setReferencesProperty(true);
            parent.addOperand(tsNode);
        }
        catch (MalformedPathException malformedPathException) {
            // empty catch block
        }
        return parent;
    }

    @Override
    public Object visit(ASTLowerFunction node, Object data) {
        RelationQueryNode parent = (RelationQueryNode)data;
        if (parent.getValueType() != 3) {
            String msg = "LOWER() function is only supported for String literal";
            throw new IllegalArgumentException(msg);
        }
        parent.addOperand(this.factory.createPropertyFunctionQueryNode(parent, "lower-case"));
        return parent;
    }

    @Override
    public Object visit(ASTUpperFunction node, Object data) {
        RelationQueryNode parent = (RelationQueryNode)data;
        if (parent.getValueType() != 3) {
            String msg = "UPPER() function is only supported for String literal";
            throw new IllegalArgumentException(msg);
        }
        parent.addOperand(this.factory.createPropertyFunctionQueryNode(parent, "upper-case"));
        return parent;
    }

    @Override
    public Object visit(ASTExcerptFunction node, Object data) {
        return data;
    }

    private RelationQueryNode createRelationQueryNode(QueryNode parent, Name propertyName, int operationType, ASTLiteral literal) throws IllegalArgumentException {
        RelationQueryNode node = null;
        try {
            Path relPath = null;
            if (propertyName != null) {
                PathBuilder builder = new PathBuilder();
                builder.addLast(propertyName);
                relPath = builder.getPath();
            }
            if (literal == null) {
                node = this.factory.createRelationQueryNode(parent, operationType);
                node.setRelativePath(relPath);
            } else if (literal.getType() == 4) {
                SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
                Date date = format.parse(literal.getValue());
                node = this.factory.createRelationQueryNode(parent, operationType);
                node.setRelativePath(relPath);
                node.setDateValue(date);
            } else if (literal.getType() == 2) {
                double d = Double.parseDouble(literal.getValue());
                node = this.factory.createRelationQueryNode(parent, operationType);
                node.setRelativePath(relPath);
                node.setDoubleValue(d);
            } else if (literal.getType() == 1) {
                long l = Long.parseLong(literal.getValue());
                node = this.factory.createRelationQueryNode(parent, operationType);
                node.setRelativePath(relPath);
                node.setLongValue(l);
            } else if (literal.getType() == 3) {
                node = this.factory.createRelationQueryNode(parent, operationType);
                node.setRelativePath(relPath);
                node.setStringValue(literal.getValue());
            } else if (literal.getType() == 5) {
                Calendar c = ISO8601.parse(literal.getValue());
                node = this.factory.createRelationQueryNode(parent, operationType);
                node.setRelativePath(relPath);
                node.setDateValue(c.getTime());
            }
        }
        catch (java.text.ParseException e) {
            throw new IllegalArgumentException(e.toString());
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(e.toString());
        }
        catch (MalformedPathException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        if (node == null) {
            throw new IllegalArgumentException("Unknown type for literal: " + literal.getType());
        }
        return node;
    }

    private void createPathQuery(String path, int operation) {
        MergingPathQueryNode pathNode = new MergingPathQueryNode(operation, this.factory.createPathQueryNode(null).getValidJcrSystemNodeTypeNames());
        pathNode.setAbsolute(true);
        if (path.equals("/")) {
            pathNode.addPathStep(this.factory.createLocationStepQueryNode(pathNode));
            this.pathConstraints.add(pathNode);
            return;
        }
        String[] names = path.split("/");
        for (int i = 0; i < names.length; ++i) {
            String name;
            if (names[i].length() == 0) {
                if (i == 0) {
                    pathNode.addPathStep(this.factory.createLocationStepQueryNode(pathNode));
                    continue;
                }
                pathNode.addPathStep(this.factory.createLocationStepQueryNode(pathNode));
                continue;
            }
            int idx = names[i].indexOf(91);
            int index = -2147483647;
            if (idx > -1) {
                name = names[i].substring(0, idx);
                String suffix = names[i].substring(idx);
                String indexStr = suffix.substring(1, suffix.length() - 1);
                if (indexStr.equals("%")) {
                    index = -2147483647;
                } else {
                    try {
                        index = Integer.parseInt(indexStr);
                    }
                    catch (NumberFormatException e) {
                        log.warn("Unable to parse index for path element: " + names[i]);
                    }
                }
                if (name.equals("%")) {
                    name = null;
                }
            } else {
                name = names[i];
                if (name.equals("%")) {
                    name = null;
                } else {
                    index = 1;
                }
            }
            Name qName = null;
            if (name != null) {
                try {
                    qName = this.resolver.getQName(name);
                }
                catch (NamespaceException e) {
                    throw new IllegalArgumentException("Illegal name: " + name);
                }
                catch (NameException e) {
                    throw new IllegalArgumentException("Illegal name: " + name);
                }
            }
            boolean descendant = name == null;
            LocationStepQueryNode step = this.factory.createLocationStepQueryNode(pathNode);
            step.setNameTest(qName);
            step.setIncludeDescendants(descendant);
            if (index > 0) {
                step.setIndex(index);
            }
            pathNode.addPathStep(step);
        }
        this.pathConstraints.add(pathNode);
    }

    private static String translateEscaping(String pattern, char from, char to) {
        if (from == to || pattern.indexOf(from) < 0 && pattern.indexOf(to) < 0) {
            return pattern;
        }
        StringBuffer translated = new StringBuffer(pattern.length());
        boolean escaped = false;
        for (int i = 0; i < pattern.length(); ++i) {
            if (pattern.charAt(i) == from) {
                if (escaped) {
                    translated.append(from);
                    escaped = false;
                    continue;
                }
                escaped = true;
                continue;
            }
            if (pattern.charAt(i) == to) {
                if (escaped) {
                    translated.append(to).append(to);
                    escaped = false;
                    continue;
                }
                translated.append(to).append(to);
                continue;
            }
            if (escaped) {
                translated.append(to);
                escaped = false;
            }
            translated.append(pattern.charAt(i));
        }
        return translated.toString();
    }

    private static class MergingPathQueryNode
    extends PathQueryNode {
        private int operation;

        MergingPathQueryNode(int operation, Collection<Name> validJcrSystemNodeTypeNames) {
            super(null, validJcrSystemNodeTypeNames);
            if (operation != 8 && operation != 7 && operation != 9) {
                throw new IllegalArgumentException("operation");
            }
            this.operation = operation;
        }

        MergingPathQueryNode[] doMerge(MergingPathQueryNode[] nodes) {
            if (this.operation == 8) {
                return this.doOrMerge(nodes);
            }
            return this.doAndMerge(nodes);
        }

        private MergingPathQueryNode[] doAndMerge(MergingPathQueryNode[] nodes) {
            if (this.operation == 7) {
                MergingPathQueryNode n = null;
                for (int i = 0; i < nodes.length; ++i) {
                    if (nodes[i].operation != 9) continue;
                    n = nodes[i];
                    nodes[i] = this;
                }
                if (n == null) {
                    throw new NoSuchElementException("Merging not possible with any node");
                }
                return super.doAndMerge(nodes);
            }
            if (this.operands.size() < 3) {
                throw new NoSuchElementException("Merging not possible");
            }
            int size = this.operands.size();
            LocationStepQueryNode n1 = (LocationStepQueryNode)this.operands.get(size - 1);
            LocationStepQueryNode n2 = (LocationStepQueryNode)this.operands.get(size - 2);
            if (n1.getNameTest() != null || n2.getNameTest() != null || !n1.getIncludeDescendants() || !n2.getIncludeDescendants()) {
                throw new NoSuchElementException("Merging not possible");
            }
            MergingPathQueryNode matchedNode = null;
            for (int i = 0; i < nodes.length; ++i) {
                boolean bl;
                if (nodes[i].operands.size() != this.operands.size() - 1) continue;
                boolean match = true;
                for (int j = 0; j < this.operands.size() - 1 && match; match &= bl, ++j) {
                    LocationStepQueryNode step = (LocationStepQueryNode)this.operands.get(j);
                    LocationStepQueryNode other = (LocationStepQueryNode)nodes[i].operands.get(j);
                    if (step.getNameTest() == null) {
                        if (other.getNameTest() == null) {
                            bl = true;
                            continue;
                        }
                        bl = false;
                        continue;
                    }
                    bl = step.getNameTest().equals(other.getNameTest());
                }
                if (!match) continue;
                matchedNode = nodes[i];
                break;
            }
            if (matchedNode == null) {
                throw new NoSuchElementException("Merging not possible with any node");
            }
            ((LocationStepQueryNode)matchedNode.operands.get(matchedNode.operands.size() - 1)).setIncludeDescendants(false);
            return nodes;
        }

        private MergingPathQueryNode[] doOrMerge(MergingPathQueryNode[] nodes) {
            MergingPathQueryNode compacted = new MergingPathQueryNode(8, this.getValidJcrSystemNodeTypeNames());
            Iterator it = this.operands.iterator();
            while (it.hasNext()) {
                LocationStepQueryNode step = (LocationStepQueryNode)it.next();
                if (step.getIncludeDescendants() && step.getNameTest() == null) {
                    if (it.hasNext()) {
                        LocationStepQueryNode next = (LocationStepQueryNode)it.next();
                        next.setIncludeDescendants(true);
                        compacted.addPathStep(next);
                        continue;
                    }
                    compacted.addPathStep(step);
                    continue;
                }
                compacted.addPathStep(step);
            }
            MergingPathQueryNode matchedNode = null;
            for (int i = 0; i < nodes.length; ++i) {
                boolean match;
                boolean bl;
                if (nodes[i].operands.size() != compacted.operands.size()) continue;
                Iterator compactedSteps = compacted.operands.iterator();
                Iterator otherSteps = nodes[i].operands.iterator();
                for (match = true; match && compactedSteps.hasNext(); match &= bl) {
                    LocationStepQueryNode n1 = (LocationStepQueryNode)compactedSteps.next();
                    LocationStepQueryNode n2 = (LocationStepQueryNode)otherSteps.next();
                    if (n1.getNameTest() == null) {
                        if (n2.getNameTest() == null) {
                            bl = true;
                            continue;
                        }
                        bl = false;
                        continue;
                    }
                    bl = n1.getNameTest().equals(n2.getNameTest());
                }
                if (!match) continue;
                matchedNode = nodes[i];
                break;
            }
            if (matchedNode == null) {
                throw new NoSuchElementException("Merging not possible with any node.");
            }
            ArrayList<MergingPathQueryNode> mergedList = new ArrayList<MergingPathQueryNode>(Arrays.asList(nodes));
            mergedList.remove(matchedNode);
            mergedList.add(compacted);
            return mergedList.toArray(new MergingPathQueryNode[mergedList.size()]);
        }

        boolean needsMerge() {
            for (LocationStepQueryNode step : this.operands) {
                if (!step.getIncludeDescendants() || step.getNameTest() != null) continue;
                return true;
            }
            return false;
        }
    }
}

