/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.thoughtworks.xstream.XStream
 */
package com.atlassian.confluence.setup.bandana;

import com.atlassian.confluence.setup.bandana.BandanaSerializer;
import com.atlassian.confluence.setup.xstream.ConfluenceXStream;
import com.thoughtworks.xstream.XStream;
import java.io.Reader;
import java.io.Writer;

public class XStreamBandanaSerializer
implements BandanaSerializer {
    private ConfluenceXStream confluenceXStream;
    private XStream xStream;

    @Deprecated
    public XStreamBandanaSerializer(XStream xStream) {
        this.xStream = xStream;
    }

    public XStreamBandanaSerializer(ConfluenceXStream confluenceXStream) {
        this.confluenceXStream = confluenceXStream;
    }

    @Override
    public void serialize(Object obj, Writer writer) {
        if (this.confluenceXStream != null) {
            this.confluenceXStream.toXML(obj, writer);
        } else {
            this.xStream.toXML(obj, writer);
        }
    }

    @Override
    public Object deserialize(Reader reader) {
        if (this.confluenceXStream != null) {
            return this.confluenceXStream.fromXML(reader);
        }
        return this.xStream.fromXML(reader);
    }
}

