/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc;

import java.io.IOException;
import org.apache.xmlgraphics.ps.dsc.DSCException;
import org.apache.xmlgraphics.ps.dsc.DSCFilter;
import org.apache.xmlgraphics.ps.dsc.DSCListener;
import org.apache.xmlgraphics.ps.dsc.DSCParser;
import org.apache.xmlgraphics.ps.dsc.events.DSCEvent;

public class FilteringEventListener
implements DSCListener {
    private DSCFilter filter;

    public FilteringEventListener(DSCFilter filter) {
        this.filter = filter;
    }

    @Override
    public void processEvent(DSCEvent event, DSCParser parser) throws IOException, DSCException {
        if (!this.filter.accept(event)) {
            parser.next();
        }
    }
}

