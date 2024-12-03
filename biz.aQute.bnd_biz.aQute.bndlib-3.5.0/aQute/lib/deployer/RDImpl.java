/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.deployer;

import aQute.bnd.service.repository.SearchableRepository;

public class RDImpl
extends SearchableRepository.ResourceDescriptor
implements Cloneable,
Comparable<RDImpl> {
    public RDImpl clone() throws CloneNotSupportedException {
        return (RDImpl)super.clone();
    }

    @Override
    public int compareTo(RDImpl o) {
        if (this == o) {
            return 0;
        }
        int r = this.bsn.compareTo(o.bsn);
        if (r == 0) {
            r = this.version.compareTo(o.version);
        }
        return r;
    }
}

