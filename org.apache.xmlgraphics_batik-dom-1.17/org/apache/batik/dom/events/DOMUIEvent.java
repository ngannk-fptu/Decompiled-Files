/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.xml.XMLUtilities
 */
package org.apache.batik.dom.events;

import java.util.ArrayList;
import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.xml.XMLUtilities;
import org.w3c.dom.events.UIEvent;
import org.w3c.dom.views.AbstractView;

public class DOMUIEvent
extends AbstractEvent
implements UIEvent {
    private AbstractView view;
    private int detail;

    @Override
    public AbstractView getView() {
        return this.view;
    }

    @Override
    public int getDetail() {
        return this.detail;
    }

    @Override
    public void initUIEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg, int detailArg) {
        this.initEvent(typeArg, canBubbleArg, cancelableArg);
        this.view = viewArg;
        this.detail = detailArg;
    }

    public void initUIEventNS(String namespaceURIArg, String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg, int detailArg) {
        this.initEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg);
        this.view = viewArg;
        this.detail = detailArg;
    }

    protected String[] split(String s) {
        ArrayList<String> a = new ArrayList<String>(8);
        int i = 0;
        int len = s.length();
        while (i < len) {
            char c;
            if (XMLUtilities.isXMLSpace((char)(c = s.charAt(i++)))) continue;
            StringBuffer sb = new StringBuffer();
            sb.append(c);
            while (i < len) {
                if (XMLUtilities.isXMLSpace((char)(c = s.charAt(i++)))) {
                    a.add(sb.toString());
                    break;
                }
                sb.append(c);
            }
            if (i != len) continue;
            a.add(sb.toString());
        }
        return a.toArray(new String[a.size()]);
    }
}

