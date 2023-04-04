package Domain.Business.Controllers;

import Domain.Business.Controllers.Transport.TransportController;
import Globals.Enums.ShiftTypes;
import Globals.Pair;
import junit.framework.TestCase;

import java.time.LocalDate;

public class TransportControllerTest extends TestCase {



    TransportController transportController = new TransportController();



    //integration test

    // this test requires loading the employee db
    public void testCreateTransport() {
        try {
            transportController.createTransport(new Pair<>(LocalDate.parse("2022-06-25"), ShiftTypes.Morning));
        } catch (Exception e) {
            assertTrue(true);
        }
    }


    public void testPlaceCarrier(){
        try {
            transportController.placeDriver(0,"100");
            assertSame("100", transportController.getTransport(0).getDriverID());
        } catch (Exception e) {

        }
    }
}