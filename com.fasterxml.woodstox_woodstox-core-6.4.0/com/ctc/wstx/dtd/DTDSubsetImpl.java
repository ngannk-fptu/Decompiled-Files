/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.validation.ValidationContext
 *  org.codehaus.stax2.validation.XMLValidator
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.dtd.DTDElement;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.dtd.DTDTypingNonValidator;
import com.ctc.wstx.dtd.DTDValidator;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.PrefixedName;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidator;

public final class DTDSubsetImpl
extends DTDSubset {
    final boolean mIsCachable;
    final boolean mFullyValidating;
    final boolean mHasNsDefaults;
    final HashMap<String, EntityDecl> mGeneralEntities;
    volatile transient List<EntityDecl> mGeneralEntityList = null;
    final Set<String> mRefdGEs;
    final HashMap<String, EntityDecl> mDefinedPEs;
    final Set<String> mRefdPEs;
    final HashMap<String, NotationDeclaration> mNotations;
    transient List<NotationDeclaration> mNotationList = null;
    final HashMap<PrefixedName, DTDElement> mElements;

    private DTDSubsetImpl(boolean cachable, HashMap<String, EntityDecl> genEnt, Set<String> refdGEs, HashMap<String, EntityDecl> paramEnt, Set<String> peRefs, HashMap<String, NotationDeclaration> notations, HashMap<PrefixedName, DTDElement> elements, boolean fullyValidating) {
        this.mIsCachable = cachable;
        this.mGeneralEntities = genEnt;
        this.mRefdGEs = refdGEs;
        this.mDefinedPEs = paramEnt;
        this.mRefdPEs = peRefs;
        this.mNotations = notations;
        this.mElements = elements;
        this.mFullyValidating = fullyValidating;
        boolean anyNsDefs = false;
        if (elements != null) {
            for (DTDElement elem : elements.values()) {
                if (!elem.hasNsDefaults()) continue;
                anyNsDefs = true;
                break;
            }
        }
        this.mHasNsDefaults = anyNsDefs;
    }

    public static DTDSubsetImpl constructInstance(boolean cachable, HashMap<String, EntityDecl> genEnt, Set<String> refdGEs, HashMap<String, EntityDecl> paramEnt, Set<String> refdPEs, HashMap<String, NotationDeclaration> notations, HashMap<PrefixedName, DTDElement> elements, boolean fullyValidating) {
        return new DTDSubsetImpl(cachable, genEnt, refdGEs, paramEnt, refdPEs, notations, elements, fullyValidating);
    }

    @Override
    public DTDSubset combineWithExternalSubset(InputProblemReporter rep, DTDSubset extSubset) throws XMLStreamException {
        HashMap<String, EntityDecl> ge1 = this.getGeneralEntityMap();
        HashMap<String, EntityDecl> ge2 = extSubset.getGeneralEntityMap();
        if (ge1 == null || ge1.isEmpty()) {
            ge1 = ge2;
        } else if (ge2 != null && !ge2.isEmpty()) {
            DTDSubsetImpl.combineMaps(ge1, ge2);
        }
        HashMap<String, NotationDeclaration> n1 = this.getNotationMap();
        HashMap<String, NotationDeclaration> n2 = extSubset.getNotationMap();
        if (n1 == null || n1.isEmpty()) {
            n1 = n2;
        } else if (n2 != null && !n2.isEmpty()) {
            DTDSubsetImpl.checkNotations(n1, n2);
            DTDSubsetImpl.combineMaps(n1, n2);
        }
        HashMap<PrefixedName, DTDElement> e1 = this.getElementMap();
        HashMap<PrefixedName, DTDElement> e2 = extSubset.getElementMap();
        if (e1 == null || e1.isEmpty()) {
            e1 = e2;
        } else if (e2 != null && !e2.isEmpty()) {
            this.combineElements(rep, e1, e2);
        }
        return DTDSubsetImpl.constructInstance(false, ge1, null, null, null, n1, e1, this.mFullyValidating);
    }

    @Override
    public XMLValidator createValidator(ValidationContext ctxt) throws XMLStreamException {
        if (this.mFullyValidating) {
            return new DTDValidator(this, ctxt, this.mHasNsDefaults, this.getElementMap(), this.getGeneralEntityMap());
        }
        return new DTDTypingNonValidator(this, ctxt, this.mHasNsDefaults, this.getElementMap(), this.getGeneralEntityMap());
    }

    @Override
    public int getEntityCount() {
        return this.mGeneralEntities == null ? 0 : this.mGeneralEntities.size();
    }

    @Override
    public int getNotationCount() {
        return this.mNotations == null ? 0 : this.mNotations.size();
    }

    @Override
    public boolean isCachable() {
        return this.mIsCachable;
    }

    @Override
    public HashMap<String, EntityDecl> getGeneralEntityMap() {
        return this.mGeneralEntities;
    }

    @Override
    public List<EntityDecl> getGeneralEntityList() {
        List<EntityDecl> l = this.mGeneralEntityList;
        if (l == null) {
            l = this.mGeneralEntities == null || this.mGeneralEntities.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<EntityDecl>(this.mGeneralEntities.values()));
            this.mGeneralEntityList = l;
        }
        return l;
    }

    @Override
    public HashMap<String, EntityDecl> getParameterEntityMap() {
        return this.mDefinedPEs;
    }

    @Override
    public HashMap<String, NotationDeclaration> getNotationMap() {
        return this.mNotations;
    }

    @Override
    public synchronized List<NotationDeclaration> getNotationList() {
        List<NotationDeclaration> l = this.mNotationList;
        if (l == null) {
            l = this.mNotations == null || this.mNotations.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<NotationDeclaration>(this.mNotations.values()));
            this.mNotationList = l;
        }
        return l;
    }

    @Override
    public HashMap<PrefixedName, DTDElement> getElementMap() {
        return this.mElements;
    }

    @Override
    public boolean isReusableWith(DTDSubset intSubset) {
        HashMap<String, EntityDecl> intGEs;
        HashMap<String, EntityDecl> intPEs;
        Set<String> refdPEs = this.mRefdPEs;
        if (refdPEs != null && refdPEs.size() > 0 && (intPEs = intSubset.getParameterEntityMap()) != null && intPEs.size() > 0 && DataUtil.anyValuesInCommon(refdPEs, intPEs.keySet())) {
            return false;
        }
        Set<String> refdGEs = this.mRefdGEs;
        return refdGEs == null || refdGEs.size() <= 0 || (intGEs = intSubset.getGeneralEntityMap()) == null || intGEs.size() <= 0 || !DataUtil.anyValuesInCommon(refdGEs, intGEs.keySet());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[DTDSubset: ");
        int count = this.getEntityCount();
        sb.append(count);
        sb.append(" general entities");
        sb.append(']');
        return sb.toString();
    }

    public static void throwNotationException(NotationDeclaration oldDecl, NotationDeclaration newDecl) throws XMLStreamException {
        throw new WstxParsingException(MessageFormat.format(ErrorConsts.ERR_DTD_NOTATION_REDEFD, newDecl.getName(), oldDecl.getLocation().toString()), newDecl.getLocation());
    }

    public static void throwElementException(DTDElement oldElem, Location loc) throws XMLStreamException {
        throw new WstxParsingException(MessageFormat.format(ErrorConsts.ERR_DTD_ELEM_REDEFD, oldElem.getDisplayName(), oldElem.getLocation().toString()), loc);
    }

    private static <K, V> void combineMaps(Map<K, V> m1, Map<K, V> m2) {
        for (Map.Entry<K, V> me : m2.entrySet()) {
            K key = me.getKey();
            V old = m1.put(key, me.getValue());
            if (old == null) continue;
            m1.put(key, old);
        }
    }

    private void combineElements(InputProblemReporter rep, HashMap<PrefixedName, DTDElement> intElems, HashMap<PrefixedName, DTDElement> extElems) throws XMLStreamException {
        for (Map.Entry<PrefixedName, DTDElement> me : extElems.entrySet()) {
            PrefixedName key = me.getKey();
            DTDElement extElem = me.getValue();
            DTDElement intElem = intElems.get(key);
            if (intElem == null) {
                intElems.put(key, extElem);
                continue;
            }
            if (extElem.isDefined()) {
                if (intElem.isDefined()) {
                    DTDSubsetImpl.throwElementException(intElem, extElem.getLocation());
                    continue;
                }
                intElem.defineFrom(rep, extElem, this.mFullyValidating);
                continue;
            }
            if (!intElem.isDefined()) {
                rep.reportProblem(intElem.getLocation(), ErrorConsts.WT_ENT_DECL, ErrorConsts.W_UNDEFINED_ELEM, extElem.getDisplayName(), null);
                continue;
            }
            intElem.mergeMissingAttributesFrom(rep, extElem, this.mFullyValidating);
        }
    }

    private static void checkNotations(HashMap<String, NotationDeclaration> fromInt, HashMap<String, NotationDeclaration> fromExt) throws XMLStreamException {
        for (Map.Entry<String, NotationDeclaration> en : fromExt.entrySet()) {
            if (!fromInt.containsKey(en.getKey())) continue;
            DTDSubsetImpl.throwNotationException(fromInt.get(en.getKey()), en.getValue());
        }
    }
}

