/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.soytree.AbstractBlockCommandNode;
import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.CommandTextAttributesParser;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.MsgPlaceholderNode;
import com.google.template.soy.soytree.MsgPluralNode;
import com.google.template.soy.soytree.MsgSelectNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class MsgNode
extends AbstractBlockCommandNode
implements SoyNode.ExprHolderNode,
SoyNode.MsgBlockNode {
    private static final CommandTextAttributesParser ATTRIBUTES_PARSER = new CommandTextAttributesParser("msg", new CommandTextAttributesParser.Attribute("genders", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("meaning", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("desc", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, "__NDVBR__"), new CommandTextAttributesParser.Attribute("hidden", CommandTextAttributesParser.Attribute.BOOLEAN_VALUES, "false"));
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    @Nullable
    private List<ExprRootNode<?>> genderExprs;
    private final String meaning;
    private final String desc;
    private final boolean isHidden;
    private SubstUnitInfo substUnitInfo = null;

    public MsgNode(int id, String commandName, String commandText) throws SoySyntaxException {
        super(id, commandName, commandText);
        Preconditions.checkArgument((commandName.equals("msg") || commandName.equals("fallbackmsg") ? 1 : 0) != 0);
        Map<String, String> attributes = ATTRIBUTES_PARSER.parse(commandText);
        String gendersAttr = attributes.get("genders");
        if (gendersAttr == null) {
            this.genderExprs = null;
        } else {
            this.genderExprs = ExprParseUtils.parseExprListElseThrowSoySyntaxException(gendersAttr, "Invalid 'genders' expression list in 'msg' command text \"" + commandText + "\".");
            assert (this.genderExprs != null);
            if (this.genderExprs.size() < 1 || this.genderExprs.size() > 3) {
                throw SoySyntaxException.createWithoutMetaInfo("Attribute 'genders' does not contain exactly 1-3 expressions (in tag {msg " + commandText + "}).");
            }
        }
        this.meaning = attributes.get("meaning");
        this.desc = attributes.get("desc");
        this.isHidden = attributes.get("hidden").equals("true");
    }

    protected MsgNode(MsgNode orig) {
        super(orig);
        if (orig.genderExprs != null) {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (ExprRootNode<?> node : orig.genderExprs) {
                builder.add((Object)node.clone());
            }
            this.genderExprs = builder.build();
        } else {
            this.genderExprs = null;
        }
        this.meaning = orig.meaning;
        this.desc = orig.desc;
        this.isHidden = orig.isHidden;
        this.substUnitInfo = MsgNode.genSubstUnitInfo(this);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_NODE;
    }

    @Nullable
    public List<ExprRootNode<?>> getAndRemoveGenderExprs() {
        List<ExprRootNode<?>> genderExprs = this.genderExprs;
        this.genderExprs = null;
        return genderExprs;
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        if (this.genderExprs != null) {
            throw new AssertionError();
        }
        return ImmutableList.of();
    }

    public String getMeaning() {
        return this.meaning;
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    public boolean isPlrselMsg() {
        return this.getChildren().size() == 1 && (this.getChild(0) instanceof MsgPluralNode || this.getChild(0) instanceof MsgSelectNode);
    }

    public MsgPlaceholderNode getRepPlaceholderNode(String placeholderName) {
        if (this.substUnitInfo == null) {
            this.substUnitInfo = MsgNode.genSubstUnitInfo(this);
        }
        return (MsgPlaceholderNode)this.substUnitInfo.varNameToRepNodeMap.get((Object)placeholderName);
    }

    public String getPlaceholderName(MsgPlaceholderNode placeholderNode) {
        if (this.substUnitInfo == null) {
            this.substUnitInfo = MsgNode.genSubstUnitInfo(this);
        }
        return (String)this.substUnitInfo.nodeToVarNameMap.get((Object)placeholderNode);
    }

    public MsgPluralNode getRepPluralNode(String pluralVarName) {
        if (this.substUnitInfo == null) {
            this.substUnitInfo = MsgNode.genSubstUnitInfo(this);
        }
        return (MsgPluralNode)this.substUnitInfo.varNameToRepNodeMap.get((Object)pluralVarName);
    }

    public String getPluralVarName(MsgPluralNode pluralNode) {
        if (this.substUnitInfo == null) {
            this.substUnitInfo = MsgNode.genSubstUnitInfo(this);
        }
        return (String)this.substUnitInfo.nodeToVarNameMap.get((Object)pluralNode);
    }

    public MsgSelectNode getRepSelectNode(String selectVarName) {
        if (this.substUnitInfo == null) {
            this.substUnitInfo = MsgNode.genSubstUnitInfo(this);
        }
        return (MsgSelectNode)this.substUnitInfo.varNameToRepNodeMap.get((Object)selectVarName);
    }

    public String getSelectVarName(MsgSelectNode selectNode) {
        if (this.substUnitInfo == null) {
            this.substUnitInfo = MsgNode.genSubstUnitInfo(this);
        }
        return (String)this.substUnitInfo.nodeToVarNameMap.get((Object)selectNode);
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getTagString());
        this.appendSourceStringForChildren(sb);
        return sb.toString();
    }

    @Override
    public MsgNode clone() {
        return new MsgNode(this);
    }

    private static SubstUnitInfo genSubstUnitInfo(MsgNode msgNode) {
        return MsgNode.genFinalSubstUnitInfoMapsHelper(MsgNode.genPrelimSubstUnitInfoMapsHelper(msgNode));
    }

    private static Pair<ArrayListMultimap<String, SoyNode.MsgSubstUnitNode>, Map<SoyNode.MsgSubstUnitNode, SoyNode.MsgSubstUnitNode>> genPrelimSubstUnitInfoMapsHelper(MsgNode msgNode) {
        ArrayListMultimap baseNameToRepNodesMap = ArrayListMultimap.create();
        HashMap nonRepNodeToRepNodeMap = Maps.newHashMap();
        ArrayDeque<SoyNode.MsgSubstUnitNode> traversalQueue = new ArrayDeque<SoyNode.MsgSubstUnitNode>();
        for (Object child : msgNode.getChildren()) {
            if (!(child instanceof SoyNode.MsgSubstUnitNode)) continue;
            traversalQueue.add((SoyNode.MsgSubstUnitNode)child);
        }
        while (traversalQueue.size() > 0) {
            String baseName;
            SoyNode.MsgSubstUnitNode node = (SoyNode.MsgSubstUnitNode)traversalQueue.remove();
            if (node instanceof MsgSelectNode || node instanceof MsgPluralNode) {
                for (CaseOrDefaultNode child : ((SoyNode.ParentSoyNode)((Object)node)).getChildren()) {
                    for (SoyNode grandchild : child.getChildren()) {
                        if (!(grandchild instanceof SoyNode.MsgSubstUnitNode)) continue;
                        traversalQueue.add((SoyNode.MsgSubstUnitNode)grandchild);
                    }
                }
            }
            if (!baseNameToRepNodesMap.containsKey((Object)(baseName = node.getBaseVarName()))) {
                baseNameToRepNodesMap.put((Object)baseName, (Object)node);
                continue;
            }
            boolean isNew = true;
            for (SoyNode.MsgSubstUnitNode other : baseNameToRepNodesMap.get((Object)baseName)) {
                if (!node.shouldUseSameVarNameAs(other)) continue;
                nonRepNodeToRepNodeMap.put(node, other);
                isNew = false;
                break;
            }
            if (!isNew) continue;
            baseNameToRepNodesMap.put((Object)baseName, (Object)node);
        }
        return Pair.of(baseNameToRepNodesMap, nonRepNodeToRepNodeMap);
    }

    private static SubstUnitInfo genFinalSubstUnitInfoMapsHelper(Pair<ArrayListMultimap<String, SoyNode.MsgSubstUnitNode>, Map<SoyNode.MsgSubstUnitNode, SoyNode.MsgSubstUnitNode>> prelimMaps) {
        ArrayListMultimap baseNameToRepNodesMap = (ArrayListMultimap)prelimMaps.first;
        Map nonRepNodeToRepNodeMap = (Map)prelimMaps.second;
        HashMap substUnitVarNameToRepNodeMap = Maps.newHashMap();
        for (String baseName : baseNameToRepNodesMap.keys()) {
            List nodesWithSameBaseName = baseNameToRepNodesMap.get((Object)baseName);
            if (nodesWithSameBaseName.size() == 1) {
                substUnitVarNameToRepNodeMap.put(baseName, nodesWithSameBaseName.get(0));
                continue;
            }
            int nextSuffix = 1;
            for (SoyNode.MsgSubstUnitNode repNode : nodesWithSameBaseName) {
                String newName;
                do {
                    newName = baseName + "_" + nextSuffix;
                    ++nextSuffix;
                } while (baseNameToRepNodesMap.containsKey((Object)newName));
                substUnitVarNameToRepNodeMap.put(newName, repNode);
            }
        }
        HashMap substUnitNodeToVarNameMap = Maps.newHashMap();
        for (Map.Entry entry : substUnitVarNameToRepNodeMap.entrySet()) {
            substUnitNodeToVarNameMap.put(entry.getValue(), entry.getKey());
        }
        for (Map.Entry entry : nonRepNodeToRepNodeMap.entrySet()) {
            SoyNode.MsgSubstUnitNode nonRepNode = (SoyNode.MsgSubstUnitNode)entry.getKey();
            SoyNode.MsgSubstUnitNode repNode = (SoyNode.MsgSubstUnitNode)entry.getValue();
            substUnitNodeToVarNameMap.put(nonRepNode, substUnitNodeToVarNameMap.get(repNode));
        }
        return new SubstUnitInfo((ImmutableMap<String, SoyNode.MsgSubstUnitNode>)ImmutableMap.copyOf((Map)substUnitVarNameToRepNodeMap), (ImmutableMap<SoyNode.MsgSubstUnitNode, String>)ImmutableMap.copyOf((Map)substUnitNodeToVarNameMap));
    }

    private static class SubstUnitInfo {
        public final ImmutableMap<String, SoyNode.MsgSubstUnitNode> varNameToRepNodeMap;
        public final ImmutableMap<SoyNode.MsgSubstUnitNode, String> nodeToVarNameMap;

        public SubstUnitInfo(ImmutableMap<String, SoyNode.MsgSubstUnitNode> varNameToRepNodeMap, ImmutableMap<SoyNode.MsgSubstUnitNode, String> nodeToVarNameMap) {
            this.varNameToRepNodeMap = varNameToRepNodeMap;
            this.nodeToVarNameMap = nodeToVarNameMap;
        }
    }
}

