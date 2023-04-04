package Domain.Service.Services.Transport;

import Domain.Business.Controllers.Transport.OrderController;
import Domain.Business.Objects.Supplier.Order;
import Domain.Service.util.Result;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderService {
    private OrderController order;
    public OrderService(){
        order = new OrderController();
    }
    public Result addOrder(int src,int dst,HashMap<Integer,Integer> product) {
        return null;
    }

    private Set<Domain.Service.Objects.SupplierObjects.ServiceOrderObject> toTOService(List<Domain.Business.Objects.Supplier.Order> orders)
    {
        Set<Domain.Service.Objects.SupplierObjects.ServiceOrderObject> orderSet = new HashSet<>();
        return orderSet;
    }

    public Result getPendingOrders() {
        try {
            return Result.makeOk(toTOService((List<Order>) (order.getPendingOrder())));
        }
        catch (Exception e){
            return Result.makeError(e.getMessage());
        }
    }

    public String[] getImportantMessages() throws Exception {
        return order.alertsToHR();
    }

}
