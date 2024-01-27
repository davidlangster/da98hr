package com.funkdefino.da98hr;

import com.funkdefino.common.unittest.CTestCase;
import com.funkdefino.common.util.xml.XmlDocument;
import com.funkdefino.da98hr.util.Configuration;
import com.funkdefino.common.midi.MidiCommon;
import junit.framework.Test;
import javax.sound.midi.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class ProcessorUnitTest extends CTestCase {

    //** ---------------------------------------------------------- Construction

    public ProcessorUnitTest(String sMethod) {
        super(sMethod);
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the suite of cases for testing.
     * @return the test suite.
     */
    public static Test suite() {
        return CTestCase.suite(ProcessorUnitTest.class,
                              "UnitTest.xml",
                              "test#2");
    }

    //** ----------------------------------------------------------------- Tests

    public void test01() throws Exception {

        MidiDevice input  = MidiCommon.getDevice("USB MS1x1 MIDI Interface", MidiCommon.Type.Input );
        MidiDevice output = MidiCommon.getDevice("USB MS1x1 MIDI Interface", MidiCommon.Type.Output);

        XmlDocument doc = XmlDocument.fromResource(getClass(), "DA98HR.xml");
        Configuration config = new Configuration(doc.getRootElement());

        Processor processor =
            new Processor(input.getTransmitter(),
                            output.getReceiver(),
                              config);
        input.open ();
        output.open();

        synchronized(this) {
            System.out.println("Waiting");
            wait();
        }

        processor.close();

        input.close();
        output.close();
     }

}   // class ProcessorUnitTest
