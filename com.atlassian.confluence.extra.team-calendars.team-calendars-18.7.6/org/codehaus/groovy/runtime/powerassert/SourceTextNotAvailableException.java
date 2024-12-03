/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.powerassert;

import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.control.SourceUnit;

public class SourceTextNotAvailableException
extends RuntimeException {
    public SourceTextNotAvailableException(AssertStatement stat, SourceUnit unit, String msg) {
        super(String.format("%s for %s at (%d,%d)-(%d,%d) in %s", msg, stat.getBooleanExpression().getText(), stat.getLineNumber(), stat.getColumnNumber(), stat.getLastLineNumber(), stat.getLastColumnNumber(), unit.getName()));
    }
}

