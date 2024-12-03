/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.GroupDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.RedefinableDeclState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.Locator;

public class GroupState
extends RedefinableDeclState {
    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.createModelGroupState(this, tag);
    }

    protected ReferenceContainer getContainer() {
        return ((XMLSchemaReader)this.reader).currentSchema.groupDecls;
    }

    protected Expression initialExpression() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (this.startTag.containsAttribute("ref")) {
            Expression exp = reader.resolveQNameRef(this.startTag, "ref", new XMLSchemaReader.RefResolver(){

                public ReferenceContainer get(XMLSchemaSchema g) {
                    return g.groupDecls;
                }
            });
            if (exp == null) {
                return Expression.epsilon;
            }
            return exp;
        }
        return null;
    }

    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (halfCastedExpression != null) {
            this.reader.reportError("GrammarReader.Abstract.MoreThanOneChildExpression");
            return halfCastedExpression;
        }
        return newChildExpression;
    }

    protected Expression annealExpression(Expression contentType) {
        GroupDeclExp decl;
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (!this.isGlobal()) {
            return contentType;
        }
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)"group", (Object)"name");
            return Expression.nullSet;
        }
        if (contentType == null) {
            reader.reportError("GrammarReader.Abstract.MissingChildExpression");
            return Expression.nullSet;
        }
        if (this.isRedefine()) {
            decl = (GroupDeclExp)this.oldDecl;
        } else {
            decl = reader.currentSchema.groupDecls.getOrCreate(name);
            if (decl.exp != null) {
                reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(decl)}, "XMLSchemaReader.DuplicateGroupDefinition", new Object[]{name});
            }
        }
        reader.setDeclaredLocationOf(decl);
        decl.exp = contentType;
        return decl;
    }
}

