package org.apache.synapse.protocol.http;


import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.mediators.base.SequenceMediator;


import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

public class InboundSourceRequestWorker implements Runnable {

    private static final Log log = LogFactory.getLog(InboundSourceRequestWorker.class);

    private BlockingQueue<InboundSourceRequest> eventQueue;

    public InboundSourceRequestWorker(BlockingQueue<InboundSourceRequest> eventQueue) {
        this.eventQueue = eventQueue;
    }

    public void run() {
        InboundSourceRequest inboundSourceRequest = eventQueue.poll();
        if (inboundSourceRequest != null) {
            try {
                org.apache.synapse.MessageContext msgCtx = createMessageContext(inboundSourceRequest);
                MessageContext axis2MsgCtx = ((org.apache.synapse.core.axis2.Axis2MessageContext) msgCtx).getAxis2MessageContext();
                byte[] bytes = inboundSourceRequest.getContentBytes();
                OMElement omElement = toOM(new ByteArrayInputStream(bytes));// building message need to check whether msg should build or not
                msgCtx.setEnvelope(TransportUtils.createSOAPEnvelope(omElement));
                msgCtx.setProperty(SynapseConstants.IS_INBOUND, "true");
                msgCtx.setProperty(SynapseConstants.CHANNEL_HANDLER_CONTEXT, inboundSourceRequest.getChannelHandlerContext());
                if (inboundSourceRequest.getInjectSeq() == null || inboundSourceRequest.getInjectSeq().equals("")) {
                    log.error("Sequence name not specified. Sequence : " + inboundSourceRequest.getInjectSeq());
                }
                SequenceMediator seq = (SequenceMediator) inboundSourceRequest.getSynapseEnvironment().getSynapseConfiguration().getSequence(inboundSourceRequest.getInjectSeq());
                seq.setErrorHandler(inboundSourceRequest.getFaultSeq());
                if (seq != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("injecting message to sequence : " + inboundSourceRequest.getInjectSeq());
                    }
                    inboundSourceRequest.getSynapseEnvironment().injectAsync(msgCtx, seq);
                } else {
                    log.error("Sequence: " + inboundSourceRequest.getInjectSeq() + " not found");
                }
            } catch (XMLStreamException e) {
                log.error(e.getMessage());
            } catch (AxisFault axisFault) {
                log.error(axisFault.getMessage());
            }


        }
    }

    private org.apache.synapse.MessageContext createMessageContext(InboundSourceRequest inboundSourceRequest) {
        org.apache.synapse.MessageContext msgCtx = inboundSourceRequest.getSynapseEnvironment().createMessageContext();
        MessageContext axis2MsgCtx = ((org.apache.synapse.core.axis2.Axis2MessageContext) msgCtx).getAxis2MessageContext();
        axis2MsgCtx.setServerSide(true);
        axis2MsgCtx.setMessageID(UUIDGenerator.getUUID());
        // There is a discrepency in what I thought, Axis2 spawns a nes threads to
        // send a message is this is TRUE - and I want it to be the other way
        msgCtx.setProperty(MessageContext.CLIENT_API_NON_BLOCKING, true);
        return msgCtx;
    }

    private OMElement toOM(InputStream inputStream) throws XMLStreamException {

        try {
            XMLStreamReader reader =
                    XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
            StAXOMBuilder builder = new StAXOMBuilder(reader);
            return builder.getDocumentElement();

        } catch (XMLStreamException e) {
            log.error("Error creating a OMElement from an input stream : ",
                    e);
            throw new XMLStreamException(e);
        }
    }

}
