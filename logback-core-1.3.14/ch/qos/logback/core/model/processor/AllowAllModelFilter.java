/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.ModelFilter;
import ch.qos.logback.core.spi.FilterReply;

public class AllowAllModelFilter
implements ModelFilter {
    @Override
    public FilterReply decide(Model model) {
        return FilterReply.ACCEPT;
    }
}

