/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.io.Serializable;

public interface WhoDefs
extends Serializable {
    public static final char whoFlag = 'W';
    public static final char notWhoFlag = 'N';
    public static final char whoFlagOwner = 'O';
    public static final char whoFlagUser = 'U';
    public static final char whoFlagGroup = 'G';
    public static final char whoFlagTicket = 'T';
    public static final char whoFlagResource = 'R';
    public static final char whoFlagVenue = 'V';
    public static final char whoFlagHost = 'H';
    public static final char whoFlagUnauthenticated = 'X';
    public static final char whoFlagAuthenticated = 'A';
    public static final char whoFlagOther = 'Z';
    public static final char whoFlagAll = 'L';
    public static final int whoTypeOwner = 0;
    public static final int whoTypeUser = 1;
    public static final int whoTypeGroup = 2;
    public static final int whoTypeHost = 3;
    public static final int whoTypeTicket = 4;
    public static final int whoTypeResource = 5;
    public static final int whoTypeVenue = 6;
    public static final int whoTypeUnauthenticated = 7;
    public static final int whoTypeAuthenticated = 8;
    public static final int whoTypeOther = 9;
    public static final int whoTypeAll = 10;
    public static final char[] whoTypeFlags = new char[]{'O', 'U', 'G', 'H', 'T', 'R', 'V', 'X', 'A', 'Z', 'L'};
    public static final boolean[] whoTypeNamed = new boolean[]{false, true, true, true, true, true, true, false, false, false, false};
    public static final String[] whoTypeNames = new String[]{"owner", "user", "group", "host", "ticket", "resource", "venue", "unauthenticated", "authenticated", "other", "all"};
}

