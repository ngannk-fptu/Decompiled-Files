/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.LaxDefaultNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import java.util.Iterator;
import java.util.StringTokenizer;

public abstract class AnyState
extends ExpressionWithoutChildState {
    protected final Expression makeExpression() {
        return this.createExpression(this.startTag.getDefaultedAttribute("namespace", "##any"), this.startTag.getDefaultedAttribute("processContents", "strict"));
    }

    protected abstract Expression createExpression(String var1, String var2);

    protected NameClass getNameClass(String namespace, XMLSchemaSchema currentSchema) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if ((namespace = namespace.trim()).equals("##any")) {
            return NameClass.ALL;
        }
        if (namespace.equals("##other")) {
            return new NotNameClass(new ChoiceNameClass(new NamespaceNameClass(currentSchema.targetNamespace), new NamespaceNameClass("")));
        }
        NameClass choices = null;
        StringTokenizer tokens = new StringTokenizer(namespace);
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            NamespaceNameClass nc = token.equals("##targetNamespace") ? new NamespaceNameClass(currentSchema.targetNamespace) : (token.equals("##local") ? new NamespaceNameClass("") : new NamespaceNameClass(token));
            if (choices == null) {
                choices = nc;
                continue;
            }
            choices = new ChoiceNameClass(choices, nc);
        }
        if (choices == null) {
            reader.reportError("GrammarReader.BadAttributeValue", (Object)"namespace", (Object)namespace);
            return NameClass.ALL;
        }
        return choices;
    }

    protected abstract NameClass getNameClassFrom(ReferenceExp var1);

    protected NameClass createLaxNameClass(NameClass allowedNc, XMLSchemaReader.RefResolver res) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        LaxDefaultNameClass laxNc = new LaxDefaultNameClass(allowedNc);
        Iterator itr = reader.grammar.iterateSchemas();
        while (itr.hasNext()) {
            XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
            if (!allowedNc.accepts(schema.targetNamespace, "*")) continue;
            ReferenceExp[] refs = res.get(schema).getAll();
            for (int i = 0; i < refs.length; ++i) {
                NameClass name = this.getNameClassFrom(refs[i]);
                if (!(name instanceof SimpleNameClass)) {
                    throw new Error();
                }
                SimpleNameClass snc = (SimpleNameClass)name;
                laxNc.addName(snc.namespaceURI, snc.localName);
            }
        }
        return new DifferenceNameClass(laxNc, new NotNameClass(allowedNc));
    }
}

