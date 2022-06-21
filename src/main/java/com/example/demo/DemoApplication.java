/* Copyright 2022 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
*/
package com.example.demo;

import com.example.demo.Reservation;

import org.springframework.stereotype.Controller;
import com.google.cloud.Timestamp;
import com.google.cloud.Date;
import java.util.Iterator;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;

/*
Main Class for the Spring Application
*/

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

/*
Controller Class for the Server-side App - REST API
*/

@RestController
class ReservationController {
  private final ReservationRepository reservationRepository;

  ReservationController(ReservationRepository reservationRepository) {
            this.reservationRepository = reservationRepository;
        }

    /*
    Read reservation by id
    */
    @GetMapping("/api/reservations/{id}")
    public Reservation getReservation(@PathVariable String id) {
        return reservationRepository.findById(id)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " not found"));
    }

    /* 
    Read reservations with parameterized checks
    */
    @GetMapping("/api/getreservations/{id}")
    public String getReservations(@PathVariable String id) {
        String dateId = id.split("_")[0];
        String hourId = id.split("_")[1];
        Iterator<Reservation> iterator = reservationRepository.findAll().iterator();
        String element = "";
        String date = "";
        String hour = "";
        
        
        while (iterator.hasNext()) {
            Reservation res = iterator.next();
            date = res.getId().toString().split("_")[1];
            hour = Integer.toString(res.getHourNumber());
            if(date.equals(dateId) && hour.equals(hourId)){
                return "present";
            }
        }
        return "absent";                 
    } 
    
    /* 
    Insert reservation
    */
    @PostMapping("/api/reservation")
    public String createReservation(@RequestBody Reservation reservation) {
            java.util.Date dt = new java.util.Date();
            java.util.Calendar c = java.util.Calendar.getInstance(); 
            c.setTime(dt); 
            dt = c.getTime();
            Date d = Date.fromJavaUtilDate(dt);
            reservation.setId(reservation.getAptId() + "_" + d);
            Reservation saved = reservationRepository.save(reservation);
        return saved.getId();
    }

    @GetMapping("/")
    public String saySomething() {
        return "Spring Boot App on Cloud Run, containerized by Jib!";
    }

    /*
    Update reservation by id
    */
    @PutMapping("/api/{id}")
	public Reservation updateReservation(@RequestBody Reservation reservation, @PathVariable ("id") String id) {
        Reservation existingReservation = this.reservationRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " not found"));
        existingReservation.setAptId(reservation.getAptId());
        existingReservation.setHourNumber(reservation.getHourNumber());
        existingReservation.setPlayerCount(reservation.getPlayerCount());
		 return this.reservationRepository.save(existingReservation);
	}
	
	/*
    Delete reservation by id
    */
	@DeleteMapping("/api/{id}")
	public ResponseEntity<Reservation> deleteReservation(@PathVariable ("id") String id){
        Reservation existingReservation = this.reservationRepository.findById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " not found"));
		 this.reservationRepository.delete(existingReservation);
		 return ResponseEntity.ok().build();
	}

}