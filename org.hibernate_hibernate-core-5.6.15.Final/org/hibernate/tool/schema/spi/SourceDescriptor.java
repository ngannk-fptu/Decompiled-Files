/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import org.hibernate.tool.schema.SourceType;
import org.hibernate.tool.schema.spi.ScriptSourceInput;

public interface SourceDescriptor {
    public SourceType getSourceType();

    public ScriptSourceInput getScriptSourceInput();
}

