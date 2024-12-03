/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.util.location.Location;
import java.util.Iterator;

interface Settings {
    public String get(String var1);

    public Location getLocation(String var1);

    public Iterator list();
}

