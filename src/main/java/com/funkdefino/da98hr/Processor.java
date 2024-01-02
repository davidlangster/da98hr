package com.funkdefino.da98hr;

import com.funkdefino.da98hr.util.*;
import javax.sound.midi.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class Processor {

    //** ------------------------------------------------------------------ Data

    private final Transmitter transmitter;
    private final Receiver receiver;
    private final Configuration config;

    //** ---------------------------------------------------------- Construction

    /**
     * Ctor.
     * @param transmitter a transmitter.
     * @param receiver a receiver.
     * @param config a configuration value object.
     */
    public Processor(Transmitter transmitter, Receiver receiver,
                     Configuration config) {

        transmitter.setReceiver(new MidiInputReceiver(this));
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.config = config;

    }   // Processor()

    //** ------------------------------------------------------------ Operations

    /**
     * Process incoming CTRL messages.
     * @param msg the CTRL message.
     */
    public synchronized void execute(ShortMessage msg) {

        try {
            MidiMessage message = Mapper.translate(msg);
            if(message != null) {
                receiver.send(message, -1);
            }

            if(config.isDbgRecv()) {
                System.out.printf("[rcv] B%X %02X %02X%n", msg.getChannel(), msg.getData1(), msg.getData2());
            }

            if(config.isDbgSend()) {
                if(message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    System.out.printf("[snd] B%X %02X %02X%n", sm.getChannel(), sm.getData1(), sm.getData2());
                }
            }

        }
        catch(Exception excp) {
            System.out.println(excp.toString());
        }

    }   // execute()

    /**
     * Releases all resources.
     */
    public void close() {
        if(transmitter != null) transmitter.close();
        if(receiver != null) receiver.close();
    }

    //** -------------------------------------------------------- Implementation

    private final static class MidiInputReceiver implements Receiver {

        private Processor proc;
        public  MidiInputReceiver(Processor proc) {
            this.proc = proc;
        }

        public void send(MidiMessage msg, long timeStamp) {
            if(msg instanceof ShortMessage) {
                ShortMessage smsg = (ShortMessage)msg;
                if(smsg.getCommand() == ShortMessage.CONTROL_CHANGE) {
                    try {proc.execute(smsg);}
                    catch(Exception excp) {
                        excp.printStackTrace();
                    }
                }
            }
        }

        public void close()
        {
        }

    }   // class MidiInputReceiver

}   // class Processor
