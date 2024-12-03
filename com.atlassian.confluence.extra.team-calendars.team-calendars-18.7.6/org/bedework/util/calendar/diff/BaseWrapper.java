/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar.diff;

import javax.xml.namespace.QName;
import org.bedework.util.calendar.diff.XmlIcalCompare;
import org.bedework.util.misc.Logged;
import org.bedework.util.xml.NsContext;

abstract class BaseWrapper<ParentT extends BaseWrapper>
extends Logged {
    protected XmlIcalCompare.Globals globals;
    private ParentT parent;
    private QName name;

    BaseWrapper(ParentT parent, QName name) {
        this.parent = parent;
        this.name = name;
        if (parent != null) {
            this.globals = ((BaseWrapper)parent).globals;
        }
    }

    ParentT getParent() {
        return this.parent;
    }

    void setGlobals(XmlIcalCompare.Globals val) {
        this.globals = val;
    }

    QName getName() {
        return this.name;
    }

    boolean skipThis(Object val) {
        return this.globals.skipMap.containsKey(val.getClass().getCanonicalName());
    }

    void appendNsName(StringBuilder sb, NsContext nsContext) {
        nsContext.appendNsName(sb, this.name);
    }

    void appendNsName(StringBuilder sb, QName nm, NsContext nsContext) {
        nsContext.appendNsName(sb, nm);
    }

    void appendXpathElement(StringBuilder sb, NsContext nsContext) {
        this.appendNsName(sb, nsContext);
    }

    protected void toStringSegment(StringBuilder sb) {
        sb.append("name=");
        sb.append(this.name);
    }
}

