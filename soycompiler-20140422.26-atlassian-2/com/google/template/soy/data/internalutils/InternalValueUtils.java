/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.google.template.soy.data.internalutils;

import com.google.common.collect.ImmutableMap;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.PrimitiveData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.exprtree.BooleanNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.FloatNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.NullNode;
import com.google.template.soy.exprtree.StringNode;
import java.util.Map;

public class InternalValueUtils {
    private InternalValueUtils() {
    }

    public static ExprNode.PrimitiveNode convertPrimitiveDataToExpr(PrimitiveData primitiveData) {
        if (primitiveData instanceof StringData) {
            return new StringNode(primitiveData.stringValue());
        }
        if (primitiveData instanceof BooleanData) {
            return new BooleanNode(primitiveData.booleanValue());
        }
        if (primitiveData instanceof IntegerData) {
            return new IntegerNode(primitiveData.integerValue());
        }
        if (primitiveData instanceof FloatData) {
            return new FloatNode(primitiveData.floatValue());
        }
        if (primitiveData instanceof NullData) {
            return new NullNode();
        }
        throw new IllegalArgumentException();
    }

    public static PrimitiveData convertPrimitiveExprToData(ExprNode.PrimitiveNode primitiveNode) {
        if (primitiveNode instanceof StringNode) {
            return StringData.forValue(((StringNode)primitiveNode).getValue());
        }
        if (primitiveNode instanceof BooleanNode) {
            return BooleanData.forValue(((BooleanNode)primitiveNode).getValue());
        }
        if (primitiveNode instanceof IntegerNode) {
            return IntegerData.forValue(((IntegerNode)primitiveNode).getValue());
        }
        if (primitiveNode instanceof FloatNode) {
            return FloatData.forValue(((FloatNode)primitiveNode).getValue());
        }
        if (primitiveNode instanceof NullNode) {
            return NullData.INSTANCE;
        }
        throw new IllegalArgumentException();
    }

    public static ImmutableMap<String, PrimitiveData> convertCompileTimeGlobalsMap(Map<String, ?> compileTimeGlobalsMap) {
        ImmutableMap.Builder resultMapBuilder = ImmutableMap.builder();
        for (Map.Entry<String, ?> entry : compileTimeGlobalsMap.entrySet()) {
            PrimitiveData value;
            Object valueObj = entry.getValue();
            boolean isValidValue = true;
            try {
                SoyValue value0 = SoyValueHelper.UNCUSTOMIZED_INSTANCE.convert(valueObj).resolve();
                if (!(value0 instanceof PrimitiveData)) {
                    isValidValue = false;
                }
                value = (PrimitiveData)value0;
            }
            catch (SoyDataException sde) {
                isValidValue = false;
                value = null;
            }
            if (!isValidValue) {
                throw SoySyntaxException.createWithoutMetaInfo("Compile-time globals map contains invalid value: " + valueObj.toString() + ".");
            }
            resultMapBuilder.put((Object)entry.getKey(), (Object)value);
        }
        return resultMapBuilder.build();
    }
}

