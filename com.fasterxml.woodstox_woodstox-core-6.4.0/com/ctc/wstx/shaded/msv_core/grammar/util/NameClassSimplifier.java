/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.util.PossibleNamesCollector;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NameClassSimplifier {
    public static NameClass simplify(NameClass nc) {
        Set possibleNames = PossibleNamesCollector.calc(nc);
        String MAGIC = "\u0000";
        HashSet<String> uris = new HashSet<String>();
        Iterator itr = possibleNames.iterator();
        while (itr.hasNext()) {
            StringPair name = (StringPair)itr.next();
            if (name.localName != "\u0000") {
                if (nc.accepts(name) == nc.accepts(name.namespaceURI, "\u0000")) {
                    itr.remove();
                    continue;
                }
            } else if (name.namespaceURI != "\u0000" && nc.accepts(name) == nc.accepts("\u0000", "\u0000")) {
                itr.remove();
                continue;
            }
            if (name.namespaceURI == "\u0000") continue;
            uris.add(name.namespaceURI);
        }
        if (!nc.accepts("\u0000", "\u0000")) {
            possibleNames.remove(new StringPair("\u0000", "\u0000"));
        }
        NameClass result = null;
        for (String uri : uris) {
            NameClass local = null;
            for (StringPair name : possibleNames) {
                if (!name.namespaceURI.equals(uri) || name.localName == "\u0000") continue;
                if (local == null) {
                    local = new SimpleNameClass(name);
                    continue;
                }
                local = new ChoiceNameClass(local, new SimpleNameClass(name));
            }
            if (possibleNames.contains(new StringPair(uri, "\u0000"))) {
                local = local == null ? new NamespaceNameClass(uri) : new DifferenceNameClass(new NamespaceNameClass(uri), local);
            }
            if (local == null) continue;
            if (result == null) {
                result = local;
                continue;
            }
            result = new ChoiceNameClass(result, local);
        }
        if (nc.accepts("\u0000", "\u0000")) {
            result = result == null ? NameClass.ALL : new NotNameClass(result);
        }
        if (result == null) {
            result = AnyNameClass.NONE;
        }
        return result;
    }
}

