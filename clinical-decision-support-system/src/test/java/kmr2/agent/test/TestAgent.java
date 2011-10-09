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
import org.junit.Ignore;
import org.junit.Test;
import org.kmr2.TemplateBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

public class TestAgent {



    private static DroolsAgent mainAgent;

    private static MockResponseInformer mainResponseInformer;


    private ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );


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









    private String probe( String patientId ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("patientId", patientId);

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("probe", args) );
        mainAgent.tell(req);
        ACLMessage ans = mainResponseInformer.getResponses(req).get(1);
        return ret(ans);
    }

    private String ret(ACLMessage ans) {
        return ((Inform) ans.getBody()).getProposition().getEncodedContent();
    }


    private String getSurvey( String userId, String patientId, String surveyId ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId",userId);
        args.put("patientId",patientId);
        args.put("surveyId",surveyId);

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getSurvey", args));
        mainAgent.tell(req);
        ACLMessage ans = mainResponseInformer.getResponses(req).get(1);
        return ret(ans);
    }

    private void setSurvey( String userId, String patientId, String surveyId, String questionId, String value ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId",userId);
        args.put("patientId",patientId);
        args.put("surveyId",surveyId);
        args.put("questionId", questionId);
        args.put("answer",value);

        ACLMessage set = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setSurvey", args) );
        mainAgent.tell(set);
    }

    private void setRiskThreshold(String userId, String patientId, String modelId, String type, Integer value) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.clear();
        args.put("userId",userId);
        args.put("patientId",patientId);
        args.put("modelId",modelId);
        args.put("type",type);
        args.put("threshold",value);
        ACLMessage setThold = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setRiskThreshold", args) );
        mainAgent.tell(setThold);
    }

    private String getModels( String userId, String patientId, List tags ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId",userId);
        args.put("patientId",patientId);
        args.put("types", tags);
        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getModels", args));
        mainAgent.tell(req);
        ACLMessage ans = mainResponseInformer.getResponses(req).get(1);


        return ret(ans);
    }

    private String getRiskModels( String userId, String patientId ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId",userId);
        args.put("patientId",patientId);
        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModels", args));
        mainAgent.tell(req);
        ACLMessage ans = mainResponseInformer.getResponses(req).get(1);


        return ret(ans);
    }

    private String getRiskModesDetail(String userId, String patientId, String[] modelsIds) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();


        args.put("userId", userId );
        args.put("patientId", patientId );
        args.put("modelIds",modelsIds );

        ACLMessage req = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getRiskModelsDetail", args) );

        mainAgent.tell(req);

        ACLMessage ans = mainResponseInformer.getResponses(req).get(1);
        return ret( ans );
    }


    public String startDiagnosticGuideProcess( String userId, String patientId, String disease ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();

        args.put("userId", userId );
        args.put("patientId", patientId );
        args.put("disease", disease );
        ACLMessage start = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("startDiagnosticGuideProcess", args) );

        mainAgent.tell(start);
        ACLMessage ans = mainResponseInformer.getResponses(start).get(1);
        MessageContentEncoder.decodeBody( ans.getBody(), Encodings.XML );
        String dxProcessId = (String) ((Inform) ans.getBody()).getProposition().getData();
        return dxProcessId;
    }

    public String getDiagnosticProcessStatus( String userId, String patientId, String dxProcessId, boolean forceRefresh ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId", userId);
        args.put("patientId", patientId );
        args.put("dxProcessId",dxProcessId);
        args.put("forceRefresh",forceRefresh );
        ACLMessage reqStatus = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("getDiagnosticProcessStatus", args) );

        mainAgent.tell(reqStatus);
        ACLMessage ans2 = mainResponseInformer.getResponses(reqStatus).get(1);
        return ret( ans2 );
    }

    public String setDiagnosticActionStatus(String userId, String patientId, String dxProcessId, String actionId, String status) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.clear();
        args.put("userId", userId);
        args.put("patientId", patientId );
        args.put("dxProcessId",dxProcessId);
        args.put("actionId",actionId);
        args.put("status",status);

        ACLMessage reqAction = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("setDiagnosticActionStatus", args) );
        mainAgent.tell(reqAction);
        ACLMessage ans = mainResponseInformer.getResponses(reqAction).get(1);
        return ret( ans ).replaceAll("<string>","").replaceAll("</string>","") ;

    }

    public void advanceDiagnosticProcessStatus( String userId, String patientId, String dxProcessId ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId", userId);
        args.put("patientId", patientId );
        args.put("dxProcessId",dxProcessId);
        ACLMessage reqStatus = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("advanceDiagnosticGuideProcess", args) );

        mainAgent.tell(reqStatus);
//           ACLMessage ans2 = mainResponseInformer.getResponses(reqStatus).get(1);

    }


    public void completeeDiagnosticProcessStatus( String userId, String patientId, String dxProcessId, String status ) {
        Map<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId", userId);
        args.put("patientId", patientId );
        args.put("dxProcessId",dxProcessId);
        args.put("status", status);
        ACLMessage reqStatus = factory.newRequestMessage("me","you", MessageContentFactory.newActionContent("completeDiagnosticGuideProcess", args) );

        mainAgent.tell(reqStatus);
//           ACLMessage ans2 = mainResponseInformer.getResponses(reqStatus).get(1);

    }



    private List<String> getModels( String xml ) {
        return getElements( xml, "//modelId" );
    }


    public List<String> getElements(String xml, String xpath) {
        try {
            Document dox = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream( xml.getBytes()) );
            XPath finder = XPathFactory.newInstance().newXPath();

            NodeList nodes = (NodeList) finder.evaluate( xpath, dox, XPathConstants.NODESET );
            List<String> list = new ArrayList<String>();
            for ( int j = 0; j < nodes.getLength(); j++ ) {
                list.add(((Element) nodes.item(j)).getTextContent());
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return Collections.emptyList();
    }

    public String getValue(String xml, String xpath) {
        try {
            Document dox = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream( xml.getBytes()) );
            XPath finder = XPathFactory.newInstance().newXPath();

            return (String) finder.evaluate( xpath, dox, XPathConstants.STRING );
        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return null;
    }

















    @Test
    public void testInform() {
        ACLMessageFactory factory = new ACLMessageFactory( Encodings.XML );

        ACLMessage info = factory.newInformMessage("client", "DSA",  new MockPatient("patient85", 33) );
        mainAgent.tell( info );

    }





    @Test
    public void testSurvey() {
        System.out.println( getSurvey( "drX", "99990070", "123456UNIQUESURVEYID" ) );
    }


    @Test
    public void testGetModels() {

        String diagModels = getModels("drX", "patient33", Arrays.asList("Diagnostic") );

        System.out.println( diagModels );

        List<String> diagList = getModels( diagModels );
        assertEquals(1, diagList.size());
        assertTrue( diagList.containsAll( Arrays.asList("MockDiag" ) ) );



        setRiskThreshold( "drX", "patient33", "MockPTSD", "Display", 35 );

        String riskModels = getRiskModels("drX", "patient33");

        System.err.println( riskModels );

        List<String> riskList = getModels( riskModels );
        assertEquals( 2, riskList.size());
        assertTrue( riskList.containsAll( Arrays.asList( "MockPTSD", "MockCold" ) ) );

        assertEquals( "35", getValue( riskModels, "//modelId[.='MockPTSD']/../displayThreshold" ) );

    }




    @Test
    public void testGetRiskModelsDetail() {

        List<String> modelsIds = getElements(getRiskModels("docX", "patient33"), "//modelId");
        String modelStats = getRiskModesDetail( "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]) );


        String sid1 = getValue( modelStats, "//modelId[.='MockPTSD']/../surveyId" );
        String sid2 = getValue( modelStats, "//modelId[.='MockCold']/../surveyId" );

        assertNotNull( sid1 );
        assertNotNull( sid2 );

        String ptsdSurvey = getSurvey( "docX", "patient33", sid1);
        String coldSurvey = getSurvey( "docX", "patient33", sid2);

        System.out.println(ptsdSurvey);

        String gender = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Gender']/../itemId" );
        String deployments = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Deployments']/../itemId" );
        String alcohol = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Alcohol']/../itemId" );
        String age = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Age']/../itemId" );

        String temperature = getValue( coldSurvey, "//questionName[.='MockCold_Temp']/../itemId" );

        assertNotNull( gender );
        assertNotNull( deployments );
        assertNotNull( age );
        assertNotNull( temperature );

        setSurvey( "drX", "patient33", sid1, deployments, "1" );
        setSurvey( "drX", "patient33", sid1, gender, "female" );
        setSurvey( "drX", "patient33", sid1, alcohol, "yes" );
        setSurvey( "drX", "patient33", sid1, age, "30" );

        setSurvey( "drX", "patient33", sid2, temperature, "39" );

        setRiskThreshold( "drX", "patient33", "MockPTSD", "Alert", 35 );



        modelStats = getRiskModesDetail( "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]) );


        System.out.println( modelStats );
        assertEquals( "30", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk" ) );
        assertEquals( "35", getValue( modelStats, "//modelId[.='MockPTSD']/../alertThreshold" ) );
        assertEquals( "Not Started", getValue( modelStats, "//modelId[.='MockPTSD']/../dxProcessStatus" ) );


        assertEquals( "50", getValue( modelStats, "//modelId[.='MockCold']/../alertThreshold" ) );
        assertEquals( "22", getValue( modelStats, "//modelId[.='MockCold']/../relativeRisk" ) );

    }



    @Test
    public void testSetDiagnostic() {

        Map<String,Object> args = new LinkedHashMap<String,Object>();

        String dxProcessId = startDiagnosticGuideProcess( "docX", "patient33", "Post Traumatic Stress Disorder");
        assertNotNull( dxProcessId );

        String statusXML = getDiagnosticProcessStatus( "drX", "patient33", dxProcessId, true );

        String actionId = getValue( statusXML, "//org.kmr2.decision.impl.AskAlcohol/questionnaireId" );
        assertNotNull( actionId );


        System.err.println(statusXML);

        String stat1 = setDiagnosticActionStatus( "drX", "patient33", dxProcessId, actionId, "Started" );
        assertEquals("Started", stat1);

        String stat2 = setDiagnosticActionStatus( "drX", "patient33", dxProcessId, actionId, "Committed" );
        assertEquals("Committed", stat2);

        String stat3 = setDiagnosticActionStatus( "drX", "patient33", dxProcessId, actionId, "Complete" );
        assertEquals("Complete", stat3);

        statusXML = getDiagnosticProcessStatus( "drX", "patient33", dxProcessId, true );
        System.out.println( statusXML );

        completeeDiagnosticProcessStatus( "drX", "patient33", dxProcessId, "Complete" );




    }




    @Test
    public void testProbe() {
        System.out.println( probe( "patient33" ) );
    }







    @Test
    public void testDiagnostic() {


        Map<String,Object> args = new LinkedHashMap<String,Object>();

        String dxProcessId = startDiagnosticGuideProcess( "docX", "patient33", "Post Traumatic Stress Disorder");
        assertNotNull( dxProcessId );

        String statusXML = getDiagnosticProcessStatus( "drX", "patient33", dxProcessId, true );

        String actionId = getValue( statusXML, "//org.kmr2.decision.impl.AskAlcohol/questionnaireId" );
        assertNotNull( actionId );

        System.out.println(statusXML);

        String stat1 = setDiagnosticActionStatus( "drX", "patient33", dxProcessId, actionId, "Started" );
        assertEquals("Started", stat1);

        String survXML = getSurvey( "drX", "patient33", actionId );
        String alcoholQid = getValue( survXML, "//questionName[.='question']/../itemId" );

        System.err.println(alcoholQid);

        statusXML = getDiagnosticProcessStatus( "drX", "patient33", dxProcessId, true );
        assertEquals( "false", getValue( statusXML, "//canAdvance") );



        setSurvey( "drX", "patient33", actionId, alcoholQid, "true" );

        survXML = getSurvey( "drX", "patient33", actionId );

        String stat = setDiagnosticActionStatus( "drX", "patient33", dxProcessId, actionId, "Complete" );
        assertEquals( "Complete", stat );

        statusXML = getDiagnosticProcessStatus( "drX", "patient33", dxProcessId, true );

        assertEquals( "true", getValue( statusXML, "//questionnaireId[.='"+ actionId + "']/../question" ) );
        assertEquals( "Complete", getValue( statusXML, "//questionnaireId[.='"+ actionId + "']/../status" ) );
        assertEquals( "Not Started", getValue( statusXML, "//org.kmr2.decision.impl.AskDeployment/status" ) );


        assertEquals( "true", getValue( statusXML, "//canAdvance") );
        advanceDiagnosticProcessStatus( "drX", "patient33", dxProcessId );

        statusXML = getDiagnosticProcessStatus( "drX", "patient33", dxProcessId, true );
        assertEquals( "true", getValue( statusXML, "//canAdvance") );


        completeeDiagnosticProcessStatus( "rdrX", "patient33", dxProcessId, "Complete" );

        statusXML = getDiagnosticProcessStatus( "drX", "patient33", dxProcessId, true );

        assertEquals( "1", getValue( statusXML, "//org.kmr2.decision.DxDecision/diseaseProbability[.='10']/../stage" ) );
        assertEquals( "0", getValue( statusXML, "//org.kmr2.decision.DxDecision/actions/org.kmr2.decision.impl.AskAlcohol/status[.='Complete']/../../../stage" ) );


    }








    @Test
    public void testExceedRiskThreshld() {

        List<String> modelsIds = getElements(getRiskModels("docX", "patient33"), "//modelId");
        String modelStats = getRiskModesDetail( "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]) );


        String sid1 = getValue( modelStats, "//modelId[.='MockPTSD']/../surveyId" );
        assertNotNull( sid1 );

        String ptsdSurvey = getSurvey( "docX", "patient33", sid1);

        System.out.println(ptsdSurvey);

        String gender = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Gender']/../itemId" );
        String deployments = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Deployments']/../itemId" );
        String alcohol = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Alcohol']/../itemId" );
        String age = getValue( ptsdSurvey, "//questionName[.='MockPTSD_Age']/../itemId" );

        assertNotNull( gender );
        assertNotNull( deployments );
        assertNotNull( age );

        setSurvey( "drX", "patient33", sid1, deployments, "1" );
        setSurvey( "drX", "patient33", sid1, gender, "female" );
        setSurvey( "drX", "patient33", sid1, alcohol, "yes" );
        setSurvey( "drX", "patient33", sid1, age, "30" );

        modelStats = getRiskModesDetail( "docX", "patient33", modelsIds.toArray(new String[modelsIds.size()]) );
        assertEquals( "30", getValue( modelStats, "//modelId[.='MockPTSD']/../relativeRisk" ) );


        setRiskThreshold( "drX", "patient33", "MockPTSD", "Alert", 05 );






        FactType alertType = mainAgent.getInnerSession("patient33").getKnowledgeBase().getFactType("org.drools.interaction", "Alert");
        Class alertClass = alertType.getFactClass();

        Collection alerts = mainAgent.getInnerSession("patient33").getObjects( new ClassObjectFilter(alertClass) );


        String patientAlertSurveyId = null;
        String ackQuestionId = null;
        for ( Object alert : alerts ) {

            String formId = (String) alertType.get( alert, "formId" );
            String dest = (String) alertType.get( alert, "destination" );

            String form = getSurvey( dest, "patient33", formId );
            assertNotNull( form );

            System.err.println(form);

            String type = getValue( form, "//surveyClass" );
            if ("org.kmr2.mock.PatientAcknowledgment".equals( type ) ) {
                System.err.println( form );

                patientAlertSurveyId = getValue( form, "//org.drools.informer.presentation.SurveyGUIAdapter/itemId" );

                ackQuestionId = getValue( form, "//org.drools.informer.presentation.QuestionGUIAdapter/questionName[.='ack']/../itemId" );

                assertEquals( formId, patientAlertSurveyId );
                assertNotNull(ackQuestionId);
            }

        }


//        setSurvey( "patient33", "patient33", patientAlertSurveyId, ackQuestionId, "accept" );


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