package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        try{
        if(!parkingLotRepository3.findById(parkingLotId).isPresent() || !userRepository3.findById(userId).isPresent()){
            throw new Exception("Cannot make reservation");
        }
        User user = userRepository3.findById(userId).get();
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        List<Spot> spots = parkingLot.getSpotList();

        int minCost = Integer.MIN_VALUE;

        Spot bookedSpot = null;

        if(numberOfWheels==2){
            for (Spot spot: spots){
                int cost = timeInHours*spot.getPricePerHour();
                if(cost<minCost && spot.getOccupied()){
                    bookedSpot = spot;
                }
            }
        }
        else if(numberOfWheels==4){
            for (Spot spot: spots){
                if(spot.getSpotType()==SpotType.TWO_WHEELER){
                    continue;
                }
                int cost = timeInHours*spot.getPricePerHour();
                if(cost<minCost && spot.getOccupied()){
                    bookedSpot = spot;
                }
            }
        }
        else{
            for (Spot spot: spots){
                if(spot.getSpotType()==SpotType.OTHERS) {
                    int cost = timeInHours * spot.getPricePerHour();
                    if (cost < minCost && spot.getOccupied()) {
                        bookedSpot = spot;
                    }
                }
            }
        }
//        if(bookedSpot==null){
//            throw new Exception("null");
//        }

        Reservation reservation = new Reservation(timeInHours);

        bookedSpot.setOccupied(true);
        reservation.setSpot(bookedSpot);
        reservation.setUser(user);

        bookedSpot.getReservationList().add(reservation);
        user.getReservationList().add(reservation);

        spotRepository3.save(bookedSpot);
        userRepository3.save(user);

        return reservation;
        }
        catch (Exception e){
            return null;
        }

    }
}
