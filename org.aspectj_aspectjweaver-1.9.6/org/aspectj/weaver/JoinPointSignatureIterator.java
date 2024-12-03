/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.aspectj.weaver.JoinPointSignature;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MissingResolvedTypeWithKnownSignature;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;

public class JoinPointSignatureIterator
implements Iterator<JoinPointSignature> {
    ResolvedType firstDefiningType;
    private Member signaturesOfMember;
    private ResolvedMember firstDefiningMember;
    private World world;
    private List<JoinPointSignature> discoveredSignatures = new ArrayList<JoinPointSignature>();
    private List<JoinPointSignature> additionalSignatures = Collections.emptyList();
    private Iterator<JoinPointSignature> discoveredSignaturesIterator = null;
    private Iterator<ResolvedType> superTypeIterator = null;
    private boolean isProxy = false;
    private Set<ResolvedType> visitedSuperTypes = new HashSet<ResolvedType>();
    private List<SearchPair> yetToBeProcessedSuperMembers = null;
    private boolean iteratingOverDiscoveredSignatures = true;
    private boolean couldBeFurtherAsYetUndiscoveredSignatures = true;
    private static final UnresolvedType jlrProxy = UnresolvedType.forSignature("Ljava/lang/reflect/Proxy;");

    public JoinPointSignatureIterator(Member joinPointSignature, World world) {
        this.signaturesOfMember = joinPointSignature;
        this.world = world;
        this.addSignaturesUpToFirstDefiningMember();
        if (!this.shouldWalkUpHierarchy()) {
            this.couldBeFurtherAsYetUndiscoveredSignatures = false;
        }
    }

    public void reset() {
        this.discoveredSignaturesIterator = this.discoveredSignatures.iterator();
        this.additionalSignatures.clear();
        this.iteratingOverDiscoveredSignatures = true;
    }

    @Override
    public boolean hasNext() {
        if (this.iteratingOverDiscoveredSignatures && this.discoveredSignaturesIterator.hasNext()) {
            return true;
        }
        if (this.couldBeFurtherAsYetUndiscoveredSignatures) {
            if (this.additionalSignatures.size() > 0) {
                return true;
            }
            return this.findSignaturesFromSupertypes();
        }
        return false;
    }

    @Override
    public JoinPointSignature next() {
        if (this.iteratingOverDiscoveredSignatures && this.discoveredSignaturesIterator.hasNext()) {
            return this.discoveredSignaturesIterator.next();
        }
        if (this.additionalSignatures.size() > 0) {
            return this.additionalSignatures.remove(0);
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("can't remove from JoinPointSignatureIterator");
    }

    private void addSignaturesUpToFirstDefiningMember() {
        ResolvedType originalDeclaringType = this.signaturesOfMember.getDeclaringType().resolve(this.world);
        ResolvedType superType = originalDeclaringType.getSuperclass();
        if (superType != null && superType.equals(jlrProxy)) {
            this.isProxy = true;
        }
        if (this.world.isJoinpointArrayConstructionEnabled() && originalDeclaringType.isArray()) {
            Member m = this.signaturesOfMember;
            ResolvedMemberImpl rm = new ResolvedMemberImpl(m.getKind(), m.getDeclaringType(), m.getModifiers(), m.getReturnType(), m.getName(), m.getParameterTypes());
            this.discoveredSignatures.add(new JoinPointSignature(rm, originalDeclaringType));
            this.couldBeFurtherAsYetUndiscoveredSignatures = false;
            return;
        }
        ResolvedMember resolvedMember = this.firstDefiningMember = this.signaturesOfMember instanceof ResolvedMember ? (ResolvedMember)this.signaturesOfMember : this.signaturesOfMember.resolve(this.world);
        if (this.firstDefiningMember == null) {
            this.couldBeFurtherAsYetUndiscoveredSignatures = false;
            return;
        }
        this.firstDefiningType = this.firstDefiningMember.getDeclaringType().resolve(this.world);
        if (this.firstDefiningType != originalDeclaringType && this.signaturesOfMember.getKind() == Member.CONSTRUCTOR) {
            return;
        }
        if (originalDeclaringType == this.firstDefiningType) {
            this.discoveredSignatures.add(new JoinPointSignature(this.firstDefiningMember, originalDeclaringType));
        } else {
            ArrayList<ResolvedType> declaringTypes = new ArrayList<ResolvedType>();
            this.accumulateTypesInBetween(originalDeclaringType, this.firstDefiningType, declaringTypes);
            for (ResolvedType declaringType : declaringTypes) {
                this.discoveredSignatures.add(new JoinPointSignature(this.firstDefiningMember, declaringType));
            }
        }
    }

    private void accumulateTypesInBetween(ResolvedType subType, ResolvedType superType, List<ResolvedType> types) {
        types.add(subType);
        if (subType == superType) {
            return;
        }
        Iterator<ResolvedType> iter = subType.getDirectSupertypes();
        while (iter.hasNext()) {
            ResolvedType parent = iter.next();
            if (!superType.isAssignableFrom(parent, true)) continue;
            this.accumulateTypesInBetween(parent, superType, types);
        }
    }

    private boolean shouldWalkUpHierarchy() {
        if (this.signaturesOfMember.getKind() == Member.CONSTRUCTOR) {
            return false;
        }
        if (this.signaturesOfMember.getKind() == Member.FIELD) {
            return false;
        }
        return !Modifier.isStatic(this.signaturesOfMember.getModifiers());
    }

    private boolean findSignaturesFromSupertypes() {
        this.iteratingOverDiscoveredSignatures = false;
        if (this.superTypeIterator == null) {
            this.superTypeIterator = this.firstDefiningType.getDirectSupertypes();
        }
        if (this.superTypeIterator.hasNext()) {
            ResolvedType superType = this.superTypeIterator.next();
            if (this.isProxy && (superType.isGenericType() || superType.isParameterizedType())) {
                superType = superType.getRawType();
            }
            if (this.visitedSuperTypes.contains(superType)) {
                return this.findSignaturesFromSupertypes();
            }
            this.visitedSuperTypes.add(superType);
            if (superType.isMissing()) {
                this.warnOnMissingType(superType);
                return this.findSignaturesFromSupertypes();
            }
            ResolvedMemberImpl foundMember = (ResolvedMemberImpl)superType.lookupResolvedMember(this.firstDefiningMember, true, this.isProxy);
            if (foundMember != null && this.isVisibleTo(this.firstDefiningMember, foundMember)) {
                ArrayList<ResolvedType> declaringTypes = new ArrayList<ResolvedType>();
                ResolvedType resolvedDeclaringType = foundMember.getDeclaringType().resolve(this.world);
                this.accumulateTypesInBetween(superType, resolvedDeclaringType, declaringTypes);
                for (ResolvedType declaringType : declaringTypes) {
                    JoinPointSignature member = null;
                    if (this.isProxy && (declaringType.isGenericType() || declaringType.isParameterizedType())) {
                        declaringType = declaringType.getRawType();
                    }
                    member = new JoinPointSignature(foundMember, declaringType);
                    this.discoveredSignatures.add(member);
                    if (this.additionalSignatures == Collections.EMPTY_LIST) {
                        this.additionalSignatures = new ArrayList<JoinPointSignature>();
                    }
                    this.additionalSignatures.add(member);
                }
                if (!this.isProxy && superType.isParameterizedType() && foundMember.backingGenericMember != null) {
                    JoinPointSignature member = new JoinPointSignature(foundMember.backingGenericMember, foundMember.declaringType.resolve(this.world));
                    this.discoveredSignatures.add(member);
                    if (this.additionalSignatures == Collections.EMPTY_LIST) {
                        this.additionalSignatures = new ArrayList<JoinPointSignature>();
                    }
                    this.additionalSignatures.add(member);
                }
                if (this.yetToBeProcessedSuperMembers == null) {
                    this.yetToBeProcessedSuperMembers = new ArrayList<SearchPair>();
                }
                this.yetToBeProcessedSuperMembers.add(new SearchPair(foundMember, superType));
                return true;
            }
            return this.findSignaturesFromSupertypes();
        }
        if (this.yetToBeProcessedSuperMembers != null && !this.yetToBeProcessedSuperMembers.isEmpty()) {
            SearchPair nextUp = this.yetToBeProcessedSuperMembers.remove(0);
            this.firstDefiningType = nextUp.type;
            this.firstDefiningMember = nextUp.member;
            this.superTypeIterator = null;
            return this.findSignaturesFromSupertypes();
        }
        this.couldBeFurtherAsYetUndiscoveredSignatures = false;
        return false;
    }

    private boolean isVisibleTo(ResolvedMember childMember, ResolvedMember parentMember) {
        if (childMember.getDeclaringType().equals(parentMember.getDeclaringType())) {
            return true;
        }
        return !Modifier.isPrivate(parentMember.getModifiers());
    }

    private void warnOnMissingType(ResolvedType missing) {
        if (missing instanceof MissingResolvedTypeWithKnownSignature) {
            MissingResolvedTypeWithKnownSignature mrt = (MissingResolvedTypeWithKnownSignature)missing;
            mrt.raiseWarningOnJoinPointSignature(this.signaturesOfMember.toString());
        }
    }

    private static class SearchPair {
        public ResolvedMember member;
        public ResolvedType type;

        public SearchPair(ResolvedMember member, ResolvedType type) {
            this.member = member;
            this.type = type;
        }
    }
}

