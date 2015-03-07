import im.tox.tox4j.ToxCoreImpl;
import im.tox.tox4j.annotations.NotNull;
import im.tox.tox4j.core.ToxOptions;
import im.tox.tox4j.core.callbacks.*;
import im.tox.tox4j.core.enums.ToxConnection;
import im.tox.tox4j.core.enums.ToxGroupJoinRejected;
import im.tox.tox4j.core.enums.ToxStatus;
import im.tox.tox4j.core.exceptions.ToxBootstrapException;
import im.tox.tox4j.core.exceptions.ToxFriendAddException;
import im.tox.tox4j.core.exceptions.ToxNewException;

import java.util.Random;

public final class Main {

    public static void main(String[] args) {
        final ToxCoreImpl tox;
        try {
            tox = new ToxCoreImpl(new ToxOptions());
            tox.setName("DiceBot".getBytes());
            tox.setStatus(ToxStatus.NONE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        tox.iteration();
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            //try {
                //connect 192.254.75.104 33445 6058FF1DA1E013AD4F829CBE8E5DDFD30A4DE55901B0997832E3E8A64E19026C
            //    //tox.bootstrap("192.254.75.104", 33445, hexStringToBytes("6058FF1DA1E013AD4F829CBE8E5DDFD30A4DE55901B0997832E3E8A64E19026C"));
            //} catch (ToxBootstrapException e) {
            //    e.printStackTrace();
            //}
            tox.callbackConnectionStatus(new ConnectionStatusCallback() {
                @Override
                public void connectionStatus(@NotNull ToxConnection connectionStatus) {
                    System.out.println(connectionStatus);
                    try {
                        tox.addFriend(hexStringToBytes("ACA5C8AB725A92FF9D1D4397BBDBF7416BBFE306CDF588C52B87BDB75363322046AB56EC3C06"), "utalsdjkf ".getBytes());
                    } catch (ToxFriendAddException e) {
                        e.printStackTrace();
                    }
                    //int groupNumber2 = tox.joinGroup(hexStringToBytes("744F3C356FEB4D49848CF9D8B8B62E0986BE23531E2A565608220230E44BA432B0D1C0B973C16CD60F7DB6EE93CDF3315F2A30AD4843B7EC24040C89B30F099E"));
                    //System.out.println("joined group2 with number " + groupNumber2);
                }
            });

            tox.callbackGroupInvite(new GroupInviteCallback() {
                @Override
                public void groupInvite(int friendNumber, @NotNull byte[] inviteData) {
                    System.out.println("got an invite from " + friendNumber);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] hexStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
