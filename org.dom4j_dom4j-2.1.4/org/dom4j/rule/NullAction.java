/*
 * Decompiled with CFR 0.152.
 */
package org.dom4j.rule;

import org.dom4j.Node;
import org.dom4j.rule.Action;

public class NullAction
implements Action {
    public static final NullAction SINGLETON = new NullAction();

    @Override
    public void run(Node node) throws Exception {
    }
}

