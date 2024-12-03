/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package org.apache.catalina.ant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.catalina.ant.AbstractCatalinaTask;
import org.apache.tools.ant.BuildException;

public class JMXQueryTask
extends AbstractCatalinaTask {
    protected String query = null;

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public void execute() throws BuildException {
        String queryString;
        super.execute();
        if (this.query == null) {
            queryString = "";
        } else {
            try {
                queryString = "?qry=" + URLEncoder.encode(this.query, this.getCharset());
            }
            catch (UnsupportedEncodingException e) {
                throw new BuildException("Invalid 'charset' attribute: " + this.getCharset());
            }
        }
        this.log("Query string is " + queryString);
        this.execute("/jmxproxy/" + queryString);
    }
}

