/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.dynamictasklist2;

import com.atlassian.confluence.extra.dynamictasklist2.model.LegacyTask;
import com.atlassian.confluence.extra.dynamictasklist2.model.LegacyTaskList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TaskListConverter
extends DefaultHandler {
    private static final Logger log = LoggerFactory.getLogger(TaskListConverter.class);
    private static final String TASK_ELEMENT_QNAME = "com.atlassian.confluence.extra.dynamictasklist2.model.LegacyTask";
    private static final String NAME_QNAME = "name";
    private static final String COMPLETER_QNAME = "completer";
    private final Map taskAttributes = new HashMap();
    private final List tasks = new LinkedList();
    private final StringBuffer listName = new StringBuffer();
    private String currentTaskProperty;
    private boolean inTask = false;
    private boolean inListName = false;
    private boolean processedListName = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!this.processedListName && NAME_QNAME.equals(qName)) {
            this.inListName = true;
            return;
        }
        if (TASK_ELEMENT_QNAME.equals(qName)) {
            this.inTask = true;
            return;
        }
        if (!this.inTask) {
            return;
        }
        this.currentTaskProperty = qName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        StringBuffer elementValue;
        if (this.inListName) {
            this.listName.append(ch, start, length);
            return;
        }
        if (!this.inTask || this.currentTaskProperty == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Building element '" + this.currentTaskProperty);
        }
        if ((elementValue = (StringBuffer)this.taskAttributes.get(this.currentTaskProperty)) == null) {
            elementValue = new StringBuffer();
            this.taskAttributes.put(this.currentTaskProperty, elementValue);
        }
        elementValue.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.inListName) {
            this.inListName = false;
            this.processedListName = true;
            return;
        }
        if (!this.inTask) {
            return;
        }
        if (!TASK_ELEMENT_QNAME.equals(qName)) {
            this.currentTaskProperty = null;
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Finalising task");
        }
        LegacyTask legacyTask = new LegacyTask();
        if (this.taskAttributes.get(NAME_QNAME) != null) {
            legacyTask.setName(this.taskAttributes.get(NAME_QNAME).toString());
        }
        if (this.taskAttributes.get(COMPLETER_QNAME) != null) {
            legacyTask.setCompleter(this.taskAttributes.get(COMPLETER_QNAME).toString());
        }
        if (log.isDebugEnabled()) {
            log.debug("Task '" + legacyTask.getName() + "' converted");
        }
        this.tasks.add(legacyTask);
        this.taskAttributes.clear();
        this.inTask = false;
    }

    public LegacyTaskList getTaskList() {
        LegacyTaskList legacyTaskList = new LegacyTaskList(this.listName.toString());
        legacyTaskList.setTasks(this.tasks);
        return legacyTaskList;
    }
}

