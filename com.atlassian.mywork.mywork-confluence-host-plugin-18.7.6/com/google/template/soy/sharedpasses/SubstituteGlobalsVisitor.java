/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses;

import com.google.common.annotations.VisibleForTesting;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.data.internalutils.InternalValueUtils;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.PrimitiveData;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoytreeUtils;
import com.google.template.soy.types.SoyEnumType;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.SoyTypeRegistry;
import java.util.Map;
import javax.annotation.Nullable;

public class SubstituteGlobalsVisitor {
    private Map<String, PrimitiveData> compileTimeGlobals;
    private final boolean shouldAssertNoUnboundGlobals;
    private final SoyTypeRegistry typeRegistry;

    public SubstituteGlobalsVisitor(@Nullable Map<String, PrimitiveData> compileTimeGlobals, @Nullable SoyTypeRegistry typeRegistry, boolean shouldAssertNoUnboundGlobals) {
        this.compileTimeGlobals = compileTimeGlobals;
        this.typeRegistry = typeRegistry;
        this.shouldAssertNoUnboundGlobals = shouldAssertNoUnboundGlobals;
    }

    public void exec(SoyFileSetNode soyTree) {
        SoytreeUtils.execOnAllV2Exprs(soyTree, new SubstituteGlobalsInExprVisitor());
    }

    @VisibleForTesting
    class SubstituteGlobalsInExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        SubstituteGlobalsInExprVisitor() {
        }

        @Override
        protected void visitGlobalNode(GlobalNode node) {
            PrimitiveData value;
            PrimitiveData primitiveData = value = SubstituteGlobalsVisitor.this.compileTimeGlobals != null ? (PrimitiveData)SubstituteGlobalsVisitor.this.compileTimeGlobals.get(node.getName()) : null;
            if (value == null && SubstituteGlobalsVisitor.this.typeRegistry != null) {
                value = this.getEnumValue(node.getName());
            }
            if (value == null) {
                if (SubstituteGlobalsVisitor.this.shouldAssertNoUnboundGlobals) {
                    throw SoySyntaxException.createWithoutMetaInfo("Found unbound global '" + node.getName() + "'.");
                }
                return;
            }
            node.getParent().replaceChild(node, InternalValueUtils.convertPrimitiveDataToExpr(value));
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildrenAllowingConcurrentModification((ExprNode.ParentExprNode)node);
            }
        }

        private PrimitiveData getEnumValue(String name) {
            int lastDot = name.lastIndexOf(46);
            if (lastDot < 0) {
                return null;
            }
            String enumTypeName = name.substring(0, lastDot);
            SoyType type = SubstituteGlobalsVisitor.this.typeRegistry.getType(enumTypeName);
            if (type != null && type instanceof SoyEnumType) {
                SoyEnumType enumType = (SoyEnumType)type;
                String enumValueName = name.substring(lastDot + 1);
                Integer enumValue = enumType.getValue(enumValueName);
                if (enumValue != null) {
                    return IntegerData.forValue(enumValue.intValue());
                }
                throw SoySyntaxException.createWithoutMetaInfo("'" + enumValueName + "' is not a member of " + enumTypeName + ".");
            }
            return null;
        }
    }
}

