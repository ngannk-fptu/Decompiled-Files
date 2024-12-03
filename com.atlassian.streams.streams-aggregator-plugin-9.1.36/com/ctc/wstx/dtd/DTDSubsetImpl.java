/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.dtd.DTDElement;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.dtd.DTDTypingNonValidator;
import com.ctc.wstx.dtd.DTDValidator;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.DataUtil;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
    final HashMap mGeneralEntities;
    volatile transient List mGeneralEntityList = null;
    final Set mRefdGEs;
    final HashMap mDefinedPEs;
    final Set mRefdPEs;
    final HashMap mNotations;
    transient List mNotationList = null;
    final HashMap mElements;

    private DTDSubsetImpl(boolean cachable, HashMap genEnt, Set refdGEs, HashMap paramEnt, Set peRefs, HashMap notations, HashMap elements, boolean fullyValidating) {
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
            Iterator it = elements.values().iterator();
            while (it.hasNext()) {
                DTDElement elem = (DTDElement)it.next();
                if (!elem.hasNsDefaults()) continue;
                anyNsDefs = true;
                break;
            }
        }
        this.mHasNsDefaults = anyNsDefs;
    }

    public static DTDSubsetImpl constructInstance(boolean cachable, HashMap genEnt, Set refdGEs, HashMap paramEnt, Set refdPEs, HashMap notations, HashMap elements, boolean fullyValidating) {
        return new DTDSubsetImpl(cachable, genEnt, refdGEs, paramEnt, refdPEs, notations, elements, fullyValidating);
    }

    public DTDSubset combineWithExternalSubset(InputProblemReporter rep, DTDSubset extSubset) throws XMLStreamException {
        HashMap ge1 = this.getGeneralEntityMap();
        HashMap ge2 = extSubset.getGeneralEntityMap();
        if (ge1 == null || ge1.isEmpty()) {
            ge1 = ge2;
        } else if (ge2 != null && !ge2.isEmpty()) {
            DTDSubsetImpl.combineMaps(ge1, ge2);
        }
        HashMap n1 = this.getNotationMap();
        HashMap n2 = extSubset.getNotationMap();
        if (n1 == null || n1.isEmpty()) {
            n1 = n2;
        } else if (n2 != null && !n2.isEmpty()) {
            DTDSubsetImpl.checkNotations(n1, n2);
            DTDSubsetImpl.combineMaps(n1, n2);
        }
        HashMap e1 = this.getElementMap();
        HashMap e2 = extSubset.getElementMap();
        if (e1 == null || e1.isEmpty()) {
            e1 = e2;
        } else if (e2 != null && !e2.isEmpty()) {
            this.combineElements(rep, e1, e2);
        }
        return DTDSubsetImpl.constructInstance(false, ge1, null, null, null, n1, e1, this.mFullyValidating);
    }

    public XMLValidator createValidator(ValidationContext ctxt) throws XMLStreamException {
        if (this.mFullyValidating) {
            return new DTDValidator(this, ctxt, this.mHasNsDefaults, this.getElementMap(), this.getGeneralEntityMap());
        }
        return new DTDTypingNonValidator(this, ctxt, this.mHasNsDefaults, this.getElementMap(), this.getGeneralEntityMap());
    }

    public int getEntityCount() {
        return this.mGeneralEntities == null ? 0 : this.mGeneralEntities.size();
    }

    public int getNotationCount() {
        return this.mNotations == null ? 0 : this.mNotations.size();
    }

    public boolean isCachable() {
        return this.mIsCachable;
    }

    public HashMap getGeneralEntityMap() {
        return this.mGeneralEntities;
    }

    public List getGeneralEntityList() {
        List<Object> l = this.mGeneralEntityList;
        if (l == null) {
            l = this.mGeneralEntities == null || this.mGeneralEntities.size() == 0 ? Collections.EMPTY_LIST : Collections.unmodifiableList(new ArrayList(this.mGeneralEntities.values()));
            this.mGeneralEntityList = l;
        }
        return l;
    }

    public HashMap getParameterEntityMap() {
        return this.mDefinedPEs;
    }

    public HashMap getNotationMap() {
        return this.mNotations;
    }

    public synchronized List getNotationList() {
        List<Object> l = this.mNotationList;
        if (l == null) {
            l = this.mNotations == null || this.mNotations.size() == 0 ? Collections.EMPTY_LIST : Collections.unmodifiableList(new ArrayList(this.mNotations.values()));
            this.mNotationList = l;
        }
        return l;
    }

    public HashMap getElementMap() {
        return this.mElements;
    }

    public boolean isReusableWith(DTDSubset intSubset) {
        HashMap intGEs;
        HashMap intPEs;
        Set refdPEs = this.mRefdPEs;
        if (refdPEs != null && refdPEs.size() > 0 && (intPEs = intSubset.getParameterEntityMap()) != null && intPEs.size() > 0 && DataUtil.anyValuesInCommon(refdPEs, intPEs.keySet())) {
            return false;
        }
        Set refdGEs = this.mRefdGEs;
        return refdGEs == null || refdGEs.size() <= 0 || (intGEs = intSubset.getGeneralEntityMap()) == null || intGEs.size() <= 0 || !DataUtil.anyValuesInCommon(refdGEs, intGEs.keySet());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
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

    private static void combineMaps(HashMap m1, HashMap m2) {
        Iterator it = m2.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = it.next();
            Object key = me.getKey();
            Object old = m1.put(key, me.getValue());
            if (old == null) continue;
            m1.put(key, old);
        }
    }

    private void combineElements(InputProblemReporter rep, HashMap intElems, HashMap extElems) throws XMLStreamException {
        Iterator it = extElems.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = it.next();
            Object key = me.getKey();
            Object extVal = me.getValue();
            Object oldVal = intElems.get(key);
            if (oldVal == null) {
                intElems.put(key, extVal);
                continue;
            }
            DTDElement extElem = (DTDElement)extVal;
            DTDElement intElem = (DTDElement)oldVal;
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

    private static void checkNotations(HashMap fromInt, HashMap fromExt) throws XMLStreamException {
        Iterator it = fromExt.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry en = it.next();
            if (!fromInt.containsKey(en.getKey())) continue;
            DTDSubsetImpl.throwNotationException((NotationDeclaration)fromInt.get(en.getKey()), (NotationDeclaration)en.getValue());
        }
    }
}

