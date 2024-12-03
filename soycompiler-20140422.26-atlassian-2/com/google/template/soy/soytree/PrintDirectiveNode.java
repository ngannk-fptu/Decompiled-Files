/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractSoyNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrintDirectiveNode
extends AbstractSoyNode
implements SoyNode.ExprHolderNode {
    private static final Set<String> V1_DIRECTIVE_NAMES = ImmutableSet.of((Object)"|noescape", (Object)"|escape", (Object)"|insertwordbreaks");
    private static final Map<String, String> DEPRECATED_DIRECTIVE_NAMES = ImmutableMap.of((Object)"|noescape", (Object)"|noAutoescape", (Object)"|escape", (Object)"|escapeHtml", (Object)"|escapeJs", (Object)"|escapeJsString", (Object)"|insertwordbreaks", (Object)"|insertWordBreaks");
    private final String name;
    private final String srcName;
    private final String argsText;
    private final ImmutableList<ExprRootNode<?>> args;

    public PrintDirectiveNode(int id, String srcName, String argsText) throws SoySyntaxException {
        super(id);
        this.srcName = srcName;
        String translatedDirectiveName = DEPRECATED_DIRECTIVE_NAMES.get(srcName);
        if (translatedDirectiveName == null) {
            this.name = srcName;
        } else {
            this.name = translatedDirectiveName;
            if (V1_DIRECTIVE_NAMES.contains(srcName)) {
                this.maybeSetSyntaxVersionBound(new SyntaxVersionBound(SyntaxVersion.V2_1, "Print directive '" + srcName + "' is from Soy V1.0."));
            }
        }
        this.argsText = argsText;
        List<Object> tempArgs = this.argsText.length() > 0 ? ExprParseUtils.parseExprListElseThrowSoySyntaxException(argsText, "Invalid arguments for print directive \"" + this.toSourceString() + "\".") : Collections.emptyList();
        this.args = ImmutableList.copyOf(tempArgs);
    }

    protected PrintDirectiveNode(PrintDirectiveNode orig) {
        super(orig);
        this.srcName = orig.srcName;
        this.name = orig.name;
        this.argsText = orig.argsText;
        ArrayList tempArgs = Lists.newArrayListWithCapacity((int)orig.args.size());
        for (ExprRootNode origArg : orig.args) {
            tempArgs.add(origArg.clone());
        }
        this.args = ImmutableList.copyOf((Collection)tempArgs);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.PRINT_DIRECTIVE_NODE;
    }

    public String getName() {
        return this.name;
    }

    public String getArgsText() {
        return this.argsText;
    }

    public List<ExprRootNode<?>> getArgs() {
        return this.args;
    }

    @Override
    public String toSourceString() {
        return this.srcName + (this.argsText.length() > 0 ? ":" + this.argsText : "");
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ExprUnion.createList(this.args);
    }

    @Override
    public PrintDirectiveNode clone() {
        return new PrintDirectiveNode(this);
    }
}

