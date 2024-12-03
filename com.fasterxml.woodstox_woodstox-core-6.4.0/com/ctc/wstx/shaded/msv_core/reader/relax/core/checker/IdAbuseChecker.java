/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core.checker;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDREFType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDType;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IdAbuseChecker
implements RELAXExpressionVisitorVoid {
    private final Set tagNames = new HashSet();
    private final Set overloadedNames = new HashSet();
    private final Set nonIdAttrNames = new HashSet();
    private final Set idAttributes = new HashSet();
    private final RELAXModule module;
    private final RELAXCoreReader reader;
    private String currentTagName;

    private IdAbuseChecker(RELAXCoreReader r, RELAXModule m) {
        this.reader = r;
        this.module = m;
    }

    public static void check(RELAXCoreReader reader, RELAXModule module) {
        new IdAbuseChecker(reader, module).run();
    }

    private void run() {
        TagClause tag;
        Iterator itr = this.module.tags.iterator();
        while (itr.hasNext()) {
            tag = (TagClause)itr.next();
            if (!(tag.nameClass instanceof SimpleNameClass)) continue;
            SimpleNameClass snc = (SimpleNameClass)tag.nameClass;
            if (this.tagNames.contains(snc.localName)) {
                this.overloadedNames.add(snc.localName);
                continue;
            }
            this.tagNames.add(snc.localName);
        }
        itr = this.module.tags.iterator();
        while (itr.hasNext()) {
            tag = (TagClause)itr.next();
            this.currentTagName = tag.nameClass instanceof SimpleNameClass ? ((SimpleNameClass)tag.nameClass).localName : null;
            tag.exp.visit(this);
        }
        for (AttributeExp atr : this.idAttributes) {
            if (atr.nameClass instanceof SimpleNameClass) {
                String name = ((SimpleNameClass)atr.nameClass).localName;
                if (!this.nonIdAttrNames.contains(name)) continue;
                this.reader.reportError("RELAXReader.IdAbuse.1", (Object)name);
                continue;
            }
            this.reader.reportError("RELAXReader.IdAbuse");
        }
    }

    public void onAttribute(AttributeExp exp) {
        if (!(exp.nameClass instanceof SimpleNameClass)) {
            return;
        }
        Expression body = exp.exp.getExpandedExp(this.reader.pool);
        if (!(body instanceof DataExp)) {
            return;
        }
        SimpleNameClass snc = (SimpleNameClass)exp.nameClass;
        if (!snc.namespaceURI.equals("")) {
            return;
        }
        Datatype dt = ((DataExp)body).dt;
        if (dt == IDType.theInstance || dt == IDREFType.theInstance) {
            if (this.currentTagName == null || this.overloadedNames.contains(this.currentTagName)) {
                this.idAttributes.add(exp);
            }
        } else {
            this.nonIdAttrNames.add(snc.localName);
        }
    }

    public void onChoice(ChoiceExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
    }

    public void onElement(ElementExp exp) {
        throw new Error();
    }

    public void onOneOrMore(OneOrMoreExp exp) {
        exp.exp.visit(this);
    }

    public void onMixed(MixedExp exp) {
        throw new Error();
    }

    public void onRef(ReferenceExp exp) {
        exp.exp.visit(this);
    }

    public void onOther(OtherExp exp) {
        exp.exp.visit(this);
    }

    public void onEpsilon() {
    }

    public void onNullSet() {
    }

    public void onAnyString() {
    }

    public void onSequence(SequenceExp exp) {
    }

    public void onData(DataExp exp) {
        throw new Error();
    }

    public void onValue(ValueExp exp) {
        throw new Error();
    }

    public void onAttPool(AttPoolClause exp) {
        exp.exp.visit(this);
    }

    public void onTag(TagClause exp) {
        throw new Error();
    }

    public void onElementRules(ElementRules exp) {
        throw new Error();
    }

    public void onHedgeRules(HedgeRules exp) {
        throw new Error();
    }

    public void onInterleave(InterleaveExp exp) {
        throw new Error();
    }

    public void onConcur(ConcurExp exp) {
        throw new Error();
    }

    public void onList(ListExp exp) {
        throw new Error();
    }
}

