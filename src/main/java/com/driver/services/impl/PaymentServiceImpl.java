package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.model.Spot;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        PaymentMode[] modes = PaymentMode.values();
        for(PaymentMode mode1: modes){
            String compMode = mode.toUpperCase();
            if(mode1.toString()!=compMode){
                throw new Exception("Payment mode not detected");
            }
        }
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Spot spot = reservation.getSpot();
        int bill = reservation.getNumberOfHours()*spot.getPricePerHour();
        if(bill>amountSent){
            throw new Exception("Insufficient Amount");
        }
        Payment payment = new Payment();
        payment.setPaymentMode(PaymentMode.valueOf(mode));
        payment.setPaymentCompleted(true);

        reservation.setPayment(payment);

        reservationRepository2.save(reservation);
        return payment;
    }
}
