/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc.applet;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.xmlrpc.applet.XmlRpcApplet;

public class JSXmlRpcApplet
extends XmlRpcApplet {
    public Object loaded = null;
    private String errorMessage;
    private Vector arguments;

    public void init() {
        this.initClient();
        this.arguments = new Vector();
        this.loaded = Boolean.TRUE;
        System.out.println("JSXmlRpcApplet initialized");
    }

    public void addIntArg(int value) {
        this.arguments.addElement(new Integer(value));
    }

    public void addIntArgToStruct(Hashtable struct, String key, int value) {
        struct.put(key, new Integer(value));
    }

    public void addIntArgToArray(Vector ary, int value) {
        ary.addElement(new Integer(value));
    }

    public void addDoubleArg(float value) {
        this.arguments.addElement(new Double(value));
    }

    public void addDoubleArgToStruct(Hashtable struct, String key, float value) {
        struct.put(key, new Double(value));
    }

    public void addDoubleArgToArray(Vector ary, float value) {
        ary.addElement(new Double(value));
    }

    public void addDoubleArg(double value) {
        this.arguments.addElement(new Double(value));
    }

    public void addDoubleArgToStruct(Hashtable struct, String key, double value) {
        struct.put(key, new Double(value));
    }

    public void addDoubleArgToArray(Vector ary, double value) {
        ary.addElement(new Double(value));
    }

    public void addBooleanArg(boolean value) {
        this.arguments.addElement(new Boolean(value));
    }

    public void addBooleanArgToStruct(Hashtable struct, String key, boolean value) {
        struct.put(key, new Boolean(value));
    }

    public void addBooleanArgToArray(Vector ary, boolean value) {
        ary.addElement(new Boolean(value));
    }

    public void addDateArg(long dateNo) {
        this.arguments.addElement(new Date(dateNo));
    }

    public void addDateArgToStruct(Hashtable struct, String key, long dateNo) {
        struct.put(key, new Date(dateNo));
    }

    public void addDateArgToArray(Vector ary, long dateNo) {
        ary.addElement(new Date(dateNo));
    }

    public void addStringArg(String str) {
        this.arguments.addElement(str);
    }

    public void addStringArgToStruct(Hashtable struct, String key, String str) {
        struct.put(key, str);
    }

    public void addStringArgToArray(Vector ary, String str) {
        ary.addElement(str);
    }

    public Vector addArrayArg() {
        Vector v = new Vector();
        this.arguments.addElement(v);
        return v;
    }

    public Vector addArrayArgToStruct(Hashtable struct, String key) {
        Vector v = new Vector();
        struct.put(key, v);
        return v;
    }

    public Vector addArrayArgToArray(Vector ary) {
        Vector v = new Vector();
        ary.addElement(v);
        return v;
    }

    public Hashtable addStructArg() {
        Hashtable ht = new Hashtable();
        this.arguments.addElement(ht);
        return ht;
    }

    public Hashtable addStructArgToStruct(Hashtable struct, String key) {
        Hashtable ht = new Hashtable();
        struct.put(key, ht);
        return ht;
    }

    public Hashtable addStructArgToArray(Vector ary) {
        Hashtable ht = new Hashtable();
        ary.addElement(ht);
        return ht;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void reset() {
        this.arguments = new Vector();
    }

    public Object execute(String methodName) {
        Object returnValue;
        block2: {
            this.errorMessage = null;
            this.showStatus("Connecting to Server...");
            returnValue = null;
            try {
                returnValue = this.execute(methodName, this.arguments);
            }
            catch (Exception e) {
                this.errorMessage = e.getMessage();
                if (this.errorMessage != null && this.errorMessage != "") break block2;
                this.errorMessage = e.toString();
            }
        }
        this.arguments = new Vector();
        this.showStatus("");
        return returnValue;
    }
}

