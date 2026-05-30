package pbo.f01;

import pbo.f01.model.ParkingArea;
import pbo.f01.model.Vehicle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    static {
        System.setProperty("org.jboss.logging.provider", "jdk");

        Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        Logger.getLogger("org.hibernate.SQL").setLevel(Level.OFF);
        Logger.getLogger("org.hibernate.type").setLevel(Level.OFF);
        Logger.getLogger("org.jboss").setLevel(Level.OFF);

        System.setProperty("hibernate.show_sql", "false");
    }

    public static void main(String[] args) {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("pbo-f01-pu");

        EntityManager em = emf.createEntityManager();

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] tokens = input.split("#");
            String command = tokens[0];

            switch (command) {

                case "area-add":

                    if (tokens.length == 4) {

                        String name = tokens[1];
                        int capacity = Integer.parseInt(tokens[2]);
                        String allowedType = tokens[3];

                        em.getTransaction().begin();

                        ParkingArea area =
                                em.find(ParkingArea.class, name);

                        if (area == null) {
                            em.persist(
                                    new ParkingArea(
                                            name,
                                            capacity,
                                            allowedType
                                    )
                            );
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

                        Vehicle vehicle =
                                em.find(Vehicle.class, plateNumber);

                        if (vehicle == null) {

                            em.persist(
                                    new Vehicle(
                                            plateNumber,
                                            owner,
                                            type
                                    )
                            );
                        }

                        em.getTransaction().commit();
                    }

                    break;

                case "park":

                    if (tokens.length == 3) {

                        String plateNumber = tokens[1];
                        String areaName = tokens[2];

                        em.getTransaction().begin();

                        Vehicle vehicle =
                                em.find(
                                        Vehicle.class,
                                        plateNumber
                                );

                        ParkingArea area =
                                em.find(
                                        ParkingArea.class,
                                        areaName
                                );

                        if (
                                vehicle != null &&
                                area != null &&
                                area.canPark(vehicle)
                        ) {

                            area.addVehicle(vehicle);

                            em.merge(vehicle);
                            em.merge(area);
                        }

                        em.getTransaction().commit();
                    }

                    break;

                case "display-all":

                    List<ParkingArea> areas =
                            em.createQuery(
                                    "SELECT a FROM ParkingArea a",
                                    ParkingArea.class
                            ).getResultList();

                    Collections.sort(areas);

                    for (ParkingArea area : areas) {

                        System.out.println(area);

                        List<Vehicle> vehicles =
                                area.getVehicles();

                        Collections.sort(vehicles);

                        for (Vehicle vehicle : vehicles) {
                            System.out.println(vehicle);
                        }
                    }

                    em.close();
                    emf.close();
                    scanner.close();
                    return;
            }
        }

        em.close();
        emf.close();
        scanner.close();
    }
}