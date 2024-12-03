/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import java.util.EventListener;
import org.springframework.beans.factory.parsing.AliasDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.DefaultsDefinition;
import org.springframework.beans.factory.parsing.ImportDefinition;

public interface ReaderEventListener
extends EventListener {
    public void defaultsRegistered(DefaultsDefinition var1);

    public void componentRegistered(ComponentDefinition var1);

    public void aliasRegistered(AliasDefinition var1);

    public void importProcessed(ImportDefinition var1);
}

