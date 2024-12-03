/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.diff;

import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.EditCommand;

public class InsertCommand<T>
extends EditCommand<T> {
    public InsertCommand(T object) {
        super(object);
    }

    @Override
    public void accept(CommandVisitor<T> visitor) {
        visitor.visitInsertCommand(this.getObject());
    }
}

