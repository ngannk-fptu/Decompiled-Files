/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.axis.monitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SOAPMonitorService
extends HttpServlet {
    private static ServerSocket server_socket = null;
    private static Vector connections = null;

    public static void publishMessage(Long id, Integer type, String target, String soap) {
        if (connections != null) {
            Enumeration e = connections.elements();
            while (e.hasMoreElements()) {
                ConnectionThread ct = (ConnectionThread)e.nextElement();
                ct.publishMessage(id, type, target, soap);
            }
        }
    }

    public void init() throws ServletException {
        if (connections == null) {
            connections = new Vector();
        }
        if (server_socket == null) {
            ServletConfig config = super.getServletConfig();
            String port = config.getInitParameter("SOAPMonitorPort");
            if (port == null) {
                port = "0";
            }
            try {
                server_socket = new ServerSocket(Integer.parseInt(port));
            }
            catch (Exception e) {
                server_socket = null;
            }
            if (server_socket != null) {
                new Thread(new ServerSocketThread()).start();
            }
        }
    }

    public void destroy() {
        Enumeration e = connections.elements();
        while (e.hasMoreElements()) {
            ConnectionThread ct = (ConnectionThread)e.nextElement();
            ct.close();
        }
        if (server_socket != null) {
            try {
                server_socket.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            server_socket = null;
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int port = 0;
        if (server_socket != null) {
            port = server_socket.getLocalPort();
        }
        response.setContentType("text/html");
        response.getWriter().println("<html>");
        response.getWriter().println("<head>");
        response.getWriter().println("<title>SOAP Monitor</title>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        response.getWriter().println("<object classid=\"clsid:8AD9C840-044E-11D1-B3E9-00805F499D93\" width=100% height=100% codebase=\"http://java.sun.com/products/plugin/1.3/jinstall-13-win32.cab#Version=1,3,0,0\">");
        response.getWriter().println("<param name=code value=SOAPMonitorApplet.class>");
        response.getWriter().println("<param name=\"type\" value=\"application/x-java-applet;version=1.3\">");
        response.getWriter().println("<param name=\"scriptable\" value=\"false\">");
        response.getWriter().println("<param name=\"port\" value=\"" + port + "\">");
        response.getWriter().println("<comment>");
        response.getWriter().println("<embed type=\"application/x-java-applet;version=1.3\" code=SOAPMonitorApplet.class width=100% height=100% port=\"" + port + "\" scriptable=false pluginspage=\"http://java.sun.com/products/plugin/1.3/plugin-install.html\">");
        response.getWriter().println("<noembed>");
        response.getWriter().println("</comment>");
        response.getWriter().println("</noembed>");
        response.getWriter().println("</embed>");
        response.getWriter().println("</object>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
    }

    class ConnectionThread
    implements Runnable {
        private Socket socket = null;
        private ObjectInputStream in = null;
        private ObjectOutputStream out = null;
        private boolean closed = false;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public ConnectionThread(Socket s) {
            this.socket = s;
            try {
                this.out = new ObjectOutputStream(this.socket.getOutputStream());
                this.out.flush();
                this.in = new ObjectInputStream(this.socket.getInputStream());
            }
            catch (Exception exception) {
                // empty catch block
            }
            Vector vector = connections;
            synchronized (vector) {
                connections.addElement(this);
            }
        }

        public void close() {
            this.closed = true;
            try {
                this.socket.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            try {
                while (!this.closed) {
                    Object o = this.in.readObject();
                }
            }
            catch (Exception e) {
                // empty catch block
            }
            Vector e = connections;
            synchronized (e) {
                connections.removeElement(this);
            }
            if (this.out != null) {
                try {
                    this.out.close();
                }
                catch (IOException ioe) {
                    // empty catch block
                }
                this.out = null;
            }
            if (this.in != null) {
                try {
                    this.in.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.in = null;
            }
            this.close();
        }

        public synchronized void publishMessage(Long id, Integer message_type, String target, String soap) {
            if (this.out != null) {
                try {
                    switch (message_type) {
                        case 0: {
                            this.out.writeObject(message_type);
                            this.out.writeObject(id);
                            this.out.writeObject(target);
                            this.out.writeObject(soap);
                            this.out.flush();
                            break;
                        }
                        case 1: {
                            this.out.writeObject(message_type);
                            this.out.writeObject(id);
                            this.out.writeObject(soap);
                            this.out.flush();
                        }
                    }
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
        }
    }

    class ServerSocketThread
    implements Runnable {
        ServerSocketThread() {
        }

        public void run() {
            while (server_socket != null) {
                try {
                    Socket socket = server_socket.accept();
                    new Thread(new ConnectionThread(socket)).start();
                }
                catch (IOException iOException) {}
            }
        }
    }
}

