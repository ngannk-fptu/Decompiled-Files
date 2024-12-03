/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 */
package com.google.template.soy.exprtree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.OperatorNodes;
import com.google.template.soy.internal.base.Pair;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Operator {
    NEGATIVE((List<SyntaxElement>)ImmutableList.of((Object)new Token("-"), (Object)Constants.OPERAND_0), 8, Associativity.RIGHT, "- (unary)", OperatorNodes.NegativeOpNode.class),
    NOT((List<SyntaxElement>)ImmutableList.of((Object)new Token("not"), (Object)Constants.SP, (Object)Constants.OPERAND_0), 8, Associativity.RIGHT, OperatorNodes.NotOpNode.class),
    TIMES((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("*"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 7, Associativity.LEFT, OperatorNodes.TimesOpNode.class),
    DIVIDE_BY((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("/"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 7, Associativity.LEFT, OperatorNodes.DivideByOpNode.class),
    MOD((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("%"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 7, Associativity.LEFT, OperatorNodes.ModOpNode.class),
    PLUS((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("+"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 6, Associativity.LEFT, OperatorNodes.PlusOpNode.class),
    MINUS((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("-"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 6, Associativity.LEFT, "- (binary)", OperatorNodes.MinusOpNode.class),
    LESS_THAN((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("<"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 5, Associativity.LEFT, OperatorNodes.LessThanOpNode.class),
    GREATER_THAN((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token(">"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 5, Associativity.LEFT, OperatorNodes.GreaterThanOpNode.class),
    LESS_THAN_OR_EQUAL((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("<="), (Object)Constants.SP, (Object)Constants.OPERAND_1), 5, Associativity.LEFT, OperatorNodes.LessThanOrEqualOpNode.class),
    GREATER_THAN_OR_EQUAL((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token(">="), (Object)Constants.SP, (Object)Constants.OPERAND_1), 5, Associativity.LEFT, OperatorNodes.GreaterThanOrEqualOpNode.class),
    EQUAL((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("=="), (Object)Constants.SP, (Object)Constants.OPERAND_1), 4, Associativity.LEFT, OperatorNodes.EqualOpNode.class),
    NOT_EQUAL((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("!="), (Object)Constants.SP, (Object)Constants.OPERAND_1), 4, Associativity.LEFT, OperatorNodes.NotEqualOpNode.class),
    AND((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("and"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 3, Associativity.LEFT, OperatorNodes.AndOpNode.class),
    OR((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("or"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 2, Associativity.LEFT, OperatorNodes.OrOpNode.class),
    NULL_COALESCING((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("?:"), (Object)Constants.SP, (Object)Constants.OPERAND_1), 1, Associativity.RIGHT, OperatorNodes.NullCoalescingOpNode.class),
    CONDITIONAL((List<SyntaxElement>)ImmutableList.of((Object)Constants.OPERAND_0, (Object)Constants.SP, (Object)new Token("?"), (Object)Constants.SP, (Object)Constants.OPERAND_1, (Object)Constants.SP, (Object)new Token(":"), (Object)Constants.SP, (Object)Constants.OPERAND_2), 1, Associativity.RIGHT, OperatorNodes.ConditionalOpNode.class);

    private static final Map<Pair<String, Integer>, Operator> FETCH_MAP;
    private final List<SyntaxElement> syntax;
    private final String tokenString;
    private final int numOperands;
    private final int precedence;
    private final Associativity associativity;
    private final String description;
    private final Class<? extends ExprNode.OperatorNode> nodeClass;

    public static Operator of(String tokenString, int numOperands) {
        Operator op = FETCH_MAP.get(Pair.of(tokenString, numOperands));
        if (op != null) {
            return op;
        }
        throw new IllegalArgumentException();
    }

    private Operator(List<SyntaxElement> syntax, int precedence, Associativity associativity, Class<? extends ExprNode.OperatorNode> nodeClass) {
        this(syntax, precedence, associativity, null, nodeClass);
    }

    private Operator(List<SyntaxElement> syntax, int precedence, Associativity associativity, String description, Class<? extends ExprNode.OperatorNode> nodeClass) {
        this.syntax = syntax;
        String tokenString = null;
        int numOperands = 0;
        for (SyntaxElement syntaxEl : syntax) {
            if (syntaxEl instanceof Operand) {
                ++numOperands;
                continue;
            }
            if (!(syntaxEl instanceof Token)) continue;
            if (tokenString == null) {
                tokenString = ((Token)syntaxEl).getValue();
                continue;
            }
            tokenString = tokenString + " " + ((Token)syntaxEl).getValue();
        }
        Preconditions.checkArgument((tokenString != null && numOperands > 0 ? 1 : 0) != 0);
        this.tokenString = tokenString;
        this.numOperands = numOperands;
        this.precedence = precedence;
        this.associativity = associativity;
        this.description = description != null ? description : tokenString;
        this.nodeClass = nodeClass;
    }

    public List<SyntaxElement> getSyntax() {
        return this.syntax;
    }

    public String getTokenString() {
        return this.tokenString;
    }

    public int getNumOperands() {
        return this.numOperands;
    }

    public int getPrecedence() {
        return this.precedence;
    }

    public Associativity getAssociativity() {
        return this.associativity;
    }

    public String getDescription() {
        return this.description;
    }

    public Class<? extends ExprNode.OperatorNode> getNodeClass() {
        return this.nodeClass;
    }

    static {
        HashMap fetchMap = Maps.newHashMap();
        for (Operator op : Operator.values()) {
            fetchMap.put(Pair.of(op.getTokenString(), op.getNumOperands()), op);
        }
        FETCH_MAP = Collections.unmodifiableMap(fetchMap);
    }

    public static class Spacer
    implements SyntaxElement {
        private Spacer() {
        }
    }

    public static class Token
    implements SyntaxElement {
        private final String value;

        private Token(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static class Operand
    implements SyntaxElement {
        private final int index;

        private Operand(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }
    }

    public static interface SyntaxElement {
    }

    public static enum Associativity {
        LEFT,
        RIGHT;

    }

    static class Constants {
        static final Spacer SP = new Spacer();
        static final Operand OPERAND_0 = new Operand(0);
        static final Operand OPERAND_1 = new Operand(1);
        static final Operand OPERAND_2 = new Operand(2);

        Constants() {
        }
    }
}

