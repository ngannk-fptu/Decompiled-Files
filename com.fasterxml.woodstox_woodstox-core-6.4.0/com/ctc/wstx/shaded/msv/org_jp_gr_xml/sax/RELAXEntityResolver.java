/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax;

import com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax.SimpleEntityResolver;

public class RELAXEntityResolver
extends SimpleEntityResolver {
    public RELAXEntityResolver() {
        String s = this.getClass().getResource("/com/ctc/wstx/shaded/msv/org_jp_gr_xml/lib/relaxCore.dtd").toExternalForm();
        String s1 = this.getClass().getResource("/com/ctc/wstx/shaded/msv/org_jp_gr_xml/lib/relaxNamespace.dtd").toExternalForm();
        String s2 = this.getClass().getResource("/com/ctc/wstx/shaded/msv/org_jp_gr_xml/lib/relax.dtd").toExternalForm();
        this.addSystemId("http://www.xml.gr.jp/relax/core1/relaxCore.dtd", s);
        this.addSystemId("relaxCore.dtd", s);
        this.addSystemId("relaxNamespace.dtd", s1);
        this.addSystemId("relax.dtd", s2);
        this.addPublicId("-//RELAX//DTD RELAX Core 1.0//JA", s);
        this.addPublicId("-//RELAX//DTD RELAX Namespace 1.0//JA", s1);
        this.addPublicId("-//RELAX//DTD RELAX Grammar 1.0//JA", s2);
    }
}

