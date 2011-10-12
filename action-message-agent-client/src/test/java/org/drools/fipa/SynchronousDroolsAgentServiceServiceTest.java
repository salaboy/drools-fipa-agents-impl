    /*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.fipa;

import org.drools.fipa.action.message.Channel;
import org.drools.fipa.body.acts.Inform;
import java.util.Map;
import org.drools.fipa.body.content.Action;
import java.util.LinkedHashMap;
import org.drools.dssagentserver.SynchronousDroolsAgentServiceImpl;
import org.drools.dssagentserver.SynchronousDroolsAgentServiceImplService;

import java.util.List;
import java.util.UUID;
import org.drools.fipa.action.message.invokers.*;
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



        //ACLMessage infoSMSChannel = factory.newInformMessage("me", "you", new Channel("SMS", "http://www.smsgateway.ca/sendsms.asmx"));
        ACLMessage infoSMSChannel = factory.newInformMessage("me", "you", new Channel("DeliverMessage", "http://192.168.5.47:8080/PresentationServices/DSAIntegration"));
        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(infoSMSChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());

        ACLMessage infoMailChannel = factory.newInformMessage("me", "you", new Channel("EMAIL", "http://webservice/mail/invoke"));
        answers = synchronousDroolsAgentServicePort.tell(infoMailChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        ACLMessage infoIdentityChannel = factory.newInformMessage("me", "you", new Channel("IdentityResolverService", "http://192.168.5.47:8080/PresentationServices/DSAIntegration"));
        answers = synchronousDroolsAgentServicePort.tell(infoIdentityChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        ACLMessage infoIdentityChannelInvoker = factory.newInformMessage("me", "you", new MockIdentityChannelInvoker());
        //ACLMessage infoIdentityChannelInvoker = factory.newInformMessage("me", "you", new LDAPIdentityChannelInvoker());
        answers = synchronousDroolsAgentServicePort.tell(infoIdentityChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        //ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new MockSMSChannelInvoker());
        //ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new DeliverMessageChannelInvoker());
        ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new MockDeliverMessageChannelInvoker());

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
        args.put("subjectabout", new int[]{10, 20, 30});
        args.put("sender", "patient33");
        args.put("mainRecipients", "Salaboy");
        args.put("secondaryRecipients", "id2");
        args.put("hiddenRecipients", "id3");
        args.put("header", "this is the Alert header of the message");
        args.put("body", "this is the Alert body of the message");
        args.put("type", "DeliverMessage");
        args.put("priority", "Low");
        args.put("deliveryDate", "Tue Oct 11 23:46:36 CEST 2011");
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
        System.out.println("ANSWER!!!! - > " + ((Inform) answer.getBody()).getProposition().getEncodedContent());

        args = new LinkedHashMap<String, Object>();
        args.put("refId", UUID.randomUUID().toString());
        args.put("conversationId", UUID.randomUUID().toString());
        args.put("subjectabout", new int[]{10, 20, 30});
        args.put("sender", "patient33");
        args.put("mainRecipients", "Salaboy");
        args.put("secondaryRecipients", "id2");
        args.put("hiddenRecipients", "id3");
        args.put("type", "EMAIL");
        args.put("header", "this is the EMAIL header of the message");
        args.put("body", "this is the EMAIL body of the message");
        args.put("priority", "Low");
        args.put("deliveryDate", "Tue Oct 11 23:46:36 CEST 2011");
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
        System.out.println("ANSWER!!!! - > " + ((Inform) answer.getBody()).getProposition().getEncodedContent());

    }

//{refId=284d7e8d-6853-46cb-bef2-3c71e565f90d, 
//    conversationId=502ed27e-682d-43b3-ac2a-8bba3b597d13, 
//            subjectAbout=[Ljava.lang.String;@59aa5257, 
//                    sender=docX, 
//                    mainRecipients=[Ljava.lang.String;@3d03fbbe, 
//                            secondaryRecipients=[Ljava.lang.String;@5084963d, 
//                            hiddenRecipients=[Ljava.lang.String;@30a3a817, 
//                                    type=ALERT, 
//                                    header=Risk threshold exceeded : MockPTSD, 
//                                            body=
//
//
//
//
//<h2>MockPTSD</h2>
//(This is free form HTML with an optionally embedded survey)
//
//Dear docX,
//
//<p>
//Your patient patient33 has a high risk of developing the disease known as MockPTSD.
//The estimated rate is around 30.000000000000004.
//</p>
//
//<p>
//They will contact you shortly.
//</p>
//
//
//MockPTSD:
//<p class='agentEmbed' type='survey' id='01f0bb3d-7f67-450a-ad8d-be29c5611055' /p>
//
//<br/>
//Thank you very much, <br/>
//<p>Your Friendly Clinical Decision Support Agent</p>
//
//, priority=Critical, deliveryDate=Tue Oct 11 23:46:36 CEST 2011, status=New}
    @Test
    public void testNewAlert() {
        SynchronousDroolsAgentServiceImpl synchronousDroolsAgentServicePort = new SynchronousDroolsAgentServiceImplService().getSynchronousDroolsAgentServiceImplPort();

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);



        ACLMessage infoSMSChannel = factory.newInformMessage("me", "you", new Channel("SMS", "http://www.smsgateway.ca/sendsms.asmx"));
        //ACLMessage infoSMSChannel = factory.newInformMessage("me", "you", new Channel("DeliverMessage", "http://192.168.5.47:8080/PresentationServices/DSAIntegration"));
        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(infoSMSChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());

        ACLMessage infoMailChannel = factory.newInformMessage("me", "you", new Channel("EMAIL", "http://webservice/mail/invoke"));
        answers = synchronousDroolsAgentServicePort.tell(infoMailChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        ACLMessage infoIdentityChannel = factory.newInformMessage("me", "you", new Channel("IdentityResolverService", "http://192.168.5.47:8080/PresentationServices/DSAIntegration"));
        answers = synchronousDroolsAgentServicePort.tell(infoIdentityChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        ACLMessage infoIdentityChannelInvoker = factory.newInformMessage("me", "you", new MockIdentityChannelInvoker());
        //ACLMessage infoIdentityChannelInvoker = factory.newInformMessage("me", "you", new LDAPIdentityChannelInvoker());
        answers = synchronousDroolsAgentServicePort.tell(infoIdentityChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        //ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new MockSMSChannelInvoker());
        ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new MockDeliverMessageChannelInvoker());
        //ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new DeliverMessageChannelInvoker());

        answers = synchronousDroolsAgentServicePort.tell(infoSMSChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());

        ACLMessage infoEMAILChannelInvoker = factory.newInformMessage("me", "you", new MockEMAILChannelInvoker());
        answers = synchronousDroolsAgentServicePort.tell(infoEMAILChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("refId", "284d7e8d-6853-46cb-bef2-3c71e565f90d");
        args.put("conversationId", "502ed27e-682d-43b3-ac2a-8bba3b597d13");
        args.put("subjectAbout", "patient1, id1, id2, id3");
        args.put("sender", "docx");
        args.put("mainRecipients", "id1");
        args.put("secondaryRecipients", "id2");
        args.put("hiddenRecipients", "id3");
        args.put("type", "ALERT");
        args.put("header", "Risk threshold exceeded : MockPTSD");
        args.put("body", "<h2>MockPTSD</h2><br/>(This is free form HTML with an optionally embedded survey)<br/>Dear docX,<br/>"
                + "<p>Your patient patient33 has a high risk of developing the disease known as MockPTSD. <br/>"
                + "     The estimated rate is around 30.000000000000004."
                + "</p>"
                + "<p>"
                + "They will contact you shortly."
                + "</p>"
                + "MockPTSD:<p class='agentEmbed' type='survey' id='01f0bb3d-7f67-450a-ad8d-be29c5611055' /p>"
                + "<br/>Thank you very much, <br/><p>Your Friendly Clinical Decision Support Agent</p>");
        args.put("priority", "Critical");
        args.put("deliveryDate", "Tue Oct 11 23:46:36 CEST 2011");
        args.put("status", "New");

        Action action = MessageContentFactory.newActionContent("deliverMessage", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);

        answers = synchronousDroolsAgentServicePort.tell(req);

        assertNotNull(answers);

    }

    //{refId=284d7e8d-6853-46cb-bef2-3c71e565f90d, 
    //conversationId=9571319a-dd35-459b-a337-422487d9aa8c, 
    //subjectAbout=[Ljava.lang.String;@faaf84c, 
    //sender=patient33, 
    //mainRecipients=[Ljava.lang.String;@21934d9d, 
    //secondaryRecipients=[Ljava.lang.String;@4dcc8fa3, 
    //hiddenRecipients=[Ljava.lang.String;@30ea3e3c, 
    //type=UPDATE, 
    //header=null, 
    //body=null, 
    //priority=Critical, 
    //deliveryDate=Tue Oct 11 23:46:36 CEST 2011, 
    //status=100}
    @Test
    public void testUpdateAlert() {
        SynchronousDroolsAgentServiceImpl synchronousDroolsAgentServicePort = new SynchronousDroolsAgentServiceImplService().getSynchronousDroolsAgentServiceImplPort();

        ACLMessageFactory factory = new ACLMessageFactory(Encodings.XML);



        //ACLMessage infoSMSChannel = factory.newInformMessage("me", "you", new Channel("SMS", "http://www.smsgateway.ca/sendsms.asmx"));
        ACLMessage infoSMSChannel = factory.newInformMessage("me", "you", new Channel("DeliverMessage", "http://192.168.5.47:8080/PresentationServices/DSAIntegration"));
        List<ACLMessage> answers = synchronousDroolsAgentServicePort.tell(infoSMSChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());

        ACLMessage infoMailChannel = factory.newInformMessage("me", "you", new Channel("EMAIL", "http://webservice/mail/invoke"));
        answers = synchronousDroolsAgentServicePort.tell(infoMailChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        ACLMessage infoIdentityChannel = factory.newInformMessage("me", "you", new Channel("IdentityResolverService", "http://192.168.5.47:8080/PresentationServices/DSAIntegration"));
        answers = synchronousDroolsAgentServicePort.tell(infoIdentityChannel);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        ACLMessage infoIdentityChannelInvoker = factory.newInformMessage("me", "you", new MockIdentityChannelInvoker());
        //ACLMessage infoIdentityChannelInvoker = factory.newInformMessage("me", "you", new LDAPIdentityChannelInvoker());
        answers = synchronousDroolsAgentServicePort.tell(infoIdentityChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());

        ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new MockDeliverMessageChannelInvoker());
        //ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new MockSMSChannelInvoker());
        //ACLMessage infoSMSChannelInvoker = factory.newInformMessage("me", "you", new DeliverMessageChannelInvoker());

        answers = synchronousDroolsAgentServicePort.tell(infoSMSChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());

        ACLMessage infoEMAILChannelInvoker = factory.newInformMessage("me", "you", new MockEMAILChannelInvoker());
        answers = synchronousDroolsAgentServicePort.tell(infoEMAILChannelInvoker);
        assertNotNull(answers);
        assertEquals(0, answers.size());


        Map<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("refId", "284d7e8d-6853-46cb-bef2-3c71e565f90d");
        args.put("conversationId", "9571319a-dd35-459b-a337-422487d9aa8c");
        args.put("subjectAbout", "patient1, id1, id2, id3");
        args.put("sender", "patient33");
        args.put("mainRecipients", "id1");
        args.put("secondaryRecipients", "id2");
        args.put("hiddenRecipients", "id3");
        args.put("type", "UPDATE");
        args.put("header", "");
        args.put("body", "");
        args.put("priority", "Critical");
        args.put("deliveryDate", "Tue Oct 11 23:46:36 CEST 2011");
        args.put("status", "100");

        Action action = MessageContentFactory.newActionContent("deliverMessage", args);
        ACLMessage req = factory.newRequestMessage("me", "you", action);

        answers = synchronousDroolsAgentServicePort.tell(req);

        assertNotNull(answers);

    }
}
