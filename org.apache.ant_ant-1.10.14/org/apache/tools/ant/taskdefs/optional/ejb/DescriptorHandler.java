/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import org.apache.tools.ant.Task;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DescriptorHandler
extends HandlerBase {
    private static final int DEFAULT_HASH_TABLE_SIZE = 10;
    private static final int STATE_LOOKING_EJBJAR = 1;
    private static final int STATE_IN_EJBJAR = 2;
    private static final int STATE_IN_BEANS = 3;
    private static final int STATE_IN_SESSION = 4;
    private static final int STATE_IN_ENTITY = 5;
    private static final int STATE_IN_MESSAGE = 6;
    private Task owningTask;
    private String publicId = null;
    private static final String EJB_REF = "ejb-ref";
    private static final String EJB_LOCAL_REF = "ejb-local-ref";
    private static final String HOME_INTERFACE = "home";
    private static final String REMOTE_INTERFACE = "remote";
    private static final String LOCAL_HOME_INTERFACE = "local-home";
    private static final String LOCAL_INTERFACE = "local";
    private static final String BEAN_CLASS = "ejb-class";
    private static final String PK_CLASS = "prim-key-class";
    private static final String EJB_NAME = "ejb-name";
    private static final String EJB_JAR = "ejb-jar";
    private static final String ENTERPRISE_BEANS = "enterprise-beans";
    private static final String ENTITY_BEAN = "entity";
    private static final String SESSION_BEAN = "session";
    private static final String MESSAGE_BEAN = "message-driven";
    private int parseState = 1;
    protected String currentElement = null;
    protected String currentText = null;
    protected Hashtable<String, File> ejbFiles = null;
    protected String ejbName = null;
    private Map<String, File> fileDTDs = new Hashtable<String, File>();
    private Map<String, String> resourceDTDs = new Hashtable<String, String>();
    private boolean inEJBRef = false;
    private Map<String, URL> urlDTDs = new Hashtable<String, URL>();
    private File srcDir;

    public DescriptorHandler(Task task, File srcDir) {
        this.owningTask = task;
        this.srcDir = srcDir;
    }

    public void registerDTD(String publicId, String location) {
        if (location == null) {
            return;
        }
        File fileDTD = new File(location);
        if (!fileDTD.exists()) {
            fileDTD = this.owningTask.getProject().resolveFile(location);
        }
        if (fileDTD.exists()) {
            if (publicId != null) {
                this.fileDTDs.put(publicId, fileDTD);
                this.owningTask.log("Mapped publicId " + publicId + " to file " + fileDTD, 3);
            }
            return;
        }
        if (this.getClass().getResource(location) != null && publicId != null) {
            this.resourceDTDs.put(publicId, location);
            this.owningTask.log("Mapped publicId " + publicId + " to resource " + location, 3);
        }
        try {
            if (publicId != null) {
                URL urldtd = new URL(location);
                this.urlDTDs.put(publicId, urldtd);
            }
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        InputStream is;
        String dtdResourceName;
        this.publicId = publicId;
        File dtdFile = this.fileDTDs.get(publicId);
        if (dtdFile != null) {
            try {
                this.owningTask.log("Resolved " + publicId + " to local file " + dtdFile, 3);
                return new InputSource(Files.newInputStream(dtdFile.toPath(), new OpenOption[0]));
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        if ((dtdResourceName = this.resourceDTDs.get(publicId)) != null && (is = this.getClass().getResourceAsStream(dtdResourceName)) != null) {
            this.owningTask.log("Resolved " + publicId + " to local resource " + dtdResourceName, 3);
            return new InputSource(is);
        }
        URL dtdUrl = this.urlDTDs.get(publicId);
        if (dtdUrl != null) {
            try {
                InputStream is2 = dtdUrl.openStream();
                this.owningTask.log("Resolved " + publicId + " to url " + dtdUrl, 3);
                return new InputSource(is2);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.owningTask.log("Could not resolve (publicId: " + publicId + ", systemId: " + systemId + ") to a local entity", 2);
        return null;
    }

    public Hashtable<String, File> getFiles() {
        return this.ejbFiles == null ? new Hashtable<String, File>(Collections.emptyMap()) : this.ejbFiles;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getEjbName() {
        return this.ejbName;
    }

    @Override
    public void startDocument() throws SAXException {
        this.ejbFiles = new Hashtable(10, 1.0f);
        this.currentElement = null;
        this.inEJBRef = false;
    }

    @Override
    public void startElement(String name, AttributeList attrs) throws SAXException {
        this.currentElement = name;
        this.currentText = "";
        if (EJB_REF.equals(name) || EJB_LOCAL_REF.equals(name)) {
            this.inEJBRef = true;
        } else if (this.parseState == 1 && EJB_JAR.equals(name)) {
            this.parseState = 2;
        } else if (this.parseState == 2 && ENTERPRISE_BEANS.equals(name)) {
            this.parseState = 3;
        } else if (this.parseState == 3 && SESSION_BEAN.equals(name)) {
            this.parseState = 4;
        } else if (this.parseState == 3 && ENTITY_BEAN.equals(name)) {
            this.parseState = 5;
        } else if (this.parseState == 3 && MESSAGE_BEAN.equals(name)) {
            this.parseState = 6;
        }
    }

    @Override
    public void endElement(String name) throws SAXException {
        this.processElement();
        this.currentText = "";
        this.currentElement = "";
        if (name.equals(EJB_REF) || name.equals(EJB_LOCAL_REF)) {
            this.inEJBRef = false;
        } else if (this.parseState == 5 && name.equals(ENTITY_BEAN)) {
            this.parseState = 3;
        } else if (this.parseState == 4 && name.equals(SESSION_BEAN)) {
            this.parseState = 3;
        } else if (this.parseState == 6 && name.equals(MESSAGE_BEAN)) {
            this.parseState = 3;
        } else if (this.parseState == 3 && name.equals(ENTERPRISE_BEANS)) {
            this.parseState = 2;
        } else if (this.parseState == 2 && name.equals(EJB_JAR)) {
            this.parseState = 1;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.currentText = this.currentText + new String(ch, start, length);
    }

    protected void processElement() {
        String className;
        if (this.inEJBRef || this.parseState != 5 && this.parseState != 4 && this.parseState != 6) {
            return;
        }
        if ((HOME_INTERFACE.equals(this.currentElement) || REMOTE_INTERFACE.equals(this.currentElement) || LOCAL_INTERFACE.equals(this.currentElement) || LOCAL_HOME_INTERFACE.equals(this.currentElement) || BEAN_CLASS.equals(this.currentElement) || PK_CLASS.equals(this.currentElement)) && !(className = this.currentText.trim()).startsWith("java.") && !className.startsWith("javax.")) {
            className = className.replace('.', File.separatorChar);
            className = className + ".class";
            this.ejbFiles.put(className, new File(this.srcDir, className));
        }
        if (this.currentElement.equals(EJB_NAME) && this.ejbName == null) {
            this.ejbName = this.currentText.trim();
        }
    }
}

