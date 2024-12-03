/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.EntityDecl;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.InputEntity;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.Resolver;
import java.io.IOException;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class ExternalEntity
extends EntityDecl {
    String systemId;
    String publicId;
    String notation;

    public ExternalEntity(InputEntity in) {
    }

    public InputSource getInputSource(EntityResolver r) throws IOException, SAXException {
        InputSource retval = r.resolveEntity(this.publicId, this.systemId);
        if (retval == null) {
            retval = Resolver.createInputSource(new URL(this.systemId), false);
        }
        return retval;
    }
}

