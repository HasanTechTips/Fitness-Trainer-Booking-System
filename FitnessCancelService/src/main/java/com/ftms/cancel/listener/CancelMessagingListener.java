package com.ftms.cancel.listener;

import com.ftms.cancel.business.CancelMessagingReceiver;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CancelMessagingListener implements ServletContextListener {
    private Thread receiverThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new CancelMessagingReceiver().startReceiver();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, "cancel-kubemq-receiver");
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (receiverThread != null) {
            receiverThread.interrupt();
        }
    }
}
