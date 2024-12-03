/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.filefilter.AndFileFilter
 *  org.apache.commons.io.filefilter.DirectoryFileFilter
 *  org.apache.commons.io.filefilter.IOFileFilter
 *  org.apache.commons.io.filefilter.NotFileFilter
 *  org.apache.commons.io.filefilter.SuffixFileFilter
 */
package net.fortuna.ical4j.vcard;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public final class VCardFileFilter
extends AndFileFilter {
    private static final long serialVersionUID = -8748884254181947028L;
    public static final VCardFileFilter INSTANCE = new VCardFileFilter();

    private VCardFileFilter() {
        super((IOFileFilter)new NotFileFilter(DirectoryFileFilter.INSTANCE), (IOFileFilter)new SuffixFileFilter(".vcf"));
    }
}

