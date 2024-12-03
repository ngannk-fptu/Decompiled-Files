/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.Checker;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ExposeTypeMunger;
import org.aspectj.weaver.PrivilegedAccessMunger;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.DeclareErrorOrWarning;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.DeclarePrecedence;
import org.aspectj.weaver.patterns.DeclareSoft;
import org.aspectj.weaver.patterns.DeclareTypeErrorOrWarning;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.PointcutRewriter;

public class CrosscuttingMembers {
    private final ResolvedType inAspect;
    private final World world;
    private PerClause perClause;
    private List<ShadowMunger> shadowMungers = new ArrayList<ShadowMunger>(4);
    private List<ConcreteTypeMunger> typeMungers = new ArrayList<ConcreteTypeMunger>(4);
    private List<ConcreteTypeMunger> lateTypeMungers = new ArrayList<ConcreteTypeMunger>(0);
    private Set<DeclareParents> declareParents = new HashSet<DeclareParents>();
    private Set<DeclareSoft> declareSofts = new HashSet<DeclareSoft>();
    private List<Declare> declareDominates = new ArrayList<Declare>(4);
    private Set<DeclareAnnotation> declareAnnotationsOnType = new LinkedHashSet<DeclareAnnotation>();
    private Set<DeclareAnnotation> declareAnnotationsOnField = new LinkedHashSet<DeclareAnnotation>();
    private Set<DeclareAnnotation> declareAnnotationsOnMethods = new LinkedHashSet<DeclareAnnotation>();
    private Set<DeclareTypeErrorOrWarning> declareTypeEow = new HashSet<DeclareTypeErrorOrWarning>();
    private boolean shouldConcretizeIfNeeded = true;
    private final Hashtable<String, Object> cflowFields = new Hashtable();
    private final Hashtable<String, Object> cflowBelowFields = new Hashtable();

    public CrosscuttingMembers(ResolvedType inAspect, boolean shouldConcretizeIfNeeded) {
        this.inAspect = inAspect;
        this.world = inAspect.getWorld();
        this.shouldConcretizeIfNeeded = shouldConcretizeIfNeeded;
    }

    public void addConcreteShadowMunger(ShadowMunger m) {
        this.shadowMungers.add(m);
    }

    public void addShadowMungers(Collection<ShadowMunger> c) {
        for (ShadowMunger munger : c) {
            this.addShadowMunger(munger);
        }
    }

    private void addShadowMunger(ShadowMunger m) {
        if (this.inAspect.isAbstract()) {
            return;
        }
        this.addConcreteShadowMunger(m.concretize(this.inAspect, this.world, this.perClause));
    }

    public void addTypeMungers(Collection<ConcreteTypeMunger> c) {
        this.typeMungers.addAll(c);
    }

    public void addTypeMunger(ConcreteTypeMunger m) {
        if (m == null) {
            throw new Error("FIXME AV - should not happen or what ?");
        }
        this.typeMungers.add(m);
    }

    public void addLateTypeMungers(Collection<ConcreteTypeMunger> c) {
        this.lateTypeMungers.addAll(c);
    }

    public void addLateTypeMunger(ConcreteTypeMunger m) {
        this.lateTypeMungers.add(m);
    }

    public void addDeclares(Collection<Declare> declares) {
        for (Declare declare : declares) {
            this.addDeclare(declare);
        }
    }

    public void addDeclare(Declare declare) {
        if (declare instanceof DeclareErrorOrWarning) {
            Checker m = new Checker((DeclareErrorOrWarning)declare);
            m.setDeclaringType(declare.getDeclaringType());
            this.addShadowMunger(m);
        } else if (declare instanceof DeclarePrecedence) {
            this.declareDominates.add(declare);
        } else if (declare instanceof DeclareParents) {
            DeclareParents dp = (DeclareParents)declare;
            this.exposeTypes(dp.getParents().getExactTypes());
            this.declareParents.add(dp);
        } else if (declare instanceof DeclareSoft) {
            Pointcut concretePointcut;
            DeclareSoft d = (DeclareSoft)declare;
            Advice m = Advice.makeSoftener(this.world, d.getPointcut(), d.getException(), this.inAspect, d);
            m.setDeclaringType(d.getDeclaringType());
            m.pointcut = concretePointcut = d.getPointcut().concretize(this.inAspect, d.getDeclaringType(), 0, m);
            this.declareSofts.add(new DeclareSoft(d.getException(), concretePointcut));
            this.addConcreteShadowMunger(m);
        } else if (declare instanceof DeclareAnnotation) {
            DeclareAnnotation da = (DeclareAnnotation)declare;
            if (da.getAspect() == null) {
                da.setAspect(this.inAspect);
            }
            if (da.isDeclareAtType()) {
                this.declareAnnotationsOnType.add(da);
            } else if (da.isDeclareAtField()) {
                this.declareAnnotationsOnField.add(da);
            } else if (da.isDeclareAtMethod() || da.isDeclareAtConstuctor()) {
                this.declareAnnotationsOnMethods.add(da);
            }
        } else if (declare instanceof DeclareTypeErrorOrWarning) {
            this.declareTypeEow.add((DeclareTypeErrorOrWarning)declare);
        } else {
            throw new RuntimeException("unimplemented");
        }
    }

    public void exposeTypes(List<UnresolvedType> typesToExpose) {
        for (UnresolvedType typeToExpose : typesToExpose) {
            this.exposeType(typeToExpose);
        }
    }

    public void exposeType(UnresolvedType typeToExpose) {
        if (ResolvedType.isMissing(typeToExpose)) {
            return;
        }
        if (typeToExpose.isParameterizedType() || typeToExpose.isRawType()) {
            typeToExpose = typeToExpose instanceof ResolvedType ? ((ResolvedType)typeToExpose).getGenericType() : UnresolvedType.forSignature(typeToExpose.getErasureSignature());
        }
        String signatureToLookFor = typeToExpose.getSignature();
        for (ConcreteTypeMunger cTM : this.typeMungers) {
            String exposedType;
            ResolvedTypeMunger rTM = cTM.getMunger();
            if (rTM == null || !(rTM instanceof ExposeTypeMunger) || !(exposedType = ((ExposeTypeMunger)rTM).getExposedTypeSignature()).equals(signatureToLookFor)) continue;
            return;
        }
        this.addTypeMunger(this.world.getWeavingSupport().concreteTypeMunger(new ExposeTypeMunger(typeToExpose), this.inAspect));
    }

    public void addPrivilegedAccesses(Collection<ResolvedMember> accessedMembers) {
        int version = this.inAspect.getCompilerVersion();
        for (ResolvedMember member : accessedMembers) {
            ResolvedMember resolvedMember = this.world.resolve(member);
            if (resolvedMember == null) {
                resolvedMember = member;
                if (resolvedMember.hasBackingGenericMember()) {
                    resolvedMember = resolvedMember.getBackingGenericMember();
                }
            } else {
                UnresolvedType resolvedDeclaringType;
                UnresolvedType unresolvedDeclaringType = member.getDeclaringType().getRawType();
                if (!unresolvedDeclaringType.equals(resolvedDeclaringType = resolvedMember.getDeclaringType().getRawType())) {
                    resolvedMember = member;
                }
            }
            PrivilegedAccessMunger privilegedAccessMunger = new PrivilegedAccessMunger(resolvedMember, version >= 7);
            ConcreteTypeMunger concreteTypeMunger = this.world.getWeavingSupport().concreteTypeMunger(privilegedAccessMunger, this.inAspect);
            this.addTypeMunger(concreteTypeMunger);
        }
    }

    public Collection<ShadowMunger> getCflowEntries() {
        ArrayList<ShadowMunger> ret = new ArrayList<ShadowMunger>();
        for (ShadowMunger m : this.shadowMungers) {
            Advice a;
            if (!(m instanceof Advice) || !(a = (Advice)m).getKind().isCflow()) continue;
            ret.add(a);
        }
        return ret;
    }

    public boolean replaceWith(CrosscuttingMembers other, boolean careAboutShadowMungers) {
        boolean changed = false;
        if (careAboutShadowMungers && (this.perClause == null || !this.perClause.equals(other.perClause))) {
            changed = true;
            this.perClause = other.perClause;
        }
        if (careAboutShadowMungers) {
            HashSet<ShadowMunger> theseShadowMungers = new HashSet<ShadowMunger>();
            HashSet<ShadowMunger> theseInlinedAroundMungers = new HashSet<ShadowMunger>();
            for (ShadowMunger munger : this.shadowMungers) {
                if (munger instanceof Advice) {
                    Advice adviceMunger = (Advice)munger;
                    if (!this.world.isXnoInline() && adviceMunger.getKind().equals(AdviceKind.Around)) {
                        theseInlinedAroundMungers.add(adviceMunger);
                        continue;
                    }
                    theseShadowMungers.add(adviceMunger);
                    continue;
                }
                theseShadowMungers.add(munger);
            }
            HashSet<ShadowMunger> tempSet = new HashSet<ShadowMunger>();
            tempSet.addAll(other.shadowMungers);
            HashSet<ShadowMunger> otherShadowMungers = new HashSet<ShadowMunger>();
            HashSet<ShadowMunger> otherInlinedAroundMungers = new HashSet<ShadowMunger>();
            for (ShadowMunger munger : tempSet) {
                if (munger instanceof Advice) {
                    Advice adviceMunger = (Advice)munger;
                    if (!this.world.isXnoInline() && adviceMunger.getKind().equals(AdviceKind.Around)) {
                        otherInlinedAroundMungers.add(this.rewritePointcutInMunger(adviceMunger));
                        continue;
                    }
                    otherShadowMungers.add(this.rewritePointcutInMunger(adviceMunger));
                    continue;
                }
                otherShadowMungers.add(this.rewritePointcutInMunger(munger));
            }
            if (!theseShadowMungers.equals(otherShadowMungers)) {
                changed = true;
            }
            if (!this.equivalent(theseInlinedAroundMungers, otherInlinedAroundMungers)) {
                changed = true;
            }
            if (!changed) {
                for (ShadowMunger munger : this.shadowMungers) {
                    int i = other.shadowMungers.indexOf(munger);
                    ShadowMunger shadowMunger = other.shadowMungers.get(i);
                    if (!(munger instanceof Advice)) continue;
                    ((Advice)shadowMunger).setHasMatchedSomething(((Advice)munger).hasMatchedSomething());
                }
            }
            this.shadowMungers = other.shadowMungers;
        }
        HashSet<ConcreteTypeMunger> theseTypeMungers = new HashSet<ConcreteTypeMunger>();
        HashSet<ConcreteTypeMunger> otherTypeMungers = new HashSet<ConcreteTypeMunger>();
        if (!careAboutShadowMungers) {
            ConcreteTypeMunger typeMunger;
            for (ConcreteTypeMunger o : this.typeMungers) {
                if (o instanceof ConcreteTypeMunger) {
                    typeMunger = o;
                    if (typeMunger.existsToSupportShadowMunging()) continue;
                    theseTypeMungers.add(typeMunger);
                    continue;
                }
                theseTypeMungers.add(o);
            }
            for (ConcreteTypeMunger o : other.typeMungers) {
                if (o instanceof ConcreteTypeMunger) {
                    typeMunger = o;
                    if (typeMunger.existsToSupportShadowMunging()) continue;
                    otherTypeMungers.add(typeMunger);
                    continue;
                }
                otherTypeMungers.add(o);
            }
        } else {
            theseTypeMungers.addAll(this.typeMungers);
            otherTypeMungers.addAll(other.typeMungers);
        }
        if (theseTypeMungers.size() != otherTypeMungers.size()) {
            changed = true;
            this.typeMungers = other.typeMungers;
        } else {
            boolean shouldOverwriteThis = false;
            boolean foundInequality = false;
            Iterator iter = theseTypeMungers.iterator();
            while (iter.hasNext() && !foundInequality) {
                Object thisOne = iter.next();
                boolean foundInOtherSet = false;
                for (Object e : otherTypeMungers) {
                    if (thisOne instanceof ConcreteTypeMunger && ((ConcreteTypeMunger)thisOne).shouldOverwrite()) {
                        shouldOverwriteThis = true;
                    }
                    if (thisOne instanceof ConcreteTypeMunger && e instanceof ConcreteTypeMunger) {
                        if (((ConcreteTypeMunger)thisOne).equivalentTo(e)) {
                            foundInOtherSet = true;
                            continue;
                        }
                        if (!thisOne.equals(e)) continue;
                        foundInOtherSet = true;
                        continue;
                    }
                    if (!thisOne.equals(e)) continue;
                    foundInOtherSet = true;
                }
                if (foundInOtherSet) continue;
                foundInequality = true;
            }
            if (foundInequality) {
                changed = true;
            }
            if (shouldOverwriteThis) {
                this.typeMungers = other.typeMungers;
            }
        }
        if (!this.lateTypeMungers.equals(other.lateTypeMungers)) {
            changed = true;
            this.lateTypeMungers = other.lateTypeMungers;
        }
        if (!this.declareDominates.equals(other.declareDominates)) {
            changed = true;
            this.declareDominates = other.declareDominates;
        }
        if (!this.declareParents.equals(other.declareParents)) {
            if (!careAboutShadowMungers) {
                HashSet<DeclareParents> trimmedThis = new HashSet<DeclareParents>();
                for (DeclareParents decp : this.declareParents) {
                    if (decp.isMixin()) continue;
                    trimmedThis.add(decp);
                }
                HashSet<DeclareParents> trimmedOther = new HashSet<DeclareParents>();
                for (DeclareParents decp : other.declareParents) {
                    if (decp.isMixin()) continue;
                    trimmedOther.add(decp);
                }
                if (!trimmedThis.equals(trimmedOther)) {
                    changed = true;
                    this.declareParents = other.declareParents;
                }
            } else {
                changed = true;
                this.declareParents = other.declareParents;
            }
        }
        if (!this.declareSofts.equals(other.declareSofts)) {
            changed = true;
            this.declareSofts = other.declareSofts;
        }
        if (!this.declareAnnotationsOnType.equals(other.declareAnnotationsOnType)) {
            changed = true;
            this.declareAnnotationsOnType = other.declareAnnotationsOnType;
        }
        if (!this.declareAnnotationsOnField.equals(other.declareAnnotationsOnField)) {
            changed = true;
            this.declareAnnotationsOnField = other.declareAnnotationsOnField;
        }
        if (!this.declareAnnotationsOnMethods.equals(other.declareAnnotationsOnMethods)) {
            changed = true;
            this.declareAnnotationsOnMethods = other.declareAnnotationsOnMethods;
        }
        if (!this.declareTypeEow.equals(other.declareTypeEow)) {
            changed = true;
            this.declareTypeEow = other.declareTypeEow;
        }
        return changed;
    }

    private boolean equivalent(Set<ShadowMunger> theseInlinedAroundMungers, Set<ShadowMunger> otherInlinedAroundMungers) {
        if (theseInlinedAroundMungers.size() != otherInlinedAroundMungers.size()) {
            return false;
        }
        for (Advice advice : theseInlinedAroundMungers) {
            boolean foundIt = false;
            for (Advice advice2 : otherInlinedAroundMungers) {
                if (!advice.equals(advice2)) continue;
                if (advice.getSignature() instanceof ResolvedMemberImpl && ((ResolvedMemberImpl)advice.getSignature()).isEquivalentTo(advice2.getSignature())) {
                    foundIt = true;
                    continue;
                }
                return false;
            }
            if (foundIt) continue;
            return false;
        }
        return true;
    }

    private ShadowMunger rewritePointcutInMunger(ShadowMunger munger) {
        PointcutRewriter pr = new PointcutRewriter();
        Pointcut p = munger.getPointcut();
        Pointcut newP = pr.rewrite(p);
        if (p.m_ignoreUnboundBindingForNames.length != 0) {
            newP.m_ignoreUnboundBindingForNames = p.m_ignoreUnboundBindingForNames;
        }
        munger.setPointcut(newP);
        return munger;
    }

    public void setPerClause(PerClause perClause) {
        this.perClause = this.shouldConcretizeIfNeeded ? perClause.concretize(this.inAspect) : perClause;
    }

    public List<Declare> getDeclareDominates() {
        return this.declareDominates;
    }

    public Collection<DeclareParents> getDeclareParents() {
        return this.declareParents;
    }

    public Collection<DeclareSoft> getDeclareSofts() {
        return this.declareSofts;
    }

    public List<ShadowMunger> getShadowMungers() {
        return this.shadowMungers;
    }

    public List<ConcreteTypeMunger> getTypeMungers() {
        return this.typeMungers;
    }

    public List<ConcreteTypeMunger> getLateTypeMungers() {
        return this.lateTypeMungers;
    }

    public Collection<DeclareAnnotation> getDeclareAnnotationOnTypes() {
        return this.declareAnnotationsOnType;
    }

    public Collection<DeclareAnnotation> getDeclareAnnotationOnFields() {
        return this.declareAnnotationsOnField;
    }

    public Collection<DeclareAnnotation> getDeclareAnnotationOnMethods() {
        return this.declareAnnotationsOnMethods;
    }

    public Collection<DeclareTypeErrorOrWarning> getDeclareTypeErrorOrWarning() {
        return this.declareTypeEow;
    }

    public Map<String, Object> getCflowBelowFields() {
        return this.cflowBelowFields;
    }

    public Map<String, Object> getCflowFields() {
        return this.cflowFields;
    }

    public void clearCaches() {
        this.cflowFields.clear();
        this.cflowBelowFields.clear();
    }
}

