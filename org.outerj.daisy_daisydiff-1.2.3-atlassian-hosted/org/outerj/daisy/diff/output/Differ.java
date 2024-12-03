/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.IProgressMonitor
 */
package org.outerj.daisy.diff.output;

import org.eclipse.core.runtime.IProgressMonitor;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.xml.sax.SAXException;

public interface Differ {
    public void diff(TextNodeComparator var1, TextNodeComparator var2) throws SAXException;

    public void diff(TextNodeComparator var1, TextNodeComparator var2, IProgressMonitor var3) throws SAXException;

    public void diff(TextNodeComparator var1, TextNodeComparator var2, IProgressMonitor var3, boolean var4) throws SAXException;
}

