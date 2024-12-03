/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.quickreload;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Commenter {
    @XmlElement
    private String userName;
    @XmlElement
    private String displayName;
    @XmlElement
    private Profile profilePicture;

    private Commenter() {
    }

    public Commenter(String userName, String displayName, String profilePicturePath) {
        this.userName = userName;
        this.displayName = displayName;
        this.profilePicture = new Profile(profilePicturePath);
    }

    @XmlRootElement
    public static class Profile {
        @XmlElement
        private String path;

        private Profile() {
        }

        public Profile(String path) {
            this.path = path;
        }
    }
}

