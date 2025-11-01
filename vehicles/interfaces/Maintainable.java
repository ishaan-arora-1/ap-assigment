package vehicles.interfaces;

public interface Maintainable {
    void scheduleMaintenance();
    boolean needsMaintenance();
    void performMaintenance();
    void setMileageAtLastService(double mileage);
    void setMaintenanceNeeded(boolean needed);
}