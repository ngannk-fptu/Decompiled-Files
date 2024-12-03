/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import java.util.ArrayList;
import java.util.Collection;
import org.bedework.webdav.servlet.common.PropFindMethod;
import org.bedework.webdav.servlet.shared.WebdavProperty;

public class PrincipalPropertySearch {
    public Collection<WebdavProperty> props = new ArrayList<WebdavProperty>();
    public PropFindMethod.PropRequest pr;
    public boolean applyToPrincipalCollectionSet;
}

