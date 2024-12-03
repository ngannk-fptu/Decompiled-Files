/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses.render;

import com.google.common.collect.Lists;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyAbstractValue;
import com.google.template.soy.data.SoyEasyDict;
import com.google.template.soy.data.SoyMap;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.NumberData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.data.restricted.UndefinedData;
import com.google.template.soy.exprtree.AbstractReturningExprNodeVisitor;
import com.google.template.soy.exprtree.BooleanNode;
import com.google.template.soy.exprtree.DataAccessNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FieldAccessNode;
import com.google.template.soy.exprtree.FloatNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.ItemAccessNode;
import com.google.template.soy.exprtree.ListLiteralNode;
import com.google.template.soy.exprtree.MapLiteralNode;
import com.google.template.soy.exprtree.NullNode;
import com.google.template.soy.exprtree.OperatorNodes;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.shared.internal.NonpluginFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.sharedpasses.render.RenderException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class EvalVisitor
extends AbstractReturningExprNodeVisitor<SoyValue> {
    private final SoyValueHelper valueHelper;
    private final Map<String, SoyJavaFunction> soyJavaFunctionsMap;
    private final SoyRecord data;
    private final SoyRecord ijData;
    private final Deque<Map<String, SoyValue>> env;

    protected EvalVisitor(SoyValueHelper valueHelper, @Nullable Map<String, SoyJavaFunction> soyJavaFunctionsMap, SoyRecord data, @Nullable SoyRecord ijData, Deque<Map<String, SoyValue>> env) {
        this.valueHelper = valueHelper;
        this.soyJavaFunctionsMap = soyJavaFunctionsMap;
        this.data = data;
        this.ijData = ijData;
        this.env = env;
    }

    @Override
    protected SoyValue visitExprRootNode(ExprRootNode<?> node) {
        return (SoyValue)this.visit((ExprNode)node.getChild(0));
    }

    @Override
    protected SoyValue visitNullNode(NullNode node) {
        return NullData.INSTANCE;
    }

    @Override
    protected SoyValue visitBooleanNode(BooleanNode node) {
        return this.convertResult(node.getValue());
    }

    @Override
    protected SoyValue visitIntegerNode(IntegerNode node) {
        return this.convertResult(node.getValue());
    }

    @Override
    protected SoyValue visitFloatNode(FloatNode node) {
        return this.convertResult(node.getValue());
    }

    @Override
    protected SoyValue visitStringNode(StringNode node) {
        return this.convertResult(node.getValue());
    }

    @Override
    protected SoyValue visitListLiteralNode(ListLiteralNode node) {
        return this.valueHelper.newEasyListFromJavaIterable(this.visitChildren(node)).makeImmutable();
    }

    @Override
    protected SoyValue visitMapLiteralNode(MapLiteralNode node) {
        int numItems = node.numChildren() / 2;
        boolean isStringKeyed = true;
        Node firstNonstringKeyNode = null;
        ArrayList keys = Lists.newArrayListWithCapacity((int)numItems);
        ArrayList values = Lists.newArrayListWithCapacity((int)numItems);
        for (int i = 0; i < numItems; ++i) {
            SoyValue key = (SoyValue)this.visit(node.getChild(2 * i));
            if (isStringKeyed && !(key instanceof StringData)) {
                isStringKeyed = false;
                firstNonstringKeyNode = node.getChild(2 * i);
            }
            keys.add(key);
            values.add(this.visit(node.getChild(2 * i + 1)));
        }
        if (isStringKeyed) {
            SoyEasyDict dict = this.valueHelper.newEasyDict();
            for (int i = 0; i < numItems; ++i) {
                dict.setField(((SoyValue)keys.get(i)).stringValue(), (SoyValueProvider)values.get(i));
            }
            return dict.makeImmutable();
        }
        throw new RenderException(String.format("Currently, map literals must have string keys (key \"%s\" in map %s does not evaluate to a string). Support for nonstring keys is a todo.", firstNonstringKeyNode.toSourceString(), node.toSourceString()));
    }

    @Override
    protected SoyValue visitVarRefNode(VarRefNode node) {
        return this.visitNullSafeNode(node);
    }

    @Override
    protected SoyValue visitDataAccessNode(DataAccessNode node) {
        return this.visitNullSafeNode(node);
    }

    private SoyValue visitNullSafeNode(ExprNode node) {
        SoyValue value = this.visitNullSafeNodeRecurse(node);
        if (value == NullSafetySentinel.INSTANCE) {
            return NullData.INSTANCE;
        }
        return value;
    }

    private SoyValue visitNullSafeNodeRecurse(ExprNode node) {
        Object value = null;
        switch (node.getKind()) {
            case VAR_REF_NODE: {
                return this.visitNullSafeVarRefNode((VarRefNode)node);
            }
            case FIELD_ACCESS_NODE: 
            case ITEM_ACCESS_NODE: {
                return this.visitNullSafeDataAccessNode((DataAccessNode)node);
            }
        }
        return (SoyValue)this.visit(node);
    }

    /*
     * Enabled aggressive block sorting
     */
    private SoyValue visitNullSafeVarRefNode(VarRefNode varRef) {
        SoyValue soyValue;
        SoyValue result = null;
        if (varRef.isInjected()) {
            if (this.ijData == null) {
                if (!varRef.isNullSafeInjected()) throw new RenderException("Injected data not provided, yet referenced (" + varRef.toSourceString() + ").");
                return NullSafetySentinel.INSTANCE;
            }
            result = this.ijData.getField(varRef.getName());
        } else {
            VarDefn.Kind varKind;
            VarDefn var = varRef.getDefnDecl();
            VarDefn.Kind kind = varKind = var != null ? var.kind() : VarDefn.Kind.UNDECLARED;
            if ((varKind == VarDefn.Kind.LOCAL_VAR || varKind == VarDefn.Kind.UNDECLARED) && this.env != null) {
                for (Map<String, SoyValue> envFrame : this.env) {
                    result = envFrame.get(varRef.getName());
                    if (result == null) continue;
                    return result;
                }
            }
            if ((varKind == VarDefn.Kind.PARAM || varKind == VarDefn.Kind.UNDECLARED && result == null) && this.data != null) {
                result = this.data.getField(varRef.getName());
            }
        }
        if (result != null) {
            soyValue = result;
            return soyValue;
        }
        soyValue = UndefinedData.INSTANCE;
        return soyValue;
    }

    private SoyValue visitNullSafeDataAccessNode(DataAccessNode dataAccess) {
        SoyValue value = this.visitNullSafeNodeRecurse(dataAccess.getBaseExprChild());
        String expectedTypeNameForErrorMsg = null;
        if (dataAccess.getKind() == ExprNode.Kind.FIELD_ACCESS_NODE) {
            if (value instanceof SoyRecord) {
                value = ((SoyRecord)value).getField(((FieldAccessNode)dataAccess).getFieldName());
            } else {
                expectedTypeNameForErrorMsg = "record";
            }
        } else if (value instanceof SoyMap) {
            SoyValue key = (SoyValue)this.visit(((ItemAccessNode)dataAccess).getKeyExprChild());
            value = ((SoyMap)value).getItem(key);
        } else {
            expectedTypeNameForErrorMsg = "map/list";
        }
        if (expectedTypeNameForErrorMsg != null) {
            if (dataAccess.isNullSafe()) {
                if (value == null || value instanceof UndefinedData || value instanceof NullData || value == NullSafetySentinel.INSTANCE) {
                    return NullSafetySentinel.INSTANCE;
                }
                throw new RenderException(String.format("While evaluating \"%s\", encountered non-%s just before accessing \"%s\".", dataAccess.toSourceString(), expectedTypeNameForErrorMsg, dataAccess.getSourceStringSuffix()));
            }
            if (value == NullSafetySentinel.INSTANCE) {
                return value;
            }
            return UndefinedData.INSTANCE;
        }
        if (dataAccess.getType() != null && value != null && !dataAccess.getType().isInstance(value)) {
            throw new RenderException(String.format("Expected value of type '" + dataAccess.getType() + "', but actual types was '" + value.getClass().getSimpleName() + "'.", new Object[0]));
        }
        return value != null ? value : UndefinedData.INSTANCE;
    }

    @Override
    protected SoyValue visitNegativeOpNode(OperatorNodes.NegativeOpNode node) {
        SoyValue operand = (SoyValue)this.visit(node.getChild(0));
        if (operand instanceof IntegerData) {
            return this.convertResult(-operand.longValue());
        }
        return this.convertResult(-operand.floatValue());
    }

    @Override
    protected SoyValue visitNotOpNode(OperatorNodes.NotOpNode node) {
        SoyValue operand = (SoyValue)this.visit(node.getChild(0));
        return this.convertResult(!operand.coerceToBoolean());
    }

    @Override
    protected SoyValue visitTimesOpNode(OperatorNodes.TimesOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        if (operand0 instanceof IntegerData && operand1 instanceof IntegerData) {
            return this.convertResult(operand0.longValue() * operand1.longValue());
        }
        return this.convertResult(operand0.numberValue() * operand1.numberValue());
    }

    @Override
    protected SoyValue visitDivideByOpNode(OperatorNodes.DivideByOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        return this.convertResult(operand0.numberValue() / operand1.numberValue());
    }

    @Override
    protected SoyValue visitModOpNode(OperatorNodes.ModOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        return this.convertResult(operand0.longValue() % operand1.longValue());
    }

    @Override
    protected SoyValue visitPlusOpNode(OperatorNodes.PlusOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        if (operand0 instanceof IntegerData && operand1 instanceof IntegerData) {
            return this.convertResult(operand0.longValue() + operand1.longValue());
        }
        if (operand0 instanceof StringData || operand1 instanceof StringData) {
            return this.convertResult(operand0.toString() + operand1.toString());
        }
        return this.convertResult(operand0.numberValue() + operand1.numberValue());
    }

    @Override
    protected SoyValue visitMinusOpNode(OperatorNodes.MinusOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        if (operand0 instanceof IntegerData && operand1 instanceof IntegerData) {
            return this.convertResult(operand0.longValue() - operand1.longValue());
        }
        return this.convertResult(operand0.numberValue() - operand1.numberValue());
    }

    @Override
    protected SoyValue visitLessThanOpNode(OperatorNodes.LessThanOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        if (operand0 instanceof IntegerData && operand1 instanceof IntegerData) {
            return this.convertResult(operand0.longValue() < operand1.longValue());
        }
        return this.convertResult(operand0.numberValue() < operand1.numberValue());
    }

    @Override
    protected SoyValue visitGreaterThanOpNode(OperatorNodes.GreaterThanOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        if (operand0 instanceof IntegerData && operand1 instanceof IntegerData) {
            return this.convertResult(operand0.longValue() > operand1.longValue());
        }
        return this.convertResult(operand0.numberValue() > operand1.numberValue());
    }

    @Override
    protected SoyValue visitLessThanOrEqualOpNode(OperatorNodes.LessThanOrEqualOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        if (operand0 instanceof IntegerData && operand1 instanceof IntegerData) {
            return this.convertResult(operand0.longValue() <= operand1.longValue());
        }
        return this.convertResult(operand0.numberValue() <= operand1.numberValue());
    }

    @Override
    protected SoyValue visitGreaterThanOrEqualOpNode(OperatorNodes.GreaterThanOrEqualOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        if (operand0 instanceof IntegerData && operand1 instanceof IntegerData) {
            return this.convertResult(operand0.longValue() >= operand1.longValue());
        }
        return this.convertResult(operand0.numberValue() >= operand1.numberValue());
    }

    private boolean compareString(StringData stringData, SoyValue other) {
        if (other instanceof StringData || other instanceof SanitizedContent) {
            return stringData.stringValue().equals(other.toString());
        }
        if (other instanceof NumberData) {
            try {
                return Double.parseDouble(stringData.stringValue()) == other.numberValue();
            }
            catch (NumberFormatException nfe) {
                return false;
            }
        }
        try {
            return stringData.stringValue().equals(other.coerceToString());
        }
        catch (Exception e) {
            return false;
        }
    }

    private boolean equals(SoyValue operand0, SoyValue operand1) {
        if (operand0 instanceof StringData) {
            return this.compareString((StringData)operand0, operand1);
        }
        if (operand1 instanceof StringData) {
            return this.compareString((StringData)operand1, operand0);
        }
        return operand0.equals(operand1);
    }

    @Override
    protected SoyValue visitEqualOpNode(OperatorNodes.EqualOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        return this.convertResult(this.equals(operand0, operand1));
    }

    @Override
    protected SoyValue visitNotEqualOpNode(OperatorNodes.NotEqualOpNode node) {
        SoyValue operand1;
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        return this.convertResult(!this.equals(operand0, operand1 = (SoyValue)this.visit(node.getChild(1))));
    }

    @Override
    protected SoyValue visitAndOpNode(OperatorNodes.AndOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        if (!operand0.coerceToBoolean()) {
            return this.convertResult(false);
        }
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        return this.convertResult(operand1.coerceToBoolean());
    }

    @Override
    protected SoyValue visitOrOpNode(OperatorNodes.OrOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        if (operand0.coerceToBoolean()) {
            return this.convertResult(true);
        }
        SoyValue operand1 = (SoyValue)this.visit(node.getChild(1));
        return this.convertResult(operand1.coerceToBoolean());
    }

    @Override
    protected SoyValue visitConditionalOpNode(OperatorNodes.ConditionalOpNode node) {
        SoyValue operand0 = (SoyValue)this.visit(node.getChild(0));
        if (operand0.coerceToBoolean()) {
            return (SoyValue)this.visit(node.getChild(1));
        }
        return (SoyValue)this.visit(node.getChild(2));
    }

    @Override
    protected SoyValue visitFunctionNode(FunctionNode node) {
        String fnName = node.getFunctionName();
        NonpluginFunction nonpluginFn = NonpluginFunction.forFunctionName(fnName);
        if (nonpluginFn != null) {
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
                    return this.visitMapLiteralNode((MapLiteralNode)node.getChild(0));
                }
            }
            throw new AssertionError();
        }
        List<SoyValue> args = this.visitChildren(node);
        SoyJavaFunction fn = this.soyJavaFunctionsMap.get(fnName);
        if (fn == null) {
            throw new RenderException("Failed to find Soy function with name '" + fnName + "' (function call \"" + node.toSourceString() + "\").");
        }
        return this.computeFunctionHelper(fn, args, node);
    }

    protected SoyValue computeFunctionHelper(SoyJavaFunction fn, List<SoyValue> args, FunctionNode fnNode) {
        try {
            return fn.computeForJava(args);
        }
        catch (Exception e) {
            throw new RenderException("While computing function \"" + fnNode.toSourceString() + "\": " + e.getMessage(), e);
        }
    }

    private SoyValue visitIsFirstFunction(FunctionNode node) {
        int localVarIndex;
        try {
            VarRefNode dataRef = (VarRefNode)node.getChild(0);
            String localVarName = dataRef.getName();
            localVarIndex = this.getLocalVar(localVarName + "__index").integerValue();
        }
        catch (Exception e) {
            throw new RenderException("Failed to evaluate function call " + node.toSourceString() + ".", e);
        }
        return this.convertResult(localVarIndex == 0);
    }

    private SoyValue visitIsLastFunction(FunctionNode node) {
        int localVarLastIndex;
        int localVarIndex;
        try {
            VarRefNode dataRef = (VarRefNode)node.getChild(0);
            String localVarName = dataRef.getName();
            localVarIndex = this.getLocalVar(localVarName + "__index").integerValue();
            localVarLastIndex = this.getLocalVar(localVarName + "__lastIndex").integerValue();
        }
        catch (Exception e) {
            throw new RenderException("Failed to evaluate function call " + node.toSourceString() + ".", e);
        }
        return this.convertResult(localVarIndex == localVarLastIndex);
    }

    private SoyValue visitIndexFunction(FunctionNode node) {
        int localVarIndex;
        try {
            VarRefNode dataRef = (VarRefNode)node.getChild(0);
            String localVarName = dataRef.getName();
            localVarIndex = this.getLocalVar(localVarName + "__index").integerValue();
        }
        catch (Exception e) {
            throw new RenderException("Failed to evaluate function call " + node.toSourceString() + ".", e);
        }
        return this.convertResult(localVarIndex);
    }

    private SoyValue convertResult(boolean b) {
        return BooleanData.forValue(b);
    }

    private SoyValue convertResult(long i) {
        return IntegerData.forValue(i);
    }

    private SoyValue convertResult(double f) {
        return FloatData.forValue(f);
    }

    private SoyValue convertResult(String s) {
        return StringData.forValue(s);
    }

    private SoyValue getLocalVar(String localVarName) {
        for (Map<String, SoyValue> envFrame : this.env) {
            SoyValue value = envFrame.get(localVarName);
            if (value == null) continue;
            return value;
        }
        throw new AssertionError();
    }

    private static final class NullSafetySentinel
    extends SoyAbstractValue {
        public static final NullSafetySentinel INSTANCE = new NullSafetySentinel();

        private NullSafetySentinel() {
        }

        @Override
        public boolean equals(SoyValue other) {
            return other == INSTANCE;
        }

        @Override
        public boolean coerceToBoolean() {
            return false;
        }

        @Override
        public String coerceToString() {
            return "null";
        }
    }

    public static interface EvalVisitorFactory {
        public EvalVisitor create(SoyRecord var1, @Nullable SoyRecord var2, Deque<Map<String, SoyValue>> var3);
    }
}

