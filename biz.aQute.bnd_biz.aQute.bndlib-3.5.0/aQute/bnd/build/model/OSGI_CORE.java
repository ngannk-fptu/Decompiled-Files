/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model;

import aQute.bnd.osgi.Domain;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

public enum OSGI_CORE {
    R4_0_1,
    R4_2_1,
    R4_3_0,
    R4_3_1,
    R5_0_0,
    R6_0_0;

    private Domain manifest;

    public Domain getManifest() throws IOException {
        if (this.manifest == null) {
            try (InputStream resource = OSGI_CORE.class.getResourceAsStream("osgi-core/" + this.name() + ".mf");){
                Manifest m = new Manifest(resource);
                this.manifest = Domain.domain(m);
            }
        }
        return this.manifest;
    }
}

