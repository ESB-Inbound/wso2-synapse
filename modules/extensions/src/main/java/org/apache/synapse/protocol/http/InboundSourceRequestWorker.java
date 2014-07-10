package org.apache.synapse.protocol.http;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.apache.synapse.transport.nhttp.util.NhttpUtil;



import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

/**
 * working class for queue.poll msgs from queue and send it to the medition engine by creating Syanpse msg context.
 */
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
                SOAPEnvelope soapEnvelope = toSOAPENV(new ByteArrayInputStream(bytes));// building message need to check whether msg should build or not
                msgCtx.setEnvelope(soapEnvelope);
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

        String oriUri = inboundSourceRequest.getTo();
        String restUrlPostfix = NhttpUtil.getRestUrlPostfix(oriUri, axis2MsgCtx.getConfigurationContext().getServicePath());
        msgCtx.setTo(new EndpointReference(oriUri));

        // There is a discrepency in what I thought, Axis2 spawns a nes threads to
        // send a message is this is TRUE - and I want it to be the other way
        msgCtx.setProperty(MessageContext.CLIENT_API_NON_BLOCKING, true);
        return msgCtx;
    }

    private SOAPEnvelope toSOAPENV(InputStream inputStream) throws XMLStreamException {

        try {
            XMLStreamReader reader =
                    XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

            SOAP11Factory f = new SOAP11Factory();

            StAXSOAPModelBuilder builder =

                    OMXMLBuilderFactory.createStAXSOAPModelBuilder(f, reader);
            SOAPEnvelope soapEnvelope = builder.getSOAPEnvelope();
            return soapEnvelope;

        } catch (XMLStreamException e) {
            log.error("Error creating a OMElement from an input stream : ",
                    e);
            throw new XMLStreamException(e);
        }
    }

}
