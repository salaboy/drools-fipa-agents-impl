/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.fipa.action.helpers;

import gov.hhs.fha.nhinc.dsa.DSAIntegration;
import gov.hhs.fha.nhinc.dsa.DSAIntegrationPortType;
import gov.hhs.fha.nhinc.dsa.DeliverMessageRequestType;
import gov.hhs.fha.nhinc.dsa.DeliverMessageResponseType;
import java.util.*;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author salaboy
 */
public class DeliverMessageHelper {

    public static String deliverMessage(String endpoint, Map<String, Object> params) {
        System.out.println("DELIVER MESSAGE REQUEST IS " + params);
        DSAIntegrationPortType port = getPort(endpoint);
        DeliverMessageRequestType request = new DeliverMessageRequestType();
        request.setRefId((String) ((params.get("refId") != null)?params.get("refId"):"") );
        request.setPriority((String) ((params.get("priority") != null)?params.get("priority"):""));
        request.setBody((String) ((params.get("body") != null)?params.get("body"):""));
        request.setHeader((String) ((params.get("header") != null)?params.get("header"):""));
        request.setDeliveryDate((String) ((params.get("deliveryDate") != null)?params.get("deliveryDate"):""));
        request.setSender((String) ((params.get("sender") != null)?params.get("sender"):""));
        request.setStatus((String) ((params.get("status") != null)?params.get("status"):""));

        request.getSubject().addAll((Collection) ((params.get("subjectAbout") != null)?params.get("subjectAbout"):Collections.EMPTY_LIST));
        request.getMainRecipients().addAll((Collection) ((params.get("mainRecipients") != null)?params.get("mainRecipients"):Collections.EMPTY_LIST));
        request.getSecondaryRecipients().addAll((Collection) ((params.get("secondaryRecipients") != null)?params.get("secondaryRecipients"):Collections.EMPTY_LIST));
        request.getHiddenRecipients().addAll((Collection) ((params.get("hiddenRecipients")!= null)?params.get("hiddenRecipients"):Collections.EMPTY_LIST));
        request.getType().addAll((Collection) ((params.get("type")!= null)?params.get("type"):Collections.EMPTY_LIST));
        DeliverMessageResponseType response = port.deliverMessage(request);

        System.out.println("DELIVER MESSAGE RESPONSE IS " + response.getStatus());

        return response.getStatus();

    }

    public static void sendSMS(String endpoint, String mobile, String text) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("body", text);
        params.put("header", text);
        params.put("sender", mobile);
        List<String> recipients = new ArrayList<String>();
        recipients.add(mobile);
        params.put("mainRecipients", recipients);
        List<String> types = new ArrayList<String>();
        types.add("SMS");
        params.put("type", types);
        System.out.println(" Sending a SMS: "+mobile+ " - text: "+text);
        deliverMessage(endpoint, params);
    }

    private static DSAIntegrationPortType getPort(String endpoint) {
        DSAIntegration service = new DSAIntegration();
        DSAIntegrationPortType port = service.getDSAIntegrationPortSoap11();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                endpoint);
        return port;
    }
}
