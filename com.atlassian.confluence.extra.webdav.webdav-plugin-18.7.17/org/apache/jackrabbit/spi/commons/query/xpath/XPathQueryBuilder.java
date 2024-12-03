/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.xpath;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.conversion.NameException;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.PathBuilder;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;
import org.apache.jackrabbit.spi.commons.query.AndQueryNode;
import org.apache.jackrabbit.spi.commons.query.DefaultQueryNodeVisitor;
import org.apache.jackrabbit.spi.commons.query.DerefQueryNode;
import org.apache.jackrabbit.spi.commons.query.LocationStepQueryNode;
import org.apache.jackrabbit.spi.commons.query.NAryQueryNode;
import org.apache.jackrabbit.spi.commons.query.NodeTypeQueryNode;
import org.apache.jackrabbit.spi.commons.query.NotQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrderQueryNode;
import org.apache.jackrabbit.spi.commons.query.PathQueryNode;
import org.apache.jackrabbit.spi.commons.query.PropertyFunctionQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.RelationQueryNode;
import org.apache.jackrabbit.spi.commons.query.TextsearchQueryNode;
import org.apache.jackrabbit.spi.commons.query.xpath.Node;
import org.apache.jackrabbit.spi.commons.query.xpath.ParseException;
import org.apache.jackrabbit.spi.commons.query.xpath.QueryFormat;
import org.apache.jackrabbit.spi.commons.query.xpath.SimpleNode;
import org.apache.jackrabbit.spi.commons.query.xpath.XPath;
import org.apache.jackrabbit.spi.commons.query.xpath.XPathTreeConstants;
import org.apache.jackrabbit.spi.commons.query.xpath.XPathVisitor;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.jackrabbit.util.ISO9075;

public class XPathQueryBuilder
implements XPathVisitor,
XPathTreeConstants {
    private static final NameFactory NAME_FACTORY = NameFactoryImpl.getInstance();
    private static final PathFactory PATH_FACTORY = PathFactoryImpl.getInstance();
    static final String NS_FN_URI = "http://www.w3.org/2005/xpath-functions";
    static final Name FN_NOT = NAME_FACTORY.create("http://www.w3.org/2005/xpath-functions", "not");
    static final Name FN_LOWER_CASE = NAME_FACTORY.create("http://www.w3.org/2005/xpath-functions", "lower-case");
    static final Name FN_UPPER_CASE = NAME_FACTORY.create("http://www.w3.org/2005/xpath-functions", "upper-case");
    static final Name REP_NORMALIZE = NAME_FACTORY.create("internal", "normalize");
    static final Name FN_NOT_10 = NAME_FACTORY.create("", "not");
    static final Name FN_TRUE = NAME_FACTORY.create("", "true");
    static final Name FN_FALSE = NAME_FACTORY.create("", "false");
    static final Name FN_POSITION = NAME_FACTORY.create("", "position");
    static final Name FN_ELEMENT = NAME_FACTORY.create("", "element");
    static final Name FN_POSITION_FULL = NAME_FACTORY.create("", "position()");
    static final Name JCR_XMLTEXT = NAME_FACTORY.create("http://www.jcp.org/jcr/1.0", "xmltext");
    static final Name FN_LAST = NAME_FACTORY.create("", "last");
    static final Name FN_FIRST = NAME_FACTORY.create("", "first");
    static final Name XS_DATETIME = NAME_FACTORY.create("http://www.w3.org/2001/XMLSchema", "dateTime");
    static final Name JCR_LIKE = NAME_FACTORY.create("http://www.jcp.org/jcr/1.0", "like");
    static final Name JCR_DEREF = NAME_FACTORY.create("http://www.jcp.org/jcr/1.0", "deref");
    static final Name JCR_CONTAINS = NAME_FACTORY.create("http://www.jcp.org/jcr/1.0", "contains");
    static final Name JCR_ROOT = NAME_FACTORY.create("http://www.jcp.org/jcr/1.0", "root");
    static final Name JCR_SCORE = NAME_FACTORY.create("http://www.jcp.org/jcr/1.0", "score");
    static final Name REP_SIMILAR = NAME_FACTORY.create("internal", "similar");
    static final Name REP_SPELLCHECK = NAME_FACTORY.create("internal", "spellcheck");
    private static final String OP_EQ = "eq";
    private static final String OP_NE = "ne";
    private static final String OP_GT = "gt";
    private static final String OP_GE = "ge";
    private static final String OP_LT = "lt";
    private static final String OP_LE = "le";
    private static final String OP_SIGN_EQ = "=";
    private static final String OP_SIGN_NE = "!=";
    private static final String OP_SIGN_GT = ">";
    private static final String OP_SIGN_GE = ">=";
    private static final String OP_SIGN_LT = "<";
    private static final String OP_SIGN_LE = "<=";
    private static final Map<NameResolver, XPath> parsers = new ReferenceMap<NameResolver, XPath>(AbstractReferenceMap.ReferenceStrength.WEAK, AbstractReferenceMap.ReferenceStrength.WEAK);
    private final QueryRootNode root;
    private final NameResolver resolver;
    private final List exceptions = new ArrayList();
    private PathBuilder tmpRelPath;
    private final QueryNodeFactory factory;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private XPathQueryBuilder(String statement, NameResolver resolver, QueryNodeFactory factory) throws InvalidQueryException {
        this.resolver = resolver;
        this.factory = factory;
        this.root = factory.createQueryRootNode();
        try {
            SimpleNode query;
            XPath parser;
            statement = "for $v in " + statement + " return $v";
            Map<NameResolver, XPath> map = parsers;
            synchronized (map) {
                parser = parsers.get(resolver);
                if (parser == null) {
                    parser = new XPath(new StringReader(statement));
                    parsers.put(resolver, parser);
                }
            }
            XPath xPath = parser;
            synchronized (xPath) {
                parser.ReInit(new StringReader(statement));
                query = parser.XPath2();
            }
            query.jjtAccept(this, this.root);
        }
        catch (ParseException e) {
            throw new InvalidQueryException(e.getMessage() + " for statement: " + statement, e);
        }
        catch (Throwable t) {
            throw new InvalidQueryException(t.getMessage() + " for statement: " + statement, t);
        }
        if (this.exceptions.size() > 0) {
            Exception e = (Exception)this.exceptions.get(0);
            if (e instanceof InvalidQueryException) {
                throw (InvalidQueryException)e;
            }
            throw new InvalidQueryException(e.getMessage(), e);
        }
    }

    public static QueryRootNode createQuery(String statement, NameResolver resolver, QueryNodeFactory factory) throws InvalidQueryException {
        return new XPathQueryBuilder(statement, resolver, factory).getRootNode();
    }

    public static String toString(QueryRootNode root, NameResolver resolver) throws InvalidQueryException {
        return QueryFormat.toString(root, resolver);
    }

    QueryRootNode getRootNode() {
        return this.root;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        QueryNode queryNode = (QueryNode)data;
        switch (node.getId()) {
            case 0: {
                queryNode = this.createPathQueryNode(node);
                break;
            }
            case 119: 
            case 120: {
                if (queryNode instanceof PathQueryNode) {
                    ((PathQueryNode)queryNode).setAbsolute(true);
                    break;
                }
                this.exceptions.add(new InvalidQueryException("Unsupported root level query node: " + queryNode));
                break;
            }
            case 122: {
                PathQueryNode relPath;
                LocationStepQueryNode[] steps;
                Name nameTest;
                if (this.isAttributeAxis(node)) {
                    if (queryNode.getType() == 2 || queryNode.getType() == 12 && ((DerefQueryNode)queryNode).getRefProperty() == null || queryNode.getType() == 3 || queryNode.getType() == 11 || queryNode.getType() == 4) {
                        node.childrenAccept(this, queryNode);
                        break;
                    }
                    if (queryNode.getType() == 9) {
                        RelationQueryNode isNull = this.factory.createRelationQueryNode(queryNode, 26);
                        this.applyRelativePath(isNull);
                        node.childrenAccept(this, isNull);
                        NotQueryNode notNode = (NotQueryNode)queryNode;
                        NAryQueryNode parent = (NAryQueryNode)notNode.getParent();
                        parent.removeOperand(notNode);
                        parent.addOperand(isNull);
                        break;
                    }
                    RelationQueryNode notNull = this.factory.createRelationQueryNode(queryNode, 27);
                    this.applyRelativePath(notNull);
                    node.childrenAccept(this, notNull);
                    ((NAryQueryNode)queryNode).addOperand(notNull);
                    break;
                }
                if (queryNode.getType() == 11) {
                    this.createLocationStep(node, (NAryQueryNode)queryNode);
                    break;
                }
                if (queryNode.getType() == 4 || queryNode.getType() == 2) {
                    node.childrenAccept(this, queryNode);
                    break;
                }
                RelationQueryNode tmp = this.factory.createRelationQueryNode(null, 27);
                node.childrenAccept(this, tmp);
                if (this.tmpRelPath == null) {
                    this.tmpRelPath = new PathBuilder();
                }
                if ((nameTest = (steps = (relPath = tmp.getRelativePath()).getPathSteps())[steps.length - 1].getNameTest()) == null) {
                    nameTest = RelationQueryNode.STAR_NAME_TEST;
                }
                this.tmpRelPath.addLast(nameTest);
                break;
            }
            case 138: {
                if (queryNode.getType() == 10 || queryNode.getType() == 12 || queryNode.getType() == 2 || queryNode.getType() == 4 || queryNode.getType() == 11) {
                    this.createNodeTest(node, queryNode);
                    break;
                }
                if (queryNode.getType() == 3) {
                    this.setOrderSpecPath(node, (OrderQueryNode)queryNode);
                    break;
                }
                node.childrenAccept(this, queryNode);
                break;
            }
            case 260: {
                SimpleNode child;
                if (queryNode.getType() != 10 || (child = (SimpleNode)node.jjtGetChild(0)).getId() == 250) break;
                this.createNodeTest(child, queryNode);
                break;
            }
            case 237: {
                if (queryNode.getType() != 10) break;
                LocationStepQueryNode loc = (LocationStepQueryNode)queryNode;
                loc.setNameTest(JCR_XMLTEXT);
                break;
            }
            case 269: {
                if (queryNode.getType() != 10) break;
                LocationStepQueryNode loc = (LocationStepQueryNode)queryNode;
                String ntName = ((SimpleNode)node.jjtGetChild(0)).getValue();
                try {
                    Name nt = this.resolver.getQName(ntName);
                    NodeTypeQueryNode nodeType = this.factory.createNodeTypeQueryNode(loc, nt);
                    loc.addPredicate(nodeType);
                }
                catch (NameException e) {
                    this.exceptions.add(new InvalidQueryException("Not a valid name: " + ntName));
                }
                catch (NamespaceException e) {
                    this.exceptions.add(new InvalidQueryException("Not a valid name: " + ntName));
                }
                break;
            }
            case 99: {
                NAryQueryNode parent = (NAryQueryNode)queryNode;
                OrQueryNode orQueryNode = this.factory.createOrQueryNode(parent);
                parent.addOperand(orQueryNode);
                node.childrenAccept(this, orQueryNode);
                break;
            }
            case 100: {
                NAryQueryNode parent = (NAryQueryNode)queryNode;
                AndQueryNode andQueryNode = this.factory.createAndQueryNode(parent);
                parent.addOperand(andQueryNode);
                node.childrenAccept(this, andQueryNode);
                break;
            }
            case 101: {
                this.createExpression(node, (NAryQueryNode)queryNode);
                break;
            }
            case 47: 
            case 145: 
            case 146: 
            case 147: {
                if (queryNode.getType() == 2) {
                    this.assignValue(node, (RelationQueryNode)queryNode);
                    break;
                }
                if (queryNode.getType() == 10) {
                    if (node.getId() == 145) {
                        int index = Integer.parseInt(node.getValue());
                        ((LocationStepQueryNode)queryNode).setIndex(index);
                        break;
                    }
                    this.exceptions.add(new InvalidQueryException("LocationStep only allows integer literal as position index"));
                    break;
                }
                this.exceptions.add(new InvalidQueryException("Parse error: data is not a RelationQueryNode"));
                break;
            }
            case 113: {
                if (queryNode.getType() == 2) {
                    ((RelationQueryNode)queryNode).setUnaryMinus(true);
                    break;
                }
                this.exceptions.add(new InvalidQueryException("Parse error: data is not a RelationQueryNode"));
                break;
            }
            case 151: {
                queryNode = this.createFunction(node, queryNode);
                break;
            }
            case 81: {
                this.root.setOrderNode(this.factory.createOrderQueryNode(this.root));
                queryNode = this.root.getOrderNode();
                node.childrenAccept(this, queryNode);
                break;
            }
            case 85: {
                OrderQueryNode orderQueryNode = (OrderQueryNode)queryNode;
                orderQueryNode.newOrderSpec();
                node.childrenAccept(this, queryNode);
                if (orderQueryNode.isValid()) break;
                this.exceptions.add(new InvalidQueryException("Invalid order specification. (Missing @?)"));
                break;
            }
            case 86: {
                if (node.jjtGetNumChildren() <= 0 || ((SimpleNode)node.jjtGetChild(0)).getId() != 88) break;
                ((OrderQueryNode)queryNode).setAscending(false);
                break;
            }
            case 143: {
                if (queryNode.getType() == 11) {
                    QueryNode[] operands = ((PathQueryNode)queryNode).getOperands();
                    queryNode = operands[operands.length - 1];
                }
                node.childrenAccept(this, queryNode);
                break;
            }
            case 144: {
                if (queryNode.getType() == 10 || queryNode.getType() == 12) {
                    node.childrenAccept(this, queryNode);
                    break;
                }
                this.exceptions.add(new InvalidQueryException("Unsupported location for predicate"));
                break;
            }
            case 136: {
                if (queryNode instanceof LocationStepQueryNode) {
                    ((LocationStepQueryNode)queryNode).setNameTest(PATH_FACTORY.getParentElement().getName());
                    break;
                }
                ((RelationQueryNode)queryNode).addPathElement(PATH_FACTORY.getParentElement());
                break;
            }
            default: {
                node.childrenAccept(this, queryNode);
            }
        }
        return queryNode;
    }

    private void applyRelativePath(RelationQueryNode node) {
        Path relPath = this.getRelativePath();
        if (relPath != null) {
            for (int i = 0; i < relPath.getLength(); ++i) {
                node.addPathElement(relPath.getElements()[i]);
            }
        }
    }

    private Path getRelativePath() {
        try {
            if (this.tmpRelPath != null) {
                Path path = this.tmpRelPath.getPath();
                return path;
            }
        }
        catch (MalformedPathException malformedPathException) {
        }
        finally {
            this.tmpRelPath = null;
        }
        return null;
    }

    private LocationStepQueryNode createLocationStep(SimpleNode node, NAryQueryNode parent) {
        LocationStepQueryNode queryNode = null;
        boolean descendant = false;
        Node p = node.jjtGetParent();
        for (int i = 0; i < p.jjtGetNumChildren(); ++i) {
            SimpleNode c = (SimpleNode)p.jjtGetChild(i);
            if (c == node) {
                queryNode = this.factory.createLocationStepQueryNode(parent);
                queryNode.setNameTest(null);
                queryNode.setIncludeDescendants(descendant);
                parent.addOperand(queryNode);
                break;
            }
            descendant = c.getId() == 121 || c.getId() == 120;
        }
        node.childrenAccept(this, queryNode);
        return queryNode;
    }

    private void createNodeTest(SimpleNode node, QueryNode queryNode) {
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode child = (SimpleNode)node.jjtGetChild(0);
            if (child.getId() == 139 || child.getId() == 267) {
                try {
                    Name name = XPathQueryBuilder.decode(this.resolver.getQName(child.getValue()));
                    if (queryNode.getType() == 10) {
                        if (name.equals(JCR_ROOT)) {
                            name = LocationStepQueryNode.EMPTY_NAME;
                        }
                        ((LocationStepQueryNode)queryNode).setNameTest(name);
                    } else if (queryNode.getType() == 12) {
                        ((DerefQueryNode)queryNode).setRefProperty(name);
                    } else if (queryNode.getType() == 2) {
                        Path.Element element = PATH_FACTORY.createElement(name);
                        ((RelationQueryNode)queryNode).addPathElement(element);
                    } else if (queryNode.getType() == 11) {
                        this.root.addSelectProperty(name);
                    } else if (queryNode.getType() == 3) {
                        this.root.getOrderNode().addOrderSpec(name, true);
                    } else if (queryNode.getType() == 4) {
                        TextsearchQueryNode ts = (TextsearchQueryNode)queryNode;
                        ts.addPathElement(PATH_FACTORY.createElement(name));
                        if (this.isAttributeNameTest(node)) {
                            ts.setReferencesProperty(true);
                        }
                    }
                }
                catch (RepositoryException e) {
                    this.exceptions.add(new InvalidQueryException("Illegal name: " + child.getValue()));
                }
            } else if (child.getId() == 140) {
                if (queryNode.getType() == 10) {
                    ((LocationStepQueryNode)queryNode).setNameTest(null);
                } else if (queryNode.getType() == 2) {
                    ((RelationQueryNode)queryNode).addPathElement(PATH_FACTORY.createElement(RelationQueryNode.STAR_NAME_TEST));
                } else if (queryNode.getType() == 4) {
                    ((TextsearchQueryNode)queryNode).addPathElement(PATH_FACTORY.createElement(RelationQueryNode.STAR_NAME_TEST));
                }
            } else {
                this.exceptions.add(new InvalidQueryException("Unsupported location for name test: " + child));
            }
        }
    }

    private void createExpression(SimpleNode node, NAryQueryNode queryNode) {
        if (node.getId() != 101) {
            throw new IllegalArgumentException("node must be of type ComparisonExpr");
        }
        String opType = node.getValue();
        int type = 0;
        if (opType.equals(OP_EQ)) {
            type = 11;
        } else if (opType.equals(OP_SIGN_EQ)) {
            type = 12;
        } else if (opType.equals(OP_GT)) {
            type = 17;
        } else if (opType.equals(OP_SIGN_GT)) {
            type = 18;
        } else if (opType.equals(OP_GE)) {
            type = 19;
        } else if (opType.equals(OP_SIGN_GE)) {
            type = 20;
        } else if (opType.equals(OP_LE)) {
            type = 21;
        } else if (opType.equals(OP_SIGN_LE)) {
            type = 22;
        } else if (opType.equals(OP_LT)) {
            type = 15;
        } else if (opType.equals(OP_SIGN_LT)) {
            type = 16;
        } else if (opType.equals(OP_NE)) {
            type = 13;
        } else if (opType.equals(OP_SIGN_NE)) {
            type = 14;
        } else {
            this.exceptions.add(new InvalidQueryException("Unsupported ComparisonExpr type:" + node.getValue()));
        }
        final RelationQueryNode rqn = this.factory.createRelationQueryNode(queryNode, type);
        node.childrenAccept(this, rqn);
        try {
            rqn.acceptOperands(new DefaultQueryNodeVisitor(){

                @Override
                public Object visit(PropertyFunctionQueryNode node, Object data) {
                    String functionName = node.getFunctionName();
                    if ((functionName.equals("lower-case") || functionName.equals("upper-case")) && rqn.getValueType() != 3) {
                        String msg = "Upper and lower case function are only supported with String literals";
                        XPathQueryBuilder.this.exceptions.add(new InvalidQueryException(msg));
                    }
                    return data;
                }
            }, null);
        }
        catch (RepositoryException e) {
            this.exceptions.add(e);
        }
        queryNode.addOperand(rqn);
    }

    private PathQueryNode createPathQueryNode(SimpleNode node) {
        this.root.setLocationNode(this.factory.createPathQueryNode(this.root));
        node.childrenAccept(this, this.root.getLocationNode());
        return this.root.getLocationNode();
    }

    private void assignValue(SimpleNode node, RelationQueryNode queryNode) {
        if (node.getId() == 47) {
            queryNode.setStringValue(this.unescapeQuotes(node.getValue()));
        } else if (node.getId() == 146) {
            queryNode.setDoubleValue(Double.parseDouble(node.getValue()));
        } else if (node.getId() == 147) {
            queryNode.setDoubleValue(Double.parseDouble(node.getValue()));
        } else if (node.getId() == 145) {
            if (queryNode.getValueType() == 6) {
                queryNode.setPositionValue(Integer.parseInt(node.getValue()));
            } else {
                queryNode.setLongValue(Long.parseLong(node.getValue()));
            }
        } else {
            this.exceptions.add(new InvalidQueryException("Unsupported literal type:" + node.toString()));
        }
    }

    private QueryNode createFunction(SimpleNode node, QueryNode queryNode) {
        String tmp = ((SimpleNode)node.jjtGetChild(0)).getValue();
        String fName = tmp.substring(0, tmp.length() - 1);
        try {
            Name funName = this.resolver.getQName(fName);
            if (FN_NOT.equals(funName) || FN_NOT_10.equals(funName)) {
                if (queryNode instanceof NAryQueryNode) {
                    NotQueryNode not = this.factory.createNotQueryNode(queryNode);
                    ((NAryQueryNode)queryNode).addOperand(not);
                    queryNode = not;
                    if (node.jjtGetNumChildren() == 2) {
                        node.jjtGetChild(1).jjtAccept(this, queryNode);
                    } else {
                        this.exceptions.add(new InvalidQueryException("fn:not only supports one expression argument"));
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Unsupported location for function fn:not"));
                }
            } else if (XS_DATETIME.equals(funName)) {
                if (node.jjtGetNumChildren() == 2) {
                    if (queryNode instanceof RelationQueryNode) {
                        RelationQueryNode rel = (RelationQueryNode)queryNode;
                        SimpleNode literal = (SimpleNode)node.jjtGetChild(1).jjtGetChild(0);
                        if (literal.getId() == 47) {
                            String value = literal.getValue();
                            Calendar c = ISO8601.parse(value = value.substring(1, value.length() - 1));
                            if (c == null) {
                                this.exceptions.add(new InvalidQueryException("Unable to parse string literal for xs:dateTime: " + value));
                            } else {
                                rel.setDateValue(c.getTime());
                            }
                        } else {
                            this.exceptions.add(new InvalidQueryException("Wrong argument type for xs:dateTime"));
                        }
                    } else {
                        this.exceptions.add(new InvalidQueryException("Unsupported location for function xs:dateTime"));
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Wrong number of arguments for xs:dateTime"));
                }
            } else if (JCR_CONTAINS.equals(funName)) {
                if (node.jjtGetNumChildren() == 3) {
                    if (queryNode instanceof NAryQueryNode) {
                        SimpleNode literal = (SimpleNode)node.jjtGetChild(2).jjtGetChild(0);
                        if (literal.getId() == 47) {
                            TextsearchQueryNode contains = this.factory.createTextsearchQueryNode(queryNode, this.unescapeQuotes(literal.getValue()));
                            SimpleNode path = (SimpleNode)node.jjtGetChild(1);
                            path.jjtAccept(this, contains);
                            ((NAryQueryNode)queryNode).addOperand(contains);
                        } else {
                            this.exceptions.add(new InvalidQueryException("Wrong argument type for jcr:contains"));
                        }
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Wrong number of arguments for jcr:contains"));
                }
            } else if (JCR_LIKE.equals(funName)) {
                if (node.jjtGetNumChildren() == 3) {
                    if (queryNode instanceof NAryQueryNode) {
                        SimpleNode literal;
                        RelationQueryNode like = this.factory.createRelationQueryNode(queryNode, 23);
                        ((NAryQueryNode)queryNode).addOperand(like);
                        node.jjtGetChild(1).jjtAccept(this, like);
                        if (like.getRelativePath() == null) {
                            this.exceptions.add(new InvalidQueryException("Wrong first argument type for jcr:like"));
                        }
                        if ((literal = (SimpleNode)node.jjtGetChild(2).jjtGetChild(0)).getId() == 47) {
                            like.setStringValue(this.unescapeQuotes(literal.getValue()));
                        } else {
                            this.exceptions.add(new InvalidQueryException("Wrong second argument type for jcr:like"));
                        }
                    } else {
                        this.exceptions.add(new InvalidQueryException("Unsupported location for function jcr:like"));
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Wrong number of arguments for jcr:like"));
                }
            } else if (FN_TRUE.equals(funName)) {
                if (queryNode.getType() == 2) {
                    RelationQueryNode rel = (RelationQueryNode)queryNode;
                    rel.setStringValue("true");
                } else {
                    this.exceptions.add(new InvalidQueryException("Unsupported location for true()"));
                }
            } else if (FN_FALSE.equals(funName)) {
                if (queryNode.getType() == 2) {
                    RelationQueryNode rel = (RelationQueryNode)queryNode;
                    rel.setStringValue("false");
                } else {
                    this.exceptions.add(new InvalidQueryException("Unsupported location for false()"));
                }
            } else if (FN_POSITION.equals(funName)) {
                if (queryNode.getType() == 2) {
                    RelationQueryNode rel = (RelationQueryNode)queryNode;
                    if (rel.getOperation() == 12) {
                        rel.setPositionValue(1);
                        rel.addPathElement(PATH_FACTORY.createElement(FN_POSITION_FULL));
                    } else {
                        this.exceptions.add(new InvalidQueryException("Unsupported expression with position(). Only = is supported."));
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Unsupported location for position()"));
                }
            } else if (FN_FIRST.equals(funName)) {
                if (queryNode.getType() == 2) {
                    ((RelationQueryNode)queryNode).setPositionValue(1);
                } else if (queryNode.getType() == 10) {
                    ((LocationStepQueryNode)queryNode).setIndex(1);
                } else {
                    this.exceptions.add(new InvalidQueryException("Unsupported location for first()"));
                }
            } else if (FN_LAST.equals(funName)) {
                if (queryNode.getType() == 2) {
                    ((RelationQueryNode)queryNode).setPositionValue(Integer.MIN_VALUE);
                } else if (queryNode.getType() == 10) {
                    ((LocationStepQueryNode)queryNode).setIndex(Integer.MIN_VALUE);
                } else {
                    this.exceptions.add(new InvalidQueryException("Unsupported location for last()"));
                }
            } else if (JCR_DEREF.equals(funName)) {
                if (node.jjtGetNumChildren() == 3) {
                    boolean descendant = false;
                    if (queryNode.getType() == 10) {
                        LocationStepQueryNode loc = (LocationStepQueryNode)queryNode;
                        descendant = loc.getIncludeDescendants();
                        queryNode = loc.getParent();
                        ((NAryQueryNode)queryNode).removeOperand(loc);
                    }
                    if (queryNode.getType() == 11) {
                        PathQueryNode pathNode = (PathQueryNode)queryNode;
                        pathNode.addPathStep(this.createDerefQueryNode(node, descendant, pathNode));
                    } else if (queryNode.getType() == 2) {
                        RelationQueryNode relNode = (RelationQueryNode)queryNode;
                        DerefQueryNode deref = this.createDerefQueryNode(node, descendant, relNode.getRelativePath());
                        relNode.getRelativePath().addPathStep(deref);
                    } else {
                        this.exceptions.add(new InvalidQueryException("Unsupported location for jcr:deref()"));
                    }
                }
            } else if (JCR_SCORE.equals(funName)) {
                if (queryNode.getType() == 3) {
                    this.setOrderSpecPath(node, (OrderQueryNode)queryNode);
                } else {
                    this.exceptions.add(new InvalidQueryException("Unsupported location for jcr:score()"));
                }
            } else if (FN_LOWER_CASE.equals(funName)) {
                if (node.jjtGetNumChildren() == 2) {
                    if (queryNode.getType() == 2) {
                        RelationQueryNode relNode = (RelationQueryNode)queryNode;
                        relNode.addOperand(this.factory.createPropertyFunctionQueryNode(relNode, "lower-case"));
                        node.jjtGetChild(1).jjtAccept(this, relNode);
                    } else if (queryNode.getType() == 3) {
                        ((OrderQueryNode)queryNode).setFunction(FN_LOWER_CASE.getLocalName());
                        node.childrenAccept(this, queryNode);
                    } else {
                        this.exceptions.add(new InvalidQueryException("Unsupported location for fn:lower-case()"));
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Wrong number of argument for fn:lower-case()"));
                }
            } else if (FN_UPPER_CASE.equals(funName)) {
                if (node.jjtGetNumChildren() == 2) {
                    if (queryNode.getType() == 2) {
                        RelationQueryNode relNode = (RelationQueryNode)queryNode;
                        relNode.addOperand(this.factory.createPropertyFunctionQueryNode(relNode, "upper-case"));
                        node.jjtGetChild(1).jjtAccept(this, relNode);
                    } else if (queryNode.getType() == 3) {
                        ((OrderQueryNode)queryNode).setFunction(FN_UPPER_CASE.getLocalName());
                        node.childrenAccept(this, queryNode);
                    } else {
                        this.exceptions.add(new InvalidQueryException("Unsupported location for fn:upper-case()"));
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Wrong number of argument for fn:upper-case()"));
                }
            } else if (REP_NORMALIZE.equals(funName)) {
                if (node.jjtGetNumChildren() == 2) {
                    if (queryNode.getType() == 3) {
                        ((OrderQueryNode)queryNode).setFunction(REP_NORMALIZE.getLocalName());
                        node.childrenAccept(this, queryNode);
                    } else {
                        this.exceptions.add(new InvalidQueryException("Unsupported location for rep:normalize()"));
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Wrong number of argument for rep:normalize()"));
                }
            } else if (REP_SIMILAR.equals(funName)) {
                if (node.jjtGetNumChildren() == 3) {
                    if (queryNode instanceof NAryQueryNode) {
                        NAryQueryNode parent = (NAryQueryNode)queryNode;
                        RelationQueryNode rel = this.factory.createRelationQueryNode(parent, 28);
                        parent.addOperand(rel);
                        node.jjtGetChild(1).jjtAccept(this, rel);
                        node.jjtGetChild(2).jjtAccept(this, rel);
                        if (rel.getStringValue() == null) {
                            this.exceptions.add(new InvalidQueryException("Second argument for rep:similar() must be of type string"));
                        }
                    } else {
                        this.exceptions.add(new InvalidQueryException("Unsupported location for rep:similar()"));
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Wrong number of arguments for rep:similar()"));
                }
            } else if (REP_SPELLCHECK.equals(funName) && queryNode.getType() != 11) {
                if (node.jjtGetNumChildren() == 2) {
                    if (queryNode instanceof NAryQueryNode) {
                        NAryQueryNode parent = (NAryQueryNode)queryNode;
                        RelationQueryNode rel = this.factory.createRelationQueryNode(parent, 29);
                        parent.addOperand(rel);
                        node.jjtGetChild(1).jjtAccept(this, rel);
                        if (rel.getStringValue() == null) {
                            this.exceptions.add(new InvalidQueryException("Argument for rep:spellcheck() must be of type string"));
                        }
                        rel.addPathElement(PATH_FACTORY.createElement(NameConstants.JCR_PRIMARYTYPE));
                    } else {
                        this.exceptions.add(new InvalidQueryException("Unsupported location for rep:spellcheck()"));
                    }
                } else {
                    this.exceptions.add(new InvalidQueryException("Wrong number of arguments for rep:spellcheck()"));
                }
            } else if (queryNode.getType() == 2) {
                try {
                    Name name = this.resolver.getQName(fName + "()");
                    Path.Element element = PATH_FACTORY.createElement(name);
                    RelationQueryNode relNode = (RelationQueryNode)queryNode;
                    relNode.addPathElement(element);
                }
                catch (NameException e) {
                    this.exceptions.add(e);
                }
            } else if (queryNode.getType() == 11) {
                try {
                    Name name = this.resolver.getQName(fName + "()");
                    this.root.addSelectProperty(name);
                }
                catch (NameException e) {
                    this.exceptions.add(e);
                }
            } else {
                this.exceptions.add(new InvalidQueryException("Unsupported function: " + fName));
            }
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        catch (IllegalNameException e) {
            this.exceptions.add(e);
        }
        return queryNode;
    }

    private DerefQueryNode createDerefQueryNode(SimpleNode node, boolean descendant, QueryNode pathNode) throws NamespaceException {
        SimpleNode literal;
        DerefQueryNode derefNode = this.factory.createDerefQueryNode(pathNode, null, false);
        node.jjtGetChild(1).jjtAccept(this, derefNode);
        if (derefNode.getRefProperty() == null) {
            this.exceptions.add(new InvalidQueryException("Wrong first argument type for jcr:deref"));
        }
        if ((literal = (SimpleNode)node.jjtGetChild(2).jjtGetChild(0)).getId() == 47) {
            String value = literal.getValue();
            if (!(value = value.substring(1, value.length() - 1)).equals("*")) {
                Name name = null;
                try {
                    name = XPathQueryBuilder.decode(this.resolver.getQName(value));
                }
                catch (NameException e) {
                    this.exceptions.add(new InvalidQueryException("Illegal name: " + value));
                }
                derefNode.setNameTest(name);
            }
        } else {
            this.exceptions.add(new InvalidQueryException("Second argument for jcr:deref must be a String"));
        }
        if (!descendant) {
            SimpleNode c;
            Node p = node.jjtGetParent();
            for (int i = 0; i < p.jjtGetNumChildren() && (c = (SimpleNode)p.jjtGetChild(i)) != node; ++i) {
                descendant = c.getId() == 121 || c.getId() == 120;
            }
        }
        derefNode.setIncludeDescendants(descendant);
        return derefNode;
    }

    private void setOrderSpecPath(SimpleNode node, OrderQueryNode queryNode) {
        SimpleNode child = (SimpleNode)node.jjtGetChild(0);
        try {
            String propName = child.getValue();
            if (child.getId() == 62) {
                propName = propName.substring(0, propName.length() - 1);
            }
            Path.Element element = PathFactoryImpl.getInstance().createElement(XPathQueryBuilder.decode(this.resolver.getQName(propName)));
            Path path = this.getRelativePath();
            path = path != null ? path.resolve(element) : PathFactoryImpl.getInstance().create(element);
            queryNode.setPath(path);
        }
        catch (NameException e) {
            this.exceptions.add(new InvalidQueryException("Illegal name: " + child.getValue()));
        }
        catch (NamespaceException e) {
            this.exceptions.add(new InvalidQueryException("Illegal name: " + child.getValue()));
        }
    }

    private boolean isAttributeAxis(SimpleNode node) {
        for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
            if (((SimpleNode)node.jjtGetChild(i)).getId() != 130) continue;
            return true;
        }
        return false;
    }

    private boolean isAttributeNameTest(SimpleNode node) {
        SimpleNode stepExpr = (SimpleNode)node.jjtGetParent().jjtGetParent();
        if (stepExpr.getId() == 122) {
            return ((SimpleNode)stepExpr.jjtGetChild(0)).getId() == 130;
        }
        return false;
    }

    private String unescapeQuotes(String literal) {
        String value = literal.substring(1, literal.length() - 1);
        if (value.length() == 0) {
            return value;
        }
        value = literal.charAt(0) == '\"' ? value.replaceAll("\"\"", "\"") : value.replaceAll("''", "'");
        return value;
    }

    private static Name decode(Name name) {
        String decodedLN = ISO9075.decode(name.getLocalName());
        if (decodedLN.equals(name.getLocalName())) {
            return name;
        }
        return NAME_FACTORY.create(name.getNamespaceURI(), decodedLN);
    }
}

