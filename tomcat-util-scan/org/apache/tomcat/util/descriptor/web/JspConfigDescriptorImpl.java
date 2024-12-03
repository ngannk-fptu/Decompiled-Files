/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.descriptor.JspConfigDescriptor
 *  javax.servlet.descriptor.JspPropertyGroupDescriptor
 *  javax.servlet.descriptor.TaglibDescriptor
 */
package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;

public class JspConfigDescriptorImpl
implements JspConfigDescriptor {
    private final Collection<JspPropertyGroupDescriptor> jspPropertyGroups;
    private final Collection<TaglibDescriptor> taglibs;

    public JspConfigDescriptorImpl(Collection<JspPropertyGroupDescriptor> jspPropertyGroups, Collection<TaglibDescriptor> taglibs) {
        this.jspPropertyGroups = jspPropertyGroups;
        this.taglibs = taglibs;
    }

    public Collection<JspPropertyGroupDescriptor> getJspPropertyGroups() {
        return new ArrayList<JspPropertyGroupDescriptor>(this.jspPropertyGroups);
    }

    public Collection<TaglibDescriptor> getTaglibs() {
        return new ArrayList<TaglibDescriptor>(this.taglibs);
    }
}

