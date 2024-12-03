/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.notifications;

import java.util.ArrayList;
import java.util.List;
import org.bedework.caldav.util.notifications.ProcessorType;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.BedeworkServerTags;

public class ProcessorsType {
    private List<ProcessorType> processor;

    public List<ProcessorType> getProcessor() {
        if (this.processor == null) {
            this.processor = new ArrayList<ProcessorType>();
        }
        return this.processor;
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(BedeworkServerTags.processors);
        for (ProcessorType p : this.getProcessor()) {
            p.toXml(xml);
        }
        xml.closeTag(BedeworkServerTags.processors);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("processor", this.getProcessor(), true);
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

