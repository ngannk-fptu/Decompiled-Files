/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.assistedinject.Assisted
 *  com.google.inject.assistedinject.AssistedInject
 */
package com.google.template.soy.jssrc.internal;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.template.soy.base.SoyBackendKind;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.exprtree.AbstractReturningExprNodeVisitor;
import com.google.template.soy.exprtree.DataAccessNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FieldAccessNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.ItemAccessNode;
import com.google.template.soy.exprtree.ListLiteralNode;
import com.google.template.soy.exprtree.MapLiteralNode;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.exprtree.OperatorNodes;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.internal.JsSrcUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsCodeUtils;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.internal.NonpluginFunction;
import com.google.template.soy.types.SoyObjectType;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.aggregate.UnionType;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class TranslateToJsExprVisitor
extends AbstractReturningExprNodeVisitor<JsExpr> {
    private final Map<String, SoyJsSrcFunction> soyJsSrcFunctionsMap;
    private final SoyJsSrcOptions jsSrcOptions;
    private final Deque<Map<String, JsExpr>> localVarTranslations;

    @AssistedInject
    TranslateToJsExprVisitor(Map<String, SoyJsSrcFunction> soyJsSrcFunctionsMap, SoyJsSrcOptions jsSrcOptions, @Assisted Deque<Map<String, JsExpr>> localVarTranslations) {
        this.soyJsSrcFunctionsMap = soyJsSrcFunctionsMap;
        this.jsSrcOptions = jsSrcOptions;
        this.localVarTranslations = localVarTranslations;
    }

    static String genCodeForParamAccess(String paramName) {
        return "opt_data" + TranslateToJsExprVisitor.genCodeForKeyAccess(paramName);
    }

    @Override
    protected JsExpr visitExprRootNode(ExprRootNode<?> node) {
        return (JsExpr)this.visit((ExprNode)node.getChild(0));
    }

    @Override
    protected JsExpr visitStringNode(StringNode node) {
        return new JsExpr(BaseUtils.escapeToSoyString(node.getValue(), true), Integer.MAX_VALUE);
    }

    @Override
    protected JsExpr visitPrimitiveNode(ExprNode.PrimitiveNode node) {
        return new JsExpr(node.toSourceString(), Integer.MAX_VALUE);
    }

    @Override
    protected JsExpr visitListLiteralNode(ListLiteralNode node) {
        StringBuilder exprTextSb = new StringBuilder();
        exprTextSb.append('[');
        boolean isFirst = true;
        for (ExprNode child : node.getChildren()) {
            if (isFirst) {
                isFirst = false;
            } else {
                exprTextSb.append(", ");
            }
            exprTextSb.append(((JsExpr)this.visit(child)).getText());
        }
        exprTextSb.append(']');
        return new JsExpr(exprTextSb.toString(), Integer.MAX_VALUE);
    }

    @Override
    protected JsExpr visitMapLiteralNode(MapLiteralNode node) {
        return this.visitMapLiteralNodeHelper(node, false);
    }

    private JsExpr visitMapLiteralNodeHelper(MapLiteralNode node, boolean doQuoteKeys) {
        StringBuilder strKeysEntriesSnippet = new StringBuilder();
        StringBuilder nonstrKeysEntriesSnippet = new StringBuilder();
        boolean isProbablyUsingClosureCompiler = this.jsSrcOptions.shouldGenerateJsdoc() || this.jsSrcOptions.shouldProvideRequireSoyNamespaces() || this.jsSrcOptions.shouldProvideRequireJsFunctions();
        int n = node.numChildren();
        for (int i = 0; i < n; i += 2) {
            ExprNode keyNode = node.getChild(i);
            ExprNode valueNode = node.getChild(i + 1);
            if (keyNode instanceof StringNode) {
                if (strKeysEntriesSnippet.length() > 0) {
                    strKeysEntriesSnippet.append(", ");
                }
                if (doQuoteKeys) {
                    strKeysEntriesSnippet.append(((JsExpr)this.visit(keyNode)).getText());
                } else {
                    String key = ((StringNode)keyNode).getValue();
                    if (BaseUtils.isIdentifier(key)) {
                        strKeysEntriesSnippet.append(key);
                    } else {
                        if (isProbablyUsingClosureCompiler) {
                            throw SoySyntaxException.createWithoutMetaInfo("Map literal with non-identifier key must be wrapped in quoteKeysIfJs() (found non-identifier key \"" + keyNode.toSourceString() + "\" in map literal \"" + node.toSourceString() + "\").");
                        }
                        strKeysEntriesSnippet.append(((JsExpr)this.visit(keyNode)).getText());
                    }
                }
                strKeysEntriesSnippet.append(": ").append(((JsExpr)this.visit(valueNode)).getText());
                continue;
            }
            if (keyNode instanceof ExprNode.ConstantNode) {
                throw SoySyntaxException.createWithoutMetaInfo("Map literal must have keys that are strings or expressions that will evaluate to strings at render time (found non-string key \"" + keyNode.toSourceString() + "\" in map literal \"" + node.toSourceString() + "\").");
            }
            if (isProbablyUsingClosureCompiler && !doQuoteKeys) {
                throw SoySyntaxException.createWithoutMetaInfo("Map literal with expression key must be wrapped in quoteKeysIfJs() (found expression key \"" + keyNode.toSourceString() + "\" in map literal \"" + node.toSourceString() + "\").");
            }
            nonstrKeysEntriesSnippet.append(" map_s[soy.$$checkMapKey(").append(((JsExpr)this.visit(keyNode)).getText()).append(")] = ").append(((JsExpr)this.visit(valueNode)).getText()).append(';');
        }
        String fullExprText = nonstrKeysEntriesSnippet.length() == 0 ? "{" + strKeysEntriesSnippet.toString() + "}" : "(function() { var map_s = {" + strKeysEntriesSnippet.toString() + "};" + nonstrKeysEntriesSnippet.toString() + " return map_s; })()";
        return new JsExpr(fullExprText, Integer.MAX_VALUE);
    }

    @Override
    protected JsExpr visitVarRefNode(VarRefNode node) {
        return this.visitNullSafeNode(node);
    }

    @Override
    protected JsExpr visitDataAccessNode(DataAccessNode node) {
        return this.visitNullSafeNode(node);
    }

    private JsExpr visitNullSafeNode(ExprNode node) {
        StringBuilder nullSafetyPrefix = new StringBuilder();
        String refText = this.visitNullSafeNodeRecurse(node, nullSafetyPrefix);
        if (nullSafetyPrefix.length() == 0) {
            return new JsExpr(refText, Integer.MAX_VALUE);
        }
        return new JsExpr(nullSafetyPrefix.toString() + refText, Operator.CONDITIONAL.getPrecedence());
    }

    private String visitNullSafeNodeRecurse(ExprNode node, StringBuilder nullSafetyPrefix) {
        switch (node.getKind()) {
            case VAR_REF_NODE: {
                VarRefNode varRef = (VarRefNode)node;
                if (varRef.isInjected()) {
                    if (varRef.isNullSafeInjected()) {
                        nullSafetyPrefix.append("(opt_ijData == null) ? null : ");
                    }
                    return "opt_ijData" + TranslateToJsExprVisitor.genCodeForKeyAccess(varRef.getName());
                }
                JsExpr translation = this.getLocalVarTranslation(varRef.getName());
                if (translation != null) {
                    return translation.getText();
                }
                return "opt_data" + TranslateToJsExprVisitor.genCodeForKeyAccess(varRef.getName());
            }
            case FIELD_ACCESS_NODE: 
            case ITEM_ACCESS_NODE: {
                DataAccessNode dataAccess = (DataAccessNode)node;
                String refText = this.visitNullSafeNodeRecurse(dataAccess.getBaseExprChild(), nullSafetyPrefix);
                if (dataAccess.isNullSafe()) {
                    nullSafetyPrefix.append("(" + refText + " == null) ? null : ");
                }
                if (node.getKind() == ExprNode.Kind.FIELD_ACCESS_NODE) {
                    FieldAccessNode fieldAccess = (FieldAccessNode)node;
                    return refText + TranslateToJsExprVisitor.genCodeForFieldAccess(fieldAccess.getBaseExprChild().getType(), fieldAccess.getFieldName());
                }
                ItemAccessNode itemAccess = (ItemAccessNode)node;
                if (itemAccess.getKeyExprChild() instanceof IntegerNode) {
                    return refText + "[" + ((IntegerNode)itemAccess.getKeyExprChild()).getValue() + "]";
                }
                JsExpr keyJsExpr = (JsExpr)this.visit(itemAccess.getKeyExprChild());
                return refText + "[" + keyJsExpr.getText() + "]";
            }
        }
        JsExpr value = (JsExpr)this.visit(node);
        return TranslateToJsExprVisitor.genMaybeProtect(value, Integer.MAX_VALUE);
    }

    static String genCodeForKeyAccess(String key) {
        return JsSrcUtils.isReservedWord(key) ? "['" + key + "']" : "." + key;
    }

    private static String genCodeForFieldAccess(SoyType baseType, String fieldName) {
        if (baseType != null) {
            SoyObjectType objType;
            String accessExpr;
            if (baseType.getKind() == SoyType.Kind.UNION) {
                UnionType unionType = (UnionType)baseType;
                String fieldAccessCode = null;
                for (SoyType memberType : unionType.getMembers()) {
                    if (memberType.getKind() == SoyType.Kind.NULL) continue;
                    String fieldAccessForType = TranslateToJsExprVisitor.genCodeForFieldAccess(memberType, fieldName);
                    if (fieldAccessCode == null) {
                        fieldAccessCode = fieldAccessForType;
                        continue;
                    }
                    if (fieldAccessCode.equals(fieldAccessForType)) continue;
                    throw SoySyntaxException.createWithoutMetaInfo("Cannot access field '" + fieldName + "' of type'" + baseType.toString() + ", because the different union member types have different access methods.");
                }
                return fieldAccessCode;
            }
            if (baseType.getKind() == SoyType.Kind.OBJECT && (accessExpr = (objType = (SoyObjectType)baseType).getFieldAccessor(fieldName, SoyBackendKind.JS_SRC)) != null) {
                return accessExpr;
            }
        }
        return TranslateToJsExprVisitor.genCodeForKeyAccess(fieldName);
    }

    @Override
    protected JsExpr visitGlobalNode(GlobalNode node) {
        return new JsExpr(node.toSourceString(), Integer.MAX_VALUE);
    }

    @Override
    protected JsExpr visitNotOpNode(OperatorNodes.NotOpNode node) {
        return this.genJsExprUsingSoySyntaxWithNewToken(node, "!");
    }

    @Override
    protected JsExpr visitAndOpNode(OperatorNodes.AndOpNode node) {
        return this.genJsExprUsingSoySyntaxWithNewToken(node, "&&");
    }

    @Override
    protected JsExpr visitOrOpNode(OperatorNodes.OrOpNode node) {
        return this.genJsExprUsingSoySyntaxWithNewToken(node, "||");
    }

    @Override
    protected JsExpr visitOperatorNode(ExprNode.OperatorNode node) {
        return this.genJsExprUsingSoySyntax(node);
    }

    @Override
    protected JsExpr visitFunctionNode(FunctionNode node) {
        String fnName = node.getFunctionName();
        int numArgs = node.numChildren();
        NonpluginFunction nonpluginFn = NonpluginFunction.forFunctionName(fnName);
        if (nonpluginFn != null) {
            if (numArgs != nonpluginFn.getNumArgs()) {
                throw SoySyntaxException.createWithoutMetaInfo("Function '" + fnName + "' called with the wrong number of arguments (function call \"" + node.toSourceString() + "\").");
            }
            switch (nonpluginFn) {
                case IS_FIRST: {
                    return this.visitIsFirstFunction(node);
                }
                case IS_LAST: {
                    return this.visitIsLastFunction(node);
                }
                case INDEX: {
                    return this.visitIndexFunction(node);
                }
                case QUOTE_KEYS_IF_JS: {
                    return this.visitMapLiteralNodeHelper((MapLiteralNode)node.getChild(0), true);
                }
            }
            throw new AssertionError();
        }
        SoyJsSrcFunction fn = this.soyJsSrcFunctionsMap.get(fnName);
        if (fn != null) {
            if (!fn.getValidArgsSizes().contains(numArgs)) {
                throw SoySyntaxException.createWithoutMetaInfo("Function '" + fnName + "' called with the wrong number of arguments (function call \"" + node.toSourceString() + "\").");
            }
            List<JsExpr> args = this.visitChildren(node);
            try {
                return fn.computeForJsSrc(args);
            }
            catch (Exception e) {
                throw SoySyntaxException.createCausedWithoutMetaInfo("Error in function call \"" + node.toSourceString() + "\": " + e.getMessage(), e);
            }
        }
        throw SoySyntaxException.createWithoutMetaInfo("Failed to find SoyJsSrcFunction with name '" + fnName + "' (function call \"" + node.toSourceString() + "\").");
    }

    private JsExpr visitIsFirstFunction(FunctionNode node) {
        String varName = ((VarRefNode)node.getChild(0)).getName();
        return this.getLocalVarTranslation(varName + "__isFirst");
    }

    private JsExpr visitIsLastFunction(FunctionNode node) {
        String varName = ((VarRefNode)node.getChild(0)).getName();
        return this.getLocalVarTranslation(varName + "__isLast");
    }

    private JsExpr visitIndexFunction(FunctionNode node) {
        String varName = ((VarRefNode)node.getChild(0)).getName();
        return this.getLocalVarTranslation(varName + "__index");
    }

    private JsExpr getLocalVarTranslation(String ident) {
        for (Map<String, JsExpr> localVarTranslationsFrame : this.localVarTranslations) {
            JsExpr translation = localVarTranslationsFrame.get(ident);
            if (translation == null) continue;
            return translation;
        }
        return null;
    }

    private JsExpr genJsExprUsingSoySyntax(ExprNode.OperatorNode opNode) {
        return this.genJsExprUsingSoySyntaxWithNewToken(opNode, null);
    }

    private JsExpr genJsExprUsingSoySyntaxWithNewToken(ExprNode.OperatorNode opNode, String newToken) {
        List<JsExpr> operandJsExprs = this.visitChildren(opNode);
        return SoyJsCodeUtils.genJsExprUsingSoySyntaxWithNewToken(opNode.getOperator(), operandJsExprs, newToken);
    }

    public static String genMaybeProtect(JsExpr expr, int minSafePrecedence) {
        return expr.getPrecedence() >= minSafePrecedence ? expr.getText() : "(" + expr.getText() + ")";
    }

    public static interface TranslateToJsExprVisitorFactory {
        public TranslateToJsExprVisitor create(Deque<Map<String, JsExpr>> var1);
    }
}

