package org.apache.synapse.protocol.http;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.BlockingQueue;

public class InboundSourceRequestWorker implements Runnable {

    private static final Log log = LogFactory.getLog(InboundSourceRequestWorker.class);

    private BlockingQueue<InboundSourceRequest> eventQueue;

    public InboundSourceRequestWorker(BlockingQueue<InboundSourceRequest> eventQueue){
        this.eventQueue = eventQueue;
    }

    public void run() {

    }



}
