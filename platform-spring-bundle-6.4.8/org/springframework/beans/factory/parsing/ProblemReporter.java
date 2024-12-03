/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.Problem;

public interface ProblemReporter {
    public void fatal(Problem var1);

    public void error(Problem var1);

    public void warning(Problem var1);
}

