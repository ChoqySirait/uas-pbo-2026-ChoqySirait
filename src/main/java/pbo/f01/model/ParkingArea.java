package pbo.f01.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_areas")
public class ParkingArea implements Comparable<ParkingArea> {
    
    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "allowed_type", nullable = false)
    private String allowedType;

    // Relasi One-to-Many ke Vehicle
    @OneToMany(mappedBy = "parkingArea", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Vehicle> vehicles = new ArrayList<>();

    // Constructor Kosong (Wajib untuk JPA)
    public ParkingArea() {}

    public ParkingArea(String name, int capacity, String allowedType) {
        this.name = name;
        this.capacity = capacity;
        this.allowedType = allowedType;
    }

    // Getter dan Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getAllowedType() { return allowedType; }
    public void setAllowedType(String allowedType) { this.allowedType = allowedType; }

    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }

    // Helper method untuk mengecek sisa kapasitas dan kecocokan tipe
    public boolean canPark(Vehicle vehicle) {
        if (!this.allowedType.equalsIgnoreCase(vehicle.getType())) {
            return false;
        }
        if (this.vehicles.size() >= this.capacity) {
            return false;
        }
        return true;
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
        vehicle.setParkingArea(this);
    }

    // Mengurutkan Area Parkir berdasarkan nama (asc)
    @Override
    public int compareTo(ParkingArea o) {
        return this.name.compareTo(o.name);
    }

    // Representasi cetak: Nama_Area allowed_type kapasitas|jumlah_kendaraan_saat_ini
    @Override
    public String toString() {
        return this.name + " " + this.allowedType + " " + this.capacity + "|" + this.vehicles.size();
    }
}