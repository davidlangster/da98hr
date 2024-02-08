package com.funkdefino.da98hr.util;

import javax.sound.midi.*;

/**
 * <p>
 * <code>$Id: $</code>
 * @author Funkdefino (David M. Lang)
 * @version $Revision: $
 */
public final class Mapper {

    //** ----------------------------------------------------------------- Types

    private enum MatrixCCType {
        Fader,
        VPot,
        SelSw,
        VSel,
        Undefined
    }

    //** ------------------------------------------------------------- Constants

    private final static int MATRIX_BASE   = 8;
    private final static int MATRIX_FDR    = MATRIX_BASE;
    private final static int MATRIX_VPOT   = MATRIX_BASE + 16;
    private final static int MATRIX_SELSW  = MATRIX_BASE + 64;
    private final static int MATRIX_VSEL   = MATRIX_BASE + 80;
    private final static int RECORD_STROBE = 0x7F;
    private final static int DA_VOLUME     = 0x07;
    private final static int DA_PAN        = 0x0A;
    private final static int DA_MUTE       = 0x0B;

    //** ------------------------------------------------------------------ Data

    private static byte    state;  // Track arm state
    private static boolean strobe; // Record strobe state

    //** ------------------------------------------------------------ Operations

    /**
     * Translates Matrix single-channel CC layer messages to multichannel DA98HR
     * messages. Matrix Faders emit CCs 0-15 (volume), VPots emit CCs 16-31 (pan)
     * and VSels emit CCs 80-95 (mute), on channel 1. DA98HR responds to three
     * CCs - volume (0x07), pan (0x0A) & mute (0x0B) - on channels 1-8. We use
     * Matrix Faders, VPots & VSel controllers to adjust volume, pan and mute on
     * the DA98HR. Additionally, Matrix Sel switches are used for track arming.
     * @param smsg the MIDI message.
     * @return the translated message (or null).
     */
    public static MidiMessage translate(ShortMessage smsg) throws Exception {


        int ctrl = smsg.getData1();
        int val  = smsg.getData2();

        // ** Record Strobe from OP1 **

        if(ctrl == RECORD_STROBE && val == 0x7F) {
            return record((strobe ^= true));
        }

        //** MATRIX processing **

        if(smsg.getChannel() != 0) return null;

        MidiMessage message = null;
        int channel;

        switch(getType(ctrl)) {
            case Fader:
                channel = ctrl - MATRIX_FDR;
                message = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, DA_VOLUME, val);
                break;
            case VPot:
                channel = ctrl - MATRIX_VPOT;
                message = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, DA_PAN,    val);
                break;
            case VSel:
                channel = ctrl - MATRIX_VSEL;
                message = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, DA_MUTE,   val);
                break;
            case SelSw:
                channel = ctrl - MATRIX_SELSW;
                if(val > 0) state |= (byte)(0x01 << channel);
                else state &= (byte)(~(0x01 << channel));
                message = trackArm(state);
                break;
            default:
                break;
        }

        return message;

    }   // translate()

    /**
     * Formats the MMC track arm message.
     *  +--+--+--+--+--+--+--+--+  +--+--+--+--+--+--+--+--+
     *  +  +T2+T1+  +  +  +  +  +  +  +  +T8+T7+T6+T5+T4+T3+
     *  +--+--+--+--+--+--+--+--+  +--+--+--+--+--+--+--+--+
     * @param  state the runtime arm state.
     * @return the SysexMessage.
     * @throws Exception on error.
     */
    public static SysexMessage trackArm(byte state) throws Exception {

        int arm1 = (state & 0b00000011) << 5;
        int arm2 = (state & 0b11111100) >> 2;
        byte[] buf = new byte[11];

        buf[ 0] = (byte)0xF0;
        buf[ 1] = (byte)0x7F;
        buf[ 2] = (byte)0x7F;
        buf[ 3] = (byte)0x06;  // Command
        buf[ 4] = (byte)0x40;  // Write
        buf[ 5] = (byte)0x04;  // L1
        buf[ 6] = (byte)0x4F;
        buf[ 7] = (byte)0x02;  // L2
        buf[ 8] = (byte)arm1;  // Track bitmap
        buf[ 9] = (byte)arm2;  // Track bitmap
        buf[10] = (byte)0xF7;

        return new SysexMessage(buf, buf.length);

    }   // trackArm()

    /**
     * Formats the MMC record strobe message.
     * @param strobe the runtime strobe state.
     * @return the SysexMessage.
     * @throws Exception on error.
     */
    public static SysexMessage record(boolean strobe) throws Exception {

        byte[] buf = new byte[6];

        buf[0] = (byte)0xF0;
        buf[1] = (byte)0x7F;
        buf[2] = (byte)0x7F;
        buf[3] = (byte)0x06;                   // Command
        buf[4] = (byte)(strobe ? 0x06 : 0x07); // Record strobe / exit
        buf[5] = (byte)0xF7;

        return new SysexMessage(buf, buf.length);

    }   // record()

    //** -------------------------------------------------------- Implementation

    /**
     * Gets the Maxtrix controller type.
     * @param ctrl the controller value.
     * @return the type.
     * @throws RuntimeException on error.
     */
    private static MatrixCCType getType(int ctrl) {

        if(ctrl >= MATRIX_FDR   && ctrl <= MATRIX_FDR   + 8) return MatrixCCType.Fader;
        if(ctrl >= MATRIX_VPOT  && ctrl <= MATRIX_VPOT  + 8) return MatrixCCType.VPot;
        if(ctrl >= MATRIX_SELSW && ctrl <= MATRIX_SELSW + 8) return MatrixCCType.SelSw;
        if(ctrl >= MATRIX_VSEL  && ctrl <= MATRIX_VSEL  + 8) return MatrixCCType.VSel;

        return MatrixCCType.Undefined;

    }   // getType()

}   // class Mapper
