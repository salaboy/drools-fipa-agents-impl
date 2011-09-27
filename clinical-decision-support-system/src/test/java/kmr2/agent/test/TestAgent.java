package kmr2.agent.test;

import org.drools.builder.ResourceType;
import org.drools.fipa.*;
import org.drools.fipa.body.acts.AbstractMessageBody;
import org.drools.fipa.body.acts.Inform;
import org.drools.fipa.body.acts.InformRef;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.rule.Variable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class TestAgent {



    private static DroolsAgent mainAgent;

    private static MockResponseInformer mainResponseInformer;


    @BeforeClass
    public static void createAgents() {

        mainResponseInformer = new MockResponseInformer();

        DroolsAgentConfiguration mainConfig = new DroolsAgentConfiguration();
        mainConfig.setAgentId("Mock Test Agent");
        mainConfig.setChangeset("default/agent_changeset.xml");
        mainConfig.setDefaultSubsessionChangeSet("default/subsession_default.xml");
        mainConfig.setResponseInformer(mainResponseInformer);
        DroolsAgentConfiguration.SubSessionDescriptor subDescr1 = new DroolsAgentConfiguration.SubSessionDescriptor(
                "sessionSurvey",
                "survey_test.xml",
                "NOT_USED_YET" );
        mainConfig.addSubSession(subDescr1);
        mainAgent = DroolsAgentFactory.getInstance().spawn( mainConfig );

    }



    @AfterClass
    public static void cleanUp() {
        if (mainAgent != null) {
            mainAgent.dispose();
        }

    }




    @Test
    public void testInform() {
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        ACLMessage info = factory.newInformMessage("client", "DSA",  new MockPatient("patient3", 33) );
        mainAgent.tell( info );

    }


    @Test
    public void testSurvey() {
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId","patient1");
        args.put("patientId","surveyPatient");
        args.put("surveyId","123456UNIQUESURVEYID");

        Collection wm = mainAgent.getInnerSession("sessionSurvey").getObjects();

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getSurvey", args));
        mainAgent.tell(req);
        ACLMessage ans = mainResponseInformer.getResponses(req).get(1);


        System.out.println( ans.getBody() );
    }



    @Test
    public void testGetRisk() {
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
        String mName = "Mock PTSD";
        String mid = "MockPTSD";
        Map<String,Object> args = new LinkedHashMap<String,Object>();


        args.put("userId","docX");
        args.put("patientId","patient33");
        args.put("modelIds",new String[] {mid} );

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModelsDetail", args) );

        mainAgent.tell(req);


        String sid = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Questionnaire", mid, Variable.v ).iterator().next().get("$id");
        String gender = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Gender", mid, Variable.v ).iterator().next().get("$id");
        String deployments = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Deployments", mid, Variable.v ).iterator().next().get("$id");
        String alcohol = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Alcohol", mid, Variable.v ).iterator().next().get("$id");
        String age = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Age", mid, Variable.v ).iterator().next().get("$id");


        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("surveyId",sid);
        ACLMessage getS1 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getSurvey", args) );

        mainAgent.tell(getS1);







        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("questionId",deployments);
        args.put("answer","1");
        ACLMessage setS2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setS2);


        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("questionId",gender);
        args.put("answer","female");
        ACLMessage setS1 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setS1);

        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("questionId",alcohol);
        args.put("answer","yes");
        ACLMessage setS3 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setS3);

        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("questionId",age);
        args.put("answer","30");
        ACLMessage setS4 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setS4);





        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("surveyId",sid);
        ACLMessage getS2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getSurvey", args) );

        mainAgent.tell(getS2);




        args.clear();
        args.put("userId","docX");
        args.put("patientId","patient33");
        args.put("modelIds",new String[] {mid} );

        ACLMessage req2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModelsDetail", args) );
        mainAgent.tell(req2);



        ACLMessage ans1 = mainResponseInformer.getResponses(req).get(1);
        ACLMessage ans2 = mainResponseInformer.getResponses(req2).get(1);

        Collection wm = mainAgent.getInnerSession("patient33").getObjects();
        System.out.println("*********");

        System.out.println( ans1.getBody().toString());
        System.out.println("*********");
        System.out.println( ans2.getBody().toString());
    }



    @Test
    public void testDiagnostic() {

        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
        String mid = "MockDiag";
        String did = "MockDecision";
        Map<String,Object> args = new LinkedHashMap<String,Object>();


        args.clear();
        args.put("providerId","docX");
        args.put("decModelId","MockDecision");
        args.put("diagModelId","MockDiag");
        args.put("patientId", "patient33" );
        ACLMessage start = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("startDiagnosticGuideProcess", args) );

        mainAgent.tell(start);
        ACLMessage ans = mainResponseInformer.getResponses(start).get(1);
        MessageContentEncoder.decodeBody( ans.getBody(), Encodings.XML );
        String dxProcessId = (String) ((Inform) ans.getBody()).getProposition().getData();

        Collection wm = mainAgent.getInnerSession("patient33").getObjects();
        System.out.println("***********************************************************************************************************************");




        args.clear();
        args.put("processId",dxProcessId);
        args.put("refresh",true);
        args.put("patientId", "patient33" );

        ACLMessage reqStatus = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getDiagnosticProcessStatus", args) );

        mainAgent.tell(reqStatus);
        ACLMessage ans2 = mainResponseInformer.getResponses(reqStatus).get(1);
        System.out.println(ans2.getBody());

        System.out.println("***********************************************************************************************************************");






        args.clear();
        args.put("processId",dxProcessId);
        args.put("patientId", "patient33" );

        ACLMessage next = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("advanceDiagnosticGuideProcess", args) );

        mainAgent.tell(next);

        System.out.println("***********************************************************************************************************************");






        args.clear();
        args.put("processId",dxProcessId);
        args.put("status","Complete");
        args.put("patientId", "patient33" );

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("completeDiagnosticGuideProcess", args) );

        mainAgent.tell(req);



        System.out.println("***********************************************************************************************************************");

        args.clear();
        args.put("processId",dxProcessId);
        args.put("refresh",true);
        args.put("patientId", "patient33" );

        ACLMessage reqStatus2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getDiagnosticProcessStatus", args) );

        mainAgent.tell(reqStatus2);
        ACLMessage ans99 = mainResponseInformer.getResponses(reqStatus2).get(1);
        System.out.println(ans99.getBody());




        Collection wm2 = mainAgent.getInnerSession("patient33").getObjects();
        System.out.println("*********");
        System.out.println();



    }



//    @Test
//    public void testRequest() {
//
//        ACLMessageFactory factory = new ACLMessageFactory(ACLMessageFactory.Encodings.XML);
//
//        Map<String,Object> args = new LinkedHashMap<String,Object>();
//        args.put("userId","patient1");
//        args.put("surveyId","123456UNIQUESURVEYID");
//
//
//
//        ACLMessage req = factory.newRequestMessage("me","you",new Action("getSurvey", args));
//
//
//
//        mainAgent.tell(req);
//
//        assertNotNull(mainResponseInformer.getResponses(req));
//        assertEquals(2,mainResponseInformer.getResponses(req).size());
//
//        ACLMessage answer = mainResponseInformer.getResponses(req).get(0);
//        assertEquals(ACLMessage.Act.AGREE,answer.getPerformative());
//        ACLMessage answer2 = mainResponseInformer.getResponses(req).get(1);
//        assertEquals(ACLMessage.Act.INFORM,answer2.getPerformative());
//
//        assertTrue(answer2.getBody().getEncodedContent().contains("SurveyGUIAdapter"));
//        System.err.println("GETSURVEY RESPONSE : " + answer2.getBody().getEncodedContent());
//
//        mainResponseInformer.getResponses(req).clear();
//
//
//
//        for (int j = 0; j < 200; j++) {
//        System.err.println("\n");
//        System.out.println("\n");
//        }
//
//
//        ACLMessage req2 = factory.newRequestMessage("me","you",new Action("getSurvey", args));
//
//        mainAgent.tell(req);
//
//        ACLMessage answer21 = mainResponseInformer.getResponses(req).get(0);
//        assertEquals(ACLMessage.Act.AGREE,answer21.getPerformative());
//        ACLMessage answer22 = mainResponseInformer.getResponses(req).get(1);
//        assertEquals(ACLMessage.Act.INFORM,answer22.getPerformative());
//
//        System.err.println("GETSURVEY RESPONSE : " + answer22.getBody().getEncodedContent());
//
//
//
//
//
//
//    }
//
//
//
//
//    @Test
//    public void testRequestNamedOutputs() {
//
//        ACLMessageFactory factory = new ACLMessageFactory(ACLMessageFactory.Encodings.XML);
//
//        Map<String,Object> args = new LinkedHashMap<String,Object>();
//        args.put("userId","patient1");
//        args.put("surveyId","123456UNIQUESURVEYID");
//        args.put("?adapter", Variable.v);
//
//
//        ACLMessage req = factory.newRequestMessage("me","you",new Action("getSurvey", args));
//
//
//
//        mainAgent.tell(req);
//
//        assertNotNull(mainResponseInformer.getResponses(req));
//        assertEquals(2,mainResponseInformer.getResponses(req).size());
//
//        ACLMessage answer = mainResponseInformer.getResponses(req).get(0);
//        assertEquals(ACLMessage.Act.AGREE,answer.getPerformative());
//        ACLMessage answer2 = mainResponseInformer.getResponses(req).get(1);
//        assertEquals(ACLMessage.Act.INFORM_REF,answer2.getPerformative());
//
//        assertTrue(answer2.getBody().getEncodedContent().contains("SurveyGUIAdapter"));
//
//
//    }
//
//
//
//
//    @Test
//    public void testPredictiveSurveyCreation() {
//        String sid = "123456";
//        StatefulKnowledgeSession k2 = mainAgent.getInnerSession("session2");
//        assertNotNull(k2);
//
//        System.err.println("\n\n\n\n\n\n\n\n");
//        for (Object o : k2.getObjects()) {
//            System.err.println(o);
//        }
//
//        QueryResults res2 = k2.getQueryResults("getItemId", "Mixed_Questionnaire", "Mixed");
//        assertEquals(1,res2.size());
//        String id2 = (String) res2.iterator().next().get("$id");
//        assertNotNull(id2);
//        System.out.println(id2);
//
//    }


}





    class MockResponseInformer implements DroolsAgentResponseInformer{

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