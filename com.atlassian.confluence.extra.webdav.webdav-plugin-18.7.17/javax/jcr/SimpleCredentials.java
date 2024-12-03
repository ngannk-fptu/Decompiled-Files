/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.util.HashMap;
import javax.jcr.Credentials;

public final class SimpleCredentials
implements Credentials {
    private final String userID;
    private final char[] password;
    private final HashMap attributes = new HashMap();

    public SimpleCredentials(String userID, char[] password) {
        this.userID = userID;
        this.password = (char[])password.clone();
    }

    public char[] getPassword() {
        return this.password;
    }

    public String getUserID() {
        return this.userID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (value == null) {
            this.removeAttribute(name);
            return;
        }
        HashMap hashMap = this.attributes;
        synchronized (hashMap) {
            this.attributes.put(name, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getAttribute(String name) {
        HashMap hashMap = this.attributes;
        synchronized (hashMap) {
            return this.attributes.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAttribute(String name) {
        HashMap hashMap = this.attributes;
        synchronized (hashMap) {
            this.attributes.remove(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getAttributeNames() {
        HashMap hashMap = this.attributes;
        synchronized (hashMap) {
            return this.attributes.keySet().toArray(new String[this.attributes.keySet().size()]);
        }
    }
}

