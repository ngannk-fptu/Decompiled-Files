/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.defn.TemplateParam;
import java.util.List;
import javax.annotation.Nullable;

public class TemplateDelegateNode
extends TemplateNode
implements SoyNode.ExprHolderNode {
    private final String delTemplateName;
    private String delTemplateVariant;
    private final ExprRootNode<?> delTemplateVariantExpr;
    private DelTemplateKey delTemplateKey;
    private final int delPriority;

    TemplateDelegateNode(int id, @Nullable SyntaxVersionBound syntaxVersionBound, String cmdText, TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo, String delTemplateName, String delTemplateVariant, ExprRootNode<?> delTemplateVariantExpr, DelTemplateKey delTemplateKey, int delPriority, String templateName, @Nullable String partialTemplateName, String templateNameForUserMsgs, AutoescapeMode autoescapeMode, SanitizedContent.ContentKind contentKind, ImmutableList<String> requiredCssNamespaces, String soyDoc, String soyDocDesc, ImmutableList<TemplateParam> params) {
        super(id, syntaxVersionBound, "deltemplate", cmdText, soyFileHeaderInfo, templateName, partialTemplateName, templateNameForUserMsgs, false, autoescapeMode, contentKind, requiredCssNamespaces, soyDoc, soyDocDesc, params);
        this.delTemplateName = delTemplateName;
        this.delTemplateVariant = delTemplateVariant;
        this.delTemplateVariantExpr = delTemplateVariantExpr;
        this.delTemplateKey = delTemplateKey;
        this.delPriority = delPriority;
    }

    protected TemplateDelegateNode(TemplateDelegateNode orig) {
        super(orig);
        this.delTemplateName = orig.delTemplateName;
        this.delTemplateVariant = orig.delTemplateVariant;
        this.delTemplateVariantExpr = orig.delTemplateVariantExpr;
        this.delTemplateKey = orig.delTemplateKey;
        this.delPriority = orig.delPriority;
    }

    static void verifyVariantName(String delTemplateVariant) {
        if (delTemplateVariant.length() > 0 && !BaseUtils.isIdentifier(delTemplateVariant)) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid variant \"" + delTemplateVariant + "\" in 'deltemplate' (when a string literal is used, value must be an identifier).");
        }
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.TEMPLATE_DELEGATE_NODE;
    }

    public String getDelTemplateName() {
        return this.delTemplateName;
    }

    public String getDelTemplateVariant() {
        if (this.delTemplateVariant != null) {
            return this.delTemplateVariant;
        }
        return this.resolveVariantExpression().variant;
    }

    public DelTemplateKey getDelTemplateKey() {
        if (this.delTemplateKey != null) {
            return this.delTemplateKey;
        }
        return this.resolveVariantExpression();
    }

    public int getDelPriority() {
        return this.delPriority;
    }

    @Override
    public TemplateDelegateNode clone() {
        return new TemplateDelegateNode(this);
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        if (this.delTemplateVariantExpr == null) {
            return ImmutableList.of();
        }
        return ImmutableList.of((Object)new ExprUnion(this.delTemplateVariantExpr));
    }

    private DelTemplateKey resolveVariantExpression() {
        if (this.delTemplateVariantExpr == null || this.delTemplateVariantExpr.numChildren() != 1) {
            throw this.invalidExpressionError();
        }
        Node exprNode = this.delTemplateVariantExpr.getChild(0);
        if (exprNode instanceof IntegerNode) {
            int variantValue = ((IntegerNode)exprNode).getValue();
            Preconditions.checkArgument((variantValue >= 0 ? 1 : 0) != 0, (Object)"Globals used as deltemplate variants must not evaluate to negative numbers.");
            this.delTemplateVariant = String.valueOf(variantValue);
            this.delTemplateKey = new DelTemplateKey(this.delTemplateName, this.delTemplateVariant);
            return this.delTemplateKey;
        }
        if (exprNode instanceof StringNode) {
            this.delTemplateVariant = ((StringNode)exprNode).getValue();
            TemplateDelegateNode.verifyVariantName(this.delTemplateVariant);
            this.delTemplateKey = new DelTemplateKey(this.delTemplateName, this.delTemplateVariant);
            return this.delTemplateKey;
        }
        if (exprNode instanceof GlobalNode) {
            return new DelTemplateKey(this.delTemplateName, null, ((GlobalNode)exprNode).getName());
        }
        throw this.invalidExpressionError();
    }

    private AssertionError invalidExpressionError() {
        return new AssertionError((Object)("Invalid expression for deltemplate variant for " + this.delTemplateName + " template"));
    }

    public static final class DelTemplateKey {
        public final String name;
        public final String variant;
        public final String variantExpr;

        public DelTemplateKey(String name, String variant) {
            this(name, variant, null);
        }

        public DelTemplateKey(String name, String variant, String variantExpr) {
            this.name = name;
            this.variant = variant;
            this.variantExpr = variantExpr;
        }

        public boolean equals(Object other) {
            if (!(other instanceof DelTemplateKey)) {
                return false;
            }
            DelTemplateKey otherKey = (DelTemplateKey)other;
            return Objects.equal((Object)this.name, (Object)otherKey.name) && Objects.equal((Object)this.variant, (Object)otherKey.variant) && Objects.equal((Object)this.variantExpr, (Object)otherKey.variantExpr);
        }

        public int hashCode() {
            return Objects.hashCode((Object[])new Object[]{this.name, this.variant, this.variantExpr});
        }

        public String toString() {
            return this.name + (this.variant == null || this.variant.length() == 0 ? "" : ":" + this.variant) + (this.variantExpr == null || this.variantExpr.length() == 0 ? "" : ":" + this.variantExpr);
        }
    }
}

