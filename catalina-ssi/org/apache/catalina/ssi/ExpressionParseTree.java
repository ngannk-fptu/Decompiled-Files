/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ssi;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.catalina.ssi.ExpressionTokenizer;
import org.apache.catalina.ssi.SSIMediator;
import org.apache.catalina.ssi.SSIStopProcessingException;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public class ExpressionParseTree {
    private static final StringManager sm = StringManager.getManager(ExpressionParseTree.class);
    private final LinkedList<Node> nodeStack = new LinkedList();
    private final LinkedList<OppNode> oppStack = new LinkedList();
    private Node root;
    private final SSIMediator ssiMediator;
    private static final int PRECEDENCE_NOT = 5;
    private static final int PRECEDENCE_COMPARE = 4;
    private static final int PRECEDENCE_LOGICAL = 1;

    public ExpressionParseTree(String expr, SSIMediator ssiMediator) throws ParseException {
        this.ssiMediator = ssiMediator;
        this.parseExpression(expr);
    }

    public boolean evaluateTree() throws SSIStopProcessingException {
        try {
            return this.root.evaluate();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            throw new SSIStopProcessingException(t);
        }
    }

    private void pushOpp(OppNode node) {
        OppNode top;
        if (node == null) {
            this.oppStack.add(0, node);
            return;
        }
        while (this.oppStack.size() != 0 && (top = this.oppStack.get(0)) != null && top.getPrecedence() >= node.getPrecedence()) {
            this.oppStack.remove(0);
            top.popValues(this.nodeStack);
            this.nodeStack.add(0, top);
        }
        this.oppStack.add(0, node);
    }

    private void resolveGroup() {
        OppNode top = null;
        while ((top = this.oppStack.remove(0)) != null) {
            top.popValues(this.nodeStack);
            this.nodeStack.add(0, top);
        }
    }

    private void parseExpression(String expr) throws ParseException {
        StringNode currStringNode = null;
        this.pushOpp(null);
        ExpressionTokenizer et = new ExpressionTokenizer(expr);
        while (et.hasMoreTokens()) {
            int token = et.nextToken();
            if (token != 0) {
                currStringNode = null;
            }
            switch (token) {
                case 0: {
                    if (currStringNode == null) {
                        currStringNode = new StringNode(et.getTokenValue());
                        this.nodeStack.add(0, currStringNode);
                        break;
                    }
                    currStringNode.value.append(' ');
                    currStringNode.value.append(et.getTokenValue());
                    break;
                }
                case 1: {
                    this.pushOpp(new AndNode());
                    break;
                }
                case 2: {
                    this.pushOpp(new OrNode());
                    break;
                }
                case 3: {
                    this.pushOpp(new NotNode());
                    break;
                }
                case 4: {
                    this.pushOpp(new EqualNode());
                    break;
                }
                case 5: {
                    this.pushOpp(new NotNode());
                    this.oppStack.add(0, new EqualNode());
                    break;
                }
                case 6: {
                    this.resolveGroup();
                    break;
                }
                case 7: {
                    this.pushOpp(null);
                    break;
                }
                case 8: {
                    this.pushOpp(new NotNode());
                    this.oppStack.add(0, new LessThanNode());
                    break;
                }
                case 9: {
                    this.pushOpp(new NotNode());
                    this.oppStack.add(0, new GreaterThanNode());
                    break;
                }
                case 10: {
                    this.pushOpp(new GreaterThanNode());
                    break;
                }
                case 11: {
                    this.pushOpp(new LessThanNode());
                    break;
                }
            }
        }
        this.resolveGroup();
        if (this.nodeStack.size() == 0) {
            throw new ParseException(sm.getString("expressionParseTree.noNodes"), et.getIndex());
        }
        if (this.nodeStack.size() > 1) {
            throw new ParseException(sm.getString("expressionParseTree.extraNodes"), et.getIndex());
        }
        if (this.oppStack.size() != 0) {
            throw new ParseException(sm.getString("expressionParseTree.unusedOpCodes"), et.getIndex());
        }
        this.root = this.nodeStack.get(0);
    }

    private static abstract class Node {
        private Node() {
        }

        public abstract boolean evaluate();
    }

    private static abstract class OppNode
    extends Node {
        Node left;
        Node right;

        private OppNode() {
        }

        public abstract int getPrecedence();

        public void popValues(List<Node> values) {
            this.right = values.remove(0);
            this.left = values.remove(0);
        }
    }

    private class StringNode
    extends Node {
        StringBuilder value;
        String resolved = null;

        StringNode(String value) {
            this.value = new StringBuilder(value);
        }

        public String getValue() {
            if (this.resolved == null) {
                this.resolved = ExpressionParseTree.this.ssiMediator.substituteVariables(this.value.toString());
            }
            return this.resolved;
        }

        @Override
        public boolean evaluate() {
            return this.getValue().length() != 0;
        }

        public String toString() {
            return this.value.toString();
        }
    }

    private static final class AndNode
    extends OppNode {
        private AndNode() {
        }

        @Override
        public boolean evaluate() {
            if (!this.left.evaluate()) {
                return false;
            }
            return this.right.evaluate();
        }

        @Override
        public int getPrecedence() {
            return 1;
        }

        public String toString() {
            return this.left + " " + this.right + " AND";
        }
    }

    private static final class OrNode
    extends OppNode {
        private OrNode() {
        }

        @Override
        public boolean evaluate() {
            if (this.left.evaluate()) {
                return true;
            }
            return this.right.evaluate();
        }

        @Override
        public int getPrecedence() {
            return 1;
        }

        public String toString() {
            return this.left + " " + this.right + " OR";
        }
    }

    private static final class NotNode
    extends OppNode {
        private NotNode() {
        }

        @Override
        public boolean evaluate() {
            return !this.left.evaluate();
        }

        @Override
        public int getPrecedence() {
            return 5;
        }

        @Override
        public void popValues(List<Node> values) {
            this.left = values.remove(0);
        }

        public String toString() {
            return this.left + " NOT";
        }
    }

    private final class EqualNode
    extends CompareNode {
        private EqualNode() {
        }

        @Override
        public boolean evaluate() {
            return this.compareBranches() == 0;
        }

        @Override
        public int getPrecedence() {
            return 4;
        }

        public String toString() {
            return this.left + " " + this.right + " EQ";
        }
    }

    private final class LessThanNode
    extends CompareNode {
        private LessThanNode() {
        }

        @Override
        public boolean evaluate() {
            return this.compareBranches() < 0;
        }

        @Override
        public int getPrecedence() {
            return 4;
        }

        public String toString() {
            return this.left + " " + this.right + " LT";
        }
    }

    private final class GreaterThanNode
    extends CompareNode {
        private GreaterThanNode() {
        }

        @Override
        public boolean evaluate() {
            return this.compareBranches() > 0;
        }

        @Override
        public int getPrecedence() {
            return 4;
        }

        public String toString() {
            return this.left + " " + this.right + " GT";
        }
    }

    private abstract class CompareNode
    extends OppNode {
        private CompareNode() {
        }

        protected int compareBranches() {
            String val1 = ((StringNode)this.left).getValue();
            String val2 = ((StringNode)this.right).getValue();
            int val2Len = val2.length();
            if (val2Len > 1 && val2.charAt(0) == '/' && val2.charAt(val2Len - 1) == '/') {
                String expr = val2.substring(1, val2Len - 1);
                ExpressionParseTree.this.ssiMediator.clearMatchGroups();
                try {
                    Pattern pattern = Pattern.compile(expr);
                    Matcher matcher = pattern.matcher(val1);
                    if (matcher.find()) {
                        ExpressionParseTree.this.ssiMediator.populateMatchGroups(matcher);
                        return 0;
                    }
                    return -1;
                }
                catch (PatternSyntaxException pse) {
                    ExpressionParseTree.this.ssiMediator.log(sm.getString("expressionParseTree.invalidExpression", new Object[]{expr}), pse);
                    return 0;
                }
            }
            return val1.compareTo(val2);
        }
    }
}

