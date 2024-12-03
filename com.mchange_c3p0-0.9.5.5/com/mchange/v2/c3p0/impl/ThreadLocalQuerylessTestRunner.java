/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.impl.DefaultConnectionTester;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLogger;
import java.sql.Connection;
import java.util.Map;
import java.util.WeakHashMap;

class ThreadLocalQuerylessTestRunner
implements DefaultConnectionTester.QuerylessTestRunner {
    static final MLogger logger = DefaultConnectionTester.logger;
    private static final ThreadLocal classToTestRunnerThreadLocal = new ThreadLocal(){

        protected Object initialValue() {
            return new WeakHashMap();
        }
    };
    private static final Class[] ARG_ARRAY = new Class[]{Integer.TYPE};

    ThreadLocalQuerylessTestRunner() {
    }

    private static Map classToTestRunner() {
        return (Map)classToTestRunnerThreadLocal.get();
    }

    private static DefaultConnectionTester.QuerylessTestRunner findTestRunner(Class cClass) {
        try {
            cClass.getDeclaredMethod("isValid", ARG_ARRAY);
            return DefaultConnectionTester.IS_VALID;
        }
        catch (NoSuchMethodException e) {
            return DefaultConnectionTester.METADATA_TABLESEARCH;
        }
        catch (SecurityException e) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.log(MLevel.WARNING, "Huh? SecurityException while reflectively checking for " + cClass.getName() + ".isValid(). Defaulting to traditional (slow) queryless test.");
            }
            return DefaultConnectionTester.METADATA_TABLESEARCH;
        }
    }

    @Override
    public int activeCheckConnectionNoQuery(Connection c, Throwable[] rootCauseOutParamHolder) {
        Class<?> cClass;
        Map map = ThreadLocalQuerylessTestRunner.classToTestRunner();
        DefaultConnectionTester.QuerylessTestRunner qtl = (DefaultConnectionTester.QuerylessTestRunner)map.get(cClass = c.getClass());
        if (qtl == null) {
            qtl = ThreadLocalQuerylessTestRunner.findTestRunner(cClass);
            map.put(cClass, qtl);
        }
        return qtl.activeCheckConnectionNoQuery(c, rootCauseOutParamHolder);
    }
}

