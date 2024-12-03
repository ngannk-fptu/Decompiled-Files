/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.repository;

import javax.naming.Context;
import org.apache.jackrabbit.commons.repository.JNDIRepositoryFactory;
import org.apache.jackrabbit.commons.repository.ProxyRepository;

public class JNDIRepository
extends ProxyRepository {
    public JNDIRepository(Context context, String name) {
        super(new JNDIRepositoryFactory(context, name));
    }
}

