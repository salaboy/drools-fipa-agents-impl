/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa;

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

        
        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("refId", UUID.randomUUID().toString());
        args.put("conversationId", UUID.randomUUID().toString());
        args.put("subjectabout", new int[]{10,20,30} );
        args.put("mainRecipients", "Salaboy");
        args.put("header", "this is the header of the message");
        args.put("body", "this is the body of the message");
        args.put("type", "SMS");
        args.put("priority", "Low");
        args.put("status", "New");
        
        

        Action action = MessageContentFactory.newActionContent("deliverMessage", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);

        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(req);

        assertNotNull(answers);
        assertEquals(2, answers.size());

        ACLMessage answer = answers.get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        
        answer = answers.get(1);
        assertEquals(Act.INFORM, answer.getPerformative());
        System.out.println("ANSWER!!!! - > "+((Inform)answer.getBody()).getProposition().getEncodedContent());

    }

}
