/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository.metadataparser;

import org.apache.felix.bundlerepository.metadataparser.XmlCommonHandler;

public class MappingProcessingInstructionHandler {
    private XmlCommonHandler m_handler;
    private String m_name;
    private String m_classname;

    public MappingProcessingInstructionHandler(XmlCommonHandler handler) {
        this.m_handler = handler;
    }

    public void process() throws Exception {
        if (this.m_name == null) {
            throw new Exception("element is missing");
        }
        if (this.m_classname == null) {
            throw new Exception("class is missing");
        }
        this.m_handler.addType(this.m_name, this.getClass().getClassLoader().loadClass(this.m_classname), null, null);
    }

    public void setElement(String element) {
        this.m_name = element;
    }

    public void setClass(String classname) {
        this.m_classname = classname;
    }
}

