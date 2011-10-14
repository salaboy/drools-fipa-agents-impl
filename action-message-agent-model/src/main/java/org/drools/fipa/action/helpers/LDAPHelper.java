/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.fipa.action.helpers;

import gov.hhs.fha.nhinc.dsa.DSAIntegration;
import gov.hhs.fha.nhinc.dsa.DSAIntegrationPortType;
import gov.hhs.fha.nhinc.dsa.GetDirectoryAttributeRequestType;
import gov.hhs.fha.nhinc.dsa.GetDirectoryAttributeResponseType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author salaboy
 */
public class LDAPHelper {
    public static Map<String, String> queryEntity(String endpoint, String id, List<String> names){
          
        Map<String, String> result = new HashMap<String, String>();
        GetDirectoryAttributeRequestType request = new GetDirectoryAttributeRequestType();
        request.setUid(id);
        request.getNames().addAll(names);
        GetDirectoryAttributeResponseType response = getPort(endpoint).getDirectoryAttribute(request);
        List<String> values = response.getValues();
        System.out.print("RESPONSE:");
        for(int i = 0; i < names.size(); i++){
            result.put(names.get(i), response.getValues().get(i));
        }
        
        return result;
        
    }
    
    public static Map<String, String> queryEntity(String endpoint, String id){
        List<String> names = new ArrayList<String>();
        names.add("cn");
        names.add("mobile");
        names.add("employeeNumber");
        names.add("displayName");
        names.add("gender");
        return queryEntity(endpoint, id, names);
    }
    
    
    private static DSAIntegrationPortType getPort(String endpoint) {
        DSAIntegration service = new DSAIntegration();
        DSAIntegrationPortType port = service.getDSAIntegrationPortSoap11();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpoint);
        return port;
    }
}
