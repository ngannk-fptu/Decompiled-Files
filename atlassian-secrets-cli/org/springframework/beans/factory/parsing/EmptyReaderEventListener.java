/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.AliasDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.DefaultsDefinition;
import org.springframework.beans.factory.parsing.ImportDefinition;
import org.springframework.beans.factory.parsing.ReaderEventListener;

public class EmptyReaderEventListener
implements ReaderEventListener {
    @Override
    public void defaultsRegistered(DefaultsDefinition defaultsDefinition) {
    }

    @Override
    public void componentRegistered(ComponentDefinition componentDefinition) {
    }

    @Override
    public void aliasRegistered(AliasDefinition aliasDefinition) {
    }

    @Override
    public void importProcessed(ImportDefinition importDefinition) {
    }
}

