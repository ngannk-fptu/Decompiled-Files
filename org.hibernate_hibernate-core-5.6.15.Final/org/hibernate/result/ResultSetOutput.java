/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.result;

import java.util.List;
import org.hibernate.result.Output;

public interface ResultSetOutput
extends Output {
    public List getResultList();

    public Object getSingleResult();
}

