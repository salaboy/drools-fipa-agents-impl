/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.fipa.action.message.invokers;

import gov.hhs.fha.nhinc.dsa.DSAIntegration;
import gov.hhs.fha.nhinc.dsa.DSAIntegrationPortType;
import gov.hhs.fha.nhinc.dsa.DeliverMessageRequestType;
import gov.hhs.fha.nhinc.dsa.DeliverMessageResponseType;
import java.util.Date;
import javax.xml.ws.BindingProvider;
import org.drools.fipa.action.message.types.AlertMessage;

/**
 *
 * @author salaboy
 */
public class DeliverMessageChannelInvoker implements ChannelInvoker{

    private String type;
    
    public DeliverMessageChannelInvoker() {
        this.type = "DeliverMessage";
    }

    
    
    public String getType() {
        return this.type;
    }

    public Object invoke(String endpoint, Object object) {
        AlertMessage msg = (AlertMessage)object;
        DSAIntegrationPortType port = getPort(endpoint);
        DeliverMessageRequestType request = new DeliverMessageRequestType();
        request.setRefId((String)msg.getRefId());
        //request.getSubject().add("1");
        request.setBody((String)msg.getBody());
        request.setHeader((String)msg.getHeader());
        //request.setDeliveryDate("10/01/2011 10:10:10");
        request.setSender("fry.emory");
        request.getMainRecipients().add("fry.emory");
        //request.setPriority("HIGH");
        //request.getType().add("MedAlerts");
        DeliverMessageResponseType response = port.deliverMessage(request);
        System.out.println("DELIVER MESSAGE RESPONSE IS " + response.getStatus());
        
        return null;
    }
    
     private DSAIntegrationPortType getPort(String endpoint) {
        DSAIntegration service = new DSAIntegration();
        DSAIntegrationPortType port = service.getDSAIntegrationPortSoap11();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpoint);
        return port;
    }
    
}
