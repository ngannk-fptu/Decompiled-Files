/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.completion;

import java.util.List;
import org.codehaus.groovy.antlr.GroovySourceToken;

public interface IdentifierCompletor {
    public boolean complete(List<GroovySourceToken> var1, List<CharSequence> var2);
}

