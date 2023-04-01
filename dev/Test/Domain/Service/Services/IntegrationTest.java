package Domain.Service.Services;

import Domain.Business.Objects.Inventory.Category;
import Domain.Business.Objects.Inventory.Product;
import Domain.DAL.Abstract.DAO;
import Domain.Service.Services.HR.EmployeeService;
import Domain.Service.Services.HR.ShiftService;
import Domain.Service.Services.Transport.DocumentService;
import Domain.Service.Services.Transport.OrderService;
import Domain.Service.Services.Transport.TransportService;
import Domain.Service.Services.Transport.TruckService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.time.LocalDate;
import java.util.HashMap;



public class IntegrationTest {
    static LocalDate date = LocalDate.parse("2021-09-19");
    static EmployeeService employeeService = new EmployeeService();
    static ShiftService shiftService = new ShiftService();
    static DocumentService documentService = new DocumentService();
    static OrderService orderService = new OrderService();
    static TransportService transportService = new TransportService();
    static TruckService truckService =new TruckService();
    static InventoryService inventoryService = new InventoryService();
    static SupplierService supplierService = new SupplierService();
    static HashMap<Integer,Integer> productMap = new HashMap<>();

    @BeforeAll
    public static void createTables(){
        DAO.setDBForTests(IntegrationTest.class);

        int orderId = supplierService.order(1, 1).getValue();
        supplierService.addItemToOrder(1,orderId, 1, 20);


    }

    @AfterAll
    public static void removeData(){
        DAO.deleteTestDB(IntegrationTest.class);
    }

    /*
    @Test
    public void carrierInTransport() {
        // note that you need to check that the transport module has inserted those id and the hash map
        orderService.addOrder(1,2,productMap);
    }




    @Test
    public void IssuingAnOrder() {
        Result<Integer> res = supplierService.order(1,1);
        assertTrue(res.isOk());
        int orderId = res.getValue();

        //TODO:  fix the function "isThereAvailableCriersAndSupForTheWeek"
        boolean availableCarriersInTheWeek  = transportService.isThereAvailableCriersAndSupForTheWeek().getValue(); // this function need to be change, and check for supplier validity as well
        if(availableCarriersInTheWeek) {
            assertTrue(supplierService.addItemToOrder(1,orderId,1,1).isOk());
            // check if that transport has been created to a valid day according to
            // check that in the transport there is the new item
            // check if all the relevant id map has been update
            // check if the db has change accordingly

            // create a situation where the addItemToOrder should fail
            // check if the HR will receive the require massage
        }

        else {
            assertFalse(supplierService.addItemToOrder(1,orderId,1,1).isOk());

            // check if the HR will receive the require massage
            // check for no change in the DB and identity map

            // create a situation that should pass
            // check if that transport has been created to a valid day according to
            // check that in the transport there is the new item
            // check if all the relevant id map has been update
            // check if the db has change accordingly
        }

    }

    @Test
    public void acceptingAnOrder(){
        // creating a scenario to accept an order
        // accept the order
        // check if everything is updated

        // create scenario to accept with defect items, and decline it.
        // updating the order manually
        // check for the correctness in the Dal and DB
    }

    @Test
    public void checkDuplicatesInReport(){
        // create an order with the same item from 2 suppliers or more
        // make sure that in the report of the order there will be only 1 item
    }
*/
}
