package edu.infrastructure.kafka;

record Address(String id,
               String street,
               String houseNumber,
               String apartmentNumber,
               String city,
               String postalCode,
               String country) {
}
