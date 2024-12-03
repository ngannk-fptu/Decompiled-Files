/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.util.StreamUtils;

public class And
extends ConditionBase
implements Condition {
    @Override
    public boolean eval() throws BuildException {
        return StreamUtils.enumerationAsStream(this.getConditions()).allMatch(Condition::eval);
    }
}

