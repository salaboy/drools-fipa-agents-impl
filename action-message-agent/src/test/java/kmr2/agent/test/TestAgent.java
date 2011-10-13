package kmr2.agent.test;

import org.drools.fipa.*;
import org.drools.fipa.body.acts.Inform;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class TestAgent {



    private static DroolsAgent mainAgent;

    private static MockResponseInformer mainResponseInformer;


    private ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );


    @BeforeClass
    public static void createAgents() {

        mainResponseInformer = new MockResponseInformer();

        DroolsAgentConfiguration mainConfig = new DroolsAgentConfiguration();
        mainConfig.setAgentId( "Mock Test Agent" );
        mainConfig.setChangeset( "default/actionAgent_changeset.xml" );
        mainConfig.setDefaultSubsessionChangeSet( "default/actionAgent_defSession_changeset.xml" );
        mainConfig.setResponseInformer( mainResponseInformer );
        DroolsAgentConfiguration.SubSessionDescriptor subDescr1 = new DroolsAgentConfiguration.SubSessionDescriptor(
                "session1",
                "default/actionAgent_defSession_changeset.xml",
                "NOT_USED_YET" );
        mainConfig.addSubSession( subDescr1 );
        mainAgent = DroolsAgentFactory.getInstance().spawn( mainConfig );

    }



    @AfterClass
    public static void cleanUp() {
        if (mainAgent != null) {
            mainAgent.dispose();
        }

    }


    private void sleep( long millis ) {
        try {
            Thread.sleep( millis );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }





    private String ret(ACLMessage ans) {
        return ((Inform) ans.getBody()).getProposition().getEncodedContent();
    }





    @Test
    public void testDeliverMsg() {

        LinkedHashMap<String, Object> args = new LinkedHashMap<String, Object>();
        args.put("refId", "284d7e8d-6853-46cb-bef2-3c71e565f90d");
        args.put("conversationId", "502ed27e-682d-43b3-ac2a-8bba3b597d13");
        args.put("subjectAbout", new String[] { "patient1", "docx", "id1", "id2", "id3" } );
        args.put("sender", "docx");
        args.put("mainRecipients", new String[] {"id1"} );
        args.put("secondaryRecipients", new String[] {"id2"});
        args.put("hiddenRecipients", new String[] {"id3"});
        args.put("type", "ALERT");
        args.put("header", "Risk threshold exceeded : MockPTSD (30%)");
        args.put("body", "<h2>MockPTSD</h2><br/>(This is free form HTML with an optionally embedded survey)<br/>Dear @{recipient.displayName},<br/>"
                + "<p>Your patient @{patient.displayName} has a high risk of developing the disease known as MockPTSD. <br/>"
                + "     The estimated rate is around 30.000000000000004%."
                + "</p>"
                + "<p>"
                + "They will contact you shortly."
                + "</p>"
                + "MockPTSD:<p class='agentEmbed' type='survey' id='01f0bb3d-7f67-450a-ad8d-be29c5611055' /p>"
                + "<br/>Thank you very much, <br/><p>Your Friendly Clinical Decision Support Agent, on behalf of @{provider.displayName}</p>");
        args.put("priority", "Critical");
        args.put("deliveryDate", "Tue Oct 11 23:46:36 CEST 2011");
        args.put("status", "New");

        ACLMessage req = factory.newRequestMessage( "me","you",  MessageContentFactory.newActionContent("deliverMessage", args) );
        mainAgent.tell(req);
        ACLMessage ansM = mainResponseInformer.getResponses(req).get(1);
        String ans = ret( ansM );


        System.err.println("----------------------------------------------------------");
        Collection c = mainAgent.getInnerSession("session1").getObjects();
        for ( Object o : c ) {
            System.err.println(o);
        }
        System.err.println("----------------------------------------------------------");
    }







}





class MockResponseInformer implements DroolsAgentResponseInformer {

    private Map<ACLMessage,List<ACLMessage>> responses = new HashMap<ACLMessage, List<ACLMessage>>();

    public synchronized void informResponse(ACLMessage originalMessage, ACLMessage response) {
        if (!responses.containsKey(originalMessage)){
            responses.put(originalMessage, new ArrayList<ACLMessage>());
        }

        responses.get(originalMessage).add(response);
    }

    public List<ACLMessage> getResponses(ACLMessage originalMessage){
        return this.responses.get(originalMessage);
    }

}