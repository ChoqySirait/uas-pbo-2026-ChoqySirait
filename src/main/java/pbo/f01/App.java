package pbo.f01;

import pbo.f01.model.ParkingArea;
import pbo.f01.model.Vehicle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class App {
    // TAMBAHKAN BLOK STATIC INI DI SINI
    static {
        // Membungkam JBoss Logging yang digunakan Hibernate
        System.setProperty("org.jboss.logging.provider", "jdk");
        // Membungkam logger bawaan JDK untuk kategori hibernate
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);
    }

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("pbo-f01-pu");

    public static void main(String[] args) {

        java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);
        EntityManager em = emf.createEntityManager();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("#");
            String command = tokens[0];

            switch (command) {
                case "area-add":
                    if (tokens.length == 4) {
                        String name = tokens[1];
                        int capacity = Integer.parseInt(tokens[2]);
                        String allowedType = tokens[3];

                        em.getTransaction().begin();
                        ParkingArea area = em.find(ParkingArea.class, name);
                        if (area == null) {
                            area = new ParkingArea(name, capacity, allowedType);
                            em.persist(area);
                        }
                        em.getTransaction().commit();
                    }
                    break;

                case "vehicle-add":
                    if (tokens.length == 4) {
                        String plateNumber = tokens[1];
                        String owner = tokens[2];
                        String type = tokens[3];

                        em.getTransaction().begin();
                        Vehicle vehicle = em.find(Vehicle.class, plateNumber);
                        if (vehicle == null) {
                            vehicle = new Vehicle(plateNumber, owner, type);
                            em.persist(vehicle);
                        }
                        em.getTransaction().commit();
                    }
                    break;

                case "park":
                    if (tokens.length == 3) {
                        String plateNumber = tokens[1];
                        String areaName = tokens[2];

                        em.getTransaction().begin();
                        Vehicle vehicle = em.find(Vehicle.class, plateNumber);
                        ParkingArea area = em.find(ParkingArea.class, areaName);

                        // Validasi keberadaan entitas, tipe kendaraan, dan kapasitas area parkir
                        if (vehicle != null && area != null && area.canPark(vehicle)) {
                            area.addVehicle(vehicle);
                            em.merge(area);
                        }
                        em.getTransaction().commit();
                    }
                    break;

                case "display-all":
                    // Mengambil semua area parkir dari database
                    List<ParkingArea> areas = em.createQuery("SELECT a FROM ParkingArea a", ParkingArea.class)
                                                .getResultList();
                    
                    // Sorting Area secara Ascending berdasarkan Nama
                    Collections.sort(areas);

                    for (ParkingArea area : areas) {
                        // Cetak informasi Area Parkir
                        System.out.println(area.toString());
                        
                        // Ambil daftar kendaraan yang sukses parkir di area tersebut
                        List<Vehicle> parkedVehicles = area.getVehicles();
                        // Sorting Kendaraan secara Ascending berdasarkan Plat Nomor
                        Collections.sort(parkedVehicles);

                        for (Vehicle vehicle : parkedVehicles) {
                            System.out.println(vehicle.toString());
                        }
                    }

                    // Menutup resource pemrosesan dan keluar dari program sesuai instruksi soal
                    em.close();
                    emf.close();
                    scanner.close();
                    return;

                default:
                    break;
            }
        }

        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
        scanner.close();
    }
}