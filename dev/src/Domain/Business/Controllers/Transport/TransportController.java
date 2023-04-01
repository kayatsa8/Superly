package Domain.Business.Controllers.Transport;

import Domain.Business.Controllers.HR.EmployeeController;
import Domain.Business.Controllers.HR.ShiftController;
import Domain.Business.Objects.Document.DestinationDocument;
import Domain.Business.Objects.Document.TransportDocument;
import Domain.Business.Objects.Employee.Carrier;
import Domain.Business.Objects.Shift.Shift;
import Domain.Business.Objects.Supplier.Order;
import Domain.Business.Objects.Transport;
import Domain.Business.Objects.Truck;
import Domain.DAL.Controllers.TransportMudel.TransportDAO;
import Globals.Enums.OrderStatus;
import Globals.Enums.ShiftTypes;
import Globals.Enums.TransportStatus;
import Globals.Pair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
public class TransportController {
    private final TransportDAO transportDataMapper = new TransportDAO();
    private TruckController truckController;
    private OrderController orderController;
    private EmployeeController employeeController;
    private DocumentController documentController;
    private SiteController siteController;
    private ShiftController shiftController;

    public TransportController() {
        truckController = new TruckController();
        employeeController = new EmployeeController();
        orderController = new OrderController();
        siteController = new SiteController();
        shiftController = new ShiftController();
        documentController = new DocumentController();
    }
    public Transport createTransport(Pair<LocalDate,ShiftTypes> shift) throws Exception {
        if(shiftController.getShift(shift.getLeft(),shift.getRight()).getStorekeeperCount()>0){
            Transport transport = new Transport(shift);
            transportDataMapper.save(transport);
            return transport;
        }
        else{
            throw new Exception("there is no Storekeeper in this shift");
        }

    }

    public List<Transport> getAllTransports(){
        return transportDataMapper.getAll();
    }



    public void advanceSite(int transportSN,int siteID) throws Exception {
        /*Transport transport = getTransport(transportSN);
        if(transport.getStatus()==TransportStatus.inProgress){
            boolean isDestVisit = transport.destVisit(siteID);
            boolean lastSite = transport.visitSite(siteID);
            transportDataMapper.save(transport);
            if(isDestVisit){
                List<Integer> orders = transport.gerOrders();
                for (Integer orderID:orders) {
                   TransportOrder order = orderController.getTransportOrder(orderID);
                   if(siteID==order.getDst()){
                       DestinationDocument document = new DestinationDocument(orderID,siteID,order.getProducts());
                       documentController.uploadDestinationDocument(document);
                       TransportDocument trd;
                       try{
                           trd = documentController.getTransportDocument(transportSN);
                       }
                       catch (Exception e) {
                           trd = new TransportDocument(transport.getSN(), transport.getStartTime(), transport.getTruckNumber(), transport.getDriverID());
                       }
                       trd.addDoc(orderID);
                       documentController.uploadTransportDocument(trd);
                   }
                }
            }
            if(lastSite){
                endTransport(transportSN);
            }
        }
        else{
            throw new Exception("This transport is not in progress");
        }*/

    }

    public Transport getTransport(int transportSN) throws Exception {
        Transport transport = transportDataMapper.get(transportSN);
        if(transport == null){
            throw new Exception("Transport not found");
        }
        return transport;
    }
    public LocalDate SchedulingOrderToTransport(Order order, List<LocalDate> available) throws Exception {
        LocalDate dateOfTransport = null;
        int weight = (int)(order.getOrderWeight());
        for(LocalDate d: available){
            List<Transport> tInDate = getTransportInDate(d);
            for(Transport t: tInDate){
                if(t.getStatus()==TransportStatus.padding){
                    if (canAddWeightToTransport(t,weight)){
                        addOrderToTransport(t.getSN(),order.getId());
                        dateOfTransport = d;
                        return dateOfTransport;
                    }
                }
            }
            Pair<Boolean,ShiftTypes> canCreate = canCreateTransport(d);
            if(canCreate.getLeft()){
                Transport created = createTransport(new Pair<>(d,canCreate.getRight()));
                addOrderToTransport(created.getSN(),order.getId());
                dateOfTransport = d;
                return dateOfTransport;
            }

        }
        return dateOfTransport;

    }
    public List<Transport> getTransportInDate(LocalDate d){
        List<Transport> all = getAllTransports();
        List<Transport> inDate = new ArrayList<>();
        for(Transport t : all){
            if(t.getShift().getLeft().toString().equals(d.toString())){
                inDate.add(t);
            }
        }
        return inDate;
    }
    public Pair<Boolean,ShiftTypes> canCreateTransportInShift(ShiftTypes shift,LocalDate d){
        try {
            Shift s = shiftController.getShift(d,shift);
            if(s.getStorekeeperCount()>0 && getTransportsInShift(getAllTransports(),new Pair<>(d,shift)).size()<truckController.getTruckNumber()){
                return new Pair<>(true,shift);
            }
        } catch (Exception e) {
            return new Pair<>(false,null);
        }
        return new Pair<>(false,null);
    }
    public Pair<Boolean,ShiftTypes> canCreateTransport(LocalDate d){
        Pair<Boolean,ShiftTypes> create = canCreateTransportInShift(ShiftTypes.Morning,d);
        if(create.getLeft()){
            return create;
        }
        return canCreateTransportInShift(ShiftTypes.Evening,d);
    }

    public void addOrderToTransport(int transportSN, int  orderID) throws Exception {
        Transport transport = getTransport(transportSN);
        if(transport.getStatus()== TransportStatus.padding)
        {

            Order order = orderController.getTransportOrder(convert(orderID));
            if(transport.isPlacedTruck()){
                if(order.getStatus()== OrderStatus.waiting){
                    int extraWeight  = (int)(orderController.getTransportOrder(convert(orderID)).getOrderWeight());
                    updateWeight(transport,extraWeight);
                    transport.addOrder(order);
                    transportDataMapper.save(transport);
                    order.order();
                    orderController.updateOrder(order);
                }
                else{
                    throw new Exception("this order already out");
                }
            }
            else{
                if(order.getStatus()== OrderStatus.waiting){
                    int weight = (int)(order.getOrderWeight());
                    transport.initWeight(weight);
                    transport.addOrder(order);
                    transportDataMapper.save(transport);
                    order.order();
                    orderController.updateOrder(order);
                }
                else{
                    throw new Exception("this order already out");
                }

            }
        }
        else {
            throw new Exception("The transport is not on the list of pending transport!");
        }
    }
    public void updateWeight(Transport transport,int newWeight) throws Exception {
        Truck truck = truckController.getTruck(transport.getTruckNumber());
        if(!transport.updateWeight(newWeight, truck.getMaxCapacityWeight()))
        {
            throw new Exception("Weight Warning!");
        }
    }
    public void placeTruck(int transportSN, int licenseNumber) throws Exception {
        Transport transport = getTransport(transportSN);
        if(transport.getStatus()==TransportStatus.padding){
            Truck truck = truckController.getTruck(licenseNumber);
            List<Transport> allTransports = getAllTransports();
            List<Transport> shiftTransports = getTransportsInShift(allTransports,transport.getShift());
            if(!(isAvailable(shiftTransports,truck) && transport.placeTruck(licenseNumber,truck.getNetWeight(),truck.getMaxCapacityWeight())))
            {
                throw new Exception("truck cant be placed");
            }
            else{
                transportDataMapper.save(transport);
            }
        }
        else{
            throw new Exception("the transport is not in padding list");
        }
    }

    public void placeDriver(int transportSN, String empID) throws Exception {
        Transport transport = getTransport(transportSN);
        if(transport.isPlacedTruck()){
            Carrier carrier = employeeController.getCarrier(empID);
            Truck truck = truckController.getTruck(transport.getTruckNumber());
            if(truck.canDriveOn(carrier.getLicenses()))
            {
                if(transport.isPlacedCarrier()) {
                    Shift shift = shiftController.getShift(transport.getShift().getLeft(),transport.getShift().getRight());
                    Set<String> carriersInShift = shift.getCarrierIDs();
                    if(carriersInShift.contains(carrier.getId())){
                        List<Transport> allTransports = getAllTransports();
                        List<Transport> shiftTransports= getTransportsInShift(allTransports, transport.getShift());
                        if(!isAvailable(shiftTransports,carrier)){
                            throw new Exception("The carrier is already in a transport in this shift");
                        }
                        else{
                            transport.placeDriver(empID);
                            transportDataMapper.save(transport);
                        }
                    }
                    else{
                        throw new Exception("The Carrier is not in this shift");
                    }

                }
                else{
                    throw new Exception("carrier is already placed");
                }
            }
            else {
                throw new Exception("The carrier can't drive on this truck!");
            }
        }
        else{
            throw new Exception("carrier cannot be placed before the truck");
        }

    }

    public void startTransport(int transportSN) throws Exception {
        Transport transport = getTransport(transportSN);
        if(transport.getStatus()==TransportStatus.padding){
            if(transport.readyToGo()){
                transport.startTransport();
                transportDataMapper.save(transport);
            }
            else{
                throw new Exception("transport not ready to go");
            }
        }
        else{
            throw new Exception("this is not a padding transport");
        }
    }

    public List<Integer> endTransport(int transportSN) throws Exception {
        Transport transport = getTransport(transportSN);
        if(transport.getStatus()==TransportStatus.inProgress){

            TransportDocument transportDocument = new TransportDocument(transport.getSN(),transport.getStartTime(),transport.getTruckNumber(),transport.getDriverID());
            for (Integer order:transport.getTransportOrders()) {
                Order o = orderController.getTransportOrder(convert(order));
                o.start();
                orderController.updateOrder(o);
                DestinationDocument document = new DestinationDocument(order,o.getStoreID(),o.getProductList());
                documentController.uploadDestinationDocument(document);
                transportDocument.addDoc(document.getID());
            }
            documentController.uploadTransportDocument(transportDocument);
            transport.endTransport();
            transportDataMapper.save(transport);
            return transport.getTransportOrders();
        }
        else{
            throw new Exception("this is not a inProgress transport");
        }
    }
    private boolean isAvailable(List<Transport> transports,Truck c){
        for (Transport t:transports) {
            if(t.getTruckNumber()==c.getLicenseNumber()){
                return false;
            }
        }
        return true;
    }
    public boolean isAvailable(List<Transport> transports,Carrier c){
        for (Transport t:transports) {
            if(t.getDriverID()==c.getId()){
                return false;
            }
        }
        return true;
    }
    //GETTERS
    public HashMap<Integer, Transport> getPendingTransports() {
        List<Transport> allTransports = getAllTransports();
        HashMap<Integer,Transport> padding = new HashMap<>();
        for(Transport t : allTransports){
            if(t.getStatus()==TransportStatus.padding){
                padding.put(t.getSN(),t);
            }
        }
        return padding;
    }

    public HashMap<Integer, Transport> getInProgressTransports() {
        List<Transport> allTransports = getAllTransports();
        HashMap<Integer,Transport> inProgress = new HashMap<>();
        for(Transport t : allTransports){
            if(t.getStatus()==TransportStatus.inProgress){
                inProgress.put(t.getSN(),t);
            }
        }
        return inProgress;
    }

    public HashMap<Integer, Transport> getCompletedTransports() {
        List<Transport> allTransports = getAllTransports();
        HashMap<Integer,Transport> complete = new HashMap<>();
        for(Transport t : allTransports){
            if(t.getStatus()==TransportStatus.done){
                complete.put(t.getSN(),t);
            }
        }
        return complete;
    }
    public List<Transport> getTransportsInShift(List<Transport >all,Pair<LocalDate,ShiftTypes> s){
        List<Transport> shiftTransports = new ArrayList<>();
        for(Transport t : all){
            if(t.getShift().getLeft()==s.getLeft() && t.getShift().getRight()==s.getRight()){
                shiftTransports.add(t);
            }
        }
        return shiftTransports;
    }
    public boolean canChangeOrder(int orderID, int amount) throws Exception {
        Transport t = getTransportFromOrder(orderID);
        Order order = orderController.getTransportOrder(convert(orderID));
        if(order.getStatus()!=OrderStatus.complete){
            return canAddWeightToTransport(t,amount);
        }
        return false;
    }
    public boolean canAddWeightToTransport(Transport t, int amount) throws Exception {

        if(t.isPlacedTruck()){
            Truck truck = truckController.getTruck(t.getTruckNumber());
            return t.canChangeWeight(amount,truck.getMaxCapacityWeight());
        }
        else{return true;}
    }
    public void changeWeight(int orderID, int amount) throws Exception {
        Transport t = getTransportFromOrder(orderID);
        t.updateWeight(amount);
    }
    public Transport getTransportFromOrder(int orderID) throws Exception {
        List<Transport> all = getAllTransports();
        for(Transport t :all) {
            if (t.getTransportOrders().contains(orderID)) {
                return t;
            }
        }
        throw new Exception("order is not in any transport ");
    }
    public String convert(int i){
        return ""+i;
    }

    public boolean canDeleteOrder(Order order) throws Exception {
        if(order.getStatus() == OrderStatus.waiting){
            return true;
        }
        else {
            if(order.getStatus() == OrderStatus.ordered){
                Transport transport = getTransportFromOrder(order.getId());
                if(transport.getStatus() == TransportStatus.padding){
                    transport.removeOrder(order.getId(),(int)(order.getOrderWeight()));
                    return true;
                }
                else return false;
            }
            return false;
        }


    }

    public void removeTruck(int licenseNumber) throws Exception {
        if(!checkIfTruckPlaceInTransport(licenseNumber)){
            truckController.removeTruck(licenseNumber);
        }
        else{
            throw new Exception("This truck is already embedded in transports that are in progress or pending!");
        }
    }
    private boolean checkIfTruckPlaceInTransport(int licenseNumber){
        List<Transport> allTransports = getAllTransports();
        for(Transport transport: allTransports){
            if(transport.getTruckNumber() == licenseNumber &&
                    (transport.getStatus() == TransportStatus.padding | transport.getStatus() == TransportStatus.inProgress)){
                return true;
            }
        }
        return false;
    }

}



//TODO will be added in the next assignment
    /*
    private List<TransportOrder> getTransportOrderInSameArea(List<ShippingAreas> as) throws Exception {
        List<TransportOrder> orderList = new ArrayList<>();
        for (Integer orderID: orders.keySet())
        {
            //TODO: Update
            if(as.contains(null))
            {
                orderList.add(orders.get(orderID));
            }
        }
        return orderList;
    }

    public List<TransportOrder> getTransportOrderInSameArea(int transportSN) throws Exception {
        if(pendingTransports.containsKey(transportSN))
        {
            Transport transport = pendingTransports.get(transportSN);
            //TODO: return getTransportOrderInSameArea(transport.getSA());
            return null;
        }
        else {
            throw new Exception("The transport is not on the list of pending transport!");
        }
    }*/


