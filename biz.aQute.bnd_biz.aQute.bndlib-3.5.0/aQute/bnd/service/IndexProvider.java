/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.bnd.service.ResolutionPhase;
import java.net.URI;
import java.util.List;
import java.util.Set;

public interface IndexProvider {
    public List<URI> getIndexLocations() throws Exception;

    public Set<ResolutionPhase> getSupportedPhases();
}

