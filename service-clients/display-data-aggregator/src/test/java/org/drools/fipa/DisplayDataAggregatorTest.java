/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.fipa;

import gov.hhs.fha.nhinc.aggregator.DisplayDataAggregatorPortType;
import gov.hhs.fha.nhinc.common.dda.DeliverMessageRequestType;
import gov.hhs.fha.nhinc.common.dda.DeliverMessageResponseType;
import gov.hhs.fha.nhinc.common.dda.GetDirectoryAttributeRequestType;
import gov.hhs.fha.nhinc.common.dda.GetDirectoryAttributeResponseType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.xml.ws.BindingProvider;
import org.junit.*;

/**
 *
 * @author salaboy
 */
public class DisplayDataAggregatorTest {
    
    public DisplayDataAggregatorTest() {
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
        assert(values.get(1).contains("21777989-09"));
    }

    private DisplayDataAggregatorPortType getPort() {
        gov.hhs.fha.nhinc.aggregator.DisplayDataAggregator service =
                new gov.hhs.fha.nhinc.aggregator.DisplayDataAggregator();
        DisplayDataAggregatorPortType port = service.getDisplayDataAggregatorPortSoap11();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "192.168.5.47");
        return port;
    }
    
    
    @Test
    public void testDeliverMessageAlert() {
         
        DeliverMessageRequestType request = new DeliverMessageRequestType();
        request.getSubject().add("1");
        request.setBody("TEST PAYLOAD - DATE IS " + new Date());
        request.setHeader("TEST HEADER - DATE IS " + new Date());
        request.setDeliveryDate(new Date().toString());
        request.setSender("fry.emory");
        request.setRefId("88888");
        request.getMainRecipients().add("fry.emory");
        request.setPriority("HIGH");
        request.getType().add("ALERT");
        DeliverMessageResponseType response = getPort().deliverMessage(request);
        System.out.println("DELIVER MESSAGE RESPONSE IS " + response.getStatus());
    }

     
  
}
