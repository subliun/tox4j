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

public final class Main {

    public static void main(String[] args) {
        final ToxCoreImpl tox;
        try {
            tox = new ToxCoreImpl(new ToxOptions());
            tox.setName("tox4jgrouptest".getBytes());
            tox.setStatus(ToxStatus.NONE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        tox.iteration();
                        try {
                            Thread.sleep(tox.iterationInterval());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            try {
                tox.bootstrap("192.254.75.104", 33445, hexStringToBytes("6058FF1DA1E013AD4F829CBE8E5DDFD30A4DE55901B0997832E3E8A64E19026C"));
            } catch (ToxBootstrapException e) {
                e.printStackTrace();
            }
            tox.callbackConnectionStatus(new ConnectionStatusCallback() {
                @Override
                public void connectionStatus(@NotNull ToxConnection connectionStatus) {
                    System.out.println(connectionStatus);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        tox.addFriend(hexStringToBytes("1068EBF134EC891FDDB78A30D3C614DCFADEC0F98A57546904F2408092B40161108164E3912B"), "u wot m8".getBytes());
                    } catch (ToxFriendAddException e) {
                        e.printStackTrace();
                    }
                    int groupNumber = tox.joinGroup(hexStringToBytes("E64E9C45758CF80BC70699F4D93CAB305997A939365D6233715CC4824AA35AFC"));
                    System.out.println("joined group with number " + groupNumber);
                    //int groupNumber2 = tox.joinGroup(hexStringToBytes("744F3C356FEB4D49848CF9D8B8B62E0986BE23531E2A565608220230E44BA432B0D1C0B973C16CD60F7DB6EE93CDF3315F2A30AD4843B7EC24040C89B30F099E"));
                    //System.out.println("joined group2 with number " + groupNumber2);
                }
            });

            tox.callbackGroupMessage(new GroupMessageCallback() {
                @Override
                public void groupMessage(int groupNumber, int peerNumber, int timeDelta, @NotNull byte[] message) {
                    String strMsg = new String(message);
                    System.out.println("got a message from group " + groupNumber + ", peer " + peerNumber + " that says :\n" + new String(message));
                    if (strMsg.startsWith("msg: ")) {
                        tox.sendGroupMessage(groupNumber, message);
                    } else if (strMsg.startsWith("kill: ")) {
                        tox.deleteGroup(groupNumber, "I'm dying".getBytes());
                    }
                }
            });

            tox.callbackGroupAction(new GroupActionCallback() {
                @Override
                public void groupAction(int groupNumber, int peerNumber, int timeDelta, @NotNull byte[] message) {
                    System.out.println("got an action from group " + groupNumber + ", peer " + peerNumber + " that says :\n" + new String(message));
                }
            });

            tox.callbackGroupInvite(new GroupInviteCallback() {
                @Override
                public void groupInvite(int friendNumber, @NotNull byte[] inviteData) {
                    System.out.println("got an invite from " + friendNumber);
                }
            });

            tox.callbackGroupNickChange(new GroupNickChangeCallback() {
                @Override
                public void groupNickChange(int groupNumber, int peerNumber, @NotNull byte[] nick) {
                    System.out.println("group number " + groupNumber + " peer " + peerNumber + " nick changed to " + nick);
                }
            });

            tox.callbackGroupTopicChange(new GroupTopicChangeCallback() {
                @Override
                public void groupTopicChange(int groupNumber, int peerNumber, @NotNull byte[] topic) {
                    System.out.println("group number " + groupNumber + " peer " + peerNumber + " topic changed to " + topic);
                }
            });

            tox.callbackGroupJoinRejected(new GroupJoinRejectedCallback() {
                @Override
                public void groupJoinRejected(int groupNumber, ToxGroupJoinRejected rejectedReason) {
                    System.out.println("group number " + groupNumber + " rejected join with reason " + rejectedReason);
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

}
