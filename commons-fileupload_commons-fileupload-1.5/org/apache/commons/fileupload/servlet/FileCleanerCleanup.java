/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  org.apache.commons.io.FileCleaningTracker
 */
package org.apache.commons.fileupload.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.io.FileCleaningTracker;

public class FileCleanerCleanup
implements ServletContextListener {
    public static final String FILE_CLEANING_TRACKER_ATTRIBUTE = FileCleanerCleanup.class.getName() + ".FileCleaningTracker";

    public static FileCleaningTracker getFileCleaningTracker(ServletContext pServletContext) {
        return (FileCleaningTracker)pServletContext.getAttribute(FILE_CLEANING_TRACKER_ATTRIBUTE);
    }

    public static void setFileCleaningTracker(ServletContext pServletContext, FileCleaningTracker pTracker) {
        pServletContext.setAttribute(FILE_CLEANING_TRACKER_ATTRIBUTE, (Object)pTracker);
    }

    public void contextInitialized(ServletContextEvent sce) {
        FileCleanerCleanup.setFileCleaningTracker(sce.getServletContext(), new FileCleaningTracker());
    }

    public void contextDestroyed(ServletContextEvent sce) {
        FileCleanerCleanup.getFileCleaningTracker(sce.getServletContext()).exitWhenFinished();
    }
}

