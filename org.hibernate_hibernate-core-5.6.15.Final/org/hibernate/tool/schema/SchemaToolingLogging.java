/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.tool.schema;

import org.jboss.logging.Logger;

public class SchemaToolingLogging {
    public static final String LOGGER_NAME = "org.hibernate.orm.tooling.schema";
    public static final Logger LOGGER = Logger.getLogger((String)"org.hibernate.orm.tooling.schema");
    public static final String AST_LOGGER_NAME = "org.hibernate.orm.tooling.schema.AST";
    public static final Logger AST_LOGGER = Logger.getLogger((String)"org.hibernate.orm.tooling.schema.AST");
    public static final boolean TRACE_ENABLED = LOGGER.isTraceEnabled();
    public static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();
    public static final boolean AST_TRACE_ENABLED = AST_LOGGER.isTraceEnabled();
}

