/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.transform.BasicTransformerAdapter;

public class DistinctResultTransformer
extends BasicTransformerAdapter {
    public static final DistinctResultTransformer INSTANCE = new DistinctResultTransformer();
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DistinctResultTransformer.class);

    private DistinctResultTransformer() {
    }

    @Override
    public List transformList(List list) {
        ArrayList result = new ArrayList(list.size());
        HashSet<Identity> distinct = new HashSet<Identity>();
        for (Object entity : list) {
            if (!distinct.add(new Identity(entity))) continue;
            result.add(entity);
        }
        LOG.debugf("Transformed: %s rows to: %s distinct results", list.size(), result.size());
        return result;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    private static final class Identity {
        final Object entity;

        private Identity(Object entity) {
            this.entity = entity;
        }

        public boolean equals(Object other) {
            return Identity.class.isInstance(other) && this.entity == ((Identity)other).entity;
        }

        public int hashCode() {
            return System.identityHashCode(this.entity);
        }
    }
}

