/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.pattern;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.jaxen.JaxenException;
import org.jaxen.JaxenHandler;
import org.jaxen.expr.DefaultAllNodeStep;
import org.jaxen.expr.DefaultCommentNodeStep;
import org.jaxen.expr.DefaultFilterExpr;
import org.jaxen.expr.DefaultNameStep;
import org.jaxen.expr.DefaultProcessingInstructionNodeStep;
import org.jaxen.expr.DefaultStep;
import org.jaxen.expr.DefaultTextNodeStep;
import org.jaxen.expr.DefaultXPathFactory;
import org.jaxen.expr.Expr;
import org.jaxen.expr.FilterExpr;
import org.jaxen.expr.LocationPath;
import org.jaxen.expr.Predicate;
import org.jaxen.expr.PredicateSet;
import org.jaxen.expr.Step;
import org.jaxen.expr.UnionExpr;
import org.jaxen.pattern.LocationPathPattern;
import org.jaxen.pattern.NameTest;
import org.jaxen.pattern.NamespaceTest;
import org.jaxen.pattern.NodeTypeTest;
import org.jaxen.pattern.Pattern;
import org.jaxen.pattern.TextNodeTest;
import org.jaxen.pattern.UnionPattern;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathReader;
import org.jaxen.saxpath.helpers.XPathReaderFactory;

public class PatternParser {
    private static final boolean TRACE = false;
    private static final boolean USE_HANDLER = false;
    static /* synthetic */ Class class$org$jaxen$expr$DefaultStep;

    public static Pattern parse(String text) throws JaxenException, SAXPathException {
        XPathReader reader = XPathReaderFactory.createReader();
        JaxenHandler handler = new JaxenHandler();
        handler.setXPathFactory(new DefaultXPathFactory());
        reader.setXPathHandler(handler);
        reader.parse(text);
        Pattern pattern = PatternParser.convertExpr(handler.getXPathExpr().getRootExpr());
        return pattern.simplify();
    }

    protected static Pattern convertExpr(Expr expr) throws JaxenException {
        if (expr instanceof LocationPath) {
            return PatternParser.convertExpr((LocationPath)expr);
        }
        if (expr instanceof FilterExpr) {
            LocationPathPattern answer = new LocationPathPattern();
            answer.addFilter((FilterExpr)expr);
            return answer;
        }
        if (expr instanceof UnionExpr) {
            UnionExpr unionExpr = (UnionExpr)expr;
            Pattern lhs = PatternParser.convertExpr(unionExpr.getLHS());
            Pattern rhs = PatternParser.convertExpr(unionExpr.getRHS());
            return new UnionPattern(lhs, rhs);
        }
        LocationPathPattern answer = new LocationPathPattern();
        answer.addFilter(new DefaultFilterExpr(expr, new PredicateSet()));
        return answer;
    }

    protected static LocationPathPattern convertExpr(LocationPath locationPath) throws JaxenException {
        LocationPathPattern answer = new LocationPathPattern();
        List steps = locationPath.getSteps();
        LocationPathPattern path = answer;
        boolean first = true;
        ListIterator iter = steps.listIterator(steps.size());
        while (iter.hasPrevious()) {
            Step step = (Step)iter.previous();
            if (first) {
                first = false;
                path = PatternParser.convertStep(path, step);
                continue;
            }
            if (PatternParser.navigationStep(step)) {
                LocationPathPattern parent = new LocationPathPattern();
                int axis = step.getAxis();
                if (axis == 2 || axis == 12) {
                    path.setAncestorPattern(parent);
                } else {
                    path.setParentPattern(parent);
                }
                path = parent;
            }
            path = PatternParser.convertStep(path, step);
        }
        if (locationPath.isAbsolute()) {
            LocationPathPattern parent = new LocationPathPattern(NodeTypeTest.DOCUMENT_TEST);
            path.setParentPattern(parent);
        }
        return answer;
    }

    protected static LocationPathPattern convertStep(LocationPathPattern path, Step step) throws JaxenException {
        if (step instanceof DefaultAllNodeStep) {
            int axis = step.getAxis();
            if (axis == 9) {
                path.setNodeTest(NodeTypeTest.ATTRIBUTE_TEST);
            } else {
                path.setNodeTest(NodeTypeTest.ELEMENT_TEST);
            }
        } else if (step instanceof DefaultCommentNodeStep) {
            path.setNodeTest(NodeTypeTest.COMMENT_TEST);
        } else if (step instanceof DefaultProcessingInstructionNodeStep) {
            path.setNodeTest(NodeTypeTest.PROCESSING_INSTRUCTION_TEST);
        } else if (step instanceof DefaultTextNodeStep) {
            path.setNodeTest(TextNodeTest.SINGLETON);
        } else if (step instanceof DefaultCommentNodeStep) {
            path.setNodeTest(NodeTypeTest.COMMENT_TEST);
        } else {
            if (step instanceof DefaultNameStep) {
                DefaultNameStep nameStep = (DefaultNameStep)step;
                String localName = nameStep.getLocalName();
                String prefix = nameStep.getPrefix();
                int axis = nameStep.getAxis();
                short nodeType = 1;
                if (axis == 9) {
                    nodeType = 2;
                }
                if (nameStep.isMatchesAnyName()) {
                    if (prefix.length() == 0 || prefix.equals("*")) {
                        if (axis == 9) {
                            path.setNodeTest(NodeTypeTest.ATTRIBUTE_TEST);
                        } else {
                            path.setNodeTest(NodeTypeTest.ELEMENT_TEST);
                        }
                    } else {
                        path.setNodeTest(new NamespaceTest(prefix, nodeType));
                    }
                } else {
                    path.setNodeTest(new NameTest(localName, nodeType));
                }
                return PatternParser.convertDefaultStep(path, nameStep);
            }
            if (step instanceof DefaultStep) {
                return PatternParser.convertDefaultStep(path, (DefaultStep)step);
            }
            throw new JaxenException("Cannot convert: " + step + " to a Pattern");
        }
        return path;
    }

    protected static LocationPathPattern convertDefaultStep(LocationPathPattern path, DefaultStep step) throws JaxenException {
        List predicates = step.getPredicates();
        if (!predicates.isEmpty()) {
            DefaultFilterExpr filter = new DefaultFilterExpr(new PredicateSet());
            Iterator iter = predicates.iterator();
            while (iter.hasNext()) {
                filter.addPredicate((Predicate)iter.next());
            }
            path.addFilter(filter);
        }
        return path;
    }

    protected static boolean navigationStep(Step step) {
        if (step instanceof DefaultNameStep) {
            return true;
        }
        if (step.getClass().equals(class$org$jaxen$expr$DefaultStep == null ? (class$org$jaxen$expr$DefaultStep = PatternParser.class$("org.jaxen.expr.DefaultStep")) : class$org$jaxen$expr$DefaultStep)) {
            return !step.getPredicates().isEmpty();
        }
        return true;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

