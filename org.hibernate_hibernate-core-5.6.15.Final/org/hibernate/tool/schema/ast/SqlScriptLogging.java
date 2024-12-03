/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.tool.schema.ast;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.ValidIdRange;

@ValidIdRange
public class SqlScriptLogging {
    public static final String SCRIPT_LOGGER_NAME = "org.hibernate.orm.tooling.schema.script";
    public static final Logger SCRIPT_LOGGER = Logger.getLogger((String)"org.hibernate.orm.tooling.schema.script");
    public static final boolean TRACE_ENABLED = SCRIPT_LOGGER.isTraceEnabled();
    public static final boolean DEBUG_ENABLED = SCRIPT_LOGGER.isDebugEnabled();
    public static final String AST_LOGGER_NAME = "org.hibernate.orm.tooling.schema.script.graph";
    public static final Logger AST_LOGGER = Logger.getLogger((String)"org.hibernate.orm.tooling.schema.script.graph");
    public static final boolean AST_TRACE_ENABLED = AST_LOGGER.isTraceEnabled();
}

