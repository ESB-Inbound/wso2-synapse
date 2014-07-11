package org.apache.synapse.inboundfactory;


import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.inbound.InboundListner;
import org.apache.synapse.inbound.ListnerFactory;
import org.apache.synapse.protocol.http.InboundHttpListner;


public class InboundListnerFactory implements ListnerFactory {
    private static final Object obj = new Object();
    public static enum Protocols {jms, file,http,https};
    public InboundListner creatInboundListner(String protocol,int port, SynapseEnvironment synapseEnvironment, String injectingSeq, String onErrorSeq,String outSequence) {
        synchronized (obj) {
            InboundListner inboundListner = null;
            if(Protocols.http.toString().equals(protocol)){
                inboundListner = new InboundHttpListner(port,synapseEnvironment,injectingSeq,onErrorSeq,outSequence);
            }else if(Protocols.https.toString().equals(protocol)){
               // pollingProcessor = new VFSProcessor(name, properties, scanInterval, injectingSeq, onErrorSeq, synapseEnvironment);
            }
            return inboundListner;
        }
    }
}
