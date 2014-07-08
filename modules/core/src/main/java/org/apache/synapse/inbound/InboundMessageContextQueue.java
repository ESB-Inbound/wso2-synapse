package org.apache.synapse.inbound;

import org.apache.synapse.MessageContext;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Keeps the MessageContext of responses that come to the InboundEndpoint
 */
public class InboundMessageContextQueue {

    private static InboundMessageContextQueue instance = null;
    private BlockingQueue<MessageContext> messageContextQueue;

    private InboundMessageContextQueue(){
        messageContextQueue = new LinkedBlockingQueue<MessageContext>();
    }

    public  static synchronized InboundMessageContextQueue getInstance(){

        if(instance == null){
            instance = new InboundMessageContextQueue();
        }
        return instance;
    }

    public BlockingQueue<MessageContext> getMessageContextQueue(){
        return messageContextQueue;
    }
}
