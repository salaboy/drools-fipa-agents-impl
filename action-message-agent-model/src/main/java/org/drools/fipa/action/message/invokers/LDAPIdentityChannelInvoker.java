/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.fipa.action.message.invokers;

import gov.hhs.fha.nhinc.dsa.DSAIntegration;
import gov.hhs.fha.nhinc.dsa.DSAIntegrationPortType;
import gov.hhs.fha.nhinc.dsa.GetDirectoryAttributeRequestType;
import gov.hhs.fha.nhinc.dsa.GetDirectoryAttributeResponseType;
import java.util.Arrays;
import java.util.List;
import javax.xml.ws.BindingProvider;
import org.drools.fipa.action.message.Recipient;

/**
 *
 * @author salaboy
 */
public class LDAPIdentityChannelInvoker implements ChannelInvoker {

    private String type;

    public LDAPIdentityChannelInvoker() {
        this.type = "IdentityChannelInvoker";
    }

    public String getType() {
        return this.type;
    }

    public Object invoke(String endpoint, Object object) {
        Recipient recipient = (Recipient)object;
        
        String uid = "1"; // = recipient.getRecipientId()
        
        List<String> names = Arrays.asList(new String[]{"mobile", "employeeNumber"}); // = recipient.getChannels()
        GetDirectoryAttributeRequestType request = new GetDirectoryAttributeRequestType();
        request.setUid(uid);
        request.getNames().addAll(names);
        GetDirectoryAttributeResponseType response = getPort(endpoint).getDirectoryAttribute(request);
        List<String> values = response.getValues();
        System.out.print("RESPONSE:");
        
        for (int i = 0; i <  values.size(); i ++) {
            System.out.println("Channel: "+ names.get(i) + "- Value: " + values.get(i));
            recipient.addChannel(names.get(i) + ":" + values.get(i));
        }
        
        return recipient;
    }
    
    private DSAIntegrationPortType getPort(String endpoint) {
        DSAIntegration service = new DSAIntegration();
        DSAIntegrationPortType port = service.getDSAIntegrationPortSoap11();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpoint);
        return port;
    }
}
