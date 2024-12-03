/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.taglib.decorator;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.taglib.AbstractTag;
import java.io.Writer;

public class PropertyTag
extends AbstractTag {
    private String propertyName;
    private String defaultValue;
    private boolean writeEntireProperty = false;

    public void setProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    protected String getProperty() {
        return this.propertyName;
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public final void setWriteEntireProperty(String writeEntireProperty) {
        if (writeEntireProperty == null || writeEntireProperty.trim().length() == 0) {
            return;
        }
        switch (writeEntireProperty.charAt(0)) {
            case '1': 
            case 'T': 
            case 'Y': 
            case 't': 
            case 'y': {
                this.writeEntireProperty = true;
                break;
            }
            default: {
                this.writeEntireProperty = false;
            }
        }
    }

    public int doEndTag() {
        try {
            Page page = this.getPage();
            String propertyValue = page.getProperty(this.propertyName);
            if (propertyValue == null || propertyValue.trim().length() == 0) {
                propertyValue = this.defaultValue;
            }
            if (propertyValue != null) {
                Writer out = this.getOut();
                if (this.writeEntireProperty) {
                    out.write(" ");
                    out.write(this.propertyName.substring(this.propertyName.lastIndexOf(46) + 1));
                    out.write("=\"");
                    out.write(propertyValue);
                    out.write("\"");
                } else {
                    out.write(propertyValue);
                }
            }
        }
        catch (Exception e) {
            PropertyTag.trace(e);
        }
        return 6;
    }
}

