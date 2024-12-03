/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.soytree.AbstractCommandNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class CssNode
extends AbstractCommandNode
implements SoyNode.StandaloneNode,
SoyNode.StatementNode,
SoyNode.ExprHolderNode {
    private static final String CSS_CLASS_NAME_RE = "-?[a-zA-Z_]+[a-zA-Z0-9_-]*";
    private static final Pattern SELECTOR_TEXT_PATTERN = Pattern.compile("^(-?[a-zA-Z_]+[a-zA-Z0-9_-]*|[$]?[a-zA-Z_][a-zA-Z_0-9]*(?:[.][a-zA-Z_][a-zA-Z_0-9]*)*)$");
    @Nullable
    private final ExprRootNode<?> componentNameExpr;
    private final String selectorText;
    Pair<SoyCssRenamingMap, String> renameCache;

    public CssNode(int id, String commandText) throws SoySyntaxException {
        super(id, "css", commandText);
        int delimPos = commandText.lastIndexOf(44);
        if (delimPos != -1) {
            String componentNameText = commandText.substring(0, delimPos).trim();
            this.componentNameExpr = ExprParseUtils.parseExprElseThrowSoySyntaxException(componentNameText, "Invalid component name expression in 'css' command text \"" + componentNameText + "\".");
            this.selectorText = commandText.substring(delimPos + 1).trim();
        } else {
            this.componentNameExpr = null;
            this.selectorText = commandText;
        }
        if (!SELECTOR_TEXT_PATTERN.matcher(this.selectorText).matches()) {
            this.maybeSetSyntaxVersionBound(new SyntaxVersionBound(SyntaxVersion.V2_1, "Invalid 'css' command text."));
        }
    }

    protected CssNode(CssNode orig) {
        super(orig);
        this.componentNameExpr = orig.componentNameExpr != null ? orig.componentNameExpr.clone() : null;
        this.selectorText = orig.selectorText;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.CSS_NODE;
    }

    @Nullable
    public ExprRootNode<?> getComponentNameExpr() {
        return this.componentNameExpr;
    }

    public String getComponentNameText() {
        return this.componentNameExpr != null ? this.componentNameExpr.toSourceString() : null;
    }

    public String getSelectorText() {
        return this.selectorText;
    }

    public String getRenamedSelectorText(SoyCssRenamingMap cssRenamingMap) {
        String mappedText;
        Pair<SoyCssRenamingMap, String> cache = this.renameCache;
        if (cache != null && cache.first == cssRenamingMap) {
            return (String)cache.second;
        }
        if (cssRenamingMap != null && (mappedText = cssRenamingMap.get(this.selectorText)) != null) {
            this.renameCache = Pair.of(cssRenamingMap, mappedText);
            return mappedText;
        }
        return this.selectorText;
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return this.componentNameExpr != null ? ImmutableList.of((Object)new ExprUnion(this.componentNameExpr)) : Collections.emptyList();
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public CssNode clone() {
        return new CssNode(this);
    }
}

