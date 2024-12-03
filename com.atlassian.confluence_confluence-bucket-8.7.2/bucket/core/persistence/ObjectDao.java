/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package bucket.core.persistence;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.core.bean.EntityObject;
import com.google.common.base.Predicate;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
@Transactional(readOnly=true)
public interface ObjectDao {
    @Deprecated
    @Transactional
    public void remove(EntityObject var1);

    @Deprecated
    @Transactional
    public void refresh(EntityObject var1);

    @Deprecated
    @Transactional
    public void replicate(Object var1);

    @Deprecated(forRemoval=true)
    public @NonNull List findAll();

    @Deprecated(forRemoval=true)
    public @NonNull List findAllSorted(String var1);

    @Deprecated(forRemoval=true)
    public @NonNull List findAllSorted(String var1, boolean var2, int var3, int var4);

    @Deprecated(forRemoval=true)
    public <T> @NonNull PageResponse<T> findByClassIds(Iterable<Long> var1, LimitedRequest var2, Predicate<? super T> var3);

    @Deprecated
    @Transactional
    public void save(EntityObject var1);

    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    @Deprecated(forRemoval=true)
    public Class getPersistentClass();

    @Deprecated
    @Transactional
    public void saveRaw(EntityObject var1);
}

