/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.trex.ElementPattern;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import java.util.Iterator;

public class AnyElementState
extends AnyState {
    protected Expression createExpression(final String namespace, final String process) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        final XMLSchemaSchema currentSchema = reader.currentSchema;
        if (process.equals("skip")) {
            NameClass nc = this.getNameClass(namespace, currentSchema);
            ElementPattern ep = new ElementPattern(nc, Expression.nullSet);
            ep.contentModel = reader.pool.createMixed(reader.pool.createZeroOrMore(reader.pool.createChoice(ep, reader.pool.createAttribute(nc))));
            return ep;
        }
        final ReferenceExp exp = new ReferenceExp("any(" + process + ":" + namespace + ")");
        reader.addBackPatchJob(new GrammarReader.BackPatch(){

            public State getOwnerState() {
                return AnyElementState.this;
            }

            public void patch() {
                if (!process.equals("lax") && !process.equals("strict")) {
                    reader.reportError("GrammarReader.BadAttributeValue", (Object)"processContents", (Object)process);
                    exp.exp = Expression.nullSet;
                    return;
                }
                exp.exp = Expression.nullSet;
                NameClass nc = AnyElementState.this.getNameClass(namespace, currentSchema);
                Iterator itr = reader.grammar.iterateSchemas();
                while (itr.hasNext()) {
                    XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
                    if (!nc.accepts(schema.targetNamespace, "*")) continue;
                    exp.exp = reader.pool.createChoice(exp.exp, schema.topLevel);
                }
                if (!process.equals("lax")) {
                    return;
                }
                NameClass laxNc = AnyElementState.this.createLaxNameClass(nc, new XMLSchemaReader.RefResolver(){

                    public ReferenceContainer get(XMLSchemaSchema schema) {
                        return schema.elementDecls;
                    }
                });
                exp.exp = reader.pool.createChoice(new ElementPattern(laxNc, reader.pool.createMixed(reader.pool.createZeroOrMore(reader.pool.createChoice(reader.pool.createAttribute(NameClass.ALL), exp)))), exp.exp);
            }
        });
        exp.exp = Expression.nullSet;
        return exp;
    }

    protected NameClass getNameClassFrom(ReferenceExp exp) {
        return ((ElementDeclExp)exp).getElementExp().getNameClass();
    }
}

