/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.writer.relaxng;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.writer.XMLWriter;
import com.ctc.wstx.shaded.msv_core.writer.relaxng.Context;
import java.util.Stack;

public class NameClassWriter
implements NameClassVisitor {
    private final XMLWriter writer;
    private final String defaultNs;

    public NameClassWriter(Context ctxt) {
        this.writer = ctxt.getWriter();
        this.defaultNs = ctxt.getTargetNamespace();
    }

    public Object onAnyName(AnyNameClass nc) {
        this.writer.element("anyName");
        return null;
    }

    protected void startWithNs(String name, String ns) {
        if (ns.equals(this.defaultNs)) {
            this.writer.start(name);
        } else {
            this.writer.start(name, new String[]{"ns", ns});
        }
    }

    public Object onSimple(SimpleNameClass nc) {
        this.startWithNs("name", nc.namespaceURI);
        this.writer.characters(nc.localName);
        this.writer.end("name");
        return null;
    }

    public Object onNsName(NamespaceNameClass nc) {
        this.startWithNs("nsName", nc.namespaceURI);
        this.writer.end("nsName");
        return null;
    }

    public Object onNot(NotNameClass nc) {
        throw new Error();
    }

    public Object onChoice(ChoiceNameClass nc) {
        this.writer.start("choice");
        this.processChoice(nc);
        this.writer.end("choice");
        return null;
    }

    private void processChoice(ChoiceNameClass nc) {
        Stack<NameClass> s = new Stack<NameClass>();
        s.push(nc.nc1);
        s.push(nc.nc2);
        while (!s.empty()) {
            NameClass n = (NameClass)s.pop();
            if (n instanceof ChoiceNameClass) {
                s.push(((ChoiceNameClass)n).nc1);
                s.push(((ChoiceNameClass)n).nc2);
                continue;
            }
            n.visit(this);
        }
    }

    public Object onDifference(DifferenceNameClass nc) {
        if (nc.nc1 instanceof AnyNameClass) {
            this.writer.start("anyName");
            this.writer.start("except");
            if (nc.nc2 instanceof ChoiceNameClass) {
                this.processChoice((ChoiceNameClass)nc.nc2);
            } else {
                nc.nc2.visit(this);
            }
            this.writer.end("except");
            this.writer.end("anyName");
        } else if (nc.nc1 instanceof NamespaceNameClass) {
            this.startWithNs("nsName", ((NamespaceNameClass)nc.nc1).namespaceURI);
            this.writer.start("except");
            if (nc.nc2 instanceof ChoiceNameClass) {
                this.processChoice((ChoiceNameClass)nc.nc2);
            } else {
                nc.nc2.visit(this);
            }
            this.writer.end("except");
            this.writer.end("nsName");
        } else {
            throw new Error();
        }
        return null;
    }
}

