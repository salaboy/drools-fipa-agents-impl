package org.drools.fipa.client.helpers;

import org.drools.dssagentserver.SynchronousDroolsAgentServiceImpl;
import org.drools.dssagentserver.SynchronousDroolsAgentServiceImplService;
import org.drools.fipa.*;
import org.drools.fipa.body.acts.AbstractMessageBody;
import org.drools.fipa.body.acts.Inform;
import org.drools.fipa.body.acts.InformRef;
import org.drools.fipa.body.content.Action;
import org.drools.fipa.body.content.Ref;
import org.drools.runtime.rule.Variable;

import java.util.LinkedHashMap;
import java.util.List;

public class SynchronousRequestHelper {

        boolean multiReturnValue = false;

        private AbstractMessageBody returnBody;

        private Encodings encode = Encodings.XML;

        public SynchronousRequestHelper() {

        }

        public SynchronousRequestHelper( Encodings enc ) {
            encode = enc;
        }


        public void invokeRequest( String methodName, LinkedHashMap<String,Object> args ) throws UnsupportedOperationException {
            invokeRequest( "", "", methodName, args );
        }

        public void invokeRequest( String sender, String receiver, String methodName, LinkedHashMap<String,Object> args ) throws UnsupportedOperationException {
            multiReturnValue = false;
            for (Object o : args.values()) {
                if ( o == Variable.v ) {
                    multiReturnValue = true;
                    break;
                }
            }
            SynchronousDroolsAgentServiceImpl synchronousDroolsAgentServicePort = new SynchronousDroolsAgentServiceImplService().getSynchronousDroolsAgentServiceImplPort();
            ACLMessageFactory factory = new ACLMessageFactory( encode );

            Action action = MessageContentFactory.newActionContent(methodName, args);
            ACLMessage req = factory.newRequestMessage(sender, receiver, action);

            List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(req);

            ACLMessage answer = answers.get(0);
            if ( ! Act.AGREE.equals( answer.getPerformative() ) ) {
                throw new UnsupportedOperationException(" Request " + methodName + " was not agreed with args " + args );
            }

            if ( ! multiReturnValue ) {
                returnBody = ((Inform) answers.get(1).getBody());
            } else {
                returnBody = ((InformRef) answers.get(1).getBody());
            }

        }


        public Object getReturn( boolean decode ) throws UnsupportedOperationException {
            if ( decode ) {
                MessageContentEncoder.decodeBody( returnBody, encode );
                return ((Inform) returnBody).getProposition().getData();
            } else {
                return ((Inform) returnBody).getProposition().getEncodedContent();
            }
        }


}