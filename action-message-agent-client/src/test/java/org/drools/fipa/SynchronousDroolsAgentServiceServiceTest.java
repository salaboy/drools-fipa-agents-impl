/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa;

import java.util.Map;
import org.drools.fipa.body.content.Action;
import java.util.LinkedHashMap;
import org.drools.dssagentserver.SynchronousDroolsAgentServiceImpl;
import org.drools.dssagentserver.SynchronousDroolsAgentServiceImplService;

import org.drools.fipa.body.acts.Inform;
import java.util.List;
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
        args.put("x", new Double(36));

        Action action = MessageContentFactory.newActionContent("deliverMessage", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);

        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(req);

        assertNotNull(answers);
        assertEquals(2, answers.size());

        ACLMessage answer = answers.get(0);
        assertEquals(Act.AGREE, answer.getPerformative());
        ACLMessage answer2 = answers.get(1);
        assertEquals(Act.INFORM, answer2.getPerformative());

        assertTrue(((Inform) answer2.getBody()).getProposition().getEncodedContent().contains("6.0"));

    }

}
