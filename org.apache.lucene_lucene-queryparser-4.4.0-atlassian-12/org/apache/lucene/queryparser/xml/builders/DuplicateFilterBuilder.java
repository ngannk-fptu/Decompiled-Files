/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.sandbox.queries.DuplicateFilter
 *  org.apache.lucene.sandbox.queries.DuplicateFilter$KeepMode
 *  org.apache.lucene.sandbox.queries.DuplicateFilter$ProcessingMode
 *  org.apache.lucene.search.Filter
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.sandbox.queries.DuplicateFilter;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;

public class DuplicateFilterBuilder
implements FilterBuilder {
    @Override
    public Filter getFilter(Element e) throws ParserException {
        String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        DuplicateFilter df = new DuplicateFilter(fieldName);
        String keepMode = DOMUtils.getAttribute(e, "keepMode", "first");
        if (keepMode.equalsIgnoreCase("first")) {
            df.setKeepMode(DuplicateFilter.KeepMode.KM_USE_FIRST_OCCURRENCE);
        } else if (keepMode.equalsIgnoreCase("last")) {
            df.setKeepMode(DuplicateFilter.KeepMode.KM_USE_LAST_OCCURRENCE);
        } else {
            throw new ParserException("Illegal keepMode attribute in DuplicateFilter:" + keepMode);
        }
        String processingMode = DOMUtils.getAttribute(e, "processingMode", "full");
        if (processingMode.equalsIgnoreCase("full")) {
            df.setProcessingMode(DuplicateFilter.ProcessingMode.PM_FULL_VALIDATION);
        } else if (processingMode.equalsIgnoreCase("fast")) {
            df.setProcessingMode(DuplicateFilter.ProcessingMode.PM_FAST_INVALIDATION);
        } else {
            throw new ParserException("Illegal processingMode attribute in DuplicateFilter:" + processingMode);
        }
        return df;
    }
}

