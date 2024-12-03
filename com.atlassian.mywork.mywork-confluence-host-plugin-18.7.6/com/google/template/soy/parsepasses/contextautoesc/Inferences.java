/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.parsepasses.contextautoesc.Context;
import com.google.template.soy.parsepasses.contextautoesc.EscapingMode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoytreeUtils;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateBasicNodeBuilder;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateDelegateNodeBuilder;
import com.google.template.soy.soytree.TemplateNode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class Inferences {
    @Nullable
    private final Inferences parent;
    private final ImmutableSet<String> autoescapeCancellingDirectives;
    private final IdGenerator idGen;
    private final Map<String, List<TemplateNode>> templatesByName = Maps.newLinkedHashMap();
    private final Map<String, Context> templateNameToEndContext = Maps.newLinkedHashMap();
    private final Map<Integer, Context> idToStartContext = Maps.newLinkedHashMap();
    private final Map<Integer, ImmutableList<EscapingMode>> idToEscapingModes = Maps.newLinkedHashMap();
    private final Map<Integer, String> callIdToDerivedCalleeName = Maps.newLinkedHashMap();
    private final Set<String> templatesChecked = Sets.newHashSet();

    public Inferences(Inferences parent) {
        this.parent = parent;
        this.autoescapeCancellingDirectives = parent.autoescapeCancellingDirectives;
        this.idGen = parent.idGen;
    }

    public Inferences(Set<String> autoescapeCancellingDirectives, IdGenerator idGen, Map<String, ImmutableList<TemplateNode>> templatesByName) {
        this.parent = null;
        this.autoescapeCancellingDirectives = ImmutableSet.copyOf(autoescapeCancellingDirectives);
        this.idGen = idGen;
        this.templatesByName.putAll(templatesByName);
    }

    public void recordTemplateEndContext(String templateName, Context context) {
        this.templateNameToEndContext.put(templateName, context);
    }

    public List<TemplateNode> lookupTemplates(String templateName) {
        Inferences inferences = this;
        while (inferences != null) {
            List<TemplateNode> tn = inferences.templatesByName.get(templateName);
            if (tn != null) {
                return tn;
            }
            inferences = inferences.parent;
        }
        return null;
    }

    public Context getTemplateEndContext(String templateName) {
        Inferences inferences = this;
        while (inferences != null) {
            Context oc = inferences.templateNameToEndContext.get(templateName);
            if (oc != null) {
                return oc;
            }
            inferences = inferences.parent;
        }
        return null;
    }

    public ImmutableList<EscapingMode> getEscapingMode(PrintNode printNode) {
        int id = printNode.getId();
        Inferences inferences = this;
        while (inferences != null) {
            ImmutableList<EscapingMode> escapingModes = inferences.idToEscapingModes.get(id);
            if (escapingModes != null) {
                return escapingModes;
            }
            inferences = inferences.parent;
        }
        ImmutableList.Builder modes = ImmutableList.builder();
        for (PrintDirectiveNode directive : printNode.getChildren()) {
            String directiveName = directive.getName();
            EscapingMode dirMode = EscapingMode.fromDirective(directiveName);
            if (dirMode != null) {
                modes.add((Object)dirMode);
                continue;
            }
            if (!this.autoescapeCancellingDirectives.contains((Object)directiveName)) continue;
            modes.add((Object)EscapingMode.NO_AUTOESCAPE);
        }
        return modes.build();
    }

    public void setEscapingDirectives(SoyNode node, Context startContext, List<EscapingMode> escapingModes) {
        Preconditions.checkArgument((node instanceof PrintNode || node instanceof CallNode ? 1 : 0) != 0, (Object)"Escaping directives may only be set for {print} or {call} nodes");
        int id = node.getId();
        this.idToStartContext.put(id, startContext);
        if (escapingModes != null) {
            this.idToEscapingModes.put(id, (ImmutableList<EscapingMode>)ImmutableList.copyOf(escapingModes));
        }
    }

    public ImmutableList<EscapingMode> getEscapingModesForId(int nodeId) {
        ImmutableList modes = this.idToEscapingModes.get(nodeId);
        if (modes == null) {
            modes = ImmutableList.of();
        }
        return modes;
    }

    public ImmutableMap<Integer, Context> getPrintNodeStartContexts() {
        return ImmutableMap.copyOf(this.idToStartContext);
    }

    public void retargetCall(CallNode cn, String derivedCalleeName) {
        this.callIdToDerivedCalleeName.put(cn.getId(), derivedCalleeName);
    }

    public String getDerivedCalleeNameForCallId(int callId) {
        return this.callIdToDerivedCalleeName.get(callId);
    }

    public List<TemplateNode> cloneTemplates(String baseName, String derivedName) {
        if (this.lookupTemplates(derivedName) != null) {
            throw new AssertionError((Object)derivedName);
        }
        ImmutableList.Builder b = ImmutableList.builder();
        for (TemplateNode tn : this.lookupTemplates(baseName)) {
            TemplateNode clone;
            TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo = tn.getSoyFileHeaderInfo();
            int cloneId = tn.getNearestAncestor(SoyFileSetNode.class).getNodeIdGenerator().genId();
            boolean useAttrStyleForName = tn.getCommandText().contains("name=");
            if (tn instanceof TemplateBasicNode) {
                TemplateBasicNode tbn = (TemplateBasicNode)tn;
                String derivedPartialName = tn.getPartialTemplateName() != null ? derivedName.substring(soyFileHeaderInfo.namespace.length()) : null;
                clone = new TemplateBasicNodeBuilder(soyFileHeaderInfo).setId(cloneId).setCmdTextInfo(derivedName, derivedPartialName, useAttrStyleForName, tbn.isOverride(), tn.isPrivate(), tn.getAutoescapeMode(), tn.getContentKind(), tn.getRequiredCssNamespaces()).setSoyDoc(tn.getSoyDoc()).build();
                if (!derivedName.equals(clone.getTemplateName()) || !Objects.equal((Object)derivedPartialName, (Object)clone.getPartialTemplateName())) {
                    throw new AssertionError();
                }
            } else if (tn instanceof TemplateDelegateNode) {
                TemplateDelegateNode tdn = (TemplateDelegateNode)tn;
                clone = new TemplateDelegateNodeBuilder(soyFileHeaderInfo).setId(cloneId).setCmdTextInfo(derivedName, tdn.getDelTemplateVariant(), tdn.getDelPriority(), tn.getAutoescapeMode(), tn.getContentKind(), tn.getRequiredCssNamespaces()).setSoyDoc(tn.getSoyDoc()).build();
                if (!derivedName.equals(((TemplateDelegateNode)clone).getDelTemplateName())) {
                    throw new AssertionError();
                }
            } else {
                throw new AssertionError((Object)("Unknown template node type: " + tn.getClass()));
            }
            clone.setSourceLocation(tn.getSourceLocation());
            for (SoyNode.StandaloneNode child : tn.getChildren()) {
                clone.addChild(SoytreeUtils.cloneWithNewIds(child, this.idGen));
            }
            b.add((Object)clone);
        }
        ImmutableList clones = b.build();
        this.templatesByName.put(derivedName, (List<TemplateNode>)clones);
        return clones;
    }

    public void foldIntoParent() {
        this.parent.idToEscapingModes.putAll(this.idToEscapingModes);
        this.parent.idToStartContext.putAll(this.idToStartContext);
        this.parent.templateNameToEndContext.putAll(this.templateNameToEndContext);
        this.parent.callIdToDerivedCalleeName.putAll(this.callIdToDerivedCalleeName);
        this.parent.templatesByName.putAll(this.templatesByName);
        this.parent.templatesChecked.addAll(this.templatesChecked);
    }

    public List<TemplateNode> getAllTemplates() {
        ImmutableList.Builder b = ImmutableList.builder();
        for (List<TemplateNode> templates : this.templatesByName.values()) {
            b.addAll(templates);
        }
        return b.build();
    }

    public void recordTemplateChecked(String templateName) {
        this.templatesChecked.add(templateName);
    }

    public boolean wasTemplateChecked(String templateName) {
        return this.templatesChecked.contains(templateName);
    }

    public IdGenerator getIdGenerator() {
        return this.idGen;
    }
}

