/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.CrosscuttingMembers;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.PrivilegedAccessMunger;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.DeclareSoft;
import org.aspectj.weaver.patterns.DeclareTypeErrorOrWarning;
import org.aspectj.weaver.patterns.IVerificationRequired;

public class CrosscuttingMembersSet {
    private transient World world;
    private final Map<ResolvedType, CrosscuttingMembers> members = new HashMap<ResolvedType, CrosscuttingMembers>();
    private transient List<IVerificationRequired> verificationList = null;
    private List<ShadowMunger> shadowMungers = null;
    private List<ConcreteTypeMunger> typeMungers = null;
    private List<ConcreteTypeMunger> lateTypeMungers = null;
    private List<DeclareSoft> declareSofts = null;
    private List<DeclareParents> declareParents = null;
    private List<DeclareAnnotation> declareAnnotationOnTypes = null;
    private List<DeclareAnnotation> declareAnnotationOnFields = null;
    private List<DeclareAnnotation> declareAnnotationOnMethods = null;
    private List<DeclareTypeErrorOrWarning> declareTypeEows = null;
    private List<Declare> declareDominates = null;
    private boolean changedSinceLastReset = false;
    public int serializationVersion = 1;

    public CrosscuttingMembersSet(World world) {
        this.world = world;
    }

    public boolean addOrReplaceAspect(ResolvedType aspectType) {
        return this.addOrReplaceAspect(aspectType, true);
    }

    private boolean excludeDueToParentAspectHavingUnresolvedDependency(ResolvedType aspectType) {
        boolean excludeDueToParent = false;
        for (ResolvedType parent = aspectType.getSuperclass(); parent != null; parent = parent.getSuperclass()) {
            if (!parent.isAspect() || !parent.isAbstract() || !this.world.hasUnsatisfiedDependency(parent)) continue;
            if (!this.world.getMessageHandler().isIgnoring(IMessage.INFO)) {
                this.world.getMessageHandler().handleMessage(MessageUtil.info("deactivating aspect '" + aspectType.getName() + "' as the parent aspect '" + parent.getName() + "' has unsatisfied dependencies"));
            }
            excludeDueToParent = true;
        }
        return excludeDueToParent;
    }

    public boolean addOrReplaceAspect(ResolvedType aspectType, boolean inWeavingPhase) {
        if (!this.world.isAspectIncluded(aspectType)) {
            return false;
        }
        if (this.world.hasUnsatisfiedDependency(aspectType)) {
            return false;
        }
        if (this.excludeDueToParentAspectHavingUnresolvedDependency(aspectType)) {
            return false;
        }
        boolean change = false;
        CrosscuttingMembers xcut = this.members.get(aspectType);
        if (xcut == null) {
            this.members.put(aspectType, aspectType.collectCrosscuttingMembers(inWeavingPhase));
            this.clearCaches();
            change = true;
        } else if (xcut.replaceWith(aspectType.collectCrosscuttingMembers(inWeavingPhase), inWeavingPhase)) {
            this.clearCaches();
            change = true;
        } else {
            if (inWeavingPhase) {
                this.shadowMungers = null;
            }
            change = false;
        }
        if (aspectType.isAbstract()) {
            boolean ancestorChange = this.addOrReplaceDescendantsOf(aspectType, inWeavingPhase);
            change = change || ancestorChange;
        }
        this.changedSinceLastReset = this.changedSinceLastReset || change;
        return change;
    }

    private boolean addOrReplaceDescendantsOf(ResolvedType aspectType, boolean inWeavePhase) {
        Set<ResolvedType> knownAspects = this.members.keySet();
        HashSet<ResolvedType> toBeReplaced = new HashSet<ResolvedType>();
        for (ResolvedType candidateDescendant : knownAspects) {
            if (candidateDescendant == aspectType || !aspectType.isAssignableFrom(candidateDescendant, true)) continue;
            toBeReplaced.add(candidateDescendant);
        }
        boolean change = false;
        for (ResolvedType next : toBeReplaced) {
            boolean thisChange = this.addOrReplaceAspect(next, inWeavePhase);
            change = change || thisChange;
        }
        return change;
    }

    public void addAdviceLikeDeclares(ResolvedType aspectType) {
        if (!this.members.containsKey(aspectType)) {
            return;
        }
        CrosscuttingMembers xcut = this.members.get(aspectType);
        xcut.addDeclares(aspectType.collectDeclares(true));
    }

    public boolean deleteAspect(UnresolvedType aspectType) {
        boolean isAspect = this.members.remove(aspectType) != null;
        this.clearCaches();
        return isAspect;
    }

    public boolean containsAspect(UnresolvedType aspectType) {
        return this.members.containsKey(aspectType);
    }

    public void addFixedCrosscuttingMembers(ResolvedType aspectType) {
        this.members.put(aspectType, aspectType.crosscuttingMembers);
        this.clearCaches();
    }

    private void clearCaches() {
        this.shadowMungers = null;
        this.typeMungers = null;
        this.lateTypeMungers = null;
        this.declareSofts = null;
        this.declareParents = null;
        this.declareAnnotationOnFields = null;
        this.declareAnnotationOnMethods = null;
        this.declareAnnotationOnTypes = null;
        this.declareDominates = null;
    }

    public List<ShadowMunger> getShadowMungers() {
        if (this.shadowMungers == null) {
            ArrayList<ShadowMunger> ret = new ArrayList<ShadowMunger>();
            Iterator<CrosscuttingMembers> i = this.members.values().iterator();
            while (i.hasNext()) {
                ret.addAll(i.next().getShadowMungers());
            }
            this.shadowMungers = ret;
        }
        return this.shadowMungers;
    }

    public List<ConcreteTypeMunger> getTypeMungers() {
        if (this.typeMungers == null) {
            ArrayList<ConcreteTypeMunger> ret = new ArrayList<ConcreteTypeMunger>();
            for (CrosscuttingMembers xmembers : this.members.values()) {
                for (ConcreteTypeMunger mungerToAdd : xmembers.getTypeMungers()) {
                    ResolvedTypeMunger resolvedMungerToAdd = mungerToAdd.getMunger();
                    if (this.isNewStylePrivilegedAccessMunger(resolvedMungerToAdd)) {
                        String newFieldName = resolvedMungerToAdd.getSignature().getName();
                        boolean alreadyExists = false;
                        for (ConcreteTypeMunger existingMunger : ret) {
                            String existingFieldName;
                            ResolvedTypeMunger existing = existingMunger.getMunger();
                            if (!this.isNewStylePrivilegedAccessMunger(existing) || !(existingFieldName = existing.getSignature().getName()).equals(newFieldName) || !existing.getSignature().getDeclaringType().equals(resolvedMungerToAdd.getSignature().getDeclaringType())) continue;
                            alreadyExists = true;
                            break;
                        }
                        if (alreadyExists) continue;
                        ret.add(mungerToAdd);
                        continue;
                    }
                    ret.add(mungerToAdd);
                }
            }
            this.typeMungers = ret;
        }
        return this.typeMungers;
    }

    public List<ConcreteTypeMunger> getTypeMungersOfKind(ResolvedTypeMunger.Kind kind) {
        ArrayList<ConcreteTypeMunger> collected = null;
        for (ConcreteTypeMunger typeMunger : this.typeMungers) {
            if (typeMunger.getMunger() == null || typeMunger.getMunger().getKind() != kind) continue;
            if (collected == null) {
                collected = new ArrayList<ConcreteTypeMunger>();
            }
            collected.add(typeMunger);
        }
        if (collected == null) {
            return Collections.emptyList();
        }
        return collected;
    }

    private boolean isNewStylePrivilegedAccessMunger(ResolvedTypeMunger typeMunger) {
        boolean b;
        boolean bl = b = typeMunger != null && typeMunger.getKind() == ResolvedTypeMunger.PrivilegedAccess && typeMunger.getSignature().getKind() == Member.FIELD;
        if (!b) {
            return b;
        }
        PrivilegedAccessMunger privAccessMunger = (PrivilegedAccessMunger)typeMunger;
        return privAccessMunger.shortSyntax;
    }

    public List<ConcreteTypeMunger> getLateTypeMungers() {
        if (this.lateTypeMungers == null) {
            ArrayList<ConcreteTypeMunger> ret = new ArrayList<ConcreteTypeMunger>();
            Iterator<CrosscuttingMembers> i = this.members.values().iterator();
            while (i.hasNext()) {
                ret.addAll(i.next().getLateTypeMungers());
            }
            this.lateTypeMungers = ret;
        }
        return this.lateTypeMungers;
    }

    public List<DeclareSoft> getDeclareSofts() {
        if (this.declareSofts == null) {
            HashSet<DeclareSoft> ret = new HashSet<DeclareSoft>();
            Iterator<CrosscuttingMembers> i = this.members.values().iterator();
            while (i.hasNext()) {
                ret.addAll(i.next().getDeclareSofts());
            }
            this.declareSofts = new ArrayList<DeclareSoft>();
            this.declareSofts.addAll(ret);
        }
        return this.declareSofts;
    }

    public List<DeclareParents> getDeclareParents() {
        if (this.declareParents == null) {
            HashSet<DeclareParents> ret = new HashSet<DeclareParents>();
            Iterator<CrosscuttingMembers> i = this.members.values().iterator();
            while (i.hasNext()) {
                ret.addAll(i.next().getDeclareParents());
            }
            this.declareParents = new ArrayList<DeclareParents>();
            this.declareParents.addAll(ret);
        }
        return this.declareParents;
    }

    public List<DeclareAnnotation> getDeclareAnnotationOnTypes() {
        if (this.declareAnnotationOnTypes == null) {
            LinkedHashSet<DeclareAnnotation> ret = new LinkedHashSet<DeclareAnnotation>();
            Iterator<CrosscuttingMembers> i = this.members.values().iterator();
            while (i.hasNext()) {
                ret.addAll(i.next().getDeclareAnnotationOnTypes());
            }
            this.declareAnnotationOnTypes = new ArrayList<DeclareAnnotation>();
            this.declareAnnotationOnTypes.addAll(ret);
        }
        return this.declareAnnotationOnTypes;
    }

    public List<DeclareAnnotation> getDeclareAnnotationOnFields() {
        if (this.declareAnnotationOnFields == null) {
            LinkedHashSet<DeclareAnnotation> ret = new LinkedHashSet<DeclareAnnotation>();
            Iterator<CrosscuttingMembers> i = this.members.values().iterator();
            while (i.hasNext()) {
                ret.addAll(i.next().getDeclareAnnotationOnFields());
            }
            this.declareAnnotationOnFields = new ArrayList<DeclareAnnotation>();
            this.declareAnnotationOnFields.addAll(ret);
        }
        return this.declareAnnotationOnFields;
    }

    public List<DeclareAnnotation> getDeclareAnnotationOnMethods() {
        if (this.declareAnnotationOnMethods == null) {
            LinkedHashSet<DeclareAnnotation> ret = new LinkedHashSet<DeclareAnnotation>();
            Iterator<CrosscuttingMembers> i = this.members.values().iterator();
            while (i.hasNext()) {
                ret.addAll(i.next().getDeclareAnnotationOnMethods());
            }
            this.declareAnnotationOnMethods = new ArrayList<DeclareAnnotation>();
            this.declareAnnotationOnMethods.addAll(ret);
        }
        return this.declareAnnotationOnMethods;
    }

    public List<DeclareTypeErrorOrWarning> getDeclareTypeEows() {
        if (this.declareTypeEows == null) {
            HashSet<DeclareTypeErrorOrWarning> ret = new HashSet<DeclareTypeErrorOrWarning>();
            Iterator<CrosscuttingMembers> i = this.members.values().iterator();
            while (i.hasNext()) {
                ret.addAll(i.next().getDeclareTypeErrorOrWarning());
            }
            this.declareTypeEows = new ArrayList<DeclareTypeErrorOrWarning>();
            this.declareTypeEows.addAll(ret);
        }
        return this.declareTypeEows;
    }

    public List<Declare> getDeclareDominates() {
        if (this.declareDominates == null) {
            ArrayList<Declare> ret = new ArrayList<Declare>();
            Iterator<CrosscuttingMembers> i = this.members.values().iterator();
            while (i.hasNext()) {
                ret.addAll(i.next().getDeclareDominates());
            }
            this.declareDominates = ret;
        }
        return this.declareDominates;
    }

    public ResolvedType findAspectDeclaringParents(DeclareParents p) {
        Set<ResolvedType> keys = this.members.keySet();
        for (ResolvedType element : keys) {
            for (DeclareParents dp : this.members.get(element).getDeclareParents()) {
                if (!dp.equals(p)) continue;
                return element;
            }
        }
        return null;
    }

    public void reset() {
        this.verificationList = null;
        this.changedSinceLastReset = false;
    }

    public boolean hasChangedSinceLastReset() {
        return this.changedSinceLastReset;
    }

    public void recordNecessaryCheck(IVerificationRequired verification) {
        if (this.verificationList == null) {
            this.verificationList = new ArrayList<IVerificationRequired>();
        }
        this.verificationList.add(verification);
    }

    public void verify() {
        if (this.verificationList == null) {
            return;
        }
        for (IVerificationRequired element : this.verificationList) {
            element.verify();
        }
        this.verificationList = null;
    }

    public void write(CompressingDataOutputStream stream) throws IOException {
        stream.writeInt(this.shadowMungers.size());
        for (ShadowMunger shadowMunger : this.shadowMungers) {
            shadowMunger.write(stream);
        }
    }
}

