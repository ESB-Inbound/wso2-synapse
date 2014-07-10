package org.apache.synapse.inboundfactory;


import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.inbound.InboundListener;
import org.apache.synapse.inbound.ListenerFactory;
import org.apache.synapse.protocol.http.InboundHttpListener;

public class InboundListenerFactory implements ListenerFactory {
    private static final Object obj = new Object();
    public static enum Protocols {jms, file,http,https};
    public InboundListener createInboundListener(String protocol, int port, SynapseEnvironment synapseEnvironment, String injectingSeq, String onErrorSeq) {
        synchronized (obj) {
            InboundListener inboundListener = null;
            if(Protocols.http.toString().equals(protocol)){
                inboundListener = new InboundHttpListener(port,synapseEnvironment,injectingSeq,onErrorSeq);
            }else if(Protocols.https.toString().equals(protocol)){
               // pollingProcessor = new VFSProcessor(name, properties, scanInterval, injectingSeq, onErrorSeq, synapseEnvironment);
            }
            return inboundListener;
        }
    }
}
