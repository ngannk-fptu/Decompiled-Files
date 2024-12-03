/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public interface Renderable {
    public boolean render(InternalContextAdapter var1, Writer var2) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException;
}

