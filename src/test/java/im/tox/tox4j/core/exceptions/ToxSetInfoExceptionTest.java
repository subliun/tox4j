package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.ToxCoreImplTestBase;
import im.tox.tox4j.core.ToxConstants;
import im.tox.tox4j.core.ToxCore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ToxSetInfoExceptionTest extends ToxCoreImplTestBase {

    @Test
    public void testSetNameTooLong() throws Exception {
        try (ToxCore tox = newTox()) {
            byte[] array = randomBytes(ToxConstants.MAX_NAME_LENGTH + 1);
            tox.setName(array);
            fail();
        } catch (ToxSetInfoException e) {
            assertEquals(ToxSetInfoException.Code.TOO_LONG, e.getCode());
        }
    }

    @Test
    public void testSetStatusMessageTooLong() throws Exception {
        try (ToxCore tox = newTox()) {
            byte[] array = randomBytes(ToxConstants.MAX_STATUS_MESSAGE_LENGTH + 1);
            tox.setStatusMessage(array);
            fail();
        } catch (ToxSetInfoException e) {
            assertEquals(ToxSetInfoException.Code.TOO_LONG, e.getCode());
        }
    }

}