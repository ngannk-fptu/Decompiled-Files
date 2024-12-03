/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractOperatorNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.Operator;

public class OperatorNodes {
    private OperatorNodes() {
    }

    public static class ConditionalOpNode
    extends AbstractOperatorNode {
        public ConditionalOpNode() {
            super(Operator.CONDITIONAL);
        }

        protected ConditionalOpNode(ConditionalOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.CONDITIONAL_OP_NODE;
        }

        @Override
        public ConditionalOpNode clone() {
            return new ConditionalOpNode(this);
        }
    }

    public static class NullCoalescingOpNode
    extends AbstractOperatorNode {
        public NullCoalescingOpNode() {
            super(Operator.NULL_COALESCING);
        }

        protected NullCoalescingOpNode(NullCoalescingOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.NULL_COALESCING_OP_NODE;
        }

        @Override
        public NullCoalescingOpNode clone() {
            return new NullCoalescingOpNode(this);
        }
    }

    public static class OrOpNode
    extends AbstractOperatorNode {
        public OrOpNode() {
            super(Operator.OR);
        }

        protected OrOpNode(OrOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.OR_OP_NODE;
        }

        @Override
        public OrOpNode clone() {
            return new OrOpNode(this);
        }
    }

    public static class AndOpNode
    extends AbstractOperatorNode {
        public AndOpNode() {
            super(Operator.AND);
        }

        protected AndOpNode(AndOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.AND_OP_NODE;
        }

        @Override
        public AndOpNode clone() {
            return new AndOpNode(this);
        }
    }

    public static class NotEqualOpNode
    extends AbstractOperatorNode {
        public NotEqualOpNode() {
            super(Operator.NOT_EQUAL);
        }

        protected NotEqualOpNode(NotEqualOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.NOT_EQUAL_OP_NODE;
        }

        @Override
        public NotEqualOpNode clone() {
            return new NotEqualOpNode(this);
        }
    }

    public static class EqualOpNode
    extends AbstractOperatorNode {
        public EqualOpNode() {
            super(Operator.EQUAL);
        }

        protected EqualOpNode(EqualOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.EQUAL_OP_NODE;
        }

        @Override
        public EqualOpNode clone() {
            return new EqualOpNode(this);
        }
    }

    public static class GreaterThanOrEqualOpNode
    extends AbstractOperatorNode {
        public GreaterThanOrEqualOpNode() {
            super(Operator.GREATER_THAN_OR_EQUAL);
        }

        protected GreaterThanOrEqualOpNode(GreaterThanOrEqualOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.GREATER_THAN_OR_EQUAL_OP_NODE;
        }

        @Override
        public GreaterThanOrEqualOpNode clone() {
            return new GreaterThanOrEqualOpNode(this);
        }
    }

    public static class LessThanOrEqualOpNode
    extends AbstractOperatorNode {
        public LessThanOrEqualOpNode() {
            super(Operator.LESS_THAN_OR_EQUAL);
        }

        protected LessThanOrEqualOpNode(LessThanOrEqualOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.LESS_THAN_OR_EQUAL_OP_NODE;
        }

        @Override
        public LessThanOrEqualOpNode clone() {
            return new LessThanOrEqualOpNode(this);
        }
    }

    public static class GreaterThanOpNode
    extends AbstractOperatorNode {
        public GreaterThanOpNode() {
            super(Operator.GREATER_THAN);
        }

        protected GreaterThanOpNode(GreaterThanOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.GREATER_THAN_OP_NODE;
        }

        @Override
        public GreaterThanOpNode clone() {
            return new GreaterThanOpNode(this);
        }
    }

    public static class LessThanOpNode
    extends AbstractOperatorNode {
        public LessThanOpNode() {
            super(Operator.LESS_THAN);
        }

        protected LessThanOpNode(LessThanOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.LESS_THAN_OP_NODE;
        }

        @Override
        public LessThanOpNode clone() {
            return new LessThanOpNode(this);
        }
    }

    public static class MinusOpNode
    extends AbstractOperatorNode {
        public MinusOpNode() {
            super(Operator.MINUS);
        }

        protected MinusOpNode(MinusOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.MINUS_OP_NODE;
        }

        @Override
        public MinusOpNode clone() {
            return new MinusOpNode(this);
        }
    }

    public static class PlusOpNode
    extends AbstractOperatorNode {
        public PlusOpNode() {
            super(Operator.PLUS);
        }

        protected PlusOpNode(PlusOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.PLUS_OP_NODE;
        }

        @Override
        public PlusOpNode clone() {
            return new PlusOpNode(this);
        }
    }

    public static class ModOpNode
    extends AbstractOperatorNode {
        public ModOpNode() {
            super(Operator.MOD);
        }

        protected ModOpNode(ModOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.MOD_OP_NODE;
        }

        @Override
        public ModOpNode clone() {
            return new ModOpNode(this);
        }
    }

    public static class DivideByOpNode
    extends AbstractOperatorNode {
        public DivideByOpNode() {
            super(Operator.DIVIDE_BY);
        }

        protected DivideByOpNode(DivideByOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.DIVIDE_BY_OP_NODE;
        }

        @Override
        public DivideByOpNode clone() {
            return new DivideByOpNode(this);
        }
    }

    public static class TimesOpNode
    extends AbstractOperatorNode {
        public TimesOpNode() {
            super(Operator.TIMES);
        }

        protected TimesOpNode(TimesOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.TIMES_OP_NODE;
        }

        @Override
        public TimesOpNode clone() {
            return new TimesOpNode(this);
        }
    }

    public static class NotOpNode
    extends AbstractOperatorNode {
        public NotOpNode() {
            super(Operator.NOT);
        }

        protected NotOpNode(NotOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.NOT_OP_NODE;
        }

        @Override
        public NotOpNode clone() {
            return new NotOpNode(this);
        }
    }

    public static class NegativeOpNode
    extends AbstractOperatorNode {
        public NegativeOpNode() {
            super(Operator.NEGATIVE);
        }

        protected NegativeOpNode(NegativeOpNode orig) {
            super(orig);
        }

        @Override
        public ExprNode.Kind getKind() {
            return ExprNode.Kind.NEGATIVE_OP_NODE;
        }

        @Override
        public NegativeOpNode clone() {
            return new NegativeOpNode(this);
        }
    }
}

