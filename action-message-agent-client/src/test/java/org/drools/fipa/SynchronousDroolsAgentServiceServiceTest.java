/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa;

import org.drools.fipa.action.message.invokers.MockEMAILChannelInvoker;
import org.drools.fipa.action.message.invokers.MockSMSChannelInvoker;
import org.drools.fipa.action.message.invokers.MockIdentityChannelInvoker;
import org.drools.fipa.action.message.Channel;
import org.drools.fipa.body.acts.Inform;
import java.util.Map;
import org.drools.fipa.body.content.Action;
import java.util.LinkedHashMap;
import org.drools.dssagentserver.SynchronousDroolsAgentServiceImpl;
import org.drools.dssagentserver.SynchronousDroolsAgentServiceImplService;

import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author salaboy
 */
public class SynchronousDroolsAgentServiceServiceTest {

    public SynchronousDroolsAgentServiceServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testRequest() {
        SynchronousDroolsAgentServiceImpl synchronousDroolsAgentServicePort = new SynchronousDroolsAgentServiceImplService().getSynchronousDroolsAgentServiceImplPort();
                
        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);

        

        ACLMessage infoSMSChannel = factory.newInformMessage("me", "you", new Channel("SMS", "http://www.smsgateway.ca/sendsms.asmx"));
        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(infoSMSChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());
        
        ACLMessage infoMailChannel = factory.newInformMessage("me", "you", new Channel("EMAIL", "http://webservice/mail/invoke"));
        answers = synchronousDroolsAgentServicePort.tell(infoMailChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());
        
        
        ACLMessage infoIdentityChannel = factory.newInformMessage("me", "you", new Channel("IdentityResolverService", "http://webservice/identity/invoke"));
        answers = synchronousDroolsAgentServicePort.tell(infoIdentityChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());
        
        
        ACLMessage infoIdentityChannelInvoker = factory.newInformMessage("me", "you", new MockIdentityChannelInvoker());
        answers = synchronousDroolsAgentServicePort.tell(infoIdentityChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());
        
        
        ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new MockSMSChannelInvoker());
        answers = synchronousDroolsAgentServicePort.tell(infoSMSChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());
        
        ACLMessage infoEMAILChannelInvoker = factory.newInformMessage("me", "you", new MockEMAILChannelInvoker());
        answers = synchronousDroolsAgentServicePort.tell(infoEMAILChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());
        
        
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("refId", UUID.randomUUID().toString());
        args.put("conversationId", UUID.randomUUID().toString());
        args.put("subjectabout", new int[]{10,20,30} );
        args.put("mainRecipients", "Salaboy");
        args.put("header", "this is the SMS header of the message");
        args.put("body", "this is the SMS body of the message");
        args.put("type", "SMS");
        args.put("priority", "Low");
        args.put("status", "New");
        
        

        Action action = MessageContentFactory.newActionContent("deliverMessage", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);

        answers = synchronousDroolsAgentServicePort.tell(req);

        assertNotNull(answers);
        assertEquals(2, answers.size());

        ACLMessage answer = answers.get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        
        answer = answers.get(1);
        assertEquals(Act.INFORM, answer.getPerformative());
        System.out.println("ANSWER!!!! - > "+((Inform)answer.getBody()).getProposition().getEncodedContent());
        
        args = new LinkedHashMap<String, Object>();
        args.put("refId", UUID.randomUUID().toString());
        args.put("conversationId", UUID.randomUUID().toString());
        args.put("subjectabout", new int[]{10,20,30} );
        args.put("mainRecipients", "Salaboy");
        args.put("header", "this is the EMAIL header of the message");
        args.put("body", "this is the EMAIL body of the message");
        args.put("type", "EMAIL");
        args.put("priority", "Low");
        args.put("status", "New");
        
        

        action = MessageContentFactory.newActionContent("deliverMessage", args);
        req = factory.newRequestMessage("me", "you", action);

        answers = synchronousDroolsAgentServicePort.tell(req);

        assertNotNull(answers);
        assertEquals(2, answers.size());

        answer = answers.get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        
        answer = answers.get(1);
        assertEquals(Act.INFORM, answer.getPerformative());
        System.out.println("ANSWER!!!! - > "+((Inform)answer.getBody()).getProposition().getEncodedContent());

    }
    
    
//    

}
