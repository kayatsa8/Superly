package Domain.Business.Controllers.Transport;

import Domain.Business.Objects.Truck;
import Domain.DAL.Controllers.TransportMudel.TruckDAO;
import Globals.Enums.TruckModel;


public class TruckController {
    private final TruckDAO truckDataMapper = new TruckDAO();
    public TruckController() {
    }

    public void removeTruck(int licenseNumber) throws Exception {
        truckDataMapper.delete(licenseNumber);
    }

    public void addTruck(int licenseNumber, TruckModel model, int netWeight, int maxCapacityWeight) throws Exception {
        Truck truck = new Truck(licenseNumber, model, netWeight, maxCapacityWeight);
        truckDataMapper.insert(truck);
    }

    public Truck getTruck(int truckNumber) throws Exception {
        Truck t = truckDataMapper.get(truckNumber);
        if(t==null){
            throw new Exception("Truck not found");
        }
        return t;
    }
    public int getTruckNumber(){
        return truckDataMapper.size();
    }
}
