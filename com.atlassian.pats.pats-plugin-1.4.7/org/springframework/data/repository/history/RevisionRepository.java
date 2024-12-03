/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.history;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface RevisionRepository<T, ID, N extends Number>
extends Repository<T, ID> {
    public Optional<Revision<N, T>> findLastChangeRevision(ID var1);

    public Revisions<N, T> findRevisions(ID var1);

    public Page<Revision<N, T>> findRevisions(ID var1, Pageable var2);

    public Optional<Revision<N, T>> findRevision(ID var1, N var2);
}

