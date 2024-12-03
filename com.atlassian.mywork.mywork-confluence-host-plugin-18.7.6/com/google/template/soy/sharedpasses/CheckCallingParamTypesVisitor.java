/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.sharedpasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.sharedpasses.FindIndirectParamsVisitor;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.CallParamValueNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.soytree.defn.HeaderParam;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.aggregate.UnionType;
import com.google.template.soy.types.primitive.NullType;
import com.google.template.soy.types.primitive.SanitizedType;
import com.google.template.soy.types.proto.SoyProtoType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CheckCallingParamTypesVisitor
extends AbstractSoyNodeVisitor<Void> {
    private TemplateRegistry templateRegistry;
    private TemplateNode template;
    private final Map<TemplateNode, TemplateParamTypes> paramTypesMap = Maps.newHashMap();

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        this.templateRegistry = new TemplateRegistry(node);
        this.visitChildren(node);
        this.templateRegistry = null;
    }

    @Override
    protected void visitCallNode(CallNode node) {
        if (node instanceof CallBasicNode) {
            TemplateBasicNode callee = this.templateRegistry.getBasicTemplate(((CallBasicNode)node).getCalleeName());
            if (callee != null) {
                this.checkCallParamTypes(node, callee);
            }
        } else {
            Set<TemplateRegistry.DelegateTemplateDivision> divisions = this.templateRegistry.getDelTemplateDivisionsForAllVariants(((CallDelegateNode)node).getDelCalleeName());
            if (divisions != null) {
                for (TemplateRegistry.DelegateTemplateDivision division : divisions) {
                    for (TemplateDelegateNode delTemplate : division.delPackageNameToDelTemplateMap.values()) {
                        this.checkCallParamTypes(node, delTemplate);
                    }
                }
            }
        }
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        this.template = node;
        this.visitChildren(node);
        this.template = null;
    }

    private void checkCallParamTypes(CallNode call, TemplateNode callee) {
        Collection declaredParamTypes;
        TemplateParamTypes calleeParamTypes = this.getTemplateParamTypes(callee);
        HashSet explicitParams = Sets.newHashSet();
        for (CallParamNode callParamNode : call.getChildren()) {
            SoyType argType = null;
            if (callParamNode.getKind() == SoyNode.Kind.CALL_PARAM_VALUE_NODE) {
                ExprRootNode<?> expr = ((CallParamValueNode)callParamNode).getValueExprUnion().getExpr();
                if (expr != null) {
                    argType = expr.getType();
                }
            } else if (callParamNode.getKind() == SoyNode.Kind.CALL_PARAM_CONTENT_NODE) {
                argType = SanitizedType.getTypeForContentKind(((CallParamContentNode)callParamNode).getContentKind());
            }
            if (argType == null) continue;
            declaredParamTypes = calleeParamTypes.params.get((Object)callParamNode.getKey());
            for (SoyType formalType : declaredParamTypes) {
                this.checkArgumentAgainstParamType(call, callParamNode.getKey(), argType, formalType, calleeParamTypes.isIndirect(callParamNode.getKey()));
            }
            explicitParams.add(callParamNode.getKey());
        }
        if (call.isPassingData() && call.isPassingAllData() && this.template.getParams() != null) {
            for (TemplateParam templateParam : this.template.getParams()) {
                String paramName;
                if (!(templateParam instanceof HeaderParam) || explicitParams.contains(paramName = templateParam.name())) continue;
                declaredParamTypes = calleeParamTypes.params.get((Object)paramName);
                for (SoyType formalType : declaredParamTypes) {
                    this.checkArgumentAgainstParamType(call, paramName, templateParam.type(), formalType, calleeParamTypes.isIndirect(paramName));
                }
            }
        }
    }

    private void checkArgumentAgainstParamType(CallNode call, String paramName, SoyType argType, SoyType formalType, boolean isIndirect) {
        if (formalType.getKind() == SoyType.Kind.UNKNOWN || formalType.getKind() == SoyType.Kind.ANY) {
            if (argType instanceof SoyProtoType) {
                this.reportProtoArgumentTypeMismatch(call, paramName, formalType, argType);
            }
        } else if (argType.getKind() != SoyType.Kind.UNKNOWN && !formalType.isAssignableFrom(argType)) {
            if (isIndirect && argType.getKind() == SoyType.Kind.UNION && ((UnionType)argType).isNullable() && UnionType.of(formalType, NullType.getInstance()).isAssignableFrom(argType)) {
                return;
            }
            this.reportArgumentTypeMismatch(call, paramName, formalType, argType);
        }
    }

    private void reportArgumentTypeMismatch(SoyNode node, String paramName, SoyType paramType, SoyType argType) {
        throw SoySyntaxExceptionUtils.createWithNode("Argument type mismatch: cannot call template parameter '" + paramName + "' with type '" + paramType + "' with value of type '" + argType + "'", node);
    }

    private void reportProtoArgumentTypeMismatch(SoyNode node, String paramName, SoyType paramType, SoyType argType) {
        throw SoySyntaxExceptionUtils.createWithNode("Argument type mismatch: cannot mix protobuf / non-protobuf types when calling template parameter '" + paramName + "' with type '" + paramType + "' with value of type '" + argType + "'", node);
    }

    private TemplateParamTypes getTemplateParamTypes(TemplateNode node) {
        TemplateParamTypes paramTypes = this.paramTypesMap.get(node);
        if (paramTypes == null) {
            paramTypes = new TemplateParamTypes();
            if (node.getParams() != null) {
                for (TemplateParam param : node.getParams()) {
                    Preconditions.checkNotNull((Object)param.type());
                    paramTypes.params.put((Object)param.name(), (Object)param.type());
                }
            }
            FindIndirectParamsVisitor.IndirectParamsInfo ipi = new FindIndirectParamsVisitor(this.templateRegistry).exec(node);
            for (String indirectParamName : ipi.indirectParamTypes.keySet()) {
                if (paramTypes.params.containsKey((Object)indirectParamName)) continue;
                paramTypes.params.putAll((Object)indirectParamName, (Iterable)ipi.indirectParamTypes.get((Object)indirectParamName));
                paramTypes.indirectParamNames.add(indirectParamName);
            }
            this.paramTypesMap.put(node, paramTypes);
        }
        return paramTypes;
    }

    private static class TemplateParamTypes {
        public final Multimap<String, SoyType> params = HashMultimap.create();
        public final Set<String> indirectParamNames = Sets.newHashSet();

        private TemplateParamTypes() {
        }

        public boolean isIndirect(String paramName) {
            return this.indirectParamNames.contains(paramName);
        }
    }
}

