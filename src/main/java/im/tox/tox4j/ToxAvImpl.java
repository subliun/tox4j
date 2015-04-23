package im.tox.tox4j;

import com.google.protobuf.InvalidProtocolBufferException;
import im.tox.tox4j.annotations.NotNull;
import im.tox.tox4j.annotations.Nullable;
import im.tox.tox4j.av.ToxAv;
import im.tox.tox4j.av.callbacks.*;
import im.tox.tox4j.av.enums.ToxCallControl;
import im.tox.tox4j.av.enums.ToxCallState;
import im.tox.tox4j.av.exceptions.*;
import im.tox.tox4j.av.proto.Av;
import im.tox.tox4j.core.ToxCore;

public final class ToxAvImpl implements ToxAv {

    static {
        System.loadLibrary("tox4j");
    }

    private final ToxCoreImpl tox;
    private final int instanceNumber;
    private CallCallback callCallback;
    private CallStateCallback callStateCallback;
    private ReceiveVideoFrameCallback receiveVideoFrameCallback;
    private ReceiveAudioFrameCallback receiveAudioFrameCallback;

    private static native int toxAvNew(int toxInstanceNumber) throws ToxAvNewException;

    public ToxAvImpl(ToxCore tox) throws ToxAvNewException {
        if (tox instanceof ToxCoreImpl) {
            this.tox = (ToxCoreImpl) tox;
            this.tox.av = this;
            instanceNumber = toxAvNew(this.tox.instanceNumber);
        } else {
            throw new IllegalArgumentException(
                    "This implementation of " + ToxAv.class.getName() +
                    " requires an instance of " + ToxCoreImpl.class.getName()
            );
        }
    }


    private static native void toxAvKill(int instanceNumber);

    @Override
    public void close() {
        tox.av = null;
        toxAvKill(instanceNumber);
    }


    private static native void finalize(int instanceNumber);

    @Override
    public void finalize() throws Throwable {
        try {
            finalize(instanceNumber);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.finalize();
    }


    private static native int toxAvIterationInterval(int instanceNumber);

    @Override
    public int iterationInterval() {
        return toxAvIterationInterval(instanceNumber);
    }

    private static native byte[] toxAvIteration(int instanceNumber);

    @Override
    public void iteration() {
        byte[] events = toxAvIteration(instanceNumber);
        Av.AvEvents toxEvents;
        try {
            toxEvents = Av.AvEvents.parseFrom(events);
        } catch (InvalidProtocolBufferException e) {
            // This would be very bad, meaning something went wrong in our own C++ code.
            throw new RuntimeException(e);
        }

        if (callCallback != null) {
            for (Av.Call call : toxEvents.getCallList()) {
                callCallback.call(call.getFriendNumber(), call.getAudioEnabled(), call.getVideoEnabled());
            }
        }
        if (callStateCallback != null) {
            for (Av.CallState callState : toxEvents.getCallStateList()) {
                callStateCallback.callState(callState.getFriendNumber(), callState.getState());
            }
        }
        if (receiveAudioFrameCallback != null) {
            for (Av.ReceiveAudioFrame receiveAudioFrame : toxEvents.getReceiveAudioFrameList()) {
                short[] pcm = new short[receiveAudioFrame.getPcmCount()];
                for (int i = 0; i < pcm.length; i++) {
                    pcm[i] = (short) receiveAudioFrame.getPcm(i);
                }
                receiveAudioFrameCallback.receiveAudioFrame(receiveAudioFrame.getFriendNumber(),
                        pcm, receiveAudioFrame.getChannels(), receiveAudioFrame.getSamplingRate());
            }
        }
        if (receiveVideoFrameCallback != null) {
            for (Av.ReceiveVideoFrame receiveVideoFrame : toxEvents.getReceiveVideoFrameList()) {
                receiveVideoFrameCallback.receiveVideoFrame(
                        receiveVideoFrame.getFriendNumber(),
                        receiveVideoFrame.getWidth(),
                        receiveVideoFrame.getHeight(),
                        receiveVideoFrame.getY().toByteArray(),
                        receiveVideoFrame.getU().toByteArray(),
                        receiveVideoFrame.getV().toByteArray()
                );
            }
        }
    }


    private static native void toxAvCall(int instanceNumber, int friendNumber, int audioBitRate, int videoBitRate) throws ToxCallException;

    @Override
    public void call(int friendNumber, int audioBitRate, int videoBitRate) throws ToxCallException {
        toxAvCall(instanceNumber, friendNumber, audioBitRate, videoBitRate);
    }

    @Override
    public void callbackCall(@Nullable CallCallback callback) {
        this.callCallback = callback;
    }


    private static native void toxAvAnswer(int instanceNumber, int friendNumber, int audioBitRate, int videoBitRate) throws ToxAnswerException;

    @Override
    public void answer(int friendNumber, int audioBitRate, int videoBitRate) throws ToxAnswerException {
        toxAvAnswer(instanceNumber, friendNumber, audioBitRate, videoBitRate);
    }


    private static native void toxAvCallControl(int instanceNumber, int friendNumber, int control) throws ToxCallControlException;

    @Override
    public void callControl(int friendNumber, @NotNull ToxCallControl control) throws ToxCallControlException {
        toxAvCallControl(instanceNumber, friendNumber, control.ordinal());
    }

    @Override
    public void callbackCallControl(@Nullable CallStateCallback callback) {
        this.callStateCallback = callback;
    }


    private static native void toxAvSetAudioBitRate(int instanceNumber, int friendNumber, int audioBitRate) throws ToxBitRateException;

    @Override
    public void setAudioBitRate(int friendNumber, int bitRate) throws ToxBitRateException {
        toxAvSetAudioBitRate(instanceNumber, friendNumber, bitRate);
    }


    private static native void toxAvSetVideoBitRate(int instanceNumber, int friendNumber, int videoBitRate) throws ToxBitRateException;

    @Override
    public void setVideoBitRate(int friendNumber, int bitRate) throws ToxBitRateException {
        toxAvSetVideoBitRate(instanceNumber, friendNumber, bitRate);
    }

    private static native void toxAvSendVideoFrame(int instanceNumber, int friendNumber, int width, int height, byte[] y, byte[] u, byte[] v) throws ToxSendFrameException;

    @Override
    public void sendVideoFrame(int friendNumber, int width, int height, @NotNull byte[] y, @NotNull byte[] u, @NotNull byte[] v) throws ToxSendFrameException {
        toxAvSendVideoFrame(instanceNumber, friendNumber, width, height, y, u, v);
    }

    private static native void toxAvSendAudioFrame(int instanceNumber, int friendNumber, short[] pcm, int sampleCount, int channels, int samplingRate) throws ToxSendFrameException;

    @Override
    public void sendAudioFrame(int friendNumber, @NotNull short[] pcm, int sampleCount, int channels, int samplingRate) throws ToxSendFrameException {
        toxAvSendAudioFrame(instanceNumber, friendNumber, pcm, sampleCount, channels, samplingRate);
    }

    @Override
    public void callbackReceiveVideoFrame(ReceiveVideoFrameCallback callback) {
        this.receiveVideoFrameCallback = callback;
    }

    @Override
    public void callbackReceiveAudioFrame(ReceiveAudioFrameCallback callback) {
        this.receiveAudioFrameCallback = callback;
    }

    @Override
    public void callback(@Nullable ToxAvEventListener handler) {
        callbackCall(handler);
        callbackCallControl(handler);
        callbackReceiveAudioFrame(handler);
        callbackReceiveVideoFrame(handler);
    }
}
