/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.soytree;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.exprtree.DataAccessNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FieldAccessNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.ItemAccessNode;
import com.google.template.soy.exprtree.VarRefNode;
import java.util.ArrayList;
import java.util.List;

public class MsgSubstUnitBaseVarNameUtils {
    private MsgSubstUnitBaseVarNameUtils() {
    }

    public static String genNaiveBaseNameForExpr(ExprRootNode<?> exprRoot, String fallbackBaseName) {
        Node exprNode = exprRoot.getChild(0);
        if (exprNode instanceof VarRefNode) {
            return BaseUtils.convertToUpperUnderscore(((VarRefNode)exprNode).getName());
        }
        if (exprNode instanceof FieldAccessNode) {
            return BaseUtils.convertToUpperUnderscore(((FieldAccessNode)exprNode).getFieldName());
        }
        if (exprNode instanceof GlobalNode) {
            String globalName = ((GlobalNode)exprNode).getName();
            return BaseUtils.convertToUpperUnderscore(BaseUtils.extractPartAfterLastDot(globalName));
        }
        return fallbackBaseName;
    }

    public static String genShortestBaseNameForExpr(ExprRootNode<?> exprRoot, String fallbackBaseName) {
        List<String> candidateBaseNames = MsgSubstUnitBaseVarNameUtils.genCandidateBaseNamesForExpr(exprRoot);
        return candidateBaseNames.size() != 0 ? candidateBaseNames.get(0) : fallbackBaseName;
    }

    public static List<String> genNoncollidingBaseNamesForExprs(List<ExprRootNode<?>> exprRoots, String fallbackBaseName) throws SoySyntaxException {
        List candidateBaseNameList;
        int numExprs = exprRoots.size();
        ArrayList candidateBaseNameLists = Lists.newArrayListWithCapacity((int)numExprs);
        for (ExprRootNode<?> exprRoot : exprRoots) {
            candidateBaseNameLists.add(MsgSubstUnitBaseVarNameUtils.genCandidateBaseNamesForExpr(exprRoot));
        }
        HashMultimap collisionStrToLongestCandidatesMultimap = HashMultimap.create();
        for (int i = 0; i < numExprs; ++i) {
            ExprRootNode<?> exprRoot = exprRoots.get(i);
            candidateBaseNameList = (List)candidateBaseNameLists.get(i);
            if (candidateBaseNameList.size() == 0) continue;
            String longestCandidate = (String)candidateBaseNameList.get(candidateBaseNameList.size() - 1);
            collisionStrToLongestCandidatesMultimap.put((Object)longestCandidate, exprRoot);
            int n = longestCandidate.length();
            for (int j = 0; j < n; ++j) {
                if (longestCandidate.charAt(j) != '_') continue;
                collisionStrToLongestCandidatesMultimap.put((Object)longestCandidate.substring(j + 1), exprRoot);
            }
        }
        ArrayList noncollidingBaseNames = Lists.newArrayListWithCapacity((int)numExprs);
        block3: for (int i = 0; i < numExprs; ++i) {
            candidateBaseNameList = (List)candidateBaseNameLists.get(i);
            if (candidateBaseNameList.size() != 0) {
                for (String candidateBaseName : candidateBaseNameList) {
                    if (collisionStrToLongestCandidatesMultimap.get((Object)candidateBaseName).size() != 1) continue;
                    noncollidingBaseNames.add(candidateBaseName);
                    continue block3;
                }
                ExprRootNode<?> exprRoot = exprRoots.get(i);
                String longestCandidate = (String)candidateBaseNameList.get(candidateBaseNameList.size() - 1);
                ExprRootNode collidingExprRoot = null;
                for (ExprRootNode er : collisionStrToLongestCandidatesMultimap.get((Object)longestCandidate)) {
                    if (er == exprRoot) continue;
                    collidingExprRoot = er;
                    break;
                }
                assert (collidingExprRoot != null);
                throw SoySyntaxException.createWithoutMetaInfo(String.format("Cannot generate noncolliding base names for msg placeholders and/or vars: found colliding expressions \"%s\" and \"%s\".", exprRoot.toSourceString(), collidingExprRoot.toSourceString()));
            }
            noncollidingBaseNames.add(fallbackBaseName);
        }
        return noncollidingBaseNames;
    }

    @VisibleForTesting
    static List<String> genCandidateBaseNamesForExpr(ExprRootNode<?> exprRoot) {
        Node exprNode = exprRoot.getChild(0);
        if (exprNode instanceof VarRefNode || exprNode instanceof DataAccessNode) {
            ArrayList baseNames = Lists.newArrayList();
            String baseName = null;
            while (exprNode != null) {
                String nameSegment = null;
                if (exprNode instanceof VarRefNode) {
                    nameSegment = ((VarRefNode)exprNode).getName();
                    exprNode = null;
                } else if (exprNode instanceof FieldAccessNode) {
                    FieldAccessNode fieldAccess = (FieldAccessNode)exprNode;
                    nameSegment = fieldAccess.getFieldName();
                    exprNode = fieldAccess.getBaseExprChild();
                } else {
                    IntegerNode keyValue;
                    if (!(exprNode instanceof ItemAccessNode)) break;
                    ItemAccessNode itemAccess = (ItemAccessNode)exprNode;
                    exprNode = itemAccess.getBaseExprChild();
                    if (!(itemAccess.getKeyExprChild() instanceof IntegerNode) || (keyValue = (IntegerNode)itemAccess.getKeyExprChild()).getValue() < 0) break;
                    nameSegment = Integer.toString(keyValue.getValue());
                    baseName = BaseUtils.convertToUpperUnderscore(nameSegment) + (baseName != null ? "_" + baseName : "");
                    continue;
                }
                baseName = BaseUtils.convertToUpperUnderscore(nameSegment) + (baseName != null ? "_" + baseName : "");
                baseNames.add(baseName);
            }
            return baseNames;
        }
        if (exprNode instanceof GlobalNode) {
            String[] globalNameParts = ((GlobalNode)exprNode).getName().split("\\.");
            ArrayList baseNames = Lists.newArrayList();
            String baseName = null;
            for (int i = globalNameParts.length - 1; i >= 0; --i) {
                baseName = BaseUtils.convertToUpperUnderscore(globalNameParts[i]) + (baseName != null ? "_" + baseName : "");
                baseNames.add(baseName);
            }
            return baseNames;
        }
        return ImmutableList.of();
    }
}

