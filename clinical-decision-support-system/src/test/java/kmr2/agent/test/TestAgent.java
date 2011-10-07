package kmr2.agent.test;

import org.drools.ClassObjectFilter;
import org.drools.definition.type.FactType;
import org.drools.dssagentserver.helpers.SynchronousRequestHelper;
import org.drools.fipa.*;
import org.drools.fipa.body.acts.Inform;
import org.drools.runtime.rule.Variable;
import org.jbpm.task.HumanTaskServiceLookup;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kmr2.TemplateBuilder;
import org.w3c.dom.Document;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TestAgent {



    private static DroolsAgent mainAgent;

    private static MockResponseInformer mainResponseInformer;


    @BeforeClass
    public static void createAgents() {

        mainResponseInformer = new MockResponseInformer();

        DroolsAgentConfiguration mainConfig = new DroolsAgentConfiguration();
        mainConfig.setAgentId( "Mock Test Agent" );
        mainConfig.setChangeset( "default/agent_changeset.xml" );
        mainConfig.setDefaultSubsessionChangeSet( "default/subsession_default.xml" );
        mainConfig.setResponseInformer( mainResponseInformer );
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

        ACLMessage info = factory.newInformMessage("client", "DSA",  new MockPatient("patient85", 33) );
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
    public void testGetRiskModels() {
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        Map<String,Object> args = new LinkedHashMap<String,Object>();



        args.clear();
        args.put("userId","drX");
        args.put("patientId","patient33");
        args.put("modelId","MockPTSD");
        args.put("type","Alert");
        args.put("threshold","35");
        ACLMessage setThold = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setRiskThreshold", args) );
        mainAgent.tell(setThold);
//


        args.clear();
        args.put("userId","drX");
        args.put("patientId","patient33");
        args.put("types", Arrays.asList("E"));

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModels", args));
        mainAgent.tell(req);
        ACLMessage ans = mainResponseInformer.getResponses(req).get(1);

        System.out.println( ans.getBody() );

    }



    @Test
    public void testGetRiskModelsDetail() {
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
        String mid = "MockPTSD";
        String mid2 = "MockCold";
        Map<String,Object> args = new LinkedHashMap<String,Object>();


        args.put("userId","docX");
        args.put("patientId","patient33");
        args.put("modelIds",new String[] {mid, mid2} );

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModelsDetail", args) );

        mainAgent.tell(req);

        mainResponseInformer.getResponses(req).get(1);

        Collection c=  mainAgent.getInnerSession("patient33").getObjects();

        String sid = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Questionnaire", mid, Variable.v ).iterator().next().get("$id");
        String gender = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Gender", mid, Variable.v ).iterator().next().get("$id");
        String deployments = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Deployments", mid, Variable.v ).iterator().next().get("$id");
        String alcohol = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Alcohol", mid, Variable.v ).iterator().next().get("$id");
        String age = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Age", mid, Variable.v ).iterator().next().get("$id");


        String sid2 = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid2 + "_Questionnaire", mid2, Variable.v ).iterator().next().get("$id");
        String temperature = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid2 + "_Temp", mid2, Variable.v ).iterator().next().get("$id");



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
        args.put("userId","drx");
        args.put("patientId","patient33");
        args.put("surveyId",sid2);
        args.put("questionId",temperature);
        args.put("answer","39");
        ACLMessage setX1 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(setX1);





        args.clear();
        args.put("userId","drX");
        args.put("patientId","patient33");
        args.put("modelId",mid);
        args.put("type","Alert");
        args.put("threshold","35");
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

        String risk = ((Inform) ans2.getBody()).getProposition().getEncodedContent();

        try {
            Document riskx = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(risk.getBytes()) );
            XPath finder = XPathFactory.newInstance().newXPath();

            String xpath1 = "//org.drools.test.RiskModelDetail/modelId[.='MockPTSD']/../relativeRisk";
            assertEquals( "30", finder.evaluate(xpath1, riskx, XPathConstants.STRING) );

            String xpath2 = "//org.drools.test.RiskModelDetail/modelId[.='MockCold']/../relativeRisk";
            assertEquals( "22", finder.evaluate(xpath2, riskx, XPathConstants.STRING) );


        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }


    }






    @Test
       public void testProbe33() {
           ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
           Map<String,Object> args = new LinkedHashMap<String,Object>();
               args.put("patientId","patient33");

           ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("probe", args));
                   mainAgent.tell(req);
                   ACLMessage ans = mainResponseInformer.getResponses(req).get(1);

           System.out.println(ans.getBody());
       }




    @Test
    public void testDiagnostic() {

        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
        String mid = "MockDiag";
        String did = "MockDecision";
        Map<String,Object> args = new LinkedHashMap<String,Object>();


        args.clear();
        args.put("userId", "docX");
        args.put("patientId", "patient33" );
//        args.put("decModelId","MockDecision");
//        args.put("diagModelId","MockDiag");
        args.put("disease", "Post Traumatic Stress Disorder");
        ACLMessage start = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("startDiagnosticGuideProcess", args) );

        mainAgent.tell(start);
        ACLMessage ans = mainResponseInformer.getResponses(start).get(1);
        MessageContentEncoder.decodeBody( ans.getBody(), Encodings.XML );
        String dxProcessId = (String) ((Inform) ans.getBody()).getProposition().getData();

        Collection wm = mainAgent.getInnerSession("patient33").getObjects();
        System.out.println("***********************************************************************************************************************");




        args.clear();
        args.put("userId", "drX");
        args.put("patientId", "patient33" );
        args.put("dxProcessId",dxProcessId);
        args.put("refresh",true);


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
            String xpath = "//org.kmr2.decision.impl.AskAlcohol/questionnaireId";
            actionId = (String) finder.evaluate(xpath, action, XPathConstants.STRING);
        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        System.err.println(actionId);


        args.clear();
        args.put("userId", "drX" );
        args.put("patientId", "patient33" );
        args.put("dxProcessId",dxProcessId);
        args.put("actionId",actionId);
        args.put("status","Started");

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
        args.put("userId", "drX" );
        args.put("patientId", "patient33" );
        args.put("dxProcessId",dxProcessId);
        args.put("actionId",actionId);
        args.put("status","Complete");


        ACLMessage reqAction2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setDiagnosticActionStatus", args) );

        mainAgent.tell(reqAction2);


        args.clear();
        args.put("userId", "drX");
        args.put("patientId", "patient33" );
        args.put("dxProcessId",dxProcessId);


        ACLMessage next = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("advanceDiagnosticGuideProcess", args) );

        mainAgent.tell(next);

        System.out.println("***********************************************************************************************************************");










        args.clear();
        args.put("userId", "drX" );
        args.put("patientId", "patient33" );
        args.put("dxProcessId",dxProcessId);
        args.put("status","Complete");

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("completeDiagnosticGuideProcess", args) );

        mainAgent.tell(req);



        System.out.println("***********************************************************************************************************************");

        args.clear();
        args.put("userId", "drX" );
        args.put("patientId", "patient33" );
        args.put("dxProcessId",dxProcessId);
        args.put("refresh",true);


        ACLMessage reqStatus2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getDiagnosticProcessStatus", args) );

        mainAgent.tell(reqStatus2);
        ACLMessage ans99 = mainResponseInformer.getResponses(reqStatus2).get(1);
        System.out.println(ans99.getBody());





        Collection wm2 = mainAgent.getInnerSession("patient33").getObjects();
        System.out.println("*********");
        System.out.println( wm2.size() );


        String diagXML = ((Inform)ans99.getBody()).getProposition().getEncodedContent();

        try {
            Document diag = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(diagXML.getBytes()) );
            XPath finder = XPathFactory.newInstance().newXPath();

            String xpath1 = "//org.kmr2.decision.DxDecision/diseaseProbability[.='10']/../stage";
            assertEquals( "1", finder.evaluate(xpath1, diag, XPathConstants.STRING) );

            String xpath2 = "//org.kmr2.decision.DxDecision/actions/org.kmr2.decision.impl.AskAlcohol/status[.='Complete']/../../../stage";
            assertEquals( "0", finder.evaluate(xpath2, diag, XPathConstants.STRING) );

        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }


    }








    @Test
    public void testExceedRiskThreshld() {
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );
        String mid = "MockPTSD";
        Map<String,Object> args = new LinkedHashMap<String,Object>();


        args.put("userId","docX");
        args.put("patientId","patient33");
        args.put("modelIds",new String[] {mid} );

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModelsDetail", args) );

        mainAgent.tell(req);
        mainResponseInformer.getResponses(req).get(1);


        String sid = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Questionnaire", mid, Variable.v ).iterator().next().get("$id");
        String gender = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Gender", mid, Variable.v ).iterator().next().get("$id");
        String deployments = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Deployments", mid, Variable.v ).iterator().next().get("$id");
        String alcohol = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Alcohol", mid, Variable.v ).iterator().next().get("$id");
        String age = (String) mainAgent.getInnerSession("patient33").getQueryResults("getItemId",mid + "_Age", mid, Variable.v ).iterator().next().get("$id");


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
        args.put("userId","docX");
        args.put("patientId","patient33");
        args.put("modelIds",new String[] {mid} );

        ACLMessage req2 = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModelsDetail", args) );
        mainAgent.tell(req2);

        ACLMessage ans2 = mainResponseInformer.getResponses(req2).get(1);
        String risk = ((Inform) ans2.getBody()).getProposition().getEncodedContent();

        try {
            Document riskx = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(risk.getBytes()) );
            XPath finder = XPathFactory.newInstance().newXPath();

            String xpath1 = "//org.drools.test.RiskModelDetail/modelId[.='MockPTSD']/../relativeRisk";
            assertEquals( "30", finder.evaluate(xpath1, riskx, XPathConstants.STRING) );

        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }














        args.clear();
        args.put("userId","drX");
        args.put("patientId","patient33");
        args.put("modelId",mid);
        args.put("type","Alert");
        args.put("threshold","05");
        ACLMessage setThold = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setRiskThreshold", args) );
        mainAgent.tell(setThold);


        Collection wm = mainAgent.getInnerSession("patient33").getObjects();
        System.out.println("*********");










        FactType alertType = mainAgent.getInnerSession("patient33").getKnowledgeBase().getFactType("org.drools.interaction", "Alert");
        Class alertClass = alertType.getFactClass();

        Collection alerts = mainAgent.getInnerSession("patient33").getObjects( new ClassObjectFilter(alertClass) );



        for ( Object alert : alerts ) {

            String formId = (String) alertType.get( alert, "formId" );
            String dest = (String) alertType.get( alert, "destination" );

            args.clear();
            args.put("userId",dest);
            args.put("patientId","patient33");
            args.put("surveyId",formId);

            ACLMessage getSrv = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getSurvey", args) );
            mainAgent.tell( getSrv );
            String body = mainResponseInformer.getResponses( getSrv ).get(1).getBody().toString();

            assertNotNull( body );
            System.err.println( body );

        }

        TaskService ts;


        Collection finalFacts = mainAgent.getInnerSession("patient33").getObjects();

        SynchronousRequestHelper helper;

        System.out.println("*********");
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