/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.io.Reader;
import org.hibernate.service.Service;

public interface ImportSqlCommandExtractor
extends Service {
    public String[] extractCommands(Reader var1);
}

