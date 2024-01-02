package com.funkdefino.da98hr;

import com.funkdefino.common.unittest.CTestCase;
import com.funkdefino.common.util.xml.XmlDocument;
import com.funkdefino.da98hr.util.Configuration;
import junit.framework.Test;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class ConfigurationUnitTest extends CTestCase {

    //** ---------------------------------------------------------- Construction

    public ConfigurationUnitTest(String sMethod) {
        super(sMethod);
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns the suite of cases for testing.
     * @return the test suite.
     */
    public static Test suite() {
        return CTestCase.suite(ConfigurationUnitTest.class,
                              "UnitTest.xml",
                              "test#1");
    }

    //** ----------------------------------------------------------------- Tests

    public void test01() throws Exception {
        XmlDocument doc = XmlDocument.fromResource(getClass(), "DA98HR.xml");
        Configuration cfg = new Configuration(doc.getRootElement());
        System.out.println(cfg.getInput ());
        System.out.println(cfg.getOutput());
        System.out.println(cfg.isDbgSend());
        System.out.println(cfg.isDbgRecv());
    }

}   // class ConfigurationUnitTest
