/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.repository;

import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.repository.SearchableRepository;
import aQute.bnd.version.Version;

public interface InfoRepository
extends RepositoryPlugin {
    public SearchableRepository.ResourceDescriptor getDescriptor(String var1, Version var2) throws Exception;
}

