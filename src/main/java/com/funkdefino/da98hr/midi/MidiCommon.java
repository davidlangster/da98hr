package com.funkdefino.da98hr.midi;

import javax.sound.midi.*;

/**
 * <p/>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class MidiCommon {

    //** ------------------------------------------------------ Enumerated types

    public enum Type {
        Input,
        Output
    }

    //** ------------------------------------------------------------ Operations

    /**
     * Returns a MIDI device.
     * @param port the device port name.
     * @param type the device IO type (input or output).
     * @return the device.
     * @throws MidiUnavailableException on error.
     */
    public static MidiDevice getDevice(String port, Type type)
                  throws MidiUnavailableException {

        MidiDevice device = null;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        for(int i = 0; i < infos.length && device == null; i++) {

            MidiDevice dvc = MidiSystem.getMidiDevice(infos[i]);
            // System.out.println(dvc.getDeviceInfo().getName());
            if(dvc.getDeviceInfo().getName().equalsIgnoreCase(port)) {
                boolean allowsInput  = (dvc.getMaxTransmitters() != 0);
                boolean allowsOutput = (dvc.getMaxReceivers() != 0);
                if((allowsOutput && (type == Type.Output)) ||
                   (allowsInput && (type == Type.Input)))  {
                       device = dvc;
                }
            }
        }

        return device;

    }   // getDevice()

}   // class MidiCommon
