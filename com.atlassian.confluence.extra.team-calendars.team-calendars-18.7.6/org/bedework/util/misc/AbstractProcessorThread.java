/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.misc;

import org.apache.log4j.Logger;

public abstract class AbstractProcessorThread
extends Thread {
    private transient Logger log;
    protected boolean debug = this.getLogger().isDebugEnabled();
    protected boolean running;
    private boolean showedTrace;

    public AbstractProcessorThread(String name) {
        super(name);
    }

    public abstract void runInit();

    public abstract void runProcess() throws Throwable;

    public abstract void close();

    public boolean handleException(Throwable val) {
        return false;
    }

    public void setRunning(boolean val) {
        this.running = val;
    }

    public boolean getRunning() {
        return this.running;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        this.info("************************************************************");
        this.info(" * Starting " + this.getName());
        this.runInit();
        this.info("************************************************************");
        long lastErrorTime = 0L;
        long errorResetTime = 300000L;
        int errorCt = 0;
        int maxErrorCt = 5;
        while (this.running) {
            try {
                this.runProcess();
            }
            catch (InterruptedException ie) {
                this.running = false;
                break;
            }
            catch (Throwable t) {
                if (!this.handleException(t)) {
                    if (System.currentTimeMillis() - lastErrorTime > 300000L) {
                        errorCt = 0;
                    }
                    if (errorCt > 5) {
                        this.error("Too many errors: stopping");
                        this.running = false;
                        break;
                    }
                    lastErrorTime = System.currentTimeMillis();
                    ++errorCt;
                    if (!this.showedTrace) {
                        this.error(t);
                    } else {
                        this.error(t.getMessage());
                    }
                }
            }
            finally {
                this.close();
            }
            this.info("************************************************************");
            this.info(" * " + this.getName() + " terminated");
            this.info("************************************************************");
        }
    }

    public static boolean stopProcess(AbstractProcessorThread proc) {
        proc.info("************************************************************");
        proc.info(" * Stopping " + proc.getName());
        proc.info("************************************************************");
        proc.setRunning(false);
        proc.interrupt();
        boolean ok = true;
        try {
            proc.join(20000L);
        }
        catch (InterruptedException interruptedException) {
        }
        catch (Throwable t) {
            proc.error("Error waiting for processor termination");
            proc.error(t);
            ok = false;
        }
        proc.info("************************************************************");
        proc.info(" * " + proc.getName() + " terminated");
        proc.info("************************************************************");
        return ok;
    }

    protected void info(String msg) {
        this.getLogger().info(msg);
    }

    protected void warn(String msg) {
        this.getLogger().warn(msg);
    }

    protected void debug(String msg) {
        this.getLogger().debug(msg);
    }

    protected void error(Throwable t) {
        this.getLogger().error(this, t);
    }

    protected void error(String msg) {
        this.getLogger().error(msg);
    }

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(this.getClass());
        }
        return this.log;
    }
}

