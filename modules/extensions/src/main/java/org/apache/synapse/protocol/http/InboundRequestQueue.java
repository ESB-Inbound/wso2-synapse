package org.apache.synapse.protocol.http;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.apache.synapse.protocol.http.utils.InboundHttpConstants;
import org.apache.synapse.protocol.http.utils.InboundThreadFactory;



import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * keeps inbound requests for processing
 */
public class InboundRequestQueue {

    private static final Log log = LogFactory.getLog(InboundRequestQueue.class);

    private BlockingQueue<InboundSourceRequest> eventQueue;
    private ExecutorService executorService;

    public InboundRequestQueue() {
        executorService = Executors.newFixedThreadPool(InboundHttpConstants.WORKER_POOL_SIZE, new InboundThreadFactory("request"));
        eventQueue = new ArrayBlockingQueue<InboundSourceRequest>(InboundHttpConstants.REQUEST_BUFFER_CAPACITY);
    }

    public void publish(InboundSourceRequest inboundSourceRequest) {
        try {
            eventQueue.put(inboundSourceRequest);
        } catch (InterruptedException e) {
            String logMessage = "Failure to insert request into queue";
            log.warn(logMessage);
        }
        executorService.submit(new InboundSourceRequestWorker(eventQueue));
    }

    @Override
    protected void finalize() throws Throwable {
        executorService.shutdown();
        super.finalize();
    }

}
