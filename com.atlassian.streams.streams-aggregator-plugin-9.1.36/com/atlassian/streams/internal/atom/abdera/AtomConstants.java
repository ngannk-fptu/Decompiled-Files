/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.internal.atom.abdera;

import javax.xml.namespace.QName;
import org.apache.abdera.util.Constants;

public interface AtomConstants
extends Constants {
    public static final String ACTIVITY_PREFIX = "activity";
    public static final String ATLASSIAN_PREFIX = "atlassian";
    public static final String MEDIA_PREFIX = "media";
    public static final String USR_PREFIX = "usr";
    public static final String ACTIVITY_NS = "http://activitystrea.ms/spec/1.0/";
    public static final String ATLASSIAN_NS = "http://streams.atlassian.com/syndication/general/1.0";
    public static final String MEDIA_NS = "http://purl.org/syndication/atommedia";
    public static final String USR_NS = "http://streams.atlassian.com/syndication/username/1.0";
    public static final String ACTIVITY_STREAMS_PERSON_TYPE = "http://activitystrea.ms/schema/1.0/person";
    public static final String LN_OBJECT = "object";
    public static final String LN_OBJECT_TYPE = "object-type";
    public static final String LN_VERB = "verb";
    public static final String LN_TARGET = "target";
    public static final String LN_APPLICATION = "application";
    public static final String LN_APPLICATION_ID = "application-id";
    public static final String LN_APPLICATION_NAME = "application-name";
    public static final String LN_APPLICATION_URI = "application-uri";
    public static final String LN_HEIGHT = "height";
    public static final String LN_WIDTH = "width";
    public static final String LN_USERNAME = "username";
    public static final String LN_AUTHORISATION_MESSAGE = "authorisation-message";
    public static final String LN_AUTHORISATION_URI = "authorisation-uri";
    public static final String LN_TIMEZONE_OFFSET = "timezone-offset";
    public static final String LN_TIMED_OUT_ACTIVITY_SOURCE_LIST = "timed-out-source-list";
    public static final String LN_THROTTLED_ACTIVITY_SOURCE_LIST = "throttled-source-list";
    public static final String LN_TIMED_OUT_ACTIVITY_SOURCE = "activity-source";
    public static final String LN_THROTTLED_ACTIVITY_SOURCE = "activity-source";
    public static final String LN_BANNED_ACTIVITY_SOURCE_LIST = "banned-source-list";
    public static final String LN_BANNED_ACTIVITY_SOURCE = "activity-source";
    public static final QName ACTIVITY_VERB = new QName("http://activitystrea.ms/spec/1.0/", "verb", "activity");
    public static final QName ACTIVITY_OBJECT = new QName("http://activitystrea.ms/spec/1.0/", "object", "activity");
    public static final QName ACTIVITY_TARGET = new QName("http://activitystrea.ms/spec/1.0/", "target", "activity");
    public static final QName ACTIVITY_OBJECT_TYPE = new QName("http://activitystrea.ms/spec/1.0/", "object-type", "activity");
    public static final QName ATLASSIAN_APPLICATION = new QName("http://streams.atlassian.com/syndication/general/1.0", "application", "atlassian");
    public static final QName ATLASSIAN_APPLICATION_ID = new QName("http://streams.atlassian.com/syndication/general/1.0", "application-id", "atlassian");
    public static final QName ATLASSIAN_APPLICATION_NAME = new QName("http://streams.atlassian.com/syndication/general/1.0", "application-name", "atlassian");
    public static final QName ATLASSIAN_APPLICATION_URI = new QName("http://streams.atlassian.com/syndication/general/1.0", "application-uri", "atlassian");
    public static final QName ATLASSIAN_AUTHORISATION_MESSAGE = new QName("http://streams.atlassian.com/syndication/general/1.0", "authorisation-message", "atlassian");
    public static final QName ATLASSIAN_AUTHORISATION_URI = new QName("http://streams.atlassian.com/syndication/general/1.0", "authorisation-uri", "atlassian");
    public static final QName ATLASSIAN_TIMEZONE_OFFSET = new QName("http://streams.atlassian.com/syndication/general/1.0", "timezone-offset", "atlassian");
    public static final QName ATLASSIAN_TIMED_OUT_ACTIVITY_SOURCE_LIST = new QName("http://streams.atlassian.com/syndication/general/1.0", "timed-out-source-list", "atlassian");
    public static final QName ATLASSIAN_THROTTLED_ACTIVITY_SOURCE_LIST = new QName("http://streams.atlassian.com/syndication/general/1.0", "throttled-source-list", "atlassian");
    public static final QName ATLASSIAN_TIMED_OUT_ACTIVITY_SOURCE = new QName("http://streams.atlassian.com/syndication/general/1.0", "activity-source", "atlassian");
    public static final QName ATLASSIAN_THROTTLED_ACTIVITY_SOURCE = new QName("http://streams.atlassian.com/syndication/general/1.0", "activity-source", "atlassian");
    public static final QName ATLASSIAN_BANNED_ACTIVITY_SOURCE_LIST = new QName("http://streams.atlassian.com/syndication/general/1.0", "banned-source-list", "atlassian");
    public static final QName ATLASSIAN_BANNED_ACTIVITY_SOURCE = new QName("http://streams.atlassian.com/syndication/general/1.0", "activity-source", "atlassian");
    public static final QName USR_USERNAME = new QName("http://streams.atlassian.com/syndication/username/1.0", "username", "usr");
    public static final QName MEDIA_HEIGHT = new QName("http://purl.org/syndication/atommedia", "height", "media");
    public static final QName MEDIA_WIDTH = new QName("http://purl.org/syndication/atommedia", "width", "media");
}

