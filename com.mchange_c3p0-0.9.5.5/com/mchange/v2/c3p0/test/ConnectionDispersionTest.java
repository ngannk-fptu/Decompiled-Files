/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.sql.DataSource;

public final class ConnectionDispersionTest {
    private static final int DELAY_TIME = 120000;
    private static final int NUM_THREADS = 600;
    private static final Integer ZERO = new Integer(0);
    private static boolean should_go = false;
    private static DataSource cpds;
    private static int ready_count;

    private static synchronized void setDataSource(DataSource ds) {
        cpds = ds;
    }

    private static synchronized DataSource getDataSource() {
        return cpds;
    }

    private static synchronized int ready() {
        return ++ready_count;
    }

    private static synchronized boolean isReady() {
        return ready_count == 600;
    }

    private static synchronized void start() {
        should_go = true;
        ConnectionDispersionTest.class.notifyAll();
    }

    private static synchronized void stop() {
        should_go = false;
        ConnectionDispersionTest.class.notifyAll();
    }

    private static synchronized boolean shouldGo() {
        return should_go;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] argv) {
        String jdbc_url = null;
        String username = null;
        String password = null;
        if (argv.length == 3) {
            jdbc_url = argv[0];
            username = argv[1];
            password = argv[2];
        } else if (argv.length == 1) {
            jdbc_url = argv[0];
            username = null;
            password = null;
        } else {
            ConnectionDispersionTest.usage();
        }
        if (!jdbc_url.startsWith("jdbc:")) {
            ConnectionDispersionTest.usage();
        }
        try {
            ComboPooledDataSource ds = new ComboPooledDataSource();
            ds.setJdbcUrl(jdbc_url);
            ds.setUser(username);
            ds.setPassword(password);
            ConnectionDispersionTest.setDataSource(ds);
            ds.getConnection().close();
            System.err.println("Generating thread list...");
            ArrayList<CompeteThread> threads = new ArrayList<CompeteThread>(600);
            for (int i = 0; i < 600; ++i) {
                CompeteThread t = new CompeteThread();
                t.start();
                threads.add(t);
                Thread.currentThread();
                Thread.yield();
            }
            System.err.println("Thread list generated.");
            Class<ConnectionDispersionTest> i = ConnectionDispersionTest.class;
            synchronized (ConnectionDispersionTest.class) {
                while (!ConnectionDispersionTest.isReady()) {
                    ConnectionDispersionTest.class.wait();
                }
                // ** MonitorExit[i] (shouldn't be in output)
                System.err.println("Starting the race.");
                ConnectionDispersionTest.start();
                System.err.println("Sleeping 120.0 seconds to let the race run");
                Thread.sleep(120000L);
                System.err.println("Stopping the race.");
                ConnectionDispersionTest.stop();
                System.err.println("Waiting for Threads to complete.");
                for (int i2 = 0; i2 < 600; ++i2) {
                    ((Thread)threads.get(i2)).join();
                }
                TreeMap<Integer, Integer> outcomeMap = new TreeMap<Integer, Integer>();
                for (int i3 = 0; i3 < 600; ++i3) {
                    Integer outcome = new Integer(((CompeteThread)threads.get(i3)).getCount());
                    Integer old = (Integer)outcomeMap.get(outcome);
                    if (old == null) {
                        old = ZERO;
                    }
                    outcomeMap.put(outcome, new Integer(old + 1));
                }
                int last = 0;
                for (Integer outcome : outcomeMap.keySet()) {
                    Integer count = (Integer)outcomeMap.get(outcome);
                    int oc = outcome;
                    int c = count;
                    while (last < oc) {
                        System.out.println(String.valueOf(10000 + last).substring(1) + ": ");
                        ++last;
                    }
                    ++last;
                    System.out.print(String.valueOf(10000 + oc).substring(1) + ": ");
                    for (int i4 = 0; i4 < c; ++i4) {
                        System.out.print('*');
                    }
                    System.out.println();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        System.err.println("java -Djdbc.drivers=<comma_sep_list_of_drivers> " + ConnectionDispersionTest.class.getName() + " <jdbc_url> [<username> <password>]");
        System.exit(-1);
    }

    static {
        ready_count = 0;
    }

    static class CompeteThread
    extends Thread {
        DataSource ds;
        int count;

        CompeteThread() {
        }

        synchronized void increment() {
            ++this.count;
        }

        synchronized int getCount() {
            return this.count;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                this.ds = ConnectionDispersionTest.getDataSource();
                Class<ConnectionDispersionTest> clazz = ConnectionDispersionTest.class;
                synchronized (ConnectionDispersionTest.class) {
                    ConnectionDispersionTest.ready();
                    ConnectionDispersionTest.class.wait();
                    // ** MonitorExit[var1_1] (shouldn't be in output)
                    while (ConnectionDispersionTest.shouldGo()) {
                        Connection c = null;
                        ResultSet rs = null;
                        try {
                            c = this.ds.getConnection();
                            this.increment();
                            rs = c.getMetaData().getTables(null, null, "PROBABLYNOT", new String[]{"TABLE"});
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                        finally {
                            try {
                                if (rs != null) {
                                    rs.close();
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                if (c == null) continue;
                                c.close();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

