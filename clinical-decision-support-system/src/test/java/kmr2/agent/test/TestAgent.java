package kmr2.agent.test;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.AssertBehaviorOption;
import org.drools.fipa.*;
import org.drools.fipa.body.acts.Inform;
import org.drools.informer.generator.FormRegistry;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.Variable;
import org.drools.spi.KnowledgeHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
                "test/survey_test.xml",
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
    public void testProbe() {
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
        Map<String,Object> args = new LinkedHashMap<String,Object>();
            args.put("patientId","surveyPatient");

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("probe", args));
                mainAgent.tell(req);
                ACLMessage ans = mainResponseInformer.getResponses(req).get(1);

        System.out.println(ans.getBody());
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
        args.put("surveyId",sid);
        args.put("questionId",deployments);
        args.put("answer","1");
        ACLMessage setS2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setS2);


        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("surveyId",sid);
        args.put("questionId",gender);
        args.put("answer","female");
        ACLMessage setS1 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setS1);

        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("surveyId",sid);
        args.put("questionId",alcohol);
        args.put("answer","yes");
        ACLMessage setS3 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setS3);

        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("surveyId",sid);
        args.put("questionId",age);
        args.put("answer","30");
        ACLMessage setS4 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setS4);





        args.clear();
        args.put("patientId","patient33");
        args.put("modelId",mid);
        args.put("threshold","22");
        ACLMessage setThold = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setRiskThreshold", args) );
        mainAgent.tell(setThold);



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
        args.put("dxProcessId",dxProcessId);
        args.put("refresh",true);
        args.put("patientId", "patient33" );

        ACLMessage reqStatus = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getDiagnosticProcessStatus", args) );

        mainAgent.tell(reqStatus);
        ACLMessage ans2 = mainResponseInformer.getResponses(reqStatus).get(1);
        System.out.println(ans2.getBody());

        System.out.println("***********************************************************************************************************************");


        String statusXML = ((Inform) ans2.getBody()).getProposition().getEncodedContent();

        String actionId = "";
        try {
            Document action = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(statusXML.getBytes()) );
            XPath finder = XPathFactory.newInstance().newXPath();
            String xpath = "//org.kmr2.decision.AskAlcohol/questionnaireId";
            actionId = (String) finder.evaluate(xpath, action, XPathConstants.STRING);
        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        System.err.println(actionId);


        args.clear();
        args.put("dxProcessId",dxProcessId);
        args.put("actionId",actionId);
        args.put("status","Started");
        args.put("patientId", "patient33" );

        ACLMessage reqAction1 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setDiagnosticActionStatus", args) );
        mainAgent.tell(reqAction1);





        args.clear();
        args.put("userId","patient33");
        args.put("patientId","patient33");
        args.put("surveyId",actionId);

        ACLMessage actQuest = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getSurvey", args));
        mainAgent.tell(actQuest);
        ACLMessage ansQuest = mainResponseInformer.getResponses(actQuest).get(1);

        String xml = ((Inform)ansQuest.getBody()).getProposition().getEncodedContent();


        String alcoholQid = "";
        try {
            Document action = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(xml.getBytes()) );
            XPath finder = XPathFactory.newInstance().newXPath();
            String xpath = "//questionName[.='question']/../itemId";
            alcoholQid = (String) finder.evaluate(xpath, action, XPathConstants.STRING);
        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        System.err.println(alcoholQid);


        args.clear();
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("surveyId",actionId);
        args.put("questionId",alcoholQid);
        args.put("answer","true");
        ACLMessage setAlchol = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setAlchol);



        args.clear();
        args.put("userId","patient33");
        args.put("patientId","patient33");
        args.put("surveyId",actionId);

        ACLMessage actQuest2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getSurvey", args));
        mainAgent.tell(actQuest2);
        ACLMessage ansQuest2 = mainResponseInformer.getResponses(actQuest2).get(1);

        String xml2 = ((Inform)ansQuest2.getBody()).getProposition().getEncodedContent();
        System.err.println(xml2);


        args.clear();
        args.put("dxProcessId",dxProcessId);
        args.put("actionId",actionId);
        args.put("status","Complete");
        args.put("patientId", "patient33" );

        ACLMessage reqAction2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setDiagnosticActionStatus", args) );

        mainAgent.tell(reqAction2);


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
        args.put("dxProcessId",dxProcessId);
        args.put("refresh",true);
        args.put("patientId", "patient33" );

        ACLMessage reqStatus2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getDiagnosticProcessStatus", args) );

        mainAgent.tell(reqStatus2);
        ACLMessage ans99 = mainResponseInformer.getResponses(reqStatus2).get(1);
        System.out.println(ans99.getBody());





        Collection wm2 = mainAgent.getInnerSession("patient33").getObjects();
        System.out.println("*********");
        System.out.println( wm2.size() );


    }



    @Test
       public void testX() {
           String s =
               "package org.drools.test\n" +
               "import " + Action.class.getCanonicalName() + "\n" +
               "import " + Answer.class.getCanonicalName() + "\n" +
               "rule r1 when\n" +
               "       $a : Action( actionName == \"setDiagnosticActionStatus\", \n" +
               "                    $processId : this[\"dxProcessId\"], \n" +
               "                    $actionId : this[\"actionId\"], \n" +
               "                    $status : this[\"status\"], \n" +
               "                    $patientId : this[\"patientId\"] )\n" +
               "then\n" +
               "    System.out.println('execute');\n" +
               "    insert( new Answer((String) $status, \"status\", (String) $actionId ) );\n" +
               "    retract( $a );" +
               "end\n";

           KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
           kbuilder.add( ResourceFactory.newByteArrayResource(s.getBytes()), ResourceType.DRL );

           if ( kbuilder.hasErrors() ) {
               fail( kbuilder.getErrors().toString() );
           }

           KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
           kconf.setOption( AssertBehaviorOption.EQUALITY );
           KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);

           kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );


           StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

           Action action = new Action();
           action.setActionName( "setDiagnosticActionStatus" );
           action.put( "dxProcessId", "v1" );
           action.put( "actionId", "v2" );
           action.put( "status", "v3" );
           action.put( "patientId", "v4" );

           ksession.insert( action );
           ksession.fireAllRules();
       }

       public static class Action extends HashMap {
           private String actionName ;

           public String getActionName() {
               return actionName;
           }

           public void setActionName(String actionName) {
               this.actionName = actionName;
           }
       }

       public static class Answer {
           private String value;
           private String field;
           private String id;

           public Answer(String value,
                         String field,
                         String id) {
               this.value = value;
               this.field = field;
               this.id = id;
           }

           public String getValue() {
               return value;
           }

           public void setValue(String value) {
               this.value = value;
           }

           public String getField() {
               return field;
           }

           public void setField(String field) {
               this.field = field;
           }

           public String getId() {
               return id;
           }

           public void setId(String id) {
               this.id = id;
           }
       }



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