package org.kmr2;


import org.drools.io.ResourceFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

import java.io.IOException;
import java.io.InputStream;

public class TemplateBuilder {

    private static TemplateRegistry kmr2Registry;

    protected static final String[] NAMED_TEMPLATES = new String[] {

            "templates/risk_alert.template",
            "templates/risk_alert_prov.template"
    };



    private static void init() {
        kmr2Registry = new SimpleTemplateRegistry();
        for (String path : NAMED_TEMPLATES) {
            try {
                InputStream stream = ResourceFactory.newClassPathResource(path, TemplateBuilder.class).getInputStream();

                kmr2Registry.addNamedTemplate( path.substring(path.lastIndexOf('/') + 1),
                                           TemplateCompiler.compileTemplate(stream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static TemplateRegistry getRegistry() {
        if ( kmr2Registry == null ) {
            init();
        }
        return kmr2Registry;
    }
}
