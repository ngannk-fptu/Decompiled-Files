/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.Immutable;

public class TemplateRegistry {
    private final Map<String, TemplateBasicNode> basicTemplatesMap;
    private final Map<String, Set<TemplateDelegateNode.DelTemplateKey>> delTemplateNameToKeysMap;
    private final Map<TemplateDelegateNode.DelTemplateKey, List<DelegateTemplateDivision>> delTemplatesMap;

    public TemplateRegistry(SoyFileSetNode soyTree) {
        HashMap tempBasicTemplatesMap = Maps.newHashMap();
        HashMap tempDelTemplateNameToKeysMap = Maps.newHashMap();
        HashMap tempDelTemplatesMap = Maps.newHashMap();
        for (SoyFileNode soyFile : soyTree.getChildren()) {
            for (TemplateNode template : soyFile.getChildren()) {
                Map tempDivision;
                if (template instanceof TemplateBasicNode) {
                    tempBasicTemplatesMap.put(template.getTemplateName(), (TemplateBasicNode)template);
                    continue;
                }
                TemplateDelegateNode delTemplate = (TemplateDelegateNode)template;
                TemplateDelegateNode.DelTemplateKey delTemplateKey = delTemplate.getDelTemplateKey();
                String delTemplateName = delTemplate.getDelTemplateName();
                Set keys = (Set)tempDelTemplateNameToKeysMap.get(delTemplateName);
                if (keys == null) {
                    keys = Sets.newLinkedHashSet();
                    tempDelTemplateNameToKeysMap.put(delTemplateName, keys);
                }
                keys.add(delTemplateKey);
                int delPriority = delTemplate.getDelPriority();
                String delPackageName = delTemplate.getDelPackageName();
                Map tempDivisions = (Map)tempDelTemplatesMap.get(delTemplateKey);
                if (tempDivisions == null) {
                    tempDivisions = Maps.newHashMap();
                    tempDelTemplatesMap.put(delTemplateKey, tempDivisions);
                }
                if ((tempDivision = (Map)tempDivisions.get(delPriority)) == null) {
                    tempDivision = Maps.newHashMap();
                    tempDivisions.put(delPriority, tempDivision);
                }
                if (tempDivision.containsKey(delPackageName)) {
                    String errorMsgPrefix;
                    TemplateDelegateNode prevTemplate = (TemplateDelegateNode)tempDivision.get(delPackageName);
                    String prevTemplateFilePath = prevTemplate.getNearestAncestor(SoyFileNode.class).getFilePath();
                    String currTemplateFilePath = delTemplate.getNearestAncestor(SoyFileNode.class).getFilePath();
                    String string = errorMsgPrefix = delPackageName == null ? "Found two default implementations" : "Found two implementations in the same delegate package";
                    if (currTemplateFilePath != null && currTemplateFilePath.equals(prevTemplateFilePath)) {
                        throw SoySyntaxException.createWithoutMetaInfo(String.format(errorMsgPrefix + " for delegate template '%s', both in the file %s.", delTemplateKey, currTemplateFilePath));
                    }
                    throw SoySyntaxException.createWithoutMetaInfo(String.format(errorMsgPrefix + " for delegate template '%s', in files %s and %s.", delTemplateKey, prevTemplateFilePath, currTemplateFilePath));
                }
                tempDivision.put(delPackageName, delTemplate);
            }
        }
        this.basicTemplatesMap = Collections.unmodifiableMap(tempBasicTemplatesMap);
        ImmutableMap.Builder delTemplatesMapBuilder = ImmutableMap.builder();
        for (TemplateDelegateNode.DelTemplateKey delTemplateKey : tempDelTemplatesMap.keySet()) {
            Map tempDivisions = (Map)tempDelTemplatesMap.get(delTemplateKey);
            ImmutableList.Builder divisionsBuilder = ImmutableList.builder();
            for (int priority = 1; priority >= 0; --priority) {
                if (!tempDivisions.containsKey(priority)) continue;
                Map tempDivision = (Map)tempDivisions.get(priority);
                DelegateTemplateDivision division = new DelegateTemplateDivision(priority, tempDivision);
                divisionsBuilder.add((Object)division);
            }
            delTemplatesMapBuilder.put((Object)delTemplateKey, (Object)divisionsBuilder.build());
        }
        this.delTemplatesMap = delTemplatesMapBuilder.build();
        this.delTemplateNameToKeysMap = Collections.unmodifiableMap(tempDelTemplateNameToKeysMap);
    }

    public Map<String, TemplateBasicNode> getBasicTemplatesMap() {
        return this.basicTemplatesMap;
    }

    public TemplateBasicNode getBasicTemplate(String templateName) {
        return this.basicTemplatesMap.get(templateName);
    }

    public Map<String, Set<TemplateDelegateNode.DelTemplateKey>> getDelTemplateNameToKeysMap() {
        return this.delTemplateNameToKeysMap;
    }

    public Map<TemplateDelegateNode.DelTemplateKey, List<DelegateTemplateDivision>> getDelTemplatesMap() {
        return this.delTemplatesMap;
    }

    public Set<TemplateDelegateNode.DelTemplateKey> getDelTemplateKeysForAllVariants(String delTemplateName) {
        return this.delTemplateNameToKeysMap.get(delTemplateName);
    }

    public Set<DelegateTemplateDivision> getDelTemplateDivisionsForAllVariants(String delTemplateName) {
        Set<TemplateDelegateNode.DelTemplateKey> keysForAllVariants = this.delTemplateNameToKeysMap.get(delTemplateName);
        if (keysForAllVariants == null) {
            return null;
        }
        LinkedHashSet divisionsForAllVariants = Sets.newLinkedHashSet();
        for (TemplateDelegateNode.DelTemplateKey delTemplateKey : keysForAllVariants) {
            divisionsForAllVariants.addAll((Collection)this.delTemplatesMap.get(delTemplateKey));
        }
        return divisionsForAllVariants;
    }

    public List<DelegateTemplateDivision> getSortedDelTemplateDivisions(TemplateDelegateNode.DelTemplateKey delTemplateKey) {
        return this.delTemplatesMap.get(delTemplateKey);
    }

    public TemplateDelegateNode selectDelTemplate(TemplateDelegateNode.DelTemplateKey delTemplateKey, Set<String> activeDelPackageNames) throws DelegateTemplateConflictException {
        TemplateDelegateNode delTemplate = this.selectDelTemplateHelper(delTemplateKey, activeDelPackageNames);
        if (delTemplate == null && delTemplateKey.variant.length() > 0) {
            delTemplate = this.selectDelTemplateHelper(new TemplateDelegateNode.DelTemplateKey(delTemplateKey.name, ""), activeDelPackageNames);
        }
        return delTemplate;
    }

    private TemplateDelegateNode selectDelTemplateHelper(TemplateDelegateNode.DelTemplateKey delTemplateKey, Set<String> activeDelPackageNames) throws DelegateTemplateConflictException {
        List<DelegateTemplateDivision> divisions = this.delTemplatesMap.get(delTemplateKey);
        if (divisions == null) {
            return null;
        }
        for (DelegateTemplateDivision division : divisions) {
            TemplateDelegateNode delTemplate = null;
            for (String delPackageName : division.delPackageNameToDelTemplateMap.keySet()) {
                if (delPackageName != null && !activeDelPackageNames.contains(delPackageName)) continue;
                if (delTemplate != null) {
                    throw new DelegateTemplateConflictException(String.format("For delegate template '%s', found two active implementations with equal priority in delegate packages '%s' and '%s'.", delTemplateKey, delTemplate.getDelPackageName(), delPackageName));
                }
                delTemplate = division.delPackageNameToDelTemplateMap.get(delPackageName);
            }
            if (delTemplate == null) continue;
            return delTemplate;
        }
        return null;
    }

    public static class DelegateTemplateConflictException
    extends Exception {
        public DelegateTemplateConflictException(String errorMsg) {
            super(errorMsg);
        }
    }

    @Immutable
    public static class DelegateTemplateDivision {
        public final int delPriority;
        public final Map<String, TemplateDelegateNode> delPackageNameToDelTemplateMap;

        public DelegateTemplateDivision(int delPriority, Map<String, TemplateDelegateNode> delPackageNameToDelTemplateMap) {
            this.delPriority = delPriority;
            this.delPackageNameToDelTemplateMap = Collections.unmodifiableMap(Maps.newHashMap(delPackageNameToDelTemplateMap));
        }
    }
}

