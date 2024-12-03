/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.spi;

import java.util.EnumSet;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;

public interface TargetDescriptor {
    public EnumSet<TargetType> getTargetTypes();

    public ScriptTargetOutput getScriptTargetOutput();
}

