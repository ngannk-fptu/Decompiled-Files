/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.sharing;

import java.io.StringWriter;
import org.bedework.util.misc.ToString;
import org.bedework.util.xml.XmlEmit;
import org.bedework.util.xml.tagdefs.AppleServerTags;
import org.bedework.util.xml.tagdefs.WebdavTags;

public class SharedAsType {
    private String href;

    public SharedAsType(String href) {
        this.href = href;
    }

    public String getHref() {
        return this.href;
    }

    public String toXml() throws Throwable {
        StringWriter str = new StringWriter();
        XmlEmit xml = new XmlEmit();
        xml.startEmit(str);
        this.toXml(xml);
        return str.toString();
    }

    public void toXml(XmlEmit xml) throws Throwable {
        xml.openTag(AppleServerTags.sharedAs);
        xml.property(WebdavTags.href, this.getHref());
        xml.closeTag(AppleServerTags.sharedAs);
    }

    protected void toStringSegment(ToString ts) {
        ts.append("href", this.getHref());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

