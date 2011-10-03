/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa;

import org.drools.fipa.body.acts.*;
import org.drools.fipa.body.content.Ref;
import org.drools.fipa.body.content.Rule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import org.drools.fipa.body.content.Action;
import org.drools.fipa.body.content.Query;
import mock.MockFact;
import org.drools.dssagentserver.SynchronousDroolsAgentServiceImpl;
import org.drools.dssagentserver.SynchronousDroolsAgentServiceImplService;

import org.drools.fipa.body.content.Info;

import org.drools.fipa.client.helpers.SynchronousRequestHelper;
import org.drools.fipa.mappers.MyMapArgsEntryType;
import org.drools.runtime.rule.Variable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author salaboy
 */
public class ClinicalDecisionSupportAgentServiceTest {

    public ClinicalDecisionSupportAgentServiceTest() {
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
    public void testGetSurvey() {

        SynchronousRequestHelper helper = new SynchronousRequestHelper();

        LinkedHashMap<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId","patient1");
        args.put("patientId","surveyPatient");
        args.put("surveyId","123456UNIQUESURVEYID");

        helper.invokeRequest("getSurvey", args);

        String rs = (String) helper.getReturn( false );

        try {
            Document dox = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(rs.getBytes()) );
            assertEquals( "org.drools.informer.presentation.SurveyGUIAdapter", dox.getDocumentElement().getNodeName() );
        } catch (Exception e) {
            fail( e.getMessage() );
        }
    }




    @Test
    public void testProbe() {
        SynchronousRequestHelper helper = new SynchronousRequestHelper();
        LinkedHashMap<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("patientId","surveyPatient");
        helper.invokeRequest("probe", args);

        String rs = (String) helper.getReturn( false );
        System.out.println(rs);

    }

    @Test
    public void testSetSurvey() {
        String[] qid = new String[8];
        String[] values = new String[8];
        XPath finder = XPathFactory.newInstance().newXPath();

        SynchronousRequestHelper helper = new SynchronousRequestHelper();

        LinkedHashMap<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId","patient1");
        args.put("patientId","surveyPatient");
        args.put("surveyId","123456UNIQUESURVEYID");

        helper.invokeRequest("getSurvey", args);


        String rs = (String) helper.getReturn( false );
        System.out.println(rs);
        try {
            Document dox = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(rs.getBytes()) );
            assertEquals( "org.drools.informer.presentation.SurveyGUIAdapter", dox.getDocumentElement().getNodeName() );

            for ( int j = 1; j < 8; j++ ) {
                String xpath = "//org.drools.informer.presentation.QuestionGUIAdapter/questionName[.='question"+j+"']/../itemId";
                qid[j] = (String) finder.evaluate(xpath, dox, XPathConstants.STRING);
                System.out.println("ID OF Q" + j + " >> " + qid[j] );
            }

        } catch (Exception e) {
            fail( e.getMessage() );
        }


        values[1] = new Integer((int) Math.ceil( 6 * Math.random() )).toString();
        values[2] = Math.random() > 0.5 ? "Urban" : "Rural";
        List v3 = new ArrayList();
        if ( Math.random() > 0.5 ) { v3.add( "CMP" ); }
        if ( Math.random() > 0.5 ) { v3.add( "FSH" ); }
        if ( Math.random() > 0.5 ) { v3.add( "GLF" ); }
        if ( Math.random() > 0.5 ) { v3.add( "HGL" ); }
        values[3] = v3.toString().replace("[","").replace("]","").replace(" ","");
        values[4] = new Integer((int) Math.round( 100 * Math.random() )).toString();
        values[5] = UUID.randomUUID().toString();
        values[6] = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        values[7] = new Integer((int) Math.round( 100 * Math.random() )).toString();



        for ( int j = 1; j < 8; j++ ) {
            LinkedHashMap<String,Object> argsx = new LinkedHashMap<String,Object>();
            argsx.put("userId","drx");
            argsx.put("patientId","surveyPatient");
            argsx.put("surveyId","123456UNIQUESURVEYID");
            argsx.put("questionId",qid[j]);
            argsx.put("answer",values[j].toString());

            System.out.println( "Setting " +j + " to " + values[j]);

            helper.invokeRequest("setSurvey", argsx);
        }






        LinkedHashMap<String,Object> args99 = new LinkedHashMap<String,Object>();
        args99.put("userId","patient1");
        args99.put("patientId","surveyPatient");
        args99.put("surveyId","123456UNIQUESURVEYID");

        helper.invokeRequest("getSurvey", args99);

        String rs99 = (String) helper.getReturn( false );

        System.out.println(rs99);

        try {
            Document dox99 = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(rs99.getBytes()) );
            assertEquals( "org.drools.informer.presentation.SurveyGUIAdapter", dox99.getDocumentElement().getNodeName() );

            for ( int j = 1; j < 8; j++ ) {
                String xpath = "//org.drools.informer.presentation.QuestionGUIAdapter/itemId[.='"+qid[j]+"']/../currentAnswer";
                String val = (String) finder.evaluate(xpath, dox99, XPathConstants.STRING);
                assertEquals( val, values[j].toString() );
            }
        } catch (Exception e) {
            fail( e.getMessage() );
        }



    }





    @Test
    public void testGetRiskModelsDetail() {
        XPath finder = XPathFactory.newInstance().newXPath();

        SynchronousRequestHelper helper = new SynchronousRequestHelper();

        String mid = "MockPTSD";

        LinkedHashMap<String,Object> args = new LinkedHashMap<String,Object>();
        args.put("userId","docX");
        args.put("patientId","patient33");
        args.put("modelIds",new String[] {mid} );


        helper.invokeRequest("getRiskModelsDetail", args);
        String xml = (String) helper.getReturn(false);

        System.out.println(xml);


        String sid = null;
        try {
            Document risk = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(xml.getBytes()) );
            assertEquals( "org.drools.test.GetRiskModelsDetail", risk.getDocumentElement().getNodeName() );

            String xpath = "//riskModels/org.drools.test.RiskModelDetail/modelId[.='"+mid+"']/../surveyId";
            sid = (String) finder.evaluate(xpath, risk, XPathConstants.STRING);

        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }



        args.clear();
        args.put("userId","drX");
        args.put("patientId","patient33");
        args.put("surveyId",sid);

        helper.invokeRequest("getSurvey", args);

        String sxml = (String) helper.getReturn(false);

        System.out.println(sxml);

        try {
            Document surv = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(sxml.getBytes()) );
            assertEquals( "org.drools.informer.presentation.SurveyGUIAdapter", surv.getDocumentElement().getNodeName() );
        } catch (Exception e) {
            fail( e.getMessage() );
        }


    }




    @Test
    public void testSetRiskThreshold() {
        XPath finder = XPathFactory.newInstance().newXPath();
        SynchronousRequestHelper helper = new SynchronousRequestHelper();
        String mid = "MockPTSD";


        String value = ""+((int) Math.round(Math.random()*100));

        LinkedHashMap<String,Object> args = new LinkedHashMap<String,Object>();
        args.clear();
        args.put("patientId","patient33");
        args.put("modelId",mid);
        args.put("threshold",value);
        helper.invokeRequest("setRiskThreshold", args);




        args.clear();
        args.put("userId","docX");
        args.put("patientId","patient33");
        args.put("modelIds",new String[] {mid} );

        helper.invokeRequest("getRiskModelsDetail", args);
        String xml = (String) helper.getReturn(false);

        System.out.println(xml);


        String thold = null;
        try {
            Document risk = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream(xml.getBytes()) );
            assertEquals( "org.drools.test.GetRiskModelsDetail", risk.getDocumentElement().getNodeName() );

            String xpath = "//riskModels/org.drools.test.RiskModelDetail/modelId[.='"+mid+"']/../displayThreshold";
            thold = (String) finder.evaluate(xpath, risk, XPathConstants.STRING);
            assertEquals(thold, value);
        } catch (Exception e) {
            e.printStackTrace();
            fail( e.getMessage() );
        }



        args.clear();
        args.put("patientId","patient33");
        args.put("modelId",mid);
        args.put("threshold","50");
        helper.invokeRequest("setRiskThreshold", args);


    }










}
