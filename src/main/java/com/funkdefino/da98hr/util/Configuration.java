package com.funkdefino.da98hr.util;

import com.funkdefino.common.util.xml.*;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class Configuration {

    //** ------------------------------------------------------------- Constants

    private final static String ElmntInput  = "Input";
    private final static String ElmntOutput = "Output";
    private final static String ElmntDebug  = "Debug";
    private final static String AttrbSend   = "send";
    private final static String AttrbRecv   = "recv";

    //** ------------------------------------------------------------------ Data

    private String  input;      // MIDI input port
    private String  output;     // MIDI output port
    private boolean dbgSend;    // Diagnostic debug
    private boolean dbgRecv;    // Diagnostic debug

    //** ---------------------------------------------------------- Construction

    /**
     * Ctor.
     * @param config a configuration element.
     * @throws Exception on error.
     */
    public Configuration(XmlElement config) throws Exception {
        initialise(config);
    }

    //** ------------------------------------------------------------ Operations

    public String  getInput ()  {return input;  }
    public String  getOutput()  {return output; }
    public boolean isDbgSend()  {return dbgSend;}
    public boolean isDbgRecv()  {return dbgRecv;}

    //** -------------------------------------------------------- Implementation

    /**
     * Performs startup initialisation.
     * @param config a configuration element.
     * @throws Exception on error.
     */
    private void initialise(XmlElement config) throws Exception {

        input  = XmlValidate.getContent(config, ElmntInput );
        output = XmlValidate.getContent(config, ElmntOutput);

        XmlElement dbg = config.getChild(ElmntDebug);
        if(dbg != null) {
            String sDbgSend = XmlValidate.getAttribute(dbg, AttrbSend, "false");
            String sDbgRecv = XmlValidate.getAttribute(dbg, AttrbRecv, "false");
            dbgSend = Boolean.parseBoolean(sDbgSend);
            dbgRecv = Boolean.parseBoolean(sDbgRecv);
        }
        else {
            dbgSend = dbgRecv = false;
        }

    }   // initialise()

}   // class Configuration
