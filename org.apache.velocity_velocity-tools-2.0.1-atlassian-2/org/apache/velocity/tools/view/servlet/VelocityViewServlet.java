/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 */
package org.apache.velocity.tools.view.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.apache.velocity.tools.view.VelocityView;

@Deprecated
public class VelocityViewServlet
extends org.apache.velocity.tools.view.VelocityViewServlet {
    protected static final String TOOLBOX_KEY = "org.apache.velocity.toolbox";
    protected static final String DEFAULT_TOOLBOX_PATH = "/WEB-INF/toolbox.xml";
    private transient VelocityView view;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.getLog().debug((Object)(((Object)((Object)this)).getClass().getName() + " has been deprecated. Use " + super.getClass().getName() + " instead."));
    }

    @Override
    protected VelocityView getVelocityView() {
        if (this.view == null) {
            this.view = new VelocityView(this.getServletConfig());
        }
        return this.view;
    }
}

