/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.atlassian.confluence.user;

import java.util.Set;
import javax.servlet.ServletContext;

public interface UserProfilePictureManager {
    public Set<String> getStandardProfilePictures(ServletContext var1);
}

