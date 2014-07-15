package org.apache.synapse.inbound;


import org.apache.log4j.Logger;
import org.apache.synapse.MessageContext;
import sun.misc.Service;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


import org.apache.synapse.MessageContext;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Keeps the MessageContext of responses that come to the InboundEndpoint
 */
public class InboundMessageContextQueue {

    private static InboundMessageContextQueue instance = null;
    private BlockingQueue<MessageContext> messageContextQueue;

     private static Logger logger = Logger.getLogger(InboundMessageContextQueue.class);



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

    public void publish(MessageContext smc){
        try {
            messageContextQueue.put(smc);
        } catch (InterruptedException e) {
          logger.error("Error when adding Message Contect to Inbound Message Context Queue "+e.getMessage());
        }

    }


}
