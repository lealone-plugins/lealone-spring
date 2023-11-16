/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.plugins.spring;

import org.lealone.plugins.tomcat.TomcatServer;
import org.lealone.server.Scheduler;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServerException;

public class LealoneTomcatWebServer extends TomcatWebServer {

    private final TomcatServer server;

    public LealoneTomcatWebServer(TomcatServer server) {
        super(server.getTomcat());
        this.server = server;
    }

    @Override
    public void start() throws WebServerException {
        super.start();
        server.start();
        Scheduler.startSchedulers();
    }
}
