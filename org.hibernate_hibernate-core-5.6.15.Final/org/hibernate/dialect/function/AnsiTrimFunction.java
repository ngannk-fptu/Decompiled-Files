/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import org.hibernate.dialect.function.TrimFunctionTemplate;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class AnsiTrimFunction
extends TrimFunctionTemplate {
    @Override
    protected String render(TrimFunctionTemplate.Options options, String trimSource, SessionFactoryImplementor factory) {
        return String.format("trim(%s %s from %s)", options.getTrimSpecification().getName(), options.getTrimCharacter(), trimSource);
    }
}

