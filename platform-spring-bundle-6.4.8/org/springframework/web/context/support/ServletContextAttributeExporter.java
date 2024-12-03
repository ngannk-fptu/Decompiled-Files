/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.context.support;

import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletContextAware;

public class ServletContextAttributeExporter
implements ServletContextAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Map<String, Object> attributes;

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        if (this.attributes != null) {
            for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
                String attributeName = entry.getKey();
                if (this.logger.isDebugEnabled() && servletContext.getAttribute(attributeName) != null) {
                    this.logger.debug((Object)("Replacing existing ServletContext attribute with name '" + attributeName + "'"));
                }
                servletContext.setAttribute(attributeName, entry.getValue());
                if (!this.logger.isTraceEnabled()) continue;
                this.logger.trace((Object)("Exported ServletContext attribute with name '" + attributeName + "'"));
            }
        }
    }
}

