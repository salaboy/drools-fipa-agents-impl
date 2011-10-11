/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.fipa;

import gov.hhs.fha.nhinc.dsa.DSAIntegration;
import gov.hhs.fha.nhinc.dsa.DSAIntegrationPortType;
import gov.hhs.fha.nhinc.dsa.DeliverMessageRequestType;
import gov.hhs.fha.nhinc.dsa.DeliverMessageResponseType;
import gov.hhs.fha.nhinc.dsa.GetDirectoryAttributeRequestType;
import gov.hhs.fha.nhinc.dsa.GetDirectoryAttributeResponseType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.xml.ws.BindingProvider;
import org.junit.*;

/**
 *
 * @author salaboy
 */
public class DSAIntegrationTest {

    public static final String ENDPT_LOCAL =
            "http://localhost:8080/PresentationServices/DSAIntegration";
    public static final String ENDPT_47 =
            "http://192.168.5.47:8080/PresentationServices/DSAIntegration";

    public DSAIntegrationTest() {
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
    public void testGetDirectoryAttribute() {
        String uid = "1";
        List<String> names = Arrays.asList(new String[]{"mobile", "employeeNumber"});
        GetDirectoryAttributeRequestType request = new GetDirectoryAttributeRequestType();
        request.setUid(uid);
        request.getNames().addAll(names);
        GetDirectoryAttributeResponseType response = getPort().getDirectoryAttribute(request);
        List<String> values = response.getValues();
        System.out.print("RESPONSE:");
        for (String value : values) {
            System.out.println(value);
        }
        assert (values.get(1).contains("21777989-09"));


    }
    
     @Test
    public void testDeliverMsgCreate() {
        DSAIntegrationPortType port = getPort();
        DeliverMessageRequestType request = new DeliverMessageRequestType();
        request.setRefId("888888");
        request.getSubject().add("1");
        request.setBody("TEST PAYLOAD - DATE IS " + new Date());
        request.setHeader("TEST HEADER - DATE IS " + new Date());
        request.setDeliveryDate("10/01/2011 10:10:10");
        request.setSender("fry.emory");
        request.getMainRecipients().add("fry.emory");
        request.setPriority("HIGH");
        request.getType().add("MedAlerts");
        DeliverMessageResponseType response = port.deliverMessage(request);
        System.out.println("DELIVER MESSAGE RESPONSE IS " + response.getStatus());
    }

    @Test
    public void testDeliverMsgUpdate() {
        DSAIntegrationPortType port = getPort();
        DeliverMessageRequestType request = new DeliverMessageRequestType();
        request.setRefId("888888");
        request.getSubject().add("1");
        request.setBody("UPDATE TEST PAYLOAD - DATE IS " + new Date());
        request.setHeader("UPDATE TEST HEADER - DATE IS " + new Date());
        request.setDeliveryDate("10/01/2011 10:10:10");
        request.setSender("fry.emory");
        request.getMainRecipients().add("doe.jane");
        request.setPriority("HIGH");
        request.getType().add("MedAlerts");
        DeliverMessageResponseType response = port.deliverMessage(request);
        System.out.println("DELIVER MESSAGE RESPONSE IS " + response.getStatus());
    }


    private DSAIntegrationPortType getPort() {
        DSAIntegration service = new DSAIntegration();
        DSAIntegrationPortType port = service.getDSAIntegrationPortSoap11();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                ENDPT_47);
        return port;
    }
}
