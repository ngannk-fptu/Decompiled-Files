/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.resource.Resource;

public abstract class InputBase
extends Directive {
    protected String getInputEncoding(InternalContextAdapter context) {
        Resource current = context.getCurrentResource();
        if (current != null) {
            return current.getEncoding();
        }
        return (String)this.rsvc.getProperty("input.encoding");
    }
}

