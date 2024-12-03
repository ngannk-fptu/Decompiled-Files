/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.AbstractOperatorNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FieldAccessNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.ItemAccessNode;
import com.google.template.soy.exprtree.ListLiteralNode;
import com.google.template.soy.exprtree.MapLiteralNode;
import com.google.template.soy.exprtree.OperatorNodes;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.ForNode;
import com.google.template.soy.soytree.ForeachNonemptyNode;
import com.google.template.soy.soytree.IfCondNode;
import com.google.template.soy.soytree.IfElseNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.LetContentNode;
import com.google.template.soy.soytree.LetValueNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.types.SoyObjectType;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.SoyTypeOps;
import com.google.template.soy.types.SoyTypeRegistry;
import com.google.template.soy.types.aggregate.ListType;
import com.google.template.soy.types.aggregate.MapType;
import com.google.template.soy.types.aggregate.UnionType;
import com.google.template.soy.types.primitive.BoolType;
import com.google.template.soy.types.primitive.IntType;
import com.google.template.soy.types.primitive.NullType;
import com.google.template.soy.types.primitive.PrimitiveType;
import com.google.template.soy.types.primitive.SanitizedType;
import com.google.template.soy.types.primitive.StringType;
import com.google.template.soy.types.primitive.UnknownType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ResolveExpressionTypesVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final SyntaxVersion declaredSyntaxVersion;
    private final SoyTypeOps typeOps;
    private TypeSubstitution substitutions;

    public ResolveExpressionTypesVisitor(SoyTypeRegistry typeRegistry, SyntaxVersion declaredSyntaxVersion) {
        this.typeOps = new SoyTypeOps(typeRegistry);
        this.declaredSyntaxVersion = declaredSyntaxVersion;
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        this.visitSoyNode(node);
    }

    @Override
    protected void visitPrintNode(PrintNode node) {
        this.visitSoyNode(node);
        ExprRootNode<?> expr = node.getExprUnion().getExpr();
        if (expr != null && expr.getType().equals(BoolType.getInstance())) {
            String errorMsg = "Bool values can no longer be printed";
            if (this.declaredSyntaxVersion.num >= SyntaxVersion.V2_3.num && expr.getChild(0) instanceof OperatorNodes.OrOpNode) {
                errorMsg = errorMsg + " (if you're intending the 'or' operator to return one of the operands instead of bool, please use the binary null-coalescing operator '?:' instead)";
            }
            errorMsg = errorMsg + ".";
            node.maybeSetSyntaxVersionBound(new SyntaxVersionBound(SyntaxVersion.V2_3, errorMsg));
        }
    }

    @Override
    protected void visitLetValueNode(LetValueNode node) {
        this.visitSoyNode(node);
        node.getVar().setType(node.getValueExpr().getType());
    }

    @Override
    protected void visitLetContentNode(LetContentNode node) {
        this.visitSoyNode(node);
        PrimitiveType varType = StringType.getInstance();
        if (node.getContentKind() != null) {
            switch (node.getContentKind()) {
                case ATTRIBUTES: {
                    varType = SanitizedType.AttributesType.getInstance();
                    break;
                }
                case CSS: {
                    varType = SanitizedType.CssType.getInstance();
                    break;
                }
                case HTML: {
                    varType = SanitizedType.HtmlType.getInstance();
                    break;
                }
                case JS: {
                    varType = SanitizedType.JsType.getInstance();
                    break;
                }
                case URI: {
                    varType = SanitizedType.UriType.getInstance();
                    break;
                }
            }
        }
        node.getVar().setType(varType);
    }

    @Override
    protected void visitForNode(ForNode node) {
        this.visitExpressions(node);
        node.getVar().setType(IntType.getInstance());
        this.visitChildren(node);
    }

    @Override
    protected void visitIfNode(IfNode node) {
        TypeSubstitution savedSubstitutionState = this.substitutions;
        for (SoyNode child : node.getChildren()) {
            if (child instanceof IfCondNode) {
                IfCondNode icn = (IfCondNode)child;
                this.visitExpressions(icn);
                TypeNarrowingConditionVisitor visitor = new TypeNarrowingConditionVisitor();
                if (icn.getExprUnion().getExpr() != null) {
                    visitor.exec(icn.getExprUnion().getExpr());
                }
                TypeSubstitution previousSubstitutionState = this.substitutions;
                this.addTypeSubstitutions(visitor.positiveTypeConstraints);
                this.visitChildren(icn);
                this.substitutions = previousSubstitutionState;
                this.addTypeSubstitutions(visitor.negativeTypeConstraints);
                continue;
            }
            if (!(child instanceof IfElseNode)) continue;
            IfElseNode ien = (IfElseNode)child;
            this.visitChildren(ien);
        }
        this.substitutions = savedSubstitutionState;
    }

    private void addTypeSubstitutions(Map<VarDefn, SoyType> substitutionsToAdd) {
        for (Map.Entry<VarDefn, SoyType> entry : substitutionsToAdd.entrySet()) {
            VarDefn defn = entry.getKey();
            SoyType previousType = defn.type();
            TypeSubstitution subst = this.substitutions;
            while (subst != null) {
                if (subst.defn == defn) {
                    previousType = subst.type;
                    break;
                }
                subst = subst.parent;
            }
            if (entry.getValue() == previousType) continue;
            this.substitutions = new TypeSubstitution(this.substitutions, entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void visitForeachNonemptyNode(ForeachNonemptyNode node) {
        this.visitExpressions(node.getParent());
        node.getVar().setType(this.getElementType(node.getExpr().getType(), node.getParent()));
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ExprHolderNode) {
            this.visitExpressions((SoyNode.ExprHolderNode)node);
        }
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    private void visitExpressions(SoyNode.ExprHolderNode node) {
        ResolveTypesExprVisitor exprVisitor = new ResolveTypesExprVisitor(node);
        for (ExprUnion exprUnion : node.getAllExprUnions()) {
            if (exprUnion.getExpr() == null) continue;
            exprVisitor.exec(exprUnion.getExpr());
        }
    }

    private SoyType getElementType(SoyType collectionType, SoyNode.ExprHolderNode owningNode) {
        Preconditions.checkNotNull((Object)collectionType);
        switch (collectionType.getKind()) {
            case UNKNOWN: {
                return UnknownType.getInstance();
            }
            case LIST: {
                return ((ListType)collectionType).getElementType();
            }
            case UNION: {
                UnionType unionType = (UnionType)collectionType;
                ArrayList fieldTypes = Lists.newArrayList();
                for (SoyType unionMember : unionType.getMembers()) {
                    fieldTypes.add(this.getElementType(unionMember, owningNode));
                }
                return this.typeOps.computeLeastCommonType(fieldTypes);
            }
        }
        throw SoySyntaxExceptionUtils.createWithNode("Cannot compute element type for collection of type '" + collectionType, owningNode);
    }

    private static class TypeSubstitution {
        public final TypeSubstitution parent;
        public final VarDefn defn;
        public final SoyType type;

        public TypeSubstitution(@Nullable TypeSubstitution parent, VarDefn var, SoyType type) {
            this.parent = parent;
            this.defn = var;
            this.type = type;
        }
    }

    private class TypeNarrowingConditionVisitor
    extends AbstractExprNodeVisitor<Void> {
        public Map<VarDefn, SoyType> positiveTypeConstraints = Maps.newHashMap();
        public Map<VarDefn, SoyType> negativeTypeConstraints = Maps.newHashMap();

        private TypeNarrowingConditionVisitor() {
        }

        @Override
        public Void exec(ExprNode node) {
            this.visit(node);
            return null;
        }

        @Override
        protected void visitExprRootNode(ExprRootNode<?> node) {
            this.visitAndImplicitlyCastToBoolean((ExprNode)node.getChild(0));
        }

        public void visitAndImplicitlyCastToBoolean(ExprNode node) {
            if (node.getKind() == ExprNode.Kind.VAR_REF_NODE) {
                VarRefNode varRef = (VarRefNode)node;
                this.positiveTypeConstraints.put(varRef.getDefnDecl(), this.removeNullability(varRef.getType()));
                this.negativeTypeConstraints.put(varRef.getDefnDecl(), NullType.getInstance());
            } else {
                this.visit(node);
            }
        }

        @Override
        protected void visitAndOpNode(OperatorNodes.AndOpNode node) {
            Preconditions.checkArgument((node.getChildren().size() == 2 ? 1 : 0) != 0);
            TypeNarrowingConditionVisitor leftVisitor = new TypeNarrowingConditionVisitor();
            TypeNarrowingConditionVisitor rightVisitor = new TypeNarrowingConditionVisitor();
            leftVisitor.visitAndImplicitlyCastToBoolean(node.getChild(0));
            rightVisitor.visitAndImplicitlyCastToBoolean(node.getChild(1));
            this.positiveTypeConstraints.putAll(this.computeUnion(leftVisitor.positiveTypeConstraints, rightVisitor.positiveTypeConstraints));
            this.negativeTypeConstraints.putAll(this.computeIntersection(leftVisitor.negativeTypeConstraints, rightVisitor.negativeTypeConstraints));
        }

        @Override
        protected void visitOrOpNode(OperatorNodes.OrOpNode node) {
            Preconditions.checkArgument((node.getChildren().size() == 2 ? 1 : 0) != 0);
            TypeNarrowingConditionVisitor leftVisitor = new TypeNarrowingConditionVisitor();
            TypeNarrowingConditionVisitor rightVisitor = new TypeNarrowingConditionVisitor();
            leftVisitor.visitAndImplicitlyCastToBoolean(node.getChild(0));
            rightVisitor.visitAndImplicitlyCastToBoolean(node.getChild(1));
            this.positiveTypeConstraints.putAll(this.computeIntersection(leftVisitor.positiveTypeConstraints, rightVisitor.positiveTypeConstraints));
            this.negativeTypeConstraints.putAll(this.computeUnion(leftVisitor.negativeTypeConstraints, rightVisitor.negativeTypeConstraints));
        }

        @Override
        protected void visitNotOpNode(OperatorNodes.NotOpNode node) {
            TypeNarrowingConditionVisitor childVisitor = new TypeNarrowingConditionVisitor();
            childVisitor.visitAndImplicitlyCastToBoolean(node.getChild(0));
            this.positiveTypeConstraints.putAll(childVisitor.negativeTypeConstraints);
            this.negativeTypeConstraints.putAll(childVisitor.positiveTypeConstraints);
        }

        @Override
        protected void visitEqualOpNode(OperatorNodes.EqualOpNode node) {
            if (node.getChild(0).getKind() == ExprNode.Kind.VAR_REF_NODE) {
                if (node.getChild(1).getKind() == ExprNode.Kind.NULL_NODE) {
                    VarRefNode varRef = (VarRefNode)node.getChild(0);
                    this.positiveTypeConstraints.put(varRef.getDefnDecl(), NullType.getInstance());
                    this.negativeTypeConstraints.put(varRef.getDefnDecl(), this.removeNullability(varRef.getType()));
                }
            } else if (node.getChild(1).getKind() == ExprNode.Kind.VAR_REF_NODE && node.getChild(0).getKind() == ExprNode.Kind.NULL_NODE) {
                VarRefNode varRef = (VarRefNode)node.getChild(1);
                this.positiveTypeConstraints.put(varRef.getDefnDecl(), NullType.getInstance());
                this.negativeTypeConstraints.put(varRef.getDefnDecl(), this.removeNullability(varRef.getType()));
            }
        }

        @Override
        protected void visitNotEqualOpNode(OperatorNodes.NotEqualOpNode node) {
            if (node.getChild(0).getKind() == ExprNode.Kind.VAR_REF_NODE) {
                if (node.getChild(1).getKind() == ExprNode.Kind.NULL_NODE) {
                    VarRefNode varRef = (VarRefNode)node.getChild(0);
                    this.positiveTypeConstraints.put(varRef.getDefnDecl(), this.removeNullability(varRef.getType()));
                    this.negativeTypeConstraints.put(varRef.getDefnDecl(), NullType.getInstance());
                }
            } else if (node.getChild(1).getKind() == ExprNode.Kind.VAR_REF_NODE && node.getChild(0).getKind() == ExprNode.Kind.NULL_NODE) {
                VarRefNode varRef = (VarRefNode)node.getChild(1);
                this.positiveTypeConstraints.put(varRef.getDefnDecl(), this.removeNullability(varRef.getType()));
                this.negativeTypeConstraints.put(varRef.getDefnDecl(), NullType.getInstance());
            }
        }

        @Override
        protected void visitNullCoalescingOpNode(OperatorNodes.NullCoalescingOpNode node) {
        }

        @Override
        protected void visitConditionalOpNode(OperatorNodes.ConditionalOpNode node) {
        }

        @Override
        protected void visitFunctionNode(FunctionNode node) {
            ExprNode argNode;
            if (node.numChildren() == 1 && node.getFunctionName().equals("isNonnull") && (argNode = node.getChild(0)).getKind() == ExprNode.Kind.VAR_REF_NODE) {
                VarRefNode varRef = (VarRefNode)argNode;
                this.positiveTypeConstraints.put(varRef.getDefnDecl(), this.removeNullability(varRef.getType()));
                this.negativeTypeConstraints.put(varRef.getDefnDecl(), NullType.getInstance());
            }
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }

        private Map<VarDefn, SoyType> computeUnion(Map<VarDefn, SoyType> left, Map<VarDefn, SoyType> right) {
            if (left.isEmpty()) {
                return right;
            }
            if (right.isEmpty()) {
                return left;
            }
            HashMap result = Maps.newHashMap(left);
            for (Map.Entry<VarDefn, SoyType> entry : right.entrySet()) {
                if (left.containsKey(entry.getKey())) continue;
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }

        private Map<VarDefn, SoyType> computeIntersection(Map<VarDefn, SoyType> left, Map<VarDefn, SoyType> right) {
            if (left.isEmpty()) {
                return left;
            }
            if (right.isEmpty()) {
                return right;
            }
            HashMap result = Maps.newHashMap();
            for (Map.Entry<VarDefn, SoyType> entry : left.entrySet()) {
                if (!right.containsKey(entry.getKey())) continue;
                SoyType rightSideType = right.get(entry.getKey());
                result.put(entry.getKey(), ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonType(entry.getValue(), rightSideType));
            }
            return result;
        }

        private SoyType removeNullability(SoyType type) {
            if (type.getKind() == SoyType.Kind.UNION) {
                Set nonNullMemberTypes = Sets.filter(((UnionType)type).getMembers(), (Predicate)new Predicate<SoyType>(){

                    public boolean apply(@Nullable SoyType memberType) {
                        return memberType.getKind() != SoyType.Kind.NULL;
                    }
                });
                if (nonNullMemberTypes.size() == 1) {
                    return (SoyType)nonNullMemberTypes.iterator().next();
                }
                return ResolveExpressionTypesVisitor.this.typeOps.getTypeRegistry().getOrCreateUnionType(nonNullMemberTypes);
            }
            return type;
        }
    }

    private class ResolveTypesExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private final SoyNode.ExprHolderNode owningSoyNode;
        private ExprRootNode<?> currExprRootNode;

        public ResolveTypesExprVisitor(SoyNode.ExprHolderNode owningSoyNode) {
            this.owningSoyNode = owningSoyNode;
        }

        @Override
        public Void exec(ExprNode node) {
            Preconditions.checkArgument((boolean)(node instanceof ExprRootNode));
            this.currExprRootNode = (ExprRootNode)node;
            this.visit(node);
            this.currExprRootNode = null;
            return null;
        }

        @Override
        protected void visit(ExprNode node) {
            super.visit(node);
            this.requireNodeType(node);
        }

        @Override
        protected void visitExprRootNode(ExprRootNode<?> node) {
            this.visitChildren(node);
            Node expr = node.getChild(0);
            node.setType(expr.getType());
        }

        @Override
        protected void visitPrimitiveNode(ExprNode.PrimitiveNode node) {
        }

        @Override
        protected void visitListLiteralNode(ListLiteralNode node) {
            this.visitChildren(node);
            ArrayList elementTypes = Lists.newArrayList();
            for (ExprNode child : node.getChildren()) {
                this.requireNodeType(child);
                elementTypes.add(child.getType());
            }
            if (elementTypes.isEmpty()) {
                elementTypes.add(UnknownType.getInstance());
            }
            node.setType(ResolveExpressionTypesVisitor.this.typeOps.getTypeRegistry().getOrCreateListType(ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonType(elementTypes)));
        }

        @Override
        protected void visitMapLiteralNode(MapLiteralNode node) {
            SoyType commonValueType;
            SoyType commonKeyType;
            this.visitChildren(node);
            int numChildren = node.numChildren();
            if (numChildren % 2 != 0) {
                throw new AssertionError();
            }
            HashMultimap recordFieldTypes = HashMultimap.create();
            if (numChildren == 0) {
                commonKeyType = UnknownType.getInstance();
                commonValueType = UnknownType.getInstance();
            } else {
                ArrayList keyTypes = Lists.newArrayListWithCapacity((int)(numChildren / 2));
                ArrayList valueTypes = Lists.newArrayListWithCapacity((int)(numChildren / 2));
                for (int i = 0; i < numChildren; i += 2) {
                    ExprNode key = node.getChild(i);
                    ExprNode value = node.getChild(i + 1);
                    if (key.getKind() == ExprNode.Kind.STRING_NODE) {
                        String fieldName = ((StringNode)key).getValue();
                        recordFieldTypes.put((Object)fieldName, (Object)value.getType());
                    }
                    keyTypes.add(key.getType());
                    valueTypes.add(value.getType());
                }
                commonKeyType = ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonType(keyTypes);
                commonValueType = ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonType(valueTypes);
            }
            if (StringType.getInstance().isAssignableFrom(commonKeyType)) {
                HashMap leastCommonFieldTypes = Maps.newHashMap();
                for (String fieldName : recordFieldTypes.keySet()) {
                    leastCommonFieldTypes.put(fieldName, ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonType(recordFieldTypes.get((Object)fieldName)));
                }
                node.setType(ResolveExpressionTypesVisitor.this.typeOps.getTypeRegistry().getOrCreateRecordType(leastCommonFieldTypes));
            } else {
                node.setType(ResolveExpressionTypesVisitor.this.typeOps.getTypeRegistry().getOrCreateMapType(commonKeyType, commonValueType));
            }
        }

        @Override
        protected void visitVarRefNode(VarRefNode varRef) {
            if (varRef.getType() == null) {
                throw this.createExceptionForInvalidExpr("Missing Soy type for variable: " + varRef.getName());
            }
            TypeSubstitution subst = ResolveExpressionTypesVisitor.this.substitutions;
            while (subst != null) {
                if (subst.defn == varRef.getDefnDecl()) {
                    varRef.setSubstituteType(subst.type);
                    break;
                }
                subst = subst.parent;
            }
        }

        @Override
        protected void visitFieldAccessNode(FieldAccessNode node) {
            this.visit(node.getBaseExprChild());
            node.setType(this.getFieldType(node.getBaseExprChild().getType(), node.getFieldName(), node.isNullSafe()));
        }

        @Override
        protected void visitItemAccessNode(ItemAccessNode node) {
            this.visit(node.getBaseExprChild());
            this.visit(node.getKeyExprChild());
            node.setType(this.getItemType(node.getBaseExprChild().getType(), node.getKeyExprChild().getType()));
        }

        @Override
        protected void visitGlobalNode(GlobalNode node) {
        }

        @Override
        protected void visitNegativeOpNode(OperatorNodes.NegativeOpNode node) {
            this.visitChildren(node);
            node.setType(node.getChild(0).getType());
        }

        @Override
        protected void visitNotOpNode(OperatorNodes.NotOpNode node) {
            this.visitChildren(node);
            node.setType(BoolType.getInstance());
        }

        @Override
        protected void visitTimesOpNode(OperatorNodes.TimesOpNode node) {
            this.visitArithmeticOpNode(node);
        }

        @Override
        protected void visitDivideByOpNode(OperatorNodes.DivideByOpNode node) {
            this.visitArithmeticOpNode(node);
        }

        @Override
        protected void visitModOpNode(OperatorNodes.ModOpNode node) {
            this.visitArithmeticOpNode(node);
        }

        @Override
        protected void visitPlusOpNode(OperatorNodes.PlusOpNode node) {
            this.visitArithmeticOpNode(node);
        }

        @Override
        protected void visitMinusOpNode(OperatorNodes.MinusOpNode node) {
            this.visitArithmeticOpNode(node);
        }

        @Override
        protected void visitLessThanOpNode(OperatorNodes.LessThanOpNode node) {
            this.visitComparisonOpNode(node);
        }

        @Override
        protected void visitGreaterThanOpNode(OperatorNodes.GreaterThanOpNode node) {
            this.visitComparisonOpNode(node);
        }

        @Override
        protected void visitLessThanOrEqualOpNode(OperatorNodes.LessThanOrEqualOpNode node) {
            this.visitComparisonOpNode(node);
        }

        @Override
        protected void visitGreaterThanOrEqualOpNode(OperatorNodes.GreaterThanOrEqualOpNode node) {
            this.visitComparisonOpNode(node);
        }

        @Override
        protected void visitEqualOpNode(OperatorNodes.EqualOpNode node) {
            this.visitComparisonOpNode(node);
        }

        @Override
        protected void visitNotEqualOpNode(OperatorNodes.NotEqualOpNode node) {
            this.visitComparisonOpNode(node);
        }

        @Override
        protected void visitAndOpNode(OperatorNodes.AndOpNode node) {
            this.visitLogicalOpNode(node);
        }

        @Override
        protected void visitOrOpNode(OperatorNodes.OrOpNode node) {
            this.visitLogicalOpNode(node);
        }

        @Override
        protected void visitNullCoalescingOpNode(OperatorNodes.NullCoalescingOpNode node) {
            this.visit(node.getChild(0));
            TypeSubstitution savedSubstitutionState = ResolveExpressionTypesVisitor.this.substitutions;
            TypeNarrowingConditionVisitor visitor = new TypeNarrowingConditionVisitor();
            visitor.visitAndImplicitlyCastToBoolean(node.getChild(0));
            ResolveExpressionTypesVisitor.this.addTypeSubstitutions(visitor.positiveTypeConstraints);
            this.visit(node.getChild(0));
            ResolveExpressionTypesVisitor.this.addTypeSubstitutions(visitor.negativeTypeConstraints);
            this.visit(node.getChild(1));
            ResolveExpressionTypesVisitor.this.substitutions = savedSubstitutionState;
            node.setType(ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonType(node.getChild(0).getType(), node.getChild(1).getType()));
        }

        @Override
        protected void visitConditionalOpNode(OperatorNodes.ConditionalOpNode node) {
            this.visit(node.getChild(0));
            TypeSubstitution savedSubstitutionState = ResolveExpressionTypesVisitor.this.substitutions;
            TypeNarrowingConditionVisitor visitor = new TypeNarrowingConditionVisitor();
            visitor.visitAndImplicitlyCastToBoolean(node.getChild(0));
            ResolveExpressionTypesVisitor.this.addTypeSubstitutions(visitor.positiveTypeConstraints);
            this.visit(node.getChild(1));
            ResolveExpressionTypesVisitor.this.substitutions = savedSubstitutionState;
            ResolveExpressionTypesVisitor.this.addTypeSubstitutions(visitor.negativeTypeConstraints);
            this.visit(node.getChild(2));
            ResolveExpressionTypesVisitor.this.substitutions = savedSubstitutionState;
            node.setType(ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonType(node.getChild(1).getType(), node.getChild(2).getType()));
        }

        @Override
        protected void visitFunctionNode(FunctionNode node) {
            this.visitChildren(node);
            node.setType(UnknownType.getInstance());
        }

        private void visitLogicalOpNode(AbstractOperatorNode node) {
            this.visitChildren(node);
            if (((ResolveExpressionTypesVisitor)ResolveExpressionTypesVisitor.this).declaredSyntaxVersion.num >= SyntaxVersion.V2_3.num) {
                node.setType(BoolType.getInstance());
            } else {
                node.setType(UnknownType.getInstance());
            }
        }

        private void visitComparisonOpNode(AbstractOperatorNode node) {
            this.visitChildren(node);
            node.setType(BoolType.getInstance());
        }

        private void visitArithmeticOpNode(AbstractOperatorNode node) {
            this.visitChildren(node);
            node.setType(ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonTypeArithmetic(node.getChild(0).getType(), node.getChild(1).getType()));
        }

        private void requireNodeType(ExprNode node) {
            if (node.getType() == null) {
                throw this.createExceptionForInvalidExpr("Missing Soy type for node: " + node.getClass().getName());
            }
        }

        private SoyType getFieldType(SoyType baseType, String fieldName, boolean isNullSafe) {
            Preconditions.checkNotNull((Object)baseType);
            switch (baseType.getKind()) {
                case UNKNOWN: {
                    return UnknownType.getInstance();
                }
                case OBJECT: {
                    SoyType fieldType = ((SoyObjectType)baseType).getFieldType(fieldName);
                    if (fieldType == null) {
                        throw this.createExceptionForInvalidExpr("Undefined field '" + fieldName + "' for object type " + baseType);
                    }
                    return fieldType;
                }
                case LIST: {
                    if (fieldName.equals("length")) {
                        this.currExprRootNode.maybeSetSyntaxVersionBound(new SyntaxVersionBound(SyntaxVersion.V2_3, "Soy lists do not have field 'length'. Use function length() instead."));
                        return IntType.getInstance();
                    }
                    throw this.createExceptionForInvalidExpr("Undefined field '" + fieldName + "' in type: " + baseType);
                }
                case RECORD: {
                    SoyType fieldType = ((SoyObjectType)baseType).getFieldType(fieldName);
                    if (fieldType == null) {
                        throw this.createExceptionForInvalidExpr("Undefined field '" + fieldName + "' for record type " + baseType);
                    }
                    return fieldType;
                }
                case MAP: {
                    throw this.createExceptionForInvalidExpr("Dot-access not supported for type " + baseType + " (consider dict instead of map)");
                }
                case UNION: {
                    UnionType unionType = (UnionType)baseType;
                    ArrayList fieldTypes = Lists.newArrayList();
                    for (SoyType unionMember : unionType.getMembers()) {
                        if (unionMember.getKind() == SoyType.Kind.NULL) continue;
                        fieldTypes.add(this.getFieldType(unionMember, fieldName, isNullSafe));
                    }
                    return ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonType(fieldTypes);
                }
            }
            throw this.createExceptionForInvalidExpr("Dot-access not supported for type " + baseType + ".");
        }

        private SoyType getItemType(SoyType baseType, SoyType keyType) {
            Preconditions.checkNotNull((Object)baseType);
            Preconditions.checkNotNull((Object)keyType);
            switch (baseType.getKind()) {
                case UNKNOWN: {
                    return UnknownType.getInstance();
                }
                case LIST: {
                    ListType listType = (ListType)baseType;
                    if (keyType.getKind() != SoyType.Kind.UNKNOWN && !IntType.getInstance().isAssignableFrom(keyType)) {
                        throw this.createExceptionForInvalidExpr("Invalid index type " + keyType + " for list of type " + baseType);
                    }
                    return listType.getElementType();
                }
                case MAP: {
                    MapType mapType = (MapType)baseType;
                    if (keyType.getKind() != SoyType.Kind.UNKNOWN && !mapType.getKeyType().isAssignableFrom(keyType)) {
                        throw this.createExceptionForInvalidExpr("Invalid key type " + keyType + " for map of type " + baseType);
                    }
                    return mapType.getValueType();
                }
                case UNION: {
                    UnionType unionType = (UnionType)baseType;
                    ArrayList itemTypes = Lists.newArrayList();
                    for (SoyType unionMember : unionType.getMembers()) {
                        itemTypes.add(this.getItemType(unionMember, keyType));
                    }
                    return ResolveExpressionTypesVisitor.this.typeOps.computeLeastCommonType(itemTypes);
                }
            }
            throw this.createExceptionForInvalidExpr("Type " + baseType + " does not support bracket-access.");
        }

        private SoySyntaxException createExceptionForInvalidExpr(String errorMsg) {
            return SoySyntaxExceptionUtils.createWithNode("Invalid expression \"" + this.currExprRootNode.toSourceString() + "\": " + errorMsg, this.owningSoyNode);
        }
    }
}

