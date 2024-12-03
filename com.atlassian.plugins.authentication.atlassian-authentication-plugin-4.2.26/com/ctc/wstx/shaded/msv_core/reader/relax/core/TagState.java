/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ClauseState;
import org.xml.sax.Locator;

public class TagState
extends ClauseState {
    protected void endSelf() {
        super.endSelf();
        String name = this.startTag.getAttribute("name");
        String role = this.startTag.getAttribute("role");
        if (role == null) {
            role = name;
        }
        if (name == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"tag", (Object)"name");
            return;
        }
        TagClause c = this.getReader().module.tags.getOrCreate(role);
        if (c.nameClass != null) {
            this.reader.reportError(new Locator[]{this.getReader().getDeclaredLocationOf(c), this.location}, "RELAXReader.MultipleTagDeclarations", new Object[]{role});
        }
        c.nameClass = new SimpleNameClass(this.getReader().module.targetNamespace, name);
        c.exp = this.exp;
        this.getReader().setDeclaredLocationOf(c);
    }
}

