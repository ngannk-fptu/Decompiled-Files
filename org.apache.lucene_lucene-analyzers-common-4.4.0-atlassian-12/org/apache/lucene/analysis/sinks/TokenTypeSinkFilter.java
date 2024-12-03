/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.AttributeSource
 */
package org.apache.lucene.analysis.sinks;

import org.apache.lucene.analysis.sinks.TeeSinkTokenFilter;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

public class TokenTypeSinkFilter
extends TeeSinkTokenFilter.SinkFilter {
    private String typeToMatch;
    private TypeAttribute typeAtt;

    public TokenTypeSinkFilter(String typeToMatch) {
        this.typeToMatch = typeToMatch;
    }

    @Override
    public boolean accept(AttributeSource source) {
        if (this.typeAtt == null) {
            this.typeAtt = (TypeAttribute)source.addAttribute(TypeAttribute.class);
        }
        return this.typeToMatch.equals(this.typeAtt.type());
    }
}

