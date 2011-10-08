/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa;

import mil.navy.med.dzreg.service.RegistriesService;
import mil.navy.med.dzreg.service.RegistriesServicePortType;
import mil.navy.med.dzreg.types.AckType;
import mil.navy.med.dzreg.types.PersonType;
import mil.navy.med.dzreg.types.RegisterPersonRequestType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author salaboy
 */
public class SimpleServiceTest {

    public SimpleServiceTest() {
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
    public void hello() {
        RegistriesServicePortType service = new RegistriesService().getRegistriesServicePort();
        RegisterPersonRequestType params = new RegisterPersonRequestType();
        PersonType personType = new PersonType();
        personType.setName("Salaboy");
        params.setPerson(personType);
        AckType ack = service.register(params);
        assertNotNull(ack);
        System.out.println("ACK = "+ack);
    }
}
