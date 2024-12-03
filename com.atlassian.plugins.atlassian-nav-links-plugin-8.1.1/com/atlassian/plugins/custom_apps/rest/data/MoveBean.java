/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.custom_apps.rest.data;

import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="version")
public class MoveBean {
    @XmlElement
    public URI after;
    @XmlElement
    public Position position;

    public static enum Position {
        Earlier,
        Later,
        First,
        Last;

    }
}

