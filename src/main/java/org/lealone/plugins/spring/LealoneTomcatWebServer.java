/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.plugins.spring;

import java.util.concurrent.CountDownLatch;

import org.lealone.main.Lealone;
import org.lealone.plugins.tomcat.TomcatServer;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServerException;

public class LealoneTomcatWebServer extends TomcatWebServer {

    private final TomcatServer server;

    public LealoneTomcatWebServer(TomcatServer server) {
        super(server.getTomcat());
        this.server = server;

        // DaemonAwaitThread不是必需的
        server.getTomcat().getServer().setPort(-2);

        // 提前启动
        startLealone();
    }

    @Override
    public void start() throws WebServerException {
        super.start();
        server.start();
        // SchedulerFactory.initDefaultSchedulerFactory(GlobalScheduler.class.getName(), server.getConfig())
        // .start();
    }

    private void startLealone() {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            new Lealone().start(new String[0], latch);
        }).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
    }
}
